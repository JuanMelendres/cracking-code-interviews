---
title: "Week 9 Resources"
week: 9
last_reviewed: 2026-07-29
---

# Week 9 Resources

Classified by authority: **PRIMARY**, **BOOK**, **TOOL**, **SECONDARY**.

---

## T-401/T-402 — Java Memory Model & volatile

| Source | Type | Note |
|---|---|---|
| [Java Language Specification §17.4 — Memory Model](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.4) | PRIMARY | |
| [JSR-133: Java Memory Model and Thread Specification Revision (FAQ)](https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html) | PRIMARY | Written by the JMM's own authors |
| OpenJDK 21.0.12 | TOOL | Produced the real visibility-failure demonstration; see `practice/java/week-09/concurrency-fundamentals/` |

## T-406 — Executors & Thread Pool Sizing

| Source | Type | Note |
|---|---|---|
| [`ThreadPoolExecutor` documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html) | PRIMARY | |
| OpenJDK 21.0.12 | TOOL | Produced the real unbounded-queue and bounded-queue-with-rejection demonstrations; see `practice/java/week-09/executors/` |

## T-409 — Deadlock, Race Conditions & Thread Diagnostics

| Source | Type | Note |
|---|---|---|
| [Java Language Specification §17.1 — Synchronization](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.1) | PRIMARY | |
| [`ThreadMXBean` documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.management/java/lang/management/ThreadMXBean.html) | PRIMARY | |
| OpenJDK 21.0.12 | TOOL | Produced the real thread-state, deadlock-detection, and race-condition-measurement demonstrations; see `practice/java/week-09/deadlock-diagnostics/` and `concurrency-fundamentals/` |

## T-410 — Virtual Threads

| Source | Type | Note |
|---|---|---|
| [JEP 444: Virtual Threads](https://openjdk.org/jeps/444) | PRIMARY | |
| [Java SE documentation — Virtual Threads guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html) | PRIMARY | |
| OpenJDK 21.0.12 | TOOL | Produced the real scale and pinning demonstrations; see `practice/java/week-09/virtual-threads/` |

## T-303/T-306 — GC Fundamentals & Log Analysis

| Source | Type | Note |
|---|---|---|
| [Oracle — G1 Garbage Collector tuning guide](https://docs.oracle.com/en/java/javase/21/gctuning/garbage-first-g1-garbage-collector1.html) | PRIMARY | |
| [JEP 248: Make G1 the Default Garbage Collector](https://openjdk.org/jeps/248) | PRIMARY | |
| OpenJDK 21.0.12, `-Xlog:gc*` | TOOL | Produced the real captured GC log; see `practice/java/week-09/gc/` |

## General

| Source | Type | Note |
|---|---|---|
| `00-project/knowledge-architecture-blueprint.md` §5.9-5.10 | PRIMARY | The Concurrency/JVM condensed dossier entries this pack implements |
| `00-project/learning-roadmap.md` §4 (Week 9) | PRIMARY | Full Week 9 (Plan B) checkpoint spec this pack implements |
| `CHANGELOG.md` errata register | PRIMARY | Source of the volatile/thread-state errata corrected this week |
