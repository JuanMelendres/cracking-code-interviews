---
title: "Cheat Sheet: API Design"
slug: api-design
document_type: cheat-sheet
domain: system-design
topic_id: T-803
canonical: ../handbook/system-design/api-design.md
last_updated: 2026-08-03
---

# API Design

**Canonical chapter:** [`handbook/system-design/api-design.md`](../handbook/system-design/api-design.md)

## Core Mental Model

An API is a contract, and every contract decision made today constrains every client written tomorrow. Pagination strategy is the sharpest example: the difference between `OFFSET` and keyset pagination is invisible at low volume and catastrophic at scale — exactly why it must be decided deliberately up front rather than discovered painfully later, once real clients depend on the shape.

## Essential Definitions

- **OFFSET pagination** — `OFFSET n` requires the database to walk and discard `n` rows before returning the next page; cost grows linearly with page depth.
- **Keyset (cursor) pagination** — `WHERE id > last_seen_id ORDER BY id LIMIT n`; costs the same regardless of depth because the index seeks directly to the starting point.
- **Resource naming** — plural nouns for collections (`/orders`, not `/order`/`/getOrders`), nesting reflects genuine ownership, no verbs in the path — the HTTP method is the verb.
- **Error envelope** — status code, machine-readable error code, human-readable message, and (where applicable) which field caused a validation failure.
- **Idempotency** — an operation is idempotent if it produces the same end state no matter how many times it's applied; this is what makes client retries safe.

## Decision Table

| Standard method | HTTP verb | Idempotent? |
|---|---|---|
| List | `GET /resources` | Yes |
| Get | `GET /resources/{id}` | Yes |
| Create | `POST /resources` | No (unless an idempotency key is supplied) |
| Update | `PUT`/`PATCH /resources/{id}` | `PUT` yes (full replace); `PATCH` depends on semantics |
| Delete | `DELETE /resources/{id}` | Yes (deleting an already-deleted resource is still "gone") |

**Situation → what to reach for:**

| Situation | What to reach for |
|---|---|
| List endpoint, table may grow large | Keyset pagination by default |
| UI needs arbitrary page-number jumping | Hybrid: keyset for next/prev, approximate count for jump-to-page |
| A `POST` with a real, costly side effect | Require a client-supplied idempotency key |
| Designing error responses | One consistent envelope across every endpoint |

## Key Numbers (real EXPLAIN ANALYZE, PostgreSQL 16, 2M-row table)

- `OFFSET`, shallow page (offset 100): **0.028ms**
- `OFFSET`, deep page (offset 1,000,000): **86.006ms** (walks and discards 1,000,020 rows before returning 20)
- Keyset, equivalent depth: **0.020ms** (index condition jumps directly there)
- **~3,000x difference at depth**, same table, same index. Degradation typically becomes visible in the tens-of-thousands-to-low-millions-of-rows-deep range.

## Common Pitfalls

- Choosing `OFFSET` pagination by default without checking whether the endpoint will ever be queried at depth
- Inconsistent error response shapes across different endpoints in the same API
- Verbs in resource paths (`/getOrders`) instead of letting the HTTP method carry that meaning
- Conflating "idempotent" with "read-only" — a `PUT` is idempotent and can still be a write

## Interview Answer Skeleton

**30-sec:** `OFFSET`'s cost grows linearly with depth because the database walks and discards every skipped row; keyset costs the same at any depth because the index seeks directly. Honest trade-off: keyset can't jump to an arbitrary page, only move from a known cursor.

**2-min:** Add why API design matters (prevents implementation leakage/inconsistent client handling) + the measured ~3,000x difference + the arbitrary-page-jump trade-off.

**Whiteboard:** Draw "OFFSET" as a long row of walked-and-discarded boxes before the returned page, vs. "keyset" as a direct arrow jumping straight to the cursor.

**Staff-level framing:** API contract decisions are among the most expensive to change once shipped — depended on by every client the moment it's public. This is why pagination is worth getting right up front, not "optimizing later."

## Production Warning Signs

- An admin tool with `OFFSET` pagination works fine for a year, then becomes unusably slow at deep pages specifically, while early pages stay fast
- `EXPLAIN ANALYZE` shows a large `rows=` walked-and-discarded count, execution time scaling with the requested offset
- **The trap:** nobody revisited the pagination decision as the table grew past a few million rows. Stopgap: cap max page-jump depth in the UI. Fix: migrate to keyset for the common case, hybrid approximate-count for jump-to-page.

## Related

- [Database Index Structures](index-structures-btree-composite-covering.md)
- [Idempotency at System Edges](idempotency.md)
- `handbook/system-design/distributed-systems-failure-modes.md`
