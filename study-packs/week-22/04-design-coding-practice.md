---
title: "Coding Practice — Design-Style Problems (T-1416)"
week: 22
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Design-Style Problems (T-1416)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 4/10 to 9/10. Previous coverage (LC 146 LRU Cache, LC 380 Insert Delete GetRandom O(1), LC 706 Design HashMap, LC 622 Design Circular Queue, across Weeks 1 and 5) established the fixed-capacity-cache and array-backed-structure basics. A pre-work audit confirmed LC 155 (Min Stack) and LC 232 (Queue via Stacks) are also design-style problems but are already solved and correctly tagged as Stacks (T-1406) elsewhere — not re-added here. LC 211 (a trie-based design problem) is similarly already tagged Tries (T-1415). This batch adds the harder LFU-cache sequel, a time-indexed store, a social-feed design, a history/navigation structure, and a rate-limiter.

**Overlap note:** "rate limiter" and "bounded queue" designs could plausibly fit either this pattern or Concurrency Coding (T-1417). LC 359 (Logger Rate Limiter, this file) is LeetCode-tagged plain "Design" with no concurrency requirement — a single-threaded frequency check — so it belongs here. LC 1188 (Design Bounded Blocking Queue, in `03-concurrency-coding-practice.md`) is LeetCode-tagged "Concurrency" specifically because it requires real thread coordination (`wait`/`notify`) under a capacity constraint — that one belongs there instead. Each is counted toward exactly one pattern.

---

## Problem 1 — LC 460 LFU Cache

**Pattern:** O(1) get/put via a `key -> Node` map plus a `frequency -> LinkedHashSet<Node>` map, with a tracked `minFreq` pointer — the sequel to LC 146's LRU Cache, adding a second eviction dimension (frequency, with recency as the tiebreaker).

```java
int get(int key) {
    Node node = keyToNode.get(key);
    if (node == null) return -1;
    touch(node);
    return node.value;
}

private void touch(Node node) {
    int oldFreq = node.freq;
    freqToNodes.get(oldFreq).remove(node);
    if (freqToNodes.get(oldFreq).isEmpty()) {
        freqToNodes.remove(oldFreq);
        if (minFreq == oldFreq) minFreq++;
    }
    node.freq++;
    freqToNodes.computeIfAbsent(node.freq, f -> new LinkedHashSet<>()).add(node);
}
```

**Retrospective:** the two-level structure exists because LFU has two competing tiebreakers — evict the *least frequently used* entry, and among entries tied on frequency, evict the *least recently used* one — and a `LinkedHashSet` per frequency bucket gets the LRU-within-a-frequency ordering for free from its insertion order, exactly like LC 146's single `LinkedHashMap` gets pure LRU ordering for free. The `minFreq` pointer is what keeps eviction O(1): without it, finding the globally-least-frequent bucket would require scanning all frequency buckets on every eviction. `minFreq` only ever needs incrementing (never decrementing) except when a brand-new key is inserted, since access can only *increase* an entry's frequency, never decrease it. **Complexity:** O(1) for both `get` and `put`, matching the LeetCode-required bound — the entire point of this problem over a naive "sort by frequency" approach, which would be O(log n) or worse per operation.

## Problem 2 — LC 981 Time Based Key-Value Store

**Pattern:** append-only, per-key timestamp-ordered list plus binary search for the "floor" (largest timestamp ≤ query) — the general applied pattern behind "as-of" or point-in-time queries in real systems.

```java
String get(String key, int timestamp) {
    List<long[]> timestamps = store.get(key);
    if (timestamps == null) return "";
    int lo = 0, hi = timestamps.size() - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (timestamps.get(mid)[0] <= timestamp) { result = mid; lo = mid + 1; }
        else hi = mid - 1;
    }
    return result == -1 ? "" : values.get(key).get(result);
}
```

**Retrospective:** `set()` calls are guaranteed strictly increasing in timestamp per LeetCode's own problem constraints, which is exactly what makes each key's timestamp list already sorted without any extra sorting step — the binary search here is the same "find the boundary/floor" template as this week's LC 34 (Find First and Last Position), just searching for "largest value ≤ target" instead of "first/last exact match." This pattern generalizes directly to real systems: an event-sourced audit log, a feature-flag rollout history, or a price-at-time-T lookup are all the same shape as this problem. **Complexity:** O(log k) per `get` (k = number of values stored for that key), O(1) amortized per `set`.

## Problem 3 — LC 355 Design Twitter

**Pattern:** a per-user max-heap merge over each followee's most-recent 10 tweets — bounding the per-user contribution to the heap is what keeps this fast even for users who follow thousands of accounts.

```java
List<Integer> getNewsFeed(int userId) {
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]);
    Set<Integer> sources = new HashSet<>(follows.getOrDefault(userId, Set.of()));
    sources.add(userId);
    for (int source : sources) {
        List<int[]> tweets = userTweets.get(source);
        if (tweets == null) continue;
        for (int i = tweets.size() - 1; i >= Math.max(0, tweets.size() - 10); i--) {
            maxHeap.offer(tweets.get(i));
        }
    }
    // pop top 10...
}
```

