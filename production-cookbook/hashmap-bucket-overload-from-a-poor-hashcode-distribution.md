---
title: "HashMap Bucket Overload From a Poor hashCode Distribution"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/collections/hashmap-internals.md
source: handbook/collections/hashmap-internals.md#production-scenarios
---

# HashMap Bucket Overload From a Poor hashCode Distribution

## Context

A service uses a `HashMap<CompositeKey, CachedResult>` as an in-memory cache. `CompositeKey`'s `hashCode()` combines its fields with a hand-rolled XOR.

## Symptoms

Over several weeks, p99 latency for cache lookups climbs steadily, even though the cache's total entry count stays within its configured bound.

## Impact

A supposedly O(1) cache lookup becomes a measurable latency contributor, eventually significant enough to show up in the service's own p99 dashboard.

## Initial Hypotheses

- The cache is simply larger than expected, and even O(1) lookups have more constant-factor overhead per entry — checked and ruled out; entry count is stable, well within the configured bound.
- GC pressure from cache churn — checked and ruled out; GC logs show no correlation with the latency trend.
- `CompositeKey`'s `hashCode()` implementation has poor distribution for the actual production key value shape, causing bucket overload — correct.

## Evidence

A production heap dump, combined with reflective bucket inspection, shows a small number of buckets holding the vast majority of cache entries — some as `TreeNode`-treeified bins — while most buckets sit empty.

## Investigation Timeline

1. **Gradual p99 climb noticed on cache lookups specifically**, with entry count staying flat.
2. **Size and GC-pressure hypotheses ruled out** against entry-count and GC-log data, neither correlating with the trend.
3. **Heap dump taken and buckets inspected directly**, revealing a small number of severely overloaded buckets against a mostly empty table.
4. **`hashCode()` implementation reviewed**, finding an XOR-combination of fields that are frequently near-equal in production, canceling out much of their entropy.

## Root Cause

`CompositeKey`'s `hashCode()` combines its fields in a way that happens to produce a narrow range of hash values for the specific value distribution seen in production — a distribution-dependent `hashCode()` weakness that unit tests with synthetic, well-spread test keys never exposed. A poor hash distribution overloads a small number of buckets regardless of table size, and even with treeification softening the worst case from O(n) to O(log n), a sufficiently overloaded bucket is still measurably, and increasingly, slower than a well-distributed one.

## Immediate Mitigation

Temporarily increase the cache's initial capacity as a partial mitigation — spreading entries across more buckets reduces, but does not eliminate, the collision concentration from a genuinely poor hash function.

## Permanent Fix

Redesign `CompositeKey.hashCode()` to properly combine its fields, for example via `Objects.hash(...)` rather than a hand-rolled XOR, and add a distribution test using production-representative key samples, not just synthetic sequential test data, to catch this class of regression before it reaches production.

## Alternatives Considered

Switching to a different map implementation entirely. Rejected — the actual defect is in the key's `hashCode()`, not in `HashMap` itself; any hash-based structure would suffer the identical problem.

## Trade-offs

None — fixing a genuinely poor `hashCode()` implementation has no downside.

## Prevention

Any custom `hashCode()` implementation used as a `HashMap` key should be tested for distribution quality against production-representative data, not just correctness (the equals/hashCode contract) against synthetic unit-test values.

## Monitoring and Alerts

- Periodic bucket-distribution sampling on production-critical caches (the same reflective inspection technique used to diagnose this incident), run proactively rather than only reached for after a latency trend is already visible.
- p99 latency for cache-lookup operations specifically, tracked separately from overall request p99 — this incident's signal was clean and specific at the lookup-operation level well before it became visible in the aggregate service dashboard.

## Interview Story

This maps to a "why is a supposedly O(1) lookup getting slower over time" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a `HashMap`-backed cache's lookup latency climbed steadily over weeks with entry count staying flat.
- **Task:** find the cause without assuming `HashMap` itself was the problem.
- **Action:** rule out size and GC-pressure explanations using existing metrics; inspect bucket distribution directly via a heap dump; trace the overload to a specific `hashCode()` weakness that only manifests against production-shaped key values.
- **Result:** fixed the `hashCode()` implementation and added a distribution test against production-representative data, closing both the incident and the test gap that let it ship.

## Staff-Level Discussion

The specific danger in this bug class is that it is invisible to the correctness testing every team already does: the equals/hashCode contract test suite verifies `hashCode()` is *consistent*, never that it's *well-distributed*, and synthetic sequential test keys almost never reproduce the collision pattern a real production key distribution can produce. This is a case where "the tests pass" and "the code is production-ready" are genuinely different claims, and a Staff engineer should recognize that any custom `hashCode()` on a hot-path key type is a specific, nameable risk category deserving its own distribution test against real data — not something covered by ordinary unit-test discipline.

## Related Handbook Chapters

- [HashMap Internals](../syllabus/02-java/collections/hashmap-internals.md) — canonical bucket, collision, and treeification mechanics used here.
