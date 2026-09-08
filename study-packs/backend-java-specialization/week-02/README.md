---
title: "Backend Java Specialization, Week 2 — Collections, from Usage to Internals"
document_type: study-pack
week: 2
track: backend-java-specialization
status: draft
estimated_hours: 9
---

# Week 2 — Collections, from Usage to Internals

## Weekly Outcome

By the end of this week you can choose the correct `List`/`Map`/`Set`/`Queue` implementation for a stated workload with a defensible reason, explain `HashMap` resizing and treeification, `ArrayList` growth versus `LinkedList` node overhead, `ConcurrentHashMap`'s lock-striping evolution, and state precisely what "fail-fast" does and does not guarantee.

## Why This Week Matters

Week 1's `equals`/`hashCode` contract (T-101) is the foundation every hash-based collection in this week depends on — a broken contract silently corrupts a `HashMap` or `HashSet` in exactly the ways this week's chapters demonstrate directly. [`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../../syllabus/00-overview/learning-paths/backend-java-specialization.md) places `collections` immediately after `language-core` for this reason.

## Prerequisites

Week 1 complete, in particular T-101 (equals/hashCode/Comparable contracts) and T-2201 (OOP fundamentals, if starting from a lower baseline).

## Schedule

| Day | Focus |
|---|---|
| Mon | Collections Usage Fundamentals (T-2207) — a fast review if already comfortable with `List`/`Map`/`Set` |
| Tue | HashMap Internals (T-201) |
| Wed | ArrayList and LinkedList Internals (T-202); ArrayDeque Internals (T-204) |
| Thu | TreeMap/TreeSet and the Navigable Hierarchy (T-203); Fail-Fast vs. Weakly-Consistent Iterators (T-208) |
| Fri | ConcurrentHashMap Internals (T-205); CopyOnWriteArrayList (T-206) |
| Sat | BlockingQueue Family (T-207); Collection Selection Decision Matrix (T-209) |
| Sun | Hands-on exercises and review checklist below |

## Required Reading

The full `collections` subdomain, per [`syllabus/02-java/INDEX.md`](../../../syllabus/02-java/INDEX.md) (the exhaustive, canonical source).

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | T-2207 — Collections Usage Fundamentals: List, Map, and Set | [`java-collections-usage-fundamentals-list-map-and-set.md`](../../../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md) |
| 2 | T-201 — HashMap Internals | [`hashmap-internals.md`](../../../syllabus/02-java/collections/hashmap-internals.md) |
| 3 | T-202 — ArrayList and LinkedList Internals | [`arraylist-and-linkedlist-internals.md`](../../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md) |
| 4 | T-203 — TreeMap/TreeSet and the Navigable Hierarchy | [`treemap-treeset-and-navigable-hierarchy.md`](../../../syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md) |
| 5 | T-204 — ArrayDeque Internals and the Legacy Stack/Vector Problem | [`arraydeque-internals-and-the-legacy-stack-problem.md`](../../../syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md) |
| 6 | T-205 — ConcurrentHashMap Internals | [`concurrenthashmap-internals.md`](../../../syllabus/02-java/collections/concurrenthashmap-internals.md) |
| 7 | T-206 — CopyOnWriteArrayList and Copy-on-Write Trade-offs | [`copyonwritearraylist-and-copy-on-write-tradeoffs.md`](../../../syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md) |
| 8 | T-207 — BlockingQueue Family and Producer-Consumer | [`blockingqueue-family.md`](../../../syllabus/02-java/collections/blockingqueue-family.md) |
| 9 | T-208 — Fail-Fast vs. Weakly-Consistent Iterators | [`fail-fast-vs-weakly-consistent-iterators.md`](../../../syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md) |
| 10 | T-209 — Collection Selection Decision Matrix | [`collection-selection-decision-matrix.md`](../../../syllabus/02-java/collections/collection-selection-decision-matrix.md) |

## Hands-On Exercises

Real, compiled, executed demos exist for 9 of this week's 10 chapters (T-209 is a decision-framework chapter with no dedicated demo of its own — it references the other nine directly):

- [`practice/java/oop-fundamentals/collections-basics/`](../../../practice/java/oop-fundamentals/collections-basics/) (T-2207)
- [`practice/java/week-14/hashmap-internals/`](../../../practice/java/week-14/hashmap-internals/) (T-201, requires `--add-opens java.base/java.util=ALL-UNNAMED` per the chapter's own provenance note)
- [`practice/java/week-14/arraylist-linkedlist/`](../../../practice/java/week-14/arraylist-linkedlist/) (T-202)
- [`practice/java/collections/treemap-treeset-internals/`](../../../practice/java/collections/treemap-treeset-internals/) (T-203)
- [`practice/java/collections/arraydeque-internals/`](../../../practice/java/collections/arraydeque-internals/) (T-204)
- [`practice/java/week-14/concurrenthashmap/`](../../../practice/java/week-14/concurrenthashmap/) (T-205)
- [`practice/java/collections/copyonwritearraylist-tradeoffs/`](../../../practice/java/collections/copyonwritearraylist-tradeoffs/) (T-206)
- [`practice/java/week-14/blockingqueue/`](../../../practice/java/week-14/blockingqueue/) (T-207)
- [`practice/java/collections/fail-fast-vs-weakly-consistent/`](../../../practice/java/collections/fail-fast-vs-weakly-consistent/) (T-208)

## Interview Answer Drills

Answer, out loud, before checking each chapter's expected answer: "why does `HashMap` treeify a bucket at 8 entries instead of just growing the linked list further?" and "what exactly does `ConcurrentModificationException` protect against, and what does it not protect against?"

## Coding Problems

None — this pack is domain-depth reading, not coding-pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given four workload descriptions (frequent random access, frequent head/tail insertion, sorted-order iteration required, high-concurrency reads with rare writes), pick the correct collection for each and justify it against T-209's decision matrix without looking first.

## Review Checklist

- [ ] Completed all 10 chapters' own L1–L4 Mastery Checklists.
- [ ] Reproduced at least 6 of the 9 real demos listed above.
- [ ] Can explain, unprompted, why `CopyOnWriteArrayList` iterators never throw `ConcurrentModificationException` and what that trade-off costs.

## Completion Criteria

- [ ] Can state `HashMap`'s resize trigger (load factor) and treeification threshold from memory.
- [ ] Can explain the `ArrayList` growth factor and why `LinkedList` rarely wins in practice despite O(1) head insertion.
- [ ] Can correctly select a collection for each of T-209's own scenario prompts.

## Retrospective

Note whether you previously assumed `LinkedList` was the "correct" choice for frequent insertions without measuring — this is one of the most common Mid-level collection misconceptions T-202 and T-209 both address directly.

## Next Week

[Week 3 — Concurrency Internals](../week-03/README.md).
