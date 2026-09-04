---
title: "Engineering Practices — Domain Index"
document_type: syllabus-domain-index
domain: 18-engineering-practices
status: 5 of 5 planned topics present — domain complete (Phase 5, 2026-09-03)
last_updated: 2026-09-03
---

# Engineering Practices

Git internals, code review, technical writing (ADRs), working with legacy code, and refactoring discipline. New topics in this domain are assigned IDs in the plan's reserved `T-1800`–`T-1899` range (§9).

> **Phase 5 update (2026-09-03).** All four gap topics named in the plan's own Section 7.6 ("code review, technical writing standards, working with legacy code, refactoring discipline") are now written, closing this domain. Each new chapter is written against the full 20-section Topic Specification with genuine L1–L4 coverage in one file. Two chapters (Legacy Code, Refactoring Discipline) are backed by real, compiled, executed Java demos; Code Review and ADRs are grounded in real, existing repository artifacts (this repository's own commit history, `templates/adr-template.md`, and `scripts/check_adr_completeness.py`, actually run) rather than a Java compile-and-run demo, since neither topic is itself an algorithm.

## Topics

| Topic ID | Title | Mastery levels covered | Location |
|---|---|---|---|
| — | Git Internals and Collaboration Workflows | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending a future retrofit pass) | `syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md` |
| T-1801 | [Code Review: Standards and Practice](code-review-standards-and-practice.md) | L1, L2, L3, L4 — fully written | `syllabus/18-engineering-practices/code-review-standards-and-practice.md` |
| T-1802 | [Architecture Decision Records and Technical Writing for Engineers](architecture-decision-records-and-technical-writing.md) | L1, L2, L3, L4 — fully written | `syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md` |
| T-1803 | [Working with Legacy Code](working-with-legacy-code.md) | L1, L2, L3, L4 — fully written | `syllabus/18-engineering-practices/working-with-legacy-code.md` |
| T-1804 | [Refactoring Discipline](refactoring-discipline.md) | L1, L2, L3, L4 — fully written | `syllabus/18-engineering-practices/refactoring-discipline.md` |

**This domain is now complete.** T-1802 deliberately does not duplicate [Trade-off Narration and Architecture Decision Records](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) (the interview-answer application of the same underlying skill, already migrated to `20-interview-preparation`) — it is the canonical, general engineering-practice reference that playbook entry's own "ADRs — the Written Form of the Same Skill" section points to. T-1803's practice code (`practice/java/engineering-practices/legacy-code/`) surfaced a genuine, real finding while being built — a discount-cliff quirk in the demo's own deliberately-legacy pricing method — used directly as the chapter's central worked example of characterization testing, rather than an invented one. T-1804's practice code (`practice/java/engineering-practices/refactoring-discipline/`) proves a real three-step Extract Method refactor is behavior-preserving via a parity test comparing before/after output across 10 real cases, all passing. Cheat sheets, flashcards, and production-cookbook entries for all four new topics have not been built yet — per this session's established batching discipline, that backlog is closed in a separate pass.

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 and 7.6 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
