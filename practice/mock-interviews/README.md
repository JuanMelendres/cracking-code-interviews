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

**Batches 1–2 cover all 7 of the Weeks 13–19 rounds** — the ones already closest to the Standard (candidate/evaluator hard-separated, per-question pass/fail signals, scoring rubric) — two bounded batches of 12, per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation. The two JVM-internals rounds (Weeks 16 and 19) cover genuinely distinct topics with zero question overlap, cross-linked to each other for candidates taking both. Remaining: the 5 earlier-format rounds (Weeks 1, 2, 4, 7, 8), which use a candidate-script/interviewer-script structure without inline per-question pass/fail signals and require conversion into the Standard's evaluator structure, not just mechanical elevation.

## Rounds

| # | Round | Competencies | Source |
|---|---|---|---|
| 1 | [Java Core Technical Round](java-core-technical-round.md) | Streams/parallel cost, equals/hashCode, generics/erasure, exception design, immutability | `study-packs/week-13/08-week-13-mock-interview.md` |
| 2 | [Collections Technical Round](collections-technical-round.md) | HashMap internals, ConcurrentHashMap atomicity, BlockingQueue backpressure, ArrayList/LinkedList trade-offs | `study-packs/week-14/08-week-14-mock-interview.md` |
| 3 | [Cloud & Infrastructure Round](cloud-infrastructure-round.md) | Kubernetes diagnostics, requests/limits, rollout guarantees, cost economics, storage selection | `study-packs/week-15/08-week-15-mock-interview.md` |
| 4 | [JVM Internals — GC and Diagnostics Round](jvm-internals-gc-diagnostics-round.md) | G1 RSet diagnostics, memory-leak diagnosis, stack/heap independence, container heap sizing, deoptimization | `study-packs/week-16/08-week-16-mock-interview.md` |
| 5 | [Security Technical Round](security-technical-round.md) | IDOR, password hashing, RBAC-to-ABAC, SQL injection, Row-Level Security scope, envelope encryption | `study-packs/week-17/10-week-17-mock-interview.md` |
| 6 | [Testing Technical Round](testing-technical-round.md) | Percentile vs mean latency, performance-testing process, live test-first coding, contract testing, coverage vs. mutation testing, JUnit 5 architecture | `study-packs/week-18/08-week-18-mock-interview.md` |
| 7 | [JVM Internals — Concurrent GC and Native Memory Round](jvm-internals-concurrent-gc-native-memory-round.md) | Reference strength, ZGC/Shenandoah migration, safepoint logging, object-layout estimation, off-heap accounting, escape analysis | `study-packs/week-19/09-week-19-mock-interview.md` |

## How this relates to other deliverables

- `study-packs/` — the source material each round is elevated from; the original study-pack file remains in place, referenced from each round's front matter.
- `handbook/` — the canonical chapters each round's competencies map to; remediation recommendations point back here.
- `interview-playbook/system-design/` and `interview-playbook/coding/` — live-delivery discipline for running any of these rounds under real interview conditions.
- `cheat-sheets/` and `flashcards/` — pre-round review material for the topics each mock interview covers.
