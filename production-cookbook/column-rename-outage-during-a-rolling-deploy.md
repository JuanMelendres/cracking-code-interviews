---
title: "Column Rename Outage During a Rolling Deploy"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/06-databases/zero-downtime-schema-migration.md
source: handbook/databases/zero-downtime-schema-migration.md#production-scenarios
---

# Column Rename Outage During a Rolling Deploy

## Context

A team renames `orders.status` to `orders.order_status` via a single `ALTER TABLE ... RENAME COLUMN` migration, deployed alongside application code that references the new name, as part of a standard rolling deploy.

## Symptoms

Mid-rollout, with roughly half the fleet still running the previous version, every request served by an old-version instance starts failing with a column-does-not-exist error.

## Impact

A rolling deploy that should have been zero-downtime causes a partial outage proportional to the fraction of the fleet still running old code — worse the slower the rollout, since the mismatch window is longer.

## Initial Hypotheses

- A bad deploy unrelated to the migration — checked and ruled out; the failing instances are all running the previous, previously-working version.
- A database connectivity issue — checked and ruled out; connections succeed, only the specific query referencing the old column name fails.
- The column rename breaking old code during the mixed-version window — correct.

## Evidence

Every failing request's error references the old column name (`orders.status`), and failures stop entirely once the rollout completes and no old-version instances remain — timing that lines up exactly with the mixed-version window, not with any database-level incident.

## Investigation Timeline

1. **Partial outage observed mid-rollout**, affecting only requests served by instances still running the previous version.
2. **Bad-deploy and connectivity hypotheses ruled out**, since the affected instances were on already-working code and connections succeed.
3. **Error content examined directly**: every failure references the old column name specifically.
4. **Timing correlated against the rollout**: failures start exactly when the migration runs and stop exactly when the rollout completes, confirming the mixed-version window as the cause.

## Root Cause

The single-statement rename changed the schema atomically for all sessions at once, but the application-code rollout is never atomic — a rolling deploy runs old and new code concurrently for its entire duration. Every old-code instance queries a column name that no longer exists the instant the migration runs, regardless of how far along the code rollout is.

## Immediate Mitigation

Roll back the application deploy to the previous version and simultaneously revert the rename, restoring the old column name so all currently-running code (old version) works again.

## Permanent Fix

Redo the change with expand-contract: add `order_status` alongside `status`, deploy application code that dual-writes both and reads from whichever is present, verify all instances are on the new code and both columns are in sync, then drop `status` in a separate, later migration.

## Alternatives Considered

Forcing an instantaneous full-fleet deploy to avoid a mixed-version window. Rejected as fragile and operationally risky — it removes the ability to gradually roll back a bad deploy — compared to accepting a longer but safe expand-contract migration.

## Trade-offs

Expand-contract takes more total calendar time — three separate deploys/migrations instead of one. Accepted, since the alternative is a guaranteed partial outage during every future rolling deploy that touches a renamed or retyped column.

## Prevention

Any column rename or type change should go through expand-contract by default. A lint rule or migration-review checklist flagging direct `RENAME COLUMN` or type-changing `ALTER TABLE` statements against tables serving live traffic would catch this before it reaches production.

## Monitoring and Alerts

- A migration-review gate (the Prevention item above) enforced in CI rather than relying on manual review discipline — a direct `RENAME COLUMN` or type-changing `ALTER TABLE` against a live-traffic table should fail the build, not merely be discouraged.
- Column-does-not-exist error rate as a distinct, first-class alert category during any deploy window, since its timing signature (starts with the migration, ends with rollout completion) is immediately diagnostic once seen, but easy to lump into generic "deploy errors" if not tracked separately.

## Interview Story

This maps to a "fast, catalog-level migration causing an outage" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a routine column rename, deployed with matching application code, caused a partial outage during the rollout instead of the intended zero-downtime change.
- **Task:** explain why an instant, catalog-level schema change caused a real outage.
- **Action:** rule out an unrelated bad deploy and connectivity issue; match the failure's error content to the renamed column and its timing to the mixed-version window; recognize that schema changes are atomic while code rollouts are not.
- **Result:** reverted the direct rename and redid it with expand-contract, and added a migration-review gate to catch the same class of change before it reaches production again.

## Staff-Level Discussion

The core insight this incident teaches is that "fast" and "safe under a rolling deploy" are different properties, and a schema change that is instantaneous at the database level can still cause an outage if the application-code rollout it depends on is not equally instantaneous — which a rolling deploy, by design, never is. Expand-contract exists specifically to decouple these two timelines. A Staff engineer's contribution is recognizing that this isn't a one-off migration mistake to fix and move past — it's a structural mismatch between how schema changes and application deploys propagate, which means the fix belongs in the migration-review process (a lint rule catching direct renames against live tables) rather than in institutional memory that this one team now happens to have.

## Related Handbook Chapters

- [Zero-Downtime Schema Migration](../syllabus/06-databases/zero-downtime-schema-migration.md) — canonical expand-contract methodology used here.
