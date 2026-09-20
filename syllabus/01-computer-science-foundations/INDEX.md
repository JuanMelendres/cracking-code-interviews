---
title: "Computer Science Foundations — Domain Index"
document_type: syllabus-domain-index
domain: 01-computer-science-foundations
status: 6 of 5 originally-planned topics written (Phase 5 base complete 2026-09-03; T-2006 gap-audit addition 2026-09-20)
last_updated: 2026-09-20
---

# Computer Science Foundations

How computers actually execute a program, memory hierarchy and virtual memory, number representation, the OS process/thread model below Java's abstraction, networking basics, and algorithmic complexity from first principles. **New domain — no migrated content; every topic here is new writing**, prioritized first among the gap domains per the plan's Section 7.6, since it is the hard prerequisite for the new Junior entry point.

> **Phase 5 update (2026-09-03).** All five topics named in the plan's own Section 2.5/§7.6 gap description are now written — this domain is complete for its originally-scoped topic list. Each applies the [Topic Specification](../00-overview/topic-specification.md) and [Mastery Model](../00-overview/mastery-model.md) with genuine L1→L4 coverage in a single file, per the plan's own "one topic, not four seniority-versioned copies" requirement (§5.3), assigned T-codes in the plan's reserved `T-2000`–`T-2099` range (§9).
>
> **Gap-audit addition (2026-09-20).** T-2006 closes a real gap the original five-topic scope missed: zero coverage anywhere of the memory hierarchy (cache lines, RAM latency) or the virtual-memory page-fault mechanism, despite two already-written `16-performance-jvm` chapters ([False Sharing](../16-performance-jvm/false-sharing-and-cache-line-contention.md), [Memory-Mapped Files](../16-performance-jvm/memory-mapped-files-and-zero-copy-io.md)) depending on exactly this foundation with no citable prerequisite. The domain's reserved `T-2000`–`T-2099` range always had room for this; "complete for its originally-scoped list" was never a hard ceiling.

## Topics

| Topic ID | Title | Mastery levels covered | Location |
|---|---|---|---|
| T-2001 | [Algorithmic Complexity and Big-O, From First Principles](algorithmic-complexity-and-big-o-from-first-principles.md) | L1, L2, L3, L4 — fully written | `syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md` |
| T-2002 | [How a Computer Executes a Program](how-a-computer-executes-a-program.md) (fetch-decode-execute, bytecode vs. machine code, the call stack below the JVM's own abstraction of it) | L1, L2, L3, L4 — fully written | `syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md` |
| T-2006 | [Memory Hierarchy: Caches, RAM, and Virtual Memory](memory-hierarchy-caches-ram-and-virtual-memory.md) (cache lines, spatial locality, pointer-chasing latency measurement, virtual memory and page faults) | L1, L2, L3, L4 — fully written | `syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md` |
| T-2003 | [Number Representation](number-representation.md) (two's complement, IEEE 754 floating point, overflow, narrowing-cast truncation) | L1, L2, L3, L4 — fully written | `syllabus/01-computer-science-foundations/number-representation.md` |
| T-2004 | [The OS Process/Thread Model, Below Java's Abstraction of It](os-process-thread-model.md) (processes, threads, context switching, the 1:1 vs. M:N threading models) | L1, L2, L3, L4 — fully written | `syllabus/01-computer-science-foundations/os-process-thread-model.md` |
| T-2005 | [Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer](networking-basics.md) (the TCP handshake, HTTP as text over a byte stream, connection pooling) | L1, L2, L3, L4 — fully written | `syllabus/01-computer-science-foundations/networking-basics.md` |

Written in dependency order, each building on real, executed evidence from the one before it: T-2001 (prerequisite-free) → T-2002 (the call stack, the layer below T-2001's own performance vocabulary) → T-2006 (fills in what sits between a T-2002 register and RAM, with real pointer-chase latency measurements on real hardware) → T-2003 (what a number actually is, once T-2002 established that a variable is bits in memory) → T-2004 (what happens when many instruction streams — Section 3 of T-2002 — run on one machine at once) → T-2005 (what happens when two machines need to talk at all). Cheat sheets for all five original topics were added 2026-09-06 and flashcard decks for all five were added 2026-09-07 (see `cheat-sheets/README.md`'s New-Writing Domain Cheat Sheets table and `flashcards/README.md`'s New-Writing Domain Decks table); T-2006's cheat sheet and flashcard deck were added alongside the chapter itself on 2026-09-20. Production-cookbook was investigated rather than batch-written (2026-09-07): 4 of the original 5 chapters already cite an existing entry in their own `production_scenarios` front matter; `number-representation.md` has no citation but its own Section 14 explicitly documents why (no existing entry has a numeric-representation-specific root cause) with a `Planned reference` note rather than a placeholder — per `production-cookbook/README.md`'s own "elevate an existing worked scenario, never invent one" rule, this is a resolved, honest gap, not unbuilt work. T-2006 follows the same honest pattern (see its own Section 14): no cookbook entry roots in CPU-cache-hierarchy latency specifically, so it cites its own two real, in-repo applied consequences instead.

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
