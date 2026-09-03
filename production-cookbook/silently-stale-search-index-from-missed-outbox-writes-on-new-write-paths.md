---
title: "Silently Stale Search Index from Missed Outbox Writes on New Code Paths"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/09-messaging-event-driven/messaging-patterns-and-change-data-capture.md
  - ../syllabus/10-distributed-systems/distributed-transactions-saga-and-outbox.md
source: handbook/system-design/messaging-patterns-and-change-data-capture.md#production-scenarios
---

# Silently Stale Search Index from Missed Outbox Writes on New Code Paths

## Context

*(Representative scenario, grounded directly in this chapter's own real CDC mechanics.)* A product-search index was kept in sync with the primary product database via a transactional outbox — every write path that touched product data had to remember to also write an outbox row.

## Symptoms

Over several years, three separate write paths were found to have been added without that outbox write, silently causing the search index to drift stale for products created via those paths.

## Impact

Products created through three specific write paths never appeared, or appeared with stale data, in search results — a silent data-freshness defect accumulating for years before being fully audited.

## Initial Hypotheses

Better code review discipline would prevent future misses.

## Evidence

Auditing every write path that touched the `products` table found CDC's real, structural advantage directly — the outbox pattern requires *every* current and future write path to remember an extra step, while log-based CDC requires none of them to know CDC exists at all, because it reads from the WAL after the fact regardless of which code path produced the write.

## Investigation Timeline

1. Search-index staleness for products created via three specific write paths identified during an audit of every write path touching the `products` table.
2. "Better code review discipline" proposed as an initial remedy for preventing future misses.
3. The outbox pattern's structural requirement examined directly: every write path, present and future, must remember to write an outbox row for the sync to work.
4. Diagnosis reached that code review had already proven insufficient — it had already missed three separate omissions over several years despite being the existing safeguard.
5. Log-based CDC's structural difference confirmed: it reads from the WAL after the fact, requiring no write path to know CDC exists at all, eliminating the "someone forgot" failure mode entirely rather than mitigating it.

## Root Cause

The outbox pattern's real cost had compounded silently for years — each new write path was a new opportunity to forget the outbox write, and code review had already proven insufficient to catch all three misses.

## Immediate Mitigation

None distinct from the permanent fix — the scenario moves directly to replacing the sync mechanism rather than patching the missed write paths individually as a stopgap.

## Permanent Fix

Replaced the outbox-based sync with a real logical-replication-based CDC pipeline (conceptually identical to this chapter's own demo, in production backed by Debezium) reading directly from the `products` table's WAL — no application code needed to change at all, and the three previously-silent write paths were immediately and automatically included.

## Alternatives Considered

None recorded beyond the outbox-to-CDC migration itself — the scenario treats the structural elimination of the "remember an extra step" requirement as the direct fix rather than a stronger review process for the outbox pattern.

## Trade-offs

The team took on the real, measured operational responsibility this chapter's own retention-risk demo makes concrete — monitoring replication-slot lag as a standing metric, since an unconsumed slot now poses a real WAL-growth risk it didn't before.

## Prevention

Any new "keep two data stores in sync" requirement now defaults to evaluating CDC first, specifically because it removes an entire category of "someone forgot to publish the event" bugs by construction.

## Monitoring and Alerts

- Alert on replication-slot lag exceeding a defined threshold — the specific new operational risk this migration introduced, since an unconsumed logical-replication slot causes WAL to accumulate on the primary and can threaten disk capacity if left unmonitored.
- Retroactively audit historical write paths against the CDC pipeline's coverage (as was done for the outbox pattern) on a recurring basis, not as a one-time migration check, since new write paths added after the migration should be automatically covered but are worth periodically confirming rather than assuming.
- Track search-index freshness (time from a product write to its appearance in the index) as a standing metric independent of the sync mechanism, so a future regression — from either pattern — is caught by observing the actual symptom rather than by auditing write paths reactively.

## Interview Story

This maps directly to the CDC-versus-outbox trade-off question, arrived at through a real accumulated cost rather than a theoretical comparison. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a product-search index synced via a transactional outbox had silently drifted stale for products created through three write paths that were added without the required outbox write, over several years.
- **Task:** decide whether better process discipline was sufficient, or whether the sync mechanism itself needed to change.
- **Action:** audited every write path touching the `products` table and confirmed code review had already failed to catch all three omissions; replaced the outbox-based sync with a logical-replication-based CDC pipeline reading directly from the WAL.
- **Result:** the three previously-silent write paths were immediately and automatically included with zero application-code changes, at the cost of taking on replication-slot-lag monitoring as a new standing operational responsibility.

## Staff-Level Discussion

The outbox pattern's failure mode here is instructive precisely because it isn't a bug in any single write path — each omission was a reasonable, unremarkable oversight by an engineer who simply didn't know (or forgot) that writing to `products` also required an outbox row. That is a structural defect in the pattern's design, not a discipline problem, and "review harder" is the kind of fix that sounds responsible but doesn't actually change the failure rate, since it had already been tried and had already failed three times. The trade-off a Staff engineer should surface when recommending CDC over outbox is not that CDC is free — it introduces its own standing operational responsibility (replication-slot lag monitoring, with a real WAL-growth risk if a slot goes unconsumed) — but that this new cost is a single, well-understood, centrally-monitored concern, in exchange for eliminating an entire recurring bug class that scales with the number of write paths and the number of engineers who don't know they exist.

## Related Handbook Chapters

- [Messaging Patterns and Change Data Capture](../syllabus/09-messaging-event-driven/messaging-patterns-and-change-data-capture.md) — canonical CDC-versus-outbox trade-off analysis this incident reproduces in production form.
- [Distributed Transactions, Saga, and Outbox](../syllabus/10-distributed-systems/distributed-transactions-saga-and-outbox.md) — the outbox pattern's own mechanics and the structural cost this incident demonstrates.
