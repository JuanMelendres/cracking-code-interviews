---
title: "Architecture — Domain Index"
document_type: syllabus-domain-index
domain: 17-architecture
status: 9 of 9 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Architecture

System-level architectural decision-making: Clean/Hexagonal Architecture, DDD (strategic and tactical), CQRS, microservice decomposition, modular monoliths, and evolutionary architecture. `handbook/architecture/` minus `design-patterns-applied.md` (→ `04-software-design`) and its two event-driven chapters (→ `09-messaging-event-driven`).

> **Phase 3 update (2026-09-03).** This domain's full existing content (9 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 9 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject: a wall-power-outlet analogy for ports/adapters (Clean/Hexagonal); a "football means different sports in different countries" analogy for bounded contexts (DDD Strategic); a packing-boxes-for-a-move analogy for aggregate boundaries (DDD Tactical); a newsroom-vs-printed-newspaper analogy for the write/read model lag (CQRS); a roommates-splitting-apartments analogy for service boundaries (Microservice Decomposition); a door-sign-vs-real-lock analogy for enforced module boundaries (Modular Monolith); a renovating-a-house-while-living-in-it analogy for incremental migration and rollback safety (Strangler Fig); a worn-brake-pads-and-vehicle-inspection analogy for economic debt framing and fitness functions (Technical Debt); a scientist's-lab-notebook analogy for durable decision reasoning (ADRs). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`17-architecture` is now fully L1–L4 (9/9)** — the fifteenth fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-901/T-903/T-912 | Clean and Hexagonal Architecture | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/17-architecture/clean-hexagonal-architecture.md` |
| T-902 | DDD Strategic Design — Bounded Contexts and Context Mapping | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md` |
| T-903/T-901 | DDD Tactical Design — Aggregates | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/17-architecture/ddd-tactical-design-aggregates.md` |
| T-904 | CQRS: Read/Write Separation | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/17-architecture/cqrs-read-write-separation.md` |
| T-907/T-908 | Microservice Decomposition and the Monolith Trade-off | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md` |
| T-910 | The Modular Monolith as a Deliberate Choice | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/17-architecture/modular-monolith-as-a-deliberate-choice.md` |
| T-912 | Strangler Fig, Anti-Corruption Layer, and Migration Patterns | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/17-architecture/strangler-fig-and-migration-patterns.md` |
| T-913 | Technical Debt and Evolutionary Architecture | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md` |
| T-916 | Architecture Decision Records | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/17-architecture/architecture-decision-records.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
