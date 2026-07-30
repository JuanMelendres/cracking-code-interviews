---
title: "T-406 · Executors & Thread Pool Sizing"
topic_id: T-406
domain: Concurrency
tier: Core
iwi: 7.15
prerequisites: [T-401]
unlocks: [T-410]
week: 9
last_reviewed: 2026-07-29
---

# T-406 · Executors & Thread Pool Sizing

**IWI 7.15 · Core tier**

**Verification note:** both traces in §3 are real, executed output from `practice/java/week-09/executors/src/ExecutorSizingDemo.java`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The unbounded-queue trap, measured](#3-the-unbounded-queue-trap-measured)
4. [Sizing from Little's Law](#4-sizing-from-littles-law)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

A thread pool decouples task submission from task execution: a fixed set of worker threads pulls tasks from a queue. The two decisions that actually matter are the pool size (how many workers) and the queue (what happens to a task that arrives when every worker is busy) — and `Executors.newFixedThreadPool()`'s default answer to the second question is a trap.

## 2. Why it exists

Without pooling, every task would either run on a new thread (unbounded thread creation, real memory/scheduling cost per thread) or block the submitter until a thread is free. Pools bound the first cost; queues decide what happens to the second — and the decision embedded in `newFixedThreadPool` (an unbounded `LinkedBlockingQueue`) quietly trades "tasks never get rejected" for "memory grows without limit under sustained overload."

## 3. The unbounded-queue trap, measured

**Real output**, `Executors.newFixedThreadPool(2)` fed 500 tasks that each take 100ms:

```
== newFixedThreadPool(2): backed by an UNBOUNDED LinkedBlockingQueue ==
200ms after submitting 500 tasks to a 2-thread pool: queue size=496, completed=2, active=2 (every unstarted task sits in memory in the unbounded queue)
after full drain: completed=500 (all 500 eventually ran -- unbounded means no rejection, just unbounded memory growth under sustained overload)
```

200ms in, 496 of 500 tasks are sitting in the queue, unbounded — under sustained overload (producer consistently faster than consumers), this queue grows without limit until the process runs out of memory. There is no backpressure signal to the caller at all; `execute()` always returns immediately regardless of how backed up the pool is.

**Real output**, a `ThreadPoolExecutor` built explicitly with a **bounded** queue and `AbortPolicy`, same 2 workers, 20 tasks submitted:

```
== ThreadPoolExecutor with a BOUNDED queue + AbortPolicy: backpressure, not silent growth ==
submitted 20 tasks to a 2-thread pool with a 5-slot bounded queue: accepted=7 rejected=13
(2 running + 5 queued = 7 can be accepted immediately; the rest are rejected loudly instead of silently piling up)
```

Exactly `corePoolSize + queueCapacity` = 2 + 5 = 7 accepted, matching the arithmetic precisely — the remaining 13 are rejected immediately via `RejectedExecutionException`, giving the caller a chance to apply backpressure (retry, shed load, alert) instead of silently accumulating unbounded memory.

## 4. Sizing from Little's Law

Little's Law: `L = λ × W` (average number of items in a system = arrival rate × average time each item spends in the system). Applied to thread pool sizing: the number of concurrent tasks a system needs to handle in steady-state equals the request rate multiplied by the average time each request takes. For a pool of purely **CPU-bound** tasks, more threads than CPU cores just adds context-switching overhead — size near `N_cores`. For **IO-bound** tasks (blocking on network/disk), threads spend most of their time waiting, not computing, so the useful pool size scales with `N_cores × (1 + waitTime/computeTime)` — a pool handling tasks that are 90% waiting can productively use far more threads than cores, bounded by memory (each platform thread reserves real stack space) rather than CPU. This is exactly the asymmetry virtual threads (§`04-virtual-threads.md`) are designed to eliminate for IO-bound workloads.

## 5. Trade-offs

| Choice | Benefit | Cost |
|---|---|---|
| Unbounded queue (`newFixedThreadPool` default) | Never rejects a task | Unbounded memory growth under sustained overload; no backpressure signal |
| Bounded queue + `AbortPolicy` | Loud, immediate backpressure | Caller must handle `RejectedExecutionException` |
| Bounded queue + `CallerRunsPolicy` | Backpressure that also slows the producer down (runs the rejected task on the calling thread) | Producer thread is now doing worker duty, which caps its own throughput |
| Larger pool for CPU-bound work | — | Diminishing/negative returns past `N_cores`, more context-switch overhead |
| Larger pool for IO-bound work | Higher achievable concurrency for genuinely wait-heavy tasks | Each platform thread costs real memory (~1MB default stack) — caps how far this scales |

## 6. Interview questions

### Q1. Size this pool. Show the arithmetic.

- **Expected answer:** applies Little's Law with a stated request rate and average service time, distinguishing CPU-bound (near `N_cores`) from IO-bound (scales with wait ratio) sizing.
- **Common mistakes:** picking a round number ("8, because that sounds reasonable") without deriving it from actual load characteristics.
- **Follow-up questions:** "What if the workload is a mix of both?"
- **Senior-level expectations:** states the CPU-bound vs IO-bound distinction and picks a formula accordingly.
- **Staff-level expectations:** proposes separating CPU-bound and IO-bound work into different pools entirely, sized independently, rather than one pool trying to serve both profiles.

### Q2. Queue is unbounded and memory is climbing. Why?

- **Expected answer:** the default `newFixedThreadPool`/`newSingleThreadExecutor` queue is an unbounded `LinkedBlockingQueue`; under sustained overload (arrival rate > service rate), tasks accumulate in the queue without limit, each one retained in memory until a worker can process it.
- **Common mistakes:** assuming the fixed thread count itself caps memory usage.
- **Follow-up questions:** "How do you fix it?"
- **Senior-level expectations:** proposes a bounded queue with an explicit rejection policy.
- **Staff-level expectations:** connects the fix to a broader system design point — a queue backing up is a signal the system is overloaded, and the correct response is backpressure/shedding, not "make the queue absorb more."

## 7. Common mistakes

- Using `Executors.newFixedThreadPool()`/`newCachedThreadPool()` in production without understanding their unbounded queue or unbounded thread-creation behavior respectively.
- Sizing a pool by intuition rather than Little's Law and the workload's actual CPU-vs-IO profile.
- Treating "the pool never rejects" as a feature rather than a hidden unbounded-memory liability.

## 8. Staff-level discussion

An unbounded queue is a specific instance of a general anti-pattern: absorbing overload internally instead of surfacing it as backpressure. The same principle governs unbounded in-memory caches, unbounded retry loops, and unbounded connection pools — anywhere a system can silently accept more work than it can sustainably process, it will eventually fail catastrophically (OOM, cascading timeout) rather than degrading predictably. A Staff-level engineer treats every queue, cache, and pool in a design as a place that needs an explicit bound and an explicit policy for what happens when that bound is hit — "unbounded" is rarely actually the intended design, just the unexamined default.

## 9. Summary

`Executors.newFixedThreadPool()`'s unbounded queue means the pool never rejects work, which sounds safe but actually means unbounded memory growth under sustained overload with zero backpressure signal to the caller — measured directly in §3 (496 of 500 tasks queued 200ms after submission). A `ThreadPoolExecutor` built explicitly with a bounded queue and rejection policy converts that silent failure mode into loud, immediate, actionable backpressure. Pool sizing itself should come from Little's Law applied to the workload's actual CPU-vs-IO profile, not intuition.

## 10. Key Takeaways

- `newFixedThreadPool`'s default queue is unbounded — no rejection, unbounded memory growth under overload.
- A bounded queue + explicit rejection policy converts silent overload into loud backpressure.
- CPU-bound pools size near `N_cores`; IO-bound pools scale with the wait/compute ratio.
- Separate CPU-bound and IO-bound work into differently-sized pools rather than one pool serving both.

## 11. Cheat Sheet

| Symptom | Likely cause | Fix |
|---|---|---|
| Memory climbing under load, pool never rejects | Unbounded default queue | Bounded queue + `RejectedExecutionHandler` |
| Pool threads mostly idle, throughput low despite CPU headroom | Pool too small for IO-bound workload | Scale pool with wait/compute ratio, or move to virtual threads |
| Pool threads maxed, CPU pegged, throughput flat or falling | Pool too large for CPU-bound workload | Size down toward `N_cores` |

## 12. Flashcards

1. **Q: What queue does `Executors.newFixedThreadPool()` use by default, and what's the consequence?** A: An unbounded `LinkedBlockingQueue` — tasks are never rejected, so memory grows without limit under sustained overload.
2. **Q: How do you get real backpressure from a thread pool?** A: Build a `ThreadPoolExecutor` with a bounded queue and an explicit `RejectedExecutionHandler` (e.g., `AbortPolicy`).
3. **Q: How should CPU-bound vs IO-bound pool sizing differ?** A: CPU-bound scales near `N_cores`; IO-bound scales with the wait/compute ratio (Little's Law), since threads spend most time blocked, not computing.

(Full week-level deck: `06-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce: `practice/java/week-09/executors/src/ExecutorSizingDemo.java`.
2. Change the bounded-queue demo's `AbortPolicy` to `CallerRunsPolicy` and explain, from the real output, why the total wall time changes even though rejected-vs-accepted counts wouldn't apply the same way.
3. Given a workload spending 90% of its time waiting on a downstream HTTP call and 10% computing, derive a pool size using Little's Law from a stated request rate.

## 14. Additional Reading

- [java.util.concurrent.ThreadPoolExecutor documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html)

## 15. Official References

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444) — the eventual answer to IO-bound pool sizing pain, covered in `04-virtual-threads.md`
