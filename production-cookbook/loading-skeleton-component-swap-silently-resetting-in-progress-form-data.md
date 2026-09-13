---
title: "Loading-Skeleton Component Swap Silently Resetting In-Progress Form Data"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-reconciliation-and-fiber.md
source: syllabus/21-frontend-web/react-reconciliation-and-fiber.md#production-scenarios
---

# Loading-Skeleton Component Swap Silently Resetting In-Progress Form Data

## Context

A multi-step form shows a loading skeleton (`<SkeletonForm />`) while fetching initial data, then swaps to the real form (`<RealForm />`) once data arrives — both conditionally rendered at the same JSX position based on a `loading` flag.

## Symptoms

A user who starts typing into `RealForm` before an unrelated background refetch flips `loading` back to `true` briefly (to show the skeleton again) loses everything they'd typed.

## Impact

A real, user-facing data-loss bug triggered by an unrelated background refetch the user never initiated or was aware of.

## Initial Hypotheses

- A bug in the form's own state management (losing values on some internal update) — checked, the form's own local state logic has no bug; the component itself is being destroyed and recreated entirely.
- An unrelated network error clearing form state as a side effect — checked, no such error-handling code path exists.
- `RealForm` and `SkeletonForm` are different component types at the same JSX position, so React destroys `RealForm`'s entire subtree when `loading` flips, then builds a brand-new instance when it flips back — correct.

## Evidence

Reproducing the exact sequence (start typing, trigger a background refetch that flips `loading` briefly) shows the form's DOM nodes and their values are fully replaced, not merely re-rendered, confirming a full unmount/remount rather than an update.

## Investigation Timeline

1. Users report losing in-progress form input during editing, correlated with unrelated background activity.
2. Form-state-bug and network-error hypotheses ruled out.
3. Reproduced the exact sequence, confirming a full component unmount/remount at the `loading`-flag flip, not merely a re-render.

## Root Cause

`RealForm` and `SkeletonForm` are different component types conditionally rendered at the same JSX position; React's reconciliation model destroys the entire subtree of a type it no longer matches at a given position, with no memory of prior state when the type is later restored.

## Immediate Mitigation

Disable the background refetch's ability to flip `loading` back to `true` once the form has already loaded once, as a quick stopgap.

## Permanent Fix

Avoid the type swap entirely — keep `RealForm` mounted, and control its visibility with CSS or a prop (e.g., `<RealForm hidden={loading} />`) — or lift the form state above the conditional so it survives any remount.

## Alternatives Considered

Persisting form state to a ref or external store so it survives a remount — a real, valid alternative, but more complex than simply avoiding the type swap that causes the remount in the first place.

## Trade-offs

Keeping `RealForm` always mounted (visibility-controlled rather than conditionally rendered) means its initial data-fetching effects may need to be guarded against re-running unnecessarily — a small, manageable cost against eliminating the data-loss risk entirely.

## Prevention

Treat any conditional rendering that swaps between two different component types at the same position as a standing review flag whenever either type can hold meaningful local state that a background event might need to be robust against losing.

## Monitoring and Alerts

- A regression test simulating the exact sequence (type into the form, then trigger the background condition that flips the loading state) as a standing test case for this and any similarly-structured conditional UI.
- Client-side error/data-loss reporting specifically instrumented around multi-step forms, to catch this class of loss even where it isn't reliably reproduced by a user's own bug report.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent bug.

- **Situation:** users occasionally lost in-progress form input, apparently triggered by unrelated background activity.
- **Task:** find why an unrelated refetch caused visible data loss in a form that had nothing to do with it.
- **Action:** reproduced the exact sequence and traced it to React's reconciliation destroying and rebuilding the form component when its conditionally-rendered type swapped.
- **Result:** kept the real form always mounted, controlling its visibility instead of conditionally rendering a different component type, eliminating the data-loss risk.

## Staff-Level Discussion

The diagnosis, directly traceable to React's reconciliation model: two different component types at the same JSX position are never diffed against each other — the old subtree is destroyed and a new one built, with no memory of what was there before. The organizational lesson is that any conditional-rendering pattern swapping component types (not just prop values) at a shared position is a standing risk for any state that type might hold, and the safest default is controlling visibility of one persistent component rather than swapping between two.

## Related Handbook Chapters

- [React Reconciliation and Fiber](../syllabus/21-frontend-web/react-reconciliation-and-fiber.md) — the canonical type-identity reconciliation mechanics behind this incident's fix.
