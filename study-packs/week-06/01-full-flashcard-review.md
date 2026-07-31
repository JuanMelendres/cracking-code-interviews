---
title: "Full Flashcard Review — Weeks 1–5"
week: 6
last_reviewed: 2026-07-31
---

# Full Flashcard Review — Weeks 1–5

**72 cards, consolidated.** For each, cover the answer, answer aloud from memory, then check. Mark ✅ or ❌ in the Result column. Every ❌ goes onto `02-weak-list-repair.md` — don't re-read the answer and move on; the point of this pass is finding what didn't stick.

## Table of Contents

- [Week 1 (12 cards) — Hexagonal architecture, database indexes](#week-1-12-cards)
- [Week 2 (14 cards) — Query plans, data modelling, aggregates, storage, trade-off narration](#week-2-14-cards)
- [Week 3 (16 cards) — Spring transactions, isolation levels, design method](#week-3-16-cards)
- [Week 4 (16 cards) — Caching, distributed failure modes, API design](#week-4-16-cards)
- [Week 5 (14 cards) — Decomposition, idempotency, CAP](#week-5-14-cards)

---

## Week 1 (12 cards)

| # | Question | Result |
|---|---|---|
| 1 | What is a port, in hexagonal architecture? | ☐ |
| 2 | What is an adapter? | ☐ |
| 3 | State the Dependency Rule. | ☐ |
| 4 | Where does a repository interface live? | ☐ |
| 5 | Cost of hexagonal architecture — name it honestly. | ☐ |
| 6 | When should you NOT use hexagonal architecture? | ☐ |
| 7 | B+Tree lookup path — one sentence. | ☐ |
| 8 | Leftmost-prefix rule, precisely. | ☐ |
| 9 | What proves an index-only scan happened, in `EXPLAIN` output? | ☐ |
| 10 | Seq-scan-wins condition — name the mechanism, not a percentage. | ☐ |
| 11 | Clustered vs non-clustered index, by engine. | ☐ |
| 12 | The LRU cache bug — name the exact missing operation. | ☐ |

Full answers: `study-packs/week-01/08-flashcards.md`.

## Week 2 (14 cards)

| # | Question | Result |
|---|---|---|
| 1 | What does an estimate-vs-actual row mismatch in `EXPLAIN ANALYZE` usually mean? | ☐ |
| 2 | Why can't a plain B-Tree index serve `WHERE UPPER(col) = ?`? | ☐ |
| 3 | Nested loop vs hash join — the deciding factor? | ☐ |
| 4 | What can't a plain many-to-many join table store? | ☐ |
| 5 | The real trigger for an explicit join entity, precisely? | ☐ |
| 6 | What is an aggregate root? | ☐ |
| 7 | What decides aggregate boundaries? | ☐ |
| 8 | How many repositories does an aggregate get? | ☐ |
| 9 | First question in storage selection? | ☐ |
| 10 | Hidden cost of polyglot persistence? | ☐ |
| 11 | Name the four beats of trade-off narration, in order. | ☐ |
| 12 | Which beat does the named interview feedback specifically target? | ☐ |
| 13 | What's an ADR? | ☐ |
| 14 | Why can't a values-only monotonic stack solve LC 739 (Daily Temperatures)? | ☐ |

Full answers: `study-packs/week-02/08-flashcards.md`.

## Week 3 (16 cards)

| # | Question | Result |
|---|---|---|
| 1 | Why does self-invocation break `@Transactional`? | ☐ |
| 2 | Does a checked exception roll back a `@Transactional` method by default? | ☐ |
| 3 | Real use case for `REQUIRES_NEW`? | ☐ |
| 4 | `REQUIRES_NEW`'s specific deadlock risk? | ☐ |
| 5 | Is `readOnly = true` guaranteed to prevent writes? | ☐ |
| 6 | Production cost of an HTTP call inside a transaction? | ☐ |
| 7 | Difference between a lost update and write skew? | ☐ |
| 8 | Does REPEATABLE READ prevent write skew? | ☐ |
| 9 | What does SERIALIZABLE do differently? | ☐ |
| 10 | What must application code do to safely use SERIALIZABLE? | ☐ |
| 11 | Name the six design-method phases, in order. | ☐ |
| 12 | Why estimate before architecture? | ☐ |
| 13 | Most important assumption to state explicitly in a QPS estimate? | ☐ |
| 14 | Most commonly skipped design-method phase, and why it matters? | ☐ |
| 15 | LC 98's classic trap? | ☐ |
| 16 | Why is LC 235's BST-specific LCA faster than the general binary-tree LCA? | ☐ |

Full answers: `study-packs/week-03/05-flashcards.md`.

## Week 4 (16 cards)

| # | Question | Result |
|---|---|---|
| 1 | How does cache/database disagreement typically happen? | ☐ |
| 2 | What happens to the database when the entire cache dies at peak? | ☐ |
| 3 | Name three cache-stampede fixes. | ☐ |
| 4 | Three hot-key mitigations? | ☐ |
| 5 | Why is a network timeout ambiguous? | ☐ |
| 6 | Precisely how do retries amplify an outage? | ☐ |
| 7 | What structurally fixes the retry-safety problem? | ☐ |
| 8 | What structurally prevents split-brain corruption? | ☐ |
| 9 | Why does OFFSET pagination get slower with depth? | ☐ |
| 10 | What does keyset pagination give up for its flat cost at any depth? | ☐ |
| 11 | Is PUT idempotent? Is POST? | ☐ |
| 12 | Three-color DFS states, and why not just visited/unvisited? | ☐ |
| 13 | Kahn's algorithm's cycle-detection signal? | ☐ |
| 14 | Union-Find's two optimizations, named? | ☐ |
| 15 | What's the trap in LC 133 (Clone Graph) that a cycle exposes? | ☐ |
| 16 | Why does single-flight caching not just relocate the latency problem? | ☐ |

Full answers: `study-packs/week-04/05-flashcards.md`.

## Week 5 (14 cards)

| # | Question | Result |
|---|---|---|
| 1 | What's the actual test for where to draw a service boundary? | ☐ |
| 2 | Two services need one transaction — what does that signal? | ☐ |
| 3 | Name a concrete signal that two services should be merged back. | ☐ |
| 4 | Should a 4-engineer team default to microservices? | ☐ |
| 5 | What does an idempotency key actually protect against? | ☐ |
| 6 | What coordinates concurrent duplicate idempotent requests correctly? | ☐ |
| 7 | Why is a TTL necessary on an idempotency-key mechanism? | ☐ |
| 8 | Correct client behavior when a response never arrives? | ☐ |
| 9 | Does CAP apply outside of an actual network partition? | ☐ |
| 10 | What does a CP system do during a partition? | ☐ |
| 11 | What does an AP system do during a partition? | ☐ |
| 12 | Should one consistency model apply uniformly across a whole system? | ☐ |
| 13 | What's the `h ^ (h >>> 16)` technique for? | ☐ |
| 14 | What was the exact audited Circular Queue defect? | ☐ |

Full answers: `study-packs/week-05/05-flashcards.md`.

---

## After the pass

Count your ❌ items. Transfer every one to `02-weak-list-repair.md` verbatim, with the source week noted — Tuesday's entire session is built from exactly this list, nothing else.
