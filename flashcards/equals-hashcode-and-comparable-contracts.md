---
title: "Flashcards: equals(), hashCode(), and Comparable Contracts"
slug: equals-hashcode-and-comparable-contracts
document_type: flashcard-deck
domain: java-core
topic_id: T-101
canonical: ../handbook/java-core/equals-hashcode-and-comparable-contracts.md
last_updated: 2026-08-06
---

# Flashcards: equals(), hashCode(), and Comparable Contracts

**Canonical chapter:** [`handbook/java-core/equals-hashcode-and-comparable-contracts.md`](../handbook/java-core/equals-hashcode-and-comparable-contracts.md)

## Card: The equals/hashCode contract

**Prompt:**
What is the equals/hashCode contract, precisely?

**Answer:**
If two objects are `equals()`, they must have the same `hashCode()`. The reverse isn't required — unequal objects may share a hash code.

**Why it matters:**
The rule every hash-based collection assumes holds.

**Common trap:**
Overriding one without the other.

**Related:**
[Definition and Purpose](../handbook/java-core/equals-hashcode-and-comparable-contracts.md#definition-and-purpose)

## Card: What breaks a HashSet silently

**Prompt:**
What happens if you override `equals()` but not `hashCode()`?

**Answer:**
Equal objects can end up with different hash codes, so a `HashSet` looks in the wrong bucket and fails to recognize a duplicate — silently, no exception.

**Why it matters:**
The most common real-world instance of this contract violation.

**Common trap:**
Assuming a broken contract produces a visible error.

**Related:**
[Internal Implementation](../handbook/java-core/equals-hashcode-and-comparable-contracts.md#internal-implementation)

## Card: What TreeSet uses for duplicate detection

**Prompt:**
What does `TreeSet` use to decide two elements are "the same"?

**Answer:**
`compareTo() == 0` exclusively — it never consults `equals()` at all.

**Why it matters:**
A `Comparable` inconsistent with `equals()` can silently drop a genuinely distinct element.

**Common trap:**
Assuming `TreeSet` falls back to `equals()` the way `HashSet` does within a bucket.

**Related:**
[Internal Implementation](../handbook/java-core/equals-hashcode-and-comparable-contracts.md#internal-implementation)
