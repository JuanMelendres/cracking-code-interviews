---
title: "Technical Debt Advocacy"
slug: technical-debt-advocacy
document_type: behavioral-handbook-chapter
domain: behavioral
status: draft
version: 1.0
last_updated: 2026-08-03
difficulty:
  - intermediate
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - behavioral-handbook/01-star-framework-and-delivery.md
related:
  - behavioral-handbook/09-cross-team-influence-without-authority.md
  - handbook/architecture/technical-debt-and-evolutionary-architecture.md
official_references: []
---

# Technical Debt Advocacy

## Table of Contents

- [Learning Objectives](#learning-objectives)
- [Why This Matters in Interviews](#why-this-matters-in-interviews)
- [Mental Model: The Business Case, Not the Technical Case](#mental-model-the-business-case-not-the-technical-case)
- [The Technical Debt Advocacy Story Structure](#the-technical-debt-advocacy-story-structure)
- [Illustrative Example](#illustrative-example)
- [Quantifying Debt: What Actually Persuades](#quantifying-debt-what-actually-persuades)
- [Interview Question: "Tell me about a time you argued for unglamorous work over new features."](#interview-question-tell-me-about-a-time-you-argued-for-unglamorous-work-over-new-features)
- [Common Mistakes](#common-mistakes)
- [Self-Review Checklist](#self-review-checklist)
- [Summary](#summary)
- [Related](#related)

## Learning Objectives

After this chapter, you can tell a technical-debt-advocacy story that makes a genuine business case for unglamorous work, rather than an appeal to code quality as an end in itself — and you understand why the latter almost never persuades a skeptical stakeholder.

## Why This Matters in Interviews

Every engineering organization has more technical debt than capacity to address it, and prioritizing debt paydown over visible feature work requires making a case to people (product managers, other engineers, sometimes executives) who are, reasonably, skeptical of "trust me, this needs fixing" arguments. This question assesses whether a candidate can translate a technical concern into terms a non-technical or differently-incentivized stakeholder actually finds compelling — a genuinely difficult communication skill, distinct from the underlying technical judgment about which debt matters.

## Mental Model: The Business Case, Not the Technical Case

The single most common failure in this story category is presenting the argument in purely technical terms — "the code is messy," "we're violating best practices," "this isn't how we should architect things." These arguments are often technically correct and almost never persuasive to someone whose incentives are measured in shipped features, not code cleanliness. A strong technical-debt-advocacy story translates the technical concern into a business-legible cost: how much slower does this debt make future feature work, what's the actual incident or outage risk it carries, what specific upcoming roadmap item does it block or make more expensive. The technical judgment about what to fix is necessary but not sufficient — the story needs to also demonstrate the translation skill.

## The Technical Debt Advocacy Story Structure

| STAR component | Debt-advocacy-specific content |
|---|---|
| Situation | What debt existed, and — critically — what was competing for the same time/priority (a feature the org wanted, another team's dependency) |
| Task | The candidate's specific role in making the case — not just identifying the debt, but advocating for it against competing priorities |
| Action | How the technical concern was translated into a business-legible cost — velocity impact, risk, a specific blocked roadmap item — and who specifically needed to be convinced |
| Result | What was actually prioritized, and — ideally — a concrete measurement of the benefit once the debt was addressed (velocity improvement, reduced incident rate, unblocked roadmap item that shipped faster than it otherwise would have) |

## Illustrative Example

This example is illustrative — a representative scenario, not a real candidate's actual experience.

*"Our payment-processing service had accumulated a pattern where every new payment method required copy-pasting an existing method's integration code and modifying it, because the original integration hadn't been built with a plugin architecture — by the time I raised this, we had six near-duplicate implementations, and product wanted a seventh payment method added within the quarter. My instinct was that we needed to refactor to a proper plugin architecture before adding the seventh, but I knew 'the code is messy, let's refactor' wouldn't survive a roadmap conversation against a quarter-end feature deadline. Instead, I measured how long each of the previous six integrations had actually taken, and found the average was three weeks, with the most recent one taking five weeks because the codebase had grown harder to safely modify with each near-duplicate added — a real, worsening trend, not just a one-time cost. I proposed a two-week investment in the plugin refactor specifically framed as: this seventh integration will take an estimated five to six weeks under the current pattern (extrapolating the worsening trend), or two weeks of refactor plus an estimated one week for the seventh integration under the new pattern — three weeks total instead of five to six, even accounting for the refactor cost, on this integration alone, with every subsequent integration (and product already had two more in their next-quarter roadmap) benefiting further. That framing — a near-term time comparison, not an abstract code-quality argument — is what got the two weeks approved. The seventh integration shipped in nine days under the new pattern, and the next two, added the following quarter, took four and five days respectively — the actual return on the refactor compounded exactly as projected."*

## Quantifying Debt: What Actually Persuades

Where a real, measurable trend exists — as in the illustrative example's "each integration has taken longer than the last" pattern — lead with it; a concrete historical trend is far more persuasive than a hypothetical projection. Where no clean historical measurement exists, the next-best framing is a specific, credible near-term cost: what's the next planned piece of work this debt will make slower or riskier, and by roughly how much. The weakest framing — worth avoiding, not just deprioritizing — is a purely qualitative appeal to code quality or "best practice" with no cost estimate attached at all; this is the argument most likely to lose against a competing, quantified feature request.

## Interview Question: "Tell me about a time you argued for unglamorous work over new features."

**What the interviewer is assessing:** whether the candidate can translate a technical concern into a business-legible case, and whether they can win a genuine prioritization argument against competing, more visible priorities — not just whether they can correctly identify technical debt.

**Weak answer characteristics:** the argument presented is purely technical ("the code was bad") with no cost translation; the debt was addressed because the candidate had unilateral authority to prioritize it, not because they persuaded a skeptical stakeholder; no measurement of the actual benefit once the work was done.

**Strong answer structure:** S/T/A/R with Action specifically describing the translation from technical concern to business cost, and naming who needed to be convinced and what ultimately persuaded them.

**Staff-level expectations:** a concrete, ideally quantified, benefit realized after the debt was addressed — not just that the argument was won, but that the projected benefit actually materialized, which is a stronger and rarer signal than the persuasion itself.

**Probing follow-ups:** "What if you'd been wrong about the time savings — how would you have known, and what would you have done?"; "How did you decide this debt mattered more than other debt you could have argued for instead?"; "What was the stakeholder's strongest objection, and how did you address it?"

**Self-review checklist:**
- [ ] The argument is framed in business-legible terms (velocity, risk, a specific blocked roadmap item), not purely code quality
- [ ] A real stakeholder with competing incentives had to be genuinely persuaded, not just informed
- [ ] Some quantification, even rough, is used rather than a purely qualitative appeal
- [ ] The actual realized benefit, once addressed, is stated, not just the projection

## Common Mistakes

- Arguing purely from code quality or best practice, with no cost translation — the single most common way this argument type fails both in real workplaces and in interview stories about them.
- Describing debt that was addressed through unilateral authority rather than genuine persuasion of a skeptical stakeholder — this doesn't demonstrate the advocacy skill being assessed.
- No follow-up measurement of whether the projected benefit actually materialized — a persuasive pitch that was never verified against reality is a weaker story than one that closes the loop.
- Choosing debt that was low-stakes or uncontested, avoiding the harder case of genuinely competing with a wanted feature.

## Self-Review Checklist

- [ ] The technical concern is translated into a business-legible cost, not left as a purely technical argument
- [ ] A real, specific stakeholder with competing priorities is named and had to be genuinely convinced
- [ ] Quantification, even approximate, supports the case
- [ ] The realized benefit is stated, closing the loop on whether the projection held up

## Summary

Technical debt advocacy is won or lost on translation, not technical correctness — the strongest stories reframe a code-quality concern as a business-legible cost (velocity impact, risk, a specific blocked roadmap item) and quantify it, even approximately, rather than appealing to best practice alone. The strongest possible close to this story names the realized benefit after the debt was addressed, demonstrating the original projection actually held up in practice, not just that the argument was won.

## Related

- [STAR Framework and Delivery Mechanics](01-star-framework-and-delivery.md) — the base structure this chapter specializes for debt-advocacy narratives.
- [Cross-Team Influence Without Authority](09-cross-team-influence-without-authority.md) — shares this chapter's persuasion-under-competing-incentives structure; technical debt advocacy is often a specific instance of the broader influence skill that chapter covers.
