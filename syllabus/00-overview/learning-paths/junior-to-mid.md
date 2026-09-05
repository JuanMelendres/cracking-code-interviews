---
title: "Learning Path: Junior → Mid"
document_type: learning-path
status: draft
version: 1.0
last_updated: 2026-09-05
source: 00-project/syllabus-transformation-plan.md §6
---

# Learning Path: Junior → Mid

**Audience:** a new engineer (roughly 0–2 years) building working competence in Java backend fundamentals — someone who can write code that runs, but hasn't yet built a reliable mental model for *why* the standard library and the JVM behave the way they do.

**Goal:** reach L2 (Practitioner) on the topics below — correct usage, and the ability to choose between named alternatives with a stated reason — not L3 internals depth yet.

**Time budget:** ~5 weeks, part-time (5–8 hours/week).

**Stops at:** L2 for every topic listed. Each topic's own chapter continues to L3/L4 — stop reading once L2's criteria are met; L3/L4 is this path's own follow-on, [Mid → Senior](mid-to-senior.md).

This path is a hand-picked cross-domain subset, not a whole-domain sweep — it exists specifically to sequence *which* topics matter first, in dependency order, rather than working through any one domain's full topic list front to back.

## Sequence

| # | Topic | Domain | Stop at | Why here |
|---|---|---|---|---|
| 1 | [How a Computer Executes a Program](../../01-computer-science-foundations/how-a-computer-executes-a-program.md) | CS Foundations | L1 | The call stack and instruction-execution model everything else — the JVM included — sits on top of. |
| 2 | [Number Representation](../../01-computer-science-foundations/number-representation.md) | CS Foundations | L1 | Explains overflow and floating-point surprises before they show up as "weird bugs" in later topics. |
| 3 | [Algorithmic Complexity and Big-O, From First Principles](../../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) | CS Foundations | L2 | The vocabulary every collection and data-structure comparison below depends on. |
| 4 | [equals(), hashCode(), and Comparable Contracts](../../02-java/language-core/equals-hashcode-and-comparable-contracts.md) | Java — language-core | L2 | Prerequisite for HashMap actually working correctly, not just compiling. |
| 5 | [HashMap Internals](../../02-java/collections/hashmap-internals.md) | Java — collections | L2 | The single most-asked collection in interviews; L2 covers the equals/hashCode contract's practical consequences. |
| 6 | [ArrayList and LinkedList Internals](../../02-java/collections/arraylist-and-linkedlist-internals.md) | Java — collections | L2 | The canonical "which one and why" comparison question. |
| 7 | [Arrays, Two Pointers, and Sliding Window](../../03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md) | DSA | L2 | First real pattern-recognition practice, directly building on Topic 3's complexity vocabulary. |
| 8 | [Hashing Patterns and Frequency Maps](../../03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md) | DSA | L2 | Direct application of Topic 5's HashMap depth to a coding-interview pattern. |
| 9 | [Binary Search, Including Search-on-Answer](../../03-data-structures-algorithms/binary-search-and-search-on-answer.md) | DSA | L2 | A second foundational pattern with a very high real interview frequency. |
| 10 | [Linked Lists and In-Place Manipulation](../../03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md) | DSA | L2 | Pairs directly with Topic 6's LinkedList internals. |
| 11 | [Database Index Structures — B+Tree, Composite, Covering](../../06-databases/index-structures-btree-composite-covering.md) | Databases | L2 | The first "why is this query slow" vocabulary, needed before any real backend work. |
| 12 | [Data Modelling and Explicit Join Tables](../../06-databases/data-modelling-and-explicit-join-tables.md) | Databases | L2 | Correct relational modeling, a prerequisite for not fighting the ORM later. |
| 13 | [Test Strategy, the Pyramid, and Test Doubles](../../08-testing/test-strategy-and-test-doubles.md) | Testing | L2 | Testing habits are cheapest to build correctly from day one. |
| 14 | [Spring Framework vs. Spring Boot: Auto-Configuration and the Embedded Server](../../05-spring/spring-framework-vs-spring-boot.md) | Spring | L2 | The minimum Spring mental model needed before any of this path's graduate, [Mid → Senior](mid-to-senior.md), goes deeper. |

## Completion criteria

- Can explain each topic above cold, at its stated stop level, without notes (see each chapter's own Interview Questions section).
- Has solved the practice exercises for Topics 7–10 (see each DSA chapter's own `practice/` links).
- Can correctly choose between HashMap vs. TreeMap vs. LinkedHashMap, and ArrayList vs. LinkedList, for three stated scenarios each, with a reason — not just a definition.

## Next

[Mid → Senior](mid-to-senior.md) — the direct continuation, taking these same domains (plus several new ones) to L3.
