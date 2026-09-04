---
title: "Tries and Prefix Structures"
slug: tries-and-prefix-structures
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2114
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - backtracking-and-pruning.md
related:
  - backtracking-and-pruning.md
  - bit-manipulation.md
  - hashing-patterns-and-frequency-maps.md
practice: ../../practice/java/week-21/tries/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-21/01-tries-coding-practice.md
---

# Tries and Prefix Structures

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-21/01-tries-coding-practice.md` — real, compiled, executed code (`practice/java/week-21/tries/`), re-verified on OpenJDK 21.0.12 while writing this chapter (14/14 assertions passing). This batch brought the register's Tries pattern to full closure (6/6).

This is Master Topic Register **T-1415** (IWI 4.7, moderate frequency, fully closed). A trie is a tree where each path from the root spells out a prefix — the natural structure whenever a problem's core operation is "does any stored string start with this prefix," a question a `HashSet<String>` cannot answer efficiently.

## 1. Why This Matters

A `HashSet<String>` answers "is this exact string present" in O(1) average, but answering "does any stored string start with this prefix" requires scanning every stored string — a trie answers the *prefix* question in time proportional only to the prefix's own length, regardless of how many strings are stored. Recognizing prefix-based questions (autocomplete, wildcard search, word-in-grid search sharing common prefixes) as a trie's specific niche, distinct from `HashMap`'s exact-match niche, is this pattern's core interview skill.

## 2. Prerequisites

[Backtracking and Pruning](backtracking-and-pruning.md) — several problems in this chapter (Section 4) combine a trie with backtracking DFS, using the trie to prune search branches that can never lead to a valid word.

## 3. Foundation (L1)

**A trie is a tree where each node represents one character, and a path from the root to a marked node spells out one complete stored string.** Two strings sharing a common prefix share the same path through the trie for that prefix's length, only diverging where the strings themselves diverge — this shared-path structure is exactly what makes prefix queries fast.

**Each trie node typically holds an array (or map) of children — one slot per possible next character — plus a flag marking whether a complete word ends at this exact node.** Inserting a string walks or creates one node per character; searching for an exact match or a prefix walks the same path, checking either the end-of-word flag (exact match) or simply that the path exists at all (prefix match).

## 4. Core Concepts (L2)

**Trie plus wildcard-aware DFS** (Design Add and Search Words, Section 7 Problem 1) fans a search out across every child when a `.` wildcard is encountered, rather than needing to enumerate all possible characters and check each resulting string against a set — the trie only ever explores children that actually exist.

**Trie plus grid backtracking** (Word Search II, Section 7 Problem 2) builds one shared trie from an entire dictionary of target words, then runs a single multi-word DFS across the grid — every search branch prunes itself the instant the path-so-far isn't a prefix of *any* remaining word, shared across all words simultaneously, a direct combination with [Backtracking's](backtracking-and-pruning.md#4-core-concepts-l2) grid-DFS-with-in-place-marking technique.

**A binary trie over fixed-width bit representations** (Maximum XOR of Two Numbers, Section 7 Problem 3) generalizes the "trie over characters" idea to "trie over bits," enabling a greedy walk toward the opposite bit at each level to construct the maximum possible XOR — a genuinely different application domain from string prefix matching, unified by the same underlying tree-of-choices structure.

**Trie plus shortest-prefix-match** (Replace Words, Section 7 Problem 4) stops a trie walk at the *first* marked end-of-word node encountered, rather than the longest possible match — correctly implementing "replace with the shortest known root" rather than the longest.

**Trie with cached aggregate values per node** (Map Sum Pairs, Section 7 Problem 5) stores not just "is this a complete word" at each node, but an aggregate (a sum) of every value whose key passes through that node — turning a prefix-sum query into an O(1) lookup at the corresponding trie node, rather than a scan over every matching key.

## 5. How It Works Internally (L3)

**Word Search II's shared-pruning efficiency, precisely**: running Word Search's single-word `exist()` once per dictionary word would re-walk the same board prefixes redundantly for every word sharing that prefix. Building one trie from the entire dictionary first means a single DFS pass over the board can check, at every step, whether the path-so-far is a prefix of *any* remaining word — the moment it isn't (the current trie node has no child for the next character), that entire branch is abandoned for *every* word simultaneously, not just one. Setting a matched word's trie-node marker to `null` after recording it (rather than leaving it set) prevents the same word being added twice if the board contains a path back to the same end node through a different route.

**Maximum XOR's greedy-bit-walk correctness, precisely**: inserting and querying against the trie in the *same* single pass over the array (rather than building the complete trie first, then querying separately) works because XOR is symmetric — by the time any given number queries the trie, every number that could pair with it to form the maximum XOR has already been inserted. At each bit level, greedily preferring the *opposite* bit (if a child exists in that direction) is always locally optimal, because a differing bit at a higher (more significant) position contributes more to the final XOR value than any possible combination of differences at lower positions ever could — the same "higher position dominates" reasoning [Number Representation's](../01-computer-science-foundations/number-representation.md) bit-pattern discussion establishes generally, applied here to maximizing rather than merely representing a value.

**Map Sum Pairs' delta-based re-insertion, precisely**: re-inserting the same key with a new value is the genuinely tricky part of this problem — naively adding the new value's full contribution to every ancestor node's cached sum would double-count the old value's contribution, which is still present from the original insertion. Tracking each key's most recently inserted value in a side hash map and applying only the *delta* (`newValue - oldValue`) to every ancestor node on re-insertion keeps every node's cached sum correct without needing to re-walk and recompute the entire trie from scratch.

## 6. Practical Usage

- **Reach for a trie the moment a problem's core question involves prefixes** (autocomplete, "starts with," shared-prefix search across many strings) rather than exact-match lookup, which a `HashSet`/`HashMap` already handles well.
- **Build one shared trie from an entire dictionary before running a per-position search** (Word Search II) whenever multiple target strings need to be searched for simultaneously across the same underlying structure — sharing the trie is what enables sharing the pruning.
- **Recognize "maximum/minimum XOR pair" problems as a binary-trie application**, a genuinely non-obvious trie use case worth having as a specific, memorable pattern-recognition trigger.

## 7. Examples

**Problem 1 — LC 211, Design Add and Search Words Data Structure.**

```java
static class WordDictionary {
    static class Node {
        Node[] children = new Node[26];
        boolean end;
    }
    private final Node root = new Node();

