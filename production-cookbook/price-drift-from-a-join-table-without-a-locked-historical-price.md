---
title: "Price Drift From a Join Table Without a Locked Historical Price"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/06-databases/data-modelling-and-explicit-join-tables.md
source: handbook/databases/data-modelling-and-explicit-join-tables.md#production-scenarios
---

# Price Drift From a Join Table Without a Locked Historical Price

## Context

The system models `Order`-to-`Product` as a naive many-to-many join table with no price column, re-fetching the live product price on every read.

## Symptoms

During a routine audit, finance flags that historical order totals reported by the system no longer match the amounts customers were actually charged at the time of purchase for a subset of older orders — the discrepancy always tracks a subsequent price change on the affected products.

## Impact

Every historical report, refund calculation, and revenue-recognition figure touching an order with a since-changed product price is silently wrong, discovered only because finance manually cross-checked against payment processor records.

## Initial Hypotheses

- A bug in the reporting query's aggregation logic — checked and ruled out; the SQL correctly sums quantity × price, the price itself is the problem.
- A data migration corrupted historical records — checked and ruled out; no migration touched the affected rows.
- The order-line join table re-fetches live product price instead of a locked historical price — correct.

## Evidence

Every affected order's discrepancy exactly equals `(quantity) × (current price − price at time of purchase)`, and every affected product has at least one price change logged after the order date.

## Investigation Timeline

1. **Discrepancy flagged during a routine finance audit**, cross-checking system-reported totals against payment processor records.
2. **Aggregation-logic and migration hypotheses ruled out**, confirming the query logic and historical row integrity were both correct.
3. **Discrepancy pattern analyzed algebraically**, matching exactly to `quantity × (current price − price at time of purchase)`.
4. **Schema reviewed directly**, finding the order-line join table has no price column and re-fetches live product price on every read.

## Root Cause

The system modeled `Order`-to-`Product` as a naive many-to-many join table with no price column, re-fetching the live product price on every read, rather than snapshotting the price at the moment the order was placed.

## Immediate Mitigation

Manually recompute and flag affected historical orders using payment processor records as the source of truth, since the database itself no longer has the correct historical price.

## Permanent Fix

Migrate to an explicit `OrderLine` entity that locks `unit_price_at_order_time` at insert time, backfilling historical rows from payment processor records where available — data unrecoverable from the database alone for older orders.

## Alternatives Considered

Adding an audit log of price changes and reconstructing historical prices at read time by joining against it. Rejected as more complex and slower than simply snapshotting the price once, at the moment it's known, in the order line itself.

## Trade-offs

The explicit entity requires backfilling data that, for some historical orders, may not be perfectly recoverable. Accepted, since the alternative is an audit-log join on every historical read, indefinitely.

## Prevention

Any relationship where a referenced fact — price, rate, terms — can change after the relationship forms should default to an explicit join entity with a snapshotted value, treated as a required review item for any new many-to-many modelling decision.

## Monitoring and Alerts

- A standing reconciliation check comparing a sample of historical order totals against payment processor records on a recurring schedule, rather than relying on an eventual manual audit to surface drift — this incident was only found because finance happened to cross-check manually.
- A schema-review checklist item flagging any new many-to-many join table where one side's referenced attribute (price, rate, status) is mutable over time, requiring an explicit justification for why a snapshot isn't needed if one genuinely isn't.

## Interview Story

This maps to "the relationship needed an attribute" — but the deeper lesson is that a referenced fact needed to survive a later change to its source. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a finance audit found historical order totals silently drifting from what customers were actually charged, tracking product price changes after the fact.
- **Task:** find the mechanism behind a silent, financially significant data-correctness bug.
- **Action:** rule out query-logic and migration explanations directly; match the discrepancy pattern algebraically to a price-change formula; trace it to a join table with no locked historical price.
- **Result:** migrated to an explicit `OrderLine` entity snapshotting price at order time, backfilling what could be recovered from payment processor records, and adding a schema-review flag for future mutable-attribute join relationships.

## Staff-Level Discussion

The mistake here is treating `Order`-to-`Product` as a simple relationship ("an order contains products") when the actual business fact being recorded is "this customer agreed to pay this specific price for this specific product at this specific moment" — a fact that must be preserved even after the source data (the product's current price) changes. This distinction — relationship versus historical fact — is easy to miss during initial modeling, when the current price and the historical price happen to be identical and the bug is invisible until enough time passes for prices to actually change. A Staff engineer reviewing a new data model should explicitly ask, for every relationship to a mutable entity, "does this relationship need to survive that entity's future changes, or does it always want the live value?" — price, discount rate, contract terms, and exchange rate are all common instances of the same underlying pattern, and treating it as a recurring, nameable modeling question rather than a one-off bug prevents the same mistake from recurring in the next feature.

## Related Handbook Chapters

- [Data Modelling and Explicit Join Tables](../syllabus/06-databases/data-modelling-and-explicit-join-tables.md) — canonical snapshotted-attribute join-table pattern used here.
