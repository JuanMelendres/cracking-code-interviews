---
title: "Kafka Consumer Group Rebalance Storm"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/09-messaging-event-driven/consumer-groups-and-rebalancing.md
source: handbook/kafka/consumer-groups-and-rebalancing.md#production-scenarios
---

# Kafka Consumer Group Rebalance Storm

## Context

A consumer group processes order events. A recently added synchronous downstream HTTP call sits inside the poll loop, and occasionally takes several seconds to return.

## Symptoms

The consumer group shows a rebalance event in the logs roughly every 30 seconds. Throughput drops noticeably during each rebalance window. On-call initially suspects network flakiness between consumers and brokers.

## Impact

Reduced effective throughput from repeated stop-the-world pauses under the eager assignor in use, plus wasted investigation time chasing the wrong hypothesis before the real trigger is found.

## Initial Hypotheses

- Network partition or flakiness between consumers and brokers — checked and ruled out; no corresponding network errors or packet loss in infrastructure metrics.
- Broker instability — checked and ruled out; broker logs show no crashes or leader elections around the rebalance events.
- A genuinely slow consumer exceeding its poll interval — correct.

## Evidence

Consumer logs show `max.poll.interval.ms` warnings immediately preceding each rebalance. Application metrics show per-batch processing time creeping close to the configured `max.poll.interval.ms` value under load — specifically because the recently added synchronous downstream HTTP call inside the poll loop occasionally takes several seconds.

## Investigation Timeline

1. **Rebalance pattern noticed**, roughly every 30 seconds, with a visible throughput dip in each window.
2. **Network and broker hypotheses ruled out** using existing infrastructure and broker-log monitoring, neither showing anything abnormal.
3. **Consumer logs inspected directly**, surfacing `max.poll.interval.ms` warnings immediately before each rebalance — a specific, mechanical signal rather than a vague "something is slow."
4. **Per-batch processing time correlated** against the timeout, showing it creeping close to the configured limit under load, and traced to the recently added synchronous HTTP call.

## Root Cause

The consumer is alive and eventually finishes each batch, but occasionally exceeds `max.poll.interval.ms` between `poll()` calls because of the added synchronous call. The group coordinator interprets this as a dead consumer and evicts it, triggering a rebalance — which recurs once the "new" (same) consumer rejoins and eventually hits the same slow path again.

## Immediate Mitigation

Increase `max.poll.interval.ms` as a stopgap, to stop the eviction-and-rebalance cycle while the root cause is addressed. This does not fix the underlying latency — it only raises the threshold at which it becomes disruptive.

## Permanent Fix

Move the synchronous HTTP call off the poll thread (into an async pipeline), or reduce `max.poll.records` so each batch's worst-case processing time stays well under the timeout regardless of the call's latency. Separately, switch the group to `CooperativeStickyAssignor` so that any future, less frequent rebalance doesn't pause the whole group under the eager assignor's stop-the-world behavior.

## Alternatives Considered

Simply raising `max.poll.interval.ms` indefinitely. Rejected as a permanent fix — it only delays detection of a genuinely dead consumer rather than addressing the actual latency problem, and widens the window before a truly stuck consumer is noticed and evicted.

## Trade-offs

Moving the HTTP call off the poll thread adds asynchronous-processing complexity (tracking in-flight calls, handling their failures independently of the poll loop) in exchange for eliminating the rebalance cycle entirely, rather than just tolerating it with a longer timeout.

## Prevention

Any change adding a network call or other unbounded-latency operation to a consumer's processing path should be reviewed specifically against its `max.poll.interval.ms` budget before shipping, not discovered after it starts triggering rebalances in production.

## Monitoring and Alerts

- `max.poll.interval.ms` warnings in consumer logs, alerted on directly rather than waiting for the resulting rebalance and throughput dip to be noticed downstream — the warning is the earliest available signal, appearing before the eviction it causes.
- Rebalance frequency per consumer group as its own metric, with a low-frequency baseline; a group rebalancing on a short, regular period (as opposed to rare and one-off) is itself the anomaly worth alerting on, independent of root cause.
- Per-batch processing time relative to the configured `max.poll.interval.ms`, tracked as a ratio rather than an absolute value, so the alert threshold scales automatically if the interval setting is later changed.

## Interview Story

This maps directly to the "consumer group rebalancing every 30 seconds" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a consumer group rebalancing on a tight, regular cycle, with throughput dropping in each window.
- **Task:** find the trigger, resisting the natural first assumption of a networking problem.
- **Action:** rule out network and broker instability using existing monitoring; read consumer logs directly rather than only dashboards, finding the `max.poll.interval.ms` warning; correlate it to a specific recently added synchronous call in the processing path.
- **Result:** moved the call off the poll thread and switched to `CooperativeStickyAssignor`, eliminating both the trigger and the severity of any future rebalance.

## Staff-Level Discussion

The mistaken first hypothesis here — networking — is not a sign of poor engineering; it is the natural first guess when a distributed system exhibits periodic instability, and it costs real investigation time before the mechanical signal (`max.poll.interval.ms` warnings) redirects attention correctly. The organizational lesson is less about this one consumer and more about two standing platform choices: first, that `max.poll.interval.ms` warnings should be a first-class alert for every consumer group, not something read only after a rebalance is already suspected; second, that `CooperativeStickyAssignor` should arguably be the default assignor for new consumer groups rather than something teams individually discover they need after their first rebalance-storm incident — the eager assignor's stop-the-world behavior turns every future slow-consumer bug into a whole-group throughput event, regardless of what causes it.

## Related Handbook Chapters

- [Consumer Groups and Rebalancing](../syllabus/09-messaging-event-driven/consumer-groups-and-rebalancing.md) — canonical rebalance-trigger mechanics and assignor comparison.
- [Kafka Architecture Fundamentals](../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md) — partition and consumer-group model this incident is stated in terms of.
