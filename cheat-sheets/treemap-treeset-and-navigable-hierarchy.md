---
title: "Cheat Sheet: TreeMap/TreeSet and the Navigable Hierarchy"
slug: treemap-treeset-and-navigable-hierarchy
document_type: cheat-sheet
domain: collections
topic_id: T-203
canonical: ../handbook/collections/treemap-treeset-and-navigable-hierarchy.md
last_updated: 2026-09-02
---

# TreeMap/TreeSet & the Navigable Hierarchy

**Canonical chapter:** [`syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md`](../syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md)

## Core Mental Model

`TreeMap`/`TreeSet` trade `HashMap`/`HashSet`'s O(1) average-case speed for a real, structural guarantee: sorted iteration order and O(log n) WORST-CASE performance, verified by attacking it with the exact ascending-insertion input that breaks an unbalanced BST.

## Essential Definitions

- **Red-Black tree** — self-balancing BST; rebalances (rotations, recoloring) on every insertion to keep height bounded near 2·log₂(n+1) regardless of insertion order.
- **`NavigableSet`/`NavigableMap`** — INTERFACES `TreeSet`/`TreeMap` implement (`Collection → Set → SortedSet → NavigableSet`), not sibling classes — confirmed via `TreeSet.class.getInterfaces()`.
- **`floor(x)`/`ceiling(x)`** — nearest element ≤/≥ x, inclusive of an exact match.
- **`lower(x)`/`higher(x)`** — nearest element </> x, strictly exclusive of an exact match.

## Decision Table

| Question | Answer |
|---|---|
| Need sorted iteration or range/nearest-neighbor queries? | `TreeMap`/`TreeSet` — only one of the three that supports this natively |
| Need predictable insertion-order iteration, no sorting? | `LinkedHashMap`/`LinkedHashSet` |
| Need neither sorting nor insertion order, just fastest average lookup? | `HashMap`/`HashSet` |
| Worried about TreeMap's worst case under adversarial (sorted) input? | Don't be — verified directly, real height stayed bounded |

## Key Numbers

- Ascending insertion (worst case for a naive BST), n=100,000: naive BST real height = 100,000 (a straight line); `TreeMap` real height = 31 (theoretical bound ~33.2).
- `floor(25)` on `{10,20,30,40,50}` = 20; `ceiling(25)` = 30; `lower(30)` = 20; `higher(30)` = 40.

## Common Pitfalls

- Drawing `NavigableSet` as a sibling of `TreeSet` rather than its supertype — a real, audited defect this chapter corrects via direct reflection.
- Assuming `TreeMap`'s O(log n) is merely "usually true" like `HashMap`'s O(1) — it's a real, structural worst-case guarantee.
- Confusing `floor`/`ceiling` (inclusive) with `lower`/`higher` (strictly exclusive).

## Interview Answer Skeleton

**30-sec:** `TreeMap`/`TreeSet` are Red-Black-tree-backed, guaranteeing O(log n) worst-case and sorted iteration — verified with a real reflective height measurement (31 at n=100,000) under the exact ascending order that degenerates a naive BST into a straight line (height 100,000). `NavigableSet`/`NavigableMap` are the interfaces `TreeSet`/`TreeMap` implement, confirmed via `getInterfaces()`.

**2-min:** Add the leaderboard production scenario: switching a "scores near rank X" query from re-sorting a `HashMap` per request to `TreeMap.subMap`/`floorEntry`/`ceilingEntry` turns an O(n log n) full-sort-per-request into O(log n).

**Whiteboard:** Draw `Collection → Set → SortedSet → NavigableSet`, each arrow labeled with the capability it adds. Dashed arrow to `TreeSet` labeled "implemented by (real getInterfaces() proof)." Beside it, two trees from ascending insertion 1..10: a straight line labeled "naive BST, height 10" and a balanced tree labeled "TreeMap, height 5."

**Staff-level framing:** Choosing `TreeMap`/`TreeSet` over `HashMap`/`HashSet` is a real, structural trade a team should make deliberately, tied to an actual required capability — not a default "safer" choice, since it costs real, measurable overhead for workloads that never use ordering/range queries.

## Production Warning Signs

- A `/leaderboard/near/{score}` endpoint's p99 latency scales with total player count because every request re-sorts the entire `HashMap`. Fix: back the leaderboard with `TreeMap<Score, Player>` for native O(log n) range queries.

## Related

- `syllabus/02-java/collections/hashmap-internals.md`
- `syllabus/02-java/collections/collection-selection-decision-matrix.md`
- `syllabus/02-java/collections/concurrenthashmap-internals.md`
