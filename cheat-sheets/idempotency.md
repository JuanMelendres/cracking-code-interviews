---
title: "Cheat Sheet: Idempotency at System Edges"
slug: idempotency
document_type: cheat-sheet
domain: system-design
topic_id: T-809
canonical: ../handbook/system-design/idempotency.md
last_updated: 2026-08-03
---

# Idempotency at System Edges

**Canonical chapter:** [`handbook/system-design/idempotency.md`](../handbook/system-design/idempotency.md)

## Core Mental Model

An idempotency key turns "I don't know if that succeeded" into "it doesn't matter — ask again and you'll get the same answer." The client's ambiguity about the outcome and the server's problem of possibly re-executing an already-handled operation are the same problem viewed from two sides. The client structurally cannot resolve this ambiguity; the server can, because it's the party with ground truth about what actually executed. Idempotency keys move resolution entirely to that side.

## Essential Definitions

- **Idempotent operation** — performing it multiple times produces the same result *and the same side effect* as performing it once.
- **Idempotency key** — a client-generated unique identifier for one logical operation, sent with every retry; lets the server recognize "I've already handled this" and return the original result instead of re-executing.
- **Storage** — a table keyed on the idempotency key, with a `UNIQUE` constraint doing the actual coordination work — not application-level locking.
- **TTL** — recovery mechanism so a crashed `IN_PROGRESS` attempt can't permanently block future retries; a request finding a stale `IN_PROGRESS` row older than the TTL treats the original as presumed-dead, deletes it, retries.

## Decision Table

| Situation | What to reach for |
|---|---|
| Any mutating endpoint with a real side effect | Require and honor a client-supplied idempotency key |
| Coordinating concurrent duplicate requests | A database unique constraint — never application-level locking alone |
| A crashed in-progress attempt | A TTL that reclaims the key after presumed death |
| Service scaled across multiple instances | Verify the idempotency state is shared (database/distributed cache), not per-instance memory |

## Key Numbers (real, measured — Java against PostgreSQL 16)

**Concurrent-duplicate trace** (two threads, same key, simultaneously):
```
Request A: charged $50.00, confirmation #49940811261291
Request B: charged $50.00, confirmation #49940811261291
Actual charges performed: 1 (both requests return the same result)
```
Mechanism: both attempt `INSERT ... VALUES (?, 'IN_PROGRESS')`; the unique constraint guarantees exactly one insert succeeds; the loser catches the unique-violation (`SQLState 23505`), polls briefly, returns the same stored result once `COMPLETED`.

**TTL crash-recovery trace:** a stale `IN_PROGRESS` row (age 10s, TTL 5s) did **not** block a fresh attempt with the same key — new request succeeded, charged $75.00, confirming TTL reclaim worked.

The mechanism is O(1) per request (an insert, and on conflict, a bounded poll loop) — the value is correctness under real concurrency, not algorithmic cost.

## Common Pitfalls

- Implementing "idempotency" as a client-side check (disabling a submit button) rather than a server-side, storage-backed mechanism — doesn't protect against a genuine network retry or a second independent client
- Application-level locking instead of a database unique constraint, introducing a race the constraint would have closed for free
- No TTL at all — a crashed in-progress attempt permanently blocks all future retries

## Interview Answer Skeleton

**30-sec:** An idempotency key lets a server recognize a retried request and return the original result instead of re-executing. Mechanism = client-generated key + DB table with unique constraint doing the coordination + TTL so a crashed attempt doesn't block future retries forever.

**2-min:** Add why it exists (structural answer to lost/slow/succeeded-but-lost-response ambiguity) + the insert-race mechanism + the concurrent-duplicate trace (exactly one charge for two simultaneous requests).

**Whiteboard:** Draw Request A and B both INSERTing into `idempotency_keys` — one gets a checkmark (SUCCESS), other gets an X (UNIQUE VIOLATION) and loops polling until COMPLETED — both arrows converge on the same result box.

**Staff-level framing:** idempotency keys are one instance of the broader pattern — moving ambiguity resolution to the party with the most information (the server, since it's the one that would have executed the side effect).

## Production Warning Signs

- Duplicate charges appear only *after* horizontal scaling — the signature symptom of per-instance-state idempotency logic
- **Real incident:** in-memory `ConcurrentHashMap` idempotency check worked on one instance, silently allowed duplicates once scaled to three (each instance had its own map, no shared state). Stopgap: sticky routing while the durable fix ships. Fix: shared/durable storage (database, distributed cache), never process memory.
- Design-review checklist item: "does this mechanism's state live somewhere shared across all instances, or only within one process's memory?"

## Related

- `handbook/system-design/distributed-systems-failure-modes.md`
- [CAP Theorem and Consistency Models](cap-theorem-and-consistency-models.md)
- `handbook/kafka/delivery-semantics-and-exactly-once.md`
