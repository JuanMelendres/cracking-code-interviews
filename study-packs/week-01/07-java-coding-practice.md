# Java Coding Practice — Week 1

**7 problems + the LRU errata drill. All code on this page was compiled and executed — see the verification block at the end and `MANIFEST.md` for the exact commands.**

Narrate all six phases from `04-coding-interview-communication.md` on every problem, even solving solo.

---

## Day 1 — LC 1 Two Sum, LC 167 Two Sum II

**Pattern:** hash map for O(n), two pointers when sorted is given for free.

```java
// LC 1 — unsorted input, hash map of value -> index
static int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int need = target - nums[i];
        if (seen.containsKey(need)) return new int[]{seen.get(need), i};
        seen.put(nums[i], i);
    }
    throw new IllegalArgumentException("no solution");
}

// LC 167 — sorted input given, two pointers, O(1) space
static int[] twoSumSorted(int[] numbers, int target) {
    int lo = 0, hi = numbers.length - 1;
    while (lo < hi) {
        int sum = numbers[lo] + numbers[hi];
        if (sum == target) return new int[]{lo + 1, hi + 1}; // 1-indexed per LC spec
        if (sum < target) lo++; else hi--;
    }
    throw new IllegalArgumentException("no solution");
}
```

**Retrospective:** LC 1's hash-map approach is O(n) time, O(n) space. LC 167 is the same problem with one added constraint (sorted) that changes the optimal approach entirely — the constraint check from `04-…` Phase 1 is what surfaces this in an interview instead of defaulting to the hash-map solution out of habit. Complexity: O(n) time, O(1) space, better than porting the hash-map solution over unchanged.

## Day 2 — LC 121 Best Time to Buy and Sell Stock

**Pattern:** single pass, track the running minimum seen so far.

```java
static int maxProfit(int[] prices) {
    int minSoFar = Integer.MAX_VALUE, best = 0;
    for (int p : prices) {
        minSoFar = Math.min(minSoFar, p);
        best = Math.max(best, p - minSoFar);
    }
    return best;
}
```

**Invariant, stated before coding (Phase 2):** at each price, the best possible sale *ending here* is `price - (lowest price seen so far)`. This is why a single backward-looking minimum suffices — no need to consider future prices when evaluating a sale at index `i`. **Complexity:** O(n) time, O(1) space. **Edge case traced (Phase 5):** monotonically decreasing prices → profit stays 0, never negative.

## Day 3 — LC 242 Valid Anagram, LC 49 Group Anagrams

```java
// LC 242 — fixed 26-slot counter, no allocation per character
static boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    int[] counts = new int[26];
    for (int i = 0; i < s.length(); i++) {
        counts[s.charAt(i) - 'a']++;
        counts[t.charAt(i) - 'a']--;
    }
    for (int c : counts) if (c != 0) return false;
    return true;
}

// LC 49 — sorted-string key groups words sharing a letter multiset
static List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> groups = new HashMap<>();
    for (String s : strs) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        groups.computeIfAbsent(new String(chars), k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(groups.values());
}
```

**Retrospective:** LC 242's counting-array approach is O(n) time, O(1) space (26 is a constant), and avoids the allocation cost of sorting both strings. LC 49's sorted-key approach is O(n · k log k) where k is average string length — the alternative (a 26-length count-array as the map key, avoiding the sort) is O(n · k) and worth naming as a follow-up variation.

## Day 4 — LC 3 Longest Substring Without Repeating Characters

```java
static int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastSeen = new HashMap<>();
    int start = 0, best = 0;
    for (int end = 0; end < s.length(); end++) {
        char c = s.charAt(end);
        if (lastSeen.containsKey(c) && lastSeen.get(c) >= start) {
            start = lastSeen.get(c) + 1;
        }
        lastSeen.put(c, end);
        best = Math.max(best, end - start + 1);
    }
    return best;
}
```

**Invariant (Phase 2):** the window `[start, end]` always holds a substring with no repeated character; when a repeat is found, `start` jumps *past* its last occurrence, never backward. **Complexity:** O(n) time (each character visited by `end` once, `start` moves forward monotonically), O(min(n, charset size)) space for the map.

## Day 5–6 — LC 146 LRU Cache + the errata drill

This is the most-asked design problem in the register, and the version in the source Notion guide was verified defective by the Phase 1 audit (module 23: `put()` unlinks an existing key from the list but never removes it from the map before the capacity check, so updating an existing key at capacity evicts an unrelated valid entry).

**Step 1 — write the buggy version and prove the failure:**

```java
void put(int key, int value) {
    if (map.containsKey(key)) {
        // BUG: unlinks from the list but never removes the stale
        // entry from the map before the capacity check below.
        unlink(map.get(key));
    }
    if (map.size() == capacity) {
        Node lru = tail.prev;
        unlink(lru);
        map.remove(lru.key);
    }
    Node fresh = new Node(key, value);
    map.put(key, fresh);
    insertFront(fresh);
}
```

**Failing sequence** (capacity 2): `put(1,1); put(2,2); put(1,10);` — updating key 1, which already exists, while the cache is at capacity. The stale map entry for key 1 is still counted, so `map.size() == capacity` reads true even though no new distinct key is being added — the eviction path fires and removes the current tail, which is key 2, a different and still-valid entry. `get(2)` then returns **-1** instead of the correct **2**.

**Step 2 — the fix:** remove the stale map entry at the same time the node is unlinked, before the capacity check runs:

```java
void put(int key, int value) {
    if (map.containsKey(key)) {
        Node existing = map.get(key);
        unlink(existing);
        map.remove(key); // <- the missing line in the buggy version
    } else if (map.size() == capacity) {
        Node lru = tail.prev;
        unlink(lru);
        map.remove(lru.key);
    }
    Node fresh = new Node(key, value);
    map.put(key, fresh);
    insertFront(fresh);
}
```

Full source: `LRUCacheBuggy.java` and `LRUCacheFixed.java` in this pack's verification build (paths in `MANIFEST.md`).

**Why this matters beyond this one bug:** the failure only shows up when updating an *existing* key while the cache is *already full* — a sequence easy to omit from manual testing and exactly the kind of case an interviewer's follow-up will probe for after seeing a first, simpler passing example.

## Verification — real, not asserted

```
== LC 146 LRU Cache — fixed implementation ==
  PASS  fixed: get(1) after put(1,1) put(2,2)
  PASS  fixed: get(2) evicted by capacity, correct eviction target
  PASS  fixed: get(3) present

== Errata drill — reproducing the buggy version's failure ==
  PASS  buggy: get(2) incorrectly evicted (this IS the bug, reproduced on purpose)

== Fixed version does not reproduce the failure on the same sequence ==
  PASS  fixed: get(2) survives the update-at-capacity sequence
  PASS  fixed: get(1) reflects the update

Week 1 suite: 18/18 assertions passed
```

Full output (all 7 problems, 18 assertions) is in `MANIFEST.md`. Compiled and run with `javac` / `java` on OpenJDK 21.0.12, this session — no JUnit/build-tool dependency was available in this environment, so verification uses a small hand-rolled assertion harness (`Check.java`) rather than `@Test` annotations; converting to real JUnit 5 is in scope for `practice/java/` later, not blocking for this pack.

## Exit check

- [ ] All 7 problems solved with a written retrospective (pattern, invariant, complexity) — not just a passing test
- [ ] LRU written correctly from scratch twice
- [ ] Can state the exact LRU bug (which line is missing, and why the failure requires *both* "existing key" and "at capacity") without looking it up
