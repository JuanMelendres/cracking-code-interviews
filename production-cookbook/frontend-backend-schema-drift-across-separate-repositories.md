---
title: "Frontend/Backend Schema Drift Across Separate Repositories"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-monorepo-layout.md
source: syllabus/21-frontend-web/nextjs-monorepo-layout.md#production-scenarios
---

# Frontend/Backend Schema Drift Across Separate Repositories

## Context

A frontend and backend team, in separate repos, each maintain their own hand-copied version of a shared TypeScript type or validation schema.

## Symptoms

A form's client-side validation and the API's own server-side validation silently diverge after an independent change on one side, discovered only in production.

## Impact

Valid submissions rejected, or invalid submissions accepted, depending on which side's validation drifted, discovered by real users rather than caught before shipping.

## Initial Hypotheses

- A communication/process failure between teams — the team's first assumption, examined directly against the actual repo structure.
- A one-off copy-paste mistake by an individual engineer — checked, the drift recurred across more than one change, indicating a structural cause rather than an isolated slip.
- The two repos have no shared, symlinked source of truth for the schema — each side maintains its own, hand-copied version — correct.

## Evidence

Comparing the two repos' schema definitions directly shows they are independently maintained copies with no shared source, confirmed to have drifted after an unremarked change on one side.

## Investigation Timeline

1. A validation mismatch between client and server is reported in production.
2. Communication-failure and one-off-mistake hypotheses considered and found insufficient given the recurrence.
3. Both repos' schema definitions compared directly, confirming no shared source of truth exists.

## Root Cause

Each side maintains its own, hand-copied version of what should be one shared schema, so any independent change on either side silently diverges from the other with no mechanism to catch it.

## Immediate Mitigation

Manually reconcile the two schema definitions to restore agreement for the specific drifted field.

## Permanent Fix

Introduce a shared workspace package that both sides import directly, so a change is either a single, atomic commit both sides build against, or a compile/type error immediately flags the drift.

## Alternatives Considered

Adding a cross-repo CI check that compares the two schema definitions for equality — a real, valid interim fix, but it treats the symptom (drift can occur) rather than the cause (two independent copies exist at all); a shared package removes the cause entirely.

## Trade-offs

A shared workspace package requires both repos to coordinate around a single published (or monorepo-internal) dependency version — a real, ongoing coordination cost, accepted in exchange for eliminating silent drift entirely.

## Prevention

Treat any type or schema genuinely shared between frontend and backend as belonging in one shared, imported source, never as two independently maintained copies, regardless of which repository structure (monorepo or polyrepo) is in use.

## Monitoring and Alerts

- A cross-repo CI check (as an interim measure) comparing schema definitions for equality, catching drift before it reaches production.
- Post-migration, a compile-time type error is the direct signal — no separate runtime monitoring is needed for the eliminated class of bug.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent migration.

- **Situation:** client-side and server-side validation silently diverged after an independent change, discovered only in production.
- **Task:** find the structural cause rather than treating it as a one-off mistake.
- **Action:** compared both repos' schema definitions directly, confirming each maintained its own independent copy with no shared source.
- **Result:** introduced a shared workspace package both sides import directly, converting future drift into an immediate compile/type error.

## Staff-Level Discussion

The real, structural fix is a shared source of truth both sides import directly — not a process or communication fix. The organizational lesson generalizes past this one schema: any concept genuinely shared between two codebases that are each allowed to maintain their own copy is a standing drift risk, and the durable fix is removing the duplication (a shared package), not adding coordination discipline to keep the copies in sync.

## Related Handbook Chapters

- [Next.js Monorepo Layout](../syllabus/21-frontend-web/nextjs-monorepo-layout.md) — the canonical shared-workspace-package pattern behind this incident's fix.
