---
title: "Flashcards: Leading Migrations and Large-Scale Technical Change"
slug: leading-migrations-and-large-technical-change
document_type: flashcard-deck
domain: 19-leadership-staff
topic_id: T-1903
canonical: ../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md
last_updated: 2026-09-07
---

# Flashcards: Leading Migrations and Large-Scale Technical Change

**Canonical chapter:** [`syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md`](../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md)

## Card: Why migrations fail despite sound technical design

**Prompt:**
Why can a well-designed migration (sound strangler-fig plan, solid dual-write strategy) still stall organizationally?

**Answer:**
Because the people who benefit from a migration's completion (often the whole organization, diffusely) are frequently not the same people who bear its cost (the specific engineers whose sprint capacity it consumes) — a strong technical case can still fail to get sustained priority unless someone actively manages that misalignment.

**Why it matters:**
Leading a migration well is a distinct, teachable skill from designing one well — the organizational layer, not the technical one, is usually where migrations actually fail.

**Common trap:**
Assuming a technically sound plan is sufficient on its own to sustain priority across multiple teams and sprints.

**Related:**
[syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md](../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md)

## Card: The "last 10%" problem

**Prompt:**
What is the "last 10%" problem in large migrations, and what causes it?

**Answer:**
The bulk of callers, usages, or data typically migrates relatively quickly, while a long tail of edge cases, rarely-touched call sites, and stragglers consumes a disproportionate share of total calendar time. A migration plan that doesn't explicitly account for this tail from the start routinely runs over its estimated timeline.

**Why it matters:**
It's one of the most concrete, recognizable failure modes in migration leadership, and directly connects to why the "contract" phase of expand-contract needs deliberate planning.

**Common trap:**
Estimating a migration's timeline as if the last stragglers will move at the same rate as the early, easy adopters.

**Related:**
[syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md](../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md)

## Card: Expand-contract's leadership gap

**Prompt:**
In the expand-contract (parallel change) pattern, which phase is where migrations actually die, and why?

**Answer:**
The contract phase — once expand has made the new path available and most callers have moved, there is no more forcing function compelling the last stragglers to move. Removing the old path (the step that actually finishes the migration and captures its benefit) requires deliberate, continued push.

**Why it matters:**
Securing a forcing function (e.g., a leadership-backed deprecation date) is far easier to obtain *before* the expand phase begins, when no specific team yet has a concrete objection to it.

**Common trap:**
Discovering at 90% adoption that nothing is compelling the remaining stragglers to complete the cutover, because no forcing function was planned from the start.

**Related:**
[syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md](../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md)

## Card: Visible tracking artifact as a leadership tool

**Prompt:**
Why is a shared tracking artifact (e.g., a dashboard or list of migration status per caller) more than a status report?

**Answer:**
It converts an invisible, easy-to-deprioritize background task into a concrete, comparably-visible piece of work — teams that can see they are the last remaining unmigrated caller on a shared list behave differently than teams with no visibility into how their inaction compares to everyone else's progress.

**Why it matters:**
It's a concrete mechanism for applying comparative social pressure without escalation — e.g., raising a stalled team's status directly, referencing that every other consumer has already moved.

**Common trap:**
Tracking migration progress informally or privately, so no team can see how their own progress compares to the rest.

**Related:**
[syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md](../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md)

## Card: Status communication decay in long migrations

**Prompt:**
When does a migration most need regular status updates, and why?

**Answer:**
During its long, unglamorous middle phase — a migration announced with fanfare at kickoff but only reported on in occasional, easy-to-miss updates loses stakeholder attention exactly when sustained prioritization pressure is most needed, since initial momentum has faded and the finish line isn't visible yet.

**Why it matters:**
Regular, lightweight, concrete status updates (not a large ceremonial review) are the mechanism that keeps a migration from silently losing priority mid-flight.

**Common trap:**
Communicating status only at kickoff and at completion, leaving the middle phase invisible to stakeholders whose continued prioritization support is needed.

**Related:**
[syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md](../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md)

## Card: Brooks's Law applied to a stalled migration

**Prompt:**
Why does "throw more engineers at it" usually backfire for a coordination-bound migration that has fallen behind?

**Answer:**
Fred Brooks's observation in *The Mythical Man-Month* — adding people to an already-late, coordination-heavy effort tends to make it later, not faster, because coordination overhead grows faster than the raw capacity added. For a multi-team migration, the bottleneck is decision and sequencing capacity, not raw engineering hours.

**Why it matters:**
It's a direct, citable rebuttal to the instinct to add headcount to hit an original deadline on a stalled, coordination-bound migration.

**Common trap:**
Responding to a behind-schedule migration by adding more engineers, when the actual constraint is cross-team prioritization negotiation, not implementation throughput.

**Related:**
[syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md](../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md)
