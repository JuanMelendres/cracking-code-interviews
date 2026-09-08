---
title: "Cheat Sheet: JavaScript Fundamentals (Variables, Functions, Objects, Closures, and Asynchrony)"
slug: javascript-fundamentals-variables-functions-and-asynchrony
document_type: cheat-sheet
domain: frontend
topic_id: F-002
tier: Beginner
canonical: ../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md
last_updated: 2026-09-08
---

# JavaScript Fundamentals (Variables, Functions, Objects, Closures, and Asynchrony)

**Canonical chapter:** [`syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md`](../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md)

## Core Mental Model

JavaScript runs on a single thread, one function call at a time — everything that looks "parallel" (a timer, a network request, a Promise) is really just a note left for later, only acted on once the current work is completely finished. Closures are just functions remembering variables from where they were created; `this` in a regular function is decided by how it's called, but an arrow function has no `this` of its own and inherits it from where it's written.

## Essential Definitions

- **`let`/`const`** — block-scoped, temporal-dead-zone protected; `const` blocks rebinding, not mutation. **`var`** — function-scoped, hoisted to `undefined`, avoid in new code.
- **Closure** — a function that keeps access to its enclosing scope's variables after that scope has already returned; the mechanism behind React's `useState`.
- **`this` (regular function)** — determined by the call site (`obj.method()` → `this` is `obj`; plain call → `undefined` in strict/ESM code).
- **`this` (arrow function)** — no own `this`; inherited lexically from the enclosing scope at definition time.
- **Microtask queue** — Promise `.then()` callbacks, `async` continuations after `await`. **Macrotask queue** — `setTimeout`/`setInterval` callbacks. Microtasks fully drain before any macrotask runs.
- **`==`** — coerces types before comparing (surprising results). **`===`** — never coerces (this repository's default).

## Decision Table

| Situation | Choice |
|---|---|
| Callback needs the *enclosing* method's `this` (event handler, `setTimeout` inside a method) | Arrow function |
| Standalone function, constructor, or method meant to be called as `obj.method()` | Regular function/method |
| Comparing any two values for equality | `===`/`!==`, always |
| Sequential async steps | `async`/`await` |
| Genuinely concurrent async steps | `Promise.all` / raw `.then()` |
| Declaring any variable | `const` by default, `let` only if reassigned, never `var` |

## Event Loop Ordering (memorize this order)

1. All currently running synchronous code finishes.
2. The entire microtask queue drains (including new microtasks queued while draining).
3. Exactly one macrotask runs.
4. Repeat from step 2.

Real captured proof (`practice/frontend/javascript-fundamentals/src/eventLoop.js`): source order has `setTimeout` first, `.then()` calls after — actual print order is `1 (sync), 2 (sync), 3 (microtask), 4 (microtask), 5 (macrotask)`.

## Common Pitfalls

- Regular-function callback (`setTimeout`, event handler) inside a method losing `this` — use an arrow function or `.bind()`.
- `==` producing surprises: `[] == false` → `true`, `'0' == false` → `true`. Both `false` under `===`.
- Believing `const` makes objects immutable — it only blocks rebinding, not mutation.
- Assuming `setTimeout(fn, 0)` runs immediately — it waits for the call stack AND the full microtask queue to empty first.
- `var` in a loop closure capturing the same final value in every callback (`[3, 3, 3]`) — `let` fixes it (`[0, 1, 2]`) via a fresh binding per iteration.

## Interview Answer Skeleton

**30-sec:** Single-threaded, event-loop driven: sync code first, then all microtasks, then one macrotask at a time. Closures let a function keep access to outer variables after the outer function returns — the mechanism behind `useState`. Arrow functions inherit `this` lexically; regular functions get `this` from the call site. `===` beats `==` because `==` coerces types unpredictably.

**2-min:** Event loop ordering with the real `1,2,3,4,5` example → closures via the counter-factory proof → `this` distinction with the three real footgun shapes (method, timer callback, detached method) → `===` vs `==` with `[] == false`.

**Whiteboard:** Three boxes: Call Stack, Microtask Queue, Macrotask Queue. Arrow from empty stack to microtask queue (loop until empty), then arrow to macrotask queue (one at a time). Annotate the real 5-line print order underneath, circling that `setTimeout` (macrotask) was scheduled first in source but prints last.

**Senior-level framing:** States the microtask-fully-drains-before-macrotask rule unprompted; correctly reasons about `.bind()`/`.call()`/`.apply()`, not just arrow-vs-regular.

## Common Interview Traps

- Answering an event-loop ordering question using source order instead of the queue-priority rule.
- Saying "it's a scope problem" for a `this` bug instead of naming the actual mechanism (call-site binding vs. lexical inheritance).
- Treating `==`/`===` as stylistic rather than behaviorally different.
- Defining a closure as just "a nested function" without the defining property (surviving access after the outer scope returns).

## Related

- `syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md`
- `practice/frontend/javascript-fundamentals/`
- `00-project/frontend-topic-register.md`
