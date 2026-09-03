---
title: "T-909 · Distributed Systems Failure Modes"
topic_id: T-909
domain: System Design
tier: Staff-Level
iwi: 8.45
prerequisites: [T-801]
unlocks: [T-1504]
week: 4
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/distributed-systems-failure-modes.md
---

# T-909 · Distributed Systems Failure Modes

**IWI 8.45 · Staff-Level tier · 4th-ranked topic in the Mandatory Core**

**Canonical chapter:** [Distributed Systems Failure Modes](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md). This file is the Week 4 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `03-api-design.md`, `06-failure-modes-deliverable.md`, `09-week-4-checklist.md`, and Week 5's `02-idempotency.md`/`09-design-exercise-payment-processing.md` all cite them directly (notably §3, the retry-amplification section, and §4, the idempotency section).

**Verification note:** the retry-amplification and fencing-token demonstrations behind this summary are real, executed Java — genuine concurrent thread pools, real measured timing and call counts. Source: `practice/java/week-04/failure-modes/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Retries and amplification, measured](#3-retries-and-amplification-measured)
4. [Distinguishing "failed" from "succeeded slowly"](#4-distinguishing-failed-from-succeeded-slowly)
5. [Split-brain and fencing tokens, reproduced](#5-split-brain-and-fencing-tokens-reproduced)
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

A component can fail, or merely appear to fail, without either side being able to distinguish the two cases with certainty — most failure modes in this domain are consequences of that single fact. → [Definition and Purpose](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#definition-and-purpose).

## 2. Why it exists

A single-process program either completes a call or the whole process crashes. Across a network, a request can succeed and have its *response* lost, indistinguishable from the request itself being lost. → [Definition and Purpose](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#definition-and-purpose).

## 3. Retries and amplification, measured

Measured: retrying without backoff cost 2.3× the load and 3× the time for the *same* 4/12 success rate as no retries at all — because a client giving up doesn't cancel the work already submitted downstream. Exponential backoff + jitter achieved 12/12 success with less amplification than no-backoff. → [Internal Implementation](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#internal-implementation) has the full measured trace.

## 4. Distinguishing "failed" from "succeeded slowly"

A request that failed is generally safe to retry immediately; one that succeeded slowly is not. Idempotency keys resolve the ambiguity by letting the server recognize a retry and return the original result, shifting the resolution from client to server. → [Core Concepts](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#core-concepts).

## 5. Split-brain and fencing tokens, reproduced

Measured: a paused node's stale write corrupts shared state without fencing; with fencing tokens (storage rejects any write older than the highest token seen), the stale write is correctly rejected. The check must live at the storage layer, never trusted from the node itself. → [Internal Implementation](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#internal-implementation) has the full trace.

## 6. Trade-offs

No retries risks any transient failure becoming permanent; retries without backoff amplify load; backoff+jitter costs worst-case latency but recovers correctly; fencing tokens require every write path to check token ordering. → [Trade-offs](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#trade-offs).

## 7. Interview questions

1. You added retries and made the outage worse. Explain the mechanism precisely.
2. How do you distinguish "the request failed" from "the request succeeded slowly," and why does it matter?
3. Two nodes both believe they are leader. How, and what breaks?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#interview-questions).

## 8. Common mistakes

Believing a timeout definitively means failure; adding retries without backoff, jitter, or idempotency; assuming leader election alone prevents split-brain corruption. → [Common Mistakes](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#common-mistakes).

## 9. Staff-level discussion

Every mechanism here — backoff, idempotency keys, fencing tokens — is a structural answer to the same fact: a distributed system cannot get instantaneous, certain knowledge of another component's state. → [Staff-Level Discussion](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#interview-answer-framework).

## 10. Summary

Distributed failure modes stem from network ambiguity between lost, slow, and succeeded-but-response-lost. Retries without backoff amplify load with no success-rate benefit; split-brain is prevented by fencing tokens at the storage layer. → [Summary](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#summary).

## 11. Key Takeaways

→ [Key Takeaways](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#key-takeaways).

## 12. Cheat Sheet

→ [Cheat Sheet](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#cheat-sheet).

## 13. Flashcards

→ [Flashcards](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 14. Practice Exercises

→ [Practice Exercises](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#practice-exercises) and [Solutions](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md#solutions). Reproducible demos: `practice/java/week-04/failure-modes/RetryStormDemo.java`, `FencingTokenDemo.java`.

## 15. Additional Reading

- Martin Kleppmann, *Designing Data-Intensive Applications*, Ch. 8 "The Trouble with Distributed Systems" (the fencing-token example in this chapter follows Kleppmann's original)
- AWS Builders' Library — ["Timeouts, retries, and backoff with jitter"](https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/)

## 16. Official References

- [Stripe API documentation — Idempotent requests](https://stripe.com/docs/api/idempotent_requests) — a widely-cited real-world idempotency-key implementation
