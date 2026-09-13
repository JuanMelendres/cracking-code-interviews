---
title: "Cheat Sheet: GraphQL API Design"
slug: graphql-api-design
document_type: cheat-sheet
domain: 07-api-design
topic_id: T-917
canonical: ../syllabus/07-api-design/graphql-api-design.md
last_updated: 2026-09-12
---

# GraphQL API Design

**Canonical chapter:** [`syllabus/07-api-design/graphql-api-design.md`](../syllabus/07-api-design/graphql-api-design.md)

## Core Mental Model

GraphQL trades a fixed, server-decided response shape for a client-decided one, at the cost of moving complexity from "how many endpoints do I call" to "how do I resolve this field efficiently no matter how the client shaped the query." The resolver-per-field model is what makes that flexibility possible — and the N+1 problem is its direct, unavoidable consequence.

## Essential Definitions

- **Resolver** — a function resolving one field's value, independent of every other field.
- **N+1 problem** — a field resolver on every item in a list running once per item unless explicitly batched.
- **`DataLoader`** — defers and batches loads within one execution tick, collapsing N calls into 1.
- **Null propagation** — a non-null field's failure bubbles the null up to the nearest nullable ancestor.

## Decision Table

| Situation | What to reach for |
|---|---|
| A field reachable from a list needs a backend/DB lookup | `DataLoader`-batched resolver, by default |
| Deciding a field's nullability | Ask: should this field's failure be allowed to null its ancestors? |
| Protecting a public GraphQL endpoint from expensive queries | Query complexity scoring or max depth limiting |
| Explaining a GraphQL error to a REST-background engineer | Still HTTP `200`; check the `errors` array, not the status code |

## Common Pitfalls

- Assuming GraphQL handles the N+1 problem automatically, with no `DataLoader` or equivalent batching.
- Assuming partial success is guaranteed regardless of schema nullability — non-null fields bubble failure up to their nearest nullable ancestor, which can null the entire response.
- Expecting a `4xx`/`5xx` HTTP status the way a REST API would return one — GraphQL errors are still `200`, reported in the response body.
- Leaving a public GraphQL endpoint with no query complexity/depth limiting, letting a client request an arbitrarily expensive query.

## Interview Answer Skeleton

**30-sec:** GraphQL lets a client request exactly the fields it needs across what would be several REST resources, in one request — solving over/under-fetching. The honest cost: naive per-object field resolution causes a real N+1 backend-call problem, fixed with `DataLoader` batching.

**2-min:** Add: a real, measured demo shows 3 backend calls for 3 books naively, collapsing to 1 call with `DataLoader` batching. Null propagation (schema nullability deciding blast radius) and the "still HTTP 200 on error" behavior are the two most commonly misunderstood mechanics for engineers coming from REST.

**Staff-level framing:** GraphQL's flexibility is a real operational cost center, not a free win — query complexity limiting and resolver-level batching discipline are required investments, not optional hardening, for any public-facing schema.

## Related

- syllabus/07-api-design/rest-api-fundamentals.md
- syllabus/07-api-design/grpc-api-design.md
- syllabus/07-api-design/api-design.md
