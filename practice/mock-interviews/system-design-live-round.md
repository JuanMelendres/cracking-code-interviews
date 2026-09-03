---
title: "Mock Interview: System Design Live Round (45 min)"
slug: system-design-live-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - Six-phase design method run unhabituated, self-transitioned
  - Caching as a reasoned response to a recomputation cost
  - Fan-out-on-write vs. fan-out-on-read as an explicit trade-off
  - Keyset vs. OFFSET pagination
related:
  - ../../handbook/system-design/system-design-method-and-estimation.md
  - ../../handbook/system-design/caching-strategies-and-invalidation.md
  - ../../syllabus/07-api-design/api-design.md
source: ../../study-packs/week-04/07-week-4-mock-interview.md
official_references: []
---

# Mock Interview: System Design Live Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** partner strongly preferred — the source explicitly notes a self-mock cannot generate the unexpected follow-ups that make this round valuable. Elevated from `study-packs/week-04/07-week-4-mock-interview.md`.

This round has a different shape from the itemized-question rounds elsewhere in this deliverable: the source is a single open-ended design prompt run through a six-phase method, not a numbered question bank. The Evaluator Section below is organized around that method's phases plus the four specific interviewer probes the source names, rather than per-question numbering.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Evidence source | Canonical Chapter |
|---|---|---|
| Six-phase method, self-transitioned | No prompted phase transitions | [System Design Method and Estimation](../../handbook/system-design/system-design-method-and-estimation.md) |
| Caching as a reasoned response | Probe 2 (minute ~20) | [Caching Strategies and Invalidation](../../handbook/system-design/caching-strategies-and-invalidation.md) |
| Fan-out-on-write vs. fan-out-on-read | Probe 3 | [Caching Strategies and Invalidation](../../handbook/system-design/caching-strategies-and-invalidation.md) (precompute strategy), design prompt's own framing |
| Keyset vs. OFFSET pagination | Probe 4 | [API Design](../../syllabus/07-api-design/api-design.md) |

## Interviewer Opening Script

*"This is a 45-minute full system design round. Design a news feed system for me — I'll act as a real user of the product and a real stakeholder with constraints, not as a checklist. Run whatever method you'd normally use; I won't prompt you between phases. Go ahead whenever you're ready."*

## Candidate Section

Design a **news feed** system (or a different unseen problem your partner supplies, if running this as a partner mock). Run the full six-phase method — Clarify, Estimate, API, Data, Architecture, Bottlenecks — from [System Design Method and Estimation](../../handbook/system-design/system-design-method-and-estimation.md), across the full 45 minutes. Caching and fan-out are mandatory discussion points: if the design hasn't naturally reached them by minute 30, raise them yourself.

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Phase habituality — no prompted transitions

**Ideal answer outline:** the candidate moves through Clarify, Estimate, API, Data, Architecture, and Bottlenecks without being told when one phase ends and the next begins — the method should be a practiced habit, not something followed only when cued.
**Common weak answers:** stalling at the end of a phase, visibly waiting for permission to continue, or skipping straight to Architecture without a stated Estimate phase.
**Pass signal:** self-transitions through all six phases within the 45 minutes with no interviewer prompting.
**Borderline signal:** needs one nudge to transition, most commonly into Estimate or Bottlenecks.
**Fail signal:** needs prompting at most phase transitions, or never reaches Bottlenecks.

### Probe: caching (raised by minute ~20 if not organic)

**Question asked if not raised organically:** *"How would a user's feed avoid being recomputed from scratch on every single page load?"*
**Ideal answer outline:** identifies a caching layer sitting in front of the recomputation cost, justified by a specific number from the Estimate phase (read QPS versus what recomputing a feed from scratch would cost per request), and states an invalidation approach.
**Pass signal:** raises caching unprompted before minute 20, tied to a stated number; or, if prompted, answers immediately and specifically.
**Borderline signal:** proposes caching only in general terms ("we'd cache it") without tying it to a number or an invalidation strategy.
**Fail signal:** never connects caching to the recomputation cost even after being prompted directly.

