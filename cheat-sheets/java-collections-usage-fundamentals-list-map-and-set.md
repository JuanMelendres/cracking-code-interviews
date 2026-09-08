---
title: "Cheat Sheet: Collections Usage Fundamentals: List, Map, and Set"
slug: java-collections-usage-fundamentals-list-map-and-set
document_type: cheat-sheet
domain: 02-java
topic_id: T-2207
canonical: ../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md
last_updated: 2026-09-08
---

# Collections Usage Fundamentals: List, Map, and Set

**Canonical chapter:** [`syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md`](../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md)

## Core Mental Model

`List` = ordered, duplicates allowed. `Set` = no duplicates, no guaranteed order. `Map` = key → value association. Choose based on the actual access pattern, not habit.

## Essential Definitions

- **`List`** — `add`, `get(index)`, `remove(value)`/`remove(index)`, `contains` (O(n) scan).
- **`Set`** — `add` (no-op if already present, returns `false`), `contains` (~O(1) for `HashSet`), `remove`.
- **`Map`** — `put(key, value)`, `get(key)` (returns `null` if missing), `getOrDefault(key, fallback)`, `containsKey`.
- **`new HashSet<>(someList)`** — builds a Set from a List, discarding duplicates in one line.

## Decision Table

| Need | Use |
|---|---|
| Preserve order, duplicates meaningful | `List` |
| Uniqueness / "does this exist" check | `Set` |
| Lookup, counting, or grouping by a key | `Map` |
| Fast membership check on a large collection | `Set`/`Map` (~O(1)) over `List.contains()` (O(n)) |

## Common Pitfalls

- `List<Integer>.remove(2)` removes by *index*, not value — a real autoboxing gotcha; use `remove(Integer.valueOf(2))` to remove by value.
- `map.get(missingKey)` returns `null`, not a default — leads to a `NullPointerException` on the next line if unhandled; use `getOrDefault` or check `containsKey`.
- Assuming `HashSet`/`HashMap` preserve insertion order — they don't, by design.
- Scanning a `List` with `contains()` where a `Set` was the right tool — fine at small scale, silently O(n) and slow as the collection grows.

## Interview Answer Skeleton

**30-sec:** `List` is ordered and allows duplicates; `Set` has no duplicates and no guaranteed order; `Map` associates a key with a value. Reach for `Set`/`Map` over a `List` scan whenever the real question is "does this exist" or "look this up."

**2-min:** Add the complexity argument directly: `HashSet.contains()`/`HashMap.get()` are ~O(1) on average; scanning a `List` is O(n) — for a large enough collection this is the entire performance story. Mention `map.get()` returning `null` on a missing key as a real, common NPE source.

**Whiteboard:** Draw three boxes — an ordered list with repeated values, a set with the duplicates collapsed, a map with arrows from keys to values — then draw the same "find X" operation as a scan (List) vs. a direct jump (Set/Map).

**Staff-level framing:** A service scanning a `List` with linear `contains()` on every request doesn't fail in development with a small dataset — it fails quietly in production as data grows, exactly the kind of load-dependent defect that's cheap to prevent by defaulting to the right collection type from the start.

## Related

- syllabus/02-java/collections/hashmap-internals.md
- syllabus/02-java/collections/arraylist-and-linkedlist-internals.md
- syllabus/02-java/collections/collection-selection-decision-matrix.md
