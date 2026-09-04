---
title: "Refactoring Discipline"
slug: refactoring-discipline
document_type: syllabus-topic
domain: 18-engineering-practices
topic_id: T-1804
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - working-with-legacy-code.md
related:
  - working-with-legacy-code.md
  - code-review-standards-and-practice.md
  - ../17-architecture/technical-debt-and-evolutionary-architecture.md
practice: ../../practice/java/engineering-practices/refactoring-discipline/
production_scenarios: []
interview_paths: [senior-to-staff]
official_references: []
source_history: []
---

# Refactoring Discipline

This is **T-1804** in `18-engineering-practices`, the last of this domain's four new-writing topics. [Working with Legacy Code](working-with-legacy-code.md) covers how to establish a safety net (characterization tests) before changing untested code; this chapter covers the discipline of the change itself — restructuring code without altering its observable behavior — whether or not the code already had tests to begin with.

## 1. Why This Matters

"Refactoring" is one of the most misused words in software engineering — used loosely to describe any restructuring, including ones that quietly change behavior. The actual, precise definition (Martin Fowler's) is a structural change that provably preserves external behavior — and the discipline of keeping that promise, verified rather than assumed, is what separates a genuine refactor from a behavior change mislabeled as one. Getting this wrong (calling a behavior change a refactor) is a common, real source of unreviewed regressions, since a change labeled "just a refactor" often receives less scrutiny than one labeled as a feature or fix.

## 2. Prerequisites

[Working with Legacy Code](working-with-legacy-code.md) — refactoring code with no existing tests requires establishing characterization tests first, per that chapter's own workflow, before this chapter's refactoring technique can be safely applied at all.

## 3. Foundation (L1)

**A refactoring is a structural change to code that does not alter its observable behavior** — the same inputs must produce the same outputs, before and after, for every case that mattered before. If behavior changes, even in a way that seems like an obvious improvement, it isn't a refactor; it's a behavior change, and should be labeled, reviewed, and tested as one.

**Refactoring requires an existing, passing test suite (or a characterization test suite, per [Working with Legacy Code](working-with-legacy-code.md)) covering the behavior being restructured** — without one, "the behavior didn't change" is an unverified assumption, not a verified fact.

## 4. Core Concepts (L2)

**Extract Method** — pulling a self-contained piece of logic out of a larger method into its own, separately named method — is the single most common refactoring, and the one this chapter's own practice demo (Section 7) applies three times in sequence to decompose one long method into three focused ones.

**Refactoring should proceed in small, individually-verifiable steps, not as one large restructuring done all at once** — each Extract Method (or other individual refactoring) should be followed by running the test suite before starting the next one, so that if a step does introduce an accidental behavior change, it's caught immediately and traced to exactly that one small step, not lost somewhere inside a much larger batch of changes.

**A refactor and a behavior change should never be committed together** — mixing "I restructured this" with "and I also fixed a bug while I was in there" makes it impossible for a reviewer (or a future `git bisect`) to distinguish the two, and defeats the entire verification value of keeping refactors behaviorally provable.

## 5. How It Works Internally (L3)

**The actual proof that a refactor preserved behavior is a test suite that passes identically before and after, unmodified** — not a reviewer's visual inspection of the diff, and not the author's confidence. This chapter's own practice demo makes this concrete and literal: `RefactoringParityTest.java` runs the exact same 10 real input cases through both the "before" (one long method) and "after" (three extracted methods) versions of a shipping-cost calculation, and asserts every single result matches exactly. This is a stronger, more direct proof than "I read the diff carefully and it looks equivalent" — it's a mechanically verified equivalence over a real, if necessarily finite, set of inputs.

**Why the test suite must remain unmodified across the refactor, precisely**: if a refactor requires changing the test suite itself to keep passing, that's direct evidence the observable behavior *did* change — the test was checking something about the old behavior that the new structure no longer satisfies. A genuine, pure refactor's test suite needs zero edits, because by definition nothing externally observable was supposed to change. This is the single cleanest, most mechanical way to tell whether a change that's labeled "just a refactor" actually is one.

**Extract Method's specific safety property**: pulling a block of code into a new method with the same inputs and the same return value, called from the exact place the original code lived, is a behavior-preserving transformation *by construction* — assuming no variable capture or side-effect-ordering mistake was introduced in the extraction itself, which is exactly what the "verify after every small step" discipline (Section 4) is designed to catch immediately, before a second extraction compounds any undetected mistake from the first.

## 6. Practical Usage

- **Never combine a refactor with a behavior change in the same commit or review** (Section 4) — even when both feel small, keeping them separate preserves the reviewer's and the codebase's ability to verify each independently.
- **Run the full relevant test suite after every individual refactoring step**, not only once at the end of a longer sequence — catches an accidental behavior change at the exact step that introduced it.
- **Treat "the test suite needed changes to pass" as a direct signal the change wasn't actually a pure refactor** (Section 5), and relabel/re-review it accordingly rather than proceeding as though it still is one.

## 7. Examples

Real, executed output from [`practice/java/engineering-practices/refactoring-discipline/`](../../practice/java/engineering-practices/refactoring-discipline/) (OpenJDK 21.0.12), proving a real three-step Extract Method refactor is behavior-preserving:

```
$ java -cp out RefactoringParityTest
  PASS  weight=0.5 region=domestic      express=false before=5.00 after=5.00
  PASS  weight=3.0 region=continental   express=true  before=38.50 after=38.50
  PASS  weight=20.0 region=international express=true  before=70.00 after=70.00
  PASS  weight=45.5 region=domestic      express=true  before=47.13 after=47.13
  ...
All 10 cases: before and after produce identical output.
```

`ShippingCostBefore.java` mixes weight-tier pricing, region multiplier, and express surcharge logic in one nested-conditional method. `ShippingCostAfter.java` reaches identical output via three successive Extract Method refactors (`weightTierBaseCost`, `regionMultiplier`, `expressSurcharge`) — no feature added, no bug fixed, purely a structure change, and `RefactoringParityTest.java` is the actual, mechanical proof of that claim, not an assertion of good faith.

## 8. Common Mistakes

- **Calling a change a "refactor" when it also changes behavior**, even subtly or with good intentions ("while I was in there, I also fixed...") — Section 4/6's core discipline this exact habit violates.
- **Refactoring in one large step and running tests only at the end**, rather than after each small step (Section 4) — makes it much harder to isolate which specific step introduced a regression, if one appears.
- **Treating a need to modify the test suite as a minor inconvenience to work around**, rather than as direct evidence the change wasn't a pure refactor (Section 5).
- **Skipping the "before" test run** — verifying the test suite passes on the *original*, unrefactored code first is what establishes the baseline the "after" run is actually being compared against.

## 9. Edge Cases

- **A refactor that appears to change output only in an edge case nobody had tested** — this is real evidence the original test suite (or characterization tests, if the code was untested) had a coverage gap, not evidence the refactor is "close enough" to correct — the gap should be closed before proceeding, not waved past.
- **A refactor requiring a genuinely different but equivalent algorithm** (e.g., replacing a manual loop with a stream operation that's mathematically equivalent but could differ in edge cases like empty input or overflow behavior) — needs explicit verification of exactly those edge cases, not just the common-case inputs.
- **Performance-motivated restructuring that happens to also change observable behavior** (e.g., changing floating-point operation order, which can produce a different rounding result) — this is not a pure refactor by this chapter's own definition (Section 3), even though it's often colloquially called one; it should be labeled and reviewed as a behavior change with a performance justification.

## 10. Performance Implications

This chapter's own real evidence (Section 7) demonstrates the actual performance argument for refactoring discipline directly: proving equivalence across 10 real test cases took one small, fast, automated test run — a genuinely cheap verification cost relative to the alternative (a reviewer manually tracing through both versions of the logic to convince themselves they're equivalent, a slower and much less reliable process). The broader, longer-term performance argument for refactoring itself — code that's easier to understand and modify accumulates lower ongoing maintenance cost — is the standard justification in the software engineering literature (Section 19), not a claim unique to this chapter.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Small, individually-verified refactoring steps | Any regression is caught and isolated immediately | More individual verification runs than one large batch |
| One large refactor, verified only at the end | Feels faster to execute | A regression, if introduced, is much harder to isolate to its specific cause |
| Separating refactors from behavior changes strictly | Both are independently reviewable and revertable | Sometimes requires two commits/PRs where one might feel more convenient |
| Refactoring without first confirming test coverage exists | Feels faster to start | No actual proof behavior was preserved — see [Working with Legacy Code](working-with-legacy-code.md) for the prerequisite this skips |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is recognizing, in the moment, when a change that started as a pure refactor is drifting into a behavior change — often because an "obvious improvement" presents itself mid-refactor — and consciously stopping to separate the two rather than letting the boundary blur. A Senior engineer reviewing someone else's "refactor" PR should specifically check whether the test suite needed any modifications to pass (Section 5) as a fast, reliable signal of whether the label is accurate.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, refactoring discipline at the level of individual methods and classes is the same underlying principle [Technical Debt and Evolutionary Architecture](../17-architecture/technical-debt-and-evolutionary-architecture.md) applies at the level of entire systems: change structure incrementally, verify behavior preservation at each step, and never conflate "restructuring" with "changing what the system does" without that conflation being an explicit, reviewed, deliberate decision. A Staff engineer establishing refactoring norms for a team — requiring separate commits/PRs for structural versus behavioral changes, requiring test-suite-unchanged as a review criterion for anything labeled "refactor" — is applying this exact chapter's discipline as an organizational policy, the same way [Code Review's](code-review-standards-and-practice.md) own Staff-level section frames establishing team-wide review norms.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a mislabeled-refactor-specific root cause, though the general pattern (a change labeled as low-risk receiving less scrutiny than its actual risk warranted) is a recognizable variant of several documented incidents in this repository under different specific framings.

> Planned reference: a future `production-cookbook/` entry covering a real incident where a change labeled "just a refactor" received reduced review scrutiny and shipped an undetected behavior change would be a natural, non-duplicative addition connecting this chapter's Section 4/12 discipline to a genuine production incident.

## 15. Interview Questions

### Question 1 — What's the precise definition of "refactoring," and why does that precision matter in practice?

**Why interviewers ask it.** It tests whether "refactoring" is understood as a precise technical term with a specific, verifiable claim, or used loosely to mean "any code cleanup," which is a common, real source of miscommunication on real teams.

**Expected answer.** A refactoring is a structural change to code's internal organization that does not alter its observable, external behavior — the same inputs produce the same outputs before and after. This precision matters because a change labeled "refactor" is often reviewed with less scrutiny than a feature or bug fix, on the reasonable assumption that behavior can't have changed — so mislabeling a behavior change as a refactor risks it receiving less review attention than it actually needs.

**Minimum acceptable answer.** States that refactoring means "changing structure, not behavior," even without articulating the practical review-scrutiny consequence.

**Strong Senior answer.** Names the review-scrutiny consequence explicitly and can describe a concrete, mechanical way to verify a change is actually a pure refactor (an unmodified, still-passing test suite, Section 5).

**Staff-level extension.** Connects this to establishing this distinction as an explicit team review norm (Section 13), not just a personal discipline — requiring "refactor" and "behavior change" to be separate commits/PRs as a standing convention.

**Common mistakes.** Using "refactor" to describe any restructuring regardless of whether behavior changed, diluting the term's usefulness as a specific signal to reviewers.

**Follow-up questions.** "How would you verify, mechanically, that a change you believe is a pure refactor actually is one?" (Section 5's core answer: the existing test suite must pass, completely unmodified, both before and after.)

### Question 2 — Why should the test suite remain completely unmodified across a refactor, rather than being updated to match the new code structure?

**Why interviewers ask it.** It's a precise test of Section 5's core mechanical-proof argument, checking whether a candidate understands *why* an unmodified test suite is the actual evidence of behavior preservation, not just a convention to follow.

**Expected answer.** Tests verify *observable behavior*, not internal structure — a pure refactor by definition doesn't change observable behavior, so a test suite that only checks observable behavior should need zero changes to keep passing. If the test suite needs edits to pass after a "refactor," that's direct, mechanical evidence the change altered something the tests were actually checking — meaning observable behavior did change, and the change wasn't a pure refactor after all, regardless of what it's labeled.

**Minimum acceptable answer.** States that tests "shouldn't need to change" for a refactor, even without articulating why that's actual proof rather than just a best practice.

**Strong Senior answer.** Explains the logical argument precisely: tests check behavior; unchanged tests passing means unchanged behavior; that's the actual definition of a refactor being satisfied, verified rather than assumed.

**Staff-level extension.** Connects this to a real, concrete example (this chapter's own `RefactoringParityTest.java`, Section 7) of exactly this proof being carried out mechanically, and generalizes it to a team review policy: any PR labeled "refactor" that includes test changes should be specifically questioned about why.

**Common mistakes.** Treating "the tests needed minor updates" as an acceptable, normal part of refactoring, rather than as a specific signal worth investigating before proceeding.

**Follow-up questions.** "What if the tests needed updating only because they were testing internal implementation details, not actual observable behavior?" (A real, legitimate exception — but it reveals the original tests were over-specified to internals rather than behavior, which is itself worth fixing separately, not just waved past.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/engineering-practices/refactoring-discipline/) yourself and confirm all 10 parity-test cases pass identically between the before and after versions.
- Perform a fourth Extract Method refactor on `ShippingCostAfter.java` (e.g., extracting the final `Math.round` rounding logic into its own named method), and re-run `RefactoringParityTest.java` unmodified to confirm your new refactor is also behavior-preserving.
- Deliberately introduce a subtle behavior change into a copy of `ShippingCostAfter.java` (e.g., changing one region's multiplier slightly) and confirm `RefactoringParityTest.java` correctly fails and identifies exactly which case diverged — direct, hands-on proof of the test suite's actual detection power.

## 17. Debugging Exercises

**Symptom:** a PR labeled "refactor: extract pricing logic into helper methods" includes a small change to an existing unit test's expected value, and the PR author explains this was needed "because the new structure calculates it slightly differently."

**Diagnose:** this is precisely Section 5/8's core warning made concrete — a test needing modification to pass after a "refactor" is direct, mechanical evidence the change is not a pure refactor, regardless of how it's labeled or how minor the author believes the difference to be. The correct response as a reviewer is to ask the author to separate this into two distinct changes: the genuine structural refactor (verified by the *original*, unmodified test passing), and a separate, explicitly labeled behavior change (with its own test update, its own justification, and its own, appropriately higher level of review scrutiny) — not to approve the combined change under the "refactor" label it was submitted with.

## 18. Design Exercises

**Design constraint:** design a team code-review checklist item specifically for pull requests labeled as refactors, to catch the mislabeled-behavior-change pattern (Section 8/17) before it reaches production with reduced scrutiny.

Design the checklist item around this chapter's own mechanical proof (Section 5) directly: "Does this PR modify any existing test's expected values or assertions? If yes, this is not a pure refactor — request the author split it into a structural change (verified by the original, unmodified tests) and a separately-reviewed behavior change." State explicitly why this specific check is more reliable than asking reviewers to manually verify behavioral equivalence by reading the diff (Section 10) — it's mechanical, fast to apply, and doesn't depend on a reviewer's ability to trace through potentially complex logic changes by inspection alone.

## 19. Further Reading

- Martin Fowler, *Refactoring: Improving the Design of Existing Code* — the foundational text defining refactoring precisely and cataloging the standard refactoring techniques (Extract Method and many others) this chapter's core discipline is built around.
- [Working with Legacy Code](working-with-legacy-code.md) — the prerequisite technique (characterization testing) for establishing the safety net this chapter's refactoring discipline requires when no test suite already exists.
- [Technical Debt and Evolutionary Architecture](../17-architecture/technical-debt-and-evolutionary-architecture.md) — the system-level analog of this chapter's method/class-level discipline, applying the same incremental-verified-change principle at architectural scale.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | State the precise definition of refactoring, and explain why a refactor requires an existing test suite | [Section 3](#3-foundation-l1) |
| L2 | Perform an Extract Method refactor in small, individually-verified steps, keeping it separate from any behavior change | [Interview Question 1](#question-1--whats-the-precise-definition-of-refactoring-and-why-does-that-precision-matter-in-practice) |
| L3 | Explain why an unmodified, still-passing test suite is mechanical proof of behavior preservation, not just a convention | [Section 7's real evidence](#7-examples), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real mislabeled-refactor PR from a test-modification signal (Section 17), and design a team review checklist item that catches this pattern mechanically (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
