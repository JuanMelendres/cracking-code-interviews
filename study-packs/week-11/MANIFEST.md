---
title: "Week 11 Study Pack — Manifest"
week: 11
plan: B
last_reviewed: 2026-07-31
---

# Week 11 Study Pack — Manifest

**Topics:** T-1101, T-1103, T-1104, T-1201, T-1204, T-1205, T-1206 · **Plan:** B, Testing, Observability, Performance
**Files:** 12 (+ this manifest) · **Total words:** 7,573 (real count, `wc -w` over all 12 files; updated 2026-07-31 — files 06, 08, 09, 10, README trimmed of redundant prose and worked-example padding, no canonical-chapter link applicable since they're practice/mock/design/checklist content, not duplicated topic explanation)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, schedule, exit criteria | 723 |
| 2 | `01-test-strategy-and-test-doubles.md` | T-1101/1103 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/testing/test-strategy-and-test-doubles.md` | 567 |
| 3 | `02-integration-testing-against-real-dependencies.md` | T-1104 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/testing/integration-testing-against-real-dependencies.md` | 617 |
| 4 | `03-percentiles-tail-latency-and-coordinated-omission.md` | T-1204 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md` | 580 |
| 5 | `04-logging-metrics-tracing-and-opentelemetry.md` | T-1205 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/performance/logging-metrics-tracing-and-opentelemetry.md` | 550 |
| 6 | `05-performance-methodology-and-slo-error-budgets.md` | T-1201/1206 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/performance/performance-methodology-and-slo-error-budgets.md` | 620 |
| 7 | `06-java-coding-practice.md` | 15-problem mixed review, all compiled and run | 1,013 |
| 8 | `07-flashcards.md` | 16 cards | 525 |
| 9 | `08-week-11-mock-behavioral.md` | 45-min behavioral mock, full 6-question set + retrofit checklist | 617 |
| 10 | `09-design-exercise-metrics-monitoring-system.md` | Full six-phase design | 1,104 |
| 11 | `10-week-11-checklist.md` | Day-by-day checklist | 323 |
| 12 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 334 |

---

## Verification

| Item | Status |
|---|---|
| Java — Unit testing (Mockito) | **Executed.** JUnit 5 console launcher + Mockito 5.11.0 (plain Maven Central jars, no Maven/Gradle). 3/3 tests pass, including `verify(gateway, times(N))` interaction assertions distinguishing retry-then-succeed, permanent-failure, and no-retry-needed cases. Source: `practice/java/week-11/testing/` |
| Java + SQL — Integration testing | **Executed.** Real Postgres 16 (Docker), real JDBC `INSERT ... RETURNING` + `SELECT`, 1/1 test passes. Testcontainers library itself not used (dependency tree doesn't fit this repo's plain-jar convention) — stated explicitly in `02-integration-testing-against-real-dependencies.md` §4; the technique (real, ephemeral, Docker-provisioned dependency) is identical to what Testcontainers automates. Source: `practice/java/week-11/testing/` |
| Java — Coordinated omission | **Executed.** Real 100,000-request simulation, same seed, two measurement methodologies: closed-loop p99=500ms vs. open-loop p99=830ms, p90 10ms vs. 380ms. Source: `practice/java/week-11/percentiles/` |
| Java — OpenTelemetry tracing | **Executed.** Real OpenTelemetry SDK, 4 real spans (1 root, 2 children, 1 grandchild), confirmed sharing one `traceId` with distinct `spanId`s. Source: `practice/java/week-11/tracing/` |
| Java — Error budget | **Executed.** Real 30-day simulation, 60M total requests, a simulated day-17 incident measured at ~14% of the entire monthly budget consumed in one day. Source: `practice/java/week-11/error-budget/` |
| Java — Coding (mixed review) | **Executed.** `27/27` assertions pass across 15 problems, none repeating a problem solved in Weeks 1–10. Source: `practice/java/week-11/mixed-review/` |
| USE/RED applied to prior artifacts | **Conceptual, applied to already-real artifacts.** Rather than new demo code, `05-performance-methodology-and-slo-error-budgets.md` §3 applies USE to Week 9's real GC log and RED to Week 8's real consumer-group demo — stated as the deliberate choice it is, per the roadmap's own "vocabulary retrofit" framing for this week |
| Interview statistics | None invented anywhere in this pack |

## Errata / defects addressed this week

None. `CHANGELOG.md`'s errata register has no open items scoped to testing, observability, or performance methodology.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs, real JUnit 5 + Mockito + Postgres 16 + OpenTelemetry SDK). One deliberate technique substitution is stated explicitly rather than glossed over — see `02-integration-testing-against-real-dependencies.md` §4 (direct Docker/JDBC orchestration instead of the Testcontainers library itself). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
