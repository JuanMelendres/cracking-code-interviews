---
title: "Loop 3 — Java Fluency, Coding, Production Judgment"
document_type: mock-interview
week: 12
status: draft
loop_number: 3
duration_minutes: 60
rounds: 3
---

# Loop 3 — Java Fluency, Coding, Production Judgment

**Target role:** Senior Backend Engineer (Java). **Duration:** 60 minutes, 3 rounds. **Competencies:** Java Fluency (§8.5), Coding (§8.2), Production Judgment (§8.6) — the two dimensions Loops 1-2 didn't directly target.

## Table of Contents

1. [Round 1 — Java fluency (18 min)](#round-1--java-fluency-18-min)
2. [Round 2 — Coding (20 min)](#round-2--coding-20-min)
3. [Round 3 — System design with production-judgment emphasis: real-time chat (22 min)](#round-3--system-design-with-production-judgment-emphasis-real-time-chat-22-min)
4. [Debrief](#debrief)

---

## Round 1 — Java fluency (18 min)

### Candidate section

1. `Executors.newFixedThreadPool(2)` fed 500 tasks. Walk through exactly what happens to task #499, memory-wise.
2. Justify `AtomicInteger` vs `synchronized` vs `LongAdder` for a request counter under heavy write contention, low read frequency.
3. A virtual thread blocks inside a `synchronized` block. What happens to its carrier, and why does `ReentrantLock` avoid it?

### Interviewer section

| # | Ideal answer outline | Common weak answer | Follow-ups |
|---|---|---|---|
| 1 | Sits in the unbounded `LinkedBlockingQueue` in memory, unstarted, no rejection (measured: 496/500 queued 200ms after submission, Week 9) | "It waits in a queue" with no mention of the queue being unbounded | "How do you get real backpressure instead?" |
| 2 | `LongAdder` for write-heavy/read-rarely (multiple internal cells, summed on read); `AtomicInteger` for moderate contention; `synchronized` rarely wins here | Defaults to `synchronized` without justifying against the alternatives | "When would `AtomicInteger` actually beat `LongAdder`?" |
| 3 | `synchronized` pins the carrier (measured 10x regression, Week 9); `ReentrantLock`'s `park`-based implementation is virtual-thread-aware and unmounts normally | Doesn't know pinning is a real, measured phenomenon | "Your team is migrating to virtual threads. What's the actual migration checklist?" |

**Scoring:** §8.5, 1-5. **4 (Senior bar):** fluent, justifies collection/concurrency choices unprompted, idiomatic modern Java.

## Round 2 — Coding (20 min)

### Candidate section

1. **LC 128 — Longest Consecutive Sequence.** Given an unsorted array of integers, find the length of the longest consecutive elements sequence, in O(n).
2. **LC 973 — K Closest Points to Origin.** Given an array of points and an integer k, return the k closest points to the origin.

### Interviewer section

| # | Pattern | Ideal complexity | Common weak answer | Follow-up |
|---|---|---|---|---|
| 1 | Hash set; only start counting from a true run-start (`n-1` not in the set) | O(n) | Sorting first (O(n log n) — works, but isn't O(n) and the interviewer should push for the hash-set approach) | "Why does checking `set.contains(n-1)` before counting matter for the complexity, specifically?" |
| 2 | Max-heap capped at size k (same "cap and evict" pattern as Week 10's LC 215/347) | O(n log k) | Sorting the entire array by distance first (O(n log n), works but not optimal for small k) | "k is 3 and n is 10 million. Does your approach still make sense?" |

Reference solutions: `practice/java/week-12/final-loop-coding/`.

**Scoring:** §8.2, 1-5.

## Round 3 — System design with production-judgment emphasis: real-time chat (22 min)

### Candidate section

**Design a real-time chat/messaging system** (like WhatsApp — 1:1 and group messaging, delivery receipts, online presence) for 500M daily active users. Full six-phase method, but the interviewer will spend disproportionate time on Phase 6 (Bottlenecks) — prioritize accordingly if time is short.

### Interviewer section

**Expected phase coverage:**
- **Clarify:** message ordering requirement per conversation (must be strict); delivery guarantee (at-least-once acceptable, duplicates handled client-side by message ID, versus attempting exactly-once); group chat fan-out size limits.
- **Estimate:** peak concurrent connections (long-lived, not request/response — this drives the connection-handling architecture choice, not just message volume) versus messages/second.
- **Architecture:** a message queue per recipient (or per conversation) is the natural fit — directly the same delivery-semantics reasoning as `study-packs/week-08/04-delivery-semantics-and-exactly-once.md`: commit-after-delivery-confirmation risks duplicates (acceptable, dedupe by message ID), commit-before-delivery risks silent loss (not acceptable for a chat app) — expect the candidate to explicitly choose at-least-once and name the client-side dedupe mechanism, not accidentally default to at-most-once.
- **Failure mode, pushed hard (this round's actual point):** a recipient's connection server goes down mid-conversation. What happens to messages sent to them in that window? Expect: they queue durably (not held only in the dead server's memory) and deliver on reconnect — the same "outbox row persists until published" durability principle as `study-packs/week-10/01-saga-outbox-and-distributed-transactions.md`, applied to a per-user message queue instead of a business-event outbox.
- **Monitoring:** message delivery latency (send to recipient-ack) as the actual SLI here — a direct application of Week 11's RED "Duration" concept to a signal that isn't literally HTTP latency.

**Common weak answer:** applies one fan-out strategy uniformly without addressing the celebrity-account skew, or names failure modes only when explicitly asked rather than volunteering them.

**Scoring:** §8.6 Production Judgment, 1-5, THIS round specifically (in addition to §8.3 for the design overall). **4 (Senior bar):** names a failure mode for every major component unprompted, discusses monitoring, cites a real-shaped incident scenario (even if illustrative, per this repo's fictionalized-example convention).

---

## Debrief

| Dimension | Score (1-5) | Evidence |
|---|---|---|
| Java Fluency (§8.5) | | |
| Coding (§8.2) | | |
| System Design (§8.3) | | |
| Production Judgment (§8.6) | | |

**What to fix before Loop 4:** _______________
