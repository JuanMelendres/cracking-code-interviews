# Collectors Internals — Real, Executed Demo

Backs the collector-internals material in [Streams and Collectors](../../../../syllabus/02-java/language-core/streams-and-collectors.md) (v1.1).

Pure JDK, no dependencies. OpenJDK 21.0.12, 10 cores.

## Run it

```bash
mkdir -p out
javac -d out src/*.java
java -cp out CollectorLifecycleDemo    # -> lifecycle-transcript.txt
java -cp out CollectorCatalogDemo      # -> catalog-transcript.txt
```

## What `CollectorLifecycleDemo` proves

It instruments a `Collector` so every supplier, accumulator, combiner, and finisher call is counted. Every claim below is an invocation count the JDK produced, not an assertion about what should happen.

**1. The combiner is never called in a sequential stream.** Collecting 1,000 elements:

```text
sequential : supplier=1    accumulator=1000   combiner=0    finisher=1
parallel   : supplier=64   accumulator=1000   combiner=63   finisher=1
```

Sequential makes one container and merges nothing. Parallel made **64** containers and merged them **63** times on a 10-core machine — the fork/join framework splits well past the core count.

**2. `IDENTITY_FINISH` genuinely skips the finisher; it does not merely make it cheap.** The same instrumented collector declared twice, differing only in that one characteristic:

```text
finisher invocations WITHOUT IDENTITY_FINISH: 1
finisher invocations WITH    IDENTITY_FINISH: 0
```

**3. The built-in collectors' real characteristic sets**, read from the JDK rather than described:

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

The contrasts are the interesting part. `toList()` can hand back its own `ArrayList`, so it declares `IDENTITY_FINISH`; `toUnmodifiableList()` must copy into an immutable list, so it declares nothing. `joining()` accumulates into a `StringBuilder` and must convert to `String`, so it has a real finisher too. `toSet()` adds `UNORDERED` because set semantics make encounter order meaningless, which frees the pipeline from preserving it.

**4. `CONCURRENT` means one shared container, not merged partials.** Grouping 10,000 elements into 4 groups, in parallel, with an instrumented downstream collector:

```text
groupingBy           (parallel)  supplier=67   accumulator=10000  combiner=63   groups=4
groupingByConcurrent (parallel)  supplier=4    accumulator=10000  combiner=0    groups=4
```

The non-concurrent version built 67 containers for 4 groups and merged 63 times. The concurrent version built **exactly 4** — one per group — and merged **zero** times, because a `CONCURRENT` collector is permitted to accumulate into a single shared container from every thread.

**5. A wrong combiner is invisible sequentially and catastrophic in parallel.** A deliberately broken combiner, `(a, b) -> a`, which discards everything accumulated in the second partial:

```text
input size                     : 1000
sequential with broken combiner: 1000  <- correct, combiner never ran
parallel   with broken combiner: 15    <- silent data loss (run 1)
parallel   with broken combiner: 15    <- silent data loss (run 2)
parallel   with broken combiner: 15    <- silent data loss (run 3)
```

No exception. No warning. 98.5% of the data gone, reproducibly. This is the concrete answer to "why does the combiner matter especially with parallel streams": a custom collector can pass every sequential test it has and still be broken, because the sequential path never executes the function that is wrong.

## What `CollectorCatalogDemo` proves

Twenty-four `Collectors` factory methods run against one shared six-employee dataset so the outputs are directly comparable — `joining` with and without delimiters, `summarizingInt`, `partitioningBy`, `groupingBy` with downstream collectors and with a map factory, `filtering`, `flatMapping`, `toMap` with a merge function and a map supplier, `reducing`, `teeing`, and `collectingAndThen`. Full output in `catalog-transcript.txt`.

Two results are worth pulling out.

**Map ordering is a choice you make, not something you get.** The same `toMap` twice:

```text
toMap(name, salary)              {Cleo=88000, Dev=72000, Ana=95000, Eve=81000, Ben=120000, Fay=91000}
toMap(..., LinkedHashMap::new)   {Ana=95000, Ben=120000, Cleo=88000, Dev=72000, Eve=81000, Fay=91000}
```

The default `HashMap` scrambles insertion order. The four-argument form takes a map supplier.

**Mutability, checked rather than assumed:**

```text
toList()                 MUTABLE   (add succeeded)
toUnmodifiableList()     IMMUTABLE (UnsupportedOperationException)
Stream.toList()          IMMUTABLE (UnsupportedOperationException)
```

`Collectors.toList()` and `Stream.toList()` (Java 16+) look interchangeable and are not: one returns a mutable `ArrayList`, the other an immutable list. Code that collects and then mutates works with the first and throws with the second, and the difference is one refactor away.
