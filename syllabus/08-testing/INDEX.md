---
title: "Testing — Domain Index"
document_type: syllabus-domain-index
domain: 08-testing
status: 7 of 7 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4; 8th chapter added 2026-09-07 (Unit Testing Fundamentals with JUnit, T-2204), a true Junior on-ramp per the repository's expanded Junior-to-Staff positioning
last_updated: 2026-09-07
---

# Testing

Test strategy, JUnit 5, contract testing, mutation testing, and live-coding test-writing discipline. Existing `handbook/testing/` (7 chapters) relocates here unchanged in content.

> **Phase 3 update (2026-09-03).** This domain's full existing content (7 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 7 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a fire-drill analogy for test doubles and the testing pyramid, a three-room-house analogy for JUnit 5's Platform/Jupiter/Vintage split, a "practicing with a friend vs. a native speaker" analogy for integration testing, a shared-document analogy for consumer-driven contracts, a bridge-load-test analogy for load/stress/soak testing, a secretly-altered-exam analogy for mutation testing, a furniture-instruction-booklet analogy for live TDD). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`08-testing` is now fully L1–L4 (7/7)** — the sixth fully-retrofitted domain in the syllabus.
>
> **Junior Fundamentals gap closed (2026-09-07).** The Phase 5 retrofit above added intuition for existing Senior-level topics; it never taught what `@Test` or `assertEquals` actually is, because this domain's original scope assumed that baseline already. Part of a repository-wide audit (see `syllabus/02-java/INDEX.md`'s matching note) triggered by the user's decision to sell this repository as Junior-through-Staff. [Unit Testing Fundamentals with JUnit](unit-testing-fundamentals-with-junit.md) (T-2204, reserved range `T-2200`–`T-2299`) is the fourth of five planned Junior Fundamentals chapters — a real JUnit 5 suite (17/17 tests passing), plus a genuine, deliberately-produced test failure (not invented) captured and kept as real teaching material. Cheat sheet and flashcard deck added 2026-09-07 (`cheat-sheets/README.md`, `flashcards/README.md`).

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-2204 | Unit Testing Fundamentals with JUnit | L1, L2, L3, L4 — fully written, real executed demo (2026-09-07) | `syllabus/08-testing/unit-testing-fundamentals-with-junit.md` |
| T-1101/T-1103 | Test Strategy, the Pyramid, and Test Doubles | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/08-testing/test-strategy-and-test-doubles.md` |
| T-1102 | JUnit 5 Architecture and Advanced Features | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/08-testing/junit5-architecture-and-advanced-features.md` |
| T-1104 | Integration Testing Against Real Dependencies | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/08-testing/integration-testing-against-real-dependencies.md` |
| T-1105 | Contract Testing for Services | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/08-testing/contract-testing-for-services.md` |
| T-1106 | Performance and Load Testing Methodology | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/08-testing/performance-and-load-testing-methodology.md` |
| T-1107 | Mutation and Property-Based Testing | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/08-testing/mutation-and-property-based-testing.md` |
| T-1108 | Writing Tests Live in an Interview | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/08-testing/writing-tests-live-in-an-interview.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
