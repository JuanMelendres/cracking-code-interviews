---
title: "Cheat Sheet: React Reconciliation and the Fiber Architecture"
slug: react-reconciliation-and-fiber
document_type: cheat-sheet
domain: frontend
topic_id: F-112
tier: Advanced
canonical: ../handbook/frontend/react-reconciliation-and-fiber.md
last_updated: 2026-09-03
---

# React Reconciliation and the Fiber Architecture

**Canonical chapter:** [`syllabus/21-frontend-web/react-reconciliation-and-fiber.md`](../syllabus/21-frontend-web/react-reconciliation-and-fiber.md)

## Core Mental Model

React's rendering model has three distinct phases: render (call component functions, compute a new tree description — pure, can be interrupted), reconciliation (diff the new tree against the previous one), and commit (apply exactly those changes to the real DOM, synchronously, in one pass). Reconciliation is a heuristic, not a full tree-diff (which would be O(n³) for arbitrary trees): elements of different types produce entirely different trees (destroy and rebuild); elements of the same type at the same position are compared prop-by-prop and patched in place. Everything else in this chapter follows from that one heuristic.

## Essential Definitions

- **Reconciliation** — the algorithm computing the minimal set of real DOM mutations needed for a new tree description vs. the previous one.
- **Fiber** — React's internal linked-list data structure (introduced in React 16, replacing the stack reconciler); one node per element, each a unit of work that can be paused/resumed — this is what makes concurrent features possible, and is distinct from the diffing heuristic itself.
- **Batching** — grouping multiple state updates within the same synchronous execution context into a single re-render and commit.
- **Same type + position** — patched in place, DOM node identity and state preserved. **Different type at same position** — full destroy of the old subtree and build of a new one; all state lost.

## Decision Table

| Situation | Reconciliation outcome |
|---|---|
| Same element type, same position, props changed | Patch in place — DOM node reused, state preserved |
| Different element type at the same position | Destroy old subtree, build new one — state and DOM nodes lost |
| Want to force a full reset of a component's state on purpose | Change its `key` — treated as an identity change even with the same type |
| Multiple `setState` calls in one event handler | Batched into one render + one commit, not one per call |

**Type change vs. prop change:**

| Concern | Same-type update | Type-change (destroy/rebuild) |
|---|---|---|
| State preservation | Preserved automatically | Lost entirely |
| DOM node identity | Preserved (verified by `===`) | New nodes created |
| Performance | Cheap — only changed attributes touched | Expensive — full unmount + remount |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- DOM node reuse: after typing into an uncontrolled input and re-rendering twice, direct `===` comparison confirmed 3 renders with the identical DOM node object reference.
- Type change vs. prop change: toggling a wrapper from `<div>` to `<section>` reset a nested counter from `2` to `0`; toggling only a prop on the same element type left the counter at `2`, unaffected.
- Batching: a handler calling three separate setters increased a real commit counter by `+2` (one commit's worth under StrictMode's double-render); a handler calling only one setter produced the identical `+2` increase.

## Common Pitfalls

- Assuming conditionally rendering different component types at the same position behaves like a same-type prop change — it destroys state instead of preserving it.
- Believing batching means "all state updates across the whole app happen together," rather than "updates within the same synchronous execution context are grouped."
- Debugging an unexpected state reset by looking inside the component's own logic first, instead of checking whether its type or `key` changed at its render position.
- Confusing batching (commit grouping) with synchronous read-after-write — batching does not make a later line in the same handler see an updated value immediately.

## Interview Answer Skeleton

**30-sec:** Reconciliation diffs a new tree against the previous one with one heuristic: same type at the same position gets patched in place (DOM node and state preserved); different type gets destroyed and rebuilt (state lost). Batching groups same-handler state updates into one commit. Fiber is the linked-list unit-of-work structure that makes this interruptible, enabling concurrent features.

**2-min:** Walk through the three phases (render, reconciliation, commit), then the same-type-vs-different-type heuristic with the direct contrast (a wrapper's type change resets a nested counter; a prop-only change preserves it). Prove DOM node reuse via the identity-comparison demo. Close with the real, measured batching guarantee: three simultaneous updates cost exactly the same single commit as one.

**Whiteboard:** Two trees side by side ("current fiber tree" / "new fiber tree"), a `<div>` root, one child `<div>` in both — solid arrow "same type: PATCH, DOM node reused." Second scenario below: child is `<div>` in current, `<section>` in new — broken/X'd arrow "different type: DESTROY old subtree, BUILD new one, state lost."

**Senior-level framing:** States the same-type/different-type heuristic precisely and unprompted; correctly distinguishes batching (commit grouping) from the unrelated stale-closure/synchronous-read concern covered in the `useReducer` chapter.

## Production Warning Signs

- A component's state unexpectedly resets whenever some unrelated condition changes — check the JSX for a conditional switching between different component types (or a changing `key`) at that position before assuming a logic bug.
- A form silently loses typed data when a loading state briefly flips back on — likely two different component types (e.g., skeleton vs. real form) swapped at the same JSX position; fix by keeping one type mounted and toggling visibility, or lifting state above the conditional.
- Manually caching a DOM node reference across renders assuming it never changes, without accounting for type changes, key changes, or unstable list keys that create new nodes.

## Related

- `syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md`
- `syllabus/21-frontend-web/react-component-patterns.md`
- `syllabus/21-frontend-web/react-concurrent-rendering.md`
