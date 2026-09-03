---
title: "Flashcards: Atomics, CAS, and the ABA Problem"
slug: atomics-cas-and-the-aba-problem
document_type: flashcard-deck
domain: concurrency
topic_id: T-405
canonical: ../handbook/concurrency/atomics-cas-and-the-aba-problem.md
last_updated: 2026-09-02
---

# Flashcards: Atomics, CAS, and the ABA Problem

**Canonical chapter:** [`syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md`](../syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md)

## Card: What CAS actually checks

**Prompt:**
Does a successful `compareAndSet` mean the value never changed?

**Answer:**
No — it means the value is currently equal to the expected value. It could have changed and changed back (ABA).

**Why it matters:**
The exact misconception ABA exploits.

**Common trap:**
Treating "CAS succeeded" as "nothing happened in between."

**Related:**
[Core Concepts](../syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md#core-concepts)

## Card: The ABA fix

**Prompt:**
How does `AtomicStampedReference` fix the ABA problem?

**Answer:**
It pairs the reference with an integer stamp incremented on every mutation; the CAS checks both, so a value returning to an identity-equal state still fails the stamp check.

**Why it matters:**
The standard, real JDK fix — verified by a rejected CAS in this chapter's demo.

**Common trap:**
Forgetting to bump the stamp on every mutation path, silently reintroducing the vulnerability.

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md#internal-implementation)

## Card: CAS vs. synchronized, measured

**Prompt:**
Is a CAS retry loop always faster than a `synchronized` block?

**Answer:**
Not guaranteed — measured ~2x faster under this chapter's specific contention level (8 threads, 500,000 increments each), but the gap depends on contention profile.

**Why it matters:**
Avoids treating "lock-free" as a performance guarantee rather than something to measure.

**Common trap:**
Choosing CAS by reputation rather than by measuring the actual workload.

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md#internal-implementation)
