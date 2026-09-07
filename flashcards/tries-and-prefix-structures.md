---
title: "Flashcards: Tries and Prefix Structures"
slug: tries-and-prefix-structures
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: "T-2114"
canonical: ../syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md
last_updated: 2026-09-07
---

# Flashcards: Tries and Prefix Structures

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md`](../syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md)

## Card: Trie vs. HashSet for prefix queries

**Prompt:**
Why does a trie answer "does any stored string start with this prefix" faster than a `HashSet<String>`?

**Answer:**
A `HashSet` answers "is this exact string present" in O(1) average, but answering a prefix query requires scanning every stored string — O(n · prefix length) worst case. A trie answers the same prefix query in O(prefix length) time, completely independent of how many strings are stored, because the prefix's existence is checked by walking a single shared path through the tree.

**Why it matters:**
The foundational trie-vs-hash-set distinction — a trie's advantage grows precisely as the stored collection grows large.

**Common trap:**
Using a `HashSet<String>` for a prefix-matching requirement, which loses this proportional-to-prefix-length efficiency.

**Related:**
[syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md](../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md)

## Card: Why Word Search II nulls out a matched word's marker

**Prompt:**
In Word Search II (LC 212), why is a matched word's trie-node marker set to `null` immediately after recording it?

**Answer:**
If the board contains a path that loops back to the same end-of-word trie node through a different sequence of grid cells, and the marker isn't cleared after the first match, the same word gets added to the result a second time.

**Why it matters:**
Building one shared trie from the entire dictionary lets a single DFS pass prune a branch for every word simultaneously the moment the path-so-far isn't a prefix of any remaining word.

**Common trap:**
Leaving the word marker set after a match, producing duplicate entries in the result list.

**Related:**
[syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md](../syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md)

## Card: Maximum XOR's single-pass insert-and-query

**Prompt:**
In Maximum XOR of Two Numbers (LC 421), why does inserting and querying the trie in the same single pass over the array — rather than building the whole trie first — still produce the correct answer?

**Answer:**
XOR is symmetric — by the time any given number queries the trie, every number that could pair with it to form the maximum XOR has already been inserted. At each bit level, greedily preferring the opposite bit is always locally optimal, because a differing bit at a higher (more significant) position contributes more to the final XOR value than any combination of lower-position differences ever could.

**Why it matters:**
A genuinely non-obvious trie application — a binary trie over bits generalizes the string-prefix trie idea to bit-level greedy optimization.

**Common trap:**
Attempting to solve max-XOR with sorting and two pointers, which doesn't apply since XOR maximization isn't monotonic with numeric value.

**Related:**
[syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md](../syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md)

## Card: Replace Words' first-match-wins rule

**Prompt:**
In Replace Words (LC 648), why does the trie walk stop at the first marked end-of-word node encountered, rather than continuing to find the longest match?

**Answer:**
The problem requires replacing each word with the shortest known root, not the longest — stopping at the first end-of-word node correctly implements "shortest root" rather than searching for a longer one.

**Why it matters:**
Overlapping root words, where a shorter root is itself a prefix of a longer one, must resolve to the shorter root under this rule.

**Common trap:**
Continuing the trie walk past the first end-of-word marker in search of a longer match.

**Related:**
[syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md](../syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md)

## Card: Map Sum Pairs' delta-based re-insertion

**Prompt:**
In Map Sum Pairs (LC 677), why does re-inserting the same key with a new value require applying only the delta (`newValue - oldValue`) to every ancestor node's cached sum, instead of adding the new value directly?

**Answer:**
Naively adding the new value's full contribution to every ancestor's cached sum would double-count the old value's contribution, which is still present from the original insertion. Tracking each key's most recently inserted value in a side hash map and applying only the delta keeps every node's cached sum correct without re-walking and recomputing the entire trie from scratch.

**Why it matters:**
The same cached-aggregate-per-node technique generalizes to any hierarchical, prefix-aggregated metric or billing rollup.

**Common trap:**
Naively adding a new value's full contribution on re-insertion rather than computing the delta.

**Related:**
[syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md](../syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md)
