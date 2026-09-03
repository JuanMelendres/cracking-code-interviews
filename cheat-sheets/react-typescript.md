---
title: "Cheat Sheet: TypeScript with React (Generics, Discriminated Unions, Exhaustiveness)"
slug: react-typescript
document_type: cheat-sheet
domain: frontend
topic_id: F-119
tier: Advanced
canonical: ../handbook/frontend/react-typescript.md
last_updated: 2026-09-03
---

# TypeScript with React (Generics, Discriminated Unions, Exhaustiveness)

**Canonical chapter:** [`syllabus/21-frontend-web/react-typescript.md`](../syllabus/21-frontend-web/react-typescript.md)

## Core Mental Model

Every technique here turns a class of bug that would otherwise surface at runtime (or not at all) into a compile-time error instead — the earliest, cheapest point to catch it. Typed props catch a wrong-shape object before it ever renders; a correctly parameterized generic component catches a type mismatch at the specific call site that introduced it; a discriminated union catches "this variant is missing a prop it specifically requires" as a property-level error, not a generic warning; exhaustiveness checking catches "a new case was added but nobody updated this switch" the moment the union changes. None of this changes runtime behavior — it moves discovery earlier and makes it unmissable.

## Essential Definitions

- **Typed props/state/hooks** — `interface`/`type` shape for props, `useState<T>`, `useReducer<S, A>`; checks every USE of a component, not just its definition.
- **Generic component** — parameterizes props by a type variable `T` (a `List<T>`), reused across many concrete types while the compiler enforces internal consistency at each usage.
- **Discriminated union for variant props** — a union of object types sharing one literal-typed discriminant field, modeling "required props differ by mode" as a real, checked constraint — something a pile of optional fields cannot express.
- **Exhaustiveness checking** — a `default: return assertNever(action)` branch; after every named case is handled, the remaining value's static type narrows to `never`, so a newly added unhandled union member is a real compile-time error.

## Decision Table

| Question | Answer |
|---|---|
| Do a component's required props genuinely change based on a mode/variant field? | Discriminated union, not a pile of optional fields |
| Would this component's logic be identical across several data shapes, differing only by item type? | Make it generic (`<T>`) rather than duplicating or widening to `any` |
| Does a switch/if-else need to handle every union member, wanting new members to force a review? | Add a `never`-typed exhaustiveness guard in `default` |
| Reaching for `any` because a type is "too complicated right now"? | Treat as a tracked shortcut, not a permanent state — every `any` is a hole in every other guarantee |

**Failure mode comparison:**

| Concern | Optional-field bag | Discriminated union | Generic (`<T>`) | `any` |
|---|---|---|---|---|
| Catches variant-specific missing props | No | Yes | N/A | No |
| Failure mode when misused | Runtime, often late | Compile-time, precisely scoped | Compile-time, precisely scoped | Runtime or silently wrong |

## Key Numbers (real, verified against a running React 19.2.8 + Vite + TypeScript app, via `tsc -b`)

- Discriminated union: omitting `onRetry` from an `'error'`-variant `<Alert>` usage produced a real `Property 'onRetry' is missing in type ... but required in type ...` error.
- Generic component: `List<T>` used with `T` inferred as `Task` and separately as `number` in the same app, no duplication, no `any`; deliberately mistyping `renderItem`'s parameter produced a real two-part cross-prop compiler error.
- Exhaustiveness: adding a `{ type: 'double' }` member to `CounterAction` with no matching `case` produced a real `Argument of type '{ type: "double"; }' is not assignable to parameter of type 'never'` error.

## Common Pitfalls

- Modeling variant-specific requirements as optional fields (`onRetry?: () => void`) instead of a discriminated union — compiles cleanly for every variant, silently discarding the compile-time guarantee.
- Reaching for `any` (or unnarrowed `unknown`) to "get past" a generic-component typing difficulty rather than working out the correct shared type parameter.
- Writing an exhaustive-looking `switch` with no `never`-typed `default` guard — compiles today, gives zero signal when a teammate adds a new union member later.
- Using `as` type assertions to silence a generic-component inference error rather than fixing the actual mismatch — hides the bug from the compiler instead of removing it.

## Interview Answer Skeleton

**30-sec:** Typed props/state catch shape mismatches early; generic components (`List<T>`) reuse logic across types while still checking cross-prop consistency at each call site; discriminated unions model variant-specific required props as a real, checked constraint; exhaustiveness checking (`never`) turns "a switch wasn't updated for a new union member" into a build failure instead of a silent runtime gap.

**2-min:** Cite the real evidence: a discriminated union made an `'error'` variant missing `onRetry` a precisely-scoped compiler error, not a generic warning. A generic `List<T>`, reused with `T` inferred as both `Task` and `number`, caught a cross-prop type mismatch at the exact call site that introduced it. An exhaustiveness-checked `useReducer` caught a newly added `'double'` action variant with no matching case as a real `never`-assignment error. Contrast with `any`, which provides none of these guarantees.

**Whiteboard:** A union type as three boxes — "info," "success," "error" — each listing its own required fields, "error" uniquely including `onRetry`. An arrow from a JSX usage missing `onRetry` pointing INTO the "error" box specifically, landing on a red X labeled with the real captured error text. Beside it: a generic `List<T>` box with three prop arrows (`items`, `renderItem`, `keyExtractor`) all converging on one shared "T" label.

**Senior-level framing:** Explains discriminated unions and exhaustiveness checking as compile-time bug-prevention mechanisms with a concrete example of what they catch, not just "TypeScript is safer."

## Production Warning Signs

- A new variant of a discriminated-union-typed component ships without its required handler — the compiler should catch this immediately, in-editor, before code review; if it doesn't, the union likely wasn't modeled correctly (check for an optional field standing in for a variant-specific requirement).
- A generic component's error message blames one prop for a type "owned" by a different prop — look for whichever prop carries an explicit type annotation, since inference is left-to-right and that one usually "won."
- A `switch` on a closed union with no `never`-typed guard — a future added member will silently no-op instead of raising a build failure.

## Related

- `syllabus/21-frontend-web/react-testing.md`
- `syllabus/21-frontend-web/react-forms.md`
- `syllabus/21-frontend-web/react-state-management.md`
