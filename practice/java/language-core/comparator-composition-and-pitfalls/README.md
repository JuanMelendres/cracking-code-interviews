# Comparator Composition and Pitfalls — Real Demo

Backs [`syllabus/02-java/language-core/comparator-composition-and-pitfalls.md`](../../../../syllabus/02-java/language-core/comparator-composition-and-pitfalls.md) (T-2413).

Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/*.java
java -cp out ComparatorCompositionDemo
java -cp out ComparatorOverflowBugDemo
java -cp out NullsAndStableSortDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **`comparing().thenComparing().reversed()` composes declaratively** — a
  three-field chain (lastName -> firstName -> age) and a mixed-direction
  chain (lastName descending, then firstName ascending) both sort
  correctly from one fluent expression, no manual `if`/`else` comparison
  logic.
- **The classic subtraction-comparator overflow bug, genuinely reproduced**
  — `(a, b) -> a.value() - b.value()` on `Integer.MIN_VALUE` and `1`
  computes `Integer.MIN_VALUE - 1`, which wraps to `Integer.MAX_VALUE` (a
  real, positive number), telling the sort that the smallest possible
  `int` is *greater than* `1`. The resulting sort is measurably,
  demonstrably out of order. `Comparator.comparingInt(...)` (backed by
  `Integer.compare`, which never overflows) sorts the identical data
  correctly.
- **A real `NullPointerException` from `Comparator.comparing()` on a
  nullable field**, and the exact fix — wrapping the key comparator in
  `Comparator.nullsFirst(...)` or `Comparator.nullsLast(...)` — verified
  against the identical data both ways.
- **`List.sort()` is a real, verified stable sort** — six tagged elements
  with duplicate sort keys keep their original relative order after
  sorting by key alone, confirmed by checking every equal-key pair's
  original insertion index post-sort.
