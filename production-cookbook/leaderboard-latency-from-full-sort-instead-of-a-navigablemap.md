---
title: "Leaderboard Latency from a Full Sort Instead of a NavigableMap"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md
source: handbook/collections/treemap-treeset-and-navigable-hierarchy.md#production-scenarios
---

# Leaderboard Latency from a Full Sort Instead of a NavigableMap

## Context

A service backs its player leaderboard with a `HashMap` and needs to serve a `/leaderboard/near/{score}` endpoint returning the top scores near a given rank.

## Symptoms

The endpoint's p99 latency scales with total player count, since every request re-sorts the entire dataset to answer a "nearest scores" query.

## Impact

A query that should be cheap and constant-shaped (a handful of nearby scores) instead costs proportionally to the entire leaderboard's size on every single request, degrading as the player base grows.

## Initial Hypotheses

- The endpoint needs a cache — an initial instinct, but investigated further before committing to it.
- The actual query shape is "give me the nearest few scores around X" — precisely what a full sort is a poor structural fit for.

## Evidence

The actual query shape — nearest few scores around a given value — is precisely what `NavigableMap.subMap`/`headMap`/`tailMap` answer directly, in O(log n), without ever sorting anything at request time; a `HashMap` has no ordering at all, which is why the endpoint's implementation had to fall back to a full sort on every request just to establish any order in the first place.

## Investigation Timeline

1. **p99 latency growth observed** on `/leaderboard/near/{score}` correlating directly with total player count rather than request volume.
2. **Caching considered as a first response**, but investigated against the actual query shape before being adopted as the fix.
3. **Endpoint implementation reviewed**, confirming it re-sorts the entire `HashMap`-backed dataset on every request purely to establish an order the underlying structure doesn't provide.
4. **Query shape analyzed directly**: the endpoint only ever needs a small, bounded window of scores nearest to a given value — not a full ranking of every player.
5. **Structural mismatch confirmed as the cause** — `HashMap` provides no ordering at all, so any "nearest" or "range" query against it requires an explicit, full O(n log n) sort at request time; a `TreeMap`'s `NavigableMap` operations answer the identical query in O(log n) with no sort step, because the data is already maintained in sorted order incrementally as it's written.

## Root Cause

The wrong data structure was chosen for the actual access pattern — `HashMap` has no ordering at all, forcing every "nearest scores" request to pay for a full sort of the entire dataset just to answer a query that only ever needs a small, bounded window around one value.

## Immediate Mitigation

None called out as a stopgap beyond the sizing/caching consideration already investigated; the direct structural fix was pursued rather than a temporary workaround, since the underlying data structure is the actual bottleneck.

## Permanent Fix

Switch the leaderboard's backing structure to a `TreeMap<Score, Player>`, which genuinely supports ordered range queries (`subMap`, `headMap`, `tailMap`, `floorKey`, `ceilingKey`) in O(log n) rather than an O(n log n) full sort per request.

## Alternatives Considered

Adding a cache in front of the existing `HashMap`-plus-full-sort implementation — considered first, but rejected as treating the symptom (repeated identical sort cost) rather than the actual mismatch between the query shape and the data structure; a cache would still require re-sorting on every write or cache invalidation, and doesn't help range queries for arbitrary, non-cached score values.

## Trade-offs

`TreeMap`'s O(log n) get/put is real, structurally slower per individual operation than `HashMap`'s O(1) average case — accepted here because the workload's actual bottleneck is the range-query shape, not raw single-key lookup throughput, and `TreeMap` answers the range query in the same O(log n) instead of an O(n log n) sort.

## Prevention

Any endpoint whose query shape is fundamentally "nearest," "range," or "ordered" should default to a `NavigableMap`/`NavigableSet`-backed structure from the start, rather than bolting sorting onto a `HashMap` after the fact.

## Monitoring and Alerts

- Track p99/p50 latency for the leaderboard endpoint alongside total player count as a labeled dimension, so a latency-versus-dataset-size correlation (the direct signature of an O(n log n)-per-request structural mismatch) is visible on a dashboard rather than requiring an ad hoc investigation to notice.
- Add a query-shape review step to API design for any new "nearest," "top-N near X," or "range between X and Y" endpoint, specifically checking whether the backing store natively supports ordered range queries before implementation begins.
- Alert on CPU time attributable to sorting operations (a profiler tag or a custom timing metric around the sort call) exceeding a fixed budget per request, catching the anti-pattern in staging load tests before it reaches production scale.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a `/leaderboard/near/{score}` endpoint's p99 latency was scaling with total player count, since it re-sorted the entire `HashMap`-backed leaderboard on every request.
- **Task:** find a fix that addressed the actual bottleneck rather than the first available workaround.
- **Action:** considered caching as an initial instinct, but analyzed the endpoint's actual query shape first — a small, bounded "nearest scores" window — and recognized this was exactly what a `NavigableMap`'s range operations answer natively in O(log n).
- **Result:** migrated the leaderboard to a `TreeMap<Score, Player>`, eliminating the per-request full sort entirely and making the endpoint's cost scale with the query's own bounded size rather than total player count.

## Staff-Level Discussion

This scenario is a useful example of the difference between a symptom-level fix and a structural one: a cache would have made repeated identical queries cheap without addressing the fact that the underlying access pattern was fundamentally mismatched to the chosen data structure, and it would have done nothing for the (likely common, for a leaderboard) case of querying "nearest scores" around a value that hasn't been queried before. The deeper, generalizable lesson is that choosing a collection type is a decision that should be driven by the actual query shape the application needs to serve, not by which structure is most familiar or was already in place — `HashMap` is often the reflexive default for "a map," but its lack of ordering is a real, structural limitation the moment "nearest" or "range" enters the requirements. A Staff engineer reviewing a data-access design should ask, for any lookup structure, "what are the actual query shapes this needs to answer, today and in the near future" before the implementation is written, since retrofitting ordering onto an unordered structure after the fact (as this incident's original implementation did, via a full sort) is exactly the kind of decision that looks acceptable at small scale and becomes a measurable production cost as the dataset grows.

## Related Handbook Chapters

- [TreeMap/TreeSet & the Navigable Hierarchy](../syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md) — canonical `NavigableMap`/`NavigableSet` mechanics, the reflectively-proven interface hierarchy, and Red-Black tree height guarantees this fix relies on.
- [Fail-Fast vs. Weakly-Consistent Iterators](../syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md) — related iteration-safety considerations relevant to a leaderboard structure updated concurrently with reads.
