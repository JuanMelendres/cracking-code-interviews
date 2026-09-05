---
title: "Syllabus — Overview"
document_type: syllabus-overview-index
status: scaffolding — Phase 1 of the approved Syllabus Transformation Plan
last_updated: 2026-09-03
---

# Syllabus — Overview

This is the entry point to the repository's new `syllabus/` structure, created during **Phase 1 (Scaffolding)** of `00-project/syllabus-transformation-plan.md` (approved 2026-09-03). Per that plan's own phase governance, Phase 1 is additive only — nothing under `handbook/`, `study-packs/`, `cheat-sheets/`, `flashcards/`, `production-cookbook/`, `architecture-atlas/`, `behavioral-handbook/`, `interview-playbook/`, `practice/`, or the repository root has moved, changed, or been rewritten to produce this tree. `git diff --stat` against the pre-Phase-1 commit shows only additions.

## What exists here today

- **[Vision](vision.md)** — from an interview-prep programme to a general-purpose Junior→Staff engineering learning system (Plan §1).
- **[Taxonomy](taxonomy.md)** — the proposed 21-domain target architecture and the reasoning behind its boundary calls (Plan §3).
- **[Topic Specification](topic-specification.md)** — the 20-section standard every canonical topic file will be written or retrofitted against (Plan §4).
- **[Mastery Model](mastery-model.md)** — the four levels (L1 Foundation → L4 Staff) and the "verify, don't just read" discipline (Plan §5).
- **[Learning Paths](learning-paths.md)** — six curated sequences through the same canonical topics, for different audiences and goals (Plan §6).
- **[Changelog](changelog.md)** — tracks changes to the `syllabus/` tree specifically, separate from the repository-root `CHANGELOG.md`.

All five content files above are extracted verbatim from the approved plan, not newly authored — see each file's own provenance note.

## The 21 domains

| # | Domain | Status |
|---|---|---|
| 01 | [Computer Science Foundations](../01-computer-science-foundations/INDEX.md) | 5/5 planned topics written — domain complete (Phase 5, 2026-09-03) |
| 02 | [Java](../02-java/INDEX.md) | 49/49 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 49/49** — first fully L1–L4 domain (Phase 5, 2026-09-04) |
| 03 | [Data Structures & Algorithms](../03-data-structures-algorithms/INDEX.md) | 17/17 planned topics written — domain complete (Phase 5, 2026-09-03) |
| 04 | [Software Design](../04-software-design/INDEX.md) | 1/1 chapter relocated (Phase 2, 2026-09-03); **L1/L2 retrofit complete, 1/1** — fully L1–L4 (Phase 5, 2026-09-04) |
| 05 | [Spring](../05-spring/INDEX.md) | 9/9 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 9/9** — fully L1–L4 (Phase 5, 2026-09-04) |
| 06 | [Databases](../06-databases/INDEX.md) | 14/14 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 14/14** — fully L1–L4 (Phase 5, 2026-09-04) |
| 07 | [API Design](../07-api-design/INDEX.md) | 2/2 chapters relocated (Phase 2, 2026-09-03); **L1/L2 retrofit complete, 2/2** — fully L1–L4 (Phase 5, 2026-09-04) |
| 08 | [Testing](../08-testing/INDEX.md) | 7/7 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 7/7** — fully L1–L4 (Phase 5, 2026-09-04) |
| 09 | [Messaging & Event-Driven Systems](../09-messaging-event-driven/INDEX.md) | 9/9 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 9/9** — fully L1–L4 (Phase 5, 2026-09-04) |
| 10 | [Distributed Systems](../10-distributed-systems/INDEX.md) | 5/5 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 5/5** — fully L1–L4 (Phase 5, 2026-09-04) |
| 11 | [System Design](../11-system-design/INDEX.md) | 9/9 chapters relocated (Phase 3, 2026-09-03) + 17 Architecture Atlas case studies (referenced); **L1/L2 retrofit complete, 9/9** — fully L1–L4 (Phase 5, 2026-09-04) |
| 12 | [Security](../12-security/INDEX.md) | 8/8 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 8/8** — fully L1–L4 (Phase 5, 2026-09-04) |
| 13 | [Observability](../13-observability/INDEX.md) | 4/4 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 4/4** — fully L1–L4 (Phase 5, 2026-09-04) |
| 14 | [DevOps & Containers](../14-devops-containers/INDEX.md) | 4/4 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 4/4** — fully L1–L4 (Phase 5, 2026-09-04) |
| 15 | [Cloud](../15-cloud/INDEX.md) | 3/3 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 3/3** — fully L1–L4 (Phase 5, 2026-09-04) |
| 16 | [Performance & JVM Tuning](../16-performance-jvm/INDEX.md) | 3/3 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 3/3** — fully L1–L4 (Phase 5, 2026-09-04) |
| 17 | [Architecture](../17-architecture/INDEX.md) | 9/9 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 9/9** — fully L1–L4 (Phase 5, 2026-09-04) |
| 18 | [Engineering Practices](../18-engineering-practices/INDEX.md) | 5/5 planned topics present — domain complete (Phase 5, 2026-09-03) |
| 19 | [Leadership & Staff Engineering](../19-leadership-staff/INDEX.md) | 5/5 planned topics written — domain complete (Phase 5, 2026-09-04) |
| 20 | [Interview Preparation](../20-interview-preparation/INDEX.md) | 19/19 chapters relocated (Phase 3, 2026-09-03); `practice/mock-interviews/` referenced, `company-prep/` private and untouched; **L1/L2 retrofit complete, 21/21** — fully L1–L4 (Phase 5, 2026-09-04) |
| 21 | [Frontend & Web (React/Next.js)](../21-frontend-web/INDEX.md) | 32/32 chapters relocated (Phase 3, 2026-09-03) |

## What's next

Per `00-project/syllabus-transformation-plan.md` §10: **Phase 2 (Low-risk relocations)** and **Phase 3 (Domain-by-domain handbook migration)** are both complete for every domain that had existing content to migrate. As of 2026-09-03: all 137 backend `handbook/` chapters, all 31 `handbook/frontend/` chapters, all 15 `behavioral-handbook/` chapters, and 5 of `interview-playbook/`'s non-private entries have relocated — 17 domains fully migrated: `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, `11-system-design`, `12-security`, `13-observability`, `14-devops-containers`, `15-cloud`, `16-performance-jvm`, `17-architecture`, `20-interview-preparation`, `21-frontend-web`. `18-engineering-practices` has its one existing chapter (git internals) relocated but is otherwise new writing (Phase 5). `01-computer-science-foundations`, `03-data-structures-algorithms`, and `19-leadership-staff` remain new-writing-only domains with no migration step — **Phase 3 has no more file relocations left to do.** What remains is entirely Phase 5 (Foundation/Working-Knowledge gap-filling) and Phase 6 (Learning-path assembly), neither authorized yet.

See `00-project/migration-mapping.md` for the exhaustive, file-by-file mapping this tree was generated from.
