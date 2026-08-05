---
title: "Silent Data Loss From a Shrunk ISR Without min.insync.replicas"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/kafka/producer-semantics-and-partition-keys.md
source: handbook/kafka/producer-semantics-and-partition-keys.md#production-scenarios
---

# Silent Data Loss From a Shrunk ISR Without min.insync.replicas

## Context

A financial-transactions topic is configured with `acks=all`, but `min.insync.replicas` was left at its default of 1.

## Symptoms

A small number of financial transaction events are missing from a downstream analytics pipeline, discovered during a monthly reconciliation, with no corresponding error or alert at write time.

## Impact

Financial reporting discrepancy requiring manual investigation and correction.

## Initial Hypotheses

- Consumer-side processing bug — checked and ruled out; offsets and logs show the records were never in the topic at all.
- Producer silently failing — checked and ruled out; producer logs show all sends returned successfully.
- A broker-level data-loss event — correct.

## Evidence

Broker logs from the affected time window show a follower replica had been dropped from the ISR shortly before the incident window, lagging beyond `replica.lag.time.max.ms`, and the topic's `acks=all` configuration had no corresponding `min.insync.replicas` setting — it was left at the default of 1.

## Investigation Timeline

1. **Missing records discovered** during a routine monthly reconciliation, with no alert having fired at write time.
2. **Consumer and producer hypotheses ruled out**, confirming the records were never durably in the topic and every producer send returned success.
3. **Broker logs inspected for the incident window**, finding a follower replica had dropped from the ISR shortly before.
4. **Topic configuration reviewed**, finding `acks=all` with `min.insync.replicas` left at its default of 1.

## Root Cause

With the ISR shrunk to just the leader, `acks=all` acknowledged writes based on a single replica. The leader broker then failed before the lagging follower caught up and was restored to the ISR, losing every record acknowledged during that window.

## Immediate Mitigation

Set `min.insync.replicas=2` on the affected topic, converting the silent-loss failure mode into a loud, immediate `NotEnoughReplicasException` the next time the ISR shrinks below the minimum.

## Permanent Fix

Audit all production topics for `acks=all` without a corresponding `min.insync.replicas` setting, and add alerting on ISR shrinkage events, not just broker-down events, since the dangerous window is exactly the period where the ISR is smaller than expected but nothing looks obviously wrong.

## Alternatives Considered

Increasing `replication.factor` alone. Rejected as insufficient in isolation, since the actual failure mode is about the current ISR at write time, not the configured replica count.

## Trade-offs

`min.insync.replicas=2` means writes fail, rather than silently succeed, during any window where fewer than two replicas are in sync. Accepted, since failing loudly and rejecting the write is strictly safer than silently accepting data that isn't actually durable.

## Prevention

Treat `acks=all` and `min.insync.replicas` as a single, inseparable configuration pair in every topic-provisioning checklist and infrastructure-as-code template.

## Monitoring and Alerts

- ISR shrinkage events alerted directly, independent of broker-down alerts — this is the earliest available signal, visible well before any resulting data loss and directly explaining the exposure window rather than just its eventual consequence.
- An automated audit checking every topic for `acks=all` without a matching `min.insync.replicas`, run on a schedule against the cluster's actual configuration, not just enforced at topic-creation time when it can still drift afterward.

## Interview Story

This maps directly to the "`acks=all` and you still lost a message" interview trap. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** financial transaction records went missing with no error or alert at write time, discovered only during a monthly reconciliation.
- **Task:** explain how `acks=all` — supposedly the strongest durability setting — still lost data.
- **Action:** rule out consumer and producer-side causes using logs and offsets; inspect broker logs for the incident window; find the ISR shrinkage event and the missing `min.insync.replicas` setting that would have prevented the write from being acknowledged.
- **Result:** set `min.insync.replicas=2` and added a standing audit ensuring the two settings are never configured independently again.

## Staff-Level Discussion

`acks=all` alone is a common and dangerous half-truth in Kafka configuration: it sounds like the strongest durability guarantee, and named alone, it is genuinely meaningless without `min.insync.replicas`, since "all" refers to whatever the current ISR happens to be, not a fixed replica count. This incident is valuable specifically because it was silent for weeks — the system did exactly what it was configured to do, and nothing about the write path signaled a problem, which is the most dangerous kind of misconfiguration: one where every individual component behaves correctly according to its own configuration, yet the composed system does not deliver the guarantee anyone assumed. A Staff engineer reviewing Kafka topic configuration should treat `acks` and `min.insync.replicas` as a single unit that can never be reviewed or set independently, and should push for infrastructure-as-code templates that make setting one without the other structurally impossible.

## Related Handbook Chapters

- [Producer Semantics and Partition Keys](../handbook/kafka/producer-semantics-and-partition-keys.md) — canonical `acks`/ISR/durability mechanics used here.
- [Delivery Semantics and Exactly-Once](../handbook/kafka/delivery-semantics-and-exactly-once.md) — the broader delivery-guarantee framing this durability setting is part of.
