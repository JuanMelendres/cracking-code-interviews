---
title: "Loop 1 — Technical, Coding, System Design"
document_type: mock-interview
week: 12
status: draft
loop_number: 1
duration_minutes: 60
rounds: 3
---

# Loop 1 — Technical, Coding, System Design

**Target role:** Senior Backend Engineer (Java). **Duration:** 60 minutes, 3 rounds, no breaks — matches a real onsite pace. **Competencies exercised:** Technical Depth (§8.1), Coding (§8.2), System Design (§8.3). Score every round against `00-project/learning-roadmap.md` §8 immediately after, not from memory afterward.

## Table of Contents

1. [Round 1 — Technical deep-dive (20 min)](#round-1--technical-deep-dive-20-min)
2. [Round 2 — Coding (20 min)](#round-2--coding-20-min)
3. [Round 3 — System design: rate limiter (20 min)](#round-3--system-design-rate-limiter-20-min)
4. [Debrief](#debrief)

---

## Round 1 — Technical deep-dive (20 min)

### Candidate section

Answer cold, out loud, before reading the interviewer section:

1. Explain `volatile` via happens-before — not caching.
2. `acks=all` and you still lost a Kafka message. How?
3. Why doesn't exactly-once extend to a write on an external database?

### Interviewer section

| # | Ideal answer outline | Common weak answer | Follow-ups | Hints (only if stuck) |
|---|---|---|---|---|
| 1 | Happens-before edge; compiler/JIT reordering prevented, not CPU caching (`study-packs/week-09/01-...md` §3-4) | "Prevents caching" | "Does it make `count++` atomic?" | "What did the JIT do to the loop in the visibility demo?" |
| 2 | ISR can shrink to the leader alone; `acks=all` waits on the CURRENT ISR, not `replication.factor`; needs `min.insync.replicas` (`week-08/02` §4) | "acks=all is always durable" | "What setting closes the gap?" | "How many replicas does `acks=all` actually wait for?" |
| 3 | Kafka's transactional EOS covers Kafka-to-Kafka only; needs an outbox or idempotent consumer for an external system (`week-10/01` §4) | "Kafka is exactly-once, full stop" | "Your consumer also writes to Postgres. Now what?" | "What's the mechanism that fixes this — named in Week 10?" |

**Scoring:** §8.1 Technical Depth, 1-5. **4 (Senior bar):** sustains 4 follow-ups, cites a real production example, names a trade-off unprompted for at least 2 of the 3 questions.

## Round 2 — Coding (20 min)

### Candidate section

Two problems, narrate throughout, state complexity before being asked.

1. **LC 3 — Longest Substring Without Repeating Characters.** Given a string, find the length of the longest substring without repeating characters.
2. **LC 207 — Course Schedule.** Given `numCourses` and a list of prerequisite pairs `[a, b]` (must take `b` before `a`), determine if it's possible to finish all courses.

### Interviewer section

| # | Pattern | Ideal complexity | Common weak answer | Follow-up |
|---|---|---|---|---|
| 1 | Sliding window + hash map of last-seen index | O(n) time, O(min(n, charset)) space | O(n²) brute force with no window narrowing | "What if the string is UTF-8 with multi-byte characters?" |
| 2 | Topological sort (Kahn's — BFS with indegree count) | O(V+E) | DFS without cycle detection, or claiming "always possible" | "Return the actual valid course order, not just true/false" |

Real, compiled reference solutions and full assertion output: `practice/java/week-12/final-loop-coding/`. **Do not read the solutions before attempting both cold.**

**Scoring:** §8.2 Coding, 1-5. **4 (Senior bar):** Medium in ≤25 min each, narrates throughout, states complexity unprompted, tests own code (walks through at least one example by hand).

## Round 3 — System design: rate limiter (20 min)

### Candidate section

**Design a rate limiter** for a public API, supporting per-user and per-endpoint limits, deployed across multiple service instances. Full six-phase method, 20 minutes — compressed from the usual 45, so prioritize ruthlessly.

### Interviewer section

**Expected phase coverage, compressed:**
- **Clarify:** per-user vs. global vs. per-endpoint scope; hard reject vs. throttle; what response on limit exceeded (429 + `Retry-After`).
- **Estimate:** peak QPS, number of distinct rate-limit keys (users × endpoints), memory footprint per key.
- **Algorithm choice:** token bucket vs. sliding window log vs. sliding window counter — expect a stated trade-off (token bucket allows bursts; sliding window log is exact but memory-heavy; sliding window counter approximates cheaply).
- **Distributed enforcement:** the hard part — local per-instance counting under-enforces the global limit; needs a shared store (Redis) with atomic increment, or accepts approximate enforcement for lower coordination cost. **This is the single most likely place a candidate hand-waves — push here.**
- **Failure mode:** what happens if the shared counter store is down — fail open (allow, risk overload) or fail closed (reject everything, risk false denial)? Either answer is acceptable if justified.

**Common weak answer:** proposes a per-instance in-memory counter without acknowledging it under-enforces the limit by up to `N_instances`x.

**Follow-up:** "Fixed windows let roughly 2x traffic through at the boundary. Where, exactly, and how do you fix it?" (Expected: the classic double-burst at a window edge; fix is a sliding window.)

**Scoring:** §8.3 System Design, 1-5. **4 (Senior bar):** all phases touched even if compressed, estimation drives at least one real decision (algorithm or storage choice), ≥3 failure modes named, ≥2 explicit trade-offs.

---

## Debrief

Fill in immediately after, not from memory later:

| Dimension | Score (1-5) | Evidence |
|---|---|---|
| Technical Depth (§8.1) | | |
| Coding (§8.2) | | |
| System Design (§8.3) | | |

**What to fix before Loop 2:** _______________
