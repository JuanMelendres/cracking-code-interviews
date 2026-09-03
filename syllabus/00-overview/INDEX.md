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
| 01 | [Computer Science Foundations](../01-computer-science-foundations/INDEX.md) | New domain — no existing content |
| 02 | [Java](../02-java/INDEX.md) | 52 chapters mapped, pending Phase 3 relocation |
| 03 | [Data Structures & Algorithms](../03-data-structures-algorithms/INDEX.md) | New domain — no existing content |
| 04 | [Software Design](../04-software-design/INDEX.md) | 1 chapter mapped |
| 05 | [Spring](../05-spring/INDEX.md) | 9 chapters mapped |
| 06 | [Databases](../06-databases/INDEX.md) | 14 chapters mapped |
| 07 | [API Design](../07-api-design/INDEX.md) | 2 chapters mapped |
| 08 | [Testing](../08-testing/INDEX.md) | 7 chapters mapped |
| 09 | [Messaging & Event-Driven Systems](../09-messaging-event-driven/INDEX.md) | 9 chapters mapped |
| 10 | [Distributed Systems](../10-distributed-systems/INDEX.md) | 5 chapters mapped |
| 11 | [System Design](../11-system-design/INDEX.md) | 9 chapters + 17 Architecture Atlas case studies (referenced) |
| 12 | [Security](../12-security/INDEX.md) | 8 chapters mapped |
| 13 | [Observability](../13-observability/INDEX.md) | 4 chapters mapped |
| 14 | [DevOps & Containers](../14-devops-containers/INDEX.md) | 4 chapters mapped |
| 15 | [Cloud](../15-cloud/INDEX.md) | 3 chapters mapped |
| 16 | [Performance & JVM Tuning](../16-performance-jvm/INDEX.md) | 3 chapters mapped |
| 17 | [Architecture](../17-architecture/INDEX.md) | 9 chapters mapped |
| 18 | [Engineering Practices](../18-engineering-practices/INDEX.md) | 1 chapter mapped — mostly new writing beyond that seed |
| 19 | [Leadership & Staff Engineering](../19-leadership-staff/INDEX.md) | New domain — references `behavioral-handbook/`, duplicates nothing |
| 20 | [Interview Preparation](../20-interview-preparation/INDEX.md) | Sourced from `behavioral-handbook/`, `interview-playbook/`, `practice/mock-interviews/` |
| 21 | [Frontend & Web (React/Next.js)](../21-frontend-web/INDEX.md) | 31 chapters mapped |

## What's next

Per `00-project/syllabus-transformation-plan.md` §10, the next phase is **Phase 2 (Low-risk relocations)** — moving the handful of clearly mis-homed single files identified in the plan's §2.6/§7.2 (git internals, design patterns, api-design) via `git mv`, followed by **Phase 3 (Domain-by-domain handbook migration)**, starting with `02-java` per the approved answer to the plan's §14 open question 6. Neither has been authorized yet — each phase requires its own explicit go/no-go.

See `00-project/migration-mapping.md` for the exhaustive, file-by-file mapping this tree was generated from.
