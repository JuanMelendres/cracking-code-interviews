---
title: "Cheat Sheet: Design Reviews and RFCs as an Organizational Practice"
slug: design-reviews-and-rfcs-as-organizational-practice
document_type: cheat-sheet
domain: 19-leadership-staff
topic_id: T-1905
canonical: ../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md
last_updated: 2026-09-06
---

# Design Reviews and RFCs as an Organizational Practice

**Canonical chapter:** [`syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md`](../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md)

## Core Mental Model

An organization's biggest decisions are made or unmade during the review process, not at the moment someone drafts a proposal — so the quality of *how the review is run* (explicit decision rights, a stated lifecycle stage, a timeboxed window) is often a larger determinant of decision quality than any individual reviewer's technical judgment.

## Essential Definitions

- **Design review vs. RFC vs. code review** — a design review can still change the fundamental approach, not just execution details; an RFC is a formalized instance with explicit lifecycle states (draft, under review, accepted/rejected, superseded); a code review examines an already-chosen implementation.
- **Decision rights** — every review has an implicit answer to "who can actually block this, versus who can only comment"; left unstated, reviewers behave as if they all have equal veto power.
- **RFC lifecycle states** — a document's stage (draft / under review / accepted) tells every reader what kind of feedback is useful right now.
- **Timeboxing** — a stated feedback deadline forces engagement within the window and gives the owner a legitimate basis to move forward once it closes.
- **Disagree and commit** — participants who argued against a decision during a legitimate review process commit to executing it fully afterward, provided their disagreement was genuinely heard during review, not simply overridden.

## Decision Table — Diagnosing Review Dysfunction

| Failure mode | Root cause | Fix |
|---|---|---|
| Rubber-stamping | Reviewers lack genuine ownership/accountability for the outcome | Name an explicit decision owner with real stakes |
| Bikeshedding | Reviewers feel entitled to comment but have no signal on what warrants scrutiny | State explicitly which questions are open vs. settled |
| Unbounded review cycle | No stated decision owner or feedback deadline | Name the owner; set a real deadline |

## Common Pitfalls

- No stated decision owner — every reviewer behaves as if they hold equal veto power, producing an unbounded review cycle.
- No distinction between settled and open questions — attention goes to whichever detail is easiest to have an opinion about, not whichever one carries real risk.
- An indefinite review window with no stated deadline — the review never legitimately closes, and late objections from disengaged stakeholders can indefinitely reopen a near-final decision.
- Treating "disagree and commit" as license to override disagreement without genuinely hearing it first.

## Interview Answer Skeleton

**30-sec:** A design review works only when decision rights are explicit — who can actually block the proposal versus who can only comment — paired with a stated lifecycle stage and a real feedback deadline; without those, reviews either rubber-stamp or spin indefinitely.

**2-min:** Add the specific fix for the two opposite failure modes: rubber-stamping and bikeshedding are both caused by unclear decision rights and unclear scope, and both are fixed the same way — the proposal owner explicitly states which decisions are genuinely open for debate and which are already settled, directing reviewer attention where it has real leverage. Written, asynchronous review scales better than a synchronous meeting for the bulk of feedback (no speaking-order or seniority bias, equal visual weight per comment); reserve a synchronous meeting for specific disagreements async comments haven't converged on.

**Whiteboard:** Draw a minimal RFC header with four labeled lines — Status (with a feedback-due date), Decision owner, Open for debate, Considered settled — and say: "a reviewer reading only these four lines already knows not to relitigate the settled line and exactly who breaks a tie on the open one."

**Staff-level framing:** At Staff scope, an engineer is often designing the review process itself — the default balance between async and synchronous review, what triggers a mandatory review vs. an informal one, and how decision ownership is assigned when no obvious single owner exists for a cross-team proposal. Staff engineers should also model "disagree and commit" visibly themselves, since continuing to relitigate a decision they lost undermines the norm's credibility for everyone else far more than the same behavior from someone more junior.

## Warning Signs

- An RFC sits in "under review" for six weeks with no stated deadline and no named decision owner, accumulating stylistic comments about naming while a genuinely load-bearing question (e.g., cache consistency during a rolling deployment) goes unaddressed. Fix: name an explicit decision owner and mark the low-stakes questions settled — the load-bearing question then typically surfaces and resolves within days, since nothing about the technical concern was unraisable, only the review's structure never directed attention to it.

## Related

- syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md
- syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md
- syllabus/20-interview-preparation/behavioral/12-design-reviews-and-rfcs.md
