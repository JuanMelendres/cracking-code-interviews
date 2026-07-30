---
title: "Integration Testing Against Real Dependencies"
slug: integration-testing-against-real-dependencies
document_type: handbook-chapter
domain: testing
status: draft
version: 1.0
last_updated: 2026-07-30
difficulty:
  - intermediate
  - advanced
target_levels:
  - senior
  - staff
estimated_reading_minutes: 20
prerequisites:
  - test-strategy-and-test-doubles.md
related:
  - test-strategy-and-test-doubles.md
  - ../../study-packs/week-11/02-integration-testing-against-real-dependencies.md
official_references:
  - https://testcontainers.com/
  - https://java.testcontainers.org/quickstart/junit_5_quickstart/
---

# Integration Testing Against Real Dependencies

> **Topic register:** T-1104 · IWI 6.50 · Advanced tier
> **Provenance:** the test run in this chapter is real, executed output from [`practice/java/week-11/testing/src/OrderRepositoryIntegrationTest.java`](../../practice/java/week-11/testing/src/OrderRepositoryIntegrationTest.java) against a genuine, live Postgres 16 (Docker) — not an in-memory fake or a mock.

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Mental Model](#mental-model)
4. [Definition and Purpose](#definition-and-purpose)
5. [Core Concepts](#core-concepts)
6. [Internal Implementation](#internal-implementation)
7. [Production Scenarios](#production-scenarios)
8. [Trade-offs](#trade-offs)
9. [Decision Framework](#decision-framework)
10. [Common Mistakes](#common-mistakes)
11. [Anti-Patterns](#anti-patterns)
12. [Best Practices](#best-practices)
13. [Interview Answer Framework](#interview-answer-framework)
14. [Interview Questions](#interview-questions)
15. [Summary](#summary)
16. [Key Takeaways](#key-takeaways)
17. [Cheat Sheet](#cheat-sheet)
18. [Flashcards](#flashcards)
19. [Practice Exercises](#practice-exercises)
20. [Solutions](#solutions)
21. [Additional Reading](#additional-reading)
22. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can:

- Explain what a real-dependency integration test catches that a mocked-database test structurally cannot.
- State the difference between what Testcontainers automates and the underlying technique it automates.
- Diagnose flaky integration tests by checking shared state first.
- Decide, per code layer, whether it needs a mock or a real dependency.

## Why This Matters in Interviews

This topic tests whether a candidate can defend real-dependency testing against a "just mock it, it's faster" objection with specifics, not vibes. It's Advanced-tier because the honest answer requires naming exactly what a mock can never verify (real SQL correctness, real constraint behavior) and because flaky integration tests are a near-universal real-world pain point interviewers use to probe debugging instinct.

## Mental Model

**An integration test's only job is to prove a boundary actually works against the real thing it talks to — a mock can only prove the code calls its own assumptions correctly.** If the code under test's entire responsibility is translating calls into real behavior against an external system (SQL against a database, HTTP against an API), a mock of that system can never validate the translation is actually correct; only the real system can.

## Definition and Purpose

An integration test verifies that code correctly integrates with a REAL instance of a dependency it talks to — a database, a message broker, an external API — rather than a mock's assumptions about how that dependency behaves. Testcontainers is the standard library for this: it provisions a real, ephemeral, Docker-based instance of the dependency for the duration of the test run, then tears it down.

[Test Strategy, the Pyramid, and Test Doubles](test-strategy-and-test-doubles.md) named the exact failure mode this closes: a repository test that mocks the database only verifies the test's own assumptions, never real SQL behavior. `OrderRepository`'s entire job is translating Java calls into real SQL against Postgres — the only way to actually test that translation is against a real Postgres.

## Core Concepts

### A real dependency exercises the real translation layer

A mocked `PaymentGateway`-style test double for the database simply returns whatever the test told it to, regardless of whether the real SQL is even valid. A real integration test genuinely exercises the JDBC driver, the real SQL, and a real server's storage and retrieval — a typo in a column name, a type mismatch, or a broken `RETURNING` clause is caught here and nowhere in a unit test.

### The technique matters more than the specific library

Testcontainers automates a pattern: start a container, wait for it to be ready, tear it down, all tied to the test lifecycle via JUnit annotations. The underlying technique — a real, ephemeral, Docker-provisioned dependency instead of a mock — can also be achieved via manual container orchestration; what differs is only how the lifecycle is managed, not whether the real property (a real database, not a mock) is preserved.

### Mock vs. real dependency is a per-layer decision

Business logic that merely calls a repository gets unit tests with the repository mocked (many, fast). The repository/boundary code itself gets integration tests against a real dependency (few, but real). Treating this as an all-or-nothing choice across a codebase misses that each layer has a different actual testing need.

### Flaky integration tests usually mean shared state

The most common cause of an integration test passing locally and failing in CI is unclean or shared state between test runs — a prior test's data leaking into this one, or tests running in parallel against the same container/schema and racing each other.

## Internal Implementation

**Real output**, `OrderRepositoryIntegrationTest` against a live Postgres 16 container:

```
├─ JUnit Jupiter ✔
│  └─ OrderRepositoryIntegrationTest ✔
│     └─ insertedOrderIsReallyPersistedAndReadableBack() ✔

Test run finished after 154 ms
[1 tests successful, 0 tests failed]
```

```java
@Test
void insertedOrderIsReallyPersistedAndReadableBack() throws Exception {
    OrderRepository repo = new OrderRepository(JDBC_URL);
    long id = repo.insert("integration-test-customer", 7777);
    long amount = repo.findAmountById(id);
    assertEquals(7777, amount, "the amount read back must be the exact real value Postgres stored");
}
```

This test genuinely exercises the JDBC driver, the real SQL (`INSERT ... RETURNING id`, `SELECT`), and a real Postgres server's storage and retrieval.

**A scoping note, stated honestly:** this chapter's integration test connects directly via JDBC to a Postgres container started with a plain `docker run` command, not via the Testcontainers Java library itself. Testcontainers automates exactly this pattern but its own dependency tree (`docker-java`, multiple transport implementations, `jna`, `commons-compress`, and more) doesn't fit a plain-jar, no-build-tool convention cleanly. The technique demonstrated — a real, ephemeral, Docker-provisioned dependency instead of a mock — is identical to what Testcontainers automates; what's different is only how the container's lifecycle is managed.

## Production Scenarios

### Scenario: integration tests pass locally but fail intermittently in CI, traced to shared container state

**Symptoms.** A service's integration test suite passes reliably on every developer's machine but fails roughly 1 in 10 CI runs, always on a different, seemingly unrelated test, with no consistent error.

**Impact.** Intermittent CI failures erode trust in the test suite; developers begin re-running failed builds reflexively instead of investigating, letting a real underlying issue persist.

**Initial hypotheses.** CI infrastructure is simply slower or less reliable than local machines (checked — failures don't correlate with CI load or timing, they correlate with specific test co-occurrence); a genuine flaky bug in the code under test (checked — the same "failing" assertions pass when the failing test is run in isolation); tests are sharing state in a single database container run in parallel (correct).

**Evidence.** The CI configuration runs the integration test suite with test-class-level parallelism against a single shared Postgres container; the intermittent failures always involve two specific tests that both insert a row with the same hardcoded ID, and the failure only occurs when both happen to run concurrently against the shared schema.

**Diagnosis.** Exactly this chapter's named flakiness cause: unclean or shared state between test runs — specifically, parallel tests racing against the same container/schema with overlapping hardcoded test data, rather than each test using isolated data or a reset schema.

**Immediate mitigation.** Disable test-class-level parallelism temporarily to stop the races while a real fix is implemented.

**Permanent remediation.** Give each test class its own schema (or wrap each test in a transaction rolled back afterward) so concurrent tests can never observe each other's data, restoring safe parallelism without the race.

**Alternatives considered.** Using unique, randomly-generated IDs per test instead of hardcoded ones — a partial mitigation, accepted as a supplementary practice, but rejected as the sole fix since it doesn't address every possible form of shared-state interference (e.g., aggregate counts, sequences).

**Trade-offs.** Per-test-class schema isolation adds setup/teardown overhead per test class — accepted, since the alternative is an unreliable CI signal that erodes confidence in every test run, not just the affected ones.

**Prevention.** Any integration test suite introducing parallelism should isolate each parallel unit's data explicitly (isolated schema, or transactional rollback), verified before parallelism is enabled, not discovered via intermittent CI failures.

**Interview lesson.** This is Interview Question 2's underlying scenario played out at real CI scale: a "passes locally, fails in CI" report resolved by checking shared state first, exactly as the expected answer names.

## Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| Mock the database | Fastest, no infrastructure needed | Tests the mock's assumptions, not real SQL behavior — misses real bugs |
| Real Postgres via Testcontainers (production convention) | Automated container lifecycle tied to test execution, portable across machines with Docker | A real dependency to manage (Docker required); slower than a unit test |
| Real Postgres via manual Docker orchestration | Same real-dependency benefit, no extra library dependency tree | Container lifecycle isn't automatically tied to test execution — must be started/stopped separately |
| In-memory fake database (e.g., H2 in Postgres-compatibility mode) | Fast, no Docker needed | Real compatibility gaps exist — an in-memory fake is still not the real database, and engine-specific behavior can differ subtly |

## Decision Framework

1. **Is this code's entire responsibility translating calls into real behavior against an external system?** Integration test, real dependency — a mock can't validate the translation.
2. **Is this business logic that merely calls a repository/client?** Unit test, mock the repository/client — the real-dependency verification belongs one layer down.
3. **Are integration tests flaky, passing locally but failing in CI?** Check shared/unclean state between parallel or sequential runs first, before assuming environmental flakiness.
4. **Is a library like Testcontainers a good fit for this project's conventions?** If not, manual container orchestration achieves the same real-dependency property — state the trade-off explicitly rather than skipping real-dependency testing entirely.

## Common Mistakes

- Believing an in-memory fake database is "close enough" to the real one, especially when the real database was chosen specifically for engine-specific behavior.
- Treating "mock vs. real dependency" as an all-or-nothing choice rather than a per-layer decision.
- Skipping integration tests entirely because they're slower, rather than keeping them few but real.

## Anti-Patterns

- **Running integration tests with shared, uncleaned state** across parallel or sequential test runs.
- **Substituting an in-memory fake for a database chosen specifically for its engine-specific behavior**, silently losing the property the choice was made for.
- **Assuming "passes locally, fails in CI" flakiness is environmental** without checking for shared state first.

## Best Practices

- Isolate each test's data (fresh schema, or a transaction rolled back after the test) before enabling any parallelism.
- Reserve integration tests for boundary/repository code; keep business logic on mocked-dependency unit tests.
- State any deviation from a standard library's convention (e.g., manual Docker orchestration instead of Testcontainers) explicitly, preserving the underlying technique.

## Interview Answer Framework

### 30-Second Answer

An integration test exercises a real instance of a dependency (a real Postgres, not a mock) specifically because a mock can only verify its own configured assumptions, never real SQL or protocol correctness. Measured directly: a real `INSERT ... RETURNING id` and `SELECT` against a live Postgres container, in 154ms.

### 2-Minute Answer

Definition: an integration test verifies code against a real instance of a dependency it talks to, rather than a mock. Why it exists: a repository's whole job is translating calls into real SQL — only a real database can verify that translation. How it works: Testcontainers (or manual Docker orchestration) provisions a real, ephemeral dependency tied to the test run. One important trade-off: mock vs. real dependency is a per-layer decision, not all-or-nothing — mock at the business-logic layer, use a real dependency at the boundary layer. Production example: a real measured test exercising a live Postgres container, and a CI flakiness incident traced to shared container state between parallel tests, not environmental noise.

### 10-Minute Deep Dive

Cover, in order: the mental model — only a real dependency can verify a translation layer (mental model); the measured real-Postgres test run (internals, real evidence); the scoping note on Testcontainers vs. manual orchestration (core concepts); the per-layer mock-vs-real decision framework (decision framework); flaky-test diagnosis via shared state (common mistakes); and close with the production scenario — a CI flakiness incident resolved by isolating shared container state.

### Whiteboard Explanation

Draw a boundary line between "business logic" and "repository/boundary code." On the business-logic side, draw a mocked repository feeding fast unit tests. On the boundary side, draw a real, ephemeral Postgres container feeding a smaller number of integration tests. Annotate the boundary line: "only real dependencies verify what crosses here."

### Production Example

The CI flakiness incident in [§ Production Scenarios](#production-scenarios): integration tests passing locally but failing intermittently in CI, traced to parallel tests racing against shared container state rather than environmental flakiness.

### Trade-offs to Mention

State unprompted: an in-memory fake database still isn't the real thing, and can hide engine-specific bugs; Testcontainers' value is lifecycle automation, not a fundamentally different technique than manual orchestration; mock-vs-real is a per-layer decision.

### Common Candidate Mistakes

Conceding the speed argument for mocking the database without defending what's actually lost; assuming CI flakiness means "the environment is slower" without checking for shared state.

### Typical Follow-Up Questions

1. "So should EVERY test hit a real database?"
2. "How do you make integration tests independent of each other?"

### Senior-Level Expectations

Correctly identifies what a mocked-database test misses; names shared/unclean state as the top suspect for flaky integration tests.

### Staff-Level Discussion

Choosing a lighter-weight technique that achieves the same real learning/verification outcome, and stating the trade-off explicitly rather than either avoiding the topic or claiming a specific library was used when it wasn't, is itself a Staff-level habit worth internalizing. The same judgment applies in real engineering decisions constantly — a team can achieve "test against a real dependency" without necessarily adopting every convention a specific library implies, as long as the actual property that matters (a REAL database, not a mock) is preserved and the deviation from convention is documented, not hidden. For flakiness specifically, connecting it to [Test Strategy](test-strategy-and-test-doubles.md)'s point that flakiness is itself a design signal worth investigating (not just a nuisance to retry past) demonstrates the deeper diagnostic instinct.

## Interview Questions

### Question 1 — Your team wants to mock the database in every repository test for speed. Convince me that's wrong.

**Why interviewers ask it.** Tests whether the candidate can defend real-dependency testing with specifics under a plausible-sounding objection.

**Expected answer.** A mocked-database test verifies only that the code calls the mock the way the test expects — it never verifies the SQL is valid, that types match, that a `RETURNING` clause behaves as expected, or that a real constraint violation is handled correctly.

**Minimum acceptable answer.** States that mocking the database misses real SQL verification, even without full specifics.

**Strong Senior answer.** Correctly identifies what a mocked-database test misses.

**Staff-level extension.** Proposes the pyramid answer — repository/boundary code gets integration tests against a real dependency (few, but real); business logic that merely calls a repository gets unit tests with the repository itself mocked (many, fast) — not an all-or-nothing choice.

**Common mistakes.** Conceding the speed argument without defending what's actually lost.

**Likely follow-ups.** "So should EVERY test hit a real database?"

**Evaluation criteria (1–5).** 1: concedes mocking is fine everywhere. 3: correctly names what's missed. 5: correct answer plus the per-layer pyramid proposal.

**Related references.** [§ Core Concepts](#core-concepts).

---

### Question 2 — Your integration tests are flaky — passing locally, failing in CI. Where do you look first?

**Why interviewers ask it.** A near-universal real-world debugging scenario; tests instinct, not memorized knowledge.

**Expected answer.** Shared, unclean state between test runs is the most common cause — a prior test's data leaking into this one, or tests running in parallel against the same container/schema and racing each other.

**Minimum acceptable answer.** Suggests checking for state-related causes, even without naming parallelism specifically.

**Strong Senior answer.** Names shared/unclean state as the top suspect and proposes per-test isolation.

**Staff-level extension.** Connects flakiness explicitly to the point that flakiness is itself a design signal worth investigating, not just a nuisance to retry past — a flaky integration test is often revealing a real concurrency or isolation assumption that doesn't hold.

**Common mistakes.** Assuming flakiness means "the CI environment is just slower" without checking for shared state first.

**Likely follow-ups.** "How do you make integration tests independent of each other?"

**Evaluation criteria (1–5).** 1: blames the environment with no investigation. 3: names shared state and proposes isolation. 5: correct diagnosis plus the flakiness-as-design-signal framing.

**Related references.** [§ Production Scenarios](#production-scenarios).

## Summary

An integration test against a real, Docker-provisioned Postgres genuinely exercises the SQL and JDBC boundary code that a mocked-database test would only pretend to verify — demonstrated directly: a real `INSERT ... RETURNING id` and a real `SELECT`, both executed against a live database, in 154ms. This chapter's implementation manages the container via direct Docker orchestration rather than the Testcontainers library itself, an explicit, stated scope decision — the technique (real, ephemeral dependency over a mock) is the same either way.

## Key Takeaways

- An integration test's entire value is exercising REAL behavior a mock would only assume.
- Testcontainers automates container lifecycle management for this pattern; the underlying technique (real, ephemeral dependency) is what matters, not the specific library.
- "Mock or real dependency" is a per-layer decision, not all-or-nothing — mock at the business-logic layer, use a real dependency at the boundary layer.

## Cheat Sheet

| Code being tested | Approach |
|---|---|
| A repository/DAO whose whole job is real SQL | Integration test, real database |
| Business logic that calls a repository | Unit test, mock the repository |
| An API client whose whole job is a real HTTP call | Integration test, real (or realistically faked) server |

## Flashcards

### Card: What a real-database integration test catches

**Prompt:**
What does an integration test against a real database catch that a mocked-database test cannot?

**Answer:**
Real SQL correctness, type mismatches, `RETURNING`-clause behavior, real constraint violations — anything about the actual database's behavior, not the test's assumptions about it.

**Why it matters:**
The concrete gap that makes mocked-only repository test suites give false confidence.

**Common trap:**
Believing a mocked test suite covers the repository layer adequately.

**Related:**
[Internal Implementation](#internal-implementation)

### Card: Mock vs. real dependency scope

**Prompt:**
Is "mock vs. real dependency" an all-or-nothing choice across a codebase?

**Answer:**
No — it's a per-layer decision: mock at the business-logic layer (fast, many tests), use a real dependency at the boundary layer (fewer, but real).

**Why it matters:**
Prevents both under-testing boundaries and over-mocking business logic.

**Common trap:**
Assuming every test must either always mock or always use a real dependency.

**Related:**
[Decision Framework](#decision-framework)

### Card: What Testcontainers automates

**Prompt:**
What does Testcontainers automate that a manual Docker orchestration doesn't?

**Answer:**
Tying the container's lifecycle directly to the test's own lifecycle (JUnit annotations start/stop it automatically) — the underlying technique (a real, ephemeral dependency) is identical either way.

**Why it matters:**
Separates the library's convenience from the actual testing property that matters.

**Common trap:**
Assuming manual container orchestration isn't "real" integration testing.

**Related:**
[Core Concepts](#core-concepts)

## Practice Exercises

1. Reproduce: [`practice/java/week-11/testing/src/OrderRepositoryIntegrationTest.java`](../../practice/java/week-11/testing/src/OrderRepositoryIntegrationTest.java).
2. Add a second integration test asserting a real constraint violation (e.g., inserting a NULL into a `NOT NULL` column) produces the exact real Postgres exception.
3. In a project using Maven or Gradle, look up how the actual Testcontainers library would replace this chapter's manual `docker run` setup — what JUnit annotations manage the container lifecycle?

## Solutions

**Exercise 1.** Expected output matches this chapter's measured trace: the insert-then-read-back test passes in ~154ms against the live container.

**Exercise 2.** A real Postgres `NOT NULL` violation raises a genuine `PSQLException` with SQLSTATE `23502` — a real exception a mock could never produce correctly without the test author having independently looked up and hardcoded the exact real error, defeating the point of testing against the real thing.

**Exercise 3.** Testcontainers provides `@Testcontainers` and `@Container` JUnit 5 annotations that start the specified container before the test class runs and tear it down afterward, automatically tied to the test lifecycle — replacing the manual `docker run`/teardown shell commands this chapter's setup uses.

## Additional Reading

- [Testcontainers documentation](https://testcontainers.com/)

## Official References

- [Testcontainers — JUnit 5 Quickstart](https://java.testcontainers.org/quickstart/junit_5_quickstart/)
