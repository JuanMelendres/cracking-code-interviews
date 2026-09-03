---
title: "Read-Your-Own-Writes Gap Hiding a Just-Created Order on the Confirmation Page"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/06-databases/replication-read-replicas-and-replica-lag.md
  - ../syllabus/17-architecture/cqrs-read-write-separation.md
source: handbook/databases/replication-read-replicas-and-replica-lag.md#production-scenarios
---

# Read-Your-Own-Writes Gap Hiding a Just-Created Order on the Confirmation Page

## Context

A checkout flow writes a new order to the primary database, then immediately redirects to a confirmation page that reads the order back — from a read replica, per the application's read/write split.

## Symptoms

Under normal load, this works fine. Under moderate load spikes, a real, measurable fraction of confirmation-page loads show "order not found" or stale (pre-order) account state.

## Impact

A user-visible correctness bug at exactly the moment (order confirmation) where trust matters most, appearing intermittently and only under load — genuinely hard to reproduce in a low-traffic staging environment.

## Initial Hypotheses

- A bug in the order-creation transaction itself — checked and ruled out; the write genuinely commits successfully on the primary every time.
- A caching layer serving stale data — checked and ruled out; no cache sits between the application and the replica for this path.
- The read-your-own-writes gap inherent to asynchronous replication — correct.

## Evidence

Under load, real replica lag (even if usually sub-millisecond to low-single-digit-ms under normal conditions) occasionally grows large enough — WAL-streaming and apply cost scales with primary write volume — that the confirmation-page read genuinely lands before the replica has applied the just-committed write.

## Investigation Timeline

1. Intermittent "order not found" and stale-state reports on the confirmation page observed, correlating with periods of moderate load.
2. Order-creation transaction on the primary audited and confirmed to commit successfully every time — ruling out a write-side bug.
3. Caching layer checked and confirmed absent from this specific read path — ruling out stale-cache as the cause.
4. Replica-lag behavior measured under load, showing lag growing from its normal sub-millisecond-to-low-single-digit-ms baseline as primary write volume increased.
5. Confirmed the confirmation-page read was landing on the replica before the just-committed write had been applied there — the structural read-your-own-writes gap of asynchronous replication.

## Root Cause

Asynchronous replication offers no guarantee that a replica reflects a write that just committed on the primary, and the gap widens under load precisely when it's most likely to be hit — more concurrent writes to replicate, more concurrent reads racing them.

## Immediate Mitigation

Route the confirmation-page read to the primary specifically for this one request — a targeted, deliberate exception to the read/write split — immediately eliminating the staleness window for this specific, correctness-sensitive path.

## Permanent Fix

Establish an explicit policy: reads that must reflect the client's own immediately-preceding write (order confirmations, "your comment was posted," account-balance-after-transfer) go to the primary; reads that can tolerate real, bounded staleness (a public product listing, an activity feed) go to replicas. Document this per-endpoint rather than applying one blanket read/write split everywhere.

## Alternatives Considered

Switching to synchronous replication for all replicas — rejected as a global fix for a narrow problem; it would add real write latency to every transaction, for every replica, to solve a staleness issue that only matters for a specific subset of reads.

## Trade-offs

Routing specific reads to the primary adds real load back to it for those paths — accepted, since it's scoped precisely to the reads that actually need read-your-own-writes consistency, not applied globally.

## Prevention

Any read/write split design should explicitly classify each read path as "can tolerate real replica lag" or "must reflect the client's own preceding write," rather than defaulting every read to a replica uniformly.

## Monitoring and Alerts

- Alert on measured replica lag exceeding a defined threshold correlated with primary write-volume spikes — this incident's own evidence shows lag growing specifically under load, so a lag metric that only samples during quiet periods would miss the exact window this bug occurs in.
- Track the "order not found on confirmation" or equivalent stale-read error rate as its own dashboard signal, distinct from general error rate, since it is the concrete, user-visible symptom of a read-your-own-writes violation and should be watchable independently of raw replica-lag numbers.
- Any new endpoint added to the read/write split should carry an explicit tag (primary-required vs. replica-tolerant) reviewed at the time it's added, rather than discovered as a gap only when a load spike surfaces it in production.

## Interview Story

This maps directly to "can a read replica ever return incorrect data, and how would you handle that?" arriving as a real, load-correlated, user-visible correctness bug. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a checkout confirmation page intermittently showed "order not found" for orders that had just been created, only under moderate load.
- **Task:** find the cause, ruling out the more obvious write-side and caching explanations first.
- **Action:** confirmed the order-creation transaction and caching layer were not at fault; measured replica lag under load and found it growing specifically during the load spikes correlated with the symptom.
- **Result:** routed the confirmation-page read to the primary as an immediate fix, then established a documented, per-endpoint policy classifying reads as primary-required or replica-tolerant.

## Staff-Level Discussion

The load-correlated nature of this bug is what makes it expensive: it is nearly invisible in staging, where traffic is light and replica lag stays negligible, and appears specifically during the traffic conditions — moderate-to-high load — that a team is least equipped to debug interactively. A blanket read/write split is an appealing simplification because it requires no per-endpoint judgment, but this incident shows that simplification has a real correctness cost hidden inside it, one that only manifests probabilistically. The organizational fix — an explicit, documented, per-endpoint classification of read consistency requirements — is a small amount of upfront design discipline that trades away the appeal of a single uniform rule for correctness on the paths where it actually matters, and a Staff engineer should treat "which reads need read-your-own-writes" as a standing question for every new endpoint added to a system with asynchronous replicas, not a lesson re-learned after each new correctness bug.

## Related Handbook Chapters

- [Replication, Read Replicas, and Replica Lag](../syllabus/06-databases/replication-read-replicas-and-replica-lag.md) — canonical explanation of asynchronous replication lag and the read-your-own-writes gap this incident reproduces.
- [CQRS: Read/Write Separation](../syllabus/17-architecture/cqrs-read-write-separation.md) — the broader pattern of routing reads differently from writes, and the consistency trade-offs that decision carries.
