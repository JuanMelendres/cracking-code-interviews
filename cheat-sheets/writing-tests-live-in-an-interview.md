---
title: "Cheat Sheet: Writing Tests Live in an Interview"
slug: writing-tests-live-in-an-interview
document_type: cheat-sheet
domain: testing
topic_id: T-1108
canonical: ../handbook/testing/writing-tests-live-in-an-interview.md
last_updated: 2026-08-05
---

# Writing Tests Live in an Interview

**Canonical chapter:** [`handbook/testing/writing-tests-live-in-an-interview.md`](../handbook/testing/writing-tests-live-in-an-interview.md)

## Core Mental Model

Live-coding test-first is a narrated loop, not a silent one: write one small failing test, run it and show it fails for the *expected* reason, write the minimum code to pass it, run it and show it passes, then decide whether to add another case or refactor — repeated in small, visible steps rather than one large batch of code followed by one large batch of tests at the end. The narration matters as much as the code.

## Essential Definitions

- **Red-green-refactor** — write a failing test, write minimal code to pass it, then optionally refactor while the test still passes.
- **Expected failure** — a red step is necessary but not sufficient; the failure must be the *expected* one (wrong value, not a compile error or unrelated exception), stated out loud.
- **Minimal implementation** — deliberately incomplete on purpose (e.g., `return "";` before real logic exists), explicitly framed as such so it doesn't read as sloppy.

## Decision Table

| Situation | Right move |
|---|---|
| Choosing the first test case | Smallest/simplest meaningful case (empty/trivial), narrated why |
| A test just went red | Confirm it failed for the *expected* reason before writing any code |
| An unexpected failure appears | Read the actual assertion message calmly; don't assume the test itself is wrong |
| Running low on time mid-kata | Say so explicitly and propose a concrete scope-reduction plan |

**Trade-offs:** narrating every step slows the visible pace under a ticking clock, but a fast, silent session gives the evaluator far less signal about actual process and judgment than a slower, narrated one — even when both produce identical final code.

## Key Numbers (real, executed — run-length-encoding kata, `"aaabbc"` → `"a3b2c1"`)

```
STEP 1: RED (empty string test, wrong impl)
  1 tests found, 0 successful, 1 failed
  AssertionFailedError: expected: <> but was: <null>

STEP 2: GREEN (minimal impl: return "";)
  1 tests found, 1 successful, 0 failed

STEP 3: RED (new test, old impl now insufficient)
  AssertionFailedError: expected: <a1> but was: <>
  2 tests found, 1 succeeded, 1 failed

STEP 4: GREEN (full impl, all 3 tests)
  3 tests found, 3 successful, 0 failed
```

Each transition is real captured console output — the exact visible checkpoint rhythm (fail for the right reason, pass minimally, fail again on a new case, pass fully) an interviewer watching a live session follows.

## Common Pitfalls

- Writing the full implementation first, adding tests afterward "to be thorough" — inverts the exact skill the format tests for, even when the final code is correct.
- Treating the minimal implementation step as if it should already be complete, causing hesitation or over-engineering under time pressure.
- Going silent while writing code, giving the evaluator no window into test-case reasoning.
- Panicking at an unexpected failure instead of calmly reading the actual assertion message.

## Interview Answer Skeleton

**30-sec:** Live-coding test-first means running a narrated red-green-refactor loop: one small failing test at a time, confirming it fails for the expected reason, writing minimal code to pass it, then deciding whether to add another case or refactor — never a large batch of implementation first, tests after.

**2-min:** Add why the live format specifically exists (tests whether testing discipline survives time pressure, since production incidents also happen under pressure) + the real captured evidence (four real steps of a run-length-encoding kata, each with actual pass/fail console output) + the trade-off (narrating costs visible pace but is what makes the session evaluable at all).

**Whiteboard:** A circular loop with four labeled arrows: "write one small failing test" → "run it, confirm RED for the expected reason" → "write minimal code" → "run it, confirm GREEN" → back to start (or branch to "refactor"). Annotate the center: "narrate every arrow out loud — the loop itself is what's being evaluated."

**Staff-level framing:** explicitly communicate scope trade-offs when time-constrained ("I'll skip the refactor step given the time") rather than silently rushing or silently dropping the discipline — this mirrors real production trade-off communication under a deadline and is itself a positive signal, not an admission of failure.

## Production Warning Signs

- A candidate writes a large batch of code silently, then runs tests once at the end — defeats the interview format's purpose regardless of final correctness; the value is in the narrated, incremental process, not just the destination.
- A test fails and the candidate can't say why without staring at the trace for a long time — suggests the test wasn't actually understood before being written, or the failure output isn't being read carefully.
- **Prevention:** default to narrating each small step even when it feels slower, and practice reading actual JUnit assertion-failure messages until doing so under pressure is fluent, not effortful.

## Related

- `handbook/testing/test-strategy-and-test-doubles.md`
- `handbook/testing/junit5-architecture-and-advanced-features.md`
