---
title: "Flashcards: Azure and GCP for Backend Engineers"
slug: azure-and-gcp-for-backend-engineers
document_type: flashcard-deck
domain: 15-cloud
topic_id: T-2404
canonical: ../syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md
last_updated: 2026-09-11
---

# Flashcards: Azure and GCP for Backend Engineers

**Canonical chapter:** [`syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md`](../syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md)

## Card: The one genuinely portable category

**Prompt:**
Which cloud category is genuinely the same technology across AWS, Azure, and GCP, rather than three analogous-but-different services?

**Answer:**
Kubernetes — EKS, AKS, and GKE are three managed control planes for the same portable technology, unlike S3/Blob Storage/Cloud Storage, which are three separate, non-portable implementations of the same concept.

**Why it matters:**
A team fluent in Kubernetes faces a genuinely smaller lift switching providers than a team built on proprietary services (Lambda, DynamoDB, Cosmos DB).

**Common trap:**
Assuming every service category is equally portable across providers.

**Related:**
[Azure and GCP for Backend Engineers](../syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md)

## Card: The Azure AD name change

**Prompt:**
What is Azure Active Directory called today, and why does citing the old name matter in an interview?

**Answer:**
Microsoft Entra ID, renamed in 2023. Citing "Azure AD" signals stale, un-refreshed cloud knowledge to an interviewer at a Microsoft-centric organization.

**Why it matters:**
A real, verified (not assumed) fact — checked live against Microsoft's own documentation before this chapter cited it.

**Common trap:**
Using outdated service names from memorized training data without verifying current status.

**Related:**
[Azure and GCP for Backend Engineers](../syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md)

## Card: GCP's split NoSQL story

**Prompt:**
Does GCP have one direct DynamoDB equivalent?

**Answer:**
No — GCP splits NoSQL into Firestore (document-oriented, app-facing) and Bigtable (wide-column, huge-scale analytical/time-series workloads). Neither alone is a full DynamoDB equivalent.

**Why it matters:**
A genuine, non-cosmetic difference a migration or multi-cloud decision must account for — the kind of detail that separates real trade-off understanding from a memorized mapping table.

**Common trap:**
Naming a single GCP service as "the DynamoDB of GCP" without acknowledging the split.

**Related:**
[Azure and GCP for Backend Engineers](../syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md)
