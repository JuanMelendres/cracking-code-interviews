---
title: "Kafka Streams Aggregation Duplicating Transiently During Rolling Deploys"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md
source: syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md#production-scenarios
---

# Kafka Streams Aggregation Duplicating Transiently During Rolling Deploys

## Context

A Kafka Streams application runs with the default `at_least_once` processing guarantee.

## Symptoms

During a rolling deployment (old instances stopping, new instances starting), some aggregated counts briefly appear higher than expected, then self-correct.

## Impact

Downstream consumers of the aggregation output see transient, incorrect values during every deployment.

## Initial Hypotheses

- A bug in the aggregation logic itself — checked, the logic is correct and the values self-correct without any code change.
- A Kafka Streams library bug — checked, this is documented, expected behavior, not a defect.
- Task reassignment during the rolling deployment causing some already-processed records to be reprocessed before the old task's final committed offset is honored by the new task, under the default `at_least_once` guarantee — correct.

## Evidence

The application's `processing.guarantee` config confirms `at_least_once` (the default), which explicitly permits this exact class of transient duplication under rebalancing.

## Investigation Timeline

1. Transient over-counts observed specifically during rolling deployments, self-correcting shortly after.
2. Aggregation-logic-bug and library-bug hypotheses ruled out via code review and documentation.
3. `processing.guarantee` config checked, confirming `at_least_once` as the active mode.

## Root Cause

`at_least_once` explicitly permits reprocessing of already-processed records during task reassignment; `exactly_once_v2` would prevent it, at a real throughput cost.

## Immediate Mitigation

Communicate to downstream consumers that this specific aggregation is `at_least_once` and may show transient over-counts during deployments, self-correcting shortly after.

## Permanent Fix

If downstream consumers genuinely cannot tolerate even transient duplication, switch to `processing.guarantee=exactly_once_v2`, understanding the real added latency/throughput cost that comes with the underlying Kafka transactions.

## Alternatives Considered

Having downstream consumers de-duplicate or treat the aggregation as eventually consistent — a real, often simpler fix than changing the processing guarantee, appropriate when the downstream consumer can absorb brief inconsistency.

## Trade-offs

`exactly_once_v2` removes this specific symptom but adds real transactional overhead to every write — a genuine cost that should be paid only when the actual downstream requirement demands it, not defensively.

## Prevention

Decide `at_least_once` vs. `exactly_once_v2` deliberately, based on what downstream consumers actually require, and document that decision rather than leaving it as an unexamined default.

## Monitoring and Alerts

- Deployment-correlated dashboards for aggregation output values, distinguishing expected transient deployment-window blips from genuine anomalies.
- An explicit, documented `processing.guarantee` value per Kafka Streams application, reviewed whenever a downstream consumer's tolerance for duplication changes.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent deployment-window observation.

- **Situation:** aggregated counts briefly spiked during every rolling deployment, then self-corrected.
- **Task:** determine whether this was a bug or expected behavior under the application's current configuration.
- **Action:** checked the `processing.guarantee` config and confirmed `at_least_once`'s documented behavior under rebalancing explained the symptom exactly.
- **Result:** communicated the expected behavior to downstream consumers, with `exactly_once_v2` available as an explicit, deliberate upgrade path if ever required.

## Staff-Level Discussion

Kafka Streams' default guarantee is a real, deliberate trade-off (throughput over strict exactly-once), not an oversight — and the correct fix depends on what the specific downstream consumer actually needs, not a blanket "always use the stronger guarantee." The organizational lesson is documenting the `processing.guarantee` decision explicitly per application, so a future on-call engineer encountering this exact symptom doesn't have to re-derive whether it's expected or a regression.

## Related Handbook Chapters

- [Kafka Streams and Stateful Processing](../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md) — the canonical `at_least_once` vs. `exactly_once_v2` trade-off behind this incident.