    void addWord(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (cur.children[i] == null) cur.children[i] = new Node();
            cur = cur.children[i];
        }
        cur.end = true;
    }

    boolean search(String word) {
        return dfs(word, 0, root);
    }

    private boolean dfs(String word, int idx, Node node) {
        if (node == null) return false;
        if (idx == word.length()) return node.end;
        char c = word.charAt(idx);
        if (c == '.') {
            for (Node child : node.children) {
                if (dfs(word, idx + 1, child)) return true;
            }
            return false;
        }
        return dfs(word, idx + 1, node.children[c - 'a']);
    }
}
```

**Retrospective:** the `.` wildcard is exactly why a trie beats a `HashSet<String>` here — a set would need to enumerate all 26 possibilities per wildcard position. **Complexity:** O(L) per `addWord`; `search` O(26^k · L) worst case, k = wildcard count.

**Problem 2 — LC 212, Word Search II.**

```java
static List<String> findWords(char[][] board, String[] words) {
    TrieNode212 root = new TrieNode212();
    for (String w : words) {
        TrieNode212 cur = root;
        for (char c : w.toCharArray()) {
            int i = c - 'a';
            if (cur.children[i] == null) cur.children[i] = new TrieNode212();
            cur = cur.children[i];
        }
        cur.word = w;
    }
    List<String> result = new ArrayList<>();
    for (int r = 0; r < board.length; r++)
        for (int c = 0; c < board[0].length; c++)
            dfs212(board, r, c, root, result);
    return result;
}
```

**Retrospective:** see Section 5's shared-pruning argument. **Complexity:** O(rows·cols·4^L) worst case, far faster in practice than repeated single-word searches.

**Problem 3 — LC 421, Maximum XOR of Two Numbers in an Array.**

```java
static int findMaximumXOR(int[] nums) {
    BitTrieNode root = new BitTrieNode();
    int maxXor = 0;
    for (int num : nums) {
        BitTrieNode insertCur = root, queryCur = root;
        int currentXor = 0;
        for (int bit = 31; bit >= 0; bit--) {
            int b = (num >> bit) & 1;
            if (insertCur.children[b] == null) insertCur.children[b] = new BitTrieNode();
            insertCur = insertCur.children[b];
            int wanted = 1 - b;
            if (queryCur.children[wanted] != null) { currentXor |= (1 << bit); queryCur = queryCur.children[wanted]; }
            else if (queryCur.children[b] != null) queryCur = queryCur.children[b];
        }
        maxXor = Math.max(maxXor, currentXor);
    }
    return maxXor;
}
```

**Retrospective:** see Section 5's greedy-bit-walk argument. **Complexity:** O(32n) — effectively O(n).

**Problem 4 — LC 648, Replace Words.**

```java
static String replaceWords(List<String> dictionary, String sentence) {
    TrieNode648 root = new TrieNode648();
    for (String r : dictionary) {
        TrieNode648 cur = root;
        for (char c : r.toCharArray()) {
            int i = c - 'a';
            if (cur.children[i] == null) cur.children[i] = new TrieNode648();
            cur = cur.children[i];
        }
        cur.end = true;
    }
    // ... walk each sentence word, stopping at the first end node
}
```

**Retrospective:** stopping at the *first* `end` node implements "shortest root" correctly. **Complexity:** O(total sentence length).

**Problem 5 — LC 677, Map Sum Pairs.**

```java
static class MapSum {
    static class Node { Node[] children = new Node[26]; int score; }
    private final Node root = new Node();
    private final Map<String, Integer> stored = new HashMap<>();

