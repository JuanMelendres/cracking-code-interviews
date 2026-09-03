---
title: "T-1302 · AuthN vs AuthZ, RBAC vs ABAC"
topic_id: T-1302
domain: Security
tier: Core
iwi: 6.00
prerequisites: [T-1301]
unlocks: [T-1307]
week: 17
last_reviewed: 2026-08-02
canonical: ../../handbook/security/authn-authz-rbac-vs-abac.md
---

# T-1302 · AuthN vs AuthZ, RBAC vs ABAC

**IWI 6.00 · Core tier · High interview frequency**

**Canonical chapter:** [AuthN vs AuthZ, RBAC vs ABAC](../../syllabus/12-security/authn-authz-rbac-vs-abac.md). This file is the Week 17 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the evidence behind this summary is real, executed output from `practice/java/week-17/authz-models/` — an RBAC lookup and an ABAC evaluator applied to identical users and identical resource context.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The measured evidence](#3-the-measured-evidence)
4. [Trade-offs](#4-trade-offs)
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

## 1. The concept

Authentication verifies identity once; authorization decides permission separately, potentially many times per request across different layers. RBAC grants static, role-based permissions; ABAC evaluates policy against subject/resource/action/environment attributes at request time. → [Mental Model](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#mental-model).

## 2. Why it exists

Many real access rules are conditional (ownership, team, time, relationship to the resource's history) in ways a static role-permission table cannot express without an unmanageable proliferation of narrow roles. → [Core Concepts](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#core-concepts).

## 3. The measured evidence

Real demo: three users share the identical RBAC role (`engineer`) and RBAC returns the identical `true` for all three — a false positive for the change's own author and for a user on the wrong team. The ABAC evaluator, given the same role plus ownership and team attributes, correctly denies both and allows the legitimate approver — and denies that same approver again when only the environment attribute (time, 02:00 vs. 14:00) changes. → [Internal Implementation](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#internal-implementation) has the full trace.

## 4. Trade-offs

RBAC is simple to audit (a table is inspectable) but can't express conditional rules; ABAC expresses them directly but needs a policy engine and reliable attribute sourcing, and is genuinely harder to audit. → [Trade-offs](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#trade-offs).

## 5. Interview questions

1. A new requirement: "a manager may approve only if the requester reports to them." How would you implement this?
2. What's the practical difference between a 401 and a 403, beyond picking the "correct" status code?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#interview-questions).

## 6. Common mistakes

Conflating AuthN and AuthZ under interview pressure; proposing "add more roles" as the fix for a rule RBAC structurally cannot express. → [Common Mistakes](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#common-mistakes).

## 7. Staff-level discussion

Recognizes "role explosion" (needing a new role per condition combination) as the practical signal a system has outgrown RBAC for a specific rule, and names ABAC's real operational costs rather than treating it as strictly superior. → [Staff-Level Discussion](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#interview-answer-framework).

## 8. Summary

AuthN answers "who," AuthZ answers "may they" — and AuthZ recurs at multiple layers. Measured directly: identical RBAC role, identical users, wrong answers for two of three; ABAC with ownership/team/time attributes gets all three right. → [Summary](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#flashcards). Full week-level deck: `09-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#practice-exercises) and [Solutions](../../syllabus/12-security/authn-authz-rbac-vs-abac.md#solutions). Reproducible demo: `practice/java/week-17/authz-models/`.

## 13. Additional Reading

- [NIST SP 800-162 — Guide to ABAC](https://csrc.nist.gov/pubs/sp/800/162/final)

## 14. Official References

- [NIST SP 800-162 — Guide to ABAC](https://csrc.nist.gov/pubs/sp/800/162/final)
