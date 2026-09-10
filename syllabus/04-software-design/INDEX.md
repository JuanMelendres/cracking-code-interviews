---
title: "Software Design — Domain Index"
document_type: syllabus-domain-index
domain: 04-software-design
status: 3 of 3 chapters written — domain gap-audited and closed (2026-09-10), fully L1-L4
last_updated: 2026-09-10
---

# Software Design

Class/module-level design decisions — a different altitude from `17-architecture`'s system-level concerns. `design-patterns-applied.md` (relocated), `solid-principles.md`, and `ood-interview-problems.md` (both new, closing a real gap) plus `practice/java/{oop-fundamentals,design-patterns,solid-principles,ood-interview-problems}/`.

> **Phase 2 update (2026-09-03).** `design-patterns-applied.md` has physically relocated here via `git mv` from `handbook/architecture/` — one of the plan's own named low-risk relocations (§10 Phase 2). It was the one chapter this domain had at the time.
>
> **Phase 5 update (2026-09-04).** The chapter gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's own additive retrofit method (§2.4) — a pure insertion, verified by diff, with the existing 25-item TOC (which already carries "Java Examples" and "Comparisons" beyond the base template) renumbered correctly. It also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter.
>
> **Gap audit and closure (2026-09-10).** A repository-wide domain gap audit found this single-chapter domain had zero coverage of SOLID principles or object-oriented design (OOD) interview problems (parking lot, elevator, vending machine, etc.) — the audit's own assessment named this "arguably the single largest gap" found across the four domains it covered in that pass, since both topics are near-universal Mid/Senior interview material squarely within this domain's own stated "class/module-level design" scope. Closed with two new chapters, using this domain's own previously-reserved-but-unused `T-1700`–`T-1799` range (per `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection): [SOLID Principles](solid-principles.md) (T-1701), backed by a real Java demo (`practice/java/solid-principles/`) with one violation and one fix per principle — reflection-based SRP evidence, a new shape added with zero edits to an existing OCP-compliant calculator, a real failing Liskov-invariant assertion, a real thrown exception from a fat ISP-violating interface, and one unmodified DIP-compliant class run against two injected implementations; and [Object-Oriented Design Interview Problems](ood-interview-problems.md) (T-1702), backed by two real, fully worked demos (`practice/java/ood-interview-problems/`) — a parking lot with real size-based spot matching and real time-based fee calculation, and a vending machine modeled as an explicit state machine, correctly rejecting illegal action sequences a scattered-boolean design has a real, demonstrated risk of allowing.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-914 | Design Patterns Applied (GoF in Production) | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/04-software-design/design-patterns-applied.md` |
| T-1701 | SOLID Principles | L1, L2, L3, L4 — fully written, real demo (2026-09-10) | `syllabus/04-software-design/solid-principles.md` |
| T-1702 | Object-Oriented Design Interview Problems | L1, L2, L3, L4 — fully written, real demo (2026-09-10) | `syllabus/04-software-design/ood-interview-problems.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
