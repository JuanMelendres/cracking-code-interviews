---
title: "Flashcards: Writing Tests Live in an Interview"
slug: writing-tests-live-in-an-interview
document_type: flashcard-deck
domain: testing
topic_id: T-1108
canonical: ../handbook/testing/writing-tests-live-in-an-interview.md
last_updated: 2026-08-06
---

# Flashcards: Writing Tests Live in an Interview

**Canonical chapter:** [`handbook/testing/writing-tests-live-in-an-interview.md`](../handbook/testing/writing-tests-live-in-an-interview.md)

## Card: What live-coding test-first tests that a take-home can't

**Prompt:**
Why does the live-coding test-first format specifically test something a take-home assignment can't?

**Answer:**
It reveals whether a candidate's testing discipline survives real time pressure, since production incidents also happen under time pressure — a take-home assignment has no equivalent time-pressure signal.

**Why it matters:**
The precise reason interviewers weight the live-coding round differently from a take-home submission.

**Common trap:**
Treating a polished take-home submission as equally informative about a candidate's under-pressure testing discipline.

**Related:**
[handbook/testing/writing-tests-live-in-an-interview.md](../handbook/testing/writing-tests-live-in-an-interview.md)

## Card: What must be confirmed before leaving a red step

**Prompt:**
What must be confirmed before moving on from a "red" step?

**Answer:**
That the test failed for the *expected* reason (a wrong value, not a compile error or unrelated exception) — not just that it failed.

**Why it matters:**
Distinguishes genuine red-green-refactor discipline from a candidate who merely runs the test suite without reading the failure.

**Common trap:**
Seeing any red output and moving straight to implementation without confirming the failure reason.

**Related:**
[handbook/testing/writing-tests-live-in-an-interview.md](../handbook/testing/writing-tests-live-in-an-interview.md)

## Card: What to do when running low on time mid-kata

**Prompt:**
What should a candidate do when running low on time mid-kata, rather than silently rushing?

**Answer:**
Communicate a concrete scope-reduction plan explicitly — this mirrors real production trade-off communication and is itself a positive signal.

**Why it matters:**
The single most transferable signal from this exercise to actual on-the-job behavior under a real deadline.

**Common trap:**
Silently dropping test-first discipline or rushing implementation without narrating the trade-off being made.

**Related:**
[handbook/testing/writing-tests-live-in-an-interview.md](../handbook/testing/writing-tests-live-in-an-interview.md)
