---
title: "Bad example: blame-coded language"
document_type: postmortem
status: draft
incident_date: 2026-08-10
severity: SEV-2
---

# Postmortem: Bad Example — Blame-Coded Language

> This document deliberately reproduces a common anti-pattern for this pack's linter
> to catch. It is not a real incident record.

## Summary

The deploy pipeline pushed a broken config to production.

## Impact

The service was degraded for 30 minutes.

## Timeline

- 10:00 — Deploy started.
- 10:05 — Errors spike.
- 10:30 — Rolled back.

## Detection

An on-call engineer noticed the error rate manually.

## Mitigation

Rolled back the deploy.

## Contributing Factors

- The on-call engineer failed to test the config change in staging before deploying
  to production, which should have caught the issue.

## Blameless Analysis

This wouldn't have happened if the engineer had been more careful and had not
forgotten to run the staging checklist.

## Action Items

- [ ] Add a mandatory staging gate to the deploy pipeline — Owner: platform-team — Due: 2026-09-01

## Lessons Learned

Engineers need to be more careful with production changes.
