---
title: "Concurrency Coding Problems"
slug: concurrency-coding-problems
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2116
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - design-style-coding-problems.md
  - ../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md
related:
  - design-style-coding-problems.md
  - ../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md
  - ../01-computer-science-foundations/os-process-thread-model.md
practice: ../../practice/java/week-22/concurrency/
production_scenarios:
  - ../../production-cookbook/opposite-order-lock-acquisition-deadlock-in-a-funds-transfer.md
  - ../../production-cookbook/lock-ordering-deadlock-under-peak-load.md
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-22/03-concurrency-coding-practice.md
---

# Concurrency Coding Problems

> **Provenance.** The four worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-22/03-concurrency-coding-practice.md` — real, compiled, executed code (`practice/java/week-22/concurrency/`), re-verified on OpenJDK 21.0.12 while writing this chapter (10/10 assertions passing, all real multi-threaded tests re-run 5 times for scheduling-dependent stability, matching the source material's own verification standard).

This is Master Topic Register **T-1417** (IWI 5.75, ⭐, moderate frequency). These are "implement a correct small concurrent Java class" problems — distinct from [Deadlock, Race Conditions, and Thread Diagnostics](../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md), which covers the Java Memory Model and deadlock-diagnostics *theory* this practice applies rather than duplicates.

## 1. Why This Matters

Concurrency coding problems test something genuinely different from single-threaded algorithm problems: correctness under a program's actual, non-deterministic execution order, not just correctness for one fixed input. A concurrency solution that "looks right" and even passes a single test run can still hide a race condition, a deadlock, or a subtle ordering bug that only manifests under specific, unlucky thread scheduling — which is exactly why this chapter's own verification standard (Section 10) requires repeated runs, not a single green pass, as real evidence.

## 2. Prerequisites

[Design-Style Coding Problems](design-style-coding-problems.md) — several problems here (the bounded blocking queue, Section 7 Problem 4) are concurrency-safe versions of data-structure design problems covered there in their single-threaded form. [Deadlock, Race Conditions, and Thread Diagnostics](../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md) — this chapter applies that chapter's theory to small, from-scratch implementations rather than re-deriving the underlying concepts.

## 3. Foundation (L1)

**A concurrency coding problem asks for a class whose methods are called from multiple threads simultaneously, and whose correctness must hold regardless of the exact order those threads actually get scheduled in.** This is fundamentally different from a single-threaded algorithm's correctness, which only needs to hold for one deterministic execution path per input.

**The standard building blocks for these problems are `Semaphore` (permits controlling how many threads may proceed), intrinsic locks via `synchronized` (mutual exclusion around a critical section), and `wait()`/`notify()`/`notifyAll()` (a thread waiting for a condition to become true, and other threads signaling that it might now hold).** Every problem in this chapter composes these primitives to enforce a specific ordering or exclusion guarantee.

## 4. Core Concepts (L2)

**Semaphore-based baton-passing** (Building H2O, Section 7 Problem 1; Fizz Buzz Multithreaded, Section 7 Problem 2) uses one or more semaphores as an explicit "turn" token — a thread acquires its semaphore, does its work, and releases whichever semaphore corresponds to the next thread's turn. Building H2O's is a fixed pattern (2 hydrogens then 1 oxygen, repeating); Fizz Buzz Multithreaded's is data-dependent (the next turn depends on divisibility, computed dynamically rather than hardcoded into a fixed rotation).

**Deadlock avoidance via asymmetric resource-acquisition order** (The Dining Philosophers, Section 7 Problem 3) breaks a potential circular-wait condition by having exactly one participant acquire its two required resources in the opposite order from everyone else — removing the possibility of every participant simultaneously holding one resource and waiting for another in a complete cycle.

**Hand-rolled blocking data structures via `wait()`/`notifyAll()`** (Design Bounded Blocking Queue, Section 7 Problem 4) implement the same behavior `java.util.concurrent.ArrayBlockingQueue` already provides, specifically to demonstrate the underlying mechanism: a thread waits (releasing its lock while waiting) until a condition becomes true, re-checking that condition in a loop rather than trusting a single wake-up.

## 5. How It Works Internally (L3)

**Building H2O's synchronization boundary, precisely**: `hydrogenSlots`, a semaphore capped at 2 permits, prevents more than 2 hydrogen threads from ever being "in flight" (acquired but not yet bonded) simultaneously — a third hydrogen thread simply blocks on `acquire()` until oxygen releases both permits back. But the permit cap alone doesn't make the shared `hydrogenCount` counter safe: since up to 2 hydrogen threads *can* run concurrently within that cap, the read-then-increment-then-read-again sequence on `hydrogenCount` is a genuine data race without an additional `synchronized` block around it — this is the one place in the whole solution where the semaphore's own guarantee (limiting concurrency to 2) isn't sufficient by itself to protect a shared, mutable counter that those same 2 concurrent threads both touch.

**The Dining Philosophers' circular-wait-breaking mechanism, precisely**: the naive "everyone always grabs their left fork first, then their right" version deadlocks when all participants simultaneously hold their left fork and wait forever for their right — a genuine circular-wait, one of the four Coffman conditions required for deadlock ([Deadlock, Race Conditions, and Thread Diagnostics](../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md) covers all four in depth). Having exactly one participant (not all, not zero) reach for their *right* fork first breaks the symmetry needed to complete a full cycle: in the worst case, every other participant holds their left fork and waits on their right, but the one asymmetric participant is waiting on a fork that a *different* participant hasn't yet claimed as their own right fork — removing the last link needed to close the circular chain. This specific fix requires no additional synchronization primitive beyond the resources themselves, in contrast to the two standard alternative fixes (a global resource-count semaphore capped below the total, or acquiring all needed resources atomically).

**The bounded blocking queue's spurious-wakeup defense and `notifyAll()` choice, precisely**: both `wait()` calls sit inside a `while` loop condition-check, not an `if` — required because the Java Language Specification explicitly permits a thread to return from `wait()` without any corresponding `notify()` call ever having happened (a "spurious wakeup"), so the actual condition must always be re-checked after waking, never assumed true just because `wait()` returned. Using `notifyAll()` rather than a single-target `notify()` is a deliberate simplicity-over-throughput trade-off: with two distinct wait conditions active (queue full vs. queue empty), a plain `notify()` could wake a thread waiting on the *wrong* condition, which would re-check its own while-loop, find it still false, and go back to sleep — functionally safe, but wasted work. `notifyAll()` avoids that specific failure mode entirely, at the cost of waking more threads than strictly necessary on each call.

## 6. Practical Usage

- **Use a semaphore as an explicit "turn" token whenever a fixed or computable ordering must be enforced across multiple threads** (Building H2O, Fizz Buzz Multithreaded) — simpler to reason about than manually coordinating with locks and condition variables for a pure ordering constraint.
- **When multiple threads each need two or more shared resources, check for a possible circular-wait and consider breaking it via asymmetric acquisition order** (Dining Philosophers) before reaching for a more complex solution.
- **Always place a `wait()` call inside a `while` loop re-checking its actual condition, never an `if`** — the standing, non-negotiable defense against spurious wakeup.

## 7. Examples

**Problem 1 — LC 1117, Building H2O.**

```java
private final Semaphore hydrogenSlots = new Semaphore(2);
private final Semaphore oxygenTurn = new Semaphore(0);
private int hydrogenCount = 0;
private final Object lock = new Object();

