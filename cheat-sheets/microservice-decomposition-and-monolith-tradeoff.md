---
title: "Cheat Sheet: Microservice Decomposition and the Monolith Trade-off"
slug: microservice-decomposition-and-monolith-tradeoff
document_type: cheat-sheet
domain: architecture
topic_id: T-907
canonical: ../handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md
last_updated: 2026-08-03
---

# Microservice Decomposition and the Monolith Trade-off

**Canonical chapter:** [`syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md`](../syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md)

## Core Mental Model

A service boundary is a consistency boundary wearing a deployment-topology costume. "Where do I split this system" and "where does strong, single-transaction consistency stop being required" are, in the well-designed case, the same question asked two ways. "Two services need one transaction" is direct evidence the boundary was drawn wrong — not a cue to reach for two-phase commit.

## Essential Definitions

- **Boundary test** — draw the service boundary where a strong, single-transaction consistency requirement does *not* cross it; the same test used for a DDD aggregate boundary.
- **"Two services need one transaction" signal** — indicates the boundary is wrong, or that eventual consistency (saga/outbox) must replace the transaction — never a cue for 2PC.
- **Merge-back signals** — three concrete conditions indicating services should be recombined (see table).
- **Conway's Law** — the actual mechanism by which service boundaries either match or fight an organization's communication structure.
- **Monolith's failure mode at scale** — organizational, not technical: one team's bad deploy can block every other team; blast radius of any change defaults to the entire system.

## Decision Table

| Benefit of splitting | Cost of splitting |
|---|---|
| Independent deployment per team | Every cross-service call becomes a network call — new failure modes |
| Independent scaling per component | Distributed transactions become sagas — eventual consistency, harder compensation logic |
| Smaller blast radius per deploy | Multiplied operational surface — more services to monitor/alert/staff on-call for |
| Team autonomy matching Conway's Law | A 4-person team gets none of the "multiple team" benefit and all of the distributed-systems cost |

**Merge-back criteria (any one is a real signal):**
1. Always deployed together in practice — the independence the split was for was never realized
2. Majority of cross-service calls are synchronous, on the critical path — pure latency cost, no independence benefit
3. Operational burden (on-call surface, monitoring, pipelines) measurably exceeds the benefit gained

**Cheat Sheet table (situation → what to reach for):**

| Situation | What to reach for |
|---|---|
| Deciding where to cut a boundary | Where strong single-transaction consistency is not required across it |
| Two services need one transaction | Question the boundary first; saga only if the boundary genuinely holds |
| Services always deploy together | Measurable merge-back signal — consider consolidating |
| Small team, one schedule | Default to a well-modularized monolith, not microservices |

## Key Numbers

- **&gt;80%** — in the real production scenario, deploys of one of five services were followed by a deploy of at least one other within the same day in over 80% of cases (the measurable "always co-deployed" signal)
- **5 services / 4 engineers** — the specific premature-decomposition ratio in that scenario

## Common Pitfalls

- Drawing service boundaries by table or code-file proximity instead of consistency requirement
- Reaching for a distributed transaction (2PC) as the default fix for cross-service coordination
- Treating decomposition as a one-way door — never considering the split might need reversing
- Defending microservices as inherently superior regardless of team size

## Interview Answer Skeleton

**30-sec:** Draw boundaries where strong single-transaction consistency isn't required across the line. Two services needing one transaction signals a wrong boundary, not a 2PC cue. Microservices' benefit is organizational; a small team gains little and pays the full distributed-systems cost.

**2-min:** Add why it exists (monolith's failure mode is organizational at scale) + the boundary test + the &gt;80% co-deployment production example.

**Whiteboard:** Draw Order/OrderLine nested in one box (must update atomically) vs. a separate Inventory box, connected by a dotted "eventually consistent, via event" arrow — narrate that they share a box because of the consistency requirement, "not because they're different tables."

**Highest-differentiating move:** state unprompted that organizational structure, not technical elegance, is the primary justification for microservices — and treat Conway's Law as a real design constraint.

## Production Warning Signs

- Deployment velocity *decreasing* after decomposition, not increasing
- Nearly every feature still requires coordinated changes across 3-4 of 5 services in the same release window
- On-call load increasing, incidents needing cross-service log correlation to diagnose
- **Real incident:** 4-engineer team split into 5 microservices prematurely, &gt;80% same-day co-deployment, no independence benefit realized, on-call burden doubled with no throughput gain. Fix: consolidate the two most tightly-coupled services as a pilot, evaluate merging the rest into a well-modularized monolith.

## Related

- `syllabus/10-distributed-systems/distributed-systems-failure-modes.md`
- `syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md`
