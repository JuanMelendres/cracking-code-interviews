---
title: "Flashcards: DTO, Entity, and Mapper Patterns"
slug: dto-entity-mapper-patterns
document_type: flashcard-deck
domain: 05-spring
topic_id: T-520
canonical: ../syllabus/05-spring/dto-entity-mapper-patterns.md
last_updated: 2026-09-16
---

# Flashcards: DTO, Entity, and Mapper Patterns

**Canonical chapter:** [`syllabus/05-spring/dto-entity-mapper-patterns.md`](../syllabus/05-spring/dto-entity-mapper-patterns.md)

## Card: Why not return the JPA entity directly from a controller?

**Prompt:**
Why is returning a JPA entity directly from a `@RestController` method a real risk, not just "bad practice"?

**Answer:**
At least one specific mechanism: a lazily-loaded association can throw a real `LazyInitializationException`
once the entity leaves its persistence-context scope; the entity may carry internal-only fields with no
business reason to be exposed; and the API's public contract becomes accidentally coupled to the database
schema.

**Why it matters:**
Naming a specific mechanism, not just "best practice," is what separates a strong interview answer here.

**Common trap:**
Answering only "it's best practice" with no concrete failure mode.

**Related:**
[DTO, Entity, and Mapper Patterns](../syllabus/05-spring/dto-entity-mapper-patterns.md)

## Card: Why can't a JPA entity usually be a Java record?

**Prompt:**
Records fit DTOs well. Why do they usually NOT fit JPA entities?

**Answer:**
Hibernate needs a no-argument constructor and mutable fields to construct and populate a managed entity
instance via reflection, and needs identity based on the entity's own persistent identifier rather than a
record's generated all-fields `equals()`/`hashCode()` — a real, mechanical incompatibility.

**Why it matters:**
A candidate who tries to make every entity a record in a live-coding round hits this exact wall.

**Common trap:**
Treating records as a strict upgrade over classes everywhere in a Spring Boot codebase.

**Related:**
[DTO, Entity, and Mapper Patterns](../syllabus/05-spring/dto-entity-mapper-patterns.md)

## Card: Is a mapper "filtering out" a sensitive field, or something else?

**Prompt:**
A MapStruct-generated mapper never touches an entity's sensitive `internalFraudScoreNotes` field. Is it
filtering that field out?

**Answer:**
No — the DTO record simply never declared a component for it, verified via reflection
(`getRecordComponents()`). No code path can populate or serialize a field the target type structurally
doesn't have.

**Why it matters:**
This is the real mechanism behind "DTOs prevent sensitive data leaks" — a structural guarantee, not a
convention someone has to remember to enforce.

**Common trap:**
Describing this as active filtering rather than simple absence from the target type.

**Related:**
[DTO, Entity, and Mapper Patterns](../syllabus/05-spring/dto-entity-mapper-patterns.md)
