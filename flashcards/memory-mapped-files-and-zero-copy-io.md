---
title: "Flashcards: Memory-Mapped Files and Zero-Copy I/O"
slug: memory-mapped-files-and-zero-copy-io
document_type: flashcard-deck
domain: 16-performance-jvm
topic_id: T-2419
canonical: ../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md
last_updated: 2026-09-20
---

# Flashcards: Memory-Mapped Files and Zero-Copy I/O

**Canonical chapter:** [`syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md`](../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md)

## Card: Sequential vs. random access — which one does memory mapping actually help?

**Prompt:**
Does memory-mapped I/O offer a dramatic advantage for sequential file access, random access, or both?

**Answer:**
Primarily random access. Measured directly: sequential full-file scans showed near-parity with ordinary buffered reads; random, scattered access showed a real ~28x speedup.

**Why it matters:**
Memory mapping is not a universal file-I/O win — proposing it without checking the access pattern reveals a shallow understanding.

**Common trap:**
Assuming memory mapping is always faster than ordinary reads regardless of access pattern.

**Related:**
[Core Concepts](../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md#core-concepts)

## Card: What's the real mechanism behind memory mapping's random-access speedup?

**Prompt:**
Why does memory mapping give such a large advantage specifically for random access?

**Answer:**
Ordinary positional reads pay a real syscall for every single access, regardless of how little data is transferred. Memory-mapped access pays kernel cost only on a genuine page fault (first touch of a not-yet-resident page) — subsequent accesses to the same page are ordinary memory reads.

**Why it matters:**
Explains precisely why the advantage concentrates in scattered-access patterns, not sequential ones.

**Common trap:**
Describing the advantage vaguely as "mmap is faster" without the syscall-overhead mechanism.

**Related:**
[Internal Implementation](../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md#internal-implementation)

## Card: Which real production system relies on memory-mapped file I/O?

**Prompt:**
Name a real production system whose I/O design relies on the memory-mapping mechanism this chapter measures.

**Answer:**
Kafka — brokers serve log-segment reads to many independently-progressing consumers, an access pattern closer to random/scattered than sequential, making memory mapping's real advantage directly applicable.

**Why it matters:**
Grounds the concept in a real, citable production system rather than only a synthetic benchmark.

**Common trap:**
Being unable to name a concrete real-world system using this mechanism when asked.

**Related:**
[Core Concepts](../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md#core-concepts)
