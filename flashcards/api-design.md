---
title: "Flashcards: API Design"
slug: api-design
document_type: flashcard-deck
domain: system-design
topic_id: T-803
canonical: ../syllabus/07-api-design/api-design.md
last_updated: 2026-09-21
---

# Flashcards: API Design

**Canonical chapter:** [`syllabus/07-api-design/api-design.md`](../syllabus/07-api-design/api-design.md)

## Card: Why OFFSET gets slower with depth

**Prompt:**
Why does `OFFSET` pagination get slower with depth?

**Answer:**
The database must walk and discard every skipped row before returning the requested page — cost grows linearly with offset.

**Why it matters:**
A measured, ~3,000× real-world difference, not a theoretical concern.

**Common trap:**
Assuming pagination cost is roughly constant regardless of implementation.

**Related:**
[Internal Implementation](../syllabus/07-api-design/api-design.md#internal-implementation)

## Card: What keyset pagination gives up

**Prompt:**
What does keyset pagination give up in exchange for flat cost at any depth?

**Answer:**
Arbitrary page-number jumping — it can only move forward/backward from a known cursor.

**Why it matters:**
The honest trade-off a Staff-level answer states unprompted.

**Common trap:**
Presenting keyset pagination as a strict, cost-free upgrade.

**Related:**
[Core Concepts](../syllabus/07-api-design/api-design.md#core-concepts)

## Card: HATEOAS in one sentence

**Prompt:**
What does HATEOAS mean, in terms of what a response actually contains?

**Answer:**
A response includes real, state-dependent links describing the actions currently available on that resource — a client discovers what it can do next from the response, rather than hardcoding state-based rules client-side.

**Why it matters:**
This is what separates Richardson Maturity Level 3 from Level 2 — most production APIs stop at Level 2.

**Common trap:**
Confusing "uses HTTP verbs correctly" (Level 2) with HATEOAS (Level 3).

**Related:**
[Core Concepts](../syllabus/07-api-design/api-design.md#core-concepts)

## Card: RFC 9457 Problem Details' standard fields

**Prompt:**
Name the standard fields RFC 9457 Problem Details defines for an error response.

**Answer:**
`type` (a URI identifying the error kind), `title`, `status`, `detail`, `instance` (a URI identifying this specific occurrence) — plus any custom extension fields an API needs.

**Why it matters:**
Spring 6's built-in `ProblemDetail` implements this directly, served as real `application/problem+json`.

**Common trap:**
Inventing a bespoke error shape instead of reaching for this standard one.

**Related:**
[Core Concepts](../syllabus/07-api-design/api-design.md#core-concepts)

## Card: PUT vs POST idempotency

**Prompt:**
Is `PUT` idempotent? Is `POST`?

**Answer:**
`PUT` yes, by definition (full replace). `POST` only with a client-supplied idempotency key.

**Why it matters:**
The precise distinction that resolves the "is idempotent the same as read-only" confusion.

**Common trap:**
Assuming only read-only methods can be idempotent.

**Related:**
[Core Concepts](../syllabus/07-api-design/api-design.md#core-concepts)

## Card: Why a bulk endpoint can't return one status code

**Prompt:**
A client submits 100 items to a bulk-create endpoint and item 47 fails validation. Why can't the response be a single HTTP status code?

**Answer:**
Because some items can succeed while others fail for item-specific reasons — the response needs a per-item result array (index, success/failure, result or error) so the caller can map every outcome back to its specific input item.

**Why it matters:**
A single aggregate status code loses which specific items succeeded and which failed.

**Common trap:**
Returning one `400` for the whole batch, losing the 99 items that actually succeeded.

**Related:**
[Core Concepts](../syllabus/07-api-design/api-design.md#core-concepts)

## Card: The async 202 lifecycle's real completion signal

**Prompt:**
An endpoint kicks off a 30-second report job. How should it respond, and how does a client find out when it's done?

**Answer:**
Return `202 Accepted` immediately with a `Location` header pointing at a status resource — never blocking for the full 30 seconds. The client polls that resource: `202` again while running, `303 See Other` pointing at the result once done — a real, structural completion signal, not a status field the client has to inspect and branch on.

**Why it matters:**
Real, measured lifecycle: `202` → `202` + `Retry-After` → `303` → `200` (the result) — each transition a distinct, structural HTTP signal, verified with a genuinely asynchronous background job (real elapsed time ≥300ms across 7 real polls).

**Common trap:**
Blocking the HTTP response for the full duration, or building a status endpoint that only ever returns `200` with a `status` field to inspect.

**Related:**
[Internal Implementation](../syllabus/07-api-design/api-design.md#internal-implementation)
