---
title: "Flashcards: CQRS Read/Write Separation"
slug: cqrs-read-write-separation
document_type: flashcard-deck
domain: architecture
topic_id: T-904
canonical: ../handbook/architecture/cqrs-read-write-separation.md
last_updated: 2026-09-02
---

# Flashcards: CQRS Read/Write Separation

**Canonical chapter:** [`syllabus/17-architecture/cqrs-read-write-separation.md`](../syllabus/17-architecture/cqrs-read-write-separation.md)

## Card: CQS vs. CQRS

**Prompt:**
What's the difference between Command-Query Separation and CQRS?

**Answer:**
CQS is a method-level style rule (a method either changes state or returns data, never both). CQRS is an architectural pattern applying that same separation to a whole model, with a real asynchronous pipeline between a write model and one or more read models.

**Why it matters:**
Conflating the two is the single most common mistake candidates make on this topic.

**Common trap:**
Calling "different read/write DTOs" CQRS — that's CQS at the interface level, not CQRS.

**Related:**
[Comparisons](../syllabus/17-architecture/cqrs-read-write-separation.md#comparisons)

## Card: The real cost of CQRS

**Prompt:**
What is the one thing every CQRS explanation must name as a real cost, not a footnote?

**Answer:**
The asynchronous boundary between write and read models produces a real, measurable eventual-consistency window — never zero, even in the best possible in-process case.

**Why it matters:**
This chapter measured a real p50 of 1.5 microseconds best-case and a real, forced 452-millisecond stale-to-converged sequence — production systems over a real broker will be larger, not smaller.

**Common trap:**
Presenting CQRS as a strict upgrade with no downside.

**Related:**
[Core Concepts](../syllabus/17-architecture/cqrs-read-write-separation.md#core-concepts)

## Card: CQRS vs. Event Sourcing

**Prompt:**
Does CQRS require Event Sourcing?

**Answer:**
No. They're independent decisions that compose well but neither requires the other — this chapter's own practice code implements full CQRS with an ordinary in-memory write model and no event store.

**Why it matters:**
A very common conflation; correcting it precisely is a strong interview signal.

**Common trap:**
Assuming "domain events" in a CQRS pipeline means the system is event-sourced.

**Related:**
[Comparisons](../syllabus/17-architecture/cqrs-read-write-separation.md#comparisons)
