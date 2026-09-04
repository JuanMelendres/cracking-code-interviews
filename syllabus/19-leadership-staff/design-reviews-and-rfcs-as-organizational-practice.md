---
title: "Design Reviews and RFCs as an Organizational Practice"
slug: design-reviews-and-rfcs-as-organizational-practice
document_type: syllabus-topic
domain: 19-leadership-staff
topic_id: T-1905
status: draft
version: 1.0
last_updated: 2026-09-04
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - cross-team-influence-without-authority.md
related:
  - ../18-engineering-practices/architecture-decision-records-and-technical-writing.md
  - ../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md
  - ../20-interview-preparation/behavioral/12-design-reviews-and-rfcs.md
practice: []
production_scenarios: []
interview_paths: [senior-to-staff]
official_references: []
source_history: []
---

# Design Reviews and RFCs as an Organizational Practice

Assigned **T-1905** in this domain's reserved `T-1900`–`T-1999` range, closing this domain's initial five-topic knowledge ladder. This chapter is deliberately distinct from its two closest relatives, and the boundary is stated explicitly since all three are easy to conflate:

- [Architecture Decision Records and Technical Writing for Engineers](../18-engineering-practices/architecture-decision-records-and-technical-writing.md) owns *writing* a clear decision record — the ADR document format and its completeness, largely a **post-decision** artifact.
- [Trade-off Narration and Architecture Decision Records](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) owns narrating a trade-off decision *verbally*, in an interview, using the same four-beat structure.
- **This chapter** owns the **pre-decision organizational process** — how a review or RFC process should be designed and run, what makes a reviewer effective, who actually holds decision rights, and the process-level anti-patterns (rubber-stamping, bikeshedding, unbounded review cycles) that cause good decisions to take too long or bad ones to ship unchallenged. It is also the engineering-practice counterpart to [Design Reviews and RFCs](../20-interview-preparation/behavioral/12-design-reviews-and-rfcs.md), which teaches how to narrate a specific review-shaping story as a STAR interview answer.

## 1. Why This Matters

An organization's biggest technical decisions are made or unmade during the review process, not at the moment someone privately decides what to propose — which means the quality of the review process itself (who has genuine decision authority, whether feedback actually surfaces before commitment, how long the process takes to converge) is often a larger determinant of decision quality than any individual engineer's technical judgment. A Staff engineer's influence is disproportionately exercised through this process — shaping how reviews are run, not only participating well in a given one — which is why this is treated as a distinct organizational-practice topic, not merely an extension of writing a good document.

## 2. Prerequisites

[Cross-Team Influence Without Authority](cross-team-influence-without-authority.md) — a design review or RFC process frequently spans teams with no shared reporting line, and shaping someone else's proposal well is a direct application of that chapter's stakeholder-mapping and framing skills.

## 3. Foundation (L1)

**A design review is the process of one or more people examining a proposed technical approach before it's committed to**, distinct from a code review (which examines an already-chosen implementation) in that it can still change the fundamental approach, not just its execution details. An **RFC** ("Request for Comments," a term and lifecycle popularized publicly by processes such as Rust's RFC process and Kubernetes' Enhancement Proposals) is a specific, formalized instance of this: a written proposal that moves through explicit states — typically draft, under review, accepted or rejected, and later, possibly, superseded — rather than an informal conversation with no defined endpoint.

**Decision rights must be explicit, not assumed.** Every review process has an implicit answer to "who can actually block this, versus who can only comment" — when that answer isn't stated explicitly, reviewers behave as if they all have equal veto power, which produces exactly the unbounded-cycle and bikeshedding failure modes covered in Section 5.

## 4. Core Concepts (L2)

**RFC lifecycle states give a proposal a legible status at any point** — a proposal in "draft" invites broad, open-ended input; one "under review" is narrowing toward a decision and needs targeted, decision-relevant feedback rather than fresh alternative approaches; one "accepted" is a closed decision that should require a new proposal to reopen, not an indefinitely re-litigable one. Naming which state a document is in, explicitly, tells every reader what kind of feedback is actually useful right now.

