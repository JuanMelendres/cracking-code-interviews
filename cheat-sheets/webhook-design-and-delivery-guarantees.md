---
title: "Cheat Sheet: Webhook Design and Delivery Guarantees"
slug: webhook-design-and-delivery-guarantees
document_type: cheat-sheet
domain: 07-api-design
topic_id: T-2415
canonical: ../syllabus/07-api-design/webhook-design-and-delivery-guarantees.md
last_updated: 2026-09-18
---

# Webhook Design and Delivery Guarantees

**Canonical chapter:** [`syllabus/07-api-design/webhook-design-and-delivery-guarantees.md`](../syllabus/07-api-design/webhook-design-and-delivery-guarantees.md)

## Core Mental Model

A webhook receiver is a small public API endpoint with a security and idempotency problem built in by
design. Three concerns aren't optional hardening — they're the actual production design: HMAC signature
verification, retry-aware failure handling, and delivery-ID deduplication.

## Essential Definitions

- **Webhook** — a provider-initiated HTTP `POST` to a consumer-registered URL when an event occurs; inverts the normal client-initiates-the-request direction.
- **HMAC signature** — a hash of the payload computed with a shared secret, proving authenticity and integrity, NOT confidentiality (HTTPS still required).
- **Exponential backoff** — increasing wait time between retries; only for `5xx`/transient failures, never for a `4xx` (permanent rejection).
- **At-least-once delivery** — the realistic guarantee every real provider offers; the same event can arrive twice, making receiver-side deduplication mandatory.

## Decision Table

| Situation | What to reach for |
|---|---|
| Any webhook receiver on a public endpoint | HMAC signature verification with a constant-time comparison (`MessageDigest.isEqual`), before any business logic |
| Receiver returns 5xx or times out | Sender retries with exponential backoff |
| Receiver returns 4xx (e.g., bad signature) | Sender does NOT retry — permanent rejection |
| Same delivery ID arrives twice | Receiver recognizes and skips it — dedupe before any mutating action |
| Payload contains sensitive data | HMAC alone isn't enough — also require HTTPS |

## Common Pitfalls

- Treating "the URL is unguessable" as sufficient security instead of real signature verification.
- Comparing signatures with `String.equals` instead of a constant-time comparison — leaks timing information.
- Retrying a `4xx` the same way as a `5xx` — wastes load for zero benefit.
- Writing a handler with no delivery-ID deduplication, assuming a webhook fires exactly once.

## Interview Answer Skeleton

**30-sec:** A webhook is a provider-initiated `POST`. Production design needs HMAC signature verification
(constant-time compare), retry-with-backoff distinguishing `5xx` from `4xx`, and delivery-ID deduplication —
because every real provider guarantees only at-least-once delivery.

**2-min:** Add: HMAC proves authenticity/integrity, not confidentiality (HTTPS still needed); real measured
evidence — a flaky receiver (two real `503`s) recovered via real 50ms/100ms exponential backoff; a real
duplicate delivery (same ID sent twice) was processed exactly once via receiver-side dedup.

**Staff-level framing:** at scale, webhook delivery is a small distributed system of its own — a durable
delivery queue, a dead-letter mechanism for receivers down longer than the retry window, and a replay API
for consumers recovering from an outage.

## Related

- syllabus/07-api-design/api-design.md
- syllabus/11-system-design/idempotency.md
- syllabus/12-security/owasp-top-10-for-backend-services.md
