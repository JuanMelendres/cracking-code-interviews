# Week 1 Study Pack — Manifest

**Topics:** T-901, T-609, T-1601, T-1501, T-1419 · **Plan:** A (Interview Emergency Sprint) · default workload 20h
**Files:** 13 (+ this manifest) · **Total words:** 8,774 (real count, `wc -w` over all 13 files, re-run 2026-08-04 after `03-technical-answer-framework.md` §2-§4 were slimmed to summaries + canonical-chapter links, per the new `interview-playbook/technical-answers/technical-answer-framework.md` — see `CHANGELOG.md`. §1 and §3 numbering deliberately kept stable, unchanged: `11-week-1-checklist.md` and `study-packs/week-02/06-answer-frameworks.md` cite them directly)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, daily schedule, 10/20/30h variants, exit criteria, recording protocol | 901 |
| 2 | `01-clean-hexagonal-architecture.md` | T-901 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/architecture/clean-hexagonal-architecture.md` | 934 |
| 3 | `02-database-index-fundamentals.md` | T-609 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/databases/index-structures-btree-composite-covering.md` | 882 |
| 4 | `03-technical-answer-framework.md` | T-1601 — slimmed to a per-section summary + link; full nine-layer stack now canonical at `interview-playbook/technical-answers/technical-answer-framework.md` | 474 |
| 5 | `04-coding-interview-communication.md` | T-1419 — six-phase protocol + 3 annotated failure transcripts | 718 |
| 6 | `05-star-story-workbook.md` | T-1501 — §1 slimmed to a summary + link to `behavioral-handbook/01-star-framework-and-delivery.md`; §2-§6 (worksheets, exercise) unchanged, no invented stories | 664 |
| 7 | `06-domain-purity-exercise.md` | Deliverable template + worked example + documented counter-case | 668 |
| 8 | `07-java-coding-practice.md` | 7 problems + LRU errata drill, all compiled and run | 1,246 |
| 9 | `08-flashcards.md` | 12 cards, each naming the misconception it catches | 413 |
| 10 | `09-week-1-mock-interview.md` | 20-min mock; candidate/interviewer sections hard-separated | 482 |
| 11 | `10-week-1-evaluation-rubric.md` | Six-dimension rubric with Week-1-specific evidence anchors | 635 |
| 12 | `11-week-1-checklist.md` | Day-by-day checklist with a fall-behind priority order | 431 |
| 13 | `resources.md` | Primary sources classified PRIMARY / BOOK / TOOL / SECONDARY | 325 |

---

## Verification

| Item | Status |
|---|---|
| Java code | **Executed.** OpenJDK 21.0.12 (Homebrew). `18/18` assertions pass, including a live reproduction of the LRU errata bug. Source: `practice/java/week-01/src/*.java`. Full output: `practice/java/week-01/README.md`. Reproduce: `cd practice/java/week-01 && mkdir -p out && javac -d out src/*.java && java -cp out Main` |
| SQL / PostgreSQL lab | **Executed.** PostgreSQL 16 via Docker, disposable container, 300,000-row seeded `orders` table. Every `EXPLAIN (ANALYZE, BUFFERS)` block quoted (now in the canonical chapter, `handbook/databases/index-structures-btree-composite-covering.md`) is real, not illustrative. Source: `practice/sql/week-01/index-lab.sql`. Full output: `practice/sql/week-01/index-lab-output.txt`. Reproduce: see `practice/sql/week-01/README.md` |
| Interview statistics | **None invented.** No frequency percentages, benchmark numbers, or company-specific claims anywhere in this pack |
| Production examples | Supplied as templates with extraction prompts (`01-…` §6, `06-…` §1) — never fabricated as if real |
| Personal experience / STAR stories | **Not invented.** `05-star-story-workbook.md` is blank worksheets only |
| Source classification markers | `[V]`/`[J]`/`[H]`/`[E-PG]` convention from `CONTRIBUTING.md` applied where a claim isn't self-evidently verifiable from the executed code/lab above |

## Errata addressed this week

| # | Defect (from `00-project/knowledge-base-audit.md`) | Status |
|---|---|---|
| 1 | LRU cache `put()` evicts a valid entry on an update to an existing key at capacity | **Fixed and verified here** — `07-java-coding-practice.md`, `practice/java/week-01/src/LRUCacheBuggy.java` / `LRUCacheFixed.java` |

See `CHANGELOG.md` for the full errata register (7 code defects, 6 incorrect claims); this pack closes #1 only. The remainder are scheduled for later weeks per the register.

## Integrity note

This manifest states only what was actually executed in this session and can be reproduced by running the commands above. It intentionally does not follow the format of the archived `study-packs/week-02/` and root-level manifests found in `archive/pre-initialization-scaffolding/` — those claimed specific verified file counts and test results for files that, on inspection, did not exist. Every number in this manifest was computed directly (`wc -w`, real `javac`/`java` and `psql` runs) rather than carried forward from a prior claim.
