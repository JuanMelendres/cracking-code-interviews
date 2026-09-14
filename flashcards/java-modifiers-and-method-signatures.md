---
title: "Flashcards: Java Modifiers and Method Signatures"
slug: java-modifiers-and-method-signatures
document_type: flashcard-deck
domain: 02-java
topic_id: T-2210
canonical: ../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md
last_updated: 2026-09-14
---

# Flashcards: Java Modifiers and Method Signatures

**Canonical chapter:** [`syllabus/02-java/language-core/java-modifiers-and-method-signatures.md`](../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md)

## Card: The four access levels

**Prompt:**
Name Java's four access levels, from most to least restrictive.

**Answer:**
`private`, package-private (no modifier), `protected`, `public`.

**Why it matters:**
Package-private is the real default when no modifier is written, and it's easy to forget it's a distinct level.

**Common trap:**
Treating "no modifier" as equivalent to `public` or `private` instead of its own real level.

**Related:**
[Java Modifiers and Method Signatures](../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md)

## Card: private is scoped to the top-level class

**Prompt:**
Can two nested classes inside the same outer class access each other's `private` members?

**Answer:**
Yes — `private` access in Java is scoped to the *top-level enclosing class*, not the immediate class body. This is a real, sometimes surprising JLS rule, not a bug.

**Why it matters:**
Contradicts the simplified "private means only this exact class" mental model most people carry.

**Common trap:**
Assuming private access never crosses a class boundary, even within the same outer class.

**Related:**
[Java Modifiers and Method Signatures](../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md)

## Card: The three effects of final

**Prompt:**
What does `final` mean when applied to a variable, a method, and a class?

**Answer:**
Variable: can be assigned exactly once, never reassigned. Method: cannot be overridden by a subclass. Class: cannot be extended (subclassed) at all.

**Why it matters:**
Three genuinely different effects sharing one keyword — a common source of imprecise answers.

**Common trap:**
Conflating `final` class (forbids subclassing) with `abstract` class (requires it) — they're opposites.

**Related:**
[Java Modifiers and Method Signatures](../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md)

## Card: Abstract method vs. concrete method

**Prompt:**
What's the difference between an abstract method and a concrete method, and can one class have both?

**Answer:**
An abstract method has a signature but no body (ends in `;`), forcing subclasses to implement it. A concrete method has a real body, directly callable and inheritable. Yes — an abstract class routinely has both.

**Why it matters:**
Basic but precise vocabulary every OOP design question assumes.

**Common trap:**
Believing an abstract class can never have a constructor — it can, called via `super()` from a subclass.

**Related:**
[Java Modifiers and Method Signatures](../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md)

## Card: Overloading vs. overriding — who resolves it, and when

**Prompt:**
Is overload resolution decided at compile time or runtime? What about override resolution?

**Answer:**
Overload resolution is decided by the compiler at compile time, from the declared types of the arguments at the call site. Override resolution is decided by the JVM at runtime, from the actual class of the object the call is made on.

**Why it matters:**
This is the exact distinction that makes polymorphism work for overriding but not for overloading — a call to an overloaded method never "looks at" the runtime type the way an overridden one does.

**Common trap:**
Assuming an overload is chosen based on the runtime type of an argument, the same way an override is chosen based on the runtime type of the receiver — it isn't; the declared (compile-time) type picks the overload.

**Related:**
[Java Modifiers and Method Signatures](../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md), [Polymorphism and Dynamic Dispatch](../syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md)

## Card: static vs. instance state

**Prompt:**
If a class has a `static int totalCreated` field incremented in every constructor call, and 3 instances are created, what does `totalCreated` equal for each instance?

**Answer:**
`3` for all three instances — `static` fields are shared across every instance of the class, not per-instance. Each instance's own (non-static) fields, by contrast, are genuinely separate.

**Why it matters:**
A real, measured demonstration of the single most basic static/instance distinction.

**Common trap:**
Declaring a field `static` by accident and being surprised it's shared across instances.

**Related:**
[Java Modifiers and Method Signatures](../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md)
