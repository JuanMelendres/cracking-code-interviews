---
title: "Cheat Sheet: Resilience Patterns"
slug: resilience-patterns
document_type: cheat-sheet
domain: system-design
topic_id: T-515
canonical: ../handbook/system-design/resilience-patterns.md
last_updated: 2026-08-03
---

# Resilience Patterns

**Canonical chapter:** [`syllabus/11-system-design/resilience-patterns.md`](../syllabus/11-system-design/resilience-patterns.md)

## Core Mental Model

Every resilience pattern answers one question: what should this service do while a dependency is unhealthy, instead of pretending it's healthy? A timeout stops waiting past a bound. A circuit breaker stops asking at all once the answer is predictably "no." Retry with jitter asks again, staggered so a recovering dependency isn't hit by every caller simultaneously. A bulkhead makes sure one unhealthy dependency can only exhaust the resources allocated to it, not the resources every other dependency also needs.

## Essential Definitions

- **Circuit breaker states** — `CLOSED` (normal, calls pass through), `OPEN` (calls fail fast without reaching the downstream, after N consecutive failures), `HALF_OPEN` (a single trial call allowed through after a cool-down, to test recovery).
- **Jitter / synchronization problem** — exponential backoff without jitter still has every client compute the *identical* delay on the same attempt number: a synchronization failure. Full jitter = a random delay uniformly between 0 and the exponential cap.
- **Timeout selection** — derive the timeout from a latency percentile chosen for the specific cost of a false timeout vs. the cost of waiting too long — often p99 as a starting point.
- **Bulkhead** — partitions a limited resource (thread pool, connection pool) per-dependency, so one dependency's slowdown can only exhaust its own allocated slice. Same principle as a ship's bulkheads containing flooding to one compartment.

## Decision Table

| Pattern | Benefit | Cost |
|---|---|---|
| Circuit breaker | Fails fast (~0ms) once a downstream is predictably down, protecting caller resources | Adds state/config; a too-low threshold trips on transient blips |
| Retry with jitter | Handles transient failures without synchronized retry storms | Adds latency; needs a retry budget to avoid amplifying real outages |
| Timeout tuned to a latency percentile | Bounds worst-case wait; frees resources sooner | Set too low, treats slow-but-successful calls as failures |
| Bulkhead (per-dependency pool) | One dependency's slowdown can't starve others | More resource partitions to size and operate |

| Situation | What to reach for |
|---|---|
| Shared pool exhaustion from one dependency | Bulkhead |
| Calls hitting a clearly-down downstream | Circuit breaker |
| Transient failures | Retry with exponential backoff + jitter, capped by a retry budget |
| Calls hang indefinitely | Timeout derived from a latency percentile |

## Key Numbers (real, executed — `CircuitBreakerDemo.java`, `RetryBackoffJitterDemo.java`)

```
Without breaker: 10/10 calls attempted, 4 succeeded, 2046ms total (every call pays full 200ms cost)
With breaker (threshold=3, open 500ms), 20 attempts:
  15 reached downstream (200ms each), 5 rejected fast (~0ms), 12 succeeded, 3569ms total
  -> 5 calls that would have cost 1000ms total instead cost ~0ms
```

```
No-jitter retry delays (5 clients, same attempt#): attempt1: 100,100,100,100,100ms (fully synchronized)
With-jitter delays,     attempt1: 72,68,30,27,66ms (spread across the window)
```

## Common Pitfalls

- Treating "retry until success" as a reliability strategy — unbounded retries under a real outage amplify load on an already-struggling downstream
- Retrying without jitter, creating synchronized retry storms
- Sharing one resource pool across multiple dependencies with different failure/latency profiles, letting one starve the others

## Interview Answer Skeleton

**30-sec:** Timeouts bound waits, retries handle transient failures, circuit breakers stop calling a downstream that's predictably down, bulkheads isolate one dependency's failure from starving others. Retries need jitter or they synchronize into a storm.

**2-min:** Add why they exist (dependencies fail, pretending otherwise wastes resources) + the measured breaker savings (5 calls: 1000ms → ~0ms) + the no-jitter-vs-jitter synchronized-delay comparison.

**Whiteboard:** Draw the circuit-breaker state diagram (`CLOSED → OPEN → HALF_OPEN → CLOSED`); annotate `OPEN` as "fails fast, ~0ms, no call reaches the downstream" to make the measured savings concrete.

**Staff-level framing:** the jitter measurement — 5 clients perfectly synchronized without jitter vs. spread across the full window with it — is a small-scale demonstration of a failure mode behind real, well-documented production outages at scale. Retry logic deserves the same design rigor as the primary request path.

## Production Warning Signs

- **Real incident pattern:** a brief 2-second network blip triggers a synchronized retry storm on recovery, knocking the downstream over again — worse and longer than the original blip.
- Monitoring signature: sharp, narrow load spikes at intervals matching the exponential backoff schedule (100ms, 200ms, 400ms, 800ms) — a fingerprint of missing jitter.
- Prevention: any retry logic without jitter, on a client population that can spike in sync, is a latent retry-storm risk.

## Related

- `syllabus/10-distributed-systems/distributed-systems-failure-modes.md`
- `syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md`
