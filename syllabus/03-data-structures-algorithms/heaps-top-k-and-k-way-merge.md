---
title: "Heaps, Top-K, and K-Way Merge"
slug: heaps-top-k-and-k-way-merge
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2106
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
related:
  - stacks-and-monotonic-stack.md
practice: ../../practice/java/week-23/heaps/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references:
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/PriorityQueue.html
source_history:
  - study-packs/week-23/03-heaps-coding-practice.md
---

# Heaps, Top-K, and K-Way Merge

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-23/03-heaps-coding-practice.md` — real, compiled, executed code (`practice/java/week-23/heaps/`), re-verified on OpenJDK 21.0.12 while writing this chapter (10/10 assertions passing).

This is Master Topic Register **T-1407** (IWI 5.5, very-high frequency). A heap (Java's `PriorityQueue`) is the standard tool whenever a problem repeatedly needs "the current largest/smallest" from a changing collection — this chapter covers the pattern family, not `PriorityQueue`'s own internal implementation.

## 1. Why This Matters

A huge class of problems reduces to "repeatedly find and remove the current extreme value" — the k largest elements, the next cheapest flight, the next-most-frequent word. A heap answers this in O(log n) per operation, versus O(n log n) to re-sort the entire collection from scratch every time something changes. Recognizing "this problem repeats an extreme-value query" as the trigger for reaching for a heap, and correctly choosing max-heap vs. min-heap vs. a size-bounded heap, is the core skill this pattern tests.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — the O(log n) complexity class a heap's insert and extract-min/max operations achieve, and why that beats O(n log n) re-sorting when the extreme-value query repeats many times.

## 3. Foundation (L1)

**A heap is a data structure specialized for one job: quickly finding and removing the current minimum (or maximum) element from a changing collection.** Unlike a fully sorted structure, a heap doesn't maintain a total order over every element — it only guarantees the single smallest (or largest) is always immediately accessible, which is exactly why inserting or removing costs only O(log n) rather than the O(n log n) a full re-sort would cost.

**Java's `PriorityQueue` is a min-heap by default** — `poll()` always returns the smallest element currently in the queue. A max-heap is built by supplying a reversed comparator (`Collections.reverseOrder()`, or `(a, b) -> b - a`), not a different class.

## 4. Core Concepts (L2)

**The "repeated extreme-value extraction" pattern** (Section 7, Problem 1, Last Stone Weight) is the simplest heap use: whenever an algorithm's core loop is "find the current max/min, use it, put a modified version back, repeat," a heap replaces an O(n log n)-per-step re-sort with an O(log n)-per-step heap operation.

**A size-bounded heap ("keep only the top k")** (Section 7, Problem 2, Top K Frequent Words) maintains a min-heap capped at size `k`, evicting the smallest element whenever the cap is exceeded — counterintuitively using a *min*-heap to track the *largest* k elements, since the element you want to discard first, when over capacity, is always the smallest one currently kept.

**A heap-based k-way merge** (Section 7, Problem 3, Find K Pairs with Smallest Sums) generalizes "merge k already-sorted sequences" without materializing all of them upfront: seed the heap with one candidate from each sequence, and each time an element is popped, push that sequence's next candidate — the heap always holds exactly one "next candidate" per active sequence, guaranteeing the true global minimum is always present.

**Greedy heap-based construction** (Section 7, Problems 4 and 5) uses a heap not just to read out a sorted order, but to make a sequence of locally-optimal choices — always placing the currently most-frequent character (Reorganize String), or always reserving a scarce resource for the largest need seen so far (Furthest Building) — where the heap's O(log n) extreme-value access is what makes each greedy step affordable.

## 5. How It Works Internally (L3)

**The comparator-flipping technique for a bounded min-heap simulating "keep the top k largest"** (Section 7, Problem 2): a min-heap always evicts its smallest element via `poll()`. To keep the *largest* k elements, the heap is capped at size `k`, and any time a new candidate arrives, it's added and then the heap's current minimum is evicted if the size exceeds `k` — meaning the heap always holds exactly the k largest candidates seen so far, with its own minimum being the *smallest of the currently-kept largest* (the correct eviction target for a new, larger candidate later). Extending this to a *string* comparator with tie-breaking (Top K Frequent Words) requires inverting the tie-break direction specifically, since the element the heap should evict first is the "worst" by the problem's own ranking, and among tied frequencies, that worst element is the lexicographically *larger* one — the reverse of `String`'s natural ordering.

**The k-way-merge-over-an-implicit-grid technique's correctness** (Find K Pairs with Smallest Sums): seeding the heap with pair `(i, 0)` for every index `i` of the first array — pairing every element of `nums1` with `nums2`'s smallest element — guarantees the single globally smallest sum is already present in the heap. Each pop only ever advances *that specific row's* second-array index by one, since advancing any other row wouldn't necessarily produce a smaller next candidate than what's already queued for other rows — the heap does the work of deciding which row to advance next, rather than the algorithm needing to reason about it directly. This is structurally identical to merging k sorted linked lists: each "row" is implicitly a sorted sequence (since `nums2` is sorted), and the heap merges across all of them lazily, only ever materializing the elements actually needed.

**The greedy-with-a-cooldown technique** (Reorganize String) enforces "no two adjacent characters are identical" by holding the just-placed character out of the heap for exactly one iteration, re-admitting it only after a *different* character has been placed next. This one-step delay is sufficient — not two, not a general "wait until safe" check — specifically because the constraint being enforced (no two *identical adjacent* characters) only ever concerns the immediately preceding placement, not any earlier ones.

## 6. Practical Usage

- **Reach for a heap the moment a problem repeats an extreme-value query** (largest, smallest, most/least frequent) as its core loop, rather than re-sorting or re-scanning on every iteration.
- **Use a size-bounded heap for any "top k" or "k closest" requirement** — capping the heap at size `k` keeps the operation at O(n log k) rather than O(n log n), a real, worthwhile improvement when `k` is small relative to the input.
- **Reach for a heap-based k-way merge whenever multiple already-sorted sequences (explicit or implicit, like a grid of pairwise sums) need their combined smallest/largest elements extracted incrementally**, without fully materializing every combination upfront.

## 7. Examples

**Problem 1 — LC 1046, Last Stone Weight.**

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

**Retrospective:** re-sorting the entire array after every smash is O(n² log n) overall; a max-heap gets each "find the two largest" step down to O(log n). **Complexity:** O(n log n) time overall.

**Problem 2 — LC 692, Top K Frequent Words.**

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

**Retrospective:** see Section 5's comparator-flipping argument. **Complexity:** O(n log k).

**Problem 3 — LC 373, Find K Pairs with Smallest Sums.**

```java
PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> nums1[a[0]] + nums2[a[1]]));
for (int i = 0; i < Math.min(nums1.length, k); i++) minHeap.offer(new int[]{i, 0});
while (k-- > 0 && !minHeap.isEmpty()) {
    int[] idx = minHeap.poll();
    result.add(List.of(nums1[idx[0]], nums2[idx[1]]));
    if (idx[1] + 1 < nums2.length) minHeap.offer(new int[]{idx[0], idx[1] + 1});
}
```

**Retrospective:** see Section 5's k-way-merge argument. **Complexity:** O(k log(min(k, m))).

**Problem 4 — LC 767, Reorganize String.**

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

**Retrospective:** see Section 5's cooldown argument. If reorganization is impossible, the result simply falls short of the input length — no separate feasibility check needed. **Complexity:** O(n log a), a = alphabet size.

**Problem 5 — LC 1642, Furthest Building You Can Reach.**

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

**Retrospective:** ladders (unlimited climb coverage) should always be reserved for the *largest* climbs; whenever more than `ladders` climbs are tracked, the smallest is evicted and paid for with bricks instead, since it's the cheapest to downgrade. **Complexity:** O(n log(ladders)).

## 8. Common Mistakes

- **Reaching for a max-heap when the problem actually needs a bounded min-heap (or vice versa)** — Section 5's "keep the top k largest via a min-heap" inversion is genuinely counterintuitive on first encounter and a common source of using the wrong heap type entirely.
- **Forgetting to flip a tie-break comparator's direction** when the natural ordering (`String.compareTo`) doesn't match the direction the heap needs to evict in (Section 5) — producing a heap that silently returns the wrong tie-break winner rather than an obvious error.
- **Materializing an entire cross-product or full merge upfront** instead of using the lazy, incremental heap-based k-way-merge technique (Section 5) — correct but wastes memory and time proportional to the full product size rather than just `k`.

## 9. Edge Cases

- **A single remaining element after repeated extraction** (Last Stone Weight's own verified `[1]` case, returning `1` immediately since the loop condition `size() > 1` never triggers) — the initial-size check matters.
- **An impossible reorganization** (Reorganize String's verified `"aaab"` case, correctly returning `""`) — the algorithm's own natural termination (running out of characters it can safely place) is the feasibility signal, not a separate upfront check.
- **Duplicate values across k-way-merge candidates** (Find K Pairs' verified duplicate-input case, correctly returning `[[1,1],[1,1]]`) — the heap's comparator must handle ties consistently without special-casing them.

## 10. Performance Implications

Real, executed verification from `practice/java/week-23/heaps/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
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

