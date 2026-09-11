---
title: "Cheat Sheet: Azure and GCP for Backend Engineers"
slug: azure-and-gcp-for-backend-engineers
document_type: cheat-sheet
domain: 15-cloud
topic_id: T-2404
canonical: ../syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md
last_updated: 2026-09-11
---

# Azure and GCP for Backend Engineers

**Canonical chapter:** [`syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md`](../syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md)

## Core Mental Model

Azure and GCP offer the same access-model/ownership-trade-off categories AWS already teaches (compute ownership, storage access model, database fit, messaging shape, identity/network boundaries), under different brand names. Kubernetes (EKS/AKS/GKE) is the one exception — genuinely the same portable technology, not just an analogous one.

## Essential Definitions

- **Microsoft Entra ID** — Azure Active Directory's real, current name (renamed 2023) — citing "Azure AD" in an interview today is a stale reference.
- **Infrastructure Manager** — GCP's current native IaC service (Terraform-based), superseding the now-deprecated Deployment Manager.
- **Cloud Run** — GCP's managed-container service; sits at a genuinely distinct ownership point versus ECS/App Service, not a direct 1:1 swap.

## Decision Table

| Category | AWS | Azure | GCP |
|---|---|---|---|
| Raw VM | EC2 | Virtual Machines | Compute Engine |
| Managed containers | ECS | App Service / Container Apps | Cloud Run |
| Managed Kubernetes | EKS | AKS | GKE |
| Serverless functions | Lambda | Azure Functions | Cloud Functions |
| Object storage | S3 | Blob Storage | Cloud Storage |
| Managed relational DB | RDS | Azure SQL Database | Cloud SQL |
| Managed NoSQL | DynamoDB | Cosmos DB | Firestore / Bigtable |
| Point-to-point queue | SQS | Service Bus | Pub/Sub |
| Identity/access | IAM | Microsoft Entra ID | Cloud IAM |
| Native IaC | CloudFormation | ARM/Bicep | Infrastructure Manager |

## Common Pitfalls

- Citing "Azure Active Directory" instead of Microsoft Entra ID — a stale name change (2023) that reads as out-of-date knowledge.
- Treating GCP's NoSQL story as one service — it's genuinely split between Firestore (document) and Bigtable (wide-column, huge scale), not a single DynamoDB equivalent.
- Assuming every category maps 1:1 — Cloud Run sits at a distinct point on the ownership spectrum versus ECS/App Service, not a drop-in swap.

## Interview Answer Skeleton

**30-sec:** Azure/GCP cover the same categories AWS does (compute ownership, storage, database, messaging, identity) under different names — Kubernetes (EKS/AKS/GKE) is the one genuinely portable exception.

**2-min:** Add why this is tested: interviewers at non-AWS shops probe whether cloud knowledge is transferable judgment or an AWS-only vocabulary list. Name one real, non-cosmetic difference (Cosmos DB's tunable consistency, or GCP's Firestore/Bigtable split) to prove genuine understanding, not table memorization.

**Staff-level framing:** A migration or multi-cloud decision must account for where the mapping breaks — Cloud Run's distinct ownership point, Cosmos DB's per-request consistency tuning — not just the name-for-name table.

## Related

- syllabus/15-cloud/aws-core-services-for-backend-engineers.md