### Probe: fan-out (a user with 10 million followers posts)

**Question asked if not raised organically:** *"A user with 10 million followers posts something. Walk me through what happens."*
**Ideal answer outline:** names both fan-out-on-write (precompute every follower's feed entry at post time — expensive for celebrity accounts) and fan-out-on-read (compute the feed at read time — expensive for heavy readers) as a genuine trade-off, and proposes handling celebrity accounts differently from ordinary ones (a hybrid).
**Common weak answers:** naming only one approach with no acknowledgment that it has a failure mode at the stated scale.
**Pass signal:** names both approaches explicitly as a trade-off, not just one approach in isolation.
**Borderline signal:** names one approach correctly but doesn't recognize it breaks down for a 10-million-follower account specifically.
**Fail signal:** no recognition of a write-time-vs-read-time trade-off at all.

### Probe: pagination (if pagination comes up during bottleneck analysis)

**Question asked:** *"Would you use OFFSET or keyset pagination here, and why?"*
**Ideal answer outline:** chooses keyset (cursor-based) pagination for the feed's "next page" case, explaining that `OFFSET` forces the database to walk and discard every skipped row — cost that grows linearly with page depth — while a keyset condition (`WHERE id > last_seen_id`) seeks directly to the right starting point regardless of depth.
**Common weak answers:** defaulting to `OFFSET` with no acknowledgment of its depth cost, or choosing keyset without being able to explain why.
**Pass signal:** chooses keyset pagination and explains the depth-cost mechanism specifically.
**Borderline signal:** chooses keyset correctly but can't explain the underlying mechanism when asked why.
**Fail signal:** chooses OFFSET with no cost discussion, for a feed-scale endpoint.

## Scoring Rubric

The source rubric ([`study-packs/week-01/10-week-1-evaluation-rubric.md`](../../study-packs/week-01/10-week-1-evaluation-rubric.md)) is explicit that System Design is "not a Week 1 Deep topic," scored only when a design exercise is attempted, and defers to the general rubric referenced in [`00-project/learning-roadmap.md`](../../00-project/learning-roadmap.md) rather than defining week-specific evidence anchors of its own. Score this round on the general 1–5 scale (3 = Mid, 4 = Senior, 5 = Staff) using the phase-habituality and three-probe evidence anchors above as this round's own anchors, since none exist in the source rubric.

## Debrief Guide

Walk the candidate through phase habituality first — it's the structural signal underlying everything else in the round, since a candidate who needs prompting between phases is also more likely to need prompting on caching, fan-out, and pagination specifically. The caching and fan-out probes share a theme: both ask whether the candidate connects an architectural decision back to a *specific* number or a *specific* named failure mode at extreme scale (10 million followers), rather than reaching for the "correct-sounding" component reflexively. A candidate who names caching and fan-out only when prompted, both times, has a general pattern worth naming directly, not two unrelated gaps.

## Remediation Recommendations

- Weak phase habituality → re-read [System Design Method and Estimation](../../handbook/system-design/system-design-method-and-estimation.md), and re-run the drill under a strict 45-minute timer with no interviewer prompting at all, even when stuck.
- Weak caching probe → re-read [Caching Strategies and Invalidation](../../handbook/system-design/caching-strategies-and-invalidation.md).
- Weak fan-out probe → revisit this design's own Estimate-phase numbers and re-derive the fan-out-on-write cost for a 10-million-follower account explicitly.
- Weak pagination probe → re-read [API Design](../../syllabus/07-api-design/api-design.md)'s pagination section, including its measured OFFSET-vs-keyset execution-time comparison.
- Any dimension scored below Senior (4) overall → retake this mock in full, ideally with a different unseen design prompt, since a memorized single scenario doesn't test the underlying method.
