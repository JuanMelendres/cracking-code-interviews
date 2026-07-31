---
title: "Design Exercise — Deployment and Infrastructure Strategy for a Payments Notification Service"
week: 15
document_type: study-pack-design-exercise
status: draft
last_reviewed: 2026-07-31
---

# Design Exercise — Deployment and Infrastructure Strategy for a Payments Notification Service

**Format:** 45 minutes, whiteboard or written. Design the full infrastructure and deployment strategy for the service described below, applying all five of this week's topics explicitly.

## The scenario

Your team owns a new service that consumes payment-completion events and sends transactional notifications (email/SMS) to customers. Requirements:

- Must process events with low latency (customers expect near-immediate notification).
- Traffic follows a strong daily pattern: ~10x higher volume during business hours than overnight.
- A notification failure must never be silently dropped — every event needs a durable, retryable delivery path.
- The team is risk-averse about deployments, since a bad release affecting customer-facing notifications is highly visible.
- The team already runs several other services on Kubernetes (EKS) and wants to be consistent with that platform.

## Design this

1. **Compute:** Where does this service run, and why (relative to the EC2/ECS/EKS/Lambda spectrum)?
2. **Resource limits and JVM sizing:** If this is a JVM service, how do you size `resources.requests`/`resources.limits` and heap, and what probes does it need?
3. **Messaging:** How do payment-completion events reach this service, and how do you guarantee no notification is silently dropped on a transient failure?
4. **Scaling economics:** Given the strong daily traffic pattern, what's your capacity/pricing strategy?
5. **Deployment strategy:** Given the team's risk-aversion, what deployment strategy do you use, and what does the promotion gate look like?

Work through your answer before reading the reference solution below.

---

## Reference Solution

**1. Compute.** EKS, consistent with the team's existing platform (per the scenario's stated constraint) — Kubernetes gives the team a consistent operational model across services, and this workload (a long-running event consumer, not a short-lived, bursty function) doesn't have the request-driven shape that would make Lambda a better fit despite its lower operational-ownership cost.

**2. Resource limits and JVM sizing.** Set `resources.requests` and `resources.limits` for memory to the same value (predictable scheduling, no throttling/OOMKill surprises), sized with an explicit accounting for both the container-aware ergonomic heap default AND non-heap memory (metaspace, thread stacks, any message-broker client library's buffer pools) — not heap alone. A `readinessProbe` checking genuine ability to process (e.g., confirming the message-consumer connection is established, not just that the HTTP server is up) so the pod isn't added to any relevant routing/consumption pool before it's actually ready; a `livenessProbe` checking for genuine deadlock/hang conditions specifically, not transient slowness; a `startupProbe` if JVM startup (Spring context init, connection establishment) takes long enough to risk a premature liveness-triggered restart.

**3. Messaging.** An event-driven trigger (e.g., a durable queue or topic the payment-completion event is published to) feeding this service, with the service's own processing wrapped in retry logic with backoff for transient send failures (an email/SMS provider being briefly unavailable), and a dead-letter path (a separate queue or store) for events that exhaust retries — ensuring a notification failure is never silently dropped, only ever explicitly parked for investigation/manual retry. This is the SQS-style durable, point-to-point delivery pattern from this week's AWS services chapter, chosen specifically because the requirement is "never silently drop," which needs a durable queue's redelivery guarantee, not a fire-and-forget call.

**4. Scaling economics.** The strong daily peak/trough pattern (10x) is exactly the shape this week's cost-economics chapter identifies as favoring autoscaling over static peak-provisioning — reserve (or otherwise commit to) only the confirmed steady overnight-trough baseline, and let a Horizontal Pod Autoscaler scale the service up for business-hours peak on-demand capacity, rather than statically provisioning for peak around the clock (which this week's Calculation 2 shows can waste over half the fleet's cost for a demand shape like this).

**5. Deployment strategy.** Canary, given the team's explicit risk-aversion and the customer-visible nature of a bad release — deploy to a small percentage of traffic first, monitor real delivery-success-rate and latency metrics for a defined window, and require an explicit human approval gate before promoting to full traffic, specifically because (per this week's CI/CD chapter) "no alert fired" during the canary window is weaker evidence than an engineer actually reviewing the canary's metrics before a customer-facing release reaches every customer.

## Self-Check

- [ ] Named a specific compute choice and justified it against the actual workload shape, not just "Kubernetes because that's what we use"
- [ ] Addressed both `requests`/`limits` sizing AND probe configuration, not just one
- [ ] Named a specific durable-delivery mechanism (not just "use a queue") and explained the dead-letter/retry path
- [ ] Connected the daily traffic pattern explicitly to an autoscaling-over-static-provisioning recommendation, with the reasoning (not just the conclusion)
- [ ] Proposed canary with an explicit human approval gate, not just "canary" as a buzzword
