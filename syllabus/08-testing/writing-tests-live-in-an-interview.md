---
title: "Writing Tests Live in an Interview"
slug: writing-tests-live-in-an-interview
document_type: handbook-chapter
domain: 08-testing
status: draft
version: 1.0
last_updated: 2026-09-04
source_history:
  - handbook/testing/writing-tests-live-in-an-interview.md
topic_id: T-1108
mastery_levels_covered:
  - L1
  - L2
  - L3
  - L4
difficulty:
  - intermediate
target_levels:
  - senior
  - staff
prerequisites:
  - test-strategy-and-test-doubles.md
related:
  - test-strategy-and-test-doubles.md
  - junit5-architecture-and-advanced-features.md
  - ../../study-packs/week-18/02-writing-tests-live-in-an-interview.md
official_references:
  - https://junit.org/junit5/docs/current/user-guide/
---

# Writing Tests Live in an Interview

> **Topic register:** T-1108 (Writing tests live in an interview, IWI 5.8) · Core tier · Moderate interview frequency [M]

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Level 1 — Foundation](#level-1--foundation)
4. [Level 2 — Working Knowledge](#level-2--working-knowledge)
5. [Mental Model](#mental-model)
6. [Definition and Purpose](#definition-and-purpose)
7. [Core Concepts](#core-concepts)
8. [Internal Implementation](#internal-implementation)
9. [Production Scenarios](#production-scenarios)
10. [Failure Modes and Debugging](#failure-modes-and-debugging)
11. [Trade-offs](#trade-offs)
12. [Decision Framework](#decision-framework)
13. [Common Mistakes](#common-mistakes)
14. [Anti-Patterns](#anti-patterns)
15. [Best Practices](#best-practices)
16. [Interview Answer Framework](#interview-answer-framework)
17. [Interview Questions](#interview-questions)
18. [Summary](#summary)
19. [Key Takeaways](#key-takeaways)
20. [Cheat Sheet](#cheat-sheet)
21. [Flashcards](#flashcards)
22. [Practice Exercises](#practice-exercises)
23. [Solutions](#solutions)
24. [Additional Reading](#additional-reading)
25. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can run a genuine red-green-refactor cycle live, under interview time pressure, narrating each step in a way an evaluator can follow, and cite a real, executed three-step TDD walkthrough (a run-length-encoding kata) with actual captured pass/fail output at each stage as a concrete template for your own live-coding sessions.

## Why This Matters in Interviews

"Write a test for this" or "implement this test-first" is one of the most common live-coding interview formats at Senior/Staff level, precisely because it tests something a take-home assignment cannot: whether a candidate's testing discipline is a genuine habit or something they only apply when there's time to be careful. Under interview time pressure, candidates who don't practice this specific skill tend to either skip tests entirely once they feel behind schedule, or write a single, weak test at the end as an afterthought — both read as a testing habit that evaporates under pressure, which is exactly the signal a Senior/Staff interviewer is trying to surface, since production incidents also happen under time pressure.

## Level 1 — Foundation

Think of assembling furniture with an instruction booklet that has you check each step before moving to the next ("does the shelf sit flush before you screw it in?") rather than assembling the whole thing silently and only checking at the very end whether it stands up. Test-first (writing a test before the code that makes it pass) works the same way: write one tiny check ("an empty input should return an empty result"), watch it fail because you haven't built anything yet, write just enough code to satisfy that one check, watch it pass, then add the next check. This "red, green" rhythm (test fails, then test passes) repeats in small steps instead of one big leap.

In a live interview specifically, the reason this matters isn't really about testing best practice — it's about giving the interviewer something to actually watch. If you silently write a full solution and add a test at the end, the interviewer only sees the destination, not how you think. Narrating each small step ("I'll start with the empty-input case since it's the simplest thing that could exist") is what lets them evaluate your reasoning, not just your typing speed.

## Level 2 — Working Knowledge

At this level you should be able to run this rhythm smoothly under time pressure without it feeling artificial: pick the smallest, simplest case first (not the hardest one), say out loud why you picked it, write a deliberately minimal implementation (even something as bare as `return "";` to pass just the first test), and say explicitly that it's intentionally incomplete rather than letting it look like a rushed or careless answer.

You should also practice the specific skill of reading a test failure message before reacting to it. A common mistake under pressure is assuming, the instant a test fails, that either the test or the code must be "obviously" wrong and rewriting one of them on a guess. The stronger habit is reading the actual assertion message first ("expected empty string but got null") and using it to decide, calmly, which side is actually at fault.

Practically, if you're ever running low on time mid-exercise, the working move is to say so explicitly — "I'm going to skip the refactor step and move straight to the next test case, given the time" — rather than silently rushing through remaining steps or silently dropping the test-first discipline altogether. That one sentence of narration is itself part of what's being evaluated.

## Mental Model

Live-coding test-first is a narrated loop, not a silent one: **write one small failing test, run it and show it fails for the *expected* reason, write the minimum code to pass it, run it and show it passes, then decide whether to add another test case or refactor** — repeated in small, visible steps rather than attempted as one large batch of code followed by one large batch of tests at the end. The narration matters as much as the code: an evaluator watching this loop wants to hear the *reasoning* behind each test case chosen ("empty input is the smallest edge case, so I'll start there"), not just watch code appear.

## Definition and Purpose

**Test-driven development (TDD)**, in its classic red-green-refactor form, is a development cycle where a failing test is written before the implementation that makes it pass, followed by a minimal implementation, followed by an optional refactoring step performed while the test still passes. In a live-coding interview specifically, this cycle serves a dual purpose beyond its normal engineering value: it structures the candidate's problem-solving into small, verifiable, narratable steps, and it gives the interviewer concrete, observable checkpoints (does the test fail for the right reason; is the implementation step actually minimal; does refactoring preserve passing tests) to evaluate rather than only judging a single, large finished solution.

## Core Concepts

### The order of test cases in a live session should move from smallest to most complex, narrated explicitly

Starting with the empty/trivial case, then a single-element case, then a case exercising the core logic, mirrors how the implementation itself should be built incrementally — and narrating *why* each case was chosen ("I'll do empty string first since it's the smallest possible input and should be trivial to satisfy") demonstrates deliberate test design, not just a list of examples pulled from memory.

### A red step must fail for the *expected* reason, and this should be said out loud

Running a new test and seeing it fail is necessary but not sufficient — the failure needs to be the *expected* one (a wrong return value, not a compile error or an unrelated exception), and stating this explicitly ("good, it fails because it returned null instead of an empty string, as expected") demonstrates the candidate is actually reading the failure output, not just running a command and moving on.

### The "minimal implementation" step is deliberately not the final solution, and saying so avoids reading as sloppy

Writing `return "";` to pass an empty-string test, before any real logic exists, can look like an incomplete or careless answer if not explicitly framed — saying "I'll return the simplest thing that passes this specific test, and let the next test case force me to add real logic" turns an otherwise confusing intermediate step into a clear demonstration of deliberate incremental design.

## Internal Implementation

**Real red-green-refactor cycle** (`practice/java/week-18/live-coding-tdd/src/`), a run-length-encoding kata (`"aaabbc"` → `"a3b2c1"`), captured at each real step:

**Step 1 — RED, first test, trivial (deliberately wrong) implementation:**
```
=== STEP 1: RED (empty string test, wrong impl) ===
[         1 tests found           ]
[         0 tests successful      ]
[         1 tests failed          ]
    => org.opentest4j.AssertionFailedError: expected: <> but was: <null>
```

**Step 2 — GREEN, minimal implementation (`return "";`):**
```
=== STEP 2: GREEN ===
[         1 tests found           ]
[         1 tests successful      ]
[         0 tests failed          ]
```

**Step 3 — RED again, a new test case added, old implementation now insufficient:**
```
=== STEP 3: RED (new test, old impl) ===
    => org.opentest4j.AssertionFailedError: expected: <a1> but was: <>
RESULT: 2 tests found, 1 succeeded, 1 failed
```

**Step 4 — GREEN, full implementation driven out by all three accumulated tests:**
```
=== STEP 4: GREEN (full impl, all 3 tests) ===
[         3 tests found           ]
[         3 tests successful      ]
[         0 tests failed          ]
```

Each transition is real, captured console output, not a narrated description — this is exactly the visible checkpoint rhythm (fail for the right reason, pass minimally, fail again on a new case, pass fully) an interviewer watching a live session is following, and exactly the rhythm a candidate should reproduce and narrate aloud, one small step at a time, rather than writing the full implementation first and tests afterward.

## Production Scenarios

**A candidate in a live-coding round writes the full implementation first, then adds a few tests at the end "to be thorough."** This is a common and understandable instinct under time pressure, but it inverts the exact skill the interview format is testing — the interviewer specifically wants to see tests *driving* design decisions (what's the smallest case, what does a genuinely useful test even check), not tests *confirming* a design that was already fully decided. Even when the end result works, this ordering reads as "knows how to write tests" rather than "has a genuine test-first habit," which is a real and noticeable difference to an evaluator watching the process, not just the final code.

**A candidate's test fails during a live session, and they immediately assume their test itself is wrong rather than investigating.** This is a realistic, high-pressure moment — and how a candidate responds to an unexpected failure (calmly reading the actual failure message, confirming whether it's the implementation or the test that's wrong, rather than panicking and rewriting both) is itself a meaningful signal, closely mirroring how the same candidate would likely behave debugging a real, unexpected test failure in a production codebase under a deadline.

## Failure Modes and Debugging

- **Symptom: a candidate writes a large batch of code silently, then runs tests once at the end.** This defeats the interview format's purpose regardless of whether the final code is correct — the value of this specific exercise is in the narrated, incremental process, not just the destination; candidates should default to narrating each small step even when it feels slower.
- **Symptom: a test fails, and the candidate can't say why without staring at the stack trace for a long time.** This suggests the test wasn't actually understood before being written, or the failure output isn't being read carefully — practicing reading JUnit's actual assertion-failure messages (as shown in this chapter's real captured output) builds the fluency needed to diagnose a failure quickly under pressure.
- **Anti-pattern to rule out first when a candidate seems to be running out of time mid-kata:** check whether they're attempting to write the complete, fully-general implementation on the first pass rather than the minimal implementation for the current test only — over-engineering the "minimal" step is a common, avoidable source of running out of time in this specific format.

## Trade-offs

Narrating every step slows down the visible pace of writing code, which can feel uncomfortable under a ticking interview clock, but produces a session an evaluator can actually follow and assess — a fast, silent session that arrives at a correct answer provides the evaluator far less signal about the candidate's actual process and judgment than a slower, narrated one, even when both produce the same final code.

## Decision Framework

Default to narrating the reasoning behind each test case chosen and each implementation step taken, even when it feels like it's costing visible time — the interview format specifically exists to observe process, and a silent-but-fast session under-delivers on exactly what's being evaluated. When genuinely running low on time mid-kata, say so explicitly and propose a plan (e.g., "I'll skip the refactor step and go straight to the next test case, given the time") rather than silently rushing or silently abandoning the test-first discipline — this itself demonstrates the kind of trade-off communication a Staff engineer needs in real production time-pressure situations.

## Common Mistakes

- Writing the full implementation first and adding tests afterward, inverting the exact skill the format tests for.
- Treating the "minimal implementation" step as if it should already be complete or elegant, causing hesitation or over-engineering under time pressure.
- Going silent while writing code, providing the evaluator no window into the reasoning behind test-case choices or implementation decisions.
- Panicking at an unexpected test failure rather than calmly reading the actual assertion message to determine whether the test or the implementation is at fault.

## Anti-Patterns

Treating every single implementation detail as needing its own dedicated test case regardless of whether it adds new information — a live-coding session has limited time, and padding it with redundant tests that don't drive any new implementation behavior wastes time that would be better spent on the next genuinely new edge case, or on a deliberate refactor step.

## Best Practices

Choose test cases in a deliberate, narrated smallest-to-most-complex order (empty/trivial, single-element, then a case exercising the core logic) rather than an arbitrary or memorized list, and say why each case was chosen before writing it. Read every test failure's actual message aloud before deciding what to change, confirming it fails for the expected reason — this single habit, visible and narrated, is one of the strongest signals of genuine TDD fluency an evaluator can observe in a short session.

## Interview Answer Framework

### 30-Second Answer

Live-coding test-first means running a narrated red-green-refactor loop: one small failing test at a time, confirming it fails for the expected reason, writing the minimal code to pass it, then deciding whether to add another case or refactor — never writing a large batch of implementation first and testing it afterward, which inverts the exact discipline the format is designed to observe.

### 2-Minute Answer

Definition: test-driven development in its classic red-green-refactor form, applied live and narrated for an evaluator. Why the live format specifically matters: it tests whether a candidate's testing discipline survives time pressure, since production incidents also happen under time pressure, and a candidate who only writes careful tests when there's no rush reveals something real about their actual habits. How it works: choose the smallest test case first, narrate why, confirm the red failure is the expected one (not a compile error or wrong exception), write the minimal passing implementation and say so explicitly, then repeat with the next case. One trade-off: narrating every step slows the visible pace under a ticking clock, but a fast silent session gives the evaluator far less signal than a slower narrated one. One worked example: a real, executed three-step TDD cycle building a run-length-encoding kata, each step's actual captured pass/fail console output shown in sequence — empty string (red, then green), single character (red again on the old implementation, then green), then repeated characters driving out the full implementation (green across all three accumulated tests).

### 10-Minute Deep Dive

Cover: why the live-coding test-first format specifically exists as an interview signal (testing discipline under pressure, not just competence); the narrated red-green-refactor loop structure and why each step (smallest-case-first ordering, confirming the expected failure reason, minimal implementation framed explicitly) matters as an observable checkpoint for an evaluator; the real captured four-step evidence from the run-length-encoding kata, walking through each transition; the common failure mode of writing implementation first and testing afterward, and why it inverts the skill being tested even when the final code is correct; how a candidate should handle running low on time mid-kata (explicit communication and a proposed plan, not silent rushing or silent abandonment); the parallel to real production debugging discipline (calmly reading an actual failure message rather than panicking) as the deeper transferable skill this format is really screening for.

### Whiteboard Explanation

Draw a small circular loop with four labeled arrows: "write one small failing test" → "run it, confirm RED for the expected reason" → "write minimal code" → "run it, confirm GREEN" → back to the start (or a branch to "refactor" before looping). Annotate the loop's center: "narrate every arrow out loud — the loop itself is what's being evaluated, not just where it ends."

### Production Example

A Staff-level candidate is asked to implement a rate limiter, test-first, in 30 minutes. Rather than starting with the full sliding-window algorithm, they start with the smallest meaningful test ("a single request under the limit should be allowed"), narrate why, watch it fail correctly against a stub implementation, and build up incrementally — pausing partway through to say, explicitly, "I'm going to skip a dedicated test for the exact boundary condition given the remaining time, and note it as something I'd add next" — demonstrating the same kind of explicit trade-off communication under time pressure that a Staff engineer needs when scoping real work against a real deadline, which is precisely the transferable signal this interview format is designed to surface.

### Trade-offs to Mention

Narrating every step costs visible pace under time pressure but is what makes the session evaluable at all; explicitly communicating a scope-reduction decision when running low on time (rather than silently rushing or silently dropping the discipline) mirrors real production trade-off communication and is itself a positive signal, not an admission of failure.

### Common Candidate Mistakes

Writing implementation first and tests afterward; going silent while coding; treating an unexpected test failure as cause for panic rather than calm investigation.

### Typical Follow-Up Questions

"What would you do if you were running low on time halfway through this kata?" → communicate it explicitly and propose a concrete scope reduction (skip a refactor step, or state which edge case will be skipped and why) rather than silently rushing or silently dropping the test-first discipline. "How do you decide when a test case is redundant and not worth adding?" → a new test case should drive new implementation behavior; if it would pass against the current implementation without requiring any change, it isn't adding new information and time is likely better spent on a genuinely new edge case.

### Senior-Level Expectations

Runs a genuine, narrated red-green-refactor loop with deliberately-ordered test cases, and correctly reads and states the reason for each test failure.

### Staff-Level Discussion

Explicitly communicates scope trade-offs when time-constrained, mirroring real production trade-off communication under a deadline, rather than silently rushing or silently abandoning the test-first discipline. Recognizes the interview format's actual purpose (observing process and habits under pressure, not just judging the final code) and structures the session to make that process maximally visible to the evaluator.

## Interview Questions

### Question 1

**Implement, test-first, a function that returns the second-largest distinct value in an array of integers. Narrate each step.**

**Expected answer:** starts with the smallest meaningful case (an array with exactly two distinct values), confirms a red failure for the expected reason against a stub, writes a minimal implementation, then adds cases for duplicates (values that shouldn't count twice) and an array too small to have a second-largest value (narrating the edge-case decision explicitly — e.g., throw an exception or return an Optional, stated as a deliberate design choice, not an afterthought).

**Common mistakes:** writing the full implementation first and retrofitting tests; forgetting to handle or explicitly discuss the too-small-array edge case.

**Follow-up questions:** "How would you decide whether 'array too small' should throw an exception or return an empty Optional?" (a real API-design trade-off — narrating this decision explicitly, rather than picking one silently, is itself a positive signal.)

**Senior-level expectations:** runs a genuine, narrated red-green-refactor loop with sensible test-case ordering.

**Staff-level expectations:** explicitly frames the too-small-array behavior as a deliberate API-design decision with a stated rationale, not an implementation detail chosen arbitrarily.

### Question 2

**Midway through a live TDD kata, your test fails in a way you don't immediately understand. Walk through what you'd do.**

**Expected answer:** read the actual assertion failure message carefully before changing anything, to determine whether the test's expectation is wrong or the implementation is wrong — narrate this investigation process aloud rather than immediately rewriting either the test or the implementation on a guess.

**Common mistakes:** immediately assuming the test itself must be wrong, or immediately rewriting the implementation without first confirming what the actual failure message says.

**Follow-up questions:** "How does this differ from how you'd handle the identical situation in real production debugging?" (it shouldn't differ meaningfully — the calm, evidence-first investigation habit is exactly what should transfer, which is part of why this interview format is used at all.)

**Senior-level expectations:** describes a calm, evidence-first investigation process rather than a guess-and-rewrite reaction.

**Staff-level expectations:** explicitly draws the parallel to real production debugging discipline, recognizing the interview format's actual purpose.

## Summary

Live-coding test-first is a narrated red-green-refactor loop, not a silent one — the process itself, not just the final code, is what a Senior/Staff live-coding round is designed to evaluate. Choosing test cases in a deliberate smallest-to-most-complex order, confirming each red failure is for the expected reason, and framing each minimal implementation step explicitly are the concrete, observable habits that distinguish a genuine TDD practice from one that only appears when there's no time pressure. A real, four-step captured cycle (a run-length-encoding kata) demonstrates exactly this rhythm: red, green, red again on a new case, green across all accumulated tests.

## Key Takeaways

- The live-coding test-first format specifically tests whether a testing discipline survives time pressure, not just competence at writing tests.
- Narrate every step — test-case choice, failure-reason confirmation, and "minimal implementation" framing — since the process, not just the final code, is what's being evaluated.
- Choose test cases in a deliberate smallest-to-most-complex order and say why each was chosen.
- A red failure must be confirmed as the *expected* one before moving on, not just observed and dismissed.
- When running low on time, communicate a scope-reduction plan explicitly rather than silently rushing or silently dropping the discipline — this mirrors real production trade-off communication.

## Cheat Sheet

| Step | What to say out loud |
|---|---|
| Choosing a test case | "I'll start with X because it's the smallest/simplest meaningful case." |
| Red | "This fails because Y, which is expected — I haven't implemented Z yet." |
| Minimal implementation | "I'll write the simplest thing that passes this specific test; the next case will force more logic." |
| Green | "All tests pass; I'll add the next case or consider a refactor." |
| Running low on time | "I'm going to skip X given the time and note it as a follow-up." |

## Flashcards

**Q: Why does the live-coding test-first format specifically test something a take-home assignment can't?**
A: It reveals whether a candidate's testing discipline survives real time pressure, since production incidents also happen under time pressure — a take-home assignment has no equivalent time-pressure signal.

**Q: What must be confirmed before moving on from a "red" step?**
A: That the test failed for the *expected* reason (a wrong value, not a compile error or unrelated exception) — not just that it failed.

**Q: What should a candidate do when running low on time mid-kata, rather than silently rushing?**
A: Communicate a concrete scope-reduction plan explicitly — this mirrors real production trade-off communication and is itself a positive signal.

## Practice Exercises

1. Reproduce the `Rle` kata's four-step cycle yourself, from scratch, narrating each step aloud (record yourself) before checking your narration against this chapter's captured evidence.
2. Pick a small, unfamiliar kata (e.g., "compress a list of intervals," "validate balanced parentheses with three bracket types") and run a genuine, narrated red-green-refactor cycle against a 20-minute timer, practicing the explicit time-management communication this chapter describes if you run short.

## Solutions

1. Your narration should name each test case's purpose before writing it, explicitly state the expected reason for each red failure, and explicitly frame each minimal-implementation step as deliberately incomplete rather than a final answer.
2. A well-run session under time pressure should show at least one explicit trade-off statement (a skipped edge case, a skipped refactor) rather than either silently running out of time mid-implementation or silently cutting corners without saying so.

## Additional Reading

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

## Official References

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
