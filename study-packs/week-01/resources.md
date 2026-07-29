# Week 1 Resources

Classified by authority: **PRIMARY** (official docs/original source), **BOOK**, **TOOL**, **SECONDARY** (blog/derived explanation).

---

## T-901 — Hexagonal / Clean Architecture

| Source | Type | Note |
|---|---|---|
| Alistair Cockburn, ["Hexagonal Architecture"](https://alistair.cockburn.us/hexagonal-architecture/) | PRIMARY | The original 2005 article coining the term |
| Robert C. Martin, *Clean Architecture*, Ch. 22–23 | BOOK | "The Clean Architecture" and "Presenters and Humble Objects" |
| Jeffrey Palermo, "The Onion Architecture" (blog series, 2008) | SECONDARY | Same shape, different name — useful for recognizing the pattern under any label an interviewer uses |
| Vaughn Vernon, *Implementing Domain-Driven Design*, Ch. 4 | BOOK | How hexagonal composes with DDD bounded contexts (previewed for Week 2's T-903) |

## T-609 — Database Indexes

| Source | Type | Note |
|---|---|---|
| [PostgreSQL documentation, Ch. 11 "Indexes"](https://www.postgresql.org/docs/current/indexes.html) | PRIMARY | Full chapter, all sections |
| [PostgreSQL documentation, Ch. 14 "Performance Tips"](https://www.postgresql.org/docs/current/performance-tips.html) | PRIMARY | §14.1 `EXPLAIN`, §14.2 planner statistics |
| Markus Winand, *Use The Index, Luke*, Ch. 1–3 | BOOK | B-Tree mechanics, concatenated (composite) indexes, clustering — also free online at [use-the-index-luke.com](https://use-the-index-luke.com/) |
| PostgreSQL 16 via Docker | TOOL | Used to produce every `EXPLAIN` block in `02-database-index-fundamentals.md` — see `MANIFEST.md` for the exact reproducible command |

## T-1601 / T-1419 / T-1501 — Communication and behavioral method

| Source | Type | Note |
|---|---|---|
| This pack's own `03-…`, `04-…`, `05-…` | PRIMARY (for this programme) | The nine-layer stack, six-phase narration, and STAR structure are this project's own synthesis, not external citations — flagged here for transparency, not attributed to an outside source |

## General

| Source | Type | Note |
|---|---|---|
| `00-project/knowledge-base-audit.md` | PRIMARY | The Phase 1 audit that identified this week's errata (LRU bug) and the feedback-block topics |
| `00-project/learning-roadmap.md` §1–3 | PRIMARY | Day 0 diagnostic, feedback override block, full Week 1 spec this pack implements |
