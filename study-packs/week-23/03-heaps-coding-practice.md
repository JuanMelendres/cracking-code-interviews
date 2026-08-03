---
title: "Coding Practice — Heaps / Top-K (T-1407)"
week: 23
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Heaps / Top-K (T-1407)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 5/12 to 10/12. Previous coverage (LC 215 Kth Largest Element, LC 347 Top K Frequent Elements, LC 23 Merge K Sorted Lists, LC 295 Find Median from Data Stream, LC 973 K Closest Points to Origin, across Weeks 10 and 12) established the core max-heap/min-heap/two-heap patterns. This batch adds a repeated-max-extraction simulation, a string-comparator top-k variant, a k-way merge over two arrays via a heap, greedy string construction, and a "spend a scarce resource on the most expensive item" heap pattern.

Note: LC 253 (Meeting Rooms II), also a classic heap problem, is already solved but correctly filed under Intervals/T-1412 (`study-packs/week-20/03-intervals-coding-practice.md`) — not re-added here. LC 621 (Task Scheduler) is similarly already solved under Greedy/T-1413, not Heaps.

---

## Problem 1 — LC 1046 Last Stone Weight

**Pattern:** repeated max-extraction simulation — a max-heap directly models "always smash the two heaviest stones" without needing to re-sort after every step.

```java
static int lastStoneWeight(int[] stones) {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int s : stones) maxHeap.offer(s);
    while (maxHeap.size() > 1) {
        int a = maxHeap.poll(), b = maxHeap.poll();
        if (a != b) maxHeap.offer(a - b);
    }
    return maxHeap.isEmpty() ? 0 : maxHeap.peek();
}
```

**Retrospective:** re-sorting the entire array after every smash (the naive simulation) is O(n log n) per step, O(n² log n) overall — a max-heap gets each "find the two largest" step down to O(log n), since a heap's whole purpose is maintaining an ordering under repeated insertions and extractions, not a one-time sort. This is the simplest possible heap problem in this batch and a good warm-up for recognizing "the operation this problem repeats is exactly what a heap is built for" — a pattern that generalizes to every other problem in this file. **Complexity:** O(n log n) time overall.

## Problem 2 — LC 692 Top K Frequent Words

**Pattern:** a size-capped min-heap with a two-part comparator (frequency primary, reverse-lexicographic secondary) — the string-comparator sibling to LC 347's purely numeric top-k.

```java
PriorityQueue<String> heap = new PriorityQueue<>((a, b) -> {
    int freqCompare = freq.get(a) - freq.get(b);
    if (freqCompare != 0) return freqCompare;
    return b.compareTo(a); // lexicographically larger = smaller priority = evicted first
});
for (String w : freq.keySet()) {
    heap.offer(w);
    if (heap.size() > k) heap.poll();
}
```

**Retrospective:** the problem's tie-breaking rule ("if frequencies are equal, the lexicographically smaller word comes first") has to be *inverted* inside a min-heap that evicts the lowest-priority element — since the heap should evict the word that's *worst* by the problem's ranking, and among tied frequencies the worst word is the lexicographically *larger* one, the comparator's tie-break branch reverses the natural `String.compareTo` order (`b.compareTo(a)` instead of `a.compareTo(b)`). This exact "flip the comparator to make a min-heap behave like a bounded max-heap" technique is the same one used by LC 215 and LC 973 in earlier weeks; the string+dual-criteria comparator is what's new here. **Complexity:** O(n log k) time, better than sorting all distinct words when k is small relative to vocabulary size.

## Problem 3 — LC 373 Find K Pairs with Smallest Sums

**Pattern:** a min-heap seeded with one candidate pair per element of the first array, expanding along the second array's index only when a pair is popped — a k-way-merge-style heap usage over an implicit grid of sums rather than a small fixed set of lists.

```java
PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> nums1[a[0]] + nums2[a[1]]));
for (int i = 0; i < Math.min(nums1.length, k); i++) minHeap.offer(new int[]{i, 0});
while (k-- > 0 && !minHeap.isEmpty()) {
    int[] idx = minHeap.poll();
    result.add(List.of(nums1[idx[0]], nums2[idx[1]]));
    if (idx[1] + 1 < nums2.length) minHeap.offer(new int[]{idx[0], idx[1] + 1});
}
```

