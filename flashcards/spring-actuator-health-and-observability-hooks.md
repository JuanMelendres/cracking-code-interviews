---
title: "Flashcards: Spring Boot Actuator, Health, and Observability Hooks"
slug: spring-actuator-health-and-observability-hooks
document_type: flashcard-deck
domain: spring
topic_id: T-516
canonical: ../handbook/spring/spring-actuator-health-and-observability-hooks.md
last_updated: 2026-09-01
---

# Flashcards: Spring Boot Actuator, Health, and Observability Hooks

**Canonical chapter:** [`syllabus/05-spring/spring-actuator-health-and-observability-hooks.md`](../syllabus/05-spring/spring-actuator-health-and-observability-hooks.md)

## Card: Does each HealthIndicator's status stay independent, or affect the overall result?

**Prompt:**
If one custom `HealthIndicator` reports `DOWN` while everything else is `UP`,
what does `/actuator/health` report overall?

**Answer:**
`DOWN` — Boot's default `StatusAggregator` reports the worst status among all
registered contributors as the overall result. Measured directly: flipping
one custom indicator's availability flag flipped the real, overall
`/actuator/health` status from UP to DOWN.

**Why it matters:**
It's the mechanism that lets orchestrators make correct traffic-routing
decisions based on one real, aggregate signal.

**Common trap:**
Assuming each indicator's status is shown independently with no effect on the
overall result.

**Related:**
[handbook/spring/spring-actuator-health-and-observability-hooks.md](../syllabus/05-spring/spring-actuator-health-and-observability-hooks.md)

## Card: What's exposed over HTTP by default?

**Prompt:**
With zero Actuator configuration beyond adding the dependency, which
endpoints are reachable over HTTP?

**Answer:**
Only `health` and `info` — every other endpoint, built-in (`beans`, `env`,
`metrics`) or custom, requires explicit inclusion in
`management.endpoints.web.exposure.include`. Measured directly: real 404s for
both `/actuator/beans` and a custom `/actuator/greetingStats` endpoint under
default configuration.

**Why it matters:**
A forgotten wildcard exposure override is a real, common security
misconfiguration with real consequences (leaking config, secrets, heap
dumps).

**Common trap:**
Assuming a documented, built-in endpoint must be reachable by default.

**Related:**
[handbook/spring/spring-actuator-health-and-observability-hooks.md](../syllabus/05-spring/spring-actuator-health-and-observability-hooks.md)

## Card: How do you programmatically control a Kubernetes readiness probe's result?

**Prompt:**
How can application code make `/actuator/health/readiness` report
`OUT_OF_SERVICE` on demand?

**Answer:**
Publish a real `AvailabilityChangeEvent` with `ReadinessState.REFUSING_TRAFFIC`
— the exact signal Boot's own graceful-shutdown machinery uses internally.
Measured directly: the real readiness probe response flipped from
`{"status":"UP"}` to `{"status":"OUT_OF_SERVICE"}` immediately after
publishing the event.

**Why it matters:**
It proves readiness reflects real, live, drivable application state, not a
static value — the exact mechanism a real Kubernetes deployment relies on to
stop routing traffic to a draining instance.

**Common trap:**
Assuming readiness/liveness probes are static, always-UP endpoints with no
real application-state connection.

**Related:**
[handbook/spring/spring-actuator-health-and-observability-hooks.md](../syllabus/05-spring/spring-actuator-health-and-observability-hooks.md)
