---
title: "T-1007 · Cloud Cost and Scaling Economics"
topic_id: T-1007
domain: Cloud
tier: Staff
iwi: 5.90
prerequisites: []
unlocks: []
week: 15
last_reviewed: 2026-07-31
canonical: ../../handbook/cloud/cloud-cost-and-scaling-economics.md
---

# T-1007 · Cloud Cost and Scaling Economics

**IWI 5.90 · Staff tier**

**Canonical chapter:** [Cloud Cost and Scaling Economics](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md). This file is the Week 15 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every number behind this summary is real arithmetic against clearly-labeled, illustrative unit prices — the calculation method, not the specific dollar figures, is the transferable skill.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Reserved-vs-on-demand and over-provisioning, worked](#3-reserved-vs-on-demand-and-over-provisioning-worked)
4. [Reserving the wrong number, worked](#4-reserving-the-wrong-number-worked)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

Cloud pricing models sit on a spectrum trading commitment/flexibility for discount: on-demand, reserved, spot. → [Definition and Purpose](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#definition-and-purpose).

## 2. Why it exists

The right pricing model and scaling strategy are workload-shape-dependent decisions, not universal defaults. → [Definition and Purpose](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#definition-and-purpose).

## 3. Reserved-vs-on-demand and over-provisioning, worked

Worked: reserving a confirmed steady 20-instance baseline saves 40% ($7,008/year) versus on-demand. Statically provisioning for a peak of 20 instead of autoscaling to the actual peak/trough shape wastes $10,220/year (58%). → [Internal Implementation](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#internal-implementation) has the full calculation.

## 4. Reserving the wrong number, worked

Worked: reserving 20 instances (matching peak, not the steady baseline) costs $10,512/year — $3,212/year MORE than correctly autoscaling on-demand for the same peak/trough demand shape. → [Internal Implementation](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#internal-implementation) has the full calculation.

## 5. Trade-offs

A reservation's discount only pays off if the committed capacity is actually used; autoscaling's savings come specifically from the peak/trough gap, not from being inherently cheaper. → [Trade-offs](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#trade-offs).

## 6. Interview questions

1. Walk me through the actual math for whether we should reserve or stay on-demand for this workload.
2. When would autoscaling NOT save money?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#interview-questions).

## 7. Common mistakes

Sizing a reservation to peak demand instead of the genuinely steady baseline; assuming autoscaling always saves money regardless of demand shape. → [Common Mistakes](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#common-mistakes).

## 8. Staff-level discussion

State the assumption, show the arithmetic, identify what would have to be true for it to hold — the same four-beat trade-off structure applied to a dollar-denominated decision. → [Staff-Level Discussion](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#interview-answer-framework).

## 9. Summary

Reserving the confirmed steady baseline captures a real discount; reserving peak demand instead can cost more than correctly autoscaling on-demand — a real, computable mistake this chapter's arithmetic demonstrates directly. → [Summary](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#practice-exercises) and [Solutions](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md#solutions).

## 14. Additional Reading

- AWS's own cost-aware architecture framing (Well-Architected Framework)

## 15. Official References

- [AWS EC2 Pricing](https://aws.amazon.com/ec2/pricing/)
