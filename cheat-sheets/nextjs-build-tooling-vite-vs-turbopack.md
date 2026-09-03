---
title: "Cheat Sheet: Vite vs. Turbopack Build Tooling"
slug: nextjs-build-tooling-vite-vs-turbopack
document_type: cheat-sheet
domain: frontend
topic_id: F-301
tier: Intermediate
canonical: ../handbook/frontend/nextjs-build-tooling-vite-vs-turbopack.md
last_updated: 2026-09-03
---

# Vite vs. Turbopack Build Tooling

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md`](../syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md)

First chapter in D-F3 (Tooling & Ecosystem). Verified against two real, independently-run dev servers and two real, independently-run production builds.

## Core Mental Model

Two philosophically different answers to "how do I serve a JS app during development." Vite doesn't bundle your own code in dev at all — the browser's native `import` resolves each source file as its own real HTTP request. Turbopack DOES bundle, incrementally and on-demand, into grouped chunks, even in dev. Both tools converge again for PRODUCTION: both genuinely tree-shake and both genuinely code-split — the decisive difference is a dev-mode architecture choice, not a difference in fundamental capability.

## Essential Definitions

- **Bundler** — resolves a module graph, transforms syntax (JSX/TS), tree-shakes provably-unused code, and chunks output for efficient loading.
- **Vite** — sidesteps bundling during dev using native ESM; still uses a real bundler (Rollup/Rolldown) for production; pre-bundles `node_modules` deps via esbuild even in dev.
- **Turbopack** — Next.js's own successor to webpack; bundles incrementally per-route, cached per-module, even in dev.
- **Runtime marker** — a real string/function call used to test tree-shaking; a code comment is the wrong test since minification strips comments regardless of usage.

## Decision Table

| Question | Choice |
|---|---|
| Choosing a bundler for a standalone (non-Next.js) React app? | Vite — mainstream default, fast dev-mode with no app-code bundling |
| Already building on Next.js specifically? | Turbopack — the framework's own integrated choice, equivalent production output |
| Debugging "my dev server feels slow"? | Check the actual network request pattern first, not a startup timestamp |
| Comparing tools by "ready in Xms"? | Don't, without controlling for app size |

## Key Numbers (real, captured network traces and grepped build output)

- Vite dev-mode load of a 3-file app: 10 real, separate requests — `main.jsx`, `App.jsx`, `mathUtils.js` each individually.
- Turbopack dev-mode load of a comparable page: ~15 grouped chunk files, zero one-to-one source-file requests.
- Identical tree-shaking test (used vs. unused function, real runtime markers) against both tools: used marker present once, unused marker absent entirely — matching results for both.
- Vite reported "ready in 400ms" for a 3-file app; Turbopack reported "Ready in 271ms" for a real 31-route app — not a fair comparison, since neither reflects full-app compilation.

## Common Pitfalls

- Treating a raw "ready in Xms" number as a fair speed comparison without controlling for app size.
- Assuming Vite "doesn't bundle at all" — it bundles the production build and pre-bundles `node_modules` deps even in dev; the precise claim is narrower: no application-code bundling specifically in dev.
- Assuming Turbopack's dev-mode bundling makes it worse at tree-shaking/code-splitting for production — identical tests show equivalent results.

## Interview Answer Skeleton

**30-sec:** Vite serves app code as native ES modules with zero bundling in dev — verified with a real trace showing one request per source file. Turbopack bundles incrementally into grouped chunks even in dev. Both converge for production: identical real tree-shaking and code-splitting tests produced matching results.

**2-min:** Cover the real dev-mode contrast (per-file requests vs. grouped chunks), the honest caveat about raw startup numbers across differently-sized apps, and the real convergence on production guarantees via identical marker-based tests.

**Whiteboard:** Two dev servers side by side. Vite: browser with ten arrows fanning to ten separate file icons. Turbopack: browser with one arrow fanning to a handful of chunk boxes, each grouping several source files. Below both: a shared "Production build (both tools)" box with two checkmarks — tree-shaking and code-splitting, both verified identical.

**Senior-level framing:** Describe the real, precise mechanical difference (native ESM vs. incremental bundling) rather than a vague speed claim, and explain why a raw startup number alone doesn't prove anything.

## Production Warning Signs

- A team migrating from Vite to Next.js is alarmed their dev server "feels different" — far fewer, larger requests instead of many small ones.
- This is expected, architectural behavior, not a misconfiguration.
- Debugging workflows relying on "which file is this code in" via the Network tab need to shift to source maps for Turbopack.

## Related

- `syllabus/21-frontend-web/nextjs-deployment-models.md`
- `syllabus/21-frontend-web/nextjs-styling-approaches.md`
- `syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md`
