---
title: "Flashcards: Polymorphism and Dynamic Dispatch Mechanics"
slug: polymorphism-and-dynamic-dispatch
document_type: flashcard-deck
domain: java-core
topic_id: T-102
canonical: ../handbook/java-core/polymorphism-and-dynamic-dispatch.md
last_updated: 2026-09-02
---

# Flashcards: Polymorphism and Dynamic Dispatch Mechanics

**Canonical chapter:** [`handbook/java-core/polymorphism-and-dynamic-dispatch.md`](../handbook/java-core/polymorphism-and-dynamic-dispatch.md)

## Card: What's actually polymorphic in Java

**Prompt:**
Which kinds of member access are dynamically dispatched in Java?

**Answer:**
Only non-`static`, non-`private` instance method calls — resolved via `invokevirtual` by the object's actual runtime class.

**Why it matters:**
Fields, static methods, and overload resolution are all resolved at compile time by declared type, despite superficially looking like they should behave polymorphically too.

**Common trap:**
Assuming any subclass member with the same name as a superclass member is "overriding" it.

**Related:**
[Core Concepts](../handbook/java-core/polymorphism-and-dynamic-dispatch.md#core-concepts)

## Card: Field hiding vs. method overriding

**Prompt:**
If a subclass declares a field with the same name as a superclass field, does it override the field?

**Answer:**
No — it hides it. Both fields exist; which one is read depends entirely on the declared type of the reference used, not the object's actual class.

**Why it matters:**
The most commonly missed contrast with method overriding, which behaves the opposite way.

**Common trap:**
Expecting a superclass-typed reference to see the subclass's field value, the way it would see an overridden method's behavior.

**Related:**
[Internal Implementation](../handbook/java-core/polymorphism-and-dynamic-dispatch.md#internal-implementation)

## Card: The constructor-calls-overridable-method pitfall

**Prompt:**
What can go wrong if a superclass constructor calls a method the subclass overrides?

**Answer:**
The subclass's override runs before the subclass's own field initializers execute, so it can observe those fields still at their default (`null`/`0`/`false`) value.

**Why it matters:**
A real, recurring production bug pattern, not just trivia — measured directly in this chapter.

**Common trap:**
Not recognizing this as a design smell to actively avoid in code review.

**Related:**
[Production Scenarios](../handbook/java-core/polymorphism-and-dynamic-dispatch.md#production-scenarios)
