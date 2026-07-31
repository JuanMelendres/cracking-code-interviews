---
title: "Week 13 — Java Core"
document_type: study-pack
week: 13
status: draft
estimated_hours: 12
---

# Week 13 — Java Core

## Weekly Outcome

By the end of this week you can explain, defend, and reproduce with real code five of the highest-frequency Java Core topics missing from the previous twelve weeks: streams and collectors (including the `parallel()` and `toMap()` traps), the equals/hashCode/Comparable contracts, generics erasure and PECS, exception design and hierarchy strategy, and immutability with defensive copying.

## Why This Week Matters

Java Core, Collections, and Cloud & Infrastructure were the three domains with zero prior coverage in this program (Weeks 1–12 covered architecture, databases, Spring, Kafka, concurrency, JVM, system design, testing, and performance, but never the language fundamentals interviewers assume are already solid). This week closes the five highest-weighted-IWI gaps in Java Core specifically — topics candidates are expected to have cold, not to reason through live for the first time in an interview.

## Prerequisites

None formally required, though `handbook/architecture/clean-hexagonal-architecture.md` and `handbook/concurrency/java-memory-model-and-volatile.md` are referenced for cross-topic connections (immutability's thread-safety implications, generic collections used in ports/adapters).

## Schedule

See `10-week-13-checklist.md` for the day-by-day breakdown.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-streams-and-collectors.md` | T-107 — summary + link; full chapter canonical at `handbook/java-core/streams-and-collectors.md` |
| 3 | `02-equals-hashcode-and-comparable-contracts.md` | T-101 — summary + link; full chapter canonical at `handbook/java-core/equals-hashcode-and-comparable-contracts.md` |
| 4 | `03-generics-erasure-and-pecs.md` | T-104 — summary + link; full chapter canonical at `handbook/java-core/generics-erasure-and-pecs.md` |
| 5 | `04-exception-design-and-hierarchy-strategy.md` | T-105 — summary + link; full chapter canonical at `handbook/java-core/exception-design-and-hierarchy-strategy.md` |
| 6 | `05-immutability-and-defensive-copying.md` | T-103 — summary + link; full chapter canonical at `handbook/java-core/immutability-and-defensive-copying.md` |
| 7 | `06-java-coding-practice.md` | 4 problems exercising this week's topics together, all compiled and run |
| 8 | `07-flashcards.md` | 15 cards |
| 9 | `08-week-13-mock-interview.md` | 45-min Java Core technical round |
| 10 | `09-code-review-exercise.md` | Spot 7 defects across this week's 5 topics in one class |
| 11 | `10-week-13-checklist.md` | Day-by-day checklist |
| 12 | `resources.md` | Sources classified PRIMARY/BOOK |

## Hands-On Exercises

Reproduce every demo in `practice/java/week-13/` — each canonical chapter's measured traces are real, executed output, not description.

## Interview Answer Drills

Deliver the 30-second and 2-minute answers for each topic aloud, unprompted, from each canonical chapter's Interview Answer Framework section.

## Coding Problems

See `06-java-coding-practice.md` — 4 problems, 12/12 assertions passing.

## System Design Exercise

None this week — Java Core topics are language-fundamentals, not system-design-scale. See `09-code-review-exercise.md` for this week's applied deliverable instead.

## Behavioral Exercise

None formally scheduled this week; continue any in-progress STAR story work from earlier weeks.

## Mock Interview

`08-week-13-mock-interview.md` — 45-minute Java Core technical round, candidate/evaluator sections hard-separated.

## Review Checklist

See `10-week-13-checklist.md`.

## Completion Criteria

- [ ] All five canonical chapters read in full
- [ ] All demos in `practice/java/week-13/` reproduced with matching output
- [ ] Coding practice: 12/12 assertions passing on your own run
- [ ] Code review exercise: all 7 defects found independently before checking the solution
- [ ] Mock interview average score ≥ 3.5

## Retrospective

Note which of the five topics needs a second pass, and whether the code review exercise revealed a gap not caught by the individual chapter demos.

## Next Week

Week 14 — Collections (HashMap internals, ConcurrentHashMap internals, and the collection-selection decision matrix).
