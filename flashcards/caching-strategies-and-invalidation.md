---
title: "Flashcards: Caching Strategies and Invalidation"
slug: caching-strategies-and-invalidation
document_type: flashcard-deck
domain: system-design
topic_id: T-804
canonical: ../handbook/system-design/caching-strategies-and-invalidation.md
last_updated: 2026-08-06
---

# Flashcards: Caching Strategies and Invalidation

**Canonical chapter:** [`handbook/system-design/caching-strategies-and-invalidation.md`](../handbook/system-design/caching-strategies-and-invalidation.md)

## Card: How cache/database disagreement happens

**Prompt:**
How does cache/database disagreement typically happen?

**Answer:**
A cache-aside read populates a stale value after a concurrent write already invalidated the cache — a race, not a generic sync bug.

**Why it matters:**
The precise, nameable mechanism interviewers expect, not a vague description.

**Common trap:**
Describing it only as "the cache didn't update" without the race's ordering.

**Related:**
[Core Concepts](../handbook/system-design/caching-strategies-and-invalidation.md#core-concepts)

## Card: Cache dying at peak

**Prompt:**
What happens to the database when the entire cache dies at peak?

**Answer:**
It receives a full-working-set stampede — every previously cached read now falls through simultaneously, against a database sized assuming cache assistance.

**Why it matters:**
Distinguishes "things get slower" from the actual systemic capacity failure.

**Common trap:**
Treating a cache outage as merely a performance degradation.

**Related:**
[Production Scenarios](../handbook/system-design/caching-strategies-and-invalidation.md#production-scenarios)

## Card: Three cache-stampede fixes

**Prompt:**
Name three cache-stampede fixes.

**Answer:**
Single-flight/request coalescing, probabilistic early expiration with jitter, stale-while-revalidate.

**Why it matters:**
A named interview question requiring all three, not one.

**Common trap:**
Naming only one fix as if it were the complete answer.

**Related:**
[Internal Implementation](../handbook/system-design/caching-strategies-and-invalidation.md#internal-implementation)

## Card: Three hot-key mitigations

**Prompt:**
Name three hot-key mitigations.

**Answer:**
Local in-process caching, key sharding across suffixed keys, edge/CDN caching.

**Why it matters:**
Different mitigations fit different access patterns — naming one isn't sufficient.

**Common trap:**
Naming only one mitigation regardless of access pattern.

**Related:**
[Core Concepts](../handbook/system-design/caching-strategies-and-invalidation.md#core-concepts)
