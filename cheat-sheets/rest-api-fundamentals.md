---
title: "Cheat Sheet: REST API Fundamentals"
slug: rest-api-fundamentals
document_type: cheat-sheet
domain: 07-api-design
topic_id: T-2205
canonical: ../syllabus/07-api-design/rest-api-fundamentals.md
last_updated: 2026-09-07
---

# REST API Fundamentals

**Canonical chapter:** [`syllabus/07-api-design/rest-api-fundamentals.md`](../syllabus/07-api-design/rest-api-fundamentals.md)

## Core Mental Model

REST APIs name endpoints after the resource (a noun: `/books`, `/books/42`) and let the HTTP verb carry the action — never a verb in the URL.

## Essential Definitions

- **`GET`** — retrieve, never changes anything.
- **`POST`** — create a new resource; **not idempotent** — calling it twice creates two resources.
- **`PUT`** — replace an existing resource's full state; **idempotent** — calling it twice leaves the same end state.
- **`DELETE`** — remove a resource.
- **Idempotency** — calling an operation multiple times produces the same end state as calling it once.
- **`201 Created`** — a new resource now exists; convention includes a `Location` header pointing at its real URL.
- **`204 No Content`** — success, deliberately no response body.

## Decision Table

| Outcome | Status code |
|---|---|
| Successful `GET`/`PUT` with a body | `200 OK` |
| Successful resource creation | `201 Created` + `Location` header |
| Successful `DELETE` | `204 No Content` |
| Requested resource doesn't exist | `404 Not Found` |
| The server itself failed | `500 Internal Server Error` (never used for "not found") |

## Common Pitfalls

- Returning `200` instead of `201` from a creation endpoint — loses the "a new thing now exists" signal and usually the `Location` header with it.
- Putting a verb in the URL (`/createBook`, `/getBookById`) — duplicates what the HTTP method already communicates; the single most common "this is RPC wearing a REST costume" tell.
- Using `POST` for an update because it "felt closer to the action" — the real test is whether a new resource is created (`POST`) or an existing one is replaced (`PUT`), not which verb sounds right.
- Returning `500` for "resource not found" instead of `404` — conflates "server is broken" with "you asked for something that doesn't exist."

## Interview Answer Skeleton

**30-sec:** REST names endpoints after resources and uses the HTTP verb for the action. `POST` creates (not idempotent — two identical calls make two resources); `PUT` replaces (idempotent — two identical calls leave the same state). `201`+`Location` for creation, `204` for delete, `404` for missing, `500` only for real server failure.

**2-min:** Add a concrete idempotency proof: two identical `POST /books` calls return two different ids and two different `Location` headers (genuinely two resources); two identical `PUT /books/1` calls return the identical resulting state and status both times.

**Whiteboard:** Draw the same request sent twice, two ways: the `POST` branch ending in two distinct boxes (two resources); the `PUT` branch ending in one box, unchanged, both times.

**Staff-level framing:** The real leverage in these conventions is consistency across an entire API surface — a dozen services each independently deciding, say, what a repeated `DELETE` should return produces an API that's exhausting to integrate against; a Staff engineer's job is establishing one organization-wide answer to each genuinely-open question (like repeated-`DELETE` semantics), not re-litigating it per endpoint.

## Related

- syllabus/07-api-design/api-design.md
- syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md
- syllabus/11-system-design/idempotency.md
