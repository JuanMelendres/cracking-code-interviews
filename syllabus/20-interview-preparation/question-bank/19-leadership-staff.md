---
title: "Interview Question Bank — 19-leadership-staff"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../19-leadership-staff/INDEX.md
  - 18-engineering-practices.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Leadership and Staff Practice

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 7 chapters yielded 12 deep questions = **12 real
questions**. No Junior Fundamentals chapter exists in this domain — this is
inherently Senior/Staff-scoped material. All 7 chapters use the older numbered
`## 15. Interview Questions` template with **no Flashcards section at all**, so
there is no lighter quick-fire layer to mine here, unlike most other domains.

---

## Cross-Team Influence Without Authority

### Q1 — Tell me about a time you needed another team to change something, and they didn't report to you.

**Canonical treatment:** [§15, Q1](../../19-leadership-staff/cross-team-influence-without-authority.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Describes a story that is really about escalating to a shared manager — the common mistake this question targets, which demonstrates access to authority rather than influence without it.
- **Senior:** Explicitly separates identifying the right decision-maker from persuading them, and articulates what the other team actually valued in the exchange.
- **Staff:** Connects the specific instance to a broader pattern — has this negotiation recurred, and if so, was a structural fix pursued instead of repeating the same ad hoc negotiation each time.

### Q2 — How do you get buy-in for a proposal from a team whose priorities conflict with yours?

**Canonical treatment:** [§15, Q2](../../19-leadership-staff/cross-team-influence-without-authority.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Claims every conflict was resolved through persuasion alone — the common mistake this question targets, an implausible answer at Staff level.
- **Senior:** Gives or constructs a specific example distinguishing "we found a genuine win-win" from "we escalated the trade-off explicitly, with both sides' costs stated honestly."
- **Staff:** Discusses designing the org structure or interaction mode to prevent the same structural conflict from recurring, rather than resolving each instance individually.

---

## Design Reviews and RFCs as an Organizational Practice

### Q1 — How would you design a design-review process for a team that currently has none?

**Canonical treatment:** [§15, Q1](../../19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes a heavyweight process uniformly for every decision — the common mistake this question targets, trading latency for a false sense of rigor on low-stakes proposals.
- **Senior:** Explains why each element (decision rights, settled-vs-open scope, a deadline) addresses a specific failure mode, rather than listing them as an unmotivated checklist.
- **Staff:** Discusses calibrating the process by decision stakes — a lightweight version for low-risk proposals, a more rigorous one for high-risk or cross-team decisions.

### Q2 — Tell me about a review process that wasn't working well, and what you changed.

**Canonical treatment:** [§15, Q2](../../19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Describes a fix that was "we all agreed to try harder," with no structural change to decision rights, scope, or timeboxing — the common mistake this question targets.
- **Senior:** Explicitly connects the observed symptom to a named failure mode (rubber-stamping, bikeshedding, unbounded cycles) and the specific structural fix that addressed the actual cause.
- **Staff:** Discusses how the fix was rolled out as a durable team norm rather than a one-off intervention, and whether it required visibly modeling the new norm to take hold.

---

## Hiring and Team Building

### Q1 — You notice two interviewers on your team give wildly different scores to candidates for the same question. What do you do?

**Canonical treatment:** [§15, Q1](../../19-leadership-staff/hiring-and-team-building.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the fix is simply telling interviewers to "align more," without a concrete mechanism — the common mistake this question targets.
- **Senior:** Correctly proposes a real calibration session — both interviewers independently score sample answers, then converge on a shared, specific, written rubric.
- **Staff:** Connects this to a broader, ongoing calibration cadence for the whole interviewer pool, and to revising the rubric itself if the disagreement reveals it's genuinely ambiguous.

---

## Incident Command: Roles and Real-Time Coordination

### Q1 — You're the most senior engineer on a call during a major outage. What do you actually do first?

**Canonical treatment:** [§15, Q1](../../19-leadership-staff/incident-command-roles-and-real-time-coordination.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Immediately starts debugging personally without addressing coordination at all — the common mistake this question targets, a real anti-pattern.
- **Senior:** Correctly names the Incident Commander role and explicitly separates it from personally debugging, and confirms a Communications lead.
- **Staff:** Connects this to severity-based role activation (not every incident needs this) and establishing it as a standing organizational convention rather than reinventing it live.

---

## Leading Migrations and Large-Scale Technical Change

### Q1 — Tell me about a migration or large technical change you led. What made it hard, and how did you keep it moving?

**Canonical treatment:** [§15, Q1](../../19-leadership-staff/leading-migrations-and-large-technical-change.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Tells a story that is entirely about the technical migration pattern (dual-write, cutover), with no account of cross-team prioritization or communication work — the common mistake this question targets.
- **Senior:** Explicitly separates the technical plan from the organizational execution challenge, and names a concrete tool (a tracking dashboard, a stated sequencing rationale) used to manage it.
- **Staff:** Discusses how the contract-phase forcing function was secured, ideally before the migration started, and whether continued justification was re-validated partway through.

### Q2 — A migration you're driving has stalled at 90% complete. What do you do?

**Canonical treatment:** [§15, Q2](../../19-leadership-staff/leading-migrations-and-large-technical-change.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes to simply escalate immediately without first diagnosing whether the blocker is prioritization or genuine resourcing — the common mistake this question targets.
- **Senior:** Names the specific forcing-function mechanism (a firm deprecation date, visible comparative status, leadership-backed mandate) and explains why it works where repeated asking hasn't.
- **Staff:** Discusses planning for this exact moment before the migration starts — securing the deprecation date up front, when there's no concrete caller yet resisting it.

---

## Mentoring and Developing Others

### Q1 — How do you decide how much autonomy to give someone you're mentoring on a task?

**Canonical treatment:** [§15, Q1](../../19-leadership-staff/mentoring-and-developing-others.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Answers only "I trust my team," which sidesteps the actual calibration judgment being asked about — the common mistake this question targets.
- **Senior:** Names something equivalent to the delegation-rung spectrum and states the right rung depends on the person's demonstrated competence at that specific task, gathered by asking rather than assuming.
- **Staff:** Recognizes that at scale, direct calibrated delegation cannot cover everyone, and that building a team culture where engineers self-calibrate their own request for support has a larger multiplier effect.

### Q2 — How do you give feedback that's actually actionable, not just true?

**Canonical treatment:** [§15, Q2](../../19-leadership-staff/mentoring-and-developing-others.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Conflates "honest" with "actionable" — the common mistake this question targets; specificity, not bluntness, is the actual lever.
- **Senior:** Gives a real or realistic before/after example contrasting a vague trait statement with an SBI-structured equivalent (Situation, Behavior, Impact).
- **Staff:** Discusses calibrating feedback frequency and forum at a team level — normalizing reinforcing feedback, not just corrective, since most teams under-deliver it by default.

---

## Technical Debt: Prioritization and Advocacy

### Q1 — Tell me about a time you advocated for paying down technical debt against competing priorities.

**Canonical treatment:** [§15, Q1](../../19-leadership-staff/technical-debt-prioritization-and-advocacy.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Tells a story that stops at "I convinced them it was important," with no account of the specific reframing or quantification that actually did the convincing — the common mistake this question targets.
- **Senior:** Explicitly contrasts a code-quality framing with the stakeholder-currency framing actually used, and names a specific measured number (delivery-time gap, incident rate).
- **Staff:** Discusses whether this was a one-off proposal or led to a structural change (a standing capacity allocation, or a fitness function preventing recurrence).

### Q2 — How do you decide which technical debt to pay down first, when you can't pay down all of it?

**Canonical treatment:** [§15, Q2](../../19-leadership-staff/technical-debt-prioritization-and-advocacy.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Prioritizes by whichever debt is currently most annoying to work around personally, rather than by measured cost and urgency — the common mistake this question targets.
- **Senior:** Applies Fowler's quadrant explicitly (distinguishing reckless-deliberate debt from prudent trade-offs) and gives a concrete example of each.
- **Staff:** Discusses portfolio-level prioritization across multiple teams' debt inventories and whether a standing capacity allocation is the right mechanism at that scale.

---

## Related

- [`18-engineering-practices.md`](18-engineering-practices.md)
- [`17-architecture.md`](17-architecture.md)
- [`16-performance-jvm.md`](16-performance-jvm.md)
- [`15-cloud.md`](15-cloud.md)
- [`14-devops-containers.md`](14-devops-containers.md)
- [`13-observability.md`](13-observability.md)
- [`12-security.md`](12-security.md)
- [`11-system-design.md`](11-system-design.md)
- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
