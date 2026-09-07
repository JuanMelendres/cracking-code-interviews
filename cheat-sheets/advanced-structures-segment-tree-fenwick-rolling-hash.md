---
title: "Cheat Sheet: Advanced Structures: Segment Tree, Fenwick Tree, and Rolling Hash"
slug: advanced-structures-segment-tree-fenwick-rolling-hash
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2117
canonical: ../syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md
last_updated: 2026-09-06
---

# Advanced Structures: Segment Tree, Fenwick Tree, and Rolling Hash

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md`](../syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md)

> This domain is explicitly outside the core interview-prep roadmap (`00-project/learning-roadmap.md` §5 deliberately excludes the Expert tier these structures belong to). This chapter is recognition-level polish, not a prerequisite — it has the lowest IWI (4.2) of the 18 D14 patterns, but recognizing these structures cold at Staff-level loops is a real, if occasional, differentiator.

## Core Mental Model

These three structures answer a shared question — "update part of a large collection, then query an aggregate over part of it, repeatedly, without recomputing from scratch each time." A **Fenwick tree** supports point update + prefix-sum query in O(log n) with a small constant factor. A **segment tree** generalizes this to arbitrary combinable range aggregates (sum, max, GCD) at a higher constant factor. A **rolling hash** recomputes a sliding window's hash in O(1) per slide instead of rehashing from scratch.

## Essential Definitions

- **Coordinate compression** — maps arbitrary, possibly sparse integers to a dense `1..m` rank space (sorted, deduplicated array + binary search) so a Fenwick tree can index into them.
- **Static array segment tree** — flat, iterative, no recursion; requires the domain to be small and known upfront.
- **Dynamic segment tree** — creates child nodes lazily, only along paths actual updates touch; supports domains too large to materialize (e.g. `[0, 1e9]`).
- **Exact bit-packing** — when the alphabet is small enough (4 DNA bases → 2 bits each), a fixed-length window packs losslessly into a machine word: a real, collision-free encoding, not a lossy hash.
- **Polynomial rolling hash** — needed for larger alphabets or variable-length windows; genuinely lossy, so a hash match is not proof of equality without an explicit character-by-character confirmation.

## Recognition Signals / When to Use This Pattern

| Signal in the problem | Technique | Complexity |
|---|---|---|
| Point update + prefix-sum query, domain coordinate-compressible | Fenwick tree (BIT) | O(log n) per op |
| Arbitrary range aggregate (max, min, GCD), not just prefix sum | Static array segment tree | O(log n) per op, O(n) build |
| Domain too large/dynamic to coordinate-compress upfront (e.g. `[0, 1e9]`, online updates) | Dynamic segment tree with lazy node creation | O(log(range)) per op |
| Fixed-length window over a small alphabet (few symbols) | Exact bit-packed encoding — zero collision risk | O(n) |
| Variable-length or large-alphabet substring matching | Polynomial rolling hash + mandatory collision-guard comparison | O(n) per check; O(n log n) expected with binary search on length |

## Common Mistakes

- Getting a doubling or comparison direction backwards in a coordinate-compressed query — Reverse Pairs' own real, documented bug: querying "`2*v < nums[i]`" is backwards; LeetCode's definition requires the *earlier* index's value to be the large one. That backwards version passed 2 of 4 test cases, including the first one checked.
- Trusting a rolling-hash match without an explicit collision-guard comparison, when the hash isn't a provably exact, lossless encoding — a genuinely worse failure mode (silently wrong) than a slower, always-correct approach.
- Reaching for a full segment tree when a Fenwick tree would suffice (prefix-sum-and-point-update only) — not incorrect, but unnecessary implementation complexity and constant-factor overhead.
- Attempting to coordinate-compress a domain that's too large or arrives online, rather than recognizing a dynamic segment tree is needed instead.

## Complexity Reference

- Count of Smaller Numbers After Self / Reverse Pairs / Count of Range Sum (Fenwick + coordinate compression): O(n log n).
- Range Sum Query - Mutable (static segment tree): O(log n) per update/query, O(n) build.
- My Calendar III (dynamic segment tree): O(log(range)) per booking.
- The Skyline Problem: O(n log n).
- Repeated DNA Sequences (exact bit-packing): O(n), zero collision risk.
- Longest Duplicate Substring (polynomial rolling hash + binary search on length): O(n log n) expected.

## Interview Answer Skeleton

**30-sec:** Fenwick trees, segment trees, and rolling hashes all answer "update part of a collection, then query an aggregate over part of it, repeatedly" — a Fenwick tree is the simplest/cheapest for prefix-sum-only needs, a segment tree generalizes to arbitrary aggregates, and a rolling hash turns re-hashing a sliding window into an O(1)-per-slide update.

**2-min:** Reach for a Fenwick tree specifically for prefix-sum-and-point-update problems over a coordinate-compressible domain — simpler and faster than a full segment tree when arbitrary range aggregates aren't needed. Reach for a dynamic segment tree when the domain is too large or too dynamic to coordinate-compress upfront (My Calendar III's `[0, 1e9]` range with online bookings). Never trust a hash match as proof of substring equality unless the "hash" is a provably exact, lossless encoding — otherwise add an explicit character-by-character confirmation.

**Whiteboard:** Draw a coordinate-compression example: a sparse set of raw values on one line, mapped by sorted rank to a dense `1..m` array on a second line below it, with a Fenwick tree's implicit binary-indexed structure sketched above that array. For the rolling hash, draw a sliding window over a string and show the O(1) update formula (subtract the outgoing character's contribution, multiply, add the incoming character) rather than a full rehash.

**Staff-level framing:** A Fenwick or segment tree's incremental range-aggregate maintenance is the same idea behind a real-time analytics dashboard answering "sum/max/count over this time range" continuously as events arrive, without re-scanning full history. A dynamic segment tree's lazy node creation over an enormous domain is the same technique behind sparse spatial-indexing structures (quadtrees) in mapping/geospatial systems. The rolling-hash collision-guard discipline generalizes to any production system relying on a hash for approximate matching (deduplication, plagiarism detection, content fingerprinting) — trusting an unguarded hash match risks a silently wrong result at scale.

## Production Warning Signs

- **Symptom:** a substring-deduplication feature using a rolling hash occasionally, rarely, incorrectly merges two genuinely different substrings as duplicates.
- **Diagnose:** check whether the implementation trusts a hash match directly as proof of equality, without a follow-up character-by-character comparison. Confirm by constructing or searching for a genuine hash collision for the specific hash function and modulus in use, and checking whether the deduplication logic incorrectly treats them as identical. Fix by adding an explicit equality check after any hash match, before trusting it.

## Related

- syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md
- syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
- syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md
