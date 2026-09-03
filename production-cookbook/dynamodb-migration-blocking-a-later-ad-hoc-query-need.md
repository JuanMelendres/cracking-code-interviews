---
title: "DynamoDB Migration Blocking a Later Ad-Hoc Query Need"
document_type: production-cookbook-entry
domain: cloud
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/15-cloud/aws-core-services-for-backend-engineers.md
source: handbook/cloud/aws-core-services-for-backend-engineers.md#production-scenarios
---

# DynamoDB Migration Blocking a Later Ad-Hoc Query Need

## Context

A team migrates a service's primary data store from RDS (Postgres) to DynamoDB, citing DynamoDB's reputation for scale and low operational overhead, evaluated against the service's then-current access pattern.

## Symptoms

Months later, a new reporting feature needs to query records by several different, ad-hoc combinations of attributes — a pattern the team assumed would "just work" the way it did in Postgres.

## Impact

The new feature can't be built efficiently against the migrated data model at all — DynamoDB's access patterns must be designed in at table/index-design time, and the ad-hoc query need wasn't anticipated during the migration.

## Initial Hypotheses

- The team's DynamoDB expertise is simply insufficient and needs training — checked and ruled out; the team correctly understands DynamoDB's documented model, the gap isn't knowledge, it's a mismatch between the actual need and what was designed for.
- A missing secondary index would fix it easily — checked and ruled out; the specific ad-hoc combination wasn't anticipated by any existing or easily-added index, since the query shape varies per report.
- The original migration decision didn't work through the access-pattern method for this system's full set of needs, including future or adjacent ones, before committing — correct.

## Evidence

DynamoDB is an excellent fit for access patterns known and designed for in advance, and a poor fit for ad-hoc, varying query shapes. The migration decision evaluated DynamoDB's fit for the existing access pattern correctly, but never checked it against the reporting feature's later, different need.

## Investigation Timeline

1. **New reporting feature blocked**, unable to query the migrated data by ad-hoc attribute combinations.
2. **Team expertise ruled out as the cause**, confirming correct understanding of DynamoDB's own documented model.
3. **Secondary-index fix evaluated and ruled out**, since the varying, per-report query shape isn't addressable by any fixed index design.
4. **Original migration decision reviewed retrospectively**, finding it evaluated only the access pattern that motivated the migration, not future or adjacent needs.

## Root Cause

DynamoDB is an excellent fit for access patterns known and designed for in advance, and a poor fit for ad-hoc, varying query shapes — the migration decision evaluated DynamoDB's fit for the existing access pattern correctly, but never checked it against the reporting feature's later, different need.

## Immediate Mitigation

Build the ad-hoc reporting feature against a separate, purpose-built store, for example exporting DynamoDB data via a stream into a relational or analytical store better suited to ad-hoc queries, rather than forcing it onto the primary DynamoDB table.

## Permanent Fix

Treat any storage migration decision as requiring the access-pattern method applied not just to current needs, but to reasonably anticipated future ones — reporting, analytics, ad-hoc operational queries.

## Alternatives Considered

Migrating back to RDS. Rejected — the original access pattern that motivated the DynamoDB migration (high-throughput, predictable-latency point lookups) is still real and still well-served by DynamoDB; the fix is adding a purpose-built secondary store for the new need, not reversing a decision that was correct for its original scope.

## Trade-offs

Maintaining two data stores — DynamoDB for the primary access pattern, a separate store fed by CDC for ad-hoc reporting — adds real operational complexity. Accepted, since forcing an ad-hoc query pattern onto DynamoDB isn't actually achievable at all, not just expensive.

## Prevention

Apply the access-pattern method explicitly to anticipated future needs, not just the access pattern motivating the immediate decision, before any database migration.

## Monitoring and Alerts

- No runtime monitoring signal directly prevents this class of gap — it's a design-time review question, not an operational metric. The structural fix is a mandatory access-pattern review checklist item for any storage migration proposal, explicitly listing the next 1-2 anticipated feature needs (reporting, analytics, cross-cutting queries) alongside the immediate one.
- Once the CDC-fed secondary store is in place, replication lag between DynamoDB and the analytical store tracked as a standing metric, since the reporting feature's data freshness now depends on it.

## Interview Story

This maps to a "you migrated to DynamoDB for one access pattern, now a new feature can't query it, why" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a service migrated to DynamoDB for a well-understood access pattern, then a later reporting feature needed ad-hoc queries the migrated model couldn't support.
- **Task:** determine whether this reflects a knowledge gap, a missing index, or something more fundamental.
- **Action:** rule out team expertise and a missing-index fix directly; recognize the access-pattern method was applied only to the original migration's motivating need, not to reasonably anticipated future needs.
- **Result:** built a separate, purpose-built analytical store fed by DynamoDB's change stream for the ad-hoc reporting need, rather than reversing the original, still-correct migration.

## Staff-Level Discussion

The important reframing in this incident is that the original migration decision was not wrong — DynamoDB was and remains the correct choice for the access pattern it was chosen for — but "correct for the pattern we evaluated" and "correct for the system's full future need" are different claims, and the gap between them is exactly what surfaced here. This is a general trade-off with any access-pattern-optimized storage choice (DynamoDB, a purpose-built cache, a specialized time-series store): the same design property that makes it excellent for its target pattern makes it structurally unable to serve orthogonal patterns, no amount of tuning fixes that, and the honest fix is a second, purpose-built store rather than forcing the mismatch. A Staff engineer evaluating any storage migration should require the access-pattern method be applied explicitly against the next one or two anticipated feature needs, not only the pattern motivating the current decision, since retrofitting this analysis after the fact costs far more than doing it up front.

## Related Handbook Chapters

- [AWS Core Services for Backend Engineers](../syllabus/15-cloud/aws-core-services-for-backend-engineers.md) — canonical DynamoDB access-pattern-design mechanics used here.
- [Storage Selection Trade-offs](../syllabus/11-system-design/storage-selection-tradeoffs.md) — the general access-pattern method this migration decision should have applied to future needs.
