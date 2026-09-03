---
title: "Flashcards: Mutation and Property-Based Testing"
slug: mutation-and-property-based-testing
document_type: flashcard-deck
domain: testing
topic_id: T-1107
canonical: ../handbook/testing/mutation-and-property-based-testing.md
last_updated: 2026-08-06
---

# Flashcards: Mutation and Property-Based Testing

**Canonical chapter:** [`syllabus/08-testing/mutation-and-property-based-testing.md`](../syllabus/08-testing/mutation-and-property-based-testing.md)

## Card: What each technique finds bugs in

**Prompt:**
What's the fundamental difference between what property-based testing and mutation testing each find bugs in?

**Answer:**
Property-based testing finds bugs in the code under test; mutation testing finds gaps in the test suite itself.

**Why it matters:**
The core distinction that keeps the two techniques from being conflated as interchangeable "advanced testing" tools.

**Common trap:**
Treating property-based testing and mutation testing as solving the same problem.

**Related:**
[handbook/testing/mutation-and-property-based-testing.md](../syllabus/08-testing/mutation-and-property-based-testing.md)

## Card: Why 95% coverage can still hide a real gap

**Prompt:**
Why can a module have 95% line coverage and still have a real, undetected test gap?

**Answer:**
Coverage measures whether code executed during tests, not whether the assertions were strong enough to actually verify correct behavior — a weak assertion can execute a buggy line without catching the bug.

**Why it matters:**
The core justification for mutation testing existing at all, distinct from a coverage metric.

**Common trap:**
Treating a high coverage percentage as proof the corresponding assertions are actually strong.

**Related:**
[handbook/testing/mutation-and-property-based-testing.md](../syllabus/08-testing/mutation-and-property-based-testing.md)

## Card: Why a property-based test's seed should be fixed

**Prompt:**
Why should a property-based test's random seed be fixed and logged, not freshly randomized every run?

**Answer:**
So any failing case is immediately, deterministically reproducible for debugging, rather than a one-off that may not recur on the next run.

**Why it matters:**
Prevents a genuinely-caught bug from becoming an unreproducible, hard-to-debug flake.

**Common trap:**
Letting the random seed vary freely on every test run, losing reproducibility for any failure it finds.

**Related:**
[handbook/testing/mutation-and-property-based-testing.md](../syllabus/08-testing/mutation-and-property-based-testing.md)
