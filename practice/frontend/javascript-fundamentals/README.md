# JavaScript Fundamentals — Real, Executed Demos

Backing evidence for [`syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md`](../../../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md) (topic F-002). Every script here was actually run with Node.js (`node --version` → `v24.18.0`), and [`output.txt`](./output.txt) is the real, captured terminal output of running all seven scripts in sequence — nothing in that file was retyped from expectation.

`package.json` sets `"type": "module"`, so every `.js` file here is a real ES module (`import`/`export`, not `require`).

## Scripts

- [`src/variablesAndScope.js`](./src/variablesAndScope.js) — `var` (function-scoped) vs `let`/`const` (block-scoped); the temporal dead zone (a real `ReferenceError` from reading a `let` before its declaration line executes); `const` blocking reassignment but not mutation; `typeof` and truthy/falsy values, including values that look "empty" but are truthy (`'0'`, `[]`, `{}`).
- [`src/thisBinding.js`](./src/thisBinding.js) — the `this`-in-arrow-vs-regular-function footgun in three shapes: an object method (`this` bound to the object at call time for a regular function, vs. lexically captured — and wrong — for an arrow function used as a method); a `setTimeout` callback (a regular-function callback's `this` is NOT the object that scheduled it; an arrow callback's `this` is, because it's captured from the enclosing method); and a detached class method (a regular method assigned to a bare variable loses its `this` entirely, while an arrow-function class field keeps working because it captured `this` at construction time).
- [`src/closures.js`](./src/closures.js) — a real closure-based counter factory (`makeCounter`) proving two independent counters never share state; the classic `var`-in-a-loop closure bug (all three callbacks return `3`) fixed by switching to `let` (returns `0, 1, 2`, since `let` creates a fresh binding per iteration); and a minimal fake `useState` implementation showing that React's hook model is the same closure mechanism under different syntax.
- [`src/eventLoop.js`](./src/eventLoop.js) — the real sync-vs-microtask-vs-macrotask ordering surprise: synchronous code always finishes first, then all queued Promise `.then()` microtasks drain, and only then does a `setTimeout(fn, 0)` macrotask run — proven by the actual printed order `1, 2, 3, 4, 5`, not the source order. Also shows `async`/`await` is sugar over the same microtask queue (code after an `await` runs as a microtask, after synchronous code that follows the `async` function call).
- [`src/equality.js`](./src/equality.js) — a table of real `==` vs `===` results, including `[] == false` (`true`) and `'0' == false` (`true`), both `false` under `===`, with the coercion chain explained for each.
- [`src/arraysObjectsDestructuring.js`](./src/arraysObjectsDestructuring.js) — array/object literals, `map`/`filter`/`reduce`/`forEach`, object and array destructuring (including nested and default-valued), and spread/rest syntax for both arrays and objects.
- [`src/mathUtils.js`](./src/mathUtils.js) + [`src/moduleDemo.js`](./src/moduleDemo.js) — a real two-file ESM `import`/`export` demo (named exports, a default export, and importing both into a second file).

## Running it yourself

```bash
node src/variablesAndScope.js
node src/thisBinding.js
node src/closures.js
node src/eventLoop.js
node src/equality.js
node src/arraysObjectsDestructuring.js
node src/moduleDemo.js
```

## A note on `thisBinding.js`'s output order

In the captured transcript, the two `setTimeout` callback lines ("regular callback: ..." / "arrow callback: ...") print AFTER the "extracting a method loses `this`" section's own output, even though `runWithRegularCallback()`/`runWithArrowCallback()` are called earlier in the file. This is not a bug — it is the event loop in action: `setTimeout(fn, 0)` never runs synchronously, so both callbacks are deferred until the rest of the script's synchronous code (including the later "extracting a method" section) has already run. See [`eventLoop.js`](./src/eventLoop.js) for the same mechanism demonstrated directly.
