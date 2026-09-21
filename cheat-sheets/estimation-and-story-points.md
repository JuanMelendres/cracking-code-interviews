---
title: "Cheat Sheet: Estimation and Story Points"
slug: estimation-and-story-points
document_type: cheat-sheet
domain: 18-engineering-practices
topic_id: T-1805
canonical: ../syllabus/18-engineering-practices/estimation-and-story-points.md
last_updated: 2026-09-21
---

# Estimation and Story Points: Relative Sizing, Velocity, and Its Misuses

**Canonical chapter:** [`syllabus/18-engineering-practices/estimation-and-story-points.md`](../syllabus/18-engineering-practices/estimation-and-story-points.md)

## Core Mental Model

A story point is a relative size comparison, not a disguised time unit. Velocity is a trend valid only within one team's own history — never a cross-team comparison, because each team calibrates its scale independently.

## Essential Definitions

- **Story point** — a relative estimate of size, calibrated against a team's own reference stories, not a time unit.
- **Velocity** — story points a team actually completes per sprint, tracked over time as a planning signal.
- **Fibonacci-like scale (1,2,3,5,8,13,20,40,100)** — deliberately non-linear; gaps widen because estimation precision genuinely degrades for bigger items.

## Decision Table

| Situation | Valid use | Invalid use |
|---|---|---|
| Planning one team's next sprint | Use that team's own recent velocity trend | — |
| Comparing two teams' output | — | Comparing raw velocity numbers directly |
| A story too big to confidently size | Split it before estimating | Force-fitting it to the top of the scale |

## Common Pitfalls

- Treating a story point as "about N hours" and multiplying to get a schedule — defeats the purpose of relative estimation.
- Comparing two different teams' velocity numbers directly.
- Setting a fixed velocity target for a team to "hit" — incentivizes point inflation over honest estimation.

## Interview Answer Skeleton

**30-sec:** A story point is a relative size comparison, calibrated only within one team. Velocity is a trend valid within that same team's own history — comparing it across teams is invalid, since each team's scale was never the same unit.

**2-min:** Add: real demo proof — two teams estimating the identical 135 hours of real work, under two different but each internally consistent calibrations, produce velocities of 61 and 127 points respectively — a 2.1x difference for delivering exactly the same work.

**Staff-level framing:** For genuine cross-team throughput comparison, use real outcomes (cycle time, commitment accuracy) instead of raw velocity — any org-wide "productivity" dashboard built on velocity numbers is a red flag.

## Related

- syllabus/18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md
- syllabus/18-engineering-practices/working-with-legacy-code.md
