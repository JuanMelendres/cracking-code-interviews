---
title: "Flashcards: JVM Memory Layout and Runtime Regions"
slug: jvm-memory-layout-and-runtime-regions
document_type: flashcard-deck
domain: jvm
topic_id: T-301
canonical: ../handbook/jvm/jvm-memory-layout-and-runtime-regions.md
last_updated: 2026-08-06
---

# Flashcards: JVM Memory Layout and Runtime Regions

**Canonical chapter:** [`handbook/jvm/jvm-memory-layout-and-runtime-regions.md`](../handbook/jvm/jvm-memory-layout-and-runtime-regions.md)

## Card: Does -Xmx control total JVM memory usage

**Prompt:**
Does `-Xmx` control total JVM memory usage?

**Answer:**
No — only the heap. Metaspace, thread stacks, and the JIT code cache are all sized independently.

**Why it matters:**
The precise reason a container OOMKill can occur even when `-Xmx` sits well under the container's memory limit.

**Common trap:**
Treating `-Xmx` as the sole determinant of the JVM's total process memory footprint.

**Related:**
[handbook/jvm/jvm-memory-layout-and-runtime-regions.md](../handbook/jvm/jvm-memory-layout-and-runtime-regions.md)

## Card: What replaced PermGen in Java 8

**Prompt:**
What replaced PermGen in Java 8, and what changed?

**Answer:**
Metaspace — native-memory-backed rather than a fixed heap region, effectively unbounded unless `-XX:MaxMetaspaceSize` caps it.

**Why it matters:**
Explains why an uncapped metaspace can grow unexpectedly under class-generation-heavy workloads (e.g., many dynamic proxies).

**Common trap:**
Assuming metaspace behaves like PermGen — a fixed-size heap region with its own OOM ceiling by default.

**Related:**
[handbook/jvm/jvm-memory-layout-and-runtime-regions.md](../handbook/jvm/jvm-memory-layout-and-runtime-regions.md)

## Card: Is thread-stack capacity affected by heap size

**Prompt:**
Is thread-stack capacity affected by heap size?

**Answer:**
No — measured directly, recursion depth scaled from 1,479 to 413,005 purely from changing `-Xss`, with heap size held constant throughout.

**Why it matters:**
A real, measured confirmation that stack depth and heap sizing are independent levers, not the same knob.

**Common trap:**
Increasing `-Xmx` to try to fix a `StackOverflowError`, when `-Xss` is the actual relevant flag.

**Related:**
[handbook/jvm/jvm-memory-layout-and-runtime-regions.md](../handbook/jvm/jvm-memory-layout-and-runtime-regions.md)
