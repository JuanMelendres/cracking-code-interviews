---
title: "Week 4 Study Pack — Manifest"
week: 4
last_reviewed: 2026-07-29
---

# Week 4 Study Pack — Manifest

**Topics:** T-804, T-909, T-803, T-1504, T-1207 · **Plan:** A (Interview Emergency Sprint) · default workload 20h
**Files:** 11 (+ this manifest) · **Total words:** 9,911 (real count, `wc -w` over all 11 files)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, schedule, exit criteria | 688 |
| 2 | `01-caching-strategies.md` | T-804 — full chapter, real cache-stampede reproduction | 2,196 |
| 3 | `02-distributed-failure-modes.md` | T-909 — full chapter, real retry-storm and fencing-token reproductions | 1,979 |
| 4 | `03-api-design.md` | T-803 — full chapter, real OFFSET-vs-keyset pagination measurement | 1,508 |
| 5 | `04-java-coding-practice.md` | 5 graph problems, all compiled and run | 938 |
| 6 | `05-flashcards.md` | 16 cards | 466 |
| 7 | `06-failure-modes-deliverable.md` | `failure-modes.md` template + fully worked example | 604 |
| 8 | `07-week-4-mock-interview.md` | 45-min full design round | 257 |
| 9 | `08-design-exercise-news-feed.md` | Full six-phase design, caching + fan-out mandatory | 720 |
| 10 | `09-week-4-checklist.md` | Day-by-day checklist | 318 |
| 11 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 237 |

---

## Verification

| Item | Status |
|---|---|
| Java — graph problems | **Executed.** OpenJDK 21.0.12. `14/14` assertions pass. Source: `practice/java/week-04/graphs/` |
| Java — cache stampede | **Executed.** 50 genuinely concurrent threads; naive = 50 database calls, single-flight fix = 1. Source: `practice/java/week-04/failure-modes/CacheStampedeDemo.java` |
| Java — retry storm | **Executed.** Real thread-pool-backed simulation: no retry 4/12 succeeded, retry-no-backoff still 4/12 at 2.3x load and 3x elapsed time, retry-with-backoff 12/12 at 2.0x load. Source: `RetryStormDemo.java` |
| Java — fencing tokens | **Executed.** Real reproduction of split-brain data corruption without fencing, and correct rejection with it. Source: `FencingTokenDemo.java` |
| SQL — pagination | **Executed.** Real PostgreSQL 16, 2M-row table: OFFSET at depth 1M measured ~3,000x slower than an equivalent-depth keyset query. Source: `practice/sql/week-04/` |
| Interview statistics | None invented anywhere in this pack |
| Production examples | The `failure-modes.md` worked example is a clearly-labeled illustrative construction, not presented as a real incident |

## Errata / defects addressed this week

None from `00-project/knowledge-base-audit.md`'s register — this week's content (caching, distributed failure modes, API design) had **zero coverage** in the original audited knowledge base, so this pack is entirely new material rather than a correction of existing (wrong) content.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` and `psql` runs). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
