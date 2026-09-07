---
title: "Flashcards: Concurrency Coding Problems"
slug: concurrency-coding-problems
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2116
canonical: ../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md
last_updated: 2026-09-07
---

# Flashcards: Concurrency Coding Problems

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/concurrency-coding-problems.md`](../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md)

## Card: What makes concurrency correctness different

**Prompt:**
How does correctness for a concurrency coding problem differ from correctness for a single-threaded algorithm?

**Answer:**
A concurrent class's correctness must hold regardless of the exact order its threads actually get scheduled in, not just for one deterministic execution path per input.

**Why it matters:**
A solution that "looks right" and even passes a single test run can still hide a race condition, deadlock, or ordering bug that only manifests under specific, unlucky scheduling.

**Common trap:**
Trusting a single passing test run as sufficient evidence a concurrency solution is correct.

**Related:**
[syllabus/03-data-structures-algorithms/concurrency-coding-problems.md](../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md)

## Card: Building H2O's semaphore doesn't protect the counter

**Prompt:**
In Building H2O, why does the `hydrogenSlots` semaphore's 2-permit cap not, by itself, make the shared `hydrogenCount` counter safe?

**Answer:**
Up to 2 hydrogen threads can run concurrently within that cap, so the read-then-increment-then-read-again sequence on `hydrogenCount` is a genuine data race without an additional `synchronized` block around it — the semaphore limits concurrency but doesn't protect the shared, mutable state those concurrent threads both touch.

**Why it matters:**
Recognizing exactly which shared state a synchronization primitive protects — and which it doesn't — is the single most common category of subtle concurrency bug in solutions that otherwise look correct.

**Common trap:**
Assuming a semaphore permit cap alone protects every shared, mutable variable the permitted threads touch.

**Related:**
[syllabus/03-data-structures-algorithms/concurrency-coding-problems.md](../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md)

## Card: Dining Philosophers' asymmetric acquisition fix

**Prompt:**
How does having exactly one philosopher acquire forks in the opposite order prevent deadlock in Dining Philosophers?

**Answer:**
The naive "everyone grabs left fork first" deadlocks when all participants simultaneously hold their left fork and wait forever for their right — a circular wait. Having one participant reach for their right fork first breaks the symmetry needed to complete a full cycle, since that one participant is waiting on a fork a different participant hasn't yet claimed as their own right fork.

**Why it matters:**
This exact mechanism is the real, documented cause behind production lock-ordering deadlock incidents, not just an academic toy exercise.

**Common trap:**
Proposing a fix that requires every participant to somehow know about every other participant's current state in real time, rather than a fix requiring no additional coordination beyond acquisition order.

**Related:**
[syllabus/03-data-structures-algorithms/concurrency-coding-problems.md](../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md)

## Card: while vs. if for wait() — spurious wakeup

**Prompt:**
Why must a `wait()` call always sit inside a `while` loop re-checking its condition, never an `if`?

**Answer:**
The Java Language Specification explicitly permits a thread to return from `wait()` without any corresponding `notify()` call ever having happened (a "spurious wakeup"), so the actual condition must always be re-checked after waking, never assumed true just because `wait()` returned.

**Why it matters:**
This is a real, non-negotiable correctness bug if violated, not defensive-programming overkill — the JLS itself permits the scenario `while` guards against.

**Common trap:**
Using `if (condition) wait();` instead of `while (condition) wait();`.

**Related:**
[syllabus/03-data-structures-algorithms/concurrency-coding-problems.md](../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md)

## Card: notifyAll() vs. notify() in the bounded blocking queue

**Prompt:**
Why does the hand-rolled bounded blocking queue use `notifyAll()` rather than a single-target `notify()`?

**Answer:**
With two distinct wait conditions active (queue full vs. queue empty), a plain `notify()` could wake a thread waiting on the wrong condition, which would re-check its while-loop, find it still false, and go back to sleep — functionally safe but wasted work. `notifyAll()` avoids that failure mode entirely, at the cost of waking more threads than strictly necessary.

**Why it matters:**
It's a deliberate simplicity-over-throughput trade-off, not an arbitrary choice — a more targeted design using separate condition variables could avoid the wasted wakeups at the cost of more complexity.

**Common trap:**
Using a missing or incorrectly-scoped notification call, which means a waiting thread's condition may have genuinely become true but no signal ever wakes it.

**Related:**
[syllabus/03-data-structures-algorithms/concurrency-coding-problems.md](../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md)

## Card: The repeated-run verification standard

**Prompt:**
Why does this chapter's own verification standard require running concurrency tests repeatedly rather than trusting a single green pass?

**Answer:**
Scheduling-dependent flakiness is a genuinely different failure mode from a deterministic algorithm's bugs — a single passing run can mask it entirely, since the same code can pass on one run and fail on the next with no code change at all.

**Why it matters:**
This chapter's own tests were re-run 5 times for scheduling-dependent stability, matching its source material's verification standard, as real evidence rather than a hypothetical caution.

**Common trap:**
Treating a single successful test run as proof a concurrent implementation is correct.

**Related:**
[syllabus/03-data-structures-algorithms/concurrency-coding-problems.md](../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md)
