---
title: "Flashcards: Integration Testing Against Real Dependencies"
slug: integration-testing-against-real-dependencies
document_type: flashcard-deck
domain: testing
topic_id: T-1104
canonical: ../handbook/testing/integration-testing-against-real-dependencies.md
last_updated: 2026-08-06
---

# Flashcards: Integration Testing Against Real Dependencies

**Canonical chapter:** [`handbook/testing/integration-testing-against-real-dependencies.md`](../handbook/testing/integration-testing-against-real-dependencies.md)

## Card: What a real-database integration test catches

**Prompt:**
What does an integration test against a real database catch that a mocked-database test cannot?

**Answer:**
Real SQL correctness, type mismatches, `RETURNING`-clause behavior, real constraint violations — anything about the actual database's behavior, not the test's assumptions about it.

**Why it matters:**
The concrete gap that makes mocked-only repository test suites give false confidence.

**Common trap:**
Believing a mocked test suite covers the repository layer adequately.

**Related:**
[Internal Implementation](../handbook/testing/integration-testing-against-real-dependencies.md#internal-implementation)

## Card: Mock vs. real dependency scope

**Prompt:**
Is "mock vs. real dependency" an all-or-nothing choice across a codebase?

**Answer:**
No — it's a per-layer decision: mock at the business-logic layer (fast, many tests), use a real dependency at the boundary layer (fewer, but real).

**Why it matters:**
Prevents both under-testing boundaries and over-mocking business logic.

**Common trap:**
Assuming every test must either always mock or always use a real dependency.

**Related:**
[Decision Framework](../handbook/testing/integration-testing-against-real-dependencies.md#decision-framework)

## Card: What Testcontainers automates

**Prompt:**
What does Testcontainers automate that a manual Docker orchestration doesn't?

**Answer:**
Tying the container's lifecycle directly to the test's own lifecycle (JUnit annotations start/stop it automatically) — the underlying technique (a real, ephemeral dependency) is identical either way.

**Why it matters:**
Separates the library's convenience from the actual testing property that matters.

**Common trap:**
Assuming manual container orchestration isn't "real" integration testing.

**Related:**
[Core Concepts](../handbook/testing/integration-testing-against-real-dependencies.md#core-concepts)
