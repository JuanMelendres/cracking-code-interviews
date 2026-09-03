---
title: "Cheat Sheet: Performance and Load Testing Methodology"
slug: performance-and-load-testing-methodology
document_type: cheat-sheet
domain: testing
topic_id: T-1106
canonical: ../handbook/testing/performance-and-load-testing-methodology.md
last_updated: 2026-08-05
---

# Performance and Load Testing Methodology

**Canonical chapter:** [`syllabus/08-testing/performance-and-load-testing-methodology.md`](../syllabus/08-testing/performance-and-load-testing-methodology.md)

## Core Mental Model

Apply the same "what confidence does this test buy, at what cost" discipline the test pyramid applies to functional tests, but on a different axis — not "how much of the system does this exercise" but "under what traffic condition, and for how long." Load testing is this axis's cheap, frequent gate (like a unit test). Stress and soak testing are its expensive, occasional exercises (like a full end-to-end suite).

## Essential Definitions

- **Load testing** — expected, realistic traffic volume; validates latency/throughput targets under normal conditions. Cheap enough for a routine pre-release gate.
- **Stress testing** — deliberately pushes traffic beyond expected levels to find the breaking point and observe *how* it fails. Run ahead of capacity-planning decisions or major events, not every release.
- **Soak testing** — sustained, moderate load over an extended duration, specifically to surface issues that only accumulate *over time* (leaks, unbounded caches). A load test's short duration structurally cannot substitute for it.
- **Traffic shape** — request mix, cache-hit pattern, data-access distribution; matters as much as traffic volume for whether a load test catches real issues.

## Decision Table

| Test type | Answers | Placement |
|---|---|---|
| Load | Does the system meet targets at expected traffic? | Routine, ideally automated, pre-release gate |
| Stress | Where does it break, and how? | Before capacity decisions or major traffic events |
| Soak | What accumulates over time? | Required for any change touching connection pooling, caching, or other long-lived state |

**Trade-offs:** mandatory automated load-test gating costs real release-process time but catches regressions before production; manual, ownerless performance testing is cheaper per-instance but has a demonstrated tendency to silently lapse, since a skipped performance test — unlike a failing unit test — produces no automatic signal.

## Key Numbers (real, executed — `LoadTestDemo.java`)

A local HTTP server with an injected 1-in-20 slow path, load-tested with 20-way concurrency over 2,000 requests:

```
requests=2000 concurrency=20
mean=12.45ms  p50=4.17ms  p95=150.54ms  p99=155.23ms  max=187.48ms
```

Mean and p50 both look healthy; p95 (150.54ms) reveals the full 5% of requests hitting the deliberately injected slow path — the exact reason a load-testing gate's pass/fail criteria must be defined against a percentile, not a mean, threshold. For the deeper percentile-mathematics and coordinated-omission pitfalls in how the load itself is generated, see [Percentiles, Tail Latency, and Coordinated Omission](percentiles-tail-latency-and-coordinated-omission.md) — that chapter's own measured evidence (p99 shifting from 500ms to 830ms purely from correcting load-generator methodology) is the natural next step.

## Common Pitfalls

- Treating load, stress, and soak testing as interchangeable or one undifferentiated activity.
- Designing traffic volume carefully while neglecting traffic *shape* — uniform, cache-friendly synthetic traffic can pass cleanly while missing the exact conditions that cause real tail-latency behavior.
- Leaving performance testing without an explicit owner or trigger, letting it silently lapse the way an automated gate cannot.
- Gating on mean latency instead of a percentile threshold, exactly the mistake this chapter's own worked example exposes.

## Interview Answer Skeleton

**30-sec:** Load, stress, and soak testing answer three different questions — expected-traffic validation, breaking-point behavior, time-accumulated issues — and belong at different points in a release process: load as a routine gate, stress before capacity decisions, soak for long-lived-state changes. Traffic shape, not just volume, determines whether a load test catches real production issues.

**2-min:** Add why the distinction matters for testing strategy (same cost/frequency logic as the test pyramid, applied to traffic condition and duration instead of scope) + the real worked example (mean 12.45ms/p50 4.17ms look healthy, p95 150.54ms reveals a real 5%-of-traffic slow path) + the trade-off (mandatory gating costs release time but catches regressions before production; manual, ownerless testing lapses silently).

**Whiteboard:** Release-process timeline. Small frequent gate icon "load test" at every release. Larger, less-frequent icon "stress test" only before major capacity/traffic events. "Soak test" icon branching off any change touching a "long-lived state" box (connection pools, caches). Annotate: same cost/frequency logic as the test pyramid, different axis.

**Staff-level framing:** performance testing needs the same process discipline as any release gate — explicit ownership, defined trigger, defined pass/fail criteria — because its silent-lapse tendency (no automatic failure signal, unlike a broken unit test) is a demonstrated organizational risk, not hypothetical. Route deeper percentile-mathematics questions to T-1204 rather than re-deriving them shallowly.

## Production Warning Signs

- A load-testing script "used to happen" but no one can say when it last ran — the signature of missing ownership/trigger; six months of drift can let a real latency regression ship undetected across multiple releases.
- A load test passes cleanly in staging but the same traffic level causes real problems in production — check traffic *shape* (cache-hit pattern, request mix) before assuming volume alone determines representativeness; staging's uniform synthetic traffic against a warm cache never exercises the cache-miss conditions production hits.
- **Prevention:** make load testing a required, automated CI gate scoped to latency-sensitive services with a percentile-based pass/fail threshold, and require soak testing specifically for any change touching connection pooling or caching.

## Related

- `syllabus/08-testing/test-strategy-and-test-doubles.md`
- `syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md`
- `syllabus/13-observability/performance-methodology-and-slo-error-budgets.md`
