---
title: "Design Reviews and RFCs"
slug: design-reviews-and-rfcs
document_type: behavioral-handbook-chapter
domain: 20-interview-preparation/behavioral
status: draft
version: 1.0
last_updated: 2026-09-03
source_history:
  - behavioral-handbook/12-design-reviews-and-rfcs.md
difficulty:
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - 01-star-framework-and-delivery.md
  - 06-conflict-and-technical-disagreement.md
related:
  - ../technical-answers/trade-off-narration-and-adrs.md
official_references: []
---

# Design Reviews and RFCs

## Table of Contents

- [Learning Objectives](#learning-objectives)
- [Why This Matters in Interviews](#why-this-matters-in-interviews)
- [Mental Model: The Story Is About Shaping the Room, Not Writing the Document](#mental-model-the-story-is-about-shaping-the-room-not-writing-the-document)
- [The Design Review Story Structure](#the-design-review-story-structure)
- [Illustrative Example](#illustrative-example)
- [When You Weren't the Author](#when-you-werent-the-author)
- [Interview Question: "Tell me about a design review or RFC you drove or significantly shaped."](#interview-question-tell-me-about-a-design-review-or-rfc-you-drove-or-significantly-shaped)
- [Common Mistakes](#common-mistakes)
- [Self-Review Checklist](#self-review-checklist)
- [Summary](#summary)
- [Related](#related)

## Learning Objectives

After this chapter, you can tell a design-review story that demonstrates how you shaped a technical decision through the review process itself — not just that you wrote a good document — and you can tell this story credibly whether you were the RFC's original author or a reviewer who substantially changed its direction.

## Why This Matters in Interviews

Design reviews and RFC processes are where an organization's biggest technical decisions actually get made or unmade, and a candidate's behavior in that process — the quality of the questions they ask, whether they can identify the load-bearing risk in someone else's proposal, whether they can shift a room's consensus — is a strong, distinct signal from their individual technical output. This question is asked separately from architecture-decision questions (see [Architecture Trade-off Narration](05-architecture-trade-off-narration.md)) specifically because *reviewing and shaping* someone else's proposal is a different skill from *making* your own decision, and Staff-level engineers are expected to do both.

## Mental Model: The Story Is About Shaping the Room, Not Writing the Document

The technical skill of writing a clear RFC — the four-beat structure (Context, Options, Decision criterion, Cost) — is covered separately in [Trade-off Narration and Architecture Decision Records](../technical-answers/trade-off-narration-and-adrs.md); this chapter assumes that skill and focuses on the behavioral question actually being asked: what did you specifically do, in the review process, that changed the outcome. A weak answer to this question is really just a restatement of "I wrote a good document" or "I read someone else's document carefully" — neither demonstrates influence over the actual decision. A strong answer names the specific comment, question, or counter-proposal that measurably changed the direction of the design.

## The Design Review Story Structure

| STAR component | Design-review-specific content |
|---|---|
| Situation | What was being decided, what stage the process was at (was this the candidate's own RFC, or someone else's proposal under review) |
| Task | The candidate's specific role — RFC author, primary reviewer, a stakeholder whose team was affected |
| Action | The specific intervention — a question that surfaced a risk no one had considered, a counter-proposal, a request for a specific additional analysis before proceeding — and how that intervention changed the eventual outcome |
| Result | What actually shipped as a result, distinguishing what changed *because of* the candidate's specific contribution from what would have happened regardless |

## Illustrative Example

This example is illustrative — a representative scenario, not a real candidate's actual experience.

*"A colleague on another team proposed an RFC to replace our synchronous inter-service calls with an async event-driven pattern for order status updates, primarily motivated by reducing coupling. The proposal was well-written and the coupling argument was sound, but reading through the consumer list, I noticed the RFC didn't address what happens to a specific downstream reporting service that currently relies on synchronous calls specifically because it needs strong read-after-write consistency for a compliance report generated immediately after certain order state transitions — an async event pattern would introduce eventual consistency that the reporting logic wasn't built to handle. I raised this specifically as a comment on the RFC rather than a private message, framing it as a question — 'how does the reporting service's read-after-write requirement get satisfied under this design?' — rather than an objection, since I wanted to understand if the author had already considered and solved this, not assume they'd missed it. They hadn't considered that specific consumer. Rather than blocking the whole proposal, I proposed a hybrid: async events for every consumer except the reporting service, which would keep a synchronous path specifically for that one compliance-critical read, with a follow-up RFC scoped separately to evaluate whether the reporting service's consistency requirement could itself be relaxed later. The RFC shipped with that hybrid approach, and the follow-up RFC for the reporting service happened two quarters later, after the reporting team had time to evaluate whether eventual consistency was actually acceptable for their compliance use case — it wasn't, so the synchronous path is still there today, and the async migration for everything else shipped without an incident that would have been a genuine compliance problem if the original all-or-nothing proposal had gone through unmodified."*

## When You Weren't the Author

Not every strong design-review story requires having authored the RFC — a story about substantially shaping someone else's proposal, as in the illustrative example above, is equally valid and sometimes more credible, since it demonstrates the reviewing/shaping skill distinctly from the authoring skill. If your strongest available story is as a reviewer rather than an author, tell it as such rather than stretching to claim primary authorship — the specific intervention that changed the outcome is what the question is actually assessing, not who held the pen.

## Interview Question: "Tell me about a design review or RFC you drove or significantly shaped."

**What the interviewer is assessing:** whether the candidate can meaningfully change a technical decision through the review process — asking a load-bearing question, proposing a genuine alternative — not just whether they can write or read a design document competently.

**Weak answer characteristics:** the story is really about writing a good RFC, with no account of pushback, questions, or changes that happened during review; or the story describes reading someone else's RFC carefully with no specific intervention that changed anything.

**Strong answer structure:** S/T/A/R with Action naming the specific comment, question, or counter-proposal, and Result distinguishing what changed *because of* that specific contribution.

**Staff-level expectations:** the intervention should demonstrate seeing something others in the review missed — not agreeing or disagreeing with the obvious parts of a proposal, but surfacing a risk, consumer, or edge case that required real domain knowledge or cross-team visibility to catch.

**Probing follow-ups:** "How did the original author react to your comment?" (tests whether the intervention was delivered collaboratively or as a challenge — see [Conflict and Technical Disagreement](06-conflict-and-technical-disagreement.md)'s "strongest form" discipline, which applies directly here); "What would have happened if you hadn't raised it?"; "How do you decide when to comment on an RFC versus when to raise something in person first?"

**Self-review checklist:**
- [ ] A specific intervention (question, comment, counter-proposal) is named, not just "I reviewed it carefully"
- [ ] The intervention required real insight — a consumer, risk, or constraint others hadn't considered — not an obvious point
- [ ] Result distinguishes what changed because of this specific contribution from what would have happened anyway
- [ ] The tone of the intervention, as described, is collaborative and curious, not adversarial

## Common Mistakes

- Describing the quality of the RFC document itself rather than a specific intervention during the review process.
- No account of pushback, disagreement, or change — implying the review process was purely confirmatory, which undermines the idea that the candidate's participation mattered.
- Claiming authorship of an RFC that was substantially someone else's, when a "shaped as reviewer" framing would be both more honest and, often, an equally strong answer.
- An intervention framed as a challenge or objection rather than a genuine question — see [Conflict and Technical Disagreement](06-conflict-and-technical-disagreement.md) for why tone matters here specifically.

## Self-Review Checklist

- [ ] A specific, nameable intervention is described, not a general sense of "careful review"
- [ ] The intervention demonstrates insight others in the room didn't have
- [ ] The outcome is attributed specifically to this intervention, not the process in general
- [ ] The story is honest about whether the candidate was the RFC's author or a reviewer who shaped it

## Summary

A design-review story is evaluated on a specific, nameable intervention — a question that surfaced a risk, a counter-proposal, a request for additional analysis — that measurably changed a decision's direction, not on the quality of a written document alone. This story is equally valid told from the reviewer's seat as from the author's; the skill being assessed is shaping a decision through the review process, which either role can demonstrate. Delivering the intervention collaboratively, as a genuine question rather than a challenge, strengthens rather than weakens the story.

## Related

- [STAR Framework and Delivery Mechanics](01-star-framework-and-delivery.md) — the base structure this chapter specializes for design-review narratives.
- [Conflict and Technical Disagreement](06-conflict-and-technical-disagreement.md) — the "strongest form" and collaborative-tone disciplines apply directly to how a design-review intervention should be told.
- [Trade-off Narration and Architecture Decision Records](../technical-answers/trade-off-narration-and-adrs.md) — the canonical chapter for the technical skill of writing or structurally evaluating an RFC (the four-beat structure); this chapter assumes that skill and focuses on the behavioral question of what changed as a result of the review process.
