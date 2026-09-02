---
title: "Flashcards: Rate Limiting and Throttling Algorithms"
slug: rate-limiting-and-throttling-algorithms
document_type: flashcard-deck
domain: system-design
topic_id: T-808
canonical: ../handbook/system-design/rate-limiting-and-throttling-algorithms.md
last_updated: 2026-09-02
---

# Flashcards: Rate Limiting and Throttling Algorithms

**Canonical chapter:** [`handbook/system-design/rate-limiting-and-throttling-algorithms.md`](../handbook/system-design/rate-limiting-and-throttling-algorithms.md)

## Card: Fixed window boundary flaw

**Prompt:**
Why can a fixed-window rate limiter admit up to 2x its configured limit?

**Answer:**
Because the window resets at an absolute wall-clock boundary with no memory of the previous window — a client can send a full limit's worth of requests in the last instant of one window and another full limit's worth in the first instant of the next.

**Why it matters:**
The single most common "gotcha" question on this topic; failing to explain it signals memorized-name-only knowledge of the algorithm.

**Common trap:**
Confusing this deterministic, single-threaded flaw with the separate concurrency race that affects all five algorithms.

**Related:**
[handbook/system-design/rate-limiting-and-throttling-algorithms.md](../handbook/system-design/rate-limiting-and-throttling-algorithms.md)

## Card: Token bucket vs. leaky bucket

**Prompt:**
What's the actual behavioral difference between token bucket and leaky bucket, given the same nominal rate?

**Answer:**
Token bucket allows a burst up to its capacity as long as the long-run average stays at the refill rate. Leaky bucket forces a genuinely constant output rate regardless of how bursty the input was, adding queuing delay to smooth the burst away entirely.

**Why it matters:**
They are not interchangeable defaults — the choice depends on whether the downstream system can tolerate burst at all.

**Common trap:**
Describing leaky bucket as "just token bucket in reverse" without naming the queuing/smoothing behavior that makes it actually different in observed effect.

**Related:**
[handbook/system-design/rate-limiting-and-throttling-algorithms.md](../handbook/system-design/rate-limiting-and-throttling-algorithms.md)

## Card: The distributed rate limiter's real bottleneck

**Prompt:**
What's the hardest part of a production rate limiter, once the algorithm itself is correct?

**Answer:**
Sharing the limiter's state correctly across every instance of a horizontally scaled service — an in-memory, per-instance limiter's effective limit silently multiplies by the instance count unless state moves to shared storage (commonly Redis with an atomic `INCR` or Lua script).

**Why it matters:**
This is the gap that separates a correct-algorithm answer from a Staff-level production-ready answer in interviews.

**Common trap:**
Assuming a correct single-process implementation is "done" without addressing multi-instance deployment.

**Related:**
[handbook/system-design/rate-limiting-and-throttling-algorithms.md](../handbook/system-design/rate-limiting-and-throttling-algorithms.md), [handbook/system-design/load-balancing-service-discovery-and-health-checking.md](../handbook/system-design/load-balancing-service-discovery-and-health-checking.md)
