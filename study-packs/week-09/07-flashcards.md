---
title: "Flashcards — Week 9"
week: 9
last_reviewed: 2026-07-29
---

# Flashcards — Week 9

16 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4.

---

**1. Q: What does `volatile` actually guarantee?**
A: A happens-before edge — writes are visible to subsequent reads of that same field, and specific compiler reorderings are forbidden. It is not a caching mechanism.

**2. Q: Does `volatile` make `count++` thread-safe?**
A: No — read-modify-write is three operations; `volatile` only guarantees each individual read/write is visible, not that the sequence is atomic.

**3. Q: Why does double-checked locking need `volatile`?**
A: Without it, a reader thread can observe a non-null reference before the constructor's writes are visible — a partially-constructed object.

**4. Q: What queue does `Executors.newFixedThreadPool()` use by default?**
A: An unbounded `LinkedBlockingQueue` — tasks are never rejected, so memory grows without limit under sustained overload.

**5. Q: How should CPU-bound vs IO-bound pool sizing differ?**
A: CPU-bound scales near `N_cores`; IO-bound scales with the wait/compute ratio (Little's Law).

**6. Q: What are the six real `Thread.State` values?**
A: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED — no separate "Running" state.

**7. Q: How do you detect a deadlock in a live JVM?**
A: `ThreadMXBean.findDeadlockedThreads()` (what `jstack` uses under the hood).

**8. Q: How much data can an unsynchronized `count++` lose under real concurrent load?**
A: Measured: 83.8% of updates lost with 10 threads × 100,000 increments each.

**9. Q: What does a virtual thread's carrier do when the virtual thread blocks on supported IO?**
A: Unmounts the virtual thread, freeing the carrier for other work — the blocking call doesn't tie up a platform thread.

**10. Q: What causes a virtual thread to pin its carrier?**
A: Blocking inside a `synchronized` block (among a few other cases) — the carrier can't run anything else until the call returns.

**11. Q: Why is pooling virtual threads an anti-pattern?**
A: They're designed to be cheap and disposable, created per-task; pooling reimposes platform-thread-style resource-limiting thinking they exist to eliminate.

**12. Q: What's the most common GC-tuning misconception?**
A: That tuning means increasing heap size — it's one lever among several and isn't always correct.

**13. Q: What does a rising post-GC occupancy trend across young collections suggest?**
A: Objects surviving longer than expected, heading toward promotion — check for a leak or a growing cache.

**14. Q: What is a "humongous allocation" in G1?**
A: An object ≥50% of a region size, handled via dedicated regions outside normal young-gen allocation — more heap doesn't fix problems caused by this allocation pattern.

**15. Q: LC 416 vs Week 8's Coin Change — same recurrence shape, what's the one structural difference?**
A: LC 416 (0/1 knapsack) iterates the target downward so each number is used at most once; Coin Change (unbounded knapsack) iterates upward to allow reuse.

**16. Q: In LC 5's interval DP, why must the table be filled by increasing interval length rather than by row/column?**
A: `dp[i][j]` depends on `dp[i+1][j-1]`, a strictly shorter interval — filling by length guarantees that dependency is already computed.
