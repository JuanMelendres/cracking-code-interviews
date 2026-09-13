---
title: "Admin Search Screen's Repository Methods Becoming Unmaintainable"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/05-spring/spring-data-jpa-repository-abstraction.md
source: syllabus/05-spring/spring-data-jpa-repository-abstraction.md#production-scenarios
---

# Admin Search Screen's Repository Methods Becoming Unmaintainable

## Context

An internal admin tool's order-search endpoint needs to filter by any combination of status, customer, minimum total, date range, and SKU — every field optional, any subset present on a given request.

## Symptoms

The repository interface has grown to a dozen overloaded `findBy...` derived methods, one per combination of filters actually requested by the frontend team so far, and the controller has a large `if`/`else` chain choosing which one to call.

## Impact

Every new filter combination the frontend team asks for requires a new repository method and a new controller branch; a genuinely new combination means the request either silently ignores a filter or the team ships a new method under time pressure.

## Initial Hypotheses

- A missing query-builder library — ruled out, the actual gap is architectural, not a missing dependency.
- A flawed entity design — ruled out, the entity model is fine; the problem is purely how filtering is expressed.
- Derived query methods being fundamentally unable to express "apply this predicate only if supplied" — correct.

## Evidence

Counting the repository interface's methods against the number of distinct filter combinations the frontend has requested over the tool's lifetime shows a 1:1 correspondence — strong evidence the team has been solving a combinatorial problem with an enumeration strategy.

## Investigation Timeline

1. Repository interface observed to have grown to a dozen near-duplicate derived methods.
2. Missing-library and entity-design hypotheses ruled out.
3. Method count cross-checked against historical filter-combination requests, confirming a 1:1 enumeration pattern.

## Root Cause

Derived query methods and static JPQL are both fixed at compile time; they cannot express "apply this predicate only if it was supplied," which is exactly what an N-optional-filter screen needs.

## Immediate Mitigation

None needed operationally — this is a maintainability problem, not an incident, but treated with the same priority the next time a new filter combination is requested under a deadline.

## Permanent Fix

Replace the overloaded derived methods with a single method taking a `Specification<Order>` (or a small filter-criteria object translated into one), built by combining `OrderSpecifications.hasStatus(...)`, `.minTotal(...)`, and similar building blocks with `.and()` only for the filters actually present in the request.

## Alternatives Considered

A single derived method with every field nullable, checked with `IsNull`-style OR-branches in the method name — rejected as producing extremely long, unreadable method names and still not covering every combination. Building JPQL strings by hand with string concatenation — rejected as a real SQL/JPQL-injection risk and a maintenance burden equivalent to the original problem.

## Trade-offs

`Specification`-based queries are less immediately readable at the call site than a descriptively-named derived method — the trade is combinatorial coverage and maintainability against per-query readability, worth it once the number of optional filters exceeds two or three.

## Prevention

Recognize "an unknown number of optional filters" as the specific signal to reach for `Specification` from the start, rather than after the derived-method interface has already grown unmanageable.

## Monitoring and Alerts

- A repository-interface method-count trend tracked over time as a lightweight maintainability signal, flagging rapid growth in near-duplicate derived methods.
- A code-review checklist item for any new derived-method repository addition that closely resembles an existing one, prompting a `Specification`-based refactor discussion before the count grows further.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent refactor.

- **Situation:** an admin search screen's repository interface grew to a dozen near-duplicate filter methods.
- **Task:** find a maintainable way to express an arbitrary combination of optional filters.
- **Action:** replaced the overloaded derived methods with a single `Specification`-based query, combining only the predicates actually present in each request.
- **Result:** collapsed a dozen methods into one, with every future filter combination requiring no new repository code.

## Staff-Level Discussion

This is Interview Question 2 in the canonical chapter's own Interview Questions section — "when would a derived query method or `@Query` genuinely not be enough" — arriving as a real, gradually-worsening maintainability shape rather than an abstract rule. The organizational lesson is recognizing the combinatorial-enumeration pattern early: any repository accumulating near-duplicate derived methods for what is really one underlying "N optional filters" problem is a `Specification`-shaped refactor waiting to happen, and catching it at three or four methods is far cheaper than catching it at a dozen.

## Related Handbook Chapters

- [Spring Data JPA Repository Abstraction](../syllabus/05-spring/spring-data-jpa-repository-abstraction.md) — the canonical `Specification` composition pattern behind this incident's fix.
