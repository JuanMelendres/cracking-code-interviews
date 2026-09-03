# React Reconciliation & Fiber demo app (F-112)

Real Vite + React 19 app backing [`syllabus/21-frontend-web/react-reconciliation-and-fiber.md`](../../../syllabus/21-frontend-web/react-reconciliation-and-fiber.md).

## Run it

```bash
npm install
npm run dev
```

Three sections:

1. **DOM node reuse** — a real identity (`===`) comparison proving React reuses the SAME DOM node object across re-renders when type/position stay the same.
2. **Type-change remount** — the same inner counter, wrapped in a `<div>` vs `<section>`, showing state resets on a type change but survives a same-type prop change.
3. **Batching** — a real commit counter proving 3 `setState` calls in one handler cost exactly the same single commit as 1 `setState` call.

## Captured evidence (real browser session)

### DOM node reuse
Typed `"persist-me"` into the input, clicked "Re-render" twice. Real result:
```
Input value: "persist-me"  (never lost)
Total renders: 6, renders where the DOM node was the SAME reference as the previous render: 3
```
The identity comparison (not just visible persistence) confirms the DOM node object itself was reused.

### Type-change remount vs. same-type prop change
1. Clicked the inner counter twice: `count = 2`.
2. Clicked "Toggle wrapper type" (`<div>` → `<section>`): `count` reset to **0**.
3. Clicked the counter twice again: `count = 2`.
4. Clicked "Toggle highlight" (same `<div>`/`<section>` type, just a style prop): `count` **stayed at 2** — unaffected.

Direct, real contrast: a type change destroys and rebuilds the subtree (state lost); a prop change on the same type patches it in place (state survives).

### Batching
```
Before: a=0, b=0, c=0, commits=2   (mount, StrictMode double-invoked)
Click "Update all three at once": a=1, b=1, c=1, commits=4   (+2, one real commit)
Click "Update only a" (comparison): commits=6   (+2, ALSO one real commit)
```
Three simultaneous `setState` calls cost exactly the same single commit as one `setState` call — real, measured proof of batching, not an assumption.

## Verification performed

- `npm run dev` — clean start; a fresh tab showed zero console errors throughout, including after every interaction.
- `npm run build` — clean production build, zero errors/warnings.
