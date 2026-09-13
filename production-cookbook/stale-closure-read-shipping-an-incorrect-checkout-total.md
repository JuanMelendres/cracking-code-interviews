---
title: "Stale Closure Read Shipping an Incorrect Checkout Total"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md
source: syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md#production-scenarios
---

# Stale Closure Read Shipping an Incorrect Checkout Total

## Context

A checkout form tracks `subtotal`, `discount`, and `total` as three separate `useState` values. When a promo code is applied, the handler calls `setDiscount(newDiscount)` and, on the next line, `setTotal(subtotal - discount)`.

## Symptoms

The displayed total is correct-looking until the next re-render triggered by anything else finally recomputes it — an intermittent, timing-dependent bug that's hard to reproduce in manual QA.

## Impact

A user who applies a promo code and immediately screenshots or completes checkout in a fast flow can see a total that hasn't caught up to the actual discount applied.

## Initial Hypotheses

- A backend pricing-calculation bug — checked, the backend's own total calculation is correct; the discrepancy is purely client-side display.
- A race condition in the network request applying the promo code — checked, the promo code applies correctly and synchronously on the client with no network round-trip involved in this specific bug.
- `setTotal(subtotal - discount)` reads `discount` from the closure, which still holds the old value at that point in the same handler — correct.

## Evidence

Reproducing the exact sequence (apply promo, immediately read the displayed total before any unrelated re-render) shows the total using the pre-promo `discount` value, confirming the stale-closure read.

## Investigation Timeline

1. Intermittent reports of an incorrect total displayed briefly after applying a promo code.
2. Backend-bug and network-race hypotheses ruled out.
3. Reproduced the exact handler sequence, confirming `setTotal` reads `discount` from the same-render closure, before the state update takes effect.

## Root Cause

Within a single event handler, `setDiscount(newDiscount)` schedules an update but does not immediately change the `discount` variable in that handler's closure — the very next line still reads the old value, exactly the same-handler stale-read pattern common to any multi-`useState` derived-value calculation.

## Immediate Mitigation

None needed beyond the fix itself — the bug is deterministic once understood and doesn't require a separate stopgap.

## Permanent Fix

Consolidate `subtotal`, `discount`, and `total` into one `useReducer`, where a single `APPLY_PROMO` action computes all three consistently from the previous state in one step.

## Alternatives Considered

Reordering the `setDiscount`/`setTotal` calls, or computing `total` from a `useEffect` reacting to `discount` changes — both considered, but rejected as still leaving the underlying multi-`useState` derived-value pattern in place, which remains a standing risk for the next developer adding a fourth related value.

## Trade-offs

Consolidating into `useReducer` is a real, one-time refactor cost — accepted, since it removes the entire class of stale-closure-read bug for these three values permanently, rather than fixing one instance of it.

## Prevention

Treat any set of `useState` values that must be updated consistently together, derived from each other within the same handler, as a signal to consolidate into a single `useReducer` action from the start, rather than multiple independent `setState` calls in sequence.

## Monitoring and Alerts

- A test asserting the displayed total is correct immediately after a promo-code application, within the same synchronous handler execution, catching this exact class of stale-read regression.
- A code-review checklist flag for any handler calling multiple `setState` functions in sequence where a later call's computation depends on a value set by an earlier call in the same handler.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent bug.

- **Situation:** a checkout form intermittently displayed an incorrect total immediately after a promo code was applied, hard to reproduce in manual QA.
- **Task:** find the root cause of a timing-dependent display bug.
- **Action:** reproduced the exact handler sequence, confirming `setTotal` read `discount` from a stale, same-render closure.
- **Result:** consolidated the three related values into a single `useReducer` action, computing all three consistently in one step.

## Staff-Level Discussion

This bug mirrors a well-known stale-closure pattern applied specifically to derived, multi-`useState` calculations — the fix (consolidating into `useReducer`) doesn't just patch this one instance, it removes the entire class of bug for these three values permanently. The organizational lesson is recognizing any set of values that must update together, derived from each other, as a `useReducer`-shaped problem from the start, rather than discovering the stale-read pattern only after it ships as an intermittent, hard-to-reproduce production bug.

## Related Handbook Chapters

- [React useReducer and Custom Hooks](../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md) — the canonical consolidated-reducer pattern behind this incident's fix.
