---
title: "Flashcards: REST API Fundamentals"
slug: rest-api-fundamentals
document_type: flashcard-deck
domain: 07-api-design
topic_id: T-2205
canonical: ../syllabus/07-api-design/rest-api-fundamentals.md
last_updated: 2026-09-11
---

# Flashcards: REST API Fundamentals

**Canonical chapter:** [`syllabus/07-api-design/rest-api-fundamentals.md`](../syllabus/07-api-design/rest-api-fundamentals.md)

## Card: POST vs. PUT idempotency

**Prompt:**
Is `POST` idempotent? Is `PUT`?

**Answer:**
`POST` is deliberately not idempotent — calling it twice with the identical body creates two separate resources. `PUT` is idempotent by convention — calling it twice with the identical body leaves the resource in the same end state both times.

**Why it matters:**
The single most interview-relevant REST concept, and a real, verb-specific guarantee, not a vague notion of "safety."

**Common trap:**
Describing PUT as "update" and POST as "create" without the idempotency distinction, which is the part interviewers actually probe.

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)

## Card: What 201 Created communicates that 200 doesn't

**Prompt:**
Why return `201 Created` instead of `200 OK` from a resource-creation endpoint, and what should come with it?

**Answer:**
`201` communicates specifically "a new resource now exists," a distinct signal from general success. Convention pairs it with a `Location` header pointing at the new resource's real URL, so the client doesn't have to construct that URL itself.

**Why it matters:**
A concrete, commonly-missed convention — many APIs default to `200` for everything.

**Common trap:**
Returning `200` from a creation endpoint and omitting the `Location` header.

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)

## Card: 404 vs. 500 — a real distinction

**Prompt:**
Why is returning `500` for "resource not found" a real mistake, not just a style nitpick?

**Answer:**
`500` communicates "the server itself is broken" — a fundamentally different signal to a caller (and to whoever gets paged for it) than `404`'s "you asked for something that doesn't exist." Conflating them turns routine not-found lookups into false alarms.

**Why it matters:**
Status codes are a machine-readable contract; using the wrong one breaks monitoring and client error-handling that branches on it.

**Common trap:**
Treating all error responses as interchangeable "something went wrong" signals.

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)

## Card: Repeated DELETE — a genuinely open design question

**Prompt:**
What should `DELETE` on an already-deleted resource return — `404` or `204`?

**Answer:**
Both are defensible, not one objectively correct: `404` is honest (the resource genuinely isn't there); `204` treats "already gone" as an equally successful idempotent outcome, giving retrying clients a uniform success signal. The important thing is picking one deliberately and staying consistent, not which one you pick.

**Why it matters:**
A real, Senior/Staff-level design question interviewers use to see if a candidate recognizes genuinely open trade-offs rather than asserting a single "correct" answer.

**Common trap:**
Asserting one of the two options is objectively correct rather than acknowledging both are real, defensible choices.

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)

## Card: Verbs in the URL

**Prompt:**
Why is `/createBook` or `/getBookById` considered a REST design mistake?

**Answer:**
It duplicates information the HTTP method already carries — `POST /books` already means "create a book," and `GET /books/{id}` already means "retrieve book {id}." Putting the verb in the URL too is the single most common tell that an API is RPC-style while being called REST.

**Why it matters:**
A fast, visible signal interviewers use to gauge whether a candidate actually understands REST's resource-noun convention.

**Common trap:**
Designing endpoints around actions ("what should this button call?") instead of resources.

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)

## Card: 400 vs. 422

**Prompt:**
A client sends valid JSON with a blank `title` field. Should the API return `400` or `422`?

**Answer:**
`422 Unprocessable Entity` — the JSON parsed fine (that's what would make it a `400`), but its content violates a semantic rule (a book needs a real title). `400` means the request couldn't even be read; `422` means it was read and found invalid.

**Why it matters:**
The real, practical distinction most candidates only gesture at without being able to state precisely.

**Common trap:**
Using `400` for every kind of "invalid request," collapsing a parsing failure and a business-rule failure into one signal.

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)

## Card: 401 vs. 403

**Prompt:**
What's the real difference between `401 Unauthorized` and `403 Forbidden`?

**Answer:**
`401` means the caller's identity isn't established at all (no credentials, or invalid ones) — an authentication failure. `403` means identity *is* established, but this specific caller isn't allowed to do this specific thing — an authorization failure.

**Why it matters:**
Retrying a `403` with the same, already-valid credentials will never succeed — a client needs to know which failure it's looking at to react correctly.

**Common trap:**
Treating both as interchangeable "access denied" signals.

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)

## Card: A real business-key conflict (409)

**Prompt:**
Two `POST /books` requests use the same `title` but different `isbn` values — no conflict. A third uses a *different* title but the *same* `isbn` as an existing book. What should happen?

**Answer:**
A real `409 Conflict` — `isbn` is a business key, and this API's own real demo proves title duplication is a legitimate, expected feature of `POST` (each call creates a new resource), while isbn duplication is a genuine data conflict on a different field entirely.

**Why it matters:**
Shows that "does this API allow duplicates" isn't a single yes/no question — it depends on which specific field.

**Common trap:**
Applying one blanket "no duplicates" or "duplicates are fine" rule to every field on a resource.

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)

## Card: Conditional GET (304)

**Prompt:**
How does a client avoid re-downloading a resource's full body on every `GET` when nothing has changed?

**Answer:**
Conditional `GET` via `ETag`/`If-None-Match`: the server computes a real `ETag` for the current representation; if the client resends that same value in `If-None-Match` and nothing changed, the server returns a real, empty-bodied `304 Not Modified` instead of the full body. Spring provides this via `ShallowEtagHeaderFilter` with zero hand-rolled hashing code.

**Why it matters:**
A real, commonly-asked caching mechanism that's easy to describe vaguely ("caching headers") without naming the actual mechanism.

**Common trap:**
Confusing `304` with a client-side-only cache — it's a real, server-computed, per-request check, not just the client deciding not to ask.

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)
