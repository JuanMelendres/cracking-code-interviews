---
title: "Cheat Sheet: React Forms (Controlled vs. Uncontrolled, Validation, RHF/Zod)"
slug: react-forms
document_type: cheat-sheet
domain: frontend
topic_id: F-114
tier: Intermediate
canonical: ../handbook/frontend/react-forms.md
last_updated: 2026-09-03
---

# React Forms (Controlled vs. Uncontrolled, Validation, RHF/Zod)

**Canonical chapter:** [`handbook/frontend/react-forms.md`](../handbook/frontend/react-forms.md)

## Core Mental Model

A form input is "controlled" when React state is the single source of truth for its value (React sets `value`, and every keystroke must round-trip through `setState`) and "uncontrolled" when the DOM itself holds the value (React reads it only when asked, via a ref). This is the same reconciliation/DOM-node-identity distinction from the Fiber chapter, applied to inputs specifically: controlled inputs force a re-render on every change because React must actively reassert the `value` prop; uncontrolled inputs never re-render from typing. Validation timing and form libraries are downstream decisions built on top of this one foundational choice.

## Essential Definitions

- **Controlled input** — `value` bound to state, updated via `onChange`; state is the single source of truth.
- **Uncontrolled input** — DOM manages its own value; React reads it on demand via a `ref`.
- **Validation timing** (`onChange`/`onBlur`/`onSubmit`) — a UX decision about when the user sees an error, independent of the validation rule itself.
- **React Hook Form** — registers inputs largely uncontrolled internally (via `register(name)`'s ref), reading values from the DOM on validation/submit rather than storing every keystroke in state.
- **Zod** — defines validation rules as a single typed schema; one source of truth for both runtime validation and (in TS) inferred types.

## Decision Table

| Question | Answer |
|---|---|
| Does this field's value need to be read by something else on every keystroke (live validation, derived UI, char counter)? | Controlled (or RHF's `watch()` for that field) |
| Only need the value at submit time, no live dependency elsewhere? | Uncontrolled — cheaper, don't default to controlled out of habit |
| Form of real size or with validation rules worth centralizing? | `react-hook-form` + `zod` |
| Choosing validation timing | `onChange` for immediate-feedback-matters fields (password strength); `onBlur` as general default; `onSubmit`-only for zero interruption |

**Trade-offs:**

| Concern | Controlled (manual) | Uncontrolled (manual) | RHF + Zod |
|---|---|---|---|
| Re-renders per keystroke | One, every field | Zero | Effectively zero |
| Live validation-as-you-type | Natural fit | Requires manual ref reads (defeats the point) | Supported via `mode: 'onChange'`, opt-in |
| Type safety / single source of truth | None built-in | None built-in | Strong — schema drives validation + types |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- Controlled: 3 keystrokes moved a render counter from 2 to 8 (+6, one per keystroke, StrictMode-doubled).
- Uncontrolled: 3 keystrokes left the render counter unchanged; only an explicit "Read value" click (+2) moved it.
- Validation timing: identical min-3-char rule on input `"a"` — `onChange` showed the error instantly; `onBlur` only after losing focus; `onSubmit` only after submitting.
- RHF + Zod: a real failed submit produced 3 simultaneous zod error messages (`submitCount: 1`, `isSubmitSuccessful: false`); correcting all fields and resubmitting produced zero errors (`submitCount: 2`, `isSubmitSuccessful: true`).

## Common Pitfalls

- Defaulting every field to controlled `useState` regardless of whether the live value is needed anywhere, then being unable to explain the re-render cost.
- Validating `onChange` for every field by default, producing an aggressively naggy UX.
- Hand-rolling validation for a large form instead of a schema library, duplicating rules and type definitions.
- Reading an uncontrolled input's value on every keystroke via a manual DOM listener "to simulate live validation" — defeats the point of choosing uncontrolled at all.
- Wrapping a 2-3 field form in `react-hook-form` where the library's overhead exceeds the plain controlled-input alternative's cost.

## Interview Answer Skeleton

**30-sec:** Controlled inputs store their value in React state, re-rendering on every keystroke; uncontrolled inputs let the DOM hold the value, read via a ref only when needed, with zero re-renders from typing. Validation timing is a UX decision independent of the rule. `react-hook-form` + `zod` gets low re-render counts (internally uncontrolled) with a schema-driven API.

**2-min:** State the mental model, cite the real measured evidence (2→8 controlled vs. flat uncontrolled), cover the three validation-timing strategies with their real observed feedback moments, then close with RHF+Zod's real failed-then-successful submit sequence.

**Whiteboard:** Two boxes — "Controlled" with a loop "keystroke → onChange → setState → re-render → value reasserted"; "Uncontrolled" with a keystroke arrow straight into a DOM box with no loop back, plus a dashed "ref.current.value, read on demand" arrow to wherever it's needed.

**Senior-level framing:** Explains WHY controlled re-renders more (React must reassert the DOM's `value` prop, which requires a render), proposes a concrete verification method (a render counter, profiling), and picks validation timing per-field with stated UX reasoning rather than a blanket default.

## Production Warning Signs

- A large, fully controlled form (10+ fields) growing progressively sluggish as fields are added — check whether most fields actually need live reactivity, or were made controlled by default.
- A monotonically-growing checkout form where React DevTools Profiler shows the whole form re-rendering on every keystroke in any field.
- A schema swap (e.g., `z.coerce.number()` → a plain string check) that silently drops an intended numeric range validation while still "looking like" it validates the field.

## Related

- `handbook/frontend/react-reconciliation-and-fiber.md`
- `handbook/frontend/react-concurrent-rendering.md`
- `handbook/frontend/react-error-boundaries.md`
