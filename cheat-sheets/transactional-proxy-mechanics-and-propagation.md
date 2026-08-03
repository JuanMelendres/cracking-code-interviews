---
title: "Cheat Sheet: Spring Transactional Proxy Mechanics and Propagation"
slug: transactional-proxy-mechanics-and-propagation
document_type: cheat-sheet
domain: spring
topic_id: T-504
canonical: ../handbook/spring/transactional-proxy-mechanics-and-propagation.md
last_updated: 2026-08-03
---

# Spring Transactional Proxy Mechanics and Propagation

**Canonical chapter:** [`handbook/spring/transactional-proxy-mechanics-and-propagation.md`](../handbook/spring/transactional-proxy-mechanics-and-propagation.md)

## Core Mental Model

`@Transactional` is not a property of your method — it's a property of the call that reaches it. The exact same method, called two different ways, can either run inside a transaction or not, depending entirely on whether the call passed through Spring's proxy. Self-invocation bypasses the proxy (no transaction starts, silently); the proxy is what applies the rollback rule; propagation is a proxy-level decision about how a new call relates to whatever transaction is already active on the calling thread.

## Essential Definitions

- **Proxy (JDK dynamic vs. CGLIB)** — interface-implementing beans get a JDK dynamic proxy; otherwise a CGLIB subclass proxy, which only overrides public/package-visible methods — explains why `@Transactional` has no effect on `private` or `final` methods.
- **Self-invocation** — a call via `this.someMethod()` reaches the real target directly, never the proxy, so no transaction starts, with no warning.
- **Default rollback rule** — rolls back on `RuntimeException`/`Error`; does **not** roll back on checked exceptions by default. `rollbackFor` overrides this.
- **`readOnly`** — a hint, not a guarantee. Calls `setReadOnly(true)`; whether a write is actually blocked depends entirely on the JDBC driver.
- **`REQUIRES_NEW`** — suspends the existing transaction, starts an independent one; a genuine self-deadlock risk if the inner transaction needs a row lock the suspended outer transaction already holds.

## Decision Table (propagation modes)

| Propagation | Behavior | Real use case |
|---|---|---|
| `REQUIRED` (default) | Joins existing, or starts one | The default for almost everything |
| `REQUIRES_NEW` | Suspends existing, always starts new/independent | Audit logging that must survive the caller's rollback |
| `SUPPORTS` | Joins if exists, non-transactional otherwise | Read methods usable both inside/outside a transaction |
| `MANDATORY` | Requires existing; throws if none | Enforcing a method is never called outside a transaction |
| `NOT_SUPPORTED` | Suspends existing, runs without one | Rare; a long-running non-transactional op |
| `NEVER` | Throws if a transaction exists | Enforcing the opposite of MANDATORY |
| `NESTED` | Savepoint-based nested transaction, if driver supports it | Partial rollback within a larger transaction |

## Key Numbers (real, executed — Spring Framework 6.1.14, plain jars, no Boot auto-config)

- **Self-invocation:** `isActualTransactionActive()` = `true` via proxy, `false` via `this.method()`
- **Default rollback rule:** checked exception thrown → row **survives** (rollback did NOT happen). With `rollbackFor=Exception.class` → row correctly rolled back
- **REQUIRES_NEW:** outer table (should roll back) = 0 rows; audit_log table (REQUIRES_NEW, should survive) = 1 row — confirms independent commit despite outer rollback
- **`readOnly` driver-dependence:** on H2, a write inside `readOnly=true` **succeeded** (no exception); on PostgreSQL, the same write **failed**: `ERROR: cannot execute INSERT in a read-only transaction`
- **Connection-pool exhaustion:** pool size 2, two threads hold a connection 6s each → third request **failed after 2010ms** waiting: `CannotCreateTransactionException`

Proxy dispatch itself is O(1)/negligible per-call overhead — these are correctness-under-a-runtime-mechanism concerns, not algorithmic ones.

## Common Pitfalls

- Assuming `@Transactional` "just works" regardless of how the method is called
- Assuming any thrown exception triggers rollback, not specifically unchecked exceptions/`Error`
- Treating `readOnly = true` as a guaranteed, portable write-prevention mechanism
- Making an external network call from inside a transaction boundary
- Placing the boundary at the controller layer (too broad) or repository layer (too narrow)

## Interview Answer Skeleton

**30-sec:** Proxy wraps the bean; only works through the proxy; self-invocation bypasses it silently; default rollback rule covers only unchecked exceptions/`Error`, checked exceptions need `rollbackFor`.

**2-min:** Add JDK/CGLIB proxy mechanism + why it exists (avoid hand-written demarcation) + the `REQUIRES_NEW` independent-commit-vs-deadlock trade-off + the measured PostgreSQL-vs-H2 `readOnly` difference.

**Whiteboard:** Draw Caller → Proxy → Target with `PlatformTransactionManager` off to the side; then a second arrow from Target back to itself labeled "self-invocation (`this.method()`)" bypassing the Proxy box entirely — "the single image that makes the whole chapter's failure modes click."

## Production Warning Signs

- p99 latency spikes on one endpoint; unrelated endpoints sharing the same DB pool start returning `CannotCreateTransactionException` under moderate load
- APM tracing shows the transaction span open for an entire HTTP call, not just DB ops
- Connection-pool utilization near 100%, correlated with a downstream service's latency spike
- **Real incident pattern:** an HTTP call placed between two DB operations inside `@Transactional` holds a pooled connection for the HTTP call's full duration. Fix isn't a bigger pool (just moves the cost to DB connection limits) — it's moving the HTTP call outside the transaction boundary.

## Related

- [Isolation Levels and Concurrency Anomalies](isolation-levels-and-concurrency-anomalies.md)
- `handbook/databases/index-structures-btree-composite-covering.md`
