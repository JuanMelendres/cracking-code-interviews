---
title: "Cheat Sheet: Virtual Threads"
slug: virtual-threads
document_type: cheat-sheet
domain: concurrency
topic_id: T-410
canonical: ../handbook/concurrency/virtual-threads.md
last_updated: 2026-08-04
---

# Virtual Threads

**Canonical chapter:** [`handbook/concurrency/virtual-threads.md`](../handbook/concurrency/virtual-threads.md)

## Core Mental Model

A virtual thread's entire value proposition is that blocking becomes cheap — but only if the blocking call actually knows how to unmount. A platform thread ties up a real OS thread (and ~1MB of memory) for the full duration of any blocking call. A virtual thread, when it blocks on a *supported* operation, gets unmounted from its carrier platform thread, freeing that carrier to run something else. The entire chapter is really about one question for any given piece of code: does this blocking call unmount cleanly, or does it pin?

## Essential Definitions

- **Virtual thread** — a JVM-scheduled, cheaply-created thread multiplexed onto a small pool of carrier platform threads. When it blocks on a supported operation (network IO, `Thread.sleep`, blocking queue operations), the JVM unmounts it from its carrier, unlike a traditional platform thread which ties up an OS thread for the blocking call's full duration.
- **Carrier** — the platform thread a virtual thread is mounted onto.
- **Pinning** — blocking inside a `synchronized` block pins the virtual thread to its carrier; the carrier cannot run anything else until the blocking call returns.
- Rationale for existing: platform threads cost roughly 1MB of stack memory each, capping concurrency often in the low thousands.

## Decision Table

| Situation | Guidance |
|---|---|
| IO-bound workload | Virtual-thread-per-task executor |
| CPU-bound workload | Won't help — size the platform pool near `N_cores` |
| Existing `synchronized` around blocking calls | Audit and migrate to `ReentrantLock` |
| Limiting downstream concurrent load | A semaphore/rate limiter, not thread pool size |

## Key Numbers (real, measured — `VirtualThreadScaleDemo.java`, `VirtualThreadPinningDemo.java`)

```
Scale (5,000 tasks, 50ms block each):
  200-platform-thread pool:   1347ms  (theoretical min 50ms)
  virtual-thread-per-task:      75ms  (theoretical min 50ms)
  -> 1347ms vs. 75ms, an 18x difference

Pinning (20 tasks, 200ms block each, carrier parallelism forced to 2):
  synchronized (pins):   2044ms
  ReentrantLock (no pin): 206ms
  -> 2044ms vs. 206ms, roughly a 10x difference
```

## Common Pitfalls

- Expecting virtual threads to speed up CPU-bound work — they don't
- Migrating IO-heavy code without auditing `synchronized` blocks first, and getting hit by a 10x-class regression
- Pooling virtual threads out of habit from platform-thread practice

## Interview Answer Skeleton

**30-sec:** Virtual threads remove the memory ceiling on IO-bound concurrency — measured at an 18x throughput improvement for 5,000 blocking tasks. `synchronized` pins a virtual thread to its carrier, causing a measured ~10x regression versus `ReentrantLock`. They don't help CPU-bound work and shouldn't be pooled.

**2-min:** Add why it exists (platform threads cost ~1MB each, capping concurrency in the low thousands) + the mount/unmount mechanism + the measured 18x scale gain and 10x pinning regression.

**Whiteboard:** Draw the mount/unmount sequence diagram, then a second version with the blocking call inside a `synchronized` box and the unmount arrow crossed out.

**Staff-level framing:** a framework-level change (adopting virtual threads) can silently invalidate an existing codebase's performance characteristics with no compile-time signal — the `synchronized`-to-`ReentrantLock` migration audit is mandatory, not optional. Naming the semaphore/rate-limiter alternative to pooling demonstrates the underlying need (bounding downstream concurrent load) was understood, not just the anti-pattern's name.

## Production Warning Signs

- **Real incident pattern:** a migration to virtual threads produces no improvement, then a measurable regression under peak load. A small number of carrier platform threads stay almost constantly busy, with mount/unmount events far less frequent than expected. Root cause: `synchronized` around a downstream HTTP call pins each virtual thread, serializing concurrency down to the configured carrier count.
- No mitigation available without a code change — permanent fix is replacing `synchronized` with `ReentrantLock` or restructuring to avoid holding a lock across the blocking call. Prevention: audit for `synchronized` before assuming an executor swap alone delivers scalability.

## Related

- [Executors and Thread Pool Sizing](executors-and-thread-pool-sizing.md)
- [Java Memory Model and volatile](java-memory-model-and-volatile.md)
