---
title: "Flashcards: REST API Fundamentals"
slug: rest-api-fundamentals
document_type: flashcard-deck
domain: 07-api-design
topic_id: T-2205
canonical: ../syllabus/07-api-design/rest-api-fundamentals.md
last_updated: 2026-09-07
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
