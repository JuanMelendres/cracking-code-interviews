# React Accessibility demo app (F-116)

Real Vite + React 19 app backing [`handbook/frontend/react-accessibility.md`](../../../handbook/frontend/react-accessibility.md).

## Run it

```bash
npm install
npm run dev
```

Three sections:

1. **Semantic HTML vs. div-soup** — a real `<button>` next to a real `<div onClick>` "button", both wired to identical click handlers.
2. **Focus trap + focus return** — a real modal dialog with a working keyboard focus trap and explicit focus return to its trigger.
3. **Accessible form error association** — real `aria-invalid`/`aria-describedby` wiring, not just a visually-adjacent error message.

## Captured evidence (real browser session)

### Semantic HTML vs. div-soup
```
Accessibility tree: real <button> -> role "button"; the div -> role "generic" (not exposed as interactive at all)
Clicked real <button> (mouse): focused, activeElement = BUTTON "Real <button>"
Pressed Tab: activeElement jumped straight to "Open Modal" -- the div was completely skipped, never received focus
Clicked the div directly (mouse): clicks: 1 -- it DOES still work for a mouse user
```
Direct, measured proof: identical visual appearance and identical mouse behavior, but the div is entirely unreachable via keyboard — exactly the kind of bug that survives a purely visual QA pass.

### Focus trap + focus return
```
Clicked "Open Modal": activeElement = INPUT (modal-input) -- focus moved INTO the modal automatically
Tab: activeElement = BUTTON (modal-close) -- last focusable
Tab again: activeElement = INPUT (modal-input) -- wrapped back to the FIRST focusable, did not escape to the page behind
Escape: activeElement = BUTTON (modal-trigger) -- focus explicitly returned to the element that opened the modal
```
All three behaviors (enter, trap, return) verified independently via `document.activeElement`, not assumed from the implementation.

### Accessible form error association
```
Typed "a" into the username field, then blurred it (Tab):
  aria-invalid: "true"
  aria-describedby: "username-error"
  document.getElementById("username-error").textContent: "Username must be at least 3 characters"
```
Real, resolved id association — confirms the exact mechanism a screen reader uses to announce the error alongside the field, not just visual proximity.

## Verification performed

- `npm run dev` — clean start; a fresh tab showed zero console errors throughout, including after every interaction.
- `npm run build` — clean production build, zero errors/warnings.