Every solution here achieves O(log n) or O(log k) per operation rather than O(n) or O(n log n) per operation from re-sorting — the practical performance implication is that a heap-based approach's advantage compounds specifically when the extreme-value query repeats many times over the algorithm's run, which is exactly the shape of every problem in this chapter.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Heap (repeated extraction) | O(log n) per insert/extract, far better than repeated full re-sorts | O(n) space; only the single extreme value is directly accessible, not any arbitrary rank |
| Size-bounded heap ("top k") | O(n log k), better than O(n log n) full sort when k ≪ n | Only correct for exactly "top k," not general sorted access |
| Heap-based k-way merge | Avoids materializing the full cross-product/merge upfront | More complex bookkeeping (tracking which "row" each heap entry belongs to) than a simple sort |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is recognizing heap applicability in problems that don't superficially look like "find the max/min" — Furthest Building (Section 7, Problem 5) is phrased as a resource-allocation simulation, not a heap problem, and recognizing "reserve the scarce resource for the largest need, using a bounded heap to track which needs are currently 'reserved'" is the actual insight the problem tests, not `PriorityQueue` API fluency itself.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the top-k-via-bounded-heap pattern (Section 5) transfers directly to real production systems: a monitoring system tracking the top-N slowest requests, or the top-N most-requested resources, over a continuous stream, uses exactly this bounded-min-heap technique to maintain that top-N in O(log N) per new observation, rather than re-sorting the entire observed history on every new data point — a real, load-bearing design choice in any dashboard or alerting system processing a high-volume, continuous event stream. Recognizing this transfer is what separates "I can solve LeetCode heap problems" from "I can design a production top-N tracker with a bounded memory footprint."

