---
title: "parallelStream Starving an Unrelated Feature via the Shared ForkJoinPool"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md
  - ../syllabus/02-java/concurrency/completablefuture-and-async-composition.md
source: handbook/concurrency/forkjoinpool-and-work-stealing.md#production-scenarios
---

# parallelStream Starving an Unrelated Feature via the Shared ForkJoinPool

## Context

A reporting endpoint uses `.parallelStream()` for a CPU-heavy aggregation. An entirely separate, unrelated feature uses `CompletableFuture.supplyAsync()` with no explicit executor.

## Symptoms

After the reporting endpoint ships, the unrelated `CompletableFuture`-based feature starts intermittently showing elevated latency, correlated in time with the reporting endpoint receiving traffic — despite the two features sharing no code, no data, and no obvious coupling.

## Impact

An unrelated feature's latency degrades in a way that's genuinely difficult to trace, since the two features appear completely independent in the codebase.

## Initial Hypotheses

- A database or downstream-service contention issue shared by both features — checked, and ruled out: they use entirely separate data stores.
- A deploy-timing coincidence — checked, and ruled out: the correlation holds across multiple, independent traffic spikes.
- Both features are unknowingly sharing `ForkJoinPool.commonPool()` — correct.

## Evidence

Thread-dump analysis during a correlated incident shows `ForkJoinPool.commonPool-worker-*` threads saturated with the reporting endpoint's parallel-stream tasks, with the unrelated feature's `CompletableFuture` callbacks queued behind them on the identical shared pool.

## Investigation Timeline

1. **Elevated latency reported** for a `CompletableFuture`-based feature, correlating in time with traffic to an entirely separate reporting endpoint.
2. **Shared downstream dependency ruled out** — the two features are confirmed to use entirely separate data stores with no overlap.
3. **Deploy-timing coincidence ruled out** — the correlation between reporting traffic and the unrelated feature's latency holds consistently across multiple, independent traffic spikes over time, not just a single deployment window.
4. **Thread dumps captured during a correlated incident**, revealing `ForkJoinPool.commonPool-worker-*` threads fully occupied by the reporting endpoint's parallel-stream tasks.
5. **Shared-pool mechanism confirmed** — the unrelated feature's `CompletableFuture` callbacks are queued behind the reporting endpoint's work on the identical `ForkJoinPool.commonPool()` instance, since both `.parallelStream()` and unqualified `CompletableFuture.supplyAsync()` route to that one shared, process-wide pool by default.

## Root Cause

`.parallelStream()` and unqualified `CompletableFuture.supplyAsync()` both route through `ForkJoinPool.commonPool()` by default, with no isolation between unrelated features that happen to both use JDK conveniences without specifying an explicit executor.

## Immediate Mitigation

Reduce the reporting endpoint's traffic (rate-limit or temporarily disable) to relieve pressure on the shared pool while a permanent fix is prepared.

## Permanent Fix

Give the reporting endpoint's parallel computation a dedicated `ForkJoinPool` (constructed explicitly, sized deliberately) instead of the default common pool, isolating it from every other common-pool consumer in the process.

## Alternatives Considered

Rewriting the aggregation to avoid `.parallelStream()` entirely — a real, valid alternative, but discarded here since a correctly-isolated dedicated pool preserves the measured parallel speedup this workload benefits from, without the cross-feature contention cost.

## Trade-offs

A dedicated pool means one more resource to size and monitor explicitly — accepted, since the alternative (silent, hard-to-trace cross-feature contention) is worse.

## Prevention

Any CPU-heavy `.parallelStream()` usage, or any `*Async` call with no explicit executor, in a request path should be flagged in review as a potential shared-common-pool contention risk, especially in services with multiple independent CPU-heavy features.

## Monitoring and Alerts

- Instrument `ForkJoinPool.commonPool().getPoolSize()`, `getActiveThreadCount()`, and `getQueuedTaskCount()` as standing metrics on any service using `.parallelStream()` or unqualified `*Async` calls, so common-pool saturation is visible on a dashboard rather than requiring an ad hoc thread dump during an active incident.
- Add cross-feature latency correlation as a standing dashboard view (latency of feature A plotted against traffic volume of feature B) for any two features suspected of sharing infrastructure implicitly, since this is exactly the kind of coupling that is invisible from the codebase alone.
- Alert when a dedicated `ForkJoinPool`'s queued-task count or active-thread count approaches its configured parallelism sustained over time, distinct from any alert on the shared common pool, so a newly-isolated workload's own capacity limits are caught proactively rather than only after isolation has already prevented cross-feature impact.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** an unrelated feature's latency degraded intermittently in a way that correlated with traffic to a completely separate reporting endpoint, despite the two sharing no code, data, or obvious coupling.
- **Task:** trace a cross-feature performance coupling that wasn't visible anywhere in the codebase's own structure.
- **Action:** ruled out a shared downstream dependency and a deploy-timing coincidence, then captured thread dumps during a correlated incident and found both features' work queued on the identical `ForkJoinPool.commonPool()` instance.
- **Result:** relieved the immediate pressure by rate-limiting the reporting endpoint, then gave it a dedicated, explicitly-sized `ForkJoinPool`, permanently isolating it from every other common-pool consumer in the process.

## Staff-Level Discussion

This incident is a clean illustration of implicit shared infrastructure coupling two features that are architecturally, organizationally, and operationally independent — nothing in either feature's code, ownership, or deployment history suggests any relationship, yet they compete for the same finite JVM-wide resource the moment both reach for a JDK convenience method with no explicit executor. This is a category of risk that's easy to miss precisely because "just use `.parallelStream()`" and "just call `supplyAsync()`" are presented as simple, low-ceremony defaults — the cost of that simplicity is an invisible, process-wide shared resource that any other team's code can also be drawing from without either team knowing. A Staff engineer's response to finding this once should be to treat "does this use the JDK's default common pool" as a standing question for any CPU-heavy or latency-sensitive workload during design review, and to establish a team-wide convention (an explicit, named, sized `Executor`/`ForkJoinPool` for any meaningfully-sized workload) rather than relying on every team independently discovering the shared-pool risk after a production incident. At a larger organizational scale, this also argues for a lightweight, discoverable registry of what's actually running on the shared common pool across services in the same process or fleet, since the coupling this incident surfaced is invisible to any single team's own service-level view.

## Related Handbook Chapters

- [ForkJoinPool and Work-Stealing](../syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md) — canonical work-stealing mechanics, the `getStealCount()` verification, and the shared-common-pool resource-sharing risk this incident reproduces.
- [CompletableFuture and Async Composition](../syllabus/02-java/concurrency/completablefuture-and-async-composition.md) — the other half of the shared-pool coupling, including why unqualified `*Async` calls route to the identical default executor.
