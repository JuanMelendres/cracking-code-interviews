# ForkJoinPool and work-stealing (T-408) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md`](../../../../syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md)
(T-408). Three independent demos: a real, measured fork/join speedup with correctness verification,
a real proof of work-stealing itself via the JDK's own `ForkJoinPool.getStealCount()` metric, and a
real check that corrected an inaccurate first-draft assumption about virtual threads sharing
`ForkJoinPool.commonPool()`.

## Setup and run

```bash
cd practice/java/concurrency/forkjoinpool-and-work-stealing
mkdir -p out
javac -d out src/*.java
java -cp out ParallelSumDemo
java -cp out WorkStealingProofDemo
java -cp out CommonPoolVsVirtualThreadCarrierDemo
```

No special flags needed.

## Real observed output (last run)

### `ParallelSumDemo` — real, verified correctness AND real measured speedup

```
Available processors: 10, pool parallelism: 10
Sequential result: -984.4970929539736 (1236ms)
Parallel result:   -984.4970929534542 (193ms)
Results match (within floating-point tolerance): true
Real measured speedup: 6.40x
```

A `RecursiveTask<Double>` fork/join computation over 20,000,000 elements, each requiring a
genuinely CPU-bound operation (`sqrt`/`sin`/`cos`, not plain addition, which is memory-bandwidth-
bound and shows little real parallel speedup). Correctness is verified first — the parallel and
sequential results agree within floating-point tolerance — before any timing claim. On this
10-core machine, the real measured speedup was 6.40x, well below the theoretical 10x ceiling
(real fork/join coordination overhead, plus the sequential-threshold leaf work), but a real,
substantial, measured improvement.

### `WorkStealingProofDemo` — real proof that stealing actually happens, via the JDK's own metric

```
== 4-worker pool (stealing possible) ==
Total leaf tasks executed: 4004 (expected 4004)
Real elapsed: 5ms
Real ForkJoinPool.getStealCount(): 9

== 1-worker pool (NO other worker to steal from -- real control) ==
Total leaf tasks executed: 4004 (expected 4004)
Real elapsed: 0ms
Real ForkJoinPool.getStealCount(): 1
```

A deliberately unbalanced task tree (one branch spawns 4,000 cheap leaf tasks, its sibling spawns
only 4) is submitted to a small, fixed-size pool. With 4 real workers, `getStealCount()` — a real,
public JDK metric, not an inferred assumption — is consistently positive (runs observed in the
8–14 range), direct proof that a worker finishing its light branch early actually stole queued
tasks off a busier sibling's own deque instead of sitting idle.

The 1-worker control run is the more interesting real finding: `getStealCount()` is not 0, but
exactly 1, every run — because `pool.invoke()` is called from the *external* main thread (not a
`ForkJoinWorkerThread`), and the JDK's own implementation counts the sole worker's handoff of that
externally-submitted root task as a steal too. This is a real, honest nuance worth knowing rather
than the oversimplified "no other worker exists, so the count should be zero" — the count reflects
*any* task moving from one queue to another via the steal mechanism, including the very first
external-to-internal handoff, not only inter-worker theft.

### `CommonPoolVsVirtualThreadCarrierDemo` — a real check that corrected an inaccurate first-draft claim

```
Virtual thread's own toString(): VirtualThread[#20]/runnable@ForkJoinPool-1-worker-1

commonPool identity hash: 1159190947
commonPool.getStealCount() before virtual thread work: 0
commonPool.getStealCount() after virtual thread work:  0 (changed: false)
commonPool.getPoolSize() before: 0, after: 0 (changed: false)
```

An earlier draft of the handbook chapter assumed `StructuredTaskScope`'s subtasks (which run on
virtual threads) shared `ForkJoinPool.commonPool()` along with parallel streams and
`CompletableFuture`'s default `*Async` calls — a reasonable-sounding assumption ("all backed by a
ForkJoinPool"), but wrong. This real check disproves it directly: the virtual thread's carrier
reports itself as `ForkJoinPool-1-worker-1` — a distinctly different pool identity from
`ForkJoinPool.commonPool-worker-*` — and running virtual thread work leaves `commonPool()`'s own
live metrics (`getStealCount()`, `getPoolSize()`) completely untouched. Virtual threads (and, by
extension, `StructuredTaskScope`) genuinely run on a separate, dedicated pool instance.
