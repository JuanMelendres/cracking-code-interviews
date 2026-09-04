---
title: "Mentoring and Developing Others"
slug: mentoring-and-developing-others
document_type: syllabus-topic
domain: 19-leadership-staff
topic_id: T-1901
status: draft
version: 1.0
last_updated: 2026-09-04
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites: []
related:
  - cross-team-influence-without-authority.md
  - ../20-interview-preparation/behavioral/07-mentoring-and-developing-others.md
practice: []
production_scenarios: []
interview_paths: [senior-to-staff]
official_references: []
source_history: []
---

# Mentoring and Developing Others

This is the first canonical chapter in `19-leadership-staff`, assigned **T-1901** in the plan's reserved `T-1900`–`T-1999` range for this domain. It is the *engineering-practice* counterpart to [Mentoring and Developing Others](../20-interview-preparation/behavioral/07-mentoring-and-developing-others.md) in `20-interview-preparation/behavioral/`: that chapter teaches how to turn a mentoring experience into a STAR interview answer; this chapter teaches the underlying skill itself — how to actually run a 1:1, calibrate feedback, delegate a stretch assignment, and decide when to intervene versus when to let someone struggle — independent of whether it ever becomes an interview story. Nothing in Sections 3–13 below duplicates that chapter; the interview-answer chapter assumes this chapter's skill and focuses only on narrating it.

## 1. Why This Matters

Mentoring is one of the few Staff-level responsibilities that scales an engineer's impact beyond their own individual output — a Staff engineer who develops three other engineers into stronger, more independent contributors has a larger organizational effect than the same engineer spending that time writing more code alone. It is also a skill with a real failure mode in both directions: an engineer who never delegates or teaches becomes an organizational bottleneck (nothing ships without them), while one who delegates without calibration either over-directs (removing all the learning value) or under-supports (setting someone up to fail). Interviewers ask about it separately from general leadership questions because it tests a specific, teachable competency, not just seniority by tenure.

## 2. Prerequisites

None — this is a foundational people-practice topic assumed by everything else in this domain.

## 3. Foundation (L1)

**Mentoring is the practice of deliberately helping another engineer grow their skills and judgment**, as distinct from simply working alongside them or reviewing their output. It differs from *management* in that a mentor typically has no formal authority over the other person's role, compensation, or performance rating — the relationship works only if the mentee finds it genuinely useful, not because it's mandated.

