---
title: "Cheat Sheet: Coding Interview Pattern-Recognition Methodology"
slug: coding-interview-pattern-recognition-methodology
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2120
canonical: ../syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md
last_updated: 2026-09-17
---

# Coding Interview Pattern-Recognition Methodology

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md`](../syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md)

## Core Mental Model

The 18 other chapters in this domain each teach one pattern deeply. This is the missing first step: a real method for going from an unfamiliar problem statement to the right pattern, fast — via constraint reading and signal matching, not memorized problem lists.

## The Five-Step Method

1. **Understand** — restate the problem, note input/output types, note the constraint on `n`.
2. **Signals** — match keywords/shape to a pattern (see table below).
3. **Brute force** — state a working, if slow, solution in one sentence.
4. **Optimize** — apply the matched pattern.
5. **Verify** — trace a small example, then state the real final complexity.

## Signal-to-Pattern Table (top rows, by real IWI)

| Signal | Pattern | Data structure |
|---|---|---|
| "contiguous subarray/substring" | Two pointers / sliding window | Array |
| "shortest/cheapest path," "connected components" | Graph traversal (BFS/DFS/Dijkstra) | Adjacency list |
| "design O(1) get/put" | Design-style composition | Combined structures |
| "count the ways," "min/max cost," optimal substructure | Dynamic programming | Memo table |
| "kth largest," "top K," "merge K sorted" | Heap / priority queue | `PriorityQueue` |
| Sorted input, "search on the answer" | Binary search | Sorted array / answer space |
| "next greater element," "valid parentheses" | Monotonic stack | Stack |
| "all combinations/permutations/subsets" | Backtracking | Recursion + state |
| "count duplicates," "group by," "anagram" | Hashing / frequency map | `HashMap`/`HashSet` |

Full table (18 rows, ranked by real IWI data): see the canonical chapter, Section 4.

## Constraint-to-Complexity Heuristic

| `n` bound | Complexity budget |
|---|---|
| ≤ ~20 | O(2ⁿ) / O(n!) |
| ≤ ~500 | O(n³) |
| ≤ ~5,000 | O(n²) |
| ≤ ~10⁶ | O(n log n) / O(n) |
| ≤ ~10⁹ or unbounded | O(log n) / O(1) |

## Common Pitfalls

- Coding before finishing steps 1–2 (understand, signals) — the single most common interview failure mode.
- Forcing a recently-practiced pattern onto a problem whose signals point elsewhere.
- Ignoring the stated bound on `n` — it directly tells you the required complexity class.

## Interview Answer Skeleton

**30-sec:** Five-step method — understand, match signals to a pattern, brute force, optimize, verify — with the constraint on `n` telling you which complexity class you're even allowed to land on.

**2-min:** Add: a real signal-to-pattern table (contiguous subarray → sliding window; shortest path → BFS; optimal substructure → DP; etc.), and the constraint heuristic (`n ≤ 20` → brute force is fine; `n ≤ 10⁶` → need O(n log n) or better).

**Staff-level framing:** The same constraint-to-complexity reasoning is what catches an O(n²) production loop before it becomes an incident at 10x the data volume — this isn't only an interview skill.

## Related

- syllabus/03-data-structures-algorithms/INDEX.md (all 18 pattern chapters this table routes to)
- syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md
