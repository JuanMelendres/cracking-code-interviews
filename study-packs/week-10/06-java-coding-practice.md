---
title: "Java Coding Practice — Week 10"
week: 10
last_reviewed: 2026-07-29
---

# Java Coding Practice — Week 10

**T-1407 heaps — LC 215, 347, 23, 295. All code compiled and executed, including a 500-trial randomized cross-check of `MedianFinder` against a sorted-list reference — see the verification block and `MANIFEST.md`.**

## Table of Contents

1. [LC 215 — Kth Largest Element in an Array](#lc-215--kth-largest-element-in-an-array)
2. [LC 347 — Top K Frequent Elements](#lc-347--top-k-frequent-elements)
3. [LC 23 — Merge K Sorted Lists](#lc-23--merge-k-sorted-lists)
4. [LC 295 — Find Median from Data Stream](#lc-295--find-median-from-data-stream)
5. [Verification](#verification--real-not-asserted)

---

## LC 215 — Kth Largest Element in an Array

```java
static int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    for (int n : nums) {
        minHeap.offer(n);
        if (minHeap.size() > k) minHeap.poll();
    }
    return minHeap.peek();
}
```

**Invariant:** keep a min-heap capped at size `k` — whenever it exceeds `k`, evict the smallest element, since that element can't possibly be among the `k` largest. After processing every element, the heap's own minimum IS the kth largest overall. **Complexity:** O(n log k) time, O(k) space — better than sorting the whole array (O(n log n)) when `k` is small relative to `n`.

## LC 347 — Top K Frequent Elements

```java
static int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int n : nums) freq.merge(n, 1, Integer::sum);
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    for (var e : freq.entrySet()) {
        minHeap.offer(new int[]{e.getKey(), e.getValue()});
        if (minHeap.size() > k) minHeap.poll();
    }
    // drain the heap into the result array
}
```

**Invariant:** the exact same "min-heap capped at size k, evict the smallest" shape as LC 215, just ordered by frequency instead of value — recognizing this as the SAME pattern applied to a different comparator is the actual skill; the frequency-counting step beforehand is separate and incidental. **Complexity:** O(n log k) time, O(n) space (for the frequency map).

## LC 23 — Merge K Sorted Lists

```java
static ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> minHeap = new PriorityQueue<>(Comparator.comparingInt(n -> n.val));
    for (ListNode node : lists) if (node != null) minHeap.offer(node);
    ListNode dummy = new ListNode(0), tail = dummy;
    while (!minHeap.isEmpty()) {
        ListNode smallest = minHeap.poll();
        tail.next = smallest; tail = smallest;
        if (smallest.next != null) minHeap.offer(smallest.next);
    }
    return dummy.next;
}
```

**Invariant:** the heap holds at most one node PER LIST at any time (the current head of each list still being merged) — polling the global minimum and immediately offering its successor keeps that invariant, so the heap never grows past `k` (the number of lists) regardless of total node count. This is a different heap-sizing discipline than LC 215/347's "cap at size k and evict" — here the cap emerges naturally from the one-node-per-source invariant instead of being enforced explicitly. **Complexity:** O(n log k) time for n total nodes across k lists, O(k) space.

## LC 295 — Find Median from Data Stream

```java
class MedianFinder {
    PriorityQueue<Integer> lowerHalf = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
    PriorityQueue<Integer> upperHalf = new PriorityQueue<>(); // min-heap

    void addNum(int num) {
        lowerHalf.offer(num);
        upperHalf.offer(lowerHalf.poll());          // route through lowerHalf first
        if (upperHalf.size() > lowerHalf.size()) {
            lowerHalf.offer(upperHalf.poll());        // rebalance back if upperHalf grew ahead
        }
    }
    double findMedian() {
        if (lowerHalf.size() > upperHalf.size()) return lowerHalf.peek();
        return (lowerHalf.peek() + upperHalf.peek()) / 2.0;
    }
}
```

**Invariant:** two heaps split the stream at its midpoint — `lowerHalf` (max-heap) holds the smaller half so its peek is the largest-of-the-small-half; `upperHalf` (min-heap) holds the larger half so its peek is the smallest-of-the-large-half; together the two peeks straddle the true median. Every insertion routes through `lowerHalf` first, then rebalances if that pushed `upperHalf` ahead in size — this two-step dance is what keeps the size invariant (`|lowerHalf.size() - upperHalf.size()| <= 1`) maintained after every single insertion, not just eventually. **Complexity:** O(log n) per insertion, O(1) per median query.

## Verification — real, not asserted

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

Full source: `practice/java/week-10/heaps/src/`. Reproduce: `cd practice/java/week-10/heaps && javac -d out src/*.java && java -cp out Main`.

## Exit check

- [ ] All 4 problems solved with a written retrospective
- [ ] Can explain why LC 23's heap never exceeds size `k` (number of lists) WITHOUT an explicit "if size > k, evict" check, unlike LC 215/347
- [ ] Can explain the exact two-step insertion dance in `MedianFinder.addNum()` and why routing through `lowerHalf` first (rather than deciding which heap to insert into directly) is what keeps the size invariant correct after every call, not just on average
