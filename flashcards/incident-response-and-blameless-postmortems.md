---
title: "Flashcards: Incident Response and Blameless Postmortems"
slug: incident-response-and-blameless-postmortems
document_type: flashcard-deck
domain: performance
topic_id: T-1207
canonical: ../handbook/performance/incident-response-and-blameless-postmortems.md
last_updated: 2026-09-02
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
[handbook/performance/incident-response-and-blameless-postmortems.md](../syllabus/13-observability/incident-response-and-blameless-postmortems.md)

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
[handbook/performance/incident-response-and-blameless-postmortems.md](../syllabus/13-observability/incident-response-and-blameless-postmortems.md)

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
[handbook/performance/incident-response-and-blameless-postmortems.md](../syllabus/13-observability/incident-response-and-blameless-postmortems.md)
