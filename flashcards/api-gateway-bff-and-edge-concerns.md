---
title: "Flashcards: API Gateway, BFF, and Edge Concerns"
slug: api-gateway-bff-and-edge-concerns
document_type: flashcard-deck
domain: system-design
topic_id: T-911
canonical: ../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md
last_updated: 2026-09-01
---

# Flashcards: API Gateway, BFF, and Edge Concerns

**Canonical chapter:** [`syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md`](../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md)

## Card: How is a gateway different from a load balancer?

**Prompt:**
What's the real, structural difference between an API gateway and a load
balancer?

**Answer:**
A load balancer distributes traffic across multiple, identical instances of
one service. A gateway routes requests to *different* services based on
request attributes (typically path) and centralizes cross-cutting concerns
(auth, rate limiting, logging) those services would otherwise each implement
independently.

**Why it matters:**
Conflating the two leads to designs where cross-cutting concerns get
duplicated inconsistently across services.

**Common trap:**
Treating "gateway" and "load balancer" as interchangeable terms.

**Related:**
[syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md](../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md), [Load Balancing, Service Discovery, and Health Checking](../syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md)

## Card: Does centralizing an edge concern actually stop a bad request from reaching a backend?

**Prompt:**
If a gateway rejects a request for a missing API key, does the backend ever
see that request?

**Answer:**
No — measured directly: a real backend's own request counter stayed at 0
for a request the gateway rejected with a real 401, proving the check
happens once, centrally, before any routing or forwarding occurs.

**Why it matters:**
It's the concrete proof of why centralizing a cross-cutting concern is more
reliable than duplicating it per service — one enforcement point, not many
chances to get it wrong.

**Common trap:**
Assuming a gateway "adds" a check on top of backend-level enforcement rather
than replacing the need for per-service duplication entirely.

**Related:**
[syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md](../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md)

## Card: What's the real mechanism behind a BFF's speed benefit?

**Prompt:**
Why is calling a BFF endpoint once faster than a client calling the same two
backends directly and sequentially?

**Answer:**
The BFF fans out to both backends *concurrently* server-side, so the client
only pays for the slowest of the two calls plus one round trip — not both
calls' latency added together plus two round trips. Measured directly: ~300ms
sequential (two 150ms calls, one after another) vs. ~150ms concurrent (both
150ms calls running in parallel).

**Why it matters:**
It's the real, measurable justification for the BFF pattern — not
architectural preference, an actual latency reduction.

**Common trap:**
Assuming a BFF is slower because it's "an extra hop," missing that its
internal fan-out is concurrent, not sequential.

**Related:**
[syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md](../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md)
