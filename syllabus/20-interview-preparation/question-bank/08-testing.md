---
title: "Interview Question Bank — 08-testing"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.1
last_updated: 2026-09-29
related:
  - ../../08-testing/INDEX.md
  - 07-api-design.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Testing

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 10 chapters yielded 20 deep questions + 5
already-leveled Junior/Mid questions (from the domain's one Junior Fundamentals
chapter, `unit-testing-fundamentals-with-junit.md`) + 12 quick-fire questions = **37
real questions**. (Updated 2026-09-27: `behavior-driven-development-with-cucumber.md`
and `testing-asynchronous-and-concurrent-code.md` had complete Interview Questions
sections never indexed — a stale-index gap, not a content gap. Added 5 questions +
3 quick-fire cards; the latter chapter has no Flashcards section. Updated
2026-09-29: `test-strategy-and-test-doubles.md` v1.1 adds measured `@Mock`-versus-`@Spy`
behavior, closing a real content gap — `@Spy` appeared nowhere in the repository and
"spy" only once, as a test-double taxonomy word. Adds 1 question + 3 quick-fire cards.)

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

### Q3 — What is the difference between `@Mock` and `@Spy`, and what is the classic bug when stubbing a spy?

**Canonical treatment:** [§ Interview Questions, Q3](../../08-testing/test-strategy-and-test-doubles.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Recites the definitions — mock returns defaults, spy calls real code — and often describes a spy as "a mock that also records calls," which is not the distinction, since `verify(...)` works identically on both.
- **Senior:** Knows `when(spy.method())` **executes the real method** while stubbing it, because Mockito must evaluate the argument to `when(...)` — measured: the stubbing line alone left one real invocation recorded with its side effect applied. Names `doReturn(...).when(spy).method()` as the fix and explains that this is why two stubbing syntaxes exist. Knows that on a mock, even a state accessor returns the type default, so checking state through one asserts on stub defaults.
- **Staff:** Frames a spy as a weakening of the test's isolation guarantee — the test can now fail because of code it never meant to exercise — and treats a growing number of spies as a design signal about the production code rather than a tooling problem, with extraction as the remedy.

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

## Behavior-Driven Development with Cucumber (T-2412)

### Q1 — What's the real difference between TDD and BDD?

**Canonical treatment:** [§ Interview Questions, Q1](../../08-testing/behavior-driven-development-with-cucumber.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes BDD as a stricter or different testing methodology rather than a vocabulary/audience layer on top of the same underlying mechanics — the common mistake this question targets.
- **Senior:** States the audience/vocabulary distinction precisely — both use the same red-green rhythm underneath; TDD writes the check directly in code, for developers; BDD writes it in structured natural language (Gherkin) that non-developers can also read and validate — without conflating it with a claim that BDD tests "more thoroughly."
- **Staff:** Discusses when BDD adoption has failed organizationally (no real non-developer involvement) versus when it's working as intended.

### Q2 — How does a `Scenario Outline` with an `Examples` table actually execute?

**Canonical treatment:** [§ Interview Questions, Q2](../../08-testing/behavior-driven-development-with-cucumber.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes it as "just a for-loop" rather than understanding each row is a real, distinct test node in the reported results — the common mistake this question targets.
- **Senior:** States that each row in the `Examples` table becomes a genuinely separate, independently-reported test instance without needing to be told, and can name a real risk in how rows are chosen (a row whose expected value doesn't actually depend on the logic being varied can't catch a bug in it).
- **Staff:** Connects this to broader data-driven-testing discipline: choosing test data that genuinely exercises different code paths, not just superficially different inputs.

### Q3 — What happens when Cucumber encounters a Gherkin step with no matching step definition?

**Canonical treatment:** [§ Interview Questions, Q3](../../08-testing/behavior-driven-development-with-cucumber.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes an undefined step is silently skipped or ignored — the common mistake this question targets.
- **Senior:** Knows this produces a real, reported failure (`UndefinedStepException`) with real, actionable output — Cucumber generates a ready-to-paste Java method snippet inferred directly from the step's own wording, marked with a `PendingException` convention rather than an empty method body.
- **Staff:** Frames this as a genuine productivity affordance for incrementally building out step definitions from stakeholder-written scenarios.

---

## Testing Asynchronous and Concurrent Code (T-2420)

### Q1 — You write a test for a method that kicks off async work, using `Thread.sleep(100)` before asserting the result. It passes locally but fails intermittently in CI. Why, and how would you fix it?

**Canonical treatment:** [§ 15, Interview Questions, Q1](../../08-testing/testing-asynchronous-and-concurrent-code.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Recognizes the sleep duration as the problem but proposes "increase the sleep" as the fix — a real, measured improvement in failure rate, never a real fix, and still genuinely flaky in principle. This is the common mistake this question targets.
- **Senior:** Names `CountDownLatch` (or an equivalent real completion signal, like `CompletableFuture.join()`) specifically, and explains why widening the sleep only narrows the failure window rather than closing it — the real work occasionally takes longer than the guessed sleep, especially on a more loaded CI machine.
- **Staff:** Connects this to a broader diagnostic habit: treating new test flakiness as a real signal worth root-causing (timing guess vs. genuine concurrency bug) rather than something to retry past.

### Q2 — How would you write a test that reliably catches a race condition, given that race conditions are inherently non-deterministic?

**Canonical treatment:** [§ 15, Interview Questions, Q2](../../08-testing/testing-asynchronous-and-concurrent-code.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes a single-threaded test, or a multi-threaded test with too few iterations to make the race statistically likely to reproduce — both provide false confidence, the common mistake this question targets.
- **Senior:** Names the specific mechanism — running many threads performing many concurrent operations against the shared state, released simultaneously to maximize contention, with real iteration counts large enough to make luck-based passing unlikely (this chapter's real demo: 8 threads × 100,000 increments) — and can cite or estimate a real order-of-magnitude failure rate at that scale.
- **Staff:** Generalizes this into a pre-release verification practice for any genuinely concurrent production code change before it ships.

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
| 7 | What's the actual difference between TDD and BDD, beyond syntax? | [Behavior-Driven Development with Cucumber](../../08-testing/behavior-driven-development-with-cucumber.md#flashcards) |
| 8 | Does a `Scenario Outline`'s `Examples` table run as one test looping over data, or as genuinely separate tests? | [Behavior-Driven Development with Cucumber](../../08-testing/behavior-driven-development-with-cucumber.md#flashcards) |
| 9 | What happens when Cucumber hits a Gherkin step with no matching step definition? | [Behavior-Driven Development with Cucumber](../../08-testing/behavior-driven-development-with-cucumber.md#flashcards) |
| 10 | On a mock versus a spy, what does an unstubbed method return and what real state changes? | [Test Strategy, the Pyramid, and Test Doubles](../../08-testing/test-strategy-and-test-doubles.md#flashcards) |
| 11 | Why is `when(spy.write("x")).thenReturn(...)` a bug, and what replaces it? | [Test Strategy, the Pyramid, and Test Doubles](../../08-testing/test-strategy-and-test-doubles.md#flashcards) |
| 12 | Does a stub apply when a spy's real method calls that method on itself? | [Test Strategy, the Pyramid, and Test Doubles](../../08-testing/test-strategy-and-test-doubles.md#flashcards) |

---

## Related

- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
