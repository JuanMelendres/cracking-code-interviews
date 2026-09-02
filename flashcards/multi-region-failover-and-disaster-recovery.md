---
title: "Flashcards: Multi-Region Failover and Disaster Recovery"
slug: multi-region-failover-and-disaster-recovery
document_type: flashcard-deck
domain: system-design
topic_id: T-814
canonical: ../handbook/system-design/multi-region-failover-and-disaster-recovery.md
last_updated: 2026-09-02
---

# Flashcards: Multi-Region Failover and Disaster Recovery

**Canonical chapter:** [`handbook/system-design/multi-region-failover-and-disaster-recovery.md`](../handbook/system-design/multi-region-failover-and-disaster-recovery.md)

## Card: RPO vs. RTO

**Prompt:**
What's the difference between RPO and RTO?

**Answer:**
RPO (Recovery Point Objective) is how much data loss is acceptable, measured as a time window. RTO (Recovery Time Objective) is how much downtime is acceptable before service resumes.

**Why it matters:**
Every DR tier trades cost against these two numbers specifically — naming them precisely is the entry point to a credible answer on this topic.

**Common trap:**
Confusing the two, or answering "as fast/safe as possible" instead of a real number.

**Related:**
[Definition and Purpose](../handbook/system-design/multi-region-failover-and-disaster-recovery.md#definition-and-purpose)

## Card: Split-brain's real cause

**Prompt:**
What actually causes split-brain?

**Answer:**
Promoting a new primary while the old primary is still alive and reachable by some clients (merely network-partitioned, not dead) — both then accept writes independently, and their histories diverge.

**Why it matters:**
This chapter reproduced it directly: two real nodes, each with a real committed row the other doesn't have.

**Common trap:**
Assuming "unreachable" means "dead" — a network partition proves neither.

**Related:**
[Failure Modes and Debugging](../handbook/system-design/multi-region-failover-and-disaster-recovery.md#failure-modes-and-debugging)

## Card: Fencing / STONITH

**Prompt:**
What does fencing actually guarantee, and why is it non-optional?

**Answer:**
A real, verifiable guarantee that the old primary cannot accept writes, established *before* promoting a new primary — not a hope, an assumption, or a best-effort network check.

**Why it matters:**
This chapter's demo showed the exact contrast: an unfenced failover produced real split-brain; a fenced one (`docker pause`, a real analog of STONITH) had the identical write attempt refused before it ever reached the database.

**Common trap:**
Treating fencing as a nice-to-have that can be skipped to fail over faster — that speed is exactly what causes split-brain.

**Related:**
[Internal Implementation](../handbook/system-design/multi-region-failover-and-disaster-recovery.md#internal-implementation)
