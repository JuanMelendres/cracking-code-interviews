# CompletableFuture internals (T-407) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`syllabus/02-java/concurrency/completablefuture-and-async-composition.md`](../../../../syllabus/02-java/concurrency/completablefuture-and-async-composition.md)
(T-407). Three independent demos, each proving one real, easy-to-miss mechanism.

## Setup and run

```bash
cd practice/java/concurrency/completablefuture-internals
mkdir -p out
javac -d out src/*.java
java -cp out AsyncBoundaryDemo
java -cp out ExceptionSwallowingDemo
java -cp out ConcurrentCombineDemo
```

No special JVM flags needed — every demo uses only public `java.util.concurrent` API.

## Real observed output (last run)

### `AsyncBoundaryDemo` — attach-before-completion vs. attach-after-completion vs. `*Async`

```
Case 1 (attach BEFORE completion): thenApply ran on thread [completer-thread]
Case 2 (attach AFTER completion):  thenApply ran on thread [main] -- same as caller thread [main], ran synchronously inline
Case 3a (thenApplyAsync, default executor): ran on thread [ForkJoinPool.commonPool-worker-1] -- NOT the caller thread, even though future was already complete
Case 3b (thenApplyAsync, custom executor):  ran on thread [custom-async-worker]
```

Real proof of the JDK-documented but rarely-internalized rule: a non-`Async` dependent stage
(`thenApply`, `thenAccept`, `thenCompose`, ...) runs on **whichever thread causes the completion
trigger to fire** — the thread that calls `complete()` if the future wasn't done yet (Case 1), or
the calling thread itself, synchronously and inline, if the future was already done when the
stage was attached (Case 2). The `*Async` variant sidesteps this ambiguity entirely by **always**
dispatching to an executor (`ForkJoinPool.commonPool()` by default, Case 3a; a supplied executor,
Case 3b) regardless of completion timing.

### `ExceptionSwallowingDemo` — a pipeline exception is silently lost unless something forces the result

```
== Fire-and-forget: exception is thrown but NEVER observed ==
Main thread reached this line normally -- no exception, no stack trace, no log line. The failure happened on a background thread and vanished.

== Same pipeline, but join() is called -- exception surfaces for real ==
join() threw CompletionException, real cause: java.lang.IllegalStateException: simulated downstream failure

== handle() observes the failure and recovers, without throwing ==
handle() saw the real exception: IllegalStateException
Final value after recovery: fallback-value
```

The first block is the real failure mode: a `supplyAsync().thenApply()` pipeline that throws, with
nothing ever calling `join()`/`get()`/`exceptionally()`/`handle()` on it, produces **zero** visible
signal — no stack trace, no log line, nothing. The exception is stored inside the `CompletableFuture`
object and discarded with it once it's no longer referenced. `join()` (Case 2) is what actually
surfaces it, wrapped in a real `CompletionException`. `handle()` (Case 3) proves the wrapping happens
even on the very first stage after the throwing supplier, not only across chained stages — and lets
the pipeline recover with a fallback value instead of propagating.

### `ConcurrentCombineDemo` — real measured wall-clock cost of a sequential-`get()` mistake

```
Sequential result: A-result + B-result (elapsed=614ms)
thenCombine result: A-result + B-result (elapsed=313ms)
```

Two independent ~300ms calls. The "sequential" run doesn't even submit call B until call A's
`get()` has returned — a real, common mistake (treating two independent calls as if one depended
on the other) — and measures ~614ms, essentially the sum of both. `thenCombine` submits both calls
before either result is required and measures ~313ms, essentially the cost of the *slower* call
alone. The ~2x real speedup is the direct, measured cost of the accidental serialization.
