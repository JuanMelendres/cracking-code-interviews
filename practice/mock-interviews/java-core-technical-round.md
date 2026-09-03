---
title: "Mock Interview: Java Core Technical Round (45 min)"
slug: java-core-technical-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - Parallel streams and fork/join coordination cost
  - equals()/hashCode() contract and hash-based collection correctness
  - Generics, type erasure, and unchecked-cast failure timing
  - Exception design and cause-chaining
  - Immutability and defensive copying
related:
  - ../../syllabus/02-java/language-core/streams-and-collectors.md
  - ../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md
  - ../../syllabus/02-java/language-core/generics-erasure-and-pecs.md
  - ../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md
  - ../../syllabus/02-java/language-core/immutability-and-defensive-copying.md
source: ../../study-packs/week-13/08-week-13-mock-interview.md
official_references: []
---

# Mock Interview: Java Core Technical Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below. Elevated from `study-packs/week-13/08-week-13-mock-interview.md`.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| Parallel streams and fork/join coordination cost | Q1 | [Streams and Collectors](../../syllabus/02-java/language-core/streams-and-collectors.md) |
| equals()/hashCode() contract | Q2 | [equals(), hashCode(), and Comparable Contracts](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md) |
| Generics and type erasure | Q3 | [Generics: Erasure, Variance, and PECS](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md) |
| Exception design and cause-chaining | Q4 | [Exception Design and Hierarchy Strategy](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md) |
| Immutability and defensive copying | Q5, Q6 | [Immutability and Defensive Copying](../../syllabus/02-java/language-core/immutability-and-defensive-copying.md) |
| Cross-topic synthesis | Q7 | All five, above |

## Interviewer Opening Script

*"This is a 45-minute Java Core technical round. I'll ask seven questions covering streams, collection contracts, generics, exceptions, and immutability — some are diagnostic ('here's a symptom, find the cause'), one is a whiteboard design, and the last is free-form synthesis across two topics of your choice. Think aloud; I want to hear your reasoning, not just your conclusion. Ready when you are."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(6 min)** Your `parallel()` change made a hot request path slower, not faster. Walk through why, and how you'd verify it properly.
2. **(6 min)** A `HashSet` isn't deduplicating records that look identical to you. What do you check first, and why?
3. **(6 min)** Why does a `ClassCastException` from an unchecked generic cast show up somewhere completely unrelated to the cast itself?
4. **(6 min)** An on-call alert shows a generic exception with no useful detail. What's the first thing you check in the code, and how do you prevent it happening again?
5. **(6 min)** Your class has only `final` fields and no setters. Is it immutable? Prove your answer, don't just assert it.
6. **(6 min, whiteboard)** Design a `Money` value class that is correctly comparable, correctly hashable, and immutable — sketch the class on a whiteboard or paper, narrating each design decision.
7. **(9 min)** Free-form: pick any two of this week's five topics and explain how they interact in a single real system (e.g., a `Comparable` value class stored in a `HashSet`, or an immutable object passed through a stream pipeline).

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — parallel() regression

**Ideal answer outline:** names fork/join coordination overhead as the likely cause for small/cheap-per-element work; proposes a properly warmed-up benchmark (not a single `nanoTime()` call) comparing sequential vs. parallel on realistic data before drawing conclusions.
**Common weak answers:** "parallel should always be faster," or no verification method proposed at all.
**Pass signal:** names the coordination-cost-vs-workload-size trade-off and proposes real measurement.
**Borderline signal:** identifies overhead exists but can't explain the measurement methodology.
**Fail signal:** insists parallel() must be faster, or blames an unrelated cause.

### Question 2 — HashSet not deduplicating

**Ideal answer outline:** checks whether `equals()` and `hashCode()` are both overridden and consistent, derived from the same fields; proposes an automated contract test.
**Common weak answers:** suspects the deduplication logic itself rather than the value class's contract.
**Pass signal:** correctly identifies the broken contract as the first check.
**Borderline signal:** vaguely mentions equals/hashCode without diagnosing the specific mismatch mechanism.
**Fail signal:** doesn't suspect the contract at all.

### Question 3 — ClassCastException far from its cause

