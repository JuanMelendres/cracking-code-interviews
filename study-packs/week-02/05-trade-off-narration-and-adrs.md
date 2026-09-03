---
title: "T-1505/T-916 · Trade-off Narration and ADRs"
topic_id: T-1505/T-916
domain: Interview Craft
tier: Advanced
iwi: 8.10
prerequisites: []
unlocks: []
week: 2
last_reviewed: 2026-07-30
canonical: ../../interview-playbook/technical-answers/trade-off-narration-and-adrs.md
---

# T-1505 / T-916 · Trade-off Narration and Architecture Decision Records

**IWI 8.10 · Advanced tier · Runs every week from here on**

**Canonical chapter:** [Trade-off Narration and Architecture Decision Records](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md). This file is the Week 2 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `06-answer-frameworks.md` cites §3 directly.

## Table of Contents

1. [Why this exists](#1-why-this-exists)
2. [The four-beat structure](#2-the-four-beat-structure)
3. [Worked example](#3-worked-example)
4. [ADRs — the written form of the same skill](#4-adrs--the-written-form-of-the-same-skill)
5. [Interview questions](#5-interview-questions)
6. [Common mistakes](#6-common-mistakes)
7. [Staff-level discussion](#7-staff-level-discussion)
8. [Summary](#8-summary)
9. [Key Takeaways](#9-key-takeaways)
10. [Cheat Sheet](#10-cheat-sheet)
11. [Flashcards](#11-flashcards)
12. [Practice Exercises](#12-practice-exercises)
13. [Additional Reading](#13-additional-reading)
14. [Official References](#14-official-references)

---

## 1. Why this exists

Named interview feedback specifically included "communicating why a decision was selected" and "explaining alternatives and trade-offs" — the highest-IWI single item in the feedback block (8.10), since it's the structural skill underneath every technical answer this programme produces. → [Why This Exists](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#why-this-exists).

## 2. The four-beat structure

Context (the real constraint), Options (genuinely considered alternatives), Decision criterion (the specific reason this option won), What it cost (the real trade-off accepted — the beat named feedback was specifically about). → [The Four-Beat Structure](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#the-four-beat-structure).

## 3. Worked example

A relational-vs-document-store decision for a catalog service, walked through all four beats: context (transactional needs with inventory/pricing), options (document store vs. EAV-pattern relational), decision criterion (transactional participation beat schema flexibility), cost (EAV query awkwardness requiring a query-builder abstraction). → [Worked Example](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#worked-example) has the full example.

## 4. ADRs — the written form of the same skill

An ADR is the four-beat structure, written down and dated: Context, Options Considered, Decision, Consequences. → [ADRs — the Written Form of the Same Skill](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#adrs--the-written-form-of-the-same-skill).

## 5. Interview questions

1. Deliver a technical decision using the four-beat structure, unprompted.
2. Why does beat 4 (cost) matter more than it seems?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#interview-questions).

## 6. Common mistakes

Presenting a strawman alternative; skipping beat 4 entirely; writing an ADR as a post-hoc justification rather than a record of reasoning at the time. → [Common Mistakes](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#common-mistakes).

## 7. Staff-level discussion

The four-beat structure is also the shape of a real design review conversation — a doc missing beat 4 reads as naive or as selling a decision rather than documenting it honestly. → [Staff-Level Discussion](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#staff-level-discussion).

## 8. Summary

Every technical trade-off answer should hit four beats, ending with the most commonly skipped and most valuable one: what it actually cost. An ADR is the same structure, written down and dated. → [Summary](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#flashcards). Full week-level deck: `08-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md#practice-exercises). This week's own deliverable — a real ADR filled from the template — is `10-adr-exercise.md`.

## 13. Additional Reading

- Michael Nygard, ["Documenting Architecture Decisions"](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions) — the original ADR format this chapter's template follows

## 14. Official References

- [adr.github.io](https://adr.github.io/) — ADR format examples and tooling
