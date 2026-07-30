---
title: "T-1104 · Integration Testing Against Real Dependencies"
topic_id: T-1104
domain: Testing
tier: Advanced
iwi: 6.50
prerequisites: [T-1101, T-1103]
unlocks: []
week: 11
last_reviewed: 2026-07-29
---

# T-1104 · Integration Testing Against Real Dependencies

**IWI 6.50 · Advanced tier**

**Verification note:** the test run in §3 is real, executed output from `practice/java/week-11/testing/src/OrderRepositoryIntegrationTest.java` against a genuine, live Postgres 16 (Docker) — not an in-memory fake or a mock.

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

An integration test verifies that code correctly integrates with a REAL instance of a dependency it talks to — a database, a message broker, an external API — rather than a mock's assumptions about how that dependency behaves. Testcontainers is the standard library for this: it provisions a real, ephemeral, Docker-based instance of the dependency for the duration of the test run, then tears it down.

## 2. Why it exists

`01-test-strategy-and-test-doubles.md` named the exact failure mode this closes: a repository test that mocks the database only verifies the test's own assumptions, never real SQL behavior. `OrderRepository`'s entire job is translating Java calls into real SQL against Postgres — the only way to actually test that translation is against a real Postgres.

## 3. A real integration test, against a real Postgres

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

This test genuinely exercises the JDBC driver, the real SQL (`INSERT ... RETURNING id`, `SELECT`), and a real Postgres server's storage and retrieval — a bug in the SQL itself (a typo in a column name, a type mismatch, a broken `RETURNING` clause) would be caught here and nowhere in a unit test, because a mocked `PaymentGateway`-style test double for the database would simply return whatever the test told it to, regardless of whether the real SQL is even valid.

## 4. A scoping note, stated honestly

This chapter's integration test connects directly via JDBC to a Postgres container started with a plain `docker run` command, **not** via the Testcontainers Java library itself. Testcontainers automates exactly this pattern (start a container, wait for it to be ready, tear it down, all from within the test lifecycle) but its own dependency tree (`docker-java`, multiple transport implementations, `jna`, `commons-compress`, and more) doesn't fit this repository's established plain-jar, no-build-tool convention cleanly. The TECHNIQUE demonstrated here — a real, ephemeral, Docker-provisioned dependency instead of a mock — is identical to what Testcontainers automates; what's different is only how the container's lifecycle is managed (a shell command here, versus JUnit lifecycle annotations backed by the library in a Maven/Gradle project). Stated explicitly rather than glossed over, per this repository's own integrity convention (see `MANIFEST.md`).

