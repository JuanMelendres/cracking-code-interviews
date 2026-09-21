---
title: "Sprint 14 Retrospective — Order Service Squad"
date: 2026-09-18
facilitator: Priya
participants: [Priya, Marcus, Deion, Wei, Alex]
---

# Sprint 14 Retrospective — Order Service Squad

## What Went Well

- The read-model migration shipped with zero production incidents, because the team
  wrote a real parity test comparing old and new query output on real data before
  cutting over.
- Pairing on the flaky integration test finally found the real root cause (a shared
  test database not being reset between runs), instead of just adding retries again.

## What Didn't Go Well

- Three separate PRs this sprint were blocked for over a day waiting on a single
  reviewer who was out sick, with no backup reviewer identified.
- The on-call handoff at the start of the sprint missed two open incidents from the
  previous rotation, because the handoff doc wasn't updated before the old on-call
  went offline.

## Action Items

- [ ] Add a second designated backup reviewer for the order-service repo — Owner: Priya, Deadline: 2026-09-25
- [ ] Add an explicit "confirm handoff doc is current" step to the on-call runbook — Owner: Wei, Deadline: 2026-09-22

## Carried Over From Last Retro

- Investigate the recurring Tuesday-morning deploy-pipeline slowdown — Status: Done (root cause was a scheduled CI cache-eviction job; rescheduled to run outside deploy windows)
- Reduce the order-service PR review SLA from 2 days to 1 day — Status: Still open (blocked on the backup-reviewer action item above)
