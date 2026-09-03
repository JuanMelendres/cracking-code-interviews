---
title: "T-1104 · Integration Testing Against Real Dependencies"
topic_id: T-1104
domain: Testing
tier: Advanced
iwi: 6.50
prerequisites: [T-1101, T-1103]
unlocks: []
week: 11
last_reviewed: 2026-07-30
canonical: ../../handbook/testing/integration-testing-against-real-dependencies.md
---

# T-1104 · Integration Testing Against Real Dependencies

**IWI 6.50 · Advanced tier**

**Canonical chapter:** [Integration Testing Against Real Dependencies](../../syllabus/08-testing/integration-testing-against-real-dependencies.md). This file is the Week 11 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `MANIFEST.md` cites §4 directly.

**Verification note:** the test run behind this summary is real, executed output from `practice/java/week-11/testing/src/OrderRepositoryIntegrationTest.java` against a genuine, live Postgres 16 (Docker) — not an in-memory fake or a mock.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [A real integration test, against a real Postgres](#3-a-real-integration-test-against-a-real-postgres)
4. [A scoping note, stated honestly](#4-a-scoping-note-stated-honestly)
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

An integration test verifies real behavior against a REAL instance of a dependency — a database, a broker, an API — rather than a mock's assumptions. Testcontainers is the standard library for provisioning a real, ephemeral, Docker-based instance for the test run. → [Definition and Purpose](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#definition-and-purpose).

## 2. Why it exists

A repository test that mocks the database only verifies the test's own assumptions, never real SQL behavior. The repository's entire job is translating calls into real SQL — only a real database can verify that translation. → [Definition and Purpose](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#definition-and-purpose).

## 3. A real integration test, against a real Postgres

Measured: a real `INSERT ... RETURNING id` and `SELECT` against a live Postgres container, in 154ms — genuinely exercising the JDBC driver and real SQL, catching bugs a mock could never surface. → [Internal Implementation](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#internal-implementation) has the full trace.

## 4. A scoping note, stated honestly

This chapter's test connects directly via JDBC to a Postgres container started with a plain `docker run`, not the Testcontainers library itself — the underlying technique (a real, ephemeral dependency) is identical to what Testcontainers automates; only lifecycle management differs. → [Internal Implementation](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#internal-implementation).

## 5. Trade-offs

Mocking the database is fastest but tests only assumptions; a real dependency (via Testcontainers or manual orchestration) catches real bugs at real infrastructure cost; an in-memory fake is fast but has real compatibility gaps. → [Trade-offs](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#trade-offs).

## 6. Interview questions

1. Your team wants to mock the database in every repository test for speed. Convince me that's wrong.
2. Your integration tests are flaky — passing locally, failing in CI. Where do you look first?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#interview-questions).

## 7. Common mistakes

Believing an in-memory fake is "close enough" to the real database; treating mock-vs-real as all-or-nothing rather than per-layer; skipping integration tests entirely for speed. → [Common Mistakes](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#common-mistakes).

## 8. Staff-level discussion

Choosing a lighter-weight technique that achieves the same real learning outcome, and stating the trade-off explicitly rather than hiding it, is itself a Staff-level habit. → [Staff-Level Discussion](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#interview-answer-framework).

## 9. Summary

A real integration test genuinely exercises the SQL and JDBC boundary a mocked test would only pretend to verify — measured directly at 154ms. The technique (real, ephemeral dependency over a mock) matters more than the specific library managing it. → [Summary](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#practice-exercises) and [Solutions](../../syllabus/08-testing/integration-testing-against-real-dependencies.md#solutions). Reproducible demo: `practice/java/week-11/testing/src/OrderRepositoryIntegrationTest.java`.

## 14. Additional Reading

- [Testcontainers documentation](https://testcontainers.com/)

## 15. Official References

- [Testcontainers — JUnit 5 Quickstart](https://java.testcontainers.org/quickstart/junit_5_quickstart/)
