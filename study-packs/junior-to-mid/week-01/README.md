---
title: "Junior → Mid, Week 1 — Java, From the Ground Up"
document_type: study-pack
week: 1
track: junior-to-mid
status: draft
estimated_hours: 7
---

# Week 1 — Java, From the Ground Up

## Weekly Outcome

By the end of this week you can write a small, correct Java program using variables, control flow, and methods from scratch; explain what a class and an object actually are with a concrete example; and describe, at a basic level, what happens when `java` runs your compiled code.

## Why This Week Matters

Every other week in this pack, and almost every other chapter in this repository, is written in Java and assumes you can already read an `if` statement, a `for` loop, and a class definition. This week is the floor everything else stands on — [Java Syntax Fundamentals](../../../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) (T-2206) and [Java OOP Fundamentals](../../../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) (T-2201) exist specifically because this repository never taught them before, and every downstream chapter silently assumed they were already known.

## Prerequisites

None from this repository — but this pack assumes you have written *some* code before, in some language. If you have never programmed at all, see [Java Syntax Fundamentals](../../../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) §2 for the honest scope note on that boundary.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Java Syntax Fundamentals](../../../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) (T-2206) — read in full, reproduce `GradeReportDemo.java` yourself |
| Wed–Thu | [Java OOP Fundamentals](../../../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) (T-2201) — read in full, reproduce all 3 demos |
| Fri | [How a Computer Executes a Program](../../../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md) (T-2002) |
| Sat | Coding/Practice Exercises from both Java chapters (their own §16) |
| Sun | Review checklist below, self-check against the Mastery Checklists |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Java Syntax Fundamentals (T-2206) | [`syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md`](../../../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) |
| 2 | Java OOP Fundamentals (T-2201) | [`syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md`](../../../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) |
| 3 | How a Computer Executes a Program (T-2002) | [`syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md`](../../../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md) |

## Hands-On Exercises

Every demo below is real, compiled, and executed — reproduce each one yourself rather than reading the output.

- [`practice/java/oop-fundamentals/syntax-basics/`](../../../practice/java/oop-fundamentals/syntax-basics/) — `GradeReportDemo.java`, 9/9 real assertions (variables, `if`/`else`, `for`, `while`, `switch`, methods, arrays, all together on one small task).
- [`practice/java/oop-fundamentals/classes-and-objects/`](../../../practice/java/oop-fundamentals/classes-and-objects/) — 3 demos, 19/19 real assertions total: encapsulation (a broken public-field class vs. a fixed one), interface vs. abstract class (used on the same problem), composition-over-inheritance (a real subclass-explosion comparison).
- [`practice/java/cs-foundations/program-execution/`](../../../practice/java/cs-foundations/program-execution/) — real call-stack-depth and bytecode-disassembly demos backing T-2002.

## Interview Answer Drills

For each of the two Java chapters' own Interview Questions (§15), answer the Junior and Junior/Mid questions out loud, unprompted, before checking the chapter's own expected answer.

## Coding Problems

None dedicated this week — Week 1 is conceptual floor-building. Coding pattern practice starts Week 4, once OOP and collections usage are both solid.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week — this pack is technical-fundamentals-focused throughout; behavioral interview preparation lives in [`syllabus/20-interview-preparation/behavioral/`](../../../syllabus/20-interview-preparation/behavioral/) as its own, separate track once you have real work experience to draw stories from.

## Mock Interview

Self-check only this week: pick 3 of the Interview Questions from T-2206 and T-2201 at random (not the ones you just reviewed) and answer them cold, out loud, timing yourself to under 90 seconds each.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists (T-2206 §20, T-2201 §20) with every box honestly checked, not skimmed.
- [ ] Reproduced all 4 real demos above and confirmed the assertion counts match what's stated here.
- [ ] Can explain, unprompted, why `7 / 2` is `3` in Java and why a `Counter b = a;` assignment shares state between `a` and `b`.

## Completion Criteria

- [ ] `GradeReportDemo.java` and all 3 OOP demos compile and run with the exact assertion counts stated above, reproduced on your own machine.
- [ ] Can write a small class with a constructor, at least one field, and at least one method from a blank file, without copying from the chapter.
- [ ] Can correctly answer T-2206's Section 17 debugging exercise (the `<=` vs. `<` loop bound) and T-2201's Section 17 exercise (reference-sharing) before checking the answer.

## Retrospective

Note which of Section 8's Common Mistakes (in either chapter) you personally made while doing the exercises, and why — this is the single most useful thing to review before Week 3 revisits collections at a deeper level.

## Next Week

[Week 2 — Numbers, Complexity, and Collections as a Concept](../week-02/README.md).
