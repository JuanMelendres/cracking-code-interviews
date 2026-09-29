---
title: "Cheat Sheet: Streams and Collectors"
slug: streams-and-collectors
document_type: cheat-sheet
domain: java-core
topic_id: T-107
canonical: ../syllabus/02-java/language-core/streams-and-collectors.md
last_updated: 2026-08-05
---

# Streams and Collectors

**Canonical chapter:** [`syllabus/02-java/language-core/streams-and-collectors.md`](../syllabus/02-java/language-core/streams-and-collectors.md)

## Core Mental Model

A stream is a recipe, not a result — nothing runs until a terminal operation asks for output, and even then only as much of the recipe executes as that terminal operation actually needs. `parallel()` changes *how* the pulling happens (fork/join, across threads), not *whether* the recipe needs to be thread-safe — which is exactly where most parallel-stream bugs live.

## Essential Definitions

- **Intermediate operations** (`filter`, `map`, `peek`) — lazy, build a pipeline description, do no work on their own.
- **Terminal operations** (`collect`, `forEach`, `count`, `findFirst`) — trigger execution; exactly one per stream, after which the stream is "operated upon" and throws `IllegalStateException` on reuse.
- **Short-circuiting** (`findFirst`, `anyMatch`, `limit`) — stop pulling elements from the source once satisfied, without processing the rest.
- **`Collector`** — four building blocks (supplier, accumulator, combiner, finisher) that accumulate stream elements into a result; the combiner exists specifically to merge per-thread partial results correctly under `parallel()`.

## Decision Table

| Question | Answer |
|---|---|
| Is the collection large and per-element work genuinely CPU-heavy? | Only then consider `parallel()` — and measure with a warmed-up benchmark first |
| Does the pipeline write to shared, non-thread-safe state? | Replace with a proper `Collector`, never a plain mutable collection with `forEach` |
| Can a `toMap()` key collide? | Use the 3-arg overload with an explicit merge function |
| Does the terminal op only need some elements? | Use `findFirst`/`anyMatch`/`limit` for the short-circuit benefit |

**Trade-offs:** sequential streams have no coordination overhead but use one core; parallel streams can use multiple cores for large CPU-heavy work but pay fork/join coordination cost and share the common pool with everything else unless given a dedicated pool.

## Key Numbers (real, executed)

Parallel-stream corruption of a plain `ArrayList` under `IntStream.range(0, 100_000).parallel().forEach(sharedList::add)`:

```
Expected size: 100000, actual size: 24494   <- CORRUPTED, lost updates
Collector-based size: 100000                 <- always correct
```

Parallel overhead for small, cheap-per-element work, measured after 20,000-iteration JIT warmup (summing a 1,000-element `IntStream`):

```
Sequential: avg 3,111 ns/iter
Parallel:   avg 20,539 ns/iter    <- ~6.6x SLOWER
```

`toMap()` duplicate-key behavior:

```
IllegalStateException: Duplicate key alice (attempted merging values 100.0 and 75.0)
```

## Common Pitfalls

- Assuming a pipeline executes as it's built rather than only at the terminal operation.
- Using two-argument `Collectors.toMap()` on data that can contain duplicate keys.
- Writing to a shared, non-thread-safe collection inside `parallel().forEach()` — silent data loss, no exception.
- Reflexively adding `.parallel()` "for performance" without a warmed-up benchmark on realistic data volume.

## Interview Answer Skeleton

**30-sec:** A stream pipeline is lazy — nothing executes until a terminal operation runs, and short-circuiting operations stop early. `toMap()` throws on duplicate keys without a merge function. `parallel()` doesn't make shared state thread-safe, and for small/cheap-per-element workloads it can be measurably slower than sequential.

**2-min:** Add why it exists (declarative transformations, runtime decides how much work is necessary) + the real measured evidence (100,000-expected/24,494-actual `ArrayList` corruption; 6.6x parallel slowdown after proper warmup) + the decision framework (only parallelize large, genuinely CPU-heavy work, and measure first).

**Whiteboard:** Source → intermediate ops (labeled "lazy, nothing runs yet") → diamond "terminal op called?" → branch to "pull everything" (count/forEach/collect) vs. "pull only until satisfied" (findFirst/anyMatch/limit). Annotate the branch point as where laziness becomes visible.

**Staff-level framing:** treat `.parallel()` like adding a thread pool or a cache — a change requiring before/after measurement on realistic data, never a reflexive optimization. The shared-common-pool contention risk across unrelated concurrent parallel streams in the same process is the subtler, more consequential version of the same coordination-cost principle at true production scale.

