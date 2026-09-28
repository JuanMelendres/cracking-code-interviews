---
title: "Cheat Sheet: HTTP Caching for APIs"
slug: http-caching-for-apis
document_type: cheat-sheet
domain: 07-api-design
topic_id: T-2435
canonical: ../syllabus/07-api-design/http-caching-for-apis.md
last_updated: 2026-09-28
---

# HTTP Caching for APIs

**Canonical chapter:** [`syllabus/07-api-design/http-caching-for-apis.md`](../syllabus/07-api-design/http-caching-for-apis.md)

## Core Mental Model

**Freshness saves the request. Validation saves the body.** Only one of them removes the round trip.

## Directives

| Directive | Actually means |
|---|---|
| `no-store` | Do not store anywhere — the only one that prevents caching |
| `no-cache` | May store, **must revalidate** before every reuse |
| `private` | Only a single-user cache may store it |
| `public` | Shared caches may store it, **even with `Authorization`** |
| `max-age=N` | Fresh for N seconds (client) |
| `s-maxage=N` | Fresh for N seconds in shared caches, overrides `max-age` |
| `must-revalidate` | Once stale, never serve without revalidating |
| `immutable` | Never changes while fresh; skip revalidation |
| `stale-while-revalidate=N` | Serve stale up to N s while refreshing in background |

## Measured

- 200 body: **50 bytes**. 304 body: **0 bytes**.
- **2 server-side renders across 4 requests** — the `304`s skipped serialization.
- Both `304`s still cost a **full round trip**.

## Validators

- Prefer `ETag` over `Last-Modified`: one-second granularity, clock skew, untracked changes.
- Strong `"abc"` = byte-identical. Weak `W/"abc"` = semantically equivalent. Range requests need strong.
- **Body-hash ETag** saves bandwidth only — you rendered before deciding. **Version-derived ETag** also saves the work.
- A `304` must carry `ETag`, `Cache-Control`, `Vary` — omitting them lets the cache fall back to heuristics.

## Decision Table

| Response | Header |
|---|---|
| Tokens, PII, financial | `no-store` |
| Per-user, non-secret | `private, max-age=<small>` |
| Shared, tolerant of staleness | `public, max-age=N` (+ `s-maxage`) + ETag |
| Must always be current | `no-cache` + ETag |
| Content-hashed asset | `public, max-age=31536000, immutable` |

## Vary

`Vary` names every request header the response depends on. Missing it on a shared cache = one user's response served to another. For `Authorization`, prefer `private`/`no-store` over trusting `Vary`.

## Common Pitfalls

- Reading `no-cache` as "do not cache."
- `public` on an authenticated endpoint — explicitly overrides the `Authorization` protection.
- Expecting an ETag to cut request **count**. It cuts bytes.
- Saying nothing at all: caches then apply **heuristic** freshness.
- A long `max-age` you cannot recall — the only remedy is a new URL.

## Beyond Caching

`If-Match` + `412 Precondition Failed` = optimistic concurrency at the HTTP layer.

## Interview Answer Skeleton

**30-sec:** `max-age` lets a client reuse with no network at all. `ETag` + `If-None-Match` still costs a round trip but returns `304` with no body. Most designs need both. `Vary` keeps a shared cache from mixing users.

## Related

- [REST API Fundamentals](../syllabus/07-api-design/rest-api-fundamentals.md)
- [Caching Strategies and Invalidation](../syllabus/11-system-design/caching-strategies-and-invalidation.md)
- [API Gateway, BFF, and Edge Concerns](../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md)
