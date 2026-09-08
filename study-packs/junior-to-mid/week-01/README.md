---
title: "Junior → Mid, Week 1 — The True Floor of Java"
document_type: study-pack
week: 1
track: junior-to-mid
status: draft
estimated_hours: 8
last_reviewed: 2026-09-08
---

# Week 1 — The True Floor of Java

## Weekly Outcome

By the end of this week you can explain what a JVM/JDK/JRE actually are and name Java's eight primitive types with their real ranges; write a small, correct Java program using variables, control flow, and methods from scratch; and explain Java's four access levels, `static`, `final`, and the difference between an abstract and a concrete method — all before touching a single class or interface.

## Why This Week Matters

Every other week in this pack, and almost every other chapter in this repository, is written in Java and silently assumes you already know what a primitive type is, what `private` actually restricts, and what `static` means. This week is the true floor everything else stands on — [Java Platform Basics](../../../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md) (T-2209), [Java Syntax Fundamentals](../../../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) (T-2206), and [Java Modifiers and Method Signatures](../../../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md) (T-2210) exist specifically because this repository never taught any of this from zero before a 2026-09-08 audit found the gap.

## Prerequisites

None from this repository — but this pack assumes you have written *some* code before, in some language. If you have never programmed at all, see [Java Syntax Fundamentals](../../../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) §2 for the honest scope note on that boundary.

## Schedule

| Day | Focus |
|---|---|
| Mon | [Java Platform Basics: JVM, JDK, JRE, and Primitive Types](../../../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md) (T-2209) |
| Tue–Wed | [Java Syntax Fundamentals](../../../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) (T-2206) — read in full, reproduce `GradeReportDemo.java` yourself |
| Thu–Fri | [Java Modifiers and Method Signatures](../../../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md) (T-2210) — read in full, reproduce all 5 real compile errors and their fixes |
| Sat | Coding/Practice Exercises from all three chapters' own §16 |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Java Platform Basics (T-2209) | [`syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md`](../../../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md) |
| 2 | Java Syntax Fundamentals (T-2206) | [`syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md`](../../../syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md) |
| 3 | Java Modifiers and Method Signatures (T-2210) | [`syllabus/02-java/language-core/java-modifiers-and-method-signatures.md`](../../../syllabus/02-java/language-core/java-modifiers-and-method-signatures.md) |

## Hands-On Exercises

Every demo below is real, compiled, and executed — reproduce each one yourself rather than reading the output.

- [`practice/java/oop-fundamentals/platform-basics/`](../../../practice/java/oop-fundamentals/platform-basics/) — `PrimitiveTypesDemo.java`, 11/11 real assertions (byte/int overflow, the real `0.1 + 0.2` surprise, the Integer -128..127 cache `==` gotcha, a real `NullPointerException` from auto-unboxing `null`), plus real JDK `bin/`/`jmods/` tooling evidence.
- [`practice/java/oop-fundamentals/syntax-basics/`](../../../practice/java/oop-fundamentals/syntax-basics/) — `GradeReportDemo.java`, 9/9 real assertions (variables, `if`/`else`, `for`, `while`, `switch`, methods, arrays, all together on one small task).
- [`practice/java/oop-fundamentals/modifiers-and-methods/`](../../../practice/java/oop-fundamentals/modifiers-and-methods/) — `StaticFinalDemo.java` (6/6 real assertions: static-vs-instance state, a real abstract class with both an abstract and a concrete method) plus 5 real, captured `javac` compiler errors (reassigning a `final` variable, overriding a `final` method, extending a `final` class, instantiating an `abstract` class, and a real, genuine finding: `private` access is scoped to the top-level class, not the immediate class body).

## Interview Answer Drills

For each of the three chapters' own Interview Questions, answer the Junior-tier questions out loud, unprompted, before checking the chapter's own expected answer.

## Coding Problems

None dedicated this week — Week 1 is conceptual floor-building. Coding pattern practice starts Week 5, once OOP and collections usage are both solid.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week — this pack is technical-fundamentals-focused throughout; behavioral interview preparation lives in [`syllabus/20-interview-preparation/behavioral/`](../../../syllabus/20-interview-preparation/behavioral/) as its own, separate track once you have real work experience to draw stories from.

## Mock Interview

Self-check only this week: pick 3 Interview Questions from across the three chapters at random and answer them cold, out loud, timing yourself to under 90 seconds each.

## Review Checklist

- [ ] Completed all three chapters' own Mastery Checklists with every box honestly checked, not skimmed.
- [ ] Reproduced all 3 real demos above and confirmed the assertion counts and compile errors match what's stated here.
- [ ] Can explain, unprompted, the JVM/JDK/JRE distinction, why `7 / 2` is `3` in Java, and why `private` access is scoped to the top-level class rather than the immediate class body.

## Completion Criteria

- [ ] `PrimitiveTypesDemo.java`, `GradeReportDemo.java`, and `StaticFinalDemo.java` all compile and run with the exact assertion counts stated above, reproduced on your own machine.
- [ ] Reproduced all 5 real compile errors from T-2210's demo and can explain each one's root cause.
- [ ] Can write a small class with a constructor, at least one `private` field, and at least one `static final` constant from a blank file, without copying from the chapter.

## Retrospective

Note which of Section 8's Common Mistakes (in any of the three chapters) you personally made while doing the exercises, and why — this is the single most useful thing to review before Week 2 introduces OOP proper.

## Next Week

[Week 2 — Objects, Interfaces, and What Java Has Become](../week-02/README.md).
