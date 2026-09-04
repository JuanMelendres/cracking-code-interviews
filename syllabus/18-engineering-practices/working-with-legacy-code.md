---
title: "Working with Legacy Code"
slug: working-with-legacy-code
document_type: syllabus-topic
domain: 18-engineering-practices
topic_id: T-1803
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - code-review-standards-and-practice.md
related:
  - refactoring-discipline.md
  - ../17-architecture/strangler-fig-and-migration-patterns.md
  - ../17-architecture/technical-debt-and-evolutionary-architecture.md
practice: ../../practice/java/engineering-practices/legacy-code/
production_scenarios: []
interview_paths: [senior-to-staff]
official_references: []
source_history: []
---

# Working with Legacy Code

This is **T-1803** in `18-engineering-practices`. [Strangler Fig and Migration Patterns](../17-architecture/strangler-fig-and-migration-patterns.md) and [Technical Debt and Evolutionary Architecture](../17-architecture/technical-debt-and-evolutionary-architecture.md) both cover *system-level* legacy migration strategy — replacing or evolving an entire legacy system over time. This chapter is one layer below that: the day-to-day technique for safely changing a single piece of legacy code — a class, a method, a module — that lacks tests and whose behavior isn't fully understood, without waiting for (or requiring) a larger system-level migration.

## 1. Why This Matters

