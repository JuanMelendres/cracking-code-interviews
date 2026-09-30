---
title: "Java Concurrency Fundamentals: Threads, Races, and Synchronization"
slug: java-concurrency-fundamentals-threads-races-and-synchronization
document_type: syllabus-topic
domain: 02-java
topic_id: T-2214
status: draft
version: 1.0
last_updated: 2026-09-30
mastery_levels_covered: [L1, L2]
prerequisites:
  - ../language-core/java-oop-fundamentals-classes-objects-and-interfaces.md
  - ../language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md
related:
  - java-memory-model-and-volatile.md
  - deadlock-race-conditions-and-thread-diagnostics.md
  - executors-and-thread-pool-sizing.md
  - atomics-cas-and-the-aba-problem.md
  - virtual-threads.md
  - ../../01-computer-science-foundations/os-process-thread-model.md
practice: ../../../practice/java/concurrency/concurrency-fundamentals/
production_scenarios: []
interview_paths: [junior-to-mid, interview-emergency-sprint]
official_references:
  - https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/package-summary.html
---

# Java Concurrency Fundamentals: Threads, Races, and Synchronization

> **Topic register:** T-2214 · Junior Fundamentals · High interview frequency [H]
> **Provenance:** every number below is real, executed output from
> [`practice/java/concurrency/concurrency-fundamentals/`](../../../practice/java/concurrency/concurrency-fundamentals/README.md)
> on OpenJDK 21.0.12 with 10 cores. Where a result varies between runs, the
> range across five runs is given rather than a single number.

## Table of Contents

