---
title: "Week 3 Study Pack — Manifest"
week: 3
checkpoint: true
last_reviewed: 2026-07-29
---

# Week 3 Study Pack — Manifest

**Topics:** T-503, T-504, T-505, T-611, T-801, T-802 · **Plan:** A (Interview Emergency Sprint) · default workload 20h · **⚑ Checkpoint week**
**Files:** 11 (+ this manifest) · **Total words:** 6,717 (real count, `wc -w` over all 11 files; updated 2026-08-04 after `08-design-exercise-ride-hailing.md` was slimmed to a per-phase summary + canonical-entry links, per the new `architecture-atlas/ride-hailing-dispatch-system.md` — see `CHANGELOG.md`)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, schedule, checkpoint summary, exit criteria | 867 |
| 2 | `01-transactions-and-propagation.md` | T-503/504/505 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/spring/transactional-proxy-mechanics-and-propagation.md` | 894 |
| 3 | `02-isolation-levels-and-write-skew.md` | T-611 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/databases/isolation-levels-and-concurrency-anomalies.md` | 708 |
| 4 | `03-system-design-method.md` | T-801/802 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/system-design/system-design-method-and-estimation.md` | 596 |
| 5 | `04-java-coding-practice.md` | 6 tree problems, all compiled and run | 1,033 |
| 6 | `05-flashcards.md` | 16 cards | 421 |
| 7 | `06-week-3-checkpoint-mock.md` | 60-min combined checkpoint round | 489 |
| 8 | `07-week-3-checkpoint-rubric.md` | Six-dimension pass/fail rubric | 735 |
| 9 | `08-design-exercise-ride-hailing.md` | Slimmed to a per-phase summary + link; full worked exercise now canonical at `architecture-atlas/ride-hailing-dispatch-system.md` | 393 |
| 10 | `09-week-3-checklist.md` | Day-by-day checklist | 350 |
| 11 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 228 |

---

## Verification

| Item | Status |
|---|---|
| Java — trees | **Executed.** OpenJDK 21.0.12. `11/11` assertions pass. Source: `practice/java/week-03/trees/`. Reproduce: see `practice/java/week-03/trees/README.md` |
| Java — Spring transaction demos | **Executed.** Spring Framework 6.1.14, plain jars from Maven Central (no Maven/Gradle), H2 in-memory + real PostgreSQL 16 for the `readOnly` contrast demo. All 6 demos produce their claimed real output. Source: `practice/java/week-03/spring-demos/`. Reproduce: `./fetch-deps.sh` then see the README |
| SQL — write skew | **Executed.** Real PostgreSQL 16, two genuinely concurrent `psql` sessions. Reproduced the anomaly at `REPEATABLE READ` and its prevention at `SERIALIZABLE`, including the real `ERROR: could not serialize access...` message. Source and output: `practice/sql/week-03/` |
| Interview statistics | None invented anywhere in this pack |
| Production examples | The audit-log and pool-exhaustion demos are constructed, clearly-labeled illustrations of real mechanisms — not presented as accounts of a specific real production incident |
| Source classification markers | `[V]`/`[J]`/`[H]`/`[E-PG]` convention applied where relevant |

## Checkpoint status

This is the first gated checkpoint in the programme (`00-project/learning-roadmap.md` §3, Week 3). Pass bar and evidence anchors: `07-week-3-checkpoint-rubric.md`. The checkpoint itself must be run and scored by the user against a real (self- or partner-) mock — it cannot be pre-verified the way the code/SQL artifacts above can, since it measures live delivery, not a reproducible technical claim.

## Errata / defects addressed this week

No new errata items from `00-project/knowledge-base-audit.md`'s register were closed this week (the audit's Week-3-relevant findings — thread-state and `volatile` misconceptions — are scheduled for Plan B Week 9, per `CHANGELOG.md`'s errata register, since concurrency/JVM is out of scope for the Plan A sprint). This week instead **added new verified material** absent from the original audit entirely: System Design (zero coverage previously) and transaction/isolation depth beyond the audit's single-sentence Spring/JPA rows.

## Integrity note

Every number in this manifest was computed directly (`wc -w`, real `javac`/`java`, `psql`, and Spring-context runs) this session. See `study-packs/week-01/MANIFEST.md` for why this convention exists.
