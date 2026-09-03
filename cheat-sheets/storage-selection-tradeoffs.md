---
title: "Cheat Sheet: Storage Selection Trade-offs"
slug: storage-selection-tradeoffs
document_type: cheat-sheet
domain: system-design
topic_id: T-811
canonical: ../handbook/system-design/storage-selection-tradeoffs.md
last_updated: 2026-08-04
---

# Storage Selection Trade-offs

**Canonical chapter:** [`syllabus/11-system-design/storage-selection-tradeoffs.md`](../syllabus/11-system-design/storage-selection-tradeoffs.md)

## Core Mental Model

Storage technology should be the last decision made, not the first. Every storage category (relational, document, key-value, wide-column) is optimized for a specific shape of read/write access, consistency requirement, and transactional scope. Naming a technology before naming the access pattern it needs to serve is working backward from a conclusion.

## Essential Definitions

- **Storage selection** — answered by working backward from the queries the system actually needs to serve, not from a general reputation ("Postgres is for structured data, Mongo is for flexible data").
- **The access-pattern method** — four ordered questions before naming a technology: (1) What are the actual read/write patterns — point lookups, range scans, complex joins, full-text search? (2) What's the consistency requirement per operation — strong or eventual? (3) What's the transactional scope — does one logical operation need to atomically touch multiple records/aggregates? (4) What's the volume and growth shape — read- or write-heavy, predictable or bursty?
- **"NoSQL" is not one category** — a document store, a key-value store, and a wide-column store have almost nothing in common except "not traditionally relational."
- **Polyglot persistence** — using multiple storage technologies in one system, justified only when a component's access pattern is different enough from the rest of the system that forcing one technology creates a real, measurable cost.

## Decision Table

| Category | Wins when | Costs |
|---|---|---|
| Relational (PostgreSQL) | Multi-entity transactions, ad-hoc queries/joins, strong consistency by default | Schema changes require migration discipline; horizontal write scaling is harder |
| Document (MongoDB-style) | Data naturally read/written as one self-contained document, flexible schema | Cross-document transactions are weaker/newer; denormalization risks update-anomaly bugs |
| Key-value | Simple point lookups at very high throughput | No query flexibility beyond the key; relational structure has to live elsewhere |
| Wide-column (Cassandra-style) | Massive write volume, time-series-like access patterns, tunable consistency | Query patterns must be designed in at schema-design time — ad-hoc queries are expensive or impossible |

## Key Numbers

Not applicable — this chapter is a decision-framework chapter with no benchmark or throughput figures; the method itself, not a measured number, is the deliverable.

## Common Pitfalls

- Choosing storage technology from reputation or trend rather than the access-pattern method
- Treating "NoSQL" as one category — a document store, key-value store, and wide-column store have almost nothing in common beyond "not traditionally relational"
- Adding a second storage technology (polyglot persistence) without weighing its ongoing operational cost against the specific access-pattern win it buys

## Interview Answer Skeleton

**30-sec:** Storage selection should follow the access pattern — read/write shape, consistency requirement, transactional scope, volume — not technology reputation. Relational wins for multi-entity transactions and ad-hoc queries; document for flexible, self-contained data; key-value for high-throughput point lookups; wide-column for massive, predictably-patterned write volume.

**2-min:** Add why it exists (working backward from queries, not reputation) + the four-question access-pattern method + the reconciliation-feature production example (fixed by adding a targeted relational store for one component, not migrating the whole catalog).

**Whiteboard:** Draw the decision flowchart: "multi-record ACID transactions needed?" → relational; "point lookup by key?" → key-value or document; "wide scans over huge volume?" → wide-column. Walk through it live for whatever workload the interviewer names.

**Staff-level framing:** a storage decision is a multi-year commitment with a real migration cost — the honest framing is closer to "which set of trade-offs can this team operate confidently for the system's expected lifetime" than "which technology is theoretically best-suited." A team with deep PostgreSQL operational experience choosing PostgreSQL for a workload technically slightly better suited to a document store, because operational maturity outweighs marginal technical fit, is frequently the better Staff-level answer.

## Production Warning Signs

- **Real incident pattern:** a catalog service on a document store (chosen for flexible per-category schema) later needs to atomically reconcile inventory counts across a batch of orders touching multiple products — the document store's weaker cross-document transaction support makes this reliably impossible without significant application-level workaround code, and has already produced at least one reconciliation discrepancy in production.
- Fix: introduce a relational store specifically for the reporting/reconciliation domain (a deliberate polyglot-persistence decision), fed by change-data-capture from the document store — not a full migration of the whole catalog.

## Related

- [Data Partitioning and Consistent Hashing](data-partitioning-and-consistent-hashing.md)
- `syllabus/06-databases/data-modelling-and-explicit-join-tables.md`
