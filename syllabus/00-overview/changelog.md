---
title: "Syllabus Changelog"
document_type: syllabus-changelog
status: active
last_updated: 2026-09-03
---

# Syllabus Changelog

Tracks changes to the `syllabus/` tree specifically — domain content migrations, gap-filling, and taxonomy adjustments. Separate from the repository-root `CHANGELOG.md`, which continues to track the whole repository including everything outside `syllabus/`.

## [2026-09-03] — Phase 1: Scaffolding

### Added

- `syllabus/00-overview/` — vision, taxonomy, topic specification, mastery model, and learning paths, extracted verbatim from the approved `00-project/syllabus-transformation-plan.md`.
- All 21 domain directories (`syllabus/01-computer-science-foundations/` through `syllabus/21-frontend-web/`), each with a populated `INDEX.md` listing its mapped topics (topic ID, title, current mastery levels, current `handbook/` location) per `00-project/migration-mapping.md`.

### Not yet done

- No content has physically moved. Every domain `INDEX.md` currently points back to the topic's real, unmoved `handbook/` (or other) location.
- Root `README.md`/`CLAUDE.md` framing rewrite (mentioned in the plan's §2.1 but not in its §13 Definition of Done for Phase 1) was deliberately deferred, per an explicit user decision to keep Phase 1 purely additive — `git diff --stat` against the pre-Phase-1 commit shows only new files.
- Phase 2 (low-risk single-file relocations) and Phase 3 (domain-by-domain handbook migration, starting with `02-java` per the approved plan) have not been authorized.
