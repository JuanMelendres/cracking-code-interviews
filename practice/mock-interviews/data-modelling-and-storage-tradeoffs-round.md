---
title: "Mock Interview: Data Modelling and Storage Trade-offs Round (30 min)"
slug: data-modelling-and-storage-tradeoffs-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 30
competencies:
  - Cold EXPLAIN ANALYZE diagnosis
  - Many-to-many modelling with an attribute-bearing relationship
  - Aggregate boundaries as transaction boundaries
  - PostgreSQL vs. DynamoDB, argued both ways
  - Monotonic-stack coding under a structural-gap interrupt
  - Production incident story (four-beat trade-off structure)
related:
  - ../../handbook/databases/query-planning-and-explain-analyze.md
  - ../../handbook/databases/data-modelling-and-explicit-join-tables.md
  - ../../handbook/architecture/ddd-tactical-design-aggregates.md
  - ../../handbook/system-design/distributed-transactions-saga-and-outbox.md
  - ../../handbook/system-design/storage-selection-tradeoffs.md
  - ../../interview-playbook/coding/coding-interview-communication-protocol.md
  - ../../behavioral-handbook/04-production-incident-narratives.md
source: ../../study-packs/week-02/09-week-2-mock-interview.md
official_references: []
---

# Mock Interview: Data Modelling and Storage Trade-offs Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 30 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below. Includes one required `EXPLAIN` walkthrough spoken aloud. Elevated from `study-packs/week-02/09-week-2-mock-interview.md`. Like the [Architecture and Database Indexing Round](architecture-and-database-indexing-round.md), this round's source scores against the shared six-dimension rubric rather than an inline per-question 1–5 scale, and states explicitly that no Week-2-specific evidence anchors exist — "general dimensions still apply." This file's Evaluator Section builds pass/borderline/fail signals from the interviewer script's own stated listening-for cues, not from invented anchors.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| Cold EXPLAIN ANALYZE diagnosis | Q1 | [Query Planning and EXPLAIN ANALYZE](../../handbook/databases/query-planning-and-explain-analyze.md) |
| Many-to-many with an attribute-bearing relationship | Q2 | [Data Modelling and Explicit Join Tables](../../handbook/databases/data-modelling-and-explicit-join-tables.md) |
| Aggregate boundaries as transaction boundaries | Q3 | [DDD Tactical Design — Aggregates](../../handbook/architecture/ddd-tactical-design-aggregates.md), [Distributed Transactions: Saga and Outbox](../../handbook/system-design/distributed-transactions-saga-and-outbox.md) |
| PostgreSQL vs. DynamoDB, argued both ways | Q4 | [Storage Selection Trade-offs](../../handbook/system-design/storage-selection-tradeoffs.md) |
| Monotonic-stack coding (LC 739) | Q5 | [Coding Interview Communication Protocol](../../interview-playbook/coding/coding-interview-communication-protocol.md) |
| Production incident story | Q6 | [Production Incident Narratives](../../behavioral-handbook/04-production-incident-narratives.md) |

## Interviewer Opening Script

*"This is a 30-minute round on data modelling and storage trade-offs, including one live EXPLAIN ANALYZE read. I'll give you six scenarios — diagnose, model, defend, code, and tell a story. Think aloud throughout; I'm listening for your reasoning as much as your final answer. Let's start with a query plan."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(3 min)** Given an unfamiliar `EXPLAIN ANALYZE` output, diagnose it aloud, cold.
2. **(4 min)** "Model a many-to-many relationship between `Order` and `Product`. Now it needs a `quantity`. What changes?"
3. **(4 min)** "What is an aggregate boundary, and why is it a transaction boundary?" Then one follow-up.
4. **(4 min)** "Choose between PostgreSQL and DynamoDB for a given workload. Defend it, then argue the opposite."
5. **(8 min)** Implement LeetCode 739 (Daily Temperatures), narrated per all six phases of the [Coding Interview Communication Protocol](../../interview-playbook/coding/coding-interview-communication-protocol.md). State, unprompted, why a values-only stack can't work.
6. **(7 min)** Deliver a production-incident story using the four-beat trade-off structure, then answer one follow-up.

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — Cold EXPLAIN ANALYZE diagnosis

**Ideal answer outline:** reads the plan structure top-down (or bottom-up, node by node), names the scan/join types present, and compares estimated vs. actual row counts to flag any planner misestimate, without the source chapter open.
**Common weak answers:** describing the query in prose without engaging with the actual plan nodes.
**Pass signal:** correctly identifies the plan's structure and any estimate-vs-actual divergence unprompted, cold.
**Borderline signal:** reaches a correct diagnosis only with hints toward specific plan fields.
**Fail signal:** cannot parse the EXPLAIN ANALYZE structure at all.

### Question 2 — Many-to-many with an attribute

**Ideal answer outline:** introduces an explicit join entity (e.g., `OrderLine`) carrying `quantity` as its own attribute, since a many-to-many relationship that needs to hold data belongs on the relationship itself, not squeezed into either side. On the follow-up ("is there still a case for the explicit entity with no extra column today?"): recognizes that the relationship's own attributes may need to evolve later (e.g., a price captured at time of order, distinct from the product's current price) — this is the specific insight the source calls out; its absence is the specific gap to flag.
**Common weak answers:** modelling the relationship as a bare join table with no explicit entity identity, and no recognition that the relationship might need its own history later.
**Pass signal:** introduces the explicit join entity for `quantity`, and reaches the price-history rationale either unprompted or promptly on the follow-up.
**Borderline signal:** models `quantity` correctly but the price-history insight doesn't surface even after the follow-up.
**Fail signal:** cannot model the many-to-many-with-attribute relationship correctly at all.

