---
title: "Cheat Sheet: React Component Patterns"
slug: react-component-patterns
document_type: cheat-sheet
domain: frontend
topic_id: F-111
tier: Advanced
canonical: ../handbook/frontend/react-component-patterns.md
last_updated: 2026-09-03
---

# React Component Patterns

**Canonical chapter:** [`syllabus/21-frontend-web/react-component-patterns.md`](../syllabus/21-frontend-web/react-component-patterns.md)

## Core Mental Model

Every pattern in this chapter solves one of two distinct problems, and confusing which is which is the most common conceptual mistake: "how do I share a piece of stateful BEHAVIOR across components that otherwise have nothing in common" (composition, HOCs, render props, custom hooks — increasingly preferred in that historical order) versus "how do I let a fixed SET of components that are always used together share implicit state without the caller wiring it up manually" (compound components).

## Essential Definitions

- **Composition** — building UI by combining simpler components via `children`/props, not class inheritance; React's own stated position: no case found where an inheritance hierarchy was the right tool.
- **Higher-Order Component (HOC)** — a function taking a component and returning a new wrapping component that injects extra props; the pre-hooks mechanism for sharing stateful cross-cutting behavior.
- **Render prop** — a component accepting a function (often via `children`) and calling it with internal state, letting the caller render inline without a separate wrapper component.
- **Custom hook** — the modern default for sharing stateful behavior; zero extra components, explicit return value.
- **Compound components** — a fixed set of components (`Tabs`, `Tabs.List`, `Tabs.Tab`, `Tabs.Panel`) sharing state implicitly via a Context scoped to just that group; `Widget.Part` is an API-design convention, not a distinct runtime mechanism.

## Decision Table

| Question | Answer |
|---|---|
| Share stateful behavior across unrelated components, in new code? | Custom hook — essentially always |
| Working in an existing codebase already using HOCs extensively? | Extend the existing pattern; don't introduce a competing one without a deliberate migration |
| Building a small set of components always used together needing shared state? | Compound components via a locally-scoped Context |
| Tempted to subclass a component to specialize appearance/behavior? | Stop — pass different props/children instead |

**Cross-cutting-behavior patterns compared:**

| Concern | HOC | Render prop | Custom hook |
|---|---|---|---|
| Extra component instance | Yes, hidden at call site | No extra named component | None |
| Data source visible at call site | No — injected as a prop | Yes — explicit function argument | Yes — explicit return value |
| JSX nesting cost | None | One extra function-as-children level | None |
| Best for | Legacy codebases | Rare now, superseded by hooks | New code |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- HOC, render prop, and custom-hook implementations of identical "live window width" logic verified functionally equivalent across two real resizes (800px, then 1200px) — all three updated identically both times.
- Real automation-tool limitation caught during verification: `resize_window` changes `window.innerWidth` but does not dispatch a native `resize` event — worked around by manually dispatching `window.dispatchEvent(new Event('resize'))`; a real user dragging a window edge does fire it natively.
- Compound-components `Tabs` demo: clicking "Settings"/"Billing" correctly switched visible panel content, with the caller's JSX never referencing `activeTab` directly.

## Common Pitfalls

- Reaching for a HOC or render prop in new code where a custom hook is simpler and more explicit — a legacy-era habit.
- Subclassing a component to specialize it instead of composing via props/children.
- Using a widely-shared, application-level Context for a compound component's internal state instead of scoping it narrowly to the component group.
- Confusing "sharing behavior across unrelated components" (HOC/render-prop/hook) with "sharing state among a fixed related set" (compound components) — different problems, not interchangeable.
- A compound component's Context value recreated as a new object every render with no memoization — the same re-render-cost mistake covered for `useContext` generally.

## Interview Answer Skeleton

**30-sec:** React favors composition over inheritance for specializing components. HOCs, render props, and custom hooks all solve sharing stateful behavior across unrelated components, with decreasing indirection in that historical order — hooks are the modern default. Compound components solve a different problem: a fixed set of components sharing implicit state via Context, for a cohesive multi-part widget API.

**2-min:** Walk through composition vs. inheritance (`Panel` specialized two ways via props, no subclass). Implement the identical "window width" behavior three ways and compare code shape: HOC injects via a hidden wrapper; render prop hands state to an inline function with extra JSX nesting; hook needs neither. Close with compound components as a genuinely different problem — a Tabs widget sharing `activeTab` implicitly via a narrowly-scoped Context.

**Whiteboard:** HOC case: `WithWindowWidth` (state) → `WindowWidthDisplayInner` (receives `width` as a prop) — circle the outer box "invisible at call site." Render-prop case: one box with an arrow "calls children(width)" to inline JSX, no second box. Hook case: a single box with `useWindowWidth()` inside — no arrows elsewhere. Separately: a `Tabs` box with three children (`List`/`Tab`/`Panel`) connected by dotted lines to a "Context" bubble — "implicit shared state, fixed set of parts."

**Senior-level framing:** Explains the historical progression (HOC → render prop → hook) and the specific structural cost each step removed; correctly distinguishes the two genuinely different problems this chapter's patterns solve, rather than treating compound components as "another way to do hooks."

## Production Warning Signs

- A new engineer can't locate where a prop's value comes from because it's injected by an invisible HOC wrapper — a real indirection cost, not a mystery bug; candidate fix is a deliberate migration to a custom hook, not an ad-hoc patch.
- A legacy codebase mixing HOCs, render props, and hooks for the identical underlying behavior — maintaining three exposed APIs for one piece of logic instead of implementing it once.
- A "compound component" that's really just independent components rendered near each other with no actual shared state — using the naming convention without the substance.

## Related

- `syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md`
- `syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md`
- `syllabus/21-frontend-web/react-reconciliation-and-fiber.md`
