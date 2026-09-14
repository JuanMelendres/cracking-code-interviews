---
title: "Interview Question Bank — 11-system-design"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../11-system-design/INDEX.md
  - 10-distributed-systems.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — System Design

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 9 chapters yielded 18 deep questions + 30 quick-fire
questions = **48 real questions**. No Junior Fundamentals chapter exists in this
domain — system design presupposes backend fundamentals already covered elsewhere.

---

## Caching Strategies and Invalidation

### Q1 — Cache and database disagree. How did it happen, how do you detect it, how do you fix it?

**Canonical treatment:** [§ Interview Questions, Q1](../../11-system-design/caching-strategies-and-invalidation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes only "the cache didn't get updated" without the specific race condition — the common mistake this question targets.
- **Senior:** Describes the race correctly and in the precise order — a cache-aside read populates a stale value *after* a concurrent write already invalidated/updated the cache.
- **Staff:** Proposes both a detection method (sampling or instrumented invalidation misses) and a structural fix (versioned keys), not just "add a shorter TTL."

### Q2 — Your cache dies at peak. Walk through what happens to the database.

**Canonical treatment:** [§ Interview Questions, Q2](../../11-system-design/caching-strategies-and-invalidation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as "the cache is just slower now" rather than a systemic capacity event — the common mistake this question targets.
- **Senior:** Identifies the full-stampede nature explicitly — every request previously served from cache now falls through simultaneously, against a database sized for a cache-assisted load.
- **Staff:** Proposes a circuit breaker or graceful-degradation strategy (stale data, rate-limited reads, fast-fail) rather than letting every request hit the now-defenseless database.

---

## Idempotency at System Edges

### Q1 — Make a payment endpoint idempotent. Full mechanism — key, storage, TTL, concurrent-duplicate behavior.

**Canonical treatment:** [§ Interview Questions, Q1](../../11-system-design/idempotency.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes only the key concept without the storage mechanism (a unique constraint doing the actual coordination) or the TTL/crash-recovery case — the common mistake this question targets.
- **Senior:** Describes key, storage, and basic duplicate detection.
- **Staff:** Covers all pieces including TTL-based recovery from a crashed (not just slow) in-progress attempt, and explains why the unique constraint — not application-level locking — makes the mechanism correct under real concurrency.

### Q2 — What does the client do when it never receives the response?

**Canonical treatment:** [§ Interview Questions, Q2](../../11-system-design/idempotency.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes the client should somehow determine whether the operation succeeded before retrying — structurally impossible in the general case — the common mistake this question targets.
- **Senior:** States that retry-with-same-key is correct.
- **Staff:** Explains precisely why this resolves the ambiguity from distributed failure modes — the client doesn't need to resolve it, the server does.

---

## Load Balancing, Service Discovery, and Health Checking

### Q1 — Why might least-connections outperform round-robin, and when would it not matter?

**Canonical treatment:** [§ Interview Questions, Q1](../../11-system-design/load-balancing-service-discovery-and-health-checking.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States least-connections is generally "better," with no mechanism or caveat — the common mistake this question targets.
- **Senior:** Correctly names the runtime-signal distinction (least-connections uses live in-flight load; round-robin uses none) and the homogeneous-backend exception.
- **Staff:** Cites or reasons toward a concrete magnitude (this chapter's own measurement: ~4.4x under a 40x cost-variance scenario) and names the small tracking cost least-connections pays that round-robin doesn't.

### Q2 — A backend crashes. How long before the load balancer stops sending it traffic, and how would you actually know?

**Canonical treatment:** [§ Interview Questions, Q2](../../11-system-design/load-balancing-service-discovery-and-health-checking.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Claims detection is instant, or names no concrete mechanism at all — the common mistake this question targets.
- **Senior:** Names check interval plus probe timeout as the real, bounded answer.
- **Staff:** Proposes combining active and passive checking to tighten the effective bound, and states the real number should be measured (per this chapter's own real 206ms result), not assumed from configuration alone.

---

## Rate Limiting and Throttling Algorithms

### Q1 — Implement a token bucket rate limiter.

**Canonical treatment:** [§ Interview Questions, Q1](../../11-system-design/rate-limiting-and-throttling-algorithms.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Produces a working single-threaded implementation with correct capacity clamping, but no thread-safety.
- **Senior:** Adds explicit thread-safety (a lock or atomic CAS loop) and can explain why lazy refill (computing elapsed time on each call) is preferred over eager background-thread refill in production.
- **Staff:** Extends to the distributed case — a Redis-backed design using an atomic Lua script combining refill-and-consume in one round trip.

### Q2 — Why does a fixed-window rate limiter allow more than its configured limit?

**Canonical treatment:** [§ Interview Questions, Q2](../../11-system-design/rate-limiting-and-throttling-algorithms.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that the flaw exists, without a precise mechanism — the common mistake this question targets, often confused with the separate concurrency race.
- **Senior:** Explains the mechanism with a concrete numeric example — the window resets at an absolute wall-clock boundary with no memory of prior activity, so a client can send its full limit at the last instant of one window and its full limit again at the first instant of the next, close to 2x the nominal rate.
- **Staff:** Names sliding window counter as the standard production fix and explains its O(1) cost advantage over sliding window log.

---

## Real-Time Delivery: WebSocket, SSE, Long-Polling, and Push

### Q1 — What's the real advantage of long-polling over short-polling, and what does it cost?

**Canonical treatment:** [§ Interview Questions, Q1](../../11-system-design/realtime-delivery-websocket-sse-and-long-polling.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that long-polling is "more efficient" without explaining the held-open-connection mechanism — the common mistake this question targets, often conflating it with lower latency rather than lower request count.
- **Senior:** Explains the wait/notify-style mechanism precisely and quantifies the request-count difference with a concrete example.
- **Staff:** Discusses the server-side resource cost of many simultaneously held-open connections as a genuine capacity-planning concern, not a free win.

### Q2 — Why can't SSE replace WebSocket for a chat application?

**Canonical treatment:** [§ Interview Questions, Q2](../../11-system-design/realtime-delivery-websocket-sse-and-long-polling.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that SSE is "one-way" without explaining what that means for a chat feature specifically — the common mistake this question targets.
- **Senior:** Explains the one-way limitation precisely and names WebSocket's actual advantage — a single connection, both directions, either side initiates.
- **Staff:** Discusses when a hybrid approach (SSE for receiving, ordinary HTTP POST for sending) might actually be an acceptable, simpler alternative to WebSocket for a lower-frequency messaging feature.

---

## Resilience Patterns: Circuit Breaker, Retry Jitter, Timeouts, and Bulkheads

### Q1 — Set the timeout — from what data?

**Canonical treatment:** [§ Interview Questions, Q1](../../11-system-design/resilience-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Picks an arbitrary number without justifying it from data — the common mistake this question targets.
- **Senior:** Justifies a percentile-derived timeout (commonly p99 as a starting point), stating the trade-off between false timeouts (too aggressive) and slow failure detection (too lax).
- **Staff:** Discusses that a highly skewed distribution (100ms p50 vs. 3s p99) might warrant a shorter timeout with a retry, rather than one long timeout.

### Q2 — Circuit opens. What does the user see?

**Canonical treatment:** [§ Interview Questions, Q2](../../11-system-design/resilience-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "the circuit opened" as the end of the design question rather than the start of "what's the fallback behavior" — the common mistake this question targets.
- **Senior:** Distinguishes fast-fail from graceful degradation as two different responses to the same open-circuit event.
- **Staff:** Ties the choice to the specific business cost of each dependency being unavailable — e.g., a recommendations service failing open to "no recommendations shown" versus a payments service that must fail loudly rather than silently degrade.

---

## Search and Indexing Systems

### Q1 — Why can't a database use a standard index for `LIKE '%term%'`?

**Canonical treatment:** [§ Interview Questions, Q1](../../11-system-design/search-and-indexing-systems.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that `LIKE '%term%'` is "slow" without explaining why an index can't help — the common mistake this question targets.
- **Senior:** Explains the prefix-searchability reasoning precisely — a standard B-tree index is ordered and searchable from a known prefix, but a leading wildcard means the term could start anywhere, so there's no prefix to binary-search from — and names an inverted index as the structural fix.
- **Staff:** Connects this to a real production scaling incident and the decision between database-native FTS and a dedicated search engine as the fix.

### Q2 — What does BM25 correct that plain TF-IDF doesn't?

**Canonical treatment:** [§ Interview Questions, Q2](../../11-system-design/search-and-indexing-systems.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that BM25 is "more accurate" without naming either specific correction — the common mistake this question targets.
- **Senior:** Names both corrections precisely — document-length normalization and term-frequency saturation — ideally with a concrete example of a long, keyword-stuffed document being over-ranked under plain TF-IDF.
- **Staff:** Discusses how to verify relevance quality in practice (measuring real ranking output against known-relevant documents) rather than assuming a scoring function works as intended.

---

## Storage Selection Trade-offs

### Q1 — Choose between PostgreSQL and DynamoDB for a given workload. Defend it, then argue the opposite.

**Canonical treatment:** [§ Interview Questions, Q1](../../11-system-design/storage-selection-tradeoffs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Picks a database by reputation ("DynamoDB scales better") with no access-pattern reasoning — the common mistake this question targets.
- **Senior:** Reaches a defensible choice using the access-pattern method for the specific workload given.
- **Staff:** Genuinely argues the opposite side, not a token concession — naming the specific access-pattern change that would flip the decision.

### Q2 — When would polyglot persistence be worth its operational cost?

**Canonical treatment:** [§ Interview Questions, Q2](../../11-system-design/storage-selection-tradeoffs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats polyglot persistence as a default good practice rather than a cost/benefit call — the common mistake this question targets.
- **Senior:** Identifies at least one legitimate polyglot use case — e.g., search needing a dedicated text-search engine alongside the primary relational store.
- **Staff:** Names the operational cost side explicitly and unprompted (extra backup/monitoring/on-call surface area) and weighs it against the access-pattern benefit.

---

## System Design Method and Estimation

### Q1 — Walk me through your design method before you start drawing anything.

**Canonical treatment:** [§ Interview Questions, Q1](../../11-system-design/system-design-method-and-estimation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Starts drawing components immediately with no stated method — the common mistake this question targets.
- **Senior:** Follows the six-phase method even if not narrated explicitly upfront.
- **Staff:** States the method explicitly, unprompted, before phase 1 even begins — signaling procedure discipline from the first second.

### Q2 — Estimate QPS and storage for a system with 10M DAU. Show every assumption.

**Canonical treatment:** [§ Interview Questions, Q2](../../11-system-design/system-design-method-and-estimation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Presents a bare final number with no visible reasoning — the common mistake this question targets.
- **Senior:** Produces the worked estimate with every assumption stated.
- **Staff:** Revises live when challenged and shows the downstream architectural consequence of the changed assumption (e.g., "at 5x peak, the single-cache design might need to become a sharded cache").

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | How does cache/database disagreement typically happen? | [Caching Strategies and Invalidation](../../11-system-design/caching-strategies-and-invalidation.md#flashcards) |
| 2 | What happens to the database when the entire cache dies at peak? | [Caching Strategies and Invalidation](../../11-system-design/caching-strategies-and-invalidation.md#flashcards) |
| 3 | Name three cache-stampede fixes. | [Caching Strategies and Invalidation](../../11-system-design/caching-strategies-and-invalidation.md#flashcards) |
| 4 | Name three hot-key mitigations. | [Caching Strategies and Invalidation](../../11-system-design/caching-strategies-and-invalidation.md#flashcards) |
| 5 | What does an idempotency key actually protect against? | [Idempotency at System Edges](../../11-system-design/idempotency.md#flashcards) |
| 6 | What coordinates concurrent duplicate requests correctly? | [Idempotency at System Edges](../../11-system-design/idempotency.md#flashcards) |
| 7 | Why is a TTL necessary on the mechanism? | [Idempotency at System Edges](../../11-system-design/idempotency.md#flashcards) |
| 8 | What's the correct client behavior when a response never arrives? | [Idempotency at System Edges](../../11-system-design/idempotency.md#flashcards) |
| 9 | What real, structural difference separates round-robin from least-connections? | [Load Balancing, Service Discovery, and Health Checking](../../11-system-design/load-balancing-service-discovery-and-health-checking.md#flashcards) |
| 10 | How quickly does an active health checker detect a dead backend? | [Load Balancing, Service Discovery, and Health Checking](../../11-system-design/load-balancing-service-discovery-and-health-checking.md#flashcards) |
| 11 | What's the real difference between active and passive health checking, and why use both? | [Load Balancing, Service Discovery, and Health Checking](../../11-system-design/load-balancing-service-discovery-and-health-checking.md#flashcards) |
| 12 | Why can a fixed-window rate limiter admit up to 2x its configured limit? | [Rate Limiting and Throttling Algorithms](../../11-system-design/rate-limiting-and-throttling-algorithms.md#flashcards) |
| 13 | What's the actual behavioral difference between token bucket and leaky bucket? | [Rate Limiting and Throttling Algorithms](../../11-system-design/rate-limiting-and-throttling-algorithms.md#flashcards) |
| 14 | What's the hardest part of a production rate limiter, once the algorithm itself is settled? | [Rate Limiting and Throttling Algorithms](../../11-system-design/rate-limiting-and-throttling-algorithms.md#flashcards) |
| 15 | What's the real difference in request volume between short- and long-polling? | [Real-Time Delivery](../../11-system-design/realtime-delivery-websocket-sse-and-long-polling.md#flashcards) |
| 16 | Is SSE actually streaming, or does it just look that way? | [Real-Time Delivery](../../11-system-design/realtime-delivery-websocket-sse-and-long-polling.md#flashcards) |
| 17 | What makes WebSocket structurally different from SSE and long-polling? | [Real-Time Delivery](../../11-system-design/realtime-delivery-websocket-sse-and-long-polling.md#flashcards) |
| 18 | What does a circuit breaker's OPEN state actually save, measured? | [Resilience Patterns](../../11-system-design/resilience-patterns.md#flashcards) |
| 19 | What does jitter fix about retry backoff, precisely? | [Resilience Patterns](../../11-system-design/resilience-patterns.md#flashcards) |
| 20 | What is a bulkhead, and what specific failure mode does it prevent? | [Resilience Patterns](../../11-system-design/resilience-patterns.md#flashcards) |
| 21 | Why is "find all documents containing this word" fast with an inverted index? | [Search and Indexing Systems](../../11-system-design/search-and-indexing-systems.md#flashcards) |
| 22 | What does BM25 add over plain TF-IDF? | [Search and Indexing Systems](../../11-system-design/search-and-indexing-systems.md#flashcards) |
| 23 | Why did a `LIKE '%term%'` search feature degrade as the catalog grew? | [Search and Indexing Systems](../../11-system-design/search-and-indexing-systems.md#flashcards) |
| 24 | What's the first question in storage selection? | [Storage Selection Trade-offs](../../11-system-design/storage-selection-tradeoffs.md#flashcards) |
| 25 | Name the four storage categories. | [Storage Selection Trade-offs](../../11-system-design/storage-selection-tradeoffs.md#flashcards) |
| 26 | What's the hidden cost of polyglot persistence? | [Storage Selection Trade-offs](../../11-system-design/storage-selection-tradeoffs.md#flashcards) |
| 27 | Name the six phases of the system design method, in order. | [System Design Method and Estimation](../../11-system-design/system-design-method-and-estimation.md#flashcards) |
| 28 | Why estimate before designing the architecture? | [System Design Method and Estimation](../../11-system-design/system-design-method-and-estimation.md#flashcards) |
| 29 | What's the single most important assumption to state explicitly in a QPS estimate? | [System Design Method and Estimation](../../11-system-design/system-design-method-and-estimation.md#flashcards) |
| 30 | What's the most commonly skipped phase, and why does it matter? | [System Design Method and Estimation](../../11-system-design/system-design-method-and-estimation.md#flashcards) |

---

## Related

- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
