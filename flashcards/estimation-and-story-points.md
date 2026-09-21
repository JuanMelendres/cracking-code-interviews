---
title: "Flashcards: Estimation and Story Points"
slug: estimation-and-story-points
document_type: flashcard-deck
domain: 18-engineering-practices
topic_id: T-1805
canonical: ../syllabus/18-engineering-practices/estimation-and-story-points.md
last_updated: 2026-09-21
---

# Flashcards: Estimation and Story Points

**Canonical chapter:** [`syllabus/18-engineering-practices/estimation-and-story-points.md`](../syllabus/18-engineering-practices/estimation-and-story-points.md)

## Card: Why cross-team velocity comparison is invalid

**Prompt:**
Is it valid to compare two teams' velocity to decide which one is more productive?

**Answer:**
No — velocity is only meaningful as a trend within one team's own history, because each team calibrates its point scale independently against its own reference stories. A real demo proves this numerically: two teams estimating the identical 135 hours of real work produced velocities of 61 and 127 points respectively — a 2.1x difference explained entirely by calibration, not delivered output.

**Why it matters:**
A real, common organizational mistake — ranking teams by raw velocity numbers.

**Common trap:**
Agreeing velocity comparison is "bad practice" without being able to explain the actual mechanism (independent calibration).

**Related:**
[Core Concepts](../syllabus/18-engineering-practices/estimation-and-story-points.md#4-core-concepts-l2)

## Card: Why the story-point scale is Fibonacci-like

**Prompt:**
Why is the story-point scale (1, 2, 3, 5, 8, 13, 20, 40, 100) non-linear instead of just counting up (1, 2, 3, 4, 5)?

**Answer:**
Because estimation precision genuinely degrades as size grows — the real difference between a 1-hour and a 2-hour task is meaningful and worth distinguishing, but the difference between a 40-hour and a 41-hour task isn't distinguishable at that scale. The widening gaps directly reflect real, growing estimation uncertainty.

**Why it matters:**
Shows the scale's design is functionally motivated, not arbitrary or traditional.

**Common trap:**
Assuming the scale is just a convention rather than a deliberate reflection of estimation uncertainty.

**Related:**
[Foundation](../syllabus/18-engineering-practices/estimation-and-story-points.md#3-foundation-l1)

## Card: A story point is not a disguised time unit

**Prompt:**
Is it correct to say "a story point equals about 4 hours, so multiply points by 4 to get the schedule"?

**Answer:**
No — that treats a story point as a disguised time unit, defeating the entire purpose of relative estimation and reintroducing the false precision it exists to avoid. A story point is a relative-size comparison against a team's own past reference stories, not a time conversion.

**Why it matters:**
One of the most common conceptual mistakes about story points, even among engineers who use them daily.

**Common trap:**
Converting points to hours as if the relationship were a fixed, universal constant.

**Related:**
[Common Mistakes](../syllabus/18-engineering-practices/estimation-and-story-points.md#8-common-mistakes)
