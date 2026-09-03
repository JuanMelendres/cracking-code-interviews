---
title: "Cheat Sheet: Event Sourcing and Its Real Costs"
slug: event-sourcing-and-its-real-costs
document_type: cheat-sheet
domain: architecture
topic_id: T-905
canonical: ../handbook/architecture/event-sourcing-and-its-real-costs.md
last_updated: 2026-09-02
---

# Event Sourcing and Its Real Costs

**Canonical chapter:** [`syllabus/09-messaging-event-driven/event-sourcing-and-its-real-costs.md`](../syllabus/09-messaging-event-driven/event-sourcing-and-its-real-costs.md)

## Core Mental Model

In a conventional system, a database row holds the current state, and history (if kept at all) is a separate, secondary concern. Event sourcing inverts this completely: the append-only sequence of events *is* the system of record, and current state is never stored directly — it's derived, every time, by replaying that sequence from the beginning (or from a snapshot forward). This single inversion produces both event sourcing's real appeal (a complete, immutable audit trail; temporal queries; rebuildable read models) and its real cost (deriving current state is never free, and gets more expensive as history grows).

## Essential Definitions

- **Event sourcing** — an aggregate's state is persisted as the complete, ordered sequence of domain events that produced it; current state is a derived, computed view.
- **Snapshot** — a periodically-saved current-state checkpoint that lets replay skip everything before it — a checkpoint, not a change to the truth.
- **Current state is always derived, never stored directly** — the single fact from which every other property in this topic follows.
- **Event sourcing vs. CQRS** — independent, composable decisions. CQRS separates read and write models; event sourcing is about how a single system persists its own state.
- **Event schema evolution difficulty** — old events are immutable and must remain replayable through current business logic indefinitely, the identical producer/consumer compatibility shape as a Kafka topic schema change.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Domain genuinely needs a complete, immutable audit trail | Event sourcing may be justified |
| "What did this look like at time T" is a real, recurring business requirement | Event sourcing may be justified |
| Current-state-only persistence would lose information the business actually needs later | Event sourcing may be justified |
| A snapshotting strategy is NOT planned before any aggregate can accumulate significant history | Required before adopting at any real scale — do not skip |
| The audit/history need is already served by a simpler mechanism (an audit log table) | Prefer the simpler mechanism |

**Comparison:**

| Dimension | Event sourcing | Current-state-only persistence |
|---|---|---|
| Read cost (no snapshot) | Grows with history (measured: 16ms at 200,000 events) | Constant, regardless of history |
| Audit trail | Complete and immutable, by construction | Requires a separate, explicit mechanism |
| Schema evolution | Genuinely harder — old events immutable, must remain replayable | Simpler — in-place migration possible |

## Key Numbers (real, executed Java 21 against a real file-backed, append-only event store)

- Replay time growth: 1,001 events → 0ms (12,513 bytes); 10,001 events → 1ms; 50,001 events → 5ms; 100,001 events → 8ms; 200,001 events → 16ms (2,500,013 bytes).
- Snapshot benefit: full replay from event 0 (no snapshot) took 20ms, real balance=700003. Snapshot + replay only the real tail (10,000 events) took 1ms, identical real balance=700003. Measured speedup: 20.0x.
- The snapshot mechanism uses a real `RandomAccessFile.seek()` to jump directly to a recorded byte offset — bytes before that offset are never read from disk at all.

## Common Pitfalls

- Describing event sourcing's benefits fluently while having no answer for its costs.
- Adopting event sourcing without a snapshotting plan, discovering the real cost only once an aggregate's history has already grown large.
- Treating event sourcing and CQRS as the same thing, or as always required together — they're independent, composable decisions.
- Assuming an event's meaning is fixed forever once written, without planning for the real schema-evolution difficulty.
- Mutating or deleting past events to "fix" a mistake, rather than appending a real, forward-moving compensating event.

## Interview Answer Skeleton

**30-sec:** Event sourcing stores an aggregate's complete event history as the system of record, deriving current state by replaying it — giving a complete audit trail and temporal queries, at the real cost of replay time growing with history length, which requires periodic snapshotting to bound.

**2-min:** Add the measured replay-cost growth (near-zero to 16ms across 200,000 events) and the real 20x snapshot speedup with an identical final balance. Name schema evolution as the second real cost: old events are immutable and must remain replayable through current logic indefinitely.

**Whiteboard:** Draw a long horizontal row of small event boxes, then an arrow from the far-right box down to a "current state" box: "derived by replaying ALL of these, every time." Redraw with a "snapshot" flag planted partway through, shortening the arrow to span only from the flag to the end: "everything to the left of this flag is never read again."

**Staff-level framing:** Discuss snapshotting strategy as a required, up-front design decision rather than a reactive fix. Connect event schema evolution explicitly to the real compatibility-mode mechanics (backward/forward compatibility). Reason about when a simpler mechanism (an audit log table) would serve the actual business need without incurring event sourcing's full, ongoing operational cost.

## Production Warning Signs

- Cart/aggregate load latency for long-lived accounts grows noticeably slower over time, eventually exceeding a timeout — debug signal: load latency correlates with that specific instance's individual event count, not overall system load; the fix is periodic snapshotting, not a database index.
- A snapshot format drifts out of sync with the current event schema — a snapshot taken under an old schema can silently misrepresent state when combined with newer post-snapshot events.
- Replaying old events through changed business logic produces a different result than it did when originally applied — a subtle correctness risk unique to event-sourced systems.
- Any correction to a "wrong" past event implemented as an edit to history rather than a new, forward-moving compensating event — breaks the immutability guarantee that is event sourcing's entire value proposition.

## Related

- `syllabus/17-architecture/cqrs-read-write-separation.md`
- `syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md`
- `syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md`
- `syllabus/10-distributed-systems/distributed-transactions-saga-and-outbox.md`
