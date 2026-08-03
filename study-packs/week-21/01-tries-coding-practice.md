---
title: "Coding Practice — Tries (T-1415)"
week: 21
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Tries (T-1415)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 1/6 to 6/6 — full register closure. Previously only LC 208 (Implement Trie) existed.

---

## Problem 1 — LC 211 Design Add and Search Words Data Structure

**Pattern:** standard trie insert, plus recursive DFS search that branches on every child when the query hits a `.` wildcard.

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

**Retrospective:** the `.` wildcard is exactly why a trie beats a `HashSet<String>` here — a set would need to enumerate all 26 possibilities per wildcard position and check each string, while the trie just fans out the DFS across whichever children actually exist at that node. **Complexity:** O(L) per `addWord`; `search` is O(26^k · L) worst case where k is the wildcard count, but real dictionaries make this fast since most nodes have far fewer than 26 children.

## Problem 2 — LC 212 Word Search II

**Pattern:** trie + backtracking DFS on a grid — build one trie from all target words, then run a single multi-word DFS instead of calling LC 79's single-word search once per word.

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

private static void dfs212(char[][] board, int r, int c, TrieNode212 node, List<String> result) {
    if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] == '#') return;
    char ch = board[r][c];
    TrieNode212 next = node.children[ch - 'a'];
    if (next == null) return;
    if (next.word != null) { result.add(next.word); next.word = null; }
    board[r][c] = '#';
    dfs212(board, r + 1, c, next, result);
    dfs212(board, r - 1, c, next, result);
    dfs212(board, r, c + 1, next, result);
    dfs212(board, r, c - 1, next, result);
    board[r][c] = ch;
}
```

**Retrospective:** the trie lets every DFS branch prune itself the moment the path-so-far isn't a prefix of *any* remaining word, shared across all words simultaneously — running LC 79's `exist()` once per word would re-walk the same board prefixes redundantly for every word that shares a prefix. Setting `next.word = null` after a match prevents the same word being added twice if the board contains a loop back to the same end node. **Complexity:** O(rows·cols·4^L) worst case, but the shared-trie pruning makes it far faster in practice than the naive per-word repeat of LC 79.

## Problem 3 — LC 421 Maximum XOR of Two Numbers in an Array

**Pattern:** binary trie over 32-bit representations — greedily walk toward the opposite bit at each level to maximize XOR.

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

**Retrospective:** inserting and querying against the trie in the *same* pass over the array (rather than building the full trie first, then querying) works because XOR is symmetric — by the time num[i] queries the trie, every number that could pair with it to form a max-XOR pair is already inserted, and greedily preferring the opposite bit at each level is always locally optimal because a differing high bit contributes more to the XOR value than any combination of lower bits ever could. **Complexity:** O(32n) time — effectively O(n).

## Problem 4 — LC 648 Replace Words

**Pattern:** trie prefix-matching — walk each sentence word down the trie, stopping at the first marked root.

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
    StringBuilder result = new StringBuilder();
    for (String word : sentence.split(" ")) {
        if (result.length() > 0) result.append(" ");
        result.append(shortestRoot(root, word));
    }
    return result.toString();
}
```

**Retrospective:** stopping the walk at the *first* `end` node encountered (rather than the longest possible match) is what correctly implements "shortest root" — if `"cat"` and `"catalog"` are both roots, `"cattle"` must become `"cat"`, not fail to match or match the longer root. **Complexity:** O(total sentence length) — each character visited once.

## Problem 5 — LC 677 Map Sum Pairs

**Pattern:** trie where every node caches the sum of all inserted values passing through it, updated incrementally on overwrite.

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

    int sum(String prefix) {
        Node cur = root;
        for (char c : prefix.toCharArray()) {
            int i = c - 'a';
            if (cur.children[i] == null) return 0;
            cur = cur.children[i];
        }
        return cur.score;
    }
}
```

**Retrospective:** the tricky part of this problem is re-inserting the *same* key with a new value — a naive implementation would double-count the old value's contribution to every ancestor node's score. Tracking each key's last-inserted value in a side `HashMap` and applying only the *delta* on re-insert keeps every prefix's cached sum correct without re-walking or recomputing the whole trie. **Complexity:** O(L) per `insert` and O(L) per `sum`, both far better than the naive "store all pairs, filter by prefix on every query" approach which is O(n·L) per query.

## Verification

```
$ cd practice/java/week-21/tries/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
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
