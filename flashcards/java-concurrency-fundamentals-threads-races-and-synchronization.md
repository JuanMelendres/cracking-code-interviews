---
title: "Flashcards: Java Concurrency Fundamentals"
slug: java-concurrency-fundamentals-threads-races-and-synchronization
document_type: flashcard-deck
domain: 02-java
topic_id: T-2214
canonical: ../syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md
last_updated: 2026-09-30
---

# Flashcards: Java Concurrency Fundamentals

**Canonical chapter:** [`syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md`](../syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md)

## Card: start() versus run()

**Prompt:**
What actually happens if you call `thread.run()` instead of `thread.start()`?

**Answer:**
`run()` is an ordinary method call on the **current** thread — no new thread is created. Measured: `d.run()` printed "ran on: main" while `d.start()` printed "ran on: Thread-1".

**Why it matters:**
The code becomes entirely sequential while looking concurrent, and nothing reports an error. It is the first silent trap in the topic.

**Common trap:**
Assuming any `Thread` method touching `run` involves a thread. Only `start()` creates one.

**Related:**
[Foundation (L1)](../syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md#3-foundation-l1)

## Card: Why counter++ loses updates

**Prompt:**
Eight threads each increment a shared `int` 100,000 times. What is the result, and why?

**Answer:**
About **230,000** of the expected **800,000** — roughly 70% lost, measured. `counter++` is three operations: read, add one, write back. Two threads can both read 41, both compute 42, and both write 42 — two increments, one net change. Nothing throws.

**Why it matters:**
This is a race condition in its simplest form: the result depends on timing rather than on the code, which is why it passes tests and fails under load.

**Common trap:**
Trusting a passing test. At 10 iterations the plain counter is usually correct; the failure needs contention to appear.

**Related:**
[Core Concepts (L2)](../syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md#4-core-concepts-l2)

## Card: Does volatile make counter++ safe?

**Prompt:**
Does marking a counter `volatile` fix the lost-update problem?

**Answer:**
**No.** Measured on the same workload: the plain counter reached ~230,000 of 800,000 and the `volatile` counter ~177,000. Across five runs the ranges overlapped (159,773–229,765 plain, 135,457–195,568 volatile), so the honest claim is that `volatile` **does not help** — not that it is worse. It guarantees each read sees the latest value; it does nothing to stop another thread interleaving between your read and your write.

**Why it matters:**
This is the single most common Java concurrency misconception, and two numbers on adjacent lines settle it without argument.

**Common trap:**
"`volatile` makes a variable thread-safe." It makes it *visible*.

**Related:**
[Core Concepts (L2)](../syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md#4-core-concepts-l2)

## Card: What volatile actually solves

**Prompt:**
A thread spins on a non-volatile `boolean` flag that another thread sets. What happens?

**Answer:**
It may **never see the write**. Measured: the reader spun **323,522,386** times and exited only because the demo caps it at three seconds; the `volatile` version exited 0 ms after the write. Reproduced identically on every run. The JIT is entitled to hoist a non-volatile read out of the loop, and it did.

**Why it matters:**
This is the visibility half of the rule. A `volatile` flag is correct; a `volatile` counter is not.

**Common trap:**
Believing the write becomes visible "eventually." The language never promised it would — and such code can work for years, then change behaviour after a JVM upgrade alters JIT decisions.

**Related:**
[The Java Memory Model and volatile](../syllabus/02-java/concurrency/java-memory-model-and-volatile.md)

## Card: Choosing between volatile, synchronized, and atomics

**Prompt:**
Two related fields must always agree to any observer. Two `AtomicInteger`s, or one lock?

**Answer:**
**One lock.** Two atomics would each be individually atomic while the *pair* stays observable half-updated. The criterion is what must be atomic **together**: one variable → an atomic; several fields that must agree → a `synchronized` block; a flag one thread writes and others read → `volatile`.

**Why it matters:**
It reframes the question from "which keyword is fastest" to "what is the invariant," which is the actual Senior-level move. Measured cost is only 63 ms versus 50 ms on a contended workload — not a basis for the decision.

**Common trap:**
Choosing by benchmark. What matters far more is how long you hold the lock — one held across an I/O call serializes every thread behind a network round trip.

**Related:**
[Trade-offs](../syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md#11-trade-offs)
