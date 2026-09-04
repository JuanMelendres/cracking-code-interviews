---
title: "System Design — Domain Index"
document_type: syllabus-domain-index
domain: 11-system-design
status: 9 of 9 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# System Design

The applied method and case studies: the six-phase design method plus all 17 Architecture Atlas systems (referenced from `architecture-atlas/`, not duplicated) — "how to design one, live, in 45 minutes."

> **Phase 3 update (2026-09-03).** This domain's full existing content (9 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 9 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a phone-call analogy for circuit breakers/jitter/bulkheads, a physical-storage analogy for storage selection, a birthday-party-planning analogy for the six-phase design method, a sticky-note analogy for caching, a restaurant-host analogy for load balancing/health checking, a nightclub-bouncer analogy for rate limiting, a mailed-form-with-reference-number analogy for idempotency, a library-card-catalog analogy for search indexing, and a package-delivery-tracking analogy for the four real-time delivery mechanisms). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`11-system-design` is now fully L1–L4 (9/9)** — the ninth fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-515 | Resilience Patterns: Circuit Breaker, Retry Jitter, Timeouts, and Bulkheads | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/11-system-design/resilience-patterns.md` |
| T-617/T-811 | Storage Selection Trade-offs | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/11-system-design/storage-selection-tradeoffs.md` |
| T-801/T-802 | System Design Method and Estimation | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/11-system-design/system-design-method-and-estimation.md` |
| T-804 | Caching Strategies and Invalidation | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/11-system-design/caching-strategies-and-invalidation.md` |
| T-805 | Load Balancing, Service Discovery, and Health Checking | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md` |
| T-808 | Rate Limiting and Throttling Algorithms | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/11-system-design/rate-limiting-and-throttling-algorithms.md` |
| T-809 | Idempotency at System Edges | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/11-system-design/idempotency.md` |
| T-810 | Search and Indexing Systems | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/11-system-design/search-and-indexing-systems.md` |
| T-812 | Real-Time Delivery: WebSocket, SSE, Long-Polling, and Push | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/11-system-design/realtime-delivery-websocket-sse-and-long-polling.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
