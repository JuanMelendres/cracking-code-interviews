# Fail-fast vs. weakly-consistent iterators (T-208) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`handbook/collections/fail-fast-vs-weakly-consistent-iterators.md`](../../../../handbook/collections/fail-fast-vs-weakly-consistent-iterators.md)
(T-208). Three independent demos: a real, reproduced `ConcurrentModificationException`
and its best-effort quirk, real reflective proof of the `modCount` mechanism, and real,
latch-forced concurrent iteration on `CopyOnWriteArrayList`/`ConcurrentHashMap`.

## Setup and run

```bash
cd practice/java/collections/fail-fast-vs-weakly-consistent
mkdir -p out
javac -d out src/*.java
java -cp out FailFastRemovalDemo
java --add-opens java.base/java.util=ALL-UNNAMED -cp out ModCountReflectionDemo
java -cp out WeaklyConsistentIterationDemo
```

`ModCountReflectionDemo` needs `--add-opens` — it reflects into `AbstractList`'s protected
`modCount` field. The other two need no special flags.

## Real observed output (last run)

### `FailFastRemovalDemo` — a real CME, and the real quirk where none is thrown

```
== Case A: list.remove() on a NON-second-to-last element during for-each ==
Real ConcurrentModificationException thrown, as expected: java.util.ConcurrentModificationException

== Case B: list.remove() on the SECOND-TO-LAST element (the classic quirk) ==
Exception thrown: false -- fail-fast is best-effort, NOT a guarantee. Result list (silently corrupted, one element short of what a caller might expect an error for): [1, 2, 3, 5]

== Case C: the correct fix -- Iterator.remove() itself ==
No exception; correctly removed 2 and 4: [1, 3, 5]
```

Case A reproduces the textbook failure: `list.remove(value)` during a for-each loop throws a real
`ConcurrentModificationException`. Case B reproduces the real, well-known JDK quirk: removing the
**second-to-last** element does NOT throw — `ArrayList$Itr.hasNext()` checks `cursor != size`
*before* `next()` gets a chance to check `modCount`, and removing the second-to-last element
shrinks `size` by one in exactly the way that makes `cursor == size` true on the next `hasNext()`
check, so the loop exits silently instead of calling `next()` (where the `modCount` check lives).
Case C shows the actual correct fix: `Iterator.remove()` updates the iterator's own
`expectedModCount` to match, so no exception occurs.

### `ModCountReflectionDemo` — real reflective proof of the mechanism itself

```
modCount after construction: 0
modCount after add(4):       1
modCount after remove(2):    2
modCount after set(0, 99):   2 -- unchanged: set() is not a structural modification, so iterators remain valid through it
modCount after get(0):       2 -- unchanged: reads never touch modCount
```

Real, reflective reads of `AbstractList`'s protected `modCount` field confirm the exact mechanism:
it increments on structural modifications (`add`, `remove`) and stays untouched by non-structural
operations (`set`, `get`) — precisely why replacing an element in place never invalidates an
in-flight iterator, while adding or removing one does.

### `WeaklyConsistentIterationDemo` — real concurrent modification, zero exceptions

```
== CopyOnWriteArrayList: iterator holds a fixed snapshot, real concurrent add() is invisible to it ==
Live list after concurrent add: [a, b, c, d-added-after-snapshot]
Elements seen by the already-created iterator: a b c  -- no exception thrown, and the concurrently-added element is genuinely absent from this iteration

== ConcurrentHashMap: real, latch-forced concurrent put() DURING live iteration, zero exceptions ==
Iterated 8756 entries; the writer inserted 10,000 more keys WHILE this iteration was genuinely paused mid-traversal (latch-forced, not timing-guessed). CME thrown: false. Saw at least one of the concurrently-inserted keys during the same iteration: true -- weakly-consistent: never throws; MAY or may not reflect an in-flight concurrent insert, unlike CopyOnWriteArrayList's fixed snapshot above. Final map size=10005
```

`CopyOnWriteArrayList`'s iterator is built over the exact array reference captured at
iterator-creation time; a concurrent `add()` replaces the underlying array with a new one entirely,
but the already-created iterator still holds its own reference to the old array — the concurrently
added element is genuinely, provably absent from that iteration, with zero exception.
`ConcurrentHashMap`'s iterator is different: using `CountDownLatch`-forced (not timing-guessed)
overlap, the reader thread is made to genuinely pause mid-iteration while the writer thread inserts
10,000 more real entries — the iteration continues afterward, sees more entries than existed at
iterator-creation time (8,756 vs. the initial 5), and directly observes at least one of the
concurrently-inserted keys — all with zero `ConcurrentModificationException`. This is the real,
measured difference between "fixed snapshot" (COW) and "weakly consistent, may reflect concurrent
changes" (`ConcurrentHashMap`) — both non-fail-fast, but not identical guarantees.
