---
title: "Spring — Domain Index"
document_type: syllabus-domain-index
domain: 05-spring
status: 9 of 9 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Spring

Dependency injection, auto-configuration, transactions, testing slices, WebFlux, and Spring's security filter chain. Existing `handbook/spring/` (9 chapters) relocates here unchanged in content.

> **Phase 3 update (2026-09-03).** This domain's full existing content (9 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 9 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion, verified by diff, on every chapter. Each Level 1/Level 2 pair is grounded in that chapter's own real subject (a bank-transfer analogy for `@Transactional`, a dashboard-warning-lights analogy for Actuator, the shared self-invocation gotcha called out explicitly for both `@Transactional` and `@Cacheable`) rather than a generic template. Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`05-spring` is now fully L1–L4 (9/9)** — the third fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-502 | Spring Bean Scopes and Proxy Modes | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/05-spring/spring-bean-scopes-and-proxy-modes.md` |
| T-503/T-504/T-505 | Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md` |
| T-506/T-501 | Spring Auto-Configuration and Bean Lifecycle | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/05-spring/auto-configuration-and-bean-lifecycle.md` |
| T-506/T-501 | Spring Framework vs. Spring Boot: Auto-Configuration and the Embedded Server | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/05-spring/spring-framework-vs-spring-boot.md` |
| T-509 | Spring WebFlux and Reactive Programming | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/05-spring/spring-webflux-and-reactive-programming.md` |
| T-511 | Spring Security Filter Chain | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/05-spring/security-filter-chain.md` |
| T-514 | Spring Cache Abstraction and Pitfalls | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/05-spring/spring-cache-abstraction-and-pitfalls.md` |
| T-516 | Spring Boot Actuator, Health, and Observability Hooks | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/05-spring/spring-actuator-health-and-observability-hooks.md` |
| T-517 | Spring Testing: Slices and Context Caching | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/05-spring/spring-testing-slices-and-context-caching.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
