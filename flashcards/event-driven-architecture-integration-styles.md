---
title: "Flashcards: Event-Driven Architecture Integration Styles"
slug: event-driven-architecture-integration-styles
document_type: flashcard-deck
domain: architecture
topic_id: T-906
canonical: ../handbook/architecture/event-driven-architecture-integration-styles.md
last_updated: 2026-09-02
---

# Flashcards: Event-Driven Architecture Integration Styles

**Canonical chapter:** [`syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md`](../syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md)

## Card: Where does coupling go when you add events?

**Prompt:**
"Events decouple services" — true or false, and why?

**Answer:**
Partially true. Direct call/temporal coupling is removed, but it relocates: into runtime availability (event notification, consumer must call back) or into schema (event-carried state transfer, consumer embedded the producer's data shape).

**Why it matters:**
The single most common interview misconception on this topic; naming the relocation precisely is a clear Staff-level signal.

**Common trap:**
Stopping at "events decouple things" without being able to say what replaced the coupling.

**Related:**
[handbook/architecture/event-driven-architecture-integration-styles.md](../syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md)

## Card: Why is choreography hard to trace?

**Prompt:**
Why can't you find the original cause of an event in a choreographed handler's call stack?

**Answer:**
Because dispatch through an event bus or broker is asynchronous by construction — `publish()` submits to an executor rather than calling the subscriber directly, so the subscriber's real call stack contains only dispatch machinery, never a frame from the original publisher.

**Why it matters:**
It's a structural property, not a logging gap — measured directly: a real 9-frame stack trace with zero frames referencing the original publish call.

**Common trap:**
Assuming "just add better logging" fixes this without a shared correlation identifier across services.

**Related:**
[handbook/architecture/event-driven-architecture-integration-styles.md](../syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md)

## Card: This chapter vs. the Saga chapter's choreography/orchestration

**Prompt:**
How does this chapter's choreography-vs-orchestration question differ from the one in the Saga/Outbox chapter?

**Answer:**
The Saga chapter's version is specifically about undoing a partially-completed multi-service transaction via compensating actions. This chapter's version is the general integration-style question for any event-driven workflow — coupling shape and debuggability, independent of whether compensation is involved at all.

**Why it matters:**
Conflating the two loses precision an interviewer is specifically listening for.

**Common trap:**
Answering a Saga-specific question with only this chapter's general framing, or vice versa.

**Related:**
[handbook/architecture/event-driven-architecture-integration-styles.md](../syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md)
