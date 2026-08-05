---
title: "Unowned Load-Testing Script Letting a Regression Ship"
document_type: production-cookbook-entry
domain: testing
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/testing/performance-and-load-testing-methodology.md
source: handbook/testing/performance-and-load-testing-methodology.md#production-scenarios
---

# Unowned Load-Testing Script Letting a Regression Ship

## Context

A team's performance-testing practice consists of one engineer manually running a load-testing script "when they remember to," with no defined trigger or ownership.

## Symptoms

Six months in, a review finds the script hasn't been run since before three significant releases, one of which introduced a real, measurable latency regression that shipped undetected.

## Impact

A latency regression that a functioning performance-testing practice should have caught reached production and stayed there, undetected by the team's own tooling, for the duration of three release cycles.

## Initial Hypotheses

- The load-testing script itself is broken or produces unreliable results — investigated as a first check on the tooling's own validity.
- The practice's ownership and trigger model — an ad hoc, unscheduled, single-engineer responsibility — is the actual gap, not the tooling itself — correct.

## Evidence

A review of the script's run history shows it hasn't been executed since before three significant releases, with no defined trigger (a release gate, a scheduled cadence) ever having existed to prompt a run.

## Investigation Timeline

1. **Latency regression discovered** in production, well after it shipped.
2. **Load-testing script itself checked** for correctness or reliability issues, coming back functional when manually re-run.
3. **Run history reviewed**, revealing the script hadn't executed since before three significant releases.
4. **Root cause identified**: no defined trigger or accountable ownership ever existed for running it — it depended entirely on one engineer's memory.

## Root Cause

The performance-testing practice consisted of one engineer manually running a load-testing script "when they remember to," with no defined trigger or ownership, and it hadn't been run since before three significant releases, one of which introduced the shipped regression.

## Immediate Mitigation

Run the load-testing script immediately against the current release to establish a current baseline and confirm the scope of the regression.

## Permanent Fix

Make performance testing a defined, owned step in the release process — a required gate for a defined class of changes, or a scheduled cadence with an accountable owner — converting it from an easily forgotten manual habit into a structural part of the release process the way a unit-test suite already is.

## Alternatives Considered

Simply reminding the engineer to run it more consistently. Rejected as treating the symptom — an informal reminder has already failed once and provides no structural guarantee against the same lapse recurring.

## Trade-offs

Making performance testing a required release gate adds a step, and potentially time, to every qualifying release. Accepted, since the alternative — as demonstrated — is regressions shipping and going undetected for multiple release cycles.

## Prevention

Any quality practice (performance testing, security scanning, dependency auditing) that depends on one individual's memory rather than a defined trigger and accountable owner should be flagged during process review as a latent risk, whether or not it has caused a visible incident yet.

## Monitoring and Alerts

- The load-testing script's own execution history tracked and alerted on directly — an absence of runs for longer than the defined cadence should itself trigger an alert, rather than requiring a manual review to notice the gap, as happened here.
- Once the practice is a defined release gate, its pass/fail status surfaced in the same release dashboard as other required checks (unit tests, security scans), so its absence or failure is as visible as any other release blocker.

## Interview Story

This maps to "your performance testing practice quietly stopped happening, how do you find out and fix it." Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a real latency regression shipped and went undetected for three release cycles because the team's only performance-testing practice depended on one engineer remembering to run a script.
- **Task:** diagnose why a nominally existing practice failed to catch a real regression.
- **Action:** verify the script itself still works correctly; review its run history to find it hadn't executed in months; identify the lack of a defined trigger and ownership as the actual gap, not a tooling defect.
- **Result:** converted performance testing from an informal, memory-dependent habit into a defined, owned release-process step — a required gate or scheduled cadence — with its own status visible alongside other release checks.

## Staff-Level Discussion

The failure here isn't technical at all — the script worked correctly every time it was run — which is precisely why it's a valuable incident to study: a quality practice with no defined trigger and no accountable owner degrades silently and predictably, not through any single dramatic failure but through ordinary competing priorities eroding an informal habit over time. This generalizes to any quality gate a team relies on: "someone runs it when they remember" is not a process, it's a hope, and it will eventually fail exactly the way this one did. A Staff engineer reviewing a team's quality practices should specifically look for gates that lack both a defined trigger (a release event, a schedule) and a clear owner, and treat any gap found as a structural risk worth fixing proactively, since by the time it causes a visible incident the cost has already been paid.

## Related Handbook Chapters

- [Performance and Load Testing Methodology](../handbook/testing/performance-and-load-testing-methodology.md) — canonical load-testing practice and ownership discipline used here.
