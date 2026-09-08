---
title: "REST API Fundamentals"
slug: rest-api-fundamentals
document_type: syllabus-topic
domain: 07-api-design
topic_id: T-2205
status: draft
version: 1.0
last_updated: 2026-09-07
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../05-spring/spring-mvc-fundamentals.md
related:
  - api-design.md
  - api-gateway-bff-and-edge-concerns.md
  - ../11-system-design/idempotency.md
practice: ../../practice/java/rest-api-fundamentals/
production_scenarios: []
interview_paths: [junior-to-mid, interview-emergency-sprint]
official_references:
  - https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods
  - https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
---

# REST API Fundamentals

## Table of Contents

1. [Why This Matters](#1-why-this-matters)
2. [Prerequisites](#2-prerequisites)
3. [Foundation (L1)](#3-foundation-l1)
4. [Core Concepts (L2)](#4-core-concepts-l2)
5. [How It Works Internally (L3)](#5-how-it-works-internally-l3)
6. [Practical Usage](#6-practical-usage)
7. [Examples](#7-examples)
8. [Common Mistakes](#8-common-mistakes)
9. [Edge Cases](#9-edge-cases)
10. [Performance Implications](#10-performance-implications)
11. [Trade-offs](#11-trade-offs)
12. [Senior-Level Considerations (L3)](#12-senior-level-considerations-l3)
13. [Staff/System-Level Considerations (L4)](#13-staffsystem-level-considerations-l4)
14. [Production Scenarios](#14-production-scenarios)
15. [Interview Questions](#15-interview-questions)
16. [Coding/Practice Exercises](#16-codingpractice-exercises)
17. [Debugging Exercises](#17-debugging-exercises)
18. [Design Exercises](#18-design-exercises)
19. [Further Reading](#19-further-reading)
20. [Mastery Checklist](#20-mastery-checklist)

## 1. Why This Matters

Every other chapter in `07-api-design` assumes you already know what a resource is, why `PUT` and `POST` mean different things, and what `404` versus `500` communicates — reasonable for the Senior/Staff-only version of this repository, not reasonable once it explicitly covers Junior through Staff. This is the fifth and final chapter in this repository's Junior Fundamentals initiative (see [Java OOP Fundamentals](../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) for the first, and the audit that started it), closing the same gap for API design that the other four closed for Java, SQL, Spring, and testing. Nearly every backend interview includes some form of "design an API for X," and a candidate who reaches for the wrong HTTP verb or the wrong status code signals a gap an interviewer will probe immediately.

## 2. Prerequisites

[Spring MVC Fundamentals](../05-spring/spring-mvc-fundamentals.md) — this chapter's own demo extends that one's `@RestController` mechanics with a second, REST-conventions-focused application.

## 3. Foundation (L1)

**REST** (Representational State Transfer) is a set of conventions for designing APIs around **resources** — nouns, not verbs. A well-designed REST API names its endpoints after the *thing* being manipulated (`/books`, `/books/42`) and lets the **HTTP verb** carry the *action*: `GET /books/42` retrieves book 42; it never spells the action into the URL as `/getBook?id=42`.

The four verbs this chapter's demo uses, and what each one means by convention:

- **`GET`** — retrieve a resource (or a list of them). Never changes anything.
- **`POST`** — create a new resource. Calling it twice creates two resources.
- **`PUT`** — replace an existing resource's full state. Calling it twice with the same body leaves the resource in the same state both times.
- **`DELETE`** — remove a resource.

An HTTP **status code** tells the caller what actually happened, in a single number a client can branch on without parsing the response body: `200 OK` (success, a body follows), `201 Created` (a new resource now exists), `204 No Content` (success, deliberately no body), `404 Not Found` (no resource at that URL), `500 Internal Server Error` (the server itself failed).

## 4. Core Concepts (L2)

**Idempotency** — the property that calling an operation multiple times produces the same end state as calling it once — is the single most interview-relevant REST concept, and it is a real, verb-specific guarantee, not a vague notion of "safety." `GET`, `PUT`, and `DELETE` are all idempotent by convention: repeating any of them leaves the system in the same state as doing it once (Section 7's `PUT` demo shows this directly: two identical calls, identical resulting resource, identical status both times). `POST` is deliberately **not** idempotent: it means "create a new thing," and calling it twice with the identical body genuinely creates two separate resources — Section 7's `POST` demo proves this directly with two different `id` values from an identical request body, not by assertion.

Resource naming follows a small number of real conventions worth internalizing: plural nouns for collections (`/books`, not `/book`), a nested path for a specific item within that collection (`/books/{id}`), and no verbs in the path at all — the verb is the HTTP method, stated once, not duplicated into the URL as well.

`201 Created`'s real convention includes more than the status number: a **`Location` header** pointing at the URL of the newly created resource (Section 7's `POST` response: `Location: /books/1`), so a client can immediately `GET` the resource it just created without having to construct that URL itself from the response body.

## 5. How It Works Internally (L3)

None of these conventions are enforced by HTTP itself — a server can technically return `200` for everything, or accept a `DELETE` request that actually creates a resource, and the protocol will not stop it. REST conventions are a shared agreement between API designers and API consumers, enforced by the code you write (Section 7's [`BookController`](../../practice/java/rest-api-fundamentals/src/demo/BookController.java) explicitly chooses `ResponseEntity.created(location)` rather than defaulting to `200`), not by the transport layer underneath it. This is exactly why Section 8's mistakes are possible at all — the framework will happily let you return the "wrong" status code if you don't deliberately choose the right one.

Spring's `ResponseEntity` is the mechanism that makes choosing a specific status code explicit rather than implicit: returning a plain object (as [T-2203's `TaskController.createTask`](../../practice/java/spring-mvc-fundamentals/src/demo/TaskController.java) does) defaults to `200 OK`; wrapping it in `ResponseEntity.created(location).body(...)` (this chapter's own [`BookController.createBook`](../../practice/java/rest-api-fundamentals/src/demo/BookController.java)) states the status explicitly. `ResponseEntity.status(HttpStatus.NO_CONTENT).build()` for `DELETE` similarly states, in code, "success, no body" rather than letting a default apply.

## 6. Practical Usage

Choose the HTTP verb by what actually happens to the resource's existence, not by which verb feels closest to the action's English name — a "toggle availability" operation still updates an existing resource's state, so it's a `PUT` (or a narrower `PATCH`, not covered in this fundamentals chapter), never a `POST` just because it "does something." Return `201` with a `Location` header from every resource-creating endpoint, not `200` — Section 8's most common mistake. Choose `404` over `500` deliberately for "the requested thing doesn't exist" — a `500` communicates "the server itself broke," a fundamentally different signal to a caller (and to whoever is paged for it) than "you asked for something that isn't there."

## 7. Examples

All output below is real, from a live Spring Boot 3.5.16 app on embedded Tomcat, `localhost:8081` — [`practice/java/rest-api-fundamentals/`](../../practice/java/rest-api-fundamentals/), full transcript in `curl-transcript.txt`.

**`POST /books` is not idempotent — two identical requests, two real, distinct resources:**
```
$ curl -s -i -X POST http://localhost:8081/books -d '{"title":"Design Patterns"}'
HTTP/1.1 201
Location: /books/1
{"id":1,"title":"Design Patterns","available":true}

$ curl -s -i -X POST http://localhost:8081/books -d '{"title":"Design Patterns"}'
HTTP/1.1 201
Location: /books/2
{"id":2,"title":"Design Patterns","available":true}
```
Both requests sent the identical body; the response has two different `id`s and two different `Location` headers, because `POST` means "create a new one" every time it's called.

**`PUT /books/1` is idempotent — two identical requests, the identical resulting state and status both times:**
```
$ curl -X PUT http://localhost:8081/books/1 -d '{"title":"Design Patterns (2nd ed.)","available":false}'
{"id":1,"title":"Design Patterns (2nd ed.)","available":false}
HTTP status: 200

$ curl -X PUT http://localhost:8081/books/1 -d '{"title":"Design Patterns (2nd ed.)","available":false}'
{"id":1,"title":"Design Patterns (2nd ed.)","available":false}
HTTP status: 200
```

**`PUT` on a missing id, and `DELETE` twice** — real `404`s where the resource genuinely doesn't (or no longer does) exist:
```
$ curl -X PUT http://localhost:8081/books/999 -d '{"title":"Ghost","available":true}'
HTTP status: 404

$ curl -X DELETE http://localhost:8081/books/2
HTTP status: 204
$ curl -X DELETE http://localhost:8081/books/2
HTTP status: 404
```

## 8. Common Mistakes

- **Returning `200 OK` from a resource-creating endpoint instead of `201 Created`** — loses the explicit "a new thing now exists" signal, and usually loses the `Location` header along with it, forcing the client to construct the new resource's URL itself from the response body.
- **Putting a verb in the URL** (`/createBook`, `/getBookById`) — duplicates information the HTTP method already carries, and is the single most common tell that a candidate is designing an RPC-style API while calling it REST.
- **Using `POST` for an update because it "felt closer to the action"** — Section 6's warning; the actual test is "does this create a new resource (`POST`) or replace an existing one (`PUT`)," not which verb's English name sounds right.
- **Returning `500` for "resource not found"** instead of `404` — conflates "the server is broken" with "you asked for something that doesn't exist," two signals a caller (and an on-call engineer) need to distinguish immediately.

## 9. Edge Cases

- **`DELETE` on an already-deleted resource** genuinely has two defensible answers, not one correct one: this chapter's own demo returns `404` (Section 7's second `DELETE /books/2`), treating "already gone" as "not found"; some real APIs instead return `204` again, treating repeated deletion as an equally successful idempotent outcome regardless of whether anything was actually removed on this specific call. Section 11 states both positions explicitly — knowing this is a real, debatable design decision (and picking one, with a stated reason) matters more than which one you pick.
- **`PUT` to a URL that doesn't exist yet** could either create the resource at that exact id (a real, valid REST pattern called "PUT to create," used when the client controls the id) or return `404` (this chapter's own choice, since [`BookController`](../../practice/java/rest-api-fundamentals/src/demo/BookController.java) generates ids server-side via `POST`) — which one is correct depends entirely on whether the API design lets clients choose resource ids at all.
- **A `POST` request that's a genuine network retry**, not a deliberate second creation (a client's connection dropped after the server processed the request but before the response arrived) is the real-world reason `POST`'s non-idempotency becomes an operational problem, not just a definitional one — [Idempotency at System Edges](../11-system-design/idempotency.md) covers the actual mechanism (an idempotency key) systems use to make retried `POST` requests safe, at a scale and depth beyond this fundamentals chapter.

## 10. Performance Implications

None of this chapter's conventions carry an inherent performance cost of their own — choosing the correct status code and verb is a design decision made once per endpoint, not a runtime cost paid per request. The real performance-relevant API design questions (pagination at scale, caching headers, payload size) belong to [API Design](api-design.md) and [API Gateway, BFF, and Edge Concerns](api-gateway-bff-and-edge-concerns.md), both of which assume this chapter's fundamentals as a starting point.

## 11. Trade-offs

| Concern | Treat repeated `DELETE` as `404` (this chapter's choice) | Treat repeated `DELETE` as `204` |
|---|---|---|
| Semantics | "Not found" is honest — the resource genuinely isn't there | "Success" emphasizes the caller's desired end state (the resource is gone) was achieved either way |
| Client retry safety | A client retrying a `DELETE` after a dropped connection must treat `404` as "probably fine, but check" | A client retrying gets a clean, uniform success signal regardless of whether this exact call did the deleting |
| Consistency with `GET`/`PUT` on a missing resource | Matches the same `404` those verbs already return for a missing id | Is the one verb behaving differently from the others for the same "resource doesn't exist" condition |

## 12. Senior-Level Considerations (L3)

A Senior engineer designing a new endpoint states, out loud, which of Section 9's genuinely-debatable design decisions (repeated-`DELETE` semantics, whether `PUT` can create) the API is choosing and why — not because there's one universally correct answer, but because an unstated, inconsistent choice (some endpoints return `404` on repeat-delete, others `204`, with no one having decided) is a real API-quality smell an interviewer will notice in a design-round follow-up question.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the actual leverage in this chapter's conventions is consistency across an entire API surface, not correctness on any one endpoint — a system with a dozen services each making its own independent, undocumented choice about repeated-`DELETE` semantics or when to use `201` versus `200` produces an API that's exhausting to integrate against, because every new integration has to rediscover each service's own local conventions by testing rather than reading a shared standard. A Staff engineer's real leverage here is establishing (and enforcing, via API review or a shared style guide) one answer to each of Section 9's genuinely-open questions across an organization's services, the same organizational-consistency argument [Spring MVC Fundamentals](../05-spring/spring-mvc-fundamentals.md)'s own Staff section makes for controller/service/repository layering.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a REST-fundamentals-specific root cause — the closest adjacent entries are pagination- and idempotency-key-scale, not basic-verb/status-code-scale.

> Planned reference: a future `production-cookbook/` entry covering a real incident caused by a client blindly retrying a non-idempotent `POST` after a timeout (genuinely unaware the original request had already succeeded server-side), producing duplicate resources, would be a natural, non-duplicative addition connecting this chapter's Section 9 edge case to [Idempotency at System Edges](../11-system-design/idempotency.md)'s system-scale fix.

## 15. Interview Questions

**Q1 (Junior): "What's the difference between PUT and POST?"**
Expected answer: `POST` creates a new resource (not idempotent — calling it twice creates two); `PUT` replaces an existing resource's state at a known location (idempotent — calling it twice leaves the same end state). A weak answer says "PUT updates, POST creates" without the idempotency distinction, which is the part interviewers actually probe.

**Q2 (Junior/Mid): "What status code should a successful DELETE return, and why no body?"**
Expected answer: `204 No Content` — the operation succeeded, and there is deliberately nothing left to describe, since the resource no longer exists.

**Q3 (Mid): "Why does a well-designed API return 201 instead of 200 from a creation endpoint, and what should come with it?"**
Expected answer: `201` communicates "a new resource now exists" specifically, distinct from a general success; convention also includes a `Location` header pointing at the new resource's real URL (Section 4/7).

**Q4 (Mid/Senior): "Is DELETE idempotent? What happens if you call it twice on the same resource?"**
Expected answer: `DELETE` is conventionally idempotent in the sense that the end state (resource gone) is the same either way, but the *response* to a second call is a genuinely debatable design choice (Section 9/11: `404` versus `204`) — the strongest answers name both options and state a reasoned preference rather than asserting only one is correct.

**Q5 (Senior/Staff): "You're reviewing an API where some endpoints return 404 and others return 204 for repeated deletes, with no stated reason. What do you do?"**
Expected answer: Section 13's framing — flag it as an unstated-inconsistency issue, not a bug in either individual endpoint; push for a documented, organization-wide convention rather than a per-endpoint fix, since the same inconsistency will otherwise keep recurring as new endpoints are added.

## 16. Coding/Practice Exercises

1. Add a `PATCH /books/{id}` endpoint that updates only the `available` field (leaving `title` untouched if not supplied), and explain in a one-line comment why this is a genuinely different operation from the existing `PUT`, not just a smaller version of it.
2. Change `deleteBook` to return `204` on a repeat delete instead of `404` (Section 9's alternative), and update the transcript to show the new, real behavior.
3. Add a `GET /books?available=true` query parameter that filters the list, and state which HTTP status code an empty (zero-match) result should return — `200` with an empty array, or `404` — with a one-sentence justification.

## 17. Debugging Exercises

Given this real API behavior, predict the output before checking Section 7's transcript:

```
POST /books {"title":"A"}   -> ?
POST /books {"title":"A"}   -> ?
GET /books                  -> ?
```

Both `POST` calls return `201`, each with a **different** `id` (`1` and `2`) and a different `Location` header, and `GET /books` afterward shows **two** separate book resources with the identical title `"A"`. A candidate predicting the second `POST` would "update" the first, or that the two calls would collapse into one resource, is treating `POST` as idempotent — the exact misunderstanding Section 4 exists to correct.

## 18. Design Exercises

Design the endpoints (verb + path + expected status codes) for a simple shopping cart: adding an item, removing an item, viewing the cart, and clearing the entire cart. For "adding an item," state explicitly whether adding the same item twice should create two line entries or increment a quantity on one — and which HTTP verb that decision implies.

## 19. Further Reading

- [API Design](api-design.md) — pagination, resource naming at scale, and error-response design, building directly on this chapter's fundamentals.
- [API Gateway, BFF, and Edge Concerns](api-gateway-bff-and-edge-concerns.md) — what happens to these conventions once multiple services and a gateway sit between the client and the resource.
- [Idempotency at System Edges](../11-system-design/idempotency.md) — the system-scale mechanism (idempotency keys) that makes a retried, non-idempotent `POST` safe, referenced in this chapter's Section 9/14.

## 20. Mastery Checklist

- [ ] Can state what `GET`, `POST`, `PUT`, and `DELETE` each conventionally mean, including which are idempotent.
- [ ] Can explain why `POST` creating two resources from an identical body is correct behavior, not a bug.
- [ ] Can state what `201 Created` communicates that `200 OK` does not, and what header should come with it.
- [ ] Can distinguish `404` from `500` and explain why conflating them is a real mistake, not just a style nitpick.
- [ ] Can correctly predict the Section 17 debugging exercise's real output before checking it.
- [ ] Can name at least one genuinely open, debatable REST design question (Section 9) and argue a reasoned position on it.
