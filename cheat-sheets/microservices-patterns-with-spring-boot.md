---
title: "Cheat Sheet: Microservices Patterns with Spring Boot"
slug: microservices-patterns-with-spring-boot
document_type: cheat-sheet
domain: 05-spring
topic_id: T-519
canonical: ../syllabus/05-spring/microservices-patterns-with-spring-boot.md
last_updated: 2026-09-16
---

# Microservices Patterns with Spring Boot

**Canonical chapter:** [`syllabus/05-spring/microservices-patterns-with-spring-boot.md`](../syllabus/05-spring/microservices-patterns-with-spring-boot.md)

## Core Mental Model

Three independent layers stacked on one inter-service call: reachability (fixed URL or service
discovery), a declarative client (`@HttpExchange` or Feign) that turns an interface into a real HTTP call,
and a `CircuitBreaker` decorating that call to fail fast once recent history looks unhealthy. Each layer
fails independently — identify which one a given symptom belongs to before proposing a fix.

## Essential Definitions

- **`@HttpExchange`** — Spring Framework 6.0+'s own declarative HTTP client annotation; `HttpServiceProxyFactory` builds a real runtime proxy implementing the interface. No Spring Cloud dependency required.
- **`CircuitBreaker`** (Resilience4j) — `CLOSED` (calling normally) → `OPEN` (rejecting immediately, real `CallNotPermittedException`, no network call) → `HALF_OPEN` (a few trial calls) → back to `CLOSED` or `OPEN`.
- **Sliding window** — a rolling record of the last N *calls* (not just failures) the breaker uses to compute a failure/slow-call rate.
- **Slow-call detection** — `slowCallDurationThreshold` + `slowCallRateThreshold`, an opt-in second failure signal separate from `failureRateThreshold`.

## Decision Table

| Situation | Lean toward | Real evidence |
|---|---|---|
| Address stable, known at deploy time | Fixed URL | This chapter's own lab |
| Instances scale/reschedule independently | Service discovery | `../11-system-design/load-balancing-service-discovery-and-health-checking.md` |
| New service, no existing Feign clients | `@HttpExchange` | Real proxy, real captured request/response |
| Existing Feign-heavy codebase | Stay with OpenFeign | Consistency > marginal technical edge |
| Downstream could succeed *slowly*, not just fail | Configure `slowCallDurationThreshold`/`slowCallRateThreshold` | Real: default config stayed `CLOSED` through 5×300ms calls; slow-aware config `OPEN`ed on call 5 |
| Call site invokes another method in the same bean | Programmatic `decorateSupplier`, not `@CircuitBreaker` | Same AOP self-invocation trap as `@Transactional` |

## Common Pitfalls

- Treating "we have a circuit breaker" as a complete resilience answer without stating whether slow-call detection is configured — the chapter's own real, executed central finding.
- Assuming the sliding window resets when a failure streak starts; it's a rolling record including prior successes (real demo opened on the 4th failing call of a 5-window, not the 5th, because 1 prior success was still in the window).
- Calling an `@CircuitBreaker`-annotated method from another method in the same class — the AOP proxy never engages (self-invocation).
- Sharing one `CircuitBreaker` instance across unrelated downstreams — one dependency's failures trip a breaker guarding an unrelated call.

## Interview Answer Skeleton

**30-sec:** A declarative HTTP client (`@HttpExchange`, or Feign) wrapped in a Resilience4j
`CircuitBreaker` — the breaker tracks recent outcomes in a sliding window and fails fast once a threshold
is crossed.

**2-min:** Add: the default config only counts thrown exceptions/error responses as failures — a
downstream that succeeds slowly needs `slowCallDurationThreshold`/`slowCallRateThreshold` explicitly
configured, verified directly by running the identical slow scenario against both configs.

**Staff-level framing:** a platform-wide gap (every service's breaker missing slow-call detection because
every team copied the same incomplete example) is an organizational governance issue, not a one-service
bug.

## Related

- syllabus/11-system-design/resilience-patterns.md
- syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md
- syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md
