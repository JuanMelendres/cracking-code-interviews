---
title: "Cheat Sheet: Executors and Thread Pool Sizing"
slug: executors-and-thread-pool-sizing
document_type: cheat-sheet
domain: concurrency
topic_id: T-406
canonical: ../handbook/concurrency/executors-and-thread-pool-sizing.md
last_updated: 2026-08-04
---

# Executors and Thread Pool Sizing

**Canonical chapter:** [`syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md`](../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md)

## Core Mental Model

A thread pool has two independent decisions, and most engineers only ever tune one of them. Pool size controls concurrent execution; the queue controls what happens when a task arrives and every worker is busy — and this second decision is the one `Executors.newFixedThreadPool()` makes silently, in the direction that feels safest (never reject) but is actually the most dangerous (unbounded memory growth with zero warning).

## Essential Definitions

- **Thread pool** — decouples task submission from task execution: a fixed set of worker threads pulls tasks from a queue. The two decisions that matter: pool size (how many workers) and the queue (what happens to a task arriving when every worker is busy).
- **Little's Law** — `L = λ × W`: the average number of items in a system equals the arrival rate times the average time each item spends in the system.
- **CPU-bound sizing** — more threads than CPU cores just adds context-switching overhead; size near `N_cores`.
- **IO-bound sizing** — threads spend most of their time waiting, not computing, so useful pool size scales with `N_cores × (1 + waitTime/computeTime)`, bounded by memory (each platform thread reserves real stack space) rather than CPU.

## Decision Table

| Choice | Benefit | Cost |
|---|---|---|
| Unbounded queue (`newFixedThreadPool` default) | Never rejects a task | Unbounded memory growth under sustained overload; no backpressure signal |
| Bounded queue + `AbortPolicy` | Loud, immediate backpressure | Caller must handle `RejectedExecutionException` |
| Bounded queue + `CallerRunsPolicy` | Backpressure that also slows the producer (runs the rejected task on the calling thread) | Producer thread now doing worker duty, capping its own throughput |
| Larger pool, CPU-bound work | — | Diminishing/negative returns past `N_cores`, more context-switch overhead |
| Larger pool, IO-bound work | Higher achievable concurrency for wait-heavy tasks | Each platform thread costs real memory (~1MB default stack) |

| Symptom | Likely cause | Fix |
|---|---|---|
| Memory climbing under load, pool never rejects | Unbounded default queue | Bounded queue + `RejectedExecutionHandler` |
| Threads mostly idle, throughput low despite CPU headroom | Pool too small for IO-bound workload | Scale pool with wait/compute ratio, or move to virtual threads |
| Threads maxed, CPU pegged, throughput flat/falling | Pool too large for CPU-bound workload | Size down toward `N_cores` |

## Key Numbers (real, executed — `ExecutorSizingDemo.java`)

```
Unbounded queue: newFixedThreadPool(2), 500 tasks @ 100ms each
  200ms after submission: queue size=496, completed=2, active=2
  after full drain: completed=500

Bounded queue: 2 workers, 5-slot queue, AbortPolicy, 20 tasks submitted
  accepted=7 rejected=13   (corePoolSize + queueCapacity = 2 + 5 = 7)
```

## Common Pitfalls

- Using `Executors.newFixedThreadPool()`/`newCachedThreadPool()` in production without understanding their unbounded-queue or unbounded-thread-creation behavior
- Sizing a pool by intuition rather than Little's Law and the workload's actual CPU-vs-IO profile
- Treating "the pool never rejects" as a feature rather than a hidden unbounded-memory liability

## Interview Answer Skeleton

**30-sec:** `Executors.newFixedThreadPool()`'s default unbounded queue is a hidden liability, not a safety feature. Use `ThreadPoolExecutor` directly with a bounded queue and an explicit rejection policy. Size CPU-bound pools near `N_cores`; size IO-bound pools with `N_cores × (1 + wait/compute)`.

**2-min:** Add why it exists (decouple submission from execution) + the measured 496/500-queued trace vs. the 7-accepted/13-rejected bounded trace + the `CallerRunsPolicy` trade-off.

**Whiteboard:** Draw the submission-queue-worker flowchart, circle the unbounded branch, annotate "this is the silent failure mode."

**Staff-level framing:** the unbounded queue is a specific instance of a general anti-pattern — absorbing overload internally instead of surfacing it as backpressure — the same pattern shows up in unbounded in-memory caches, unbounded retry loops, and unbounded connection pools.

## Production Warning Signs

- **Real incident pattern:** a service using `Executors.newFixedThreadPool(10)` hits `OutOfMemoryError` and crashes when a downstream dependency becomes slow (not down). Heap dumps show retained memory is tens of thousands of queued `Runnable` objects, not leaked application objects — request rate was normal, only per-task latency rose.
- Fix: replace with an explicit `ThreadPoolExecutor` using a bounded queue + `CallerRunsPolicy` (or `AbortPolicy` + retry/circuit-breaker). Rejecting some requests during a slowdown beats an OOM crash for all of them. Flag any bare `Executors.newXxxThreadPool()` call in code review.

## Related

- [Java Memory Model and volatile](java-memory-model-and-volatile.md)
- `syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md`
- `syllabus/02-java/concurrency/virtual-threads.md`
