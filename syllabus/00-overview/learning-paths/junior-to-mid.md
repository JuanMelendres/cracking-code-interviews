---
title: "Learning Path: Junior → Mid"
document_type: learning-path
status: draft
version: 1.0
last_updated: 2026-09-07
source: 00-project/syllabus-transformation-plan.md §6
---

# Learning Path: Junior → Mid

**Audience:** a new engineer (roughly 0–2 years) building working competence in Java backend fundamentals — someone who can write code that runs, but hasn't yet built a reliable mental model for *why* the standard library and the JVM behave the way they do.

**Goal:** reach L2 (Practitioner) on the topics below — correct usage, and the ability to choose between named alternatives with a stated reason — not L3 internals depth yet.

**Time budget:** ~6–7 weeks, part-time (5–8 hours/week) — longer than this path's original ~5-week estimate, since it now includes the 5 true-fundamentals chapters a genuinely new engineer needs first; each fundamentals chapter is lighter than an internals-depth one, so the increase is smaller than 5 full extra weeks.

**Stops at:** L2 for every topic listed. Each topic's own chapter continues to L3/L4 — stop reading once L2's criteria are met; L3/L4 is this path's own follow-on, [Mid → Senior](mid-to-senior.md).

This path is a hand-picked cross-domain subset, not a whole-domain sweep — it exists specifically to sequence *which* topics matter first, in dependency order, rather than working through any one domain's full topic list front to back.

**Updated 2026-09-07** to lead with the five Junior Fundamentals chapters (T-2201–T-2205), written after this path's original version and closing exactly the gap this path's own stated audience ("someone who can write code that runs, but hasn't yet built a reliable mental model") needs first — the original sequence jumped straight into `HashMap` internals, index structures, and `@Test` usage without ever teaching what a class, a table, or a unit test actually is.

## Sequence

| # | Topic | Domain | Stop at | Why here |
|---|---|---|---|---|
| 1 | [Java OOP Fundamentals: Classes, Objects, and Interfaces](../../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) | Java — language-core | L1/L2 | The true starting point — no prior Java assumed. Every other topic below is written in Java and assumes you can read a class. |
| 2 | [How a Computer Executes a Program](../../01-computer-science-foundations/how-a-computer-executes-a-program.md) | CS Foundations | L1 | The call stack and instruction-execution model everything else — the JVM included — sits on top of. |
| 3 | [Number Representation](../../01-computer-science-foundations/number-representation.md) | CS Foundations | L1 | Explains overflow and floating-point surprises before they show up as "weird bugs" in later topics. |
| 4 | [Algorithmic Complexity and Big-O, From First Principles](../../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) | CS Foundations | L2 | The vocabulary every collection and data-structure comparison below depends on. |
| 5 | [equals(), hashCode(), and Comparable Contracts](../../02-java/language-core/equals-hashcode-and-comparable-contracts.md) | Java — language-core | L2 | Prerequisite for HashMap actually working correctly, not just compiling. |
| 6 | [HashMap Internals](../../02-java/collections/hashmap-internals.md) | Java — collections | L2 | The single most-asked collection in interviews; L2 covers the equals/hashCode contract's practical consequences. |
| 7 | [ArrayList and LinkedList Internals](../../02-java/collections/arraylist-and-linkedlist-internals.md) | Java — collections | L2 | The canonical "which one and why" comparison question. |
| 8 | [Arrays, Two Pointers, and Sliding Window](../../03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md) | DSA | L2 | First real pattern-recognition practice, directly building on Topic 4's complexity vocabulary. |
| 9 | [Hashing Patterns and Frequency Maps](../../03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md) | DSA | L2 | Direct application of Topic 6's HashMap depth to a coding-interview pattern. |
| 10 | [Binary Search, Including Search-on-Answer](../../03-data-structures-algorithms/binary-search-and-search-on-answer.md) | DSA | L2 | A second foundational pattern with a very high real interview frequency. |
| 11 | [Linked Lists and In-Place Manipulation](../../03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md) | DSA | L2 | Pairs directly with Topic 7's LinkedList internals. |
| 12 | [SQL and Relational Database Fundamentals](../../06-databases/sql-and-relational-database-fundamentals.md) | Databases | L1/L2 | Tables, primary/foreign keys, and JOINs — needed before Index Structures (next) assumes you already know what an index is speeding up. |
| 13 | [Database Index Structures — B+Tree, Composite, Covering](../../06-databases/index-structures-btree-composite-covering.md) | Databases | L2 | The first "why is this query slow" vocabulary, needed before any real backend work. |
| 14 | [Data Modelling and Explicit Join Tables](../../06-databases/data-modelling-and-explicit-join-tables.md) | Databases | L2 | Correct relational modeling, a prerequisite for not fighting the ORM later. |
| 15 | [Unit Testing Fundamentals with JUnit](../../08-testing/unit-testing-fundamentals-with-junit.md) | Testing | L1/L2 | `@Test`, `@BeforeEach`, and `assertThrows` — needed before Test Strategy (next) assumes you already know how to write one. |
| 16 | [Test Strategy, the Pyramid, and Test Doubles](../../08-testing/test-strategy-and-test-doubles.md) | Testing | L2 | Testing habits are cheapest to build correctly from day one. |
| 17 | [Spring MVC Fundamentals](../../05-spring/spring-mvc-fundamentals.md) | Spring | L1/L2 | `@Controller`, `@Service`, `@Repository`, and constructor injection — needed before Spring Framework vs. Spring Boot (next) assumes you already know what a bean is. |
| 18 | [Spring Framework vs. Spring Boot: Auto-Configuration and the Embedded Server](../../05-spring/spring-framework-vs-spring-boot.md) | Spring | L2 | The minimum Spring mental model needed before any of this path's graduate, [Mid → Senior](mid-to-senior.md), goes deeper. |
| 19 | [REST API Fundamentals](../../07-api-design/rest-api-fundamentals.md) | API Design | L1/L2 | Resource naming, HTTP verbs, status codes, and idempotency — the API-design vocabulary every controller in Topic 17 was already speaking without naming it. |

## Completion criteria

- Can explain each topic above cold, at its stated stop level, without notes (see each chapter's own Interview Questions section).
- Has solved the practice exercises for Topics 8–11 (see each DSA chapter's own `practice/` links), and reproduced the real executed evidence in Topics 1, 12, 15, and 17's own `practice/` demos (compiled Java, a live PostgreSQL lab, a live JUnit run, a live Spring Boot app).
- Can correctly choose between HashMap vs. TreeMap vs. LinkedHashMap, and ArrayList vs. LinkedList, for three stated scenarios each, with a reason — not just a definition.

## Next

[Mid → Senior](mid-to-senior.md) — the direct continuation, taking these same domains (plus several new ones) to L3.
