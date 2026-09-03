---
title: "Cheat Sheet: Multi-Tenancy Isolation Models"
slug: multi-tenancy-isolation-models
document_type: cheat-sheet
domain: security
topic_id: T-1307
canonical: ../handbook/security/multi-tenancy-isolation-models.md
last_updated: 2026-08-05
---

# Multi-Tenancy Isolation Models

**Canonical chapter:** [`syllabus/12-security/multi-tenancy-isolation-models.md`](../syllabus/12-security/multi-tenancy-isolation-models.md)

## Core Mental Model

Three ways to house multiple companies in one building: **silo** gives each tenant its own building — maximum isolation, maximum cost. **Pool** (shared schema) puts every tenant in one building with locked office doors — cheaper, but every door's lock (every query's `tenant_id` filter) must be correctly installed and checked, and one unlocked door is a real breach. **Bridge** (hybrid) dedicates some floors to specific high-value tenants. Row-Level Security is the building itself refusing to open a door unless the badge matches — a guarantee that doesn't depend on every employee remembering to check.

## Essential Definitions

- **Silo** — fully separate infrastructure per tenant. Strongest isolation, highest operational cost (scales linearly with tenant count).
- **Pool** — shared database/schema, tenant identity as a data attribute (`tenant_id`). Lowest cost, weakest structural isolation unless database-enforced.
- **Bridge/hybrid** — a mix, commonly shared infrastructure for most tenants with dedicated infrastructure for specific high-tier/compliance-need tenants.
- **Row-Level Security (RLS)** — a PostgreSQL feature enforcing a filtering policy on every query against a table at the database level, regardless of which code issued the query.

## Decision Table

| Model | Isolation strength | Operational cost | When to choose |
|---|---|---|---|
| Silo | Highest (structural) | Highest (scales linearly) | Compliance/contractual requirement, very large tenants |
| Pool + RLS | Strong, database-enforced | Lowest | Default for the common case |
| Pool, app-level filtering only | Weakest — depends on every query being correct | Lowest | Avoid as the sole enforcement mechanism |
| Bridge/hybrid | Mixed, matched to tier | Mixed | Customer base with genuinely different requirements per tier |

**Trade-offs:** application-level `tenant_id` filtering must be correct in every query, every code path, forever — one missed filter is a direct cross-tenant leak. RLS converts this to "the database refuses by default," but is not unconditional: superuser/`BYPASSRLS` roles bypass it entirely.

## Key Numbers (real, executed — live PostgreSQL 16 RLS demonstration)

```
As app_user, app.tenant_id = tenant_a: returns only tenant_a's 2 rows
As app_user, app.tenant_id = tenant_b: returns only tenant_b's 1 row
As app_user, no tenant_id set at all:  0 rows  <- fail-closed, not an error, not all rows

As superuser postgres (BYPASSRLS), no SET needed:
  returns ALL 3 rows across both tenants  <- RLS bypassed entirely, documented behavior
```

`current_setting('app.tenant_id', true)` returns `NULL` when unset, and `tenant_id = NULL` is never true in SQL's three-valued logic — the policy denies everything by default rather than failing open.

## Common Pitfalls

- Treating "we filter every query by `tenant_id`" as equivalent to "we have tenant isolation" — real but fragile, dependent on every query everywhere being correct, including code not yet written.
- Assuming enabling RLS alone is sufficient without auditing which database roles are exempt (`BYPASSRLS`, superuser) and which code paths use them.
- Not accounting for RLS's fail-closed default (zero rows when context is unset) as a distinct failure mode from an error — can look like "no data" instead of "misconfigured session."
- Defaulting to silo for the whole product without a concrete requirement driving the extra cost, when pool+RLS would meet the actual need.

## Interview Answer Skeleton

**30-sec:** Three isolation models: silo (separate infrastructure, strongest, most expensive), pool (shared schema, cheapest, weakest by default), bridge (mixed, by tenant tier). RLS strengthens pool isolation by moving the guarantee from "every query must remember" to "the database refuses by default" — but any role with superuser or `BYPASSRLS` status bypasses it entirely.

**2-min:** Add why application-level filtering alone is fragile (must be correct in every code path, forever) + the real measured evidence (correct per-tenant scoping, fail-closed zero rows when context unset, and — the critical caveat — a superuser query returning all tenants' rows with no `SET` at all) + the trade-off (RLS's guarantee depends entirely on which database role each code path actually uses).

**Whiteboard:** Three buildings — "Silo" (separate small buildings), "Pool" (one building, individually locked office doors), "Bridge" (one building with a few separate wings). Zoom into Pool's office doors: a badge-reader lock labeled "RLS policy — checked by the building itself." Draw one door explicitly bypassing the reader, labeled "superuser / BYPASSRLS — walks past every lock."

**Staff-level framing:** treat RLS's superuser/`BYPASSRLS` exemption as the central, non-obvious risk in an RLS-based architecture, and factor organization-wide database-role auditing into the isolation strategy — not just the primary application's connection, but background jobs, analytics tools, and admin scripts too.

## Production Warning Signs

- An incident review finds a batch analytics job connected using a superuser-equivalent role and inadvertently exported cross-tenant data — the RLS policy was never broken; the leak came entirely from an out-of-band access path exempt from it. Audit every database credential across the organization, not just the primary application.
- A query returns zero rows unexpectedly for a legitimate request in an RLS-based system — check whether the session-level tenant context was actually set on that connection before assuming a data or logic bug.
- **Prevention:** provision the application's primary database role explicitly without superuser/`BYPASSRLS`, verify this as part of infrastructure provisioning, and periodically audit all database roles across every system component for exempt status.

## Related

- `syllabus/12-security/authn-authz-rbac-vs-abac.md`
- `syllabus/12-security/owasp-top-10-for-backend-services.md`
- `syllabus/06-databases/table-partitioning-and-sharding-strategies.md`
