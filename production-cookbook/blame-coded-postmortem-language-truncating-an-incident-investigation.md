---
title: "Blame-Coded Postmortem Language Truncating an Incident Investigation"
document_type: production-cookbook-entry
domain: performance
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/13-observability/incident-response-and-blameless-postmortems.md
  - ../syllabus/17-architecture/architecture-decision-records.md
source: handbook/performance/incident-response-and-blameless-postmortems.md#production-scenarios
---

# Blame-Coded Postmortem Language Truncating an Incident Investigation

## Context

*(Representative scenario, following this repository's fictionalized-scenario labeling convention.)* A postmortem draft for a failed deployment stated, in its first paragraph, that "the on-call engineer failed to run the pre-deploy checklist."

## Symptoms

The engineer named stopped participating actively in the review meeting and, in a private follow-up, said they felt like the postmortem was building a case against them rather than trying to understand what happened.

## Impact

Beyond the immediate interpersonal cost, reviewing the team's prior six postmortems found the same "an engineer failed to X" framing in three of them, and those same three incidents had noticeably thinner "how did this become possible" analysis than the other three, which used systems-and-process framing throughout.

## Initial Hypotheses

The phrasing was just imprecise, not a real problem.

## Evidence

Reviewing the team's prior six postmortems found the same "an engineer failed to X" framing in three of them, and the same three incidents had noticeably thinner "how did this become possible" analysis than the other three, which used systems-and-process framing throughout — the blame-framed postmortems consistently stopped investigating once a person could be identified, rather than continuing to ask why the process allowed that person's single action to cause a full incident.

## Investigation Timeline

1. Postmortem draft's blame-coded first-paragraph framing ("the on-call engineer failed to...") noted, along with the named engineer's withdrawal from the review meeting and their private follow-up feedback.
2. "Just imprecise phrasing, not a real problem" hypothesis raised initially.
3. A retrospective audit run across the team's prior six postmortems, checking for the same blame-coded framing pattern.
4. Three of six postmortems found to use the same "an engineer failed to X" framing, and compared against the other three for analysis depth.
5. The three blame-framed postmortems found to have noticeably thinner "how did this become possible" analysis, consistently stopping once a person was identified, while the three systems-framed postmortems continued investigating past that point.

## Root Cause

Blame-coded language wasn't just an interpersonal problem — it was actively truncating the investigation, because once a "who" was found, the "why did the system allow this" line of inquiry lost momentum.

## Immediate Mitigation

The specific postmortem draft was rewritten before publication, replacing "the engineer failed to run the checklist" with "the deploy pipeline had no enforced gate requiring the checklist be run" — a systems statement that led directly to a concrete action item (add the gate) that "be more careful" never would have.

## Permanent Fix

Adopted a linter as a real, mandatory CI check on every postmortem document before it could be merged into the team's incident archive, flagging blame-coded language for revision.

## Alternatives Considered

None recorded beyond automated linting — the scenario treats it as the direct, necessary enforcement mechanism rather than relying on reviewer vigilance to catch blame-coded language manually.

## Trade-offs

A small amount of postmortem-authoring friction (rewriting flagged sentences) in exchange for postmortems that reliably continue past the first identifiable person.

## Prevention

Postmortem review now explicitly asks "if this sentence names a person, what's the systems-level version of the same fact?" as a standing review question.

## Monitoring and Alerts

- Run the blameless-language linter as a required CI check on every postmortem document, converting the manual audit that surfaced this pattern (across six historical documents) into an automatic, per-document check going forward.
- Periodically re-run the "analysis depth" comparison this incident's evidence used — comparing postmortems the linter flagged (and had rewritten) against ones that passed cleanly — to confirm the fix is actually correlating with deeper systems-level analysis over time, not just cleaner-sounding language.
- Track action-item count and specificity per postmortem as a proxy metric for investigation depth; the correlation this incident found (blame framing paired with thinner analysis) predicts that a postmortem generating fewer, vaguer action items may be worth a manual review even if the linter itself passes it.

## Interview Story

This maps directly to "why does blameless postmortem language actually matter, beyond morale" backed by a real comparative finding. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a postmortem draft named an engineer as having "failed to" follow a process, and the engineer disengaged from the review, feeling blamed rather than helped.
- **Task:** determine whether this was purely an interpersonal issue or something with a measurable investigative cost.
- **Action:** audited the team's prior six postmortems and found the same blame-coded framing in three of them, each with measurably thinner "how did this become possible" analysis than the systems-framed postmortems.
- **Result:** rewrote the specific draft into a systems-level statement that led directly to a concrete fix (an enforced pipeline gate), then adopted a mandatory linter checking every future postmortem for blame-coded language before it could be merged.

## Staff-Level Discussion

The real finding in this scenario is not that blame-coded language is unkind — it's that it is measurably correlated with shallower root-cause analysis, because identifying a person who "failed to" do something gives an investigation a natural, premature stopping point, while a systems-level framing ("the pipeline had no enforced gate") has nowhere to stop except at an actual structural cause. That distinction is the argument a Staff engineer needs when defending blameless-postmortem practice against a skeptical stakeholder who sees it as a soft, morale-focused nicety rather than an investigative-rigor requirement: the real cost of blame-coded language is incidents that recur because the process gap behind them was never named, not just the (real, separate) cost to the named engineer's willingness to participate honestly in future reviews. The enforcement choice — a mandatory CI linter rather than relying on reviewer discipline — reflects the same lesson this program applies elsewhere: a norm that depends on every reviewer independently remembering to police language will eventually fail exactly the way this postmortem draft did, while an automated check makes the standard uniform regardless of who is reviewing.

## Related Handbook Chapters

- [Incident Response and Blameless Postmortems](../syllabus/13-observability/incident-response-and-blameless-postmortems.md) — canonical blameless-language framing and the linter this incident's permanent fix adopts.
- [Architecture Decision Records](../syllabus/17-architecture/architecture-decision-records.md) — the broader documentation-quality discipline (grounding conclusions in checkable evidence) that also underlies rigorous postmortem analysis.
