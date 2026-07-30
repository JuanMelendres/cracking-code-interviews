---
title: "Loop 2 — Technical, Coding, System Design, Behavioral"
document_type: mock-interview
week: 12
status: draft
loop_number: 2
duration_minutes: 75
rounds: 4
---

# Loop 2 — Technical, Coding, System Design, Behavioral

**Target role:** Senior Backend Engineer (Java). **Duration:** 75 minutes, 4 rounds — the first full loop shape this week, all six §8 dimensions touched across the loop. **Competencies:** Technical Depth, Coding, System Design, Behavioral Communication.

## Table of Contents

1. [Round 1 — Technical deep-dive (18 min)](#round-1--technical-deep-dive-18-min)
2. [Round 2 — Coding (20 min)](#round-2--coding-20-min)
3. [Round 3 — System design: web crawler (22 min)](#round-3--system-design-web-crawler-22-min)
4. [Round 4 — Behavioral (15 min)](#round-4--behavioral-15-min)
5. [Debrief](#debrief)

---

## Round 1 — Technical deep-dive (18 min)

### Candidate section

1. Add a node to a 10-node consistent-hash ring. How much data moves, and why?
2. `CREATE INDEX` on a live, actively-written 500M-row table. Walk me through it.
3. Explain double-checked locking's failure mode without `volatile`, precisely.

### Interviewer section

| # | Ideal answer outline | Common weak answer | Follow-ups |
|---|---|---|---|
| 1 | ~1/N of keys move (measured 9.2% for N=10 in Week 10's demo, close to the 10% ideal) vs. naive hash%N's ~90%+ | "Some data moves" with no quantification | "Why virtual nodes, not one ring point per physical node?" |
| 2 | `CREATE INDEX CONCURRENTLY` — multi-pass, doesn't hold the blocking `SHARE` lock (measured 1943ms blocked vs. 84ms unblocked, Week 10) | Doesn't know `CONCURRENTLY` exists | "It failed halfway through. What state is the index in now?" |
| 3 | Without `volatile`, no happens-before edge on the singleton field; a reader can observe a non-null reference to a partially-constructed object | "It's about caching" | "Name the alternative fix using a static holder class" |

**Scoring:** §8.1, 1-5.

## Round 2 — Coding (20 min)

### Candidate section

1. **LC 56 — Merge Intervals.** Given an array of intervals, merge all overlapping intervals.
2. **LC 139 — Word Break.** Given a string and a dictionary of words, determine if the string can be segmented into a space-separated sequence of dictionary words.

### Interviewer section

| # | Pattern | Ideal complexity | Common weak answer | Follow-up |
|---|---|---|---|---|
| 1 | Sort by start, merge while the next interval's start ≤ current merged end | O(n log n) (sort-dominated) | Forgetting to sort first, or comparing against the wrong running end | "Now insert one new interval into an already-merged, sorted list without re-sorting everything — what's the complexity?" |
| 2 | 1D DP: `dp[i]` = can `s[0..i)` be segmented | O(n²) time (n² substrings checked against the dict) | Pure recursion with no memoization (exponential) | "Return one actual valid segmentation, not just true/false" |

Reference solutions: `practice/java/week-12/final-loop-coding/`.

**Scoring:** §8.2, 1-5.

## Round 3 — System design: web crawler (22 min)

### Candidate section

**Design a web crawler** that discovers and indexes 1 billion pages, respecting `robots.txt` and avoiding re-crawling the same content excessively. Full six-phase method.

### Interviewer section

**Expected phase coverage:**
- **Clarify:** breadth-first vs. priority-based crawl order; freshness requirements (how often re-crawl); politeness (rate limit per domain, not just globally).
- **Estimate:** 1B pages, average page size, crawl rate needed to complete a full pass in a target window — this should drive the fetcher fleet size.
- **Architecture:** a URL frontier (queue) partitioned per-domain (this is a direct reuse of `study-packs/week-10/03-consistent-hashing.md`'s domain-key partitioning idea, applied to crawl politeness rather than cache sharding) so one domain's rate limit doesn't block crawling every other domain — expect this connection to be made explicitly, unprompted, for a 5.
- **Dedup:** a Bloom filter or a distributed hash set of seen-URL fingerprints, to avoid re-queuing already-crawled/queued URLs — memory-vs-false-positive trade-off worth naming.
- **Failure mode:** a single slow/hanging domain must not stall the whole crawl — same bulkhead-isolation principle as `study-packs/week-10/04-resilience-patterns.md`.

**Common weak answer:** treats politeness/rate-limiting as an afterthought rather than a first-class partitioning concern.

**Follow-up:** "One domain hosts 40% of all target pages. What breaks, and how do you fix it?" (Expected: the same hot-key/hot-partition problem named in Weeks 8 and 10 — a sub-key or additional distributing dimension, with the explicit trade-off it reintroduces.)

**Scoring:** §8.3, 1-5.

## Round 4 — Behavioral (15 min)

### Candidate section

Deliver ONE story, 4-beat structure (situation, task/decision, action, result), ≤2 minutes, quantified:

- **Your choice:** Story 3 (production incident) or Story 11 (scaling/performance) — both should already be retrofitted with Week 11's error-budget/percentile vocabulary. Deliver whichever is stronger.

### Interviewer section

**Listening for (§8.4):** first-person throughout (not "we"); a quantified result (not "it went well"); a named alternative that was considered and rejected; what the choice cost.

**If Story 3:** push for the exact error-budget framing — "what fraction of the month's budget did this consume, and how do you know?"
**If Story 11:** push for the exact percentile cited, and whether the original measurement was closed- or open-loop.

**Common weak answer:** rambling narrative with no clear decision point, or a result stated qualitatively ("it improved a lot") instead of quantified.

**Scoring:** §8.4, 1-5. **4 (Senior bar):** ≤2 min, quantified, first-person, names an alternative considered, states what it cost.

---

## Debrief

| Dimension | Score (1-5) | Evidence |
|---|---|---|
| Technical Depth (§8.1) | | |
| Coding (§8.2) | | |
| System Design (§8.3) | | |
| Behavioral (§8.4) | | |

**What to fix before Loop 3:** _______________
