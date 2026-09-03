---
title: "Flashcards: Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation"
slug: transactional-proxy-mechanics-and-propagation
document_type: flashcard-deck
domain: spring
topic_id: T-504
canonical: ../handbook/spring/transactional-proxy-mechanics-and-propagation.md
last_updated: 2026-08-06
---

# Flashcards: Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation

**Canonical chapter:** [`syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md`](../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md)

## Card: Why self-invocation breaks @Transactional

**Prompt:**
Why does calling an `@Transactional` method via `this` from within the same class not start a transaction?

**Answer:**
The call never passes through the Spring-managed proxy that intercepts calls and starts the transaction — it goes directly to the real target object.

**Why it matters:**
The single most reliable interview question in this domain for separating surface familiarity from real understanding.

**Common trap:**
Assuming Spring applies the transaction anyway via some form of bytecode magic.

**Related:**
[Core Concepts](../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md#core-concepts)

## Card: Default rollback rule

**Prompt:**
Does a checked exception roll back a `@Transactional` method by default?

**Answer:**
No — only `RuntimeException` and `Error` trigger rollback by default, unless `rollbackFor` says otherwise.

**Why it matters:**
The most common source of "why didn't my transaction roll back" production surprises.

**Common trap:**
Assuming any thrown exception triggers a rollback.

**Related:**
[Internal Implementation](../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md#internal-implementation), Demo 2

## Card: REQUIRES_NEW deadlock risk

**Prompt:**
What's the specific deadlock risk unique to `REQUIRES_NEW`?

**Answer:**
It suspends the outer transaction's connection and starts an independent one; if the inner transaction needs a row lock the suspended outer transaction already holds, it blocks until the inner transaction completes — which it can't, without that lock.

**Why it matters:**
`REQUIRES_NEW` looks safely independent but can self-deadlock in a way that's easy to miss in review.

**Common trap:**
Naming the audit-logging use case without naming the deadlock risk.

**Related:**
[Internal Implementation](../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md#internal-implementation), Demo 3

## Card: readOnly enforcement

**Prompt:**
Is `@Transactional(readOnly = true)` guaranteed to prevent writes?

**Answer:**
No — enforcement is driver-dependent. Confirmed not enforced on H2, enforced on PostgreSQL, in this chapter's own measured demo.

**Why it matters:**
Prevents treating a hint as a portable, cross-database guarantee.

**Common trap:**
Relying on `readOnly = true` as the sole write-prevention mechanism.

**Related:**
[Internal Implementation](../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md#internal-implementation), Demo 4 & 5
