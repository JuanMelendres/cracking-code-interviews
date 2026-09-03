---
title: "Connection Pool Exhaustion From an HTTP Call Inside a Transaction"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md
source: handbook/spring/transactional-proxy-mechanics-and-propagation.md#production-scenarios
---

# Connection Pool Exhaustion From an HTTP Call Inside a Transaction

## Context

A checkout endpoint's `@Transactional` method makes an HTTP call to an inventory-reservation service between two database operations. Every endpoint in the application shares one connection pool.

## Symptoms

A checkout endpoint's p99 latency spikes. During the same window, several unrelated endpoints sharing the same database connection pool start returning `CannotCreateTransactionException` under only moderate load.

## Impact

A single slow downstream dependency (the inventory-reservation HTTP call) degrades endpoints that have nothing to do with inventory, because they all draw from the same connection pool.

## Initial Hypotheses

- The database itself is slow — checked and ruled out; query latency on the actual `INSERT`s is normal.
- The connection pool is undersized — partially true, but sizing isn't the real question; the real question is why it's being exhausted.
- A downstream service outage — confirmed; the inventory service's own p99 spiked independently, at the same time.

## Evidence

APM tracing shows the checkout transaction's Spring transaction span open for the entire duration of the HTTP call to the inventory service, not just the database operations within it. Connection pool metrics show near-100% utilization correlated exactly with the inventory service's own latency spike.

## Investigation Timeline

1. **Latency and pool-exhaustion symptoms observed together**, on endpoints unrelated to checkout or inventory.
2. **Database ruled out** using query-level latency metrics, which stay normal throughout.
3. **Downstream outage confirmed independently**, correlating in time with the pool-exhaustion window.
4. **Mechanism confirmed via tracing**: the transaction span — and therefore the held connection — spans the full HTTP call duration, not just the database work inside it.

## Root Cause

The `@Transactional` boundary is placed around a method that makes an HTTP call to the inventory service between two database operations. Every in-flight checkout request holds a pooled database connection for the HTTP call's entire duration, so a slow downstream call directly exhausts the shared pool for every other endpoint.

## Immediate Mitigation

Increase the connection pool size as a stopgap while the code fix ships. This buys headroom but does not address the root cause, and has its own downstream cost against the database's own connection limit.

## Permanent Fix

Move the inventory HTTP call outside the transaction boundary entirely: reserve inventory before opening the transaction, accepting a compensating action if the subsequent database write fails, or restructure into two smaller transactions with the HTTP call between them.

## Alternatives Considered

Making the HTTP call asynchronous within the same transaction. Rejected — it does not shorten how long the connection is held, since the transaction still cannot commit until the async result is known.

## Trade-offs

Splitting into two transactions sacrifices all-or-nothing atomicity across the inventory call and the order write. Accepted, with an explicit compensating-action path for the case where inventory reservation fails after the order row is already committed.

## Prevention

A code-review checklist item — and ideally a static check — that flags any network call (HTTP client, message broker publish, another service's blocking call) inside a method annotated `@Transactional`, or called from within one while a transaction is active.

## Monitoring and Alerts

- Connection pool utilization correlated against downstream-dependency latency, not just watched in isolation — the defining signature here is that pool exhaustion on unrelated endpoints tracks a specific downstream's latency, which is a much faster diagnostic than working backward from `CannotCreateTransactionException` alone.
- APM span duration for `@Transactional`-annotated methods, flagged when a span's duration is dominated by a non-database call rather than the database operations it nominally wraps.

## Interview Story

This maps to the "HTTP call inside a transaction" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** unrelated endpoints started failing with connection-pool errors during a checkout-latency spike.
- **Task:** explain how a downstream inventory outage caused failures on endpoints that never call inventory.
- **Action:** rule out the database directly; confirm the downstream outage independently; use APM tracing to show the transaction span — and the held connection — spanning the full HTTP call, not just the database work.
- **Result:** moved the HTTP call outside the transaction boundary with a compensating action for reservation failure, and added a review check for network calls inside `@Transactional` methods.

## Staff-Level Discussion

The mechanism is narrow — one method's transaction boundary is drawn too wide — but the blast radius is organization-wide, because a shared connection pool means any team's transaction-boundary mistake can degrade every other team's endpoint. This is the argument for treating "no network calls inside `@Transactional`" as a platform-level, ideally tooling-enforced rule rather than a per-team code-review reminder: the cost of the mistake is not contained to the team that made it, so the review discipline can't be either. A Staff engineer should also recognize that pool exhaustion is the *shared-resource* signature of this bug class — the same root cause (a synchronous call held open too long) will show up differently depending on what resource is shared, so the review rule needs to generalize beyond "database connections" to any bounded shared resource held across a blocking call.

## Related Handbook Chapters

- [Transactional Proxy Mechanics and Propagation](../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md) — canonical `@Transactional` boundary and connection-holding mechanics used here.
