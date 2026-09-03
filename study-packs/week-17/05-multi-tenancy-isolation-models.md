---
title: "T-1307 · Multi-Tenancy Isolation Models"
topic_id: T-1307
domain: Security
tier: Staff
iwi: 5.60
prerequisites: [T-1302]
unlocks: []
week: 17
last_reviewed: 2026-08-02
canonical: ../../handbook/security/multi-tenancy-isolation-models.md
---

# T-1307 · Multi-Tenancy Isolation Models

**IWI 5.60 · Staff tier · Occasional interview frequency**

**Canonical chapter:** [Multi-Tenancy Isolation Models](../../syllabus/12-security/multi-tenancy-isolation-models.md). This file is the Week 17 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the evidence behind this summary is real, executed output against a live PostgreSQL 16 container — see `practice/java/week-17/multi-tenancy/rls-demo.sql`.

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

Three isolation models — silo (separate infrastructure), pool (shared schema, filtered by tenant attribute), bridge (mixed by tenant tier) — trading isolation strength against operational cost in opposite directions. → [Mental Model](../../syllabus/12-security/multi-tenancy-isolation-models.md#mental-model).

## 2. Why it exists

Application-level `tenant_id` filtering alone requires every query, everywhere, forever, to be correct — a single missed filter is a direct cross-tenant leak. Row-Level Security moves the guarantee into the database itself. → [Core Concepts](../../syllabus/12-security/multi-tenancy-isolation-models.md#core-concepts).

## 3. The measured evidence

Real PostgreSQL RLS: as `app_user` (non-superuser) with `app.tenant_id` set, each tenant sees only its own rows; with no tenant context set, zero rows (fail-closed, not an error). As the superuser role (`BYPASSRLS` by default), the identical query returns every tenant's rows unconditionally, no `SET` required — the single most important caveat about RLS. → [Internal Implementation](../../syllabus/12-security/multi-tenancy-isolation-models.md#internal-implementation) has the full trace.

## 4. Trade-offs

Silo maximizes isolation strength at the cost of linear operational scaling; pool minimizes cost but concentrates risk onto one shared enforcement surface, whose strength depends on database-role discipline. → [Trade-offs](../../syllabus/12-security/multi-tenancy-isolation-models.md#trade-offs).

## 5. Interview questions

1. A team says "we've enabled RLS, so we're protected against cross-tenant leaks." What follow-up would you ask?
2. Why might a company migrate its largest customers from pool to silo despite RLS providing real, measured isolation?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/12-security/multi-tenancy-isolation-models.md#interview-questions).

## 6. Common mistakes

Treating "we filter by tenant_id" as equivalent to "we have isolation"; assuming RLS enabled is sufficient without auditing which roles are exempt (`BYPASSRLS`, superuser). → [Common Mistakes](../../syllabus/12-security/multi-tenancy-isolation-models.md#common-mistakes).

## 7. Staff-level discussion

Treats the superuser/`BYPASSRLS` exemption as the central, non-obvious risk in an RLS architecture, and factors organization-wide database-role auditing (not just the primary application) into the isolation strategy. → [Staff-Level Discussion](../../syllabus/12-security/multi-tenancy-isolation-models.md#interview-answer-framework).

## 8. Summary

Pool + RLS converts isolation from an application-code discipline into a database-enforced invariant — but not an unconditional one. Measured directly: correct per-tenant scoping, fail-closed on missing context, and a full bypass under a superuser role. → [Summary](../../syllabus/12-security/multi-tenancy-isolation-models.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/12-security/multi-tenancy-isolation-models.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/12-security/multi-tenancy-isolation-models.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/12-security/multi-tenancy-isolation-models.md#flashcards). Full week-level deck: `09-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/12-security/multi-tenancy-isolation-models.md#practice-exercises) and [Solutions](../../syllabus/12-security/multi-tenancy-isolation-models.md#solutions). Reproducible demo: `practice/java/week-17/multi-tenancy/rls-demo.sql`.

## 13. Additional Reading

- [PostgreSQL — Row Security Policies](https://www.postgresql.org/docs/current/ddl-rowsecurity.html)

## 14. Official References

- [PostgreSQL — Row Security Policies](https://www.postgresql.org/docs/current/ddl-rowsecurity.html)
