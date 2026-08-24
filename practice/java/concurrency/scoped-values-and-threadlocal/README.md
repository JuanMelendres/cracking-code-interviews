# Scoped Values and ThreadLocal migration (T-412) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`handbook/concurrency/scoped-values-and-threadlocal-migration.md`](../../../../handbook/concurrency/scoped-values-and-threadlocal-migration.md)
(T-412). `ScopedValue` is a **preview API in JDK 21** ([JEP 446](https://openjdk.org/jeps/446),
second preview) — every command below needs `--enable-preview`.

## Setup and run

```bash
cd practice/java/concurrency/scoped-values-and-threadlocal
mkdir -p out
javac --release 21 --enable-preview -d out src/*.java
java --enable-preview -cp out ScopedValueBasicsDemo
java --enable-preview -cp out ThreadLocalLeakDemo
java --enable-preview -cp out InheritanceComparisonDemo
```

## Real observed output (last run)

### `ScopedValueBasicsDemo` — real binding, dynamic extent, and structured shadowing

```
get() threw real NoSuchElementException: no binding exists yet
isBound() before binding: false

Inside run(): REQUEST_ID.get() = req-42, isBound()=true
  Inside a nested method call (no parameter passed): REQUEST_ID.get() = req-42

isBound() after run() returned: false
get() threw real NoSuchElementException again: the binding's dynamic extent genuinely ended

Outer binding: outer
Inner binding (shadows outer): inner
Back in outer scope, real restoration: outer
```

A `ScopedValue` genuinely throws `NoSuchElementException` when read outside any binding, is readable inside `ScopedValue.where(...).run(...)`'s dynamic extent — including from a nested method call with no parameter passed — and becomes genuinely unbound again the instant `run()` returns. Nested rebinding real, correctly shadows and restores.

### `ThreadLocalLeakDemo` — a real thread-pool-reuse leak, and `ScopedValue`'s structural immunity to it

```
== Real ThreadLocal leak across pooled-thread reuse (single-thread pool, forces reuse) ==
Task 1 (thread=pool-1-thread-1): set USER_CONTEXT=user-A, forgot to remove() it
Task 2 (thread=pool-1-thread-1), UNRELATED task, never set its own context: USER_CONTEXT.get() = user-A  <-- REAL LEAK: this is Task 1's stale value, on a reused pooled thread

== ScopedValue: structurally immune to this class of bug ==
Task 1 (thread=pool-2-thread-1): bound SCOPED_USER_CONTEXT=user-A for the dynamic extent of this run() only
Task 2 (thread=pool-2-thread-1), UNRELATED task, on the SAME reused pooled thread: SCOPED_USER_CONTEXT.isBound() = false  <-- REAL: no leak, no stale value, nothing to forget to clean up
```

Two tasks run sequentially on a real single-thread pool, forcing the same physical thread to be reused. Task 1 sets a `ThreadLocal` and never calls `remove()` — the real, common bug. Task 2, an entirely unrelated task, genuinely sees Task 1's stale value on the reused thread. The identical scenario with `ScopedValue` shows no leak at all: there's no `remove()` step to forget, because the binding's dynamic extent already ended when `run()` returned.

### `InheritanceComparisonDemo` — real propagation differences

```
== Plain ThreadLocal: genuinely NOT visible on a manually-created child thread ==
Child thread sees THREAD_LOCAL_CTX = null  <-- REAL: null, plain ThreadLocal does NOT propagate to a new thread automatically

== ScopedValue: real, verified propagation into a StructuredTaskScope subtask ==
subtask saw SCOPED_CTX = parent-value  <-- REAL: ScopedValue genuinely propagated into the forked subtask's own virtual thread
```

A plain `ThreadLocal` set on the main thread is genuinely invisible (`null`) on a manually-created child `Thread` — no automatic propagation at all. A `ScopedValue` bound on the parent, by contrast, is real and directly visible inside a subtask forked from a `StructuredTaskScope` opened within that binding — the mechanism `ScopedValue` was specifically designed to support.
