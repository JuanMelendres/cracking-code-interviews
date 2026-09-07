---
title: "Flashcards: Design Reviews and RFCs as an Organizational Practice"
slug: design-reviews-and-rfcs-as-organizational-practice
document_type: flashcard-deck
domain: 19-leadership-staff
topic_id: T-1905
canonical: ../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md
last_updated: 2026-09-07
---

# Flashcards: Design Reviews and RFCs as an Organizational Practice

**Canonical chapter:** [`syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md`](../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md)

## Card: RFC lifecycle states

**Prompt:**
What are the typical lifecycle states of an RFC, and why does naming the current state matter?

**Answer:**
Draft, under review, accepted or rejected, and later possibly superseded (popularized by processes like Rust's RFC process and Kubernetes' Enhancement Proposals). A document in "draft" invites broad, open-ended input; one "under review" needs targeted, decision-relevant feedback; one "accepted" is closed and should require a new proposal to reopen rather than indefinite re-litigation.

**Why it matters:**
Naming which state a document is in tells every reader what kind of feedback is actually useful right now, preventing both premature narrowing and endless relitigation.

**Common trap:**
Leaving a proposal's lifecycle stage unstated, so reviewers can't tell whether broad alternative approaches or narrow, decision-relevant feedback is appropriate.

**Related:**
[syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md](../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md)

## Card: Explicit decision rights

**Prompt:**
What happens to a review process when "who can actually block this versus who can only comment" is never stated explicitly?

**Answer:**
Reviewers behave as if they all have equal veto power, which produces unbounded review cycles and bikeshedding.

**Why it matters:**
Decision rights being explicit — not assumed — is the single structural fix underneath most of this chapter's other practices (deadlines, settled-vs-open scope).

**Common trap:**
Running a review with no stated decision owner, so nobody knows when the review has legitimately concluded.

**Related:**
[syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md](../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md)

## Card: Rubber-stamping and bikeshedding share a cause

**Prompt:**
What single underlying cause produces both rubber-stamping and bikeshedding, and what fixes both?

**Answer:**
Unclear decision rights and unclear review scope. Rubber-stamping happens when reviewers don't feel real ownership of the outcome, so they approve without scrutiny; bikeshedding happens when reviewers do feel entitled to comment but have no signal for which parts warrant deep scrutiny. Both are fixed by the same intervention: the proposal owner explicitly stating which decisions are open for debate and which are settled.

**Why it matters:**
It reframes two seemingly opposite review failures as one diagnosable, one-fix problem rather than two unrelated culture issues.

**Common trap:**
Treating bikeshedding as a personality problem with a specific reviewer rather than a missing settled-vs-open signal from the proposal owner.

**Related:**
[syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md](../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md)

## Card: Async written review vs. synchronous meetings

**Prompt:**
What bias does asynchronous, written review remove that a synchronous review meeting has, and at what cost?

**Answer:**
A synchronous meeting has a hard attention-capacity limit and is heavily influenced by whoever speaks first, most confidently, or most senior (anchoring/seniority bias). Written async review removes speaking-order and confidence-signal effects — every comment has equal visual weight — but loses the fast back-and-forth clarification a live conversation provides.

**Why it matters:**
This is why many effective processes (e.g., Google's internal design-doc culture) use written review for the bulk of substantive feedback and reserve meetings only for resolving specific disagreements async hasn't converged on.

**Common trap:**
Defaulting to a synchronous meeting as the primary review forum for every proposal, which doesn't scale past the room's attention capacity and favors the most senior or confident voice.

**Related:**
[syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md](../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md)

## Card: "Disagree and commit"

**Prompt:**
What does "disagree and commit" require, beyond participants executing a decision they argued against?

**Answer:**
It requires that dissenters' disagreement was genuinely heard *during* the review, not simply overridden. It is the mechanism that lets an organization actually finish making decisions rather than reopening every one indefinitely — but treating it as license to override disagreement without hearing it first converts a legitimate norm into a justification for ignoring real objections.

**Why it matters:**
A Staff engineer who keeps relitigating a decision they lost, after it was made through a legitimate process, undermines the norm's credibility for everyone else more than a junior engineer doing the same.

**Common trap:**
Using "disagree and commit" to shut down objections that were never genuinely heard in the first place, rather than only after a fair process concluded.

**Related:**
[syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md](../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md)

## Card: Timeboxing a review

**Prompt:**
What problem does a stated feedback deadline solve that an open-ended review period doesn't?

**Answer:**
A timeboxed review forces stakeholders who care about the outcome to engage within the window, and gives the proposal owner a legitimate basis for moving forward once it closes — without either waiting forever for total consensus or feeling entitled to ignore late, legitimate objections.

**Why it matters:**
Combined with review-cycle latency compounding like code-review latency, an unbounded review adds invisible calendar time before implementation can even begin.

**Common trap:**
Treating the deadline so rigidly that a genuinely new, load-bearing risk raised late is dismissed just because it arrived after the cutoff — the deadline is meant to prevent low-value relitigation, not suppress a real risk discovered late.

**Related:**
[syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md](../syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md)
