---
title: "Java Coding Practice — Week 14 (Collections Fluency)"
week: 14
document_type: study-pack-coding
status: draft
last_reviewed: 2026-07-30
---

# Java Coding Practice — Week 14 (Collections Fluency)

Three small problems, each exercising this week's topics together against a realistic small scenario, rather than in isolation.

**Verification note:** all code is real, compiled and executed on OpenJDK 21.0.12. Source: [`practice/java/week-14/mixed-review/src/CollectionsCodingPractice.java`](../../practice/java/week-14/mixed-review/src/CollectionsCodingPractice.java).

## Problem 1 — A thread-safe frequency counter (T-205)

Count word frequencies across a large input, split and processed by 4 worker threads, using `ConcurrentHashMap.merge()` for the atomic per-word increment.

**Why it matters:** exercises the atomic-increment discipline from `02-concurrenthashmap-internals.md` against a real multi-threaded workload, not a toy example.

## Problem 2 — A bounded producer-consumer pipeline (T-207)

Implement a producer thread and a consumer thread coordinated through an `ArrayBlockingQueue`, verifying every produced value is consumed exactly once, in order, with none lost.

**Why it matters:** exercises `03-blockingqueue-family.md`'s backpressure mechanism end to end, not just a single blocking call in isolation.

## Problem 3 — Correct deduplication via records (T-201, connecting back to Week 13)

Use Java records (which auto-generate correct `equals()`/`hashCode()`) as `HashSet` elements to count distinct points, connecting this week's HashMap internals directly to last week's equals/hashCode contract.

**Why it matters:** shows the equals/hashCode discipline from Week 13 and this week's HashMap bucket mechanics are the same underlying concern, applied together.

## Real output

```
PASS: wordFrequency: exactly 5 distinct words
PASS: wordFrequency: total count across all words equals input size (no lost updates)
PASS: wordFrequency: word0 counted exactly 2000 times
PASS: boundedProduceConsume: all 5000 items consumed, none lost
PASS: boundedProduceConsume: every produced value 0..4999 consumed exactly once
PASS: countDistinctPoints: record equals()/hashCode() correctly dedupes

6/6 assertions passed.
```

## Reproduce

```bash
cd practice/java/week-14/mixed-review
mkdir -p out && javac -d out src/*.java && java -cp out CollectionsCodingPractice
```