    void insert(String key, int val) {
        int delta = val - stored.getOrDefault(key, 0);
        stored.put(key, val);
        Node cur = root;
        cur.score += delta;
        for (char c : key.toCharArray()) {
            int i = c - 'a';
            if (cur.children[i] == null) cur.children[i] = new Node();
            cur = cur.children[i];
            cur.score += delta;
        }
    }
}
```

**Retrospective:** see Section 5's delta-based re-insertion argument. **Complexity:** O(L) per `insert` and `sum`.

## 8. Common Mistakes

- **Using a `HashSet<String>` for a prefix-matching requirement** — functionally workable via scanning, but loses the trie's proportional-to-prefix-length efficiency, and becomes the wrong tool entirely for a wildcard search (Section 7, Problem 1).
- **Running a single-word search once per dictionary word** instead of building one shared trie — correct but wastes redundant board re-scans for every word sharing a common prefix (Section 5).
- **Naively adding a new value's full contribution on re-insertion** rather than computing and applying only the delta (Map Sum Pairs, Section 5) — double-counts the old value's contribution to every ancestor's cached aggregate.

## 9. Edge Cases

- **A word not previously added, queried exactly** (Design Add and Search Words' verified `search("pad")` case, correctly returning `false`).
- **Overlapping root words in Replace Words**, where a shorter root is itself a prefix of a longer one — the first-match-wins rule (Section 4/5) must correctly choose the shorter root.
- **Re-inserting the exact same key with a different value** (Map Sum Pairs' verified overwrite case, `apple` re-inserted from `3` to `4`, correctly producing a combined sum of `6`, not `9` or another double-counted value).

## 10. Performance Implications

Real, executed verification from `practice/java/week-21/tries/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  LC211 search(pad) not added -> false
  PASS  LC211 search(bad) exact match -> true
  PASS  LC211 search(.ad) wildcard -> true
  PASS  LC211 search(b..) wildcard -> true
  PASS  LC211 search(b.a) no match -> false
  PASS  LC212 findWords count = 2
  PASS  LC212 findWords -> {oath, eat}
  PASS  LC421 findMaximumXOR([3,10,5,25,2,8]) = 28
  PASS  LC421 findMaximumXOR(12 nums) = 127
  PASS  LC648 replaceWords standard case
  PASS  LC648 replaceWords single-letter roots
  PASS  LC677 sum(ap) after insert(apple,3) = 3
  PASS  LC677 sum(ap) after insert(app,2) = 5
  PASS  LC677 sum(ap) after overwriting apple to 4 = 6 (2+4)
