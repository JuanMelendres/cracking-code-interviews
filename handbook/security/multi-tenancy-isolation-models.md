---
title: "Multi-Tenancy Isolation Models"
slug: multi-tenancy-isolation-models
document_type: handbook-chapter
domain: security
status: draft
version: 1.0
last_reviewed: 2026-08-02
difficulty:
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - authn-authz-rbac-vs-abac.md
related:
  - authn-authz-rbac-vs-abac.md
  - owasp-top-10-for-backend-services.md
  - ../databases/table-partitioning-and-sharding-strategies.md
  - ../../study-packs/week-17/05-multi-tenancy-isolation-models.md
official_references:
  - https://www.postgresql.org/docs/current/ddl-rowsecurity.html
---

# Multi-Tenancy Isolation Models

> **Topic register:** T-1307 (Multi-tenancy isolation models, IWI 5.6) · Staff tier · Occasional interview frequency [O]

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Mental Model](#mental-model)
4. [Definition and Purpose](#definition-and-purpose)
5. [Core Concepts](#core-concepts)
6. [Internal Implementation](#internal-implementation)
7. [Production Scenarios](#production-scenarios)
8. [Failure Modes and Debugging](#failure-modes-and-debugging)
9. [Trade-offs](#trade-offs)
10. [Decision Framework](#decision-framework)
11. [Common Mistakes](#common-mistakes)
12. [Anti-Patterns](#anti-patterns)
13. [Best Practices](#best-practices)
14. [Interview Answer Framework](#interview-answer-framework)
15. [Interview Questions](#interview-questions)
16. [Summary](#summary)
17. [Key Takeaways](#key-takeaways)
18. [Cheat Sheet](#cheat-sheet)
19. [Flashcards](#flashcards)
20. [Practice Exercises](#practice-exercises)
21. [Solutions](#solutions)
22. [Additional Reading](#additional-reading)
23. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can name the three standard multi-tenancy isolation models (silo, pool/shared-schema, bridge/hybrid), explain the specific security and operational trade-off each makes, and cite a real, measured demonstration of PostgreSQL Row-Level Security enforcing cross-tenant isolation in a shared schema — including the critical detail that a superuser role bypasses it entirely.

## Why This Matters in Interviews

Multi-tenancy isolation questions probe whether a candidate understands that "which tenant owns this row" is a security boundary, not just a data-modeling detail — and that the strength of that boundary depends entirely on *where* it's enforced (application code, a shared database's row-level policies, or fully separate infrastructure per tenant) and *who* can bypass it. This is a favorite Staff-level topic specifically because the "obvious" cheap answer (shared schema, filter by `tenant_id` in application code) is also the answer with the weakest isolation guarantee — a single missed `WHERE tenant_id = ?` clause anywhere in the codebase is a cross-tenant data leak, and interviewers want to see whether a candidate reaches for that risk unprompted or needs it pointed out.

## Mental Model

Picture three ways to house multiple companies in one building: **silo** gives each tenant its own separate building — maximum isolation, maximum cost, and no possibility of a bug in one tenant's building affecting another's. **Pool** (shared schema) puts every tenant in the same building with locked doors on individual offices — cheaper to build and maintain one building, but every door's lock (every query's `tenant_id` filter) has to be correctly installed and correctly checked, and a single unlocked door is a real breach. **Bridge** (hybrid) is a shared building with some floors dedicated to specific high-value tenants — a pragmatic middle ground, often used when one tier of the pricing model needs stronger guarantees than the shared default. The database-level defense discussed in this chapter, Row-Level Security, is the equivalent of the building itself refusing to open a door unless the person's badge matches that specific office — a guarantee that doesn't depend on every individual employee remembering to check badges manually.

## Definition and Purpose

**Multi-tenancy isolation models** describe the architectural strategies for how a system separates one customer's ("tenant's") data and resource usage from another's when multiple tenants share underlying infrastructure. The three standard models are: **silo** (fully separate infrastructure per tenant — separate database instances, sometimes separate deployments entirely), **pool** (a shared database and schema, with tenant identity as a data attribute — typically a `tenant_id` column — used to filter every query), and **bridge/hybrid** (a mix, commonly shared infrastructure for most tenants with dedicated infrastructure for specific high-tier or high-compliance-need tenants). **Row-Level Security (RLS)**, a PostgreSQL feature (also available in some other relational databases under different names), lets the database itself enforce a filtering policy on every query against a table, rather than relying on application code to always include the correct filter.

## Core Concepts

### Isolation strength and operational cost move in opposite directions across the three models

Silo provides the strongest isolation (a bug or breach affecting one tenant's dedicated infrastructure structurally cannot reach another tenant's separate infrastructure) but the highest operational cost — N tenants means managing N sets of infrastructure, migrations, and scaling decisions. Pool provides the weakest structural isolation (all tenants share one enforcement surface, and every query anywhere in the codebase must correctly apply tenant filtering) but the lowest operational cost — one schema, one migration path, one set of infrastructure to scale. Bridge accepts this trade-off is not one-size-fits-all across a customer base and applies different models to different tenant tiers.

### Application-level tenant filtering is the weakest enforcement point, because it must be correct everywhere, every time

In a pool model without database-level enforcement, tenant isolation depends entirely on every single query, in every code path, in every service that touches the shared tables, correctly including a `WHERE tenant_id = ?` clause — a single missed filter (a new report feature, an internal admin tool, a bulk-export script, a background job) is a direct cross-tenant data leak. This is structurally the same category of risk as the IDOR vulnerability in [OWASP Top 10](owasp-top-10-for-backend-services.md) — a missing check rather than a wrong one — but at the scale of an entire tenant's data rather than a single record.

### Database-enforced isolation (RLS) moves the guarantee from "every query must remember" to "the database refuses regardless"

Row-Level Security attaches a policy directly to a table, evaluated by the database on every query against it, regardless of which application code, ad hoc script, or administrative tool issued the query. This converts tenant isolation from an application-code discipline (fragile, must be correct everywhere) into a database-enforced invariant (much harder to accidentally bypass) — but it is not an unconditional guarantee, as the demonstration below shows directly: a database superuser role bypasses RLS by default, meaning the application's actual database connection role matters as much as the policy itself.

## Internal Implementation

**Real PostgreSQL 16 Row-Level Security demonstration** — an `orders` table with a `tenant_id` column, RLS enabled, and a policy scoping every query to `current_setting('app.tenant_id')`:

```sql
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON orders
  USING (tenant_id = current_setting('app.tenant_id', true));
```

As the non-superuser `app_user` role, with the session variable set to each tenant in turn:

```
=== As app_user, app.tenant_id = tenant_a ===
 id | tenant_id | customer_name | amount_usd
----+-----------+---------------+------------
  1 | tenant_a  | Acme Corp     |     500.00
  2 | tenant_a  | Acme Corp     |     750.00
(2 rows)

=== As app_user, app.tenant_id = tenant_b ===
 id | tenant_id | customer_name | amount_usd
----+-----------+---------------+------------
  3 | tenant_b  | Globex Inc    |    1200.00
(1 row)
```

Identical query (`SELECT * FROM orders`) against identical underlying data, run by the identical database role — the only thing that changed between the two runs is the session-level tenant context, and the database itself, not any application code, restricted the visible rows accordingly.

**Real evidence of RLS's fail-closed behavior when no tenant context is set:**

```
=== As app_user, no tenant_id set at all ===
 id | tenant_id | customer_name | amount_usd
----+-----------+---------------+------------
(0 rows)
```

Zero rows, not an error and not all rows — `current_setting('app.tenant_id', true)` returns `NULL` when unset, and `tenant_id = NULL` is never true in SQL's three-valued logic, so the policy correctly denies everything by default rather than failing open. This fail-closed behavior is a meaningful, deliberate property: a bug that forgets to set the tenant context produces an empty result (a visible, debuggable failure) rather than a silent cross-tenant leak.

**Real evidence of the superuser bypass — the single most important caveat about RLS:**

```
=== As superuser postgres (BYPASSRLS), no SET needed ===
 id | tenant_id | customer_name | amount_usd
----+-----------+---------------+------------
  1 | tenant_a  | Acme Corp     |     500.00
  2 | tenant_a  | Acme Corp     |     750.00
  3 | tenant_b  | Globex Inc    |    1200.00
(3 rows)
```

Connected as the PostgreSQL superuser (which has the `BYPASSRLS` attribute by default), the identical `SELECT * FROM orders` query — no `SET` of tenant context at all — returns every tenant's rows unconditionally. This is not a bug in RLS; it's documented, intended behavior (superusers and roles explicitly granted `BYPASSRLS` are exempt) — but it means the entire isolation guarantee depends on the application's actual database connection using a non-superuser, non-`BYPASSRLS` role. An application accidentally connecting as a superuser (a surprisingly common default in local development that sometimes leaks into production configuration) has zero RLS protection despite the policy being correctly defined.

## Production Scenarios

**A SaaS company scales from a handful of enterprise customers to thousands of small-business tenants, and the original per-tenant-database (silo) model becomes an operational bottleneck** — migrations must run against every tenant database individually, and infrastructure cost scales linearly with tenant count regardless of how small some tenants' actual usage is. The company migrates its small-tenant tier to a shared pool model with RLS-enforced isolation, while keeping its largest enterprise customers (who specifically contracted for dedicated infrastructure as a compliance requirement) on the silo model — a real bridge/hybrid architecture driven by the actual difference in tenant needs, not a uniform default.

**An incident review finds a batch analytics job, run by a data-engineering team with direct database access for ad hoc reporting, connected using a superuser-equivalent role and inadvertently included cross-tenant data in an exported report.** The application's own request-serving code correctly enforced RLS-based isolation; the incident originated entirely from an out-of-band access path (the analytics job) that used a database role exempt from the policy. The remediation is not "fix the RLS policy" (it was never broken) but auditing every database connection across the entire organization — not just the primary application — for `BYPASSRLS` or superuser privileges, and provisioning a dedicated, RLS-subject role for any tool that needs database access outside the main application.

## Failure Modes and Debugging

- **Symptom: a query returns zero rows unexpectedly in a pool/RLS-based system, even for a legitimate, correctly-scoped request.** Check whether the session-level tenant context (`SET app.tenant_id = ...` or equivalent) was actually set on the connection handling this request — RLS's fail-closed default means a missing context produces an empty result, not an error, which can look like "no data" rather than "misconfigured session."
- **Symptom: a cross-tenant data leak is found despite RLS policies being correctly defined and tested.** Check the specific database role used by *every* code path that touches the table — not just the primary application's connection pool — for `BYPASSRLS` or superuser status; RLS provides zero protection against any connection using an exempt role, regardless of how correct the policy itself is.
- **Anti-pattern to rule out first when auditing multi-tenant isolation:** assuming "we have RLS enabled" is itself sufficient evidence of isolation — confirm which specific database roles are actually used by which specific code paths (application, background jobs, analytics tools, admin scripts, migration tooling), since any one of them using an exempt role is a real gap regardless of the policy's correctness.

## Trade-offs

Silo isolation provides the strongest security boundary and the simplest reasoning about blast radius (a compromise or bug affecting one tenant's infrastructure cannot structurally reach another's) but scales operational cost and complexity linearly with tenant count. Pool isolation with RLS provides meaningfully stronger enforcement than application-level filtering alone, converting the guarantee from "every query must remember" to "the database refuses by default" — but it remains a single shared enforcement surface, and its guarantee is only as strong as the discipline around which database roles are ever granted `BYPASSRLS` or superuser status.

## Decision Framework

Default to pool (shared schema) with database-enforced RLS for the common case — it captures most of the operational-cost benefit of a shared architecture while meaningfully strengthening the isolation guarantee beyond application-code-only filtering. Escalate specific tenants to silo isolation when there's a concrete, named requirement driving it — a compliance mandate (data residency, dedicated infrastructure contractual terms), a scale/noisy-neighbor concern for a very large tenant, or a genuinely different reliability SLA for that tier — rather than defaulting every tenant to the most expensive model preemptively. Treat "which database roles have `BYPASSRLS` or superuser status, and which code paths use them" as a standing audit item, not a one-time setup check, since new tools (analytics jobs, admin scripts, migration tooling) are a recurring source of accidentally-exempt access paths.

## Common Mistakes

- Treating "we filter every query by `tenant_id` in application code" as equivalent to "we have tenant isolation" — this is real but fragile protection, dependent on every single query everywhere being correct, forever, including code paths not yet written.
- Assuming enabling Row-Level Security is itself sufficient without auditing which database roles are exempt from it (`BYPASSRLS`, superuser) and which code paths use those roles.
- Not accounting for RLS's fail-closed default (zero rows when tenant context is unset) as a distinct failure mode from an actual error — a query silently returning nothing can look like "there's no data" rather than "the session context wasn't set."
- Defaulting an entire multi-tenant product to the silo model preemptively, without a concrete requirement driving the extra operational cost, when a pool-with-RLS model would meet the actual isolation need at a fraction of the operational overhead.

## Anti-Patterns

Granting a background job, analytics tool, or administrative script a superuser or `BYPASSRLS`-equivalent database role "for convenience," rather than provisioning it a dedicated, RLS-subject role scoped to only the specific tenant context (or explicit cross-tenant reporting need, handled through an audited, deliberate path) it actually requires — this is precisely the access pattern that produced the cross-tenant leak in this chapter's second production scenario, and it recurs because it's the path of least resistance for whoever sets up a new internal tool.

## Best Practices

Provision the application's primary database connection role explicitly without superuser or `BYPASSRLS` privileges, and verify this as part of infrastructure provisioning (not assumed) — since the entire RLS guarantee depends on it. Set the tenant-context session variable as early and as centrally as possible in the request lifecycle (a single piece of middleware or connection-acquisition logic), rather than at each individual query site, to minimize the number of places a missing-context bug could originate. Periodically audit all database roles in use across every system component — not just the primary application — for `BYPASSRLS` or superuser status, since new tools are a recurring, easy-to-miss source of exempt access paths.

## Interview Answer Framework

### 30-Second Answer

The three standard multi-tenancy isolation models are silo (separate infrastructure per tenant, strongest isolation, highest cost), pool (shared schema, tenant identity as a data attribute, lowest cost but weakest default isolation), and bridge/hybrid (a mix, driven by different tenants' actual needs). Database-enforced Row-Level Security strengthens pool isolation by moving the guarantee from "every query must remember to filter" to "the database refuses by default" — but any role with superuser or `BYPASSRLS` status bypasses it entirely, so the guarantee is only as strong as the discipline around which roles are ever granted that exemption.

### 2-Minute Answer

Definition: three models for isolating tenant data on shared infrastructure — silo (fully separate), pool (shared schema, filtered by tenant attribute), bridge (mixed, by tenant tier). Why RLS specifically matters: application-level `tenant_id` filtering alone requires every query, everywhere, forever, to be correct — a single missed filter is a direct cross-tenant leak. RLS moves that enforcement into the database itself, evaluated on every query regardless of which code issued it. How it works: a policy attached to a table restricts visible rows based on a session-level context variable, and fails closed (zero rows) rather than open when that context is unset. One trade-off: RLS meaningfully strengthens isolation but is not unconditional — a database role with superuser or `BYPASSRLS` status is exempt entirely, so the application's actual connection role matters as much as the policy. One production example: measured directly, an RLS-protected `orders` table returned only tenant-A rows for tenant-A's session context, only tenant-B rows for tenant-B's, and correctly zero rows when no context was set at all — but the identical query as a superuser role returned every tenant's rows unconditionally, with no `SET` required.

### 10-Minute Deep Dive

Cover: the three isolation models and the isolation-strength-versus-operational-cost trade-off between them; why application-level filtering alone is structurally fragile (must be correct in every code path, including ones not yet written); the real RLS demonstration showing correct per-tenant scoping, the fail-closed zero-row behavior when context is unset (a deliberate, valuable property distinct from an error), and — the single most important nuance — the superuser bypass, demonstrating that RLS is not an unconditional guarantee but one that depends entirely on which database role issues the query; the production scenario of an out-of-band analytics tool using an exempt role and causing a real leak despite a correctly-configured policy; the bridge/hybrid model as the pragmatic response to a customer base with genuinely different isolation requirements rather than a one-size-fits-all default.

### Whiteboard Explanation

Draw three buildings side by side labeled "Silo," "Pool," "Bridge" — separate small buildings for silo, one large building with individually locked office doors for pool, one large building with a few separate wings for bridge. Zoom into the pool building's office doors and draw a badge-reader lock on each, labeled "RLS policy — checked by the building itself, not by each visitor remembering to check." Then draw one door explicitly bypassing the badge reader, labeled "superuser / BYPASSRLS — walks past every lock," to make the exemption visually explicit as the critical caveat.

### Production Example

A SaaS platform's core product uses pool isolation with RLS, correctly enforced for every customer-facing request. A newly-hired data scientist sets up a Jupyter-notebook-based analytics workflow against the same production database, using credentials provisioned quickly (as superuser, for simplicity, during initial setup) by an engineer helping them get started. Months later, a routine security audit — not an incident — discovers this connection path bypasses RLS entirely, and that several ad hoc queries run from it during that period technically had access to cross-tenant data, even though no evidence suggests any query was written to specifically target another tenant's data. The remediation provisions a dedicated, RLS-subject role for all analytics access going forward, and the audit is expanded to catalog every database credential in use across the organization, not just the primary application's.

### Trade-offs to Mention

Silo maximizes isolation strength at the cost of linear operational scaling with tenant count; pool minimizes operational cost but concentrates isolation risk onto a single shared enforcement surface, whose strength depends entirely on consistent database-role discipline across every system component that touches the shared schema.

### Common Candidate Mistakes

Treating "we filter by `tenant_id`" as equivalent to "we have isolation," without acknowledging the fragility of application-only enforcement; not knowing that RLS has a superuser/`BYPASSRLS` exemption at all.

### Typical Follow-Up Questions

"If RLS fails closed (zero rows) when tenant context is unset, could that itself become a reliability problem?" → Yes — a bug or deployment issue that fails to set the tenant context on every request would produce empty results for legitimate requests rather than an obvious error, which is why setting the context centrally and early (one piece of middleware, not scattered per-query) reduces the surface for this specific failure. "How would you audit an existing system for RLS-bypass risk?" → Enumerate every distinct database role/credential in use across all system components — not just the primary application — and check each for superuser or `BYPASSRLS` status, cross-referenced against which code paths actually use each role.

### Senior-Level Expectations

Correctly names the three isolation models and their trade-off, and can explain why application-level filtering alone is fragile compared to database-enforced RLS.

### Staff-Level Discussion

Treats RLS's superuser/`BYPASSRLS` exemption as the central, non-obvious risk in an RLS-based architecture, and factors organization-wide database-role auditing into the isolation strategy rather than treating "RLS is enabled" as sufficient. Reasons about bridge/hybrid architecture decisions as driven by specific, named tenant requirements (compliance, scale, contractual terms) rather than uniform defaults, and considers RLS's fail-closed behavior as a reliability property worth deliberately designing around (centralized, early context-setting) rather than an incidental detail.

## Interview Questions

### Question 1

**A team says: "We've enabled Row-Level Security on our multi-tenant tables, so we're protected against cross-tenant data leaks." What follow-up question would you ask, and why?**

**Expected answer:** ask which database role every code path that touches those tables actually connects as — the primary application, but also any background jobs, analytics tools, admin scripts, and migration tooling — and whether any of them has superuser or `BYPASSRLS` status. RLS provides zero protection for any connection using an exempt role, regardless of how correctly the policy itself is defined, so "RLS is enabled" alone doesn't establish that isolation actually holds across the whole system.

**Common mistakes:** accepting "RLS is enabled" as sufficient without probing which roles are exempt from it.

**Follow-up questions:** "How would you structurally prevent a new tool from accidentally being provisioned with an exempt role in the future?" (a standing provisioning policy/checklist requiring justification for any `BYPASSRLS` grant, plus periodic audits, rather than relying on each engineer remembering the risk.)

**Senior-level expectations:** correctly identifies the superuser/`BYPASSRLS` exemption as the specific gap to probe.

**Staff-level expectations:** proposes a structural, ongoing audit process rather than a one-time check.

### Question 2

**Why might a company choose to migrate its largest enterprise customers from a shared pool model to dedicated silo infrastructure, even though the pool model with RLS provides real, measured isolation?**

**Expected answer:** RLS provides strong logical isolation within shared infrastructure, but doesn't provide physical/infrastructural isolation — a shared database's performance issue, outage, or a vulnerability in the database engine itself still affects every tenant sharing it. Some contractual or compliance requirements (specific data-residency rules, dedicated-infrastructure clauses, or a customer's own risk tolerance) require infrastructure-level separation that no amount of logical, in-database isolation satisfies.

**Common mistakes:** treating strong logical isolation (RLS) as equivalent to the physical isolation some compliance requirements specifically demand.

**Follow-up questions:** "Is this an all-or-nothing decision for the whole platform?" (No — a bridge/hybrid model applying silo isolation only to the specific tenants with that requirement, while keeping the operational-cost benefit of pool isolation for the rest, is the common pragmatic answer.)

**Senior-level expectations:** correctly distinguishes logical isolation from physical/infrastructural isolation.

**Staff-level expectations:** proposes the bridge/hybrid model as the pragmatic middle ground rather than an all-or-nothing platform-wide choice.

## Summary

Multi-tenancy isolation has three standard models — silo, pool, bridge — trading isolation strength against operational cost in opposite directions. Application-level tenant filtering alone is structurally fragile, since it requires every query in every code path to be correct, forever. Row-Level Security strengthens pool isolation by enforcing tenant scoping at the database level, evaluated on every query regardless of which code issued it, and fails closed (zero rows) rather than open when tenant context is unset — both demonstrated with real, measured PostgreSQL evidence. The single most important caveat, also demonstrated directly, is that RLS's guarantee is not unconditional: any database role with superuser or `BYPASSRLS` status bypasses it entirely, meaning the isolation guarantee is only as strong as the organization's discipline around which roles are ever granted that exemption, across every system component — not just the primary application.

## Key Takeaways

- Silo, pool, and bridge trade isolation strength against operational cost in opposite directions; there's no universally-correct default independent of actual tenant requirements.
- Application-level `tenant_id` filtering alone is fragile — it requires every query, everywhere, forever to be correct, including code not yet written.
- Row-Level Security converts tenant isolation from an application-code discipline into a database-enforced invariant, evaluated regardless of which code issued the query.
- RLS fails closed (zero rows, not an error) when tenant context is unset — a deliberate, valuable property, but one that can look like "no data" rather than "misconfigured session" when debugging.
- RLS's guarantee is not unconditional: superuser and `BYPASSRLS` roles bypass it entirely — auditing which database roles are exempt, across every system component, is as important as the policy definition itself.

## Cheat Sheet

| Model | Isolation strength | Operational cost | When to choose |
|---|---|---|---|
| Silo | Highest (structural, infrastructure-level) | Highest (scales linearly with tenant count) | Compliance/contractual requirement, very large tenants |
| Pool + RLS | Strong logical isolation, database-enforced | Lowest (one shared schema/infrastructure) | Default for the common case |
| Pool, app-level filtering only | Weakest (depends on every query being correct) | Lowest | Avoid as the sole enforcement mechanism |
| Bridge/hybrid | Mixed, matched to tenant tier | Mixed | Customer base with genuinely different requirements per tier |

## Flashcards

**Q: What's the key weakness of application-level `tenant_id` filtering as the sole isolation mechanism?**
A: It requires every single query, in every code path, forever, to correctly apply the filter — one missed instance is a direct cross-tenant leak.

**Q: Does enabling Row-Level Security guarantee isolation unconditionally?**
A: No — database roles with superuser or `BYPASSRLS` status bypass RLS entirely; the guarantee depends on which role each code path actually uses to connect.

**Q: What does an RLS-protected query return when the tenant-context session variable is unset?**
A: Zero rows (fail-closed), not an error and not all rows — because `tenant_id = NULL` is never true in SQL's three-valued logic.

## Practice Exercises

1. Reproduce this chapter's RLS setup against your own local PostgreSQL instance, then add a third tenant and confirm the policy scopes correctly for all three without any application-code changes.
2. Create a second database role with `BYPASSRLS` explicitly granted (rather than superuser), and confirm it also bypasses the policy identically to the superuser demonstration in this chapter — this distinguishes the specific privilege responsible (`BYPASSRLS`) from superuser status more generally.

## Solutions

1. The policy's `USING (tenant_id = current_setting('app.tenant_id', true))` clause requires no changes for additional tenants — any value set into the session variable is matched identically, since the policy expression is data-driven, not tenant-count-specific.
2. `GRANT BYPASSRLS` (via `ALTER ROLE rolename BYPASSRLS`) on a non-superuser role produces the identical bypass behavior — confirming that `BYPASSRLS` specifically, not superuser status as a whole, is the responsible privilege, and that any role explicitly granted it (a common pattern for backup or migration tooling) carries the same isolation risk.

## Additional Reading

- [PostgreSQL Documentation — Row Security Policies](https://www.postgresql.org/docs/current/ddl-rowsecurity.html)

## Official References

- [PostgreSQL — Row Security Policies](https://www.postgresql.org/docs/current/ddl-rowsecurity.html)
