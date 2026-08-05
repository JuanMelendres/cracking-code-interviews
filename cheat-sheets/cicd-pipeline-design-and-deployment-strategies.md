---
title: "Cheat Sheet: CI/CD Pipeline Design and Deployment Strategies"
slug: cicd-pipeline-design-and-deployment-strategies
document_type: cheat-sheet
domain: cloud
topic_id: T-1009
canonical: ../handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md
last_updated: 2026-08-05
---

# CI/CD Pipeline Design and Deployment Strategies

**Canonical chapter:** [`handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md`](../handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md)

## Core Mental Model

Every deployment strategy answers the same question differently: how much of production is exposed to a bad release before a human or automated check catches it? Rolling exposes a shrinking fraction of existing capacity as the rollout proceeds. Blue-green exposes zero traffic until an explicit cutover, then all of it at once. Canary exposes a small, deliberately-chosen slice of real traffic first, specifically to get real signal before wider exposure — and canary's entire value depends on someone actually looking at that signal before promoting further.

## Essential Definitions

- **Rolling deployment** — gradually replaces old-version instances with new-version instances; exposure is a side effect of the mechanism, not a deliberate signal-gathering stage.
- **Blue-green deployment** — runs the new version fully alongside the old with zero production traffic on the new one, then cuts over all at once; can cut back instantly.
- **Canary deployment** — routes a small percentage of real traffic to the new version first, monitors it, then progressively increases the percentage.
- **Promotion gate** — the decision point between canary exposure and full rollout; "no alert fired" and "a human confirmed this is healthy" are different levels of evidence.

## Decision Table

| Need | Strategy |
|---|---|
| Simple rollout, no extra infrastructure cost | Rolling |
| Instant, complete rollback option | Blue-green |
| Real production signal on a bounded slice before wide exposure | Canary, with a human approval gate |
| Release risk well-covered by comprehensive, well-tuned alerting | Canary with automated promotion may be acceptable — verify the "well-covered" claim explicitly first |

**Trade-offs:** blue-green's instant rollback costs double infrastructure during the transition; canary with automated-only promotion is fast but can only catch what its alerting threshold was explicitly designed to catch — missing patterns a human would flag.

## Key Numbers (real, syntax-validated GitHub Actions pipeline)

```
$ ruby -ryaml -e "d = YAML.load_file('ci-cd-pipeline.yaml'); puts d['jobs'].keys.join(', ')"
Parsed OK. Jobs: build-and-test, deploy-canary, promote-to-stable
```

Pipeline structure: `build-and-test` gates on real test success before pushing an image. `deploy-canary` deploys to a separate canary Deployment and watches metrics for a 10-minute window with a defined error-rate threshold (`--max-error-rate=0.01`). `promote-to-stable` uses GitHub's `environment: production` mechanism specifically to enforce a manual approval gate — because absence of an alert is not the same as a human confirming the canary looked healthy.

## Common Pitfalls

- Treating "no alert fired during the canary window" as equivalent to "a human confirmed this release is healthy."
- Choosing a deployment strategy by habit rather than the specific service's risk tolerance and rollback needs.
- Running blue-green's double infrastructure longer than the transition actually requires instead of tearing down the old environment promptly.
- Sizing a canary window shorter than the failure mode a release actually risks (e.g., a memory leak that only manifests after tens of minutes, checked against a 5-minute window).

## Interview Answer Skeleton

**30-sec:** Rolling, blue-green, and canary bound release risk differently: rolling by shrinking exposure as the rollout proceeds, blue-green by keeping the new version at zero traffic until an instant, reversible cutover, canary by deliberately routing a small, monitored slice of real traffic first. Canary's entire value depends on someone actually evaluating that signal.

**2-min:** Add why (every deployment carries risk, strategies differ in how it's bounded) + the real pipeline evidence (a validated three-stage GitHub Actions workflow with an explicit `environment: production` approval gate) + the trade-off (blue-green costs double infrastructure; fully-automated canary can miss what its threshold wasn't designed to catch).

**Whiteboard:** build-and-test → deploy-canary (10% traffic, watched 10 minutes) → a manual approval gate (person icon) → promote-to-stable (100% traffic). Annotate the gate: "this exists because 'no alert fired' and 'a human looked and confirmed healthy' are different levels of evidence."

**Staff-level framing:** the "no alert" vs. "confirmed healthy" gap generalizes far beyond deployment — the same gap exists between a passing test suite and genuine code review, or a clean dashboard and an on-call engineer's judgment during an ambiguous incident. Treat any fully-automated gate as only as good as what it was explicitly designed to check, and ask explicitly what it would miss.

## Production Warning Signs

- A fully-automated canary promotion (no human gate) ships a subtle regression that stayed within the alert threshold but was visually obvious on a dashboard — the more fundamental gap isn't the threshold value, it's the missing human review step.
- Double-environment blue-green infrastructure lingers long after a successful cutover — a cost leak from not tearing down the old environment promptly.
- **Prevention:** require an explicit human approval gate for canary-to-full-production promotion for any release where the cost of a missed regression is meaningful; treat "no alert" and "confirmed healthy" as genuinely different evidence levels.

## Related

- `handbook/cloud/kubernetes-objects-scheduling-and-networking.md`
- `handbook/system-design/resilience-patterns.md`
