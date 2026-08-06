---
title: "Flashcards: Distributed Systems Failure Modes"
slug: distributed-systems-failure-modes
document_type: flashcard-deck
domain: system-design
topic_id: T-909
canonical: ../handbook/system-design/distributed-systems-failure-modes.md
last_updated: 2026-08-06
---

# Flashcards: Distributed Systems Failure Modes

**Canonical chapter:** [`handbook/system-design/distributed-systems-failure-modes.md`](../handbook/system-design/distributed-systems-failure-modes.md)

## Card: Why a timeout is ambiguous

**Prompt:**
Why is a network timeout ambiguous?

**Answer:**
It can't distinguish "request lost," "still processing," and "succeeded but the response was lost."

**Why it matters:**
The root cause of nearly every failure mode in this domain.

**Common trap:**
Treating a timeout as definite proof the request failed.

**Related:**
[Core Concepts](../handbook/system-design/distributed-systems-failure-modes.md#core-concepts)

## Card: How retries amplify an outage

**Prompt:**
Precisely how do retries amplify an outage?

**Answer:**
They add new work on top of the still-running original attempt rather than replacing it, multiplying load on an already-degraded system.

**Why it matters:**
The exact, measured mechanism — not a vague "retries are risky" statement.

**Common trap:**
Assuming a client giving up cancels the work already submitted downstream.

**Related:**
[Internal Implementation](../handbook/system-design/distributed-systems-failure-modes.md#internal-implementation)

## Card: What fixes retry safety structurally

**Prompt:**
What structurally fixes the retry-safety problem?

**Answer:**
Idempotency keys — the server recognizes a retried operation and returns the original result instead of re-executing it.

**Why it matters:**
Shifts the burden of resolving retry ambiguity from the client to the server.

**Common trap:**
Believing better retry-policy tuning alone (without idempotency) is sufficient.

**Related:**
[Java Examples](../handbook/system-design/distributed-systems-failure-modes.md#java-examples)

## Card: What prevents split-brain corruption

**Prompt:**
What structurally prevents split-brain corruption?

**Answer:**
A fencing token, enforced at the storage/resource layer, rejecting any write carrying an older token than one already accepted.

**Why it matters:**
Leader election alone only decides who is elected, not whether a stale former leader can still cause damage.

**Common trap:**
Assuming leader election alone is sufficient to prevent split-brain.

**Related:**
[Internal Implementation](../handbook/system-design/distributed-systems-failure-modes.md#internal-implementation)
