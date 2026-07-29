---
title: "T-803 · API Design"
topic_id: T-803
domain: System Design
tier: Advanced
iwi: 7.10
prerequisites: [T-804]
unlocks: []
week: 4
last_reviewed: 2026-07-29
---

# T-803 · API Design

**IWI 7.10 · Advanced tier**

**Verification note:** the pagination comparison in §3 is real, executed PostgreSQL 16 output against a 2-million-row table. Source: `practice/sql/week-04/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Pagination, measured — why not OFFSET](#3-pagination-measured--why-not-offset)
4. [Resource naming and standard methods](#4-resource-naming-and-standard-methods)
5. [Error design](#5-error-design)
6. [Trade-offs](#6-trade-offs)
7. [Interview questions](#7-interview-questions)
8. [Common mistakes](#8-common-mistakes)
9. [Staff-level discussion](#9-staff-level-discussion)
10. [Summary](#10-summary)
11. [Key Takeaways](#11-key-takeaways)
12. [Cheat Sheet](#12-cheat-sheet)
13. [Flashcards](#13-flashcards)
14. [Practice Exercises](#14-practice-exercises)
15. [Additional Reading](#15-additional-reading)
16. [Official References](#16-official-references)

---

## 1. The concept

API design is the discipline of choosing a stable, predictable contract at a system's boundary — resource shapes, standard methods, error formats, and pagination — that client code can be written against without needing to know the implementation behind it. Every decision in this chapter trades a small amount of upfront design effort for a large amount of avoided client-side breakage and confusion later.

## 2. Why it exists

Without deliberate API design, an interface tends to leak implementation details directly — endpoint shapes that mirror internal database tables, pagination that mirrors whatever the ORM's default query happened to produce, error responses inconsistent across endpoints because each was implemented independently. A consistent API design discipline exists specifically so that a client written against one endpoint transfers its assumptions correctly to the next one.

## 3. Pagination, measured — why not OFFSET

**Design pagination for a 500M-row endpoint. Why not `OFFSET`?**

Real `EXPLAIN ANALYZE` output, PostgreSQL 16, 2-million-row table:

```
OFFSET, shallow page (offset 100):        Execution Time: 0.028ms
OFFSET, deep page (offset 1,000,000):     Execution Time: 86.006ms   (rows=1000020 walked before returning 20)
Keyset, equivalent depth (id > 1000000):  Execution Time: 0.020ms    (Index Cond jumps directly there)
```

**~3,000x difference at depth, same table, same index.** `OFFSET n` requires the database to walk and discard `n` rows before it can return the next page — cost grows linearly with page depth. Keyset (cursor) pagination — `WHERE id > last_seen_id ORDER BY id LIMIT n` — costs the same regardless of depth, because the index condition seeks directly to the right starting point.

```
POST /feed?limit=20                              -- first page
  -> {items: [...], nextCursor: "1000020"}

GET /feed?limit=20&after=1000020                  -- next page, keyset-based
  -> {items: [...], nextCursor: "1000040"}
