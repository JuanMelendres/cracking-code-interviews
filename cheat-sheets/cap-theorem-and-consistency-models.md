---
title: "Cheat Sheet: CAP Theorem and Consistency Models"
slug: cap-theorem-and-consistency-models
document_type: cheat-sheet
domain: system-design
topic_id: T-807
canonical: ../handbook/system-design/cap-theorem-and-consistency-models.md
last_updated: 2026-08-03
---

# CAP Theorem and Consistency Models

**Canonical chapter:** [`syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md`](../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md)

## Core Mental Model

CAP is not a permanent tax — it's a question that only gets asked when the network actually breaks. Outside of a partition, a well-designed system can be both consistent and available; CAP has nothing to say in that case. The moment a partition genuinely occurs, the system must answer one question: does the partitioned-off side keep serving requests (possibly stale) or refuse them (possibly unavailable)? Every real system has already answered this — "we've never thought about it" isn't a third option.

## Essential Definitions

- **CAP theorem** — during an actual network partition, a distributed system must choose between Consistency (every read sees the most recent write, or an error) and Availability (every request gets a non-error response, possibly stale) — not both, for the partition's duration.
- **CP system** — refuses to serve on the minority side of a partition rather than risk stale/conflicting data; gives up availability. E.g. `etcd`, `ZooKeeper`, a strongly consistent config store.
- **AP system** — keeps serving on both sides, accepting they may disagree until reconciliation; gives up consistency. E.g. DNS, a shopping cart, a session store.
- **Strong consistency** — the moment a change is made, every subsequent read from anyone, on any replica, reflects it — at the cost of higher write latency and reduced availability during a partition.
- **Eventual consistency** — a change may not be immediately visible on the next read, even to the same user, if it hits a different replica — but converges eventually.

## Decision Table

| Data type | Right choice | Why |
|---|---|---|
| Financial ledger, inventory count | CP | Overselling/wrong balance is worse than an error |
| Configuration/coordination store | CP | Stale config can cause worse downstream failures than unavailability |
| Session store, shopping cart | AP | Being logged out or unable to add to cart is worse than brief staleness |
| Social feed, "recently viewed" list | AP | Staleness is imperceptible; an error is a worse UX |

**Decision sequence:** Is a partition actually occurring, or is this a general design question? → For this specific data, what's worse: an error, or stale data? → Is one consistency model being applied uniformly, or does different data warrant different models?

## Key Evidence

The production scenario's only concrete figure: a network partition lasting **under one minute** between two data centers caused a full regional outage — a CP config store correctly refused minority-side reads/writes, taking down every dependent service in that DC even though application services themselves were healthy.

## Common Pitfalls

- Treating CAP as an always-active, permanent trade-off rather than specifically about partition behavior
- Answering "what does your system give up" in the abstract, with no real system or real user-facing consequence named
- Assuming eventual consistency is uniformly acceptable across an entire system rather than assessed per data type

## Interview Answer Skeleton

**30-sec:** During an actual partition, choose consistency (reject unguaranteed-current requests) or availability (serve possibly-stale data). Only applies during a partition. A good answer names a real system and what it specifically gives up, not the abstract theorem.

**2-min:** Add `etcd` as the CP example (refuses minority reads/writes) + shopping cart as the AP example (reconciles later) + the config-store outage as the production example.

**Whiteboard:** Draw the partition decision tree (partition occurring? → no: both achievable, CAP says nothing; yes: choose C or A), annotate each branch with one concrete example system plus one sentence of user experience — this is what turns the diagram into the actual answer.

**Staff-level move:** per-data-type consistency modeling, unprompted — partition by staleness tolerance, mirroring the same discipline as cache-invalidation strategy selection.

## Production Warning Signs

- Service-discovery/config lookups fail entirely in one data center during a brief partition, taking down every dependent service there even though the services themselves are healthy — the CP store working exactly as designed, not a malfunction
- Any dependency on a CP system where it hasn't been explicitly reviewed whether *every* consumer genuinely needs the consistency guarantee
- **Fix pattern:** add a bounded, explicitly-stale local fallback cache of last-known-good config for services that can tolerate staleness, without changing the CP guarantee for services (e.g. security policy) that genuinely need it

## Related

- [Idempotency at System Edges](idempotency.md)
- [Caching Strategies and Invalidation](caching-strategies-and-invalidation.md)
