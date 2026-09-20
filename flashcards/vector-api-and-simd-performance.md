---
title: "Flashcards: Vector API and SIMD Performance"
slug: vector-api-and-simd-performance
document_type: flashcard-deck
domain: 16-performance-jvm
topic_id: T-2418
canonical: ../syllabus/16-performance-jvm/vector-api-and-simd-performance.md
last_updated: 2026-09-20
---

# Flashcards: Vector API and SIMD Performance

**Canonical chapter:** [`syllabus/16-performance-jvm/vector-api-and-simd-performance.md`](../syllabus/16-performance-jvm/vector-api-and-simd-performance.md)

## Card: Does HotSpot already vectorize simple loops?

**Prompt:**
Does a plain, scalar-looking Java `for` loop over a primitive array always run as scalar machine code?

**Answer:**
Not necessarily — HotSpot's C2 JIT compiler auto-vectorizes many simple, regular loops via SuperWord optimization (`-XX:+UseSuperWord`, on by default), compiling them to real SIMD instructions without any explicit API use.

**Why it matters:**
Verified directly in this chapter: the Vector API showed no measurable advantage (0.98x) over such a loop under default settings.

**Common trap:**
Assuming "scalar Java source" means "scalar machine code."

**Related:**
[Core Concepts](../syllabus/16-performance-jvm/vector-api-and-simd-performance.md#core-concepts)

## Card: Real measured Vector API speedup, with and without auto-vectorization

**Prompt:**
This chapter measured the Vector API against a scalar loop twice — once under default JVM settings, once with `-XX:-UseSuperWord`. What were the real results?

**Answer:**
Under default settings: ~0.98x (essential parity). With auto-vectorization disabled: a real ~2.2x speedup — the Vector API's true, isolated advantage.

**Why it matters:**
Shows the Vector API's real value depends entirely on whether JIT auto-vectorization already covers the loop.

**Common trap:**
Citing only one number without the auto-vectorization context.

**Related:**
[Internal Implementation](../syllabus/16-performance-jvm/vector-api-and-simd-performance.md#internal-implementation)

## Card: Why did fma() and a*b+a produce different bit-level results?

**Prompt:**
Comparing the Vector API's `fma()` against a plain scalar `a[i]*b[i] + a[i]` produced non-identical outputs in this chapter's demo. Why, and how was it fixed?

**Answer:**
A hardware FMA performs the multiply and add as one single IEEE 754 rounding step; separate `*` and `+` perform two independent roundings — a genuine, expected floating-point difference, not a bug. Fixed by using `Math.fma()` for the scalar baseline too, matching the single-rounding behavior.

**Why it matters:**
A real, easy-to-miss correctness detail when comparing numeric implementations.

**Common trap:**
Treating any bit-level mismatch as an implementation bug rather than considering rounding-mode differences.

**Related:**
[Core Concepts](../syllabus/16-performance-jvm/vector-api-and-simd-performance.md#core-concepts)
