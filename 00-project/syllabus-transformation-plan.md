---
title: "Syllabus Transformation Plan"
document_type: project-planning-document
status: approved — 2026-09-03, all six open questions (§14) resolved; Phase 0 complete; Phase 1 not yet authorized
version: 1.0
last_updated: 2026-09-03
author: Claude (Cowork), commissioned by the repository owner
supersedes: none
depends_on:
  - 00-project/knowledge-architecture-blueprint.md
  - 00-project/blueprint-v1.1-corrections.md
  - 00-project/learning-roadmap.md
  - 00-project/frontend-topic-register.md
  - CLAUDE.md
  - AGENTS.md
  - CONTRIBUTING.md
---

# Syllabus Transformation Plan

## 0. Reading order and what this document is

This document is a **plan, not a migration**. Nothing in the repository has been moved, deleted, or rewritten to produce it. It is the deliverable requested before any of that happens: an audit of what exists, a proposed target architecture, and a mapping from old to new. It requires explicit approval before any execution phase begins.

Read it in order. Section 1 states the strategic shift. Section 2 is the audit — what actually exists today, file by file at the category level. Section 3 is the proposed target architecture. Sections 4–6 define the reusable specifications (topic spec, mastery model, learning paths) that the architecture depends on. Section 7 is the migration mapping. Sections 8–13 cover the mechanics: duplication strategy, naming, phases, risk, validation, and definition of done.

---

## 1. Vision

### 1.1 From: an interview-prep programme

The repository today is a **personal, time-boxed interview-preparation system** for one specific goal: passing Senior Java Backend Engineer and Staff Engineer interviews, on a defined timeline (Plan A/B/C, 6–56 weeks), scored against one person's real interview feedback. Its organizing question is *"what do I need to survive my next interview loop?"*

### 1.2 To: a learning and reference system

The repository becomes a **general-purpose engineering learning system**, structured around a career-long progression:

```
Junior Software Engineer → Mid-Level Engineer → Senior Engineer → Staff Engineer
```

Java backend engineering remains the primary specialization and the deepest track, but the system now also teaches the surrounding disciplines a backend engineer needs across a career: computer science foundations, software design, data structures and algorithms, databases, distributed systems, cloud, DevOps, testing, observability, security, system design, and technical leadership. Its organizing question becomes *"what does someone need to know, at increasing depth, to grow from Junior to Staff — and can they prove it?"*

This is a **superset**, not a replacement. Every piece of interview-specific machinery that exists today — the topic weighting model, the study-pack execution format, the mock-interview rubrics, the STAR-story discipline, the privacy/sanitization rules — survives unchanged. It becomes one *learning path* (`Interview Preparation`) among several that the new syllabus supports, and one *layer* (the "Interview questions" and "Practice" sections) inside the new per-topic document standard, rather than the organizing principle for the whole repository.

### 1.3 What does not change

- No file is deleted or overwritten by this plan.
- `study-packs/`, `handbook/`, `architecture-atlas/`, `practice/`, `flashcards/`, `cheat-sheets/`, `production-cookbook/`, `behavioral-handbook/`, `interview-playbook/`, `templates/`, `resources/`, `00-project/` all continue to exist, at least through the transition window defined in §9.
- The Interview Weight Index (IWI) methodology, the 198-topic Master Topic Register, the Canonical Chapter Template, the privacy/sanitization discipline in `CONTRIBUTING.md`, and the single-canonical-source rule in `CLAUDE.md` are **inherited by the new system**, not discarded. This plan extends them; it does not compete with them.
- Existing depth is never simplified. Where a chapter today is written at Senior/Staff depth only (which is most of `handbook/`, by original design — see §2.4), this plan adds the missing Foundation/Working-Knowledge layers *underneath* it. It does not thin out what is already there.

---

## 2. Audit — what exists today

This audit is based on a full traversal of the git-tracked tree (`git ls-files`, 2,096 tracked files) plus targeted sampling of representative files in every category. `node_modules/`, `target/`, `*.class`, `*.jar` and other build output are excluded throughout — they are gitignored, local, and not source content. `graft/` (435 files: `INDEX.md`, `practice/`, `scripts/`) is also gitignored in its entirety — it reads as a working cache/tooling directory, not curated content, and is treated as out of scope for migration (see §2.9).

### 2.1 Inventory by top-level directory (git-tracked files only)

| Directory | Tracked files | Character | Migration relevance |
|---|---|---|---|
| `practice/` | 1,121 | Runnable Java (485 `.java`), React/Next.js demos (with their own `node_modules`, gitignored), SQL labs, git labs, k8s manifests, ADR examples, mock-interview transcripts | High — becomes the **Practice layer** referenced from canonical topics |
| `study-packs/` | 278 (25 weeks) | Weekly execution bundles: chapter summaries + links, coding drills, STAR workbooks, mock interviews, evaluation rubrics, checklists | High — becomes the source for the **Interview Emergency Sprint** and **Backend Java Specialization** learning paths |
| `handbook/` | 181 | Canonical technical chapters, 13 backend domains + `frontend/` | Highest — becomes the backbone of the **canonical topic library** |
| `cheat-sheets/` | 165 | One condensed cheat sheet per handbook chapter, 1:1 by filename, front-matter-linked to a `canonical:` chapter | High — becomes each topic's **condensed reference layer** |
| `production-cookbook/` | 137 | Scenario-based production-incident write-ups (context → symptoms → impact → diagnosis → fix → prevention), cross-linked to a handbook chapter's Production Scenarios section | High — becomes the **Production scenarios** layer, largely by reference |
| `flashcards/` | 138 | One flashcard deck per handbook chapter, 1:1 by filename | High — becomes each topic's **spaced-repetition layer** |
| `architecture-atlas/` | 18 | Full system-design case studies (17 systems + README), each a 45–60 min timed exercise using the canonical design method | High — becomes **applied System Design case studies**, referenced from `11-system-design/` |
| `behavioral-handbook/` | 16 | STAR/leadership *methodology* chapters (frameworks, not personal stories) | High — becomes the backbone of **Interview Preparation → Behavioral**, with a reclassification question addressed in §2.7 |
| `interview-playbook/` | 12 | Cross-cutting "how to say it" material: technical-answer framework, coding-interview communication protocol, system-design narration, **one company-specific prep guide** | Medium — mostly high value, but contains the repository's one clear **privacy-flagged** file (§2.8) |
| `00-project/` | 7 | Strategic planning: knowledge-base audit, architecture blueprint (198-topic register), corrections, roadmap, frontend register, coverage audit, file-mapping | Foundational input to this very plan — becomes `syllabus/00-overview/` provenance material, not migrated as "content" |
| `archive/` | 6 | Superseded pre-initialization scaffolding, explicitly marked non-authoritative | No migration — stays archived as-is |
| `templates/` | 3 | ADR template, postmortem template | High reuse value — becomes shared templates referenced from the Topic Specification and from `18-engineering-practices/` |
| `scripts/` | 3 | `validate.py`, `check_postmortem_blameless.py`, `check_adr_completeness.py` | Reused and extended, not migrated as content |
| `resources/` | 2 | Source bibliography, `repository-tree.md` | Low — folded into `syllabus/00-overview/` |
| root `.md` files | 5 | `README.md`, `CLAUDE.md`, `AGENTS.md`, `CONTRIBUTING.md`, `CHANGELOG.md` | Rewritten in Phase 0 of migration (§9) to describe the new vision; content preserved, framing updated |

