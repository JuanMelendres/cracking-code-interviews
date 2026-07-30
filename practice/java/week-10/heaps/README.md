# Week 10 Java — Heaps (T-1407) — runnable verification

LC 215, 347, 23, 295. No external dependencies.

## Setup and run

```bash
cd practice/java/week-10/heaps
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

**Real observed output (last run):**

```
== LC 215: Kth Largest Element in an Array ==
  PASS  kthLargest([3,2,1,5,6,4], k=2) = 5
  PASS  kthLargest([3,2,3,1,2,4,5,5,6], k=4) = 4

== LC 347: Top K Frequent Elements ==
  PASS  topKFrequent([1,1,1,2,2,3], k=2) = [1,2] (sorted for comparison)
  PASS  topKFrequent([1], k=1) = [1]

== LC 23: Merge K Sorted Lists ==
  PASS  mergeKLists([[1,4,5],[1,3,4],[2,6]]) = [1,1,2,3,4,4,5,6]
  PASS  mergeKLists([]) = []

== LC 295: Find Median from Data Stream ==
  PASS  after adding 1,2: median = 1.5
  PASS  after adding 3: median = 2.0

== LC 295 cross-check: MedianFinder vs a sorted-list reference over 500 random insertions ==
  PASS  MedianFinder matches a sorted-list reference after every one of 500 random insertions
Week 10 heaps suite: 9/9 assertions passed
```

`Main.java`'s `MedianFinder` cross-check maintains a sorted reference list via `Collections.binarySearch` insertion and compares its own computed median against the heap-based `MedianFinder` after every one of 500 random insertions (seeded `Random(7)` for reproducibility).
