---
title: "Cheat Sheet: Architecture Decision Records"
slug: architecture-decision-records
document_type: cheat-sheet
domain: architecture
topic_id: T-916
canonical: ../handbook/architecture/architecture-decision-records.md
last_updated: 2026-09-02
---

# Architecture Decision Records

**Canonical chapter:** [`handbook/architecture/architecture-decision-records.md`](../handbook/architecture/architecture-decision-records.md)

## Core Mental Model

An ADR is not a record of what was decided — it's a record of why, aimed at a reader who disagrees with the outcome. The test for whether an ADR is any good is not "does it state the decision clearly" (a Slack message does that) but "if someone reads this in two years, in the middle of arguing the decision was wrong, does it tell them what was actually known and weighed at the time, so they can tell whether circumstances genuinely changed or whether the original reasoning simply wasn't good enough." An ADR that only justifies the chosen option, with no real accounting of what was rejected and why, fails this test even if it's well-written.

## Essential Definitions

- **Architecture Decision Record (ADR)** — a short, standalone document capturing one significant architectural decision: context, options considered, decision made, and consequences (including negative ones) accepted.
- **Architecturally significant** — a decision is genuinely hard to reverse, affects more than one team or component, or trades off a quality attribute (consistency, latency, cost, operability) in a way future engineers need to understand before proposing a change.
- **The four required sections (Nygard, 2011)** — Status, Context, Decision, Consequences — deliberately minimal so teams actually keep doing it.
- **Immutability** — an accepted ADR is never edited to reflect a later reversal; a new ADR is written, the old one marked Superseded, with an explicit link between them.
- **MADR** — an extension adding explicit Decision Drivers and Considered Options sections, while preserving Nygard's four required sections underneath.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Decision is hard to reverse, cross-team, or trades off a quality attribute | Write a formal ADR |
| Context can only honestly be written as "we wanted to use X" | The decision likely isn't significant enough yet, or the real driver hasn't been identified |
| Consequences section would list only positive outcomes | The decision hasn't been interrogated enough to write up yet |
| Circumstances have changed since a past ADR | Write a new ADR, mark the old one Superseded — never edit it |

**Comparison:**

| | ADR | Design doc | Wiki page / tribal knowledge |
|---|---|---|---|
| Scope | One decision | Often a whole system/feature | Anything |
| Lifespan | Immutable once accepted; superseded, not edited | Often edited in place, can drift | Frequently stale, no versioning |
| Best for | Capturing *why*, durably | Explaining *how* a system works, currently | Anything not requiring durability |

## Common Pitfalls

- Writing an ADR that only justifies the chosen option, with no honest accounting of what was rejected and why.
- Treating "we already decided" as a reason not to write the ADR — its value is precisely for after the decision, not instead of making it.
- Editing an accepted ADR in place instead of superseding it with a new one, destroying the historical reasoning trail.
- Writing ADRs for every decision regardless of significance, diluting the practice until nobody reads them.
- Omitting real negative consequences — a Consequences section listing only benefits signals the decision wasn't honestly interrogated.

## Interview Answer Skeleton

**30-sec:** An ADR is a short, durable record of one architecturally significant decision — its context, the options considered, the choice made, and the consequences accepted, including the negative ones — written so someone who disagrees with the outcome years later can still tell whether the original reasoning was sound.

**2-min:** Add the immutable-then-superseded lifecycle discipline and the honesty test: Consequences must include a real negative, or the decision wasn't properly interrogated. Cite a worked example: an ADR choosing streaming replication over log-shipping DR because the latter measurably lost 10 of 10 rows in a real test — grounding the decision in already-measured evidence, not a general argument.

**Whiteboard:** Draw one document icon with four labeled bands: Status, Context, Decision, Consequences. Point at Context: "written for someone who disagrees with the outcome." Point at Consequences: "must include at least one real negative." Draw a second document below with an arrow labeled "supersedes" pointing back up to the first.

**Staff-level framing:** Frame ADR value as organizational memory that scales with team size and turnover, not with technical complexity — a two-person team that never turns over gets little marginal value; a team that has grown or seen turnover gets real, compounding value. Propose a cheap, honest-about-its-limits automation (a structural completeness check) rather than relying purely on review discipline, and explicitly name what such a check can and cannot verify.

## Production Warning Signs

This is a documentation-governance practice, not a running system, so its real-world signal is process health rather than an incident:

- An ADR passes a structural completeness check (all four headings present) but its Consequences section lists only benefits — the checker verifies section presence, not content quality; a second reviewer must explicitly ask "what's the real cost of this decision?"
- A growing pile of numbered ADR files with no index — ADR sprawl defeats the pattern's purpose just as thoroughly as never writing any.
- An ADR written after a decision has already shipped, to justify it retroactively rather than to capture contemporaneous reasoning — loses the pattern's real value.

## Related

- `handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md`
- `handbook/architecture/cqrs-read-write-separation.md`
- `handbook/system-design/multi-region-failover-and-disaster-recovery.md`
- `handbook/kafka/schema-registry-and-compatibility-evolution.md`
