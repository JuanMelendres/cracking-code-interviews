# Algorithmic Complexity — Real Scaling Demo

Backs [Algorithmic Complexity and Big-O, From First Principles](../../../../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) (T-2001).

## What this proves

Real, measured wall-clock time for five operations — O(1) array access, O(log n) binary search, O(n) linear sum, O(n log n) sort, O(n²) all-pairs comparison — at increasing input sizes, on OpenJDK 21.0.12. Not a rigorous JMH microbenchmark (see [Benchmarking and JMH Pitfalls](../../../../syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md) for what that would take); this is a from-scratch demonstration of the underlying growth-rate idea, with warmup rounds and min-of-N-repeats to keep JIT/GC noise from swamping the signal.

## Run it

```bash
cd practice/java/cs-foundations/algorithmic-complexity
mkdir -p out
javac -d out src/ComplexityScalingDemo.java
java -cp out ComplexityScalingDemo
```

## What was actually measured (this exact run, OpenJDK 21.0.12)

```
=== O(1): array index access, repeated 10,000 times regardless of n ===
  n=     1,000  ->      0.0256 ms
  n=    10,000  ->      0.0181 ms
  n=   100,000  ->      0.0268 ms
  n= 1,000,000  ->      0.0197 ms
  n=10,000,000  ->      0.0007 ms

=== O(log n): binary search for a present key ===
  n=     1,000  ->      0.0005 ms
  n=    10,000  ->      0.0003 ms
  n=   100,000  ->      0.0003 ms
  n= 1,000,000  ->      0.0003 ms
  n=10,000,000  ->      0.0004 ms

=== O(n): linear sum over the array ===
  n=     1,000  ->      0.0049 ms
  n=    10,000  ->      0.0241 ms
  n=   100,000  ->      0.1312 ms
  n= 1,000,000  ->      0.2249 ms
  n=10,000,000  ->      2.2723 ms

=== O(n log n): Arrays.sort (Dual-Pivot Quicksort for int[]) ===
  n=     1,000  ->      0.0880 ms
  n=    10,000  ->      0.3482 ms
  n=   100,000  ->      7.0250 ms
  n= 1,000,000  ->     76.0696 ms
  n=10,000,000  ->    664.1764 ms

=== O(n^2): all-pairs comparison (bubble-sort-shaped nested loop) ===
  n=     1,000  ->      0.4610 ms
  n=     2,000  ->      1.8498 ms
  n=     4,000  ->      7.4191 ms
  n=     8,000  ->     29.4588 ms
  n=    16,000  ->    118.3056 ms
```

## Honest reading of the numbers

- **O(1) and O(log n)** are both indistinguishable from measurement noise across five orders of magnitude of `n` — that flatness is itself the finding, not a measurement failure.
- **O(n)** scales roughly linearly but not perfectly proportionally (0.0049 ms → 2.2723 ms across a 10,000× growth in `n`, a ~464× time increase, not exactly 10,000×) — real machines have cache-locality and JIT-warmup effects an idealized model doesn't capture.
- **O(n log n)** scales faster than linear but far slower than quadratic, converging toward the theoretical ratio as `n` grows large enough for per-call overhead to stop dominating.
- **O(n²)** is the cleanest result: time increases almost exactly **4×** every time `n` doubles (1,000→2,000→4,000→8,000→16,000), which is exactly what squaring a doubled input predicts. Also notice the size range had to shrink — the same growth that makes `n²` a clean demonstration is what makes it operationally unworkable past a few tens of thousands of elements.
