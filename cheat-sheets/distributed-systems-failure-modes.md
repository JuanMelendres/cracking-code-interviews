---
title: "Cheat Sheet: Distributed Systems Failure Modes"
slug: distributed-systems-failure-modes
document_type: cheat-sheet
domain: system-design
topic_id: T-909
canonical: ../handbook/system-design/distributed-systems-failure-modes.md
last_updated: 2026-08-03
---

# Distributed Systems Failure Modes

**Canonical chapter:** [`handbook/system-design/distributed-systems-failure-modes.md`](../handbook/system-design/distributed-systems-failure-modes.md)

## Core Mental Model

A network can't distinguish "lost," "slow," and "succeeded but the response was lost" — every mechanism in this chapter is a structural answer to that one ambiguity. A single-process call either completes or the process crashes; across a network, all the in-between states are common, and a timeout tells you nothing about which one happened. Retries, idempotency keys, and fencing tokens are three necessary answers to the same underlying fact, not a memorized list.

## Essential Definitions

- **Retry amplification** — retrying a slow request adds load *on top of* the still-running original (doesn't replace it); under fixed downstream capacity this turns a slowdown into an outage.
- **Idempotency key** — client-supplied key on every retry of the same logical op; server recognizes in-flight/completed work and returns the original result instead of re-executing.
- **Split-brain** — two nodes both believe they're leader (paused node's lease expires unnoticed, new leader elected, paused node's stale write later accepted by shared storage).
- **Fencing token** — monotonically increasing number per lease; storage rejects any write carrying a token older than the highest it's seen. Must live at the storage layer, never the nodes themselves.
- **Circuit breaker** — stops issuing new calls to a dependency once its error rate crosses a threshold, instead of retrying into a degraded dependency indefinitely.

## Decision Table

| Mechanism | Protects against | Where it must live |
|---|---|---|
| Exponential backoff + jitter | Retry amplification turning a slowdown into an outage | Client-side retry logic |
| Idempotency keys | Duplicate side effects from a retried, ambiguous-outcome request | Server-side, checked before any non-idempotent action |
| Fencing tokens | Split-brain corruption from a stale former leader's write | Storage/resource layer — never the nodes claiming leadership |
| Circuit breakers | Cascading failure from calling an already-degraded dependency | Client-side, wrapping outbound calls |

## Key Numbers (measured retry-amplification trace)

Downstream capacity 4, degraded to 400ms/unit, 12-request burst, 700ms client timeout:

| Strategy | Elapsed | Success within SLA | Downstream work units |
|---|---|---|---|
| No retry | 708ms | 4/12 | 12 |
| Retry, no backoff | 2114ms | 4/12 | 28 (2.3x load, same success rate) |
| Retry, exponential backoff + jitter | 2606ms | **12/12** | 24 (2.0x — less amplification, full success) |

All three mechanisms (backoff, idempotency check, fencing check) are O(1) per request — the value is correctness under partial failure, not algorithmic cost.

## Common Pitfalls

- Believing a timeout definitively means the request failed, rather than merely ambiguous
- Adding retries as a default resilience measure with no backoff, jitter, or idempotency mechanism
- Assuming leader election alone (without fencing) prevents split-brain — election answers "who's elected," not "can a stale former leader still cause damage"

## Interview Answer Skeleton

**30-sec:** Network can't distinguish lost/slow/succeeded-but-lost-response. Naive retries amplify an outage because they add load on top of still-running attempts. Idempotency keys and fencing tokens are the structural fixes for retry ambiguity and split-brain — protocol decisions, not tuning knobs.

**2-min:** Add the ambiguity mechanism + the measured trace (2.3x load/3x time for same 4/12 success without backoff, vs 12/12 success with less amplification using backoff).

**Whiteboard:** Draw a timeline box for one request ("submitted," width = processing time), a vertical line marking client timeout mid-box, label the post-timeout region "still running, not cancelled." Draw a second retried-request box starting at the timeout line, overlapping the tail of the first — the two stack rather than replace, making "retries add load" self-evident.

## Production Warning Signs

- Each upstream service's outbound call volume to a slow dependency is several times its inbound request rate during an incident — retry amplification
- Retry configs use immediate, fixed-count retries with no backoff/jitter
- An outage's blast radius keeps growing after the initial trigger resolves — retry amplification still generating load from earlier attempts
- **Real incident:** a single downstream slowdown cascaded into three unrelated upstream services failing within minutes. Immediate fix: disable retries to stop the amplification loop. Permanent fix: exponential backoff + jitter on every retry policy, plus a circuit breaker.

## Related

- [Caching Strategies and Invalidation](caching-strategies-and-invalidation.md)
- [System Design Method and Estimation](system-design-method-and-estimation.md)
- `handbook/databases/isolation-levels-and-concurrency-anomalies.md`
