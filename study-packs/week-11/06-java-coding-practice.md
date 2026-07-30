---
title: "Java Coding Practice — Week 11"
week: 11
last_reviewed: 2026-07-29
---

# Java Coding Practice — Week 11

**Mixed review, timed — 15 problems spanning patterns from arrays, strings, stacks, linked lists, trees, graphs, binary search, sliding window, and two pointers, none repeating a problem already solved in Weeks 1–10. All code compiled and executed — see the verification block and `MANIFEST.md`.**

## Table of Contents

1. [The 15 problems](#the-15-problems)
2. [Pattern map](#pattern-map)
3. [Verification](#verification--real-not-asserted)

---

## The 15 problems

| # | Problem | Pattern | Key invariant |
|---|---|---|---|
| 1 | LC 1 — Two Sum | Hash map | One pass; store what's SEEN, check for the complement before inserting |
| 2 | LC 20 — Valid Parentheses | Stack | Push openers; on a closer, pop and match — mismatch or leftover = invalid |
| 3 | LC 704 — Binary Search | Binary search | `lo <= hi`, narrow by comparing `nums[mid]` to target |
| 4 | LC 53 — Maximum Subarray | Kadane's | `current = max(nums[i], current + nums[i])` — reset when extending hurts more than restarting |
| 5 | LC 206 — Reverse Linked List | Linked list | Three pointers (`prev`, `head`, `next`), rewire one link per step |
| 6 | LC 102 — Level Order Traversal | BFS | Process one full queue-size's worth of nodes per level, not one node at a time |
| 7 | LC 200 — Number of Islands | Flood fill (DFS) | Sink every connected `'1'` to `'0'` on first visit so it's never counted twice |
| 8 | LC 33 — Search in Rotated Sorted Array | Modified binary search | Exactly one half is always properly sorted — determine which, then decide which half to search |
| 9 | LC 155 — Min Stack | Auxiliary stack | A parallel stack tracks the running minimum at each push, so `getMin()` is O(1) |
| 10 | LC 121 — Best Time to Buy/Sell Stock | Single pass | Track the minimum price seen SO FAR; profit = current price minus that running minimum |
| 11 | LC 15 — 3Sum | Sort + two pointers | Fix one element, two-pointer the rest; skip duplicates at BOTH the anchor and the two pointers |
| 12 | LC 236 — Lowest Common Ancestor | Tree recursion | If a node's left AND right subtrees both report finding a target, that node IS the LCA |
| 13 | LC 76 — Minimum Window Substring | Sliding window | Expand right until the window satisfies the need; then shrink left while it still does, tracking the best |
| 14 | LC 42 — Trapping Rain Water | Two pointers | Water trapped at a position is bounded by the SMALLER of the two running maxes from each side |
| 15 | LC 208 — Implement Trie | Trie (26-ary tree) | Each node is an array of 26 children plus an `isWord` flag; insert/search/prefix all just walk the same path |

## Pattern map

This week's drill is deliberately organized by PATTERN, not by problem number, because the actual skill being reviewed is pattern recognition under time pressure — given an unseen problem, which of these ~10 shapes does it resemble, not "have I seen this exact problem before." Two pairs worth noting explicitly:

- **LC 704 and LC 33 are the same skeleton** (binary search), but LC 33 requires one extra decision per iteration (which half is sorted) before the LC 704 logic applies — recognizing "this is binary search, PLUS one wrinkle" is faster than re-deriving from scratch.
- **LC 53 and LC 121 are structurally identical** (track a running extreme value, derive an answer from the current element relative to it) despite looking like different domains (subarray sum vs. stock price) — both are "single pass, track one running value" problems wearing different costumes.

## Verification — real, not asserted

```
== LC 1: Two Sum ==
  PASS  twoSum([2,7,11,15], 9) = [0,1]

== LC 20: Valid Parentheses ==
  PASS  "()[]{}" is valid
  PASS  "(]" is invalid

== LC 704: Binary Search ==
  PASS  binarySearch finds 9 at index 4
  PASS  binarySearch: 2 not present

== LC 53: Maximum Subarray ==
  PASS  maxSubArray = 6 ([4,-1,2,1])

== LC 206: Reverse Linked List ==
  PASS  reverseList([1,2,3,4,5]) = [5,4,3,2,1]

== LC 102: Binary Tree Level Order Traversal ==
  PASS  levelOrder = [[3],[9,20],[15,7]]

== LC 200: Number of Islands ==
  PASS  numIslands = 3

== LC 33: Search in Rotated Sorted Array ==
  PASS  searchRotated finds 0 at index 4
  PASS  searchRotated: 3 not present

== LC 155: Min Stack ==
  PASS  getMin() = -3
  PASS  top() = 0 after pop
  PASS  getMin() = -2 after pop

== LC 121: Best Time to Buy and Sell Stock ==
  PASS  maxProfit = 5 (buy at 1, sell at 6)
  PASS  maxProfit = 0 (never profitable)

== LC 15: 3Sum ==
  PASS  threeSum([-1,0,1,2,-1,-4]) finds 2 unique triples
  PASS  threeSum finds [-1,-1,2]
  PASS  threeSum finds [-1,0,1]

== LC 236: Lowest Common Ancestor ==
  PASS  LCA(5,1) = root(3)
  PASS  LCA(6,2) = 5

== LC 76: Minimum Window Substring ==
  PASS  minWindow("ADOBECODEBANC","ABC") = "BANC"
  PASS  minWindow("a","aa") = "" (impossible)

== LC 42: Trapping Rain Water ==
  PASS  trap(...) = 6 units

== LC 208: Implement Trie ==
  PASS  trie contains "apple" after insert
  PASS  trie does NOT contain "app" as a full word
  PASS  trie DOES have "app" as a prefix
Week 11 mixed-review suite (15 problems): 27/27 assertions passed
```

Full source: `practice/java/week-11/mixed-review/src/`. Reproduce: `cd practice/java/week-11/mixed-review && javac -d out src/*.java && java -cp out Main`.

## Exit check

- [ ] All 15 problems solved cold, timed, without looking at the pattern map first
- [ ] Can name the shared pattern (not just solve) for the LC 704/LC 33 pair and the LC 53/LC 121 pair unprompted
- [ ] Given an unseen 16th problem, can correctly identify which of these ~10 patterns it most resembles within the first minute of reading it