void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
    hydrogenSlots.acquire();
    boolean isSecond;
    synchronized (lock) {
        hydrogenCount++;
        isSecond = (hydrogenCount % 2 == 0);
    }
    releaseHydrogen.run();
    if (isSecond) oxygenTurn.release();
}

void oxygen(Runnable releaseOxygen) throws InterruptedException {
    oxygenTurn.acquire();
    releaseOxygen.run();
    hydrogenSlots.release(2);
}
```

**Retrospective:** see Section 5's synchronization-boundary argument. **Verified:** every real oxygen release was preceded by exactly 2 hydrogen releases, across 10 concurrently-started molecules.

**Problem 2 — LC 1195, Fizz Buzz Multithreaded.**

```java
private void dispatchNext() {
    int next = current.get();
    if (next > n) { return; }
    if (next % 15 == 0) fizzBuzzTurn.release();
    else if (next % 3 == 0) fizzTurn.release();
    else if (next % 5 == 0) buzzTurn.release();
    else numberTurn.release();
}
```

**Retrospective:** the dispatch sequence is data-dependent, computed dynamically rather than a fixed rotation like a two-way alternation. **Verified:** the real four-thread execution matched a single-threaded reference sequence exactly for n=20.

**Problem 3 — LC 1226, The Dining Philosophers.**

```java
if (philosopher == 4) {
    // break the circular wait: philosopher 4 reaches for its right fork first
    firstFork = forks[right]; secondFork = forks[left];
} else {
    firstFork = forks[left]; secondFork = forks[right];
}
firstFork.lock();
try { secondFork.lock(); /* eat */ } finally { secondFork.unlock(); }
firstFork.unlock();
```

**Retrospective:** see Section 5's circular-wait-breaking argument. **Verified:** 5 philosophers × 50 real meals each completed within a bounded `join(10_000)` timeout (actual: ~2ms) with zero starvation.

**Problem 4 — LC 1188, Design Bounded Blocking Queue.**

```java
void enqueue(int element) throws InterruptedException {
    synchronized (lock) {
        while (queue.size() == capacity) lock.wait();
        queue.addLast(element);
        lock.notifyAll();
    }
}

