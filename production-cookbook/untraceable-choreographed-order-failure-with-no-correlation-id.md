---
title: "Untraceable Choreographed Order-Fulfillment Failure with No Correlation ID"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md
  - ../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md
source: handbook/architecture/event-driven-architecture-integration-styles.md#production-scenarios
---

# Untraceable Choreographed Order-Fulfillment Failure with No Correlation ID

## Context

A choreographed order-fulfillment pipeline: services react to each other's events independently (Payment publishes `PaymentCharged`, Shipping is expected to react to it), with no centralized orchestrator coordinating the flow.

## Symptoms

A customer's order was charged but never shipped; support could see the `PaymentCharged` event in the Payment service's own logs but had no way to determine whether Shipping ever received it, or received it and failed silently, or was never subscribed to it correctly after a recent deploy.

## Impact

Support could not answer "why did order-42 never ship?" in under two hours — a customer-facing failure with no efficient path to root cause, tying up investigation time for a single order across three separate services' logs.

## Initial Hypotheses

A Shipping bug, a broker delivery failure, a missed subscription.

## Evidence

This chapter's own `ChoreographyTraceabilityDemo` reproduces exactly why the investigation was hard — the real captured stack trace at the Shipping handler contains nine frames, all either JDK executor internals or the event bus's own dispatch code, and explicitly does not reference the original publish call. There was no single log line, stack trace, or trace ID anywhere in the system that already connected "payment charged" to "shipping's reaction to it," because none had been deliberately built.

## Investigation Timeline

1. Customer report received: order was charged but never shipped.
2. Payment service's own logs checked, confirming `PaymentCharged` was published for the order.
3. Shipping service's logs and behavior checked for any of the three candidate hypotheses (a bug, a broker delivery failure, a missed subscription) — no direct evidence tying Shipping's state to this specific event was available in any single log source.
4. Stack trace at the Shipping handler captured and inspected, showing nine frames of JDK executor and event-bus dispatch internals with no reference back to the original publish call.
5. Diagnosis reached: no correlation mechanism (shared ID, distributed trace) existed anywhere in the system to connect the two services' independent event handling for the same order, so the investigation required manually correlating timestamps and `orderId` values across three services' separate log files.

## Root Cause

The system had been designed with choreography's decoupling benefit but without its required companion — a correlation ID propagated through every event and indexed centrally (a distributed tracing system, or at minimum a shared `orderId` field logged consistently at every hop).

## Immediate Mitigation

Manually correlated timestamps and `orderId` values across three services' separate log files.

## Permanent Fix

Added a mandatory `correlationId` field to every event schema and adopted OpenTelemetry trace propagation across the event bus.

## Alternatives Considered

None recorded as rejected — the permanent fix (correlation ID plus distributed tracing) is presented as the direct, necessary companion to choreography that was missing, not one option among several.

## Trade-offs

Every event now carries tracing metadata, a small but real payload and schema-evolution cost.

## Prevention

Any new choreographed workflow's design review now requires naming its tracing strategy before implementation starts, not after the first unexplainable incident.

## Monitoring and Alerts

- Once `correlationId` and OpenTelemetry propagation are in place, build a standing "trace an order end-to-end" dashboard or query capability keyed on `orderId`/`correlationId`, converting what took a multi-service, multi-log manual correlation effort into a single lookup.
- Alert on any event published without a `correlationId` field populated, catching a future service's schema regression (or a new service onboarded without the mandatory field) before it recreates this incident's exact blind spot.
- Track end-to-end latency and drop-off between `PaymentCharged` and its expected downstream `Shipped` event as a business-level SLO, so a silent failure like this one (payment succeeded, shipping never happened) is caught by an automated timeout/alert rather than waiting for a customer complaint.

## Interview Story

This maps directly to "trace a request across seven services" arriving as a real support investigation with no shortcut available. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a choreographed order-fulfillment pipeline charged a customer but never shipped their order, and support could not determine why within two hours.
- **Task:** find out whether Shipping ever received, or correctly processed, the `PaymentCharged` event — without any existing mechanism connecting the two services' independent event handling.
- **Action:** manually correlated timestamps and `orderId` values across three services' separate log files, since no correlation ID or distributed trace existed to do it automatically.
- **Result:** added a mandatory `correlationId` field to every event schema and adopted OpenTelemetry trace propagation, so any future investigation of this shape becomes a single trace lookup instead of a manual, multi-log reconstruction.

## Staff-Level Discussion

Choreography's core appeal — services react to events independently, with no central coordinator to become a bottleneck or single point of failure — is inseparable from a real operability cost: nothing in the pattern itself gives an investigator a way to answer "what happened to this one business transaction across every service that touched it," and that capability has to be deliberately built, not assumed to come along for free. The honest answer to "how do you trace a request across many choreographed services" is that you can't, unless the tracing mechanism was designed in from the start — which is precisely the kind of answer that distinguishes a Senior-level description of choreography's benefits from a Staff-level accounting of its full cost. The organizational fix (a mandatory `correlationId` field enforced at the schema level, not left as an optional convention) matters because an optional field is exactly as reliable as this incident's original system, where a genuinely useful practice existed nowhere until an unexplainable production incident forced it into existence.

## Related Handbook Chapters

- [Event-Driven Architecture: Integration Styles](../syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md) — canonical choreography-versus-orchestration trade-off and the `ChoreographyTraceabilityDemo` this incident reproduces.
- [Logging, Metrics, Tracing, and OpenTelemetry](../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md) — the distributed-tracing mechanism adopted as this incident's permanent fix.
