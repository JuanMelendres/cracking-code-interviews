---
title: "Unbounded Executor Queue Causing an OOM Crash"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/concurrency/executors-and-thread-pool-sizing.md
source: handbook/concurrency/executors-and-thread-pool-sizing.md#production-scenarios
---

# Unbounded Executor Queue Causing an OOM Crash

## Context

A service uses `Executors.newFixedThreadPool(10)` to call a downstream dependency. `newFixedThreadPool` backs its pool with an unbounded `LinkedBlockingQueue` by default.

## Symptoms

The service experiences a sudden `OutOfMemoryError` and crashes during an incident where the downstream dependency itself became slow — not fully down, just slow.

## Impact

A slow, not even failed, dependency cascades into a full crash of the calling service, rather than a bounded, recoverable slowdown.

## Initial Hypotheses

- A memory leak unrelated to the incident — checked and ruled out; heap dumps from the crash show the vast majority of retained memory is queued `Runnable` task objects, not leaked application objects.
- A sudden traffic spike — checked and ruled out; request rate was within normal bounds throughout.
- The unbounded default queue accepting more work than the pool could ever drain — correct.

## Evidence

Heap analysis at crash time shows tens of thousands of queued tasks, each representing an in-flight call to the now-slow downstream dependency, submitted faster than the fixed 10-thread pool could process them once each call's latency increased.

## Investigation Timeline

1. **Crash observed** during an incident where a downstream dependency's latency increased but the dependency itself stayed up.
2. **Memory-leak and traffic-spike hypotheses ruled out** via heap-dump composition and request-rate metrics.
3. **Heap dump examined directly**, revealing the retained memory is almost entirely queued task objects rather than application state.
4. **Arrival-vs-drain-rate mismatch confirmed**: each task's latency rose with the downstream slowdown, and the fixed 10-thread pool's drain rate fell below the submission rate, so the unbounded queue absorbed the difference until heap ran out.

## Root Cause

The fixed thread pool's default unbounded queue accepted every submitted task with no rejection. As the downstream dependency slowed — increasing time-per-task — the arrival rate exceeded the drain rate, and the queue grew without limit until the process ran out of heap.

## Immediate Mitigation

Restart the service to recover, and manually throttle traffic to the slow downstream dependency while it recovers.

## Permanent Fix

Replace the `Executors.newFixedThreadPool()` call with an explicitly configured `ThreadPoolExecutor` using a bounded queue and a `CallerRunsPolicy` — or `AbortPolicy` paired with an explicit retry/circuit-breaker layer — converting a silent, unbounded failure mode into a loud, immediate, actionable one.

## Alternatives Considered

Simply increasing the pool size. Rejected as treating the symptom — a slow, not merely under-provisioned, downstream dependency would still eventually overwhelm any fixed-size pool's drain rate; the actual fix is bounding the queue, not growing the pool.

## Trade-offs

A bounded queue with `AbortPolicy` means some requests are now explicitly rejected during a downstream slowdown, rather than silently queued. Accepted, since the alternative — an eventual OOM crash — is strictly worse for every request, not just the rejected ones.

## Prevention

Any use of `Executors.newFixedThreadPool()`, `newCachedThreadPool()`, or `newSingleThreadExecutor()` in code review should be flagged for its default unbounded-queue (or unbounded-thread-creation) behavior and replaced with an explicitly configured `ThreadPoolExecutor` stating the queue bound and rejection policy deliberately.

## Monitoring and Alerts

- Executor queue depth as a first-class, per-pool metric, alerted on a rising trend well before heap exhaustion — this is the earliest available signal, visible long before an OOM crash and directly explaining the mechanism rather than just its eventual symptom.
- Task submission rate versus completion rate per pool, tracked as a ratio; a ratio sustained above 1 for more than a brief window is the precise, mechanical definition of this failure mode in progress.

## Interview Story

This maps to the "queue is unbounded and memory is climbing, why" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a service crashed with an `OutOfMemoryError` during an incident where a downstream dependency merely slowed down, not failed.
- **Task:** explain how a slow (not down) dependency caused a full crash rather than a graceful slowdown.
- **Action:** rule out a memory leak and a traffic spike using heap-dump composition and request metrics; identify the queued-task-dominated heap; connect the arrival-rate/drain-rate mismatch to the default unbounded queue behind `Executors.newFixedThreadPool()`.
- **Result:** replaced the executor with an explicitly bounded `ThreadPoolExecutor` and a rejection policy, converting a silent unbounded failure into a loud, immediate, and recoverable one.

## Staff-Level Discussion

The `java.util.concurrent.Executors` factory methods are convenient exactly because they hide a configuration decision — queue bound and rejection policy — that should never be implicit for a production service. This incident is the textbook consequence: a fixed-size pool reads as "bounded" from its name alone, but the actual unboundedness lives in the queue behind it, which is easy to miss without reading the factory method's own documentation closely. The organizational fix generalizes past this one service: banning the bare `Executors.newX()` factory methods from production code (via a lint rule) in favor of explicitly configured `ThreadPoolExecutor` instances forces every future pool's queue-bound decision to be conscious rather than accidental, at the point of creation rather than discovered during an outage.

## Related Handbook Chapters

- [Executors and Thread Pool Sizing](../handbook/concurrency/executors-and-thread-pool-sizing.md) — canonical pool-sizing, queueing, and rejection-policy mechanics used here.
