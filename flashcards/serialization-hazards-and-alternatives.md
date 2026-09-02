---
title: "Flashcards: Serialization Hazards and Alternatives"
slug: serialization-hazards-and-alternatives
document_type: flashcard-deck
domain: java-core
topic_id: T-115
canonical: ../handbook/java-core/serialization-hazards-and-alternatives.md
last_updated: 2026-09-02
---

# Flashcards: Serialization Hazards and Alternatives

**Canonical chapter:** [`handbook/java-core/serialization-hazards-and-alternatives.md`](../handbook/java-core/serialization-hazards-and-alternatives.md)

## Card: The constructor-bypass fact

**Prompt:**
Does `ObjectInputStream.readObject()` call the class's constructor?

**Answer:**
No — verified directly with real byte-level stream tampering. It reconstructs state directly from bytes, bypassing any constructor-enforced validation entirely.

**Why it matters:**
The single foundational fact every serialization hazard in this chapter traces back to.

**Common trap:**
Assuming constructor validation applies universally, including on the deserialization path.

**Related:**
[Internal Implementation](../handbook/java-core/serialization-hazards-and-alternatives.md#internal-implementation)

## Card: Fixing a broken singleton

**Prompt:**
How do you keep a Singleton's `==` identity intact across serialization?

**Answer:**
Implement `private Object readResolve() { return INSTANCE; }` — verified directly to restore `==` equality across a real round trip.

**Why it matters:**
A real, common, and easy-to-miss way `Serializable` silently breaks an existing design guarantee.

**Common trap:**
Assuming a private constructor alone is sufficient once the class implements `Serializable`.

**Related:**
[Internal Implementation](../handbook/java-core/serialization-hazards-and-alternatives.md#internal-implementation)

## Card: The real, current defense

**Prompt:**
What's the JDK's own current, standard mechanism for restricting what a deserialization stream is allowed to reconstruct?

**Answer:**
`ObjectInputFilter` (JEP 290, standard since Java 9; JEP 415 in Java 17+) — a real, verified allow-list that rejects disallowed classes with `InvalidClassException` before the object graph is reconstructed.

**Why it matters:**
The real, current JDK-native mitigation for the gadget-chain RCE risk class.

**Common trap:**
Assuming a process has a default filter configured — verified directly in this chapter, it does not unless explicitly set.

**Related:**
[Internal Implementation](../handbook/java-core/serialization-hazards-and-alternatives.md#internal-implementation)
