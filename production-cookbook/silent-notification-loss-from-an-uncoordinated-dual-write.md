---
title: "Silent Notification Loss From an Uncoordinated Dual Write"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/system-design/distributed-transactions-saga-and-outbox.md
source: handbook/system-design/distributed-transactions-saga-and-outbox.md#production-scenarios
---

# Silent Notification Loss From an Uncoordinated Dual Write

## Context

The order-placement code path writes to the orders table, then makes a separate, non-transactional call to publish a Kafka event afterward, triggering confirmation email and downstream fulfillment.

## Symptoms

A small, unpredictable fraction of successfully placed orders never trigger a confirmation email or downstream fulfillment event, discovered only via customer complaints of "I ordered but got no confirmation," not via any internal alert.

## Impact

Silent, intermittent business-process failure with no corresponding error signal — the order itself always succeeded.

## Initial Hypotheses

- An email-service outage — checked and ruled out; the email service's own logs show no corresponding failed sends, because it never received the trigger event at all.
- A Kafka producer misconfiguration — checked and ruled out; producer logs show no send failures for the affected orders, because no send was ever attempted.
- An uncoordinated dual write — correct.

## Evidence

Application deployment logs show a pattern of restarts — routine deploys, occasional crashes — correlating with the missing-notification incidents. The order-placement code path writes to the orders table, then makes a separate, non-transactional call to publish a Kafka event afterward.

## Investigation Timeline

1. **Customer complaints of missing confirmations trickled in**, with no matching internal alert or error.
2. **Email-service and Kafka producer hypotheses ruled out**, both confirming the trigger event simply never arrived.
3. **Deployment logs cross-referenced against affected orders**, revealing a correlation with restart timing.
4. **Code path reviewed**, finding the database write and the Kafka publish call as two separate, non-transactional steps.

## Root Cause

On the small fraction of order placements where a deploy or crash landed between the database commit and the Kafka publish call, the order row committed successfully but the publish call was never made, and nothing recorded that it was owed.

## Immediate Mitigation

Manually identify affected orders via a reconciliation query — orders with no corresponding published event — and manually trigger their notifications.

## Permanent Fix

Implement the transactional outbox pattern: write the order and an outbox row in the same transaction, and add a poller that reliably relays outbox rows to Kafka, eliminating the possibility of the gap entirely, and making the downstream email and fulfillment consumers idempotent to handle the outbox's at-least-once duplicates safely.

## Alternatives Considered

Wrapping the Kafka publish call in a retry loop at the point of failure. Rejected — a crash before the call is even reached leaves nothing to retry; the retry loop only helps if the process survives long enough to attempt and fail the call, which is not the failure mode actually observed.

## Trade-offs

The outbox pattern introduces new infrastructure — the poller, or a CDC-based alternative — and requires the downstream consumer to handle duplicates. Accepted, since the alternative is unrecoverable, silent event loss.

## Prevention

Any code path that writes to a database and separately calls an external system — a message broker, another service — as part of the same logical operation should be reviewed for this exact hazard. The fix is always either an outbox, or an idempotent design that tolerates the dual-write's own failure mode by some other means.

## Monitoring and Alerts

- A reconciliation query — orders with no corresponding published event — run on a regular schedule rather than only reached for reactively after customer complaints, converting the detection path from external reports to an internal, proactive check.
- Once the outbox pattern is in place, outbox-row age (time between insertion and successful relay) tracked as a standing metric, alerting on any row older than an expected threshold, surfacing poller failures or backlog directly.

## Interview Story

This maps directly to "you wrote to the DB and published to Kafka, prove no message is lost" — and the honest answer for a plain dual write is that it cannot be proven. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a small, unpredictable fraction of orders never triggered their confirmation and fulfillment events, discovered only via customer complaints.
- **Task:** find the mechanism behind an intermittent, alert-free failure.
- **Action:** rule out the email service and Kafka producer as the cause using their own logs; correlate the incident pattern against deployment and crash timing; identify the two-step, non-transactional write as the structural gap.
- **Result:** implemented the transactional outbox pattern, converting the dual write into a single atomic transaction plus a reliable relay, and made downstream consumers idempotent to handle the resulting at-least-once duplicates.

## Staff-Level Discussion

This is one of the cleanest demonstrations in distributed systems that "the code that publishes the event runs right after the code that writes the row" is never a durability guarantee, regardless of how reliable each individual step looks in isolation — the gap between two non-transactional operations is exactly where a crash or restart, an event that will eventually happen at any nonzero rate, causes silent, unrecoverable loss. The interview framing ("prove no message is lost") is deliberately adversarial because a plain dual write genuinely cannot be proven safe; only a structural fix — the outbox pattern, making both writes part of one atomic transaction — actually closes the gap. A Staff engineer reviewing any code path that combines a database write with an external side effect should treat the dual-write hazard as the default assumption to disprove, not something to notice only after an incident demonstrates it.

## Related Handbook Chapters

- [Distributed Transactions: Saga and Outbox](../handbook/system-design/distributed-transactions-saga-and-outbox.md) — canonical outbox-pattern and dual-write-hazard mechanics used here.
- [Kafka Delivery Semantics and Exactly-Once](../handbook/kafka/delivery-semantics-and-exactly-once.md) — the at-least-once/idempotent-consumer design the outbox relies on downstream.
