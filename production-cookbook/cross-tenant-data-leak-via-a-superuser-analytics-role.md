---
title: "Cross-Tenant Data Leak via a Superuser Analytics Role"
document_type: production-cookbook-entry
domain: security
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/security/multi-tenancy-isolation-models.md
source: handbook/security/multi-tenancy-isolation-models.md#production-scenarios
---

# Cross-Tenant Data Leak via a Superuser Analytics Role

## Context

A SaaS application enforces tenant isolation via row-level security (RLS) policies on its shared database. A data-engineering team runs ad hoc batch analytics jobs against the same database, connecting with direct database access outside the main application.

## Symptoms

An incident review finds a batch analytics job, run by the data-engineering team, connected using a superuser-equivalent role and inadvertently included cross-tenant data in an exported report.

## Impact

A cross-tenant data exposure — customer A's data appearing in an export intended only for customer B's reporting — despite the application's RLS policy being correctly configured.

## Initial Hypotheses

- The RLS policy itself is broken or misconfigured — checked and ruled out; the application's own request-serving code correctly enforced RLS-based isolation throughout the incident window.
- An application-level authorization bug in the reporting feature — checked and ruled out; the exported report was generated entirely outside the application's request path.
- An out-of-band access path exempt from the RLS policy — correct.

## Evidence

The analytics job's database connection used a role with `BYPASSRLS` (or superuser-equivalent) privileges — a role that, by definition, is not subject to the RLS policy that correctly isolates every request going through the application itself.

## Investigation Timeline

1. **Cross-tenant data found in an exported report**, triggering an incident review.
2. **Application-level RLS enforcement checked first**, as the natural initial suspect, and confirmed correctly configured and functioning for every request-path query during the window.
3. **Export's origin traced**, finding it was produced by a batch analytics job rather than any application code path.
4. **Analytics job's database role inspected**, revealing `BYPASSRLS`/superuser-equivalent privileges — a role structurally exempt from the isolation policy.

## Root Cause

The application's RLS policy was never broken. The incident originated entirely from an out-of-band access path — the analytics job — that used a database role exempt from the policy, because RLS enforcement applies per-role, and the role in question was granted a privilege that bypasses it.

## Immediate Mitigation

Revoke the analytics job's superuser-equivalent role immediately, and manually review and redact the affected export.

## Permanent Fix

Audit every database connection across the entire organization — not just the primary application — for `BYPASSRLS` or superuser privileges, and provision a dedicated, RLS-subject role for any tool that needs database access outside the main application.

## Alternatives Considered

Fixing or hardening the RLS policy itself. Rejected as addressing the wrong layer — the policy was never broken; the gap was a role exempt from it entirely, which no policy change can close.

## Trade-offs

A dedicated, RLS-subject analytics role may need read access patterns the RLS policy doesn't naturally support for cross-tenant aggregate reporting (e.g., "total revenue across all tenants"), requiring an explicit, audited exception mechanism rather than a blanket bypass. Accepted, since the alternative is exactly the exposure this incident demonstrates.

## Prevention

Any database role with `BYPASSRLS` or superuser-equivalent privileges should require explicit, documented justification and periodic re-review, treated as a standing security risk rather than a convenience default for internal tooling.

## Monitoring and Alerts

- A recurring, automated audit of all database roles for `BYPASSRLS` or superuser-equivalent privileges, alerting on any role holding one without a currently valid, documented justification — this is the direct structural fix, run continuously rather than discovered only during an incident review.
- Query-level auditing on any bypass-capable role, logging every query it executes, so a future misuse is visible in an audit trail rather than only discoverable after data has already left the system in an export.

## Interview Story

This maps to a "how do you ensure tenant isolation actually holds, not just in the application" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a cross-tenant data exposure was found in an analytics export, despite the application's own RLS-based isolation being correctly implemented.
- **Task:** determine whether the isolation policy itself failed or something else caused the leak.
- **Action:** verify the application-level RLS enforcement first, confirming it held throughout; trace the export's origin outside the application entirely; find the specific bypass-capable database role used by the out-of-band analytics job.
- **Result:** revoked the bypass role, and started a standing audit of every database role across the organization for bypass privileges, not just the application's own connection.

## Staff-Level Discussion

The most important insight in this incident is that tenant isolation is a property of every access path to the data, not just the application's own request-handling code — and RLS enforcement is inherently role-scoped, so any tool, script, or team with a bypass-capable role sits entirely outside the isolation guarantee regardless of how well the application itself is built. This is a common blind spot: security review naturally focuses on the application's own code paths, because that's what's reviewed and tested, while ad hoc internal tooling with direct database access often isn't held to the same standard. A Staff engineer's contribution is recognizing that a security guarantee stated as "the application enforces X" is incomplete — the actual guarantee needs to be "every access path to this data enforces X," which requires auditing infrastructure and tooling access, not just application code.

## Related Handbook Chapters

- [Multi-Tenancy Isolation Models](../handbook/security/multi-tenancy-isolation-models.md) — canonical RLS and shared-pool isolation mechanics used here.
