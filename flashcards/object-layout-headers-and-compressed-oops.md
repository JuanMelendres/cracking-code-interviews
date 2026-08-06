---
title: "Flashcards: Object Layout, Headers, and Compressed Oops"
slug: object-layout-headers-and-compressed-oops
document_type: flashcard-deck
domain: jvm
topic_id: T-302
canonical: ../handbook/jvm/object-layout-headers-and-compressed-oops.md
last_updated: 2026-08-06
---

# Flashcards: Object Layout, Headers, and Compressed Oops

**Canonical chapter:** [`handbook/jvm/object-layout-headers-and-compressed-oops.md`](../handbook/jvm/object-layout-headers-and-compressed-oops.md)

## Card: Does a zero-field object occupy zero memory

**Prompt:**
Does an object with zero declared fields occupy zero memory?

**Answer:**
No — every object still carries a fixed header (12-16 bytes), regardless of declared field count.

**Why it matters:**
Corrects the intuitive but wrong assumption that an "empty" object costs nothing.

**Common trap:**
Assuming a class with no fields has effectively zero per-instance memory cost.

**Related:**
[handbook/jvm/object-layout-headers-and-compressed-oops.md](../handbook/jvm/object-layout-headers-and-compressed-oops.md)

## Card: How compressed oops fits a reference in 32 bits

**Prompt:**
What mechanism does compressed oops use to represent a reference in 32 bits instead of 64?

**Answer:**
It exploits object-alignment guarantees — the low bits of any real object address are always zero and don't need to be stored, letting a 32-bit value address a wider effective range via an implicit shift/multiply.

**Why it matters:**
The specific mechanism behind a memory-saving feature many candidates can name but not explain.

**Common trap:**
Describing compressed oops as "just truncating the address" without the alignment-based shift mechanism.

**Related:**
[handbook/jvm/object-layout-headers-and-compressed-oops.md](../handbook/jvm/object-layout-headers-and-compressed-oops.md)

## Card: What happens past the compressed oops heap ceiling

**Prompt:**
What happens to reference-field memory cost when a heap grows past compressed oops' ~32GB addressability ceiling?

**Answer:**
The JVM silently falls back to full 64-bit references — every reference field's cost doubles, a real structural cost of that specific scaling decision.

**Why it matters:**
A concrete, easy-to-miss cost of scaling heap size past a specific threshold, not a generic "bigger heap costs more."

**Common trap:**
Assuming heap growth past 32GB scales memory cost linearly with no structural change to reference size.

**Related:**
[handbook/jvm/object-layout-headers-and-compressed-oops.md](../handbook/jvm/object-layout-headers-and-compressed-oops.md)
