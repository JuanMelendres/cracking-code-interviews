---
title: "Flashcards: Incident Response and Blameless Postmortems"
slug: incident-response-and-blameless-postmortems
document_type: flashcard-deck
domain: performance
topic_id: T-1207
canonical: ../syllabus/13-observability/incident-response-and-blameless-postmortems.md
last_updated: 2026-09-14
---

# Flashcards: Incident Response and Blameless Postmortems

**Canonical chapter:** [`syllabus/13-observability/incident-response-and-blameless-postmortems.md`](../syllabus/13-observability/incident-response-and-blameless-postmortems.md)

## Card: Mitigate or diagnose first?

**Prompt:**
During an active incident, should you mitigate or fully diagnose first?

**Answer:**
Mitigate first, by default — user-facing impact compounds every minute, and a mitigation (rollback, feature flag) is usually much faster than full diagnosis, which can proceed in parallel or afterward.

**Why it matters:**
It's the register's own named follow-up question, testing real incident instinct under pressure, not just theoretical knowledge.

**Common trap:**
Defending "diagnose first" as more thorough, without weighing the real cost of continued user-facing impact.

**Related:**
[syllabus/13-observability/incident-response-and-blameless-postmortems.md](../syllabus/13-observability/incident-response-and-blameless-postmortems.md)

## Card: Contributing factors vs. root cause

**Prompt:**
Why is "Contributing Factors" the correct postmortem frame, not "Root Cause"?

**Answer:**
Real incidents typically have several genuinely independent contributing factors — collapsing them into a single root cause leaves the others unaddressed, and the same category of incident can recur through a different proximate trigger.

**Why it matters:**
It's the register's own named misconception, and this chapter's real linter enforces it structurally by flagging a singular "Root Cause" heading directly.

**Common trap:**
Using "5 Whys" until reaching one satisfying-sounding cause, without asking whether other, independent causal chains also contributed.

**Related:**
[syllabus/13-observability/incident-response-and-blameless-postmortems.md](../syllabus/13-observability/incident-response-and-blameless-postmortems.md)

## Card: The eight-step diagnosis process

**Prompt:**
Name the general, repeatable process for diagnosing a production bug, in order.

**Answer:**
Confirm and scope the impact → gather evidence (logs/metrics/traces) → form a hypothesis the evidence actually supports and test it → isolate by bisecting layer, deploy, or code path → confirm root cause with reproducible evidence, not just correlation → implement the smallest fix → deploy gradually and verify via the same evidence that showed the symptom → add a regression test and any monitoring that would have caught it sooner.

**Why it matters:**
"Walk me through your debugging process" is a standard interview question testing for a real, repeatable method versus jumping straight to guessing at a fix.

**Common trap:**
Skipping straight from symptom to a guessed fix, without gathering evidence first or confirming the root cause is actually reproducible rather than merely plausible.

**Related:**
[syllabus/13-observability/incident-response-and-blameless-postmortems.md](../syllabus/13-observability/incident-response-and-blameless-postmortems.md), [syllabus/18-engineering-practices/working-with-legacy-code.md](../syllabus/18-engineering-practices/working-with-legacy-code.md)

## Card: What changes in legacy code

**Prompt:**
Does the production-debugging process change in a legacy, undertested codebase?

**Answer:**
Only one step changes: before implementing the fix, write a characterization test that pins the code's actual current behavior as a safety net — since there's no existing test suite to catch an unintended side effect of the change. Every other step is identical regardless of the codebase's age.

**Why it matters:**
A candidate who claims legacy code needs a "completely different" process usually hasn't actually reasoned about *why* it's harder — the real answer is one precise, mandatory pre-step, not a different philosophy.

**Common trap:**
Treating "be extra careful" as a substitute for a characterization test — care isn't checkable or repeatable; a test is.

**Related:**
[syllabus/13-observability/incident-response-and-blameless-postmortems.md](../syllabus/13-observability/incident-response-and-blameless-postmortems.md), [syllabus/18-engineering-practices/working-with-legacy-code.md](../syllabus/18-engineering-practices/working-with-legacy-code.md)

## Card: Blameless is checkable, not just name-free

**Prompt:**
Is a postmortem blameless simply because it doesn't name an individual?

**Answer:**
No — blame-coded language ("failed to," "should have caught," "human error") carries the same effect under different phrasing. This chapter's real linter checks for this mechanically and quotes the exact offending sentence when found.

**Why it matters:**
Blame-coded language has a real, measurable cost beyond morale — this chapter's production scenario found blame-framed postmortems had measurably shallower "contributing factors" analysis, because blame gives an investigation a place to stop early.

**Common trap:**
Treating "we didn't say whose fault it was" as sufficient without checking the document's actual language.

**Related:**
[syllabus/13-observability/incident-response-and-blameless-postmortems.md](../syllabus/13-observability/incident-response-and-blameless-postmortems.md)
