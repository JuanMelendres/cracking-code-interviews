---
title: "Flashcards: JVM Startup Performance — CDS, AppCDS, and GraalVM Native Image"
slug: jvm-startup-performance-cds-and-native-image
document_type: flashcard-deck
domain: 16-performance-jvm
topic_id: T-2416
canonical: ../syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md
last_updated: 2026-09-18
---

# Flashcards: JVM Startup Performance — CDS, AppCDS, and GraalVM Native Image

**Canonical chapter:** [`syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md`](../syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md)

## Card: Why did AppCDS show no measurable improvement in this chapter's own measurement?

**Prompt:**
This chapter measured AppCDS on a tiny real application and found no measurable startup improvement. Is AppCDS broken, or is this expected?

**Answer:**
Expected. AppCDS's mechanism skips re-parsing/re-verifying already-loaded classes — a tiny application with only a handful of classes has almost nothing for that mechanism to save. The benefit is genuinely proportional to class-loading volume.

**Why it matters:**
Commonly-cited AppCDS percentage improvements come from framework-heavy apps loading thousands of classes — not a universal, fixed number.

**Common trap:**
Quoting a memorized AppCDS percentage without checking whether it applies to the application at hand.

**Related:**
[Core Concepts](../syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md#core-concepts)

## Card: What does native-image's "closed-world assumption" actually break?

**Prompt:**
What does GraalVM native-image's closed-world assumption mean in practice, and what commonly breaks because of it?

**Answer:**
Every reachable class, method, and reflectively-accessed member must be knowable at build time. Naive reflection and dynamic proxies (common in Spring/Jackson-style frameworks) break unless explicitly configured, because the AOT compiler can't discover them by running the code the way a JIT profiler can.

**Why it matters:**
This is the real cost behind native-image's speed — not a minor footnote.

**Common trap:**
Treating a native-image reflection failure as a bug in native-image rather than the closed-world assumption working as designed.

**Related:**
[Core Concepts](../syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md#core-concepts)

## Card: Real measured startup-time and memory numbers, plain JVM vs. native-image

**Prompt:**
This chapter measured a real plain JVM against a real GraalVM native-image binary for the same tiny HTTP service. What were the real, measured differences in startup time and peak memory?

**Answer:**
Startup-to-first-response: ~136ms (plain JVM) vs. ~9ms (native-image) — ~15x faster. Peak RSS: ~50.1MB (plain JVM) vs. ~15.7MB (native-image) — ~3.2x smaller.

**Why it matters:**
Two real, independently measured wins, not a single number — worth citing both in an interview answer.

**Common trap:**
Citing only startup time and forgetting the memory-footprint dimension.

**Related:**
[Internal Implementation](../syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md#internal-implementation)
