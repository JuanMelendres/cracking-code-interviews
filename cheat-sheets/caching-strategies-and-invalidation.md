---
title: "Cheat Sheet: Caching Strategies and Invalidation"
slug: caching-strategies-and-invalidation
document_type: cheat-sheet
domain: system-design
topic_id: T-804
canonical: ../handbook/system-design/caching-strategies-and-invalidation.md
last_updated: 2026-08-03
---

# Caching Strategies and Invalidation

**Canonical chapter:** [`handbook/system-design/caching-strategies-and-invalidation.md`](../handbook/system-design/caching-strategies-and-invalidation.md)

## Core Mental Model

A cache is not a performance feature — it's a deliberate correctness trade. The moment a value is cached, the system has two copies of the truth that can disagree. Every caching decision reduces to three questions: when does a cached value get written, when does it get removed/refreshed, and what happens to a request arriving during the gap between those two events? Every failure mode below is a specific, nameable answer to one of those three questions going wrong.

## Essential Definitions

- **TTL** — expires automatically after a fixed duration; serves stale data for up to the full TTL window after a write.
- **Write-through** — write updates cache and DB together, synchronously; a partial failure (DB ok, cache write fails) leaves them disagreeing.
- **Write-behind** — write goes to cache first, DB updated async; data-loss window if cache fails before the async write lands.
- **Cache-aside** — app checks cache, falls through to DB on miss, populates cache; the gap between "DB write" and "cache invalidation" is where disagreement lives.
- **Cache stampede** — a hot entry expires, every concurrent request for that key misses at once, all fall through to the DB simultaneously.
- **Hot key** — a single key taking a disproportionate share of traffic.

## Decision Table

| Situation | What to reach for |
|---|---|
| Data tolerates a bounded staleness window | TTL, sized per data type's tolerance |
| Write path reliably identifies every affected key | Explicit invalidation, backstopped by a short TTL |
| Hot key, read-heavy | Local in-process caching |
| Hot key, write-heavy | Key sharding across suffixed keys |
| Hot key, client-facing content | Edge/CDN caching |
| Cache stampede on expiry | Single-flight coordination, probabilistic early expiration, or stale-while-revalidate |
| Entire cache could become unavailable | Explicit graceful-degradation path (circuit breaker, stale-serving, load shedding) |

**Solves / does NOT solve:**

| Mechanism | Solves | Does NOT solve |
|---|---|---|
| TTL | Bounds staleness automatically, self-healing | The cache-aside race |
| Versioned cache keys | Stale write overwriting a newer one | Stampede |
| Single-flight coalescing | Stampede on a single key | Full-cache-unavailability stampede |
| Circuit breaking / graceful degradation | Full-cache-unavailability stampede | Individual-key staleness or the aside-race |

## Key Numbers (measured, 50 genuinely concurrent threads)

- Naive cache-aside (no coordination): **50 database load calls** for one expired key
- Single-flight (request coalescing): **1 database load call**, coordination working as intended

Both mechanisms are O(1) per-request overhead — the value is eliminating redundant O(concurrent misses) DB load, not algorithmic complexity.

## Common Pitfalls

- Treating TTL as sufficient invalidation without considering the concurrent-write race
- Not modeling what happens to the DB when the *entire* cache — not just one key — becomes unavailable
- Proposing a single stampede fix without recognizing the three mechanisms address different situations (single-flight ≠ early refresh ≠ stale-while-revalidate)

## Interview Answer Skeleton

**30-sec:** A cache trades latency for a correctness question (how stale, what happens in the write→cache-reflects-it gap). Stampede is real and measurable; the fix is coordinating concurrent misses, not changing the expiration policy alone.

**2-min:** Add the four invalidation mechanisms + failure modes, the single-flight latency-sharing trade-off, and the 50-calls-vs-1-call measured evidence.

**Whiteboard:** Draw the sequence (client → cache → miss → DB → cache populated → client), then 50 arrows converging on one DB box (stampede), then cross out 49 and replace with one arrow labeled "wait on in-flight future" to show single-flight visually.

## Production Warning Signs

- Sudden DB load spike with no corresponding traffic increase
- DB load spikes specifically during cache-cluster maintenance
- **Real incident:** routine cache-node replacement briefly took the whole cache unavailable → full-working-set stampede against a DB sized only for cache-assisted load → near-total connection-pool exhaustion. Fix: rolling (never full) cache maintenance, plus a DB-side circuit breaker/graceful-degradation path.

## Related

- [System Design Method and Estimation](system-design-method-and-estimation.md)
- [Distributed Systems Failure Modes](distributed-systems-failure-modes.md)
- `handbook/databases/index-structures-btree-composite-covering.md`
