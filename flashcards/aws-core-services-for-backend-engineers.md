---
title: "Flashcards: AWS Core Services for Backend Engineers"
slug: aws-core-services-for-backend-engineers
document_type: flashcard-deck
domain: cloud
topic_id: T-1006
canonical: ../syllabus/15-cloud/aws-core-services-for-backend-engineers.md
last_updated: 2026-08-06
---

# Flashcards: AWS Core Services for Backend Engineers

**Canonical chapter:** [`syllabus/15-cloud/aws-core-services-for-backend-engineers.md`](../syllabus/15-cloud/aws-core-services-for-backend-engineers.md)

## Card: The compute spectrum

**Prompt:**
What does the EC2 → ECS/EKS → Lambda spectrum trade off?

**Answer:**
Control for reduced operational ownership — EC2 gives full control and full ownership; Lambda gives zero server management at the cost of execution limits and a different programming model.

**Why it matters:**
The core organizing principle for AWS compute choices.

**Common trap:**
Choosing a compute service by popularity rather than this actual trade-off.

**Related:**
[Core Concepts](../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#core-concepts)

## Card: S3 vs EBS vs EFS

**Prompt:**
What's the actual access-model difference between S3, EBS, and EFS?

**Answer:**
S3 is object storage (HTTP-style API, not mounted); EBS is block storage attached to one instance at a time; EFS is a shared, network-attached filesystem multiple instances can mount simultaneously.

**Why it matters:**
Prevents treating all three as interchangeable "AWS storage."

**Common trap:**
Choosing based on price alone without matching to the actual access model needed.

**Related:**
[Core Concepts](../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#core-concepts)

## Card: SQS vs SNS

**Prompt:**
What's the difference between SQS and SNS, and why are they often combined?

**Answer:**
SQS is point-to-point durable delivery (one message, one consumer); SNS is pub/sub fan-out (one message, many subscribers). Combined via SNS fanning out to multiple SQS queues, each processed independently and durably.

**Why it matters:**
They solve different problems; a workflow needing both fan-out and durable per-consumer processing needs both services together.

**Common trap:**
Treating SQS and SNS as alternatives rather than complementary.

**Related:**
[Core Concepts](../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#core-concepts)

### Card: S3 object size limits and multipart upload

**Prompt:**
How large can a single S3 object be, and at what point are you forced to change how you upload it?

**Answer:**
An object can be up to **5 TB**, but a single `PUT` caps at **5 GB** — beyond that, multipart upload is mandatory: the object is split into parts uploaded independently and assembled server-side.

**Why it matters:**
It turns "upload a large file" from one request into an operation with its own initiate/upload/complete lifecycle. Multipart is worth using well below 5 GB anyway, because parts upload in parallel and a failed part retries on its own instead of restarting the transfer.

**Common trap:**
Forgetting that an abandoned multipart upload leaves billable incomplete parts behind until a lifecycle rule removes them.

**Related:**
[Core Concepts](../syllabus/15-cloud/aws-core-services-for-backend-engineers.md#core-concepts)
