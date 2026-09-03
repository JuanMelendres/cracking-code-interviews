---
title: "Cheat Sheet: AWS Core Services for Backend Engineers"
slug: aws-core-services-for-backend-engineers
document_type: cheat-sheet
domain: cloud
topic_id: T-1006
canonical: ../handbook/cloud/aws-core-services-for-backend-engineers.md
last_updated: 2026-08-05
---

# AWS Core Services for Backend Engineers

**Canonical chapter:** [`syllabus/15-cloud/aws-core-services-for-backend-engineers.md`](../syllabus/15-cloud/aws-core-services-for-backend-engineers.md)

## Core Mental Model

Every AWS service in this chapter exists to remove one specific category of undifferentiated operational work — provisioning servers, managing storage durability, running a database, delivering messages reliably — so a backend team can focus on what's actually differentiated. The right service isn't "whichever is most popular" — it's whichever removes the specific operational burden this workload would otherwise force the team to own, at a cost (control, price, lock-in) explicitly weighed.

## Essential Definitions

- **Compute spectrum** — EC2 (raw VMs, full control/ownership) → ECS (AWS-native containers) → EKS (managed Kubernetes) → Lambda (zero server management, execution limits + cold starts).
- **Storage access models** — S3 (object storage, HTTP-style API, not mounted), EBS (block storage, one instance at a time), EFS (shared network filesystem, multiple instances).
- **Database** — RDS (managed relational, flexible ad-hoc queries) vs. DynamoDB (key-value/document, extremely high predictable throughput, requires upfront access-pattern design).
- **Messaging** — SQS (point-to-point, durable, one consumer per message) vs. SNS (pub/sub fan-out, many subscribers) — often combined (SNS fanning out to multiple SQS queues).

## Decision Table

| Need | Service |
|---|---|
| Full control over compute | EC2 |
| Managed containers, AWS-native | ECS |
| Managed containers, portable Kubernetes | EKS |
| Event-driven, zero server management | Lambda |
| Durable object storage, not a filesystem | S3 |
| Single-instance block storage | EBS |
| Shared filesystem across instances | EFS |
| Relational, multi-row transactions | RDS |
| High-throughput, key-based access, known query patterns | DynamoDB |
| Point-to-point, durable, buffered delivery | SQS |
| Fan-out to multiple independent consumers | SNS (often with SQS per consumer) |

**Trade-offs:** Lambda's zero-server-management benefit comes with cold-start and execution-time-limit costs; DynamoDB's throughput/scale benefits require upfront access-pattern design, unlike RDS's flexibility; EFS's shared-filesystem convenience costs more than S3 for workloads that don't actually need POSIX semantics.

## Key Numbers (real, applied worked scenario)

Migrating a service from RDS to DynamoDB for its known access pattern later blocked an ad-hoc reporting need:

```
Original access pattern (point lookups): well-served by DynamoDB, correctly chosen
New requirement (ad-hoc multi-attribute reporting query): NOT servable by the same
  table/index design -- DynamoDB requires access patterns designed in upfront

Fix: NOT reverting to RDS -- the original access pattern is still real and still
  well-served by DynamoDB. Instead: a separate store fed by DynamoDB Streams/CDC,
  purpose-built for the new ad-hoc query need.
```

## Common Pitfalls

- Choosing a compute service by reputation/trend rather than the actual operational-ownership-vs-control trade-off needed.
- Treating S3/EBS/EFS as interchangeable "AWS storage" without matching to the actual access model.
- Choosing DynamoDB without applying the access-pattern method to anticipated *future* query needs, not just the current one.
- Using SNS or SQS alone when the actual need is genuinely both fan-out and durable per-consumer processing.

## Interview Answer Skeleton

**30-sec:** AWS's core services cluster into compute (EC2 → ECS/EKS → Lambda, trading control for less operational ownership), storage (S3/EBS/EFS by access model), database (RDS for relational flexibility, DynamoDB for high-throughput key-based access with upfront-designed patterns), and messaging (SQS point-to-point, SNS fan-out, often combined). The right choice matches the actual access/operational model needed, not reputation.

**2-min:** Add why each category exists (removing a specific undifferentiated operational burden) + the access-pattern-first method applied one layer up from in-memory collections (state the actual current AND anticipated-future access pattern before naming a service) + a real-shaped production example (a DynamoDB migration correct for its original access pattern becoming a structural blocker for a later, unanticipated ad-hoc reporting need).

**Whiteboard:** The compute spectrum (EC2 → ECS → EKS → Lambda, "more managed" arrows), plus three parallel spectra alongside it for storage (object/block/filesystem), database (relational/key-value), and messaging (point-to-point/fan-out) — making the point that every category has its own version of "which access model does this actually need."

**Staff-level framing:** every cloud-service decision is a specific instance of the same general access-pattern-first method this program applies to in-memory collections and general storage technology — the service names change, the discipline doesn't. State the access pattern, including what's reasonably anticipated to change, before committing to a technology scoped to today's need alone.

## Production Warning Signs

- A team migrates to DynamoDB citing "scale and low operational overhead," then a later reporting feature needs ad-hoc multi-attribute queries the table/index design never anticipated — the access-pattern method was applied to the current need but not future ones; fix with a purpose-built, CDC-fed secondary store, not a reversal of the original decision.
- Lambda chosen for a workload with sustained, predictable, long-running compute needs — cold-start and execution-limit trade-offs provide no benefit over a simpler EC2/ECS deployment.
- **Prevention:** apply the access-pattern method explicitly to reasonably-anticipated future needs (reporting, analytics, ad-hoc operational queries), not just the pattern motivating the immediate decision, before any storage-technology migration.

## Related

- `syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md`
- `syllabus/15-cloud/cloud-cost-and-scaling-economics.md`
- `syllabus/11-system-design/storage-selection-tradeoffs.md`
