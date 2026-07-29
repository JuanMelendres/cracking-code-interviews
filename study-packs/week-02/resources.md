---
title: "Week 2 Resources"
week: 2
last_reviewed: 2026-07-29
---

# Week 2 Resources

Classified by authority: **PRIMARY**, **BOOK**, **TOOL**, **SECONDARY**.

---

## T-610 — Query Planning

| Source | Type | Note |
|---|---|---|
| [PostgreSQL documentation, Ch. 14.1 "Using EXPLAIN"](https://www.postgresql.org/docs/current/using-explain.html) | PRIMARY | |
| [PostgreSQL documentation, Ch. 14.2 "Statistics Used by the Planner"](https://www.postgresql.org/docs/current/planner-stats.html) | PRIMARY | |
| Markus Winand, *Use The Index, Luke*, Ch. 4 "The Join Operation" | BOOK | |
| PostgreSQL 16 via Docker | TOOL | Produced every real plan in `01-query-planning-and-explain.md`; see `practice/sql/week-02/` |

## T-605/T-608 — Data Modelling

| Source | Type | Note |
|---|---|---|
| Vaughn Vernon, *Implementing Domain-Driven Design*, Ch. 4 | BOOK | Read alongside `03-ddd-tactical-aggregates.md` |
| [Jakarta Persistence specification](https://jakarta.ee/specifications/persistence/) | PRIMARY | `@ManyToMany` semantics |

## T-903 — DDD Aggregates

| Source | Type | Note |
|---|---|---|
| Eric Evans, *Domain-Driven Design*, Ch. 6 | BOOK | Original aggregate definition |
| Vaughn Vernon, *Domain-Driven Design Distilled*, Ch. 5 | BOOK | |

## T-617/T-811 — Storage Selection

| Source | Type | Note |
|---|---|---|
| Martin Kleppmann, *Designing Data-Intensive Applications*, Ch. 2–3 | BOOK | |

## T-1505/T-916 — Trade-off Narration and ADRs

| Source | Type | Note |
|---|---|---|
| Michael Nygard, ["Documenting Architecture Decisions"](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions) | PRIMARY | Original ADR format |
| [adr.github.io](https://adr.github.io/) | SECONDARY | Format examples and tooling |

## General

| Source | Type | Note |
|---|---|---|
| `00-project/knowledge-base-audit.md` | PRIMARY | The Phase 1 audit that identified this week's errata (monotonic-stack diagram/code contradiction) |
| `00-project/learning-roadmap.md` §3 (Week 2) | PRIMARY | Full Week 2 spec this pack implements |