int dequeue() throws InterruptedException {
    synchronized (lock) {
        while (queue.isEmpty()) lock.wait();
        int value = queue.removeFirst();
        lock.notifyAll();
        return value;
    }
}
```

**Retrospective:** see Section 5's spurious-wakeup and `notifyAll()` argument. **Verified:** a real producer and consumer thread moved 200 items with strict FIFO order preserved, queue size never exceeding capacity.

## 8. Common Mistakes

- **Assuming a semaphore permit cap alone protects every shared, mutable variable those permitted threads touch** — Building H2O's `hydrogenCount` (Section 5) needs its own separate `synchronized` block even though the semaphore already limits concurrency to 2 threads.
- **Using `if (condition) wait();` instead of `while (condition) wait();`** — the standard, real defense against spurious wakeup that the JLS explicitly permits; skipping the `while` re-check is a genuine, real correctness bug, not defensive-programming overkill.
- **Trusting a single passing test run as sufficient evidence a concurrency solution is correct** — Section 10's own established verification standard (5 repeated runs) exists specifically because scheduling-dependent bugs can pass on one run and fail on the next with no code change at all.

## 9. Edge Cases

- **All participants attempting to acquire their first resource at exactly the same moment** (Dining Philosophers' real, verified stress test — 5 philosophers, 50 meals each, completing without deadlock) — the asymmetric-order fix must hold under genuine, real concurrent contention, not just in a sequential trace.
- **A `wait()` call racing with a `notifyAll()` that happens to fire before the waiting thread actually starts waiting** — the `while`-loop re-check (Section 5) combined with `synchronized`'s own mutual exclusion around both `wait()` and the state change is what prevents this specific race from causing a missed wakeup.
- **A queue at exactly its capacity boundary, with a concurrent producer and consumer both active** (Bounded Blocking Queue's own verified real-concurrent-execution test) — the queue's observed size must never exceed capacity, not merely "usually" stay within it.

## 10. Performance Implications

Real, executed verification from `practice/java/week-22/concurrency/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  H2O total releases = 30 (2H+1O per molecule)
  PASS  H2O exactly 20 H releases
  PASS  H2O exactly 10 O releases
  PASS  H2O every O is preceded by exactly 2 H since the prior O (real bonding order)
  PASS  fizzbuzz(20) exact sequence matches single-threaded reference
  PASS  all 5 philosophers finished without deadlocking (took 2ms)
  PASS  every philosopher ate exactly 50 times, none starved
  PASS  bounded queue delivered all 200 items
  PASS  bounded queue preserved FIFO order under real concurrent producer/consumer
  PASS  queue size never observed above capacity=5 (was 5)
