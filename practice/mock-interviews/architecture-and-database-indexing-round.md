---
title: "Mock Interview: Architecture and Database Indexing Round (20 min)"
slug: architecture-and-database-indexing-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 20
competencies:
  - Hexagonal architecture, cold explanation and Staff-differentiating follow-up
  - B+Tree index traversal, root to heap
  - Index-added-query-slower diagnosis (two mechanisms)
  - LRU cache implementation with narrated communication phases
  - Architecture trade-off story delivery (STAR, ≤2 min)
related:
  - ../../syllabus/17-architecture/clean-hexagonal-architecture.md
  - ../../syllabus/06-databases/index-structures-btree-composite-covering.md
  - ../../syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md
  - ../../syllabus/20-interview-preparation/behavioral/05-architecture-trade-off-narration.md
source: ../../study-packs/week-01/09-week-1-mock-interview.md
official_references: []
---

# Mock Interview: Architecture and Database Indexing Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 20 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below. Elevated from `study-packs/week-01/09-week-1-mock-interview.md`. This round predates the Weeks 13-19 rounds' inline per-question pass/fail format — the source instead scores against a shared six-dimension rubric (`study-packs/week-01/10-week-1-evaluation-rubric.md`), which this file's Evaluator Section and Scoring Rubric preserve rather than replace with an invented generic scale.

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
| Hexagonal architecture, cold + Staff-differentiator follow-up | Q1–Q2 | [Clean and Hexagonal Architecture](../../syllabus/17-architecture/clean-hexagonal-architecture.md) |
| B+Tree traversal, root to heap | Q3 | [Database Index Structures — B+Tree, Composite, Covering](../../syllabus/06-databases/index-structures-btree-composite-covering.md) |
| Index-added-query-slower diagnosis | Q4 | [Database Index Structures — B+Tree, Composite, Covering](../../syllabus/06-databases/index-structures-btree-composite-covering.md) |
| LRU cache, narrated implementation | Q5 | [Coding Interview Communication Protocol](../../syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md) |
| Architecture trade-off story | Q6 | [Architecture Trade-off Narration](../../syllabus/20-interview-preparation/behavioral/05-architecture-trade-off-narration.md) |

## Interviewer Opening Script

*"This is a 20-minute round covering architecture, database indexing, one coding exercise, and one behavioral story. First question, cold, no notes: explain hexagonal architecture. Take whatever time you need for the opening, then I'll follow up."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(2 min)** "Explain hexagonal architecture" — cold, no notes. Aim for a 30-second opening, then let the follow-up determine how far to push.
2. **(3 min)** Follow-up, interviewer's choice: *"Where do JPA entities live in this model?"* or *"Would you use this on every project?"*
3. **(2 min)** "How does a B+Tree index actually find a row?" — walk it from root to heap, out loud.
4. **(3 min)** "You added an index and the query got slower. Give two distinct mechanisms."
5. **(5 min)** Implement an LRU cache (LeetCode 146), narrated per all six phases of the [Coding Interview Communication Protocol](../../syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md). Mid-implementation, expect: *"What happens if I call `put` on a key that already exists, when the cache is already full?"*
6. **(5 min)** Deliver a real architecture-decision story in under 2 minutes, then answer: *"What would you have done if the alternative had won instead?"*

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Questions 1–2 — Hexagonal architecture, cold + follow-up

**Ideal answer outline:** a 30-second opening naming ports and adapters as the mechanism separating domain logic from infrastructure concerns, delivered without running past ~45 seconds. On the follow-up, if asked about JPA entity placement: correctly places them behind an adapter, not in the domain model. If asked *"would you use this on every project?"*: a conditional answer naming the mapping-code cost against the isolation benefit — this is the Staff-differentiating question, and a confident, unconditional "yes" is a specific, scoreable weakness.
**Common weak answers:** reciting the definition with no placement reasoning; an unconditional "yes" on the "every project" follow-up with no acknowledgment of cost.
**Pass signal (rubric "4"):** sustains 4 follow-ups; states the pattern's trade-off unprompted; names a real or template-filled production example.
**Borderline signal (rubric "3"):** sustains 2 follow-ups; no unprompted production example.
**Fail signal (rubric "1–2"):** recites the definition and cannot say where a repository interface lives; or names ports/adapters correctly but the chain collapses at the "every project" question.

### Question 3 — B+Tree traversal, root to heap

**Ideal answer outline:** describes descending the B+Tree from root to leaf by key comparison, then — for a non-covering index — a second step: fetching the actual row from the heap via the leaf entry's row pointer. The two-step nature (index traversal, then heap fetch) is the part a generic "it's a binary search tree" answer misses.
**Common weak answers:** describing tree traversal alone with no heap-fetch step, or describing a generic BST/hash structure without B+Tree-specific fan-out.
**Pass signal:** names both the leaf-level traversal and the separate heap-fetch step unprompted.
**Borderline signal:** describes traversal only; names the heap-fetch step only once probed.
**Fail signal:** cannot describe the traversal mechanism at all, or conflates it with a different data structure.

