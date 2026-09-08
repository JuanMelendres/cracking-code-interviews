---
title: "Junior → Mid, Week 2 — Objects, Interfaces, and What Java Has Become"
document_type: study-pack
week: 2
track: junior-to-mid
status: draft
estimated_hours: 8
last_reviewed: 2026-09-08
---

# Week 2 — Objects, Interfaces, and What Java Has Become

## Weekly Outcome

By the end of this week you can explain what a class and an object actually are with a concrete example, including interfaces' `default`/`static` methods and Java's real diamond-problem resolution rule; correctly attribute at least 4 major Java features (lambdas, records, sealed types, virtual threads) to the version that introduced them; and describe, at a basic level, what happens when `java` runs your compiled code.

## Why This Week Matters

Week 1 gave you the raw material (variables, methods, modifiers) — this week assembles it into real objects and interfaces, then places that knowledge on a timeline: [Java OOP Fundamentals](../../../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) (T-2201) and [Java Version Features Timeline](../../../syllabus/02-java/language-core/java-version-features-timeline.md) (T-2211) exist specifically because this repository never taught either from zero before a 2026-09-08 audit found the gap.

## Prerequisites

Week 1 — comfortable with variables, control flow, methods, access modifiers, `static`/`final`, and abstract vs. concrete method signatures.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Java OOP Fundamentals](../../../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) (T-2201) — read in full, reproduce all 5 demos |
| Wed | [Java Version Features Timeline: Java 8 Through 25](../../../syllabus/02-java/language-core/java-version-features-timeline.md) (T-2211) |
| Thu | [How a Computer Executes a Program](../../../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md) (T-2002) |
| Fri–Sat | Coding/Practice Exercises from all three chapters' own practice sections |
| Sun | Review checklist below, self-check against the Mastery Checklists |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Java OOP Fundamentals (T-2201) | [`syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md`](../../../syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) |
| 2 | Java Version Features Timeline (T-2211) | [`syllabus/02-java/language-core/java-version-features-timeline.md`](../../../syllabus/02-java/language-core/java-version-features-timeline.md) |
| 3 | How a Computer Executes a Program (T-2002) | [`syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md`](../../../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md) |

## Hands-On Exercises

Every demo below is real, compiled, and executed — reproduce each one yourself rather than reading the output.

- [`practice/java/oop-fundamentals/classes-and-objects/`](../../../practice/java/oop-fundamentals/classes-and-objects/) — 5 real demos, assertions passing throughout: encapsulation (a broken public-field class vs. a fixed one), interface vs. abstract class, composition-over-inheritance (a real subclass-explosion comparison), a real interface static-method call plus an inherited default method (2/2 assertions), and Java's real diamond-problem collision — a genuine `javac` error from two interfaces with conflicting `default` methods, then the real fix via `InterfaceName.super.method()` (1/1 assertion).
- [`practice/java/oop-fundamentals/java-version-features/`](../../../practice/java/oop-fundamentals/java-version-features/) — `VersionFeaturesDemo.java`, 9/9 real assertions spanning `var` (Java 10), text blocks (Java 15), records and pattern matching for `instanceof` (Java 16), sealed interfaces (Java 17), exhaustive pattern matching for `switch` (Java 21), and 1,000 real virtual threads completing concurrently (Java 21).
- [`practice/java/cs-foundations/program-execution/`](../../../practice/java/cs-foundations/program-execution/) — real call-stack-depth and bytecode-disassembly demos backing T-2002.

## Interview Answer Drills

For each of the three chapters' own Interview Questions, answer the Junior and Junior/Mid questions out loud, unprompted, before checking the chapter's own expected answer. Pay particular attention to T-2201's diamond-problem question and T-2211's "which version introduced what" question — both are extremely common Junior/Mid interview questions.

## Coding Problems

None dedicated this week — Week 2 is conceptual floor-building. Coding pattern practice starts Week 5, once OOP and collections usage are both solid.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week — see Week 1's note on where behavioral preparation lives in this repository.

## Mock Interview

Self-check only this week: pick 3 of the Interview Questions from T-2201 and T-2211 at random (not the ones you just reviewed) and answer them cold, out loud, timing yourself to under 90 seconds each.

## Review Checklist

- [ ] Completed all three chapters' own Mastery Checklists with every box honestly checked, not skimmed.
- [ ] Reproduced all 3 real demos above and confirmed the assertion counts match what's stated here.
- [ ] Can explain, unprompted, why a `Counter b = a;` assignment shares state between `a` and `b`, and can correctly attribute lambdas (Java 8), records (Java 16), sealed types (Java 17), and virtual threads (Java 21) to their real versions.

## Completion Criteria

- [ ] All 5 OOP demos and the version-features demo compile and run with the exact assertion counts stated above, reproduced on your own machine.
- [ ] Can write a small class with a constructor, at least one field, and at least one method from a blank file, without copying from the chapter.
- [ ] Can correctly answer T-2201's Section 17 exercise (reference-sharing) and reproduce the real diamond-problem collision and its fix, before checking the answer.

## Retrospective

Note which of Section 8's Common Mistakes (in either chapter) you personally made while doing the exercises, and why — this is the single most useful thing to review before Week 3 revisits collections at a deeper level.

## Next Week

[Week 3 — Numbers, Complexity, and Collections as a Concept](../week-03/README.md).
