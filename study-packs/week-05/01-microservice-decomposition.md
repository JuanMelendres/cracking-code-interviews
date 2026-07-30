---
title: "T-907/T-908 · Microservice Decomposition and the Monolith Trade-off"
topic_id: T-907/T-908
domain: Architecture
tier: Staff-Level
iwi: 8.40
prerequisites: [T-901, T-903]
unlocks: []
week: 5
last_reviewed: 2026-07-30
canonical: ../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md
---

# T-907 / T-908 · Microservice Decomposition and the Monolith Trade-off

**IWI 8.40 / 7.90 · Staff-Level tier · A judgment topic — the expected answer is frequently "don't"**

**Canonical chapter:** [Microservice Decomposition and the Monolith Trade-off](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md). This file is the Week 5 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `06-decomposition-analysis-deliverable.md` and `10-week-5-checklist.md` cite §3 directly.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Where to draw a boundary](#3-where-to-draw-a-boundary)
4. [When to merge services back together](#4-when-to-merge-services-back-together)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

Microservice decomposition splits a system so each resulting service can be developed, deployed, and scaled independently — "independently" carries the entire weight of the decision. → [Definition and Purpose](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#definition-and-purpose).

## 2. Why it exists

A monolith's principal failure mode at scale is organizational, not technical — one team's bad deploy blocks every other team's work. Microservices exist to give teams independently deployable units of ownership. → [Definition and Purpose](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#definition-and-purpose).

## 3. Where to draw a boundary

Draw the line where strong single-transaction consistency is not required across it — the same test as an aggregate boundary, not an arbitrary table split. Two services needing one transaction signals a possibly-wrong boundary, not a cue for distributed 2PC. → [Core Concepts](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#core-concepts).

## 4. When to merge services back together

Three signals: always co-deployed together, mostly synchronous critical-path calls, or operational cost exceeding benefit. A four-engineer team is very likely better served by a well-modularized monolith than microservices at all. → [Core Concepts](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#core-concepts).

## 5. Trade-offs

Independent deployment and scaling per team, at the cost of every cross-service call becoming a network call, distributed transactions becoming sagas, and multiplied operational surface. → [Trade-offs](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#trade-offs).

## 6. Interview questions

1. Where exactly do you draw a service boundary, and why there rather than one table over?
2. Two services need one transaction. Now what?
3. When would you merge two services back together?
4. You have four engineers. Does microservices still make sense? Defend it.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#interview-questions).

## 7. Common mistakes

Drawing boundaries by table or code proximity; reaching for distributed 2PC by default; treating decomposition as a one-way door; defending microservices unconditionally. → [Common Mistakes](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#common-mistakes).

## 8. Staff-level discussion

The organizational structure — not technical elegance — is the primary justification for microservices; a team too small to have this organizational problem gains little from paying the distributed-systems tax. → [Staff-Level Discussion](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#interview-answer-framework).

## 9. Summary

A service boundary follows the same consistency-driven test as an aggregate boundary. Two services needing one transaction is a signal to question the boundary. Merge back when co-deployment, synchronous coupling, or operational cost outweigh the benefit. → [Summary](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#practice-exercises) and [Solutions](../../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#solutions).

## 14. Additional Reading

- Sam Newman, *Building Microservices*, 2nd ed., Ch. 1–3

## 15. Official References

- No single official specification governs microservice decomposition — this chapter draws on widely-cited industry practice (Newman's work, Conway's Law) rather than one canonical source.
