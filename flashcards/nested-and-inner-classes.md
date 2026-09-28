---
title: "Flashcards: Nested and Inner Classes"
slug: nested-and-inner-classes
document_type: flashcard-deck
domain: 02-java/language-core
topic_id: T-2431
canonical: ../syllabus/02-java/language-core/nested-and-inner-classes.md
last_updated: 2026-09-28
---

# Flashcards: Nested and Inner Classes

**Canonical chapter:** [`syllabus/02-java/language-core/nested-and-inner-classes.md`](../syllabus/02-java/language-core/nested-and-inner-classes.md)

## Card: The one structural difference

**Prompt:**
What is the single structural difference between a static nested class and an inner class, and what field implements it?

**Answer:**
An inner-class instance is bound to an enclosing instance; a static nested one is not. The compiler implements this with a `final synthetic` field named `this$0`, added as the first constructor parameter. Reflection prints it as `NestedClassesDemo this$0  synthetic=true`, and the demo confirms it points at the exact outer instance.

**Why it matters:**
Every other difference — unqualified outer access, memory retention, serialization behavior — follows from this field.

**Common trap:**
Reading `static` on a nested class as "shared state." On a nested class it means only "no enclosing instance."

**Related:**
[Nested and Inner Classes](../syllabus/02-java/language-core/nested-and-inner-classes.md)

## Card: Why serialization fails with the wrong class name

**Prompt:**
A `Serializable` class declares only `String` and `int` fields, yet serializing it throws `NotSerializableException` naming a completely different class. Why?

**Answer:**
It is a non-static nested class, so its state includes the synthetic `this$0` reference to its enclosing instance, and the enclosing class is not `Serializable`. Real captured output: `NotSerializableException: NestedClassesDemo` while serializing an `UnsafeRecordHolder`. The fix is adding `static`.

**Why it matters:**
The exception names a class the failing source never mentions, which is what makes it slow to debug.

**Common trap:**
Marking declared fields `transient` — the problematic reference is not declared in source, so there is nothing to mark.

**Related:**
[Serialization Hazards and Alternatives](../syllabus/02-java/language-core/serialization-hazards-and-alternatives.md)

## Card: When the outer reference is actually retained

**Prompt:**
Does an inner class always keep its enclosing instance alive on JDK 21?

**Answer:**
No. Measured with three 8 MB services: the inner class whose body **reads** an enclosing field emitted `this$0` and was **not** collected; the inner class that never touched the outer instance had **no** `this$0` field emitted at all (confirmed by `javap`) and **was** collected; the static nested version likewise. Design as if the reference existed, but the retention is conditional on use.

**Why it matters:**
The blanket claim "an inner class always retains its outer object" is no longer accurate as stated, and knowing the real rule is a Senior-level signal.

**Common trap:**
Testing this with a `private final String name = "literal"` — that is a compile-time constant, javac folds it in, the outer access disappears, and the leak does not reproduce.

**Related:**
[GC Roots, Reachability, and Reference Strength](../syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md)

## Card: Outer.this

**Prompt:**
An inner class declares a field with the same name as one in its enclosing class. How do you read the enclosing one?

**Answer:**
`Outer.this.field`. Real output: unqualified `label` gives `INNER-label`, while `NestedClassesDemo.this.label` gives `OUTER-label`.

**Why it matters:**
It is the standard follow-up after "what is `this$0`," and it appears in real code whenever a nested class mirrors an outer field name.

**Common trap:**
Writing `super.label`, which reaches the superclass, not the enclosing instance — two unrelated relationships.

**Related:**
[Nested and Inner Classes](../syllabus/02-java/language-core/nested-and-inner-classes.md)

## Card: Local versus anonymous class names

**Prompt:**
What runtime class names does javac produce for a local class and for an anonymous class, and why do they differ?

**Answer:**
`Outer$1Multiplier` for a local class and `Outer$1` for an anonymous one — both real, printed output. Local classes get a numeric prefix before the name because two different methods may declare local classes with the same name; anonymous classes have no name, so they get a bare index. Neither appears in `getDeclaredClasses()`, because neither is a member.

**Why it matters:**
Framework scanners that enumerate nested types silently skip both, and these names are what you will see in production stack traces.

**Common trap:**
Assuming "anonymous" implies "synthetic" — the demo prints `isSynthetic() == false` for an anonymous class.

**Related:**
[Lambdas and Functional Interfaces](../syllabus/02-java/language-core/lambdas-and-functional-interfaces.md)
