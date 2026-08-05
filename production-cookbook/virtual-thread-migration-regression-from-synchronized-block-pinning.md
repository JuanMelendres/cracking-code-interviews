---
title: "Virtual Thread Migration Regression From Synchronized-Block Pinning"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/concurrency/virtual-threads.md
source: handbook/concurrency/virtual-threads.md#production-scenarios
---

# Virtual Thread Migration Regression From Synchronized-Block Pinning

## Context

A service migrates its request-handling executor from a fixed platform-thread pool to `newVirtualThreadPerTaskExecutor()`, expecting a significant throughput improvement for its IO-heavy workload — mostly downstream HTTP calls. The legacy request-handling path wraps its downstream HTTP call inside a `synchronized` block guarding a shared, rarely updated configuration cache lookup.

## Symptoms

After the migration, throughput barely changes, and under peak load it's measurably worse than before the migration.

## Impact

A migration expected to improve scalability instead produces no benefit and a regression, with no compiler error or exception pointing at the cause.

## Initial Hypotheses

- The virtual thread executor itself is misconfigured — checked and ruled out; configuration matches documented best practice.
- The downstream dependency got slower coincidentally — checked and ruled out; downstream latency is unchanged.
- Existing `synchronized` blocks around blocking calls are pinning virtual threads — correct.

## Evidence

Profiling under load shows a small number of carrier platform threads, matching the configured scheduler parallelism, almost constantly busy, with virtual thread mount/unmount events far less frequent than expected for the workload's actual blocking-call volume. Code review finds that the legacy request-handling path wraps its downstream HTTP call inside a `synchronized` block guarding a shared, rarely updated configuration cache lookup.

## Investigation Timeline

1. **No throughput improvement observed after migration**, with a measurable regression under peak load specifically.
2. **Executor configuration and downstream-latency hypotheses ruled out**, both matching expected/unchanged baselines.
3. **Load profiling run**, revealing a small, fixed number of carrier threads almost constantly busy — inconsistent with the workload's expected blocking-call volume under true virtual-thread scheduling.
4. **Code review of the request-handling path**, finding a `synchronized` block wrapping the downstream HTTP call.

## Root Cause

The `synchronized` block around the blocking HTTP call pins each virtual thread to its carrier for the call's full duration, serializing effective concurrency down to the small number of configured carrier threads — functionally reproducing the old platform-thread-pool bottleneck, plus the overhead of virtual thread creation with none of its benefit.

## Immediate Mitigation

None available without a code change — the pinning is a structural property of the code path, not a runtime-tunable setting.

## Permanent Fix

Replace the `synchronized` block with a `ReentrantLock`, or restructure to avoid holding any lock across the blocking call at all — for example, by caching the configuration value outside the lock's scope.

## Alternatives Considered

Reverting to the platform-thread pool. Rejected as abandoning the migration's goal entirely rather than fixing the actual, narrow, identifiable cause.

## Trade-offs

Migrating from `synchronized` to `ReentrantLock` requires explicit `lock()`/`unlock()`, or try/finally, discipline that `synchronized`'s block-scoped syntax previously handled automatically. Accepted, since the alternative is a virtual-thread migration that delivers none of its intended benefit.

## Prevention

Any migration to virtual threads should include an explicit audit for `synchronized` blocks, and other pinning-prone constructs, around blocking calls, before assuming a simple executor swap alone will deliver the expected scalability improvement.

## Monitoring and Alerts

- Carrier-thread utilization and virtual-thread mount/unmount event rate as standing metrics post-migration, comparing observed concurrency against the workload's actual blocking-call volume — this is the exact signal that surfaced the pinning here, and it should be reviewed at migration time rather than only after a regression is noticed.
- A JFR (JDK Flight Recorder) pinning event capture enabled for any service running virtual threads, since pinning events are directly observable at the JVM level and don't require inferring the cause from throughput metrics alone.

## Interview Story

This maps to "what's the catch with virtual threads, is there code that actively regresses" — a direct follow-up to the basic virtual-threads pitch. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a migration to virtual threads, expected to significantly improve IO-heavy throughput, instead produced no improvement and a regression under peak load.
- **Task:** find the cause with no compiler error or exception to point at it.
- **Action:** rule out executor misconfiguration and a downstream slowdown; profile carrier-thread utilization directly, finding a small fixed number constantly busy; trace it to a legacy `synchronized` block wrapping the blocking call.
- **Result:** replaced the `synchronized` block with a `ReentrantLock`, removing the pinning and finally realizing the expected throughput improvement.

## Staff-Level Discussion

This incident is the canonical gotcha of adopting virtual threads onto an existing codebase: virtual threads' scalability benefit depends on an invariant — blocking operations don't pin the carrier thread — that `synchronized` silently violates for any code path already using it, with no warning at compile time or even at first glance during code review, since `synchronized` looks identical whether or not it wraps a blocking call. This is a specific instance of a broader migration risk: swapping an underlying execution model (thread pool to virtual threads, one database engine to another, a synchronous call to an async one) can silently interact badly with existing code written against different assumptions, and the interaction is invisible until measured under real load. A Staff engineer leading a virtual-thread migration should treat a `synchronized`-block audit as a mandatory pre-migration step, not a reactive fix applied only after a regression is already in production.

## Related Handbook Chapters

- [Virtual Threads](../handbook/concurrency/virtual-threads.md) — canonical pinning mechanics and the `synchronized`-to-`ReentrantLock` fix used here.
- [Executors and Thread Pool Sizing](../handbook/concurrency/executors-and-thread-pool-sizing.md) — the platform-thread pool model this migration was replacing.