**Total: 1,086 tracked Markdown files** across the categories above (excluding code/config files), representing **~2.9M words** of markdown content by raw word count across all categories combined (the `practice/` figure includes code comments and READMEs, not prose).

### 2.2 The existing topic register is the strongest asset in the repository

`00-project/knowledge-architecture-blueprint.md` already contains a **Master Topic Register**: 198 topics, organized into 16 domains (`D1`–`D16`), each with a stable ID (`T-101`, `T-609`, `T-1504`, …), a tier (`FDN`/`COR`/`ADV`/`STF`/`EXP`), an Interview Weight Index, and a gap status. `00-project/frontend-topic-register.md` does the same for React/Next.js (`F-101`…), spanning Beginner→Expert.

This register is not being discarded or re-derived. It is the **primary input to the new canonical topic list** (§7.1). Its domain structure (`D1 Java Core` … `D16 Interview Craft`) maps almost directly onto the proposed `syllabus/` taxonomy, and its topic IDs (`T-201` for HashMap, etc.) already appear as `topic_id:` front matter in most cheat sheets and flashcards — meaning **the migration mapping can be built programmatically from this ID rather than hand-matched file by file** (see §7.2).

### 2.3 Editorial and structural discipline is already high

Nearly every file sampled across `handbook/`, `cheat-sheets/`, `flashcards/`, `architecture-atlas/`, `production-cookbook/`, and `behavioral-handbook/` carries consistent YAML front matter (`title`, `slug`, `document_type`, `domain`, `status`, `difficulty`, `target_levels`, `prerequisites`, `related`, `official_references`), uses relative cross-links rather than duplicating content, and follows the **Canonical Chapter Template** defined in `CLAUDE.md` (Table of Contents → Learning Objectives → Mental Model → Core Concepts → Internal Implementation → Production Scenarios → Trade-offs → Interview Answer Framework → Interview Questions → Cheat Sheet → Flashcards → Practice Exercises). This is unusual and valuable: it means the repository is **already halfway to the Topic Specification this plan proposes** (§5) rather than needing to be built from an unstructured pile of notes.

The one-cheat-sheet-per-chapter and one-flashcard-deck-per-chapter pattern, in particular, is exactly the "single canonical explanation, several concise contextual references" principle `CLAUDE.md` already mandates (`Avoiding Duplication`, line 2249) — so the biggest de-duplication risk a project like this usually carries (three different files independently re-explaining HashMap) does not appear to exist here. This was spot-checked, not exhaustively verified — full verification is a migration-phase activity (§10).

### 2.4 The central gap: almost everything is written for Senior/Staff, nothing for Junior/Mid

This is the most important finding of the audit, and it is a direct, structural gap relative to the new vision — not a quality problem.

`CLAUDE.md`'s own **"Depth by Interview Level"** section (line 2179) defines five levels (Junior → Mid → Senior → Staff → Principal) but then states: *"Most canonical content should target Senior and Staff depth."* This was a deliberate, correct choice for the original goal (the reader is defined as having 5+ years of experience). It means that, spot-checked against `handbook/collections/hashmap-internals.md` as a representative example: the chapter has **Core Concepts, Internal Implementation, Production Scenarios, Trade-offs, and a Staff-Level Discussion** — but no section that answers "what is a HashMap and when would I reach for one," "what is the Collections Framework and why does it exist," or "what's the difference between `List`, `Set`, and `Map` and when do I pick each." A Junior or Mid-level reader cannot enter this chapter; a Senior reader can start reading on line one.

This pattern repeats across essentially all 181 handbook chapters. It is exactly the gap the user's own worked example (§ Foundation → Practical → Implementation depth → Senior depth → Staff/system depth, for HashMap) identifies. **The fix is additive** (§5, §7.4): every canonical topic gains an explicit `Level 1 — Foundation` and `Level 2 — Working Knowledge` section at the top, the existing content becomes `Level 3 — Senior Depth`, and the existing Staff-Level Discussion becomes `Level 4 — Staff/System Depth`. No existing sentence needs to be deleted or reworded to do this.

The frontend domain is the exception that proves the rule: `00-project/frontend-topic-register.md` already spans `BEG`/`INT`/`ADV`/`EXP` tiers by design (Scope Addendum, `CLAUDE.md`), because the user explicitly asked for beginner-through-expert coverage there. Its topic IDs (`F-101` "JSX, elements vs components, the virtual DOM idea," tier `BEG`) are the closest existing precedent inside this repository for what Foundation-level material for the rest of the syllabus should look like.

### 2.5 A second structural gap: no canonical home for Data Structures & Algorithms, and none for CS foundations

Cross-referencing the Master Topic Register against `handbook/`: **Domain D14 — Algorithms & Coding (19 topics, T-1401–T-1419: complexity analysis, arrays/two-pointers, hashing patterns, binary search, linked lists, stacks, heaps, trees, graphs, backtracking, dynamic programming, intervals, greedy, bit manipulation, tries, design-style problems, concurrency problems, advanced structures) has no corresponding `handbook/` directory at all.** This material exists only as runnable code under `practice/java/{collections,advanced-structures,...}` and `practice/java/week-*/`, referenced piecemeal from study-pack chapters, with no canonical prose chapter teaching the concept, its trade-offs, or its production relevance the way `handbook/collections/hashmap-internals.md` does for HashMap.

Similarly: **there is no content anywhere in the repository covering computer science fundamentals** — how a computer actually executes a program, number representation, the OS process/thread model at a level below Java's abstraction of it, networking basics (TCP/IP, HTTP mechanics below the Spring MVC layer), or algorithmic complexity taught from first principles rather than assumed. This is a hard requirement for the new "Junior" entry point of the syllabus and did not exist in the original interview-prep scope, because a Senior/Staff audience is assumed to already have it.

Both gaps are large, both are new writing (not migration), and both are flagged explicitly in the migration mapping (§7) as **new canonical domains with a mostly-empty starting inventory**, not areas where existing content is being reorganized.

### 2.6 A third gap: some canonical content is mis-homed by today's own domain boundaries

Two clear examples surfaced during sampling:

- `handbook/cloud/git-internals-and-collaboration-workflows.md` (plus its paired cheat sheet and flashcard deck) is filed under **Cloud**, but its content — Git's object model, merge vs. rebase, reflog/bisect — is an **Engineering Practices** topic, not a cloud-infrastructure one. `practice/git/` (object-model, merge-vs-rebase, reflog-and-bisect) is a parallel, correctly-scoped practice track for the same material.
- `handbook/architecture/design-patterns-applied.md` sits inside **Architecture** (system-level: DDD, hexagonal, CQRS, event sourcing), but Gang-of-Four design patterns are class/module-level design, not system architecture — a different altitude of decision-making that the user's own proposed taxonomy separates out as `04-software-design`.

These are small, single-file relocations — cited here because they are representative of the kind of boundary correction the audit surfaces, not because they are individually significant. §7 catalogs the full set found so far; more will surface during per-domain migration (§9).

### 2.7 A classification question, not a defect: Behavioral/Leadership content is interview-shaped by design

`behavioral-handbook/` (16 files) already reads as high-quality *methodology* — STAR mechanics, story-portfolio design, scope/impact framing, conflict narratives, mentoring, cross-team influence, design reviews/RFCs, technical-debt advocacy — none of it is personal story content (verified by sampling; see also §2.8 for what *is* personal). But every chapter is framed as "how to turn this into an interview answer," which is exactly right for `20-interview-preparation/behavioral/` and slightly narrower than what a standalone `19-leadership-staff` knowledge domain needs (e.g., "how to write an RFC" as a working engineering skill, independent of whether it will ever be an interview story).

