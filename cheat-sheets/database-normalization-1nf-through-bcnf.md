---
title: "Cheat Sheet: Database Normalization — 1NF Through BCNF"
slug: database-normalization-1nf-through-bcnf
document_type: cheat-sheet
domain: 06-databases
topic_id: T-2411
canonical: ../syllabus/06-databases/database-normalization-1nf-through-bcnf.md
last_updated: 2026-09-15
---

# Database Normalization — 1NF Through BCNF

**Canonical chapter:** [`syllabus/06-databases/database-normalization-1nf-through-bcnf.md`](../syllabus/06-databases/database-normalization-1nf-through-bcnf.md)

## Core Mental Model

Every normal form answers the same narrower question: can this schema currently represent two contradictory facts about the same real-world thing? Each form closes one specific way that's possible.

## Essential Definitions

- **1NF** — every column holds a single, atomic value; no repeating groups.
- **2NF** — 1NF + no partial dependency (a non-key column depending on only part of a composite key).
- **3NF** — 2NF + no transitive dependency (a non-key column depending on another non-key column).
- **BCNF** — every functional dependency's determinant is a candidate key; closes a real gap 3NF's own definition leaves open.

## Decision Table

| Form | Rule | Real anomaly it prevents |
|---|---|---|
| 1NF | Atomic values, no repeating groups | Can't query/index into a comma-separated cell |
| 2NF | No partial dependency on a composite key | A fact tied to part of the key disagrees with itself |
| 3NF | No transitive dependency | Same anomaly, via a non-key column |
| BCNF | Every determinant is a candidate key | A non-key column's real-world rule goes unenforced |
| Denormalize (deliberate) | Documented, measured exception only | Trades write safety for read speed on purpose |

## Common Pitfalls

- Reciting normal-form definitions without being able to point at a concrete anomaly each one prevents.
- Treating 2NF and 3NF as unrelated rules instead of the same anomaly pattern at two distances from the key.
- Assuming a 3NF-satisfying schema has no remaining anomalies — a real BCNF counterexample exists.
- Denormalizing without measuring both the real read-speed win and the real write-amplification cost first.

## Interview Answer Skeleton

**30-sec:** Normalization removes duplicated, potentially-contradictory data by splitting it into tables keyed on exactly what each fact depends on. 1NF: atomic values. 2NF: no partial dependency. 3NF: no transitive dependency. BCNF: every determinant is a candidate key.

**2-min:** Add: each form is motivated by a real anomaly, not an abstract rule — a fact duplicated across rows that can disagree with itself. Denormalizing trades that safety for read speed, deliberately and measurably (real evidence: ~3.27× faster reads, ~80× more write amplification).

**Staff-level framing:** An org with many independently-denormalized tables, each added under local deadline pressure, accumulates real distributed data-consistency risk with no single owner — push toward a documented policy or monitored materialized views instead.

## Related

- syllabus/06-databases/sql-and-relational-database-fundamentals.md
- syllabus/06-databases/data-modelling-and-explicit-join-tables.md
- syllabus/06-databases/views-and-materialized-views.md