```

**Real trade-off, stated honestly:** keyset pagination cannot jump directly to "page 500,000" the way an offset-based UI control can — it can only move forward/backward from a known cursor. For a UI that needs arbitrary page-number jumping (rare in practice for feeds; common for admin back-office tools), a hybrid is often used: keyset for the common "next page" case, with an approximate, separately-computed count/estimate for a jump-to-page control that doesn't need to be exact.

## 4. Resource naming and standard methods

| Standard method | HTTP verb | Idempotent? |
|---|---|---|
| List | `GET /resources` | Yes |
| Get | `GET /resources/{id}` | Yes |
| Create | `POST /resources` | No (unless an idempotency key is supplied — see `02-distributed-failure-modes.md` §4) |
| Update | `PUT/PATCH /resources/{id}` | `PUT` yes (full replace), `PATCH` depends on semantics |
| Delete | `DELETE /resources/{id}` | Yes (deleting an already-deleted resource is still "gone") |

**Resource naming convention:** plural nouns for collections (`/orders`, not `/order` or `/getOrders`), nesting reflecting genuine ownership (`/orders/{id}/lines`, not a flat `/order-lines?orderId=`), and no verbs in the path — the HTTP method *is* the verb.

## 5. Error design

A consistent error envelope — status code, machine-readable error code, human-readable message, and (where applicable) which field caused a validation failure — lets client code handle errors programmatically rather than string-matching a message. The specific status code matters for idempotency reasoning too: a `409 Conflict` on a duplicate `POST` (with idempotency key) tells the client definitively "already handled," versus a `500` which is genuinely ambiguous per `02-distributed-failure-modes.md` §4.

## 6. Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| Offset pagination | Simple, supports arbitrary page-jump | Cost grows linearly with depth — unusable at scale, demonstrated at ~3,000x |
| Keyset pagination | Flat cost regardless of depth | Cannot jump to an arbitrary page number directly |
| `PUT` (full replace) | Simple, idempotent by definition | Requires the client to send the full resource even for a one-field change |
| `PATCH` (partial update) | Efficient for small changes | Idempotency and merge semantics must be defined explicitly, or aren't guaranteed |

## 7. Interview questions

### Q1. Design pagination for a 500M-row endpoint. Why not `OFFSET`?

- **Expected answer:** the §3 measured result and mechanism — cost grows linearly with offset depth because the database walks and discards every skipped row; keyset pagination avoids this by seeking directly via an index condition.
- **Common mistakes:** citing "it's slower" without the specific mechanism (rows walked and discarded) or without real numbers.
- **Follow-up questions:** "What do you lose by switching to keyset pagination?"
- **Senior-level expectations:** correctly explains why `OFFSET` degrades and proposes keyset pagination.
- **Staff-level expectations:** names the honest trade-off (no arbitrary page-jump) and proposes the hybrid approach for UIs that need it.

### Q2. What makes an API idempotent, and why does it matter for retries?

- **Expected answer:** an idempotent operation produces the same end state no matter how many times it's applied; it matters because a client that isn't sure whether a request succeeded (per `02-distributed-failure-modes.md` §4) can safely retry an idempotent operation without risk.
- **Common mistakes:** conflating "idempotent" with "read-only" — a `PUT` is idempotent and can still be a write.
- **Follow-up questions:** "Is `POST` ever idempotent?" *(Yes — with a client-supplied idempotency key that the server uses to recognize and deduplicate a retried request.)*
- **Senior-level expectations:** states the definition correctly and connects it to safe retries.
- **Staff-level expectations:** explicitly ties this back to the idempotency-key mechanism from Week 4's distributed failure modes chapter.

## 8. Common mistakes

- Choosing `OFFSET` pagination by default without checking whether the endpoint will ever be queried at depth.
- Inconsistent error response shapes across different endpoints in the same API.
- Verbs in resource paths (`/getOrders`, `/createOrder`) instead of letting the HTTP method carry that meaning.

## 9. Staff-level discussion

API design decisions, once shipped, are among the most expensive to change in a system — a pagination scheme, a resource shape, or an error format is depended upon by every client the moment it's public, and changing it requires either a breaking change (coordinated client migration) or permanent dual-support. This is why the pagination decision in §3 is worth getting right from the start rather than "optimizing later" — by the time an `OFFSET`-based endpoint's depth problem becomes visible in production, it usually has existing clients depending on the exact page-jump behavior a keyset migration would need to give up.

## 10. Summary

API design choices — pagination, resource naming, error format — are contracts that become expensive to change once clients depend on them. `OFFSET` pagination has a real, measured, linear-with-depth cost (demonstrated at ~3,000x between shallow and deep pages on identical data); keyset pagination avoids it at the cost of losing arbitrary page-jump capability, a trade-off worth stating explicitly rather than treating as a free upgrade.

## 11. Key Takeaways

- `OFFSET`'s cost grows linearly with page depth because the database must walk and discard every skipped row — measured at ~3,000x in this chapter.
- Keyset pagination's cost is flat regardless of depth, at the honest cost of losing arbitrary page-jump.
- Idempotency (via `PUT`'s definition, or a client-supplied key for `POST`) is what makes retries safe — directly connecting to Week 4's distributed failure modes chapter.
- Resource naming and error-format consistency exist to let client code be written once and generalize across endpoints.

## 12. Cheat Sheet

See §4's standard-methods table and §6's trade-off table.

## 13. Flashcards

1. **Q: Why does `OFFSET` pagination get slower with depth?** A: The database must walk and discard every skipped row before returning the requested page — cost grows linearly with offset.
2. **Q: What does keyset pagination give up in exchange for flat cost at any depth?** A: Arbitrary page-number jumping — it can only move forward/backward from a known cursor.
3. **Q: Is `PUT` idempotent? Is `POST`?** A: `PUT` yes, by definition (full replace). `POST` only with a client-supplied idempotency key.

(Full week-level deck: `05-flashcards.md`.)

## 14. Practice Exercises

1. Reproduce §3 yourself: `practice/sql/week-04/pagination-lab.sql`.
2. Design a hybrid pagination scheme for an admin UI that needs both efficient "next page" behavior and an approximate jump-to-page control.
3. Take an endpoint in a system you know using `OFFSET` pagination. Estimate the row count at which its cost would become noticeable, using this chapter's measured growth pattern as a reference.

## 15. Additional Reading

- [Google API Design Guide](https://cloud.google.com/apis/design) — resource naming, standard methods, error design

## 16. Official References

- [RFC 9457 — Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457) — a standardized error-response format