**Recommendation (open for approval in this plan, not yet acted on):** `behavioral-handbook/` becomes the canonical, unduplicated home for `20-interview-preparation/behavioral/` as-is. `19-leadership-staff` is treated as a genuinely new domain that *references* the relevant behavioral-handbook chapters (e.g., its RFC/design-review chapter, its mentoring chapter) as source material for a new Level-1–4 "Technical Leadership" topic ladder, rather than either duplicating that content or leaving `19-leadership-staff` structurally empty. This keeps the single-canonical-source rule intact while giving both destinations real content.

### 2.8 Privacy finding: one file requires handling before any public/commercial use

`interview-playbook/company-prep/nordstrom-senior-backend-remote.md` names a real employer and role and is written as live, in-progress interview preparation ("Auditoría de los 8 temas que pediste (2026-09-03)"). This is exactly the kind of content `CONTRIBUTING.md`'s own privacy section (§ Never commit → interviewer-identifying detail; § Local-only escape hatch) anticipates, and the repository's own discipline already has a mechanism for it (`*.private.md` / `local/`, both gitignored).

No other personal identifiers, employer names, or client references were found in a repository-wide grep sample. This appears to be an isolated instance, not a systemic problem — the repository's sanitization discipline (per `CONTRIBUTING.md`) is otherwise being followed.

**This plan does not move or alter this file.** It flags it for the user's own decision in §11 (Risks) and proposes, for approval, that `interview-playbook/company-prep/` become a permanently `.private`/local-only category in the new architecture — excluded by construction from anything that becomes public or commercial material, the same way STAR-story drafts already are.

### 2.9 Explicitly out of scope for migration

- **`graft/`** (435 files, gitignored) — reads as a scratch/tooling working area (`INDEX.md`, `scripts/`, its own `practice/`). Not migrated; not deleted. If it in fact contains material the user wants preserved, that should be stated explicitly before Phase 1, since this audit treated its gitignored status as a signal it is disposable tooling, not curated content.
- **`archive/pre-initialization-scaffolding/`** — already explicitly marked, in `CHANGELOG.md`, as unverified/superseded content kept only for provenance. Stays archived.
- **Build artifacts, `node_modules/`, `.class`/`.jar` files** — not content, gitignored, not part of any inventory count in this document.

---

## 3. Proposed target architecture

### 3.1 Evaluating the user's proposed structure

The starting-point structure offered in the brief is a strong first draft and is **adopted with modifications**, justified below against the audit in §2. Three changes from the original 20-domain proposal:

1. **`21-frontend-web` is added.** The original 20-domain list omits frontend entirely, but the repository already has a substantial, deliberately-scoped React/Next.js domain (`handbook/frontend/`, 25 chapters; `practice/frontend/`; `interview-playbook/frontend/`) that `CLAUDE.md` explicitly treats as "additive, not merged" into the Java backend track. Dropping it would be a silent content loss, which the user explicitly ruled out. It is added as domain 21, kept structurally separate (its own topic register, its own learning path variant) exactly as it is today.
2. **`19-leadership-staff` and `20-interview-preparation` are kept as two domains, not collapsed into one, per the reasoning in §2.7** — one is engineering-practice knowledge, the other is interview-application craft — but they are explicitly cross-linked rather than duplicated.
3. **Numbering is treated as ordering guidance, not a hard dependency chain.** A few domains (e.g., `07-api-design`, `13-observability`) pull content out of a domain that appears later in the list (`11-system-design`, `16-performance-jvm`) because the *pedagogical* order and the *current filing location* aren't always the same thing. This is intentional and noted per-domain below.

### 3.2 The proposed taxonomy

```
syllabus/
├── 00-overview/                        Vision, taxonomy, mastery model, learning paths, changelog
├── 01-computer-science-foundations/    NEW — how computers work, OS/process model, networking, complexity theory
├── 02-java/
│   ├── language-core/                  handbook/java-core/ (15 topics)
│   ├── collections/                    handbook/collections/ (9 topics)
│   ├── jvm-internals/                  handbook/jvm/ (12 topics)
│   └── concurrency/                    handbook/concurrency/ (16 topics)
├── 03-data-structures-algorithms/      NEW canonical prose — practice/java/{collections,advanced-structures,...} as Practice layer
├── 04-software-design/                 handbook/architecture/design-patterns-applied.md + practice/java/{oop-fundamentals,design-patterns}
├── 05-spring/                          handbook/spring/ (9 topics)
├── 06-databases/                       handbook/databases/ (14 topics)
├── 07-api-design/                      handbook/system-design/api-design.md, api-gateway-bff-and-edge-concerns.md
├── 08-testing/                         handbook/testing/ (7 topics)
├── 09-messaging-event-driven/          handbook/kafka/ (6 topics) + architecture's event-driven/event-sourcing/CDC chapters
├── 10-distributed-systems/             handbook/system-design's CAP, consistent hashing, failure modes, distributed transactions
├── 11-system-design/                   handbook/system-design's design method + architecture-atlas/ (17 case studies)
├── 12-security/                        handbook/security/ (7 topics)
├── 13-observability/                   handbook/performance's logging/tracing/SLO/incident-response chapters
├── 14-devops-containers/               handbook/cloud's kubernetes/container/CI-CD chapters
├── 15-cloud/                           handbook/cloud's AWS/cost-economics/12-factor chapters
├── 16-performance-jvm/                 handbook/jvm's profiling/benchmarking + handbook/performance's capacity-planning
├── 17-architecture/                    handbook/architecture/ minus design-patterns (→ 04)
├── 18-engineering-practices/           NEW canonical home — git internals (relocated from cloud), ADRs, code review, technical writing, templates/
├── 19-leadership-staff/                NEW — technical-leadership knowledge ladder, referencing behavioral-handbook source material
├── 20-interview-preparation/
│   ├── behavioral/                     behavioral-handbook/ (as-is, canonical)
│   ├── coding/                         interview-playbook/coding/ + D14's interview-question layer
│   ├── system-design/                  interview-playbook/system-design/
│   ├── technical-answers/              interview-playbook/technical-answers/
│   ├── mock-interviews/                practice/mock-interviews/
│   └── company-prep/  [PRIVATE]        interview-playbook/company-prep/ — excluded from public/commercial material, see §2.8
└── 21-frontend-web/                    handbook/frontend/, practice/frontend/, interview-playbook/frontend/, 00-project/frontend-topic-register.md
```

Cross-cutting, non-domain resources continue to live at repository root or in shared directories, referenced from every domain rather than duplicated per-domain:

- `study-packs/` — retained as-is; becomes the source for two learning paths (§6) rather than being absorbed into `syllabus/`.
- `flashcards/`, `cheat-sheets/`, `production-cookbook/` — retained as-is; each canonical topic's front matter points to its existing companion files in these directories (no file moves required for this layer — see §7.4).
- `templates/`, `resources/`, `scripts/` — retained, extended.

### 3.3 Why domain boundaries were drawn where they were

A few boundaries are non-obvious and are justified explicitly so they can be challenged during approval rather than silently accepted:

- **`10-distributed-systems` vs. `11-system-design`.** These are frequently taught as one thing. They are split here because the existing content already separates them by *nature*, not just by file: `10` becomes the theory (CAP/PACELC, consistent hashing, replication, distributed transactions, failure modes — "what is true about distributed systems"), while `11` becomes the applied method and case studies (the six-phase design method, the 17 Architecture Atlas systems — "how to design one, live, in 45 minutes"). A reader can study `10` without ever doing a mock system-design interview; `11` is unusable without `10` as a prerequisite. Keeping them separate also matches how `architecture-atlas/` entries already declare their prerequisites in front matter (sampled: `distributed-cache.md` lists `system-design-method-and-estimation.md`, `data-partitioning-and-consistent-hashing.md`, and `resilience-patterns.md` as prerequisites — i.e., the existing content already assumes this split).
- **`13-observability` vs. `16-performance-jvm`.** Today both live under `handbook/performance/`. They're split because "how do I know something is wrong in production" (logging, tracing, SLOs, incident response) is a different skill, and a different Staff-level conversation, than "how do I make the JVM fast" (profiling, GC tuning, JMH). The user's own proposed taxonomy makes this same split; the audit confirms the underlying content already separates cleanly along this line.
- **`14-devops-containers` vs. `15-cloud`.** Today both live under `handbook/cloud/`. Split because Kubernetes/containers/CI-CD is operational mechanics a backend engineer runs day-to-day, while AWS service selection and cloud cost economics is a more architectural, less daily-operational concern — again matching the user's proposed split, and cleanly separable in the existing file set (`kubernetes-*.md`, `container-image-internals.md`, `cicd-*.md` vs. `aws-core-services-*.md`, `cloud-cost-and-scaling-economics.md`, `twelve-factor-config.md`).
- **`07-api-design` as its own domain.** Today `api-design.md` lives inside `handbook/system-design/`. Pulling it out matches the user's explicit request and is defensible on content grounds too: REST/gRPC/GraphQL design, versioning, and pagination is foundational-through-senior knowledge every backend engineer needs regardless of whether they ever do a "design Twitter" interview, whereas the rest of `system-design/` is more Staff-oriented, interview-shaped, and prerequisite-heavy.

---

## 4. Topic Specification

### 4.1 Relationship to the existing Canonical Chapter Template

`CLAUDE.md` already defines a Canonical Chapter Template (44 sections, reproduced in full at `CLAUDE.md:1299`). The Topic Specification below is **that template, restructured around explicit mastery levels** rather than an implicit "assume Senior/Staff" depth, plus a small number of additions the audit showed were missing (explicit Prerequisites as their own section rather than only front matter; an explicit Mastery Checklist; explicit Debugging and Design exercise categories rather than one undifferentiated "Practice Exercises"). Every existing chapter section maps onto exactly one part of this spec — nothing is orphaned.

### 4.2 The standard sections (in order)

| # | Section | Existing equivalent | Notes |
|---|---|---|---|
| 1 | **Why this matters** | "Why This Matters in Interviews" | Broadened: why it matters for building systems, not only for interviews |
| 2 | **Prerequisites** | Front-matter `prerequisites:` list | Promoted to a visible section with one-line reasons, not just links |
| 3 | **Foundation (L1)** | *Mostly absent — the core gap, see §2.4* | What it is, why it exists, when you'd reach for it, in plain language |
| 4 | **Core concepts (L2)** | "Core Concepts" | The practical vocabulary and comparisons a working engineer needs |
| 5 | **How it works internally (L3)** | "Internal Implementation," "Execution Flow" | Existing Senior-depth content mostly lands here unchanged |
| 6 | **Practical usage** | Scattered across "Java Examples," "Comparisons" | Runnable code, drawn from `practice/` |
| 7 | **Examples** | "Java Examples" | |
| 8 | **Common mistakes** | "Common Mistakes" | |
| 9 | **Edge cases** | *Often folded into "Common Mistakes" today* | Split out where it exists |
| 10 | **Performance implications** | "Performance Implications" | |
| 11 | **Trade-offs** | "Trade-offs," "Decision Framework" | |
| 12 | **Senior-level considerations (L3)** | "Senior-Level Expectations," "Concurrency/Security/Memory Implications" | |
| 13 | **Staff/system-level considerations (L4)** | "Staff-Level Discussion" | |
| 14 | **Production scenarios** | "Production Scenarios" | Linked from `production-cookbook/`, not duplicated |
| 15 | **Interview questions** | "Interview Questions," "Interview Answer Framework" | Unchanged; this is already strong |
| 16 | **Coding/practice exercises** | "Practice Exercises" | Linked to `practice/` |
| 17 | **Debugging exercises** | *New, split out of Practice Exercises* | Given production-cookbook's incident-diagnosis format is already this exact exercise type, most topics can source this directly |
| 18 | **Design exercises** | *New, split out; overlaps `architecture-atlas/` for system-scale topics* | |
| 19 | **Further reading** | "Additional Reading," "Official References" | |
| 20 | **Mastery checklist** | *New — see §5* | Verifiable per-level criteria, not "read this chapter" |

A topic may omit a section with a one-line justification in an HTML comment (`<!-- Edge cases: N/A — this is a design methodology, not an API -->`), matching the existing rule that a section may be dropped when it doesn't apply.

### 4.3 Front matter (extends the existing schema)

```yaml
---
title: <Topic Title>
slug: <kebab-case-slug>
document_type: syllabus-topic
domain: <e.g. 02-java/collections>
topic_id: <stable ID, e.g. T-201 — reused from the Master Topic Register where one exists>
status: draft | reviewed | canonical
version: 1.0
last_updated: YYYY-MM-DD
mastery_levels_covered: [L1, L2, L3, L4]   # which levels this document currently has written
prerequisites:
  - <topic_id or relative link>
related:
  - <topic_id or relative link>
practice: <relative link into practice/>
cheat_sheet: <relative link into cheat-sheets/>
flashcards: <relative link into flashcards/>
production_scenarios:
  - <relative link(s) into production-cookbook/>
interview_paths: [interview-emergency-sprint, senior-to-staff]   # which learning paths reference this topic
source_history:
  - <original path this content was migrated from, for traceability — required whenever content moves>
official_references: []
---
```

The `source_history` field is the mechanism that satisfies the "conserva el historial y la trazabilidad" requirement at the document level, independent of and in addition to `git mv` preserving file history at the VCS level (§9.4).

---

## 5. Mastery model

### 5.1 The four levels

| Level | Name | What it verifies | Example criterion (HashMap, `T-201`) |
|---|---|---|---|
| **L1** | Foundation | Can explain what it is, why it exists, and when to reach for it | "Can state what a HashMap is, name its three or four practical use cases, and explain in one sentence why it's usually faster than a list for lookups — without being asked about internals." |
| **L2** | Practitioner | Can use it correctly and compare it to its alternatives | "Can explain the `equals`/`hashCode` contract, name what breaks when it's violated, state HashMap's average-case complexity, and choose correctly between HashMap/LinkedHashMap/TreeMap for three given scenarios." |
| **L3** | Senior | Can explain internals, reason about performance, and debug it in production | "Can explain bucket resizing, load factor, treeification, and walk through diagnosing a real lookup-latency regression caused by poor hash distribution, using only symptoms as the starting point." |
| **L4** | Staff | Can reason about systemic consequences and defend an architectural decision | "Can explain when an in-memory HashMap-based cache stops being sufficient for a given throughput/consistency requirement, and can defend the trade-off of moving to a distributed cache (or not) to a skeptical peer." |