**Retrospective:** feeding only each followee's *last 10* tweets into the heap (not their entire history) is the key optimization — the final answer can only ever contain at most 10 tweets total, so no followee could ever contribute an 11th-most-recent tweet to a top-10 result; pulling in a followee's entire tweet history would work correctly but waste heap operations on tweets that can mathematically never appear in the answer. A global monotonically-increasing `timeCounter` (rather than wall-clock time) sidesteps any real-clock precision or ordering-tie issues entirely. **Complexity:** O(f · log(10f)) per `getNewsFeed`, where f is the number of followees — far better than sorting the full combined tweet history.

## Problem 4 — LC 1472 Design Browser History

**Pattern:** a single growable list plus a current-position pointer — simpler than the "two stacks" design many candidates reach for first, and it naturally implements both `back` and `forward` without transferring elements between two structures.

```java
void visit(String url) {
    history.subList(current + 1, history.size()).clear(); // discard forward history
    history.add(url);
    current++;
}

String back(int steps) {
    current = Math.max(0, current - steps);
    return history.get(current);
}
```

**Retrospective:** the two-stack design (a "back stack" and a "forward stack") is the instinctive first reach for this problem, but it requires manually popping from one stack and pushing to the other on every `back`/`forward` call; a single list with a movable pointer gets the same behavior with less bookkeeping, since "forward history" is just "the suffix of the list past the current pointer" and `visit()`'s job of discarding it becomes a single `subList().clear()` call. This is a good example of recognizing when a problem's *apparent* two-structure shape (back/forward) can collapse into one simpler structure. **Complexity:** O(steps) for `back`/`forward` in the worst case if implemented with a true stack-pop loop, but O(1) here since it's index arithmetic on a single pointer; O(k) for `visit` in the rare case a large forward history must be discarded (amortized O(1) otherwise).

## Problem 5 — LC 359 Logger Rate Limiter

**Pattern:** a single `HashMap<String, Integer>` of last-printed timestamps per distinct message — the simplest possible design problem in this batch, included for contrast with the concurrency-coding rate-limiter-adjacent problems in `03-concurrency-coding-practice.md`.

```java
boolean shouldPrintMessage(int timestamp, String message) {
    Integer last = lastPrinted.get(message);
    if (last != null && timestamp - last < 10) return false;
    lastPrinted.put(message, timestamp);
    return true;
}
```

**Retrospective:** this is deliberately a single-threaded, no-concurrency-primitives problem — the interesting design decision is entirely about the data model (one last-seen timestamp per distinct message string, not a global rate limit and not a sliding window of past timestamps), not about synchronization. Contrasting this with LC 1188 (Design Bounded Blocking Queue, the actual concurrency-coding entry in this week's batch) is a useful interview moment: correctly identifying that a "rate limiter"-sounding problem doesn't automatically imply thread-safety is required is itself a signal of judgment, not just implementation speed. **Complexity:** O(1) per call, O(m) space where m is the number of distinct messages ever seen.

## Verification

```
$ cd practice/java/week-22/design/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC460 get(1) after put(1,1),put(2,2) = 1
  PASS  LC460 get(2) after eviction = -1
  PASS  LC460 get(3) = 3
  PASS  LC460 get(1) after tie-break eviction = -1 (least recently used among tied freq)
  PASS  LC460 get(4) = 4
  PASS  LC460 get(3) survives = 3
  PASS  LC981 get(foo,1) = bar
  PASS  LC981 get(foo,3) = bar (no exact match, uses floor)
  PASS  LC981 get(foo,4) = bar2
  PASS  LC981 get(foo,8) = bar2 (floor of 4)
  PASS  LC981 get(foo,0) before any set = empty string
  PASS  LC355 getNewsFeed(1) after own post = [5]
  PASS  LC355 getNewsFeed(1) after following user 2 who posts 6 = [6,5]
  PASS  LC355 getNewsFeed(1) after unfollow = [5]
  PASS  LC1472 back(1) from youtube = facebook.com
  PASS  LC1472 back(1) again = google.com
  PASS  LC1472 forward(1) = facebook.com
  PASS  LC1472 back(2) after new visit clips forward history, clamps at google.com
  PASS  LC1472 forward(2) clamps at linkedin.com (youtube.com discarded)
  PASS  LC359 shouldPrintMessage(1,foo) -> true (first time)
  PASS  LC359 shouldPrintMessage(2,foo) -> false (within 10s)
  PASS  LC359 shouldPrintMessage(10,foo) -> false (still within 10s window, 10-1=9)
  PASS  LC359 shouldPrintMessage(11,foo) -> true (11-1=10, window elapsed)
Week 22 — Design (LC 460, 981, 355, 1472, 359): 23/23 assertions passed
```
