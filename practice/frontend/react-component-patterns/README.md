# React Component Patterns demo app (F-111)

Real Vite + React 19 app backing [`syllabus/21-frontend-web/react-component-patterns.md`](../../../syllabus/21-frontend-web/react-component-patterns.md).

## Run it

```bash
npm install
npm run dev
```

Three sections:

1. **Composition vs. inheritance** — a generic `Panel` specialized via props at two different call sites, no class hierarchy.
2. **HOC vs. render prop vs. custom hook** — the SAME "live window width" behavior implemented three ways, side by side.
3. **Compound components** — a real, clickable Tabs widget sharing state implicitly among `Tabs.List`/`Tabs.Tab`/`Tabs.Panel` via Context.

## Captured evidence (real browser session)

### Window-width patterns — a real automation quirk caught and worked around
`resize_window` (the browser-automation tool) changes the CDP viewport metrics without dispatching a native `resize` DOM event — confirmed directly: after resizing to 800px, `window.innerWidth` correctly read `800` via JS, but all three demo components still showed their old value (`1280`), because no `resize` event had fired for their listeners to react to. This is a real limitation of programmatic viewport resizing (a genuine user dragging a window edge does fire `resize`); worked around by dispatching a synthetic `window.dispatchEvent(new Event('resize'))` after each resize.

With that in place, real captured proof of functional equivalence across two separate resizes:

```
After resize to 800px + dispatched resize event:
  HOC:         800px
  Render prop: 800px
  Hook:        800px

After resize to 1200px + dispatched resize event:
  HOC:         1200px
  Render prop: 1200px
  Hook:        1200px
```

All three implementations tracked identically, every time — proving the HOC, render-prop, and custom-hook versions are functionally equivalent, differing only in the code structure each requires (visible directly in the source files, not something that needs runtime proof).

### Compound components — real tab switching
Clicked "Settings", then "Billing": the page correctly showed "Settings panel content." then "Billing panel content." — `Tabs`'s internal `activeTab` state, shared via Context with `Tab` and `TabPanel`, correctly drove which panel rendered without the caller ever touching that state directly.

## Verification performed

- `npm run dev` — clean start; a fresh tab showed zero console errors throughout, including after all interactions.
- `npm run build` — clean production build, zero errors/warnings.
