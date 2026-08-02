---
title: "T-1305 · Injection, Input Validation, and Output Encoding"
topic_id: T-1305
domain: Security
tier: Core
iwi: 5.70
prerequisites: [T-1301]
unlocks: []
week: 17
last_reviewed: 2026-08-02
canonical: ../../handbook/security/injection-input-validation-output-encoding.md
---

# T-1305 · Injection, Input Validation, and Output Encoding

**IWI 5.70 · Core tier · Moderate interview frequency**

**Canonical chapter:** [Injection, Input Validation, and Output Encoding](../../handbook/security/injection-input-validation-output-encoding.md). This file is the Week 17 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the evidence behind this summary is real, executed output from `practice/java/week-17/injection/` — a live PostgreSQL 16 SQL-injection authentication bypass and a real stored-XSS demonstration.

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

Injection is one recurring failure shape — untrusted data crossing into an interpreter's syntax instead of staying data — across SQL, HTML, shell, and other contexts. The fix is always separating the data and syntax channels, never better escaping. → [Mental Model](../../handbook/security/injection-input-validation-output-encoding.md#mental-model).

## 2. Why it exists

Input validation is a necessary early filter but insufficient alone; output encoding/parameterization at the actual point of use is what prevents injection, and must be reapplied correctly at every new consumer of the same data. → [Core Concepts](../../handbook/security/injection-input-validation-output-encoding.md#core-concepts).

## 3. The measured evidence

Real SQL injection: the username `admin' --` closes the string literal and comments out the password check, granting access with any password against a live PostgreSQL instance; a `PreparedStatement` version of the identical query correctly fails. Real stored XSS: an attacker's `<script>` payload executes live when concatenated into HTML; the same payload, output-encoded, renders as inert literal text. → [Internal Implementation](../../handbook/security/injection-input-validation-output-encoding.md#internal-implementation) has the full trace.

## 4. Trade-offs

Strict input validation reduces attack surface early but risks false-positive rejection of legitimate input; output encoding is more foundational but must be applied correctly at every single point of use. → [Trade-offs](../../handbook/security/injection-input-validation-output-encoding.md#trade-offs).

## 5. Interview questions

1. Evaluate: "we're safe from SQL injection because we validate the username field is alphanumeric."
2. Explain, mechanically, why `... username = 'admin' --' AND password_hash = 'anything'` grants access.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/security/injection-input-validation-output-encoding.md#interview-questions).

## 6. Common mistakes

Believing validation alone is sufficient; using one generic "sanitize" function for every output context instead of context-specific encoding; describing prepared statements as "escaping quotes better" rather than channel separation. → [Common Mistakes](../../handbook/security/injection-input-validation-output-encoding.md#common-mistakes).

## 7. Staff-level discussion

Recognizes that encoding protection doesn't propagate automatically to new consumers of the same data, and centralizes rendering/query-building through shared, well-tested utilities specifically to shrink that risk surface. → [Staff-Level Discussion](../../handbook/security/injection-input-validation-output-encoding.md#interview-answer-framework).

## 8. Summary

Injection is one failure shape, not a SQL-specific bug category. Measured directly: a real authentication bypass via SQL comment-syntax truncation, closed by parameterization; a real stored-XSS payload, neutralized by context-aware encoding. → [Summary](../../handbook/security/injection-input-validation-output-encoding.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../handbook/security/injection-input-validation-output-encoding.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../handbook/security/injection-input-validation-output-encoding.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../handbook/security/injection-input-validation-output-encoding.md#flashcards). Full week-level deck: `09-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../handbook/security/injection-input-validation-output-encoding.md#practice-exercises) and [Solutions](../../handbook/security/injection-input-validation-output-encoding.md#solutions). Reproducible demo: `practice/java/week-17/injection/`.

## 13. Additional Reading

- [OWASP Cheat Sheet Series — Injection Prevention](https://cheatsheetseries.owasp.org/cheatsheets/Injection_Prevention_Cheat_Sheet.html)

## 14. Official References

- [OWASP — SQL Injection](https://owasp.org/www-community/attacks/SQL_Injection)
