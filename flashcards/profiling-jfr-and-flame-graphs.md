---
title: "Flashcards: Profiling, JFR, and Flame Graphs"
slug: profiling-jfr-and-flame-graphs
document_type: flashcard-deck
domain: performance
topic_id: T-1202
canonical: ../handbook/performance/profiling-jfr-and-flame-graphs.md
last_updated: 2026-09-02
---

# Flashcards: Profiling, JFR, and Flame Graphs

**Canonical chapter:** [`handbook/performance/profiling-jfr-and-flame-graphs.md`](../handbook/performance/profiling-jfr-and-flame-graphs.md)

## Card: What does flame graph width mean?

**Prompt:**
In a flame graph, does a taller stack or a wider frame indicate a hotspot?

**Answer:**
A wider frame. Height only encodes call depth; width encodes how frequently that frame appeared across all sampled stacks — the real signal for where time is spent.

**Why it matters:**
Misreading height as significance is one of the most common flame-graph interpretation mistakes.

**Common trap:**
Assuming the deepest part of the stack is automatically the hotspot.

**Related:**
[handbook/performance/profiling-jfr-and-flame-graphs.md](../handbook/performance/profiling-jfr-and-flame-graphs.md)

## Card: Profiling vs. intuition, proven

**Prompt:**
What did this chapter's own real profiling run find, and why does it matter?

**Answer:**
An innocuous autoboxing call (`Long.valueOf`) consumed more real CPU samples (719) than a method deliberately written to be an O(n²) hotspot (88, plus 153 for its own downstream string-allocation cost). It matters because it's real, reproducible proof that code-review intuition about "which code looks slow" is an unreliable substitute for an actual profile.

**Why it matters:**
This is the entire justification for profiling as a discipline, demonstrated concretely rather than asserted.

**Common trap:**
Assuming a profile will simply confirm whatever code already looks suspicious.

**Related:**
[handbook/performance/profiling-jfr-and-flame-graphs.md](../handbook/performance/profiling-jfr-and-flame-graphs.md)

## Card: JFR vs. async-profiler

**Prompt:**
When would you reach for async-profiler instead of JFR?

**Answer:**
When you need better native-frame visibility — JIT compilation threads, native library calls — that JFR's default configuration doesn't always capture as completely, or when you need even lower overhead than JFR provides. JFR's advantage is being built into the JDK with zero install and production-safe overhead for continuous use.

**Why it matters:**
Treating the two tools as interchangeable misses each one's real, specific strength.

**Common trap:**
Defaulting to whichever tool is more familiar without considering the actual diagnostic need.

**Related:**
[handbook/performance/profiling-jfr-and-flame-graphs.md](../handbook/performance/profiling-jfr-and-flame-graphs.md)
