---
title: "T-1304 · Secrets Management and Key Rotation"
topic_id: T-1304
domain: Security
tier: Advanced
iwi: 5.50
prerequisites: [T-1303]
unlocks: []
week: 17
last_reviewed: 2026-08-02
canonical: ../../handbook/security/secrets-management-and-key-rotation.md
---

# T-1304 · Secrets Management and Key Rotation

**IWI 5.50 · Advanced tier · Moderate interview frequency**

**Canonical chapter:** [Secrets Management and Key Rotation](../../syllabus/12-security/secrets-management-and-key-rotation.md). This file is the Week 17 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the evidence behind this summary is real, executed output from `practice/java/week-17/secrets-rotation/` — AES-256-GCM envelope encryption with real key-version tagging.

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

Envelope encryption tags every ciphertext with the key version that produced it, so old and new keys can coexist during a rotation without a synchronous, all-at-once re-encryption of every existing record. → [Mental Model](../../syllabus/12-security/secrets-management-and-key-rotation.md#mental-model).

## 2. Why it exists

Keys need rotation even without a known compromise — limiting the volume of data ever protected by one key, meeting compliance schedules, and bounding the exposure window of an undetected slow leak. → [Core Concepts](../../syllabus/12-security/secrets-management-and-key-rotation.md#core-concepts).

## 3. The measured evidence

Real AES-256-GCM demo: rotating from key v1 to v2 is instantaneous (zero downtime); both v1- and v2-tagged records decrypt correctly simultaneously afterward. Removing v1 from the key ring (simulating retirement without a completed re-encryption sweep) immediately and permanently breaks decryption for the v1-tagged record — direct proof retirement must follow re-encryption, never precede it. → [Internal Implementation](../../syllabus/12-security/secrets-management-and-key-rotation.md#internal-implementation) has the full trace.

## 4. Trade-offs

More frequent rotation reduces exposure window but increases the operational overhead of managing multiple key versions and re-encrypting data as old versions retire. → [Trade-offs](../../syllabus/12-security/secrets-management-and-key-rotation.md#trade-offs).

## 5. Interview questions

1. Walk through what needs to happen to safely rotate a key today, given millions of existing records encrypted under the current key.
2. Why rotate keys periodically even with no evidence of compromise?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/12-security/secrets-management-and-key-rotation.md#interview-questions).

## 6. Common mistakes

Treating "we encrypted the data" as a completed, static task; deleting an old key immediately upon "rotating" without confirming a re-encryption sweep completed. → [Common Mistakes](../../syllabus/12-security/secrets-management-and-key-rotation.md#common-mistakes).

## 7. Staff-level discussion

Reasons about the retrofitting-cost asymmetry: designing key versioning in from day one versus a multi-month migration project later, and factors this into new-system architecture decisions before rotation is imminently required. → [Staff-Level Discussion](../../syllabus/12-security/secrets-management-and-key-rotation.md#interview-answer-framework).

## 8. Summary

Naive key replacement breaks existing data; envelope encryption's per-record version tag is what makes rotation practical. Measured directly: zero-downtime rotation with simultaneous old/new decryption, and permanent data loss when a key is retired before its re-encryption sweep. → [Summary](../../syllabus/12-security/secrets-management-and-key-rotation.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/12-security/secrets-management-and-key-rotation.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/12-security/secrets-management-and-key-rotation.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/12-security/secrets-management-and-key-rotation.md#flashcards). Full week-level deck: `09-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/12-security/secrets-management-and-key-rotation.md#practice-exercises) and [Solutions](../../syllabus/12-security/secrets-management-and-key-rotation.md#solutions). Reproducible demo: `practice/java/week-17/secrets-rotation/`.

## 13. Additional Reading

- [NIST SP 800-57 Part 1 Rev. 5 — Key Management](https://csrc.nist.gov/pubs/sp/800/57/pt1/r5/final)

## 14. Official References

- [NIST SP 800-57 Part 1 Rev. 5 — Key Management](https://csrc.nist.gov/pubs/sp/800/57/pt1/r5/final)