### 5.2 Verification, not reading

Per the user's explicit requirement, **"read this chapter" is never a criterion.** Every level's criteria must be demonstrable in one of these forms, and every topic's Mastery Checklist section must use at least one:

1. **Explain-it-cold** — answer a specific interview-style question without notes (the existing Interview Questions section already supplies raw material for this).
2. **Apply-it** — solve a coding/practice exercise correctly (existing `practice/` code, once linked, already supplies this for most Java/DSA topics).
3. **Debug-it** — diagnose a described symptom to its root cause (existing `production-cookbook/` entries already supply this almost verbatim for the topics that have one).
4. **Compare-it** — correctly choose between named alternatives for a given scenario, with a stated reason.
5. **Defend-it** — produce a short written or spoken justification for a decision under a stated constraint (existing ADR template and `interview-playbook/technical-answers/trade-off-narration-and-adrs.md` already supply the mechanics for this).

A topic's Mastery Checklist is a short table, e.g.:

```markdown
## Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | State what a HashMap is and give 2 real use cases | [Interview Q1](#question-1) |
| L2 | Explain equals/hashCode contract; choose HashMap vs TreeMap vs LinkedHashMap for 3 scenarios | [Practice Exercise 2](../../practice/java/collections/) |
| L3 | Explain resizing/treeification; diagnose the latency scenario in Production Scenarios | [Debugging Exercise](../../production-cookbook/...) |
| L4 | Defend when to move from in-memory HashMap to a distributed cache | [Design Exercise](#design-exercises) |
```

### 5.3 Why this is not four separate documents

This directly implements the user's "one TOPIC, not four seniority-versioned copies" requirement. A Junior reader opens the same file as a Staff reader; the Junior reader stops after L1/L2, the Staff reader skims L1/L2 (already known) and spends their time in L3/L4. The Table of Contents (already a mandatory section in the existing template) becomes the navigation mechanism — each level is its own set of headings, so a reader can jump straight to `## Level 3 — Senior Depth` without reading L1/L2 first.

---

## 6. Learning paths

Learning paths are **curated sequences of existing canonical topic references** — never copies of topic content. A path document is short: an ordered list of topic links, a stated time budget, and per-topic guidance on *which mastery level to stop at* for this path's purpose.

| Path | Audience / goal | Primary source material | Stops at level |
|---|---|---|---|
| **Junior → Mid** | New engineer building working competence | New CS-foundations + DSA content, `02-java` L1–L2, `06-databases` L1–L2, `08-testing` L1–L2 | L2 |
| **Mid → Senior** | Working engineer building depth and production judgment | `02-java`–`17-architecture` domains, L2–L3, with production-cookbook debugging exercises | L3 |
| **Senior interview refresh** | Experienced engineer with an upcoming interview, needs recall not new learning | Existing `flashcards/`, `cheat-sheets/`, D1–D13 topics at L3, `20-interview-preparation` | L3 (review only) |
| **Senior → Staff** | Senior engineer building systemic/organizational judgment | `10-distributed-systems`, `11-system-design`, `17-architecture`, `19-leadership-staff`, all domains' L4 sections | L4 |
| **Interview emergency sprint** | ≤8 weeks to a known interview loop | **This is `study-packs/` Plan A, referenced wholesale, unchanged** — the existing 6-week, IWI-ranked, feedback-driven sprint | L3, selectively L4 |
| **Backend Java specialization** | Deep-dive into the primary track specifically | `02-java`, `05-spring`, `06-databases`, `09-messaging-event-driven`, `16-performance-jvm`, all levels | L4 |

Two of these six paths (**Interview emergency sprint**, and much of **Senior interview refresh**) are not new — they are `study-packs/` and the existing `interview-playbook/`/`flashcards/`/`cheat-sheets/` layer, given a name and a place in the new taxonomy's navigation, with zero content migration required. This is deliberate: it demonstrates the "same canonical topics, different route through them" principle using material that already exists, rather than asking the reader to trust an untested new mechanism.

---

## 7. Migration mapping

### 7.1 Methodology

A literal file-by-file table for 1,086 tracked Markdown files would be too large to hand-author reliably and would go stale the moment a file is touched. Instead, this plan defines the mapping at two levels, and proposes the mechanism to make it exhaustive and machine-checkable before any file is moved:

1. **Domain-level mapping** (§7.3) — every existing top-level content directory mapped to its new canonical domain(s), with the reasoning already given in §3.3.
2. **Topic-ID-level mapping** (§7.2) — since `cheat-sheets/`, `flashcards/`, and most of `handbook/` already carry a `topic_id:` (`T-xxx`/`F-xxx`) in front matter that corresponds to a row in the Master Topic Register, **a script can generate the complete, exhaustive file-to-domain mapping automatically** by joining on that ID, rather than this document asserting it by hand. This is proposed as the first concrete task of Migration Phase 1 (§9), producing `00-project/migration-mapping.csv` (or `.md` table) as a reviewable artifact *before* any file is touched — giving the user a second, more granular approval checkpoint beyond this document.

### 7.2 Sample of the generated mapping (illustrative, not exhaustive)

| Existing path | Topic ID | New canonical domain | Category |
|---|---|---|---|
| `handbook/collections/hashmap-internals.md` | T-201 | `02-java/collections/hashmap-internals.md` | canonical topic |
| `cheat-sheets/hashmap-internals.md` | T-201 | referenced from same topic's front matter | supporting resource |
| `flashcards/hashmap-internals.md` | T-201 | referenced from same topic's front matter | supporting resource |
| `handbook/architecture/design-patterns-applied.md` | T-914 | `04-software-design/design-patterns-applied.md` | canonical topic (relocated domain) |
| `handbook/cloud/git-internals-and-collaboration-workflows.md` | — | `18-engineering-practices/git-internals.md` | canonical topic (relocated domain) |
| `handbook/system-design/api-design.md` | T-803 | `07-api-design/api-design.md` | canonical topic (relocated domain) |
| `handbook/performance/logging-metrics-tracing-and-opentelemetry.md` | T-1205 | `13-observability/logging-metrics-tracing-opentelemetry.md` | canonical topic (relocated domain) |
| `architecture-atlas/distributed-cache.md` | — (references T-801, T-806, T-515) | `11-system-design/case-studies/distributed-cache.md` | architecture case study |
| `production-cookbook/cache-cluster-failover-...md` | — (linked from T-804) | stays in `production-cookbook/`, referenced from `11-system-design`'s caching topic | supporting resource, unmoved |
| `behavioral-handbook/02-story-portfolio-design.md` | T-1502 | `20-interview-preparation/behavioral/02-story-portfolio-design.md` | canonical topic (interview-application framing, per §2.7) |
| `interview-playbook/company-prep/nordstrom-senior-backend-remote.md` | — | `20-interview-preparation/company-prep/` **[private, excluded from public build]** | private — flagged, not migrated by default |
| `00-project/knowledge-architecture-blueprint.md` | — (the register itself) | `00-project/` (unchanged) + primary input to `00-overview/topic-register.md` | supporting resource / provenance |
| `practice/java/week-01/src/LRUCacheFixed.java` | T-1416 (design-style coding) | `practice/` unchanged path, referenced from `03-data-structures-algorithms/design-style-problems.md` (new) | practice |
| `study-packs/week-01/` (entire pack) | multiple | unchanged path, referenced wholesale from the **Interview emergency sprint** learning path | learning-path source, unmoved |
| `archive/pre-initialization-scaffolding/*` | — | unchanged, stays archived | archive |
| `graft/*` | — | out of scope (§2.9) | excluded pending user decision |

