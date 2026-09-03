---
title: "Unbounded BlockingQueue Losing Backpressure Into an OOM"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/collections/blockingqueue-family.md
source: handbook/collections/blockingqueue-family.md#production-scenarios
---

# Unbounded BlockingQueue Losing Backpressure Into an OOM

## Context

A service ingests events from an upstream source and processes them via a background worker pool, buffering incoming events in a `LinkedBlockingQueue` constructed with no capacity argument — unbounded by default.

## Symptoms

During an incident where the downstream processing step becomes slow, not failed, the service's memory usage climbs steadily and it eventually crashes with `OutOfMemoryError`.

## Impact

A slowdown in one downstream dependency, which should have caused at most a processing delay, instead crashes the entire ingesting service.

## Initial Hypotheses

- A memory leak unrelated to the incident — checked and ruled out; heap analysis shows the overwhelming majority of retained memory is queued event objects, not leaked application state.
- A sudden ingestion spike — checked and ruled out; ingestion rate was within normal historical bounds.
- The unbounded queue accepting events faster than the slowed-down processing step can drain them — correct.

## Evidence

Heap dump analysis at crash time shows millions of queued event objects, all waiting for the now-slow downstream processing step, with the ingestion rate exceeding the reduced processing rate for the duration of the incident.

## Investigation Timeline

1. **Steady memory climb observed** during an incident where the downstream processing step was slow but not failed.
2. **Memory-leak and ingestion-spike hypotheses ruled out** using heap-dump composition and ingestion-rate history.
3. **Heap dump examined directly**, revealing millions of queued event objects as the dominant retained memory.
4. **Arrival-vs-drain-rate mismatch confirmed**: ingestion continued at its normal rate while the processing step's drain rate fell, and the unbounded queue absorbed the entire gap.

## Root Cause

An unbounded queue removes the backpressure signal entirely. As the downstream got slower, the queue simply absorbed the growing backlog instead of the producer — the ingestion path — ever being made to wait, until available memory ran out.

## Immediate Mitigation

Restart the service, clearing the backlog, and manually throttle ingestion while the downstream dependency recovers.

## Permanent Fix

Replace the unbounded `LinkedBlockingQueue` with an explicitly bounded one — or an `ArrayBlockingQueue` — sized to a deliberate, reasoned capacity, converting the failure mode from silently growing memory until crash to the ingestion path blocking, or a bounded-queue-specific rejection policy firing, applying real backpressure to whatever is producing events faster than they can be consumed.

## Alternatives Considered

Scaling up the processing worker pool alone. Rejected as treating the symptom — a genuinely slow, not merely under-provisioned, downstream dependency would still eventually overwhelm any fixed processing rate, and the actual fix is bounding the queue so the failure mode becomes visible backpressure instead of an eventual crash.

## Trade-offs

A bounded queue means the ingestion path can now block, or reject, during a genuine downstream slowdown, rather than silently absorbing unlimited backlog. Accepted, since the alternative — an eventual OOM crash — is strictly worse for the system as a whole.

## Prevention

Any `BlockingQueue` construction should specify an explicit, deliberately reasoned capacity. An unbounded queue in a production pipeline should be treated as a specific, flagged design decision, not a default.

## Monitoring and Alerts

- Queue depth as a first-class metric on every internal buffering queue in the pipeline, alerted on a rising trend well before heap exhaustion — this is the earliest available signal, visible long before an OOM crash and directly explaining the mechanism.
- Ingestion rate versus processing (drain) rate tracked as a ratio; a ratio sustained above 1 for more than a brief window is the precise, mechanical definition of this failure mode in progress, regardless of which specific queue is involved.

## Interview Story

This maps to "your service crashed with OOM during a downstream slowdown, why" directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** an ingestion service crashed with `OutOfMemoryError` during an incident where a downstream processing step merely slowed down, not failed.
- **Task:** explain how a slow, not down, dependency caused a full crash rather than a bounded delay.
- **Action:** rule out a memory leak and an ingestion spike using heap-dump composition and rate history; identify the queued-event-dominated heap; connect the arrival-rate/drain-rate mismatch to the default unbounded `LinkedBlockingQueue`.
- **Result:** replaced the queue with an explicitly bounded implementation, converting a silent, unbounded failure mode into visible, controllable backpressure.

## Staff-Level Discussion

This is the same structural mistake as an unbounded `ThreadPoolExecutor` queue, arrived at from a different code path — any unbounded buffer between a variable-rate producer and a variable-rate consumer removes the system's only natural backpressure mechanism, deferring a visible slowdown into an eventual, catastrophic crash. The pattern is worth naming explicitly rather than fixing case by case: any queue, buffer, or channel connecting two components with independent throughput should default to bounded, with the bound and the resulting behavior under saturation (block, reject, drop) chosen deliberately at design time. A Staff engineer reviewing a new pipeline component should treat every unbounded internal buffer as a finding regardless of whether it has caused an incident yet — the failure mode is latent until the first sustained downstream slowdown, which is exactly the kind of event a system should be designed to survive gracefully rather than crash under.

## Related Handbook Chapters

- [BlockingQueue Family](../syllabus/02-java/collections/blockingqueue-family.md) — canonical bounded-queue and backpressure mechanics used here.
- [Executors and Thread Pool Sizing](../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md) — the analogous unbounded-executor-queue failure mode.
