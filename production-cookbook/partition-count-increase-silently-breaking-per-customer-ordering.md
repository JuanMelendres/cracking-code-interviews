---
title: "Partition Count Increase Silently Breaking Per-Customer Ordering"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/kafka/kafka-architecture-fundamentals.md
source: handbook/kafka/kafka-architecture-fundamentals.md#production-scenarios
---

# Partition Count Increase Silently Breaking Per-Customer Ordering

## Context

A downstream consumer reconstructs per-customer event sequences from a keyed topic, assuming that a given customer's records all land on one partition, in order. A routine capacity change doubles the topic's partition count to relieve a hot broker.

## Symptoms

After the partition-count change, the downstream consumer starts producing out-of-order results for a subset of customers, discovered only when a reconciliation job flags inconsistent state days later.

## Impact

Silent, delayed-discovery data-correctness bug, not a crash — the kind that erodes trust in a pipeline slowly rather than paging anyone immediately.

## Initial Hypotheses

- A consumer-side bug — checked and ruled out; consumer code unchanged.
- A producer regression — checked and ruled out; producer code unchanged.
- The partition-count change itself — correct.

## Evidence

Comparing `hash(customerId) % oldPartitionCount` against `hash(customerId) % newPartitionCount` for affected customers shows the mapping changed for a subset of keys — new records for those customers now land on a different partition than their historical records.

## Investigation Timeline

1. **Out-of-order results reported days later**, via a reconciliation job rather than any real-time signal.
2. **Consumer and producer code hypotheses ruled out**, confirming neither changed around the incident window.
3. **Partition-count change correlated with the incident timing**, identified as a routine, unrelated-seeming capacity fix.
4. **Key-to-partition mapping recomputed before and after**, confirming the specific customers affected are exactly those whose `hash(customerId) % partitionCount` changed with the new partition count.

## Root Cause

Partition count for a keyed topic is a one-way door: increasing it remaps every key, and any consumer logic assuming "this customer's records are all on one partition, in order" breaks the moment old and new records for the same customer are split across partitions with no ordering relationship between them.

## Immediate Mitigation

Halt further scale-driven partition-count changes on ordering-dependent topics, and document which topics have this dependency.

## Permanent Fix

For genuinely ordering-dependent topics, plan partition count from projected peak scale before going live, since post-hoc increases are not safe. Where a topic must eventually grow, migrate via a new topic with the target partition count and a controlled cutover, rather than resizing in place.

## Alternatives Considered

Leaving partition count fixed and addressing hot-broker load via a compound key or different sharding strategy instead of touching partition count.

## Trade-offs

Provisioning for projected peak scale up front costs more idle capacity early. Accepted, because the alternative — a live remapping of an ordering-dependent topic — is not actually a safe operation at all.

## Prevention

A pre-change checklist item: is this topic keyed, and does any consumer depend on per-key ordering? If yes, partition count is out of scope for a "quick capacity fix."

## Monitoring and Alerts

- A pre-change gate on any partition-count modification, explicitly checking whether the topic is keyed and whether any known consumer depends on per-key ordering, blocking the change rather than relying on operators to remember this dependency.
- A per-key ordering-consistency check run periodically (not just via an eventual reconciliation job), sampling recent records for a set of keys and verifying partition assignment consistency, surfacing this class of break in hours rather than days.

## Interview Story

This maps to "partition count is a one-way door" as a real, slow-burning incident rather than an abstract warning. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a routine partition-count increase, done to relieve a hot broker, silently broke per-customer event ordering for a subset of customers.
- **Task:** find the connection between an infrastructure-only capacity change and a downstream data-correctness bug discovered days later.
- **Action:** rule out consumer and producer code changes; correlate the incident timing with the partition-count change; recompute key-to-partition mappings before and after to confirm exactly which customers were affected and why.
- **Result:** halted further scale-driven partition-count changes on ordering-dependent topics, and required future growth to go through a new-topic migration with controlled cutover instead of in-place resizing.

## Staff-Level Discussion

The most dangerous property of this incident is that the triggering change — a partition-count increase — looks like a purely operational, infrastructure-layer decision with no application-level consequence, made by whoever is managing broker capacity, likely without consulting whoever wrote the ordering-dependent consumer logic. The actual dependency (ordering-dependent consumer logic assumes a stable key-to-partition mapping) lives entirely in application code that the infrastructure change never touches or reviews. This is a recurring category of cross-team incident risk: a change that is safe from the perspective of the team making it can silently violate an invariant owned by a different team, unless the dependency is made explicit and checkable — which is exactly what the pre-change gate does, converting an implicit, easily-forgotten cross-team dependency into an explicit, structural one.

## Related Handbook Chapters

- [Kafka Architecture Fundamentals](../handbook/kafka/kafka-architecture-fundamentals.md) — canonical partition-count and key-to-partition mapping mechanics used here.
- [Producer Semantics and Partition Keys](../handbook/kafka/producer-semantics-and-partition-keys.md) — the partition-key design this ordering dependency relies on.
