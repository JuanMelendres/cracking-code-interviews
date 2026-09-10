# Sorting Algorithms — Real Demo

Backs [`syllabus/03-data-structures-algorithms/sorting-algorithms.md`](../../../../syllabus/03-data-structures-algorithms/sorting-algorithms.md) (T-2119).

Pure JDK, no dependencies. Real, instrumented sort implementations with
comparison counters — no complexity claim in the chapter is asserted
without a matching measured number here.

## Run it

```bash
./run.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **Naive (first-element-pivot) QuickSort really is O(n²) on already-sorted
  and reverse-sorted input.** For n=2000, it makes exactly 1,999,000
  comparisons on both — exactly `n(n-1)/2`, the real signature of every
  partition being maximally unbalanced. A random-pivot version of the
  *identical* algorithm makes ~23,000–24,000 comparisons on the same two
  inputs — real, measured proof of why production-grade QuickSorts
  randomize (or median-of-three) the pivot instead of always taking the
  first element.
- **`Arrays.sort(Object[])` (TimSort) is really stable; a naive in-place
  QuickSort is really not.** Sorting the identical 12-element input with
  duplicate keys by both algorithms: TimSort preserves every tied
  element's original relative order exactly; the naive quicksort does
  not — checked programmatically, not eyeballed, against the real
  post-sort arrays.
- **InsertionSort really does beat MergeSort on small, nearly-sorted
  input, and really does lose catastrophically on large, random input.**
  n=12, nearly-sorted: 14 comparisons for insertion sort vs. 21 for merge
  sort. n=5000, random: 6,237,056 for insertion sort vs. 55,277 for merge
  sort — the real, measured reason hybrid sorts (TimSort included) fall
  back to insertion sort only for small runs, never for large ones.
