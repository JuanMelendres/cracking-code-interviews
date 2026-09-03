---
title: "Scaling Consumers Past Partition Count Did Nothing to Reduce Lag"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md
  - ../handbook/kafka/consumer-groups-and-rebalancing.md
source: handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md#production-scenarios
---

# Scaling Consumers Past Partition Count Did Nothing to Reduce Lag

## Context

A paging alert fires for "consumer lag > 10,000" on an order-events topic. The on-call engineer, under pressure, doubles the consumer deployment's replica count from 3 to 6, expecting lag to drop as messages are processed in parallel by more workers.

## Symptoms

Lag does not improve after the consumer scale-up.

## Impact

The paging alert remains active after the first remediation attempt, extending the incident, and the scale-up itself adds infrastructure cost (three additional idle consumer instances) with no offsetting benefit.

## Initial Hypotheses

The new consumers hadn't started up yet, or there was a deployment issue.

## Evidence

A check of the consumer group's partition assignment (exactly what `ConsumersExceedPartitionsDemo` reproduces directly) showed the topic had only 3 partitions — the original 3 consumers were each already assigned one partition at 100% utilization, and the 3 new consumers had been assigned nothing at all, sitting completely idle.

## Investigation Timeline

1. Consumer lag alert fires at >10,000 on the order-events topic; on-call scales the consumer deployment from 3 to 6 replicas as the first remediation.
2. Lag observed to remain unchanged after the scale-up, with no improvement despite double the consumer count.
3. Deployment-issue and slow-startup hypotheses raised, but ruled out on further check.
4. Consumer group's partition assignment inspected directly, revealing the topic has only 3 partitions.
5. Confirmed the original 3 consumers were each already at 100% utilization on their assigned partition, and the 3 new consumers were assigned nothing — idle by Kafka's own partition-to-consumer assignment mechanics, not a startup delay.
6. Per-message processing time on the existing consumers investigated as the real bottleneck, and found to have regressed after a recent deployment.

## Root Cause

The actual bottleneck was partition count, not consumer count: a consumer group cannot have more active consumers than the topic has partitions, so consumers beyond the partition count sit idle no matter how many are deployed. The real cause of the lag was a per-message processing-time regression introduced by a recent deployment.

## Immediate Mitigation

Reverted the consumer scale-up — the idle instances added no value and only extra infrastructure cost — and identified the actual per-message processing-time regression.

## Permanent Fix

Fixed the processing-time regression directly, and separately opened a capacity-planning discussion about increasing the topic's partition count for future headroom (a change requiring careful handling of key-based ordering guarantees, since it affects which keys land on which partition going forward).

## Alternatives Considered

None recorded as rejected — the scale-up itself is presented as the initial, mistaken action rather than a deliberated alternative; repartitioning was deliberately scheduled as planned work rather than an emergency response.

## Trade-offs

Repartitioning a topic is a nontrivial operational change and was deliberately scheduled as planned work, not an emergency incident response, because of its effect on key-based ordering guarantees.

## Prevention

The team's lag runbook now states explicitly, as its first troubleshooting step, "check partition count vs. consumer count before scaling consumers" — turning a live, costly mistake into a documented first check.

## Monitoring and Alerts

- Add a standing metric or dashboard panel showing active consumer count against topic partition count per consumer group, so an on-call engineer sees immediately, at the moment of a lag alert, whether scaling consumers further is even mechanically possible before attempting it.
- Alert on per-message processing time (or per-partition consumption rate) trending upward independently of the lag alert itself — this incident's real cause was a processing-time regression from an unrelated deployment, and a dedicated processing-time metric would have pointed there directly instead of via elimination.
- Track idle consumer instances (assigned no partitions) as its own alert condition post-scale-up, so a scale-up that added no effective capacity is flagged automatically rather than discovered only when lag fails to improve.

## Interview Story

This maps directly to a well-known Kafka misconception — that adding consumers always increases parallelism — arriving as a real, live incident rather than an interview trivia question. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a consumer lag alert fired, and doubling the consumer replica count from 3 to 6 produced no improvement.
- **Task:** find out why scaling consumers didn't help, under the time pressure of an active page.
- **Action:** checked the consumer group's partition assignment directly and found the topic had only 3 partitions, meaning the 3 new consumers had nothing to be assigned; separately investigated and found the real cause was a per-message processing-time regression from a recent deployment.
- **Result:** reverted the ineffective scale-up, fixed the actual processing-time regression, and added "check partition count vs. consumer count" as the first step in the team's lag runbook.

## Staff-Level Discussion

This incident is a clean illustration of a mechanical constraint (partition count bounds effective consumer parallelism) being encountered under real operational pressure rather than as an abstract interview fact — and the cost of not knowing it in the moment was real infrastructure spend on idle instances plus extended time-to-resolution while lag continued unaddressed. The deeper organizational lesson is that "add more consumers" is a plausible-sounding first response precisely because it works for many other kinds of load problems, which makes it an easy mistake to make under page pressure without a runbook enforcing the partition-count check first. Separately, the decision to schedule the partition-count increase as planned work rather than an emergency change reflects a mature risk posture: repartitioning affects key-based ordering guarantees for every downstream consumer of that topic, and a Staff engineer should recognize that fixing a capacity constraint under incident pressure is exactly the wrong time to also introduce an ordering-affecting schema change.

## Related Handbook Chapters

- [Consumer Lag, Backpressure, and DLQ Strategy](../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md) — canonical explanation of the partition-count-bounds-parallelism mechanism this incident reproduces.
- [Consumer Groups and Rebalancing](../handbook/kafka/consumer-groups-and-rebalancing.md) — the partition-assignment mechanics that leave excess consumers idle.
