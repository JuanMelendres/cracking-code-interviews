---
title: "Data Structures & Algorithms — Domain Index"
document_type: syllabus-domain-index
domain: 03-data-structures-algorithms
status: 9 of 18 planned topics written (Phase 5, in progress, started 2026-09-03)
last_updated: 2026-09-03
---

# Data Structures & Algorithms

Complexity analysis through advanced structures (Master Topic Register D14, T-1401–T-1419). [Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) in `01-computer-science-foundations` already covers T-1401 (complexity analysis itself) — this domain covers T-1402 onward, one canonical chapter per coding-interview pattern, each assigned a new `T-21xx` topic ID per the plan's reserved range (§9), distinct from the D14 practice-log IDs which remain attached to their existing coding-problem meaning.

> **Phase 5 update (2026-09-03).** Writing started, in the Master Topic Register's own priority order (§9 of `knowledge-architecture-blueprint.md`, ranked by IWI). Each chapter **elevates** already-real, already-compiled, already-verified practice code and its study-pack retrospective — not new algorithmic writing from scratch — into the full 20-section Topic Specification, adding Foundation (L1), Core Concepts (L2), Staff-level (L4), and interview-question framing the source study-pack material (written for engineers already doing practice-volume drilling) didn't need. Every chapter's `source_history` front-matter field records the exact study-pack file it was elevated from.

## Topics

| Topic ID | Title | D14 register ID | Mastery levels covered | Location |
|---|---|---|---|---|
| — | Complexity analysis & amortization | T-1401 | Covered by [Algorithmic Complexity](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) in `01-computer-science-foundations` | Not duplicated here |
| T-2101 | [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md) | T-1402 | L1, L2, L3, L4 — fully written | `syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md` |
| T-2102 | [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) | T-1403 | L1, L2, L3, L4 — fully written | `syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md` |
| T-2103 | [Binary Search, Including Search-on-Answer](binary-search-and-search-on-answer.md) | T-1404 | L1, L2, L3, L4 — fully written | `syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md` |
| T-2104 | [Linked Lists and In-Place Manipulation](linked-lists-and-in-place-manipulation.md) | T-1405 | L1, L2, L3, L4 — fully written | `syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md` |
| T-2105 | [Stacks and the Monotonic Stack](stacks-and-monotonic-stack.md) | T-1406 | L1, L2, L3, L4 — fully written | `syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md` |
| T-2106 | [Heaps, Top-K, and K-Way Merge](heaps-top-k-and-k-way-merge.md) | T-1407 | L1, L2, L3, L4 — fully written | `syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md` |
| T-2107 | [Trees, BSTs, and Traversal Patterns](trees-bst-and-traversal-patterns.md) | T-1408 | L1, L2, L3, L4 — fully written | `syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md` |
| T-2108 | [Graphs: BFS, DFS, Topological Sort, Dijkstra, Union-Find](graphs-bfs-dfs-and-shortest-paths.md) ⭐ | T-1409 | L1, L2, L3, L4 — fully written | `syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md` |
| T-2109 | [Backtracking and Pruning](backtracking-and-pruning.md) | T-1410 | L1, L2, L3, L4 — fully written | `syllabus/03-data-structures-algorithms/backtracking-and-pruning.md` |
| T-2110 | Dynamic Programming: 1D, 2D, Knapsack, Intervals ⭐ | T-1411 | Not yet written | Planned — elevate `study-packs/week-21/04-dynamic-programming-coding-practice.md` and `week-23/01-dp-coding-practice.md` |
| T-2111 | Intervals, Merging, and Sweep Line | T-1412 | Not yet written | Planned — elevate `study-packs/week-20/03-intervals-coding-practice.md` |
| T-2112 | Greedy and the Exchange Argument | T-1413 | Not yet written | Planned — elevate `study-packs/week-20/02-greedy-coding-practice.md` |
| T-2113 | Bit Manipulation | T-1414 | Not yet written | Planned — elevate `study-packs/week-20/04-bit-manipulation-coding-practice.md` |
| T-2114 | Tries and Prefix Structures | T-1415 | Not yet written | Planned — elevate `study-packs/week-21/01-tries-coding-practice.md` |
| T-2115 | Design-Style Coding Problems (LRU, LFU, Iterators) ⭐ | T-1416 | Not yet written | Planned — elevate `study-packs/week-22/04-design-coding-practice.md` |
| T-2116 | Concurrency Coding Problems ⭐ | T-1417 | Not yet written | Planned — elevate `study-packs/week-22/03-concurrency-coding-practice.md`; cross-links `02-java/concurrency/` |
| T-2117 | Advanced Structures: Segment Tree, Fenwick Tree, Rolling Hash | T-1418 | Not yet written | Planned — practice code exists at `practice/java/advanced-structures/` |
| T-2118 | Coding Interview Communication Protocol | T-1419 | Not yet written | Planned — the register's own note: "absent and disproportionately valuable" |

T-2101 through T-2109 were written first because they are exactly the Master Topic Register's own top-9-by-priority patterns (T-1402–T-1410) and because their underlying practice code and study-pack retrospectives already existed in full, verified depth — the fastest path to real canonical coverage with the lowest fabrication risk. Each of the nine real, compiled demos was re-run on OpenJDK 21.0.12 while writing its chapter (10–18 assertions passing each, 108 total across all nine) rather than trusted from the study-pack's own prior verification alone. T-2108 (Graphs, IWI 6.25) is the register's single highest-weighted pattern overall. Cheat sheets, flashcards, and production-cookbook entries for all nine have not been built yet — per this session's established batching discipline, that backlog is closed in a separate pass. T-2110 (Dynamic Programming, ⭐, IWI 5.85) is the next-highest-weighted remaining pattern and the natural next chapter — its two source study-packs (`week-21/04` and `week-23/01`) together cover 8 problems, roughly 60% more source material than any single chapter written so far.

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, `00-project/knowledge-architecture-blueprint.md` lines 353–370 for the full D14 register (T-1401–T-1419) this domain's topic list is drawn from, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
