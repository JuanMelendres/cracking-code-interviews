---
title: "Cheat Sheet: Consumer Lag, Backpressure, and DLQ Strategy"
slug: consumer-lag-backpressure-and-dlq-strategy
document_type: cheat-sheet
domain: kafka
topic_id: T-707
canonical: ../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md
last_updated: 2026-09-02
---

# Consumer Lag, Backpressure, and DLQ Strategy

**Canonical chapter:** [`syllabus/09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md`](../syllabus/09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md)

## Core Mental Model

A Kafka partition is a strictly ordered log, and a consumer reads it strictly in order — there is no way to skip ahead while an earlier message is "in progress" without an explicit action. Consumer lag is just "how far behind the end of the log is my current read position," a poison message blocks everything behind it because the consumer cannot advance past an uncommitted offset without being told to, and a DLQ strategy is an explicit, deliberate way of saying "skip this one, I've dealt with it elsewhere."

## Essential Definitions

- **Consumer lag** — the difference between a partition's latest offset and a consumer group's current committed offset for that partition; a real SLO, not just a diagnostic.
- **Backpressure** — what happens when a consumer cannot keep up with the produce rate; lag grows and needs an explicit strategy (scale, shed load, or accept it temporarily).
- **Dead-letter queue (DLQ)** — a separate topic where messages a consumer cannot successfully process after a bounded number of attempts are routed, so the consumer can commit past them and continue.
- **Consumers beyond partition count sit idle** — Kafka's consumer-group protocol assigns each partition to at most one consumer per group at a time; extra consumers get nothing.
- **Committing an offset ≠ moving the fetch position** — `poll()` advances the real fetch position at fetch time; an explicit `seek()` is required after dead-lettering to actually continue past the skipped record.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Lag on a specific partition growing while others are healthy | Check for a poison message or a hot key, not a broad capacity problem |
| Consumer scaling has stopped improving lag | Check partition count vs. consumer count before adding more consumers |
| Message type can tolerate being processed out of order relative to a failed one | Bounded retry + DLQ |
| Strict per-key ordering is absolutely required, even at the cost of blocking | Unbounded retry with alerting / manual intervention, not a DLQ |
| Downstream failure is a data problem vs. a capacity problem | Data problem → DLQ; capacity problem → rate limiting / throttling |

**Strategy comparison:**

| Strategy | Preserves ordering | Unblocks the partition | Real cost |
|---|---|---|---|
| Unbounded retry | Yes | No — total blockage (measured) | Total processing halt on that partition |
| Bounded retry + DLQ | No, for that message | Yes — 9 of 10 processed (measured) | DLQ monitoring and replay burden |
| Adding consumers beyond partition count | N/A | No effect (measured) | Wasted infrastructure, zero benefit |

## Key Numbers (real, executed against apache/kafka:3.7.0 in Docker)

- Naive unbounded retry: 4 of 10 messages processed, lag stuck at 6, across 5 real retry rounds — no further rounds change the outcome.
- DLQ-aware version: 9 of 10 processed, 1 dead-lettered, 0 lost — verified by an independent consumer reading the DLQ topic's real contents.
- 3 partitions, 5 consumers in one group: consumer-0 got 11 messages, consumer-1 got 9, consumer-2 got 10, consumer-3 and consumer-4 got 0 (idle).

## Common Pitfalls

- Assuming adding consumers always increases throughput — real parallelism is capped at partition count.
- Treating "one bad message blocks the partition" as a bug rather than the direct consequence of the ordering guarantee.
- Building a DLQ strategy with no bound on retries, effectively recreating unbounded-retry blockage with extra steps.
- Monitoring aggregate topic-level lag only, missing a single stalled partition hiding behind healthy averages.
- Forgetting the explicit `seek()` after dead-lettering — committing an offset alone does not move the consumer's actual fetch position, causing a silent stall.

## Interview Answer Skeleton

**30-sec:** Consumer lag is how far behind the end of a partition's log a consumer's committed offset is — a real SLO. A single unprocessable message blocks everything behind it because Kafka's ordering guarantee means you can't skip ahead without explicit action; bounded retry plus DLQ is that action. Adding consumers beyond partition count doesn't help — Kafka assigns at most one consumer per partition per group.

**2-min:** Add the measured numbers: naive retry stuck at 4/10 processed with lag 6; DLQ fix reaching 9/10 with the failed message safely dead-lettered; the required explicit `seek()` discovered while building the demo; and the 3-partition/5-consumer result showing 2 consumers fully idle.

**Whiteboard:** Draw 10 boxes labeled order-1 through order-10 with order-5 in red; a consumer arrow reaches order-5 and stops, boxes to the right greyed out. Redraw with order-5 moved to a "DLQ" box below, consumer arrow continuing cleanly to order-10.

**Staff-level framing:** Discuss when the DLQ ordering trade-off is unacceptable (strict per-key ordering like financial transactions) versus routine (most independent business events). Require real ownership and a replay process for every DLQ, and connect this to the organizational cost of a scaling decision made under paging pressure without checking partition count first.

## Production Warning Signs

- A lag alert fires and an on-call engineer doubles consumer replica count with zero effect — check partition assignment before scaling; the real bottleneck was partition count (3) vs. consumer count (6), leaving 3 idle.
- Lag stops decreasing at a specific, reproducible point lining up with a known message — a silently stuck consumer position after dead-lettering, missing the required `seek()`.
- A DLQ that accumulates messages forever with no alert — never monitored or drained, silently losing the business value those records represented.

## Related

- `syllabus/09-messaging-event-driven/consumer-groups-and-rebalancing.md`
- `syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md`
- `syllabus/11-system-design/rate-limiting-and-throttling-algorithms.md`
- `syllabus/11-system-design/resilience-patterns.md`
