---
title: "Lost Update in a Get-Then-Put Counter Increment"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/collections/concurrenthashmap-internals.md
source: handbook/collections/concurrenthashmap-internals.md#production-scenarios
---

# Lost Update in a Get-Then-Put Counter Increment

## Context

A service tracks per-endpoint request counts in a `ConcurrentHashMap<String, Integer>`, incrementing via a `get()`-then-`put()` pattern on each request.

## Symptoms

Under low traffic, the dashboard's counts match load-balancer request logs closely. Under peak traffic, the dashboard consistently undercounts by a significant, load-dependent margin.

## Impact

A metric used for capacity planning and alerting silently underreports exactly when traffic is highest — the moment accurate counting matters most.

## Initial Hypotheses

- The load balancer's own request log double-counts — checked and ruled out; the load balancer's counts are independently verified accurate.
- Metric collection is being sampled or dropped under load — checked and ruled out; no sampling or dropping is configured anywhere in the pipeline.
- The counter-increment pattern itself loses updates under concurrent access — correct.

## Evidence

The undercount percentage scales with concurrent request rate — exactly what a lost-update race would produce, since more concurrent threads means more opportunities for two `get()` calls to observe the same value before either `put()` call commits.

## Investigation Timeline

1. **Undercount noticed** by comparing dashboard counts against independently verified load-balancer logs.
2. **Sampling and log-duplication hypotheses ruled out** by checking the collection pipeline and the load balancer's own counts directly.
3. **Load-scaling pattern examined**: the undercount percentage tracks concurrent request rate, not absolute volume — the specific signature of a read-modify-write race, not a fixed-rate data-loss bug.
4. **Increment pattern inspected**, confirming the `get()`-then-`put()` sequence has an unprotected gap between the two calls.

## Root Cause

`ConcurrentHashMap`'s individual method calls are thread-safe, but the `get()`-then-`put()` two-call sequence has an unprotected gap where concurrent threads can read the same stale value and each write back an increment that overwrites the other's.

## Immediate Mitigation

None available without a code change — the undercounting is a structural property of the increment pattern, not a runtime-tunable setting.

## Permanent Fix

Replace every `get()`-then-`put()` counter increment with `map.merge(key, 1, Integer::sum)`, converting the read-modify-write into one atomic per-key operation and eliminating the race entirely.

## Alternatives Considered

Wrapping the increment in an external `synchronized` block. Rejected as strictly worse than `merge()` — it would serialize all increments across all keys through one lock, discarding `ConcurrentHashMap`'s per-bucket concurrency for no additional correctness benefit over the built-in atomic operation.

## Trade-offs

None — `merge()` is both correct and at least as performant as the broken pattern it replaces.

## Prevention

Treat any `get()`-then-`put()` sequence on a `ConcurrentHashMap` as a code-review flag by default, requiring justification for why an atomic compound operation (`merge`, `compute`, `computeIfAbsent`) isn't used instead.

## Monitoring and Alerts

- Cross-checking an internally collected counter against an independent source of truth (here, load-balancer logs) on a recurring schedule, not just at incident time — this comparison is what surfaced the bug, and it is cheap to automate as a standing consistency check for any metric with an external counterpart.
- A grep-based or static-analysis CI check for `.get(` immediately followed by `.put(` on the same map variable, catching the specific anti-pattern before it reaches production rather than after a metric quietly degrades.

## Interview Story

This maps to a "why is our metric silently wrong under load" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a request-counting dashboard undercounted specifically during peak traffic, the exact moment the metric mattered most.
- **Task:** find the cause without assuming `ConcurrentHashMap` itself was unsafe.
- **Action:** rule out log duplication and sampling using independent data sources; notice the undercount scales with concurrency rather than volume; identify the `get()`-then-`put()` gap as the specific unprotected sequence.
- **Result:** replaced the increment with `map.merge()`, eliminating the race with no performance cost.

## Staff-Level Discussion

The mistake here is a common and subtle one: `ConcurrentHashMap` being thread-safe per-call gets generalized, incorrectly, to "any sequence of calls on it is safe" — but thread safety composes per-operation, not across operations, and a `get()`-then-`put()` pair is two operations with a gap between them regardless of the underlying map's own safety. The organizational fix is not "remember to use `merge()`" as a personal discipline; it's a cheap, mechanical CI check that catches the exact anti-pattern (`get` immediately followed by `put` on the same map) before it ships, which generalizes the fix beyond whoever happens to remember the rule during code review.

## Related Handbook Chapters

- [ConcurrentHashMap Internals](../syllabus/02-java/collections/concurrenthashmap-internals.md) — canonical per-bucket concurrency model and lost-update mechanics used here.
