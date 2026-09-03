---
title: "Cheat Sheet: React Fundamentals (JSX, Components, Props, and State)"
slug: react-fundamentals-jsx-components-props-and-state
document_type: cheat-sheet
domain: frontend
topic_id: F-101-F-104
tier: Beginner
canonical: ../handbook/frontend/react-fundamentals-jsx-components-props-and-state.md
last_updated: 2026-09-03
---

# React Fundamentals (JSX, Components, Props, and State)

**Canonical chapter:** [`syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md`](../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md)

## Core Mental Model

JSX is syntax sugar for building a tree of plain JavaScript objects describing what the UI should look like — not HTML, not magic. React's job is to compare that tree to the previous one and make the smallest possible set of real changes to the actual DOM (reuse and patch, not rebuild). Once that clicks, props (how a parent's description feeds a child's), state (data an instance remembers across tree comparisons), and `key` (which previous DOM node a tree node corresponds to) stop being separate rules to memorize.

## Essential Definitions

- **JSX** — compiles to plain object descriptions (e.g. `{ type: 'p', props: {...} }`), not DOM nodes; optional but near-universal.
- **Props** — flow one direction, parent → child, read-only from the child's side; a child affects a parent only by calling a callback prop.
- **Composition (`children`)** — lets a wrapper component (e.g. `Card`) render arbitrary content without importing or knowing what it is.
- **`useState`** — state belongs to a component *instance* (its position in the render tree), not its function definition; proven by three independent `<Counter />` instances sharing one function but not state.
- **`key`** — React's instruction for "this DOM node corresponds to this piece of data, across renders"; only needs to be unique among siblings, not globally.

## Decision Table

| Situation | Choice |
|---|---|
| List can be reordered, filtered, or have items removed from the middle | Stable, data-derived `key` (a real ID) — never the array index |
| List is provably static in order and length for its entire lifetime | Index-as-key is technically safe, but a real key costs nothing |
| Need the typed value on every keystroke (live validation, counters) | Controlled input (`value` + `onChange`) |
| Only need the value on submit; keystroke re-renders are a measured concern | Uncontrolled input (`defaultValue` + `ref`) — understand the key-reuse risk |

**Controlled vs. uncontrolled input trade-offs:**

| Concern | Controlled | Uncontrolled |
|---|---|---|
| Source of truth | React state | The DOM itself |
| Re-render cost | Per keystroke | None per keystroke |
| Interacts with key-reuse bugs | Immune (React re-sets value every render) | Exposed (DOM-owned value persists across a reused node) |

## Common Pitfalls

- Using the array index as `key` for any list that can reorder/filter/remove items — the single most common React interview red flag.
- Believing JSX "is" HTML rendered directly, rather than compiled syntax describing a JS object tree.
- Mutating props directly inside a child (`props.value = ...`) instead of calling a callback prop.
- Assuming `useState`'s initial value re-runs the initializer function on every render (it runs once, at mount).
- Defining a component inside another component's render body — creates a new component type every render, resetting all internal state.
- Prop drilling as a first resort instead of composition (`children`) or Context past two or three levels.

## Interview Answer Skeleton

**30-sec:** JSX compiles to plain JS objects describing a UI tree; React diffs that tree against the previous one and patches only what changed. Props flow one-way and are read-only. `useState` gives an instance its own memory across renders. `key` tells React which DOM node a list item corresponds to — index keys break the moment order/membership changes.

**2-min:** Add the mental model (declarative description → diffing → minimal DOM patch), the three-independent-counters proof for per-instance state, then the key bug's concrete before/after: an uncontrolled input's typed text staying attached to the wrong row after a deletion, because the DOM node got reused across different data when keyed by position.

**Whiteboard:** Three boxes "Ana(0)/Bilal(1)/Carmen(2)" before removal; below, shifted boxes "Bilal(0)/Carmen(1)" after removing Ana — circle that key=0 pointed to different data before/after, so React treats it as the SAME node. Redo with real IDs (`p1`/`p2`/`p3`): `p1`'s box is crossed out entirely on removal, `p2`/`p3` untouched.

**Senior-level framing:** States the DOM-node-reuse mechanism unprompted (not just "it's bad practice"), and names the exact condition under which index keys are safe (list provably never reorders/filters/inserts/removes for its whole lifetime).

## Common Interview Traps

- Saying "index keys are bad" without explaining the DOM-node-reuse mechanism.
- Confusing props (parent-to-child, read-only) with state (owned by the component itself).
- Believing JSX forces React to re-create every DOM node on every render.
- Not knowing whether the same bug happens with a controlled input (it's masked, not absent — React resets `value` from state regardless of node reuse).

## Related

- `syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md`
- `syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md`
- `00-project/frontend-topic-register.md`