**Ideal answer outline:** generics are compile-time only; an unchecked cast lets an incompatible value in with no immediate check, and the failure surfaces later, at the point the declared type is relied upon (e.g., `get()`).
**Common weak answers:** assumes some runtime generic checking exists that should have caught it earlier.
**Pass signal:** correctly explains the read-time failure mechanism with a concrete example.
**Borderline signal:** knows it's related to generics/casting but can't explain the timing.
**Fail signal:** no coherent explanation of erasure's role.

### Question 4 — Generic on-call exception

**Ideal answer outline:** checks whether the exception's construction site passed the caught exception as the cause; proposes a code-review or static-analysis rule requiring cause-chaining on every custom exception.
**Common weak answers:** assumes the failure was inherently hard to capture rather than that detail was captured and discarded.
**Pass signal:** correctly identifies the missing-cause pattern and proposes prevention.
**Borderline signal:** identifies the missing cause but no systematic prevention.
**Fail signal:** doesn't suspect the wrapping code at all.

### Question 5 — Is a final-fields-only class immutable?

**Ideal answer outline:** no, not necessarily — checks every constructor for a defensive copy of mutable arguments, and every getter for a defensive copy/immutable view rather than a live reference.
**Common weak answers:** treats final-fields-plus-no-setters as sufficient proof.
**Pass signal:** identifies both leak points (constructor and getter).
**Borderline signal:** identifies one leak point but not both.
**Fail signal:** asserts immutability without checking either boundary.

### Question 6 — Whiteboard: design a Money class

**Ideal answer outline:** derives `equals()`, `hashCode()`, and `compareTo()` from the identical fields (currency, cents); makes fields `final` and of immutable types; `compareTo()` throws on cross-currency comparison rather than silently comparing.
**Pass signal:** produces a class satisfying all three contracts consistently, narrating the "why" for each design choice.
**Borderline signal:** produces a mostly-correct class but misses one consistency point (e.g., compareTo not matching equals's fields).
**Fail signal:** doesn't connect the design to the contracts explicitly.

### Question 7 — Free-form cross-topic synthesis

**Pass signal:** picks a genuine interaction (e.g., "an immutable Money class is what makes it safe to use as a HashMap key across threads with zero synchronization") and explains the connection precisely, not just juxtaposing two topics.
**Fail signal:** describes two topics separately without a real connective insight.

## Scoring Rubric

Score each of the seven questions 1–5 (self-assessed, or by the evaluator):

| Score | Meaning |
|---|---|
| 1 | No coherent answer, or a factually wrong one |
| 2 | Names the right topic but no working mechanism |
| 3 | Correct mechanism, Senior-level bar met |
| 4 | Correct mechanism plus one Staff-level extension |
| 5 | Correct mechanism, Staff-level extension, and a real/plausible production connection |

**Pass threshold for this mock:** average score ≥ 3.5 across all seven questions, with no individual score below 2.

## Debrief Guide

Walk the candidate through their own scores question by question, starting with the lowest. For any score of 2 or below, ask the candidate to re-answer immediately after hearing the ideal answer outline — the goal is confirming they can absorb the correction on the spot, not just recording the gap. For Question 7, discuss whether the chosen pairing was the strongest available interaction among the five topics, or a superficial one; this itself is a Staff-level signal independent of the specific pairing chosen.

## Remediation Recommendations

- Any score ≤ 2 on Q1 or Q7 (parallel streams) → re-read [Streams and Collectors](../../syllabus/02-java/language-core/streams-and-collectors.md), focusing on the chapter's own measured parallel-stream evidence before re-attempting.
- Any score ≤ 2 on Q2 or Q6 (equals/hashCode/Comparable) → re-read [equals(), hashCode(), and Comparable Contracts](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md); redo the chapter's own contract-violation demo.
- Any score ≤ 2 on Q3 → re-read [Generics: Erasure, Variance, and PECS](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md)'s Internal Implementation section on erasure timing.
- Any score ≤ 2 on Q4 → re-read [Exception Design and Hierarchy Strategy](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md)'s cause-chaining material.
- Any score ≤ 2 on Q5 or Q6 (immutability) → re-read [Immutability and Defensive Copying](../../syllabus/02-java/language-core/immutability-and-defensive-copying.md), specifically the two-leak-points framing.
- Below the 3.5 pass threshold overall → retake this mock in full after remediation, not just the failed questions — Q7's synthesis quality depends on fluency across all five topics simultaneously.
