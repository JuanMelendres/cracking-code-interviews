---
title: "Week 16 Resources"
week: 16
document_type: study-pack-resources
status: draft
last_reviewed: 2026-07-31
---

# Week 16 Resources

| Source | Type | Notes |
|---|---|---|
| [OpenJDK Wiki — G1 Garbage Collector](https://wiki.openjdk.org/display/HotSpot/G1+Garbage+Collector) | PRIMARY | G1 remembered sets, write barriers |
| [Oracle — G1 Garbage Collector tuning guide](https://docs.oracle.com/en/java/javase/21/gctuning/garbage-first-g1-garbage-collector1.html) | PRIMARY | G1 internals reference |
| [`jmap` command reference (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jmap.html) | PRIMARY | Live-object histogramming |
| [`jcmd` command reference (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jcmd.html) | PRIMARY | Heap dumps, NMT, diagnostics |
| [Eclipse Memory Analyzer (MAT) documentation](https://eclipse.dev/mat/) | PRIMARY | Heap dump GC-roots analysis |
| [The Java Virtual Machine Specification, §2.5 — Runtime Data Areas](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html#jvms-2.5) | PRIMARY | Memory layout / runtime regions |
| [Oracle — Troubleshooting Memory Leaks (Native Memory Tracking)](https://docs.oracle.com/en/java/javase/21/troubleshoot/diagnostic-tools.html) | PRIMARY | NMT tooling |
| [Java containers and the mystery of the disappearing memory](https://developers.redhat.com/articles/2022/04/19/java-17-whats-new-openjdks-container-awareness) | SECONDARY | Container-aware ergonomics history |
| [`Runtime.availableProcessors()` (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Runtime.html#availableProcessors()) | PRIMARY | Container CPU detection |
| [Java HotSpot VM Performance Enhancements (Java 21)](https://docs.oracle.com/en/java/javase/21/vm/java-hotspot-virtual-machine-performance-enhancements.html) | PRIMARY | Tiered compilation |
| [Aleksey Shipilëv — JVM Anatomy Quarks: Deoptimization](https://shipilev.net/jvm/anatomy-quarks/2-deoptimization/) | SECONDARY | Deoptimization mechanics |
| Docker 29.6.2 / `eclipse-temurin:21-jre` | TOOL | Produced the real container CPU/memory ergonomics demonstrations; see `practice/java/week-16/container-ergonomics/` |
| OpenJDK 21.0.12 | TOOL | Produced all other real demonstrations this week; see `practice/java/week-16/` |
