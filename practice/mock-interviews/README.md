---
title: "Mock Interviews — Index"
document_type: mock-interview-index
status: draft
last_updated: 2026-08-11
---

# Mock Interviews

Full-round mock interview simulations per `CLAUDE.md`'s Mock Interview Standard: target role, duration, competencies assessed, an interviewer opening script, a hard-separated candidate section, an evaluator section with ideal-answer outlines and pass/borderline/fail signals per question, a scoring rubric, a debrief guide, and remediation recommendations pointing back to the relevant canonical `handbook/` chapters.

## A note on scope

The programme's study packs contain 12 real, already-written mock interview files across Weeks 1, 2, 4, 7, 8, and 13–19, each a timed technical round exercising that week's specific topics. Seven of these (Weeks 13–19) already follow a candidate/evaluator structure closely matching the Mock Interview Standard; the remaining five (Weeks 1, 2, 4, 7, 8) use an earlier candidate-script/interviewer-script format without per-question pass/fail signals. This deliverable elevates each into a standalone file here, adding the competencies table, interviewer opening script, debrief guide, and remediation recommendations the source files don't yet have, while keeping every question, ideal answer, and pass/fail signal exactly as the source states it — no fabricated content.

**Batch 1 covers 3 of the 7 Weeks 13–19 rounds** (the closest to the Standard already, plus the pool this began with) — the first bounded batch of 12, per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation. Remaining rounds are built in subsequent batches.

## Rounds

| # | Round | Competencies | Source |
|---|---|---|---|
| 1 | [Java Core Technical Round](java-core-technical-round.md) | Streams/parallel cost, equals/hashCode, generics/erasure, exception design, immutability | `study-packs/week-13/08-week-13-mock-interview.md` |
| 2 | [Collections Technical Round](collections-technical-round.md) | HashMap internals, ConcurrentHashMap atomicity, BlockingQueue backpressure, ArrayList/LinkedList trade-offs | `study-packs/week-14/08-week-14-mock-interview.md` |
| 3 | [Cloud & Infrastructure Round](cloud-infrastructure-round.md) | Kubernetes diagnostics, requests/limits, rollout guarantees, cost economics, storage selection | `study-packs/week-15/08-week-15-mock-interview.md` |

## How this relates to other deliverables

- `study-packs/` — the source material each round is elevated from; the original study-pack file remains in place, referenced from each round's front matter.
- `handbook/` — the canonical chapters each round's competencies map to; remediation recommendations point back here.
- `interview-playbook/system-design/` and `interview-playbook/coding/` — live-delivery discipline for running any of these rounds under real interview conditions.
- `cheat-sheets/` and `flashcards/` — pre-round review material for the topics each mock interview covers.