Week 22 — Concurrency Coding (LC 1117, 1195, 1226, 1188): 10/10 assertions passed
```

Re-run 5 times in a row with identical results (10/10 every time) — the actual verification standard applied here, not just a single transcript. **This repeated-run discipline is itself the chapter's central performance/correctness lesson**: a single green run of any concurrent test is not sufficient evidence of correctness, since scheduling-dependent flakiness is a genuinely different failure mode from a deterministic algorithm's bugs — one that a single passing run can mask entirely.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Semaphore-based baton-passing | Simple, explicit ordering guarantee | Doesn't generalize easily to more complex, data-dependent coordination without careful design |
| `synchronized` + `wait()`/`notifyAll()` (hand-rolled) | Demonstrates the underlying mechanism directly; no external dependency | More verbose and error-prone than `java.util.concurrent`'s battle-tested equivalents; `notifyAll()` wakes more threads than strictly necessary |
| Asymmetric lock-ordering (deadlock avoidance) | No additional synchronization primitive needed | Requires identifying which participant(s) to make asymmetric; global semaphore or atomic multi-lock alternatives can be simpler to reason about in some designs |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is recognizing exactly which shared state a synchronization primitive actually protects, and which shared state it doesn't — Building H2O's semaphore caps *concurrency* at 2 threads but says nothing about protecting the shared counter those 2 threads both mutate (Section 5); missing this distinction is the single most common category of subtle concurrency bug in problems that otherwise look correct.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the Dining Philosophers' asymmetric-lock-ordering deadlock fix is not an academic exercise — it's the exact, real mechanism behind two documented production incidents: [Opposite-Order Lock Acquisition Deadlock in a Funds Transfer](../../production-cookbook/opposite-order-lock-acquisition-deadlock-in-a-funds-transfer.md) and [Lock Ordering Deadlock Under Peak Load](../../production-cookbook/lock-ordering-deadlock-under-peak-load.md), both real incidents where a system's threads acquired multiple locks in inconsistent order under load, producing the same circular-wait condition this chapter's toy Dining Philosophers implementation is specifically designed to teach and fix. The Staff-level pattern is establishing a *global, consistent lock-acquisition order* across an entire codebase (e.g., always lock lower-ID accounts before higher-ID ones in a funds-transfer system) as a standing team convention, rather than relying on each individual code path to independently reason about deadlock avoidance the way this chapter's five-philosopher toy problem does for exactly one hard-coded case.

## 14. Production Scenarios

- **[Opposite-Order Lock Acquisition Deadlock in a Funds Transfer](../../production-cookbook/opposite-order-lock-acquisition-deadlock-in-a-funds-transfer.md)** — a real, documented instance of exactly the circular-wait condition Dining Philosophers (Section 5) is designed to teach and fix, at production scale.
- **[Lock Ordering Deadlock Under Peak Load](../../production-cookbook/lock-ordering-deadlock-under-peak-load.md)** — a second real, documented instance of the same lock-ordering failure class, specifically surfacing under load rather than during normal operation, connecting directly to Section 9's edge case (deadlock potential is often invisible until genuine concurrent contention actually occurs).

## 15. Interview Questions

### Question 1 — Implement a bounded blocking queue that supports concurrent producers and consumers, without using `java.util.concurrent`'s built-in blocking queue classes.

**Why interviewers ask it.** It's the canonical `wait()`/`notifyAll()` mechanism test, checking whether a candidate understands the underlying primitives well enough to build the behavior from scratch, not just call an existing library class.

**Expected answer.** Use a `synchronized` block around both `enqueue` and `dequeue`. Inside `enqueue`, `while (queue.size() == capacity) lock.wait();` before adding, then `notifyAll()` after. Inside `dequeue`, `while (queue.isEmpty()) lock.wait();` before removing, then `notifyAll()` after. The `while` (not `if`) loop guards against spurious wakeup; `notifyAll()` (not `notify()`) avoids waking a thread stuck waiting on the wrong condition given two distinct wait conditions are in play.

**Minimum acceptable answer.** Produces a broadly correct implementation, even if using `if` instead of `while` for the wait condition (a real, flagged bug, not just a stylistic issue).

**Strong Senior answer.** Uses `while` correctly and can explain spurious wakeup as the specific, JLS-permitted reason it's required, not just "best practice."

**Staff-level extension.** Explains the `notify()`-vs-`notifyAll()` trade-off precisely (Section 5), and can discuss when a more targeted, higher-throughput design (e.g., separate condition variables for "not full" and "not empty," as `java.util.concurrent.locks.Condition` supports) would be worth the added complexity over the simpler `notifyAll()`-based version.

**Common mistakes.** Using `if` instead of `while` for the wait condition — a real, not merely stylistic, correctness bug given the JLS explicitly permits spurious wakeup.

**Follow-up questions.** "How would you test this for correctness, given it's inherently non-deterministic?" (Section 10's own answer: run the same test repeatedly, multiple times, since a single passing run is not sufficient evidence.)

### Question 2 — Five philosophers sit at a table, each needing both forks adjacent to them to eat, and forks are shared between neighbors. How do you prevent deadlock?

**Why interviewers ask it.** It's the canonical deadlock-avoidance-via-resource-ordering test, and a strong Staff-level signal when a candidate can connect the toy problem directly to a real production failure mode.

**Expected answer.** The naive "everyone grabs their left fork, then their right" approach deadlocks if all five simultaneously grab their left fork and wait forever for their right — a circular wait. Breaking the symmetry for exactly one philosopher (having them grab their *right* fork first, everyone else still left-then-right) removes the possibility of completing a full circular chain, since that one philosopher's acquisition order doesn't fit the pattern needed to close the cycle.

**Minimum acceptable answer.** Recognizes the naive approach deadlocks and proposes *some* fix (even a less elegant one, like a global semaphore capping concurrent eaters below the total philosopher count).

**Strong Senior answer.** Produces the asymmetric-ordering fix specifically, and can explain precisely why breaking the pattern for even just one participant is sufficient to prevent the cycle.

**Staff-level extension.** Names the real, documented production incidents this exact failure mode causes (Section 13/14) and proposes the corresponding real-world fix: establishing a consistent, global lock-acquisition ordering convention across a codebase, rather than relying on ad hoc per-code-path reasoning.

**Common mistakes.** Proposing a fix that requires every participant to somehow "know" about every other participant's current state in real time, rather than a fix requiring no additional coordination beyond the resource-acquisition order itself.

**Follow-up questions.** "What are the other standard ways to prevent this deadlock, besides asymmetric ordering?" (A global resource-count semaphore capped below the total count of a shared resource, or acquiring all needed resources atomically in one step — both real, standard alternatives worth naming even if the ordering fix is the one implemented.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-22/concurrency/) yourself, at least 3 times in a row, and confirm 10/10 assertions pass every time — reproducing this chapter's own repeated-run verification standard directly.
- This pattern has additional real, already-solved problems: LC 1114 (Print in Order), LC 1115 (Print FooBar Alternately), and LC 1116 (Print Zero Even Odd) — all `Semaphore`-based baton-passing problems — in `practice/java/week-09/concurrency-coding/`, the direct, simpler precursors to this chapter's Building H2O and Fizz Buzz Multithreaded.
- Implement the alternative Dining Philosophers fix (a global semaphore capping concurrent eaters at 4 out of 5 total philosophers) and verify it also prevents deadlock under the same stress test this chapter's asymmetric-ordering version uses.

## 17. Debugging Exercises

**Symptom:** a hand-rolled bounded blocking queue occasionally hangs indefinitely under real concurrent load, with both a producer and a consumer thread each appearing to be permanently blocked in `wait()`.

**Diagnose:** check whether every state-changing operation (`enqueue`, `dequeue`) calls `notifyAll()` (not a scoped, conditional, or missing notify) after changing the queue's state — a missing or incorrectly-scoped notification call means a waiting thread's condition may have genuinely become true, but no signal ever wakes it to re-check. Separately, confirm the wait condition uses a `while` loop, not `if` — a spurious wakeup combined with an `if`-based check could cause a thread to proceed when the condition is actually still false, corrupting queue state in a way that produces a *different* thread's subsequent condition check to hang forever afterward. Reproduce by running the exact scenario repeatedly (Section 10's own standard) rather than relying on a single hang to fully characterize the bug, since the specific interleaving that triggers it may not reproduce on every run.

## 18. Design Exercises

**Design constraint:** design a thread-safe, bounded work queue for a task-processing system where multiple producer threads submit tasks and multiple worker threads consume them, and the system must never deadlock even under sustained high concurrent load from both sides.

Design this using the `wait()`/`notifyAll()` bounded-queue technique from Section 4/5/7 directly as the core mechanism, but explicitly evaluate — per Section 11's own trade-off table — whether `java.util.concurrent.ArrayBlockingQueue` (a battle-tested, more efficient real implementation using more targeted condition variables) should be used instead of the hand-rolled version in an actual production system, reserving the from-scratch implementation for interview/educational contexts specifically. State the real production risk of choosing to hand-roll this in a real system anyway: every one of Section 8's common mistakes (missing `while`, incorrect notification scope) becomes a genuine, hard-to-reproduce production incident risk that a well-tested library implementation has already eliminated.

## 19. Further Reading

- [Deadlock, Race Conditions, and Thread Diagnostics](../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md) — the canonical, in-depth theory this chapter's small implementations apply directly, including the full Coffman conditions this chapter's Section 5 references.
- [Design-Style Coding Problems](design-style-coding-problems.md) — the single-threaded design-problem sibling pattern; Section 4/8 of this chapter directly addresses when a "design"-sounding problem does or doesn't actually require the concurrency techniques covered here.
- [The OS Process/Thread Model](../01-computer-science-foundations/os-process-thread-model.md) — the underlying OS-level thread and scheduling model these Java-level synchronization primitives are built on top of.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, why concurrency correctness is different from single-threaded correctness, and name the standard Java synchronization primitives | [Section 3](#3-foundation-l1) |
| L2 | Apply semaphore-based baton-passing and the `wait()`/`while`-loop/`notifyAll()` pattern correctly to a new, unfamiliar small concurrent class | [Interview Question 1](#question-1--implement-a-bounded-blocking-queue-that-supports-concurrent-producers-and-consumers-without-using-javautilconcurrents-built-in-blocking-queue-classes) |
| L3 | Derive precisely which shared state a given synchronization primitive protects (and which it doesn't), and explain the asymmetric-lock-ordering deadlock-avoidance mechanism | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Connect a toy deadlock-avoidance exercise to real, documented production incidents (Section 14), and design a real concurrent system while correctly judging when to hand-roll versus use a library implementation (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
