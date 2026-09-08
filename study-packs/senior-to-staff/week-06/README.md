---
title: "Senior → Staff, Week 6 — Provisioning Ahead of Load, Multiplying the Team"
document_type: study-pack
week: 6
track: senior-to-staff
status: draft
estimated_hours: 7
---

# Week 6 — Provisioning Ahead of Load, Multiplying the Team

## Weekly Outcome

By the end of this week you can apply Little's Law to a real staffing or capacity decision instead of reacting to load after it arrives, and you can describe how you'd run a mentoring relationship — calibrating feedback, delegating a stretch assignment, deciding when to intervene — as a deliberate practice rather than an incidental byproduct of seniority.

## Why This Week Matters

This week is deliberately a transition. [Capacity Planning & Headroom](../../../syllabus/16-performance-jvm/capacity-planning-and-headroom.md) closes the technical/architecture block this pack has run since Week 1 — "provisioning ahead of load, not reacting to it," per the learning path. [Mentoring and Developing Others](../../../syllabus/19-leadership-staff/mentoring-and-developing-others.md) opens the Leadership & Staff block that runs through Week 8 — "multiplying the team's effectiveness, not just personal output." From here on, this pack's Behavioral Exercise section stops being empty: the learning path's own Completion Criteria require a real or realistic story for every Leadership & Staff topic.

## Prerequisites

Weeks 1–5 of this pack. [Mid → Senior](../../mid-to-senior/README.md) Week 2 (JVM internals) for capacity planning's throughput and headroom vocabulary.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Capacity Planning & Headroom](../../../syllabus/16-performance-jvm/capacity-planning-and-headroom.md) — read through its Staff-Level Discussion |
| Wed | Reproduce the capacity-planning demos below |
| Thu–Fri | [Mentoring and Developing Others](../../../syllabus/19-leadership-staff/mentoring-and-developing-others.md) — read through its L4 section |
| Sat | [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md) and the mentoring-specific behavioral chapter below |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Capacity Planning & Headroom | [`syllabus/16-performance-jvm/capacity-planning-and-headroom.md`](../../../syllabus/16-performance-jvm/capacity-planning-and-headroom.md) |
| 2 | Mentoring and Developing Others | [`syllabus/19-leadership-staff/mentoring-and-developing-others.md`](../../../syllabus/19-leadership-staff/mentoring-and-developing-others.md) |

## Hands-On Exercises

[`practice/java/performance/capacity-planning-and-headroom/`](../../../practice/java/performance/capacity-planning-and-headroom/) — `LittlesLawDemo.java` and `SaturationPointDemo.java`. The chapter itself states every number it cites is real, measured output from these two programs under controlled real load, not mocked timing. Predates this pack's construction; not re-executed here.

## Production Cookbook Cross-Reference

- [`notification-service-failing-at-launch-for-skipping-capacity-estimation.md`](../../../production-cookbook/notification-service-failing-at-launch-for-skipping-capacity-estimation.md) — a design document approved and shipped without an explicit capacity estimation section, jumping straight from a one-paragraph problem statement to an architecture diagram. Pairs directly with Topic 1.

Read it after finishing the chapter and confirm you can restate the diagnosis without looking.

## Interview Answer Drills

Answer, out loud, unprompted: "how do you size a system's capacity before launch, and what's Little's Law got to do with it?" and "tell me about a time you helped a more junior engineer grow — what specifically did you do, and how did you know it worked?" before checking the capacity chapter's own Interview Answer Framework and the mentoring chapter's own Interview Framing section.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Take the `notification-service-failing-at-launch` scenario and write the capacity estimation section that should have preceded the architecture diagram, applying Little's Law to the stated (or a reasonably assumed) request volume.

## Behavioral Exercise

Read [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md) and [Mentoring and Developing Others](../../../syllabus/20-interview-preparation/behavioral/07-mentoring-and-developing-others.md) (the behavioral-narrative counterpart to this week's Topic 2 chapter — that one teaches the underlying skill, this one teaches turning it into a STAR answer). Then draft or identify a real or realistic story for a time you mentored, sponsored, or deliberately developed another engineer, and fill in the "Mentoring and Developing Others" slot in your own competency matrix per Story Portfolio Design's own method.

## Mock Interview

Self-check: tell your mentoring story from the Behavioral Exercise above out loud, in the 2-minute STAR length, then check it against [STAR Framework and Delivery Mechanics](../../../syllabus/20-interview-preparation/behavioral/01-star-framework-and-delivery.md)'s own delivery checklist.

## Review Checklist

- [ ] Completed the capacity-planning chapter's own Staff-Level Mastery Checklist.
- [ ] Reproduced both real demos above.
- [ ] Read the cross-referenced cookbook entry and can restate its diagnosis without looking.
- [ ] Have a filled mentoring slot in your story portfolio, not a placeholder.

## Completion Criteria

- [ ] Can apply Little's Law to a stated capacity scenario, with real or reasonably assumed numbers.
- [ ] Produced the capacity estimation section above.
- [ ] Have a real or realistic mentoring story ready in the 2-minute STAR length, per this week's Behavioral Exercise.

## Retrospective

Note whether your mentoring story leans more toward coaching (working alongside someone) or sponsorship (advocating for them when they weren't in the room) — the mentoring chapter distinguishes the two explicitly, and most engineers have far more coaching stories ready than sponsorship ones.

## Next Week

[Week 7 — Moving an Organization](../week-07/README.md).
