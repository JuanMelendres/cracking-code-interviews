---
title: "Observability — Domain Index"
document_type: syllabus-domain-index
domain: 13-observability
status: 4 of 4 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Observability

"How do I know something is wrong in production" — logging/metrics/tracing, SLOs and error budgets, incident response, and percentile/tail-latency measurement. Split out of `performance/` per the plan's Section 3.3 (a different skill than JVM tuning).

> **Phase 3 update (2026-09-03).** This domain's full existing content (4 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 4 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a car-diagnostics analogy for USE/RED and a monthly-allowance analogy for error budgets, a coffee-shop-wait-time analogy for percentiles and coordinated omission, a package-tracking-number analogy for logs/metrics/traces, and a leaking-kitchen-sink analogy for mitigate-before-diagnose and blameless postmortems). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`13-observability` is now fully L1–L4 (4/4)** — the eleventh fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-1201/T-1206 | Performance Methodology (USE/RED) and SLI/SLO/Error Budgets | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/13-observability/performance-methodology-and-slo-error-budgets.md` |
| T-1204 | Percentiles, Tail Latency, and Coordinated Omission | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md` |
| T-1205 | Logging, Metrics, Tracing, and OpenTelemetry | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md` |
| T-1207 | Incident Response and Blameless Postmortems | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/13-observability/incident-response-and-blameless-postmortems.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
