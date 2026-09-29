---
title: "Streams and Collectors"
slug: streams-and-collectors
document_type: handbook-chapter
domain: 02-java/language-core
status: draft
version: 1.1
last_updated: 2026-09-29
source_history:
  - handbook/java-core/streams-and-collectors.md
difficulty:
  - intermediate
  - advanced
target_levels:
  - senior
  - staff
estimated_reading_minutes: 38
topic_id: T-107
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites: []
practice: ../../../practice/java/language-core/collectors-internals/
related:
  - generics-erasure-and-pecs.md
  - ../../../practice/java/language-core/collectors-internals/README.md
  - lambdas-and-functional-interfaces.md
  - optional-and-null-strategy.md
  - ../concurrency/executors-and-thread-pool-sizing.md
  - ../../../study-packs/week-13/01-streams-and-collectors.md
official_references:
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collector.html
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collector.Characteristics.html
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html
---

# Streams and Collectors

> **Topic register:** T-107 · IWI 6.2 · Core tier
> **Provenance:** every trace in this chapter is real, executed output from [`practice/java/week-13/streams-collectors/src/`](../../../practice/java/week-13/streams-collectors/src/) on OpenJDK 21.0.12. The collector-internals material added in v1.1 is measured separately in [`practice/java/language-core/collectors-internals/`](../../../practice/java/language-core/collectors-internals/README.md), where an instrumented `Collector` counts every supplier, accumulator, combiner, and finisher invocation the JDK actually makes.

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Level 1 — Foundation](#level-1-foundation)
4. [Level 2 — Working Knowledge](#level-2-working-knowledge)
5. [Mental Model](#mental-model)
6. [Definition and Purpose](#definition-and-purpose)
7. [Core Concepts](#core-concepts)
8. [Internal Implementation](#internal-implementation)
9. [Diagrams](#diagrams)
10. [Java Examples](#java-examples)
11. [Production Scenarios](#production-scenarios)
12. [Trade-offs](#trade-offs)
13. [Decision Framework](#decision-framework)
14. [Common Mistakes](#common-mistakes)
15. [Anti-Patterns](#anti-patterns)
16. [Best Practices](#best-practices)
17. [Interview Answer Framework](#interview-answer-framework)
18. [Interview Questions](#interview-questions)
19. [Summary](#summary)
20. [Key Takeaways](#key-takeaways)
21. [Cheat Sheet](#cheat-sheet)
22. [Flashcards](#flashcards)
23. [Practice Exercises](#practice-exercises)
24. [Solutions](#solutions)
25. [Additional Reading](#additional-reading)
26. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can:

- Explain, with a measured trace, why a stream pipeline does nothing until a terminal operation runs, and why `findFirst()` short-circuits.
- State exactly why `Collectors.toMap()` throws on duplicate keys, and how a merge function fixes it.
- Explain why a shared, non-thread-safe collection corrupts under `parallel().forEach()`, with a measured corruption count.
- Justify, with properly-warmed-up measurements, why parallel streams can be *slower* than sequential for small or cheap-per-element workloads.

## Why This Matters in Interviews

Streams questions separate candidates who use the fluent API correctly from those who understand its actual execution model. The laziness/short-circuiting behavior and the `parallel()` pitfalls are both near-universal real-world traps — most Java codebases have at least one `parallel()` call added for a speedup that never materialized, or a `toMap()` call one production dataset away from an `IllegalStateException`.

## Level 1 — Foundation

**A stream is a pipeline for processing a collection of items step by step**, instead of writing a manual loop with an intermediate list at every stage. `names.stream().filter(n -> n.startsWith("A")).map(String::toUpperCase).collect(Collectors.toList())` reads as "take these names, keep only the ones starting with A, uppercase each, gather the results into a list" — one readable chain, versus a loop with a manually-managed accumulator list.

Think of it like an assembly line: items pass through a sequence of stations (filter, transform), each doing exactly one thing, and a final station collects the finished output. Reach for a stream whenever the task is naturally "take this collection, keep/transform/combine its elements" — it's often more readable than nested loops with mutable state, though a plain loop can still be clearer for simple iteration or when you need to exit early with complex control flow.

## Level 2 — Working Knowledge

The everyday stream operations a working engineer uses constantly: **`filter(predicate)`** keeps only matching elements; **`map(function)`** transforms each element; **`collect(Collectors.toList())`** (or `toSet()`, `toMap()`) gathers the results; **`forEach(action)`** performs a side effect per element; **`count()`** counts elements.

**A practical, easy-to-hit gotcha**: a stream can only be consumed once — calling a second terminal operation (like `collect()` again) on the same stream throws `IllegalStateException`. If you need to process the same data two different ways, create a fresh stream from the source collection each time, rather than trying to reuse one stream object. And a working default worth stating plainly: don't reach for `.parallel()` by default expecting a free speedup — Section 5's real, measured evidence shows it can make small or cheap-per-element workloads *slower*, not faster.

## Mental Model

**A stream is a recipe, not a result — nothing runs until a terminal operation asks for output, and even then, only as much of the recipe executes as the terminal operation actually needs.** `peek()`-based tracing makes this concrete: elements only flow through the pipeline when someone downstream is actually pulling. `parallel()` changes *how* that pulling happens (across threads via fork/join), not *whether* the recipe itself needs to be thread-safe — which is exactly where most parallel-stream bugs live.

## Definition and Purpose

A **stream** is a sequence of elements supporting functional-style, lazily-evaluated operations, built by chaining zero or more *intermediate* operations (`filter`, `map`, `peek`) and ending in exactly one *terminal* operation (`collect`, `forEach`, `count`, `findFirst`). **Collectors** are the standard mechanism for accumulating a stream's elements into a result (a `List`, a `Map`, a grouped structure) via `Stream.collect(Collector)`.

Streams exist to let element-at-a-time transformations be expressed declaratively, and — because the pipeline is only a description until a terminal operation runs — to let the runtime decide how much work is actually necessary (short-circuiting) and, optionally, whether to parallelize it.

## Core Concepts

### Intermediate operations are lazy; terminal operations trigger execution

Building a pipeline with `filter`/`map`/`peek` does no work at all. Only calling a terminal operation (`count()`, `forEach()`, `collect()`, `findFirst()`) pulls elements through the pipeline.

### Short-circuiting terminal operations can stop pulling early

`findFirst()`, `anyMatch()`, and `limit()` don't need every element — the stream machinery stops requesting more elements from the source the moment the terminal operation is satisfied.

### A stream can only be consumed once

Calling a terminal operation marks the stream "operated upon"; any further operation on the same stream object throws `IllegalStateException`.

### A `Collector` is four functions, and the combiner runs only in parallel

`collect()` is not a conversion. It takes a `Collector<T, A, R>` — a bundle of four functions the pipeline calls at defined points: a **supplier** creating a mutable container, an **accumulator** folding one element into it, a **combiner** merging two containers, and a **finisher** turning the container into the result type.

The part that surprises people is that the combiner is not always used. With an instrumented collector counting real invocations over 1,000 elements:

```text
sequential : supplier=1    accumulator=1000   combiner=0    finisher=1
parallel   : supplier=64   accumulator=1000   combiner=63   finisher=1
```

A sequential stream creates one container and merges nothing — the combiner is **never called**. A parallel stream on this 10-core machine created 64 containers and merged them 63 times; fork/join splits well past the core count, so "one container per core" is not a safe assumption either.

The consequence matters more than the trivia: a custom collector with a wrong combiner passes every sequential test and silently corrupts parallel results. See [Internal Implementation](#internal-implementation) for the measured damage.

### `Collector.Characteristics` is how a collector tells the pipeline what it may skip

Three flags let the stream implementation take shortcuts, and each one is observable.

**`IDENTITY_FINISH`** declares the accumulation container *is* the result, so the pipeline casts it instead of calling the finisher. Not an optimisation of the finisher — the call does not happen. The same instrumented collector declared twice, differing only in that flag:

```text
finisher invocations WITHOUT IDENTITY_FINISH: 1
finisher invocations WITH    IDENTITY_FINISH: 0
```

**`UNORDERED`** declares the result does not depend on encounter order, freeing the pipeline from preserving it across a parallel merge.

**`CONCURRENT`** declares the accumulator is safe to call from multiple threads on a *single shared container*, so the pipeline can skip per-thread containers and merging entirely.

Read from the JDK rather than described:

```text
toList()                   [IDENTITY_FINISH]
toUnmodifiableList()       (none)
toSet()                    [UNORDERED, IDENTITY_FINISH]
joining()                  (none)
counting()                 (none)
groupingBy(f)              [IDENTITY_FINISH]
groupingByConcurrent(f)    [CONCURRENT, UNORDERED, IDENTITY_FINISH]
toMap(k,v)                 [IDENTITY_FINISH]
toConcurrentMap(k,v)       [CONCURRENT, UNORDERED, IDENTITY_FINISH]
```

The contrasts explain themselves. `toList()` hands back its own `ArrayList`, so it can declare `IDENTITY_FINISH`; `toUnmodifiableList()` must copy into an immutable list and declares nothing. `joining()` accumulates into a `StringBuilder` and must convert to `String`, so it has a real finisher too. `toSet()` adds `UNORDERED` because set semantics make encounter order meaningless.

### `groupingByConcurrent` is a different execution strategy, not a faster `groupingBy`

The `CONCURRENT` flag changes what the pipeline does, and the difference is visible in container counts. Grouping 10,000 elements into 4 groups, in parallel, with an instrumented downstream collector:

```text
groupingBy           (parallel)  supplier=67   accumulator=10000  combiner=63   groups=4
groupingByConcurrent (parallel)  supplier=4    accumulator=10000  combiner=0    groups=4
```

The non-concurrent version built 67 containers for 4 groups and merged 63 times. The concurrent version built **exactly 4** — one per group — and merged **zero** times.

That is a real reduction in allocation and merging, but it is not free and it is not a drop-in replacement. `groupingByConcurrent` returns a `ConcurrentMap`, it is `UNORDERED` so encounter order within each group is not preserved, and it is only a win when the stream is genuinely parallel *and* the downstream accumulation is cheap enough that merging was the dominant cost. On a sequential stream it does strictly more work than `groupingBy` for no benefit.

### `Collectors.toMap()` throws on duplicate keys without a merge function

The two-argument `toMap(keyFn, valueFn)` overload has no way to resolve a collision, so it throws. The three-argument overload `toMap(keyFn, valueFn, mergeFn)` requires the caller to state how duplicates combine.

### `parallel()` does not make shared mutable state thread-safe

`parallel().forEach(sharedList::add)` runs `add` calls from multiple threads concurrently; a non-thread-safe collection (a plain `ArrayList`) loses updates under that concurrent access, silently, with no exception.

### Parallel streams have real overhead that can exceed the savings

Splitting work across the fork/join pool costs thread coordination; for small collections or cheap per-element work, that coordination cost can dominate, making `parallel()` a net loss — measurable only after JIT warmup, since a single untuned timing call is not a reliable benchmark.

## Internal Implementation

**Laziness and short-circuiting, measured:**

```
== A stream pipeline does nothing until a terminal operation runs ==
Pipeline built. No peek output above this line yet.
Now calling .count() (a terminal operation):
peek saw: 1
peek saw: 2
peek saw: 3
peek saw: 4
peek saw: 5
count = 2

== Short-circuiting: findFirst() stops pulling elements once satisfied ==
evaluating: 1
evaluating: 2
evaluating: 3
findFirst result = 3
(peek should only have printed 1, 2, 3 -- not the whole 10-element source)

== A stream can only be consumed once ==
IllegalStateException on reuse: stream has already been operated upon or closed
```

**`toMap()` duplicate-key behavior, measured:**

```
== Collectors.toMap() throws on duplicate keys without a merge function ==
IllegalStateException: Duplicate key alice (attempted merging values 100.0 and 75.0)

== Fixed: a merge function tells the collector how to combine duplicates ==
totals per customer: {alice=175.0, bob=50.0, carol=200.0}
```

**Parallel-stream corruption of shared state, measured** — `IntStream.range(0, 100_000).parallel().forEach(sharedList::add)` against a plain `ArrayList`:

```
== A non-thread-safe accumulator corrupted by a parallel stream ==
Expected size: 100000, actual size: 24494  <-- CORRUPTED, lost updates from concurrent ArrayList.add()

== The correct, thread-safe way: a proper collector ==
Collector-based size: 100000 (always correct)
```

**Parallel overhead for small, cheap-per-element work, measured with proper JIT warmup** (20,000 warmup iterations, then 20,000 measured iterations, summing a 1,000-element `IntStream`):

```
Sequential sum=499500, avg over 20,000 iters: 3,111 ns/iter
Parallel   sum=499500, avg over 20,000 iters: 20,539 ns/iter
```

Roughly 6.6x slower for the parallel version, entirely from fork/join task-splitting and thread-handoff overhead — for a workload this small and this cheap per element, there is no computation expensive enough to amortize that cost. A naive, single-shot `nanoTime()` comparison without warmup can even show the opposite result purely from JIT compilation timing, which is itself a lesson in why microbenchmarks need warmup, not just a lesson about streams.

**What a wrong combiner actually costs, measured.** A deliberately broken combiner — `(a, b) -> a`, discarding everything accumulated in the second partial — collecting the same 1,000 elements:

```
== 5. What a BROKEN combiner costs, sequential vs parallel ==
  input size                     : 1000
  sequential with broken combiner: 1000  <- correct, combiner never ran
  parallel   with broken combiner: 15  <- silent data loss (run 1)
  parallel   with broken combiner: 15  <- silent data loss (run 2)
  parallel   with broken combiner: 15  <- silent data loss (run 3)
```

No exception, no warning, 98.5% of the data gone, reproducibly. This is the concrete answer to "why does the combiner matter especially with parallel streams." It is not that the combiner is *more important* in parallel — it is that the sequential path never executes it, so a wrong combiner is undetectable by any sequential test. A custom collector's combiner needs a test that actually runs in parallel, or it is untested.

## Diagrams

```mermaid
flowchart LR
    Source[Stream source] -->|lazy| Filter[filter/map/peek<br/>intermediate ops]
    Filter -->|nothing runs yet| Terminal{Terminal op called?}
    Terminal -->|count/forEach/collect| PullAll[Pull every element]
    Terminal -->|findFirst/anyMatch/limit| PullSome[Pull only until satisfied<br/>-- short-circuit]
```

## Java Examples

```java
// Java 21. A custom Collector via Collector.of -- the four building blocks:
// supplier, accumulator, combiner, finisher.
Collector<Order, double[], String> runningTotalCollector = Collector.of(
        () -> new double[1],                         // supplier: fresh accumulator
        (acc, order) -> acc[0] += order.amount(),     // accumulator: fold one element in
        (acc1, acc2) -> { acc1[0] += acc2[0]; return acc1; }, // combiner: merge parallel partials
        acc -> String.format("$%.2f", acc[0])         // finisher: produce the final result
);
String grandTotal = orders.stream().collect(runningTotalCollector);
```

```java
// Java 21. groupingBy with a downstream collector -- count per group,
// not just the grouped lists themselves.
Map<String, Long> counts = orders.stream()
        .collect(Collectors.groupingBy(Order::customer, Collectors.counting()));
```

**The `Collectors` catalog.** All of the following were run against one shared six-employee dataset; full output in [`catalog-transcript.txt`](../../../practice/java/language-core/collectors-internals/catalog-transcript.txt).

| Collector | Use case | Real output (excerpt) |
|---|---|---|
| `toList()` | Elements as a list | `[Ana, Ben, Cleo, Dev, Eve, Fay]` |
| `toUnmodifiableList()` | Same, immutable | throws on `add` |
| `toSet()` | Unique elements | `[Design, Engineering, Sales]` |
| `joining(", ", "[", "]")` | String concatenation with delimiter/prefix/suffix | `[Ana, Ben, Cleo, Dev, Eve, Fay]` |
| `counting()` | Element count as `Long` | `6` |
| `summingInt(f)` | Total | `547000` |
| `averagingInt(f)` | Mean as `Double` | `36.333333333333336` |
| `summarizingInt(f)` | Count, sum, min, average, max in one pass | `IntSummaryStatistics{count=6, sum=547000, min=72000, average=91166.666667, max=120000}` |
| `minBy(cmp)` / `maxBy(cmp)` | Extremes as `Optional` | `Dev` / `Ben` |
| `partitioningBy(pred)` | Two-way split, always both keys | `{false=[Cleo, Dev, Eve], true=[Ana, Ben, Fay]}` |
| `groupingBy(f)` | N-way grouping | `{Design=[Cleo, Fay], Engineering=[Ana, Ben], Sales=[Dev, Eve]}` |
| `groupingBy(f, downstream)` | Grouping plus per-group reduction | `{Design=2, Engineering=2, Sales=2}` |
| `groupingBy(f, mapFactory, downstream)` | Control the map type | `TreeMap` -> keys sorted |
| `mapping(f, downstream)` | Transform before collecting | names instead of whole objects |
| `filtering(pred, downstream)` | Filter *within* a group, keeping empty groups | `{Design=[Cleo, Fay], Engineering=[Ana], Sales=[Eve]}` |
| `flatMapping(f, downstream)` | One-to-many within a group | `{Engineering=[A, a, B, e, n], ...}` |
| `toMap(k, v)` | Key-value mapping | throws on duplicate keys |
| `toMap(k, v, merge)` | …with a duplicate-key resolution | `{Design=179000, Engineering=215000, Sales=153000}` |
| `toMap(k, v, merge, mapFactory)` | …and a chosen map type | `LinkedHashMap` -> insertion order |
| `reducing(identity, f, op)` | Custom reduction | `547000` |
| `teeing(c1, c2, merger)` | Two collectors, one pass (Java 12+) | `Dev ... Ben` |
| `collectingAndThen(c, f)` | Post-process the collected result | `6` |

Two of these deserve emphasis because they are routinely confused.

`partitioningBy` is not `groupingBy` with a boolean key. It always returns both `true` and `false` entries even when one side is empty, which `groupingBy` does not — code that reads `result.get(false)` cannot NPE with `partitioningBy` and can with `groupingBy`.

`filtering` is not `filter`. Filtering the stream before grouping removes groups that end up empty; `Collectors.filtering` filters *inside* each group and keeps the empty ones. Which you want depends on whether "a department with no matching employees" is a row you need to see.

**Mutability, checked rather than assumed:**

```
toList()                 MUTABLE   (add succeeded)
toUnmodifiableList()     IMMUTABLE (UnsupportedOperationException)
Stream.toList()          IMMUTABLE (UnsupportedOperationException)
```

`Collectors.toList()` and `Stream.toList()` (Java 16+) look interchangeable and are not. Collecting and then mutating works with the first and throws with the second, and the difference is one refactor away. `Collectors.toList()` also makes no guarantee about the list type — only that it is mutable — so relying on it being an `ArrayList` is relying on an implementation detail.

**Complexity note:** a single-pass stream pipeline (`filter`/`map`/`collect`) is `O(n)` in the source size; `sorted()` is `O(n log n)`; `parallel()` changes the constant factor and threading model, not the asymptotic complexity.

## Production Scenarios

### Scenario: a "performance optimization" adding `parallel()` regresses a hot request path

**Symptoms.** A service processes a list of roughly 200 items per request using a stream pipeline. An engineer adds `.parallel()` to "speed it up," and after deployment, p99 latency for that endpoint increases rather than decreases, and CPU utilization across the fleet rises noticeably under load.

**Impact.** A change intended as a performance win becomes a regression, consuming more CPU fleet-wide for worse latency on the affected endpoint.

**Initial hypotheses.** An unrelated deploy caused the regression (checked — the parallel-stream change is the only diff in the release); the workload changed (checked — request shapes are unchanged); the parallel stream's fork/join overhead exceeds the per-element work it parallelizes (correct).

**Evidence.** Profiling shows every request now spawns fork/join tasks competing for the shared common pool, and per-request latency variance increases (some requests wait behind other requests' fork/join tasks sharing the same pool) — exactly the overhead this chapter measures directly for small, cheap-per-element workloads.

**Diagnosis.** 200 elements with cheap per-element transformation is exactly the profile where `parallel()`'s fork/join coordination cost dominates — the same mechanism measured in this chapter's 6.6x-slower benchmark, now at production scale and additionally contending on the shared `ForkJoinPool.commonPool()` across concurrent requests.

**Immediate mitigation.** Revert the `.parallel()` call.

**Permanent remediation.** Establish a rule that `.parallel()` is only added after measuring with a proper warmed-up benchmark on realistic data volume, never applied reflexively as a "should be faster" change; if genuine parallelism is needed for a large, CPU-heavy batch job, use a dedicated `ForkJoinPool` rather than the shared common pool to avoid cross-request contention.

**Alternatives considered.** Tuning the common pool's parallelism level instead of removing `.parallel()` — rejected, since the fundamental issue (per-element work too cheap to amortize coordination cost) isn't fixed by pool sizing.

**Trade-offs.** None — reverting a change that measurably regressed both latency and CPU cost has no downside here.

**Prevention.** Treat `parallel()` as requiring the same measurement discipline as any other performance change: a warmed-up, realistic benchmark before merging, not an assumption that "parallel" implies "faster."

**Interview lesson.** This is the production-scale version of this chapter's own measured benchmark: `parallel()` added without measurement, regressing exactly the way the chapter's warmed-up numbers predict for small, cheap-per-element work.

## Trade-offs

| Choice | Benefit | Cost |
|---|---|---|
| Sequential stream | No coordination overhead, predictable | Single-threaded — doesn't use multiple cores |
| Parallel stream | Can use multiple cores for large, CPU-heavy work | Fork/join coordination overhead; can regress small/cheap workloads; shares the common pool with everything else unless a custom pool is used |
| `toMap()` (2-arg) | Simplest form | Throws on any duplicate key |
| `toMap()` (3-arg, with merge function) | Handles duplicates explicitly | Requires the caller to decide the merge semantics up front |

## Decision Framework

1. **Is the collection large and is the per-element work genuinely CPU-heavy?** Only then consider `parallel()` — and measure with a warmed-up benchmark before keeping it.
2. **Does the stream pipeline write to any shared, non-thread-safe state** (a plain collection, a mutable field)? If using `parallel()`, that state must be replaced with a proper collector or an atomic/concurrent structure — never assume `forEach` serializes the writes.
3. **Can the key ever collide** in a `toMap()` call? Use the 3-argument overload with an explicit merge function rather than risking `IllegalStateException` in production.
4. **Does the terminal operation only need some elements** (existence check, first match)? Use `findFirst`/`anyMatch`/`limit` to get the short-circuiting benefit rather than collecting everything first.

## Common Mistakes

- Assuming a stream pipeline executes as it's being built, rather than only at the terminal operation.
- Using `Collectors.toMap()` without a merge function on data that can contain duplicate keys.
- Writing to a shared, non-thread-safe collection inside a `parallel().forEach()`.
- Adding `.parallel()` without measuring, assuming "parallel" always means "faster."
- Writing a custom collector's combiner without ever testing it in parallel. The sequential path never calls it — measured — so a combiner that discards one side passes every sequential test and loses 98.5% of the data in parallel, with no exception.
- Assuming `Collectors.toList()` and `Stream.toList()` are interchangeable. The first is mutable, the second is not.
- Relying on `Collectors.toList()` returning an `ArrayList`. Only mutability is specified.
- Reaching for `groupingByConcurrent` as a faster `groupingBy`. It returns a `ConcurrentMap`, is `UNORDERED`, and only helps when the stream is genuinely parallel and merging was the dominant cost.
- Using `groupingBy` with a boolean key where `partitioningBy` is meant — `groupingBy` omits the empty side, so `get(false)` can return `null`.
- Confusing `Collectors.filtering` with `Stream.filter`: the first keeps empty groups, the second removes them.
- Assuming `toMap`'s result preserves insertion order. The default is a `HashMap`; pass a map factory if order matters.

## Anti-Patterns

- **`parallel().forEach(sharedMutableCollection::add)`** — a specific, common instance of the shared-state corruption this chapter measures directly.
- **Reflexively adding `.parallel()`** to any stream "for performance" without a warmed-up benchmark showing an actual improvement for the real data size.
- **Reusing a `Stream` reference** after a terminal operation has already consumed it.

## Best Practices

- Measure before adding `.parallel()`, with proper JIT warmup and realistic data volume — never assume it helps.
- Use a proper `Collector` (or a concurrent/atomic accumulator) for any parallel accumulation, never a plain mutable collection.
- Default to the 3-argument `Collectors.toMap()` with an explicit merge function whenever key collisions are even plausible.
- Prefer short-circuiting terminal operations (`findFirst`, `anyMatch`, `limit`) when the full result set isn't actually needed.

## Interview Answer Framework

### 30-Second Answer

A stream pipeline is lazy — intermediate operations build a recipe, and nothing executes until a terminal operation runs, with short-circuiting terminal operations (`findFirst`) stopping early. `Collectors.toMap()` throws on duplicate keys without a merge function. `parallel()` doesn't make shared state thread-safe — measured directly: a plain `ArrayList` loses updates under `parallel().forEach()` — and for small or cheap-per-element workloads, parallel overhead can make it measurably slower than sequential.

### 2-Minute Answer

Definition: a stream chains lazy intermediate operations ending in one terminal operation; collectors accumulate results. Why it exists: lets transformations be expressed declaratively while letting the runtime decide how much work is actually necessary. How it works: nothing runs until the terminal operation; short-circuiting operations stop early; `parallel()` changes the execution model (fork/join) but not the need for thread-safety. One important trade-off: parallel streams have real coordination overhead that can exceed their benefit for small/cheap workloads. Production example: a real measured 6.6x slowdown from adding `parallel()` to a small, cheap-per-element pipeline after proper JIT warmup, and a real ArrayList corruption (100,000 expected, ~24,000 actual) from a non-thread-safe accumulator under `parallel().forEach()`.

### 10-Minute Deep Dive

Cover, in order: the mental model — a stream is a recipe, not a result (mental model); the measured laziness/short-circuiting trace (internals, real evidence); `toMap()`'s duplicate-key behavior and the merge-function fix (internals, real evidence); the measured parallel-stream shared-state corruption (internals, real evidence); the measured, warmed-up parallel-vs-sequential overhead comparison (internals, real evidence); the decision framework for when `parallel()` is actually justified (decision framework); and close with the production scenario — a reflexive `parallel()` addition regressing a hot path exactly as the chapter's benchmark predicts.

### Whiteboard Explanation

Draw the [§ Diagrams](#diagrams) flowchart: source → intermediate ops (labeled "lazy, nothing runs yet") → a diamond "terminal op called?" branching to "pull everything" (count/forEach/collect) versus "pull only until satisfied" (findFirst/anyMatch/limit). Annotate the branch point: "this is where laziness becomes visible — before this, nothing has executed."

### Production Example

The `parallel()` regression in [§ Production Scenarios](#production-scenarios): adding `.parallel()` to a small, cheap-per-element hot-path pipeline increased both latency and fleet-wide CPU usage, exactly matching this chapter's own measured 6.6x overhead finding.

### Trade-offs to Mention

State unprompted: `parallel()` is not a free performance win and needs measurement; a stream can only be consumed once; `toMap()`'s duplicate-key behavior needs an explicit merge function decision, not a hope that data never collides.

### Common Candidate Mistakes

Assuming stream operations execute as the pipeline is built rather than at the terminal operation; assuming `parallel()` always improves performance; writing to a shared collection inside a parallel stream without considering thread-safety.

### Typical Follow-Up Questions

1. "Your `parallel()` change made things slower. Why might that happen?"
2. "How would you accumulate results safely from a parallel stream instead of a shared list?"

### Senior-Level Expectations

Correctly explains stream laziness and short-circuiting with a concrete example; identifies that `parallel()` requires thread-safe accumulation.

### Staff-Level Discussion

The 6.6x measured slowdown from adding `parallel()` to a small, cheap workload is a specific instance of a general principle: any concurrency mechanism has a fixed coordination cost, and that cost only pays for itself past some workload-size threshold that must be measured, not assumed. A Staff engineer treats "add `.parallel()`" the same way they'd treat "add a thread pool" or "add a cache" — as a change requiring a before/after measurement on realistic data, not a reflexive optimization. The shared-common-pool contention risk (multiple unrelated parallel streams across a service competing for the same fork/join pool) is the less obvious, more consequential version of this same principle at true production scale.

## Interview Questions

### Question 1 — Your `parallel()` change made things slower. Why might that happen?

**Why interviewers ask it.** Tests whether the candidate understands that parallelism has a real, measurable coordination cost, not just abstract concurrency benefits.

**Expected answer.** Fork/join task-splitting and thread-handoff overhead can exceed the savings for small collections or cheap per-element work; the fix is measuring with proper warmup before adding `parallel()`, not assuming it helps.

**Minimum acceptable answer.** States that parallel streams have overhead, even without the fork/join specifics.

**Strong Senior answer.** Correctly explains the coordination-cost-vs-workload-size trade-off.

**Staff-level extension.** Names the shared-common-pool contention risk across unrelated concurrent parallel streams in the same process, and proposes measuring with a proper warmed-up benchmark before any such change ships.

**Common mistakes.** Assuming `parallel()` always improves performance regardless of workload size or per-element cost.

**Likely follow-ups.** "How would you benchmark this properly?"

**Evaluation criteria (1–5).** 1: no explanation, "should be faster." 3: names fork/join overhead as the cause. 5: correct cause plus the shared-pool contention risk and proper measurement discipline.

**Related references.** [§ Internal Implementation](#internal-implementation); [§ Production Scenarios](#production-scenarios).

---

### Question 2 — How would you accumulate results safely from a parallel stream instead of a shared list?

**Why interviewers ask it.** Tests whether the candidate knows the correct fix, not just that the bug exists.

**Expected answer.** Use a proper `Collector` (`Collectors.toList()`, `Collectors.toMap()` with a merge function, or a custom `Collector.of(...)`) rather than a shared mutable collection with `forEach`.

**Minimum acceptable answer.** States that `forEach` with a shared collection is unsafe under `parallel()`.

**Strong Senior answer.** Proposes `.collect(Collectors.toList())` or an equivalent proper collector.

**Staff-level extension.** Explains why: collectors are designed with a combiner step specifically to merge per-thread partial results correctly, which a shared mutable collection's `add()` calls cannot do safely under concurrent access.

**Common mistakes.** Reaching for a manually-synchronized shared list instead of the built-in collector mechanism designed for exactly this.

**Likely follow-ups.** "What does the combiner step of a custom Collector actually do?"

**Evaluation criteria (1–5).** 1: doesn't recognize the shared-list approach is unsafe. 3: proposes a proper collector. 5: correct proposal plus explains the combiner mechanism.

**Related references.** [§ Internal Implementation](#internal-implementation); [§ Java Examples](#java-examples).

### Question 3 — If you write a custom `Collector`, what is the purpose of the combiner, and why does it become especially important with parallel streams?

**Why interviewers ask it.** It is the one question that cannot be answered from having *used* collectors. Anyone who has only called `Collectors.toList()` has never needed the combiner to exist, so the answer reveals whether the candidate understands the model or just the syntax.

**Expected answer.** A `Collector` is four functions: a supplier creating a mutable container, an accumulator folding one element into it, a combiner merging two containers, and a finisher converting the container to the result type. The combiner exists because a parallel stream splits the source, accumulates each chunk into its **own** container, and must then merge those partial results into one.

The sharp point is that the combiner is **not called at all** in a sequential stream — verified by instrumenting a collector and counting invocations: `combiner=0` sequentially, `combiner=63` in parallel over the same 1,000 elements on a 10-core machine (which also created 64 containers, so "one per core" is not a safe assumption). It is not that the combiner matters *more* in parallel; it is that the sequential path never executes it.

**Minimum acceptable answer.** Knows the combiner merges partial results and that this relates to parallelism.

**Strong Senior answer.** The above, plus the practical consequence: a wrong combiner is undetectable by any sequential test. Measured — a combiner of `(a, b) -> a`, which discards the second partial, returned all 1,000 elements sequentially and **15** in parallel, reproducibly, with no exception. So a custom collector's combiner needs a test that actually runs in parallel, or it is untested. Also knows the combiner must be associative and side-effect-free for the merge to be correct in any split order.

**Staff-level extension.** Brings in `Collector.Characteristics` as the mechanism by which a collector tells the pipeline what it may skip — `IDENTITY_FINISH` means the finisher call does not happen at all (measured: 1 invocation without the flag, 0 with it), and `CONCURRENT` means the accumulator is safe on a single shared container, so per-thread containers and merging are skipped entirely (measured: `groupingByConcurrent` built 4 containers and merged 0 times where `groupingBy` built 67 and merged 63). Then argues the governance point: a hand-written collector is a concurrency primitive disguised as a utility method, and the correct default is to compose built-in collectors — which are tested against the JDK's own splitting behaviour — rather than to write one.

**Common mistakes.** Describing the combiner as "it combines the results" without saying which results or when. Claiming it runs in sequential streams. Assuming one container per core. Writing a combiner that mutates and returns the second argument, or that is not associative.

**Likely follow-ups.** "Does the combiner run in a sequential stream?" (No — measured at zero.) "How would you test a custom collector?" (In parallel, over a source large enough to actually split.) "What does `IDENTITY_FINISH` buy you?" (The finisher is skipped, not merely cheap.)

**Evaluation criteria (1–5).** 1: cannot describe the collector model. 3: names the four functions and that the combiner merges partials in parallel. 5: knows the combiner is never called sequentially, can state the testing consequence that follows, and connects it to `Characteristics`.

## Summary

A stream pipeline is lazy — nothing executes until a terminal operation runs, measured directly via `peek()` tracing, and short-circuiting operations like `findFirst()` stop pulling elements once satisfied. `Collectors.toMap()` throws on duplicate keys without an explicit merge function. `parallel()` does not make shared state thread-safe — a plain `ArrayList` measurably loses updates under concurrent `forEach`-based writes — and for small or cheap-per-element workloads, parallel overhead can make execution measurably slower than sequential, visible only after proper JIT warmup.

## Key Takeaways

- Streams are lazy: intermediate operations build a pipeline, terminal operations execute it.
- Short-circuiting terminal operations (`findFirst`, `anyMatch`, `limit`) stop pulling elements once satisfied.
- `Collectors.toMap()` throws on duplicate keys unless given an explicit merge function.
- `parallel()` requires thread-safe accumulation and real measurement — it is not a free performance win.
- A `Collector` is four functions — supplier, accumulator, combiner, finisher — and the combiner is **never called** in a sequential stream, so a wrong one is undetectable without a parallel test.
- `Collector.Characteristics` is how a collector tells the pipeline what it may skip: `IDENTITY_FINISH` means the finisher call does not happen, `CONCURRENT` means one shared container instead of per-thread containers plus merging, `UNORDERED` frees the pipeline from preserving encounter order.
- `Collectors.toList()` is mutable; `Stream.toList()` and `Collectors.toUnmodifiableList()` are not.

## Cheat Sheet

| Need | Approach |
|---|---|
| Accumulate stream results safely, possibly in parallel | A proper `Collector`, never a shared mutable collection with `forEach` |
| Build a `Map` from a stream with possible duplicate keys | `Collectors.toMap(keyFn, valueFn, mergeFn)` |
| Stop as soon as one match is found | `findFirst()` / `anyMatch()`, not `collect()` then check |
| Decide whether to add `.parallel()` | Measure first, with warmup, on realistic data size — never assume |

## Flashcards

### Card: When a stream pipeline actually executes

**Prompt:**
When does a stream pipeline actually execute?

**Answer:**
Only when a terminal operation is called — intermediate operations (filter, map, peek) build a lazy pipeline that does nothing on its own.

**Why it matters:**
Explains why `peek()`-based debugging can look confusing if you expect output immediately.

**Common trap:**
Assuming intermediate operations run as soon as they're called.

**Related:**
[Internal Implementation](#internal-implementation)

### Card: Why toMap() throws on duplicates

**Prompt:**
Why does `Collectors.toMap()` throw on duplicate keys by default?

**Answer:**
The two-argument overload has no way to resolve a collision; the three-argument overload requires an explicit merge function.

**Why it matters:**
A common production `IllegalStateException` waiting to happen on real-world data.

**Common trap:**
Using the two-argument `toMap()` on data that could plausibly contain duplicate keys.

**Related:**
[Internal Implementation](#internal-implementation)

### Card: What parallel() does and doesn't do

**Prompt:**
Does `parallel()` make a stream's writes to shared state thread-safe?

**Answer:**
No — measured directly: a plain `ArrayList` loses updates under `parallel().forEach()`. Use a proper collector instead.

**Why it matters:**
A silent, no-exception data-loss bug, not a crash — easy to miss without a size check.

**Common trap:**
Assuming `parallel()` handles thread-safety of the stream's own side effects.

**Related:**
[Production Scenarios](#production-scenarios)

### Card: Does the combiner run in a sequential stream?

**Prompt:**
You write a custom `Collector`. In a sequential stream, how many times is the combiner called?

**Answer:**
Zero. Measured with an instrumented collector over 1,000 elements: `combiner=0` sequentially, `combiner=63` in parallel on a 10-core machine (which also created 64 containers, not 10). A sequential stream accumulates into one container and has nothing to merge.

**Why it matters:**
A wrong combiner is undetectable by any sequential test. A combiner of `(a, b) -> a` returned all 1,000 elements sequentially and **15** in parallel, reproducibly, with no exception — 98.5% silent data loss.

**Common trap:**
Testing a custom collector only sequentially, then enabling `parallel()` later and corrupting results.

**Related:**
[Core Concepts](#core-concepts)

### Card: What does IDENTITY_FINISH actually do?

**Prompt:**
`Collectors.toList()` declares `IDENTITY_FINISH`. What does that change at runtime?

**Answer:**
The finisher is **not called at all** — the pipeline casts the accumulation container to the result type instead. Measured: the same instrumented collector recorded 1 finisher invocation without the flag and **0** with it. It is not an optimisation of the finisher; the call does not happen.

**Why it matters:**
It explains the real characteristic sets: `toList()` is `[IDENTITY_FINISH]` because it hands back its own `ArrayList`, while `toUnmodifiableList()` declares nothing because it must copy into an immutable list, and `joining()` declares nothing because it must turn a `StringBuilder` into a `String`.

**Common trap:**
Reading "the finisher is identity" as "the finisher is cheap."

**Related:**
[Core Concepts](#core-concepts)

### Card: groupingBy vs groupingByConcurrent

**Prompt:**
What does the `CONCURRENT` characteristic actually change, and when is `groupingByConcurrent` worth it?

**Answer:**
`CONCURRENT` declares the accumulator safe to call from many threads on a **single shared container**, so the pipeline skips per-thread containers and merging. Measured, grouping 10,000 elements into 4 groups in parallel: `groupingBy` built **67** containers and merged **63** times; `groupingByConcurrent` built **4** and merged **0**.

**Why it matters:**
It is a different execution strategy, not a faster variant. It returns a `ConcurrentMap`, it is `UNORDERED` so within-group encounter order is not preserved, and on a sequential stream it does strictly more work for no benefit.

**Common trap:**
Swapping it in as a drop-in "parallel-friendly" replacement without a parallel stream, or where encounter order matters.

**Related:**
[Core Concepts](#core-concepts)

### Card: Collectors.toList() vs Stream.toList()

**Prompt:**
Are `stream().collect(Collectors.toList())` and `stream().toList()` interchangeable?

**Answer:**
No. Checked directly: `Collectors.toList()` returns a **mutable** list (an `add` succeeds), while `Stream.toList()` (Java 16+) and `Collectors.toUnmodifiableList()` both throw `UnsupportedOperationException`. Also, `Collectors.toList()` guarantees only mutability, not that the result is an `ArrayList`.

**Why it matters:**
Code that collects and then mutates works with one and throws with the other, and the difference is one "modernise this" refactor away.

**Common trap:**
Treating `Stream.toList()` as pure syntax sugar for the older form.

**Related:**
[Java Examples](#java-examples)

## Practice Exercises

1. Reproduce all three demos: [`StreamLazinessDemo.java`](../../../practice/java/week-13/streams-collectors/src/StreamLazinessDemo.java), [`ParallelStreamPitfallDemo.java`](../../../practice/java/week-13/streams-collectors/src/ParallelStreamPitfallDemo.java), [`CustomCollectorDemo.java`](../../../practice/java/week-13/streams-collectors/src/CustomCollectorDemo.java).
2. Modify `ParallelStreamPitfallDemo` to use a much larger, genuinely CPU-heavy per-element computation (e.g., checking primality of large numbers) and confirm whether `parallel()` becomes faster than sequential at that workload size.
3. Write a custom `Collector` that computes both the min and max of a stream of `Integer` in a single pass, using an `int[]` accumulator.

## Solutions

**Exercise 1.** Expected output matches this chapter's measured traces exactly, including the ~24,000-of-100,000 corrupted `ArrayList` size (the exact number varies run to run due to timing, but is reliably far below 100,000) and the ~6.6x parallel slowdown after warmup.

**Exercise 2.** With a genuinely CPU-heavy per-element computation (e.g., trial-division primality checking on numbers in the millions), `parallel()` should measurably outperform sequential once the per-element cost is high enough to amortize the fork/join coordination overhead — confirming the decision framework's "large and CPU-heavy" criterion rather than a blanket rule against `parallel()`.

**Exercise 3.** An `int[2]` accumulator (`{Integer.MAX_VALUE, Integer.MIN_VALUE}`) updated via `Math.min`/`Math.max` in the accumulator step, merged via `Math.min`/`Math.max` in the combiner step, and finished by returning both values (e.g., as an `int[]` or a small record) computes min and max in a single pass without two separate stream traversals.

## Additional Reading

- [Martin Fowler-adjacent] Oracle's own Streams tutorial: [The Java Tutorials — Aggregate Operations](https://docs.oracle.com/javase/tutorial/collections/streams/)
- [Lambdas and Functional Interfaces](lambdas-and-functional-interfaces.md) — the mechanism (capture rules, `invokedynamic`, method references) underneath every lambda used throughout this chapter.
- [Optional and Null Strategy](optional-and-null-strategy.md) — shares `map`/`filter`/`flatMap` vocabulary directly with the `Stream` API in this chapter.

## Official References

- [java.util.stream.Stream (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html)
- [java.util.stream.Collectors (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html)
