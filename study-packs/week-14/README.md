---
title: "Week 14 — Collections"
document_type: study-pack
week: 14
status: draft
estimated_hours: 12
---

# Week 14 — Collections

## Weekly Outcome

By the end of this week you can explain, defend, and reproduce with real code the five highest-frequency Collections topics: HashMap internals (resize, treeification, hash-distribution failure), ConcurrentHashMap internals (per-call vs. multi-call atomicity), the BlockingQueue family (backpressure and direct handoff), ArrayList/LinkedList internals (growth factor, indexed access vs. head/tail insertion), and a synthesized collection-selection decision framework.

## Why This Week Matters

HashMap internals (T-201) is explicitly the single most-asked Java data structure question and carries the highest IWI in the entire Collections domain despite being Foundation-tier — Phase 1 confirmed this topic was previously entirely absent from this program's coverage, the same gap this week closes for four related topics.

## Prerequisites

Week 13 (Java Core), specifically the equals/hashCode/Comparable contracts chapter — HashMap's bucket mechanics only make sense in light of that contract.

## Schedule

See `10-week-14-checklist.md` for the day-by-day breakdown.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-hashmap-internals.md` | T-201 — summary + link; full chapter canonical at `handbook/collections/hashmap-internals.md` |
| 3 | `02-concurrenthashmap-internals.md` | T-205 — summary + link; full chapter canonical at `handbook/collections/concurrenthashmap-internals.md` |
| 4 | `03-blockingqueue-family.md` | T-207 — summary + link; full chapter canonical at `handbook/collections/blockingqueue-family.md` |
| 5 | `04-arraylist-and-linkedlist-internals.md` | T-202 — summary + link; full chapter canonical at `handbook/collections/arraylist-and-linkedlist-internals.md` |
| 6 | `05-collection-selection-decision-matrix.md` | T-209 — summary + link; full chapter canonical at `handbook/collections/collection-selection-decision-matrix.md` |
| 7 | `06-java-coding-practice.md` | 3 problems exercising this week's topics together, all compiled and run |
| 8 | `07-flashcards.md` | 15 cards |
| 9 | `08-week-14-mock-interview.md` | 45-min Collections technical round |
| 10 | `09-code-review-exercise.md` | Spot 5 defects across this week's 5 topics in one class |
| 11 | `10-week-14-checklist.md` | Day-by-day checklist |
| 12 | `resources.md` | Sources classified PRIMARY/BOOK |

## Hands-On Exercises

Reproduce every demo in `practice/java/week-14/` — note that the HashMap and ArrayList growth-factor demos require `--add-opens java.base/java.util=ALL-UNNAMED` for reflective access to private JDK fields, stated explicitly.

## Interview Answer Drills

Deliver the 30-second and 2-minute answers for each topic aloud, unprompted, from each canonical chapter's Interview Answer Framework section.

## Coding Problems

See `06-java-coding-practice.md` — 3 problems, 6/6 assertions passing.

## System Design Exercise

None this week — see `09-code-review-exercise.md` for this week's applied deliverable instead, matching Week 13's format.

## Behavioral Exercise

None formally scheduled this week; continue any in-progress STAR story work from earlier weeks.

## Mock Interview

`08-week-14-mock-interview.md` — 45-minute Collections technical round, candidate/evaluator sections hard-separated.

## Review Checklist

See `10-week-14-checklist.md`.

## Completion Criteria

- [ ] All five canonical chapters read in full
- [ ] All demos in `practice/java/week-14/` reproduced with matching output
- [ ] Coding practice: 6/6 assertions passing on your own run
- [ ] Code review exercise: all 5 defects found independently before checking the solution
- [ ] Mock interview average score ≥ 3.5

## Retrospective

Note which of the five topics needs a second pass, and whether the code review exercise revealed a gap not caught by the individual chapter demos.

## Next Week

Week 15 — Cloud & Infrastructure (Kubernetes resource limits and JVM sizing, Kubernetes objects and scheduling, cloud cost economics, CI/CD pipeline design, and AWS core services for backend engineers).