## 14. Production Scenarios

No existing `production-cookbook/` entry has a heap/top-k-specific algorithmic root cause.

> Planned reference: a future `production-cookbook/` entry covering a real top-N tracking or monitoring dashboard design (e.g., top-N slowest endpoints, recomputed continuously without re-sorting full request history) would be a natural, non-duplicative addition connecting this chapter's bounded-heap technique to a genuine production system.

## 15. Interview Questions

### Question 1 — How would you find the k largest elements in a stream of numbers, without storing the entire stream?

**Why interviewers ask it.** It's the canonical size-bounded-heap test, checking whether a candidate reaches for a min-heap capped at size k (counterintuitive on first encounter) rather than a max-heap holding everything seen so far.

**Expected answer.** Maintain a min-heap capped at size `k`: for each new number, add it to the heap, then evict the minimum if the heap's size exceeds `k`. After processing the entire stream, the heap holds exactly the k largest numbers seen, with O(log k) per insertion and O(n) total space for the stream itself never required (only O(k) for the heap).

**Minimum acceptable answer.** Produces a correct solution, even if it's "keep a sorted list of size k" rather than a heap specifically.

**Strong Senior answer.** Explains why a *min*-heap (not a max-heap) is the correct choice for tracking the largest k — the element to discard when over capacity is always the smallest currently kept.

**Staff-level extension.** Connects this directly to a real streaming/monitoring system design (Section 13) — the same technique, applied continuously rather than to a finite, already-known stream.

**Common mistakes.** Using a max-heap holding all n elements, then popping k times — correct, but O(n) space and no advantage over a size-bounded min-heap's O(k) space for the streaming case specifically.

**Follow-up questions.** "What if you needed the k largest elements at every point in time, not just at the end?" (The same bounded min-heap already supports this — its contents at any point in the stream are always the correct answer for "so far.")

### Question 2 — Why does keeping the top k largest elements use a min-heap rather than a max-heap?

**Why interviewers ask it.** It directly probes whether the comparator-flipping/eviction-direction reasoning (Section 5) is genuinely understood, or whether "heap" is used as an undifferentiated single concept without the min-vs-max distinction mattering.

**Expected answer.** The heap needs to quickly identify which element to *evict* when the size cap is exceeded — and the element to evict is always the *smallest* of the currently-kept "largest k" candidates, since it's the weakest one that a new, larger candidate should displace. A min-heap makes exactly that smallest element instantly accessible via `peek()`/`poll()`; a max-heap would make the *largest* element instantly accessible, which is the opposite of what's needed for eviction decisions here.

