---
title: "Scope, Impact, and Influence Framing"
slug: scope-impact-and-influence-framing
document_type: behavioral-handbook-chapter
domain: behavioral
status: draft
version: 1.0
last_updated: 2026-08-03
difficulty:
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - behavioral-handbook/01-star-framework-and-delivery.md
  - behavioral-handbook/02-story-portfolio-design.md
related: []
official_references: []
---

# Scope, Impact, and Influence Framing

## Table of Contents

- [Learning Objectives](#learning-objectives)
- [Why This Matters in Interviews](#why-this-matters-in-interviews)
- [Mental Model: The Same Facts, a Different Telling](#mental-model-the-same-facts-a-different-telling)
- [The Reframing Lens](#the-reframing-lens)
- [Worked Example](#worked-example)
- [Applying the Lens to Your Own Stories](#applying-the-lens-to-your-own-stories)
- [When There's Genuinely No Broader Scope](#when-theres-genuinely-no-broader-scope)
- [Interview Question: "What's the biggest impact you've had beyond your immediate team?"](#interview-question-whats-the-biggest-impact-youve-had-beyond-your-immediate-team)
- [Common Mistakes](#common-mistakes)
- [Self-Review Checklist](#self-review-checklist)
- [Summary](#summary)
- [Related](#related)

## Learning Objectives

After this chapter, you can take a story you've already told at Senior scope and identify — without inventing anything — the details that were true of the original event but weren't part of that telling, then rebuild the story to surface them at Staff scope.

## Why This Matters in Interviews

Staff-level loops score influence and organizational scope directly, often as an explicit rubric line separate from technical judgment. A candidate can have excellent technical judgment and still read as "not yet Staff" if every story they tell stops at "and it worked for my team" — because the interviewer has no evidence of impact beyond the candidate's own immediate scope of control. This is the single most common reason a strong Senior candidate is assessed as not-yet-ready for Staff, and it's frequently a *telling* problem, not a *reality* problem — the broader impact often genuinely happened, it just wasn't part of the story as told.

## Mental Model: The Same Facts, a Different Telling

Reframing is not embellishment and it is not inventing new events. The same real event, told at Senior scope, typically ends at "the decision was adopted for our service." Told at Staff scope, the *same event* continues further: who else was affected by the decision, who had to be convinced (and what was their strongest counter-argument), and what changed beyond the immediate problem being solved. These details were true of the original event — they just weren't surfaced in the shorter, Senior-scope telling, either because they seemed like unnecessary detail or because the story was originally built before the teller was thinking about scope at all.

## The Reframing Lens

Apply these four questions to any existing story:

| Question | What it surfaces |
|---|---|
| Who else was affected by this, beyond your immediate team? | Organizational scope |
| Who did you have to convince, and what was their strongest argument against you? | Influence, and intellectual honesty about the opposition |
| What changed as a result, beyond the original problem being solved? | Downstream impact |
| Would you make the same call again, knowing what you know now? | Judgment, not just execution |

The second question's "strongest argument against you" clause matters specifically: a Staff interviewer listening for judgment is listening for whether the candidate can represent an opposing view fairly. A story where the opposition is a strawman — dismissed too easily, or not really disagreeing with anything substantive — reads as a lack of intellectual honesty, regardless of how the disagreement was actually resolved. This is the same "strongest form" discipline that applies to dedicated conflict stories (see [Conflict and Technical Disagreement](06-conflict-and-technical-disagreement.md)), applied here to any story that happens to involve convincing someone.

## Worked Example

This example is illustrative — a representative scenario, not a real candidate's actual experience — used to demonstrate the reframing technique concretely rather than only describing it abstractly.

**Senior-scope telling:** *"We chose hexagonal architecture for the order service because we needed to swap persistence technology later without a rewrite. It cost us mapping code but paid off when we actually did the swap."*

**Same story, Staff-scope reframe:** *"Two other teams consumed the order service's internal domain model directly at the time — a dependency that would have made any refactor risky. Convincing the platform lead to invest in the port/adapter boundary meant first convincing those two teams to go through the new port interface instead of the internals directly, which they resisted because it meant short-term rework on their side for a benefit they wouldn't see for months. The strongest argument against me was fair: 'we're slowing down two teams today for a benefit that's speculative.' The boundary held anyway; six months later, when the persistence swap happened, the two consuming teams didn't need to change anything, and one of them independently adopted the same port/adapter pattern for their own service afterward — an org-wide practice shift that outlasted the original technical reason for asking."*

**What changed in the reframe:** the same facts, but now visible — two other teams affected, a real objection represented fairly, and a downstream consequence (a practice adopted elsewhere) beyond the original service. Nothing in the Staff-scope version contradicts the Senior-scope version; it's a superset, not a rewrite of the facts.

## Applying the Lens to Your Own Stories

For each story in your portfolio (see [Story Portfolio Design](02-story-portfolio-design.md)), work through the four-question lens and capture the answers:

```markdown
### <Story name> reframed
Who else was affected:
Who you had to convince, their strongest argument:
What changed beyond the original problem:
Would you do it again:
```

Do this reframing pass *after* the story portfolio's first draft exists, not while drafting each story initially — reframing is a rewrite of existing material with real facts already established, not a prompt for constructing scope artificially where none existed. Architecture-decision and cross-team-influence stories (see [Story Portfolio Design](02-story-portfolio-design.md)'s competency matrix) tend to have the richest scope to surface; a purely individual-contributor debugging story may have genuinely little beyond-team scope to find, which is fine — see the next section.

## When There's Genuinely No Broader Scope

Not every real story has organizational scope beyond the immediate team, and inventing scope that wasn't there is worse than not having it — a fabricated "downstream impact" collapses the moment a follow-up question probes it. If a story's honest answer to "who else was affected" is genuinely "no one, this was purely internal to my team," say that plainly rather than stretching for a scope that doesn't exist. A portfolio doesn't need every single story reframed to Staff scope — it needs *some* stories that demonstrate real organizational reach, and the reframing lens is how you find out which stories in your existing portfolio already qualify.

## Interview Question: "What's the biggest impact you've had beyond your immediate team?"

**What the interviewer is assessing:** whether the candidate has genuine cross-team or cross-org influence experience, and whether they can distinguish real influence from proximity to something impactful (being on the team that shipped something big is not the same as having personally driven cross-team adoption of anything).

**Weak answer characteristics:** the answer describes something the candidate's *team* accomplished, with the candidate's individual contribution unclear; or the "impact beyond the team" is really just "other teams used the service my team built," with no account of the candidate personally doing anything to cause that adoption.

**Strong answer structure:** S/T/A/R with Action specifically detailing who the candidate had to convince, what resistance existed, and what the candidate did to address it — not just "I proposed X and it was accepted."

**Staff-level expectations:** the story should show the candidate navigating a real trade-off between competing teams' interests, not just presenting technically-obvious advice that any reasonable engineer would have agreed with immediately. Staff-level influence usually involves cases where the "right answer" wasn't obvious to everyone up front.

**Probing follow-ups:** "What was the strongest argument against your position?" (tests intellectual honesty per the reframing lens above); "How did you know you'd actually convinced them, versus them just complying?" (tests whether the influence was durable or superficial); "What would you do differently if you had to do it again?"

**Self-review checklist:**
- [ ] The story names a specific person or team convinced, not a vague "stakeholders"
- [ ] The opposition's argument is represented in its strongest form, not a strawman
- [ ] The candidate's own action — not the team's — is what's credited with the outcome
- [ ] A concrete downstream consequence is named, not just "it was well-received"

## Common Mistakes

- Fabricating scope that doesn't exist — a follow-up question ("who specifically pushed back?") exposes this immediately.
- Confusing "my team's project was big and visible" with "I personally had cross-team influence" — proximity to impact is not the same as having caused it.
- Reframing every single story to Staff scope even when some are legitimately Senior-scope stories — a portfolio with variety (a mix of individual-execution stories and organizational-influence stories) is more credible than one where every single story claims sweeping cross-org impact.
- Doing the reframing pass before the base story exists in solid S/T/A/R form — reframing sharpens an existing story, it doesn't substitute for having one.

## Self-Review Checklist

- [ ] Each reframe names a real, specific, non-strawman objection someone raised — if a story's honest reframe has no genuine opposition to represent, that's noted honestly rather than invented
- [ ] The reframed version doesn't contradict the original Senior-scope telling — it's a superset of the same facts, not a different story
- [ ] At least 2-3 stories in the portfolio have a credible Staff-scope reframe; not every story needs one
- [ ] The "would you do it again" question has an honest answer, not a reflexive "yes"

## Summary

Scope reframing surfaces detail that was already true of a real event but wasn't part of its original, shorter telling — it is a rewrite of what actually happened, not an invention. The four-question lens (who else was affected, who had to be convinced and what was their strongest argument, what changed beyond the original problem, would you do it again) applied to an existing story portfolio identifies which stories already carry Staff-level organizational scope and sharpens how that scope gets told. Not every story needs this treatment, and honestly naming when a story has no broader scope to surface is better than fabricating one that collapses under a follow-up.

## Related

- [STAR Framework and Delivery Mechanics](01-star-framework-and-delivery.md) — the base structure this lens is applied on top of.
- [Story Portfolio Design](02-story-portfolio-design.md) — the full competency matrix this reframing technique is applied across.
- [Conflict and Technical Disagreement](06-conflict-and-technical-disagreement.md) — shares this chapter's "represent the opposition in its strongest form" discipline, applied to dedicated conflict stories specifically.
