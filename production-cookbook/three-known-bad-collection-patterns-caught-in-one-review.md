---
title: "Three Known-Bad Collection Patterns Caught in One Review"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/collections/collection-selection-decision-matrix.md
source: handbook/collections/collection-selection-decision-matrix.md#production-scenarios
---

# Three Known-Bad Collection Patterns Caught in One Review

## Context

A new feature's pull request includes three separate collection choices, none reviewed against their actual access pattern before being written.

## Symptoms

During code review, a reviewer notices three separate collection choices in the same pull request that each match a known-bad pattern: an unbounded `LinkedBlockingQueue` for a new ingestion buffer, a `HashMap` shared across a newly added background thread, and a `LinkedList` used purely for its `get(index)` calls in a hot loop.

## Impact

Without the review catching all three, the service would likely have reproduced three separate, previously diagnosed production incidents — an eventual OOM from the unbounded queue, silent corruption from the unsynchronized shared `HashMap`, and a latency regression from `LinkedList`'s O(n) indexed access — simultaneously, in one release.

## Initial Hypotheses

Not applicable — this scenario is the successful case: the review process worked as intended, catching the defects before merge rather than diagnosing them after an incident.

## Evidence

Each of the three patterns matches a previously diagnosed production scenario directly: the unbounded-queue pattern matches the `BlockingQueue` chapter's own production incident; the shared-`HashMap`-under-concurrency pattern matches the `ConcurrentHashMap` chapter's scenario — here, worse, since a plain `HashMap` was used at all rather than even attempting `ConcurrentHashMap`; and the `LinkedList` indexed-access pattern matches the `ArrayList`/`LinkedList` chapter's regression scenario.

## Investigation Timeline

Not applicable in the incident-investigation sense — this scenario describes prevention, not diagnosis. The relevant sequence is procedural:

1. **Pull request submitted** with three new collection fields, none stated against an access-pattern rationale.
2. **Reviewer applies the standing "what's the access pattern" checklist question** to each new collection field individually.
3. **All three flagged simultaneously**, each matching a previously documented incident pattern from a different chapter.
4. **Fixes required before merge**: a bounded queue with explicit capacity, `ConcurrentHashMap` for the shared map, `ArrayList` for the indexed-read loop.

## Root Cause

All three defects share the same root cause: the collection type was chosen without first stating the actual access pattern — concurrent or not, indexed-read-heavy or not, needing real backpressure or not.

## Immediate Mitigation

The reviewer requests all three be fixed before merge: a bounded queue with an explicit capacity, `ConcurrentHashMap` for the shared map, and `ArrayList` for the indexed-read loop.

## Permanent Fix

Add "state the dominant access pattern for every new collection field" as an explicit code-review checklist item, specifically because these three mistakes are common enough, and costly enough individually, to warrant a standing process check rather than relying on catching them ad hoc.

## Alternatives Considered

Relying on load testing alone to catch these before production. Rejected as a weaker, later, and more expensive signal than a design-time review question that takes seconds to ask.

## Trade-offs

A small amount of additional review-time overhead per pull request touching a new collection field. Accepted, given the demonstrated cost of each individual mistake elsewhere as its own full production incident.

## Prevention

Making "what is the access pattern for this collection" a standing, explicit review question, rather than trusting that each individual JDK class's trade-offs will be recalled correctly in the moment by whoever writes the code.

## Monitoring and Alerts

- The review checklist item itself, tracked as a required field in the pull-request template for any diff touching a collection field declaration, rather than left to reviewer memory — this converts a review habit into a structural gate.
- A static-analysis rule flagging the three specific known-bad patterns directly (unbounded `BlockingQueue`/`Executors` construction, non-concurrent map types captured by a background thread, `LinkedList` used with indexed access) as a complementary, automated backstop to human review.

## Interview Story

This maps to "what questions do you ask before choosing a collection type" — the synthesis version of several individually diagnosable incidents. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a single pull request contained three separate collection choices, each independently capable of reproducing a known, previously diagnosed production incident.
- **Task:** explain how one review question caught all three simultaneously.
- **Action:** apply "what is the access pattern for this collection" to each new field, rather than reviewing each collection type's correctness in isolation; recognize each pattern's match to a previously documented incident class.
- **Result:** required all three fixes before merge, and formalized the access-pattern question as a standing review checklist item and a complementary static-analysis rule.

## Staff-Level Discussion

This scenario is valuable specifically because it's the successful case, not a failure — it demonstrates that the same underlying question ("what is this collection's actual access pattern?") generalizes across what look like three unrelated defect types (a backpressure bug, a concurrency-safety bug, a complexity bug), because all three stem from the identical underlying mistake: choosing a JDK collection class by habit or convenience rather than by its stated performance and safety contract against the code's actual usage. A Staff engineer's highest-leverage contribution to code review isn't catching each individual defect type separately — it's recognizing and institutionalizing the one question that catches an entire class of defects at once, converting three separate "remember this specific gotcha" burdens into a single, repeatable review habit.

## Related Handbook Chapters

- [Collection Selection Decision Matrix](../syllabus/02-java/collections/collection-selection-decision-matrix.md) — canonical access-pattern decision framework used here.
- [BlockingQueue Family](../syllabus/02-java/collections/blockingqueue-family.md) — the unbounded-queue incident this pattern matches.
- [HashMap Internals](../syllabus/02-java/collections/hashmap-internals.md) — the shared-mutable-map-under-concurrency risk this pattern matches.
