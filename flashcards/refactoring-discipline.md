---
title: "Flashcards: Refactoring Discipline"
slug: refactoring-discipline
document_type: flashcard-deck
domain: 18-engineering-practices
topic_id: T-1804
canonical: ../syllabus/18-engineering-practices/refactoring-discipline.md
last_updated: 2026-09-07
---

# Flashcards: Refactoring Discipline

**Canonical chapter:** [`syllabus/18-engineering-practices/refactoring-discipline.md`](../syllabus/18-engineering-practices/refactoring-discipline.md)

## Card: The precise definition of refactoring

**Prompt:**
By Martin Fowler's precise definition, what is a "refactoring"?

**Answer:**
A structural change to code that does not alter its observable behavior — the same inputs must produce the same outputs, before and after, for every case that mattered before. If behavior changes at all, even in a way that seems like an obvious improvement, it isn't a refactor.

**Why it matters:**
A change labeled "refactor" is often reviewed with less scrutiny than a feature or bug fix — so mislabeling a behavior change as a refactor risks it receiving less review attention than it actually needs.

**Common trap:**
Using "refactor" loosely to mean "any code cleanup," diluting its usefulness as a specific signal to reviewers.

**Related:**
[syllabus/18-engineering-practices/refactoring-discipline.md](../syllabus/18-engineering-practices/refactoring-discipline.md)

## Card: An unmodified test suite is the mechanical proof

**Prompt:**
What's the actual proof that a refactor preserved behavior — a reviewer's careful reading of the diff, or something else?

**Answer:**
A test suite that passes identically before and after, completely unmodified. If the test suite needs edits to keep passing, that's direct, mechanical evidence the change altered something the tests were actually checking — meaning observable behavior did change.

**Why it matters:**
Tests verify observable behavior, not internal structure; a pure refactor by definition doesn't touch observable behavior, so its test suite should need zero changes to keep passing.

**Common trap:**
Treating "the tests needed minor updates" as a normal, acceptable part of refactoring rather than a signal worth investigating before proceeding.

**Related:**
[syllabus/18-engineering-practices/refactoring-discipline.md](../syllabus/18-engineering-practices/refactoring-discipline.md)

## Card: Never commit a refactor and a behavior change together

**Prompt:**
Why should "I restructured this" and "I also fixed a bug while I was in there" never land in the same commit?

**Answer:**
Mixing the two makes it impossible for a reviewer — or a future `git bisect` — to distinguish them, defeating the entire verification value of keeping refactors behaviorally provable.

**Why it matters:**
Separating them lets both changes be independently reviewed, verified, and reverted.

**Common trap:**
Bundling an "obvious improvement" noticed mid-refactor into the same change instead of splitting it out.

**Related:**
[syllabus/18-engineering-practices/refactoring-discipline.md](../syllabus/18-engineering-practices/refactoring-discipline.md)

## Card: Extract Method is behavior-preserving by construction

**Prompt:**
Why is Extract Method considered safe "by construction," and what can still go wrong with it?

**Answer:**
Pulling a block of code into a new method with the same inputs and same return value, called from the exact place the original code lived, preserves behavior by construction — assuming no variable-capture or side-effect-ordering mistake was introduced during the extraction itself.

**Why it matters:**
The "verify after every small step" discipline exists specifically to catch that kind of mistake immediately, before a second extraction compounds an undetected error from the first.

**Common trap:**
Performing several Extract Method steps in a row without re-running tests after each one, making it hard to isolate which step introduced a regression.

**Related:**
[syllabus/18-engineering-practices/refactoring-discipline.md](../syllabus/18-engineering-practices/refactoring-discipline.md)

## Card: Real parity-test evidence for a three-step refactor

**Prompt:**
What did `RefactoringParityTest.java` actually prove about `ShippingCostBefore.java` vs. `ShippingCostAfter.java`?

**Answer:**
Running the exact same 10 real input cases through both the "before" (one long nested-conditional method) and "after" (three extracted methods: `weightTierBaseCost`, `regionMultiplier`, `expressSurcharge`) versions produced identical results in every case — a mechanically verified equivalence, not just a diff that "looks equivalent."

**Why it matters:**
This is a stronger, more direct proof of behavior preservation than a reviewer's visual inspection of the diff.

**Common trap:**
Believing that a refactor "looks obviously equivalent" is sufficient without an actual before/after parity test run.

**Related:**
[syllabus/18-engineering-practices/refactoring-discipline.md](../syllabus/18-engineering-practices/refactoring-discipline.md)

## Card: Small, individually-verified refactoring steps

**Prompt:**
Should a multi-step refactor (e.g., three successive Extract Method calls) be verified only once at the end, or after each step?

**Answer:**
After each individual step — running the test suite before starting the next one — so that if a step does introduce an accidental behavior change, it's caught immediately and traced to exactly that one step, not lost inside a larger batch of changes.

**Why it matters:**
Verifying only at the end makes it much harder to isolate which specific step in a larger refactor introduced a regression.

**Common trap:**
Performing an entire multi-step restructuring in one pass and running tests only once, at the very end.

**Related:**
[syllabus/18-engineering-practices/refactoring-discipline.md](../syllabus/18-engineering-practices/refactoring-discipline.md)
