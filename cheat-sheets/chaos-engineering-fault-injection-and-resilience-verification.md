---
title: "Cheat Sheet: Chaos Engineering"
slug: chaos-engineering-fault-injection-and-resilience-verification
document_type: cheat-sheet
domain: 13-observability
topic_id: T-2423
canonical: ../syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md
last_updated: 2026-09-21
---

# Chaos Engineering: Fault Injection and Resilience Verification

**Canonical chapter:** [`syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md`](../syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md)

## Core Mental Model

SLOs define acceptable, burn-rate alerting detects a violation, incident response reacts. Chaos engineering proactively verifies the first two actually work, by deliberately, safely injecting real failure before a real incident forces the question.

## Essential Definitions

- **Chaos engineering** — hypothesis-driven, controlled experiments injecting real failure to verify resilience/alerting claims actually hold.
- **Steady-state hypothesis** — a measurable, falsifiable claim about normal system behavior, stated before the experiment runs.
- **Blast radius** — the scoped, minimized slice of real traffic an experiment targets; what makes it responsible rather than reckless.

## Five Real Principles (Principles of Chaos Engineering)

| # | Principle |
|---|---|
| 1 | Build a hypothesis around steady-state behavior |
| 2 | Vary real-world events (not synthetic, unrealistic faults) |
| 3 | Run experiments in production |
| 4 | Automate experiments to run continuously |
| 5 | Minimize blast radius |

## Common Pitfalls

- Treating chaos engineering as "randomly breaking things in prod" rather than a hypothesis-driven, reversible, minimized-blast-radius experiment.
- Conflating chaos engineering (verification practice) with circuit breakers/retries (the implementation being verified) — different things, complementary.
- Running an experiment once, fixing the gap, and never automating it — regressions go undetected until the next real incident.

## Interview Answer Skeleton

**30-sec:** Chaos engineering is deliberately, safely injecting real failure at a minimized blast radius, with a measurable steady-state hypothesis, to verify resilience and alerting claims actually hold — instead of finding out during a real incident.

**2-min:** Add: distinct from a circuit breaker (implementation) — chaos engineering is the verification practice that confirms the breaker (and its alerting) actually works. Real demo: a fault injected against a 25% canary cohort, detected by real burn-rate monitoring in ~1.1s/12 requests, recovered ~310ms after rollback.

**Staff-level framing:** Deciding which failure modes justify automated, recurring experiments (vs. one-time manual game days) is a real risk/investment trade-off, including the organizational buy-in needed to run experiments against real production traffic safely.

## Related

- syllabus/13-observability/metric-cardinality-and-alert-fatigue.md
- syllabus/13-observability/incident-response-and-blameless-postmortems.md
- syllabus/11-system-design/resilience-patterns.md
