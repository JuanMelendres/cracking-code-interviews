---
title: "Observability — Domain Index"
document_type: syllabus-domain-index
domain: 13-observability
status: 4 of 4 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4; 5th chapter added 2026-09-11 (Metric Cardinality and Alert Fatigue, T-2409 — gap found in a full 22-domain audit)
last_updated: 2026-09-11
---

# Observability

"How do I know something is wrong in production" — logging/metrics/tracing, SLOs and error budgets, incident response, percentile/tail-latency measurement, and metric cardinality/alert-fatigue management. Split out of `performance/` per the plan's Section 3.3 (a different skill than JVM tuning).

> **Phase 3 update (2026-09-03).** This domain's full existing content (4 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 4 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a car-diagnostics analogy for USE/RED and a monthly-allowance analogy for error budgets, a coffee-shop-wait-time analogy for percentiles and coordinated omission, a package-tracking-number analogy for logs/metrics/traces, and a leaking-kitchen-sink analogy for mitigate-before-diagnose and blameless postmortems). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`13-observability` is now fully L1–L4 (4/4)** — the eleventh fully-retrofitted domain in the syllabus.
>
> **Gap found and closed: Metric Cardinality and Alert Fatigue (2026-09-11).** A full repository-wide 22-domain gap audit found this domain had zero coverage of metric cardinality management or alert fatigue/runbook discipline, despite both being real, common, costly production problems with no dedicated entry anywhere in the original Master Topic Register. Closed with [Metric Cardinality and Alert Fatigue](metric-cardinality-and-alert-fatigue.md) (T-2409), backed by a real demo (`practice/java/observability/metric-cardinality-and-alert-fatigue/`) proving two things directly: a real Micrometer `SimpleMeterRegistry` shows an unbounded metric label producing a real 6,667x more distinct time series (and ~43x more heap) than a bounded one for identical request volume; and a real, seeded 7-day synthetic error-rate simulation shows Google's own published multi-window, multi-burn-rate alerting technique firing 14x less often than a naive single-threshold rule, while both real, injected incidents are still caught by both rules — real, quantified proof the technique reduces alert noise without sacrificing detection.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-1201/T-1206 | Performance Methodology (USE/RED) and SLI/SLO/Error Budgets | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/13-observability/performance-methodology-and-slo-error-budgets.md` |
| T-1204 | Percentiles, Tail Latency, and Coordinated Omission | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md` |
| T-1205 | Logging, Metrics, Tracing, and OpenTelemetry | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md` |
| T-1207 | Incident Response and Blameless Postmortems | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/13-observability/incident-response-and-blameless-postmortems.md` |
| T-2409 | Metric Cardinality and Alert Fatigue | L1, L2, L3, L4 — fully written, real demo (2026-09-11) | `syllabus/13-observability/metric-cardinality-and-alert-fatigue.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