### Question 4 — Index added, query got slower

**Ideal answer outline:** two distinct mechanisms — write amplification (every insert/update now also maintains the new index) and stale statistics (the planner's cost estimates no longer reflect the table's real distribution until statistics are refreshed). A candidate naming only one gets the nudge: *"what else, besides the query getting a worse plan?"*
**Common weak answers:** "the planner chose a worse plan" as the entire answer, with no named mechanism behind why.
**Pass signal:** names both write amplification and stale statistics unprompted.
**Borderline signal:** names one mechanism; reaches the second only after the nudge.
**Fail signal:** cannot name either mechanism.

### Question 5 — LRU cache, live coding

**Ideal answer outline:** a working `LinkedHashMap`- or manual-doubly-linked-list-plus-hashmap-based LRU cache, implemented in ≤20 minutes from scratch, narrating all six communication-protocol phases, stating time/space complexity unprompted. On the mid-implementation interrupt (`put` on an existing key at capacity): the correct behavior updates the existing entry's value and marks it most-recently-used *without* evicting — a candidate who evicts here has the specific errata bug this exercise is designed to expose.
**Common weak answers:** a version that evicts on `put`-to-existing-key at capacity, indicating the buggy variant was memorized rather than reasoned through.
**Pass signal (rubric "4"):** correct in ≤20 min, narrates all six phases, states complexity unprompted, and handles the existing-key-at-capacity interrupt correctly.
**Borderline signal (rubric "3"):** correct in ~30 min; narrates partially.
**Fail signal (rubric "1–2"):** cannot produce a working LRU cache, or only produces one after hints with no narration; or fails the existing-key-at-capacity interrupt.
**Staff signal (rubric "5"):** proactively tests the existing-key-at-capacity edge case *before* being asked.

### Question 6 — Architecture trade-off story

**Ideal answer outline:** a ≤2-minute STAR-structured story naming the alternative that was considered and rejected, and explicitly stating what the chosen path *cost* — not only what it gained. On the follow-up (what if the alternative had won), represents the rejected alternative in its strongest, most charitable form rather than a strawman.
**Common weak answers:** a story that rambles past 2:30 with no clear STAR structure, or a story that names a gain without naming a cost.
**Pass signal (rubric "4"):** ≤2 min; names the alternative considered; states the cost of the choice, not just the benefit.
**Borderline signal (rubric "3"):** clear STAR, quantified, first-person, but the cost isn't named unprompted.
**Fail signal (rubric "1–2"):** rambles past 2:30 with no clear STAR structure, or a loose STAR with no quantified result.
**Staff signal (rubric "5"):** represents the rejected alternative in its strongest form when asked the follow-up.

## Scoring Rubric

This round predates the per-question 1–5 scale used in later rounds. It is scored instead against the shared six-dimension rubric in [`study-packs/week-01/10-week-1-evaluation-rubric.md`](../../study-packs/week-01/10-week-1-evaluation-rubric.md) — Technical Depth (Q1–Q2), Coding (Q5), Behavioral Communication (Q6), plus Java Fluency and Production Judgment as cross-cutting dimensions evaluators should score from the same answers. Each dimension uses the 1–5 scale reproduced in the Evaluator Section above (3 = Mid, 4 = Senior, 5 = Staff). The source rubric states explicitly that Week 1 has no formal pass/fail checkpoint (the first gated checkpoint is Week 3); instead, use the six scores to identify the single weakest dimension and prioritize remediation there.

## Debrief Guide

Walk the candidate through their six dimension scores, starting with the weakest. Questions 1–2 and Question 6 share a theme worth naming explicitly: both test whether the candidate can represent a rejected option — an alternative architecture, an alternative technical decision — honestly and in its strongest form, rather than dismissing it to make the chosen path look obviously correct. Questions 4 and 5 share a different theme: both use a targeted nudge or interrupt to check whether the candidate's first-pass answer holds up, or whether it was surface-level pattern matching (naming one mechanism instead of two; a memorized-but-buggy LRU implementation).

## Remediation Recommendations

- Any Technical Depth score ≤ 2 → re-read [Clean and Hexagonal Architecture](../../syllabus/17-architecture/clean-hexagonal-architecture.md), specifically its trade-off table and "every project" framing.
- Weak Q3/Q4 answers → re-read [Database Index Structures — B+Tree, Composite, Covering](../../syllabus/06-databases/index-structures-btree-composite-covering.md).
- Weak Q5 answers, or the errata bug reproduced → re-read [Coding Interview Communication Protocol](../../syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md) and compare against the fixed reference implementation at [`practice/java/week-01/src/LRUCacheFixed.java`](../../practice/java/week-01/src/LRUCacheFixed.java).
- Weak Q6 answers → re-read [Architecture Trade-off Narration](../../syllabus/20-interview-preparation/behavioral/05-architecture-trade-off-narration.md).
- Any dimension scored below Senior (4) overall → retake this mock in full after remediation; this week has no formal pass bar, so use the retake to confirm the weakest dimension specifically improved.