**Minimum acceptable answer.** States "a min-heap is used to know what to evict," even without a fully precise justification.

**Strong Senior answer.** Generalizes the principle explicitly: whichever extreme a bounded-heap problem needs to *evict* is the extreme the heap type should expose directly via its root — the heap type is chosen by what needs evicting, not by what the problem is nominally "about" (top-k-*largest* still uses a *min*-heap).

**Staff-level extension.** Connects this to a comparator-design discipline more broadly: any custom comparator or heap ordering should be derived explicitly from "what does peek()/poll() need to return in this specific algorithm," rather than intuited from the problem's surface phrasing — the same discipline that catches the tie-break-direction bug in Top K Frequent Words (Section 5, Section 8).

**Common mistakes.** Defaulting to a max-heap for any "find the largest" framing without checking which specific operation (insertion, eviction, or both) the algorithm actually needs to be fast.

**Follow-up questions.** "How would this change for finding the k *smallest* elements instead?" (Symmetric — a bounded *max*-heap, evicting the current largest when over capacity.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-23/heaps/) yourself and confirm the same 10/10 assertions pass.
- This pattern has additional real, already-solved problems: LC 215 (Kth Largest Element), LC 347 (Top K Frequent Elements), LC 23 (Merge K Sorted Lists), LC 295 (Find Median from Data Stream, the two-heap pattern), and LC 973 (K Closest Points to Origin) across earlier weeks' practice code — study Merge K Sorted Lists specifically as the explicit-list version of this chapter's Problem 3's implicit-grid k-way merge.
- Attempt LC 253 (Meeting Rooms II) from scratch — it's heap-shaped but already solved and correctly categorized under Intervals ([Intervals, Merging, and Sweep Line](../03-data-structures-algorithms/INDEX.md), not yet written) elsewhere in this repository; working through it here checks whether the heap primitive transfers to an interval-scheduling framing.

## 17. Debugging Exercises

**Symptom:** a "top k most frequent items" feature works correctly for most inputs but occasionally returns the wrong tie-break winner when two items have identical frequency.

**Diagnose:** check the heap's comparator tie-break branch specifically — Section 5/8 names this exact bug: the tie-break direction inside a bounded min-heap must be the *inverse* of the problem's stated tie-break rule, since the heap evicts its "worst" element (by the problem's own ranking) first, and among ties, "worst" is the opposite end of whatever the problem's tie-break criterion favors. Confirm by constructing a small, controlled input with a known tie and checking whether the comparator's tie-break comparison (`a.compareTo(b)` vs. `b.compareTo(a)`) matches the required eviction direction, not the problem's display-order direction.

## 18. Design Exercises

**Design constraint:** design a real-time leaderboard that must report the top 10 highest scores at any moment, given a continuous stream of score submissions from millions of players, without ever re-sorting the full set of all submitted scores.

Design this using the bounded min-heap technique from Section 5/15 directly: a min-heap capped at size 10, updated in O(log 10) — effectively O(1) — per new score submission, evicting the current minimum whenever a higher score arrives and the heap is already full. State explicitly why this scales to millions of submissions without ever holding more than 10 scores in the heap itself, and name the real trade-off versus a design that keeps every score sorted: this design cannot answer "what's the 50th highest score" without re-deriving it from a different structure, since the heap deliberately discards everything below the top 10 the moment it's evicted.

## 19. Further Reading

- [`java.util.PriorityQueue`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/PriorityQueue.html) — official documentation for the class this entire chapter's techniques are built on.
- [Stacks and the Monotonic Stack](stacks-and-monotonic-stack.md) — a different structure solving a superficially similar "track the relevant extreme as you scan" family of problems, worth contrasting directly: a monotonic stack tracks *all* currently-relevant candidates in order; a heap tracks only the single current extreme.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what a heap is for and why it beats re-sorting for repeated extreme-value queries | [Section 3](#3-foundation-l1) |
| L2 | Choose correctly between a max-heap, a min-heap, and a size-bounded heap for a new problem | [Interview Question 2](#question-2--why-does-keeping-the-top-k-largest-elements-use-a-min-heap-rather-than-a-max-heap) |
| L3 | Derive the comparator-flipping argument for a bounded top-k heap, and explain the k-way-merge-over-an-implicit-grid technique | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real tie-break bug in a bounded-heap comparator (Section 17), and design a real streaming top-N system using this chapter's bounded-heap technique (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
