---
title: "Cheat Sheet: Mutation and Property-Based Testing"
slug: mutation-and-property-based-testing
document_type: cheat-sheet
domain: testing
topic_id: T-1107
canonical: ../handbook/testing/mutation-and-property-based-testing.md
last_updated: 2026-08-05
---

# Mutation and Property-Based Testing

**Canonical chapter:** [`handbook/testing/mutation-and-property-based-testing.md`](../handbook/testing/mutation-and-property-based-testing.md)

## Core Mental Model

Property-based testing finds bugs in your *code* by throwing many random, varied inputs at an invariant you assert should always hold, hoping to stumble onto the input combination your hand-picked examples missed. Mutation testing finds bugs in your *test suite* by deliberately introducing small, real defects (mutants) into already-correct code and checking whether your existing tests notice — a surviving mutant reveals a specific, real gap in what your tests actually verify. One stress-tests the implementation; the other stress-tests the tests themselves.

## Essential Definitions

- **Property-based testing** — generates many randomized inputs and checks a stated invariant holds for each, rather than only specific hand-picked examples.
- **Mutation testing** — introduces small, automated code changes (mutants — `>=` to `>`, `+` to `-`) and re-runs the existing suite; a mutant that causes a failure is "killed," one that survives reveals a real test-suite gap.
- **Equivalent mutant** — a code change with genuinely identical observable behavior for every input; cannot be killed by any test, and chasing it is a real time sink.

## Decision Table

| Technique | Question answered | Finds bugs in |
|---|---|---|
| Property-based testing | Does this invariant hold for any valid input? | The code under test |
| Mutation testing | Would the suite actually catch a real defect here? | The test suite itself |
| Line coverage | Did this code execute during testing? | Neither — measures execution only, not verification |

**Trade-offs:** mutation testing directly measures test-suite effectiveness that line coverage cannot, but running it full-codebase is computationally expensive (the suite reruns once per mutant) — typically scoped to high-risk modules. Property-based testing finds genuine bugs biased examples miss, but requires a clean, precisely-statable invariant, not always available for arbitrary business logic.

## Key Numbers (real, executed)

Property-based test finding a real bug (merge-sorted-arrays, forgotten tail-copy loop) two biased hand-picked examples both missed:

```
Example tests (hand-picked, biased toward a-longer): 2 tests, 2 succeeded
Property-based test (random relative lengths, 2000 trials):
  FAILED on TRIAL 2: a=[1,4,5] b=[5,6,9] expected=[1,4,5,5,6,9] actual=[1,4,5,0,0,0]
```

Mutation testing (`>=` mutated to `>` in a discount-eligibility check):

```
WEAK suite vs MUTANT:   2 tests, 2 succeeded  <- mutant SURVIVED (no boundary test)
STRONG suite vs MUTANT: 3 tests, 2 succeeded, 1 FAILED  <- mutant KILLED (added boundary=100 test)
```

## Common Pitfalls

- Conflating mutation testing and property-based testing as interchangeable, rather than answering fundamentally different questions.
- Treating high line coverage as sufficient evidence of test quality — coverage measures execution, not verification strength.
- Running property-based tests with a freshly-randomized seed every run, making failures hard to reproduce deterministically.
- Chasing an equivalent mutant indefinitely, mistaking an unkillable-by-design mutant for a real test gap.

## Interview Answer Skeleton

**30-sec:** Property-based testing finds bugs in code by checking a stated invariant against many randomized inputs, catching cases hand-picked examples miss due to unconscious example-writer bias. Mutation testing finds gaps in a test suite by deliberately introducing real code defects and checking whether existing tests notice — a surviving mutant reveals a specific weakness line coverage cannot detect.

**2-min:** Add why both exist (example-based tests are only as good as the examples chosen, and a developer's own mental model biases which examples they think to write) + the real evidence (two biased example tests both passed a genuinely buggy merge function; a property-based test found the bug on trial 2; a real mutant survived a weak suite and was killed only after adding one boundary test) + the trade-off (mutation testing's computational cost argues for scoping to high-risk modules, not blanket adoption).

**Whiteboard:** Two diagrams side by side. Left "Property-based testing" — a box "Code under test" with many random arrows "random inputs" flowing in, checkmark/x for "invariant holds?" Right "Mutation testing" — "Code under test" with a wrench icon "mutant," an arrow from "Existing test suite" branching to "still passes? → SURVIVED" vs. "fails? → KILLED."

**Staff-level framing:** propose deliberate scoping for mutation testing given its real computational cost, rather than a blanket CI requirement. Recognize which code categories (round-trip, structural invariants) are genuinely well-suited to property-based testing versus which need deliberately-chosen example/boundary tests instead.

## Production Warning Signs

- A module reports 95% line coverage and is treated as well-tested, but a real bug ships in a boundary condition — a retrospective mutation-testing run on that module reveals surviving mutants clustered exactly at the boundary conditions the bug occurred in; the coverage number was real, the assertions were too weak.
- A property-based test fails intermittently with a different counterexample each run — use a fixed, logged random seed so any failing case is immediately, deterministically reproducible.
- **Prevention:** scope mutation testing deliberately to a codebase's highest-risk modules (financial calculations, security boundaries) rather than a full-codebase CI gate; fix and log the random seed for any property-based test as standard practice.

## Related

- `handbook/testing/test-strategy-and-test-doubles.md`
- `handbook/testing/junit5-architecture-and-advanced-features.md`
