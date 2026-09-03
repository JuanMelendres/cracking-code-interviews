---
title: "T-1006 · AWS Core Services for Backend Engineers"
topic_id: T-1006
domain: Cloud
tier: Core
iwi: 5.60
prerequisites: [T-1002]
unlocks: []
week: 15
last_reviewed: 2026-07-31
canonical: ../../handbook/cloud/aws-core-services-for-backend-engineers.md
---

# T-1006 · AWS Core Services for Backend Engineers

**IWI 5.60 · Core tier · Moderate interview frequency**

**Canonical chapter:** [AWS Core Services for Backend Engineers](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md). This file is the Week 15 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Compute and storage service selection](#3-compute-and-storage-service-selection)
4. [Database and messaging service selection](#4-database-and-messaging-service-selection)
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

AWS's core backend services cluster into compute, storage, database, and messaging, each offering a spectrum trading operational ownership, access model, or distribution shape for convenience or scale. → [Definition and Purpose](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#definition-and-purpose).

## 2. Why it exists

Each service removes one specific category of undifferentiated operational work, so a backend team can focus on what's actually differentiated. → [Definition and Purpose](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#definition-and-purpose).

## 3. Compute and storage service selection

EC2 → ECS/EKS → Lambda trades control for reduced operational ownership. S3 (objects), EBS (single-instance block storage), and EFS (shared filesystem) have genuinely different access models, not just different price points. → [Core Concepts](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#core-concepts).

## 4. Database and messaging service selection

RDS vs. DynamoDB follows the same access-pattern method as any storage decision. SQS (point-to-point, durable) and SNS (fan-out) solve different problems and are often combined. → [Core Concepts](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#core-concepts).

## 5. Trade-offs

Lambda's zero-server-management benefit comes with cold-start and execution-time-limit costs; DynamoDB's throughput/scale benefits require upfront access-pattern design. → [Trade-offs](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#trade-offs).

## 6. Interview questions

1. You migrated to DynamoDB for scale, and a new reporting feature can't be built against it. What happened, and how do you fix it?
2. When would you choose EKS over ECS, given both are "managed containers"?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#interview-questions).

## 7. Common mistakes

Choosing a compute service by reputation rather than the operational-ownership-vs-control trade-off; treating S3/EBS/EFS as interchangeable "AWS storage." → [Common Mistakes](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#common-mistakes).

## 8. Staff-level discussion

Every cloud service decision is a specific instance of the same access-pattern-first method this program applies to in-memory collections and general storage technology. → [Staff-Level Discussion](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#interview-answer-framework).

## 9. Summary

The right choice in every AWS service category follows the access-pattern-first discipline — a real-shaped scenario shows a DynamoDB migration correct for its original access pattern becoming a blocker for a later, unanticipated need. → [Summary](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#practice-exercises) and [Solutions](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#solutions).

## 14. Additional Reading

- AWS Well-Architected Framework

## 15. Official References

- [AWS documentation](https://docs.aws.amazon.com/)
