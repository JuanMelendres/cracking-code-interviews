---
title: "Cheat Sheet: Spring Data JPA Repository Abstraction"
slug: spring-data-jpa-repository-abstraction
document_type: cheat-sheet
domain: 05-spring
topic_id: T-510
canonical: ../syllabus/05-spring/spring-data-jpa-repository-abstraction.md
last_updated: 2026-09-11
---

# Spring Data JPA Repository Abstraction

**Canonical chapter:** [`syllabus/05-spring/spring-data-jpa-repository-abstraction.md`](../syllabus/05-spring/spring-data-jpa-repository-abstraction.md)

## Core Mental Model

A repository interface is a form you fill in, not a program you write — you describe *what* you want (a method name, JPQL, native SQL, or a composable predicate), and Spring Data generates the *how*. The method-name vocabulary is deliberately the most restrictive of the four, precisely because that restriction lets Spring Data validate it against the entity's real structure before the application ever starts.

## Essential Definitions

- **Derived query method** — a method name Spring Data parses into a property tree and validates against the entity's JPA metamodel at startup.
- **`@Query` (JPQL)** — hand-written, operates on the entity model (joins, associations).
- **`@Query(nativeQuery = true)`** — raw SQL, untranslated, for database-specific syntax.
- **`Specification`** — a predicate composed at runtime; the only mechanism that handles a variable number of optional filters.

## Decision Table

| Situation | Mechanism | Real evidence |
|---|---|---|
| Fixed, small set of conditions | Derived query method | Real generated SQL from method name alone |
| Join/aggregate over entities | `@Query` (JPQL) | Real `DISTINCT o FROM Order o JOIN o.items i` |
| DB-specific SQL or narrow projection | `@Query(nativeQuery = true)` | Real native `GROUP BY` + `StatusSummary` projection |
| Unknown/varying optional filters | `Specification` | Real 0/1/2-filter counts composed at runtime |
| Need total-count metadata | `Pageable`/`Page<T>` | Real, captured second `COUNT` query |

## Common Pitfalls

- Assuming a derived-method typo would be caught by the compiler — it fails at `ApplicationContext` startup instead, with a real `PropertyReferenceException`.
- Growing a repository interface to a dozen overloaded derived methods to cover every observed filter combination, instead of switching to `Specification`.
- Not knowing `Pageable` executes two real queries (content + `COUNT`), not one.

## Interview Answer Skeleton

**30-sec:** Four mechanisms — derived method names (validated against the entity metamodel at startup), `@Query` JPQL/native, and `Specification` for a variable number of optional filters.

**2-min:** Add: a derived method's typo produces a real `PropertyReferenceException` at context-refresh time, not on first call — Spring's own message even suggests the fix. `Pageable` adds a real, separate `COUNT` query.

**Staff-level framing:** A repository interface whose method count tracks the number of *observed filter combinations* rather than the number of distinct *concepts* is the specific, nameable smell that signals a missed `Specification` migration.

## Related

- syllabus/05-spring/bean-validation-and-global-exception-handling.md
- syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md
