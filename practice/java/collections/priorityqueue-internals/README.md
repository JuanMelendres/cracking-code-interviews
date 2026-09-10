# PriorityQueue Internals — Real Demo

Backs [`syllabus/02-java/collections/priorityqueue-internals.md`](../../../../syllabus/02-java/collections/priorityqueue-internals.md) (T-210).

Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/PriorityQueueInternalsDemo.java
java -cp out PriorityQueueInternalsDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **Ordering guarantee** — `poll()` always returns ascending order regardless
  of insertion order; a `Comparator.reverseOrder()` constructor argument
  turns the same class into a max-heap.
- **The classic trap** — `for`-each iteration order is the internal array's
  storage order, demonstrably *not* sorted, while repeated `poll()` on the
  identical queue *is* sorted.
- **Real, measured O(log n)** — a comparison-counting `Comparator` wrapper
  measures exactly how many comparisons one `poll()` call performs at five
  different heap sizes (100 through 1,000,000); comparison count tracks
  `log2(N)`, not `N` — real evidence, not an assertion from the Javadoc.
- **Fail-fast, not thread-safe** — structurally modifying the queue mid-iteration
  throws a real `ConcurrentModificationException`, the same fail-fast family
  as `ArrayList`'s and `HashMap`'s iterators.
