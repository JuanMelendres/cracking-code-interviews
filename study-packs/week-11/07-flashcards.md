---
title: "Flashcards — Week 11"
week: 11
last_reviewed: 2026-07-29
---

# Flashcards — Week 11

16 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4.

---

**1. Q: What does `verify(gateway, times(3))` prove that a return-value assertion alone cannot?**
A: The exact interaction — that the dependency was called the exact expected number of times with the exact arguments.

**2. Q: What's wrong with mocking the database in a repository test?**
A: It only verifies the test's own assumptions about what the database does — never real SQL correctness.

**3. Q: What does coverage percentage actually measure?**
A: Execution (lines/branches run at least once) — nothing about assertion quality. A diagnostic, not a target.

**4. Q: What does an integration test against a real database catch that mocking it cannot?**
A: Real SQL correctness, type mismatches, real constraint violations — anything about actual database behavior.

**5. Q: Is "mock or real dependency" an all-or-nothing choice?**
A: No — a per-layer decision: mock at the business-logic layer, use a real dependency at the boundary layer.

**6. Q: What is coordinated omission?**
A: A load-testing bug where a closed-loop generator sends fewer requests exactly when the service is slow, understating true tail latency. Measured: p99 500ms (closed-loop) vs 830ms (open-loop), same service.

**7. Q: Why can't average latency characterize user experience?**
A: It can't distinguish "uniformly mediocre" from "mostly fast, occasionally very slow" — different experiences, same average.

**8. Q: Why is p99.9 usually better than max/p100 as an SLO target?**
A: Max is dominated by rare, often environmental outliers; p99.9 targets a representative tail without chasing unrepresentative extremes.

**9. Q: What single piece of data lets a tracing backend reconstruct a request's full path?**
A: A shared `traceId` across every span in that request, with parent-child `spanId` relationships.

**10. Q: Metrics, traces, logs — what does each answer?**
A: Metrics: is something wrong, in aggregate? Traces: where in the call chain? Logs: why, in detail, for that specific span?

**11. Q: What does USE stand for, and what does it diagnose?**
A: Utilization, Saturation, Errors — a RESOURCE (CPU, disk, heap, connection pool).

**12. Q: What does RED stand for, and what does it diagnose?**
A: Rate, Errors, Duration — a SERVICE (an endpoint or consumer group).

**13. Q: Why can a monthly error-budget aggregate mislead on its own?**
A: It can hide a severe single-day incident — measured: one 40-minute incident consumed ~14% of an entire month's budget, invisible in the 35.1%-of-budget monthly total alone.

**14. Q: LC 704 and LC 33 — what's the one extra decision LC 33 adds to plain binary search?**
A: Determining which half of the array is currently properly sorted, before deciding which half to search.

**15. Q: LC 53 and LC 121 — why are they the same underlying pattern despite different domains?**
A: Both are "single pass, track one running extreme value, derive the answer from the current element relative to it."

**16. Q: In LC 76 (Minimum Window Substring), what triggers shrinking the window from the left?**
A: The window currently satisfies the full character-count requirement — shrink while it still does, tracking the best (smallest) window found.
