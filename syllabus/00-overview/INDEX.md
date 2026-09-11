---
title: "Syllabus — Overview"
document_type: syllabus-overview-index
status: scaffolding — Phase 1 of the approved Syllabus Transformation Plan
last_updated: 2026-09-06
---

# Syllabus — Overview

This is the entry point to the repository's new `syllabus/` structure, created during **Phase 1 (Scaffolding)** of `00-project/syllabus-transformation-plan.md` (approved 2026-09-03). Per that plan's own phase governance, Phase 1 is additive only — nothing under `handbook/`, `study-packs/`, `cheat-sheets/`, `flashcards/`, `production-cookbook/`, `architecture-atlas/`, `behavioral-handbook/`, `interview-playbook/`, `practice/`, or the repository root has moved, changed, or been rewritten to produce this tree. `git diff --stat` against the pre-Phase-1 commit shows only additions.

## What exists here today

- **[Vision](vision.md)** — from an interview-prep programme to a general-purpose Junior→Staff engineering learning system (Plan §1).
- **[Taxonomy](taxonomy.md)** — the proposed 21-domain target architecture and the reasoning behind its boundary calls (Plan §3).
- **[Topic Specification](topic-specification.md)** — the 20-section standard every canonical topic file will be written or retrofitted against (Plan §4).
- **[Mastery Model](mastery-model.md)** — the four levels (L1 Foundation → L4 Staff) and the "verify, don't just read" discipline (Plan §5).
- **[Learning Paths](learning-paths.md)** — six curated sequences through the same canonical topics, for different audiences and goals (Plan §6). The Phase 1 outline is extracted verbatim; as of Phase 6 (2026-09-05), all six are also real, assembled documents in [`learning-paths/`](learning-paths/) — see that directory's six files for the actual ordered topic sequences.
- **[Changelog](changelog.md)** — tracks changes to the `syllabus/` tree specifically, separate from the repository-root `CHANGELOG.md`.

Vision, Taxonomy, Topic Specification, and Mastery Model are extracted verbatim from the approved plan, not newly authored — see each file's own provenance note. Learning Paths' outline table is likewise verbatim; its six real path documents (Phase 6) are newly authored, referencing only existing canonical content per §6's own "curated sequence, never a copy" rule.

## The 22 domains

