---
title: "Cheat Sheet: Strangler Fig and Migration Patterns"
slug: strangler-fig-and-migration-patterns
document_type: cheat-sheet
domain: architecture
topic_id: T-912
canonical: ../handbook/architecture/strangler-fig-and-migration-patterns.md
last_updated: 2026-09-02
---

# Strangler Fig, Anti-Corruption Layer, and Migration Patterns

**Canonical chapter:** [`syllabus/17-architecture/strangler-fig-and-migration-patterns.md`](../syllabus/17-architecture/strangler-fig-and-migration-patterns.md)

## Core Mental Model

A big-bang rewrite bets everything on one cutover event: the new system either works correctly on day one for the entire scope, or the failure is total and hard to localize. The Strangler Fig pattern instead treats migration as a long series of small, independently-reversible cutovers: a facade sits in front of both systems, routes each request to whichever system currently owns it, and that routing decision can move in small increments — one endpoint, one percentage point, one tenant at a time — each increment cheap to observe and cheap to reverse.

## Essential Definitions

- **Strangler Fig pattern** — incrementally replaces a legacy system by routing an increasing share of traffic to a new system through a facade, until the legacy system handles nothing and can be retired.
- **Anti-Corruption Layer (migration context)** — a temporary translation layer letting the new system be built against its own clean model while still reading/writing legacy data during the transition; contrast with its permanent, steady-state role in DDD strategic design.
- **Dual-write** — writing every change to both systems during the migration window, so either system has a complete, current view of the data regardless of which currently serves reads.
- **Incremental cutover** — reads move to the new system gradually (by percentage, tenant, or endpoint) rather than all at once, bounding the blast radius of a defect.
- **Rollback safety window** — the most commonly-missed concept: a rollback is only safe for as long as legacy has continued receiving every write. The moment dual-write to legacy is disabled, "rollback" silently becomes lossy with no error or warning at that moment.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Legacy system has significant undocumented business logic | Strangler Fig — a rewrite will silently miss it |
| Bounded blast radius per change matters more than migration speed | Strangler Fig, incremental cutover |
| Legacy system is small, well-understood, low-risk to replace outright | A rewrite may genuinely be faster and safer |
| A rollback-safety window and its expiration date have NOT been explicitly planned | Don't disable dual-write yet — the answer is "not yet safe" |
| New system needs to be built against a clean model, not legacy's schema | Anti-Corruption Layer during the migration window |

**Approach comparison:**

| Approach | Blast radius per step | Rollback option | Real cost |
|---|---|---|---|
| Big-bang rewrite | Entire system, at once | None once cut over | High risk concentrated in one event |
| Strangler Fig, no dual-write | Bounded, but no data safety net | None for data written post-cutover | Lower risk, but false confidence about rollback |
| Strangler Fig, with dual-write | Bounded | Real, for the duration dual-write stays on | Real ongoing infrastructure cost, genuine safety |

## Key Numbers (real, executed Java 21 — a real router, two real independent stores)

- Rollback safety, identical scenario differing by one boolean flag: dual-write disabled immediately at cutover → 3 of 6 orders unrecoverable after rollback. Dual-write kept running through the rollback window → 0 of 6 orders unrecoverable.
- Incremental cutover accuracy: configured 0% new-system → observed new=0 (0.0%), legacy=1000 (100.0%). Configured 25% → observed new=251 (25.1%), legacy=749 (74.9%). Configured 100% → observed new=1000 (100.0%), legacy=0 (0.0%).

## Common Pitfalls

- Claiming a rewrite would be faster without accounting for the legacy system's undocumented business logic.
- Disabling dual-write as soon as the team "feels confident," rather than against an explicit, pre-planned, calendared rollback-safety window.
- Treating "we have a rollback plan" as true indefinitely, rather than recognizing that plan silently expires the moment dual-write stops.
- Building the facade's routing logic as ad hoc conditionals scattered through the codebase rather than one auditable, version-controlled seam.
- Running a one-time backfill with no ongoing reconciliation check, silently leaving gaps that surface later as unexplained missing records.

## Interview Answer Skeleton

**30-sec:** Strangler Fig migrates a legacy system incrementally, routing traffic through a facade that moves in small, reversible steps, rather than betting everything on one rewrite cutover. Dual-write keeps both systems current during the transition, which is also what makes rollback possible — but only for as long as dual-write to legacy stays on.

**2-min:** Add the real rollback-safety proof: an identical scenario with one flag flipped goes from 3-of-6 orders lost to 0-of-6 lost. Add the real, measured incremental-cutover accuracy (configured 25% → observed 25.1%).

**Whiteboard:** Draw "Legacy" and "New" boxes with a "Facade" diamond between them and an arrow from "Client" into the facade. Draw a routing percentage slider under the facade, moving it 0% → 25% → 100%, each step independently observable. Draw a second arrow from the facade into "Legacy" labeled "dual-write," then explicitly cross it out at some point: "the moment this line disappears, rollback stops being real."

**Staff-level framing:** Reason about the rollback-safety window as an explicit, planned, cost-bearing commitment rather than an emergent property of "how the migration happened to go." Discuss the organizational discipline required to keep a team from prematurely declaring victory and disabling dual-write early. Connect the pattern's dependency on bounded contexts — you can only safely strangle a legacy system one bounded context's worth of functionality at a time.

## Production Warning Signs

- A rollback runbook fails when actually attempted, revealing missing historical orders — check whether dual-write was disabled well before the incident that triggered the rollback attempt; the "rollback plan" had already silently expired.
- A rollback attempt reveals a real, unexplained data gap corresponding exactly to the interval since dual-write was disabled — the defining debug signal for silent rollback expiration.
- Legacy and new systems have different validation rules or defaults, causing a write that succeeds against one to fail or be silently altered against the other during dual-write — a real divergence even while both are supposedly receiving every write.
- A facade's routing configuration isn't version-controlled or auditable, creating "which system is actually serving this request right now" confusion during an incident.

## Related

- `syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md`
- `syllabus/17-architecture/clean-hexagonal-architecture.md`
- `syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md`
- `syllabus/17-architecture/architecture-decision-records.md`
