---
title: "Flash-Sale Latency Collapse from Pessimistic Locks Held Across Payment Authorization"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/databases/optimistic-vs-pessimistic-locking.md
  - ../handbook/databases/locks-deadlocks-and-lock-escalation.md
source: handbook/databases/optimistic-vs-pessimistic-locking.md#production-scenarios
---

# Flash-Sale Latency Collapse from Pessimistic Locks Held Across Payment Authorization

## Context

An inventory-decrement endpoint uses `LockModeType.PESSIMISTIC_WRITE` on every read of a product's stock row, with the lock held across the full transaction — including an external payment authorization call.

## Symptoms

During a promotional flash sale, the inventory-decrement endpoint's p99 latency rises from 40ms to over 4 seconds, and a significant fraction of requests time out entirely, even though the database's CPU and I/O utilization are both well within normal bounds.

## Impact

A large share of flash-sale checkout requests either stall for multiple seconds or time out completely, at exactly the moment of peak customer demand and peak business value.

## Initial Hypotheses

A database performance regression or a missing index.

## Evidence

Under normal traffic, contention on any single popular product row is rare enough to be invisible. During the flash sale, thousands of concurrent requests target the same handful of popular product rows simultaneously, and each request genuinely serializes behind the previous one's full transaction duration — including payment authorization, which the lock is held across — exactly the mechanism this chapter's `PessimisticLockingBlockingDemo` measures directly, just compounded across thousands of waiters instead of one.

## Investigation Timeline

1. p99 latency spike (40ms to 4+ seconds) and elevated timeout rate observed during a flash sale, with database CPU and I/O both within normal bounds — ruling out a resource-saturation explanation on its own.
2. Missing-index and general performance-regression hypotheses raised, but inconsistent with normal-range database resource utilization.
3. Locking strategy for the inventory-decrement endpoint reviewed, confirming `PESSIMISTIC_WRITE` is acquired on every stock-row read.
4. Lock hold duration confirmed to span the entire transaction, including the external payment-authorization call.
5. Contention pattern confirmed against `PessimisticLockingBlockingDemo`: thousands of concurrent requests targeting the same small set of popular product rows, each waiter serializing behind the full held-lock duration of the request ahead of it.

## Root Cause

Pessimistic locking was the wrong default for a workload with occasional extreme contention on a small number of hot rows, because every waiting request pays the *entire* held-lock duration, and that duration included a slow, external call (payment authorization) that had no reason to happen while holding a database lock.

## Immediate Mitigation

Moved payment authorization outside the locked transaction entirely, shrinking the lock's held duration dramatically.

## Permanent Fix

Switched to optimistic locking with a bounded retry loop for the stock-decrement operation specifically, accepting a small number of real `OptimisticLockException` retries under contention in exchange for not holding a database lock across any external call ever again.

## Alternatives Considered

None recorded beyond the shift from pessimistic to optimistic locking for this specific hot-row operation — the scenario treats it as the direct structural fix rather than one option among several.

## Trade-offs

A small percentage of requests during peak contention now retry once or twice rather than waiting in a lock queue — a real, measured latency cost, but bounded and far smaller than the multi-second queuing the pessimistic approach produced.

## Prevention

Any new locking decision on a hot-row workload must now explicitly state its expected contention profile and justify pessimistic locking specifically when chosen, rather than defaulting to it as the "safer-sounding" option.

## Monitoring and Alerts

- Alert on p99 latency for lock-guarded endpoints diverging from database-level CPU/I/O utilization — the specific signature this incident showed (severe latency spike with normal-range database resources) is the tell that the bottleneck is application-level lock queuing, not database capacity, and should route the investigation there directly.
- Track `OptimisticLockException` retry rate on the permanent-fix code path as a standing metric; a rate climbing well past the "small number of retries" baseline observed during a normal flash sale signals contention has grown past what the bounded retry loop was sized for.
- Add a lock-hold-duration audit (or an automated check) for any code path acquiring `PESSIMISTIC_WRITE` or an equivalent lock, flagging any external call (payment, third-party API, network I/O) made while the lock is held, before that code path is exercised at real contention volume.

## Interview Story

This maps directly to a "choose a locking strategy by contention profile" question. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a flash-sale inventory system using pessimistic locking everywhere saw p99 latency jump from 40ms to over 4 seconds with database resources still healthy.
- **Task:** find the real bottleneck, since it wasn't the database itself.
- **Action:** confirmed via measurement that thousands of concurrent requests were serializing behind pessimistic locks on a handful of hot product rows, each wait spanning the full transaction duration including an external payment-authorization call.
- **Result:** moved payment authorization outside the lock as an immediate fix, then switched the stock-decrement path to optimistic locking with bounded retries, trading a small bounded retry cost for eliminating multi-second lock queuing.

## Staff-Level Discussion

The instructive failure here isn't "pessimistic locking is wrong" — it's that a locking strategy chosen without an explicit contention-profile assumption behaves invisibly correctly under normal load and catastrophically under a load shape (thousands of requests hitting the same few rows) that only shows up during exactly the events — flash sales, viral moments — where the business cares most about performance. The deeper mistake compounding the locking choice was holding an external, slow call (payment authorization) inside the lock's scope at all: that decision alone multiplies the cost of whatever locking strategy is chosen, and a Staff engineer reviewing any lock-guarded transaction should treat "does this hold a lock across an external call" as a standing design-review question independent of pessimistic-versus-optimistic. The organizational fix — requiring an explicit contention-profile justification before choosing pessimistic locking — converts a plausible-sounding default ("pessimistic sounds safer") into a decision that has to be defended against the specific hot-row failure mode this incident demonstrated.

## Related Handbook Chapters

- [Optimistic vs. Pessimistic Locking](../handbook/databases/optimistic-vs-pessimistic-locking.md) — canonical trade-off analysis and the `PessimisticLockingBlockingDemo` measurement this incident reproduces at scale.
- [Locks, Deadlocks, and Lock Escalation](../handbook/databases/locks-deadlocks-and-lock-escalation.md) — the underlying row-locking mechanics `PESSIMISTIC_WRITE` relies on.
