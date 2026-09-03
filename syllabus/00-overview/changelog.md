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

## [2026-09-03] — Phase 2: Low-risk relocations

### Changed

- Relocated, via `git mv`, the four low-risk files the plan named in §10: `git-internals-and-collaboration-workflows.md` (`handbook/cloud/` → `18-engineering-practices/`), `design-patterns-applied.md` (`handbook/architecture/` → `04-software-design/`), and both `07-api-design/` files (`api-design.md`, `api-gateway-bff-and-edge-concerns.md`, both from `handbook/system-design/`). Each gained a `source_history` field recording its real original path.
- Updated 42 files' inbound references across the repository to the new paths (`cheat-sheets/`, `flashcards/`, `production-cookbook/`, other `handbook/` chapters, `architecture-atlas/`, `practice/`, `study-packs/`) — see the repository-root `CHANGELOG.md`'s matching entry for the full accounting, including two link-breakage classes caught during verification (the moved files' own links to siblings left behind, and staying files that referenced a moved file by bare same-directory filename).
- Updated the three affected domain `INDEX.md` files (`04-software-design/`, `07-api-design/`, `18-engineering-practices/`) to reflect real, physically-present content instead of a scaffolding placeholder.

### Not yet done

- These four relocated files still carry only L3/L4 (Senior/Staff-depth) content — Foundation/Working-Knowledge layers remain Phase 5 gap-filling work, same as every other existing chapter.

## [2026-09-03] — Phase 3: First domain migration (02-java)

### Changed

- Relocated all 49 mapped `02-java` chapters via `git mv`: `handbook/java-core/` (15) → `language-core/`, `handbook/collections/` (9) → `collections/`, `handbook/jvm/` (12 of 13 — `benchmarking-and-jmh-pitfalls.md` stays for `16-performance-jvm`'s own turn) → `jvm-internals/`, `handbook/concurrency/` (13) → `concurrency/`. Each gained `source_history` and an updated `domain` field.
- Built a general link fixer that recomputes every one of these 49 files' own outbound links from their pristine pre-move content, correctly handling both "the target moved too" and "only I moved" cases — the subdomain nesting here is one level deeper than the old `handbook/` layout, so even links to unmoved content needed depth recalculation.
- Fixed 235 other files' inbound references (1,353 individual link fixes) across the rest of the repository. See the repository-root `CHANGELOG.md` for the full account, including a caught-and-fixed regression (7 `practice/` READMEs) and 51 discovered-but-out-of-scope pre-existing broken links unrelated to this migration.
- Updated `syllabus/02-java/INDEX.md` to reflect the real relocation.

### Not yet done

- Phase 3 for every other domain (19 more, plus the 2 new-writing-only domains) has not been authorized.
- `02-java`'s Foundation/Working-Knowledge (L1/L2) layers remain Phase 5 work.
