---
title: "Duplicate Payment Charge From Kafka Redelivery"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md
source: handbook/kafka/delivery-semantics-and-exactly-once.md#production-scenarios
---

# Duplicate Payment Charge From Kafka Redelivery

## Context

A Kafka consumer processes order events and charges the customer's payment method as part of handling each event. The consumer commits offsets after processing — at-least-once delivery, the correct default for avoiding silent loss.

## Symptoms

A small number of customers report being charged twice for a single order, discovered via support tickets rather than an internal alert.

## Impact

Direct financial exposure, refund processing overhead, and customer trust damage.

## Initial Hypotheses

- A client-side double-submit — checked and ruled out; request logs show only one original client request per affected order.
- A payment-gateway-side bug — checked with the vendor and ruled out.
- A Kafka consumer redelivery without deduplication — correct.

## Evidence

Consumer logs show the same Kafka record — same partition, same offset — processed twice, several seconds apart, with a consumer restart in between. The payment-charging code had no dedupe check; it called the payment gateway directly on every invocation of the handler.

## Investigation Timeline

1. **Duplicate charges reported** via support tickets, not caught by any internal alert.
2. **Client and gateway hypotheses ruled out** by checking request logs and confirming with the payment vendor directly.
3. **Consumer logs inspected**, finding the exact same partition/offset processed twice with a consumer restart between the two.
4. **Handler code reviewed**, confirming no dedupe check exists between the payment call and offset commit.

## Root Cause

The consumer commits offsets after processing, but the processing step itself — charging a payment — was not made idempotent. A crash after the first charge succeeded but before the offset committed caused a redelivery, and the redelivered record triggered a second, real charge.

## Immediate Mitigation

Manually identify and refund the duplicate charges found via reconciliation against the payment gateway's own transaction log.

## Permanent Fix

Add a durable dedupe table keyed by a stable event ID, not the Kafka offset: check-and-skip before calling the payment gateway, and only mark the record processed after the charge succeeds, within the same transaction boundary as the offset-commit bookkeeping.

## Alternatives Considered

Switching to at-most-once (commit before processing). Rejected outright — it converts a duplicate-charge risk into a silent-loss risk (a paid order that's never fulfilled), which is a strictly worse failure mode for this business case.

## Trade-offs

The dedupe table adds a lookup and a write to every payment-processing call, and a small amount of storage growth. Accepted as clearly worthwhile against the cost of duplicate financial transactions.

## Prevention

Any consumer whose side effect is not naturally idempotent — payments, sending a notification, provisioning a resource — requires a design-review checklist item: what is the dedupe key, where is it stored durably, and is the dedupe check part of the same transaction as the side effect?

## Monitoring and Alerts

- Consumer restart events cross-referenced against records processed in the surrounding window, surfacing redelivery-prone incidents before a customer complaint does — this incident was found by support tickets, which is the slowest possible detection path for a financial-correctness bug.
- A reconciliation job comparing internal charge records against the payment gateway's own transaction log on a recurring schedule, independent of any specific incident, catching duplicate or missing charges as a standing check rather than only after they're reported.

## Interview Story

This maps to the "consumer crashes after processing but before committing" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a small number of customers were charged twice for the same order, discovered through support tickets.
- **Task:** find the mechanism, and explain why "the crash caused it" is the wrong framing.
- **Action:** rule out client-side and gateway-side causes; find the exact same offset processed twice around a consumer restart; recognize that the crash and redelivery are the expected, correct behavior of at-least-once delivery — the missing idempotency check is the actual defect.
- **Result:** added a durable, event-ID-keyed dedupe check inside the same transaction boundary as the charge, closing the gap without reverting to a riskier at-most-once delivery model.

## Staff-Level Discussion

The most important framing in this incident is naming what did *not* go wrong: at-least-once delivery, the consumer restart, and the redelivery are all working exactly as designed — none of them are the defect. The actual gap is a missing architectural guarantee (idempotent side effects) that at-least-once delivery inherently requires from anything downstream of it. This distinction matters because the wrong fix — switching to at-most-once to "stop the duplicates" — would trade a visible, refundable problem for an invisible, much worse one (silently dropped paid orders). A Staff engineer's job here is recognizing which failure mode is actually acceptable for the business (a rare duplicate charge, refundable) versus which is not (a paid order silently never fulfilled), and designing the fix around that judgment rather than around whichever failure mode is currently visible.

## Related Handbook Chapters

- [Delivery Semantics and Exactly-Once](../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md) — canonical at-least-once/idempotency mechanics used here.
- [Idempotency at System Edges](../syllabus/11-system-design/idempotency.md) — the general idempotency-key pattern this dedupe table is an instance of.
