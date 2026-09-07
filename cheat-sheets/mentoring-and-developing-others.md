---
title: "Cheat Sheet: Mentoring and Developing Others"
slug: mentoring-and-developing-others
document_type: cheat-sheet
domain: 19-leadership-staff
topic_id: T-1901
canonical: ../syllabus/19-leadership-staff/mentoring-and-developing-others.md
last_updated: 2026-09-06
---

# Mentoring and Developing Others

**Canonical chapter:** [`syllabus/19-leadership-staff/mentoring-and-developing-others.md`](../syllabus/19-leadership-staff/mentoring-and-developing-others.md)

## Core Mental Model

Mentoring is deliberately growing someone else's skill and judgment — asking before telling — not solving problems for them or working alongside them by default. The calibration skill is matching delegation to the person's *demonstrated competence at that specific task*, not their general seniority, and distinguishing productive struggle (making genuine, if slow, progress) from a stall (stuck on a wrong mental model) by trajectory over time, not a single difficulty snapshot.

## Essential Definitions

- **Mentoring vs. sponsorship** — mentoring is coaching when together; sponsorship is advocating for someone's advancement when they're not in the room. No formal authority is required for either, but Ibarra's research finds sponsorship often matters more for advancement.
- **GROW model** (Whitmore) — Goal, Reality, Options, Will: a conversation structure that has the mentee supply the answer rather than the mentor handing one over.
- **SBI feedback model** (Center for Creative Leadership) — Situation, Behavior, Impact: structures feedback around a specific event and its concrete effect, not a trait judgment.
- **Delegation ladder** — "do exactly this" / "propose a plan, I'll approve it" / "keep me informed after you decide" / "full autonomy, tell me only if something changes materially."
- **Zone of proximal development** (Vygotsky) — the gap between unaided and supported competence; a well-calibrated stretch assignment sits inside it, not below (no learning) or above (unsupported failure) it.

## Decision Table — Delegation Ladder

| Rung | Meaning | Fit signal |
|---|---|---|
| Do exactly this | Mentor specifies the steps | Mentee has no demonstrated competence at this specific task yet |
| Propose a plan, I'll approve | Mentee drafts, mentor reviews before execution | Mentee has partial exposure (e.g., reviewed others' work) but no solo track record |
| Keep me informed after deciding | Mentee acts, reports after | Mentee has a demonstrated track record at this specific task |
| Full autonomy | Mentee acts, flags only material changes | Mentee is ready; check-ins would only slow them down |

Calibrate per task, not per person — a senior mentee can still sit at rung 2 for a genuinely new task type.

## Common Pitfalls

- Giving the answer instead of asking the question — collapses GROW into the mentor doing the mentee's thinking; correct short-term outcome, no durable skill transfer.
- Feedback as trait judgment ("you need to be more proactive") instead of SBI-structured — not concrete enough to act on.
- Delegating at one fixed rung regardless of the specific task — competence is task-specific, not a single global level.
- Confusing "being helpful" with "being available for every question immediately" — removes the productive-struggle zone before it can operate.

## Interview Answer Skeleton

**30-sec:** Mentoring is deliberately growing someone's skill and judgment through structured conversation (GROW) and specific, timely feedback (SBI), calibrated to their demonstrated competence at the specific task — not their general seniority.

**2-min:** Add the delegation ladder as the calibration mechanism and a concrete example: a mentee who has reviewed four RFCs but never authored one gets rung 2 ("draft the Context section, review before continuing") — not rung 1 (too low, wastes their existing exposure) or rung 4 (too high, their first solo attempt). Layer in feedback timing: immediate feedback is more specific and actionable than feedback saved for a quarterly review, since the decay curve makes delayed feedback both vaguer and too late to change the behavior it's about.

**Whiteboard:** Draw the GROW conversation as four boxes in sequence — Goal, Reality, Options, Will — and narrate that the mentor's job is to ask into each box, not fill it in. Next to it, draw the four-rung delegation ladder as a vertical scale and mark where the mentee's *stated* Reality (not an assumed default) places them.

**Staff-level framing:** At Staff scope, mentoring becomes a multiplier decision — a Staff engineer cannot mentor everyone with equal depth, so time splits between deep 1:1 coaching for one or two people and broader sponsorship, which scales further per unit of time. Staff engineers are also the ones who notice a team's growth is bottlenecked by an absent mentoring *culture* (fast solvers who never narrate their reasoning) and address it structurally, e.g., by making design reasoning visible in written RFCs.

## Warning Signs

- A team's only engineer able to debug a legacy system becomes unavailable, and no one else can make progress — the underlying cause is usually a mentoring gap (fixes made alone, quickly, for a long time, with no diagnostic reasoning ever transferred), not a documentation gap. Fix: pair on the next several fixes specifically to transfer the mental model, not just the immediate resolution.

## Related

- syllabus/19-leadership-staff/cross-team-influence-without-authority.md
- syllabus/20-interview-preparation/behavioral/07-mentoring-and-developing-others.md
