---
title: "Week 9 Study Pack — Manifest"
week: 9
plan: B
last_reviewed: 2026-07-29
---

# Week 9 Study Pack — Manifest

**Topics:** T-401, T-402, T-406, T-409, T-410, T-303, T-306 · **Plan:** B, first checkpoint since Week 6
**Files:** 12 (+ this manifest) · **Total words:** 13,923 (real count, `wc -w` over all 12 files)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, schedule, exit criteria | 738 |
| 2 | `01-java-memory-model-and-volatile.md` | T-401/402 — full chapter, real visibility-failure demo, errata corrected | 1,733 |
| 3 | `02-executors-and-thread-pool-sizing.md` | T-406 — full chapter, real unbounded/bounded queue measurements | 1,546 |
| 4 | `03-deadlock-races-and-thread-diagnostics.md` | T-409 — full chapter, real deadlock detection + race measurement, errata corrected | 1,682 |
| 5 | `04-virtual-threads.md` | T-410 — full chapter, real scale (18x) and pinning (10x) measurements | 1,684 |
| 6 | `05-gc-fundamentals-and-log-analysis.md` | T-303/306 — full chapter, real captured and read GC log | 1,898 |
| 7 | `06-java-coding-practice.md` | LC 1114/1115/1116 + LC 62/1143/416/5, all compiled and run | 1,403 |
| 8 | `07-flashcards.md` | 16 cards | 507 |
| 9 | `08-week-9-checkpoint.md` | Full 3-round loop + roadmap's own scorecard | 886 |
| 10 | `09-design-exercise-distributed-job-scheduler.md` | Full six-phase design | 1,131 |
| 11 | `10-week-9-checklist.md` | Day-by-day checklist | 371 |
| 12 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 344 |

---

## Verification

| Item | Status |
|---|---|
| Java — JMM/volatile | **Executed.** OpenJDK 21.0.12. Real visibility-failure demo (non-volatile flag: worker thread still spinning 5+ seconds after the flag was set; volatile flag: stops in 0ms), reproduced identically across 3 separate runs, not a one-off. Source: `practice/java/week-09/concurrency-fundamentals/` |
| Java — thread states, deadlock, races | **Executed.** Real six-value `Thread.State` enum printed from a running JVM (corrects the errata). Real deadlock detected via `ThreadMXBean.findDeadlockedThreads()`. Real race-condition measurement: 838,094 of 1,000,000 increments lost (83.8%) with a plain `int`, zero lost with `AtomicInteger`. Source: `practice/java/week-09/deadlock-diagnostics/`, `concurrency-fundamentals/` |
| Java — executor sizing | **Executed.** Real unbounded-queue measurement (496/500 tasks queued 200ms after submission) and real bounded-queue-with-rejection measurement (7 accepted, 13 rejected, matching `corePoolSize+queueCapacity` exactly). Source: `practice/java/week-09/executors/` |
| Java — virtual threads | **Executed.** Real scale measurement (1347ms platform-pool vs 75ms virtual-thread, 18x, identical workload) and real pinning measurement (2044ms pinned vs 206ms unpinned, ~10x, isolated from lock contention via per-task independent lock objects). Source: `practice/java/week-09/virtual-threads/` |
| Java — GC log | **Executed.** Real `-Xlog:gc*` capture from a genuine forced allocation storm (~4.9GB allocated into a 64MB heap): 4 real young-generation collections, sub-millisecond pauses, rising post-collection occupancy trend. Source: `practice/java/week-09/gc/` |
| Java — coding (concurrency) | **Executed.** LC 1114 verified across 100 randomized-scheduling trials (threads deliberately started in the wrong order every time); LC 1115/1116 verified at n=1000 with full-sequence pattern checks, not spot checks. Source: `practice/java/week-09/concurrency-coding/` |
| Java — coding (DP part 2) | **Executed.** `21/21` assertions pass, including palindrome-property + substring-membership verification (not brittle exact-string matching) for LC 5's ambiguous-answer cases. Source: `practice/java/week-09/dp-part2/` |
| Interview statistics | None invented anywhere in this pack |

## Errata / defects addressed this week

| Defect (from `CHANGELOG.md`'s errata register) | Status |
|---|---|
| Incorrect thread-lifecycle states (missing `TIMED_WAITING`) | **Fixed and verified here** — `03-deadlock-races-and-thread-diagnostics.md` §3, real `Thread.State.values()` output from a running JVM |
| `volatile` reduced to "prevents caching" instead of happens-before | **Fixed and verified here** — `01-java-memory-model-and-volatile.md` §3, real reproducing visibility-failure demo plus the corrected mechanism (compiler/JIT reordering, not CPU cache coherence) |

`CHANGELOG.md`'s errata register updated to reflect both fixes.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs, including a deliberately constrained `-Xmx64m` JVM for the GC log and a `-Djdk.virtualThreadScheduler.parallelism=2` override for the pinning demo). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
