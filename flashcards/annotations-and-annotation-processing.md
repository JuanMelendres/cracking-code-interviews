---
title: "Flashcards: Annotations and Annotation Processing"
slug: annotations-and-annotation-processing
document_type: flashcard-deck
domain: java-core
topic_id: T-112
canonical: ../handbook/java-core/annotations-and-annotation-processing.md
last_updated: 2026-09-02
---

# Flashcards: Annotations and Annotation Processing

**Canonical chapter:** [`handbook/java-core/annotations-and-annotation-processing.md`](../handbook/java-core/annotations-and-annotation-processing.md)

## Card: The invisible default

**Prompt:**
What retention policy does an annotation get if you don't specify `@Retention` at all?

**Answer:**
`CLASS` — real bytecode, but invisible to reflection's `getAnnotations()`. Only `RUNTIME` is reflection-visible.

**Why it matters:**
A real, common, silent source of "why doesn't my framework code see this annotation" bugs.

**Common trap:**
Assuming the default behaves like `RUNTIME`.

**Related:**
[Internal Implementation](../handbook/java-core/annotations-and-annotation-processing.md#internal-implementation)

## Card: The real annotation-framework mechanism

**Prompt:**
What's the actual mechanism behind JPA's `@Column` or Jackson's `@JsonProperty`?

**Answer:**
Reflection scanning a class's members at runtime, reading each `RUNTIME`-retained annotation's values, and building real behavior from what's found — verified directly with a real, working mini-ORM.

**Why it matters:**
Demystifies "framework magic" into a concrete, buildable mechanism.

**Common trap:**
Treating annotation-driven frameworks as unexplainable magic rather than reflection plus metadata.

**Related:**
[Internal Implementation](../handbook/java-core/annotations-and-annotation-processing.md#internal-implementation)

## Card: @Inherited's real scope

**Prompt:**
Does `@Inherited` propagate an annotation from an interface to an implementing class?

**Answer:**
No — verified directly, real `false`, even when the interface's own annotation is marked `@Inherited`. It only works through class `extends`.

**Why it matters:**
A real, documented but frequently-missed limitation.

**Common trap:**
Assuming `@Inherited` works like general Java inheritance across any `extends`/`implements` relationship.

**Related:**
[Internal Implementation](../handbook/java-core/annotations-and-annotation-processing.md#internal-implementation)
