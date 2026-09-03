---
title: "T-515 · Resilience: Retry, Circuit Breaker, Bulkhead, Timeout"
topic_id: T-515
domain: DistributedData
tier: Staff
iwi: 7.60
prerequisites: []
unlocks: []
week: 10
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/resilience-patterns.md
---

# T-515 · Resilience: Retry, Circuit Breaker, Bulkhead, Timeout

**IWI 7.60 · Staff tier**

**Canonical chapter:** [Resilience Patterns: Circuit Breaker, Retry Jitter, Timeouts, and Bulkheads](../../syllabus/11-system-design/resilience-patterns.md). This file is the Week 10 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every measurement behind this summary is real, executed output from `practice/java/week-10/resilience/src/CircuitBreakerDemo.java` and `RetryBackoffJitterDemo.java`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [A real circuit breaker, all three states](#3-a-real-circuit-breaker-all-three-states)
4. [Retry storms and jitter, measured](#4-retry-storms-and-jitter-measured)
5. [Timeout selection and bulkheads](#5-timeout-selection-and-bulkheads)
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

Timeouts bound how long you wait, retries handle transient failures, circuit breakers stop calling a downstream that's clearly down, and bulkheads isolate one dependency's failure from starving resources needed by others. → [Definition and Purpose](../../syllabus/11-system-design/resilience-patterns.md#definition-and-purpose).

## 2. Why it exists

Without these patterns, a single slow dependency can exhaust the calling service's own thread/connection pool waiting on it, cascading the failure to every other caller — the same unbounded-queue trap from Week 9, one layer up. → [Definition and Purpose](../../syllabus/11-system-design/resilience-patterns.md#definition-and-purpose).

## 3. A real circuit breaker, all three states

Measured: without a breaker, all 10 calls to a down-then-recovering dependency pay the full 200ms each. With a breaker (threshold=3, 500ms cool-down), all three states fire (`CLOSED → OPEN → HALF_OPEN → CLOSED`) and 5 of 20 calls are rejected in ~0ms instead of 200ms. → [Internal Implementation](../../syllabus/11-system-design/resilience-patterns.md#internal-implementation) has the full trace.

## 4. Retry storms and jitter, measured

Measured: 5 clients retrying with exponential backoff and no jitter all retry at the identical instant on every attempt. With full jitter, retry instants spread across the full backoff window instead. → [Internal Implementation](../../syllabus/11-system-design/resilience-patterns.md#internal-implementation) has the full trace.

## 5. Timeout selection and bulkheads

A timeout should derive from the downstream's actual latency percentile (often p99), not a round number. Bulkheads partition a shared resource per dependency so one slow dependency can't exhaust the pool every other dependency also needs. → [Core Concepts](../../syllabus/11-system-design/resilience-patterns.md#core-concepts).

## 6. Trade-offs

Circuit breakers fail fast but add state to reason about; jittered retries avoid storms but still cost latency/load; percentile-derived timeouts bound worst-case wait but can misfire if set too aggressively; bulkheads isolate failure at the cost of reserved, sometimes-idle resources. → [Trade-offs](../../syllabus/11-system-design/resilience-patterns.md#trade-offs).

## 7. Interview questions

1. Set the timeout — from what data?
2. Circuit opens. What does the user see?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/11-system-design/resilience-patterns.md#interview-questions).

## 8. Common mistakes

Treating "retry until success" as a reliability strategy; retrying without jitter; sharing one resource pool across dependencies with different failure/latency profiles. → [Common Mistakes](../../syllabus/11-system-design/resilience-patterns.md#common-mistakes).

## 9. Staff-level discussion

Thousands of clients retrying in lockstep after a brief blip can produce a load spike exceeding the original traffic pattern, re-triggering the outage the retries were meant to recover from — retry logic needs the same design rigor as the primary request path. → [Staff-Level Discussion](../../syllabus/11-system-design/resilience-patterns.md#interview-answer-framework).

## 10. Summary

A real circuit breaker measurably saves latency during an outage and cycles correctly through all three states. Retry without jitter genuinely synchronizes every client's retry instant — exactly the risk jitter exists to prevent. Timeouts should derive from latency percentiles, and bulkheads isolate one dependency's resource consumption from starving others. → [Summary](../../syllabus/11-system-design/resilience-patterns.md#summary).

## 11. Key Takeaways

→ [Key Takeaways](../../syllabus/11-system-design/resilience-patterns.md#key-takeaways).

## 12. Cheat Sheet

→ [Cheat Sheet](../../syllabus/11-system-design/resilience-patterns.md#cheat-sheet).

## 13. Flashcards

→ [Flashcards](../../syllabus/11-system-design/resilience-patterns.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 14. Practice Exercises

→ [Practice Exercises](../../syllabus/11-system-design/resilience-patterns.md#practice-exercises) and [Solutions](../../syllabus/11-system-design/resilience-patterns.md#solutions). Reproducible demos: `practice/java/week-10/resilience/src/CircuitBreakerDemo.java` and `RetryBackoffJitterDemo.java`.

## 15. Additional Reading

- [Netflix Tech Blog — Fault Tolerance in a High Volume, Distributed System](https://netflixtechblog.com/fault-tolerance-in-a-high-volume-distributed-system-91ab4faae74a)

## 16. Official References

- [Resilience4j documentation — Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker) — a production library implementing the same state machine built from scratch in this chapter