## Production Warning Signs

- p99 latency rises and fleet CPU usage climbs right after a `.parallel()` "optimization" ships — for ~200-element, cheap-per-element pipelines this is the exact 6.6x-slower profile this chapter measures directly, now additionally contending on the shared `ForkJoinPool.commonPool()` across concurrent requests.
- A `HashSet`/list built via `parallel().forEach(sharedCollection::add)` has an unexpectedly small or inconsistent size across runs — classic silent corruption, not a crash.
- **Prevention:** require a warmed-up, realistic-data benchmark before merging any `.parallel()` addition; default to a proper `Collector` for all parallel accumulation.

## Collector Internals (measured)

**Four functions:** supplier (create container) → accumulator (fold one element) → combiner (merge two containers) → finisher (convert to result type).

**The combiner runs only in parallel.** 1,000 elements, instrumented:

```
sequential : supplier=1    accumulator=1000   combiner=0    finisher=1
parallel   : supplier=64   accumulator=1000   combiner=63   finisher=1
```

64 containers on a 10-core machine — "one per core" is not a safe assumption.

**A wrong combiner is invisible sequentially.** `(a, b) -> a` (discards the second partial):

```
sequential: 1000 elements   <- correct, combiner never ran
parallel:     15 elements   <- silent data loss, no exception, reproducible
```

Test a custom collector's combiner **in parallel**, or it is untested.

### `Collector.Characteristics`

| Flag | What the pipeline may skip |
|---|---|
| `IDENTITY_FINISH` | The finisher call itself (measured: 1 invocation without, **0** with) |
| `UNORDERED` | Preserving encounter order across a parallel merge |
| `CONCURRENT` | Per-thread containers + merging — one shared container instead |

Real sets, read from the JDK:

```
toList()                 [IDENTITY_FINISH]     toUnmodifiableList()  (none)
toSet()                  [UNORDERED, IDENTITY_FINISH]
joining()                (none)                counting()            (none)
groupingBy(f)            [IDENTITY_FINISH]
groupingByConcurrent(f)  [CONCURRENT, UNORDERED, IDENTITY_FINISH]
```

`toUnmodifiableList()` and `joining()` declare nothing because they genuinely must transform the container.

**`groupingByConcurrent` is a different strategy, not a faster variant.** 10,000 elements into 4 groups, parallel: `groupingBy` built **67** containers and merged **63** times; `groupingByConcurrent` built **4** and merged **0**. But it returns a `ConcurrentMap`, is `UNORDERED`, and does strictly more work on a sequential stream.

## Collector Selection

| Need | Collector |
|---|---|
| List / immutable list | `toList()` / `toUnmodifiableList()` |
| Unique elements | `toSet()` |
| Concatenate with delimiter | `joining(", ", "[", "]")` |
| Count / sum / mean | `counting()` / `summingInt(f)` / `averagingInt(f)` |
| Count+sum+min+max+avg in one pass | `summarizingInt(f)` |
| Two-way split, **both keys always present** | `partitioningBy(pred)` |
| N-way grouping | `groupingBy(f[, mapFactory][, downstream])` |
| Transform before collecting | `mapping(f, downstream)` |
| Filter **inside** a group, keeping empty groups | `filtering(pred, downstream)` |
| Key-value map | `toMap(k, v[, merge][, mapFactory])` |
| Two collectors, one pass | `teeing(c1, c2, merger)` |
| Post-process the result | `collectingAndThen(c, f)` |

**Mutability, checked:** `Collectors.toList()` is **MUTABLE**; `Stream.toList()` (16+) and `toUnmodifiableList()` throw `UnsupportedOperationException`. Not interchangeable, and one refactor apart.

**`partitioningBy` ≠ `groupingBy` on a boolean** — `partitioningBy` always returns both keys, so `get(false)` cannot be `null`.
**`Collectors.filtering` ≠ `Stream.filter`** — the first keeps empty groups, the second removes them.
**`toMap` default is a `HashMap`** — pass `LinkedHashMap::new` if order matters.

## Related

- `syllabus/02-java/collections/hashmap-internals.md`
- `syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md`
- `syllabus/02-java/language-core/generics-erasure-and-pecs.md`
- [`practice/java/language-core/collectors-internals/`](../practice/java/language-core/collectors-internals/README.md)
