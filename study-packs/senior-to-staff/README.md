---
title: "Senior → Staff Study Pack — Index"
document_type: study-pack-index
status: draft
last_updated: 2026-09-08
---

# Senior → Staff Study Pack

The scheduled, week-by-week version of [`syllabus/00-overview/learning-paths/senior-to-staff.md`](../../syllabus/00-overview/learning-paths/senior-to-staff.md) — that path is the *content* (16 named topics, in sequence, each with a stated reason for its place in the order); this is the *schedule* built on top of it, the same relationship [`study-packs/mid-to-senior/`](../mid-to-senior/README.md) has to its own learning path.

## Audience and scope

A Senior engineer who already has L3 internals depth and production-debugging judgment — [Mid → Senior](../mid-to-senior/README.md) is assumed complete, not retaught. This pack builds the L4 (Staff) systemic, organizational, and long-horizon reasoning a Staff loop specifically probes: not "how does this work" but "should we do this, what changes the answer, and how do we get an organization to move." It stops at L4 for every topic, per the learning path's own stated goal.

**No duplicated content.** Every week below links to its topics' real, canonical `syllabus/` chapters and, where a genuine match exists, real `practice/` demos and already-diagnosed [`production-cookbook/`](../../production-cookbook/README.md) entries — this pack adds scheduling, sequencing rationale, and lightweight review checkpoints on top of material that already exists in full, per this repository's own no-duplication rule.

## Why this exists

[Mid → Senior](../mid-to-senior/README.md) has an operational, week-by-week layer of its own. [Senior → Staff](../../syllabus/00-overview/learning-paths/senior-to-staff.md) — the direct continuation into L4 — did not, until now. This pack fills that gap the same way `study-packs/mid-to-senior/` did for its own path. See [`study-packs/README.md`](../README.md) for how all the programs in this repository relate.

## Weeks

| Week | Theme | Topics | Estimated hours |
|---|---|---|---|
| [01](week-01/README.md) | Should we split it? | Microservice Decomposition and the Monolith Trade-off; The Modular Monolith as a Deliberate Choice | 6–8h |
| [02](week-02/README.md) | Read/write separation and incremental migration | CQRS: Read/Write Separation; Strangler Fig, Anti-Corruption Layer, and Migration Patterns | 6–8h |
| [03](week-03/README.md) | Debt as an economic decision, decisions as organizational memory | Technical Debt and Evolutionary Architecture; Architecture Decision Records | 6–8h |
| [04](week-04/README.md) | Failure and scale across regions | Multi-Region, Failover, and Disaster Recovery; Data Partitioning and Consistent Hashing | 6–8h |
| [05](week-05/README.md) | What to store, what it costs | Storage Selection Trade-offs; Cloud Cost and Scaling Economics | 6–8h |
| [06](week-06/README.md) | Provisioning ahead of load, multiplying the team | Capacity Planning & Headroom; Mentoring and Developing Others | 6–8h |
| [07](week-07/README.md) | Moving an organization | Cross-Team Influence Without Authority; Leading Migrations and Large-Scale Technical Change | 6–8h |
| [08](week-08/README.md) | Advocacy and the review process itself | Technical Debt: Prioritization and Advocacy; Design Reviews and RFCs as an Organizational Practice | 6–8h |

**Total: ~48–64 hours across 8 weeks**, matching the learning path's own ~8-week, part-time estimate.

Week 6 is deliberately a transition: it closes the technical/architecture block (Topics 1–11) and opens the Leadership & Staff block (Topics 12–16). Weeks 6 through 8, covering the Leadership & Staff domain, each carry a real Behavioral Exercise — the learning path's own Completion Criteria require a real or realistic story for every Leadership & Staff topic, per [Story Portfolio Design](../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## After this pack

There is no further pack in this repository's ladder — Senior → Staff is the top of the sequence. Return to [`syllabus/00-overview/learning-paths/`](../../syllabus/00-overview/learning-paths/) for the full set of paths, or to the [Mastery Model](../../syllabus/00-overview/mastery-model.md) for how L4 is assessed across a whole interview loop rather than one topic at a time.
