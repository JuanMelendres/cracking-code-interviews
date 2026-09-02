---
title: "Cheat Sheet: Load Balancing, Service Discovery, and Health Checking"
slug: load-balancing-service-discovery-and-health-checking
document_type: cheat-sheet
domain: system-design
topic_id: T-805
canonical: ../handbook/system-design/load-balancing-service-discovery-and-health-checking.md
last_updated: 2026-09-02
---

# Load Balancing, Service Discovery, and Health Checking

**Canonical chapter:** [`handbook/system-design/load-balancing-service-discovery-and-health-checking.md`](../handbook/system-design/load-balancing-service-discovery-and-health-checking.md)

## Core Mental Model

A load balancer is only as good as the information it's routing on, and that information is always at least slightly stale. Round-robin routes on no information at all — every backend looks identical, which is exactly wrong the moment backends aren't identical. Least-connections routes on real, current in-flight load, a better signal that still lags reality slightly. Health checking is the same story one level up: "healthy" is a fact about the *last* check, not a live fact about right now — a real, boundable gap (check interval plus timeout), not zero. Every design decision here manages that inherent staleness rather than eliminating it.

## Essential Definitions

- **Load balancing** — distributing incoming requests across backend instances via a selection algorithm so no single instance is overwhelmed.
- **Service discovery** — the mechanism by which a client (or load balancer) learns which backend instances currently exist and are reachable.
- **Health checking** — deciding whether a known instance can actually serve a request right now, distinct from merely existing.
- **Active health checking** — the load balancer proactively polls each backend's health endpoint on a fixed interval, independent of real traffic.
- **Passive health checking** — infers health from real production traffic (e.g., ejecting after N consecutive real failures); by definition detects only after a real failure.
- **L4 vs. L7** — L4 routes on IP/port alone (fast, content-blind); L7 terminates and inspects the actual HTTP request, enabling path/header-based routing.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Backends have genuinely variable request cost | Least-connections or least-response-time |
| Correctness/performance needs the same client on the same backend | Consistent hashing (accept its rebalancing cost) |
| Multiple teams/languages need to reach this service | Server-side discovery (avoids per-client coupling) |
| A specific detection-latency bound is required | State it as a number (interval + timeout) and tune active-check frequency deliberately |
| The failure mode of concern doesn't show up in a synthetic health check | Add passive checking on real traffic |

**Algorithm comparison:**

| | Round-robin | Least-connections | Consistent hashing |
|---|---|---|---|
| Signal used | None | Real, live in-flight count | Deterministic hash of a request property |
| Adapts to uneven backend cost | No (measured ~4.4x cost from this gap) | Yes, automatically | Not its purpose |
| Best fit | Genuinely homogeneous backends | Variable request cost | Session affinity / cache locality |

## Key Numbers (real, executed against a real reverse proxy + backend processes)

- Round-robin, 300 requests across 2 fast (5ms) + 1 slow (200ms) backend: total batch time 921ms.
- Least-connections, identical scenario: total batch time 208ms — a measured ~4.4x improvement from the algorithm alone, zero backend change. Slow backend received only 10 of 300 requests vs. round-robin's blind 100 of 300.
- Real backend killed mid-run against an active health checker polling every 300ms: detection latency 206ms; 12 requests fired immediately after all correctly landed on the 2 remaining healthy backends. Re-detection after restart: 70ms.

## Common Pitfalls

- Assuming round-robin distributes *load* evenly because it distributes *requests* evenly.
- Treating "the load balancer detects failures instantly" as true instead of naming the real, bounded detection latency (interval + timeout).
- Relying on active health checking alone for failure modes a synthetic health endpoint doesn't actually exercise.
- Confusing client-side and server-side discovery, or not naming client-side discovery's real per-client coupling cost.
- A health endpoint that returns 200 unconditionally, creating false confidence worse than no check at all.

## Interview Answer Skeleton

**30-sec:** A load balancer distributes requests using a selection algorithm — round-robin uses no runtime signal and can badly overload a slower backend; least-connections uses real, live in-flight load and adapts automatically. Health checking keeps the load balancer's view of "which backends are alive" current, with a real, bounded staleness window between failure and detection.

**2-min:** Add the measured numbers: round-robin's 921ms vs. least-connections' 208ms on the identical 300-request batch (~4.4x), and the real 206ms detection latency after killing a backend, with the next 12 requests correctly routed around it.

**Whiteboard:** Draw a load balancer with arrows to three backends, one visibly larger/slower. Round-robin: three equal-width arrows regardless — visibly wrong. Least-connections: arrows of different widths, thinner into the slow one. Add a clock icon by the health-checker box labeled "interval + timeout = real detection lag."

**Staff-level framing:** Ground algorithm choice in the actual runtime signal used, state detection latency as a real bound rather than "the load balancer handles it," and articulate the concrete operational cost difference between client-side and server-side discovery rather than treating them as interchangeable.

## Production Warning Signs

- Uneven latency across "equal" backend instances under load — check the load-balancing algorithm first; round-robin routing on no signal is a common, fully explanatory cause.
- Requests briefly failing after a backend crash before eventually stopping — expected, bounded behavior (interval + timeout); if unacceptably wide, shorten the interval or add passive checking.
- A backend repeatedly marked healthy then unhealthy (flapping) — usually a too-aggressive timeout relative to real response-time variance; widen the timeout or require multiple consecutive failures before ejecting.
- Health checks passing while real requests fail — a health-endpoint-doesn't-exercise-the-real-failure-mode gap; fix with passive checking on real traffic.

## Related

- `handbook/system-design/resilience-patterns.md`
- `handbook/system-design/multi-region-failover-and-disaster-recovery.md`
- `handbook/system-design/data-partitioning-and-consistent-hashing.md`
- `handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md`
