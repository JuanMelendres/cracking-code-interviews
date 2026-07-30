---
title: "Week 11 Resources"
week: 11
last_reviewed: 2026-07-29
---

# Week 11 Resources

Classified by authority: **PRIMARY**, **BOOK**, **TOOL**, **SECONDARY**.

---

## T-1101/T-1103 — Test Strategy & Test Doubles

| Source | Type | Note |
|---|---|---|
| [Martin Fowler — TestPyramid](https://martinfowler.com/bliki/TestPyramid.html) | PRIMARY | |
| [Mockito documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html) | PRIMARY | |
| JUnit 5 console-standalone + Mockito 5.11.0 (Maven Central plain jars) | TOOL | Produced the real unit-test run; see `practice/java/week-11/testing/` |

## T-1104 — Integration Testing

| Source | Type | Note |
|---|---|---|
| [Testcontainers documentation](https://testcontainers.com/) | PRIMARY | |
| [Testcontainers — JUnit 5 Quickstart](https://java.testcontainers.org/quickstart/junit_5_quickstart/) | PRIMARY | |
| Postgres 16 (Docker) + JDBC (Maven Central plain jar) | TOOL | Produced the real integration-test run against a live database; see `practice/java/week-11/testing/` |

## T-1204 — Percentiles & Coordinated Omission

| Source | Type | Note |
|---|---|---|
| [Gil Tene — "How NOT to Measure Latency"](https://www.infoq.com/presentations/latency-response-time/) | PRIMARY | Original coordinated-omission talk |
| [HdrHistogram documentation](http://hdrhistogram.org/) | PRIMARY | |
| OpenJDK 21.0.12 | TOOL | Produced the real closed-loop vs. open-loop percentile measurement; see `practice/java/week-11/percentiles/` |

## T-1205 — Logging, Metrics, Tracing

| Source | Type | Note |
|---|---|---|
| [OpenTelemetry documentation — Traces](https://opentelemetry.io/docs/concepts/signals/traces/) | PRIMARY | |
| [OpenTelemetry Java SDK](https://github.com/open-telemetry/opentelemetry-java) | TOOL | Produced the real 4-span distributed trace; see `practice/java/week-11/tracing/` |

## T-1201/T-1206 — Performance Methodology & SLI/SLO

| Source | Type | Note |
|---|---|---|
| [Google SRE Book — Service Level Objectives](https://sre.google/sre-book/service-level-objectives/) | PRIMARY | |
| [Google SRE Workbook — Implementing SLOs](https://sre.google/workbook/implementing-slos/) | PRIMARY | |
| [Brendan Gregg — The USE Method](https://www.brendangregg.com/usemethod.html) | PRIMARY | |
| OpenJDK 21.0.12 | TOOL | Produced the real 30-day error-budget simulation; see `practice/java/week-11/error-budget/` |

## General

| Source | Type | Note |
|---|---|---|
| `00-project/knowledge-architecture-blueprint.md` §5.10, ch. 11/12 overviews | PRIMARY | The testing/performance chapter entries this pack implements |
| `00-project/learning-roadmap.md` §4 (Week 11) | PRIMARY | Full Week 11 (Plan B) spec this pack implements |
