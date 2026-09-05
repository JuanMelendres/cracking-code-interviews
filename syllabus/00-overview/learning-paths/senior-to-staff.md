---
title: "Learning Path: Senior → Staff"
document_type: learning-path
status: draft
version: 1.0
last_updated: 2026-09-05
source: 00-project/syllabus-transformation-plan.md §6
---

# Learning Path: Senior → Staff

**Audience:** a Senior engineer who already has internals depth and production judgment, building the systemic, organizational, and long-horizon reasoning a Staff loop specifically probes.

**Goal:** reach L4 (Staff) — reasoning about systemic consequences and defending an architectural decision to a skeptical peer — on every topic below.

**Time budget:** ~8 weeks, part-time.

**Stops at:** L4. This path assumes [Mid → Senior](mid-to-senior.md)'s L3 material is already solid; it does not re-cover internals depth.

Unlike [Mid → Senior](mid-to-senior.md), this path names individual topics rather than whole domains — the domains here (`17-architecture`, `19-leadership-staff`) are small and every topic in them is genuinely Staff-relevant, so the curation *is* close to the full domain, stated explicitly rather than left implicit.

## Sequence

| # | Topic | Domain | Why here |
|---|---|---|---|
| 1 | [Microservice Decomposition and the Monolith Trade-off](../../17-architecture/microservice-decomposition-and-monolith-tradeoff.md) | Architecture | The single highest-IWI Staff-tier topic in the register — "should we split this" is the canonical Staff system-design question. |
| 2 | [The Modular Monolith as a Deliberate Choice](../../17-architecture/modular-monolith-as-a-deliberate-choice.md) | Architecture | The direct follow-up once Topic 1's answer is "not yet" — how to stay well-organized without splitting. |
| 3 | [CQRS: Read/Write Separation](../../17-architecture/cqrs-read-write-separation.md) | Architecture | A named judgment trap — the expected Staff answer is knowing when *not* to reach for it. |
| 4 | [Strangler Fig, Anti-Corruption Layer, and Migration Patterns](../../17-architecture/strangler-fig-and-migration-patterns.md) | Architecture | The standard follow-up to any legacy-system design question — incremental extraction, not a rewrite. |
| 5 | [Technical Debt and Evolutionary Architecture](../../17-architecture/technical-debt-and-evolutionary-architecture.md) | Architecture | The economic-framing reflex a Staff engineer is expected to have, backed by fitness functions as the durable mechanism. |
| 6 | [Architecture Decision Records](../../17-architecture/architecture-decision-records.md) | Architecture | Organizational memory — decisions surviving past the meeting where they were made. |
| 7 | [Multi-Region, Failover, and Disaster Recovery](../../10-distributed-systems/multi-region-failover-and-disaster-recovery.md) | Distributed Systems | RPO/RTO trade-offs and split-brain risk at the scale a Staff engineer is expected to own. |
| 8 | [Data Partitioning and Consistent Hashing](../../10-distributed-systems/data-partitioning-and-consistent-hashing.md) | Distributed Systems | The scaling decision underneath most "how would this handle 10x load" follow-ups. |
| 9 | [Storage Selection Trade-offs](../../11-system-design/storage-selection-tradeoffs.md) | System Design | The access-pattern-first method this whole path's other trade-off decisions reuse. |
| 10 | [Cloud Cost and Scaling Economics](../../15-cloud/cloud-cost-and-scaling-economics.md) | Cloud | Cost as an explicit, quantified architectural dimension — a frequently-missing Staff signal. |
| 11 | [Capacity Planning & Headroom](../../16-performance-jvm/capacity-planning-and-headroom.md) | Performance & JVM | Provisioning ahead of load, not reacting to it — Little's Law applied to a real staffing decision. |
| 12 | [Mentoring and Developing Others](../../19-leadership-staff/mentoring-and-developing-others.md) | Leadership & Staff | Multiplying the team's effectiveness, not just personal output. |
| 13 | [Cross-Team Influence Without Authority](../../19-leadership-staff/cross-team-influence-without-authority.md) | Leadership & Staff | Driving direction in rooms with no formal authority — a defining Senior/Staff differentiator. |
| 14 | [Leading Migrations and Large-Scale Technical Change](../../19-leadership-staff/leading-migrations-and-large-technical-change.md) | Leadership & Staff | Sequencing and risk management at organizational scale, directly building on Topic 4. |
| 15 | [Technical Debt: Prioritization and Advocacy](../../19-leadership-staff/technical-debt-prioritization-and-advocacy.md) | Leadership & Staff | The organizational-advocacy half of Topic 5's technical framing. |
| 16 | [Design Reviews and RFCs as an Organizational Practice](../../19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md) | Leadership & Staff | Shaping decisions through the review process itself, at scale. |

## Completion criteria

- Can defend each architecture decision above to a skeptical peer, naming the specific condition that would change the answer (per each chapter's own Staff-Level Discussion section).
- Can connect at least three of the topics above across domains in a single answer (e.g., a migration story that cites both Strangler Fig's rollback-safety window and Cross-Team Influence's trust-building mechanism) — this cross-domain synthesis is itself the L4 signal, not any one topic in isolation.
- Has a real or realistic story ready for each Leadership & Staff topic, per [Story Portfolio Design](../../20-interview-preparation/behavioral/02-story-portfolio-design.md).
