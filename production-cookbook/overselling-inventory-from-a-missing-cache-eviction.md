---
title: "Overselling Inventory From a Missing Cache Eviction"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/spring/spring-cache-abstraction-and-pitfalls.md
source: handbook/spring/spring-cache-abstraction-and-pitfalls.md#production-scenarios
---

# Overselling Inventory From a Missing Cache Eviction

## Context

An inventory service exposed a `@Cacheable` stock-check endpoint (`findById`) over product data. Months later, a separate, newer restocking endpoint was added that wrote directly to the same product rows without going through the write path the original cache design assumed.

## Symptoms

A limited-quantity promotional item was oversold by several units past its real available stock. The overselling was discovered only after fulfillment failed for the last few orders.

## Impact

Customers were sold units of a promotional item that did not exist in real inventory, producing fulfillment failures and the operational cost of unwinding those orders.

## Initial Hypotheses

- A database-level race condition in the stock-decrement logic — this was the first hypothesis pursued.

## Evidence

The stock-check endpoint (`findById`, `@Cacheable`) was correctly cached for performance. A separate, newer restocking endpoint had been added later that called update logic performing a real, direct database write with no corresponding cache eviction. Every stock check after that point served the pre-restock cached quantity, which read as "in stock" for units that had, in the real database, already sold out.

## Investigation Timeline

1. **Overselling detected** when fulfillment failed for the last few orders of a promotional item.
2. **Race-condition hypothesis pursued first**, focused on the stock-decrement logic itself.
3. **Write paths audited**, surfacing a newer restocking endpoint added by a different engineer, writing directly to the database with no corresponding `@CacheEvict`.
4. **Cache-vs-database comparison confirmed** the cached value was serving a pre-restock quantity that no longer matched the real row.

## Root Cause

The caching layer was correct at the time it was written. The bug was introduced later, when a different engineer added a new write path to data covered by an existing `@Cacheable` method without knowing a cache existed over it — the write updated the database but never evicted the corresponding cache entry.

## Immediate Mitigation

Manually cleared the affected cache entries and paused the promotion.

## Permanent Fix

Added `@CacheEvict` to every write path touching product data, and added an architecture-level rule requiring any new write path to data covered by an existing `@Cacheable` method to be reviewed specifically for eviction correctness.

## Alternatives Considered

None recorded as seriously considered — the fix is the structurally correct one (an eviction on every write path), not a workaround.

## Trade-offs

The review checklist adds real friction to adding new write paths. This was accepted because the alternative — silent overselling — was already a real, costly incident.

## Prevention

Any new `@Cacheable` annotation now requires documenting, in the same commit, every write path that must evict it — not left to be discovered the next time someone forgets.

## Monitoring and Alerts

- A code-review gate specifically checking eviction correctness for any new write path touching already-cached data, rather than relying on the original author's memory of the caching layer.
- Cross-checking cached stock levels against the real row value for high-velocity, limited-quantity items, since these are exactly the items where a stale cache read has the most immediate customer-facing consequence.

## Interview Story

This maps to a "why did our cache serve wrong data" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a promotional item was oversold because a stock-check cache kept serving a pre-restock quantity.
- **Task:** find why a correctly written cache started returning wrong data.
- **Action:** ruled out a database race condition; traced the actual cause to a newer write path added without a matching `@CacheEvict`.
- **Result:** added the missing eviction, plus a review rule requiring every new write path to already-cached data to be checked for eviction correctness.

## Staff-Level Discussion

Declarative caching's biggest organizational risk is not that any single engineer forgets to add an eviction — it's that the cache annotation and the write paths that must invalidate it can live arbitrarily far apart in the codebase, and nothing in the language or framework forces them to be reviewed together. `@Cacheable` documents the read side; there is no equivalent enforced documentation of the write side. The durable fix is not "remember to evict" as a personal discipline across the team — it's making the write-path obligation visible at the point the cache is declared, so a reviewer encountering a new write path months later has something concrete to check against rather than needing full institutional memory of every cache in the system.

## Related Handbook Chapters

- [Spring Cache Abstraction and Pitfalls](../handbook/spring/spring-cache-abstraction-and-pitfalls.md) — canonical eviction-responsibility model and stale-cache mechanism used here.
