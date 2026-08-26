---
title: "Bad example: single root cause, no owned action items"
document_type: postmortem
status: draft
incident_date: 2026-08-10
severity: SEV-2
---

# Postmortem: Bad Example — Single Root Cause

> This document deliberately reproduces a common anti-pattern for this pack's linter
> to catch. It is not a real incident record.

## Summary

A database migration locked a hot table for longer than expected.

## Impact

Write requests to the affected table failed for 12 minutes.

## Timeline

- 09:00 — Migration started.
- 09:12 — Migration completed, writes resumed.

## Detection

Error rate alert fired at 09:04.

## Mitigation

Waited for the migration to complete naturally; no intervention was possible mid-migration.

## Root Cause

The migration used `ALTER TABLE` without `CONCURRENTLY`, which took an exclusive
lock for the full duration of the schema change.

## Action Items

- [ ] Use CONCURRENTLY for all future schema migrations on hot tables.
- [ ] Add a migration review checklist item for lock behavior.

## Lessons Learned

Schema migrations need more review.