### Question 3 — Aggregate boundary as transaction boundary

**Ideal answer outline:** defines an aggregate as a consistency boundary — invariants inside it are enforced atomically, in one transaction, by design. On the follow-up (two transactions need to update two different aggregates together): names a specific mechanism — saga or outbox — not just the unexplained buzzword "eventual consistency."
**Common weak answers:** saying "eventual consistency" as if it were itself a mechanism, with no named implementation pattern behind it.
**Pass signal:** correctly defines the aggregate-as-transaction-boundary relationship and names saga or outbox specifically on the follow-up.
**Borderline signal:** correct on the definition; says "eventual consistency" on the follow-up without naming a mechanism.
**Fail signal:** proposes a single transaction spanning both aggregates, defeating the purpose of the boundary.

### Question 4 — PostgreSQL vs. DynamoDB, argued both ways

**Ideal answer outline:** defends an initial choice with workload-specific reasoning, then genuinely argues the opposite rather than restating the same position weakly. When interrupted mid-defense (*"what specific access pattern would flip your answer?"*), names a concrete pattern — e.g., a need for ad hoc multi-key transactional queries favoring PostgreSQL, or single-key access at extreme write throughput favoring DynamoDB.
**Common weak answers:** a one-sided answer that can't genuinely argue the opposite position, or a vague "it depends" with no named access pattern when pushed.
**Pass signal:** names a specific, concrete access pattern under the interrupt.
**Borderline signal:** argues both sides adequately but the interrupt only produces a generic "it depends."
**Fail signal:** cannot argue the opposite position at all.

### Question 5 — Daily Temperatures, live coding

**Ideal answer outline:** recognizes, before writing code, that the answer requires a monotonic stack of *indices*, not values — because computing the day-distance to a warmer day requires knowing which day a candidate value came from, which a values-only stack discards. States this unprompted. If the candidate starts with a values-based stack, the interrupt (*"how will you know which day that value came from?"*) is designed to surface the structural impossibility directly, not as a code-review nitpick.
**Common weak answers:** starting with a values-based stack and not recognizing the structural gap even after the interrupt.
**Pass signal:** states the index-vs-value reasoning unprompted, before or without needing the interrupt.
**Borderline signal:** needs the interrupt to recognize the structural gap, but corrects promptly once asked.
**Fail signal:** does not recognize the structural gap even after the interrupt, or never arrives at a working solution.

### Question 6 — Production incident story

**Ideal answer outline:** a four-beat trade-off structure (situation/decision point, action taken, the specific decision criterion used, and the cost incurred by that decision) delivered clearly. On the follow-up, if the decision criterion (beat 3) or the cost (beat 4) wasn't explicit in the initial telling, the candidate should be able to supply it precisely when asked.
**Common weak answers:** a story that names what was done and what improved, but never states the specific criterion used to choose that path, or never names what it cost.
**Pass signal:** all four beats present, including the decision criterion and the cost, either unprompted or promptly on request.
**Borderline signal:** the story is coherent but the decision criterion or cost has to be extracted through the follow-up and arrives vaguely.
**Fail signal:** no clear four-beat structure, or cannot supply the criterion/cost even when asked directly.

## Scoring Rubric

Like the [Architecture and Database Indexing Round](architecture-and-database-indexing-round.md), this round is scored against the shared six-dimension rubric in [`study-packs/week-01/10-week-1-evaluation-rubric.md`](../../study-packs/week-01/10-week-1-evaluation-rubric.md) — the source mock states explicitly that "general dimensions still apply" for Week 2, with no week-specific evidence anchors defined. Score Technical Depth (Q1–Q4), Coding (Q5), and Behavioral Communication (Q6) using the rubric's general 1–5 scale (3 = Mid, 4 = Senior, 5 = Staff), and use the pass/borderline/fail signals above as this round's own evidence anchors for each dimension.

## Debrief Guide

Walk the candidate through their scores, starting with the weakest. Questions 2 and 3 share a theme: both ask the candidate to recognize when a structural pattern (an explicit join entity, a strict aggregate boundary) is worth the extra design cost *before* a concrete forcing requirement exists — a future-proofing judgment, not a YAGNI violation. Questions 4 and 5 both use a live interrupt to test whether the candidate's stated position holds up under one sharp, specific follow-up — a data-store defense that collapses under "what access pattern would flip this" and a coding approach that collapses under "how would you know which day" are the same underlying signal: reasoning depth versus a plausible-sounding first pass.

## Remediation Recommendations

- Weak Q1 → re-read [Query Planning and EXPLAIN ANALYZE](../../handbook/databases/query-planning-and-explain-analyze.md).
- Weak Q2 → re-read [Data Modelling and Explicit Join Tables](../../handbook/databases/data-modelling-and-explicit-join-tables.md).
- Weak Q3 → re-read [DDD Tactical Design — Aggregates](../../handbook/architecture/ddd-tactical-design-aggregates.md) and [Distributed Transactions: Saga and Outbox](../../handbook/system-design/distributed-transactions-saga-and-outbox.md).
- Weak Q4 → re-read [Storage Selection Trade-offs](../../handbook/system-design/storage-selection-tradeoffs.md).
- Weak Q5 → re-read [Coding Interview Communication Protocol](../../interview-playbook/coding/coding-interview-communication-protocol.md) and redo the kata under a timer, starting from an index-based stack.
- Weak Q6 → re-read [Production Incident Narratives](../../behavioral-handbook/04-production-incident-narratives.md).
- Any dimension scored below Senior (4) overall → retake this mock in full after remediation.
