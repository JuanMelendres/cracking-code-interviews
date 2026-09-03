---
title: "Cheat Sheet: Integration Testing Against Real Dependencies"
slug: integration-testing-against-real-dependencies
document_type: cheat-sheet
domain: testing
topic_id: T-1104
canonical: ../handbook/testing/integration-testing-against-real-dependencies.md
last_updated: 2026-08-04
---

# Integration Testing Against Real Dependencies

**Canonical chapter:** [`syllabus/08-testing/integration-testing-against-real-dependencies.md`](../syllabus/08-testing/integration-testing-against-real-dependencies.md)

## Core Mental Model

An integration test's only job is to prove a boundary actually works against the real thing it talks to — a mock can only prove the code calls its own assumptions correctly. If the code under test's entire responsibility is translating calls into real behavior against an external system (SQL against a database, HTTP against an API), a mock of that system can never validate the translation is actually correct; only the real system can.

## Essential Definitions

- **Integration test** — verifies that code correctly integrates with a REAL instance of a dependency it talks to — a database, a message broker, an external API — rather than a mock's assumptions about how that dependency behaves.
- **Testcontainers** — the standard library for this: provisions a real, ephemeral, Docker-based instance of the dependency for the duration of the test run, then tears it down.
- **Flaky integration tests (root cause)** — the most common cause of an integration test passing locally and failing in CI is unclean or shared state between test runs, not environmental slowness.

## Decision Table

| Approach | Benefit | Cost |
|---|---|---|
| Mock the database | Fastest, no infrastructure needed | Tests the mock's assumptions, not real SQL behavior — misses real bugs |
| Real Postgres via Testcontainers | Automated container lifecycle tied to test execution, portable across machines with Docker | A real dependency to manage; slower than a unit test |
| Real Postgres via manual Docker orchestration | Same real-dependency benefit, no extra library | Lifecycle not automatically tied to test execution — must be started/stopped separately |
| In-memory fake database (e.g., H2) | Fast, no Docker needed | Real compatibility gaps — engine-specific behavior can differ subtly |

| Code being tested | Approach |
|---|---|
| A repository/DAO whose whole job is real SQL | Integration test, real database |
| Business logic that calls a repository | Unit test, mock the repository |
| An API client whose whole job is a real HTTP call | Integration test, real (or realistically faked) server |

## Key Numbers (real, executed — `OrderRepositoryIntegrationTest.java`, live Postgres 16 container)

```
Test run finished after 154 ms — 1 tests successful, 0 tests failed
Real INSERT ... RETURNING id + SELECT against a live Postgres container: 154ms total
Amount 7777 inserted, read back matching exactly
```
A genuine NOT NULL constraint violation raises a real `PSQLException`, SQLSTATE `23502` — a real error a mock could not reproduce faithfully.

## Common Pitfalls

- Believing an in-memory fake database is "close enough" to the real one, especially when the real database was chosen specifically for engine-specific behavior
- Treating "mock vs. real dependency" as an all-or-nothing choice rather than a per-layer decision
- Skipping integration tests entirely because they're slower, rather than keeping them few but real

## Interview Answer Skeleton

**30-sec:** An integration test exercises a real instance of a dependency specifically because a mock can only verify its own configured assumptions, never real SQL or protocol correctness. Measured directly: a real `INSERT ... RETURNING id` and `SELECT` against a live Postgres container, in 154ms.

**2-min:** Add why it exists + how it works (Testcontainers vs. manual orchestration) + the per-layer decision (mock business logic's dependencies, use real dependencies at the boundary) + the CI flakiness production example.

**Whiteboard:** Draw a boundary line between "business logic" and "repository/boundary code." On the business-logic side, a mocked repository feeding fast unit tests. On the boundary side, a real, ephemeral Postgres container feeding a smaller number of integration tests. Annotate the line: "only real dependencies verify what crosses here."

**Staff-level framing:** flakiness is a design signal worth investigating, not just a nuisance to retry past — it usually points at unclean or shared state, the same category of reasoning as [Test Strategy and Test Doubles](test-strategy-and-test-doubles.md)'s per-layer mocking decision.

## Production Warning Signs

- **Real incident pattern:** integration tests pass on every dev machine but fail roughly 1 in 10 CI runs, always a different, seemingly unrelated test. Root cause: CI runs test-class-level parallelism against a single shared Postgres container, and two specific tests insert a row with the same hardcoded ID, racing when run concurrently — not environmental slowness.
- Fix: give each test class its own schema (or wrap each test in a rolled-back transaction); unique random IDs per test help but don't cover aggregate counts/sequences alone. Prevention: isolate parallel units' data explicitly before enabling parallelism, not after a flaky run is noticed.

## Related

- [Test Strategy and Test Doubles](test-strategy-and-test-doubles.md)
