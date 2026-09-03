---
title: "Flashcards: Strangler Fig and Migration Patterns"
slug: strangler-fig-and-migration-patterns
document_type: flashcard-deck
domain: architecture
topic_id: T-912
canonical: ../handbook/architecture/strangler-fig-and-migration-patterns.md
last_updated: 2026-09-02
---

# Flashcards: Strangler Fig and Migration Patterns

**Canonical chapter:** [`syllabus/17-architecture/strangler-fig-and-migration-patterns.md`](../syllabus/17-architecture/strangler-fig-and-migration-patterns.md)

## Card: Why does rollback silently stop being safe?

**Prompt:**
Why can a "we have a rollback plan" migration still lose data on rollback?

**Answer:**
Because rollback safety depends on dual-write to legacy staying active. The moment dual-write is disabled, every subsequent write exists only in the new system — rolling reads back to legacy after that point silently loses that data, with no error at the moment dual-write was turned off.

**Why it matters:**
This chapter's own demo proves it directly: an identical rollback scenario loses 3 of 6 orders with dual-write disabled at cutover, and 0 of 6 with it kept on.

**Common trap:**
Treating "we can roll back" as a permanent property of having built a migration plan, rather than a time-bounded state tied to dual-write's current status.

**Related:**
[handbook/architecture/strangler-fig-and-migration-patterns.md](../syllabus/17-architecture/strangler-fig-and-migration-patterns.md)

## Card: Strangler Fig vs. rewrite

**Prompt:**
Why is "a rewrite would be faster" usually the wrong call for a legacy system migration?

**Answer:**
It discounts the legacy system's accumulated, undocumented business logic and edge cases. A rewrite that doesn't reproduce all of it silently breaks real workflows at one high-stakes cutover event, whereas Strangler Fig's incremental extraction surfaces those gaps in small, observable, reversible steps.

**Why it matters:**
This is the register's own named misconception, and a fast way to fail this specific follow-up question.

**Common trap:**
Assuming legacy code has no value beyond being "old" and hard to work with.

**Related:**
[handbook/architecture/strangler-fig-and-migration-patterns.md](../syllabus/17-architecture/strangler-fig-and-migration-patterns.md)

## Card: Migration ACL vs. steady-state ACL

**Prompt:**
How does this chapter's use of Anti-Corruption Layer differ from its use in DDD strategic design?

**Answer:**
Same pattern, different lifecycle: DDD strategic design's ACL is a permanent, steady-state relationship isolating one bounded context from another's ongoing evolution. This chapter's ACL is temporary — it exists specifically for the finite window of a migration, and is retired once the legacy system it protects against is gone.

**Why it matters:**
Conflating the two loses precision about exit criteria — a migration ACL should have a planned end date; a context-mapping ACL generally doesn't.

**Common trap:**
Treating every ACL as permanent infrastructure rather than asking whether this particular one has a retirement plan.

**Related:**
[handbook/architecture/strangler-fig-and-migration-patterns.md](../syllabus/17-architecture/strangler-fig-and-migration-patterns.md), [handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md](../syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md)