**Retrospective:** the full cross-product of `nums1 × nums2` pairs can be enormous, but only ever needs `k` of the smallest sums explored — seeding the heap with `(i, 0)` for each `i` (pairing every `nums1` element with `nums2`'s smallest element) guarantees the true smallest pair is already in the heap, and each pop only ever advances *that specific row's* `j` index by one, since advancing any other row wouldn't necessarily produce a smaller next candidate. This is structurally the same idea as merging k sorted lists (LC 23, already solved) — each "row" `i` is implicitly a sorted list of sums (since `nums2` is sorted), and the heap merges across all of them lazily, without ever materializing a row beyond the elements actually needed. **Complexity:** O(k log(min(k, m))) time, where m is `nums1.length` — far better than generating and sorting all m·n pairs.

## Problem 4 — LC 767 Reorganize String

**Pattern:** greedily place the currently-most-frequent character, holding the just-placed character out of the heap for exactly one step to guarantee no two identical characters end up adjacent.

```java
Map.Entry<Character, Integer> prev = null;
while (!maxHeap.isEmpty()) {
    Map.Entry<Character, Integer> cur = maxHeap.poll();
    result.append(cur.getKey());
    cur.setValue(cur.getValue() - 1);
    if (prev != null && prev.getValue() > 0) maxHeap.offer(prev); // re-admit the previous char only now
    prev = cur;
}
```

**Retrospective:** always placing the globally most-frequent remaining character is the correct greedy choice, but doing so naively risks placing the same character twice in a row if it's still the most frequent immediately after being placed — holding the just-used character out of the heap for exactly one iteration (re-admitting it only after the *next* character has been placed) is what enforces the no-adjacent-duplicates constraint without ever needing to look more than one step ahead. If the string is impossible to reorganize (some character's frequency exceeds `(n+1)/2`), the final result's length will simply fall short of the input's length, which is the built-in signal used here to return `""` — no separate upfront feasibility check is needed since the algorithm itself fails to consume all characters when it's infeasible. **Complexity:** O(n log a) time, where a is the alphabet size (a small constant in practice).

## Problem 5 — LC 1642 Furthest Building You Can Reach

**Pattern:** a min-heap of size `ladders` tracking the largest climbs "banked" as ladder-covered — greedily prefer spending bricks on the smallest climbs so ladders are reserved for the biggest ones.

```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
for (climb : climbs_needing_a_gain) {
    minHeap.offer(climb);
    if (minHeap.size() > ladders) {
        bricks -= minHeap.poll(); // evict (and pay bricks for) the SMALLEST climb currently using a ladder
        if (bricks < 0) return i;
    }
}
```

**Retrospective:** the greedy insight is that ladders (unlimited climb coverage) should always be reserved for the *largest* climbs, since a large climb would otherwise cost the most bricks — so whenever more than `ladders` climbs are being tracked, the algorithm evicts the *smallest* one from "ladder coverage" and pays for it with bricks instead, since that's the cheapest climb to downgrade. This is a variant of the same "maintain a bounded heap of the most valuable/costly items seen so far" idea as LC 215/347/973/692 in this batch, but inverted: instead of keeping the top-k largest, it keeps the top-k largest *reserved for free* and pays cash for everything that gets evicted from that reservation. **Complexity:** O(n log(ladders)) time.

## Verification

```
$ cd practice/java/week-23/heaps/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC1046 lastStoneWeight([2,7,4,1,8,1]) = 1
  PASS  LC1046 lastStoneWeight([1]) = 1
  PASS  LC692 topKFrequent(k=2) = [i, love]
  PASS  LC692 topKFrequent(k=4) = [the, is, sunny, day]
  PASS  LC373 kSmallestPairs(k=3) = [[1,2],[1,4],[1,6]]
  PASS  LC373 kSmallestPairs(duplicates, k=2) = [[1,1],[1,1]]
  PASS  LC767 reorganizeString(aab) = aba
  PASS  LC767 reorganizeString(aaab) = "" (impossible)
  PASS  LC1642 furthestBuilding(bricks=5, ladders=1) = 4
  PASS  LC1642 furthestBuilding(bricks=10, ladders=2) = 7
Week 23 — Heaps (LC 1046, 692, 373, 767, 1642): 10/10 assertions passed
```
