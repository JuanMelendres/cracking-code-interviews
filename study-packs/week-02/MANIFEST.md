---
title: "Week 2 Study Pack — Manifest"
week: 2
last_reviewed: 2026-07-29
---

# Week 2 Study Pack — Manifest

**Topics:** T-610, T-605, T-608, T-903, T-617/T-811, T-1505, T-916 · **Plan:** A (Interview Emergency Sprint) · default workload 20h
**Files:** 13 (+ this manifest) · **Total words:** 7,908 (real count, `wc -w` over all 13 files; updated 2026-07-30 after all six T-topics were slimmed to a summary + canonical-chapter link — see `CHANGELOG.md`)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, schedule, variants, exit criteria | 858 |
| 2 | `01-query-planning-and-explain.md` | T-610 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/databases/query-planning-and-explain-analyze.md` | 732 |
| 3 | `02-data-modelling-join-tables.md` | T-605/T-608 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/databases/data-modelling-and-explicit-join-tables.md` | 663 |
| 4 | `03-ddd-tactical-aggregates.md` | T-903 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/architecture/ddd-tactical-design-aggregates.md` | 537 |
| 5 | `04-storage-selection-tradeoffs.md` | T-617/T-811 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/system-design/storage-selection-tradeoffs.md` | 484 |
| 6 | `05-trade-off-narration-and-adrs.md` | T-1505/T-916 — slimmed to a per-section summary + link; full chapter now canonical at `interview-playbook/technical-answers/trade-off-narration-and-adrs.md` | 543 |
| 7 | `06-answer-frameworks.md` | Nine-layer treatment for T-610, T-605, T-1505 | 572 |
| 8 | `07-java-coding-practice.md` | 8 problems + monotonic-stack errata, all compiled and run | 1,578 |
| 9 | `08-flashcards.md` | 14 cards | 361 |
| 10 | `09-week-2-mock-interview.md` | 30-min mock; hard-separated | 395 |
| 11 | `10-adr-exercise.md` | ADR-001 template + fully worked example | 577 |
| 12 | `11-week-2-checklist.md` | Day-by-day checklist | 331 |
| 13 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 277 |

---

## Verification

| Item | Status |
|---|---|
| Java code | **Executed.** OpenJDK 21.0.12. `21/21` assertions pass, including LC 739 (Daily Temperatures) with the corrected index-based monotonic stack. Source: `practice/java/week-02/src/*.java`. Reproduce: `cd practice/java/week-02 && mkdir -p out && javac -d out src/*.java && java -cp out Main` |
| SQL / PostgreSQL labs | **Executed.** PostgreSQL 16 via Docker, two labs: `query-plan-lab.sql` (3 real before/after diagnoses) and `many-to-many-lab.sql` (real reproduction of the price-history data-integrity bug). Source and full output: `practice/sql/week-02/`. Reproduce: see `practice/sql/week-02/README.md` |
| Interview statistics | None invented anywhere in this pack |
| Production examples | Templates with extraction prompts where personal experience is required; the ADR worked example is a fully constructed hypothetical, explicitly labeled as such, not presented as a real personal decision |
| Source classification markers | `[V]`/`[J]`/`[H]`/`[E-PG]` convention applied where relevant |

## Errata addressed this week

| # | Defect (from `00-project/knowledge-base-audit.md`) | Status |
|---|---|---|
| 6 | Monotonic stack: diagram describes indices, code (as audited) pushed values — the two are inconsistent, and a values-only implementation is structurally incapable of the required output | **Corrected and explained here** — `07-java-coding-practice.md` Day 5, `practice/java/week-02/src/StackProblems.java` |

Note: this errata is different in kind from Week 1's LRU bug — it is a documentation self-contradiction (diagram vs. described code) rather than a subtle runtime logic error, and this pack explains *why* the values-only approach is structurally impossible for this specific problem rather than constructing an artificial "buggy but compiles" version, since the honest technical claim is that such a version cannot correctly solve the problem at all.

## Integrity note

Every number in this manifest was computed directly (`wc -w`, real `javac`/`java` and `psql` runs) this session. See `study-packs/week-01/MANIFEST.md` for why this matters — the archived pre-existing manifests in `archive/pre-initialization-scaffolding/` made unverifiable claims about nonexistent files; this pack's manifest states only what was actually run and how to reproduce it.
