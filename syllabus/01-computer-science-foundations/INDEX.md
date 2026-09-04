---
title: "Computer Science Foundations — Domain Index"
document_type: syllabus-domain-index
domain: 01-computer-science-foundations
status: 2 of ~5 planned topics written (Phase 5, in progress, started 2026-09-03)
last_updated: 2026-09-03
---

# Computer Science Foundations

How computers actually execute a program, number representation, the OS process/thread model below Java's abstraction, networking basics, and algorithmic complexity from first principles. **New domain — no migrated content; every topic here is new writing**, prioritized first among the gap domains per the plan's Section 7.6, since it is the hard prerequisite for the new Junior entry point.

> **Phase 5 update (2026-09-03).** Writing continues. The plan's own Section 2.5/§7.6 names five explicit gap areas for this domain (how a computer executes a program, number representation, the OS process/thread model, networking basics, algorithmic complexity) — this is the working topic list, assigned new T-codes in the plan's reserved `T-2000`–`T-2099` range (§9). Two topics are written so far, applying the [Topic Specification](../00-overview/topic-specification.md) and [Mastery Model](../00-overview/mastery-model.md) with genuine L1→L4 coverage in a single file, per the plan's own "one topic, not four seniority-versioned copies" requirement (§5.3).

## Topics

| Topic ID | Title | Mastery levels covered | Location |
|---|---|---|---|
| T-2001 | [Algorithmic Complexity and Big-O, From First Principles](algorithmic-complexity-and-big-o-from-first-principles.md) | L1, L2, L3, L4 — fully written | `syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md` |
| T-2002 | [How a Computer Executes a Program](how-a-computer-executes-a-program.md) (fetch-decode-execute, bytecode vs. machine code, the call stack below the JVM's own abstraction of it) | L1, L2, L3, L4 — fully written | `syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md` |
| T-2003 | Number Representation (binary, two's complement, floating point, overflow) | Not yet written | Planned |
| T-2004 | The OS Process/Thread Model, Below Java's Abstraction of It | Not yet written | Planned |
| T-2005 | Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer | Not yet written | Planned |

T-2001 was chosen first because it is genuinely prerequisite-free, is explicitly named in the plan's own gap description, and is the vocabulary every other existing chapter in this syllabus that discusses performance already assumes. T-2002 was chosen next because it is the layer directly below T-2001 and below [JVM Memory Layout and Runtime Regions](../02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md) — it builds real evidence (a `-Xss`-vs-recursion-depth measurement, a `javap -c` disassembly) rather than duplicating either. Cheat sheets, flashcards, and production-cookbook entries for T-2001/T-2002 have not been built yet — per this session's established batching discipline, chapter-writing and complementary-deliverable backlogs are closed in separate, bounded passes rather than bundled into one turn.

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