Week 21 — Tries (LC 211, 212, 421, 648, 677): 14/14 assertions passed
```

Every operation here runs proportional to a string's own length (or, for the binary trie, a fixed 32 bits) rather than to the total number of stored strings — the practical performance implication is that a trie's advantage grows precisely as the stored collection grows large, since a `HashSet`-based prefix scan's cost grows with collection size while a trie's prefix-query cost does not.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Trie | O(prefix length) queries regardless of collection size; natural fit for prefix/wildcard search | More memory overhead per stored string than a hash set (one node per character, potentially unshared) |
| `HashSet<String>` | Simple, O(1) average exact-match lookup, less memory overhead for exact-match-only use cases | No efficient prefix query — must scan every entry |
| Binary trie (bit-level) | Enables greedy bit-level optimization (max/min XOR) | Fixed 32 (or 64) levels regardless of value magnitude — no savings for small values |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is recognizing tries' specific, narrower niche relative to hash-based structures — prefix and wildcard matching — rather than treating them as an interchangeable, generally "fancier" string container. A candidate who reaches for a trie on a plain exact-match lookup problem, or a hash set on a genuine prefix-matching problem, reveals the same underlying gap: not distinguishing which structure's actual guarantees the problem needs.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, tries underlie real production systems directly: autocomplete and search-suggestion features are trie-shaped by nature (Design Add and Search Words' wildcard technique generalizes directly to fuzzy-prefix suggestion), IP routing tables use a binary trie over address bits (structurally identical to Maximum XOR's binary trie, applied to longest-prefix-matching instead of XOR maximization), and Map Sum Pairs' cached-aggregate-per-node technique is the same underlying idea behind any hierarchical, prefix-aggregated metric or billing rollup (e.g., "total cost for everything under this account/organization path prefix").

## 14. Production Scenarios

No existing `production-cookbook/` entry has a trie-specific algorithmic root cause.

> Planned reference: a future `production-cookbook/` entry covering a real autocomplete or prefix-search feature's design (e.g., a search-suggestion service that initially used linear string scanning and needed to migrate to a trie-based structure at scale) would be a natural, non-duplicative addition connecting this chapter's Section 13 transfer to a genuine production system.

## 15. Interview Questions

### Question 1 — Why would you use a trie instead of a hash set to store a dictionary of words, if you need to support "does any word start with this prefix" queries?

**Why interviewers ask it.** It's the foundational trie-vs-hash-set distinction, checking whether a candidate understands the specific query shape (prefix, not exact match) that motivates choosing a trie at all.

**Expected answer.** A hash set answers "is this exact string present" in O(1) average, but answering "does any stored word start with this prefix" requires scanning every stored word and checking each one's prefix — O(n · prefix length) in the worst case. A trie answers the same prefix query in O(prefix length) time, completely independent of how many words are stored, because the prefix's existence is checked by walking a single path through the tree, shared by every word starting with that prefix.

**Minimum acceptable answer.** States that a trie is "better for prefixes" even without precisely quantifying the complexity difference.

**Strong Senior answer.** States both complexities precisely and explains the shared-path mechanism that makes the trie's cost independent of collection size.

**Staff-level extension.** Connects this to a real system (Section 13) — an autocomplete feature backing a search box, where query latency must stay flat regardless of how large the underlying dictionary grows.

**Common mistakes.** Describing a trie as generally "faster" without being able to state specifically which operation (prefix query) it's faster for, versus exact-match lookup where a hash set is equally fast or faster with less overhead.

**Follow-up questions.** "What if you also needed to support wildcard queries (a `.` matching any single character)?" (Design Add and Search Words, Section 7, Problem 1 — the trie naturally extends to this via a DFS that fans out across every child at a wildcard position, something a hash set has no efficient equivalent for.)

### Question 2 — How would you find the maximum XOR of any two numbers in an array, faster than checking every pair?

**Why interviewers ask it.** It's a check for whether "trie" is understood as a general tree-of-choices structure applicable beyond strings, or narrowly associated only with character-based prefix matching.

**Expected answer.** Build a binary trie where each number is inserted bit by bit (most significant bit first), one level per bit. For each number, walk the trie again greedily preferring the *opposite* bit at each level (if a child exists in that direction) — this greedy choice is always locally optimal because a differing bit at a higher position contributes more to the final XOR value than any combination of lower-position differences could. Doing the insert and the query for each number in the same pass (since XOR is symmetric) achieves this in O(32n), effectively O(n).

**Minimum acceptable answer.** Recognizes this is faster than the O(n²) brute-force pairwise check, even without producing the full binary-trie solution unprompted.

**Strong Senior answer.** Produces the binary-trie solution and explains why greedily preferring the opposite bit at each level is provably optimal (higher bit positions dominate the final value).

**Staff-level extension.** Connects binary tries to a real system application (Section 13) — IP routing's longest-prefix-match, a structurally similar binary-trie technique applied to a different optimization goal.

**Common mistakes.** Attempting to solve this by sorting and using some form of two-pointer technique, which doesn't actually apply here since XOR maximization isn't monotonic with respect to numeric value the way sum or difference problems are.

**Follow-up questions.** "Why does inserting and querying in the same single pass work correctly, rather than needing to build the whole trie first?" (Section 5 — XOR's symmetry guarantees that by the time any number queries, every number that could pair with it for the maximum XOR is already inserted.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-21/tries/) yourself and confirm the same 14/14 assertions pass.
- Implement LC 208 (Implement Trie, the basic insert/search/startsWith operations) from scratch, as the foundational exercise every other problem in this chapter builds on.
- Extend Replace Words (Section 7, Problem 4) to instead implement "longest root" matching rather than "shortest root," and verify your change against a constructed test case where the two behaviors genuinely differ.

## 17. Debugging Exercises

**Symptom:** a Word Search II-style multi-word grid search implementation occasionally reports the same word as found more than once in its result list.

**Diagnose:** check whether the trie node's `word` marker is cleared (set to `null`) immediately after a match is recorded — Section 5 names this exact bug: if the board contains a path that loops back to the same end-of-word trie node through a different sequence of grid cells, and the marker isn't cleared after the first match, the same word gets added to the result a second time. Confirm by constructing a small grid with two distinct paths spelling the same target word and checking whether the result list contains a duplicate entry.

## 18. Design Exercises

**Design constraint:** design an autocomplete feature for a search box that must, given a partial query string, return the top 5 most popular complete queries sharing that prefix, from a dictionary of millions of distinct historical queries.

Design this using a trie augmented with a cached top-k list at each node (a direct extension of Map Sum Pairs' cached-aggregate-per-node technique, Section 4/5, generalized from "cached sum" to "cached top-k by popularity"): each trie node stores the top 5 most popular complete queries passing through it, updated incrementally as query popularity changes, so that answering an autocomplete request for a given prefix is a single O(prefix length) trie walk followed by an O(1) read of the destination node's cached top-5 list — rather than scanning every query sharing that prefix on every single autocomplete request. State the real trade-off: maintaining each node's cached top-5 list incrementally as popularity counts change is real, ongoing bookkeeping cost, traded against making every read-time autocomplete request fast regardless of how many queries share the requested prefix.

## 19. Further Reading

- [Backtracking and Pruning](backtracking-and-pruning.md) — the grid-DFS-with-in-place-marking technique Word Search II (Section 4/5) combines with trie-based pruning.
- [Bit Manipulation](bit-manipulation.md) — the bit-level vocabulary the binary trie (Maximum XOR, Section 4/5) operates on directly.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what a trie is and why it answers prefix queries faster than scanning a set of strings | [Section 3](#3-foundation-l1) |
| L2 | Recognize when a problem's prefix, wildcard, or shared-search-across-many-strings shape signals a trie is the right structure | [Interview Question 1](#question-1--why-would-you-use-a-trie-instead-of-a-hash-set-to-store-a-dictionary-of-words-if-you-need-to-support-does-any-word-start-with-this-prefix-queries) |
| L3 | Derive the shared-pruning efficiency argument for multi-word grid search, and the greedy-bit-walk correctness argument for a binary trie | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real duplicate-result bug in a trie-based multi-word search (Section 17), and design a real autocomplete system using a cached-aggregate trie deliberately (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
