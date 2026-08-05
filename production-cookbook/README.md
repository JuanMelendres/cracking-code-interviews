---
title: "Production Cookbook — Index"
document_type: production-cookbook-index
status: draft
last_updated: 2026-08-05
---

# Production Cookbook

The Production Cookbook owns incident-oriented and troubleshooting content per `CLAUDE.md`'s Production Cookbook Standard: context, symptoms, impact, hypotheses, evidence, investigation timeline, root cause, immediate mitigation, permanent fix, alternatives, trade-offs, prevention, monitoring, an interview-story framing, and a Staff-level discussion — each entry referencing its canonical `handbook/` chapter rather than restating it.

## A note on scope and sourcing

Every entry here is elevated from an existing `## Production Scenarios` section already present in a canonical `handbook/` chapter — each of those chapters was written with a symptoms/evidence/diagnosis/mitigation scenario already worked out and grounded in the chapter's own measured demos. This deliverable does not invent new incidents; it takes each scenario's real content and expands it into the Cookbook's full incident-report template (investigation timeline, monitoring and alerts, interview story, Staff-level discussion), staying honest about which fields are direct extensions of the source material and which are new synthesis grounded in it. Per `CLAUDE.md`, no personal experience is fabricated — the Interview Story section in every entry is explicitly framed as a representative scenario to adapt, not a claimed personal history.

72 `handbook/` chapters currently carry a `## Production Scenarios` section; this first batch covers 4, chosen for domain spread (concurrency, JVM, system design, messaging) rather than any other ordering. Further batches will draw from the remaining 68.

## Entries

| Entry | Domain | Companion chapter | What it's about |
|---|---|---|---|
| [Lock-Ordering Deadlock Under Peak Load](lock-ordering-deadlock-under-peak-load.md) | Concurrency | [Deadlock, Race Conditions, and Thread Diagnostics](../handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md) | Two code paths acquiring the same two locks in opposite order deadlock only often enough to matter under peak load; `ThreadMXBean.findDeadlockedThreads()` turns a raw thread dump into a mechanical diagnosis. |
| [Unconditional Heap Growth and Memory Leak Diagnosis](unconditional-heap-growth-and-memory-leak-diagnosis.md) | JVM | [Memory Leak Diagnosis and Heap Dump Analysis](../handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md) | A slow, monotonic, days-long heap-growth trend with no deploy correlation is the signature of an unconditional leak; coarse `jmap -histo:live` sampling before a targeted heap dump finds the specific reference chain fast. |
| [Synchronized Retry Storm Without Jitter](synchronized-retry-storm-without-jitter.md) | System Design / Resilience | [Resilience Patterns](../handbook/system-design/resilience-patterns.md) | Jitter-free exponential backoff synchronizes every caller's retry onto the same instant, turning a 2-second blip into a multi-minute self-inflicted outage on a service that had already recovered. |
| [Kafka Consumer Group Rebalance Storm](kafka-consumer-group-rebalance-storm.md) | Kafka | [Consumer Groups and Rebalancing](../handbook/kafka/consumer-groups-and-rebalancing.md) | A synchronous call added inside the poll loop occasionally exceeds `max.poll.interval.ms`, triggering an eviction-and-rebalance cycle mistaken at first for a networking issue. |

## How this relates to other deliverables

- `handbook/` — canonical mechanics each entry's root cause and fix are grounded in; this Cookbook references them rather than re-teaching them.
- `architecture-atlas/` — full system designs (the shape of a system); this Cookbook covers how a system fails and is diagnosed once running, not how it's designed.
- `behavioral-handbook/` — [Production Incident Narratives](../behavioral-handbook/04-production-incident-narratives.md) covers how to *deliver* an incident story in an interview; this Cookbook supplies the technical substance such a story would be built from.
