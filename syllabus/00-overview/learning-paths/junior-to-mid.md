---
title: "Learning Path: Junior → Mid"
document_type: learning-path
status: draft
version: 1.0
last_updated: 2026-09-08
source: 00-project/syllabus-transformation-plan.md §6
---

# Learning Path: Junior → Mid

**Audience:** a new engineer (roughly 0–2 years) building working competence in Java backend fundamentals — someone who can write code that runs, but hasn't yet built a reliable mental model for *why* the standard library and the JVM behave the way they do.

**Goal:** reach L2 (Practitioner) on the topics below — correct usage, and the ability to choose between named alternatives with a stated reason — not L3 internals depth yet.

**Time budget:** ~7 weeks, part-time (5–8 hours/week) — this path's third revision: originally ~5 weeks, extended to ~6–7 for the first five Junior Fundamentals chapters, and now includes two more (Java syntax itself, and basic List/Map/Set usage) found necessary when the user directly asked whether this repository actually served a true low-to-high-seniority reader yet.

**Stops at:** L2 for every topic listed. Each topic's own chapter continues to L3/L4 — stop reading once L2's criteria are met; L3/L4 is this path's own follow-on, [Mid → Senior](mid-to-senior.md).

This path is a hand-picked cross-domain subset, not a whole-domain sweep — it exists specifically to sequence *which* topics matter first, in dependency order, rather than working through any one domain's full topic list front to back.

**Updated 2026-09-07** to lead with the five Junior Fundamentals chapters (T-2201–T-2205), written after this path's original version and closing exactly the gap this path's own stated audience ("someone who can write code that runs, but hasn't yet built a reliable mental model") needs first — the original sequence jumped straight into `HashMap` internals, index structures, and `@Test` usage without ever teaching what a class, a table, or a unit test actually is.

**Updated again 2026-09-08** with two more chapters found the same way: even T-2201 (OOP Fundamentals) assumed the reader could already read an `if` statement and a `for` loop, and the path jumped from "here's a class" straight into `HashMap`/`ArrayList` internals with no "what is a List/Map/Set and when do you use each" stop first.

**Updated a fourth time 2026-09-08** to add Topic 22, Docker and Containers Fundamentals (T-2208) — written the same day as a Junior Fundamentals chapter but never actually added to this sequence until now, caught while building this path's own weekly study pack (`study-packs/junior-to-mid/`).

## Sequence

