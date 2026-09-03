---
title: "Cheat Sheet: Deadlock, Race Conditions, and Thread Diagnostics"
slug: deadlock-race-conditions-and-thread-diagnostics
document_type: cheat-sheet
domain: concurrency
topic_id: T-409
canonical: ../handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md
last_updated: 2026-08-04
---

# Deadlock, Race Conditions, and Thread Diagnostics

**Canonical chapter:** [`syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md`](../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md)

## Core Mental Model

Deadlock, livelock, starvation, and a plain race condition are four different answers to "what happens when threads share state badly" — and each has its own diagnostic fingerprint. Deadlock: permanently stuck, nothing moves. Livelock: threads are busy, actively responding to each other, but nothing ever finishes. Starvation: one thread never gets its turn under an unfair scheduler. Race condition: the outcome depends on timing, not on threads getting stuck at all. Treating these as one vague "concurrency bug" category is what makes them hard to diagnose; naming which one you're looking at tells you exactly which tool to reach for.

## Essential Definitions

- **Deadlock** — a cycle of threads each waiting on a lock the next one holds (permanent stall).
- **Livelock** — threads actively responding to each other but making no progress.
- **Starvation** — a thread perpetually denied a resource by unfair scheduling.
- **Race condition** — any outcome that depends on unlucky timing of unsynchronized access; deadlock is a race condition's more dramatic cousin, not a separate category.

## Decision Table

| Failure mode | Threads' apparent state | Fixed by |
|---|---|---|
| Deadlock | Stuck, `BLOCKED`, zero CPU | Consistent lock ordering |
| Livelock | Active, consuming CPU, no progress | Randomized backoff or escalating priority/give-up rule |
| Starvation | One thread perpetually denied | Fair scheduling or redesign |
| Race condition | No stalling — incorrect result | Atomic classes or `synchronized` |

| Symptom | Diagnostic | Fix |
|---|---|---|
| Threads permanently stuck, CPU idle | `ThreadMXBean.findDeadlockedThreads()` / `jstack` | Consistent lock-acquisition ordering |
| Counter/metric undercounting under load | Code review for `count++`-style ops | `AtomicInteger`/`AtomicLong`/`LongAdder` |
| Thread stuck in `WAITING` forever | Missed `notify()`/`notifyAll()` | Ensure every `wait()` has a matching, reachable `notify()` |

## Key Numbers (real, executed — `DeadlockDemo.java`, `RaceConditionDemo.java`)

```
Deadlock trace: thread-1-A-then-B and thread-2-B-then-A, both BLOCKED
  ThreadMXBean names the specific held/wanted lock hashes directly

Race condition: 10 threads x 100,000 increments each (expected 1,000,000)
  Plain int ++ (unsynchronized):  actual=161,906  lost=838,094  (83.8% lost)
  AtomicInteger.incrementAndGet(): actual=1,000,000  lost=0
```

## Common Pitfalls

- Believing the thread lifecycle has a distinct "Running" state separate from `RUNNABLE`, or forgetting `TIMED_WAITING` exists
- Assuming a race condition is a rare, unlucky-timing edge case rather than something that reliably manifests under real concurrent load (measured 83.8% loss above, not 0.1%)
- Debugging a suspected deadlock by reading logs rather than pulling an actual thread dump / using `ThreadMXBean`

## Interview Answer Skeleton

**30-sec:** Deadlock, livelock, starvation, and race conditions are four distinct failure modes with different fingerprints and different fixes. Deadlock is diagnosed via `ThreadMXBean.findDeadlockedThreads()` (what `jstack` uses) and prevented structurally with consistent lock-acquisition ordering. Race conditions from unsynchronized compound operations are not rare — measured at 83.8% lost updates under realistic load, fully fixed by `AtomicInteger`.

**2-min:** Add why the taxonomy matters (different tools for different fingerprints) + the lock-ordering trade-off (not compiler-enforced) + the measured 838,094-of-1,000,000-lost production example.

**Whiteboard:** Draw the two-node lock-cycle diagram with crossing "wants" arrows; annotate that this is the exact graph `findDeadlockedThreads()` walks.

**Staff-level framing:** deadlock and the 83.8%-lost-update measurement are both instances of the same lesson — concurrent bugs are not rare edge cases; under real load they manifest reliably and severely. Treat "this shared-state code has no explicit synchronization strategy" as a near-certain future incident, not a maybe.

## Production Warning Signs

- **Real incident pattern:** roughly once a week, under peak load, a service's request-handling threads all become unresponsive simultaneously — CPU drops to near-zero despite a full queue, and the process must be restarted to recover. `jstack`/`ThreadMXBean.findDeadlockedThreads()` confirms two threads `BLOCKED` in a two-lock cycle: the normal request-flow path and the admin/audit-flow path each acquire a user-record lock and audit-log lock in opposite order.
- Fix: consistent lock order (user-record lock before audit-log lock) — low load rarely triggers the cycle, peak load raises the probability to roughly weekly. A lock-acquisition timeout alone is not sufficient.

## Related

- [Java Memory Model and volatile](java-memory-model-and-volatile.md)
- [ConcurrentHashMap Internals](concurrenthashmap-internals.md)
- [Executors and Thread Pool Sizing](executors-and-thread-pool-sizing.md)
