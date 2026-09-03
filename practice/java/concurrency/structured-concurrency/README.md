# Structured Concurrency (T-411) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`syllabus/02-java/concurrency/structured-concurrency.md`](../../../../syllabus/02-java/concurrency/structured-concurrency.md)
(T-411). `StructuredTaskScope` is a **preview API in JDK 21** ([JEP 453](https://openjdk.org/jeps/453),
second preview) — every command below needs `--enable-preview`.

## Setup and run

```bash
cd practice/java/concurrency/structured-concurrency
mkdir -p out
javac --release 21 --enable-preview -d out src/*.java
java --enable-preview -cp out BasicForkJoinDemo
java --enable-preview -cp out FailFastCancellationDemo
java --enable-preview -cp out UnstructuredLeakDemo
```

## Real observed output (last run)

### `BasicForkJoinDemo` — two real, concurrent subtasks, joined together

```
user=user-42, orders=orders-[7,8,9]
Real elapsed: 264ms -- both ~200ms and ~250ms calls ran CONCURRENTLY, not sequentially (sequential would be ~450ms)
```

Two subtasks (~200ms and ~250ms of real `Thread.sleep`) forked into a `StructuredTaskScope.ShutdownOnFailure`,
joined together with `scope.join()`. The real elapsed time (~264ms) is close to the *slower* of the two, not
their sum — real, measured proof they ran concurrently.

### `FailFastCancellationDemo` — real, measured automatic cancellation on sibling failure

```
Real elapsed: 116ms (the long task's FULL budget was 5000ms)
Long-running sibling was really interrupted (flag check or Thread.sleep() throwing): true
scope.throwIfFailed() correctly surfaced the real failure: java.lang.IllegalStateException: simulated fast failure
```

One subtask fails after ~100ms; its sibling has a real 5-second budget (checking for interruption every 50ms).
The scope's real elapsed time is ~116ms — nowhere near the sibling's 5-second budget — and the sibling really
was interrupted (its `Thread.sleep()` call itself threw `InterruptedException`, since the interrupt landed
mid-sleep rather than at one of the loop's own `isInterrupted()` checks). This is real, measured, automatic
cancellation propagation — not a documentation claim.

### `UnstructuredLeakDemo` — the real problem structured concurrency solves

```
Caller observes the failure at +109ms and "moves on" -- but the sibling task is STILL RUNNING in the background right now, uncancelled.
isDone() on the sibling immediately after 'moving on': false  <-- still running, a real orphaned/leaked task
  [background] orphaned task FINALLY finished at +2009ms -- it ran its full real 2000ms even though the caller already moved on
Total real wall time until the orphaned task actually finished: 2011ms -- versus StructuredTaskScope's real ~100ms in FailFastCancellationDemo
```

The identical shape (one fast-failing task, one long-running sibling), but built with plain `CompletableFuture`
instead of `StructuredTaskScope`. The caller "moves on" at ~109ms after the first failure, but the sibling —
real, verified via `isDone() == false` at that exact moment — is still running, uncancelled, and only actually
finishes at the full real ~2011ms mark. This is the real, measured cost of the exact failure mode structured
concurrency's automatic cancellation propagation exists to prevent: an orphaned background task consuming
real resources long after the code that spawned it has already handled a failure and continued.
