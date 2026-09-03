---
title: "Flashcards: Cloud Cost and Scaling Economics"
slug: cloud-cost-and-scaling-economics
document_type: flashcard-deck
domain: cloud
topic_id: T-1007
canonical: ../handbook/cloud/cloud-cost-and-scaling-economics.md
last_updated: 2026-08-06
---

# Flashcards: Cloud Cost and Scaling Economics

**Canonical chapter:** [`syllabus/15-cloud/cloud-cost-and-scaling-economics.md`](../syllabus/15-cloud/cloud-cost-and-scaling-economics.md)

## Card: What the commitment spectrum trades

**Prompt:**
What does the on-demand → reserved → spot spectrum trade for a lower price?

**Answer:**
Flexibility/guarantee — reserved requires a term commitment, spot accepts sudden reclamation risk.

**Why it matters:**
The right choice depends on demand predictability, not just per-unit price.

**Common trap:**
Choosing purely by discount percentage without considering the workload's tolerance for the trade-off.

**Related:**
[Definition and Purpose](../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#definition-and-purpose)

## Card: What a reservation should be sized to

**Prompt:**
What should a capacity reservation be sized to — peak or steady baseline?

**Answer:**
The confirmed steady baseline, never peak — reserving peak means paying the committed rate for capacity unused most of the day, which can cost more than correctly autoscaling.

**Why it matters:**
A real, computable cost mistake this chapter's arithmetic demonstrates directly.

**Common trap:**
Sizing a reservation to peak demand "to be safe."

**Related:**
[Internal Implementation](../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#internal-implementation)

## Card: When autoscaling doesn't save money

**Prompt:**
When does autoscaling NOT save meaningful money?

**Answer:**
When demand is genuinely flat/steady — the savings come specifically from the peak/trough demand gap, which a flat workload doesn't have.

**Why it matters:**
Prevents applying autoscaling as a default assumed-cost-win regardless of demand shape.

**Common trap:**
Treating autoscaling as inherently cost-reducing.

**Related:**
[Core Concepts](../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#core-concepts)
