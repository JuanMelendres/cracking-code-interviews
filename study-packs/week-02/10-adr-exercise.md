---
title: "ADR Exercise"
week: 2
last_reviewed: 2026-07-29
---

# ADR Exercise

**Deliverable for T-916 · this week's `ADR-001.md`**

Write a real Architecture Decision Record for a decision you have actually made. The four sections below map directly onto the four-beat structure from `05-trade-off-narration-and-adrs.md`.

## Table of Contents

1. [Template](#1-template)
2. [Worked example](#2-worked-example)
3. [Exit check](#3-exit-check)

---

## 1. Template

```markdown
# ADR-001: [Title — the decision, stated as a decision, not a topic]

## Status
[Proposed | Accepted | Superseded by ADR-XXX]

## Context
[Beat 1 — the actual constraint that forced this decision. Specific, not generic.]

## Options Considered
1. [Option A — including its genuine strengths, not a strawman]
2. [Option B]
3. [Option C, if a real third option existed]

## Decision
[Beat 3 — which option, and the SPECIFIC criterion that decided it]

## Consequences
### Positive
- [What this decision buys]

### Negative — Beat 4, the one most often skipped
- [What this decision actually costs. This section must not be empty.]
```

## 2. Worked example

```markdown
# ADR-001: Use PostgreSQL with an EAV attribute table for the product catalog

## Status
Accepted

## Context
The catalog service needs to support ad-hoc filtering across product
attributes that vary significantly by category (a shirt has size/color;
a laptop has RAM/screen-size), while also participating in the same
transaction as inventory and pricing updates when an order is placed.

## Options Considered
1. **Document store** (e.g. MongoDB) — natural fit for the per-category
   attribute variation; each product is a self-contained document with
   whatever fields its category needs.
2. **Relational with EAV attribute table** — a `product_attributes` table
   of (product_id, attribute_name, value) rows alongside a normal
   `products` table for the fixed fields.
3. **Relational with per-category tables** — a separate table per product
   category with its own fixed schema.

## Decision
Option 2 (relational, EAV attribute table). The deciding factor: the
catalog must participate in the same ACID transaction as inventory and
pricing when an order is placed, and a document store's cross-document
transaction story was weaker than what this specific requirement needed.
Option 3 was rejected because new categories are added by product
managers roughly weekly, and per-category tables would require a schema
migration for each one.

## Consequences

### Positive
- Full transactional consistency between catalog, inventory, and pricing
- New attribute types require no schema migration

### Negative
- Every attribute filter requires an extra join against the EAV table,
  which is measurably more awkward to query than a document store would
  have been — we built a small query-builder abstraction specifically to
  keep this awkwardness out of application code
- The EAV table has no way to enforce "a shirt must have a size" at the
  database level — that validation lives in the application layer, which
  is a real, accepted loss of the enforcement guarantee a fixed schema
  would have given us
```

**Why this is a complete ADR:** the negative-consequences section is not empty and not token — it names two specific, real costs, one of which (validation enforcement) is arguably the more important admission and the one most ADR templates in the wild omit.

## 3. Exit check

Your own `ADR-001.md` must include a real decision (not a hypothetical one constructed for the exercise) and a non-empty, substantive Negative consequences section. If the negative section is empty, the decision either had no real trade-off (unusual — worth double-checking) or the trade-off wasn't looked for hard enough.
