---
title: "Flashcards: GC Roots, Reachability, and Reference Strength"
slug: gc-roots-reachability-and-reference-strength
document_type: flashcard-deck
domain: jvm
topic_id: T-303
canonical: ../handbook/jvm/gc-roots-reachability-and-reference-strength.md
last_updated: 2026-08-06
---

# Flashcards: GC Roots, Reachability, and Reference Strength

**Canonical chapter:** [`handbook/jvm/gc-roots-reachability-and-reference-strength.md`](../handbook/jvm/gc-roots-reachability-and-reference-strength.md)

## Card: What GC roots are, concretely

**Prompt:**
What are GC roots, concretely?

**Answer:**
The fixed set of reference locations treated as inherently alive — active thread stacks, static fields, JNI references, and a few JVM-internal roots.

**Why it matters:**
Grounds "reachability" in a concrete, enumerable set rather than a vague abstraction.

**Common trap:**
Describing GC roots only abstractly ("things the GC starts from") without naming the concrete categories.

**Related:**
[handbook/jvm/gc-roots-reachability-and-reference-strength.md](../handbook/jvm/gc-roots-reachability-and-reference-strength.md)

## Card: WeakReference vs SoftReference

**Prompt:**
What's the key behavioral difference between `WeakReference` and `SoftReference`?

**Answer:**
Weak clears immediately upon otherwise-unreachability, with no memory-pressure consideration; soft is retained under normal conditions and only cleared under real memory pressure (guaranteed before `OutOfMemoryError`).

**Why it matters:**
The precise distinction between the two most commonly conflated reference-strength types.

**Common trap:**
Treating `WeakReference` and `SoftReference` as interchangeable, pressure-aware caching mechanisms.

**Related:**
[handbook/jvm/gc-roots-reachability-and-reference-strength.md](../handbook/jvm/gc-roots-reachability-and-reference-strength.md)

## Card: Why PhantomReference.get() never returns the referent

**Prompt:**
Why can't `PhantomReference.get()` ever return the referent?

**Answer:**
A deliberate design choice preventing object resurrection through the cleanup mechanism itself — phantom references exist purely for post-collection notification via a `ReferenceQueue`, not for renewed access.

**Why it matters:**
Explains the specific design intent behind `PhantomReference`, distinct from the other reference-strength types.

**Common trap:**
Assuming `PhantomReference` behaves like `WeakReference` but with a different clearing timing, rather than a fundamentally different access contract.

**Related:**
[handbook/jvm/gc-roots-reachability-and-reference-strength.md](../handbook/jvm/gc-roots-reachability-and-reference-strength.md)