### 7.3 Domain-level mapping (complete)

| New domain | Primary existing sources | Status |
|---|---|---|
| `00-overview` | `00-project/*`, `resources/repository-tree.md`, `README.md` | Mostly new writing, old docs referenced as provenance |
| `01-computer-science-foundations` | *none* | **New domain — no existing content** |
| `02-java` | `handbook/{java-core,collections,jvm,concurrency}/` (52 chapters) | Reclassify domain path; add L1/L2 |
| `03-data-structures-algorithms` | `practice/java/{collections,advanced-structures}/`, scattered study-pack coding sections | **Mostly new canonical prose — practice code is real, teaching layer is a gap** |
| `04-software-design` | `handbook/architecture/design-patterns-applied.md`, `practice/java/{oop-fundamentals,design-patterns}/` | Reclassify domain path; add L1/L2 |
| `05-spring` | `handbook/spring/` (9 chapters) | Reclassify domain path; add L1/L2 |
| `06-databases` | `handbook/databases/` (14 chapters) | Reclassify domain path; add L1/L2 |
| `07-api-design` | `handbook/system-design/{api-design,api-gateway-bff-and-edge-concerns}.md` | Split out of `system-design`; add L1/L2 |
| `08-testing` | `handbook/testing/` (7 chapters) | Reclassify domain path; add L1/L2 |
| `09-messaging-event-driven` | `handbook/kafka/` (6), + `handbook/architecture/{event-driven-architecture-integration-styles,event-sourcing-and-its-real-costs}.md`, `handbook/system-design/messaging-patterns-and-change-data-capture.md` | Consolidate three current homes; add L1/L2 |
| `10-distributed-systems` | `handbook/system-design/{cap-theorem-...,data-partitioning-...,distributed-transactions-...,multi-region-...}.md`, `handbook/architecture/distributed-systems-failure-modes.md` | Split out of `system-design`; add L1/L2 |
| `11-system-design` | `handbook/system-design/system-design-method-and-estimation.md` + remaining system-design chapters, `architecture-atlas/` (17 case studies) | Reclassify; case studies become a `case-studies/` subfolder |
| `12-security` | `handbook/security/` (7 chapters) | Reclassify domain path; add L1/L2 |
| `13-observability` | `handbook/performance/{logging-metrics-tracing-and-opentelemetry,performance-methodology-and-slo-error-budgets,incident-response-and-blameless-postmortems}.md` | Split out of `performance`; add L1/L2 |
| `14-devops-containers` | `handbook/cloud/{kubernetes-*,container-image-internals,cicd-*}.md` | Split out of `cloud`; add L1/L2 |
| `15-cloud` | `handbook/cloud/{aws-core-services-...,cloud-cost-and-scaling-economics,twelve-factor-config...}.md` | Remainder of `cloud`; add L1/L2 |
| `16-performance-jvm` | `handbook/jvm/{benchmarking-...,profiling-...}.md`, `handbook/performance/capacity-planning-and-headroom.md` | Consolidate two current homes; add L1/L2 |
| `17-architecture` | `handbook/architecture/` minus `design-patterns-applied.md` (11 chapters) | Reclassify domain path; add L1/L2 |
| `18-engineering-practices` | `handbook/cloud/git-internals-and-collaboration-workflows.md`, `practice/git/`, `templates/`, `interview-playbook/technical-answers/trade-off-narration-and-adrs.md`, `scripts/check_adr_completeness.py` | Consolidate from 4 current homes; mostly-new writing for code-review/technical-writing sub-topics |
| `19-leadership-staff` | *References* `behavioral-handbook/{07,09,10,11,12}` as source material (§2.7) | **New domain, references not duplicates existing chapters** |
| `20-interview-preparation` | `interview-playbook/`, `behavioral-handbook/`, `practice/mock-interviews/`, `flashcards/`, `cheat-sheets/` | Reclassify; wraps existing interview-specific layer largely unchanged |
| `21-frontend-web` | `handbook/frontend/` (25), `practice/frontend/`, `interview-playbook/frontend/`, `00-project/frontend-topic-register.md` | Reclassify domain path only; already spans BEG→EXP |

### 7.4 What actually moves vs. what stays and gets referenced

To minimize churn and risk, this plan proposes that **not every category physically relocates**:

- **Physically relocated** (via `git mv`, preserving history — §9.4): `handbook/*` chapters, since they become the canonical topic files themselves and their location *is* the taxonomy.
- **Stays in place, referenced by link/front-matter, never duplicated:** `cheat-sheets/`, `flashcards/`, `production-cookbook/`, `practice/`, `study-packs/`, `architecture-atlas/` case-study bodies (though a thin index file in `11-system-design/case-studies/` may link to each), `templates/`, `archive/`. These directories are already well-organized flat namespaces; moving 137 production-cookbook entries or 1,121 practice files into 21 nested domain folders would multiply the risk of broken links for no organizational benefit, since they're already discoverable by filename-matching their canonical topic.

### 7.5 Duplicates identified

Two areas of possible duplication were surfaced and require a migration-phase decision, not a decision this document makes unilaterally:

1. **`practice/java/{collections,concurrency,jvm,...}/` vs. `practice/java/week-XX/`.** Both exist; sampling (`practice/java/collections/`, 9 files) suggests the domain-indexed folders may be a topic-organized *duplicate or superset* of code that also appears inside the chronological week folders the study packs reference, rather than genuinely distinct material. This needs a file-level diff pass before Phase 3 (Practice-layer linking) to determine whether one is canonical and the other should become a redirect, or whether they are legitimately complementary (e.g., domain folders hold extended/polished versions of week-folder drafts).
2. **`behavioral-handbook/` vs. `interview-playbook/behavioral/`.** Only one file exists in the latter today (`company-loop-structures-and-question-pattern-recognition.md`), so this is low-risk, but the two directories serve overlapping purposes and should be consolidated under `20-interview-preparation/behavioral/` as part of Phase 1 rather than left split.

### 7.6 Gaps identified (new writing required, not migration)

In priority order, based on how foundational they are to the new Junior entry point:

1. **`01-computer-science-foundations`** — entirely new. No existing content.
2. **`03-data-structures-algorithms`** — canonical prose layer entirely new; practice code exists and is reusable as-is.
3. **L1/Foundation and L2/Working-Knowledge sections across all ~181 existing handbook chapters** — the single largest body of new writing this plan implies, but it is additive per-chapter work, not new-topic work (§2.4).
4. **`18-engineering-practices`** beyond its git-internals seed — code review, technical writing standards, working with legacy code, refactoring discipline: all currently absent.
5. **`19-leadership-staff`** as a standalone knowledge ladder (distinct from its interview-application counterpart) — currently has reference material but no dedicated canonical chapters of its own.

### 7.7 Content already at or near canonical quality, minimal rework needed

Per §2.3, the majority of `handbook/` (L3/L4 layers), all of `architecture-atlas/`, all of `production-cookbook/`, and `behavioral-handbook/` are assessed as ready to become canonical material with a domain-path change and front-matter update only — no rewriting of existing prose. This is the majority of the repository's word count.

---

## 8. Duplication strategy

Extending the existing rule in `CLAUDE.md` (`Avoiding Duplication`, line 2249) rather than replacing it:

- **One canonical explanation per concept**, identified by its `topic_id`. Every other document (cheat sheet, flashcard, study-pack summary, learning-path reference, mock-interview prep note) links to it and adds only what that context needs that the canonical chapter doesn't already say.
- **A learning path never repeats topic content** — it is a sequenced list of links plus a stated stopping mastery-level per topic, per §6.
- **Study packs continue their existing pattern**: where a study-pack file already says "T-901 — summary + link; full chapter now canonical at `handbook/architecture/...`" (verified in `study-packs/week-01/README.md`), that pattern is preserved, with the link target updated to the new canonical path.
- **When two existing files cover the same ground** (§7.5), the resolution is: keep the deeper/more current one as canonical, and turn the other into either (a) a redirect stub pointing to the canonical file, retained for one migration cycle, or (b) if it's genuinely complementary (e.g., a shorter *practice* companion to a longer *canonical* explanation), keep both but make the relationship explicit in front matter (`related:` / `duplicate_of:` / `companion_to:`).
- **Explained repetition is allowed**, per the existing rule, when it serves a materially different learning objective — e.g., a Foundation-level restating of "what problem does caching solve" inside `07-api-design`'s discussion of HTTP caching headers is not a duplicate of the full `caching-strategies-and-invalidation.md` chapter in `10-distributed-systems`/`11-system-design`, provided it's two sentences of grounding, not a second full explanation, and links to the canonical chapter for depth.

---

## 9. Naming conventions

Extends `CLAUDE.md`'s existing File Naming and YAML Front Matter rules (line 2311) rather than replacing them.

- **Directories**: `NN-kebab-case-domain-name/`, two-digit zero-padded prefix, matching §3.2. Subdomains inside a large domain (e.g., `02-java/collections/`) are unprefixed kebab-case — numeric prefixes are reserved for cases where sequence matters (as the existing rule already states), and reading order within a domain is expressed via `prerequisites:` front matter and the domain's own `INDEX.md`, not via filename numbering, so that inserting a new topic later never requires renumbering siblings.
- **Files**: unchanged existing convention — lowercase kebab-case, descriptive, e.g. `hashmap-internals.md`.
- **Topic IDs**: existing `T-xxx` (backend) and `F-xxx` (frontend) IDs from the Master Topic Register are preserved as the stable identifier through migration — a file's path may change; its `topic_id` never does. New topics created for gap domains (§7.6) get new IDs in reserved ranges: `T-1700`–`T-1799` for Software Design, `T-1800`–`T-1899` for Engineering Practices, `T-1900`–`T-1999` for Leadership/Staff knowledge, `T-2000`–`T-2099` for CS Foundations, `T-2100`–`T-2199` for Data Structures & Algorithms canonical chapters (distinct from the existing D14 interview-question IDs, which remain attached to their existing coding-problem meaning).
- **Domain `INDEX.md`**: every `syllabus/NN-domain/` directory gets one `INDEX.md` — the domain's table of contents, its own short "why this domain, how it's organized, where to start" note, and a table of its topics with `topic_id`, title, and current `mastery_levels_covered`. This is new; nothing today plays this role at the domain level (only the repository-root README does, for the whole repo).

---

## 10. Migration phases

No phase below is authorized by this document. It defines what execution would look like *if and when* the user approves it — each phase is its own explicit go/no-go decision, not a chain that starts automatically once this plan is accepted.

| Phase | Scope | Deliverable | Destructive? |
|---|---|---|---|
| **0 — Provenance and tooling** | Generate the exhaustive topic-ID-joined migration mapping (§7.1) as a reviewable file; update `scripts/validate.py` to also check `source_history` and cross-domain link integrity | `00-project/migration-mapping.md`, updated `scripts/` | No — read-only analysis |
| **1 — Scaffolding** | Create `syllabus/00-overview/` and all 21 domain directories with `INDEX.md` stubs only; write the new root `README.md`/`CLAUDE.md` framing (content preserved, vision updated) | Empty-but-navigable new tree, old tree fully untouched | No |
| **2 — Low-risk relocations** | Move the handful of clearly mis-homed single files identified in §2.6/§7.2 (git internals, design patterns, api-design) via `git mv`; update the small number of inbound links (script-assisted) | ~5–10 files relocated, cross-references updated, `source_history` recorded | Low — small blast radius, easy to verify exhaustively by hand |
| **3 — Domain-by-domain handbook migration** | One domain at a time (suggested order: `02-java` first, since it's the largest and least ambiguous; `19-leadership-staff` and `01-computer-science-foundations` last, since they're mostly new writing, not migration), `git mv` each domain's chapters into their new path, add L1/L2 sections, update front matter to the new schema | One PR/commit per domain, independently reviewable and revertable | Medium per-domain, but domains are independent — a bad migration of one domain doesn't block or corrupt the others |
| **4 — Cross-linking pass** | Wire `cheat-sheets/`, `flashcards/`, `production-cookbook/`, `practice/`, `architecture-atlas/`, `study-packs/` references to the new canonical paths, without moving those directories (§7.4) | Updated `related:`/`canonical:`/`source:` front matter across ~600 files, script-assisted | Low — link updates only, `scripts/validate.py` catches breakage |
| **5 — Gap-filling** | Write the new domains and new L1/L2 layers identified in §7.6, in priority order | New canonical content, additive only | No — pure addition |
| **6 — Learning-path assembly** | Author the six learning-path documents (§6) | New short documents, referencing existing content | No |
| **7 — Deprecation of old paths** | After a transition window (suggested: one full review cycle after Phase 3 completes for a given domain), remove redirect stubs at old `handbook/<domain>/` paths | Old paths removed | **Yes — the only genuinely destructive phase, and only for redirect stubs, not content** — requires its own explicit approval, separate from this document |

---

## 11. Risks

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| Migration mapping generated in Phase 0 finds the `topic_id` join is less complete than sampling suggested (some chapters lack a `topic_id`, or IDs collide) | Medium | Medium — would require hand-mapping the remainder | Phase 0's output is a reviewable artifact specifically so this surfaces before any file moves, not after |
| The `practice/java/{domain}/` vs. `practice/java/week-XX/` overlap (§7.5) turns out to be a real, unintentional duplication rather than complementary material | Medium | Low — no user-facing harm, just wasted disk/attention until resolved | Explicit file-level diff task scheduled before Phase 4 touches `practice/` links |
| `interview-playbook/company-prep/nordstrom-senior-backend-remote.md`, or similar files not yet written, leak into a future public/commercial build if the "private" domain boundary isn't enforced by tooling, not just convention | Medium | High — real privacy exposure once this repo is a product | Recommend a build-time check (extend `scripts/validate.py`) that fails if any `company-prep/` or `*.private.md` file is included in whatever "publishable" export/build step gets built later; not building that check is itself a decision the user should make explicitly, not by omission |
| Adding L1/L2 sections to 181 chapters is a very large amount of new writing, risking scope creep that stalls the whole migration | High | Medium | Phase 3 is explicitly domain-by-domain and independently shippable; a domain can migrate its structure/front-matter/links first and have its L1/L2 content follow later without blocking other domains — "structure now, depth incrementally" is a valid, tracked partial state (see Definition of Done, §13, which allows `mastery_levels_covered: [L3, L4]` as a legitimate interim state) |
| `graft/` (§2.9) is mistakenly excluded from migration if it actually contains content the user wants kept | Low | Medium if true | Flagged explicitly for user confirmation before Phase 1 |
| Cross-reference rot: existing files link to `../../handbook/domain/file.md`-style relative paths; a domain relocation breaks every inbound link from `cheat-sheets/`, `flashcards/`, `study-packs/`, and other `handbook/` chapters that reference it | High if unmanaged | Medium — broken links, not data loss | This is precisely why Phase 3 is script-assisted and why `scripts/validate.py` is extended in Phase 0 to check link integrity as a gate before each domain's migration is considered done |
| The new taxonomy's boundary choices (§3.3) turn out to be wrong once real content is placed into them (e.g., a topic that genuinely straddles `10-distributed-systems` and `11-system-design`) | Medium | Low | Domain boundaries are a navigation convenience, not a hard constraint — a topic's `related:` front matter can point across domains freely; nothing about the architecture prevents adjusting a boundary later without re-migrating content, since the topic's identity is its `topic_id`, not its path |