1. [Why This Matters](#1-why-this-matters)
2. [Prerequisites](#2-prerequisites)
3. [Foundation (L1)](#3-foundation-l1)
4. [Core Concepts (L2)](#4-core-concepts-l2)
5. [How It Works Internally (L3)](#5-how-it-works-internally-l3)
6. [Practical Usage](#6-practical-usage)
7. [Examples](#7-examples)
8. [Common Mistakes](#8-common-mistakes)
9. [Edge Cases](#9-edge-cases)
10. [Performance Implications](#10-performance-implications)
11. [Trade-offs](#11-trade-offs)
12. [Senior-Level Considerations (L3)](#12-senior-level-considerations-l3)
13. [Staff/System-Level Considerations (L4)](#13-staffsystem-level-considerations-l4)
14. [Production Scenarios](#14-production-scenarios)
15. [Interview Questions](#15-interview-questions)
16. [Coding/Practice Exercises](#16-codingpractice-exercises)
17. [Debugging Exercises](#17-debugging-exercises)
18. [Design Exercises](#18-design-exercises)
19. [Further Reading](#19-further-reading)
20. [Mastery Checklist](#20-mastery-checklist)

---

## 1. Why This Matters

"Tell me about concurrency in Java" is asked in almost every backend screening round, and it is asked early, because the answer separates people who have written a `main` method from people who have written a service. Every web request in a traditional Spring application runs on its own thread; every `@Async` method, every scheduled job, and every Kafka listener does too. Shared state is therefore the default condition, not an advanced topic.

This chapter is the on-ramp. `02-java/concurrency` holds fifteen chapters, all of them excellent and all of them written for Senior and Staff readers — the Java Memory Model, compare-and-swap, `VarHandle`, structured concurrency, work-stealing. None of them starts from "what is a thread and how do I make one." This one does, and then hands off to them by name.

The specific payoff for an interview: the three-way distinction between `synchronized`, `volatile`, and `AtomicInteger` is the most commonly asked and most commonly fumbled question in this area, and [Section 4](#4-core-concepts-l2) settles it with two measured numbers on adjacent lines rather than an argument.

## 2. Prerequisites

- [Java OOP Fundamentals](../language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) — you need classes, objects, and interfaces, because a `Runnable` is an interface and a lambda is how you implement it concisely.
- [Java Syntax Fundamentals](../language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) — variables, loops, and methods.
- Helpful but not required: [OS Process and Thread Model](../../01-computer-science-foundations/os-process-thread-model.md), which covers what a thread is at the operating-system level. This chapter covers what it is in Java.

## 3. Foundation (L1)

**A thread is a path of execution through your program.** Ordinary Java code runs on one — the one called `main`. Starting a second thread means two paths are running at the same time, each with its own position in the code and its own local variables, but sharing the same objects on the heap.

That last clause is the whole subject. Two threads with entirely separate data cannot interfere with each other. The moment they touch the same object, the questions in this chapter appear.

**Creating one.** Three forms, all producing the same thing:

```java
// (a) Subclass Thread -- rarely the right choice, since you are inheriting
// when you only need to supply behaviour.
Thread a = new Thread() {
    @Override public void run() { System.out.println("hello from a"); }
};

// (b) Implement Runnable and hand it to a Thread. A Runnable is just "some
// code to run" -- it has no thread of its own.
Runnable job = () -> System.out.println("hello from b");
Thread b = new Thread(job, "worker-b");

// (c) The same thing inline.
Thread c = new Thread(() -> System.out.println("hello from c"), "worker-c");

a.start();
b.start();
c.start();
```

**`start()` creates a thread. `run()` does not.** This is the first real trap, and it is silent:

```text
calling d.run()   ->     ran on: main
calling d.start() ->     ran on: Thread-1
```

`run()` is an ordinary method call. Code that calls it instead of `start()` is completely sequential while looking concurrent, and nothing reports an error.

**`join()` waits.** `t.join()` blocks the *calling* thread until `t` finishes. Without it, `main` can reach the end of the program before a worker has printed anything.

**Finishing order is not starting order.** Six threads released together, four consecutive runs of the same program:

```text
3 0 1 4 2 5
3 4 0 1 2 5
0 3 1 4 2 5
0 3 4 1 5 2
```

Nothing is broken. Which thread runs when is the operating system's decision, and code that depends on a particular order is relying on something nobody guaranteed.

## 4. Core Concepts (L2)

### A race condition, measured

Eight threads each incrementing the same `int` one hundred thousand times. Expected: 800,000.

```text
plain int, counter++                 229,765 /   800,000   lost 570,235  <- UPDATES LOST
volatile int, counter++              177,394 /   800,000   lost 622,606  <- STILL LOST
synchronized block                   800,000 /   800,000   lost       0  correct
AtomicInteger.incrementAndGet        800,000 /   800,000   lost       0  correct
```

Roughly **70–85% of the increments vanish**. Across six runs the plain counter landed between 132,587 and 229,765.

**Why.** `counter++` looks like one operation and is three: read the current value, add one, write it back. Two threads can both read 41, both compute 42, and both write 42 — two increments, one net change. Nothing throws; the number is simply wrong.

**A race condition is exactly this**: the result depends on timing rather than on the code. That is what makes it dangerous — it passes tests, passes review, and fails under load.

### `volatile` does not make `counter++` safe

This is the most commonly fumbled point in the whole topic, and the second line of the table above settles it: the volatile counter lost 622,606 updates.

Across six runs the plain counter ranged 132,587–229,765 and the volatile counter 135,457–195,568. The two ranges overlap and their relative order flips between runs — the volatile counter came out *higher* than the plain one in two of the six — so the honest claim is that **`volatile` does not help here**, not that it is worse.

`volatile` guarantees every read sees the most recent write. It does nothing to stop a second thread interleaving *between* your read and your write. The gap is still there.

### `volatile` solves a different problem: visibility

A reader thread spins until a flag flips; another thread sets it 200 ms later.

```text
plain boolean flag : reader exited 2,794 ms after the write  <- NEVER SAW IT (hit the 3s cap)
volatile flag      : reader exited 0 ms after the write
(spins recorded by the plain reader: 323,522,386)
```

The non-volatile reader **never observed the write at all**. It spun 323 million times and exited only because the demo caps it at three seconds. This reproduced identically on every run. The JIT compiler is entitled to hoist a non-volatile read out of a loop — the language never promised the reader would see another thread's write — and here it did exactly that.

So the rule, in one line each:

- **`volatile` fixes visibility, not atomicity.** Use it for a flag one thread writes and others read.
- **`synchronized` and `AtomicInteger` fix atomicity** (and, as a consequence, visibility too).

A `volatile` flag is correct. A `volatile` counter is not.

### Three correct tools, and how to choose

| Tool | Guarantees | Use when |
|---|---|---|
| `volatile` | Visibility only | One thread writes a flag or a reference, others read it. No read-modify-write. |
| `synchronized` | Atomicity + visibility, over a block | Several fields must change **together**, or the operation is more than one step |
| `AtomicInteger` and friends | Atomicity + visibility, on **one** variable | A single counter or reference, updated independently |

The choice is about **what must be atomic together**, not about speed. A lock protecting two related fields is not replaceable by two atomics — each would be individually atomic and the pair would still be observable half-updated.

### "Thread-safe" means a class is safe to call from several threads

`ArrayList` is not thread-safe: two threads calling `add` concurrently can corrupt it or lose elements. `ConcurrentHashMap` is. The term is a statement about a class's own guarantees, and the default in the JDK's collections is **not** thread-safe — see [Collection Selection Decision Matrix](../collections/collection-selection-decision-matrix.md).

## 5. How It Works Internally (L3)

A Java thread maps onto a real operating-system thread (until virtual threads, below). Creating one costs roughly a megabyte of stack plus kernel bookkeeping, which is why applications use pools rather than a thread per task — covered in [Executors and Thread Pool Sizing](executors-and-thread-pool-sizing.md).

`synchronized` acquires a **monitor** — every Java object has one. The JVM optimises the uncontended case heavily (biased and thin locks historically; lightweight CAS paths now), which is why `synchronized` on an uncontended object is nearly free and why microbenchmarks that never contend are misleading.

`AtomicInteger` uses **compare-and-swap**: read the value, compute the new one, and atomically swap only if the variable still holds what you read. If another thread changed it, the swap fails and the loop retries. That is a single CPU instruction on modern hardware, and the full treatment — including the ABA problem — is in [Atomics, CAS, and the ABA Problem](atomics-cas-and-the-aba-problem.md).

`volatile` inserts memory barriers that prevent both the compiler and the CPU from reordering or caching the access. The formal rules are the **Java Memory Model**, covered in [The Java Memory Model and volatile](java-memory-model-and-volatile.md). The visibility failure measured in Section 4 is that model being exercised: without `volatile`, the JIT had no obligation to re-read the field, so it did not.

## 6. Practical Usage

Most application code should not create threads directly. Use an `ExecutorService`:

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
Future<Integer> result = pool.submit(() -> expensiveComputation());
int value = result.get();          // blocks until done
pool.shutdown();
```

This gives you reuse, a bounded thread count, and a way to get a value back — none of which raw `Thread` provides. Java 21 adds virtual threads, where creating one per task genuinely is reasonable:

```java
try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
    pool.submit(() -> blockingIoCall());
}
```

See [Virtual Threads](virtual-threads.md) for what changes and what does not — notably that virtual threads change the cost of blocking, not the rules about shared mutable state. Every race condition in this chapter behaves identically on virtual threads.

## 7. Examples

The full, runnable source is in [`practice/java/concurrency/concurrency-fundamentals/`](../../../practice/java/concurrency/concurrency-fundamentals/README.md). The three shapes worth memorising:

```java
// A flag: volatile is correct and sufficient.
private volatile boolean shutdownRequested = false;

// A counter: volatile is NOT sufficient. Use an atomic.
private final AtomicInteger processed = new AtomicInteger();
processed.incrementAndGet();

// Two fields that must change together: only a lock will do.
private final Object lock = new Object();
private int balance;
private int transactionCount;

void deposit(int amount) {
    synchronized (lock) {
        balance += amount;
        transactionCount++;     // an observer must never see one without the other
    }
}
```

## 8. Common Mistakes

- **Calling `run()` instead of `start()`.** Produces entirely sequential code that looks concurrent, with no error.
- **Believing `volatile` makes `counter++` safe.** Measured: it loses roughly as many updates as no keyword at all.
- **Synchronizing on a mutable field**, so different threads lock different objects and the lock does nothing.
- **Synchronizing on `String` literals or boxed types**, which are interned or cached and therefore shared with unrelated code.
- **Assuming test success means correctness.** A race that loses 70% of updates under eight contending threads may lose zero in a single-threaded test.
- **Sharing a non-thread-safe collection** — `ArrayList`, `HashMap` — across threads without synchronization.
- **Depending on finishing order.** Four runs of the same six threads gave four different orders.
- **Creating threads per request instead of using a pool**, which works until the load arrives.
- **Catching `InterruptedException` and ignoring it.** Restore the flag with `Thread.currentThread().interrupt()` or the cancellation signal is lost.

## 9. Edge Cases

- **A `volatile` reference to a mutable object** publishes the reference safely but says nothing about the object's fields. `volatile List<String>` does not make the list thread-safe.
- **`synchronized` methods lock `this`**, which is a public object — external code can lock on your instance. A `private final Object lock` avoids that.
- **Self-invocation and locks**: Java monitors are reentrant, so a `synchronized` method calling another on the same object does not deadlock. This is the opposite of Spring's `@Transactional` self-invocation behaviour.
- **`Thread.sleep` does not release a held lock.** `Object.wait()` does. Sleeping inside `synchronized` blocks every other thread for the duration.
- **A thread that throws** dies silently unless an `UncaughtExceptionHandler` is set. In a pool, the task fails and the worker is usually replaced — [Executors and Thread Pool Sizing](executors-and-thread-pool-sizing.md) covers what surfaces and what does not.

## 10. Performance Implications

8 threads × 200,000 contended increments, after JIT warmup:

```text
synchronized block            :    63 ms  (result 1,600,000)
AtomicInteger                 :    50 ms  (result 1,600,000)
```

A modest gap, and deliberately not the basis for a recommendation. Under **contention** atomics generally win, because a failed CAS retries in user space while a contended lock may park the thread. Under **no** contention the difference is close to nothing, because the JVM optimises uncontended locking aggressively.

The performance decision that actually matters is not which primitive but **how long you hold it**. A lock held across an I/O call serializes every thread behind a network round trip — orders of magnitude worse than any difference above. See [`production-cookbook/flash-sale-latency-collapse-from-pessimistic-locks-held-across-payment-auth.md`](../../../production-cookbook/flash-sale-latency-collapse-from-pessimistic-locks-held-across-payment-auth.md).

## 11. Trade-offs

| Decision | Gains | Costs |
|---|---|---|
| `volatile` over `synchronized` | No locking, no blocking | Visibility only — wrong for anything read-modify-write |
| `AtomicInteger` over `synchronized` | Lock-free, better under contention | One variable only; cannot make two fields atomic together |
| `synchronized` over atomics | Protects a block, several fields, invariants | Blocking; deadlock becomes possible with more than one lock |
| Immutability over any of them | No synchronization needed at all | Allocation per change; not always a natural fit |
| Thread pool over raw threads | Bounded resources, reuse, results via `Future` | Sizing becomes a decision; a full queue needs a policy |

The option people skip is the first one worth considering: **if the state is never mutated after construction, none of this applies.** See [Immutability and Defensive Copying](../language-core/immutability-and-defensive-copying.md).

## 12. Senior-Level Considerations (L3)

The step up is from "which keyword" to **what the invariant is**. `synchronized` and atomics protect operations; what needs protecting is the relationship between fields. "`balance` and `transactionCount` must always agree" is the real requirement, and it determines that a single lock covers both rather than two atomics covering one each.

Second: **testing concurrency is genuinely hard**, and a passing test proves less than usual. The failures here are timing-dependent, so a test that passes a thousand times can fail in production under different scheduling. The measurements in this chapter only reproduce reliably because they use eight contending threads and a hundred thousand iterations; at ten iterations the plain counter is usually correct. See [Testing Asynchronous and Concurrent Code](../../08-testing/testing-asynchronous-and-concurrent-code.md).

Third: know the diagnostics before you need them. A thread dump (`jcmd <pid> Thread.print`) reports Java-level deadlocks explicitly, naming the cycle — you do not read it manually. [Deadlock, Race Conditions, and Thread Diagnostics](deadlock-race-conditions-and-thread-diagnostics.md) covers the full workflow.

## 13. Staff/System-Level Considerations (L4)

At system scale the useful move is usually to **remove the shared mutable state rather than protect it better**. Partitioning work so each thread owns its own data eliminates the problem category instead of managing it; a queue between producers and consumers converts shared state into message passing; immutable values plus an atomic reference swap gives lock-free reads.

The organisational version: hand-rolled synchronization is a reasonable thing to *understand* and a poor thing to *write* in application code. `ConcurrentHashMap`, `ExecutorService`, and the `java.util.concurrent` primitives are correct, reviewed, and hard to reproduce. A codebase with `synchronized` scattered through business logic usually has a design problem rather than a concurrency problem.

Worth naming as a counterweight: this chapter's material is genuinely load-bearing knowledge even though most of it should not appear in day-to-day code. Understanding why `volatile` does not fix a counter is what lets someone review a change correctly — the goal is not to write these primitives often, it is to recognise the one time it matters.

## 14. Production Scenarios

Concurrency failures already documented in this repository, each with full symptoms and diagnosis:

- [Lost update in a get-then-put counter increment](../../../production-cookbook/lost-update-in-a-get-then-put-counter-increment.md) — this chapter's race condition, in a real system.
- [Intermittent ConcurrentModificationException from an unsynchronized shared list](../../../production-cookbook/intermittent-concurrentmodificationexception-from-an-unsynchronized-shared-list.md).
- [Cross-request ThreadLocal leak from pooled thread reuse](../../../production-cookbook/cross-request-threadlocal-leak-from-pooled-thread-reuse.md) — the failure mode of pooling, which this chapter recommends.
- [Lock-ordering deadlock under peak load](../../../production-cookbook/lock-ordering-deadlock-under-peak-load.md) — what more than one lock costs.
- [Stale flag read exposed by a JVM upgrade's JIT timing](../../../production-cookbook/stale-flag-read-exposed-by-a-jvm-upgrades-jit-timing.md) — Section 4's visibility failure, in production, surfacing only after an upgrade changed JIT behaviour.

## 15. Interview Questions

### Interview Answer Framework

Pre-built delivery layers for this topic, in the shape [The Technical Answer Framework](../../20-interview-preparation/technical-answers/technical-answer-framework.md) describes. The 10-minute layer is an **outline, not a script** — expanding it into ten minutes of speech is work that happens out loud and in advance, per [Explaining Technical Concepts Under Pressure](../../20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md).

#### 30-Second Answer

A thread is a path of execution; `start()` creates one, `run()` just calls a method on the current thread. The problem is shared mutable state: `counter++` is read, add, write, so two threads can both read 41 and both write 42. `synchronized` and `AtomicInteger` fix that. `volatile` does not — it fixes visibility, not atomicity.

#### 2-Minute Answer

Open as above, then make the distinction pay with the measurement, because it is what separates a memorised answer from a used one.

**Eight threads incrementing the same `int` a hundred thousand times each — 800,000 expected — produced about 230,000.** Roughly 70% of the increments vanished, and nothing threw. Marking the field `volatile` produced about 177,000: no better.

**Then say what `volatile` is for.** A reader thread spinning on a non-volatile flag never saw the write at all — 323 million spins, and it only stopped because the demo capped it. With `volatile` it exited immediately. So `volatile` is correct for a flag and wrong for a counter.

**Close with the choice rule**, which is the part interviewers listen for: pick by what must be atomic *together*. One variable, use an atomic. Several fields that must agree, use a lock — two atomics would each be atomic and the pair would still be observable half-updated.

#### 10-Minute Deep Dive

Cover, in order: what a thread is and the `start()`-versus-`run()` trap (Section 3); non-deterministic finishing order and why depending on it is a bug (Section 3, measured); `counter++` as three operations and the measured lost-update rate (Section 4); why `volatile` does not fix it, with the second measured line; what `volatile` *does* fix, with the visibility measurement and the JIT hoisting that explains it; the three-tool decision table and the "atomic together" criterion; what "thread-safe" means as a property of a class; monitors, compare-and-swap, and memory barriers as the mechanisms underneath (Section 5, linking [The Java Memory Model and volatile](java-memory-model-and-volatile.md) and [Atomics, CAS, and the ABA Problem](atomics-cas-and-the-aba-problem.md)); pools instead of raw threads; and close with the Staff framing — removing shared mutable state beats protecting it.

#### Whiteboard Explanation

Draw a single box labelled `counter = 41`. Above it draw two threads as parallel arrows. On each arrow write three ticks: `read 41`, `+1`, `write 42`. Then interleave them visually — thread A reads, thread B reads, A writes, B writes — and write `42` in the box with "two increments, one change" beside it. That picture is the entire race condition, and it takes about twenty seconds to draw while narrating.

### Question 1 — What is the difference between `synchronized`, `volatile`, and `AtomicInteger`?

**Why interviewers ask it.** It is the standard discriminator for this topic, and the `volatile` half is fumbled often enough to be diagnostic.

**Expected answer.** `volatile` guarantees visibility — a read sees the latest write — and nothing else. `synchronized` guarantees atomicity over a block, and visibility as a consequence. `AtomicInteger` guarantees atomicity on a single variable via compare-and-swap, without a lock. The practical rule: `volatile` for a flag, atomics for one variable, a lock for several fields that must change together.

**Minimum acceptable answer.** Knows `synchronized` provides mutual exclusion and that `volatile` is "about visibility," without being able to say why that is insufficient for a counter.

**Strong Senior answer.** Explains *why* `volatile` fails on `counter++`: it is read-add-write, and `volatile` does not prevent interleaving between the read and the write. Ideally cites the measurement — eight threads, 800,000 expected, about 230,000 plain and about 177,000 volatile.

**Staff-level extension.** Reframes the choice as "what is the invariant" rather than "which keyword," notes that two atomics cannot make two fields atomic together, and observes that the better move at scale is usually to remove the shared mutable state — partitioning, message passing, or immutability — rather than to protect it more carefully.

**Common mistakes.** Saying `volatile` "makes a variable thread-safe." Claiming atomics are always faster. Not distinguishing visibility from atomicity at all.

**Likely follow-ups.** "So can I use `volatile` for a counter?" (No — measured.) "When would `volatile` be the right choice?" (A flag one thread writes and others read.) "Two fields must always agree — two atomics or one lock?" (One lock.)

**Evaluation criteria (1–5).** 1: cannot distinguish them. 3: correct definitions of all three. 5: explains the read-modify-write mechanism, applies the atomic-together criterion, and knows removing shared state is the better system-level answer.

### Question 2 — What is a race condition? Give an example.

**Why interviewers ask it.** It checks whether the candidate can produce a concrete instance or only a definition. "Depends on timing" is memorisable; the three-step example is not.

**Expected answer.** A bug where the result depends on the relative timing of threads rather than on the code. The canonical example is `counter++` on a shared `int`: it is read, add one, write back, so two threads can both read 41, both compute 42, and both write 42 — two increments, one net change. Measured, eight threads lost roughly 70% of 800,000 increments.

**Minimum acceptable answer.** "Two threads accessing the same thing at the same time and getting the wrong result," without the read-modify-write decomposition.

**Strong Senior answer.** Adds why it is dangerous rather than merely wrong: nothing throws, tests pass, and it surfaces under load. Notes that the same code is usually correct single-threaded and at low iteration counts, which is exactly why it reaches production.

**Staff-level extension.** Distinguishes a race condition from a data race, and observes that the real defence is design — no shared mutable state means no race — with synchronization as the fallback when sharing is genuinely required.

**Common mistakes.** Confusing it with deadlock. Giving only the definition with no concrete operation.

**Likely follow-ups.** "How would you fix it?" "Why didn't the test catch it?" "Would `volatile` fix it?"

**Evaluation criteria (1–5).** 1: no working definition. 3: definition plus a concrete example. 5: the three-step decomposition, why tests miss it, and design-level prevention.

### Question 3 — Why might a thread never see another thread's write to a `boolean` flag?

**Why interviewers ask it.** It is the visibility half, and it is the question that reveals whether someone has actually hit this or only read the keyword list.

**Expected answer.** Because the language does not promise they will. Without `volatile` (or another happens-before edge), the JIT may hoist the field read out of the loop and the CPU may serve a cached value, so the reader spins on a stale copy indefinitely. Measured directly: a non-volatile reader spun 323 million times and never observed the write, exiting only on the demo's three-second cap; the `volatile` version exited immediately, and the result reproduced on every run.

**Minimum acceptable answer.** Knows that `volatile` is needed for a flag shared between threads, without being able to explain what would otherwise go wrong.

**Strong Senior answer.** Names hoisting and caching as the mechanisms, and notes that the correctness of the non-volatile version is not merely unlikely but unguaranteed — it may work for years and change behaviour after a JVM upgrade alters JIT decisions.

**Staff-level extension.** Connects it to the Java Memory Model as a contract about what the runtime is *allowed* to do rather than a description of what it usually does, and to the operational consequence: a latent visibility bug can surface from a runtime upgrade with no application change, which makes it very hard to attribute. Points at [Stale flag read exposed by a JVM upgrade's JIT timing](../../../production-cookbook/stale-flag-read-exposed-by-a-jvm-upgrades-jit-timing.md).

**Common mistakes.** Believing the write "eventually" becomes visible. Assuming a `sleep` in the loop makes it correct — it often masks the problem without fixing it.

**Likely follow-ups.** "Would `AtomicBoolean` also work?" (Yes, with atomicity you do not need.) "Does `synchronized` give you this too?" (Yes, as a consequence.)

**Evaluation criteria (1–5).** 1: believes it always works. 3: knows `volatile` is required. 5: names the mechanism and the upgrade-surfaces-latent-bug consequence.

## 16. Coding/Practice Exercises

1. Run the demo in [`practice/java/concurrency/concurrency-fundamentals/`](../../../practice/java/concurrency/concurrency-fundamentals/README.md). Before running, predict the plain-counter result to the nearest hundred thousand. Then run it three times and note the spread.
2. Change `INCREMENTS` from 100,000 to 10. Run it twenty times and count how often the plain counter is correct. That ratio is why this bug reaches production.
3. Replace the `synchronized` block with a `synchronized` method on a shared object and confirm the result is still correct. Then make the lock object non-`final` and reassign it mid-run; explain what breaks.
4. Write a bounded buffer — `put` blocks when full, `take` blocks when empty — using `synchronized`, `wait`, and `notifyAll`. Then replace it with `ArrayBlockingQueue` and compare the line counts.
5. Convert the counter demo to virtual threads (`Executors.newVirtualThreadPerTaskExecutor()`). Predict whether the race still occurs before running.

## 17. Debugging Exercises

1. The visibility demo's plain reader never exits within the cap. Add `System.out.println()` inside its spin loop and re-run. Explain why it now terminates, and why that is *not* a fix. (Printing involves a synchronized stream, which introduces the memory barrier the loop lacked.)
2. Take a thread dump with `jcmd <pid> Thread.print` while the visibility demo is spinning. Identify the spinning thread and its stack frame.
3. Write a two-lock deadlock, run it, and confirm the JVM reports the cycle explicitly in a thread dump rather than requiring you to work it out.

## 18. Design Exercises

1. A service counts requests per endpoint for a metrics dashboard. Choose between `synchronized`, `AtomicLong` per endpoint, and a `ConcurrentHashMap<String, LongAdder>`, and justify the choice on the atomic-together criterion.
2. A cache must be readable by many threads and updated rarely. Compare a `synchronized` map, a `ConcurrentHashMap`, and an immutable map behind a `volatile` reference swapped on each update.
3. An in-memory bank account must keep `balance` and `transactionCount` consistent for any observer. Explain why two `AtomicInteger`s do not satisfy that requirement.

## 19. Further Reading

- [The Java Memory Model and volatile](java-memory-model-and-volatile.md) — the formal rules behind Section 4's visibility result.
- [Deadlock, Race Conditions, and Thread Diagnostics](deadlock-race-conditions-and-thread-diagnostics.md) — the diagnostic workflow, and what more than one lock costs.
- [Atomics, CAS, and the ABA Problem](atomics-cas-and-the-aba-problem.md) — how `AtomicInteger` works, and where it is subtle.
- [Executors and Thread Pool Sizing](executors-and-thread-pool-sizing.md) — the next step after raw `Thread`.
- [Virtual Threads](virtual-threads.md) — what Java 21 changes, and what it does not.
- [Immutability and Defensive Copying](../language-core/immutability-and-defensive-copying.md) — the option that removes the problem.
- [OS Process and Thread Model](../../01-computer-science-foundations/os-process-thread-model.md) — threads below the JVM.
- [Java Tutorial: Concurrency](https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html) — the official introduction.

## 20. Mastery Checklist

**L1 — Foundation.** You can start a thread three ways and say why `start()` and `run()` differ. You can explain what `join()` does. You accept that finishing order is not starting order.

**L2 — Working Knowledge.** You can decompose `counter++` into three operations and explain the lost update from that. You can state what `volatile` guarantees and what it does not, and give one correct use and one incorrect use. You can pick between `volatile`, `synchronized`, and an atomic using the atomic-together criterion. You know `ArrayList` is not thread-safe and `ConcurrentHashMap` is.

**L3 — Senior.** You reason about invariants rather than keywords. You know why a passing test proves little here. You can take and read a thread dump. You know the mechanisms — monitors, CAS, memory barriers — well enough to explain *why* each tool gives what it gives.

**L4 — Staff.** You look for ways to remove the shared mutable state before reaching for a primitive. You treat hand-rolled synchronization in business logic as a design signal. You can explain why a latent visibility bug can surface from a JVM upgrade with no application change.
