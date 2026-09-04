---
title: "Leadership & Staff Engineering — Domain Index"
document_type: syllabus-domain-index
domain: 19-leadership-staff
status: 5 of 5 planned topics written — domain complete (Phase 5, 2026-09-04)
last_updated: 2026-09-04
---

# Leadership & Staff Engineering

The technical-leadership knowledge ladder: mentoring, cross-team influence, leading large-scale technical change, technical-debt advocacy, and design-review/RFC process design — each treated as a working engineering skill in its own right, distinct from its interview-narration counterpart in `syllabus/20-interview-preparation/behavioral/`. New topics in this domain are assigned IDs in the plan's reserved `T-1900`–`T-1999` range (§9).

> **Phase 5 update (2026-09-04).** Per the Section 2.7 decision (approved 2026-09-03), this domain *references* — never duplicates — five existing `20-interview-preparation/behavioral/` chapters (07 Mentoring, 09 Cross-Team Influence, 10 Migrations, 11 Technical Debt Advocacy, 12 Design Reviews and RFCs) as source material for a new Level 1–4 knowledge ladder. Each behavioral chapter is exclusively STAR-interview-narration content (verified by reading all five in full before writing); none of them cover the underlying working skill itself — how to actually run a 1:1, build a stakeholder map, sequence a migration, quantify debt, or design a review process. That gap is what this domain's five new chapters fill. Two chapters (Leading Migrations, Technical Debt) also draw an explicit boundary against existing `17-architecture` chapters that own the architectural/technical mechanics of the same subject; one chapter (Design Reviews and RFCs) draws a three-way boundary against both the ADR-writing chapter in `18-engineering-practices` and the verbal-narration chapter in `20-interview-preparation/technical-answers`. All boundaries are stated explicitly in each new chapter's own opening section, not left implicit.

## Topics

| Topic ID | Title | Mastery levels covered | Location |
|---|---|---|---|
| T-1901 | [Mentoring and Developing Others](mentoring-and-developing-others.md) | L1, L2, L3, L4 — fully written | `syllabus/19-leadership-staff/mentoring-and-developing-others.md` |
| T-1902 | [Cross-Team Influence Without Authority](cross-team-influence-without-authority.md) | L1, L2, L3, L4 — fully written | `syllabus/19-leadership-staff/cross-team-influence-without-authority.md` |
| T-1903 | [Leading Migrations and Large-Scale Technical Change](leading-migrations-and-large-technical-change.md) | L1, L2, L3, L4 — fully written | `syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md` |
| T-1904 | [Technical Debt: Prioritization and Advocacy](technical-debt-prioritization-and-advocacy.md) | L1, L2, L3, L4 — fully written | `syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md` |
| T-1905 | [Design Reviews and RFCs as an Organizational Practice](design-reviews-and-rfcs-as-organizational-practice.md) | L1, L2, L3, L4 — fully written | `syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md` |

**This domain is now complete.** Two real, existing `production-cookbook/` entries are cited as grounding evidence rather than fabricated: [Shared Customer Entity Requiring a Three-Team Migration](../../production-cookbook/shared-customer-entity-forcing-a-three-team-migration-for-one-field.md) (T-1902, T-1903) and [Gradual Coupling Erosion Turning a Core Class into a Release Bottleneck](../../production-cookbook/gradual-coupling-erosion-turning-a-core-class-into-a-release-bottleneck.md) (T-1904). T-1901 and T-1905 have no matching existing cookbook entry (the cookbook is technical-incident-shaped by design, not people/process-incident-shaped); both use an explicitly labeled representative scenario, following this repository's own established labeling convention for illustrative-not-literal examples, with a `> Planned reference:` note for a future dedicated entry. Cheat sheets, flashcards, and production-cookbook entries for all five new topics have not been built yet — per this session's established batching discipline, that backlog is closed in a separate pass.

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 2.7, 3.2–3.3, and 7.6 (item 5) for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
