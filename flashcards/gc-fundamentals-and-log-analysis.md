---
title: "Flashcards: GC Fundamentals and Log Analysis"
slug: gc-fundamentals-and-log-analysis
document_type: flashcard-deck
domain: jvm
topic_id: T-306
canonical: ../handbook/jvm/gc-fundamentals-and-log-analysis.md
last_updated: 2026-08-06
---

# Flashcards: GC Fundamentals and Log Analysis

**Canonical chapter:** [`handbook/jvm/gc-fundamentals-and-log-analysis.md`](../handbook/jvm/gc-fundamentals-and-log-analysis.md)

## Card: Most common GC tuning misconception

**Prompt:**
What's the most common misconception about GC tuning?

**Answer:**
That tuning means increasing heap size — it's one lever among several and isn't always correct.

**Why it matters:**
The default instinct most candidates reach for first, and frequently the wrong one.

**Common trap:**
Proposing more heap without reading the log first.

**Related:**
[Decision Framework](../handbook/jvm/gc-fundamentals-and-log-analysis.md#decision-framework)

## Card: Rising post-GC occupancy trend

**Prompt:**
What does a rising post-GC occupancy trend across successive young collections suggest?

**Answer:**
Objects are surviving longer than expected, heading toward promotion — check for a leak or an intentionally growing cache.

**Why it matters:**
The real diagnostic signal, versus any single pause line.

**Common trap:**
Drawing a conclusion from one log line instead of the trend.

**Related:**
[Internal Implementation](../handbook/jvm/gc-fundamentals-and-log-analysis.md#internal-implementation)

## Card: Humongous allocations

**Prompt:**
What is a "humongous allocation" in G1, and why doesn't more heap fix problems it causes?

**Answer:**
An object ≥50% of a region size, handled via dedicated regions outside normal young-gen allocation — the problem is allocation pattern, not total heap volume.

**Why it matters:**
A specific, diagnosable cause of bad GC behavior that a naive "increase the heap" response doesn't address.

**Common trap:**
Treating all bad GC pauses as a sizing problem.

**Related:**
[Core Concepts](../handbook/jvm/gc-fundamentals-and-log-analysis.md#core-concepts)
