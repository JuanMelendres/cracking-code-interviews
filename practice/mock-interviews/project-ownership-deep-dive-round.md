---
title: "Mock Interview: Project Ownership Deep-Dive Round (30 min)"
slug: project-ownership-deep-dive-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-09-23
target_levels:
  - junior
  - mid
  - senior
  - staff
duration_minutes: 30
competencies:
  - Full-lifecycle narration (Design, Develop, Deploy, Support), not a technology list
  - Support-phase depth — a real production signal and what changed as a result
  - Scope honesty — described ownership matches what follow-up questions can actually support
  - Design-phase trade-off narration (the four beats, applied live)
related:
  - ../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md
  - ../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md
  - ../../syllabus/20-interview-preparation/technical-answers/technical-answer-framework.md
source: None — built from this deliverable's own new Project Ownership Narrative chapter (no prior study-pack mock existed for this format)
official_references: []
---

# Mock Interview: Project Ownership Deep-Dive Round

**Target role:** Any level, Junior through Staff — this round's format is level-agnostic by design; what changes per level is the expected scope of the answer, not the questions asked. **Duration:** 30 minutes.

Unlike this deliverable's topic-specific rounds, this round runs against the candidate's **own real project**, not a repository-supplied prompt — the interviewer's job is to probe whatever project the candidate brings, using the four-phase structure from [Project Ownership Narrative](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md) as the evaluation lens.

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
| Full-lifecycle narration | All four probes below | [Project Ownership Narrative](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md) |
| Support-phase depth | Probe 4 | [Project Ownership Narrative § The Four-Phase Structure](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md#the-four-phase-structure) |
| Scope honesty | Probes 5–6 (the "one level deeper" follow-ups) | [Project Ownership Narrative § Scope Escalation, Phase by Phase](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md#scope-escalation-phase-by-phase) |
| Design-phase trade-off narration | Probe 1 | [Trade-off Narration and ADRs](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) |

## Interviewer Opening Script

*"Tell me about a project you built — something you can walk me through from why it was needed, through how it got built and shipped, to what happened after it was live. Take your time; I'll ask follow-up questions as you go."*

## Candidate Section

Pick one real project you worked on — any scope, from a single feature to a system you owned end to end. Narrate it across all four phases from [Project Ownership Narrative](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md): **Design** (what and why), **Develop** (how it got built), **Deploy** (how it went live), **Support** (what happened after). Expect follow-up questions after each phase — this is a conversation, not a monologue.

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Probe 1 — Design: "Why this way, and not some other way?"

**Question asked if not addressed organically.** *"You mentioned [the approach chosen] — what else did you consider, and why did this one win?"*
**Ideal answer outline.** Names a genuine alternative (not a strawman) and the specific deciding factor, per [Trade-off Narration and ADRs](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md)'s four beats — bonus if the candidate also names what the chosen approach cost.
**Pass signal.** A real alternative plus a specific deciding factor, unprompted or with one nudge.
**Borderline signal.** Names an alternative only when asked directly, or the "deciding factor" is vague ("it seemed better").
**Fail signal.** No real alternative — either no design decision is described at all, or the "alternative" is an obvious strawman.

### Probe 2 — Develop: "What was the hardest part of actually building this?"

**Question asked if not addressed organically.** *"What's a specific bug, edge case, or implementation challenge you ran into while building this?"*
**Ideal answer outline.** One specific, concrete technical difficulty — not "it was tricky" — with enough detail that a technical listener could picture the actual code or system state involved.
**Pass signal.** A specific, concrete implementation detail with real technical substance.
**Borderline signal.** Names a challenge in general terms without enough specificity to picture it concretely.
**Fail signal.** No implementation detail beyond restating the feature description ("I built the API endpoint").

### Probe 3 — Deploy: "How did this actually go live?"

**Question asked if not addressed organically.** *"Walk me through the actual rollout — what would have happened if something had gone wrong?"*
**Ideal answer outline.** Names the real deployment mechanism (a CI/CD pipeline, a feature flag, a canary, a manual runbook step) and what the safety net was, scaled to the candidate's actual role (a Junior candidate describing "I deployed through our existing pipeline and watched the dashboard" is a complete, honest answer at that scope).
**Pass signal.** Names a real mechanism and a real answer to "what if it went wrong," scaled honestly to the candidate's role.
**Borderline signal.** Names the deployment mechanism but has no answer for what the safety net was.
**Fail signal.** No description of how the change actually reached production at all.

### Probe 4 — Support: "What happened after it launched?" (the required probe — always ask this one)

**Question asked, always.** *"What's something that happened after this went live — a metric, an alert, an incident, a piece of feedback — and what, if anything, changed as a result?"*
**Ideal answer outline.** A real, specific post-launch signal and a real consequence (a code change, a process change, a runbook update, a design change for the next iteration) — per [Project Ownership Narrative](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md), this is the single most commonly skipped phase and the one this round weights most heavily.
**Pass signal.** A specific post-launch signal plus a real, named consequence.
**Borderline signal.** Names a post-launch signal ("it worked fine") with no real consequence or learning attached.
**Fail signal.** The story ends at deployment with no Support-phase content at all, even after this probe is asked directly.

### Probe 5 — Scope check: "What did you personally decide versus what did you personally build?"

**Question asked, always, after the main narrative.** *"In this story, which parts were decisions you made yourself, and which parts were you executing against someone else's design?"*
**Ideal answer outline.** A precise, honest split — the candidate can distinguish their own decision-making from their own execution, and the split is consistent with their stated level (per [Scope Escalation, Phase by Phase](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md#scope-escalation-phase-by-phase)).
**Pass signal.** A precise, consistent answer that matches the level being interviewed for.
**Borderline signal.** Vague on the distinction, or the split doesn't quite match the stated seniority but isn't a glaring mismatch.
**Fail signal.** Claims a scope of decision-making the rest of the story's details directly contradict (e.g., claims to have "architected" something already described as an existing interface they implemented against).

### Probe 6 — One level deeper (Senior/Staff candidates only)

**Question asked if candidate claims Senior/Staff-level scope.** *"You said you [drove/owned/decided] X — walk me through the specific moment that decision got made. Who was in the room, what was the actual disagreement, if any, and what tipped it?"*
**Ideal answer outline.** A specific, concrete recollection — names or role-anonymized stand-ins for real people, a real point of disagreement or open question, and the actual factor that resolved it.
**Pass signal.** Specific, concrete, holds up under this follow-up with no contradiction of the earlier narrative.
**Borderline signal.** Generic answer ("we discussed it as a team and agreed") with no real specificity.
**Fail signal.** The claimed ownership doesn't survive this question — the story reveals the candidate wasn't actually the decision-maker, contradicting Probe 5's answer.

## Scoring Rubric

Score on the general 1–5 scale (3 = Mid, 4 = Senior, 5 = Staff), using the six probes above as this round's own evidence anchors. Probe 4 (Support) and Probe 5/6 (scope honesty) carry the most weight — a candidate can pass Probes 1–3 with a purely technical, well-executed story and still fail the round overall by skipping Support entirely or by having their claimed scope collapse under Probe 6.

## Debrief Guide

Walk the candidate through Probe 4 first, regardless of how the rest of the round went — Support-phase depth is this round's single most diagnostic signal, and a candidate who's strong everywhere else but weak here has a specific, nameable gap worth calling out directly rather than folding into a vague "overall communication" note. Then walk through Probes 5–6 together: if the candidate's self-assessed scope (Probe 5) didn't survive the follow-up (Probe 6), point at the *specific* detail in the earlier narrative that contradicted the claimed scope — this is more useful feedback than a general "be careful not to overstate your role," since it shows the candidate exactly which sentence created the mismatch.

## Remediation Recommendations

- Weak Design probe (no real alternative or deciding factor) → re-read [Trade-off Narration and ADRs](../../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) and redeliver the same project's Design phase using the explicit four-beat structure.
- Weak Develop probe (no concrete technical detail) → pick one specific bug or edge case from the same project and describe it with enough detail that a listener could reproduce the mental model of what actually happened in the code.
- Weak Support probe (thin or missing) → this is the most common and most consequential gap; re-read [Project Ownership Narrative](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md)'s [Common Mistakes](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md#common-mistakes) section and identify one real post-launch signal from the same project before retaking this round.
- Scope mismatch under Probe 6 → re-read [Scope Escalation, Phase by Phase](../../syllabus/20-interview-preparation/technical-answers/project-ownership-narrative.md#scope-escalation-phase-by-phase) and re-tell the same story at the honestly-supportable scope, checking each phase against what the table actually attributes to that level.
- Any dimension scored below the target level overall → retake this round with a *different* real project, since a rehearsed single story can mask a gap in the underlying skill.
