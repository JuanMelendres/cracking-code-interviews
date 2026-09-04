---
title: "Design-Style Coding Problems (LRU, LFU, Iterators)"
slug: design-style-coding-problems
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2115
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - linked-lists-and-in-place-manipulation.md
  - heaps-top-k-and-k-way-merge.md
  - binary-search-and-search-on-answer.md
related:
  - linked-lists-and-in-place-manipulation.md
  - heaps-top-k-and-k-way-merge.md
  - binary-search-and-search-on-answer.md
  - concurrency-coding-problems.md
practice: ../../practice/java/week-22/design/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-22/04-design-coding-practice.md
---

# Design-Style Coding Problems (LRU, LFU, Iterators)

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-22/04-design-coding-practice.md` — real, compiled, executed code (`practice/java/week-22/design/`), re-verified on OpenJDK 21.0.12 while writing this chapter (23/23 assertions passing).

This is Master Topic Register **T-1416** (IWI 6.2, ⭐, very-high frequency). A design-style problem asks for a small class with multiple operations — not a single function — and its difficulty is almost always in composing 2–3 already-familiar data structures so every required operation hits its target complexity simultaneously, not in any single novel algorithm.

## 1. Why This Matters

Design problems (LRU/LFU caches, rate limiters, browser history, time-indexed stores) are among the most practically-relevant coding interview questions, since they mirror real components engineers actually build — and they specifically test whether a candidate can compose multiple data structures together so that *every* required operation (not just the most obvious one) hits its target complexity. A design that gets `get()` to O(1) but leaves `put()` at O(n) has usually missed the actual point of the exercise.

## 2. Prerequisites

[Linked Lists and In-Place Manipulation](linked-lists-and-in-place-manipulation.md) — LRU/LFU-style caches are built on a hash map plus a doubly-linked list, combining that chapter's pointer-manipulation discipline with O(1) hash lookups. [Heaps, Top-K, and K-Way Merge](heaps-top-k-and-k-way-merge.md) and [Binary Search](binary-search-and-search-on-answer.md) — both are reused as components in this chapter's own composed designs (Sections 4, 7).

## 3. Foundation (L1)

**A design-style problem specifies a class with multiple methods, each with its own required time complexity, and the entire problem is choosing and combining data structures so every method meets its bound simultaneously.** Unlike a single-function algorithm problem, there's rarely one "correct" algorithm — there's a composition of already-familiar structures (a hash map for O(1) lookup, a linked list for O(1) reordering, a heap for O(log n) extremes) chosen specifically because their combined guarantees satisfy every required operation.

**The single most common composition pattern in this family is "a hash map from key to node, plus a linked structure maintaining some order among those same nodes"** — the hash map gives O(1) lookup by key; the linked structure gives O(1) reordering or removal once a node is already found, without needing to search for it again.

## 4. Core Concepts (L2)

**LFU Cache's two-eviction-dimension composition** (Section 7, Problem 1) extends the LRU pattern with a second competing tiebreaker: evict the least *frequently* used entry, and among ties, the least *recently* used one. A `key -> Node` map gives O(1) lookup; a `frequency -> LinkedHashSet<Node>` map gives O(1) access to each frequency bucket, with the `LinkedHashSet`'s own insertion order providing the LRU-within-a-frequency tiebreak for free; a tracked `minFreq` pointer avoids ever having to scan for the globally least-frequent bucket.

**Timestamp-ordered storage plus binary search** (Time Based Key-Value Store, Section 7 Problem 2) reuses [Binary Search's](binary-search-and-search-on-answer.md#4-core-concepts-l2) boundary-finding template directly: since each key's values arrive in strictly increasing timestamp order (a guarantee this specific problem provides), the per-key list is already sorted with no extra work, and a "find the floor" binary search (largest timestamp ≤ query) answers point-in-time lookups in O(log k).

**Bounded per-source heap merge** (Design Twitter, Section 7 Problem 3) reuses [Heaps'](heaps-top-k-and-k-way-merge.md#4-core-concepts-l2) k-way-merge idea, but bounds each source's contribution upfront: feeding only each followee's *last 10* tweets into the heap (never their entire history) is safe because the final answer can only ever contain 10 tweets total, so no followee could ever contribute an 11th-most-recent tweet to a top-10 result.

**Structure-collapsing recognition** (Design Browser History, Section 7 Problem 4): the "two stacks" design many candidates reach for instinctively can be replaced by a single growable list plus a movable current-position pointer, since "forward history" is just the suffix of the list past the current pointer — recognizing when a problem's apparent two-structure shape collapses into one simpler structure is a real, transferable design instinct.

**Judgment about what a "design" problem's name implies** (Logger Rate Limiter, Section 7 Problem 5): despite sounding like it might require thread-safety, this specific problem is LeetCode-tagged plain "Design," not "Concurrency" — its actual difficulty is entirely in the data model (one last-seen timestamp per distinct message), not synchronization. Correctly identifying that a "rate limiter"-sounding problem doesn't automatically imply concurrency concerns (contrasted directly with [Concurrency Coding Problems'](concurrency-coding-problems.md) genuinely thread-safe bounded queue) is itself a signal of judgment, not just implementation speed.

## 5. How It Works Internally (L3)

**LFU Cache's `minFreq`-never-decrements invariant, precisely**: an entry's frequency can only ever *increase* on access (each `get`/`put` on an existing key bumps its frequency by one) — it never decreases. This means `minFreq` only ever needs incrementing (when the bucket at the current `minFreq` becomes empty after an entry's frequency increases past it) or resetting to `1` (when a brand-new key is inserted, since a fresh entry always starts at frequency 1, which can only ever be ≤ the current `minFreq`). Without this invariant, finding the globally-least-frequent bucket on every eviction would require scanning all frequency buckets — an O(number of distinct frequencies) cost the `minFreq` pointer entirely eliminates, keeping both `get` and `put` genuinely O(1).

**Design Twitter's bounded-heap-input correctness, precisely**: the final `getNewsFeed` result can contain at most 10 tweets, drawn from however many followees a user has. Since the heap will only ever *emit* the top 10 across all candidates fed into it, feeding in more than 10 tweets from any single followee is provably wasteful — none of that followee's older tweets could ever displace a different followee's more recent tweet in a top-10 result, since at most 10 total slots exist regardless of how many followees contribute. Bounding each followee's contribution to their own last 10 tweets keeps the heap's total size at `O(10 · f)` (f = followee count) rather than `O(total tweet history across all followees)`, a real, significant difference for a user following many prolific accounts.

**Design Browser History's list-versus-two-stacks trade-off, precisely**: a two-stack design requires manually popping from a "back stack" and pushing onto a "forward stack" (or vice versa) on every `back`/`forward` call — real bookkeeping that a single list with a movable index pointer avoids entirely, since "the forward history" is definitionally just `history.subList(current + 1, history.size())`, and discarding it on a new `visit()` call is a single `clear()` operation rather than manually draining a stack.

## 6. Practical Usage

- **Before choosing data structures, list every required method and its target complexity separately** — a composition that achieves O(1) for `get` but overlooks `put`'s requirement is an incomplete design, not a partially-correct one.
- **Default to "hash map for lookup, plus a linked/ordered structure for the secondary property" as the starting composition** for any cache-eviction-style design problem, then adapt for the specific eviction rule (LRU: recency only; LFU: frequency then recency).
- **Explicitly ask whether a "design" problem's name implies thread-safety, rather than assuming it** (Section 4) — a rate limiter or bounded-queue-sounding problem may be entirely single-threaded, and adding unnecessary synchronization primitives is itself a design misstep in an interview.

## 7. Examples

**Problem 1 — LC 460, LFU Cache.**

```java
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

