---
title: "Cheat Sheet: Refactoring Discipline"
slug: refactoring-discipline
document_type: cheat-sheet
domain: 18-engineering-practices
topic_id: T-1804
canonical: ../syllabus/18-engineering-practices/refactoring-discipline.md
last_updated: 2026-09-06
---

# Refactoring Discipline

**Canonical chapter:** [`syllabus/18-engineering-practices/refactoring-discipline.md`](../syllabus/18-engineering-practices/refactoring-discipline.md)

## Core Mental Model

The actual proof that a refactor preserved behavior is a test suite that passes identically before and after, unmodified — not a reviewer's visual inspection of the diff, and not the author's confidence.

## Essential Definitions

- **Refactoring** (Martin Fowler's precise definition) — a structural change to code that does not alter its observable behavior; the same inputs must produce the same outputs before and after. If behavior changes, even for an obvious improvement, it isn't a refactor.
- **Extract Method** — pulling a self-contained piece of logic out of a larger method into its own, separately named method; the single most common refactoring.
- **Small, individually-verifiable steps** — each refactoring step should be followed by running the test suite before starting the next, so a regression is caught and traced immediately, not lost inside a larger batch.
- **Refactor/behavior-change separation** — a refactor and a behavior change should never be committed together; mixing them defeats a reviewer's (or `git bisect`'s) ability to distinguish the two.
- **Unmodified test suite as proof** — if a refactor requires changing the test suite to keep passing, that's direct evidence observable behavior *did* change, regardless of the "refactor" label.

## Decision Table

| Situation | Response |
|---|---|
| No existing test suite covers the code being restructured | Establish characterization tests first (see Working with Legacy Code); refactoring without them is an unverified assumption |
| A refactoring step is about to be followed by another | Run the full relevant test suite after each step, not only once at the end |
| An "obvious improvement" idea appears mid-refactor | Stop; separate it into its own behavior-change commit, don't blend it in |
| A PR labeled "refactor" also modifies a test's expected value | Treat as direct evidence it isn't a pure refactor; split into structural change + separately-reviewed behavior change |
| Output differs only in an untested edge case | Treat as a real test-coverage gap to close, not as "close enough" |
| Replacing a loop with a mathematically-equivalent but edge-case-differing construct (e.g., stream ops changing float rounding) | Explicitly verify those edge cases; if output changes, it's a behavior change with a performance justification, not a pure refactor |

## Common Pitfalls

- Calling a change a "refactor" when it also changes behavior, even subtly or with good intentions ("while I was in there, I also fixed...").
- Refactoring in one large step and running tests only at the end, making it hard to isolate which step introduced a regression.
- Treating a need to modify the test suite as a minor inconvenience to work around, rather than direct evidence the change wasn't a pure refactor.
- Skipping the "before" test run — verifying the test suite passes on the original, unrefactored code is what establishes the baseline the "after" run is compared against.

## Interview Answer Skeleton

**30-sec:** A refactoring is a structural change that provably preserves external behavior — same inputs, same outputs, before and after. The mechanical proof is a test suite that passes identically, completely unmodified, both before and after; if tests need edits to pass, the change wasn't a pure refactor.

**2-min:** Refactoring requires an existing (or characterization) test suite covering the behavior being restructured — without one, "behavior didn't change" is an assumption, not a verified fact. Extract Method is the most common refactoring: pulling a self-contained block into its own named method. This repo's own practice demo proves the discipline concretely — `RefactoringParityTest.java` runs the same 10 real input cases through both a "before" (one long method) and "after" (three extracted methods) version of a shipping-cost calculation, and every result matches exactly. The reason the test suite must stay unmodified is precise: tests verify observable behavior, and a pure refactor by definition doesn't change observable behavior, so a test suite checking only behavior should need zero edits to keep passing. If it needs edits, that's mechanical evidence behavior changed, regardless of the label. Refactors should proceed in small steps, each verified before the next, and a refactor should never be committed together with a behavior change.

**Staff-level framing:** Refactoring discipline at the method/class level is the same underlying principle technical-debt management applies at the system level: change structure incrementally, verify behavior preservation at each step, and never conflate "restructuring" with "changing what the system does" without that being an explicit, reviewed decision. A Staff engineer establishing refactoring norms for a team — requiring separate commits/PRs for structural versus behavioral changes, requiring an unchanged test suite as a review criterion for anything labeled "refactor" — is applying this discipline as organizational policy.

## Production Warning Signs

- A PR labeled "refactor: extract pricing logic into helper methods" includes a small change to an existing unit test's expected value, justified as "the new structure calculates it slightly differently." This is direct, mechanical evidence the change is not a pure refactor regardless of its label; the correct reviewer response is to require splitting it into the genuine structural refactor (verified by the original, unmodified test) and a separate, explicitly labeled behavior change with its own justification and higher review scrutiny.

## Related

- syllabus/18-engineering-practices/working-with-legacy-code.md
- syllabus/18-engineering-practices/code-review-standards-and-practice.md
- syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md
