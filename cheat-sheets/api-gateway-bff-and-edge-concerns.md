---
title: "Cheat Sheet: API Gateway, BFF, and Edge Concerns"
slug: api-gateway-bff-and-edge-concerns
document_type: cheat-sheet
domain: system-design
topic_id: T-911
canonical: ../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md
last_updated: 2026-09-01
---

# API Gateway, BFF, and Edge Concerns

**Canonical chapter:** [`syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md`](../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md)

## Core Mental Model

Three concentric layers between a client and a system of services, each solving a different problem. The **API gateway** is the single front door: it routes to the right backend and enforces shared concerns (auth, rate limiting, logging) exactly once instead of once per service. A **BFF** is a specialized gateway variant shaped around one client's actual needs — it fans out to multiple backends and reshapes/aggregates their responses into exactly what that client wants, in one round trip instead of several. **Edge concerns** (TLS termination, WAF, DDoS mitigation) sit even further out, at the infrastructure/CDN layer.

## Essential Definitions

- **API gateway** — a single entry point that routes to *different* services (not identical instances) and centralizes cross-cutting concerns those services would otherwise each reimplement.
- **BFF (Backend for Frontend)** — a gateway variant dedicated to one client type, aggregating and reshaping calls to multiple backends into a single, tailored response.
- **Edge concerns** — TLS termination, WAF rules, DDoS protection, CDN edge caching, geo-routing; deliberately pushed as far from application code as possible.
- **Load balancer vs. gateway** — a load balancer distributes traffic across identical instances of *one* service; a gateway routes to genuinely different services.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Do multiple backend services need identical cross-cutting enforcement? | A shared API gateway enforcing it once |
| Does a client type routinely need data assembled from several backends in one screen? | A dedicated BFF for that client type |
| Are TLS termination, WAF, or DDoS currently implemented per-service? | Push them to infrastructure/edge |
| Is a generic gateway shape causing one client type many follow-up calls? | A BFF tailored to that client |

**Trade-offs:**

| Layer | Primary job | Client-facing shape |
|---|---|---|
| Load balancer | Distribute across identical instances | Unchanged |
| API gateway | Route to different services + centralize concerns | Mostly pass-through |
| BFF | Aggregate + reshape for one client type | Tailored, client-specific |
| CDN/edge | TLS termination, WAF, DDoS, edge caching | Unchanged |

## Key Numbers (real, executed against a real HTTP gateway routing to real backend processes)

Centralized edge concern stopping a bad request before any backend is reached:

```
=== Request with NO API key ===
Real gateway status: 401
Real orders backend request count: 0 (the backend was NEVER reached)
```

BFF concurrent fan-out vs. sequential direct client calls (two 150ms-delay backends):

```
=== Client calling both backends directly, sequentially (2 round trips) ===
Real total client time: 357ms

=== Client calling the BFF endpoint ONCE (gateway fans out concurrently) ===
Real total client time: 159ms
```

## Common Pitfalls

- Treating an API gateway as "just a load balancer with routing," missing that its real value is centralizing cross-cutting concerns.
- Building one generic gateway response shape and expecting every client to adapt, instead of introducing a BFF where needs genuinely diverge.
- Letting a BFF accumulate real business logic beyond aggregation and reshaping, creating a second source of truth.
- Underestimating a shared gateway's blast radius — an incident there affects every service behind it.

## Interview Answer Skeleton

**30-sec:** An API gateway is a single entry point routing to different services and centralizing cross-cutting concerns so they're enforced once. A BFF is a specialized gateway per client type that aggregates and reshapes multiple backend calls into one tailored response, cutting round trips.

**2-min:** Add the real 401-with-zero-backend-hits proof for centralized edge enforcement, and the measured ~2x BFF speedup (357ms sequential vs. 159ms concurrent fan-out) — the client also made one round trip instead of two.

**Whiteboard:** Client → gateway → several backends fanning out — label the gateway "routing + auth + rate limiting, once." Second diagram: client → BFF, which fans out to two backends *concurrently* (both arrows leaving at the same time) and merges results before one arrow returns — label it "one round trip, tailored shape."

**Staff-level framing:** Frame a shared gateway as a platform-level, cross-team dependency requiring its own governance (who owns default limits, who approves new routes, who's on call) — discuss the organizational cost of centralization alongside its technical benefits.

## Production Warning Signs

- A mobile app's dashboard screen making 6 sequential API calls on every load, causing noticeable startup latency on slower networks — collapse into a BFF endpoint fanning out concurrently server-side (data-center inter-service latency vs. mobile WAN latency).
- A cross-cutting concern (auth, rate limiting) implemented inconsistently across services instead of centrally — a strong signal it should live in the gateway.
- A single gateway becoming a shared bottleneck — mitigate with genuine redundancy and per-route isolation (circuit breakers, timeouts per backend).

## Related

- `syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md`
- `syllabus/11-system-design/realtime-delivery-websocket-sse-and-long-polling.md`
- `syllabus/05-spring/spring-webflux-and-reactive-programming.md`