| # | Topic | Domain | Stop at | Why here |
|---|---|---|---|---|
| 1 | [Java Syntax Fundamentals: Variables, Control Flow, and Methods](../../02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) | Java — language-core | L1/L2 | The true starting point — assumes only prior exposure to *some* programming language, not Java specifically. Every other topic below is written in Java and assumes you can read an `if` statement and a `for` loop. |
| 2 | [Java OOP Fundamentals: Classes, Objects, and Interfaces](../../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) | Java — language-core | L1/L2 | Classes, objects, and interfaces — the next layer once Topic 1's syntax is solid. |
| 3 | [How a Computer Executes a Program](../../01-computer-science-foundations/how-a-computer-executes-a-program.md) | CS Foundations | L1 | The call stack and instruction-execution model everything else — the JVM included — sits on top of. |
| 4 | [Number Representation](../../01-computer-science-foundations/number-representation.md) | CS Foundations | L1 | Explains overflow and floating-point surprises before they show up as "weird bugs" in later topics. |
| 5 | [Algorithmic Complexity and Big-O, From First Principles](../../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) | CS Foundations | L2 | The vocabulary every collection and data-structure comparison below depends on. |
| 6 | [Collections Usage Fundamentals: List, Map, and Set](../../02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md) | Java — collections | L1/L2 | What a `List`/`Map`/`Set` is *for*, before the next two topics explain how `HashMap` and `ArrayList` work *inside*. |
| 7 | [equals(), hashCode(), and Comparable Contracts](../../02-java/language-core/equals-hashcode-and-comparable-contracts.md) | Java — language-core | L2 | Prerequisite for HashMap actually working correctly, not just compiling. |
| 8 | [HashMap Internals](../../02-java/collections/hashmap-internals.md) | Java — collections | L2 | The single most-asked collection in interviews; L2 covers the equals/hashCode contract's practical consequences. |
| 9 | [ArrayList and LinkedList Internals](../../02-java/collections/arraylist-and-linkedlist-internals.md) | Java — collections | L2 | The canonical "which one and why" comparison question. |
| 10 | [Arrays, Two Pointers, and Sliding Window](../../03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md) | DSA | L2 | First real pattern-recognition practice, directly building on Topic 5's complexity vocabulary. |
| 11 | [Hashing Patterns and Frequency Maps](../../03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md) | DSA | L2 | Direct application of Topic 8's HashMap depth to a coding-interview pattern. |
| 12 | [Binary Search, Including Search-on-Answer](../../03-data-structures-algorithms/binary-search-and-search-on-answer.md) | DSA | L2 | A second foundational pattern with a very high real interview frequency. |
| 13 | [Linked Lists and In-Place Manipulation](../../03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md) | DSA | L2 | Pairs directly with Topic 9's LinkedList internals. |
| 14 | [SQL and Relational Database Fundamentals](../../06-databases/sql-and-relational-database-fundamentals.md) | Databases | L1/L2 | Tables, primary/foreign keys, and JOINs — needed before Index Structures (next) assumes you already know what an index is speeding up. |
| 15 | [Database Index Structures — B+Tree, Composite, Covering](../../06-databases/index-structures-btree-composite-covering.md) | Databases | L2 | The first "why is this query slow" vocabulary, needed before any real backend work. |
| 16 | [Data Modelling and Explicit Join Tables](../../06-databases/data-modelling-and-explicit-join-tables.md) | Databases | L2 | Correct relational modeling, a prerequisite for not fighting the ORM later. |
| 17 | [Unit Testing Fundamentals with JUnit](../../08-testing/unit-testing-fundamentals-with-junit.md) | Testing | L1/L2 | `@Test`, `@BeforeEach`, and `assertThrows` — needed before Test Strategy (next) assumes you already know how to write one. |
| 18 | [Test Strategy, the Pyramid, and Test Doubles](../../08-testing/test-strategy-and-test-doubles.md) | Testing | L2 | Testing habits are cheapest to build correctly from day one. |
| 19 | [Spring MVC Fundamentals](../../05-spring/spring-mvc-fundamentals.md) | Spring | L1/L2 | `@Controller`, `@Service`, `@Repository`, and constructor injection — needed before Spring Framework vs. Spring Boot (next) assumes you already know what a bean is. |
| 20 | [Spring Framework vs. Spring Boot: Auto-Configuration and the Embedded Server](../../05-spring/spring-framework-vs-spring-boot.md) | Spring | L2 | The minimum Spring mental model needed before any of this path's graduate, [Mid → Senior](mid-to-senior.md), goes deeper. |
| 21 | [REST API Fundamentals](../../07-api-design/rest-api-fundamentals.md) | API Design | L1/L2 | Resource naming, HTTP verbs, status codes, and idempotency — the API-design vocabulary every controller in Topic 19 was already speaking without naming it. |
| 22 | [Docker and Containers Fundamentals](../../14-devops-containers/docker-and-containers-fundamentals.md) | DevOps & Containers | L1/L2 | How the API built in Topic 21 actually ships and runs — a natural closing topic once there's a real application worth containerizing. |

## Completion criteria

- Can explain each topic above cold, at its stated stop level, without notes (see each chapter's own Interview Questions section).
- Has solved the practice exercises for Topics 10–13 (see each DSA chapter's own `practice/` links), and reproduced the real executed evidence in Topics 1, 2, 6, 14, 17, 19, and 22's own `practice/` demos (compiled Java syntax and collections demos, a live PostgreSQL lab, a live JUnit run, a live Spring Boot app, a real built-and-run Docker image).
- Can correctly choose between HashMap vs. TreeMap vs. LinkedHashMap, and ArrayList vs. LinkedList, for three stated scenarios each, with a reason — not just a definition.

## Weekly study pack

[`study-packs/junior-to-mid/`](../../../study-packs/junior-to-mid/) turns this sequence into a scheduled, week-by-week program — reading assignments, hands-on exercises, coding drills, and a lightweight mock-interview checkpoint per week — the same operational layer `study-packs/week-01` through `week-25` provide for the original Senior-focused program. This path's own 22 topics are the *content*; the study pack is the *schedule*.

## Next

[Mid → Senior](mid-to-senior.md) — the direct continuation, taking these same domains (plus several new ones) to L3.
