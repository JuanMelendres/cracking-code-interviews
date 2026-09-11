---
title: "Flashcards: Spring Data JPA Repository Abstraction"
slug: spring-data-jpa-repository-abstraction
document_type: flashcard-deck
domain: 05-spring
topic_id: T-510
canonical: ../syllabus/05-spring/spring-data-jpa-repository-abstraction.md
last_updated: 2026-09-11
---

# Flashcards: Spring Data JPA Repository Abstraction

**Canonical chapter:** [`syllabus/05-spring/spring-data-jpa-repository-abstraction.md`](../syllabus/05-spring/spring-data-jpa-repository-abstraction.md)

## Card: When does a derived method's typo actually fail?

**Prompt:**
`findByTotlAmountGreaterThan` compiles without error. When does this actually break, and why not sooner?

**Answer:**
At `ApplicationContext` startup, when Spring Data creates the repository proxy and validates the parsed property path against the entity's real JPA metamodel — not at compile time (the property name is just a string inside a method name) and not on first invocation.

**Why it matters:**
A real, demonstrated example of a whole class of bugs caught before the application ever serves a request.

**Common trap:**
Assuming the compiler catches this, or assuming it fails silently/lazily on first call.

**Related:**
[Spring Data JPA Repository Abstraction](../syllabus/05-spring/spring-data-jpa-repository-abstraction.md)

## Card: Derived method vs. Specification

**Prompt:**
Why can't a derived query method or a static `@Query` handle "any subset of five independent optional filters"?

**Answer:**
Both are fixed at compile time — a method name or query string can't decide, per call, which conditions to include. `Specification` composes predicates at runtime with `.and()`/`.or()`, including only the ones actually present for a given call.

**Why it matters:**
A real, common production shape (search/filter screens).

**Common trap:**
Writing one overloaded derived method per observed filter combination instead of switching to `Specification`.

**Related:**
[Spring Data JPA Repository Abstraction](../syllabus/05-spring/spring-data-jpa-repository-abstraction.md)

## Card: The real cost of Pageable

**Prompt:**
What does returning `Page<Order>` instead of `List<Order>` really cost?

**Answer:**
A real, separate `COUNT` query in addition to the content query — two SQL statements executed per call, not one, verified directly in this chapter's Hibernate SQL log.

**Why it matters:**
A genuine trade-off worth knowing before defaulting every list endpoint to `Pageable`.

**Common trap:**
Assuming `Page<T>` is a free wrapper around the same single query a `List<T>` return would use.

**Related:**
[Spring Data JPA Repository Abstraction](../syllabus/05-spring/spring-data-jpa-repository-abstraction.md)
