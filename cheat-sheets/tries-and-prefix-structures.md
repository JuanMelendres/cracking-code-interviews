---
title: "Cheat Sheet: Tries and Prefix Structures"
slug: tries-and-prefix-structures
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2114
canonical: ../syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md
last_updated: 2026-09-06
---

# Tries and Prefix Structures

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md`](../syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md)

## Core Mental Model

A `HashSet<String>` answers "is this exact string present" in O(1) average, but answering "does any stored string start with this prefix" requires scanning every stored string — a trie answers the prefix question in time proportional only to the prefix's own length, regardless of how many strings are stored, because two strings sharing a prefix share the same path through the trie for that prefix's length.

## Essential Definitions

- **Trie node** — typically holds an array/map of children (one slot per possible next character) plus a flag marking whether a complete word ends at this node.
- **Trie + wildcard-aware DFS** — fans a search out across every child when a wildcard is encountered, rather than enumerating all possible characters.
- **Trie + grid backtracking** — builds one shared trie from an entire dictionary, then runs a single multi-word DFS; every search branch prunes itself the instant the path-so-far isn't a prefix of *any* remaining word, shared across all words simultaneously.
- **Binary trie over bits** — generalizes "trie over characters" to "trie over bits," enabling a greedy walk toward the opposite bit at each level (Maximum XOR of Two Numbers).
- **Trie with cached aggregate values per node** — stores an aggregate (e.g., a sum) of every value whose key passes through that node, turning a prefix-sum query into an O(1) lookup (Map Sum Pairs).

## Recognition Signals / When to Use This Pattern

| Signal | Technique |
|---|---|
| Core question involves prefixes (autocomplete, "starts with," shared-prefix search across many strings) | Trie |
| Multiple target strings must be searched for simultaneously across the same structure | One shared trie built from the whole dictionary first |
| "Maximum/minimum XOR pair" | Binary trie over bit representations |
| Need shortest matching root/prefix, not longest | Stop the trie walk at the *first* marked end-of-word node |
| Aggregate value keyed by prefix (e.g., cost rollup by path) | Trie with cached per-node aggregate |

**Complexity:** Design Add and Search Words O(L) per `addWord`, O(26^k · L) worst case for `search` with k wildcards; Word Search II O(rows·cols·4^L) worst case, far faster in practice than repeated single-word searches; Maximum XOR O(32n), effectively O(n); Replace Words O(total sentence length); Map Sum Pairs O(L) per `insert`/`sum`.

## Common Pitfalls

- Using a `HashSet<String>` for a prefix-matching requirement — workable via scanning, but loses the trie's proportional-to-prefix-length efficiency, and becomes the wrong tool entirely for wildcard search.
- Running a single-word search once per dictionary word instead of building one shared trie — correct but wastes redundant re-scans for every word sharing a common prefix.
- Naively adding a new value's full contribution on re-insertion rather than computing and applying only the delta — double-counts the old value's contribution to every ancestor's cached aggregate.

## Interview Answer Skeleton

**30-sec:** A hash set answers exact-match membership in O(1) average but needs O(n · prefix length) to answer a prefix query; a trie answers the same prefix query in O(prefix length), independent of how many strings are stored, because the prefix's existence is checked by walking one shared path.

**2-min:** Word Search II builds one trie from the entire dictionary, then runs a single DFS over the board that prunes a branch for *every* remaining word simultaneously the moment the current trie node has no child for the next character — far cheaper than re-walking the same board prefixes once per word. Maximum XOR of Two Numbers extends the same tree-of-choices idea to bits: insert and query in the same pass (safe because XOR is symmetric), greedily preferring the opposite bit at each level since a differing bit at a higher position always dominates any combination of lower-position differences.

**Whiteboard:** Draw a small trie as a branching tree of characters with end-of-word markers at certain nodes; show two words sharing a prefix path and diverging only where the strings differ.

**Staff-level framing:** Tries underlie real production systems directly — autocomplete/search-suggestion features are trie-shaped by nature, IP routing tables use a binary trie over address bits (structurally identical to the Maximum XOR binary trie, applied to longest-prefix-matching instead), and the cached-aggregate-per-node technique is the same idea behind any hierarchical, prefix-aggregated metric or billing rollup.

## Production Warning Signs

- A Word Search II-style multi-word grid search implementation occasionally reports the same word as found more than once in its result list.
- Diagnose: check whether the trie node's `word` marker is cleared (set to `null`) immediately after a match is recorded — if the board contains a path that loops back to the same end-of-word trie node through a different sequence of grid cells, and the marker isn't cleared after the first match, the same word gets added to the result a second time.

## Related

- syllabus/03-data-structures-algorithms/backtracking-and-pruning.md
- syllabus/03-data-structures-algorithms/bit-manipulation.md
- syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
