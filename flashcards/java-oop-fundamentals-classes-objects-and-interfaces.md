---
title: "Flashcards: Java OOP Fundamentals: Classes, Objects, and Interfaces"
slug: java-oop-fundamentals-classes-objects-and-interfaces
document_type: flashcard-deck
domain: 02-java
topic_id: T-2201
canonical: ../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md
last_updated: 2026-09-07
---

# Flashcards: Java OOP Fundamentals: Classes, Objects, and Interfaces

**Canonical chapter:** [`syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md`](../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md)

## Card: Class vs. object

**Prompt:**
What's the actual difference between a class and an object?

**Answer:**
A class is a blueprint, written once. An object is a specific instance built from it via `new`, with its own independent copy of the class's fields.

**Why it matters:**
The most basic distinction interviewers check a candidate can articulate concretely, not just define.

**Common trap:**
Describing them as synonyms, or unable to give a concrete example.

**Related:**
[Java OOP Fundamentals: Classes, Objects, and Interfaces](../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md)

## Card: Why Java allows single class inheritance but multiple interfaces

**Prompt:**
Why can a Java class extend only one class but implement many interfaces?

**Answer:**
Multiple inheritance of real field/method state creates an unresolvable ambiguity (which parent's `x` does a class inherit if both declare one) that the JVM has no consistent rule for. Interfaces avoid this because, until default methods, they contributed no state; the narrower default-method collision case is resolved by forcing the implementing class to override it itself.

**Why it matters:**
A genuinely common "why does Java work this way" interview question with a real, non-arbitrary answer.

**Common trap:**
Answering "just because" or "that's the rule" without the actual diamond-ambiguity reasoning.

**Related:**
[Java OOP Fundamentals: Classes, Objects, and Interfaces](../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md)

## Card: Interface vs. abstract class decision rule

**Prompt:**
When do you reach for an abstract class instead of an interface?

**Answer:**
When there's real, shared implementation (fields, a constructor, working method bodies) to hand down within a genuinely related family of types. Reach for an interface when you only need to promise a capability, especially across otherwise-unrelated classes.

**Why it matters:**
The single most common "which tool do I use" confusion at this level, with a precise, non-taste-based answer.

**Common trap:**
Treating the choice as arbitrary style preference rather than based on whether real shared implementation exists.

**Related:**
[Java OOP Fundamentals: Classes, Objects, and Interfaces](../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md)

## Card: Composition avoiding subclass explosion

**Prompt:**
Why does composition avoid the "subclass explosion" problem inheritance can produce?

**Answer:**
Inheritance can only vary along one hierarchy; a second independent axis of variation forces one subclass per combination (2 axes x 2 options = 4 subclasses). Composition assembles independent small classes/interfaces at construction time, so a new axis adds one class, not a multiplied set of combinations.

**Why it matters:**
The concrete mechanism behind "favor composition over inheritance," not just the slogan.

**Common trap:**
Saying "composition is more flexible" without being able to explain why, concretely.

**Related:**
[Java OOP Fundamentals: Classes, Objects, and Interfaces](../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md)

## Card: Reference assignment vs. object copy

**Prompt:**
After `Counter b = a;` and then `b.increment()`, does `a`'s state change too?

**Answer:**
Yes — assignment copies the *reference*, not the object. `a` and `b` point at the exact same object in memory, so a mutation through either is visible through both.

**Why it matters:**
The single most common source of confusion at this level — candidates expecting object-copy semantics like a primitive `int` assignment.

**Common trap:**
Assuming `b = a` creates an independent copy of the object.

**Related:**
[Java OOP Fundamentals: Classes, Objects, and Interfaces](../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md)
