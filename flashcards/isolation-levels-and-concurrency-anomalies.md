---
title: "Flashcards: Isolation Levels and Concurrency Anomalies"
slug: isolation-levels-and-concurrency-anomalies
document_type: flashcard-deck
domain: databases
topic_id: T-611
canonical: ../handbook/databases/isolation-levels-and-concurrency-anomalies.md
last_updated: 2026-08-06
---

# Flashcards: Isolation Levels and Concurrency Anomalies

**Canonical chapter:** [`syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md`](../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md)

## Card: Lost update vs. write skew

**Prompt:**
What's the difference between a lost update and write skew?

**Answer:**
Lost update is a same-row conflict, prevented by locking. Write skew is a cross-row invariant violation — each transaction's own single-row write looks fine in isolation, but the combination breaks an invariant spanning both rows.

**Why it matters:**
The single most commonly conflated pair of anomalies; this project's own interview feedback names the write-skew question as specifically discriminating.

**Common trap:**
Answering a write-skew question by describing a lost update instead.

**Related:**
[Core Concepts](../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#core-concepts)

## Card: Does REPEATABLE READ prevent write skew?

**Prompt:**
Does REPEATABLE READ prevent write skew?

**Answer:**
No — confirmed via real reproduction in this chapter. It prevents same-row lost updates but not cross-row invariant violations.

**Why it matters:**
The specific, non-obvious gap that makes write skew "the discriminating question."

**Common trap:**
Assuming any isolation level stronger than READ COMMITTED must prevent all anomalies.

**Related:**
[Internal Implementation](../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#internal-implementation)

## Card: What SERIALIZABLE requires from application code

**Prompt:**
What must application code do to safely use SERIALIZABLE?

**Answer:**
Implement retry-on-serialization-failure — an aborted transaction under SSI is expected, recoverable behavior, not an error to surface to the user.

**Why it matters:**
SERIALIZABLE without retry logic fails intermittently under contention instead of being "more correct."

**Common trap:**
Treating SERIALIZABLE as a drop-in stronger guarantee with no code changes required.

**Related:**
[Internal Implementation](../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#internal-implementation)

## Card: Cheaper fix for a same-row race

**Prompt:**
What's the READ-COMMITTED-compatible fix for an application-level read-then-write race, without escalating isolation level?

**Answer:**
`SELECT ... FOR UPDATE` — takes the row lock at read time, closing the window a plain `SELECT` leaves open.

**Why it matters:**
Prevents reflexively escalating every concurrency bug to SERIALIZABLE when a cheaper, narrower fix exists.

**Common trap:**
Reaching for SERIALIZABLE for a same-row problem that a row lock would solve more cheaply.

**Related:**
[Java Examples](../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#java-examples)