**Sponsorship is a related but distinct practice: advocating for someone's advancement when they are not in the room**, as opposed to mentorship's coaching-when-together focus. Research on career advancement (notably Herminia Ibarra's work distinguishing the two) finds sponsorship — someone with influence naming you for a stretch project, a promotion case, or a visible opportunity — often matters more for advancement than mentorship alone, and the two are frequently conflated in casual usage.

## 4. Core Concepts (L2)

**The GROW model** (Goal, Reality, Options, Will — from coaching literature, notably John Whitmore's *Coaching for Performance*) structures a developmental conversation around what the mentee wants to achieve, their honest current state, the options available to close that gap, and their actual commitment to a next step — rather than the mentor supplying a solution unprompted. Its core discipline is asking before telling: a mentor who immediately hands over their own answer denies the mentee the chance to develop the underlying judgment themselves.

**The SBI feedback model** (Situation, Behavior, Impact — from the Center for Creative Leadership) structures feedback around a specific situation, the observable behavior in it, and its concrete impact, rather than a general trait judgment ("you're not detail-oriented" vs. "in yesterday's incident review, the root-cause section didn't cite the log evidence, which meant a second re-investigation was needed to confirm it"). The second form is actionable; the first invites defensiveness because it attacks identity rather than describing an event.

**Delegation exists on a spectrum, not as a binary.** A useful frame (adapted from *The Manager's Path* by Camille Fournier and widely used delegation-ladder models): "do exactly this," "propose a plan, I'll approve it," "keep me informed after you decide," and "full autonomy, tell me only if something changes materially." Matching the rung to the person's actual current competence in that specific task — not their general seniority — is the calibration skill; delegating at the wrong rung either smothers a capable person or abandons someone not yet ready.

## 5. How It Works Internally (L3)

**The zone of proximal development** (a concept from developmental psychology, Lev Vygotsky) describes the gap between what someone can do unaided and what they can do with the right amount of support — the productive mentoring zone is inside that gap, not below it (assigning only what they can already do, which teaches nothing) and not above it (assigning something requiring skills they don't yet have any scaffolding for, which produces failure without learning). A stretch assignment calibrated correctly sits precisely in this zone: hard enough that the mentee must grow to succeed, supported enough that failure is a recoverable, informative event rather than a costly one.

**Letting someone struggle productively is a deliberate act, not passive neglect.** The internal judgment call is distinguishing a struggle that is *building* the skill (the mentee is making genuine forward progress, just slower than the mentor would) from one that is *stalling* it (the mentee is stuck on a wrong mental model and will not converge without an intervention). The signal that separates the two is trajectory over time, not the current difficulty snapshot — checking in at a fixed cadence rather than only when asked is what surfaces a stall early enough to correct cheaply.

**Feedback timing has a real decay curve.** Feedback delivered immediately after the observed behavior is both easier to make concrete (SBI's "Situation" is fresh and specific) and more actionable (the mentee can still apply it to the very next similar situation); feedback saved for a quarterly review is harder to make specific and arrives too late to change the behavior it's about. This is the mechanical reason "give feedback promptly" outperforms "save it for the scheduled 1:1," even though the latter feels lower-friction in the moment.

## 6. Practical Usage

- **Run 1:1s with a standing structure that leaves room for the mentee's agenda first** — a 1:1 that is entirely status-update-driven from the mentor's side crowds out the developmental conversation it should also be making room for.
- **Apply SBI (Section 4) to both corrective and reinforcing feedback** — reinforcing feedback ("that risk you caught in review last week prevented a real production issue") benefits from the same specificity as corrective feedback, and is under-delivered by default in most teams.
- **State the delegation rung explicitly** (Section 4) when handing off a task — "propose a plan and I'll review it before you start" removes the ambiguity that otherwise causes either under- or over-checking-in.

## 7. Examples

A worked GROW conversation outline, applied to a mentee who wants to lead their first design review as primary author:

```
Goal:    "I want to run point on my first RFC, not just contribute comments on others'."
Reality: "I've reviewed and commented on four RFCs, but never structured one from scratch,
          and I'm unsure how to scope the Options section without it becoming a laundry list."
Options: "You could: (a) shadow me writing the next one and narrate my choices out loud,
          (b) draft one solo with a review checkpoint after just the Context section,
          (c) draft the full thing and get end-to-end feedback."
Will:    "Option (b) — draft the Context section, review it with you before I go further,
          so I don't invest a full draft down a wrong framing."
```

This is a delegation-rung-2 outcome ("propose a plan, I'll approve it," Section 4), chosen deliberately over rung-1 (shadowing, too low for someone who has already reviewed four RFCs) and rung-4 (full autonomy, too high for someone's literal first attempt) — the mentee's own stated reality in the conversation is what places them correctly on the ladder, not an assumption made in advance.

## 8. Common Mistakes

- **Giving the answer instead of asking the question** — collapses the GROW conversation into the mentor doing the mentee's thinking, which produces a correct short-term outcome but no durable skill transfer.
- **Feedback as trait judgment rather than SBI-structured** ("you need to be more proactive") — vague enough that the recipient cannot identify a concrete next action.
- **Delegating at a fixed rung regardless of the specific task** — treating a senior mentee as rung-4-for-everything ignores that competence is task-specific, not a single global level.
- **Confusing "being helpful" with "being available for every question immediately"** — a mentor who removes every obstacle the instant it appears prevents the productive-struggle zone (Section 5) from ever operating.

## 9. Edge Cases

- **A mentee who consistently asks for the answer rather than working through Options themselves** — worth naming directly rather than continuing to supply answers by default, since it's often a learned pattern from a previous, more directive manager rather than a fixed trait.
- **A mentoring relationship where the mentor has less domain-specific context than the mentee** (common in cross-functional or cross-team mentoring) — GROW still works, since it doesn't require the mentor to already know the answer; the mentor's value is the structure of the conversation, not superior domain knowledge.
- **Feedback about something outside the mentee's control** (a team-level process failure, not an individual one) — SBI's Impact component still applies, but the intervention target shifts from the individual to the process itself; see [Cross-Team Influence Without Authority](cross-team-influence-without-authority.md) for changing something you don't personally own.

## 10. Performance Implications

Not applicable in the runtime sense; the equivalent "performance" claim here is organizational throughput — a team where senior engineers successfully delegate rung-appropriate work (Section 4) distributes its total capacity across more people capable of independent, correct decisions, rather than routing every non-trivial decision through the same one or two people, which is the concrete mechanism behind a single senior engineer becoming an availability bottleneck for an entire team.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| High delegation rung (full autonomy) | Fastest to execute if the mentee is ready; strongest growth signal | Real risk of a costly failure if miscalibrated above actual readiness |
| Low delegation rung (do exactly this) | Lowest immediate risk | Little to no growth value; mentor remains the bottleneck |
| Immediate feedback | Most actionable, best decay-curve outcome (Section 5) | Can feel abrupt without an established trust baseline |
| Saved-for-1:1 feedback | Lower social friction in the moment | Arrives too late to change the specific behavior it's about |

## 12. Senior-Level Considerations (L3)

A Senior engineer mentoring another individual contributor calibrates delegation rung and feedback timing (Sections 4–5) per task, and recognizes the difference between productive struggle and a stall (Section 5) early enough to intervene cheaply rather than after a deadline has already slipped. The Senior-level failure mode to watch for in oneself is under-delegating: an engineer who has learned to solve problems fast alone often (without deciding to) keeps solving them alone rather than developing someone else's ability to solve the next one.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, mentoring becomes a multiplier decision, not a per-person one: a Staff engineer typically cannot mentor everyone on a growing team with equal depth, and part of the actual skill is choosing where mentoring time produces the largest organizational return — often a mix of direct mentoring for one or two people and *sponsorship* (Section 3) for a wider set, since sponsorship scales further per unit of the sponsor's time than deep 1:1 coaching does. A Staff engineer is also frequently the one who notices a team's growth is bottlenecked by the *absence* of a mentoring culture at all (senior engineers who solve problems fast but never explain their reasoning aloud), and addressing that systemic gap — for instance, by making design reasoning visible in written RFCs (see [Design Reviews and RFCs as an Organizational Practice](design-reviews-and-rfcs-as-organizational-practice.md)) — has a larger effect than any single 1:1 relationship.

## 14. Production Scenarios

No existing `production-cookbook/` entry documents a people-development incident (the cookbook's entries are technical-incident postmortems by design). A representative scenario, illustrative rather than a literally observed incident, following this repository's own labeling convention:

*A team's only engineer capable of debugging a particular legacy payment-reconciliation job became unavailable during an incident, and no one else on the team could make progress on it for several hours. The underlying cause was not a documentation gap but a mentoring gap: that engineer had fixed similar issues alone, quickly, for over a year, without ever narrating the diagnostic process to anyone else — every fix was correct, but no transferable judgment was built in the rest of the team. The team's subsequent fix was procedural: the next three fixes to that job were done as paired debugging sessions specifically to transfer the diagnostic mental model, not just the immediate fix.*

> Planned reference: a dedicated `production-cookbook/` entry for this exact scenario, once one occurs and can be documented from a real, specific incident rather than this representative illustration.

## 15. Interview Questions

### Question 1 — How do you decide how much autonomy to give someone you're mentoring on a task?

**Why interviewers ask it.** It tests whether the candidate has an actual calibration framework (Section 4's delegation ladder) or delegates uniformly regardless of the specific person and task.

**Expected answer.** Names something equivalent to the delegation-rung spectrum (do exactly this / propose a plan / keep me informed / full autonomy) and states that the right rung depends on the person's demonstrated competence at *that specific task*, not their general seniority, gathered by asking rather than assuming.

**Minimum acceptable answer.** States that autonomy should match the person's skill level, even without a named framework.

**Strong Senior answer.** Gives a concrete example of choosing a specific rung and why, and describes checking in at the chosen rung's appropriate cadence rather than either micromanaging or disappearing.

**Staff-level extension.** Connects this to Section 13 — recognizes that at scale, direct calibrated delegation cannot cover everyone, and that building a team culture where engineers *self-calibrate* their own request for support (asking for a checkpoint before diving in) has a larger multiplier effect than the mentor doing all the calibration themselves.

**Common mistakes.** Answering only "I trust my team," which sidesteps the actual calibration judgment being asked about.

**Follow-up questions.** "Tell me about a time you delegated at the wrong level — too high or too low. What signal told you, and what did you do?"

### Question 2 — How do you give feedback that's actually actionable, not just true?

**Why interviewers ask it.** Tests whether the candidate has an actual structure (SBI, Section 4) for feedback, versus giving accurate-but-vague trait judgments that the recipient can't act on.

**Expected answer.** Describes structuring feedback around a specific situation, the observed behavior, and its concrete impact — rather than a general characterization — and delivering it close to the event rather than saving it for a scheduled review (Section 5's decay-curve argument).

**Minimum acceptable answer.** States that feedback should be specific and timely, even without naming SBI explicitly.

**Strong Senior answer.** Gives a real or realistic before/after example contrasting a vague trait statement with an SBI-structured equivalent, and explains why the second is more actionable.

**Staff-level extension.** Discusses calibrating feedback *frequency and forum* at a team level — e.g., normalizing reinforcing feedback (not just corrective), since most teams under-deliver it by default (Section 6) — as a deliberate culture-shaping act, not just an individual habit.

**Common mistakes.** Conflating "honest" with "actionable" — brutally honest but vague feedback is not more useful than gentle vague feedback; specificity, not bluntness, is the actual lever.

**Follow-up questions.** "How do you deliver corrective feedback to someone more senior than you, or someone in a different reporting line?" (Section 9 — the SBI structure still applies; the delivery channel and tone typically need more care, not a different framework.)

## 16. Coding/Practice Exercises

- Take a piece of vague feedback you've given or received recently ("be more proactive," "this needs more polish") and rewrite it in SBI form: a specific situation, the observed behavior, and its concrete impact.
- For a task you're currently delegating (or would like to), write down which of the four delegation rungs (Section 4) you're actually operating at, and whether that matches the other person's demonstrated competence at that specific task — not their general seniority.

## 17. Debugging Exercises

**Symptom:** a mentee you delegated a well-scoped task to has gone quiet for several days past the point you expected an update.

**Diagnose:** this is Section 5's stall-vs-struggle judgment made concrete — the fact pattern alone (silence past an expected checkpoint) doesn't tell you which one it is, and assuming either without checking is the actual mistake. Check in directly rather than waiting further or immediately taking the task back; the check-in's purpose is to determine whether they're converging slowly (productive struggle — needs only encouragement and maybe more time) or stuck on a wrong mental model (a stall — needs a specific unblocking conversation, not more time).

## 18. Design Exercises

**Design constraint:** design a lightweight mentoring structure for a team of 6 engineers spanning two full seniority bands (two new-grad hires, four experienced engineers), where formal 1:1 time is limited to 30 minutes weekly per pairing.

Design the structure around this chapter's two core levers explicitly: a standing GROW-style agenda item in each 1:1 (Section 4) so the mentee's own stated goal drives the conversation rather than only status updates, and an explicit, task-specific delegation-rung negotiation (Section 4) at the start of each significant piece of work rather than an assumed default. State the real trade-off: with only 30 minutes weekly, the design should favor a small number of well-calibrated stretch assignments (Section 5's zone-of-proximal-development target) over broad, shallow oversight of everything the mentee touches — depth on the highest-leverage task beats breadth across all of them at this time budget.

## 19. Further Reading

- *Coaching for Performance*, John Whitmore — the original source of the GROW model referenced in Section 4.
- *The Manager's Path*, Camille Fournier — delegation-ladder and growth-conversation concepts referenced in Sections 4 and 12.
- Center for Creative Leadership's SBI (Situation-Behavior-Impact) feedback model, referenced in Section 4.
- [Mentoring and Developing Others](../20-interview-preparation/behavioral/07-mentoring-and-developing-others.md) — the interview-application sibling to this chapter; assumes this chapter's skill and focuses on narrating it as a STAR story.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain what mentoring is, how it differs from sponsorship, and why both matter for advancement | [Section 3](#3-foundation-l1) |
| L2 | Apply the GROW conversation structure and the SBI feedback model, and state a delegation rung explicitly when handing off work | [Section 7's worked example](#7-examples), [Interview Question 2](#question-2--how-do-you-give-feedback-thats-actually-actionable-not-just-true) |
| L3 | Distinguish productive struggle from a stall, and explain why immediate feedback beats saved-for-review feedback | [Section 5](#5-how-it-works-internally-l3), [Debugging Exercise](#17-debugging-exercises) |
| L4 | Reason about mentoring as an organizational multiplier decision, and design a mentoring structure calibrated to real time constraints | [Section 13](#13-staffsystem-level-considerations-l4), [Design Exercise](#18-design-exercises) |
