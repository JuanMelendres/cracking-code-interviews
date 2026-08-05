---
title: "Boundary Condition Bug Behind a 95% Coverage Figure"
document_type: production-cookbook-entry
domain: testing
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/testing/mutation-and-property-based-testing.md
source: handbook/testing/mutation-and-property-based-testing.md#production-scenarios
---

# Boundary Condition Bug Behind a 95% Coverage Figure

## Context

A critical pricing module carries 95% line coverage, treated by the team as strong evidence of test quality.

## Symptoms

A real production bug ships anyway, specifically in a boundary condition of the pricing logic.

## Impact

A module considered well-tested by its coverage figure produces an incorrect pricing outcome in production, undermining confidence in coverage as a quality signal for this module and others like it.

## Initial Hypotheses

- The 95% coverage figure itself is inaccurate or misleading — investigated directly rather than assumed.
- The covered lines execute during test runs but the assertions checking them are too weak to catch a boundary-shifted defect — correct, confirmed via a retrospective mutation-testing run.

## Evidence

A retrospective mutation-testing run on the same module reveals numerous surviving mutants at exactly the boundary conditions the shipped bug occurred in — the 95% coverage number was real, the boundary-adjacent lines did execute during test runs, but the assertions at those lines were too weak to actually catch a boundary-shifted defect.

## Investigation Timeline

1. **Production bug shipped** in a boundary condition of a module carrying 95% line coverage.
2. **Coverage figure itself verified as accurate**, ruling out a simple measurement or tooling error.
3. **Retrospective mutation-testing run** against the same module and same boundary-condition code paths.
4. **Surviving mutants found concentrated exactly at the boundary conditions** the shipped bug occurred in, confirming the assertions there were structurally too weak to catch the defect class.

## Root Cause

The 95% coverage number was real — the boundary-adjacent lines did execute during test runs — but the assertions at those lines were too weak to actually catch a boundary-shifted defect. Coverage measures whether code executed, not whether the test's assertions would catch a plausible mutation of that code's logic.

## Immediate Mitigation

Fix the specific shipped bug and strengthen the assertions at the exact boundary conditions the mutation-testing run flagged.

## Permanent Fix

Add mutation testing specifically for the highest-risk modules, where coverage alone has already been shown not to be a sufficient quality signal, rather than abandoning coverage metrics entirely.

## Alternatives Considered

Abandoning line coverage as a metric entirely, in favor of mutation score alone. Rejected — coverage still usefully catches wholly untested code paths cheaply and quickly; mutation testing is a targeted, more expensive complement for high-risk modules, not a wholesale replacement.

## Trade-offs

Mutation testing is significantly more computationally expensive than line coverage, making it impractical to run on every module on every commit. Accepted by scoping it deliberately to the highest-risk modules rather than applying it uniformly.

## Prevention

Any module where a correctness bug carries meaningful business or financial consequence — pricing, billing, payment calculation — should have a mutation-testing pass as part of its quality bar, not rely on line coverage alone.

## Monitoring and Alerts

- Mutation score tracked as a standing metric for designated high-risk modules, reviewed alongside line coverage rather than as a one-off retrospective exercise triggered only after an incident.
- A CI gate requiring a minimum mutation score (not just line coverage) specifically for modules flagged as high-risk, catching weak-assertion regressions before they reach production rather than discovering them retrospectively after a bug ships.

## Interview Story

This maps directly to a "your coverage was 95%, how did a bug still ship" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a critical pricing module with 95% line coverage shipped a real bug at a boundary condition.
- **Task:** explain the gap between a high coverage number and an actual missed defect.
- **Action:** verify the coverage figure itself was accurate; run mutation testing retrospectively against the same module; find surviving mutants concentrated exactly at the boundary conditions the bug occurred in, confirming weak assertions rather than untested code.
- **Result:** strengthened the flagged assertions and added mutation testing as a standing quality gate for the highest-risk modules going forward, without discarding coverage as a metric.

## Staff-Level Discussion

This incident is a precise demonstration of the difference between two claims that are easy to conflate: "this code ran during a test" (coverage) and "a test would catch a plausible bug in this code" (mutation score). Coverage is necessary but not sufficient — code that never executes during testing definitely isn't verified, but code that executes with a weak assertion isn't meaningfully verified either, and coverage alone cannot distinguish the two. The Staff-level judgment is knowing where this distinction actually matters enough to justify mutation testing's real computational cost: not every module needs it, but any module where a boundary-condition defect has real business consequence is exactly the kind of module where "95% coverage" should prompt the follow-up question "coverage of what quality of assertion," not be treated as sufficient reassurance on its own.

## Related Handbook Chapters

- [Mutation and Property-Based Testing](../handbook/testing/mutation-and-property-based-testing.md) — canonical mutation-testing mechanics and the `>=`-to-`>` demonstration used here.
