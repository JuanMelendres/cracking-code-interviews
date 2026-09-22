---
title: "Flashcards: System Design Patterns Recognition and Quick-Reference"
slug: system-design-patterns-recognition-and-quick-reference
document_type: flashcard-deck
domain: 11-system-design
topic_id: T-2427
canonical: ../syllabus/11-system-design/system-design-patterns-recognition-and-quick-reference.md
last_updated: 2026-09-22
---

# Flashcards: System Design Patterns Recognition and Quick-Reference

**Canonical chapter:** [`syllabus/11-system-design/system-design-patterns-recognition-and-quick-reference.md`](../syllabus/11-system-design/system-design-patterns-recognition-and-quick-reference.md)

## Card: Caching vs. read replica

**Prompt:**
A requirement says "reduce repeated read load on the database." How do you decide between caching and a read replica?

**Answer:**
Reach for caching when the same computed or fetched value is read far more often than it changes and a bounded staleness window is acceptable. Reach for a read replica instead when the reader needs real SQL query flexibility (arbitrary filters, joins) against data that's only slightly stale — a cache can't answer an arbitrary new query, only the exact key it was populated with.

**Why it matters:**
Both patterns reduce database read load, but they solve genuinely different access patterns — picking the wrong one either wastes a cache on queries it can't serve, or adds replica infrastructure for a problem a simple cache would have solved more cheaply.

**Common trap:**
Reaching for "add a cache" as a reflex for any read-load problem without checking whether the actual need is query flexibility, not just repeated-value lookup.

**Related:**
[Decision Framework](../syllabus/11-system-design/system-design-patterns-recognition-and-quick-reference.md#decision-framework-when-two-patterns-both-seem-to-fit)

## Card: Rate limiting vs. circuit breaker

**Prompt:**
Both rate limiting and a circuit breaker "protect" a system under load. What's the actual mechanism difference?

**Answer:**
Rate limiting is proactive and caller-side: it bounds how much load a client sends, regardless of whether the callee is currently healthy. A circuit breaker is reactive, triggered by observed failures from the callee, and stops sending traffic specifically once that dependency is already failing.

**Why it matters:**
A robust system frequently needs both, at different layers (rate limiting typically at the edge/gateway, circuit breakers typically at each service-to-service call site) — treating them as interchangeable "protection" mechanisms misses that one is preventive and the other is responsive.

**Common trap:**
Assuming a circuit breaker alone is enough to prevent one misbehaving client from overwhelming a service — it only reacts once a dependency has already started failing, it doesn't bound a single caller's own volume.

**Related:**
[Decision Framework](../syllabus/11-system-design/system-design-patterns-recognition-and-quick-reference.md#decision-framework-when-two-patterns-both-seem-to-fit)

## Card: Idempotency vs. message deduplication

**Prompt:**
A requirement says "handle duplicate requests safely." When is that idempotency, and when is it message deduplication?

**Answer:**
Idempotency is enforced at a synchronous API/write boundary, keyed by a client-supplied identifier — a retried write returns the original result instead of repeating the effect. Message deduplication is enforced by an asynchronous consumer reading an at-least-once broker, typically keyed by the broker's own message ID or offset.

**Why it matters:**
Both prevent a duplicate effect, but they apply at different architectural boundaries (synchronous request/response vs. asynchronous broker/consumer) — the fix belongs at the boundary where the duplication actually originates.

**Common trap:**
Treating a synchronous API's retry problem and an asynchronous consumer's at-least-once delivery problem as the same fix, when they need different keys and different enforcement points.

**Related:**
[Decision Framework](../syllabus/11-system-design/system-design-patterns-recognition-and-quick-reference.md#decision-framework-when-two-patterns-both-seem-to-fit)
