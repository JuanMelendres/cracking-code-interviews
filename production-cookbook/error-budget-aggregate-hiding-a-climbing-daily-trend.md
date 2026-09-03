---
title: "Error-Budget Aggregate Hiding a Climbing Daily Trend"
document_type: production-cookbook-entry
domain: performance
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/13-observability/performance-methodology-and-slo-error-budgets.md
source: handbook/performance/performance-methodology-and-slo-error-budgets.md#production-scenarios
---

# Error-Budget Aggregate Hiding a Climbing Daily Trend

## Context

A team tracks its monthly error budget as a single aggregate percentage consumed, used to gate risk-bearing decisions like shipping migrations.

## Symptoms

With two weeks left in the month, the team sees 40% of the monthly error budget consumed and decides the remaining 60% is ample room to ship a moderately risky database migration. Three days after shipping, the service breaches its SLO for the month.

## Impact

An SLO breach that could have been anticipated and avoided by looking one level deeper than the aggregate percentage, damaging trust in the team's operational judgment for that quarter.

## Initial Hypotheses

- The migration itself introduced an unrelated new bug — checked and ruled out; the migration's own error rate is within expected bounds.
- A coincidental unrelated incident occurred the same week — checked and ruled out; no other incident is recorded.
- The pre-migration 40% consumption was already trending upward daily, not flat, and the migration's modest additional risk tipped an already-climbing trend over the edge — correct.

## Evidence

A day-by-day breakdown of the pre-migration budget consumption shows a steadily increasing daily failure count for two weeks straight, unrelated to any single incident — a slow-burning, cumulative issue, later identified as a memory leak causing gradually increasing timeout-driven failures, that the monthly aggregate alone didn't surface as urgent.

## Investigation Timeline

1. **SLO breach observed three days after shipping** a migration that had been judged low-risk based on remaining budget headroom.
2. **Migration-specific bug and coincidental-incident hypotheses ruled out**, confirming the migration's own error rate was normal and no separate incident occurred.
3. **Pre-migration budget consumption re-examined at daily granularity** rather than only the monthly aggregate that informed the original ship decision.
4. **Climbing trend found**: a steadily increasing daily failure count for two weeks prior, later traced to a memory leak, invisible in the monthly percentage alone.

## Root Cause

The team's aggregate-only view — "60% of budget remaining" — missed a real risk signal: an error budget's monthly aggregate can hide a severe or worsening trend that only the daily burn-rate breakdown reveals. A steadily climbing daily rate left far less real headroom than the aggregate percentage suggested.

## Immediate Mitigation

Roll back the migration to remove its incremental risk while the underlying climbing-trend issue is investigated separately.

## Permanent Fix

Fix the root cause — the memory leak — and add a standing practice of reviewing the daily burn-rate trend, not just the aggregate percentage, before any risk-bearing decision: ship, migrate, or run a maintenance window.

## Alternatives Considered

Simply lowering the threshold for what counts as "ample room," for example requiring 80% remaining instead of 60%. Rejected as treating the symptom — the actual fix is looking at trend, not just raising a static threshold that a climbing trend could still exceed.

## Trade-offs

Reviewing the daily trend before every risk decision adds a small analysis step to the process. Accepted, since the alternative is exactly the kind of avoidable SLO breach this incident represents.

## Prevention

Any decision gated on error-budget headroom should require the daily burn-rate chart, not just the current aggregate percentage, as a matter of process.

## Monitoring and Alerts

- A standing daily burn-rate chart surfaced alongside the aggregate percentage in any dashboard used for ship/no-ship decisions, rather than requiring someone to manually decompose the aggregate after the fact — the trend should be the default view, not a follow-up investigation.
- An automated trend alert on sustained daily burn-rate increases (e.g., N consecutive days of rising daily failure count), independent of whether the aggregate percentage has yet crossed any threshold — this surfaces the underlying leak days before the aggregate looked concerning.

## Interview Story

This maps to a "healthy error budget on paper, SLO breach anyway" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a migration judged low-risk based on 60% remaining monthly error budget led to an SLO breach three days after shipping.
- **Task:** explain how a budget that looked healthy in aggregate failed to predict the breach.
- **Action:** rule out a migration-specific bug and a coincidental incident; break down the pre-migration budget consumption by day instead of relying on the monthly aggregate; find a steadily climbing daily trend the aggregate had obscured.
- **Result:** rolled back the migration, fixed the underlying leak, and made daily burn-rate review a standing requirement before any future risk-bearing decision.

## Staff-Level Discussion

An aggregate percentage and a trend are different kinds of information, and this incident is the clean demonstration of why treating the former as sufficient for a risk decision is a category error: 60% remaining is compatible with both "stable, low daily consumption with genuine headroom" and "rapidly climbing consumption about to exhaust the budget," and only the daily breakdown distinguishes them. This generalizes beyond error budgets to any operational metric reported as a single rolled-up number for a decision-gating purpose — a Staff engineer reviewing a team's decision process should ask not just "what number gates this decision" but "does that number's aggregation hide a trend that would change the decision," and push for the underlying trend to be part of the default view rather than something reached for only in hindsight.

## Related Handbook Chapters

- [Performance Methodology and SLO Error Budgets](../syllabus/13-observability/performance-methodology-and-slo-error-budgets.md) — canonical error-budget and burn-rate mechanics used here.
