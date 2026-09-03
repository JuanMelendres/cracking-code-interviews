---
title: "Cheat Sheet: Spring Boot Actuator, Health, and Observability Hooks"
slug: spring-actuator-health-and-observability-hooks
document_type: cheat-sheet
domain: spring
topic_id: T-516
canonical: ../handbook/spring/spring-actuator-health-and-observability-hooks.md
last_updated: 2026-09-01
---

# Spring Boot Actuator, Health, and Observability Hooks

**Canonical chapter:** [`syllabus/05-spring/spring-actuator-health-and-observability-hooks.md`](../syllabus/05-spring/spring-actuator-health-and-observability-hooks.md)

## Core Mental Model

Actuator is not one feature — it's three composable pieces sharing one HTTP surface: **health** (an aggregate, pluggable up/down signal built from every registered `HealthIndicator`), **metrics** (Micrometer's `MeterRegistry`, exposed read-only over HTTP), and **endpoints** (a generic mechanism for exposing arbitrary operational data or actions — `health` and `metrics` are themselves just two built-in examples). Every one of these is opt-in for HTTP exposure by default except `health` and `info` — the framework's default posture is "assume you don't want your internals on the network until you say so."

## Essential Definitions

- **`HealthIndicator`** — a bean whose status feeds into one overall `/actuator/health` result; Boot's `StatusAggregator` reports the worst status among all contributors by default.
- **`@Endpoint` + `@ReadOperation`/`@WriteOperation`** — a distinct, protocol-agnostic exposure mechanism from `@RestController`, resolved through Boot's own `EndpointDiscoverer`, not Spring MVC's `RequestMappingHandlerMapping`.
- **`MeterRegistry`** — Micrometer's registry; any bean can inject it and register a `Counter`/`Timer`/`Gauge`, which shows up under `/actuator/metrics/{name}` with zero additional wiring.
- **`ApplicationAvailability`** — real, live readiness/liveness state; driven by publishing an `AvailabilityChangeEvent`, the same signal Boot's own graceful-shutdown machinery uses internally.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Does a dependency's unavailability mean this instance genuinely shouldn't receive traffic? | A custom `HealthIndicator` for it, feeding the aggregate status |
| Does an endpoint reveal configuration, secrets, or memory contents (`env`, `heapdump`, `configprops`)? | Keep it off the exposure list except in tightly controlled environments |
| Does the operational data need a custom shape not covered by metrics or health? | A custom `@Endpoint` |
| Is the deployment target Kubernetes (or another HTTP-health-check orchestrator)? | Enable and wire readiness/liveness health groups to real application state |

**Trade-offs:**

| Mechanism | Exposed by default? | Backing data |
|---|---|---|
| `/actuator/health` | Yes | Aggregated `HealthIndicator`s |
| `/actuator/info` | Yes | Static `info.*` properties/build info |
| `/actuator/metrics/{name}` | No (opt-in) | Micrometer `MeterRegistry` |
| Custom `@Endpoint` | No (opt-in) | Whatever `@ReadOperation` returns |
| `/actuator/health/readiness`\|`/liveness` | Opt-in (`probes.enabled`, auto in K8s) | `ApplicationAvailability` |

## Key Numbers (real, executed against Spring Boot 3.3.5 + Actuator + Micrometer)

Health aggregation:

```
Real /actuator/health body (downstream UP): {"status":"UP", ...}
Real /actuator/health body after flipping downstream DOWN: {"status":"DOWN", ...}
```

Default-exposure security result: `/actuator/health` → real `200`; `/actuator/beans` and a custom `/actuator/greetingStats` → real `404`s, with zero explicit configuration.

Readiness transition:

```
Real readiness before: {"status":"UP"}
Real readiness after REFUSING_TRAFFIC: {"status":"OUT_OF_SERVICE"}
```

## Common Pitfalls

- Assuming `/actuator/health`'s aggregate status alone is enough to diagnose a problem, without checking the per-component breakdown (`show-details=always` required).
- Setting `management.endpoints.web.exposure.include=*` "temporarily" during a debugging session and never reverting it.
- Building a custom `@Endpoint` as if it were a `@RestController`, missing `@ReadOperation`/`@WriteOperation` and Boot's own discovery mechanism.
- Wiring K8s probes to `/actuator/health` generically instead of the dedicated `/readiness`/`/liveness` groups, losing the "should receive traffic" vs. "should be restarted" distinction.

## Interview Answer Skeleton

**30-sec:** Actuator exposes health, metrics, and custom `@Endpoint`s over one HTTP surface — but only `health` and `info` by default. A custom `HealthIndicator` genuinely drives the aggregate status, and readiness/liveness probes reflect real, live `ApplicationAvailability` state.

**2-min:** Add the real health-flip proof (UP → DOWN), the real 404s proving secure-by-default exposure, and the real readiness transition (UP → OUT_OF_SERVICE) via a real `AvailabilityChangeEvent`.

**Whiteboard:** Draw three boxes — Health, Metrics, Endpoints — feeding into one "/actuator/*" surface. Inside Health, several small `HealthIndicator` boxes feed into one "aggregate status" diamond; shade one red and show the diamond turning red too. Draw a lock icon over every box except Health and Info, labeled "opt-in only."

**Staff-level framing:** Frame Actuator's exposure configuration as a genuine security control requiring the same review discipline as any other security-relevant setting, and discuss designing custom health checks around "can this instance actually serve traffic" rather than "is everything perfect."

## Production Warning Signs

- `/actuator/env` publicly reachable for months, leaking configuration property names — check for a forgotten `management.endpoints.web.exposure.include=*` added during a debugging session.
- An expected Actuator endpoint returns 404 — check the exposure include list before assuming a routing/deployment problem.
- A custom `@Endpoint` never appears even when included — confirm the bean is actually registered (`@Component` + `@Endpoint`) and its `id` matches exactly.

## Related

- `syllabus/05-spring/spring-testing-slices-and-context-caching.md`
- `syllabus/15-cloud/twelve-factor-config.md`
- `syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md`
