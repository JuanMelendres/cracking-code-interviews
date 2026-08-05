---
title: "Double Refund From a Same-Row Read-Then-Write Race"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/databases/isolation-levels-and-concurrency-anomalies.md
source: handbook/databases/isolation-levels-and-concurrency-anomalies.md#production-scenarios
---

# Double Refund From a Same-Row Read-Then-Write Race

## Context

A refund-eligibility check — "has this order already been refunded?" — executes as a separate `SELECT` before the `UPDATE order SET refunded = true`, running at the connection pool's default READ COMMITTED isolation level, with no row lock held across the two statements.

## Symptoms

A small number of refund requests, processed within seconds of each other by two different support agents (or an agent and an automated retry), result in the customer being refunded twice for the same order.

## Impact

Direct financial loss, plus a support/finance reconciliation burden discovered only in a monthly audit — not caught by any error log, because neither transaction failed.

## Initial Hypotheses

- A duplicate API call from a flaky client — checked and ruled out; request IDs differ, so this isn't simple retried-request duplication.
- A missing idempotency key — plausible, and a real gap, but doesn't fully explain why both refunds succeeded rather than one being rejected.
- A database-level race — plausible, and ultimately confirmed.

## Evidence

Application logs show the refund-eligibility check executed as a separate `SELECT` before the `UPDATE`, with both requests' `SELECT`s completing before either `UPDATE` — the classic read-then-write race, running at READ COMMITTED.

## Investigation Timeline

1. **Duplicate refunds discovered during a monthly reconciliation audit**, with no corresponding error anywhere in the logs.
2. **Duplicate-client-call and simple-idempotency-gap hypotheses examined**, neither fully explaining why both refund attempts succeeded rather than one failing.
3. **Application logs traced for the specific affected orders**, finding two `SELECT`-then-`UPDATE` sequences interleaved in time.
4. **Isolation level and locking reviewed**, confirming READ COMMITTED with no row lock across the eligibility check and the update.

## Root Cause

No row lock was held across the eligibility check and the state-changing update. Both concurrent requests read `refunded = false`, both proceeded, both wrote — not a lost update in the narrow sense (the final `refunded = true` value is correct), but a duplicated side effect: two refund transactions issued to the payment processor, caused by the identical read-then-write race pattern.

## Immediate Mitigation

Add `SELECT ... FOR UPDATE` on the order row for the duration of the refund-eligibility check and the state update, closing the window without changing the isolation level or requiring retry logic.

## Permanent Fix

Add an idempotency key to the refund endpoint — a separate, complementary fix addressing client-side duplicate calls, while the row lock addresses the server-side race — and a unique constraint on `(order_id)` in a dedicated `refunds` table, so even a future code path that reintroduces the race fails at the database's constraint layer rather than silently double-refunding.

## Alternatives Considered

Escalating the whole transaction to SERIALIZABLE. Rejected as unnecessarily expensive and requiring new retry-handling for a same-row race that `FOR UPDATE` closes more cheaply — SERIALIZABLE is reserved for genuinely cross-row invariants elsewhere in the system.

## Trade-offs

`SELECT ... FOR UPDATE` holds the row lock for the duration of the transaction, a real, small, bounded concurrency cost on that specific order row. Acceptable, since concurrent refund attempts on the same order are rare and the lock only blocks other transactions touching that exact row.

## Prevention

Any "check a condition, then act on it" pattern touching money, inventory, or another externally-visible side effect gets a design-review checklist item: is this same-row (row lock or atomic `UPDATE` suffices) or cross-row (needs SERIALIZABLE plus retry)?

## Monitoring and Alerts

- A reconciliation check comparing refund-transaction count against unique order count on a daily, not monthly, cadence — this incident was only caught during a monthly audit, which is a slow detection path for a financial-correctness bug; a daily check would have surfaced it far sooner.
- The unique constraint on `(order_id)` in the `refunds` table (part of the Permanent Fix) doubling as a monitoring signal: any constraint-violation attempt logged and alerted, surfacing near-miss double-refund attempts even after the primary fix is in place.

## Interview Story

This maps directly to the read-then-write race from the isolation-levels canonical chapter, arriving with a real financial consequence. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a small number of orders were refunded twice, discovered only via a monthly reconciliation audit.
- **Task:** find the mechanism and choose the right-sized fix — not automatically the strongest one.
- **Action:** rule out duplicate client calls; trace application logs to find the exact interleaved `SELECT`-then-`UPDATE` sequence; identify the missing row lock at READ COMMITTED as the cause.
- **Result:** added `SELECT ... FOR UPDATE` for the same-row race and a unique constraint plus idempotency key as defense in depth, deliberately avoiding SERIALIZABLE since the conflict was same-row, not cross-row.

## Staff-Level Discussion

The sharpest technical point in this incident is the distinction between "same-row race" and "cross-row write skew," which look superficially similar (both are concurrency anomalies producing an incorrect outcome from individually reasonable-looking transactions) but have different correct fixes: a same-row race is closed cheaply with `SELECT ... FOR UPDATE`, while a genuine cross-row invariant violation needs SERIALIZABLE and retry handling. Reaching for SERIALIZABLE here would work, but at real, unnecessary cost and complexity for a problem `FOR UPDATE` solves more cheaply. A Staff engineer's value in reviewing a proposed fix for a concurrency bug is verifying the fix matches the actual anomaly class, not simply reaching for the strongest available tool because "money is involved" — over-fixing has its own real cost, just a less visible one than under-fixing.

## Related Handbook Chapters

- [Isolation Levels and Concurrency Anomalies](../handbook/databases/isolation-levels-and-concurrency-anomalies.md) — canonical read-then-write race and write-skew distinction used here.
- [Idempotency at System Edges](../handbook/system-design/idempotency.md) — the client-side idempotency-key mechanism used as a complementary fix.