Most real engineering work happens inside existing, imperfect code, not on a greenfield project — and "legacy code," in the sense this chapter uses the term (following Michael Feathers' well-known definition), simply means code without tests, regardless of its age or how well-written it otherwise is. Changing such code safely is a genuinely different, learnable skill from writing new code: the central risk isn't writing a bug into new logic, it's inadvertently changing behavior in code you don't fully understand, with no test suite to catch the regression.

## 2. Prerequisites

[Code Review: Standards and Practice](code-review-standards-and-practice.md) — reviewing a change to legacy code specifically needs to check not just the change's correctness but whether a characterization test (Section 4) was actually established first.

## 3. Foundation (L1)

**"Legacy code" is code without tests** — this is the specific, actionable definition this chapter uses (not "old code," not "code someone else wrote," not "code using an outdated framework"). Code without tests is legacy precisely because you cannot know, mechanically, whether a change preserved its existing behavior — you can only find out by inspection, which is slow and error-prone, or by observing production after the fact, which is far too late.

**A characterization test captures what code *actually does*, right now, as observed by running it — not what it *should* do, and not what a specification says it should do.** Writing one requires no understanding of the code's internals; it requires only running the code with real inputs and recording the real outputs it actually produces, including any surprising or seemingly-wrong behavior.

## 4. Core Concepts (L2)

**The characterization-testing workflow has two concrete steps, in strict order**: first, probe the code's actual behavior across a spread of realistic inputs, printing (not asserting) the results — this step deliberately makes no claims about correctness. Second, turn every observed result into an explicit assertion, pinning the current behavior down as a regression-detecting safety net. Only after this safety net exists is it safe to refactor or extend the code with any real confidence that an accidental behavior change will be caught.

**A "seam" (Michael Feathers' term) is a place in the code where behavior can be changed without editing the code in that exact spot** — a point where a dependency can be substituted (an interface, a parameter, a subclass override point). Finding seams is what makes it possible to isolate a piece of legacy code well enough to test it at all, without first performing a large, risky refactor just to make it testable.

**"Sprout method" and "sprout class"** are two of the simplest, lowest-risk techniques for adding new behavior to legacy code that lacks tests: rather than modifying an existing, untested method to add new logic (risking an accidental behavior change in code with no safety net), write the new logic as a new, separately-testable method or class, and call it from one minimal, easy-to-verify point in the existing code.

## 5. How It Works Internally (L3)

**Characterization testing's core discipline: probe before asserting, and never adjust an observed result to match an expectation.** This chapter's own practice demo makes this concrete: running `LegacyOrderPricer.price()` across a spread of quantities and unit prices (Section 7) surfaced a genuinely surprising, real result — at one specific unit price, ordering 9 units and ordering 10 units produce the *identical* total price, because the bulk discount at the 10-unit threshold exactly offsets the cost of the extra unit. This was not anticipated before running the code; it was discovered by running it. A characterization test then locks in exactly this observed behavior, labeled explicitly as *current, real behavior being pinned down* — not as a claim that the discount-cliff behavior is correct or intended. Whether it's a bug worth fixing is now a separate, deliberate decision a team can make with full visibility, rather than something an unrelated refactor might silently remove or alter without anyone noticing.

**Why probing before asserting matters, precisely**: if you write assertions based on what you *expect* the code to do, before actually running it, any expectation that's subtly wrong (an off-by-one, a misunderstood threshold) becomes a false assertion — one that either fails immediately (revealing your own misunderstanding, which is fine) or, worse, one you "fix" to match your expectation rather than the code's actual behavior, silently defeating the entire purpose of characterization testing. Probing first and asserting only on real, observed output removes this entire failure mode.

**A characterization test suite is deliberately not a specification test suite** — it makes no claim that the pinned-down behavior is desirable, only that it's real and current. This distinction matters directly for how such a test suite should be treated during a later, deliberate bug fix: when the discount-cliff behavior (or any other characterized quirk) is eventually addressed on purpose, the specific characterization assertion documenting it should be updated deliberately, with a clear note explaining the change — not silently left in place to fail forever, and not silently deleted without comment.

## 6. Practical Usage

- **Before changing any code with no existing tests, spend the first pass purely observing its actual behavior** (Section 4's "probe" step) rather than jumping straight to modifying it.
- **Look for a seam near the code you need to change** before performing any broader restructuring just to make the code testable — the smallest possible seam that lets you isolate and test the relevant behavior is usually sufficient.
- **Add new behavior via sprout method/class rather than editing existing untested logic directly**, whenever the new behavior can be cleanly separated — this confines risk to new, testable code rather than mixing it into code with no safety net.

## 7. Examples

Real, executed output from [`practice/java/engineering-practices/legacy-code/`](../../practice/java/engineering-practices/legacy-code/) (OpenJDK 21.0.12), demonstrating the two-step characterization workflow (Section 4) on a small, deliberately untested legacy class:

```
$ java -cp out Explore
price(qty=9, unitPrice=19.99) = 179.91
price(qty=10, unitPrice=19.99) = 179.91
price(qty=11, unitPrice=19.99) = 197.9

$ java -cp out CharacterizationTest
  PASS  no discount below threshold -> 179.91
  PASS  discount applies at threshold -> 179.91
  PASS  discount applies above threshold -> 197.9
  PASS  qty=9 and qty=10 produce the identical price at this unit price (real discount-cliff behavior) -> 179.91
All characterization assertions passed -- current behavior is now pinned.
```

The `Explore` step made no claims about correctness — it only printed what the code actually does. The `CharacterizationTest` step then pinned down every one of those real, observed values (including the surprising discount-cliff finding) as an explicit, regression-detecting assertion.

## 8. Common Mistakes

- **Writing assertions based on what you expect legacy code to do, without first running it and observing its real behavior** — Section 5's exact failure mode this chapter's two-step workflow is designed to prevent.
- **Performing a large refactor on untested legacy code before establishing any characterization tests** — removes the one safety net that would catch an accidental behavior change during the refactor.
- **Editing an existing, untested method directly to add new behavior**, rather than using sprout method/class (Section 4) to isolate the new logic somewhere testable.
- **Treating a passing characterization test suite as proof the code is correct** — it only proves the code's *current* behavior is unchanged from when the tests were written; correctness is a separate, deliberate question (Section 5).

## 9. Edge Cases

- **Legacy code with side effects that are hard to observe** (writing to a database, calling an external service) — characterization testing still applies, but the "probe" step needs a way to observe the actual side effect (a test double, a real but isolated environment), not just a return value.
- **Legacy code whose behavior is genuinely non-deterministic** (depends on wall-clock time, random values, or external state that changes between runs) — a characterization test needs to control or account for that non-determinism (injecting a fixed clock, seeding a random source) before a stable assertion is even possible.
- **A characterized behavior that's later intentionally changed** — the specific assertion documenting the old behavior should be updated deliberately with a clear note, not silently deleted or left to fail (Section 5).

## 10. Performance Implications

This chapter's own real evidence (Section 7) demonstrates the actual "performance" argument for characterization testing directly: the `Explore` step took a handful of `print` statements and one compile-and-run cycle to surface a genuinely surprising, real behavior (the discount cliff) that could easily have been silently altered by a well-intentioned refactor with no test suite to catch it. The cost of establishing this safety net (minutes) is small relative to the cost of a silent, undetected behavior regression reaching production (potentially a real, customer-facing pricing bug, in this specific example's domain).

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Characterization testing before any change | A real safety net against accidental behavior regression | Time spent writing tests for code whose "correctness" isn't yet verified, only its current behavior |
| Refactoring untested legacy code without characterization tests first | Faster to start | No safety net — an accidental behavior change may go completely undetected until production |
| Sprout method/class for new behavior | Confines risk to new, testable code | The existing untested method still isn't tested — this defers, doesn't eliminate, the underlying test debt |
| A large, upfront rewrite instead of incremental legacy work | A clean slate, no legacy constraints | High risk, long timeline, and defers all value delivery until the rewrite completes — the exact risk [Strangler Fig](../17-architecture/strangler-fig-and-migration-patterns.md) is designed to avoid at the system level |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is resisting the urge to "clean up" legacy code as a side effect of an unrelated task — mixing a characterization-testing-then-safe-change effort with an opportunistic broader refactor multiplies the risk of both, and makes it much harder to isolate what actually caused a regression if one appears. A Senior engineer scopes a legacy-code change to exactly what's needed, characterizes exactly the behavior that change touches, and defers unrelated cleanup to its own, separately-reviewed change (a direct application of [Refactoring Discipline's](refactoring-discipline.md) own small-change principle).

