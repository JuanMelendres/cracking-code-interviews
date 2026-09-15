---
title: "Interview Question Bank — 08-testing"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../08-testing/INDEX.md
  - 07-api-design.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Testing

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 8 chapters yielded 14 deep questions + 5
already-leveled Junior/Mid questions (from the domain's one Junior Fundamentals
chapter, `unit-testing-fundamentals-with-junit.md`) + 6 quick-fire questions = **25
real questions**.

---

## Unit Testing Fundamentals with JUnit

Junior Fundamentals chapter — its Interview Questions already tag each by seniority.

### Q1 — What's the difference between `@Test` and `@BeforeEach`?

**Canonical treatment:** [§15](../../08-testing/unit-testing-fundamentals-with-junit.md#15-interview-questions)

**What's expected:**
- **Junior:** `@Test` marks a method JUnit runs as an actual test with a pass/fail outcome; `@BeforeEach` marks a setup method that runs before every `@Test`, not itself a test. Target tier.
- **Mid/Senior/Staff:** Not typically asked in this exact form.

### Q2 — How do you test that a method throws an exception?

**Canonical treatment:** [§15](../../08-testing/unit-testing-fundamentals-with-junit.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** `assertThrows(ExceptionType.class, () -> methodCall())`, ideally with a note on why this is more reliable than a manual `try`/`catch`/`fail()`. Target tier.
- **Senior/Staff:** Not typically asked in this exact form.

### Q3 — You have four nearly-identical test methods differing only in one input value. What would you do?

**Canonical treatment:** [§15](../../08-testing/unit-testing-fundamentals-with-junit.md#15-interview-questions)

**What's expected:**
- **Mid:** `@ParameterizedTest` with `@ValueSource` or `@CsvSource`, with a concrete before/after description. Target tier.
- **Junior/Senior/Staff:** Not typically asked in this exact form.

### Q4 — A test suite passes locally but fails intermittently in CI. What's your first hypothesis?

**Canonical treatment:** [§15](../../08-testing/unit-testing-fundamentals-with-junit.md#15-interview-questions)

**What's expected:**
- **Mid/Senior:** Check test isolation (shared mutable state, order dependence) before assuming the production code has a concurrency bug — states this as the first hypothesis, not the last one tried. Target tier.
- **Junior/Staff:** Not typically asked in this exact form.

### Q5 — How do you keep a 3,000-test suite trustworthy as a team scales?

**Canonical treatment:** [§15](../../08-testing/unit-testing-fundamentals-with-junit.md#15-interview-questions)

**What's expected:**
- **Senior/Staff:** Isolation discipline enforced by review/tooling rather than individual diligence, enabling safe parallel execution; connects this to the actual cost of an untrustworthy suite (engineers ignoring "flaky" failures, eventually missing a real one). Target tier.
- **Junior/Mid:** Not typically asked in this exact form.

---

## Test Strategy and Test Doubles

### Q1 — Where do you draw the unit/integration line?

**Canonical treatment:** [§ Interview Questions, Q1](../../08-testing/test-strategy-and-test-doubles.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States the general unit-vs-integration distinction, without the "mock verifies assumptions" framing.
- **Senior:** Correctly identifies that a mocked-database repository test only verifies the test's own assumptions, not real SQL correctness.
- **Staff:** Connects this to a concrete failure mode — a real production incident where mocked tests passed but a real migration/schema mismatch broke in production.

### Q2 — What does coverage percentage actually measure?

**Canonical treatment:** [§ Interview Questions, Q2](../../08-testing/test-strategy-and-test-doubles.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats coverage percentage as a target rather than a diagnostic — the common mistake this question targets.
- **Senior:** Explains that coverage measures only that lines/branches executed, not that assertions were meaningful.
- **Staff:** Frames coverage as a diagnostic for finding untested code, not a quality target, and names flakiness itself as a more useful design signal.

---

## Integration Testing Against Real Dependencies

### Q1 — Your team wants to mock the database in every repository test for speed. Convince me that's wrong.

**Canonical treatment:** [§ Interview Questions, Q1](../../08-testing/integration-testing-against-real-dependencies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Concedes the speed argument without defending what's actually lost — the common mistake this question targets.
- **Senior:** Correctly identifies what a mocked-database test misses — it never verifies the SQL is valid, types match, or a real constraint violation is handled correctly.
- **Staff:** Proposes the pyramid answer — repository/boundary code gets real-dependency integration tests (few); business logic gets unit tests with the repository mocked (many, fast).

### Q2 — Your integration tests are flaky — passing locally, failing in CI. Where do you look first?

**Canonical treatment:** [§ Interview Questions, Q2](../../08-testing/integration-testing-against-real-dependencies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes flakiness means "the CI environment is just slower" without checking for shared state first — the common mistake this question targets.
- **Senior:** Names shared/unclean state (a prior test's data leaking, or parallel tests racing on the same container/schema) as the top suspect and proposes per-test isolation.
- **Staff:** Connects flakiness explicitly to being a design signal worth investigating, not just a nuisance to retry past.

---

## Contract Testing for Services

### Q1 — Your organization relies on manually notifying downstream teams before any API change. What would you propose?

**Canonical treatment:** [§ Interview Questions, Q1](../../08-testing/contract-testing-for-services.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes contract testing as a purely provider-side tooling change, without acknowledging the ongoing consumer-side maintenance it requires — the common mistake this question targets.
- **Senior:** Correctly proposes consumer-driven contract testing and names the real ownership shift it requires.
- **Staff:** Identifies the "consumer without a contract" gap and proposes making contract authorship a required step for any new consumer integration.

### Q2 — A contract-verification test fails. How do you determine whether it's a genuine breaking change or a stale contract?

**Canonical treatment:** [§ Interview Questions, Q2](../../08-testing/contract-testing-for-services.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats every contract-test failure as automatically "definitely a real break" or "definitely stale" without checking actual usage — the common mistake this question targets.
- **Senior:** Correctly describes checking whether the consumer's real, current code genuinely uses the field/behavior as the deciding factor.
- **Staff:** Proposes a process fix — updating the contract in the same change that removes a dependency, not as a separate, easily-forgotten cleanup task.

---

## JUnit 5 Architecture and Advanced Features

### Q1 — When would you choose `@TestFactory` over `@ParameterizedTest`, and why not always use the more flexible option?

**Canonical treatment:** [§ Interview Questions, Q1](../../08-testing/junit5-architecture-and-advanced-features.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats `@TestFactory` as a strictly superior, always-preferable choice — the common mistake this question targets.
- **Senior:** Correctly distinguishes the two use cases — `@TestFactory` for runtime-computed case sets, `@ParameterizedTest` for a fixed, known-in-advance set.
- **Staff:** Provides a concrete, realistic example of genuinely runtime-computed test cases, not just a restated definition.

### Q2 — Run only fast tests on every commit, and the full suite nightly, without maintaining two test source trees. How?

**Canonical treatment:** [§ Interview Questions, Q2](../../08-testing/junit5-architecture-and-advanced-features.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes a separate test source directory or module split — the common mistake this question targets, introducing unnecessary structural duplication.
- **Senior:** Correctly proposes tag-based filtering (`@Tag("slow")`) from a single test source, configured in the CI pipeline.
- **Staff:** Proactively raises the tag-name-typo risk (a silent mismatch producing no error) and proposes shared, documented tag-name constants.

---

## Mutation and Property-Based Testing

### Q1 — A module has 95% line coverage. Does this tell you the suite would catch a real bug? Why or why not?

**Canonical treatment:** [§ Interview Questions, Q1](../../08-testing/mutation-and-property-based-testing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats high coverage as strong or sufficient evidence of test-suite quality — the common mistake this question targets.
- **Senior:** Correctly explains that coverage measures execution, not verification strength — a test can execute every line while asserting something weak.
- **Staff:** Proposes mutation testing as the concrete tool to measure the gap, scoped appropriately given its computational cost.

### Q2 — A teammate suggests adding a property-based test instead of trusting two example-based tests. When is this valuable, and when not?

**Canonical treatment:** [§ Interview Questions, Q2](../../08-testing/mutation-and-property-based-testing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats property-based testing as universally superior to example-based testing regardless of whether a clean property exists — the common mistake this question targets.
- **Senior:** Correctly identifies when property-based testing is and isn't a good fit — valuable when a clean, general invariant can be stated (round-trip, sorted output).
- **Staff:** Names concrete, recognizable categories of code (round-trip, structural invariants) well-suited to the technique.

---

## Performance and Load Testing Methodology

### Q1 — Your team's load-testing script hasn't run in six months, and no one can say why. What's the underlying process problem?

**Canonical treatment:** [§ Interview Questions, Q1](../../08-testing/performance-and-load-testing-methodology.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as a one-time fix ("run it now") rather than identifying the structural gap — the common mistake this question targets.
- **Senior:** Correctly identifies the lack of an automatic failure signal as the root structural issue — unlike a functional test suite, performance testing produces no automatic signal when skipped.
- **Staff:** Proposes a proportionate, scoped gating design (required for specific services/paths) rather than an all-or-nothing blanket requirement.

### Q2 — A load test passes cleanly in staging, but the same traffic volume causes real problems in production. What would you check?

**Canonical treatment:** [§ Interview Questions, Q2](../../08-testing/performance-and-load-testing-methodology.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes volume alone determines whether a load test is representative — the common mistake this question targets.
- **Senior:** Correctly identifies traffic shape (request mix, cache-hit pattern, data-access distribution), not volume, as the likely gap.
- **Staff:** Proposes an ongoing validation process for traffic-shape representativeness, recognizing it can drift over time.

---

## Writing Tests Live in an Interview

### Q1 — Implement, test-first, a function returning the second-largest distinct value in an array. Narrate each step.

**Canonical treatment:** [§ Interview Questions, Q1](../../08-testing/writing-tests-live-in-an-interview.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Writes the full implementation first and retrofits tests — the common mistake this question targets.
- **Senior:** Runs a genuine, narrated red-green-refactor loop with sensible test-case ordering (smallest meaningful case first, then duplicates, then the too-small-array edge case).
- **Staff:** Explicitly frames the too-small-array behavior as a deliberate API-design decision with a stated rationale, not an implementation detail chosen arbitrarily.

### Q2 — Midway through a live TDD kata, your test fails in a way you don't immediately understand. Walk through what you'd do.

**Canonical treatment:** [§ Interview Questions, Q2](../../08-testing/writing-tests-live-in-an-interview.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Immediately assumes the test itself must be wrong, or rewrites the implementation on a guess — the common mistake this question targets.
- **Senior:** Describes a calm, evidence-first investigation process — reading the actual assertion failure message before changing anything.
- **Staff:** Explicitly draws the parallel to real production debugging discipline, recognizing the interview format's actual purpose.

---

## Quick-fire questions (from this domain's Flashcards)

Only 2 of the 8 chapters in this domain have Flashcards sections.

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What does an integration test against a real database catch that a mocked-database test cannot? | [Integration Testing Against Real Dependencies](../../08-testing/integration-testing-against-real-dependencies.md#flashcards) |
| 2 | Is "mock vs. real dependency" an all-or-nothing choice across a codebase? | [Integration Testing Against Real Dependencies](../../08-testing/integration-testing-against-real-dependencies.md#flashcards) |
| 3 | What does Testcontainers automate that a manual Docker orchestration doesn't? | [Integration Testing Against Real Dependencies](../../08-testing/integration-testing-against-real-dependencies.md#flashcards) |
| 4 | What does `verify(gateway, times(3))` prove that `assertTrue(result)` alone cannot? | [Test Strategy, the Pyramid, and Test Doubles](../../08-testing/test-strategy-and-test-doubles.md#flashcards) |
| 5 | What's wrong with mocking the database in a repository test? | [Test Strategy, the Pyramid, and Test Doubles](../../08-testing/test-strategy-and-test-doubles.md#flashcards) |
| 6 | What does coverage percentage actually measure? | [Test Strategy, the Pyramid, and Test Doubles](../../08-testing/test-strategy-and-test-doubles.md#flashcards) |

---

## Related

- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
