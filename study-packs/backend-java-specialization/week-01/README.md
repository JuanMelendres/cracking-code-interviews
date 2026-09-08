---
title: "Backend Java Specialization, Week 1 — Java Language Core"
document_type: study-pack
week: 1
track: backend-java-specialization
status: draft
estimated_hours: 13
---

# Week 1 — Java Language Core

## Weekly Outcome

By the end of this week you can explain, with a real code example for each, every core language-level mechanic Java interviews probe below the surface syntax: `equals`/`hashCode`/`Comparable` contracts, dynamic dispatch, immutability and defensive copying, generics erasure and PECS, exception hierarchy design, string interning, streams/lambdas, `Optional`, records/sealed types/pattern matching, enums, annotations, reflection, class loading, and serialization hazards — the full `language-core` subdomain this path schedules first.

## Why This Week Matters

[`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../../syllabus/00-overview/learning-paths/backend-java-specialization.md) sequences Java's own subdomains as `language-core` → `collections` → `concurrency` → `jvm-internals` specifically because that order is itself a dependency chain: collections depend on `equals`/`hashCode` contracts taught this week, and concurrency's visibility guarantees build on language-level immutability concepts also introduced here. This is the largest single week in the pack (17 chapters) because `language-core` is the foundation every other domain in this path — Spring, databases, messaging — assumes is already solid.

## Prerequisites

None from outside this path if you are starting fresh at Staff-track depth. If you have not yet built basic Java literacy (variables, control flow, classes) at all, work through [Junior → Mid](../../junior-to-mid/README.md) Week 1 first — this week's L3/L4 sections assume that floor is already in place, even though its L1/L2 sections re-teach the same ground.

## Schedule

| Day | Focus |
|---|---|
| Mon | Java Syntax and OOP Fundamentals (T-2206, T-2201) — a fast review if already comfortable, a real study session if not |
| Tue | Equality, polymorphism, immutability (T-101, T-102, T-103) |
| Wed | Generics, exceptions, strings (T-104, T-105, T-106) |
| Thu | Streams, lambdas, `Optional` (T-107, T-108, T-109) |
| Fri | Records/sealed/pattern matching, enums (T-110, T-111) |
| Sat | Annotations, reflection, class loading, serialization (T-112, T-113, T-114, T-115) |
| Sun+ | Hands-on exercises and review checklist below; carry into a half-week 10 if 6 days was not enough for 17 chapters |

## Required Reading

The full `language-core` subdomain, in the order [`syllabus/02-java/INDEX.md`](../../../syllabus/02-java/INDEX.md) lists it — that index is the exhaustive, canonical source; this table is this week's reading list, not a duplicate of it.

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | T-2206 — Java Syntax Fundamentals: Variables, Control Flow, and Methods | [`java-syntax-fundamentals-variables-control-flow-and-methods.md`](../../../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) |
| 2 | T-2201 — Java OOP Fundamentals: Classes, Objects, and Interfaces | [`java-oop-fundamentals-classes-objects-and-interfaces.md`](../../../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) |
| 3 | T-101 — equals(), hashCode(), and Comparable Contracts | [`equals-hashcode-and-comparable-contracts.md`](../../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md) |
| 4 | T-102 — Polymorphism and Dynamic Dispatch Mechanics | [`polymorphism-and-dynamic-dispatch.md`](../../../syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md) |
| 5 | T-103 — Immutability and Defensive Copying | [`immutability-and-defensive-copying.md`](../../../syllabus/02-java/language-core/immutability-and-defensive-copying.md) |
| 6 | T-104 — Generics: Erasure, Variance, and PECS | [`generics-erasure-and-pecs.md`](../../../syllabus/02-java/language-core/generics-erasure-and-pecs.md) |
| 7 | T-105 — Exception Design and Hierarchy Strategy | [`exception-design-and-hierarchy-strategy.md`](../../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md) |
| 8 | T-106 — Strings: Interning, Compact Strings, and Builders | [`strings-interning-compact-strings-and-builders.md`](../../../syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md) |
| 9 | T-107 — Streams and Collectors | [`streams-and-collectors.md`](../../../syllabus/02-java/language-core/streams-and-collectors.md) |
| 10 | T-108 — Lambdas and Functional Interfaces | [`lambdas-and-functional-interfaces.md`](../../../syllabus/02-java/language-core/lambdas-and-functional-interfaces.md) |
| 11 | T-109 — Optional and Null Strategy | [`optional-and-null-strategy.md`](../../../syllabus/02-java/language-core/optional-and-null-strategy.md) |
| 12 | T-110 — Records, Sealed Types, and Pattern Matching | [`records-sealed-types-and-pattern-matching.md`](../../../syllabus/02-java/language-core/records-sealed-types-and-pattern-matching.md) |
| 13 | T-111 — Enums, EnumMap, and EnumSet | [`enums-enummap-and-enumset.md`](../../../syllabus/02-java/language-core/enums-enummap-and-enumset.md) |
| 14 | T-112 — Annotations and Annotation Processing | [`annotations-and-annotation-processing.md`](../../../syllabus/02-java/language-core/annotations-and-annotation-processing.md) |
| 15 | T-113 — Reflection and Dynamic Proxies | [`reflection-and-dynamic-proxies.md`](../../../syllabus/02-java/language-core/reflection-and-dynamic-proxies.md) |
| 16 | T-114 — ClassLoaders and Class Initialization | [`classloaders-and-class-initialization.md`](../../../syllabus/02-java/language-core/classloaders-and-class-initialization.md) |
| 17 | T-115 — Serialization Hazards and Alternatives | [`serialization-hazards-and-alternatives.md`](../../../syllabus/02-java/language-core/serialization-hazards-and-alternatives.md) |

## Hands-On Exercises

Every chapter above states its own real, compiled, executed demo in a "Provenance" note near its start — follow that link from the specific chapter for the exact pairing. The real directories backing this week are:

- [`practice/java/oop-fundamentals/syntax-basics/`](../../../practice/java/oop-fundamentals/syntax-basics/) and [`practice/java/oop-fundamentals/classes-and-objects/`](../../../practice/java/oop-fundamentals/classes-and-objects/) (T-2206, T-2201)
- [`practice/java/week-13/equality-contracts/`](../../../practice/java/week-13/equality-contracts/) (T-101)
- [`practice/java/oop-fundamentals/polymorphism/`](../../../practice/java/oop-fundamentals/polymorphism/) (T-102)
- [`practice/java/week-13/immutability/`](../../../practice/java/week-13/immutability/) (T-103)
- [`practice/java/week-13/generics-erasure/`](../../../practice/java/week-13/generics-erasure/) (T-104)
- [`practice/java/week-13/exception-design/`](../../../practice/java/week-13/exception-design/) (T-105)
- [`practice/java/java-core/strings-interning-compact-builders/`](../../../practice/java/java-core/strings-interning-compact-builders/) (T-106)
- [`practice/java/week-13/streams-collectors/`](../../../practice/java/week-13/streams-collectors/) (T-107)
- [`practice/java/lambdas-and-functional-interfaces/`](../../../practice/java/lambdas-and-functional-interfaces/) (T-108)
- [`practice/java/java-core/optional-and-null-strategy/`](../../../practice/java/java-core/optional-and-null-strategy/) (T-109)
- [`practice/java/records-sealed-pattern-matching/`](../../../practice/java/records-sealed-pattern-matching/) (T-110)
- [`practice/java/java-core/enums-enummap-enumset/`](../../../practice/java/java-core/enums-enummap-enumset/) (T-111)
- [`practice/java/java-core/annotations-and-processing/`](../../../practice/java/java-core/annotations-and-processing/) (T-112)
- [`practice/java/java-core/reflection-and-dynamic-proxies/`](../../../practice/java/java-core/reflection-and-dynamic-proxies/) (T-113)
- [`practice/java/java-core/classloaders-and-class-initialization/`](../../../practice/java/java-core/classloaders-and-class-initialization/) (T-114)
- [`practice/java/java-core/serialization-hazards/`](../../../practice/java/java-core/serialization-hazards/) (T-115)

## Interview Answer Drills

Pick 5 of the 17 chapters' own Interview Questions sections at random each day and answer out loud before checking the expected answer — with 17 chapters this week, spaced retrieval across the week matters more than reading speed.

## Coding Problems

None — this pack is domain-depth reading, not coding-pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a class with a custom `equals()` but no overridden `hashCode()`, predict — before running it — what breaks when an instance is put in a `HashSet`, then verify against T-101's own worked example.

## Review Checklist

- [ ] Completed all 17 chapters' own L1–L4 Mastery Checklists.
- [ ] Reproduced at least 8 of the 16 real demos listed above — with 17 chapters in one week, full reproduction of every demo is not required this week, but at least half is.
- [ ] Can state, unprompted, the three-part `equals`/`hashCode` contract and why breaking it silently corrupts hash-based collections.

## Completion Criteria

- [ ] Can explain PECS ("Producer Extends, Consumer Super") with a correct generic method signature, from a blank file.
- [ ] Can name the checked-vs-unchecked exception trade-off and state a defensible rule for when to use each.
- [ ] Can explain why a `record`'s generated `equals`/`hashCode` is component-based, and when that stops being sufficient.

## Retrospective

Note which of the 17 topics you would need to re-read before an actual interview — this is the highest-density week in the pack, and it is normal for 2–3 topics to need a second pass before Week 3 (concurrency) builds on the immutability and equality concepts introduced here.

## Next Week

[Week 2 — Collections, from Usage to Internals](../week-02/README.md).
