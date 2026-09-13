---
title: "One Broken Widget Blanking an Entire Dashboard With No Error Boundaries"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-error-boundaries.md
source: syllabus/21-frontend-web/react-error-boundaries.md#production-scenarios
---

# One Broken Widget Blanking an Entire Dashboard With No Error Boundaries

## Context

A dashboard renders several independent widgets (a chart, an activity feed, a third-party embedded map) inside a single top-level layout with no error boundaries anywhere.

## Symptoms

A malformed API response causes the map widget to throw while rendering (accessing a property on `undefined`); the entire dashboard goes blank.

## Impact

The chart and activity feed, which had nothing wrong with them, disappear too, because with no boundary anywhere, React unmounts the whole tree.

## Initial Hypotheses

- A global, dashboard-wide data-loading failure — checked, the chart and activity feed's own data sources returned successfully.
- A layout-level rendering bug — checked, the layout component itself has no logic that could throw.
- One widget (the map) threw during render with no error boundary anywhere in the tree to contain it — correct.

## Evidence

Tracing the error shows it originates specifically inside the map widget's render logic, accessing a property on `undefined` from a malformed API response — with no boundary present anywhere between it and the tree root.

## Investigation Timeline

1. The entire dashboard is reported as blank following a malformed API response affecting only one widget.
2. Global-data-failure and layout-bug hypotheses ruled out via independent checks of the other widgets' data sources.
3. The error traced to the map widget specifically, with no boundary anywhere in the ancestor chain.

## Root Cause

With zero error boundaries, the "nearest ancestor" boundary search finds nothing, and React unmounts the entire tree in response to any single component's render-time error.

## Immediate Mitigation

Manually patch the specific malformed-API-response case in the map widget to stop the immediate crashes while boundaries are added.

## Permanent Fix

Wrap each independent widget in its own boundary, so a single widget's failure is contained to that widget's fallback, leaving the rest of the dashboard fully functional.

## Alternatives Considered

A single top-level boundary wrapping the whole dashboard — rejected as having the exact same all-or-nothing blast radius as having no boundary at all; it would just show one general fallback instead of unmounting, which is barely better for the user.

## Trade-offs

Per-widget boundaries require slightly more boilerplate (one boundary per independent section) than a single top-level boundary — accepted, since the blast-radius reduction is the entire point of the fix.

## Prevention

Default to wrapping every independent, potentially-failing section of a multi-widget page in its own error boundary from the start, treating "no boundary anywhere" as a standing design-review flag for any dashboard-shaped page.

## Monitoring and Alerts

- Per-widget error-boundary fallback triggers logged and alerted individually, so a single widget's recurring failures are visible without requiring the whole dashboard to go down first.
- A design-review checklist item requiring explicit error-boundary placement for any new multi-widget page before it ships.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a single third-party widget's malformed data caused an entire dashboard to go blank, taking down unrelated, healthy widgets with it.
- **Task:** find why one widget's failure had such a broad blast radius.
- **Action:** traced the error to the specific widget, confirmed no error boundary existed anywhere in the tree, and wrapped each widget in its own independent boundary.
- **Result:** a future failure in any one widget now only affects that widget's own fallback, leaving the rest of the dashboard fully functional.

## Staff-Level Discussion

A single top-level boundary would have the exact same all-or-nothing blast radius as having no boundary at all — the real fix is granularity matched to the actual independent failure units on the page, not merely the presence of a boundary somewhere. The organizational lesson is that error-boundary placement is itself a design decision with real blast-radius consequences, not a checkbox item satisfied by having "some" boundary anywhere in the tree.

## Related Handbook Chapters

- [React Error Boundaries](../syllabus/21-frontend-web/react-error-boundaries.md) — the canonical per-widget boundary-granularity pattern behind this incident's fix.
