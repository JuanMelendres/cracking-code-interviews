---
title: "Code Review Exercise — Spot the Five Collections Antipatterns"
week: 14
document_type: study-pack-exercise
status: draft
last_reviewed: 2026-07-31
---

# Code Review Exercise — Spot the Five Collections Antipatterns

This week's deliverable, in the same style as Week 13's: a single, plausible-looking class with one antipattern from each of this week's five topics baked in.

## The code under review

```java
public class RequestMetricsCache {

    private final Map<String, Integer> requestCounts = new HashMap<>(); // (A)
    private final List<String> recentPaths = new ArrayList<>();          // (B)
    private final Queue<String> pendingWrites = new LinkedList<>();      // (C)

    public void recordRequest(String path) {
        Integer current = requestCounts.get(path);        // (D)
        requestCounts.put(path, (current == null ? 0 : current) + 1);
        recentPaths.add(0, path); // keep most recent first  // (E)
        pendingWrites.offer(path); // unbounded -- never rejects
    }

    public int getCount(String path) {
        return requestCounts.getOrDefault(path, 0);
    }
}
```

This class is called from many concurrent request-handling threads.

## Your task

For each of (A) through (E), identify:

1. Which of this week's five topics it touches.
2. What actually breaks (with a concrete, plausible failure scenario).
3. The specific fix.

Do this in writing before reading further.

---

## Worked Solution

**(A) — A plain `HashMap` accessed from many concurrent threads (T-201/T-205).**
Breaks: concurrent `put()` calls from multiple request-handling threads can corrupt `HashMap`'s internal bucket structure — measured directly in this week's `ConcurrentHashMapDemo` as entries silently disappearing, no exception thrown, just a wrong final count.
Fix: `new ConcurrentHashMap<>()`.

**(B) — An `ArrayList` accessed from many concurrent threads, in addition to being the wrong structure for its actual usage (T-201/T-205, T-202).**
Breaks: `ArrayList` isn't thread-safe either — concurrent `add()` calls can corrupt its internal array, or throw `ArrayIndexOutOfBoundsException`/`ConcurrentModificationException` depending on the exact interleaving.
Fix: at minimum, wrap in `Collections.synchronizedList(new ArrayList<>())` or use `CopyOnWriteArrayList` if reads vastly outnumber writes; better, reconsider the design entirely — see (E) below, which shows this list is also the wrong *type* for its access pattern regardless of thread-safety.

**(C) — An unbounded `LinkedList`-backed `Queue` (T-207).**
Breaks: `pendingWrites` never rejects a write; if whatever drains it (a background writer, presumably) ever falls behind — exactly the scenario this week's `BlockingQueueDemo` production scenario describes — memory grows without limit until an eventual `OutOfMemoryError`.
Fix: replace with a bounded `BlockingQueue` (e.g., `new ArrayBlockingQueue<>(1000)`), sized deliberately, with an explicit decision for what happens when it's full (drop, block, or reject with a signal back to the caller).

**(D) — A `get()`-then-`put()` counter increment on a (soon-to-be) concurrent map (T-205).**
Breaks: even after fixing (A) to use `ConcurrentHashMap`, this specific increment pattern still loses updates under real concurrent access — measured directly in this week's chapter at a ~84% lost-update rate under 8-thread contention.
Fix: `requestCounts.merge(path, 1, Integer::sum);` — one atomic call, no separate get/put.

**(E) — `ArrayList.add(0, path)` on every single request (T-202).**
Breaks: every call shifts every existing element in `recentPaths` one slot right — O(n) per call, so the *total* cost of tracking N recent paths this way is O(n²), not O(n). Measured directly in this week's chapter: front-insertion on `ArrayList` was ~117x slower than the `LinkedList.addFirst()` equivalent at just 20,000 insertions — and this cache presumably handles far more requests than that over its lifetime.
Fix: use a `LinkedList` (or `ArrayDeque`) and call `addFirst(path)` instead — O(1) per call regardless of size — or, if only a bounded number of "recent" paths need retaining, use a fixed-size structure that evicts the oldest entry instead of growing forever.

## Self-Check

- [ ] Found all five defects before reading the solution
- [ ] For each, named the specific failure scenario and connected it to this week's measured evidence
- [ ] Noticed that (A), (C), and (D) all stem from the same root cause (concurrent access without the right structure/operation), while (E) is a separate, purely single-threaded performance defect
- [ ] Can explain why fixing (A) alone (switching to `ConcurrentHashMap`) does NOT fix (D) — they are two separate, independent defects
