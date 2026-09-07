---
title: "Cheat Sheet: Working with Legacy Code"
slug: working-with-legacy-code
document_type: cheat-sheet
domain: 18-engineering-practices
topic_id: T-1803
canonical: ../syllabus/18-engineering-practices/working-with-legacy-code.md
last_updated: 2026-09-06
---

# Working with Legacy Code

**Canonical chapter:** [`syllabus/18-engineering-practices/working-with-legacy-code.md`](../syllabus/18-engineering-practices/working-with-legacy-code.md)

## Core Mental Model

Probe before asserting, and never adjust an observed result to match an expectation — a characterization test captures what code *actually does*, right now, as observed by running it, not what it *should* do.

## Essential Definitions

- **Legacy code** — code without tests (Michael Feathers' definition), regardless of age or how well-written it otherwise is; you cannot mechanically know whether a change preserved its behavior without a test suite.
- **Characterization test** — an assertion pinning down code's real, observed current behavior, written in two strict steps: (1) probe actual behavior across realistic inputs, printing (not asserting) results; (2) turn every observed result into an explicit assertion.
- **Seam** — a place in code where behavior can be changed without editing that exact spot (an interface, a parameter, a subclass override point); finding seams makes it possible to isolate legacy code for testing without a large, risky refactor first.
- **Sprout method / sprout class** — adding new behavior as a new, separately-testable method or class called from one minimal point in existing untested code, rather than modifying the untested method directly.

## Decision Table

| Situation | Response |
|---|---|
| About to change code with no tests | Probe real behavior first (print, don't assert), then write characterization tests, then change |
| New behavior can be cleanly separated | Use sprout method/class rather than editing existing untested logic |
| Side effects hard to observe (DB writes, external calls) | Characterization still applies, but the probe step needs a test double or isolated real environment |
| Behavior is non-deterministic (clock, random, external state) | Control the non-determinism (fixed clock, seeded random) before a stable assertion is possible |
| A characterized quirk is later intentionally fixed | Update that specific assertion deliberately with a clear note — don't leave it failing or silently delete it |
| Legacy code's risk has grown very large | Consider a system-level migration (Strangler Fig) instead of continuing incremental characterization indefinitely — document the decision as an ADR |

## Common Pitfalls

- Writing assertions based on what you expect legacy code to do, without first running it and observing real behavior.
- Performing a large refactor on untested legacy code before establishing any characterization tests, removing the one safety net that would catch a regression.
- Editing an existing, untested method directly to add new behavior instead of using sprout method/class.
- Treating a passing characterization test suite as proof the code is correct — it only proves current behavior is unchanged from when the tests were written; correctness is a separate, deliberate question.

## Interview Answer Skeleton

**30-sec:** Legacy code means code without tests. The technique for changing it safely is characterization testing: probe its actual behavior first (observe, don't assume), then pin those observations down as explicit assertions before making any intended change.

**2-min:** The workflow is two strict steps, in order. First, probe the code's real behavior across a spread of realistic inputs, printing — not asserting — the results, making no claims about correctness. Second, turn every observed result into an explicit assertion, creating a regression-detecting safety net. Only after that safety net exists is it safe to refactor or extend the code with real confidence. This repo's own practice demo makes it concrete: running `LegacyOrderPricer.price()` across a spread of quantities and unit prices surfaced a genuinely surprising real result — ordering 9 units and 10 units at one specific unit price produce the *identical* total, because the bulk discount at the 10-unit threshold exactly offsets the extra unit's cost. The characterization test then pins down exactly this observed behavior — labeled as current, real behavior, not as a claim it's correct. Probing before asserting matters precisely because writing assertions based on assumed rather than observed behavior either fails immediately (revealing a misunderstanding) or, worse, gets "fixed" to match the wrong expectation, silently defeating the whole technique.

**Staff-level framing:** At Staff scope, the real judgment call is recognizing when a piece of legacy code's risk has grown large enough to warrant a system-level migration strategy (Strangler Fig) rather than continuing incremental characterization-protected changes indefinitely. Deciding which legacy code merits incremental investment in test coverage versus which system has accumulated enough risk to need gradual replacement should be made deliberately and documented as an ADR, not defaulted into by inertia.

## Production Warning Signs

- A seemingly small, unrelated refactor of an untested legacy pricing method caused a real production pricing discrepancy discovered only after customers were charged incorrectly for hours. The refactor had no characterization test suite to catch the behavior change, so the regression went undetected until the most expensive place to find it: production. The fix is establishing characterization tests for the pricing method's *entire* current behavior, not just the case that broke, before any further changes.

## Related

- syllabus/18-engineering-practices/refactoring-discipline.md
- syllabus/17-architecture/strangler-fig-and-migration-patterns.md
- syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md
