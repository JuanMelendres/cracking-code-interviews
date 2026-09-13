---
title: "Senior → Staff, Week 11 — Git Internals and Collaboration Workflows"
document_type: study-pack
week: 11
track: senior-to-staff
status: draft
estimated_hours: 6
---

# Week 11 — Git Internals and Collaboration Workflows

## Weekly Outcome

By the end of this week you can explain Git's content-addressable object model concretely (not just "it's a version control tool"), diagnose and prevent the collaboration failures this path's other topics assume you already avoid, and reason about the real risk `git push --force` carries in a shared branch.

## Why This Week Matters

This is the mechanics underneath every collaboration failure this path's other topics assume a Staff engineer can already diagnose and prevent — a lost teammate's commits from a careless force-push, a merge conflict resolved incorrectly, a branching strategy that doesn't scale to the team size. Closes this pack's 11-week program.

## Prerequisites

None beyond ordinary day-to-day Git usage — this week is a depth pass on internals and collaboration risk, not an introduction to Git commands.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Git Internals and Collaboration Workflows](../../../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md) — object model through collaboration workflows |
| Wed | Reproduce the object-model and force-push demos below |
| Thu | Read the cross-referenced cookbook entry |
| Fri–Sat | Interview answer drills and mock interview below |
| Sun | Review checklist below |

## Required Reading

[`syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md`](../../../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md) — read through its Staff-Level Discussion.

## Hands-On Exercises

[`practice/git/`](../../../practice/git/) — real, captured `git` 2.55.0 output against scratch repositories, each a self-contained `setup.sh` plus a `transcript.txt`. Specifically reproduce [`practice/git/object-model/`](../../../practice/git/object-model/)'s content-addressable-storage demo (two files with identical content producing the exact same blob hash).

## Production Cookbook Cross-Reference

- [`force-push-silently-discarding-a-teammates-pushed-commits.md`](../../../production-cookbook/force-push-silently-discarding-a-teammates-pushed-commits.md)

## Interview Answer Drills

Answer, out loud: "explain what a `git push --force` actually does at the object-model level, and why it can silently discard a teammate's work" and "why do two files with identical content produce the same blob hash, and what does that tell you about how Git actually stores data?" before checking the chapter's own Interview Answer Framework.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Design a branch-protection and collaboration policy for a team of 8 engineers that makes the force-push cookbook entry's incident structurally harder to reproduce (e.g., protected-branch rules, `--force-with-lease` as the team default) without blocking legitimate history rewrites on a developer's own feature branch.

## Behavioral Exercise

None this week — Weeks 6 through 8 cover the Leadership & Staff domain and carry this pack's real behavioral work, per [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## Mock Interview

Self-check: given a described "a teammate's pushed commits disappeared after another engineer's force-push," walk through what happened at the object/ref level and what policy would have prevented it, out loud, in under 10 minutes.

## Review Checklist

- [ ] Completed the chapter's own Staff-Level Mastery Checklist.
- [ ] Reproduced the object-model demo, confirming the identical blob hash.
- [ ] Read the cross-referenced cookbook entry and can restate its diagnosis without looking.

## Completion Criteria

- [ ] Can explain Git's content-addressable object model with a real, reproduced example.
- [ ] Can explain exactly what `git push --force` does and does not check before overwriting remote history.
- [ ] Produced the branch-protection policy design above.

## Retrospective

Note whether your own team currently defaults to `--force` or `--force-with-lease` for feature-branch rewrites — this is a fast, concrete self-audit the force-push cookbook entry's own Prevention section directly recommends.

## Next Week

This is the final week of the Senior → Staff program.
