---
title: "Context Split Misdiagnosed as the Fix for an Unmemoized Parent Re-Render"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md
source: syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md#production-scenarios
---

# Context Split Misdiagnosed as the Fix for an Unmemoized Parent Re-Render

## Context

A dashboard app has a single `AppStateContext` holding `{ user, searchQuery, sidebarCollapsed }`. Every keystroke in a search input updates `searchQuery`.

## Symptoms

The sidebar, reading only `sidebarCollapsed`, re-renders on every keystroke, causing a visible stutter.

## Impact

A day of ineffective refactoring effort spent on a fix that had no measurable effect, before the real cause was found.

## Initial Hypotheses

- Splitting `AppStateContext` into three separate contexts will fix the sidebar's unnecessary re-renders — the team's first fix attempt, tested directly rather than assumed correct.
- The sidebar's own render logic is expensive — checked, the component's render work itself is trivial; the issue is that it re-renders at all, not that each render is costly.
- `Sidebar` was never wrapped in `memo()`, so it re-renders because its parent (which owns all the app state) re-renders on every keystroke — a cause entirely unrelated to Context — correct.

## Evidence

Splitting `AppStateContext` into three separate contexts has no measurable effect on the sidebar's re-render count, profiled with React DevTools' Profiler, showing the Context split alone did not address the actual cause.

## Investigation Timeline

1. Sidebar stutter reported, correlated with search-box typing.
2. Context split attempted as the first fix, based on the assumption that Context was the cause.
3. Profiling after the split shows no measurable change in sidebar re-render frequency.
4. Further profiling with React DevTools' Profiler reveals the sidebar re-renders because its parent re-renders on every keystroke, with `Sidebar` never wrapped in `memo()`.

## Root Cause

The sidebar was re-rendering due to its parent's own re-renders (from owning all the app state), not from any Context subscription — the initial diagnosis blaming Context alone was incomplete.

## Immediate Mitigation

None separately needed — both required fixes are small, direct changes once correctly diagnosed.

## Permanent Fix

Apply both fixes together: split `sidebarCollapsed` into its own context, and wrap `Sidebar` in `memo()` — only the combination stopped unrelated keystrokes from causing sidebar re-renders.

## Alternatives Considered

Continuing to split every value in `AppStateContext` further, assuming finer-grained Context splitting would eventually fix it — rejected once profiling showed the actual cause was the missing `memo()`, not Context granularity at all.

## Trade-offs

None meaningful — both fixes (a narrower context, a `memo()` wrapper) are small, low-cost, complementary changes.

## Prevention

Profile with React DevTools' Profiler before committing to a specific fix hypothesis for a re-render performance complaint, rather than assuming the most commonly-cited cause (Context) without first confirming it against the actual component tree's re-render behavior.

## Monitoring and Alerts

- A standing practice of Profiler-based verification before and after any re-render-performance fix, regardless of how confident the initial diagnosis feels.
- Render-count regression tests for components previously identified as unnecessarily re-rendering, catching a recurrence even if a future refactor reintroduces the missing `memo()`.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent misdiagnosis.

- **Situation:** a sidebar re-rendered on every keystroke in an unrelated search box, and the team's first fix (splitting Context) had no measurable effect.
- **Task:** find the actual cause after a plausible-looking first fix failed to resolve it.
- **Action:** profiled with React DevTools' Profiler, discovering the sidebar was never wrapped in `memo()` and was re-rendering purely because its parent re-rendered.
- **Result:** applied both the Context split and the `memo()` wrapper together, finally eliminating the unrelated re-renders.

## Staff-Level Discussion

The team's initial diagnosis (blaming Context alone) cost a day of ineffective refactoring before profiling with React DevTools' Profiler revealed the real cause. The organizational lesson is that Context and re-render frequency are often conflated, but a component's re-render cause is frequently its parent's own re-render behavior, not its Context subscriptions at all — profiling directly, rather than pattern-matching to the most commonly-blamed cause, is what actually locates the real fix.

## Related Handbook Chapters

- [React useMemo, useCallback, and useContext](../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md) — the canonical `memo()`-plus-Context-granularity distinction behind this incident's fix.
