---
title: "Cheat Sheet: CQRS Read/Write Separation"
slug: cqrs-read-write-separation
document_type: cheat-sheet
domain: architecture
topic_id: T-904
canonical: ../handbook/architecture/cqrs-read-write-separation.md
last_updated: 2026-09-02
---

# CQRS: Read/Write Separation

**Canonical chapter:** [`handbook/architecture/cqrs-read-write-separation.md`](../handbook/architecture/cqrs-read-write-separation.md)

## Core Mental Model

CQRS is not "reads and writes go through different code" — it's "the read model is a different, purpose-built truth, kept honest by a real asynchronous pipe instead of a shared schema." The pattern starts at the point where the read side stops asking "give me the current row" and starts asking "give me a materialized answer that was already computed, in a shape the write side's schema was never designed to produce efficiently." That materialized answer is fed by something, and that something is necessarily not instantaneous. The entire pattern is a trade: query speed and shape freedom, purchased with a real lag window and a second thing to keep correct.

## Essential Definitions

- **CQRS (Command Query Responsibility Segregation)** — separates the model used to change state (command/write) from the model used to read state (query/read), each independently designed, scaled, and evolved.
- **CQS (Command-Query Separation)** — the older, narrower, method-level rule (Bertrand Meyer, 1988): a method is either a command or a query, never both — no architectural weight of its own.
- **The projector** — a real asynchronous consumer that folds domain events into the read model; this is where the pattern's real, measured cost lives, not a formality.
- **Eventual consistency** — the real, nonzero window between a write committing and the read model reflecting it; demonstrated, not asserted.
- **CQRS-lite** — a single hot query bypassing the domain/repository abstraction, same schema, no separate store, no event pipeline — the cheaper default move before reaching for full CQRS.

## Decision Table

Use full CQRS only when **all** of these hold:

| Condition | Why it matters |
|---|---|
| The read pattern's *shape* has diverged from the write pattern's, not just its volume | "Reads happen more than writes" alone is true of nearly every system |
| A read replica or materialized view has already been ruled out for a stated reason | These solve read *load* without introducing a second model to keep correct |
| The team can own a real asynchronous pipeline (queue, projector, lag budget) | This is ongoing operational surface, not a one-time cost |
| The business can genuinely tolerate the measured lag window for this specific read | A spend report tolerating seconds is different from a balance check gating a withdrawal |

**Comparison:**

| | CQS | CQRS-lite | Full CQRS | Event Sourcing |
|---|---|---|---|---|
| Scope | One method | One hot query path | A whole bounded context | State representation itself |
| Separate data store? | No | No | Usually yes | Independent decision |
| Async boundary? | No | No | Yes, real | Independent decision |

CQRS does **not** require Event Sourcing, and Event Sourcing does not require CQRS — independent decisions that compose well but neither requires the other.

## Key Numbers (real, executed Java demo: normalized write model, denormalized read model, `BlockingQueue` projector)

- Best-case, in-process, zero-network eventual-consistency lag (write commit to read model reflecting it): median 1.5 microseconds, p99 9.6 microseconds over 5,000 samples.
- Forced stale-read sequence after a deliberately slowed projector: at t+11ms and t+96ms, the read model has no record of the order at all; at t+181ms/t+266ms, a partially-applied state (`status=OPEN`, `items=0`); fully converged only at t+452ms.
- Query speedup on 50,000 orders (4 items each, 300,000 domain events): re-deriving the total on the normalized write model took 15.84ms; reading the precomputed value off the read model took 3.45ms — a real 4.6x speedup (a repeat run measured 5.4x). Both answers checked for exact equality and matched.

## Common Pitfalls

- Calling CQS "CQRS" or vice versa — conflating a method-level style rule with an architectural pattern carrying real operational cost.
- Reaching for full CQRS because "reads and writes are different" — true of virtually every system, not by itself the justifying condition.
- Introducing a second data store for reads before confirming a read replica or materialized view genuinely can't do the job.
- Treating Event Sourcing as a required companion to CQRS, or vice versa.
- Underestimating the eventual-consistency window as "basically instant" without ever measuring it.

## Interview Answer Skeleton

**30-sec:** CQRS splits the model used to write data from the model used to read it, so each can be shaped and scaled independently — usually because a read pattern's shape has diverged enough from the write pattern's that sharing one schema now costs more than maintaining two. The trade is a real asynchronous pipeline and a real, measurable eventual-consistency window.

**2-min:** Add the measured numbers: microsecond-scale best-case lag, the forced stale-then-converged sequence over 452ms, and the 15.84ms → 3.45ms (4.6x) query speedup on a "total spend per customer" report.

**Whiteboard:** Draw "Command side" and "Query side" boxes, each with its own database cylinder — do not connect the cylinders directly. Between them, a queue/pipe icon labeled "events," arrow from command side in, arrow from it into a "projector" box pointing at the query cylinder. Circle the queue-and-projector piece: "this is where the real cost is." Annotate the read cylinder: "can be rebuilt from the event log."

**Staff-level framing:** Recognize CQRS as an organizational and operational commitment — a second thing on-call must understand, a second thing that can silently fall behind without an explicit lag alert, and a schema-evolution contract (the events) with no compiler enforcing it. Propose CQRS-lite or a read replica as the cheaper first move and explain precisely why each falls short before reaching for the full pattern. Treat rebuildability from the event stream as non-negotiable.

## Production Warning Signs

- A reporting query re-derives an aggregate (sum, join) on every request against a normalized write schema and degrades linearly with row count — the structural signal that a precomputed read model, not a cache, is the right fix.
- Projector falls behind under load — event queue backs up and observed lag grows without bound; needs scaling the projector, partitioning the event stream, or backpressure on writes, not treated as a bug.
- A crashed, unrecovered projector leaves the read model frozen at its last-applied event while the write model keeps moving — recovery requires resuming from a durable offset/checkpoint, not restarting from empty.
- Silent schema drift between an event's shape and its projector, changed on the assumption "it's just an internal event" — the events are the contract, with no compile-time signal across the boundary.

## Related

- `handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md`
- `handbook/architecture/event-driven-architecture-integration-styles.md`
- `handbook/system-design/distributed-transactions-saga-and-outbox.md`
- `handbook/databases/replication-read-replicas-and-replica-lag.md`
