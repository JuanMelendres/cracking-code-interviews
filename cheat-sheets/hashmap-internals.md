---
title: "Cheat Sheet: HashMap Internals"
slug: hashmap-internals
document_type: cheat-sheet
domain: collections
topic_id: T-201
canonical: ../handbook/collections/hashmap-internals.md
last_updated: 2026-08-04
---

# HashMap Internals

**Canonical chapter:** [`syllabus/02-java/collections/hashmap-internals.md`](../syllabus/02-java/collections/hashmap-internals.md)

## Core Mental Model

A `HashMap` is an array of buckets, and every design decision in its internals exists to answer one question: what do we do when two keys land in the same bucket? Load factor and resizing keep the average bucket short as the map grows. Treeification is insurance for when collisions happen anyway despite a well-sized table — converting an overloaded bucket from a linked list (O(n) worst case) into a balanced tree (O(log n) worst case), so a pathological hash distribution degrades gracefully instead of catastrophically.

## Essential Definitions

- **HashMap** — stores key-value pairs in an array of buckets; a key's `hashCode()` (spread through an internal hash-spreading function) determines its bucket. Ideally each bucket holds 0-1 entries, giving O(1) average-case `get`/`put`.
- **Load factor** (default 0.75) — controls when the array resizes: once `size` exceeds `capacity × loadFactor` (the threshold), the array doubles and every entry is rehashed.
- **Lazy initialization** — the backing `table` array is `null` until the first `put()`; construction alone doesn't allocate it.
- **Treeification** (JDK 8+) — a bucket with ≥8 nodes (`TREEIFY_THRESHOLD`) converts from a linked list to a red-black tree, but **only if** table capacity is also ≥64 (`MIN_TREEIFY_CAPACITY`) — otherwise the map resizes the whole table first. Bounds worst-case bucket lookup at O(log n) instead of O(n).

## Decision Table

| Choice | Benefit | Cost |
|---|---|---|
| Lower load factor (e.g., 0.5) | Fewer collisions, faster average lookup | More memory per entry |
| Higher load factor (e.g., 0.9) | Less memory overhead | More collisions, more resize events |
| Default load factor 0.75 | Measured, historically-tuned balance | Not optimal for every specific workload |
| Treeification (JDK 8+) | Bounds worst case at O(log n) instead of O(n) | Tree nodes are larger than list nodes |

| Symptom | Likely cause |
|---|---|
| Lookup latency climbing with stable entry count | Poor hash distribution overloading specific buckets — check for treeified bins |
| Many resize events during population | Initial capacity not sized for the known final entry count |
| Non-deterministic-seeming iteration order | Expected — iteration order is not a contract |

## Key Numbers (real, executed — OpenJDK 21.0.12)

```
Default capacity 16, load factor 0.75 -> threshold 12
table before first put(): null
after 12 entries (size=12): table length=16, threshold=12
after 13th entry  (size=13): table length=32, threshold=24

Well-distributed hash: 50,000 lookups in 1,202,750 ns  (24.1 ns/lookup)
Constant hash (all collide): 50,000 lookups in 3,699,804,084 ns (73996.1 ns/lookup)
-> ~3,076x slowdown
Bucket node's actual runtime class after collision: TreeNode
```

## Common Pitfalls

- Assuming O(1) lookup holds regardless of the key's `hashCode()` quality
- Not sizing initial capacity when the final entry count is known, causing avoidable resize/rehash events
- Believing treeification eliminates the cost of a poor hash distribution rather than merely bounding its worst case

## Interview Answer Skeleton

**30-sec:** `HashMap` is an array of buckets; a key's spread hash picks the bucket. The array resizes (doubles) once size exceeds capacity × 0.75 — measured directly at the 13th entry. A poor `hashCode()` forces collisions regardless of table size — measured at a ~3,076x slowdown for a constant-hash key, even with treeification converting the bucket to a tree.

**2-min:** Add why it exists (O(1) average lookup, trading memory and ordering for speed) + the load-factor trade-off + the measured 3,076x slowdown production example.

**Whiteboard:** Draw key → hash spread → bucket index → empty (insert) or collision (treeify if ≥8 nodes and capacity ≥64, else append/resize). Annotate: "this bounds the worst case, but doesn't fix a bad hashCode() — it just makes the damage O(log n) instead of O(n)."

**Staff-level framing:** the dual treeification condition (bucket overload AND table capacity) is itself a design lesson — it distinguishes a sizing problem from a genuine hash-collision problem, built directly into the mechanism rather than treeifying reflexively at the first long bucket.

## Production Warning Signs

- **Real incident pattern:** a `HashMap<CompositeKey, CachedResult>` cache's p99 latency climbs steadily over weeks despite entry count staying bounded. Root cause: `CompositeKey.hashCode()` XOR-combines two frequently near-equal fields, cancelling entropy — a heap dump + reflective bucket inspection shows a few (treeified) buckets holding most entries.
- Fix has no downside: rewrite `hashCode()` via `Objects.hash(...)`, add a distribution test against production-representative key samples. Switching map implementations does not help — the defect is in the key, not the map.

## Related

- `syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md`
- `syllabus/02-java/collections/concurrenthashmap-internals.md`
