---
title: "Flashcards: Domain Events vs. Integration Events"
slug: domain-events-vs-integration-events
document_type: flashcard-deck
domain: 17-architecture
topic_id: T-2426
canonical: ../syllabus/17-architecture/domain-events-vs-integration-events.md
last_updated: 2026-09-21
---

# Flashcards: Domain Events vs. Integration Events

**Canonical chapter:** [`syllabus/17-architecture/domain-events-vs-integration-events.md`](../syllabus/17-architecture/domain-events-vs-integration-events.md)

## Card: Domain event vs. integration event

**Prompt:**
What's the real difference between a domain event and an integration event?

**Answer:**
A domain event describes an internal state change, scoped to consumers inside the same bounded context, and is free to evolve with the domain model. An integration event is a separate, deliberately stable, versioned public contract describing the same real-world fact, for consumers outside that bounded context.

**Why it matters:**
Three existing chapters (CQRS, DDD Tactical, Event-Driven Architecture) all use "domain event" and "event" extensively without ever naming this distinction.

**Common trap:**
Treating "the event" as one shape with one true representation, rather than two deliberately separate ones for two different audiences.

**Related:**
[Definition and Purpose](../syllabus/17-architecture/domain-events-vs-integration-events.md#definition-and-purpose)

## Card: Why publishing a domain event directly is a real, deferred risk

**Prompt:**
What real, measured evidence shows publishing a domain event directly as an external message is dangerous?

**Answer:**
A real demo: a `NotificationConsumer` reads a `"total"` field off a directly-published domain event and works fine — until a real, well-motivated internal refactor (`double total` → `BigDecimal grandTotal`) happens, at which point the same consumer genuinely breaks (`Consumer expected field "total" but it was not present`). A translated integration event's `"totalAmount"` field stayed `49.99` before and after the identical refactor.

**Why it matters:**
The mistake is invisible today and only surfaces later, on an unrelated internal-only change — exactly the deferred-cost pattern that makes it easy to introduce and hard to trace back.

**Common trap:**
Assuming an internal refactor is safe without checking whether the changed event has any external consumers.

**Related:**
[Internal Implementation](../syllabus/17-architecture/domain-events-vs-integration-events.md#internal-implementation)

## Card: What the translation boundary actually does

**Prompt:**
Does a translation boundary between a domain event and an integration event only rename fields?

**Answer:**
No — it should also deliberately DROP internal-only fields that never should have been externally visible. This chapter's demo carries a `loyaltyPointsEarned` field on the domain event but deliberately excludes it from the public `OrderCompletedIntegrationEvent` contract.

**Why it matters:**
A well-built integration event minimizes public contract surface area to exactly what's actually being promised, not everything the internal model happens to carry.

**Common trap:**
Assuming the translation boundary is pure 1:1 field renaming rather than a deliberate filter.

**Related:**
[Core Concepts](../syllabus/17-architecture/domain-events-vs-integration-events.md#core-concepts)