**Retrospective:** see Section 5's `minFreq`-invariant argument. **Complexity:** O(1) for both `get` and `put`.

**Problem 2 — LC 981, Time Based Key-Value Store.**

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

**Retrospective:** the "floor" binary-search template, applied to a per-key timestamp list already sorted by the problem's own constraints. **Complexity:** O(log k) per `get`, O(1) amortized per `set`.

**Problem 3 — LC 355, Design Twitter.**

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

**Retrospective:** see Section 5's bounded-input argument. **Complexity:** O(f · log(10f)), f = followee count.

**Problem 4 — LC 1472, Design Browser History.**

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

**Retrospective:** see Section 5's structure-collapsing argument. **Complexity:** O(1) for `back`/`forward` (index arithmetic), amortized O(1) for `visit`.

**Problem 5 — LC 359, Logger Rate Limiter.**

```java
boolean shouldPrintMessage(int timestamp, String message) {
    Integer last = lastPrinted.get(message);
    if (last != null && timestamp - last < 10) return false;
    lastPrinted.put(message, timestamp);
    return true;
}
```

**Retrospective:** deliberately single-threaded — the design decision is entirely about the data model, not synchronization (Section 4). **Complexity:** O(1) per call.

## 8. Common Mistakes

- **Optimizing one required operation while overlooking another's stated complexity requirement** — a design that achieves O(1) `get` but O(n) `put` (or vice versa) hasn't actually solved an LRU/LFU-style problem.
- **Reaching for the two-stack design reflexively for any "history/navigation" problem** without checking whether a single structure with a movable pointer (Section 4/5) is simpler and equally correct.
- **Assuming any problem with "rate limiter," "queue," or "cache" in its name requires thread-safety primitives** — Section 4/8 names this directly as a real, checkable judgment call, not an automatic assumption.

