---
title: "Cheat Sheet: DTO, Entity, and Mapper Patterns"
slug: dto-entity-mapper-patterns
document_type: cheat-sheet
domain: 05-spring
topic_id: T-520
canonical: ../syllabus/05-spring/dto-entity-mapper-patterns.md
last_updated: 2026-09-16
---

# DTO, Entity, and Mapper Patterns

**Canonical chapter:** [`syllabus/05-spring/dto-entity-mapper-patterns.md`](../syllabus/05-spring/dto-entity-mapper-patterns.md)

## Core Mental Model

Entity, DTO, and Mapper answer three different questions about the same logical data: what the database
needs (Entity), what a boundary should expose or accept (DTO), and how to convert between the two
(Mapper). Keeping them separate lets a database schema evolve independently of an API's public contract.

## Essential Definitions

- **Entity** — the persistence-mapped, Hibernate-managed shape.
- **DTO (Data Transfer Object)** — a shape built specifically for crossing a boundary (request/response body).
- **Model** — an overloaded term; means the Entity, the DTO, or a separate domain object depending on the codebase.
- **Mapper** — code (hand-written or generated, e.g. MapStruct) converting between shapes.

## Decision Table

| Situation | Mechanism | Real evidence |
|---|---|---|
| Pure data crossing a boundary | DTO as a `record` | This chapter's `OrderResponse`/`CreateOrderRequest` |
| Hibernate-managed identity/lifecycle | Entity as a mutable class | Records can't provide a no-arg constructor + mutability JPA needs |
| More than a few entity/DTO pairs | Generated mapper (MapStruct) | Real captured `OrderMapperImpl.java` |
| Need to hide a sensitive internal field | DTO simply omits the field | Reflective proof: `OrderResponse` has no component for it |

## Common Pitfalls

- Returning a JPA entity directly from a controller — real risk: `LazyInitializationException` outside the persistence context.
- One shared DTO for both request and response, creating ambiguity about server-assigned fields.
- Assuming "Model" has one fixed meaning across codebases.
- Describing a mapper as "filtering out" a field rather than the DTO simply never declaring it.

## Interview Answer Skeleton

**30-sec:** Entity = persistence shape; DTO = boundary-crossing shape; Mapper = the conversion between them, kept separate so schema and API contract evolve independently.

**2-min:** Add: returning an entity directly risks a real `LazyInitializationException`; a mapper's real safety property is structural — it can't leak a field the DTO never declared, verified via reflection in this chapter's own lab.

**Staff-level framing:** DTO/Entity separation is a platform-wide contract-stability rule — conflating them means every schema migration becomes a breaking API change.

## Related

- syllabus/05-spring/bean-validation-and-global-exception-handling.md
- syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md
- syllabus/05-spring/spring-data-jpa-repository-abstraction.md
