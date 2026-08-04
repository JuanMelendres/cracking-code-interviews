---
title: "Cheat Sheet: DDD Tactical Design — Aggregates"
slug: ddd-tactical-design-aggregates
document_type: cheat-sheet
domain: architecture
topic_id: T-903
canonical: ../handbook/architecture/ddd-tactical-design-aggregates.md
last_updated: 2026-08-04
---

# DDD Tactical Design: Aggregates

**Canonical chapter:** [`handbook/architecture/ddd-tactical-design-aggregates.md`](../handbook/architecture/ddd-tactical-design-aggregates.md)

## Core Mental Model

An aggregate boundary answers exactly one question: what has to be true, all at once, for this data to be valid? Everything that must be consistent together belongs inside the boundary; everything else — no matter how conceptually related — belongs outside it, referenced only by ID, kept consistent eventually rather than atomically. Getting the boundary right means resisting the urge to group things that merely *feel* related.

## Essential Definitions

- **Aggregate** — a cluster of domain objects treated as a single consistency unit. One object (the aggregate root) is the only entry point the outside world is allowed to reference; everything inside is reachable only through the root.
- **Aggregate root** — the only object in an aggregate that external code is allowed to reference directly.
- **Sizing rule** — an aggregate should be as small as the true invariant requires, not as large as "everything related."
- **Transaction boundary** — the aggregate boundary is deliberately also the transaction boundary: a single aggregate is saved atomically, in one transaction; cross-aggregate consistency uses events/sagas (eventual consistency), not a shared database transaction.

## Decision Table

| Benefit | Cost |
|---|---|
| Invariants enforced in exactly one place, always | Cross-aggregate operations can't use a single ACID transaction — need sagas/eventual consistency |
| Clear repository-per-aggregate boundary simplifies persistence | Under-sizing pushes invariants outside the model; over-sizing hurts concurrency (whole aggregate locked for any change) |
| Concurrency conflicts scoped to one aggregate instance, not the whole database | Requires real domain modelling effort — not a mechanical decision |

| Question to ask | If yes | If no |
|---|---|---|
| Must these objects be consistent *right now*, together, always? | Same aggregate | Separate aggregates — reference by ID, eventual consistency |
| Would a change to one require locking/loading the other anyway? | Same aggregate | Separate aggregates |

## Key Numbers

Not applicable — this is a domain-modelling discipline, not a runtime mechanism; the chapter contains no measured benchmark data.

## Common Pitfalls

- Modelling an aggregate around object *composition* ("an order has lines, so they're one aggregate, and also has a customer, so that's included too") instead of around the actual consistency invariant
- Giving a non-root entity (e.g., `OrderLine`) its own repository, which allows code to bypass the root's invariant-enforcing methods entirely
- Assuming aggregates map one-to-one onto database tables — they don't; `Order`+`OrderLine` is one aggregate spanning two tables

## Interview Answer Skeleton

**30-sec:** An aggregate is a cluster of objects that must be consistent together, entered only through its root, saved atomically as a transaction boundary. `Order`+`OrderLine` are one aggregate because the total must match the lines; `Customer` is separate because no invariant requires it to change atomically with any one order.

**2-min:** Add why it exists (without a boundary, invariants have no single enforcement point) + the sizing rule + the under-sizing-vs-over-sizing trade-off + the flash-sale lock-contention production example.

**Whiteboard:** Draw the `Order` aggregate boundary containing the root and its `OrderLine`s, external arrows pointing only at the root, a crossed-out arrow attempting to reach `OrderLine` directly. Add `Customer` outside the boundary, connected by a plain reference arrow, annotated "no invariant requires these to change together."

**Staff-level framing:** aggregate boundaries are, in practice, the seams along which a system can later be decomposed into services — a well-drawn aggregate boundary today is very often the correct service boundary tomorrow. Getting this wrong early is one of the most common root causes of a monolith that resists decomposition later — the coupling wasn't accidental, it was modeled in from the start.

## Production Warning Signs

- **Real incident pattern:** during a flash sale, every order placement against a popular product times out even though the database has ample capacity and inventory counts update correctly — unrelated customer-profile updates during the same window also slow significantly. Root cause: `Customer` and `Order` modeled as one aggregate, so order-placement transactions lock the entire `Customer` row.
- Fix: re-model `Order` as its own aggregate referencing `Customer` by ID only — no significant trade-off, the original modelling was simply wrong.

## Related

- `handbook/databases/data-modelling-and-explicit-join-tables.md`
- [Microservice Decomposition and the Monolith Trade-off](microservice-decomposition-and-monolith-tradeoff.md)
- [Clean and Hexagonal Architecture](clean-hexagonal-architecture.md)
