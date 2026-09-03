---
title: "Rollback Runbook That Was Already Impossible by the Time It Was Needed"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/17-architecture/strangler-fig-and-migration-patterns.md
  - ../syllabus/17-architecture/architecture-decision-records.md
source: handbook/architecture/strangler-fig-and-migration-patterns.md#production-scenarios
---

# Rollback Runbook That Was Already Impossible by the Time It Was Needed

## Context

A payments migration cut reads over to a new system, with a runbook stating "rollback to legacy if a critical issue is found." Dual-write to legacy was in place during the migration as the mechanism intended to make that rollback possible.

## Symptoms

Three weeks after cutting over payment reads to the new system, a subtle currency-rounding bug was discovered affecting a small percentage of international transactions. When the team attempted the documented rollback, historical orders from the prior two weeks were missing from legacy entirely.

## Impact

The team could not execute the documented rollback plan at the exact moment it was needed, converting what should have been a controlled rollback into an unplanned, slow, error-prone data-reconstruction effort under incident pressure.

## Initial Hypotheses

A bug in the rollback procedure itself.

## Evidence

The actual migration timeline showed dual-write to legacy had been turned off exactly one week after cutover, once the team felt "confident" — a full two weeks before the rounding bug was discovered.

## Investigation Timeline

1. Currency-rounding bug discovered three weeks post-cutover, affecting a small percentage of international transactions.
2. Documented rollback-to-legacy runbook invoked as the intended response.
3. Rollback attempt found historical orders from the prior two weeks missing from legacy, prompting a "bug in the rollback procedure" hypothesis.
4. Actual migration timeline reviewed, revealing dual-write to legacy had been disabled exactly one week after cutover — two full weeks before the rounding bug was found.
5. Diagnosis reached: the rollback runbook had never been tested against the actual state of the system at the time it would realistically be needed, and "rollback" had silently stopped being possible the moment dual-write was disabled, with no monitoring or alert marking that transition.

## Root Cause

The rollback runbook had never been tested against the actual state of the system at the time it would realistically be needed; "rollback" had silently stopped being possible the moment dual-write was disabled, with no monitoring or alert marking that transition.

## Immediate Mitigation

Manually reconstructed the missing two weeks of orders from application logs and the new system's own database, a slow, error-prone, three-day effort that a real rollback would have made unnecessary.

## Permanent Fix

Established a fixed, deliberately conservative rollback-safety window (dual-write stays on for a minimum of 30 days post-cutover, not "until we feel confident") and added an explicit dashboard tracking "time remaining in rollback-safety window" so the option's expiration is visible before it's needed, not discovered when it's already too late.

## Alternatives Considered

Continuing to disable dual-write based on a subjective "confidence" judgment — implicitly rejected in favor of a fixed, calendared minimum window, since the "confidence" criterion was exactly what produced the incident.

## Trade-offs

30 days of double the write load and double the storage cost, deliberately, in exchange for a rollback option that's still real when needed.

## Prevention

The migration runbook template now requires a named, calendared rollback-safety window and an explicit sign-off before disabling dual-write, not a vague "once we're confident" criterion.

## Monitoring and Alerts

- Add the "time remaining in rollback-safety window" dashboard (the Permanent Fix's own addition) as a required, actively-monitored artifact for every migration using dual-write, not an optional nicety — this is the specific instrumentation that would have made the disable-dual-write decision visible and reviewable before this incident occurred.
- Alert explicitly at the moment dual-write is disabled for any migration, requiring an explicit sign-off event logged alongside it — converting a silent configuration change into an auditable, deliberate decision with a visible timestamp.
- Track "critical issues found per week since cutover" as a standing metric during any migration's rollback-safety window, giving the team direct evidence for whether the calendared window length is actually long enough for the kinds of subtle bugs (like a currency-rounding issue affecting a small transaction percentage) that take real production volume and time to surface.

## Interview Story

This maps directly to "how do you roll back mid-migration" arriving as a real incident where the rollback plan quietly stopped being real before anyone needed it. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a documented rollback-to-legacy runbook failed when actually needed, three weeks after a payments migration cutover, because two weeks of historical orders were missing from the legacy system.
- **Task:** find out why the rollback failed and prevent the same gap from recurring.
- **Action:** traced the migration timeline and found dual-write had been disabled based on a subjective "feeling confident" judgment, a full two weeks before the issue that actually required rollback was discovered.
- **Result:** manually reconstructed the missing data as an immediate fix, then replaced the subjective disable-criterion with a fixed, calendared 30-day rollback-safety window and a dashboard tracking time remaining in that window, made visible before it expires rather than discovered afterward.

## Staff-Level Discussion

The honest answer to "how do you roll back mid-migration" is that the rollback-safety window's duration has to be planned *before* migration starts, as a deliberate, monitored commitment — not an ad hoc decision made under the pressure of feeling done with the migration. The specific failure mode here — a safety mechanism (dual-write) silently expiring with no alert marking the transition — generalizes to any "insurance" mechanism kept around during a risky change: a feature flag left in place "just in case," a database backup retention policy, a canary deployment's rollback window. Each of these degrades from "actually protective" to "documentation of a protection that no longer exists" the moment someone disables it based on a subjective confidence judgment rather than a calendared, monitored, explicitly-signed-off decision, and the cost of that gap is invisible until precisely the moment the protection is needed — which is also the worst possible moment to discover it. A Staff engineer designing any migration should treat the rollback window's expiration as a first-class, monitored event, not an implementation detail buried in a runbook that nobody re-reads after cutover.

## Related Handbook Chapters

- [Strangler Fig and Migration Patterns](../syllabus/17-architecture/strangler-fig-and-migration-patterns.md) — canonical dual-write and rollback-safety-window mechanics this incident reproduces.
- [Architecture Decision Records](../syllabus/17-architecture/architecture-decision-records.md) — the documentation discipline that should record a migration's rollback-safety window and disable criteria explicitly, rather than leaving them as an undocumented judgment call.
