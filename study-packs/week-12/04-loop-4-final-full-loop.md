---
title: "Loop 4 — Final Full Loop (All Six Dimensions)"
document_type: mock-interview
week: 12
status: draft
loop_number: 4
duration_minutes: 90
rounds: 4
---

# Loop 4 — Final Full Loop

**Target role:** Senior Backend Engineer (Java). **Duration:** 90 minutes, 4 rounds. **This is the loop `00-project/learning-roadmap.md` §8.7's "Final loop" checkpoint bar is scored against: ≥4/5 on all six §8 dimensions across one full multi-round loop.** Every dimension is deliberately covered exactly once below.

## Table of Contents

1. [Round 1 — Technical depth + Java fluency (25 min)](#round-1--technical-depth--java-fluency-25-min)
2. [Round 2 — Coding (20 min)](#round-2--coding-20-min)
3. [Round 3 — System design + production judgment: hotel booking system (30 min)](#round-3--system-design--production-judgment-hotel-booking-system-30-min)
4. [Round 4 — Behavioral (15 min)](#round-4--behavioral-15-min)
5. [§8.7 Final scorecard](#87-final-scorecard)

---

## Round 1 — Technical depth + Java fluency (25 min)

### Candidate section

Pick 3 of these 5, interviewer's choice, any prior week fair game:

1. Why does write skew survive `REPEATABLE READ` but not `SERIALIZABLE`?
2. Compensate a charged payment — walk the actual mechanism, not the word "rollback."
3. `@Transactional` called from within the same class. What happens, and name the 3 fixes.
4. Why is `LongAdder` preferred over `AtomicLong` for a very-high-contention counter, mechanically?
5. A GC log shows rising post-collection occupancy across 4 successive young collections. Diagnose.

### Interviewer section

Push for 5+ genuine follow-ups on at least one question — this round alone should feel like the hardest 25 minutes of the week. Score §8.1 AND §8.5 from this single round: note separately when an answer demonstrates conceptual depth (8.1) versus idiomatic Java/JVM fluency (8.5) — they often overlap but aren't identical (e.g., Q1 is pure conceptual depth; Q4 is fluency-coded).

**Pass signal for a 5 on §8.1:** volunteers the trap before being asked (e.g., for Q2, names that a failed refund is ITSELF a dual-write-shaped problem, unprompted) and extends to org/cost implications.

## Round 2 — Coding (20 min)

### Candidate section

1. **LC 55 — Jump Game.** Given an array where each element is your maximum jump length from that position, determine if you can reach the last index.
2. **LC 127 — Word Ladder.** Given a `beginWord`, `endWord`, and a word list, find the length of the shortest transformation sequence, changing one letter at a time, each intermediate word must be in the list.

### Interviewer section

| # | Pattern | Ideal complexity | Common weak answer | Follow-up |
|---|---|---|---|---|
| 1 | Greedy — track furthest reachable index | O(n) | DP with O(n²) (works, but greedy is the expected "aha") | "Now return the MINIMUM number of jumps, not just reachability" |
| 2 | BFS over the implicit word graph (26 neighbors per position tried) | O(n × 26 × L) where L = word length | DFS (finds A path, not the SHORTEST) | "n is 10,000 words, each 10 letters. Is your approach fast enough — and if not, what's the fix (bidirectional BFS)?" |

Reference solutions: `practice/java/week-12/final-loop-coding/`.

**Scoring:** §8.2, 1-5. **5 (Staff-adjacent bar):** discusses the bidirectional-BFS optimization for LC 127 unprompted when the follow-up is asked.

## Round 3 — System design + production judgment: hotel booking system (30 min)

### Candidate section

**Design a hotel booking system** — search availability, hold a room, confirm the booking (payment + inventory decrement), with strict no-double-booking and no-lost-booking guarantees. Full six-phase method — this is deliberately the week's most demanding design prompt, drawing directly on Weeks 8 and 10's material.

### Interviewer section

**Expected phase coverage:**
- **Clarify:** a booking is a MULTI-STEP flow across at least two resources (room inventory, payment) owned by different services — this is the central design fork, distinct from a single payment charge in isolation.
- **Estimate:** peak search QPS vs. peak booking-confirmation QPS (search dominates heavily; the booking-confirmation path is lower volume but far higher consistency requirement).
- **Data:** a room-inventory hold with a short TTL (reserve, don't commit, on search-to-cart) plus a transactional outbox (direct reuse of `study-packs/week-10/01-saga-outbox-and-distributed-transactions.md`) so "booking confirmed" and "booking-event published to downstream systems (confirmation email, loyalty points, partner notification)" are atomic — expect this named explicitly, not reinvented from scratch.
- **Idempotency:** an idempotency key supplied by the CALLER on the confirm-booking call, so a network-retried confirmation is recognized as a duplicate and returns the original result rather than double-booking or double-charging — direct reuse of Week 5's idempotency material plus Week 10's outbox-consumer-must-be-idempotent lesson.
- **Saga across services:** confirming a booking is exactly a Saga (`week-10/01` §5) — reserve room, charge payment, confirm booking; if the charge fails, the compensating action is releasing the room hold (a forward action, not a "rollback" of the reservation); if confirmation itself fails after a successful charge, the compensating action is a refund. Expect a named, concrete compensating action for EACH step, not one generic "roll it back."
- **Failure mode, pushed hardest:** the payment provider confirms the charge but the confirmation response is lost before the booking service marks it charged. Expect: this is EXACTLY the dual-write hazard shape, and the fix is the same family — a reconciliation job querying the provider's own charge-status API for any booking left in an ambiguous state past a timeout, rather than assuming failure and re-charging.

**Common weak answer:** treats the booking flow as one atomic operation rather than a Saga with per-step compensating actions, or doesn't distinguish "no double booking" (inventory hold + idempotency) from "no lost booking" (outbox/durability) as two separate guarantees needing two separate mechanisms.

**Scoring:** §8.3 System Design AND §8.6 Production Judgment, both from this round. **4 (Senior bar) on 8.6 specifically:** names the ambiguous-confirmation failure mode unprompted, not only after the interviewer's push.

## Round 4 — Behavioral (15 min)

### Candidate section

One story, interviewer's choice from the full 12-story bank, ≤2 minutes, quantified, four-beat structure.

### Interviewer section

Push once, hard, on whichever beat is weakest live — a real interviewer does not accept the first pass at face value in a final loop. If the story is one of the Week-11-retrofitted ones (3, 7, 11), confirm the quantified claim is stated precisely (an error-budget percentage, a specific percentile), not rounded to a vague "a lot" or "significantly."

**Scoring:** §8.4, 1-5. **5 (Staff-adjacent bar):** scope beyond own team, names who disagreed and their best argument, states what they'd do differently now.

---

## §8.7 Final scorecard

This is the actual checkpoint artifact — fill in immediately, honestly, from THIS loop only:

| Dimension | Score (1-5) | ≥4 required | Evidence |
|---|---|---|---|
| Technical Depth (§8.1) | | | |
| Coding (§8.2) | | | |
| System Design (§8.3) | | | |
| Behavioral (§8.4) | | | |
| Java Fluency (§8.5) | | | |
| Production Judgment (§8.6) | | | |

**§8.7 bar: ≥4/5 on all six.** Any dimension below 4 is the actual, specific thing to keep working on — not "study more," but the named gap from this loop's evidence column. Carry this scorecard into `06-final-readiness-assessment.md`.