| # | Domain | Status |
|---|---|---|
| 01 | [Computer Science Foundations](../01-computer-science-foundations/INDEX.md) | 5/5 planned topics written — domain complete (Phase 5, 2026-09-03) |
| 02 | [Java](../02-java/INDEX.md) | 49/49 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 49/49** — first fully L1–L4 domain (Phase 5, 2026-09-04); 61 chapters as of 2026-09-10 (gap audit added PriorityQueue Internals, java.time API, concurrency Synchronizers, the Java Platform Module System filling its own long-reserved T-116 slot, MethodHandle and java.lang.invoke, and Bytecode and Class File Fundamentals — all three of `CLAUDE.md`'s named Java Core gaps now closed) |
| 03 | [Data Structures & Algorithms](../03-data-structures-algorithms/INDEX.md) | 17/17 planned topics written — domain complete (Phase 5, 2026-09-03); 18 chapters as of 2026-09-10 (gap audit added Sorting Algorithms — never part of the original plan, despite being the domain's most reused primitive) |
| 04 | [Software Design](../04-software-design/INDEX.md) | 1/1 chapter relocated (Phase 2, 2026-09-03); **L1/L2 retrofit complete, 1/1** — fully L1–L4 (Phase 5, 2026-09-04); gap-audited and closed, 3/3 (2026-09-10, added SOLID Principles and OOD Interview Problems) |
| 05 | [Spring](../05-spring/INDEX.md) | 9/9 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 9/9** — fully L1–L4 (Phase 5, 2026-09-04); 11 chapters as of 2026-09-10 (gap audit added Bean Validation and Global Exception Handling; Spring Data JPA repository abstraction remains open) |
| 06 | [Databases](../06-databases/INDEX.md) | 14/14 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 14/14** — fully L1–L4 (Phase 5, 2026-09-04); 17 chapters as of 2026-09-10 (gap audit added Window Functions and CTEs, and JSONB/GIN/GiST/BRIN advanced index types) |
| 07 | [API Design](../07-api-design/INDEX.md) | 2/2 chapters relocated (Phase 2, 2026-09-03); **L1/L2 retrofit complete, 2/2** — fully L1–L4 (Phase 5, 2026-09-04) |
| 08 | [Testing](../08-testing/INDEX.md) | 7/7 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 7/7** — fully L1–L4 (Phase 5, 2026-09-04) |
| 09 | [Messaging & Event-Driven Systems](../09-messaging-event-driven/INDEX.md) | 9/9 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 9/9** — fully L1–L4 (Phase 5, 2026-09-04); 11 chapters as of 2026-09-10 (gap audit filled two long-reserved slots — Retention/Log Compaction and Kafka Streams — with real Docker-based demos; Kafka Connect remains open) |
| 10 | [Distributed Systems](../10-distributed-systems/INDEX.md) | 5/5 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 5/5** — fully L1–L4 (Phase 5, 2026-09-04); 7 chapters as of 2026-09-10 (gap audit added Consensus Algorithms, a real PACELC fix, and Vector Clocks/Quorum-Based Replication — this domain's audit gaps are now fully closed) |
| 11 | [System Design](../11-system-design/INDEX.md) | 9/9 chapters relocated (Phase 3, 2026-09-03) + 19 Architecture Atlas case studies (referenced); **L1/L2 retrofit complete, 9/9** — fully L1–L4 (Phase 5, 2026-09-04); 18th and 19th Atlas entries (Video Streaming Platform, Distributed File Storage System) added 2026-09-10/11 by a gap audit, beyond T-813's own closed 12-problem target — web crawler/autocomplete remains open |
| 12 | [Security](../12-security/INDEX.md) | 8/8 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 8/8** — fully L1–L4 (Phase 5, 2026-09-04); gap-audited and closed, 9/9 (2026-09-10, added CSRF/CORS/session security) |
| 13 | [Observability](../13-observability/INDEX.md) | 4/4 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 4/4** — fully L1–L4 (Phase 5, 2026-09-04) |
| 14 | [DevOps & Containers](../14-devops-containers/INDEX.md) | 4/4 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 4/4** — fully L1–L4 (Phase 5, 2026-09-04) |
| 15 | [Cloud](../15-cloud/INDEX.md) | 3/3 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 3/3** — fully L1–L4 (Phase 5, 2026-09-04); 4 chapters as of 2026-09-10 (gap audit added Azure and GCP for Backend Engineers — the domain was AWS-only despite the program's own multi-cloud target companies) |
| 16 | [Performance & JVM Tuning](../16-performance-jvm/INDEX.md) | 3/3 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 3/3** — fully L1–L4 (Phase 5, 2026-09-04) |
| 17 | [Architecture](../17-architecture/INDEX.md) | 9/9 chapters relocated (Phase 3, 2026-09-03); **L1/L2 retrofit complete, 9/9** — fully L1–L4 (Phase 5, 2026-09-04) |
| 18 | [Engineering Practices](../18-engineering-practices/INDEX.md) | 5/5 planned topics present — domain complete and fully L1–L4 (Phase 5, 2026-09-06) |
| 19 | [Leadership & Staff Engineering](../19-leadership-staff/INDEX.md) | 5/5 planned topics written — domain complete (Phase 5, 2026-09-04) |
| 20 | [Interview Preparation](../20-interview-preparation/INDEX.md) | 19/19 chapters relocated (Phase 3, 2026-09-03); `practice/mock-interviews/` referenced, `company-prep/` private and untouched; **L1/L2 retrofit complete, 21/21** — fully L1–L4 (Phase 5, 2026-09-04) |
| 21 | [Frontend & Web (React/Next.js)](../21-frontend-web/INDEX.md) | 32/32 chapters relocated (Phase 3, 2026-09-03); **L1–L4 mastery equivalence formally mapped, 32/32** (Phase 5, 2026-09-05) — domain was exempt from content retrofit, already Beginner–Expert by design |
| 22 | [AI/LLM Engineering](../22-ai-llm-engineering/INDEX.md) | 6/6 originally-planned topics written (2026-09-10), domain remains open — new-writing, L1–L4 from the start |

## What's next

Per `00-project/syllabus-transformation-plan.md` §10, as of 2026-09-05:

- **Phase 0 (Provenance/tooling), Phase 1 (Scaffolding), Phase 2 (Low-risk relocations), and Phase 3 (Domain-by-domain migration)** are complete for every domain that had existing content to migrate — no file relocations remain.
- **Phase 5 (Foundation/Working-Knowledge gap-filling) is complete for all 21 domains, with no known exceptions remaining.** 16 domains were retrofitted with real L1/L2 content underneath their existing Senior/Staff-only chapters (`02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, `11-system-design`, `12-security`, `13-observability`, `14-devops-containers`, `15-cloud`, `16-performance-jvm`, `17-architecture`, `20-interview-preparation`). `21-frontend-web` was exempted from the content retrofit (it already spans Beginner→Expert by original design, per the Scope Addendum) and instead got its own four-tier system formally mapped onto L1–L4. The remaining four domains (`01-computer-science-foundations`, `03-data-structures-algorithms`, `18-engineering-practices`, `19-leadership-staff`) were new-writing domains, written directly to L1–L4 from the start. `18-engineering-practices/git-internals-and-collaboration-workflows.md`, the one chapter left L3/L4-only after its domain's initial 2026-09-03 closure, was retrofitted with real Level 1/2 content on 2026-09-06, closing this gap.
- **Phase 6 (Learning-path assembly) is complete.** All six paths from §6 are real, short documents in [`syllabus/00-overview/learning-paths/`](learning-paths/) — see [Learning Paths](learning-paths.md) for the index.
- **Phase 4 (Cross-linking pass) is complete (2026-09-06).** Every reference across `cheat-sheets/`, `flashcards/`, `production-cookbook/`, `practice/`, `architecture-atlas/`, and `study-packs/` to a pre-migration path (`handbook/<domain>/`, `behavioral-handbook/`, `interview-playbook/{behavioral,coding,system-design,technical-answers}/`) has been rewritten to its real `syllabus/` canonical location — 526 files, verified with zero broken links across 4,091 checked references. See `syllabus/00-overview/changelog.md`'s matching entry for the full account, including a real scoping bug caught mid-pass (not after) and a distinct, pre-existing relative-path defect class fixed along the way in `production-cookbook/`.
- **Phase 7 (Deprecation of old paths) is complete (2026-09-06).** All 16 tracked redirect-stub `.gitkeep` files at old paths (13 remaining `handbook/<domain>/`, 3 remaining `interview-playbook/{coding,system-design,technical-answers}/`) were removed via `git rm`, on the user's explicit go-ahead, after re-verifying zero live navigational references still pointed at any of them anywhere in the repository. `handbook/` and `behavioral-handbook/` no longer exist. `interview-playbook/` still exists, unaffected in substance (`README.md`, `company-prep/`). See `syllabus/00-overview/changelog.md`'s matching entry for the full account, including what was explicitly left out of scope (`CLAUDE.md`/`AGENTS.md`'s stale Repository Structure sections, `resources/repository-tree.md`'s stale generated snapshot — pre-existing documentation debt, not something this phase created).

Every phase of `00-project/syllabus-transformation-plan.md` is now complete. The complementary-deliverable backlog for the Phase 5 new-writing chapters (`01-computer-science-foundations`, `03-data-structures-algorithms`, `18-engineering-practices`, `19-leadership-staff`) is fully resolved: cheat sheets (2026-09-06) and flashcards (2026-09-07) were built new; production-cookbook was investigated (2026-09-07) rather than batch-written, and found to need no new files — every one of the 32 chapters already either cites an existing entry or carries its own honest, documented gap (a "Planned reference" note, not a placeholder awaiting action), consistent with that deliverable's own long-standing "elevate an existing worked scenario, never invent one" rule. See `syllabus/00-overview/changelog.md`'s matching entries.

See `00-project/migration-mapping.md` for the exhaustive, file-by-file mapping this tree was generated from.
