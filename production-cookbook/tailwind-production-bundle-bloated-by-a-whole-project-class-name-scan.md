---
title: "Tailwind Production Bundle Bloated by a Whole-Project Class Name Scan"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-styling-approaches.md
source: syllabus/21-frontend-web/nextjs-styling-approaches.md#production-scenarios
---

# Tailwind Production Bundle Bloated by a Whole-Project Class Name Scan

## Context

A team's Tailwind production CSS bundle is larger than expected.

## Symptoms

Grepping the built CSS for utility classes turns up several that no developer remembers intentionally using.

## Impact

Unnecessary CSS bloat shipped to every visitor, from utility classes that no component actually uses.

## Initial Hypotheses

- A bug in Tailwind's purge/scanning configuration — the team's initial assumption, tested directly rather than accepted.
- A stray, unused component still present in the source tree referencing the classes — checked, no such component exists.
- Tailwind's default scan is project-wide by design, picking up class-like strings outside of actual component code — correct.

## Evidence

Searching the whole project — not just `className` usages, and not just source code, but comments, strings, and critically, README/markdown files — for one of the surprising class names finds it sitting in a piece of documentation prose, not application code at all.

## Investigation Timeline

1. Unexpectedly large production CSS bundle noticed via a routine bundle-size check.
2. Purge-configuration-bug and stray-unused-component hypotheses ruled out.
3. A whole-project (not just source-code) search for a surprising class name locates it inside documentation markdown, not application code.

## Root Cause

Tailwind's default scan is project-wide by design, not scoped to source directories — not a bug, the documented real mechanism — so any occurrence of a class-like string anywhere in the project, including documentation, is picked up.

## Immediate Mitigation

Remove the offending documentation reference immediately to confirm the diagnosis and shrink the bundle, watching the class disappear from the build.

## Permanent Fix

Apply the documented `source(none)` plus explicit `@source` pattern, narrowing the scan to an intentional allowlist rather than relying on the broad, whole-project default.

## Alternatives Considered

Manually auditing and removing every unintentionally-matched string across the project on an ongoing basis — rejected as unsustainable; the scan's default scope would continue picking up new false matches indefinitely without a structural fix to the scan's boundaries.

## Trade-offs

Explicitly scoping `@source` requires the team to maintain that allowlist as new source directories are added — a real, small ongoing cost, accepted in exchange for a bundle that only contains intentionally-used utility classes.

## Prevention

Configure `source(none)` plus explicit `@source` entries at project setup time for any Tailwind project of meaningful size, rather than relying on the broad, whole-project default and discovering unintended matches only via a bundle-size investigation.

## Monitoring and Alerts

- A CSS bundle-size regression check in CI, flagging unexpected growth for investigation before it ships.
- A periodic grep of the built CSS against the project's actual `className` usages, catching class names present in the bundle but absent from any real component.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent investigation.

- **Situation:** a Tailwind production bundle was larger than expected, with several utility classes nobody remembered using.
- **Task:** find the source of the unexpected classes without assuming a tooling bug.
- **Action:** searched the whole project, not just source code, for one of the surprising class names, finding it in documentation prose rather than application code.
- **Result:** confirmed Tailwind's project-wide default scan as the (correctly-documented) mechanism, and applied an explicit `@source` allowlist to narrow it.

## Staff-Level Discussion

Tailwind's default scan being project-wide by design, not scoped to source directories, is not a bug — but assuming a scan is implicitly limited to "real code" is an easy, costly assumption to make. The organizational lesson is verifying a build tool's actual documented scope explicitly, rather than assuming it matches an intuitive but unverified mental model, especially for any tool whose default behavior scans broadly across a whole project.

## Related Handbook Chapters

- [Next.js Styling Approaches](../syllabus/21-frontend-web/nextjs-styling-approaches.md) — the canonical `@source` scan-scoping pattern behind this incident's fix.
