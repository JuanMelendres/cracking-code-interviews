---
title: "Cheat Sheet: Domain Events vs. Integration Events"
slug: domain-events-vs-integration-events
document_type: cheat-sheet
domain: 17-architecture
topic_id: T-2426
canonical: ../syllabus/17-architecture/domain-events-vs-integration-events.md
last_updated: 2026-09-21
---

# Domain Events vs. Integration Events: Contract Boundaries and Translation

**Canonical chapter:** [`syllabus/17-architecture/domain-events-vs-integration-events.md`](../syllabus/17-architecture/domain-events-vs-integration-events.md)

## Core Mental Model

Domain event = internal design doc, free to change with the domain model. Integration event = public API documentation, a stable, versioned promise to everyone else. The same real-world fact, two deliberately separate representations.

## Essential Definitions

- **Domain event** — describes an internal state change; scoped to consumers inside the same bounded context; free to evolve with the domain model.
- **Integration event** — a separate, stable, versioned public contract describing the same fact, for consumers outside the bounded context.
- **Translation boundary** — the mapper/adapter that converts a domain event into an integration event, absorbing internal shape changes so they never become silent breaking external changes.

## Decision Table

| Situation | Correct approach |
|---|---|
| Event consumed only inside the bounded context | Domain event is enough; no translation needed |
| Event consumed by ANY external service | Translate through a stable, versioned integration event |
| Internal-only field (audit data, convenience denormalization) | Drop it at the translation boundary — never expose it externally |

## Common Pitfalls

- Publishing a domain event object directly onto a message broker as the external contract.
- Assuming an internal refactor is automatically safe without checking whether the changed event has external consumers.
- Treating the translation boundary as pure renaming, not also a deliberate filter for internal-only fields.

## Interview Answer Skeleton

**30-sec:** A domain event is internal and free to evolve; an integration event is a stable, versioned public contract. Publishing a domain event directly as the external message means every future internal refactor becomes a silent breaking change for external consumers.

**2-min:** Add: real demo proof — a consumer reading a domain-event field broke after a real internal refactor (`total` → `grandTotal`); the same consumer reading a translated integration-event field (`totalAmount`) didn't notice the same refactor at all.

**Staff-level framing:** Detecting an undocumented external dependency on an internal event — before it causes an incident — is a real cross-team governance question, not just a code-review checklist item.

## Related

- syllabus/17-architecture/cqrs-read-write-separation.md
- syllabus/17-architecture/ddd-tactical-design-aggregates.md
- syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md
- syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md
