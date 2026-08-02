---
title: "Week 18 Flashcards — Testing Domain Closure"
week: 18
document_type: study-pack-flashcards
status: draft
last_reviewed: 2026-08-02
---

# Week 18 Flashcards — Testing Domain Closure

15 cards, three per topic, each naming the misconception it catches.

## Card 1

**Prompt:** Why is mean latency structurally blind to tail-latency problems, not just occasionally?
**Answer:** The arithmetic mean is mathematically pulled toward the bulk of a distribution's mass — in any heavy-tailed real-world latency distribution, this means the mean is dominated by fast requests regardless of tail severity.
**Why it matters:** The core reason percentile-based monitoring, not mean-based, is the correct default.
**Common trap:** Treating a flat mean-latency dashboard as evidence there's no real regression.
**Related:** `01-performance-and-load-testing-methodology.md`

## Card 2

**Prompt:** Why can't a load test substitute for a soak test?
**Answer:** A load test's duration is typically too short to surface a problem that only manifests through time-accumulation (a leak, an unbounded cache) — regardless of traffic volume generated in that window.
**Why it matters:** Explains why soak testing is a distinct, required practice for changes touching long-lived state.
**Common trap:** Treating "we ran a load test" as covering all performance-testing concerns.
**Related:** `01-performance-and-load-testing-methodology.md`

## Card 3

**Prompt:** Besides traffic volume, what determines whether a load test catches real production issues?
**Answer:** Traffic *shape* — request mix, cache-hit pattern, data-access distribution. Uniform synthetic traffic can pass cleanly while missing the exact conditions that cause real tail latency.
**Why it matters:** A common, easy-to-miss gap even in a well-run load-testing practice.
**Common trap:** Designing a load test's volume carefully while neglecting its traffic shape.
**Related:** `01-performance-and-load-testing-methodology.md`

## Card 4

**Prompt:** Why does the live-coding test-first interview format specifically test something a take-home assignment can't?
**Answer:** It reveals whether a candidate's testing discipline survives real time pressure, since production incidents also happen under time pressure.
**Why it matters:** Explains why interviewers weight the narrated *process*, not just the final code.
**Common trap:** Writing the full implementation first and adding tests afterward.
**Related:** `02-writing-tests-live-in-an-interview.md`

## Card 5

**Prompt:** What must be confirmed before moving on from a "red" step in a live TDD cycle?
**Answer:** That the test failed for the *expected* reason (a wrong value, not a compile error or unrelated exception) — not just that it failed at all.
**Why it matters:** Demonstrates the candidate is actually reading failure output, not just running commands.
**Common trap:** Seeing red, assuming it's correct, and moving on without checking the actual message.
**Related:** `02-writing-tests-live-in-an-interview.md`

## Card 6

**Prompt:** What should a candidate do when running low on time mid-kata, rather than silently rushing?
**Answer:** Communicate a concrete scope-reduction plan explicitly — this mirrors real production trade-off communication and is itself a positive signal.
**Why it matters:** The transferable skill the interview format is really screening for.
**Common trap:** Silently cutting corners, or silently running out of time, without saying so.
**Related:** `02-writing-tests-live-in-an-interview.md`

## Card 7

**Prompt:** Who should author a consumer-driven contract — the provider or the consumer?
**Answer:** The consumer — only the consumer knows what it actually, specifically depends on; provider-authored contracts drift toward the provider's full spec rather than real usage.
**Why it matters:** The defining feature of consumer-driven contract testing versus provider-driven specs.
**Common trap:** Having the provider team define contracts unilaterally.
**Related:** `03-contract-testing-for-services.md`

## Card 8

**Prompt:** Does contract verification run against a mock of the provider, or the provider's real implementation?
**Answer:** The provider's real, live implementation — this is what gives contract testing integration-test-level confidence.
**Why it matters:** The key distinction from a unit test with a stubbed dependency.
**Common trap:** Assuming contract testing is just schema validation against a fixed spec.
**Related:** `03-contract-testing-for-services.md`

## Card 9

**Prompt:** What's the main ongoing risk to a contract-testing practice's long-term value?
**Answer:** Contract staleness — a contract no longer reflecting the consumer's real current usage, producing false positives or false negatives.
**Why it matters:** Explains why contract maintenance needs active, ongoing ownership, not a one-time setup.
**Common trap:** Treating contract authorship as a one-time task rather than tied to the consumer's own change process.
**Related:** `03-contract-testing-for-services.md`

## Card 10

**Prompt:** Why does JUnit 5 split into three modules (Platform, Jupiter, Vintage) rather than one?
**Answer:** So a framework-agnostic launcher, the modern programming model, and legacy JUnit 3/4 compatibility can evolve independently — most concretely enabling incremental, not big-bang, migration.
**Why it matters:** The architectural payoff behind the version bump, not just new annotations.
**Common trap:** Treating the module split as an implementation detail with no practical consequence.
**Related:** `04-junit5-architecture-and-advanced-features.md`

## Card 11

**Prompt:** What replaced JUnit 4's separate Runner and Rule mechanisms in JUnit 5?
**Answer:** A single, composable `Extension` interface family, registered via `@ExtendWith` — multiple extensions can apply to one test class.
**Why it matters:** Directly solves JUnit 4's deep-base-class-hierarchy pain point via composition instead of inheritance.
**Common trap:** Building a deep abstract-base-test-class hierarchy instead of composing extensions.
**Related:** `04-junit5-architecture-and-advanced-features.md`

## Card 12

**Prompt:** When should `@TestFactory` be chosen over `@ParameterizedTest`?
**Answer:** Only when the test-case set is genuinely computed at runtime — for a fixed, known set, `@ParameterizedTest`'s built-in data-source annotations are simpler and more idiomatic.
**Why it matters:** Prevents unnecessary complexity for the common, fixed-case scenario.
**Common trap:** Treating `@TestFactory` as strictly superior and always preferable.
**Related:** `04-junit5-architecture-and-advanced-features.md`

## Card 13

**Prompt:** What's the fundamental difference between what property-based testing and mutation testing each find bugs in?
**Answer:** Property-based testing finds bugs in the code under test; mutation testing finds gaps in the test suite itself.
**Why it matters:** The single most common conceptual error in this area is conflating the two.
**Common trap:** Using the two names interchangeably.
**Related:** `05-mutation-and-property-based-testing.md`

## Card 14

**Prompt:** Why can a module have 95% line coverage and still have a real, undetected test gap?
**Answer:** Coverage measures whether code executed during tests, not whether the assertions were strong enough to actually verify correct behavior.
**Why it matters:** The core reason mutation testing exists as a distinct signal from coverage percentage.
**Common trap:** Treating high line coverage as sufficient evidence of test-suite quality.
**Related:** `05-mutation-and-property-based-testing.md`

## Card 15

**Prompt:** Why should a property-based test's random seed be fixed and logged, not freshly randomized every run?
**Answer:** So any failing case is immediately, deterministically reproducible for debugging, rather than a one-off that may not recur.
**Why it matters:** A practical necessity for actually using a property-based test's failures.
**Common trap:** Running with an unlogged, freshly-randomized seed, making failures hard to reproduce.
**Related:** `05-mutation-and-property-based-testing.md`
