---
title: "Proposed Target Architecture (Taxonomy)"
document_type: syllabus-overview
status: extracted from the approved Syllabus Transformation Plan — Phase 1 scaffolding
last_updated: 2026-09-03
source: 00-project/syllabus-transformation-plan.md
---

> **Provenance note.** This file's content is extracted verbatim from `00-project/syllabus-transformation-plan.md` (approved 2026-09-03), not newly authored. The plan document remains the canonical source of record for any future dispute about intent; this file exists so the content lives at its designated `syllabus/` home per Phase 1 scaffolding.

# Taxonomy

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
