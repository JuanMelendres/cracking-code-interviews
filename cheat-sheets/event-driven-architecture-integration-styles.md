---
title: "Cheat Sheet: Event-Driven Architecture Integration Styles"
slug: event-driven-architecture-integration-styles
document_type: cheat-sheet
domain: architecture
topic_id: T-906
canonical: ../handbook/architecture/event-driven-architecture-integration-styles.md
last_updated: 2026-09-02
---

# Event-Driven Architecture: Integration Styles, Choreography, and Orchestration

**Canonical chapter:** [`handbook/architecture/event-driven-architecture-integration-styles.md`](../handbook/architecture/event-driven-architecture-integration-styles.md)

## Core Mental Model

Every event-driven integration answers two independent questions: what does the event contain (a bare notification, a full data snapshot, or the full history of everything that happened), and who decides what happens next (each service reacting locally with no one in charge — choreography — or a single coordinator explicitly directing every step — orchestration). These two questions are orthogonal: you can orchestrate with thin events or choreograph with fat events. Events don't remove coupling — they relocate it, into runtime availability or into schema, depending on the style chosen.

## Essential Definitions

- **Event notification** — publishes a minimal event (an identifier and a fact); a consumer needing more must query the producer directly.
- **Event-carried state transfer** — publishes the data itself, embedded in the event, so consumers are self-sufficient once received.
- **Event sourcing** — the event stream itself is the system of record; current state is derived by replaying it (a separate, deeper topic).
- **Choreography** — each service subscribes to events and reacts, with no central authority over the overall flow.
- **Orchestration** — a coordinator explicitly invokes each participant in sequence and knows the whole flow.
- **Coupling relocation, not removal** — event notification keeps temporal coupling for the callback the consumer must eventually make; event-carried state transfer removes that but creates schema coupling (every consumer depends on the producer never breaking that shape).

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Overall workflow logic needs to be visible and auditable in one place | Orchestration |
| Services will be added to or removed from this workflow frequently | Choreography |
| A consumer must remain functional if the producer is briefly unavailable | Event-carried state transfer |
| Minimizing event schema surface/versioning burden is the priority | Event notification |
| Distributed tracing is NOT already in place across the system | Lean orchestration (choreography needs tracing to be debuggable) |

**Style comparison:**

| Dimension | Choreography | Orchestration |
|---|---|---|
| Workflow logic location | Distributed, implicit | Centralized, explicit |
| Debuggability | Requires deliberate tracing — real stack trace has no causal link | Native — one call stack shows the whole flow |
| Single point of failure | None structurally, but harder to reason about | The coordinator, unless made durable |

## Key Numbers (real, executed Java 21)

- Choreography's captured stack trace at the Shipping handler: 9 frames, all JDK executor internals or the event bus's own dispatch code — explicitly no frame referencing the original publish call.
- Orchestration's captured stack trace for the equivalent step: includes the entire causal chain — `placeOrder` and `Main` both present.
- Producer-outage simulation: event notification's consumer genuinely throws (`IllegalStateException`) when the producer is unavailable at consumption time; event-carried state transfer's consumer genuinely succeeds under the identical simulated outage.

## Common Pitfalls

- Saying "events decouple services" without being able to name what the coupling turned into.
- Confusing event notification with event-carried state transfer, or treating event sourcing as a synonym for either rather than a distinct, more radical style.
- Choosing choreography for a complex multi-step workflow without first committing to a tracing strategy, then being unable to answer "how would you debug this in production."
- Conflating this chapter's general choreography-vs-orchestration question with the Saga/Outbox chapter's compensating-transaction-specific version — they cross-reference, not duplicate.

## Interview Answer Skeleton

**30-sec:** Event-driven integration has two independent choices: what the event carries (notification, full data, or full history) and who coordinates the workflow (choreography or orchestration). Events don't remove coupling, they relocate it — into runtime availability, or into schema, depending on the style.

**2-min:** Add the real stack-trace evidence (choreography's 9-frame dispatch-only stack vs. orchestration's causal chain intact) and the real producer-outage result (event notification's consumer throws; event-carried state transfer's consumer succeeds).

**Whiteboard:** Draw three services connected by a direct call arrow, cross it out, redraw around an "event bus" box with "publish" in and "dispatch (async)" out. Draw a dashed line attempting to connect the original publish to the final consumer's reaction, and cross that out too: "there is no line here unless we build one."

**Staff-level framing:** Reason about organizational scale — choreography's implicit workflow logic becomes a real cross-team coordination cost once five or more teams each own one participant, since no one team can see or change the whole flow without cross-team agreement. Discuss the migration path from event notification to event-carried state transfer as callback latency becomes measured, and the schema-governance process that migration then requires.

## Production Warning Signs

- Support cannot determine within hours why an order was charged but never shipped in a choreographed pipeline — no single log line, stack trace, or trace ID connects "payment charged" to "shipping's reaction to it" because none was deliberately built. Fix: mandatory `correlationId` on every event, distributed trace propagation across the event bus.
- A consumer's callback to the producer fails and the failure is swallowed rather than retried or dead-lettered — the consumer silently never completes its reaction.
- A producer changes an embedded event's shape (adds a required field, renames one) and every consumer that embedded the old shape breaks silently at deserialization time, not loudly at the point of change.
- An orchestrator accumulating business logic that belongs in the participants themselves — a "god service" anti-pattern; or an orchestrator whose own state isn't made durable, losing track of which steps completed on a crash mid-workflow.

## Related

- `handbook/architecture/cqrs-read-write-separation.md`
- `handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md`
- `handbook/system-design/distributed-transactions-saga-and-outbox.md`
- `handbook/kafka/schema-registry-and-compatibility-evolution.md`
