# Styling approaches comparison (F-302)

A real, minimal Vite + React app comparing three styling approaches side by side, backing [`handbook/frontend/nextjs-styling-approaches.md`](../../../handbook/frontend/nextjs-styling-approaches.md) (F-302): CSS Modules, Tailwind CSS (v4, via `@tailwindcss/vite`), and CSS-in-JS (styled-components v6).

## Run it

```bash
npm install
npm run dev     # this chapter used --port 5300
npm run build   # real vite build — captures the real output below
```

## What's here

- `src/CssModulesDemo.module.css` + `src/CssModulesDemoTwo.module.css` — the SAME source-level class name (`.title`), defined independently in two files, to prove real scoping.
- `src/CssModulesDemo.jsx` — renders both, printing each resolved class name string directly.
- `src/TailwindDemo.jsx` — a real, applied utility class (`bg-fuchsia-700`) used for a real purge test.
- `src/StyledComponentsDemo.jsx` — a `Badge` styled-component whose background color is driven by a real `$urgent` prop at runtime.

## Captured evidence

### CSS Modules: real, proven scoping

```
$ grep -o '\._title_[a-zA-Z0-9_]*' dist/assets/index-*.css | sort -u
._title_18b5g_1
._title_1uj7y_1
```

Two DIFFERENT real compiled class names for the identical source-level `.title` class, defined independently in two different `.module.css` files — no collision, confirmed directly from the built output, not just from the absence of a visible bug.

### Tailwind: real JIT purge — and a real, unexpected finding about HOW it decides what to generate

A genuinely unmentioned utility (`bg-lime-300`, appearing nowhere in `src/` or `index.html` — not as a className, not as a comment, not as any string) is correctly absent from the built CSS. But TWO real, decisive, unexpected findings surfaced while building this test, each reproduced deliberately:

**Finding 1 — a mere text mention (even in a comment) is enough to trigger generation.** Adding a plain code COMMENT (`// TODO: consider bg-cyan-300 for a future variant`) to `src/TailwindDemo.jsx`, with no corresponding `className` usage anywhere:

```
$ echo "// TODO: consider bg-cyan-300 for a future variant" >> src/TailwindDemo.jsx
$ npm run build
$ grep -c "bg-cyan-300" dist/assets/index-*.css
1
```

Removed immediately after capture.

**Finding 2 — Tailwind v4's DEFAULT scan is project-wide, not source-directory-scoped, and includes this app's own `README.md`.** With this very README.md mentioning both `bg-lime-300` and `bg-cyan-300` as plain documentation text (exactly the paragraphs you're reading now), a real rebuild picked BOTH up — genuinely present in the built CSS, confirmed by grep, with zero corresponding `className` usage anywhere in `src/`. Verified precisely by temporarily moving `README.md` out of the directory entirely and rebuilding: both classes vanished (`0`). Restored immediately after.

**The real, documented fix** — `src/index.css` uses `@import "tailwindcss" source(none);` plus an explicit `@source "./";` directive (scoped to the `src/` directory relative to that CSS file), replacing Tailwind v4's default whole-project scan with a narrow, explicit allowlist:

```
$ npx vite build   # with the fix in place, README.md's own mentions still present in the project
$ grep -c "\.bg-lime-300\|\.bg-cyan-300" dist/assets/index-*.css
0
$ grep -c "bg-fuchsia-700" dist/assets/index-*.css
1   -- correctly still generated, since it IS used in src/TailwindDemo.jsx
```

Tailwind's real JIT engine does textual pattern scanning, by default across the WHOLE project (respecting `.gitignore`) — not semantic, AST-aware analysis of actual `className` usage, and not scoped to source directories unless a team explicitly configures it that way.

### CSS-in-JS (styled-components): real, runtime-generated styles keyed to a prop value

Real, live browser session, two `<Badge>` elements — one plain, one with `$urgent`:

```js
normal.className   // => "sc-bdvwhi CGjfy"
urgent.className   // => "sc-bdvwhi cHVSUp"   -- a DIFFERENT dynamically-generated class
```

The real, injected CSSOM rules (styled-components v6 inserts via the CSSOM API directly — `<style>.textContent` reads EMPTY even though real rules exist; `document.styleSheets[n].cssRules` is what actually shows them):

```
.CGjfy { display: inline-block; padding: 4px 10px; border-radius: 999px; color: white; background: seagreen; }
.cHVSUp { display: inline-block; padding: 4px 10px; border-radius: 999px; color: white; background: crimson; }
```

Two genuinely different class names AND rule bodies, generated at RUNTIME from a single component definition based on the `$urgent` prop — something neither CSS Modules nor Tailwind's static, build-time class names can do without pre-generating every variant as its own class.
