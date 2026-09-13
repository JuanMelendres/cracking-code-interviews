---
title: "Discriminated Union Catching a Missing Handler Before Code Review"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-typescript.md
source: syllabus/21-frontend-web/react-typescript.md#production-scenarios
---

# Discriminated Union Catching a Missing Handler Before Code Review

## Context

A team's `Notification` component uses a discriminated union (`'info' | 'success' | 'action-required'`), with `'action-required'` uniquely requiring an `onAction` handler. A developer, copy-pasting an existing `'info'` usage to add a new `'action-required'` notification, forgets to add the required handler.

## Symptoms

Without type-level modeling, this would normally surface as a runtime bug — clicking a rendered-but-nonfunctional button, or a `TypeError` when an `undefined` handler is invoked — likely caught late, possibly in QA or production.

## Impact (as prevented)

Because the props are modeled as a discriminated union rather than a pile of optional fields, the build fails immediately, in the developer's own editor, naming `onAction` as missing specifically for the `'action-required'` variant.

## Initial Hypotheses

- This class of mistake would typically be caught in code review by a careful reviewer — considered, but unreliable; a copy-paste mistake like this is easy for a human reviewer to miss since the diff otherwise looks correct.
- Runtime testing (QA) would catch it before shipping — a real possibility, but later and more expensive than a compile-time signal.
- Modeling the variant-specific requirement at the type level would surface the mistake at the earliest possible point — correct, and the approach adopted.

## Evidence

The captured compiler output shows a `TS2322`/"Property is missing" error naming `onAction` specifically, at the exact line of the new `'action-required'` usage, before the change is even committed.

## Investigation Timeline

1. A developer copy-pastes an existing notification usage to add a new `'action-required'` variant.
2. The missing `onAction` handler is omitted in the copy-paste.
3. The build fails immediately in the editor, naming the exact missing prop for the exact variant.

## Root Cause

Modeling `'action-required'`'s requirement as a discriminated union (rather than a pile of optional fields) makes the compiler itself enforce the variant-specific requirement, rather than relying on a human noticing the gap.

## Immediate Mitigation

Not applicable — the type system caught the mistake before it could ship at all.

## Permanent Fix

Continue modeling any component prop set with variant-specific requirements as a discriminated union rather than a flat set of optional fields, so future variant additions get the same compile-time enforcement.

## Alternatives Considered

Modeling `onAction` as a single, always-optional prop with a runtime check inside the component — rejected, since it defers the catch to runtime (or, worse, silently no-ops) instead of surfacing it at compile time, before a human reviewer or a runtime user ever sees it.

## Trade-offs

Discriminated unions require slightly more upfront type-definition work than a flat optional-props interface — the cost is paid once, at the type definition; the payoff is every future misuse of that component being caught immediately, by the compiler.

## Prevention

Default to discriminated unions for any component whose valid prop combinations depend on a variant/kind field, rather than a flat interface with several optional fields whose validity depends on each other implicitly.

## Monitoring and Alerts

Not applicable in the runtime sense — the entire value of this pattern is that no runtime monitoring is needed for this specific class of mistake, since the compiler catches it before the code can run at all.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent near-miss.

- **Situation:** a copy-paste mistake omitted a required handler for a new notification variant.
- **Task:** understand why this didn't become a runtime bug the way an equivalent mistake would in a plain-JavaScript codebase.
- **Action:** traced the immediate compiler error to the discriminated-union modeling of the component's props.
- **Result:** the mistake was caught and fixed in the developer's own editor, before code review or any runtime test ever ran.

## Staff-Level Discussion

The lesson generalized: the cost of correctly modeling variant-specific requirements at the type level is paid once, at the type definition; the payoff is every future misuse of that component being caught immediately, by the compiler, before a human reviewer or a runtime user ever sees it. The organizational value of this pattern compounds specifically because it applies to every future developer touching the component, not just the one who wrote the original type definition.

## Related Handbook Chapters

- [React + TypeScript](../syllabus/21-frontend-web/react-typescript.md) — the canonical discriminated-union prop-modeling pattern behind this near-miss.
