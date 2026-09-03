---
title: "T-804 · Caching Strategies and Invalidation"
topic_id: T-804
domain: System Design
tier: Staff-Level
iwi: 8.45
prerequisites: [T-801]
unlocks: [T-803]
week: 4
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/caching-strategies-and-invalidation.md
---

# T-804 · Caching Strategies and Invalidation

**IWI 8.45 · Staff-Level tier · 3rd-ranked topic in the Mandatory Core**

**Canonical chapter:** [Caching Strategies and Invalidation](../../syllabus/11-system-design/caching-strategies-and-invalidation.md). This file is the Week 4 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `08-design-exercise-news-feed.md` and `09-week-4-checklist.md` cite them directly (notably §4, the cache-stampede section).

**Verification note:** the cache-stampede reproduction behind this summary is real, executed Java — 50 genuinely concurrent threads, real thread pools, real measured database-call counts. Source: `practice/java/week-04/failure-modes/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Invalidation strategies and their failure modes](#3-invalidation-strategies-and-their-failure-modes)
4. [Cache stampede, reproduced and fixed](#4-cache-stampede-reproduced-and-fixed)
5. [Hot-key mitigation](#5-hot-key-mitigation)
6. [Trade-offs](#6-trade-offs)
7. [Interview questions](#7-interview-questions)
8. [Common mistakes](#8-common-mistakes)
9. [Staff-level discussion](#9-staff-level-discussion)
10. [Summary](#10-summary)
11. [Key Takeaways](#11-key-takeaways)
12. [Cheat Sheet](#12-cheat-sheet)
13. [Flashcards](#13-flashcards)
14. [Practice Exercises](#14-practice-exercises)
15. [Additional Reading](#15-additional-reading)
16. [Official References](#16-official-references)

---

## 1. The concept

A cache trades storage and staleness risk for latency, at the cost of a new correctness question: when is a value written, when is it invalidated, and what happens during the gap. → [Definition and Purpose](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#definition-and-purpose).

## 2. Why it exists

A database sized for data volume and durability is very often not sized for its read volume — a cache absorbs read traffic that would otherwise repeatedly re-fetch the same value. → [Definition and Purpose](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#definition-and-purpose).

## 3. Invalidation strategies and their failure modes

TTL, write-through, write-behind, cache-aside, and explicit invalidation each have a distinct failure mode. The classic race: a cache-aside read populates a stale value *after* a concurrent write already invalidated the cache — detection via sampling, fix via backstop TTL or versioned keys. → [Core Concepts](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#core-concepts).

## 4. Cache stampede, reproduced and fixed

Measured: 50 concurrent misses for the same key produce 50 database calls uncoordinated, 1 with single-flight coordination. Three distinct fixes: single-flight coalescing, probabilistic early expiration with jitter, stale-while-revalidate. → [Internal Implementation](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#internal-implementation) has the full measured trace and code.

## 5. Hot-key mitigation

Three mitigations for a key taking a disproportionate share of traffic: local in-process caching, key sharding across suffixed keys, edge/CDN caching — chosen by access pattern. → [Core Concepts](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#core-concepts).

## 6. Trade-offs

TTL is simple but guarantees a staleness window; single-flight eliminates stampede at the cost of shared first-request latency; stale-while-revalidate means no request ever waits, by design. → [Trade-offs](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#trade-offs).

## 7. Interview questions

1. Cache and database disagree. How did it happen, how do you detect it, how do you fix it?
2. Your cache dies at peak. Walk through what happens to the database.
3. One key takes 40% of traffic. Three mitigations.
4. Cache stampede — what is it and give three distinct fixes.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#interview-questions).

## 8. Common mistakes

Treating TTL as sufficient invalidation; not considering what happens when the *entire* cache becomes unavailable, not just one key; proposing a single stampede fix as if it were universal. → [Common Mistakes](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#common-mistakes).

## 9. Staff-level discussion

A Staff-level caching design partitions data by staleness tolerance first and picks the mechanism per partition, rather than applying one invalidation strategy uniformly. → [Staff-Level Discussion](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#interview-answer-framework).

## 10. Summary

Caching trades latency for a correctness question about staleness. Cache stampede is real and measurable, fixed by coordinating concurrent misses, not by changing the expiration policy alone. → [Summary](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#summary).

## 11. Key Takeaways

→ [Key Takeaways](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#key-takeaways).

## 12. Cheat Sheet

→ [Cheat Sheet](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#cheat-sheet).

## 13. Flashcards

→ [Flashcards](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 14. Practice Exercises

→ [Practice Exercises](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#practice-exercises) and [Solutions](../../syllabus/11-system-design/caching-strategies-and-invalidation.md#solutions). Reproducible demo: `practice/java/week-04/failure-modes/CacheStampedeDemo.java`.

## 15. Additional Reading

- AWS Builders' Library — ["Caching challenges and strategies"](https://aws.amazon.com/builders-library/caching-challenges-and-strategies/)

## 16. Official References

- No single official specification governs caching strategy — this chapter draws on widely-documented industry patterns (cache-aside, write-through, single-flight) rather than one canonical source.
