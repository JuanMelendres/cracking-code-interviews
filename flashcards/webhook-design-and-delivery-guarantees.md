---
title: "Flashcards: Webhook Design and Delivery Guarantees"
slug: webhook-design-and-delivery-guarantees
document_type: flashcard-deck
domain: 07-api-design
topic_id: T-2415
canonical: ../syllabus/07-api-design/webhook-design-and-delivery-guarantees.md
last_updated: 2026-09-18
---

# Flashcards: Webhook Design and Delivery Guarantees

**Canonical chapter:** [`syllabus/07-api-design/webhook-design-and-delivery-guarantees.md`](../syllabus/07-api-design/webhook-design-and-delivery-guarantees.md)

## Card: What does an HMAC webhook signature actually prove?

**Prompt:**
A webhook request includes an HMAC signature header. What does a valid signature actually prove, and what does it NOT provide?

**Answer:**
Proves authenticity (the sender knows the shared secret) and integrity (the payload wasn't modified in transit). Does NOT provide confidentiality — HMAC doesn't encrypt the payload, so HTTPS is still required for sensitive data.

**Why it matters:**
A common mistake is assuming a valid signature means the payload was also encrypted in transit.

**Common trap:**
Skipping HTTPS because "the payload is already signed."

**Related:**
[Core Concepts](../syllabus/07-api-design/webhook-design-and-delivery-guarantees.md#core-concepts)

## Card: Should a webhook sender retry a 401 the same way as a 503?

**Prompt:**
A webhook delivery gets a real `401` (bad signature) on one attempt and a real `503` (receiver overloaded) on another. Should the sender retry both the same way?

**Answer:**
No. A `503` is transient and worth retrying with backoff. A `401` is a permanent rejection — retrying an identical request gets an identical rejection every time, so a well-behaved sender stops immediately.

**Why it matters:**
Retrying a permanent rejection wastes load on both sides for zero benefit.

**Common trap:**
Treating every non-2xx response as equally retry-worthy.

**Related:**
[Core Concepts](../syllabus/07-api-design/webhook-design-and-delivery-guarantees.md#core-concepts)

## Card: Why is exactly-once webhook delivery not realistically offered?

**Prompt:**
Why do real webhook providers guarantee "at least once" delivery instead of "exactly once"?

**Answer:**
A provider can't always be certain a delivery was processed — a response can be lost even after the receiver successfully processed the event — so providers retry rather than risk silently losing an event, meaning the same event can genuinely arrive twice.

**Why it matters:**
This makes receiver-side deduplication (by delivery ID) mandatory, not optional hardening.

**Common trap:**
Assuming duplicate delivery is a provider bug rather than an inherent property of at-least-once semantics.

**Related:**
[Production Scenarios](../syllabus/07-api-design/webhook-design-and-delivery-guarantees.md#production-scenarios)
