---
title: "Flashcards: Event Sourcing and Its Real Costs"
slug: event-sourcing-and-its-real-costs
document_type: flashcard-deck
domain: architecture
topic_id: T-905
canonical: ../handbook/architecture/event-sourcing-and-its-real-costs.md
last_updated: 2026-09-02
---

# Flashcards: Event Sourcing and Its Real Costs

**Canonical chapter:** [`handbook/architecture/event-sourcing-and-its-real-costs.md`](../handbook/architecture/event-sourcing-and-its-real-costs.md)

## Card: What's the register's own named cost of event sourcing?

**Prompt:**
What real, measurable cost does event sourcing introduce that this chapter proves directly?

**Answer:**
Replay time growing with event count — measured directly at near-zero for ~1,000 events, growing to 16ms for 200,000 events, using a real file-backed event store.

**Why it matters:**
It's the exact gap that separates a candidate who's read about event sourcing's appeal from one who understands its operational reality — the register's own topic title names it explicitly.

**Common trap:**
Describing event sourcing's benefits fluently with no mention of this cost.

**Related:**
[handbook/architecture/event-sourcing-and-its-real-costs.md](../handbook/architecture/event-sourcing-and-its-real-costs.md)

## Card: How does a snapshot actually help?

**Prompt:**
Does adding a snapshot change what an event-sourced aggregate's current state is?

**Answer:**
No — measured directly: the final balance was identical with and without the snapshot. What changes is only how expensively that state is reached: a real 20x speedup, from a real byte-offset seek that skips the snapshotted prefix entirely rather than merely skipping already-read events in memory.

**Why it matters:**
Clarifies that snapshotting is purely an optimization of the read path, never a change to the system's actual source of truth (the event log).

**Common trap:**
Treating a snapshot as if it replaces or supersedes the event log, rather than being a derived, disposable-and-rebuildable optimization on top of it.

**Related:**
[handbook/architecture/event-sourcing-and-its-real-costs.md](../handbook/architecture/event-sourcing-and-its-real-costs.md)

## Card: Event sourcing vs. CQRS

**Prompt:**
Are event sourcing and CQRS the same architectural decision?

**Answer:**
No — CQRS separates read and write models; event sourcing is about how state is persisted (as an event log, not current-state values). They're independent and composable: you can use either without the other, though they're frequently paired in practice because an event log is a convenient source for a CQRS read-model projector.

**Why it matters:**
Conflating the two is one of the most common imprecisions on this topic.

**Common trap:**
Treating "event sourcing" and "CQRS" as interchangeable terms.

**Related:**
[handbook/architecture/event-sourcing-and-its-real-costs.md](../handbook/architecture/event-sourcing-and-its-real-costs.md), [handbook/architecture/cqrs-read-write-separation.md](../handbook/architecture/cqrs-read-write-separation.md)
