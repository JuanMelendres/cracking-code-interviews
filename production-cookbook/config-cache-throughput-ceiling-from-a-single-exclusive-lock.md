---
title: "Config Cache Throughput Ceiling from a Single Exclusive Lock"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md
source: handbook/concurrency/reentrantlock-readwritelock-and-stampedlock.md#production-scenarios
---

# Config Cache Throughput Ceiling from a Single Exclusive Lock

## Context

A service caches a rarely-updated configuration object (refreshed every few minutes by a background thread) behind a plain `synchronized` block guarding both reads and writes.

## Symptoms

Under load, request throughput plateaus well below what CPU utilization suggests should be possible — CPUs are mostly idle, but request latency climbs with concurrency.

## Impact

The service can't scale request throughput with added capacity, because nearly every request serializes behind the same lock to read a value that changes only once every few minutes.

## Initial Hypotheses

- A downstream dependency bottleneck — checked, and ruled out: the config read never leaves the process.
- GC pauses — checked, and ruled out: GC logs show nothing unusual.
- Lock contention on the config read path — correct, confirmed via thread-dump sampling showing most request threads `BLOCKED` on the same monitor.

## Evidence

Thread dumps taken during a load test show dozens of request-handling threads simultaneously `BLOCKED`, all waiting on the exact same `synchronized` block guarding the config object — for a read-only access, on a value that changes on the order of minutes, not requests.

## Investigation Timeline

1. **Throughput plateau observed** under load, with CPU utilization suggesting significant unused headroom rather than genuine saturation.
2. **Downstream dependency bottleneck ruled out** — the config read is confirmed to never leave the process, eliminating any external service or network cause.
3. **GC pauses ruled out** — GC logs for the affected window show nothing unusual, eliminating garbage collection as the throughput ceiling's cause.
4. **Thread-dump sampling performed under load**, revealing dozens of request-handling threads simultaneously in the `BLOCKED` state.
5. **Blocking point identified precisely** — every `BLOCKED` thread is waiting on the identical `synchronized` block guarding the configuration object, confirming the config-read path itself as the actual bottleneck.

## Root Cause

A plain mutual-exclusion lock serializes every acquirer regardless of read/write intent, capping concurrent reads at 1 regardless of available CPU capacity — for a value read constantly and written only once every few minutes, this forces nearly every request to wait its turn behind every other request purely to read an unchanging value.

## Immediate Mitigation

None available without a code change — this is a structural bottleneck, not a transient condition.

## Permanent Fix

Replace the `synchronized` block with a `ReentrantReadWriteLock`: request threads take the (shared) read lock; the background refresh thread takes the (exclusive) write lock. Request throughput on the config-read path is no longer capped at 1 concurrent holder.

## Alternatives Considered

`StampedLock`'s optimistic-read path — considered as a further optimization once the read/write split alone already resolves the throughput bottleneck; not necessary unless profiling shows the read-lock's own bookkeeping (not the serialization) is still the limiting factor.

## Trade-offs

`ReadWriteLock` adds a small amount of bookkeeping overhead per acquisition compared to `synchronized` — accepted, since it's dwarfed by the measured throughput gain from allowing concurrent reads.

## Prevention

Any lock guarding a read-heavy, infrequently-written shared value should default to `ReadWriteLock` (or `StampedLock` for extreme read-to-write ratios), not a plain mutual-exclusion lock — code review should flag `synchronized`/`ReentrantLock` guarding a value with an obvious read/write access-pattern asymmetry.

## Monitoring and Alerts

- Add thread-state sampling (a standing, low-overhead JFR or thread-dump-based profile taken periodically, not only during an active incident) so a high proportion of threads in `BLOCKED` state on any single monitor is visible on a dashboard before it manifests as a full throughput plateau.
- Track "CPU utilization versus request throughput" as a joint metric; a growing gap between available CPU headroom and actual achieved throughput under increasing concurrency is the direct signature of a lock-serialization bottleneck and should trigger investigation before the gap becomes severe.
- After migrating to `ReadWriteLock`, monitor read-lock versus write-lock acquisition counts and hold durations separately, confirming the access pattern actually matches the read-heavy assumption that motivated the change — a workload that turns out to be more write-heavy than assumed may need a different structure entirely.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a service's request throughput plateaued well below what available CPU capacity suggested should be possible, despite no obvious downstream bottleneck or GC issue.
- **Task:** find the actual constraint limiting throughput when the usual suspects (external dependencies, garbage collection) were both ruled out.
- **Action:** captured thread dumps under load and found dozens of request threads simultaneously blocked on the identical `synchronized` block guarding a configuration object that changes only once every few minutes.
- **Result:** replaced the exclusive lock with a `ReentrantReadWriteLock`, allowing concurrent reads while preserving exclusive access for the rare background refresh, removing the artificial cap on concurrent throughput.

## Staff-Level Discussion

This incident is a good example of a bottleneck that is invisible from ordinary application metrics (request latency, error rate, CPU) in isolation, but obvious the moment thread state is actually sampled — the mismatch between "CPU looks idle" and "throughput is capped" is a specific, learnable signature of lock contention rather than compute or I/O saturation, and a Staff engineer should recognize that signature quickly enough to go straight to thread dumps rather than continuing to investigate downstream dependencies or GC. The deeper architectural point is that `synchronized` (and any plain mutual-exclusion lock) is a poor default for any shared value whose access pattern is asymmetric — read far more often than written — because it makes no distinction between the two, and that asymmetry is usually knowable at design time rather than something that needs to be discovered via a production throughput ceiling. This generalizes into a standing design-review heuristic: any shared, mutable, lock-guarded value should have its actual read/write ratio considered explicitly, with `ReadWriteLock` or `StampedLock` as the default choice whenever reads dominate, rather than reaching for `synchronized` out of habit and only reconsidering after a measured production bottleneck forces the question.

## Related Handbook Chapters

- [ReentrantLock, ReadWriteLock, and StampedLock](../syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md) — canonical mechanics of the read/write split and the measured concurrent-read-overlap evidence this fix relies on.
- [Atomics, CAS, and the ABA Problem](../syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md) — related lock-free alternative worth considering for sufficiently simple shared-state access patterns.
