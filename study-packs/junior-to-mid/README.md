---
title: "Junior → Mid Study Pack — Index"
document_type: study-pack-index
status: draft
last_updated: 2026-09-08
---

# Junior → Mid Study Pack

The scheduled, week-by-week version of [`syllabus/00-overview/learning-paths/junior-to-mid.md`](../../syllabus/00-overview/learning-paths/junior-to-mid.md) — that path is the *content* (22 topics, in dependency order, with a stated stop-at level and a one-line reason for each); this is the *schedule* built on top of it, the same relationship `study-packs/week-01` through `week-25` have to the original Senior-focused program's own roadmap.

## Audience and scope

A new engineer (roughly 0–2 years) with some prior programming exposure — able to write code that runs, but without a reliable mental model yet for *why* Java's standard library and the JVM behave the way they do. This pack assumes the reader has never done anything backend-specific before: no OOP, no SQL, no Spring, no automated testing, no containers. It stops at L2 (Practitioner — correct usage, and the ability to choose between named alternatives with a stated reason) for every topic; L3/L4 internals depth is [Mid → Senior](../../syllabus/00-overview/learning-paths/mid-to-senior.md)'s job, not this pack's.

**No duplicated content.** Every week below links to its topics' real, canonical `syllabus/` chapters and their real, compiled/executed `practice/` demos — this pack adds scheduling, sequencing rationale, and lightweight review checkpoints on top of material that already exists in full, per this repository's own no-duplication rule.

## Why this exists

The Junior Fundamentals initiative (T-2201–T-2208, built 2026-09-07/08) closed a real gap: this repository's Java backend domain assumed 5+ years of experience before that week, teaching internals of things (`HashMap`, Spring beans, REST conventions) it never actually taught the basics of first. The learning path sequenced those chapters correctly the same week they were written. This study pack is the natural next step — the same operational structure (schedule, hands-on exercises, review checklist, lightweight mock) the Senior track already has, built for this track specifically rather than assuming it can be skipped because the audience is newer.

## Weeks

| Week | Theme | Topics | Estimated hours |
|---|---|---|---|
| [01](week-01/README.md) | Java, from the ground up | Java Syntax Fundamentals (T-2206), OOP Fundamentals (T-2201), How a Computer Executes a Program (T-2002) | 6–8h |
| [02](week-02/README.md) | Numbers, complexity, and collections as a concept | Number Representation (T-2003), Algorithmic Complexity and Big-O (T-2001), Collections Usage Fundamentals (T-2207) | 6–8h |
| [03](week-03/README.md) | Collections, for real this time | equals()/hashCode()/Comparable (T-101), HashMap Internals (T-201), ArrayList/LinkedList Internals (T-202) | 6–8h |
| [04](week-04/README.md) | Coding patterns, part 1 | Arrays/Two Pointers/Sliding Window (T-2101), Hashing Patterns and Frequency Maps (T-2102) | 6–8h |
| [05](week-05/README.md) | Coding patterns, part 2 + first SQL | Binary Search (T-2103), Linked Lists (T-2104), SQL and Relational Database Fundamentals (T-2202) | 6–8h |
| [06](week-06/README.md) | Databases and testing | Database Index Structures (T-609), Data Modelling and Explicit Join Tables (T-605/T-608), Unit Testing Fundamentals with JUnit (T-2204), Test Strategy and Test Doubles (T-1101/T-1103) | 8h |
| [07](week-07/README.md) | Ship something | Spring MVC Fundamentals (T-2203), Spring Framework vs. Spring Boot (T-506/T-501), REST API Fundamentals (T-2205), Docker and Containers Fundamentals (T-2208) | 8h |

**Total: ~48–56 hours across 7 weeks**, matching the learning path's own ~7-week, 5–8h/week estimate.

## After this pack

[Mid → Senior](../../syllabus/00-overview/learning-paths/mid-to-senior.md) — the direct continuation, taking these same domains (plus several new ones) to L3.
