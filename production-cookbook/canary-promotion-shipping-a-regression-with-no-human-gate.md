---
title: "Canary Promotion Shipping a Regression With No Human Gate"
document_type: production-cookbook-entry
domain: cloud
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md
source: handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md#production-scenarios
---

# Canary Promotion Shipping a Regression With No Human Gate

## Context

A team's canary pipeline automatically promotes to 100% traffic after a 10-minute window with no alert fired, with no human approval step in between.

## Symptoms

A release with a subtle latency regression — within the alerting threshold, but a real, noticeable degradation — passes the canary window silently and reaches 100% of production traffic. It's only caught hours later via a broader trend dashboard, not the canary's own alerting.

## Impact

A regression that a human glancing at the canary's actual metrics would likely have caught immediately instead reaches full production exposure and persists for hours.

## Initial Hypotheses

- The canary window was too short to catch the regression — checked and ruled out; even a longer window wouldn't have crossed the alert threshold, since the regression was within it by design of this specific incident.
- The alerting threshold itself needs tightening — a reasonable but separate finding.
- The promotion gate had no human review step at all, relying purely on "no alert fired" as evidence of health — correct, and the more fundamental gap.

## Evidence

The canary's actual latency metrics, visible on a dashboard the whole time, showed a real, visually obvious upward shift versus the stable baseline — clearly within the numeric alert threshold, but the kind of shift an engineer glancing at the dashboard for 30 seconds would very likely have flagged as worth investigating before promoting further.

## Investigation Timeline

1. **Regression discovered hours later** via a broader trend dashboard, not any canary-stage alert.
2. **Canary window length ruled out** as the cause, since the regression stayed within the alert threshold regardless of window length.
3. **Canary's own dashboard reviewed retroactively**, showing the regression was visually obvious the entire time — the data existed, nothing consumed it.
4. **Promotion-gate design reviewed**, finding no human review step between the canary window and full promotion.

## Root Cause

An alerting threshold — a specific, numeric definition of "unhealthy" — is a necessarily incomplete proxy for genuine health. Removing the human review step entirely means the pipeline can only catch what the threshold was explicitly designed to catch, nothing else, including patterns a human would recognize as suspicious even without crossing a defined line.

## Immediate Mitigation

Roll back the stable deployment to the previous version while the regression is investigated.

## Permanent Fix

Reintroduce an explicit human approval gate between the canary window and full promotion, specifically for the judgment call a numeric threshold alone can't make, while separately tightening the alerting threshold as a complementary — not substitute — improvement.

## Alternatives Considered

Relying solely on tightening alert thresholds to eventually catch this specific regression pattern. Rejected as an endless, reactive game of adding thresholds for every previously missed pattern, rather than restoring the human judgment step that would have caught this — and likely other, not-yet-seen — patterns generically.

## Trade-offs

A human approval gate adds latency to every release, waiting for a person to review and approve. Accepted, since the alternative's demonstrated cost is a regression reaching full production for hours before a separate, unrelated dashboard caught it.

## Prevention

Treat "no alert fired" and "a human confirmed this looks healthy" as two genuinely different levels of evidence, and require the stronger one before any full-production promotion, for any release where the cost of a missed regression is meaningful.

## Monitoring and Alerts

- A mandatory human approval step in the promotion pipeline itself (the Permanent Fix), enforced structurally rather than as a documented-but-skippable process step.
- The canary's own dashboard surfaced directly in the approval step's UI, so the human reviewer sees the same visual signal that would have caught this regression, rather than only a pass/fail alert status.

## Interview Story

This maps to a "why did an automated pipeline ship a regression a human would have caught" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a fully automated canary promotion shipped a real, visually obvious latency regression that stayed within the numeric alert threshold.
- **Task:** explain why "no alert fired" wasn't sufficient evidence of health.
- **Action:** rule out canary window length as the cause; review the canary's own dashboard retroactively to confirm the regression was visible the entire time; identify the missing human review step as the structural gap.
- **Result:** reintroduced a mandatory human approval gate between canary and full promotion, alongside a separate, complementary threshold-tightening effort.

## Staff-Level Discussion

This incident is a specific instance of a general trap in automation: a numeric threshold is a compression of human judgment into a rule, and compression always loses information — specifically, it loses everything the rule's author didn't anticipate. Full automation of a promotion gate is attractive because it's fast and consistent, but it inherits exactly the blind spots of whoever wrote the thresholds, with no fallback when reality falls outside them. The Staff-level judgment here isn't "automation is bad" — it's recognizing which decisions in a pipeline are genuinely mechanical (did the number cross the line) versus which require judgment (does this look wrong even though it didn't cross the line), and keeping a human in the loop specifically for the latter, for releases where the cost of being wrong is meaningful.

## Related Handbook Chapters

- [CI/CD Pipeline Design and Deployment Strategies](../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md) — canonical canary/promotion-gate design used here.
