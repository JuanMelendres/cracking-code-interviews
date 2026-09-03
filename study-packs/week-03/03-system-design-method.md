---
title: "T-801/T-802 · System Design Method and Estimation"
topic_id: T-801/T-802
domain: System Design
tier: Staff-Level
iwi: 8.65
prerequisites: []
unlocks: []
week: 3
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/system-design-method-and-estimation.md
---

# T-801 / T-802 · System Design Method and Estimation

**IWI 8.65 · Staff-Level tier · Highest-IWI topic in the entire register**

**Canonical chapter:** [System Design Method and Estimation](../../syllabus/11-system-design/system-design-method-and-estimation.md). This file is the Week 3 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `02-isolation-levels-and-write-skew.md`, `08-design-exercise-ride-hailing.md`, and Week 4/6's mock-interview files cite this file (and its §3/§4) directly.

## Table of Contents

1. [The concept — a repeatable procedure, not inspiration](#1-the-concept--a-repeatable-procedure-not-inspiration)
2. [Why it exists](#2-why-it-exists)
3. [The six phases](#3-the-six-phases)
4. [Estimation — the math, worked](#4-estimation--the-math-worked)
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

## 1. The concept — a repeatable procedure, not inspiration

A system design interview tests whether a candidate has a repeatable procedure that produces a defensible design under time pressure, not whether they memorized the "right" architecture. → [Definition and Purpose](../../syllabus/11-system-design/system-design-method-and-estimation.md#definition-and-purpose).

## 2. Why it exists

Without an explicit procedure, candidates either dive into components with no justification, or spend disproportionate time on one favorite phase and never reach bottleneck analysis. → [Definition and Purpose](../../syllabus/11-system-design/system-design-method-and-estimation.md#definition-and-purpose).

## 3. The six phases

Clarify (2-3 min) → Estimate (3-5 min) → API (2-3 min) → Data (3-5 min) → Architecture (10-15 min) → Bottlenecks (5-10 min). Estimation must precede architecture so every box is justified by a number, not reflex. → [Core Concepts](../../syllabus/11-system-design/system-design-method-and-estimation.md#core-concepts) has the full phase-by-phase breakdown.

## 4. Estimation — the math, worked

Worked example: 10M DAU → ~580 avg write QPS → ~1,740 peak write QPS (3x peak-to-average) → ~17,400 peak read QPS (10:1 read:write) → ~27.3 TB/year storage (with 3x replication). Every assumption stated explicitly so it can be challenged and revised live. → [Internal Implementation](../../syllabus/11-system-design/system-design-method-and-estimation.md#internal-implementation) has the full worked math.

## 5. Trade-offs

Following all six phases feels slower at first but makes every decision defensible; skipping estimation or bottleneck analysis feels faster but loses the production-judgment signal. → [Trade-offs](../../syllabus/11-system-design/system-design-method-and-estimation.md#trade-offs).

## 6. Interview questions

1. Walk me through your design method before you start drawing anything.
2. Estimate QPS and storage for a system with 10M DAU. Show every assumption.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/11-system-design/system-design-method-and-estimation.md#interview-questions).

## 7. Common mistakes

Jumping to components before establishing scale; presenting an estimate with no stated assumptions; running out of time before bottlenecks; treating the six phases as a rigid script. → [Common Mistakes](../../syllabus/11-system-design/system-design-method-and-estimation.md#common-mistakes).

## 8. Staff-level discussion

The six-phase method is also a real design-review discipline, not interview-specific theater — the same discipline that makes a design doc defensible to a skeptical review board. → [Staff-Level Discussion](../../syllabus/11-system-design/system-design-method-and-estimation.md#interview-answer-framework).

## 9. Summary

A repeatable six-phase procedure converts a design interview from "recall the right architecture" into "demonstrate a defensible reasoning process." → [Summary](../../syllabus/11-system-design/system-design-method-and-estimation.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/11-system-design/system-design-method-and-estimation.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/11-system-design/system-design-method-and-estimation.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/11-system-design/system-design-method-and-estimation.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/11-system-design/system-design-method-and-estimation.md#practice-exercises) and [Solutions](../../syllabus/11-system-design/system-design-method-and-estimation.md#solutions).

## 14. Additional Reading

- The System Design Primer (github.com/donnemartin/system-design-primer) — broad component reference to draw from during phase 5

## 15. Official References

- No single official specification governs system design method — this chapter's six phases are this programme's own synthesis, flagged here for transparency rather than attributed to an external source.
