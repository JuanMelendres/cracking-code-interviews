---
title: "T-1306 · Supply Chain Security, SBOM, and Dependency Risk"
topic_id: T-1306
domain: Security
tier: Staff
iwi: 5.00
prerequisites: []
unlocks: []
week: 17
last_reviewed: 2026-08-02
canonical: ../../handbook/security/supply-chain-security-sbom-and-dependency-risk.md
---

# T-1306 · Supply Chain Security, SBOM, and Dependency Risk

**IWI 5.00 · Staff tier · Occasional interview frequency**

**Canonical chapter:** [Supply Chain Security, SBOM, and Dependency Risk](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md). This file is the Week 17 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. This is also the last of the seven topics that closes Security to 7/7 register coverage this week.

**Verification note:** the evidence behind this summary is real Docker Scout output against `eclipse-temurin:21-jre` — the same base image used in Week 16's container-ergonomics chapter. See `practice/java/week-17/supply-chain/README.md` for the exact reproduce commands.

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

An SBOM is a structured, complete inventory of every component in deployed software — direct dependencies, transitive dependencies, and base-image OS packages — not just what a project's own build file directly declares. → [Mental Model](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#mental-model).

## 2. Why it exists

Most real vulnerabilities live in transitive dependencies or inherited base images, entirely invisible to a review of a project's own directly-declared dependencies. → [Core Concepts](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#core-concepts).

## 3. The measured evidence

Real SBOM against `eclipse-temurin:21-jre`: 213 packages, spanning both `deb` (OS layer) and `golang` (bundled tooling) ecosystems in a single image. Real vulnerability scan against that SBOM: 13 vulnerabilities across 3 packages (1 CRITICAL, 1 HIGH, 7 MEDIUM), including a CRITICAL CVE in `golang.org/x/net`, a transitive package no application code chose directly. → [Internal Implementation](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#internal-implementation) has the full trace.

## 4. Trade-offs

Continuous SBOM generation and scanning add pipeline overhead and a real volume of findings to triage; skipping it leaves an organization structurally unable to answer "are we affected" quickly during an actual supply-chain incident. → [Trade-offs](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#trade-offs).

## 5. Interview questions

1. A scanner reports a critical CVE in a package no one on the team has ever written or imported. How is this possible?
2. Your team generates an SBOM once a quarter for a compliance audit. Is this sufficient?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#interview-questions).

## 6. Common mistakes

Reviewing only directly-declared dependencies and considering that complete; treating every SBOM-surfaced CVE as equally urgent regardless of exploitability, producing alert fatigue. → [Common Mistakes](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#common-mistakes).

## 7. Staff-level discussion

Recognizes shared base images as a cross-cutting organizational risk best centrally owned (one fix, many services rebuild) rather than each team independently discovering the same finding. → [Staff-Level Discussion](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#interview-answer-framework).

## 8. Summary

SBOM depth is the point — transitive dependencies and base-image packages dominate real-world findings. Measured directly against this handbook's own JVM base image: 213 packages, 13 vulnerabilities, one CRITICAL finding entirely inherited from the base image choice. → [Summary](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#flashcards). Full week-level deck: `09-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#practice-exercises) and [Solutions](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md#solutions). Reproducible demo: `practice/java/week-17/supply-chain/`.

## 13. Additional Reading

- [CISA — Software Bill of Materials](https://www.cisa.gov/sbom)

## 14. Official References

- [CycloneDX — SBOM Standard](https://cyclonedx.org/)
