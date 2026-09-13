---
title: "Server Action Mutating Data Despite a Failed Authorization Check"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md
source: syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md#production-scenarios
---

# Server Action Mutating Data Despite a Failed Authorization Check

## Context

A user submits a form edit via a Server Action with an invalid or missing session.

## Symptoms

A user reports seeing a "please sign in" redirect when submitting a form, but the record they were editing shows their edit applied regardless — a support ticket surfaces the discrepancy.

## Impact

A write is applied despite an apparent authorization failure — a real, unauthorized-mutation-shaped correctness and security incident, not just a confusing UX.

## Initial Hypotheses

- A caching bug, with the client seeing a stale, pre-edit page — checked directly against the underlying data store, not the app's own (possibly cached) read path, and the write genuinely went through.
- A UI-only glitch showing an incorrect redirect while the mutation correctly failed — checked, the redirect is real but the mutation succeeded anyway.
- The Server Action's own authorization check is missing or failing open, letting the write execute before a separate, later re-render step redirects the user — correct.

## Evidence

A raw request replicating the failing POST, sent with an invalid/missing session, produces the same redirect the user saw — but the underlying data store, checked directly, shows the write went through.

## Investigation Timeline

1. Support ticket reports a rejected-looking form submission whose edit nonetheless applied.
2. Caching-bug and UI-only-glitch hypotheses ruled out via direct data-store inspection.
3. A raw, deliberately-unauthorized request replicated against the Server Action, confirming the write succeeds despite the redirect.

## Root Cause

The Server Action's own authorization check is missing or is failing open (proceeding despite a failed check, e.g., a caught exception that doesn't return early) — the write executes, and only a separate, later re-render step (a page-level redirect) produces the response the user actually sees, creating the illusion of a clean rejection.

## Immediate Mitigation

Disable the affected Server Action (or add an emergency early-return guard) immediately to stop further unauthorized writes while the real fix is prepared.

## Permanent Fix

Audit every Server Action for an explicit, early-return authorization check before any write, matching the pattern already correctly present in other actions in the same codebase.

## Alternatives Considered

Relying on the page-level redirect alone as the authorization boundary — rejected, since it's a separate, later step from the mutation itself and provides no actual guarantee the write was gated; the redirect and the write must be tied to the same authorization check, not two independent code paths that happen to usually agree.

## Trade-offs

None meaningful — an explicit early-return auth check is standard, low-cost practice, and its absence here was the actual defect, not a deliberate trade-off.

## Prevention

Treat "does this Server Action have an explicit, early-return authorization check before any write" as a mandatory code-review item for every Server Action, not an assumption verified once and never re-checked as the codebase grows.

## Monitoring and Alerts

- An audit log recording every Server Action write alongside its authorization-check outcome, making a fail-open write directly visible rather than only discoverable via user report.
- A recurring, automated security test replicating this exact attack pattern (a raw, unauthorized request against every Server Action) as a standing regression check.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent security incident.

- **Situation:** a form submission appeared to be rejected for an unauthenticated user, but the underlying data showed the edit had actually applied.
- **Task:** determine the real state of the system rather than trusting the client-visible outcome.
- **Action:** checked the underlying data store directly and replicated the failing request raw, confirming the Server Action's authorization check was failing open.
- **Result:** added an explicit, early-return authorization check, and extended the same audit to every other Server Action in the codebase.

## Staff-Level Discussion

The illusion of a clean rejection — a redirect the user actually saw — masked a real write that had already happened, because the authorization check and the visible response were two separate, decoupled steps. The organizational lesson is that a fail-open authorization check is one of the most dangerous classes of bug precisely because the user-visible symptom (a redirect) looks exactly like correct behavior; verifying against the underlying data store directly, not just the app's own response, is what actually surfaces it.

## Related Handbook Chapters

- [Next.js Server Actions and Mutations](../syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md) — the canonical early-return authorization-check pattern behind this incident's fix.
