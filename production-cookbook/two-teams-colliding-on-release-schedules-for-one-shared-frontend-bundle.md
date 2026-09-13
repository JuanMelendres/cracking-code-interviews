---
title: "Two Teams Colliding on Release Schedules for One Shared Frontend Bundle"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/micro-frontends-and-module-federation.md
source: syllabus/21-frontend-web/micro-frontends-and-module-federation.md#production-scenarios
---

# Two Teams Colliding on Release Schedules for One Shared Frontend Bundle

## Context

Team A (checkout) and Team B (product recommendations) both ship changes to the same page in a monolithic frontend bundle.

## Symptoms

Every release requires both teams' changes to be merged, tested, and deployed together — a change either team wants to ship urgently is blocked behind the other team's readiness.

## Impact

Release velocity for both teams is capped by the slower of the two; a production incident in one team's code forces an emergency rollback of the entire shared bundle, including the other team's unrelated, working changes.

## Initial Hypotheses

- A process/communication failure — checked, teams already coordinate deploy windows carefully; the constraint is structural, not a communication gap.
- A testing gap — checked, both teams have adequate test coverage for their own code.
- Both teams' code is compiled into one shared build artifact, so there's no way to deploy one team's change without redeploying the other's unchanged code — correct.

## Evidence

Confirming that both teams' code lives in the same build pipeline, producing a single deployable bundle, is the structural signature of this problem, distinct from a process issue a retro could fix.

## Investigation Timeline

1. Repeated release delays and shared-blast-radius rollbacks observed across both teams.
2. Process and testing-gap hypotheses ruled out via review of existing coordination and coverage.
3. Build pipeline inspected, confirming a single shared bundle as the structural cause.

## Root Cause

A single, shared build artifact structurally couples both teams' release timelines and failure blast radius, regardless of how well the teams coordinate.

## Immediate Mitigation

Establish a stricter deploy-coordination schedule as a short-term relief valve, while accepting it doesn't fix the underlying coupling.

## Permanent Fix

Split the shared page into a host (owned by whichever team owns the page's overall shell/layout) and one or more remotes (each team's own feature, exposed via its own `ModuleFederationPlugin`), each with its own independent build and deploy pipeline.

## Alternatives Considered

A monorepo with better internal module boundaries but still one shared build and deploy — rejected as insufficient, since it improves code organization but doesn't remove the single-artifact deploy coupling that's the actual root cause.

## Trade-offs

Real, new operational costs apply once the split happens: dependency duplication risk if shared libraries (the UI framework) aren't configured as `shared`/`singleton`, and more infrastructure/tooling coordination across now-separate repos and pipelines — a real cost against the real, gained deploy independence.

## Prevention

Treat "does this feature genuinely need to ship on another team's release schedule" as an explicit, early architectural question for any multi-team frontend product, not an afterthought once teams start blocking each other.

## Monitoring and Alerts

- Deploy-frequency and rollback-blast-radius metrics tracked per team, surfacing this exact coupling pattern before it becomes a chronic complaint.
- A dependency-duplication check (verifying `shared`/`singleton` configuration) run in CI once a Module Federation split ships, to catch the trade-off's own real risk early.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent architecture decision.

- **Situation:** two teams sharing one frontend bundle had their release velocity capped by each other, with any incident forcing a shared rollback.
- **Task:** determine whether this was a process problem or a structural one, and fix the actual cause.
- **Action:** confirmed the single shared build pipeline as the root cause, then split the page into an independently-deployable host and remote via Module Federation.
- **Result:** decoupled both teams' release schedules and failure blast radius, at the cost of new shared-dependency coordination overhead.

## Staff-Level Discussion

The failure mode here is structural (a shared build artifact), and the fix is structural too (a real runtime composition boundary) — recognizing this distinction early prevents a team from spending effort on process fixes (better coordination, more testing) that can't actually resolve a structural coupling. The organizational lesson generalizes past this one incident: any team-scoped feature that shares a build artifact with another team's feature carries this same latent risk, and the earlier it's identified as an architectural question, the cheaper the eventual split.

## Related Handbook Chapters

- [Micro-Frontends and Module Federation](../syllabus/21-frontend-web/micro-frontends-and-module-federation.md) — the canonical host/remote independent-deploy pattern behind this incident's fix.