## 5. Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| Mock the database | Fastest, no infrastructure needed | Tests the mock's assumptions, not real SQL behavior — misses real bugs |
| Real Postgres via Testcontainers (production convention) | Automated container lifecycle tied to test execution, portable across machines with Docker | A real dependency to manage (Docker required); slower than a unit test |
| Real Postgres via manual Docker orchestration (this chapter's approach) | Same real-dependency benefit, no extra library dependency tree | Container lifecycle isn't automatically tied to test execution — must be started/stopped separately |
| In-memory fake database (e.g., H2 in Postgres-compatibility mode) | Fast, no Docker needed | Real compatibility gaps exist — an in-memory fake is still not the real database, and Postgres-specific behavior (this program's whole reason for choosing Postgres, per earlier weeks) can differ subtly |

## 6. Interview questions

### Q1. Your team wants to mock the database in every repository test for speed. Convince me that's wrong.

- **Expected answer:** a mocked-database test verifies only that the code calls the mock the way the test expects — it never verifies the SQL is valid, that types match, that a `RETURNING` clause behaves as expected, or that a real constraint violation is handled correctly. It provides false confidence at exactly the layer where real bugs live.
- **Common mistakes:** conceding the speed argument without defending what's actually lost.
- **Follow-up questions:** "So should EVERY test hit a real database?"
- **Senior-level expectations:** correctly identifies what a mocked-database test misses.
- **Staff-level expectations:** proposes the pyramid answer — repository/boundary code gets integration tests against a real dependency (few, but real); business logic that merely CALLS a repository gets unit tests with the repository itself mocked (many, fast) — not an all-or-nothing choice.

### Q2. Your integration tests are flaky — passing locally, failing in CI. Where do you look first?

- **Expected answer:** shared, unclean state between test runs is the most common cause — a prior test's data leaking into this one because the database wasn't reset between tests, or tests running in parallel against the same container/schema and racing each other.
- **Common mistakes:** assuming flakiness means "the CI environment is just slower" without checking for shared state first.
- **Follow-up questions:** "How do you make integration tests independent of each other?"
- **Senior-level expectations:** names shared/unclean state as the top suspect and proposes per-test isolation (a fresh schema, a transaction rolled back after each test, or a fresh container per test class).
- **Staff-level expectations:** connects flakiness explicitly to `01-test-strategy-and-test-doubles.md`'s point that flakiness is itself a design signal worth investigating, not just a nuisance to retry past — a flaky integration test is often revealing a real concurrency or isolation assumption that doesn't hold.

## 7. Common mistakes

- Believing an in-memory fake database is "close enough" to the real one, especially when the real database was chosen specifically for engine-specific behavior (as this program's PostgreSQL focus has been throughout).
- Treating "mock vs. real dependency" as an all-or-nothing choice rather than a per-layer decision (mock at the business-logic layer, real dependency at the boundary layer).
- Skipping integration tests entirely because they're slower, rather than keeping them few but real.

## 8. Staff-level discussion

The scoping note in §4 is itself worth internalizing as a Staff-level habit: choosing a lighter-weight technique that achieves the same real learning outcome, and stating the trade-off explicitly rather than either avoiding the topic or claiming a library was used when it wasn't. The same judgment applies in real engineering decisions constantly — a team can achieve "test against a real dependency" without necessarily adopting every convention a specific library implies, as long as the actual property that matters (a REAL Postgres, not a mock) is preserved and the deviation from convention is documented, not hidden.

## 9. Summary

An integration test against a real, Docker-provisioned Postgres genuinely exercises the SQL and JDBC boundary code that a mocked-database test would only pretend to verify — demonstrated directly: a real `INSERT ... RETURNING id` and a real `SELECT`, both executed against a live database, in 154ms. This chapter's implementation manages the container via direct Docker orchestration rather than the Testcontainers library itself, an explicit, stated scope decision — the technique (real, ephemeral dependency over a mock) is the same either way.

## 10. Key Takeaways

- An integration test's entire value is exercising REAL behavior a mock would only assume.
- Testcontainers automates container lifecycle management for this pattern; the underlying technique (real, ephemeral dependency) is what matters, not the specific library.
- "Mock or real dependency" is a per-layer decision, not all-or-nothing — mock at the business-logic layer, use a real dependency at the boundary layer.

## 11. Cheat Sheet

| Code being tested | Approach |
|---|---|
| A repository/DAO whose whole job is real SQL | Integration test, real database |
| Business logic that calls a repository | Unit test, mock the repository |
| An API client whose whole job is a real HTTP call | Integration test, real (or realistically faked) server |

## 12. Flashcards

1. **Q: What does an integration test against a real database catch that a mocked-database test cannot?** A: Real SQL correctness, type mismatches, `RETURNING`-clause behavior, real constraint violations — anything about the actual database's behavior, not the test's assumptions about it.
2. **Q: Is "mock vs. real dependency" an all-or-nothing choice across a codebase?** A: No — it's a per-layer decision: mock at the business-logic layer (fast, many tests), use a real dependency at the boundary layer (fewer, but real).
3. **Q: What does Testcontainers automate that a manual Docker orchestration doesn't?** A: Tying the container's lifecycle directly to the test's own lifecycle (JUnit annotations start/stop it automatically) — the underlying technique (a real, ephemeral dependency) is identical either way.

(Full week-level deck: `07-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce: `practice/java/week-11/testing/src/OrderRepositoryIntegrationTest.java` (see that directory's `README.md` for the exact Docker setup).
2. Add a second integration test asserting a real constraint violation (e.g., inserting a NULL into a `NOT NULL` column) produces the exact real Postgres exception — something a mock could never verify without the test author guessing what Postgres actually throws.
3. In a project using Maven or Gradle, look up how the actual Testcontainers library would replace this chapter's manual `docker run` setup — what JUnit annotations manage the container lifecycle?

## 14. Additional Reading

- [Testcontainers documentation](https://testcontainers.com/)

## 15. Official References

- [Testcontainers — JUnit 5 Quickstart](https://java.testcontainers.org/quickstart/junit_5_quickstart/)
