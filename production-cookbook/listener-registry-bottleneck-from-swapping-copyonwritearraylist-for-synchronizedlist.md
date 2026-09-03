---
title: "Listener Registry Bottleneck from Swapping CopyOnWriteArrayList for synchronizedList"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md
source: handbook/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md#production-scenarios
---

# Listener Registry Bottleneck from Swapping CopyOnWriteArrayList for synchronizedList

## Context

A service's event-listener registry, originally a `CopyOnWriteArrayList<Listener>` (registered rarely at startup, iterated on every event — thousands of times per second), is changed to `Collections.synchronizedList(new ArrayList<>())` during a refactor aimed at "more standard" synchronization.

## Symptoms

Afterward, event-dispatch latency increases measurably, and profiling shows significant time spent in lock contention on the listener list specifically.

## Impact

A measurable throughput regression on the hot event-dispatch path, introduced by a change intended to be a stylistic/consistency improvement rather than a functional one.

## Initial Hypotheses

- A regression in the listeners' own logic — checked, and ruled out: listener implementations are unchanged.
- Increased event volume — checked, and ruled out: traffic is flat across the change.
- The synchronization mechanism itself is now the bottleneck — correct.

## Evidence

Profiling shows measurable time spent acquiring the `synchronizedList`'s shared intrinsic lock on every single event-dispatch iteration.

## Investigation Timeline

1. **Event-dispatch latency increase observed** immediately following the refactor from `CopyOnWriteArrayList` to `Collections.synchronizedList(new ArrayList<>())`.
2. **Listener implementations reviewed** and confirmed unchanged — the regression is not caused by any individual listener's own logic.
3. **Event traffic volume checked** and confirmed flat across the change — the regression is not explained by increased load.
4. **Profiling attributes measurable time to lock acquisition** on the `synchronizedList`'s shared intrinsic lock, occurring on every single event-dispatch iteration.
5. **Access-pattern mismatch confirmed as the cause** — the listener registry is read (iterated) thousands of times per second and written to only rarely, at startup; `synchronizedList` requires every read to acquire the same lock as every write, while `CopyOnWriteArrayList` allowed reads to proceed lock-free.

## Root Cause

The refactor swapped a genuinely lock-free-read collection for one requiring lock acquisition on every read, for a workload (thousands of reads per second, listener registration only at startup) that is exactly `CopyOnWriteArrayList`'s intended profile — the "more standard" choice was actually the wrong one for this specific access pattern.

## Immediate Mitigation

Revert the listener registry back to `CopyOnWriteArrayList`, immediately restoring lock-free dispatch-path reads.

## Permanent Fix

Document, at the collection's declaration site, *why* `CopyOnWriteArrayList` is used here specifically (read-heavy, write-rare access pattern) so a future refactor doesn't repeat the same mistaken "more standard" substitution without understanding the original trade-off reasoning.

## Alternatives Considered

`ConcurrentHashMap`-backed or other lock-free structures — unnecessary here, since the access pattern (iterate all listeners on every event, add/remove rarely) is exactly what `CopyOnWriteArrayList` is built for.

## Trade-offs

None new — this is simply reverting to the collection whose trade-off (rare O(n) write cost, free lock-free reads) actually matches the workload.

## Prevention

Any change to a collection's concurrency strategy on a hot path should require re-measuring, not just "this one is more standard."

## Monitoring and Alerts

- Add a comment (and, where feasible, a small unit test asserting the concrete collection type) at any collection declaration whose type was chosen deliberately for a specific access-pattern trade-off, so a future refactor sees the reasoning before making a "more standard" substitution.
- Track dispatch-path latency (p50/p99) for any hot event/listener-iteration path as a standing metric, so a concurrency-strategy change that regresses it is caught immediately in a canary or staged rollout rather than discovered later via a general profiling pass.
- Require a before/after throughput or latency measurement as part of code review for any pull request that changes a collection type on a documented hot path, specifically to catch exactly this class of well-intentioned but workload-mismatched substitution.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a service's event-dispatch latency regressed measurably after a refactor swapped its listener registry from `CopyOnWriteArrayList` to `Collections.synchronizedList`, intended as a "more standard" synchronization choice.
- **Task:** find why a change with no intended functional impact produced a real throughput regression on a hot path.
- **Action:** ruled out a listener-logic regression and increased traffic, then profiled directly and found lock contention on every single dispatch iteration, tracing it to the mismatch between the registry's actual read-heavy, write-rare access pattern and the newly-introduced lock-per-read collection.
- **Result:** reverted to `CopyOnWriteArrayList`, immediately restoring lock-free reads, and documented the reasoning at the declaration site so a future "consistency" refactor doesn't repeat the mistake.

## Staff-Level Discussion

This incident is a clean example of "more standard" not meaning "more correct" — `Collections.synchronizedList()` is indeed the more commonly-reached-for general-purpose thread-safe list, which is precisely why a refactor motivated by consistency rather than measurement chose it here, without recognizing that the original `CopyOnWriteArrayList` choice was not an oversight but a deliberate trade-off matched to this specific, unusually read-heavy access pattern. The organizational risk this surfaces is that a collection type chosen deliberately for a non-obvious reason is indistinguishable, in the source code alone, from one chosen carelessly or by habit — nothing in `CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();` visibly communicates "this was measured and matters." A Staff engineer's response should be twofold: require the reasoning to be documented at the point of a deliberate, non-default choice (a comment referencing the measured trade-off, exactly as this incident's permanent fix does), and treat any pull request that changes a collection type on a documented or suspected hot path as requiring a before/after measurement rather than being approved on code-style grounds alone. This generalizes well beyond `CopyOnWriteArrayList` specifically — any time a codebase makes a deliberately "unusual" choice for a measured reason, that reasoning needs to survive the next well-intentioned engineer who doesn't yet know it was deliberate.

## Related Handbook Chapters

- [CopyOnWriteArrayList and Copy-on-Write Trade-offs](../syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md) — canonical cost model and the measured ~44x lock-free-read advantage this incident's regression traces back to.
- [Fail-Fast vs. Weakly-Consistent Iterators](../syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md) — related snapshot-isolation guarantee `CopyOnWriteArrayList`'s iterator provides that `synchronizedList` does not.
