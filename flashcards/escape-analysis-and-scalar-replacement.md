---
title: "Flashcards: Escape Analysis and Scalar Replacement"
slug: escape-analysis-and-scalar-replacement
document_type: flashcard-deck
domain: jvm
topic_id: T-309
canonical: ../handbook/jvm/escape-analysis-and-scalar-replacement.md
last_updated: 2026-08-06
---

# Flashcards: Escape Analysis and Scalar Replacement

**Canonical chapter:** [`syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md`](../syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md)

## Card: Does escape analysis eliminate GC cost too

**Prompt:**
Does escape analysis eliminate only allocation cost, or GC cost too?

**Answer:**
Both — an object that's scalar-replaced is never actually allocated on the heap at all, so it produces zero garbage and needs zero future collection.

**Why it matters:**
Prevents underselling escape analysis as merely an allocation-speed optimization rather than a GC-load reducer.

**Common trap:**
Describing scalar replacement as only making allocation faster, missing that it eliminates the garbage entirely.

**Related:**
[handbook/jvm/escape-analysis-and-scalar-replacement.md](../syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md)

## Card: Does escape analysis apply to interpreted code

**Prompt:**
Does escape analysis apply to interpreted (not-yet-compiled) code?

**Answer:**
No — it's a JIT-compilation-time optimization; interpreted execution allocates every object for real regardless of whether it would theoretically qualify once compiled.

**Why it matters:**
Explains why the same code can allocate real objects early in a run and then stop once the JIT compiles it.

**Common trap:**
Assuming escape analysis benefits apply uniformly from the very first execution of a method.

**Related:**
[handbook/jvm/escape-analysis-and-scalar-replacement.md](../syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md)

## Card: The measured GC-pause contrast

**Prompt:**
What real, measured GC-pause-count contrast demonstrates escape analysis's effect?

**Answer:**
Zero GC pauses across 600 million allocation attempts with escape analysis enabled, versus 362 real pauses for the identical workload with it explicitly disabled.

**Why it matters:**
A concrete, measured number rather than an abstract claim of benefit.

**Common trap:**
Citing escape analysis's benefit only qualitatively, without a measured before/after comparison.

**Related:**
[handbook/jvm/escape-analysis-and-scalar-replacement.md](../syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md)