**Timeboxing a review is what prevents "still gathering feedback" from becoming an indefinite state.** A review with a stated deadline for feedback (not an indefinite open comment period) forces stakeholders who care about the outcome to engage within the window, and gives the proposal owner a legitimate basis for moving forward once it closes, rather than either waiting forever for total consensus or feeling entitled to ignore late, legitimate objections.

**"Disagree and commit"** (a decision-making norm publicly associated with Amazon's leadership principles, though the underlying idea predates that specific naming) describes the expectation that once a decision is made through a legitimate process, participants who argued against it during review commit to executing it fully rather than continuing to relitigate it afterward — while still requiring that their disagreement was genuinely heard *during* the review, not simply overridden. This is the mechanism that lets an organization actually finish making decisions rather than re-opening every one indefinitely.

## 5. How It Works Internally (L3)

**Rubber-stamping and bikeshedding are opposite failures of the same underlying cause: unclear decision rights and unclear review scope.** Rubber-stamping happens when reviewers don't feel genuine ownership or accountability for the outcome, so they approve without real scrutiny; bikeshedding (spending disproportionate review time on a low-stakes, easy-to-have-an-opinion-about detail, such as a naming choice, while a genuinely load-bearing risk goes unexamined) happens when reviewers *do* feel entitled to comment but have no signal for which parts of a proposal actually warrant deep scrutiny versus which are the author's call. Both are fixed by the same intervention: an explicit statement, from the proposal's owner, of which specific decisions are actually open for debate and which are settled, directing reviewer attention where it has real leverage.

**Asynchronous, written review scales differently than synchronous meeting-based review.** A synchronous review meeting has a hard capacity limit (the room's collective attention for a bounded time) and is heavily influenced by whoever speaks first, most confidently, or most senior in the room — a well-documented dynamic sometimes called anchoring or seniority bias. Written, asynchronous review removes the speaking-order and confidence-signal effects (every comment has equal visual weight) and lets a reviewer take the time to actually think before responding, at the cost of losing the fast back-and-forth clarification a live conversation provides — which is why many effective processes (including Google's internal design-doc culture, publicly described in several external accounts) use written review for the bulk of substantive feedback and reserve a synchronous meeting only for resolving specific, already-surfaced disagreements that async comments haven't converged on.

**Review cycle time compounds the same way code-review latency does** (see [Code Review: Standards and Practice](../18-engineering-practices/code-review-standards-and-practice.md), Section 5): a design that requires three review rounds at a week each adds three weeks of calendar time before any implementation can begin, and that latency is often invisible in a project's stated timeline until it's already been spent.

## 6. Practical Usage

- **State the proposal's lifecycle stage and decision rights explicitly at the top of the document** (Section 3/4) — who is Accountable for the final call, and what stage of review this currently is.
- **Name which specific parts of the proposal are genuinely open for debate**, directing reviewer attention (Section 5) rather than leaving every paragraph equally contestable.
- **Set a stated feedback deadline** (Section 4) rather than an open-ended review period, and communicate what happens if a stakeholder doesn't engage within it.

## 7. Examples

A minimal RFC header applying Section 3/4/6's explicit-state and decision-rights principles:

```
Status:            Under review (feedback due by 2026-09-18)
Decision owner:    Platform team lead (final call on adoption timeline)
Open for debate:   Whether to support both the old and new auth token formats
                    simultaneously during a transition window, or require a
                    hard cutover
Considered settled: The choice of JWT as the token format itself (already
                    validated in a prior spike, referenced below)
```

This header does the concrete work Section 5 describes: a reviewer immediately knows not to relitigate the JWT choice (settled, and a relitigation there would be a real instance of Section 5's bikeshedding-on-the-wrong-detail failure), knows exactly which decision is actually open (the transition-window question), and knows who ultimately decides if reviewers disagree after the stated deadline.

## 8. Common Mistakes

- **No stated decision owner** — every reviewer behaves as if they have equal veto power, producing Section 5's unbounded-cycle failure mode.
- **No distinction between settled and open questions** — reviewer attention gets spent on whichever detail is easiest to have an opinion about, not whichever one actually carries risk (Section 5's bikeshedding).
- **An indefinite review window** with no stated deadline — the review never legitimately closes, and late objections from disengaged stakeholders can indefinitely reopen a near-final decision.
- **Treating "disagree and commit" as license to override disagreement without genuinely hearing it first** (Section 4) — this converts a legitimate decision-making norm into a justification for ignoring real, substantive objections.

## 9. Edge Cases

- **A reviewer who raises a genuinely new, load-bearing risk after the stated feedback deadline has passed** — the "disagree and commit" norm (Section 4) is not a rule that late-but-genuinely-important objections must be ignored; the deadline exists to prevent indefinite low-value relitigation, not to suppress a real risk discovered late. The judgment call is distinguishing a genuinely new risk from a previously-raised-and-settled objection being repeated.
- **A design review with no clear single decision owner because the decision genuinely spans two peer teams with equal stake** — this requires an explicit, agreed-upon tiebreaker named *before* the review starts (a shared manager, or a specific agreed process), not discovered only if the two teams actually disagree.
- **A synchronous meeting still needed despite a written-review-first process** — reserved, per Section 5, for specific disagreements that async comments have failed to converge on, not used as the default forum for every review.

## 10. Performance Implications

Not applicable in the runtime sense; the organizational equivalent is decision latency — Section 5's review-cycle-time compounding is the direct, measurable cost of an unclear-decision-rights, unbounded review process, and it's frequently invisible in a project's stated timeline until several review rounds have already been spent.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Explicit decision owner named up front | Prevents unbounded relitigation, clear accountability | Requires the organization to actually agree on who that is, sometimes a hard conversation itself |
| Written, asynchronous review as the default | Removes speaking-order/seniority bias, scales past meeting-room capacity | Slower clarification loop than a live conversation |
| Timeboxed feedback window | Forces engagement, prevents indefinite "still gathering input" | Risk of missing a genuinely late but important objection if the deadline is treated too rigidly |
| Synchronous meeting as the default forum | Fast clarification, immediate resolution | Hard capacity limit, seniority/confidence bias, doesn't scale across many stakeholders |

## 12. Senior-Level Considerations (L3)

A Senior engineer running their own design review states decision rights and lifecycle stage explicitly (Section 3/6), directs reviewer attention toward genuinely open questions rather than settled ones (Section 5), and sets a real feedback deadline. The Senior-level judgment call is recognizing, within a specific review, whether a stalled disagreement needs a synchronous conversation to resolve (Section 5/9) or is actually converging fine asynchronously and just needs more time.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, an engineer is frequently the one *designing* the review process itself for a team or organization, not only running individual reviews within an existing one — deciding, for the organization, the default balance between async written review and synchronous meetings (Section 5), what triggers a mandatory design review versus what a team can decide informally, and how decision ownership is assigned for cross-team proposals with no obvious single owner (Section 9). A Staff engineer is also positioned to notice a systemic pattern of review dysfunction across many individual reviews — chronic bikeshedding, or a pattern of decisions being quietly re-litigated after being marked accepted — and address the structural cause (making decision rights and lifecycle states genuinely explicit as a team norm, not a single review's fix) rather than repeatedly correcting the symptom review by review. Staff engineers should also model the "disagree and commit" norm (Section 4) visibly themselves, since a Staff engineer who continues relitigating a decision they lost during review, after it has been made through a legitimate process, undermines the norm's credibility for everyone else far more than a more junior engineer doing the same would.

## 14. Production Scenarios

No existing `production-cookbook/` entry documents a review-process governance failure directly (the cookbook's entries are technical-incident postmortems by design, and [Code Review: Standards and Practice](../18-engineering-practices/code-review-standards-and-practice.md) already cites the closest existing entry — a review that failed to catch an unsubstantiated claim before it became load-bearing). A representative scenario, illustrative rather than a literally observed incident, following this repository's own labeling convention:

*An RFC proposing a new caching layer sat in "under review" for six weeks with no stated deadline and no named decision owner, accumulating stylistic comments about naming and file structure while a genuinely load-bearing question — what happens to cache consistency during a rolling deployment — went unaddressed, since no comment had signaled that question as the one that actually mattered. The proposal was eventually revised to name an explicit decision owner and mark the naming questions as settled, at which point the deployment-consistency question was raised and resolved within days — the underlying technical concern had been raisable from week one; nothing about the review's structure had directed attention to it until decision rights and settled-versus-open scope were made explicit.*

> Planned reference: a dedicated `production-cookbook/` entry for this exact scenario, once one occurs and can be documented from a real, specific incident rather than this representative illustration.

## 15. Interview Questions

### Question 1 — How would you design a design-review process for a team that currently has none?

**Why interviewers ask it.** Tests whether the candidate thinks about review as a process to be deliberately designed (decision rights, lifecycle states, Sections 3–4) or only knows how to participate well in an existing one.

**Expected answer.** Names an explicit decision-owner convention, a stated lifecycle (draft/under review/accepted), a default preference for written async review with synchronous meetings reserved for specific unresolved disagreements (Section 5), and a stated feedback deadline.

**Minimum acceptable answer.** Proposes some structure beyond "have a meeting and discuss it," even without naming every element above.

**Strong Senior answer.** Explains *why* each element addresses a specific failure mode (decision rights prevent unbounded relitigation; settled-vs-open scope prevents bikeshedding; a deadline prevents indefinite review) rather than listing them as an unmotivated checklist.

**Staff-level extension.** Discusses calibrating the process by decision stakes (Section 13) — a lightweight version for low-risk proposals, a more rigorous one requiring explicit sign-off for high-risk or cross-team decisions — rather than one uniform process for every proposal regardless of scale.

**Common mistakes.** Proposing a heavyweight process uniformly for every decision, which trades Section 10's latency cost for a false sense of rigor on low-stakes proposals that didn't need it.

**Follow-up questions.** "How do you handle a reviewer who keeps raising already-settled questions?" (Section 6/8 — the settled-vs-open distinction, stated explicitly, is the direct fix.)

### Question 2 — Tell me about a review process that wasn't working well, and what you changed.

**Why interviewers ask it.** Tests real, specific diagnostic experience with the failure modes in Section 5 (rubber-stamping, bikeshedding, unbounded cycles), not just abstract process knowledge.

**Expected answer.** Names a specific, observed symptom (a review that dragged on for weeks, or one where obviously wrong proposals kept getting approved without real scrutiny), traces it to a specific structural cause (no decision owner, no settled-vs-open distinction, no deadline), and describes the specific structural change made — not just "we tried to be more careful."

**Minimum acceptable answer.** Describes a real process problem and some improvement made, even without an explicit causal diagnosis.

**Strong Senior answer.** Explicitly connects the observed symptom to one of Section 5's named failure modes and the specific structural fix that addressed the actual cause.

**Staff-level extension.** Discusses how the fix was rolled out as a durable team norm (Section 13) rather than a one-off intervention on a single review, and whether it required modeling the new norm visibly (e.g., disagree-and-commit, Section 4) to actually take hold.

**Common mistakes.** A story where the fix was "we all agreed to try harder," with no structural change to decision rights, scope, or timeboxing — this rarely holds up under a Staff-level follow-up about what specifically changed.

**Follow-up questions.** "How did you get buy-in for changing an established process people were used to?" (a direct application of [Cross-Team Influence Without Authority](cross-team-influence-without-authority.md).)

## 16. Coding/Practice Exercises

- Take a real or hypothetical proposal you're reviewing or writing, and add an explicit header (Section 7's format): decision owner, lifecycle stage, and which specific questions are open versus settled.
- Observe (or recall) a real review conversation and classify each comment as addressing a genuinely open, load-bearing question versus a settled or low-stakes one — estimate what fraction of total review time was spent on each category.

## 17. Debugging Exercises

**Symptom:** a proposal has been "under review" for over a month, with dozens of comments, but no clear sense of whether it's converging toward a decision.

**Diagnose:** this is Section 5's unbounded-cycle failure mode made concrete. Check first whether a decision owner has ever been explicitly named — if every stakeholder believes they hold equal veto power, convergence has no defined endpoint by construction. A second check: whether the comments are concentrated on a small number of genuinely load-bearing open questions, or scattered across many low-stakes ones (Section 5's bikeshedding) — if the latter, the fix is an explicit settled-versus-open statement from the proposal owner, redirecting attention, not more review time.

## 18. Design Exercises

**Design constraint:** design a tiered design-review process for an engineering organization of 50 engineers, where a single uniform process (mandatory synchronous review for every change) has become a bottleneck, but removing review entirely for smaller decisions feels too risky.

Design the process around this chapter's two core levers explicitly: tier proposals by stakes (Section 13) — a lightweight, single-reviewer, async-only path for low-risk, single-team decisions; a full written-RFC path with a named decision owner, explicit settled-versus-open scope, and a stated feedback deadline (Sections 3–4, 6) for cross-team or high-risk decisions; and reserve synchronous meetings, in either tier, only for specific disagreements async comments haven't resolved (Section 5). State the real trade-off: tiering introduces a new judgment call (which tier a given proposal belongs in) that itself needs an explicit, simple rule (e.g., cross-team impact or irreversibility triggers the full path) rather than being left ambiguous, since an ambiguous tiering rule would recreate the same unclear-decision-rights problem this design is meant to solve, one level up.

## 19. Further Reading

- Rust's RFC process and Kubernetes Enhancement Proposals (KEPs) — public, well-documented examples of explicit RFC lifecycle states, referenced in Section 3–4.
- Publicly documented accounts of Google's internal design-doc culture — referenced in Section 5 for the async-review-plus-targeted-synchronous-meeting pattern.
- "Disagree and commit" — a decision-making norm publicly associated with Amazon's leadership principles, referenced in Section 4 and 13.
- [Architecture Decision Records and Technical Writing for Engineers](../18-engineering-practices/architecture-decision-records-and-technical-writing.md) — the written-artifact sibling to this chapter; owns the ADR document format this chapter's decisions eventually get recorded into.
- [Trade-off Narration and Architecture Decision Records](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) — the verbal-narration sibling for interview delivery.
- [Design Reviews and RFCs](../20-interview-preparation/behavioral/12-design-reviews-and-rfcs.md) — the interview-application sibling to this chapter.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain what a design review and an RFC lifecycle are, and why decision rights must be explicit | [Section 3](#3-foundation-l1) |
| L2 | Write an RFC header stating decision ownership, lifecycle stage, and settled-versus-open scope | [Section 7](#7-examples), [Practice Exercise](#16-codingpractice-exercises) |
| L3 | Diagnose rubber-stamping, bikeshedding, and unbounded review cycles to their shared structural cause | [Section 5](#5-how-it-works-internally-l3), [Debugging Exercise](#17-debugging-exercises) |
| L4 | Design a tiered, organization-scale review process and model "disagree and commit" credibly as a Staff engineer | [Section 13](#13-staffsystem-level-considerations-l4), [Design Exercise](#18-design-exercises) |
