---
title: "Flashcards: Native Memory, Direct Buffers, and Off-Heap"
slug: native-memory-direct-buffers-and-off-heap
document_type: flashcard-deck
domain: jvm
topic_id: T-311
canonical: ../handbook/jvm/native-memory-direct-buffers-and-off-heap.md
last_updated: 2026-08-06
---

# Flashcards: Native Memory, Direct Buffers, and Off-Heap

**Canonical chapter:** [`syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md`](../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md)

## Card: Does -Xmx bound total JVM process memory

**Prompt:**
Does `-Xmx` bound a JVM process's total memory usage?

**Answer:**
No — it bounds only the Java heap; thread stacks, metaspace, code cache, and direct buffers all live outside it, with their own separate budgets.

**Why it matters:**
The recurring reason a container can OOMKill a JVM whose `-Xmx` sits comfortably under the container limit.

**Common trap:**
Treating `-Xmx` as an effective ceiling on the JVM process's total memory footprint.

**Related:**
[handbook/jvm/native-memory-direct-buffers-and-off-heap.md](../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md)

## Card: Why direct buffers are faster for I/O

**Prompt:**
Why are direct buffers faster for I/O specifically?

**Answer:**
OS I/O calls need a fixed, stable memory address; heap memory can move during GC, requiring a copy to native memory before I/O — direct buffers are already in a fixed native location, eliminating that copy.

**Why it matters:**
The precise mechanism, not just "direct buffers are faster," behind a common NIO performance choice.

**Common trap:**
Describing direct buffers as generically faster without naming the GC-movement/copy-elimination mechanism.

**Related:**
[handbook/jvm/native-memory-direct-buffers-and-off-heap.md](../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md)

## Card: What tool shows direct-buffer memory when a heap dump won't

**Prompt:**
What tool would show direct-buffer memory usage, when a heap dump won't?

**Answer:**
Native Memory Tracking (`jcmd VM.native_memory summary`) or JMX's `BufferPoolMXBean` — both report direct-buffer memory specifically, unlike heap-focused tools.

**Why it matters:**
Names the specific diagnostic tool for exactly the memory category a standard heap dump can't see.

**Common trap:**
Relying only on heap-dump analysis to diagnose memory growth that's actually coming from direct buffers.

**Related:**
[handbook/jvm/native-memory-direct-buffers-and-off-heap.md](../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md)
