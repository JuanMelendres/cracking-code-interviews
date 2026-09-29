---
title: "Flashcards: Streams and Collectors"
slug: streams-and-collectors
document_type: flashcard-deck
domain: java-core
topic_id: T-107
canonical: ../syllabus/02-java/language-core/streams-and-collectors.md
last_updated: 2026-08-06
---

# Flashcards: Streams and Collectors

**Canonical chapter:** [`syllabus/02-java/language-core/streams-and-collectors.md`](../syllabus/02-java/language-core/streams-and-collectors.md)

## Card: When a stream pipeline actually executes

**Prompt:**
When does a stream pipeline actually execute?

**Answer:**
Only when a terminal operation is called — intermediate operations (filter, map, peek) build a lazy pipeline that does nothing on its own.

**Why it matters:**
Explains why `peek()`-based debugging can look confusing if you expect output immediately.

**Common trap:**
Assuming intermediate operations run as soon as they're called.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/streams-and-collectors.md#internal-implementation)

## Card: Why toMap() throws on duplicates

**Prompt:**
Why does `Collectors.toMap()` throw on duplicate keys by default?

**Answer:**
The two-argument overload has no way to resolve a collision; the three-argument overload requires an explicit merge function.

**Why it matters:**
A common production `IllegalStateException` waiting to happen on real-world data.

**Common trap:**
Using the two-argument `toMap()` on data that could plausibly contain duplicate keys.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/streams-and-collectors.md#internal-implementation)

## Card: What parallel() does and doesn't do

**Prompt:**
Does `parallel()` make a stream's writes to shared state thread-safe?

**Answer:**
No — measured directly: a plain `ArrayList` loses updates under `parallel().forEach()`. Use a proper collector instead.

**Why it matters:**
A silent, no-exception data-loss bug, not a crash — easy to miss without a size check.

**Common trap:**
Assuming `parallel()` handles thread-safety of the stream's own side effects.

**Related:**
[Production Scenarios](../syllabus/02-java/language-core/streams-and-collectors.md#production-scenarios)

## Card: Does the combiner run in a sequential stream?

**Prompt:**
You write a custom `Collector`. In a sequential stream, how many times is the combiner called?

**Answer:**
Zero. Measured with an instrumented collector over 1,000 elements: `combiner=0` sequentially, `combiner=63` in parallel on a 10-core machine (which also created 64 containers, not 10). A sequential stream accumulates into one container and has nothing to merge.

**Why it matters:**
A wrong combiner is undetectable by any sequential test. A combiner of `(a, b) -> a` returned all 1,000 elements sequentially and **15** in parallel, reproducibly, with no exception — 98.5% silent data loss.

**Common trap:**
Testing a custom collector only sequentially, then enabling `parallel()` later and corrupting results.

**Related:**
[Core Concepts](../syllabus/02-java/language-core/streams-and-collectors.md#core-concepts)

## Card: What does IDENTITY_FINISH actually do?

**Prompt:**
`Collectors.toList()` declares `IDENTITY_FINISH`. What does that change at runtime?

**Answer:**
The finisher is **not called at all** — the pipeline casts the accumulation container to the result type instead. Measured: the same instrumented collector recorded 1 finisher invocation without the flag and **0** with it. It is not an optimisation of the finisher; the call does not happen.

**Why it matters:**
It explains the real characteristic sets: `toList()` is `[IDENTITY_FINISH]` because it hands back its own `ArrayList`, while `toUnmodifiableList()` declares nothing because it must copy into an immutable list, and `joining()` declares nothing because it must turn a `StringBuilder` into a `String`.

**Common trap:**
Reading "the finisher is identity" as "the finisher is cheap."

**Related:**
[Core Concepts](../syllabus/02-java/language-core/streams-and-collectors.md#core-concepts)

## Card: groupingBy vs groupingByConcurrent

**Prompt:**
What does the `CONCURRENT` characteristic actually change, and when is `groupingByConcurrent` worth it?

**Answer:**
`CONCURRENT` declares the accumulator safe to call from many threads on a **single shared container**, so the pipeline skips per-thread containers and merging. Measured, grouping 10,000 elements into 4 groups in parallel: `groupingBy` built **67** containers and merged **63** times; `groupingByConcurrent` built **4** and merged **0**.

**Why it matters:**
It is a different execution strategy, not a faster variant. It returns a `ConcurrentMap`, it is `UNORDERED` so within-group encounter order is not preserved, and on a sequential stream it does strictly more work for no benefit.

**Common trap:**
Swapping it in as a drop-in "parallel-friendly" replacement without a parallel stream, or where encounter order matters.

**Related:**
[Core Concepts](../syllabus/02-java/language-core/streams-and-collectors.md#core-concepts)

## Card: Collectors.toList() vs Stream.toList()

**Prompt:**
Are `stream().collect(Collectors.toList())` and `stream().toList()` interchangeable?

**Answer:**
No. Checked directly: `Collectors.toList()` returns a **mutable** list (an `add` succeeds), while `Stream.toList()` (Java 16+) and `Collectors.toUnmodifiableList()` both throw `UnsupportedOperationException`. Also, `Collectors.toList()` guarantees only mutability, not that the result is an `ArrayList`.

**Why it matters:**
Code that collects and then mutates works with one and throws with the other, and the difference is one "modernise this" refactor away.

**Common trap:**
Treating `Stream.toList()` as pure syntax sugar for the older form.

**Related:**
[Java Examples](../syllabus/02-java/language-core/streams-and-collectors.md#java-examples)
