---
title: "Force-Push Silently Discarding a Teammate's Already-Pushed Commits"
document_type: production-cookbook-entry
domain: cloud
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md
source: syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md#production-scenarios
---

# Force-Push Silently Discarding a Teammate's Already-Pushed Commits

## Context

Two engineers push to the same feature branch. The second push is a `git push --force` after a rebase.

## Symptoms

The force-push silently discards the first engineer's already-pushed commits from the remote branch — their local copy still has them, they just no longer exist on `origin`.

## Impact

The affected engineer's already-pushed work disappears from the shared remote branch with no warning at the time of the force-push, discovered only when they next fetch or when their commits are found missing from the branch's history.

## Initial Hypotheses

None stated as separately investigated — the mechanism (a force-push overwriting the remote ref) is understood directly rather than diagnosed through elimination.

## Evidence

The affected engineer's local clone retains the pre-force-push commits if they had fetched them locally; `git reflog` on their own local clone still has the commit in that case. If not, the commits may be recoverable from the force-pusher's own reflog — their local history still contains the old branch tip in `HEAD@{n}` even after force-pushing — within the local reflog expiry window, but only if nobody has run `git gc --prune` in the meantime.

## Investigation Timeline

1. First engineer's commits, previously pushed to the shared feature branch, found missing from `origin` after a second engineer's rebase-and-force-push.
2. Affected engineer's own local clone checked via `git reflog` for the pre-force-push commit — recoverable directly if they had fetched it locally before the force-push occurred.
3. If not locally recoverable, the force-pusher's own local reflog checked for the old branch tip (`HEAD@{n}`), since their local history still retains it even after force-pushing.
4. Recovery time-boxed against the local reflog expiry window and confirmation that `git gc --prune` has not run in the meantime, since either would permanently remove the otherwise-recoverable commit.

## Root Cause

A `git push --force` unconditionally overwrites the remote branch's history with the pusher's local history, with no check for whether the remote branch had moved (i.e., someone else had pushed) since the force-pusher's last fetch.

## Immediate Mitigation

Recover the discarded commits from whichever reflog (affected engineer's or force-pusher's) still retains the pre-force-push tip, within the local reflog expiry window and before any `git gc --prune` removes it.

## Permanent Fix

Use `git push --force-with-lease` instead of `--force` — it refuses the push if the remote branch has moved since the pusher's last fetch, catching exactly this race — and enable branch protection rules requiring PRs, which most platforms can configure to reject force-pushes entirely on protected branches.

## Alternatives Considered

None recorded beyond `--force-with-lease` and branch protection — both are presented together as the direct, sufficient prevention rather than alternatives to weigh against each other.

## Trade-offs

None stated — `--force-with-lease` provides the same rebase-and-overwrite capability as `--force` with an added safety check, at no meaningful cost to the workflow it replaces.

## Prevention

Adopt `git push --force-with-lease` as the team default for any force-push, and configure branch protection rules to reject force-pushes on shared/protected branches outright.

## Monitoring and Alerts

- Configure the Git hosting platform's branch protection to reject any bare `--force` push on shared branches outright, converting this incident's root cause into a rejected operation rather than a silent overwrite, with no reliance on individual engineers remembering to use `--force-with-lease`.
- Where a hosting platform surfaces force-push events in its audit log or webhook stream, alert on any force-push to a shared branch that protection rules don't already block (e.g., branches not yet covered by protection), giving visibility into the exact moment a potentially destructive push occurs rather than discovering it only when commits are found missing.
- Document the recovery procedure (checking both parties' reflogs, and the `git gc --prune` time pressure) as a standing runbook entry, since the recovery window is time-limited and an engineer under the stress of "my commits just vanished" benefits from a pre-written procedure rather than reconstructing the reflog-recovery steps under pressure.

## Interview Story

This maps directly to "what happens when two people push to the same branch and one force-pushes" backed by the actual Git mechanics involved. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a teammate's already-pushed commits vanished from a shared feature branch after another engineer rebased and force-pushed.
- **Task:** recover the lost work, and prevent the same silent overwrite from happening again.
- **Action:** checked both engineers' local reflogs for the pre-force-push branch tip, recovering the commits within the local reflog expiry window before any garbage collection removed them.
- **Result:** adopted `git push --force-with-lease` as the team default (which refuses to push over a remote that has moved unexpectedly) and enabled branch protection rules rejecting force-pushes on shared branches outright.

## Staff-Level Discussion

The choice between `--force` and `--force-with-lease` is a small, single-flag difference with an outsized safety implication: `--force-with-lease` costs nothing in the common case (a rebase where nobody else has pushed) and prevents exactly the scenario here (a rebase where someone else *has* pushed) by checking the remote hasn't moved since the pusher's last fetch — it is close to a strictly-dominant default, which is why a Staff engineer establishing team Git conventions should make it the default rather than leaving it as an individual engineer's judgment call under time pressure. The recovery mechanism (reflog) is itself worth understanding at a systemic level: it is a local, time-limited, `gc`-vulnerable safety net, not a durable one, which means the actual, durable fix has to prevent the destructive push from succeeding in the first place — branch protection rejecting force-pushes on shared branches — rather than relying on every future incident being caught and recovered in time before reflog expiry or a garbage-collection pass removes the only remaining copy.

## Related Handbook Chapters

- [Git Internals and Collaboration Workflows](../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md) — canonical explanation of reflog recovery, `--force-with-lease`, and branch protection this incident reproduces.
