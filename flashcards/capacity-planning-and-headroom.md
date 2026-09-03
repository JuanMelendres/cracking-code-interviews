---
title: "Flashcards: Capacity Planning & Headroom"
slug: capacity-planning-and-headroom
document_type: flashcard-deck
domain: performance
topic_id: T-1208
canonical: ../handbook/performance/capacity-planning-and-headroom.md
last_updated: 2026-09-01
---

# Flashcards: Capacity Planning & Headroom

**Canonical chapter:** [`syllabus/16-performance-jvm/capacity-planning-and-headroom.md`](../syllabus/16-performance-jvm/capacity-planning-and-headroom.md)

## Card: Little's Law and why it's broadly useful

**Prompt:**
What does Little's Law state, and what makes it broadly useful for capacity planning?

**Answer:**
`L = λW` (average number in system = throughput × average time-in-system). It holds for any stable queueing system regardless of arrival distribution or server count, so any one quantity can be reasoned about from the other two.

**Why it matters:**
Gives a single, distribution-agnostic formula for reasoning about capacity from whichever two of the three quantities (arrival rate, time-in-system, concurrency) are actually known.

**Common trap:**
Assuming a queueing relationship like this only holds under specific, idealized arrival patterns rather than any stable system.

**Related:**
[handbook/performance/capacity-planning-and-headroom.md](../syllabus/16-performance-jvm/capacity-planning-and-headroom.md)

## Card: Why a healthy-looking throughput graph can hide saturation

**Prompt:**
Why can a throughput dashboard look "healthy" during a real capacity incident?

**Answer:**
At saturation, throughput flattens at the system's maximum service rate — it cannot show demand beyond that ceiling, so a flat or slowly-rising throughput line can actually indicate the system is already maxed out, not that load is under control.

**Why it matters:**
A flat throughput graph is exactly the false-reassurance signal a capacity-limited system produces during an incident, so reading it correctly is the difference between diagnosing saturation and missing it.

**Common trap:**
Treating stable or flat throughput as evidence the system is handling load comfortably rather than checking whether it has simply hit its ceiling.

**Related:**
[handbook/performance/capacity-planning-and-headroom.md](../syllabus/16-performance-jvm/capacity-planning-and-headroom.md)

## Card: Why utilization-to-latency is non-linear near saturation

**Prompt:**
Why is utilization-to-latency non-linear near saturation?

**Answer:**
Wait time grows roughly as `ρ/(1-ρ)` as utilization `ρ` approaches 1 — the denominator shrinks toward zero, so small increases in utilization near the ceiling produce disproportionately large latency increases.

**Why it matters:**
Explains why headroom near full utilization is disproportionately valuable — the same percentage-point increase in utilization costs far more latency near saturation than it does at low utilization.

**Common trap:**
Extrapolating latency linearly from utilization instead of accounting for the `ρ/(1-ρ)` blowup near the ceiling.

**Related:**
[handbook/performance/capacity-planning-and-headroom.md](../syllabus/16-performance-jvm/capacity-planning-and-headroom.md)
