---
title: "Week 5 Study Pack — Manifest"
week: 5
last_reviewed: 2026-07-29
---

# Week 5 Study Pack — Manifest

**Topics:** T-907, T-908, T-809, T-807, T-1503 · **Plan:** A (Interview Emergency Sprint) · default workload 20h
**Files:** 12 (+ this manifest) · **Total words:** 6,073 (real count, `wc -w` over all 12 files; updated 2026-08-04 after `09-design-exercise-payment-processing.md` was slimmed to a per-phase summary + link, per the new `architecture-atlas/payment-processing-system.md` — see `CHANGELOG.md`)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, schedule, exit criteria | 725 |
| 2 | `01-microservice-decomposition.md` | T-907/908 — slimmed to a per-section summary + link; full chapter now canonical at `syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md` | 599 |
| 3 | `02-idempotency.md` | T-809 — slimmed to a per-section summary + link; full chapter now canonical at `syllabus/11-system-design/idempotency.md` | 617 |
| 4 | `03-cap-and-consistency.md` | T-807 — slimmed to a per-section summary + link; full chapter now canonical at `syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md` | 590 |
| 5 | `04-java-coding-practice.md` | LC 380, 706, 622, all compiled and run | 674 |
| 6 | `05-flashcards.md` | 14 cards | 377 |
| 7 | `06-decomposition-analysis-deliverable.md` | Template + worked example with genuine counter-argument | 827 |
| 8 | `07-story-scope-reframing.md` | T-1503 — theory slimmed to a summary + link to `syllabus/20-interview-preparation/behavioral/03-scope-impact-and-influence-framing.md`; reframe worksheets for Stories 1, 4, 7, 8 unchanged | 565 |
| 9 | `08-week-5-behavioral-mock.md` | 45-min, 6-question behavioral round | 297 |
| 10 | `09-design-exercise-payment-processing.md` | Slimmed to a per-phase summary + link; full design now canonical at `architecture-atlas/payment-processing-system.md` | 340 |
| 11 | `10-week-5-checklist.md` | Day-by-day checklist | 266 |
| 12 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 181 |

---

## Verification

| Item | Status |
|---|---|
| Java — design coding | **Executed.** OpenJDK 21.0.12. `23/23` assertions pass, including a deliberately forced hash collision and the full LC 622 errata-fix verification. Source: `practice/java/week-05/design-coding/` |
| Java — idempotency mechanism | **Executed.** Real PostgreSQL 16, real concurrent threads racing on the same idempotency key: exactly 1 charge performed for 2 simultaneous duplicate requests, both receiving the identical stored result; real TTL-based recovery from a simulated crashed in-progress attempt. Source: `practice/java/week-05/idempotency/` |
| Interview statistics | None invented anywhere in this pack |
| Production examples | The `decomposition-analysis.md` and payment-processing worked examples are clearly-labeled illustrative constructions, not presented as real incidents |

## Errata / defects addressed this week

| # | Defect (from `00-project/knowledge-base-audit.md`) | Status |
|---|---|---|
| — | Circular Queue: `size` field declared but never used; `Front()`/`Rear()`/`isEmpty()`/`isFull()` missing | **Fixed and verified here** — `04-java-coding-practice.md`, `practice/java/week-05/design-coding/src/MyCircularQueue.java` |

## Scope note

The roadmap's Track B lists LC 155 alongside 380/706/622 this week; LC 155 (Min Stack) was already implemented and verified in Week 2 and is not repeated — see `practice/java/week-05/design-coding/README.md` for the explicit note on this deviation.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` and `psql` runs). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