---

## 12. Validation rules

Extends the existing `scripts/validate.py` and the Quality Gates already defined in `CLAUDE.md` (line 2397), rather than replacing them.

1. **Every `syllabus/` topic file has complete, schema-valid front matter** per §4.3, including a non-empty `topic_id`.
2. **Every relative link resolves** to an existing file or an explicitly marked `> Planned reference:` placeholder (existing rule, `CLAUDE.md:2249`, extended to cover the new domain paths).
3. **No topic file duplicates a definition, code example, trade-off table, or interview-question set** that already exists at another `topic_id`'s canonical location, unless justified inline (existing rule, extended).
4. **Every topic file has a non-empty Mastery Checklist** (§5.2) using at least one of the five verification forms — "read this chapter" as a stated criterion is a hard validation failure.
5. **`mastery_levels_covered` in front matter is accurate** — a topic claiming `[L1, L2, L3, L4]` must have all four level headings present with non-placeholder content; this is checkable by a simple heading-presence script, not just human review.
6. **Every learning path's referenced topics all exist** and are not broken links (a path is a curation layer, so this is cheap and high-value to check).
7. **No file under `interview-playbook/company-prep/` or matching `*.private.md`/`local/` is referenced from any `syllabus/` canonical topic, learning path, or `00-overview/` index** — enforces the privacy boundary from §2.8/§11 structurally, not just by convention.
8. **`git mv` is used for every relocation**, never delete+recreate, so `git log --follow` continues to show a file's full history — this is the mechanical half of "conserva el historial," alongside the `source_history` front-matter field which is the human-readable half.
9. **A domain is not marked `status: canonical` in its `INDEX.md`** until every topic file inside it passes rules 1–5 above — "structure exists" and "content is canonical" are tracked as distinct states, matching the interim-state allowance in §11.

---

## 13. Definition of done

This plan itself is done when the user has read it and either approves it, approves it with modifications, or rejects it — there is no other deliverable expected from this task per the user's explicit instruction not to migrate anything yet.

For the *migration*, once approved, each phase in §10 has its own definition of done:

- **Phase 0** is done when `00-project/migration-mapping.md` exists, is reviewed by the user, and accounts for all 1,086 tracked Markdown files (either mapped to a new domain, or explicitly listed as out-of-scope per §2.9).
- **Phase 1** is done when all 21 domain directories exist with a populated `INDEX.md` and zero broken links, and the old tree is provably untouched (a `git diff --stat` against the pre-Phase-1 commit shows only additions).
- **Phase 3**, per domain, is done when: every chapter in that domain has moved via `git mv` with `source_history` recorded, front matter is schema-valid (validation rule 1), all inbound/outbound links resolve (rule 2), and the domain's `INDEX.md` accurately reflects `mastery_levels_covered` per topic (rule 5) — even if that's honestly `[L3, L4]` only, pending Phase 5 gap-filling for that domain.
- **The overall transformation** is "done" only in the sense that any living reference system is never finished — the working definition proposed here is: all 21 domains have non-empty `INDEX.md`s, zero validation failures under §12, the six learning paths in §6 are published and link-valid, and every topic in the original 198-item Master Topic Register (plus the frontend register) has a `topic_id`-traceable home in the new structure. Reaching full L1–L4 coverage on every topic is explicitly an ongoing, long-horizon goal (matching the original repository's own multi-month roadmap discipline), not a blocking condition for calling the *architecture* migration complete.

---

## 14. Open questions for approval

Explicit decisions this document surfaces but does not make unilaterally:

1. **Confirm the treatment of `graft/` (§2.9)** — excluded from migration as tooling/cache, or does it contain content that needs to be pulled in?
   - **Resolved 2026-09-03 — excluded.** Confirmed by direct inspection during Phase 0, not left as an assumption: `.gitignore`'s own comment states `graft/` is "graft's local graph cache — regenerable, not committed (run `graft build`)." `graft/INDEX.md` self-describes as "430 per-file wiring cards" (symbol/function indexes with `file:line` references, no original prose). `graft/practice/` and `graft/scripts/` are exact directory mirrors of the real `practice/`/`scripts/` trees, each file containing only extracted symbol lists (e.g., `graft/scripts/validate.md` lists `err`, `warn`, `note`, `ok`, `head`, `md_files` with line numbers, zero authored content). Nothing to pull in; excluded with certainty, not judgment.
2. **Confirm the `19-leadership-staff` vs. `20-interview-preparation/behavioral/` split and reference-not-duplicate approach (§2.7).**
   - **Resolved 2026-09-03 — approved as proposed.** `behavioral-handbook/` (16 files) relocates as-is, unchanged, to `20-interview-preparation/behavioral/`. `19-leadership-staff` is a new domain that references the relevant `behavioral-handbook/` chapters (07 mentoring, 09 cross-team influence, 10 migrations, 11 technical-debt advocacy, 12 design reviews/RFCs) as source material for its own Level 1–4 ladder, never duplicating their content.
3. **Confirm `interview-playbook/company-prep/` becomes a permanent, tooling-enforced private category (§2.8, §11, §12 rule 7).**
   - **Resolved 2026-09-03 — approved as proposed.** `company-prep/` (and any future `*.private.md`/`local/` file) is a permanent private category, enforced structurally by validation rule §12.7 (no `syllabus/` canonical topic, learning path, or `00-overview/` index may reference it), not merely by convention — never sanitized into public/commercial material.
4. **Approve, adjust, or reject the 21-domain taxonomy (§3.2) and the specific boundary calls in §3.3.**
   - **Resolved 2026-09-03 — approved as proposed, no adjustments.** The 21-domain taxonomy in §3.2, including the `10` vs. `11`, `13` vs. `16`, `14` vs. `15` splits and `07-api-design` as its own domain (§3.3), stands as written.
5. **Approve the Topic Specification (§4) and Mastery Model (§5) as the standard going forward.**
   - **Resolved 2026-09-03 — approved as proposed.** The 20-section Topic Specification (§4.2) and the four-level (L1 Foundation → L4 Staff) Mastery Model (§5) become the standard every existing and future chapter is written or retrofitted against.
6. **Decide the migration-order priority within Phase 3 (§10).**
   - **Resolved 2026-09-03 — `02-java` first, as the plan suggested** (largest, least ambiguous domain).

**All six open questions are now resolved.** Per §13's Definition of Done, this plan itself is done: approved with the six decisions recorded above. Phase 0 (§10) is complete — see `00-project/migration-mapping.md`. Phase 1 (Scaffolding) has not been authorized and does not begin automatically; it requires its own explicit go/no-go per §10's governance model.
