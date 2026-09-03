---
title: "Cheat Sheet: Rate Limiting and Throttling Algorithms"
slug: rate-limiting-and-throttling-algorithms
document_type: cheat-sheet
domain: system-design
topic_id: T-808
canonical: ../handbook/system-design/rate-limiting-and-throttling-algorithms.md
last_updated: 2026-09-02
---

# Rate Limiting and Throttling Algorithms

**Canonical chapter:** [`handbook/system-design/rate-limiting-and-throttling-algorithms.md`](../handbook/system-design/rate-limiting-and-throttling-algorithms.md)

## Core Mental Model

Every rate limiter answers the same question — "has this identity used up its budget in the relevant window of time?" — and every algorithm trades off three things: how exact the answer is, how much memory it costs per identity, and how bursty a legitimate client is allowed to be. Fixed window is cheap and wrong at the boundary. Sliding window log is exact and expensive. Sliding window counter approximates the log cheaply. Token bucket allows controlled burst up to a capacity, then a steady refill rate. Leaky bucket forces a constant output rate regardless of input burstiness — it smooths, not just caps.

## Essential Definitions

- **Rate limiting** — restricts how many requests an identity may make in a time period, protecting a shared resource from being overwhelmed.
- **Throttling** — deliberately slowing/delaying a caller once it exceeds budget, rather than rejecting outright; rate limiting is the decision, throttling is one possible response (the other being HTTP 429).
- **Fixed window counter** — one integer per identity, reset at absolute wall-clock boundaries; allows up to 2x the nominal limit straddling a boundary.
- **Sliding window log** — exact, timestamp-ordered log per identity; O(limit) memory, O(evicted-count) work per call.
- **Sliding window counter** — O(1) approximation using current + previous window counts weighted by overlap fraction; the Cloudflare/Kong production standard.
- **Token bucket** — capacity + continuous refill rate; lazy refill computed on access, no background thread needed; allows burst up to capacity.
- **Leaky bucket** — a fixed-capacity queue drained at a constant rate; smooths bursts into a delayed, steady stream rather than accepting/rejecting instantly.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Limit must be exact, never off by even one request | Sliding window log (if identity cardinality is low) |
| Memory-per-identity is a real constraint (millions of identities) | Sliding window counter or token bucket |
| Legitimate bursts should be allowed as long as the average holds | Token bucket |
| Downstream system needs a truly constant, non-bursty rate | Leaky bucket |
| Running on more than one service instance | Any algorithm, but state must move to shared storage (Redis) |

**Algorithm comparison:**

| Algorithm | Exactness | Memory/identity | Allows burst |
|---|---|---|---|
| Fixed window | Flawed (2x at boundary) | O(1) | Yes, at boundary only |
| Sliding window log | Exact | O(limit) | No |
| Sliding window counter | Approximate | O(1) | Minimal |
| Token bucket | Exact by design | O(1) | Yes, up to capacity |
| Leaky bucket | Exact by design | O(capacity) queued | No — smooths to constant |

## Key Numbers (real, executed Java 21 implementations)

- Boundary-burst attack (limit 10 per 200ms): `FixedWindowCounter` admitted 20 total (10 before + 10 after boundary); `SlidingWindowLog`, `SlidingWindowCounter`, and `TokenBucket` each capped at 10.
- Unsynchronized check-then-increment race: 64 concurrent threads, 3,200 total attempts against a limit of 100 — overshot on 3 of 10 real runs (101-102 admitted). The `synchronized`-guarded version was exact at 100 on every run.
- Leaky bucket smoothing: measured average 104ms gap between completions against an expected 100ms (leak rate 10/s); last of 30 burst-submitted requests completed at real t+3120ms.

## Common Pitfalls

- Saying "sliding window" without specifying log vs. counter — different cost profiles and exactness guarantees.
- Designing the algorithm correctly but never addressing where its state lives once the service runs on more than one instance — the single most common real gap.
- Confusing token bucket's "burst up to capacity" with leaky bucket's "constant output rate" — not interchangeable defaults.
- Treating check-then-increment as atomic without justification — either name the synchronization mechanism or acknowledge the race.
- Using wall-clock time instead of a monotonic clock (`System.nanoTime()`) for window/refill math — an NTP adjustment can corrupt the calculation.

## Interview Answer Skeleton

**30-sec:** A rate limiter restricts how many requests an identity can make in a time window to protect a shared resource. The five standard algorithms trade off exactness, memory cost, and burst tolerance differently; token bucket and sliding window counter are the most common production choices.

**2-min:** Add the real boundary-burst measurement (fixed window: 20 admitted vs. limit 10; sliding-based algorithms correctly capped at 10) and the real concurrency race (3 of 10 runs overshot at 64 threads, unsynchronized). Close on the production reality: sharing state across instances (Redis `INCR`/Lua script) is usually the hard part, not the algorithm.

**Whiteboard:** Draw a timeline with window boundary tick marks; 10 dots clustered just before one boundary, 10 more just after — "fixed window: 20 admitted, limit was 10." Redraw the same 20 dots with a sliding window line moving continuously, showing at most 10 inside any 200ms span — "sliding window: 10 admitted, correctly capped."

**Staff-level framing:** Reason about the operational cost of the shared-state dependency (Redis becomes availability-critical for every rate-limited request), the fail-open/fail-closed decision as a deliberate documented trade-off, and the governance question a shared platform-level rate-limiting layer creates (who owns default limits, how does a team request a higher one).

## Production Warning Signs

- A per-tenant limit appears to triple or double under load with three or more service instances — check whether each instance runs its own independent in-memory limiter with no shared state; the effective limit multiplies by instance count.
- Burst violations cluster suspiciously close to round wall-clock times (every :00 second, every minute mark) — the fixed-window boundary flaw working as designed, not a bug.
- Overshoot is small (a handful of requests) and doesn't reproduce on every run — a hallmark of a genuine unsynchronized check-then-increment race, not a logic bug.
- Rejected requests carry no `Retry-After` header — clients can't distinguish "come back later" from a hard failure and may hammer the endpoint immediately, worsening overload.

## Related

- `handbook/system-design/idempotency.md`
- `handbook/system-design/load-balancing-service-discovery-and-health-checking.md`
- `handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md`
- `syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md`
