---
title: "Stale Route Table Producing Blank Pages on Orphaned Legacy Routes"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-fundamentals.md
source: syllabus/21-frontend-web/nextjs-fundamentals.md#production-scenarios
---

# Stale Route Table Producing Blank Pages on Orphaned Legacy Routes

## Context

A team's existing Create React App + React Router app has a manually maintained `<Routes>` configuration file, separate from the actual page components.

## Symptoms

Several page component files were deleted or renamed during refactors, but the corresponding `<Route>` entries were forgotten and left pointing at stale imports.

## Impact

Some of these were caught only by a build error; others produced a runtime blank page for real users landing on an orphaned route still linked to from elsewhere.

## Initial Hypotheses

- A one-off developer mistake on a single route — checked, the pattern recurred across multiple refactors over time, not a single isolated incident.
- A build-tooling bug failing to catch stale imports — checked, the build did catch some cases; others simply weren't caught because the route table isn't validated against real usage at runtime.
- The route table is a second source of truth that can drift from the actual page files — correct.

## Evidence

Auditing the route configuration against the actual page component directory shows multiple `<Route>` entries pointing at files that no longer exist or were renamed.

## Investigation Timeline

1. Users report blank pages on specific, still-linked-to routes.
2. Developer-mistake and build-tooling-bug hypotheses considered and found insufficient to explain the recurring pattern.
3. Route table audited against the real page-file directory, confirming systemic drift.

## Root Cause

A manually maintained route table is a second source of truth that can drift from the actual page files whenever a refactor changes the latter without updating the former.

## Immediate Mitigation

Manually audit and correct the current route table against the real page-file directory.

## Permanent Fix

Migrate to Next.js's file-based routing (the App Router), converting each page into a file at the corresponding `app/` path with no separate route configuration to keep in sync — the file's existence and location is the route registration.

## Alternatives Considered

Adding a CI check that validates the existing route table against the actual page-file directory — a real, valid interim fix, but it treats the symptom (drift can occur) rather than the cause (a second source of truth exists at all); the file-based-routing migration removes the cause entirely.

## Trade-offs

The migration itself is a real, one-time engineering cost — accepted, since it removes an entire class of configuration-drift bug permanently rather than requiring ongoing vigilance.

## Prevention

Prefer file-based routing over a manually maintained route table for any new project, specifically because it removes an entire class of drift bug by removing the second source of truth rather than requiring discipline to keep it in sync.

## Monitoring and Alerts

- A synthetic link-checker crawling all internally-linked routes on a schedule, catching an orphaned route before a real user does.
- (Post-migration) no ongoing route-table-specific monitoring is needed, since the class of bug is structurally eliminated.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent migration.

- **Situation:** a manually maintained route table repeatedly drifted from the actual page files across several refactors, producing real blank pages for users on orphaned routes.
- **Task:** find a fix that addresses the recurring pattern, not just the current instance of drift.
- **Action:** audited and corrected the current drift, then migrated to file-based routing during a broader Next.js migration.
- **Result:** eliminated the second source of truth entirely — a route's existence and location is now the route registration itself.

## Staff-Level Discussion

This is a concrete, not just theoretical, argument for file-based routing's actual engineering value beyond "less code to write": it removes an entire class of configuration-drift bug by removing the configuration. The organizational lesson generalizes past routing — any manually maintained configuration that duplicates information already derivable from the codebase's actual structure is a standing drift risk, and the durable fix is usually removing the duplicate source of truth, not adding process discipline to keep it synchronized.

## Related Handbook Chapters

- [Next.js Fundamentals](../syllabus/21-frontend-web/nextjs-fundamentals.md) — the canonical file-based-routing argument behind this incident's fix.
