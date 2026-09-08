---
title: "Junior → Mid, Week 3 — Numbers, Complexity, and Collections as a Concept"
document_type: study-pack
week: 3
track: junior-to-mid
status: draft
estimated_hours: 7
---

# Week 3 — Numbers, Complexity, and Collections as a Concept

## Weekly Outcome

By the end of this week you can explain why floating-point and integer arithmetic have real limits, describe an algorithm's cost using Big-O without hand-waving, and correctly choose between `List`, `Map`, and `Set` for a described access pattern.

## Why This Week Matters

These three topics are the shared vocabulary the rest of this pack depends on: Week 4's `HashMap`/`ArrayList` internals assume you already know what a `Map`/`List` is *for* (this week's [Collections Usage Fundamentals](../../../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md)), and Week 5's coding patterns assume you can already reason about complexity ([Algorithmic Complexity and Big-O](../../../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md)) without that being introduced mid-problem.

## Prerequisites

Weeks 1–2 — this week assumes you can read a class and a `for` loop without effort.

## Schedule

| Day | Focus |
|---|---|
| Mon | [Number Representation](../../../syllabus/01-computer-science-foundations/number-representation.md) (T-2003) |
| Tue–Wed | [Algorithmic Complexity and Big-O, From First Principles](../../../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) (T-2001) |
| Thu–Fri | [Collections Usage Fundamentals](../../../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md) (T-2207) — read in full, reproduce `WordFrequencyDemo.java` |
| Sat | Coding/Practice Exercises from all three chapters' own §16 |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Number Representation (T-2003) | [`syllabus/01-computer-science-foundations/number-representation.md`](../../../syllabus/01-computer-science-foundations/number-representation.md) |
| 2 | Algorithmic Complexity and Big-O (T-2001) | [`syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md`](../../../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) |
| 3 | Collections Usage Fundamentals (T-2207) | [`syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md`](../../../syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md) |

## Hands-On Exercises

- [`practice/java/cs-foundations/number-representation/`](../../../practice/java/cs-foundations/number-representation/) — real demos backing T-2003 (overflow, floating-point precision).
- [`practice/java/cs-foundations/algorithmic-complexity/`](../../../practice/java/cs-foundations/algorithmic-complexity/) — real demos backing T-2001.
- [`practice/java/oop-fundamentals/collections-basics/`](../../../practice/java/oop-fundamentals/collections-basics/) — `WordFrequencyDemo.java`, 14/14 real assertions (List preserving duplicates/order, Set deduplicating, Map counting, real removal semantics for each).

## Interview Answer Drills

For T-2207 specifically: answer "when would you use a Map instead of a List" and "why is checking membership with a HashSet faster than a List" out loud before checking the chapter's own §15 expected answers.

## Coding Problems

None dedicated this week — same reasoning as Weeks 1–2. Coding pattern practice starts Week 5.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described access pattern (e.g., "you need to look up a user's data by their ID, quickly, thousands of times a second"), state which collection you'd use and why, timed under 60 seconds, for 3 different scenarios you invent yourself.

## Review Checklist

- [ ] Completed T-2207's Mastery Checklist (§20).
- [ ] Reproduced `WordFrequencyDemo.java` and confirmed the 14/14 assertion count.
- [ ] Can state, from memory, the real complexity of `HashSet.contains()` vs. scanning a `List` for the same check.

## Completion Criteria

- [ ] Can explain why `0.1 + 0.2` doesn't exactly equal `0.3` in floating-point arithmetic, at a basic level.
- [ ] Can classify a described algorithm's complexity as O(1), O(n), or O(n²) correctly for 3 simple examples.
- [ ] `WordFrequencyDemo.java` reproduced with the exact 14/14 assertion count confirmed on your own machine.

## Retrospective

Note any collection-choice mistake you made in this week's exercises (reaching for a `List` where a `Set` was right, or vice versa) — this is exactly the pattern Week 4's `HashMap`/`ArrayList` internals chapters build on.

## Next Week

[Week 4 — Collections, For Real This Time](../week-04/README.md).
