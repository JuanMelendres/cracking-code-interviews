---
title: "Cheat Sheet: Design-Style Coding Problems (LRU, LFU, Iterators)"
slug: design-style-coding-problems
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2115
canonical: ../syllabus/03-data-structures-algorithms/design-style-coding-problems.md
last_updated: 2026-09-06
---

# Design-Style Coding Problems (LRU, LFU, Iterators)

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/design-style-coding-problems.md`](../syllabus/03-data-structures-algorithms/design-style-coding-problems.md)

## Core Mental Model

A design-style problem specifies a class with multiple methods, each with its own required time complexity — the entire problem is choosing and combining data structures so *every* method meets its bound simultaneously, not finding one novel algorithm. A design that gets `get()` to O(1) but leaves `put()` at O(n) has missed the point. The single most common composition: a hash map from key to node, plus a linked structure maintaining some order among those same nodes — the map gives O(1) lookup, the linked structure gives O(1) reordering/removal once a node is found.

## Essential Definitions

- **LRU/LFU composition** — hash map (key → node) + doubly-linked list (or `frequency -> LinkedHashSet<Node>` for LFU) for O(1) lookup and O(1) reordering.
- **`minFreq` invariant (LFU)** — a fresh entry always starts at frequency 1, and frequency only ever increases, so `minFreq` only ever needs incrementing or resetting to 1 — never a full bucket scan.
- **Bounded per-source heap merge** — feed only each source's last-k contributions into a k-way heap merge when only the top-k final result matters; no source could contribute past position k anyway.
- **Structure-collapsing** — an apparent two-stack ("back"/"forward" history) design often collapses into a single list plus a movable position pointer, since "forward history" is just the suffix past the current pointer.

## Recognition Signals / When to Use This Pattern

| Signal in the problem | Technique | Complexity |
|---|---|---|
| Cache with O(1) get/put, evict least-recently-used | Hash map + doubly-linked list | O(1) per op |
| Cache with a second eviction dimension (frequency, then recency) | Hash map + `frequency -> LinkedHashSet<Node>` + tracked `minFreq` | O(1) per op |
| Per-key values arrive in strictly increasing timestamp order, need point-in-time lookup | Sorted-by-construction list + "floor" binary search | O(log k) per get |
| Merge top-k results across many independent sources | Bound each source's contribution to k before heap-merging | O(f · log(k·f)) |
| "Back/forward" navigation history | Single growable list + movable current-position pointer (not two stacks) | O(1) for back/forward, amortized O(1) for visit |

## Common Mistakes

- Optimizing one required operation while overlooking another's stated complexity requirement — a design with O(1) `get` but O(n) `put` (or vice versa) hasn't solved an LRU/LFU-style problem.
- Reaching for the two-stack design reflexively for any "history/navigation" problem without checking whether a single structure with a movable pointer is simpler and equally correct.
- Assuming any problem with "rate limiter," "queue," or "cache" in its name requires thread-safety primitives — check the problem's actual tag/intent (Logger Rate Limiter is plain "Design," not "Concurrency"; its difficulty is entirely in the data model, one last-seen timestamp per distinct message).

## Complexity Reference

- LFU Cache: O(1) for both `get` and `put`.
- Time Based Key-Value Store: O(log k) per `get`, O(1) amortized per `set`.
- Design Twitter: O(f · log(10f)), f = followee count.
- Design Browser History: O(1) for `back`/`forward`, amortized O(1) for `visit`.
- Logger Rate Limiter: O(1) per call.

## Interview Answer Skeleton

**30-sec:** A design problem asks for a class whose every method must hit its own stated complexity simultaneously; the skill is composing already-familiar structures (hash map, linked list, heap, sorted list) so no operation is left behind, not inventing a new algorithm.

**2-min:** Before choosing data structures, list every required method and its target complexity separately. Default to "hash map for lookup, plus a linked/ordered structure for the secondary property" for cache-eviction designs, adapting the secondary structure to the eviction rule — LRU needs only recency (doubly-linked list); LFU needs frequency then recency (`frequency -> LinkedHashSet<Node>` plus a `minFreq` pointer that never needs to scan buckets, since frequency only increases). Explicitly check whether a "design"-sounding problem actually implies thread-safety rather than assuming it.

**Whiteboard:** Draw the hash map on one side (key → node reference) and the doubly-linked list on the other (nodes in recency order), with arrows showing how a `get` call jumps via the map directly to a node, then splices it to the front of the list in O(1) using its own prev/next pointers — no traversal needed. For LFU, add a second map from frequency → bucket, with a `minFreq` arrow pointing at the currently-lowest occupied bucket.

**Staff-level framing:** This compositional design skill is directly the skill needed to design a real production component: an in-memory cache with a specific eviction policy, a feature-flag rollout history queryable by point-in-time, a social-feed aggregation service. Design Twitter's bounded-per-source-heap technique is a real, load-bearing pattern in production feed-aggregation systems at scale; the time-indexed store's floor-binary-search technique is the same shape as any "as-of" or audit-log point-in-time query a real system needs to answer efficiently.

## Production Warning Signs

- **Symptom:** an LFU cache implementation passes basic get/put tests but occasionally evicts the wrong entry when multiple entries share the same (non-minimum) frequency.
- **Diagnose:** check whether the per-frequency bucket's own internal structure is genuinely providing insertion-order (recency) semantics for ties — a bug here often traces to using a plain `HashSet` instead of a `LinkedHashSet` for the per-frequency bucket, silently losing the LRU-within-frequency tiebreak the problem requires, since a plain `HashSet` has no defined iteration order.

## Related

- syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md
- syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md
- syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md
- syllabus/03-data-structures-algorithms/concurrency-coding-problems.md
