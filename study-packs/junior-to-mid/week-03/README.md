---
title: "Junior → Mid, Week 3 — Collections, For Real This Time"
document_type: study-pack
week: 3
track: junior-to-mid
status: draft
estimated_hours: 7
---

# Week 3 — Collections, For Real This Time

## Weekly Outcome

By the end of this week you can explain why `HashMap` needs a correct `equals()`/`hashCode()` pair to work at all, describe what actually happens inside a `HashMap` on a collision, and choose correctly between `ArrayList` and `LinkedList` for a stated access pattern with a reason tied to their internals, not folklore.

## Why This Week Matters

Week 2 taught *when* to reach for a `Map` or a `List`. This week explains *why* they behave the way they do underneath — [equals(), hashCode(), and Comparable Contracts](../../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md) is the direct prerequisite for [HashMap Internals](../../../syllabus/02-java/collections/hashmap-internals.md) actually making sense, and both interview constantly at every level from Mid up.

## Prerequisites

Week 2 — you should already be comfortable using `List`, `Map`, and `Set` correctly before opening up how one of them works inside.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [equals(), hashCode(), and Comparable Contracts](../../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md) (T-101) |
| Wed–Thu | [HashMap Internals](../../../syllabus/02-java/collections/hashmap-internals.md) (T-201) |
| Fri | [ArrayList and LinkedList Internals](../../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md) (T-202) |
| Sat | Practice exercises from all three chapters' own practice sections |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | equals()/hashCode()/Comparable (T-101) | [`syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md`](../../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md) |
| 2 | HashMap Internals (T-201) | [`syllabus/02-java/collections/hashmap-internals.md`](../../../syllabus/02-java/collections/hashmap-internals.md) |
| 3 | ArrayList and LinkedList Internals (T-202) | [`syllabus/02-java/collections/arraylist-and-linkedlist-internals.md`](../../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md) |

## Hands-On Exercises

Each chapter above links its own real, compiled `practice/` demo — follow those links from the chapter itself rather than a copy here, since this pack does not duplicate chapter content. Reproduce each demo and confirm the behavior described (a broken `equals()` silently failing a `HashSet.contains()` check; a real bucket-collision walkthrough; a measured `ArrayList` vs. `LinkedList` insertion-cost comparison).

## Interview Answer Drills

Answer, out loud, before checking the chapters: "why does overriding `equals()` without `hashCode()` break `HashMap`?" and "why is `ArrayList.add(0, x)` slow but `LinkedList.addFirst(x)` isn't?"

## Coding Problems

None dedicated this week — Week 4 starts applying this internals knowledge to real coding patterns.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: explain what happens, step by step, when you call `map.put(key, value)` on a `HashMap` that already has a colliding key at that bucket — say it out loud as if drawing it on a whiteboard, then check against T-201's own Whiteboard Explanation section.

## Review Checklist

- [ ] Completed all three chapters' own Mastery Checklists.
- [ ] Reproduced each chapter's real demo and confirmed the described behavior on your own machine.
- [ ] Can state, from memory, the equals/hashCode contract's three rules (reflexive, and consistent with hashCode).

## Completion Criteria

- [ ] Can correctly implement `equals()` and `hashCode()` together for a small class from scratch.
- [ ] Can explain a HashMap collision and how it's resolved, unprompted.
- [ ] Can choose ArrayList vs. LinkedList correctly for 3 stated access patterns with a reason.

## Retrospective

Note any place you previously used a class in a `HashSet`/`HashMap` without a correct `equals()`/`hashCode()` override — this is one of the most common real bugs this week's material directly prevents.

## Next Week

[Week 4 — Coding Patterns, Part 1](../week-04/README.md).
