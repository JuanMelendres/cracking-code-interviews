---
title: "N+1 Query Regression from a Lazy Collection Read Inside a DTO Mapping Loop"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md
  - ../syllabus/06-databases/query-planning-and-explain-analyze.md
source: handbook/databases/jpa-entity-lifecycle-and-the-n1-problem.md#production-scenarios
---

# N+1 Query Regression from a Lazy Collection Read Inside a DTO Mapping Loop

## Context

An `/orders` listing endpoint, previously fast with flat latency regardless of page size, is changed by a "harmless" feature addition: each order's line-item count is added to the response DTO by calling `order.getLineItems().size()` inside the DTO-mapping loop. `lineItems` is a `LAZY` `@OneToMany` association.

## Symptoms

Immediately after the feature ships, the endpoint's p99 latency starts scaling almost linearly with the number of orders returned per page — a property the endpoint never had before.

## Impact

A page of 50 orders now takes noticeably longer than a page of 10, degrading the experience specifically for power users and any client requesting larger pages — exactly the users a team least wants to punish.

## Initial Hypotheses

- A missing database index on the orders table — checked and ruled out; the base query itself is fast and unchanged.
- An algorithmic inefficiency in the new line-item-counting logic — checked and ruled out; the counting logic is `O(1)` per order, just `lineItems.size()`.
- The line-item association is being lazily loaded once per order — correct.

## Evidence

Enabling Hibernate's statistics (`hibernate.generate_statistics=true`) in a staging environment shows the endpoint's prepared-statement count is `1 + N` for `N` orders on the page — one query for the order list, then one additional query per order the moment `.getLineItems().size()` is called on each — a direct, measured N+1, not a guess.

## Investigation Timeline

1. p99 latency regression observed immediately following the line-item-count feature's release, tracking almost linearly with page size.
2. Missing-index hypothesis checked against the base listing query — ruled out; the query itself is unchanged and fast.
3. New counting logic's algorithmic complexity checked — ruled out; `lineItems.size()` is `O(1)` per order.
4. Hibernate statistics enabled in staging, exposing a `1 + N` prepared-statement count for `N` orders on the page.
5. The extra queries traced to `order.getLineItems().size()` executing inside the DTO-mapping loop against a `LAZY` `@OneToMany` collection.

## Root Cause

The line-item-count feature reads `order.getLineItems().size()` inside the DTO-mapping loop, and `lineItems` is a `LAZY` `@OneToMany` — triggering one additional query per order on every request to this endpoint, on top of the base listing query.

## Immediate Mitigation

None needed beyond the fix itself — this isn't correctness-breaking, only a latency regression, so it goes straight to the permanent fix without a stopgap.

## Permanent Fix

Change the specific listing query to a `JOIN FETCH` on `lineItems` (if the full entities are genuinely needed elsewhere in the response), or — since only a *count* is actually needed here, not the full line-item entities — replace the lazy-collection-size approach entirely with a single aggregate query (`SELECT o.id, COUNT(li) FROM Order o LEFT JOIN o.lineItems li GROUP BY o.id`) or a DTO projection, both of which return exactly the needed data in one round-trip without loading full `LineItem` entities the endpoint never uses.

## Alternatives Considered

Marking `lineItems` `EAGER` — rejected explicitly, since it would apply to every other code path loading an `Order` too, most of which never need line items at all, permanently adding an unconditional join (or unconditional extra query) to every one of those unrelated call sites.

## Trade-offs

The aggregate-query/projection approach is the most efficient fix but requires writing a query specific to this endpoint's exact needs rather than reusing the general-purpose entity-loading code path — a small amount of extra, endpoint-specific query code in exchange for avoiding both N+1 and loading data the endpoint never actually uses.

## Prevention

Enable Hibernate statistics (or an equivalent SQL-count assertion in integration tests) in any environment where N+1 could plausibly be introduced, and add a regression test asserting a fixed, small query count for hot-path listing endpoints — turning "prepared statement count doubled" into a test failure instead of a production latency regression discovered later.

## Monitoring and Alerts

- Export Hibernate's per-request statement count as a metric for listing endpoints and alert when it exceeds a small, fixed threshold — this catches the exact `1 + N` signature the staging investigation found, before it reaches production traffic at scale.
- Track p99 latency against page size as a standing dashboard panel for paginated endpoints; a curve that stops being flat is the earliest visible symptom of a reintroduced N+1, even for endpoints where statement-count instrumentation isn't yet wired up.
- Treat the CI regression test asserting fixed query count (the Prevention step above) as a release gate for any endpoint that maps entities with lazy associations into a response DTO.

## Interview Story

This maps directly to a "N+1 query at real production scale" question. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** an order-listing endpoint's p99 latency began scaling linearly with page size immediately after a change that looked entirely unrelated to persistence — adding a line-item count to a DTO.
- **Task:** find the cause without assuming it was a database or algorithmic problem.
- **Action:** ruled out a missing index and an inefficient counting algorithm; enabled Hibernate statistics in staging and found a `1 + N` prepared-statement count; traced it to a lazy collection's `.size()` call inside the DTO-mapping loop.
- **Result:** replaced the lazy-collection read with a single aggregate query, eliminating the N+1 and adding a query-count regression test to catch the pattern automatically in the future.

## Staff-Level Discussion

The dangerous property of this bug class is that the triggering change looks nothing like a persistence change — "add a count to a DTO" reads as a display-layer tweak, not a data-access decision, so it is exactly the kind of change unlikely to get scrutiny from a reviewer thinking about query plans. The fix itself forces a real trade-off a Staff engineer should name explicitly: writing an endpoint-specific aggregate query or projection is more efficient than reusing the general-purpose entity-loading path, but it also means the codebase now carries two ways to get an order's line-item information, each optimized for a different caller. The systemic response — enabling statement-count instrumentation and a regression test for hot-path listing endpoints — matters more than the one-line fix, because the same lazy-association trap is available to the next engineer who adds the next "harmless" field to the next DTO, and only an automated check, not reviewer vigilance, closes that door for good.

## Related Handbook Chapters

- [JPA Entity Lifecycle and the N+1 Problem](../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md) — canonical mechanics of lazy loading and the N+1 pattern this incident reproduces.
- [Query Planning and EXPLAIN ANALYZE](../syllabus/06-databases/query-planning-and-explain-analyze.md) — the diagnostic discipline (verify with real query counts and plans, not code-review intuition) that this incident's evidence-gathering follows.
