---
title: "Flashcards: System Design Method and Estimation"
slug: system-design-method-and-estimation
document_type: flashcard-deck
domain: system-design
topic_id: T-801
canonical: ../handbook/system-design/system-design-method-and-estimation.md
last_updated: 2026-08-06
---

# Flashcards: System Design Method and Estimation

**Canonical chapter:** [`handbook/system-design/system-design-method-and-estimation.md`](../handbook/system-design/system-design-method-and-estimation.md)

## Card: The six phases in order

**Prompt:**
Name the six phases, in order.

**Answer:**
Clarify, Estimate, API, Data, Architecture, Bottlenecks.

**Why it matters:**
The repeatable procedure this entire topic exists to teach.

**Common trap:**
Skipping straight to Architecture without the preceding phases.

**Related:**
[Core Concepts](../handbook/system-design/system-design-method-and-estimation.md#core-concepts)

## Card: Why estimate before architecture

**Prompt:**
Why estimate before designing the architecture?

**Answer:**
So every architectural decision (e.g., "we need a cache") is justified by a specific number, not reflex.

**Why it matters:**
The core ordering discipline that makes a design defensible under challenge.

**Common trap:**
Designing architecture first and retrofitting justification afterward.

**Related:**
[Mental Model](../handbook/system-design/system-design-method-and-estimation.md#mental-model)

## Card: The most important estimation assumption

**Prompt:**
What's the single most important assumption to state explicitly in a QPS estimate?

**Answer:**
The peak-to-average ratio — the architecture must be sized to peak, not average.

**Why it matters:**
The number most directly determining whether the architecture is actually adequate under real load.

**Common trap:**
Sizing an architecture to average load rather than peak.

**Related:**
[Internal Implementation](../handbook/system-design/system-design-method-and-estimation.md#internal-implementation)

## Card: The most commonly skipped phase

**Prompt:**
What's the most commonly skipped phase, and why does it matter?

**Answer:**
Phase 6, bottlenecks — it's the phase most directly testing production judgment, and skipping it under time pressure is a scored gap.

**Why it matters:**
Directly tied to the production-judgment signal disproportionately weighted at Senior/Staff level.

**Common trap:**
Letting the architecture phase consume all remaining time.

**Related:**
[Production Scenarios](../handbook/system-design/system-design-method-and-estimation.md#production-scenarios)
