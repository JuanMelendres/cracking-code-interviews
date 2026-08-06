---
title: "Flashcards: Idempotency at System Edges"
slug: idempotency
document_type: flashcard-deck
domain: system-design
topic_id: T-809
canonical: ../handbook/system-design/idempotency.md
last_updated: 2026-08-06
---

# Flashcards: Idempotency at System Edges

**Canonical chapter:** [`handbook/system-design/idempotency.md`](../handbook/system-design/idempotency.md)

## Card: What an idempotency key protects against

**Prompt:**
What does an idempotency key actually protect against?

**Answer:**
A duplicate side effect (e.g., a double charge) from a client retrying a request it can't confirm succeeded.

**Why it matters:**
The precise scope of the guarantee — not "prevents all bugs," specifically duplicate side effects under retry.

**Common trap:**
Implementing it as a client-side-only check that doesn't survive a genuine network retry.

**Related:**
[Definition and Purpose](../handbook/system-design/idempotency.md#definition-and-purpose)

## Card: What coordinates concurrent duplicates correctly

**Prompt:**
What coordinates concurrent duplicate requests correctly?

**Answer:**
The storage layer's own unique constraint — not application-level locking.

**Why it matters:**
Application-level locking reintroduces the exact race the database constraint eliminates for free.

**Common trap:**
Using a `synchronized` block or in-memory map instead of a database constraint.

**Related:**
[Core Concepts](../handbook/system-design/idempotency.md#core-concepts)

## Card: Why the TTL is necessary

**Prompt:**
Why is a TTL necessary on the mechanism?

**Answer:**
Without it, a crashed in-progress attempt permanently blocks every future retry of that key.

**Why it matters:**
The mechanism must handle its own failure mode, not just the happy path.

**Common trap:**
Building the key/storage mechanism without any TTL or crash-recovery path.

**Related:**
[Internal Implementation](../handbook/system-design/idempotency.md#internal-implementation)

## Card: Correct client behavior under ambiguity

**Prompt:**
What's the correct client behavior when a response never arrives?

**Answer:**
Retry, unconditionally, with the same idempotency key.

**Why it matters:**
The client structurally cannot resolve the ambiguity itself; the server-side mechanism makes retrying safe regardless.

**Common trap:**
Trying to have the client determine whether the operation succeeded before retrying.

**Related:**
[Core Concepts](../handbook/system-design/idempotency.md#core-concepts)
