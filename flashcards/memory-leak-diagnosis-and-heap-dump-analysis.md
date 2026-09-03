---
title: "Flashcards: Memory Leak Diagnosis and Heap Dump Analysis"
slug: memory-leak-diagnosis-and-heap-dump-analysis
document_type: flashcard-deck
domain: jvm
topic_id: T-307
canonical: ../handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md
last_updated: 2026-08-06
---

# Flashcards: Memory Leak Diagnosis and Heap Dump Analysis

**Canonical chapter:** [`syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md`](../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md)

## Card: What makes an object a Java-specific leak

**Prompt:**
What makes an object a "leak" in Java specifically, as opposed to a native-language leak?

**Answer:**
It's still reachable from a GC root — an accidental reference, not missing/dangling memory; the GC behaves correctly given what it can see.

**Why it matters:**
The precise reframing that makes "the GC didn't collect it" the wrong mental model for a Java leak.

**Common trap:**
Describing a Java memory leak as a garbage-collector failure rather than an accidental live reference.

**Related:**
[handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md](../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md)

## Card: What :live adds to jmap -histo

**Prompt:**
What does `:live` add to `jmap -histo:live` that plain `jmap -histo` lacks?

**Answer:**
It forces a GC before counting, so the histogram reflects genuinely-reachable objects, not garbage that just hasn't been collected yet.

**Why it matters:**
Prevents misreading a pre-collection snapshot as evidence of a leak.

**Common trap:**
Running `jmap -histo` without `:live` and treating the raw counts as proof of a leak.

**Related:**
[handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md](../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md)

## Card: Distinguishing a real leak from a warming cache

**Prompt:**
How do you distinguish a real leak from a warming cache using histogram sampling?

**Answer:**
Sample 3+ times spaced apart — a real leak's count never plateaus; a warming cache's count does.

**Why it matters:**
The concrete diagnostic procedure that turns "memory looks high" into an actual leak-or-not determination.

**Common trap:**
Concluding a leak from a single histogram snapshot instead of a trend across multiple spaced samples.

**Related:**
[handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md](../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md)
