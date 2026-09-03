---
title: "Flashcards: JVM Flags and Container Ergonomics"
slug: jvm-flags-and-container-ergonomics
document_type: flashcard-deck
domain: jvm
topic_id: T-312
canonical: ../handbook/jvm/jvm-flags-and-container-ergonomics.md
last_updated: 2026-08-06
---

# Flashcards: JVM Flags and Container Ergonomics

**Canonical chapter:** [`syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md`](../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md)

## Card: Does container-aware ergonomics affect only heap sizing

**Prompt:**
Does container-aware JVM ergonomics affect only heap sizing, or CPU-scaled defaults too?

**Answer:**
Both — memory detection feeds a percentage-based heap cap; CPU detection feeds GC thread counts and similar concurrency-scaled defaults.

**Why it matters:**
Prevents underestimating how broadly container detection changes JVM runtime behavior beyond just `-Xmx`.

**Common trap:**
Assuming container-aware ergonomics only affects heap sizing, missing the CPU-driven GC-thread-count effect.

**Related:**
[handbook/jvm/jvm-flags-and-container-ergonomics.md](../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md)

## Card: The default heap-cap percentage of container memory

**Prompt:**
What percentage of detected container memory becomes the heap cap by default?

**Answer:**
25% (`-XX:MaxRAMPercentage`, default 25.0).

**Why it matters:**
The specific default number behind "the JVM sizes itself against the container automatically."

**Common trap:**
Assuming the JVM claims the full container memory limit as heap by default.

**Related:**
[handbook/jvm/jvm-flags-and-container-ergonomics.md](../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md)

## Card: What availableProcessors() reflects in a container

**Prompt:**
Does `Runtime.availableProcessors()` reflect the host's real core count or the container's CPU limit?

**Answer:**
The container's cgroup CPU quota — measured directly, a 10-core host reported "2 available" or "6 available" depending on the container's `--cpus` setting.

**Why it matters:**
Explains why thread-pool sizing logic based on `availableProcessors()` behaves correctly in containers without extra configuration.

**Common trap:**
Assuming `availableProcessors()` reports the physical host's core count regardless of container CPU limits.

**Related:**
[handbook/jvm/jvm-flags-and-container-ergonomics.md](../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md)