## 13. Staff/System-Level Considerations (L4)

At Staff scope, recognizing when a piece of legacy code's risk has grown large enough to warrant a system-level migration strategy — rather than continuing incremental, characterization-test-protected changes indefinitely — is a real, consequential judgment call, directly connecting this chapter's code-level technique to [Technical Debt and Evolutionary Architecture's](../17-architecture/technical-debt-and-evolutionary-architecture.md) system-level framing. A Staff engineer's role is often deciding *which* legacy code is worth the investment of establishing real test coverage incrementally (this chapter's technique) versus which legacy system has accumulated enough risk and complexity that a [Strangler Fig](../17-architecture/strangler-fig-and-migration-patterns.md)-style gradual replacement is the more appropriate response — a decision that should be made deliberately and documented (an ADR, per [Architecture Decision Records](architecture-decision-records-and-technical-writing.md), is exactly the right artifact for it), not defaulted into by inertia.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a legacy-code-characterization-specific root cause.

> Planned reference: a future `production-cookbook/` entry covering a real incident caused by a refactor of untested legacy code that silently changed behavior (e.g., a pricing or business-rule regression that shipped because no characterization test existed to catch it) would be a natural, non-duplicative addition connecting this chapter's Section 5/10 lesson to a genuine production incident.

## 15. Interview Questions

### Question 1 — You need to make a change to a method with no existing tests, in code you don't fully understand. What's your process?

**Why interviewers ask it.** It's a direct, practical test of whether a candidate has an actual, disciplined process for this extremely common real-world situation, versus either avoiding legacy code entirely or changing it with no safety net.

**Expected answer.** First, probe the method's actual behavior across a spread of realistic inputs, observing (not assuming) what it currently does — including any surprising edge cases. Second, turn those observations into characterization tests, pinning down the current behavior as a regression-detecting safety net. Only then make the intended change, running the characterization tests continuously to catch any accidental behavior change the new work introduces.

**Minimum acceptable answer.** Describes wanting to "add tests first," even without the specific two-step probe-then-assert discipline.

**Strong Senior answer.** Explicitly separates the probing step from the asserting step, and can explain why writing assertions based on assumed (rather than observed) behavior defeats the technique's purpose (Section 5).

**Staff-level extension.** Connects this to a broader judgment call (Section 13) — recognizing when incremental characterization-and-change is the right approach versus when the code's risk has grown enough to warrant a larger migration strategy instead.

**Common mistakes.** Jumping straight to refactoring or changing the code before establishing any characterization tests, trusting inspection alone to catch behavior changes.

**Follow-up questions.** "What if the method has side effects that are hard to observe directly, like writing to a database?" (Section 9 — the characterization approach still applies, but the "probe" step needs a way to observe the actual side effect, not just a return value.)

### Question 2 — What's the difference between a characterization test and a normal unit test that verifies correct behavior?

**Why interviewers ask it.** It's a precise check for whether the distinction between "pinning down current behavior" and "asserting desired behavior" (Section 5) is genuinely understood.

**Expected answer.** A normal unit test asserts what the code *should* do, based on a specification or requirement — if the test fails, the code is assumed wrong. A characterization test asserts what the code *actually does*, observed by running it, without any claim about whether that behavior is correct — if a characterization test fails after a change, it means the change altered existing behavior, which may or may not be intentional, but is always worth an explicit look before proceeding.

**Minimum acceptable answer.** States that one checks "what it does" and the other checks "what it should do," even without elaborating on the practical consequence of that distinction.

**Strong Senior answer.** Gives a concrete example (like this chapter's own discount-cliff finding, Section 7) of a characterization test capturing a genuinely surprising behavior without endorsing it as correct.

**Staff-level extension.** Connects this to the deliberate-update discipline (Section 5) — when a characterized behavior is intentionally changed later, the specific assertion should be updated with a clear note, treating the characterization suite as a living record of intentional decisions about legacy behavior, not a frozen artifact.

**Common mistakes.** Treating a characterization test suite, once established, as equivalent to a specification-verifying test suite — conflating "this behavior is pinned down" with "this behavior is correct."

**Follow-up questions.** "If a characterization test starts failing after a change, how do you decide whether that's a problem?" (Determine whether the behavior change was intentional and desired, or an accidental side effect of the change — the test's failure itself doesn't answer that question, it only surfaces it for a deliberate decision.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/engineering-practices/legacy-code/) yourself, in order: `Explore` first, then `CharacterizationTest` — confirm you observe the same discount-cliff finding before seeing it asserted.
- Find (or write) a small, genuinely untested method in a personal or practice project, and apply the full two-step workflow to it: probe its real behavior first, then write characterization tests pinning down what you actually observed.
- Modify `LegacyOrderPricer.price()` to intentionally fix the discount-cliff behavior (e.g., applying the discount only to the portion of the order above the threshold), then update the specific characterization assertion documenting the old behavior with a clear note explaining the intentional change — practicing Section 5's deliberate-update discipline directly.

## 17. Debugging Exercises

**Symptom:** a seemingly small, unrelated refactor of an untested legacy pricing method caused a real production pricing discrepancy, discovered only after customers were charged incorrectly for several hours.

**Diagnose:** this is the exact, real-world cost Section 10 names in the abstract — the refactor had no characterization test suite to catch the behavior change it introduced, so the regression went undetected until observed in production, the most expensive place to find it. The retrospective fix isn't just correcting the immediate pricing bug — it's establishing characterization tests for the pricing method's *entire* current behavior (not just the specific case that broke) before any further changes are made to it, following exactly this chapter's two-step workflow, so a future refactor of the same code has the safety net this one lacked.

## 18. Design Exercises

**Design constraint:** your team has inherited a large, untested legacy billing module that must be modified regularly to support new business requirements, and each modification currently carries real risk of a silent regression reaching production.

Design an incremental de-risking strategy using this chapter's techniques directly: before any new feature work touches the module, establish characterization tests for the specific code paths that feature work will touch (not an attempt to characterize the entire module upfront, which would be prohibitively slow) — a targeted, "test what you're about to touch" discipline rather than an all-or-nothing test-everything mandate. Use sprout method/class (Section 4) for genuinely new behavior wherever it can be cleanly separated from the existing untested code. State explicitly, per Section 13's judgment call, the threshold at which this incremental approach should be reconsidered in favor of a larger, deliberate migration strategy (e.g., if the module's change frequency and risk profile suggest the incremental cost is exceeding what a more structural investment would cost) — and that this decision itself deserves an ADR (per [Architecture Decision Records](architecture-decision-records-and-technical-writing.md)) documenting why one approach was chosen over the other.

## 19. Further Reading

- Michael Feathers, *Working Effectively with Legacy Code* — the foundational text this chapter's core techniques (the "legacy code" definition, characterization testing, seams, sprout method/class) are drawn from.
- [Strangler Fig and Migration Patterns](../17-architecture/strangler-fig-and-migration-patterns.md) — the system-level migration strategy this chapter's code-level technique complements, for when legacy risk has grown beyond what incremental characterization-and-change can reasonably address.
- [Technical Debt and Evolutionary Architecture](../17-architecture/technical-debt-and-evolutionary-architecture.md) — the broader framing for deciding when and how much to invest in addressing legacy risk at a system level.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what "legacy code" means in this specific sense, and what a characterization test captures | [Section 3](#3-foundation-l1) |
| L2 | Apply the two-step probe-then-assert characterization workflow to a new, unfamiliar piece of untested code | [Interview Question 2](#question-2--whats-the-difference-between-a-characterization-test-and-a-normal-unit-test-that-verifies-correct-behavior) |
| L3 | Explain why probing before asserting matters, and the distinction between characterization tests and specification tests | [Section 7's real evidence](#7-examples), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real production regression as a missing-characterization-test failure (Section 17), and design a targeted, incremental de-risking strategy for a large legacy module (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
