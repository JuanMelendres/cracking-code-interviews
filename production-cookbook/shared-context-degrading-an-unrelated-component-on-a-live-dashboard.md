---
title: "Shared Context Degrading an Unrelated Component on a Live Dashboard"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-state-management.md
source: syllabus/21-frontend-web/react-state-management.md#production-scenarios
---

# Shared Context Degrading an Unrelated Component on a Live Dashboard

## Context

A live trading/analytics dashboard bundles several independently-updating values — a live price ticker, a user's watchlist, a UI theme toggle — into one Context for convenience.

## Symptoms

As the price ticker starts updating multiple times per second, the entire dashboard — including completely unrelated components that only read the watchlist or the theme — starts re-rendering multiple times per second too.

## Impact

Real, ongoing wasted re-render cost across the whole dashboard, driven by a data source most components never actually read.

## Initial Hypotheses

- React is fundamentally slow for real-time data at this update frequency — the team's initial framing, disproven by measurement.
- The watchlist component's own logic has a performance bug — checked, its own render logic is simple and inexpensive.
- Every consumer of the shared Context subscribes to all of it, so any single value's update re-renders every consumer regardless of which value it actually reads — correct.

## Evidence

A render counter (or React DevTools Profiler) on the watchlist component shows it re-rendering in lockstep with the price ticker despite never reading price data.

## Investigation Timeline

1. Dashboard performance complaints correlate with the price ticker's update frequency increasing.
2. React-is-slow and watchlist-bug hypotheses ruled out via direct measurement.
3. A render counter on the watchlist confirms it re-renders on every price-ticker update despite reading only the watchlist portion of the shared Context.

## Root Cause

The shared Context, not React itself, is the cause — every consumer of one Context subscribes to all of it, so bundling independently-updating values into one Context couples every consumer's render frequency to the fastest-updating value, regardless of what each consumer actually reads.

## Immediate Mitigation

None separately needed — the permanent fix is a scoped, targeted change once the actual cause is identified.

## Permanent Fix

Split the price ticker into its own store with per-selector granularity (a separate, narrower Context, a library like Zustand, or a ref-driven imperative update if the ticker doesn't even need to trigger re-renders for most consumers), leaving the watchlist and theme on whatever they were using, completely unaffected.

## Alternatives Considered

Memoizing every consumer component individually to reduce the cost of each unnecessary re-render — rejected as treating the symptom; the components would still re-render unnecessarily on every price update, just cheaply, rather than not re-rendering at all.

## Trade-offs

None meaningful — reducing the subscription scope with a tool that has per-selector granularity is a small, targeted change, not a full rewrite; the choice of state-management tool for a specific piece of state can and should be made independently per state slice, not once for an entire app.

## Prevention

Evaluate update frequency per value before bundling multiple values into one shared Context, and default to splitting any value with a meaningfully different update frequency into its own subscription scope from the start.

## Monitoring and Alerts

- Render-count-per-component tracking correlated against specific state-update sources, so a high-frequency value's blast radius across unrelated components is visible before it becomes a user-facing complaint.
- A design-review checklist item requiring update-frequency analysis before combining multiple values into one shared Context.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent optimization.

- **Situation:** an entire live dashboard began re-rendering multiple times per second once a price ticker's update frequency increased, including components that never read price data at all.
- **Task:** find the actual cause rather than assuming React itself couldn't handle the update frequency.
- **Action:** measured render counts directly on an unrelated component, confirming it re-rendered in lockstep with the price ticker purely because of the shared Context.
- **Result:** split the price ticker into its own narrowly-scoped store, leaving unrelated components completely unaffected by its update frequency.

## Staff-Level Discussion

The fix is a small, targeted change — reduce the subscription scope using a tool with per-selector granularity — not a full rewrite; the choice of state-management tool for a specific piece of state is a decision that can and should be made independently per state slice, not once for an entire app. The organizational lesson is that bundling convenience (one Context for several values) has a real, easy-to-overlook coupling cost: every consumer's render frequency becomes tied to the fastest-updating value in the bundle, regardless of what that specific consumer actually needs.

## Related Handbook Chapters

- [React State Management](../syllabus/21-frontend-web/react-state-management.md) — the canonical subscription-scope-splitting pattern behind this incident's fix.
