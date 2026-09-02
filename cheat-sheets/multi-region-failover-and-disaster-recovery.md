---
title: "Cheat Sheet: Multi-Region, Failover, and Disaster Recovery"
slug: multi-region-failover-and-disaster-recovery
document_type: cheat-sheet
domain: system-design
topic_id: T-814
canonical: ../handbook/system-design/multi-region-failover-and-disaster-recovery.md
last_updated: 2026-09-02
---

# Multi-Region, Failover, and Disaster Recovery

**Canonical chapter:** [`handbook/system-design/multi-region-failover-and-disaster-recovery.md`](../handbook/system-design/multi-region-failover-and-disaster-recovery.md)

## Core Mental Model

RPO is how much you're willing to lose; RTO is how long you're willing to be down — every DR pattern is a different, honest price tag on that pair of numbers. A pattern that buys a smaller RPO or RTO always costs more to run continuously; there is no free tier. The moment a design conversation reaches "we need multi-region," the next sentence should be a number — an RPO in minutes-or-seconds and an RTO in minutes-or-hours the business actually signed off on — not "as fast as possible," which is an unbounded budget, not a target.

## Essential Definitions

- **RPO (Recovery Point Objective)** — the maximum acceptable data loss, measured as a time window.
- **RTO (Recovery Time Objective)** — the maximum acceptable downtime before the system is serving again.
- **The four DR tiers (AWS's standard model)** — backup-restore, pilot light, warm standby, multi-site active-active — ordered cheapest-and-slowest to most-expensive-and-fastest.
- **Split-brain** — a failover promotes a new primary while the old primary is still alive and reachable by some clients (merely partitioned, not dead); both accept writes independently and histories diverge.
- **Fencing (STONITH — Shoot The Other Node In The Head)** — forcibly and verifiably cutting off the old primary's ability to accept writes *before* promoting anything else; not asking nicely, guaranteeing it.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| No business-approved RPO/RTO exists yet | Get one before designing anything — "as fast as possible" is not a target |
| Cost must be minimized and RPO/RTO tolerance is loose | Backup & restore or pilot light |
| RPO needs to be near zero and budget allows a full running standby | Warm standby |
| Best possible RTO is required and concurrent cross-region writes are acceptable | Multi-site active-active — but name the concrete consistency mechanism (CRDTs, partitioned ownership, accepted last-writer-wins loss) |

**DR tier trade-offs:**

| DR tier | Real cost | RPO | RTO |
|---|---|---|---|
| Backup & restore | Lowest | Bounded by archive interval (can be worse than configured under light load) | Worst — infra provisioning from scratch |
| Pilot light | Low | Similar to a continuously-replicating data tier | Better — compute only, no data-tier bootstrap |
| Warm standby | Moderate | Near zero under real load (measured) | Fast (measured ~1s) |
| Multi-site active-active | Highest | Near zero for the surviving region | Fastest — often no promotion step |

## Key Numbers (real, executed on PostgreSQL 16 via Docker)

- Warm standby (streaming replication): a real primary destroyed (container + volume) mid-burst, 0.4s into a 150,000-statement burst. Result: 2,437 rows committed, 0 lost. RTO from destruction to first accepted write on the promoted standby: 0.98 seconds.
- Log-shipping (WAL archiving, `archive_timeout=3`): 10 rows written over 10 real seconds, primary destroyed. Result: only the startup WAL segment ever archived — 10 of 10 rows genuinely unrecoverable, despite the 3-second configured target.
- Split-brain reproduction: unfenced failover during a real network partition — old primary accepted `accepted-by-old-primary-unaware-of-failover`, new primary independently accepted `accepted-by-new-primary-after-failover`; histories genuinely diverged. Fenced re-run (`docker pause`): the identical write attempt was refused (`Container ... is paused`) before reaching PostgreSQL.

## Common Pitfalls

- Quoting an RTO/RPO target as "as fast as possible" instead of a real, business-approved number.
- Assuming a configured value like `archive_timeout` equals real observed behavior rather than testing it.
- Treating replication as backup — a bug or bad deploy replicates just as faithfully as a legitimate write; DR for logical/application failures needs a separate mechanism (point-in-time recovery, immutable backups).
- Reaching for multi-site active-active by default for its best numbers, without a stated business need or a real plan for write conflicts.
- Designing a failover procedure that promotes based on "looks unreachable" without a real fencing step.

## Interview Answer Skeleton

**30-sec:** DR is choosing, ahead of time, how much data loss (RPO) and downtime (RTO) a full region outage is allowed to cost, and picking the cheapest of four tiers that meets both numbers — with a real, tested, automated fencing step in the failover procedure so a partitioned-but-alive old primary can never keep writing after a new one is promoted.

**2-min:** Add the contrasting real measurements: warm standby lost 0 rows destroying a primary mid-burst; log-shipping with `archive_timeout=3` lost 10 of 10 rows over 10 seconds because no segment closed in time. Add the real split-brain reproduction and its fencing fix.

**Whiteboard:** Draw "Region A" and "Region B" with a dashed line between them. On Region A, draw a padlock icon labeled "fence" sitting directly between the primary and the dashed line: "this has to close before anything gets promoted on the other side, not after." Below, four small boxes, one per DR tier, each with a rough cost and RPO/RTO.

**Staff-level framing:** Treat a DR pattern's RPO/RTO as a claim requiring real verification, not a number trusted from documentation. Decompose RTO into its component costs (detection, fencing, promotion, traffic redirection) rather than quoting one number, and push back on reflexive active-active proposals by naming the specific consistency mechanism required.

## Production Warning Signs

- A DR plan's RPO/RTO has never been verified by an actual drill — an unverified claim, not a plan.
- Fencing depends on a human remembering to do it correctly during a real, stressful incident — it will eventually be skipped; it must be an automated, unconditional step.
- A "multi-region" system with a single-region control plane (DNS management, secrets, deployment pipeline hosted only in the primary region) — the single point of failure was moved, not removed.
- DNS TTL or traffic-manager health-check intervals left untuned — they directly add to real observed RTO even when promotion itself completes in under a second.

## Related

- `handbook/databases/replication-read-replicas-and-replica-lag.md`
- `handbook/system-design/cap-theorem-and-consistency-models.md`
- `handbook/system-design/resilience-patterns.md`
- `handbook/system-design/load-balancing-service-discovery-and-health-checking.md`
