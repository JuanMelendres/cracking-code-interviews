---
title: "API Design — Domain Index"
document_type: syllabus-domain-index
domain: 07-api-design
status: 2 of 2 mapped chapters physically relocated (Phase 2, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4; 3rd chapter added 2026-09-07 (REST API Fundamentals, T-2205), a true Junior on-ramp per the repository's expanded Junior-to-Staff positioning; 4th and 5th chapters added 2026-09-09 (GraphQL API Design T-917, gRPC API Design T-918) closing the gap where T-803's title promised both protocols but neither was ever written
last_updated: 2026-09-11
---

# API Design

REST/gRPC/GraphQL design, versioning, and pagination — foundational-through-Senior knowledge every backend engineer needs, split out of `system-design` per the plan's Section 3.3 reasoning (it doesn't require the rest of system-design's Staff-oriented prerequisite chain).

> **Phase 2 update (2026-09-03).** Both chapters below have physically relocated here via `git mv` from `handbook/system-design/` — the plan's own named low-risk relocation (§10 Phase 2). This domain's full existing content is now in place.
>
> **Phase 5 update (2026-09-04) — domain complete.** Both chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a phone-book-vs-bookmark analogy for OFFSET vs. keyset pagination, an apartment-building concierge analogy for the API gateway, a personal-assistant analogy for the BFF pattern) rather than a generic template. Both chapters also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`07-api-design` is now fully L1–L4 (2/2)** — the fifth fully-retrofitted domain in the syllabus.
>
> **Junior Fundamentals gap closed (2026-09-07) — initiative complete.** The Phase 5 retrofit above added intuition for existing Senior-level topics; it never taught what a resource, an HTTP verb, or a status code actually is, because this domain's original scope assumed that baseline already. This is the fifth and final chapter of a repository-wide audit (see `syllabus/02-java/INDEX.md`'s matching note) triggered by the user's decision to sell this repository as Junior-through-Staff. [REST API Fundamentals](rest-api-fundamentals.md) (T-2205, reserved range `T-2200`–`T-2299`) closes it, with a real Spring Boot app proving `POST`'s non-idempotency and `PUT`'s idempotency directly rather than asserting them. **All five Junior Fundamentals chapters (T-2201–T-2205) are now written** — see `00-project/syllabus-transformation-plan.md` and `syllabus/00-overview/changelog.md` for the full initiative. Cheat sheet and flashcard deck for T-2205 added 2026-09-07 (`cheat-sheets/README.md`, `flashcards/README.md`) — all five Junior Fundamentals chapters now have both.
>
> **Status-code gap closed (2026-09-11).** A direct user question ("do we have API status codes?") found [REST API Fundamentals](rest-api-fundamentals.md) covered only `200`/`201`/`204`/`404`/`500` — real gaps in `400`, `401`/`403`, `405`, `409`, `422`, `304`, `429`, and `502`/`503`/`504`. Closed in place (same chapter, no new topic_id): real, executed evidence added for `400` (malformed JSON), `405` (unmapped verb, real `Allow` header), `409` (a genuine business-key conflict, deliberately distinct from the chapter's own title-duplication proof), `422` (semantic validation), and `304` (a real conditional `GET` via Spring's built-in `ShallowEtagHeaderFilter`) — a real bug (a `PUT` handler silently dropping a field) was caught and fixed by this same demo before shipping. `401`/`403`/`429`/`502`-`504` are covered conceptually with real cross-links to where their own deep, already-executed evidence lives (`12-security`'s OAuth2/CSRF chapters, `11-system-design`'s rate-limiting chapter, `api-gateway-bff-and-edge-concerns.md`) rather than duplicated. Cheat sheet and flashcard deck updated in place the same day.
>
> **Full-range gap closed (2026-09-11, same day, follow-up).** User pushed further: were the `2xx`/`3xx`/`4xx`/`5xx` *ranges* themselves actually covered, not just individual codes? They weren't — no `3xx` member existed beyond `304`, and the chapter's status-code content had no explicit range structure at all. Closed the same day: a new, real `GET /books/latest` endpoint (a genuinely computed `302 Found` redirect whose target changes as new books are created) and real, zero-code `415 Unsupported Media Type` evidence, plus an explicit per-range reference table (`1xx`–`5xx`) naming every code and marking each as real-demoed or conceptual-with-a-stated-reason (`202`/`206`/`301`/`307`/`308`/`410`/`501` — no honest real demo available for these in a synchronous JSON CRUD API). Cheat sheet and flashcard deck (+2 cards) updated in place the same day.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-2205 | REST API Fundamentals | L1, L2, L3, L4 — fully written, real executed demo (2026-09-07) | `syllabus/07-api-design/rest-api-fundamentals.md` |
| T-803 | API Design | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/07-api-design/api-design.md` |
| T-911 | API Gateway, BFF, and Edge Concerns | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md` |
| T-917 | GraphQL API Design | L1, L2, L3, L4 — fully written, real graphql-java N+1/DataLoader demo (2026-09-09) | `syllabus/07-api-design/graphql-api-design.md` |
| T-918 | gRPC API Design | L1, L2, L3, L4 — fully written, real protoc-generated client/server demo (2026-09-09) | `syllabus/07-api-design/grpc-api-design.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
