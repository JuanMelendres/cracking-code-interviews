---
title: "Cheat Sheet: Java Concurrency Fundamentals"
slug: java-concurrency-fundamentals-threads-races-and-synchronization
document_type: cheat-sheet
domain: 02-java
topic_id: T-2214
canonical: ../syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md
last_updated: 2026-09-30
---

# Java Concurrency Fundamentals

**Canonical chapter:** [`syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md`](../syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md)

## Core Mental Model

A thread is a path of execution. Two threads with separate data cannot interfere. **The moment they touch the same object, everything in this sheet applies.**

## Starting One

```java
Runnable job = () -> System.out.println("work");
Thread t = new Thread(job, "worker");
t.start();   // NEW THREAD
t.run();     // ordinary method call on the CURRENT thread -- silent bug
t.join();    // blocks the CALLING thread until t finishes
```

Finishing order ≠ starting order. Six threads, four runs: `3 0 1 4 2 5` / `3 4 0 1 2 5` / `0 3 1 4 2 5` / `0 3 4 1 5 2`.

## The Race Condition (measured)

8 threads × 100,000 increments, 800,000 expected:

```
plain int, counter++          229,765 / 800,000   <- ~70% LOST
volatile int, counter++       177,394 / 800,000   <- STILL LOST
synchronized block            800,000 / 800,000   correct
AtomicInteger                 800,000 / 800,000   correct
```

**`counter++` is three operations:** read, add, write. Two threads both read 41, both write 42 → two increments, one change. Nothing throws.

## `volatile`: Visibility, NOT Atomicity

The half people get wrong ↑. The half it actually solves ↓:

```
plain boolean flag : NEVER SAW THE WRITE (323,522,386 spins, hit the 3s cap)
volatile flag      : exited 0 ms after the write
```

The JIT may hoist a non-volatile read out of a loop. It did.

> **A `volatile` flag is correct. A `volatile` counter is not.**

## Choosing

| Tool | Guarantees | Use when |
|---|---|---|
| `volatile` | Visibility only | One writer sets a flag/reference, others read. **No read-modify-write** |
| `synchronized` | Atomicity + visibility, over a **block** | Several fields must change **together** |
| `AtomicInteger` | Atomicity + visibility, **one** variable | A single counter or reference |

**Pick by what must be atomic *together*, not by speed.** Two atomics on two fields = each atomic, the pair still observable half-updated.

```java
private volatile boolean shutdownRequested;          // flag: volatile is right
private final AtomicInteger processed = new AtomicInteger();  // counter: atomic
synchronized (lock) { balance += x; txCount++; }     // two fields: lock
```

## Cost (8 threads × 200k contended, warmed)

```
synchronized : 63 ms
AtomicInteger: 50 ms
```

Modest. **Not the basis for the decision.** What matters far more is *how long you hold it* — a lock held across an I/O call serializes everyone behind a network round trip.

## Common Pitfalls

- `run()` instead of `start()` — sequential code that looks concurrent, no error.
- Believing `volatile` fixes `counter++`.
- Synchronizing on a **mutable** field, or on a `String` literal / boxed type (interned, shared with unrelated code).
- Sharing `ArrayList`/`HashMap` across threads. `ConcurrentHashMap` is the safe one.
- Depending on finishing order.
- Swallowing `InterruptedException` — restore with `Thread.currentThread().interrupt()`.
- `Thread.sleep` inside `synchronized` — it does **not** release the lock.
- Trusting a passing test. At 10 iterations the plain counter is usually correct; at 100,000 it loses 70%.

## Prefer, In Order

1. **No shared mutable state** — partition the work, or make it immutable.
2. `java.util.concurrent` — `ConcurrentHashMap`, `ExecutorService`, `BlockingQueue`.
3. Hand-rolled `synchronized` — understand it, rarely write it.

## Interview Answer Skeleton

**30-sec:** A thread is a path of execution; `start()` creates one, `run()` does not. `counter++` is read-add-write, so two threads both read 41 and both write 42. `synchronized` and `AtomicInteger` fix that; `volatile` does not — it fixes visibility, not atomicity.

**2-min:** Add the numbers (800,000 expected, ~230,000 plain, ~177,000 volatile), then the visibility contrast (323M spins never seeing the write), then the choice rule: what must be atomic *together*.

**Whiteboard:** One box `counter = 41`, two parallel thread arrows, three ticks each (`read 41`, `+1`, `write 42`), interleaved. Write `42` in the box. "Two increments, one change."

## Related

- [The Java Memory Model and volatile](../syllabus/02-java/concurrency/java-memory-model-and-volatile.md)
- [Deadlock, Race Conditions, and Thread Diagnostics](../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md)
- [Atomics, CAS, and the ABA Problem](../syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md)
- [Executors and Thread Pool Sizing](../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md)
- [`practice/java/concurrency/concurrency-fundamentals/`](../practice/java/concurrency/concurrency-fundamentals/README.md)
