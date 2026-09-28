---
title: "Flashcards: HTTP Caching for APIs"
slug: http-caching-for-apis
document_type: flashcard-deck
domain: 07-api-design
topic_id: T-2435
canonical: ../syllabus/07-api-design/http-caching-for-apis.md
last_updated: 2026-09-28
---

# Flashcards: HTTP Caching for APIs

**Canonical chapter:** [`syllabus/07-api-design/http-caching-for-apis.md`](../syllabus/07-api-design/http-caching-for-apis.md)

## Card: Freshness versus validation

**Prompt:**
What is the difference between `max-age` and an `ETag`, in terms of what each one saves?

**Answer:**
`max-age` is **freshness**: the client reuses its copy with no network at all until it expires — it saves the entire round trip. An `ETag` with `If-None-Match` is **validation**: the request is still sent and answered, returning `304` with an empty body — it saves the bytes and possibly the server's rendering, never the round trip.

**Why it matters:**
It explains the common outcome "we added ETags and request volume did not drop." By design, it cannot.

**Common trap:**
Treating both as one undifferentiated notion of "caching."

**Related:**
[HTTP Caching for APIs](../syllabus/07-api-design/http-caching-for-apis.md)

## Card: no-cache vs no-store

**Prompt:**
Which `Cache-Control` directive actually prevents a response from being stored?

**Answer:**
`no-store`. `no-cache` permits storage and only requires revalidation with the origin before each reuse. `must-revalidate` is narrower still: it applies once a response is already stale, forbidding a cache from serving that stale copy without revalidating.

**Why it matters:**
A response intended to be protected with `no-cache` is still sitting in a cache somewhere.

**Common trap:**
Setting nothing at all — caches may then apply *heuristic* freshness, so silence is not prohibition.

**Related:**
[HTTP Caching for APIs](../syllabus/07-api-design/http-caching-for-apis.md)

## Card: How a header leaks one tenant's data to another

**Prompt:**
A CDN served one customer's data to another and the query filtering was correct. What was wrong?

**Answer:**
The response was `Cache-Control: public` on an authenticated endpoint — which explicitly overrides the default rule that shared caches must not store responses to `Authorization`-carrying requests — with no `Vary` to key the entry by anything but the URL. One stored copy, served to everyone. The diagnostic tell is one origin request in the logs while many users saw the response.

**Why it matters:**
It makes cache headers a confidentiality concern, not just a performance one.

**Common trap:**
Reaching for `Vary: Authorization` as the fix; cache support is inconsistent, and `private`/`no-store` is the real answer.

**Related:**
[API Gateway, BFF, and Edge Concerns](../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md)

## Card: Body-hash versus version-derived ETag

**Prompt:**
Two ways to compute an `ETag`. What does each one actually save?

**Answer:**
A hash of the rendered body is trivially correct but requires doing all the work before discovering a `304` was possible — it saves bandwidth only. An ETag derived from a cheap version source (a row version, an update counter) can be computed before rendering, so it saves the server work too. Measured: 2 renders across 4 requests, because the `304`s skipped serialization.

**Why it matters:**
On an endpoint assembled from several service calls, the work is the cost, not the bytes.

**Common trap:**
Adding `ShallowEtagHeaderFilter`-style body hashing to an expensive endpoint and expecting CPU relief.

**Related:**
[HTTP Caching for APIs](../syllabus/07-api-design/http-caching-for-apis.md)

## Card: What a 304 must carry

**Prompt:**
Which headers belong on a `304 Not Modified`, and why?

**Answer:**
The cache-relevant ones — `ETag`, `Cache-Control`, `Vary` — because the cache updates its stored entry's metadata from the `304`. If `Cache-Control` is missing, it falls back to heuristic freshness and may reuse the entry for a window nobody chose. The body must genuinely be empty (`sendResponseHeaders(304, -1)` in the JDK's server, distinct from `0`).

**Why it matters:**
It is a real bug that silently changes caching behavior rather than throwing anything.

**Common trap:**
Treating the `304` path as "just return a status."

**Related:**
[REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)
