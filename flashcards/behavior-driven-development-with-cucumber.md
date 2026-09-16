---
title: "Flashcards: Behavior-Driven Development with Cucumber"
slug: behavior-driven-development-with-cucumber
document_type: flashcard-deck
domain: 08-testing
topic_id: T-2412
canonical: ../syllabus/08-testing/behavior-driven-development-with-cucumber.md
last_updated: 2026-09-16
---

# Flashcards: Behavior-Driven Development with Cucumber

**Canonical chapter:** [`syllabus/08-testing/behavior-driven-development-with-cucumber.md`](../syllabus/08-testing/behavior-driven-development-with-cucumber.md)

## Card: TDD vs. BDD, the real distinction

**Prompt:**
What's the actual difference between TDD and BDD, beyond syntax?

**Answer:**
Both share the same red-green testing rhythm. TDD writes the check in code, for developers only. BDD writes it in structured natural language (Gherkin) readable by developers and non-developers together, then translates it to real code via step definitions.

**Why it matters:**
The single most commonly confused pair of terms in this space.

**Common trap:**
Treating BDD as a stricter or more thorough testing technique rather than an audience/vocabulary layer.

**Related:**
[Behavior-Driven Development with Cucumber](../syllabus/08-testing/behavior-driven-development-with-cucumber.md)

## Card: Scenario Outline produces real separate results

**Prompt:**
Does a `Scenario Outline`'s `Examples` table run as one test looping over data, or as genuinely separate tests?

**Answer:**
Genuinely separate, independently-reported test instances — this chapter's lab shows 3 real `Example` results (`#1.1`–`#1.3`) from one written scenario, each with its own pass/fail status.

**Why it matters:**
A bug affecting only one specific input combination is visible precisely, not hidden inside an aggregate loop result.

**Common trap:**
Describing it as "just a for-loop" internally.

**Related:**
[Behavior-Driven Development with Cucumber](../syllabus/08-testing/behavior-driven-development-with-cucumber.md)

## Card: Choosing test data deliberately

**Prompt:**
This chapter found a real, unplanned issue with one of its own `Examples` rows. What was it, and what does it teach?

**Answer:**
A 0%-discount row still passed even when the discount calculation had a real, deliberately-introduced bug — multiplying a broken percentage by zero produces zero regardless. Test data must be chosen to actually exercise the logic under test, not just superficially vary the input.

**Why it matters:**
Broad-looking `Examples` coverage can still miss real bugs if the chosen rows don't genuinely exercise different code paths.

**Common trap:**
Assuming more `Examples` rows automatically means better coverage.

**Related:**
[Behavior-Driven Development with Cucumber](../syllabus/08-testing/behavior-driven-development-with-cucumber.md)

## Card: Undefined steps are real failures

**Prompt:**
What happens when a Gherkin step has no matching step definition?

**Answer:**
A real, reported failure (`UndefinedStepException`), and Cucumber generates a real, ready-to-paste Java method snippet inferred from the step's own wording, marked with a `PendingException` convention — not a silent skip.

**Why it matters:**
Candidates often assume an unimplemented step is silently ignored.

**Common trap:**
Believing undefined steps pass or are skipped by default.

**Related:**
[Behavior-Driven Development with Cucumber](../syllabus/08-testing/behavior-driven-development-with-cucumber.md)
