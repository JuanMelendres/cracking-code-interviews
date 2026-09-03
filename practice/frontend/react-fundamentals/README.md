# React Fundamentals demo app (F-101–104)

Real Vite + React 19 app backing [`syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md`](../../../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md). Not a description of concepts — every claim in the chapter was verified live in a browser against this running app.

## Run it

```bash
npm install
npm run dev
```

Then open the printed `http://localhost:5173` URL. Five sections, top to bottom:

1. **F-101 JSX Basics** — a JSX element rendered next to the plain object it compiles to.
2. **F-102 Props and Composition** — a reusable `Card` that never imports what it wraps.
3. **F-103 useState** — three independent `<Counter />` instances proving state lives per instance, not per function.
4. **F-104a Events and Conditional Rendering** — a status state machine driven by click handlers.
5. **F-104b the index-as-key bug** — the centerpiece: two identical lists, one keyed by array index, one by stable ID.

## Reproducing the key-as-key bug (`src/demos/ListKeysPitfall.jsx`)

Type any text into Ana's note field in **both** lists, then click "Remove first person" on both. Captured, real result (verified via direct DOM inspection, not just visual reading):

```
before_removal:
  index list: Ana="NOTE-FOR-ANA", Bilal="", Carmen=""
  id list:    Ana="NOTE-FOR-ANA", Bilal="", Carmen=""

after removing the first person from both lists:
  index list: Bilal="NOTE-FOR-ANA"  <-- BUG: Ana's note is now attached to Bilal
  id list:    Bilal=""              <-- correct: the note was removed with Ana
```

The index-keyed list's DOM `<input>` for position 0 gets reused for whatever data is now at position 0 (Bilal) — since the input is uncontrolled (`defaultValue`, no `value`/`onChange`), its typed text lives in the DOM node itself and follows the node, not the person. The id-keyed list's DOM node for `key="p1"` (Ana) is correctly destroyed when Ana is removed, taking her note with it.

## Verification performed

- `npm run dev` — started cleanly, no console errors (`read_console_messages` confirmed empty).
- Every section's static content confirmed rendered via `get_page_text`.
- F-103 isolation confirmed by clicking A's `+1` three times and reading `A: 3, B: 0, C: 0`.
- F-104b bug reproduced and captured via direct `input.value` inspection before and after removal, shown above.
- `npm run build` — clean production build, no errors, no warnings (`vite build`, 581ms, 3 output files).
