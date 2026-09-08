---
title: "API Design — Domain Index"
document_type: syllabus-domain-index
domain: 07-api-design
status: 2 of 2 mapped chapters physically relocated (Phase 2, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4; 3rd chapter added 2026-09-07 (REST API Fundamentals, T-2205), a true Junior on-ramp per the repository's expanded Junior-to-Staff positioning
last_updated: 2026-09-07
---

# API Design

REST/gRPC/GraphQL design, versioning, and pagination — foundational-through-Senior knowledge every backend engineer needs, split out of `system-design` per the plan's Section 3.3 reasoning (it doesn't require the rest of system-design's Staff-oriented prerequisite chain).

> **Phase 2 update (2026-09-03).** Both chapters below have physically relocated here via `git mv` from `handbook/system-design/` — the plan's own named low-risk relocation (§10 Phase 2). This domain's full existing content is now in place.
>
> **Phase 5 update (2026-09-04) — domain complete.** Both chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a phone-book-vs-bookmark analogy for OFFSET vs. keyset pagination, an apartment-building concierge analogy for the API gateway, a personal-assistant analogy for the BFF pattern) rather than a generic template. Both chapters also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`07-api-design` is now fully L1–L4 (2/2)** — the fifth fully-retrofitted domain in the syllabus.
>
> **Junior Fundamentals gap closed (2026-09-07) — initiative complete.** The Phase 5 retrofit above added intuition for existing Senior-level topics; it never taught what a resource, an HTTP verb, or a status code actually is, because this domain's original scope assumed that baseline already. This is the fifth and final chapter of a repository-wide audit (see `syllabus/02-java/INDEX.md`'s matching note) triggered by the user's decision to sell this repository as Junior-through-Staff. [REST API Fundamentals](rest-api-fundamentals.md) (T-2205, reserved range `T-2200`–`T-2299`) closes it, with a real Spring Boot app proving `POST`'s non-idempotency and `PUT`'s idempotency directly rather than asserting them. **All five Junior Fundamentals chapters (T-2201–T-2205) are now written** — see `00-project/syllabus-transformation-plan.md` and `syllabus/00-overview/changelog.md` for the full initiative. Cheat sheet and flashcard deck for T-2205 added 2026-09-07 (`cheat-sheets/README.md`, `flashcards/README.md`) — all five Junior Fundamentals chapters now have both.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-2205 | REST API Fundamentals | L1, L2, L3, L4 — fully written, real executed demo (2026-09-07) | `syllabus/07-api-design/rest-api-fundamentals.md` |
| T-803 | API Design | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/07-api-design/api-design.md` |
| T-911 | API Gateway, BFF, and Edge Concerns | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
