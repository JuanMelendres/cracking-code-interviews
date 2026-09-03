---
title: "Week 14 Study Pack — Manifest"
week: 14
plan: B
last_reviewed: 2026-07-31
---

# Week 14 Study Pack — Manifest

**Topics:** T-201, T-205, T-207, T-202, T-209 · **Plan:** B, Collections (Phase 4/5 — second new-domain week; Collections previously had zero study-pack coverage)
**Files:** 12 (+ this manifest) · **Total words:** 6,216 (real count, `wc -w` over all 12 files; checked 2026-07-31 for slimming — this week was already authored lean, post-dating the study-pack slimming convention, so only trivial prose tightening in `09`/README applied; 01–05 already summary+link, `06`/`07`/`08`/`10`/`resources.md` had no cuttable fat)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, schedule, exit criteria | 566 |
| 2 | `01-hashmap-internals.md` | T-201 — summary + link; full chapter canonical at `syllabus/02-java/collections/hashmap-internals.md` | 590 |
| 3 | `02-concurrenthashmap-internals.md` | T-205 — summary + link; full chapter canonical at `syllabus/02-java/collections/concurrenthashmap-internals.md` | 531 |
| 4 | `03-blockingqueue-family.md` | T-207 — summary + link; full chapter canonical at `syllabus/02-java/collections/blockingqueue-family.md` | 518 |
| 5 | `04-arraylist-and-linkedlist-internals.md` | T-202 — summary + link; full chapter canonical at `syllabus/02-java/collections/arraylist-and-linkedlist-internals.md` | 526 |
| 6 | `05-collection-selection-decision-matrix.md` | T-209 — summary + link; full chapter canonical at `syllabus/02-java/collections/collection-selection-decision-matrix.md` | 486 |
| 7 | `06-java-coding-practice.md` | 3 problems, all compiled and run, 6/6 assertions pass | 298 |
| 8 | `07-flashcards.md` | 15 cards | 957 |
| 9 | `08-week-14-mock-interview.md` | 45-min Collections technical round | 679 |
| 10 | `09-code-review-exercise.md` | 5 defects across 5 topics in one class, with worked solution | 659 |
| 11 | `10-week-14-checklist.md` | Day-by-day checklist | 280 |
| 12 | `resources.md` | Sources classified PRIMARY/BOOK | 126 |

---

## Verification

| Item | Status |
|---|---|
| Java — HashMap Internals | **Executed.** OpenJDK 21.0.12, `--add-opens java.base/java.util=ALL-UNNAMED`. Real reflective proof of lazy table init, resize at the 13th entry (16→32, threshold 12→24), a real ~3,076x lookup slowdown from a constant-hashCode key, and real reflective confirmation of `TreeNode` treeification. Source: `practice/java/week-14/hashmap-internals/` |
| Java — ConcurrentHashMap Internals | **Executed.** Real 8-thread concurrent-write corruption of a plain `HashMap` (160,000 expected, ~68,683 actual), real correctness of the identical workload on `ConcurrentHashMap` (160,000), real lost-update rate for `get()`+`put()` (26,212 of 160,000), real correctness of `merge()`-based atomic increment (160,000). Source: `practice/java/week-14/concurrenthashmap/` |
| Java — BlockingQueue Family | **Executed.** Real `ArrayBlockingQueue.put()` blocking (~313ms) confirmed via thread state `WAITING`, real `SynchronousQueue` zero-capacity handoff (~305ms) similarly confirmed. Source: `practice/java/week-14/blockingqueue/` |
| Java — ArrayList/LinkedList Internals | **Executed.** Real reflective proof of ArrayList's ~1.5x growth factor, real ~320x random-access slowdown for LinkedList vs. ArrayList on 50,000 elements, real ~117x front-insertion slowdown for ArrayList vs. LinkedList. Source: `practice/java/week-14/arraylist-linkedlist/` |
| Java — Coding practice | **Executed.** `6/6` assertions pass across 3 problems, each exercising this week's topics together. Source: `practice/java/week-14/mixed-review/` |
| Interview statistics | None invented anywhere in this pack |

## Errata addressed this week

None. This is new-domain content (Collections had zero prior coverage), not a correction to existing material.

## Scope note

This week covers the 5 highest-weighted-IWI Collections topics (T-201, T-205, T-207, T-202, T-209) out of 9 total in the D2 register (`00-project/knowledge-architecture-blueprint.md` §D2). The remaining 4 (T-203 TreeMap/TreeSet & Red-Black mechanics, T-204 ArrayDeque & the legacy Stack/Vector problem, T-206 CopyOnWriteArrayList, T-208 Fail-fast vs. weakly-consistent iterators) are deferred — stated explicitly, not silently dropped.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, including `--add-opens` for two of the five demos). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
