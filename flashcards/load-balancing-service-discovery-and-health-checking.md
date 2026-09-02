---
title: "Flashcards: Load Balancing, Service Discovery, and Health Checking"
slug: load-balancing-service-discovery-and-health-checking
document_type: flashcard-deck
domain: system-design
topic_id: T-805
canonical: ../handbook/system-design/load-balancing-service-discovery-and-health-checking.md
last_updated: 2026-09-02
---

# Flashcards: Load Balancing, Service Discovery, and Health Checking

**Canonical chapter:** [`handbook/system-design/load-balancing-service-discovery-and-health-checking.md`](../handbook/system-design/load-balancing-service-discovery-and-health-checking.md)

## Card: Round-robin vs. least-connections

**Prompt:**
What real, structural difference separates round-robin from least-connections?

**Answer:**
Round-robin uses no runtime signal — every backend is treated as identical. Least-connections uses a real, live in-flight-request count.

**Why it matters:**
This chapter measured a real ~4.4x cost difference (921ms vs. 208ms for the same 300-request batch) when backend request cost varies significantly.

**Common trap:**
Assuming round-robin distributes load evenly just because it distributes request count evenly.

**Related:**
[Core Concepts](../handbook/system-design/load-balancing-service-discovery-and-health-checking.md#core-concepts)

## Card: Health-check detection latency is real and bounded

**Prompt:**
How quickly does an active health checker detect a dead backend?

**Answer:**
Bounded by the check interval plus the probe timeout — never instantaneous, but a real, specific, tunable number.

**Why it matters:**
This chapter measured it directly: 206ms real detection latency for a real killed backend, with zero subsequent requests reaching it.

**Common trap:**
Saying "the load balancer handles it" without naming the actual bound.

**Related:**
[Production Scenarios](../handbook/system-design/load-balancing-service-discovery-and-health-checking.md#production-scenarios)

## Card: Active vs. passive health checking

**Prompt:**
What's the real difference between active and passive health checking, and why use both?

**Answer:**
Active checking proactively polls a health endpoint on a fixed interval, independent of real traffic. Passive checking infers health from real production request failures. Active catches problems before user impact; passive catches failure modes a synthetic probe doesn't exercise.

**Why it matters:**
Relying on only one leaves a real gap the other would have caught.

**Common trap:**
Treating them as interchangeable rather than complementary.

**Related:**
[Core Concepts](../handbook/system-design/load-balancing-service-discovery-and-health-checking.md#core-concepts)
