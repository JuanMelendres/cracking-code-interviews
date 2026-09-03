---
title: "Flashcards: Lambdas and Functional Interfaces"
slug: lambdas-and-functional-interfaces
document_type: flashcard-deck
domain: java-core
topic_id: T-108
canonical: ../handbook/java-core/lambdas-and-functional-interfaces.md
last_updated: 2026-09-02
---

# Flashcards: Lambdas and Functional Interfaces

**Canonical chapter:** [`syllabus/02-java/language-core/lambdas-and-functional-interfaces.md`](../syllabus/02-java/language-core/lambdas-and-functional-interfaces.md)

## Card: Why effectively final

**Prompt:**
Why must a local variable captured by a lambda be effectively final?

**Answer:**
The lambda captures the variable's value at creation time, not a live reference — allowing reassignment would let the captured copy silently diverge from the real variable.

**Why it matters:**
The reasoning, not just the rule, is what interviewers probe for.

**Common trap:**
Reciting it as an arbitrary Java rule without the value-capture explanation.

**Related:**
[Core Concepts](../syllabus/02-java/language-core/lambdas-and-functional-interfaces.md#core-concepts)

## Card: Lambda vs. anonymous class, on disk

**Prompt:**
Does compiling a lambda produce an extra `.class` file, like an anonymous class does?

**Answer:**
No — verified directly: the anonymous class produces a real, separate `$1.class`; the lambda produces nothing beyond the enclosing class itself.

**Why it matters:**
The concrete, measurable difference behind "lambdas are more lightweight."

**Common trap:**
Assuming lambdas are "just anonymous classes with shorter syntax" at the bytecode level.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/lambdas-and-functional-interfaces.md#internal-implementation)

## Card: What counts toward SAM

**Prompt:**
Do `default` and `static` interface methods count toward the single-abstract-method requirement?

**Answer:**
No — verified by a real compiling example with one abstract method plus both kinds of extras.

**Why it matters:**
A common source of unnecessary hesitation when designing functional interfaces.

**Common trap:**
Assuming any extra method on the interface breaks `@FunctionalInterface`.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/lambdas-and-functional-interfaces.md#internal-implementation)