## 9. Edge Cases

- **A tie-break eviction among entries at the same frequency in LFU Cache** (verified case, correctly evicting the least-recently-used among tied-frequency entries) — confirms the `LinkedHashSet`'s insertion-order tiebreak works as intended.
- **A query timestamp before any value was ever set** (Time Based Key-Value Store's verified case, correctly returning an empty string) — the binary search must correctly report "no valid floor exists" rather than an incorrect default.
- **Unfollowing a user removes their tweets from the news feed on the very next call** (Design Twitter's verified unfollow case) — confirms the news-feed computation is derived fresh from current follow state each call, not cached incorrectly from a prior state.
- **A new `visit()` call after navigating back, discarding forward history** (Design Browser History's verified case) — confirms the `subList().clear()` discard happens correctly before the new URL is added.

## 10. Performance Implications

Real, executed verification from `practice/java/week-22/design/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
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

The performance lesson specific to design problems isn't any single number — it's that a correct design achieves its *stated* complexity for *every* required operation simultaneously, verified here across all five problems' full operation sets, not just their most prominent method.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Hash map + doubly-linked list (LRU/LFU) | O(1) for both lookup and reordering | More implementation complexity than either structure alone |
| Sorted list + binary search (time-indexed store) | O(log k) point-in-time queries, no extra structure needed given the sorted-by-construction guarantee | Relies on the specific guarantee that inserts arrive in sorted order; wouldn't work unmodified otherwise |
| Bounded per-source heap merge | Avoids materializing full history across many sources | Requires proving the bound (Section 5) is actually safe for the specific problem |
| Single list + pointer (browser history) | Simpler than two stacks, same asymptotic complexity | Less immediately "obvious" as a design than the two-stack instinct, though simpler once seen |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is checking every required operation's stated complexity *before* committing to a design, rather than optimizing the first operation that comes to mind and hoping the rest follow. LFU Cache specifically punishes designs that get `get()` right but hand-wave `put()`'s eviction logic — the `minFreq` pointer (Section 5) is exactly the kind of detail that separates a design that "mostly works" from one that actually meets its stated bound.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, this chapter's compositional design skill — combining a hash map, a linked structure, a heap, or a sorted structure so every required operation hits its target complexity — is directly the skill needed to design a real production component: an in-memory cache with a specific eviction policy, a feature-flag rollout history queryable by point-in-time, a social-feed aggregation service. Design Twitter's bounded-per-source-heap technique (Section 5) is a real, load-bearing pattern in production feed-aggregation systems at scale; Time Based Key-Value Store's floor-binary-search technique is the same shape as any "as-of" or audit-log point-in-time query a real system needs to answer efficiently.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a design-problem-specific algorithmic root cause, though the underlying components (caching, time-indexed queries) connect to real production concerns documented elsewhere in this repository under different topic boundaries (e.g., `handbook/databases/` caching strategy chapters).

> Planned reference: a future `production-cookbook/` entry covering a real cache-eviction-policy design decision (e.g., a service that needed LFU rather than LRU semantics after discovering its actual access pattern didn't match LRU's recency assumption) would be a natural, non-duplicative addition connecting this chapter's Section 13 transfer to a genuine production system.

## 15. Interview Questions

### Question 1 — Design a data structure that supports get and put in O(1), evicting the least recently used entry when a capacity limit is reached.

**Why interviewers ask it.** LRU Cache is the foundational design problem this entire pattern builds from — it's the canonical hash-map-plus-linked-list composition, and how a candidate arrives at it (and defends each piece's necessity) predicts how they'll handle every harder variant (LFU, here Section 7 Problem 1).

**Expected answer.** A hash map from key to a doubly-linked-list node gives O(1) lookup. The doubly-linked list maintains recency order (most recently used at one end, least at the other); accessing an existing entry requires O(1) removal and re-insertion at the recent end, which a doubly-linked list supports directly (a singly-linked list would need O(n) to find the node's predecessor for removal). Eviction removes the node at the least-recent end, also O(1).

**Minimum acceptable answer.** Produces a correct design, even if reasoning about why *doubly*-linked (not singly) is required needs prompting.

**Strong Senior answer.** Explicitly states why a doubly-linked list is required (O(1) removal of an arbitrary interior node needs a reference to its predecessor, which only a doubly-linked list's own node carries) rather than a singly-linked one.

**Staff-level extension.** Extends directly to LFU Cache (Section 7, Problem 1) unprompted, correctly identifying the additional `frequency -> LinkedHashSet<Node>` layer and the `minFreq` pointer as the natural generalization once a second eviction dimension is introduced.

**Common mistakes.** Using a singly-linked list and then needing an O(n) traversal to find a node's predecessor for removal — technically workable but violates the required O(1) bound.

**Follow-up questions.** "How would you extend this to LFU (least frequently used, with recency as a tiebreaker)?" (Exactly Section 4/5's answer.)

### Question 2 — When would you choose a single list with a position pointer over two separate stacks for a "back/forward" navigation history feature?

**Why interviewers ask it.** It's a direct test of the structure-collapsing recognition (Section 4/5) — whether a candidate can recognize when an apparently two-structure problem is actually simpler than it looks.

**Expected answer.** A single growable list plus a movable current-position index achieves the same back/forward behavior as two stacks, with less bookkeeping: "forward history" is simply the suffix of the list past the current position, and visiting a new URL discards that suffix in one operation (a `subList().clear()` or equivalent), rather than requiring the two-stack design's manual pop-from-one-push-to-the-other transfer on every navigation call.

**Minimum acceptable answer.** Produces a correct two-stack design, even without recognizing the simpler single-list alternative unprompted.

**Strong Senior answer.** Recognizes and produces the single-list-plus-pointer design directly, and can explain why it's equivalent in complexity while being simpler to implement and reason about.

**Staff-level extension.** Generalizes the underlying recognition skill: any design problem whose two apparent "structures" are actually two views (a prefix and a suffix) of one underlying ordered collection is a candidate for this same collapsing simplification — a transferable design-review instinct, not a fact specific to browser history.

**Common mistakes.** Defaulting to the two-stack design purely because it's the more commonly taught textbook answer, without evaluating whether a simpler equivalent exists.

**Follow-up questions.** "Does this design still work if `back`/`forward` needed to jump by a variable number of steps in one call?" (Yes — Section 7's actual implementation already handles this via `Math.max(0, current - steps)`, clamping at the list's boundary.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-22/design/) yourself and confirm the same 23/23 assertions pass.
- This pattern has additional real, already-solved problems: LC 146 (LRU Cache), LC 380 (Insert Delete GetRandom O(1)), LC 706 (Design HashMap), and LC 622 (Design Circular Queue) across earlier weeks' practice code — study LRU Cache directly alongside this chapter's LFU Cache (Section 7, Problem 1) as the simpler, single-eviction-dimension precursor.
- Implement LC 1206 (Design Skiplist) or LC 155 (Min Stack, already solved and correctly tagged under Stacks rather than Design) as an exercise in recognizing which pattern category a new "design" problem actually belongs to, given the overlap-resolution discipline this chapter's own source material documents explicitly (Section 1).

## 17. Debugging Exercises

**Symptom:** an LFU cache implementation passes basic get/put tests but occasionally evicts the wrong entry when multiple entries share the same (non-minimum) frequency.

**Diagnose:** check whether entries are only ever compared/evicted from the globally tracked `minFreq` bucket, and whether the frequency bucket's own internal structure (a `LinkedHashSet`, per Section 4/5) is genuinely providing insertion-order (recency) semantics for ties — a bug here often traces to using a plain `HashSet` instead of a `LinkedHashSet` for the per-frequency bucket, silently losing the LRU-within-frequency tiebreak the problem requires, since a plain `HashSet` has no defined iteration order. Confirm by constructing a test case with several entries at the same frequency, accessed in a known order, and checking whether eviction correctly picks the least-recently-accessed among them.

## 18. Design Exercises

**Design constraint:** design a service-level API rate limiter that must track, per API key, whether that key has exceeded N requests in the trailing 60-second window, supporting millions of distinct API keys with O(1) or near-O(1) per-request overhead.

Design this by composing techniques from across this chapter and its prerequisites directly: a hash map from API key to a small per-key structure (analogous to this chapter's own hash-map-plus-secondary-structure compositions) tracking recent request timestamps — either a bounded queue of the last N timestamps (evicting any older than 60 seconds on each check) or a simpler fixed-window counter reset periodically, depending on whether a strict sliding window or an approximate fixed window meets the actual product requirement. State explicitly, per Section 4's judgment-call lesson, whether this specific design genuinely requires thread-safety (a real API gateway handling concurrent requests to the same key from multiple threads, unlike this chapter's own single-threaded Logger Rate Limiter) — and if so, name which specific operations need synchronization, connecting directly to [Concurrency Coding Problems'](concurrency-coding-problems.md) own bounded-structure techniques.

## 19. Further Reading

- [Linked Lists and In-Place Manipulation](linked-lists-and-in-place-manipulation.md) — the doubly-linked-list pointer discipline every LRU/LFU-style design in this chapter depends on.
- [Concurrency Coding Problems](concurrency-coding-problems.md) — the genuinely thread-safe sibling pattern, and the direct point of contrast for Section 4/8's "does this design problem actually need concurrency" judgment call.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what a "design" problem asks for and why it's about composing structures, not one novel algorithm | [Section 3](#3-foundation-l1) |
| L2 | Choose the right composition of hash map, linked structure, heap, or sorted structure for a new cache/history/store design problem | [Interview Question 1](#question-1--design-a-data-structure-that-supports-get-and-put-in-o1-evicting-the-least-recently-used-entry-when-a-capacity-limit-is-reached) |
| L3 | Derive the `minFreq`-invariant argument for LFU Cache and the bounded-heap-input argument for Design Twitter | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real LFU tie-break eviction bug (Section 17), and design a real rate-limiting system while correctly judging whether it requires thread-safety (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
