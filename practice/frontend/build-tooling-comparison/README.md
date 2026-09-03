# Build tooling comparison (F-301): Vite vs. Next.js/Turbopack

A real, minimal Vite + React app, purpose-built to directly compare against [`practice/frontend/react-nextjs-fundamentals/`](../react-nextjs-fundamentals/) (Next.js 16.3.1, Turbopack) for [`syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md`](../../../syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md) (F-301). Deliberately small and separate from [`react-fundamentals`](../react-fundamentals/) (F-101–F-119's own Vite app) — this app's purpose (bundler mechanics: tree-shaking, code-splitting, dev-server architecture) is materially different from that chapter's own (React concepts), so it gets its own, focused app rather than reusing or modifying that one.

## Run it

```bash
npm install
npm run dev     # Vite dev server, default port 5173 (this chapter used --port 5199)
npm run build   # real vite build — captures the real dist/ manifest below
```

## What's here

- `src/mathUtils.js` — `add` (imported and used) and `unusedSubtract` (exported, never imported) — a real tree-shaking test, with runtime `console.log` markers (not comments, which minification strips regardless of tree-shaking).
- `src/LazyPanel.jsx` — only ever loaded via `React.lazy()` + dynamic `import()` in `App.jsx` — a real code-splitting test.
- `src/App.jsx` — wires both together, plus a button to trigger the lazy load.

The SAME two tests were reproduced in `react-nextjs-fundamentals` via `lib/f301-math-utils.js`, `app/components/F301LazyPanel.js`, `app/components/F301Demo.js`, and `app/build-tooling-demo/page.js` — see that app's own README for the Turbopack-side captured evidence.

## Captured evidence

### Real dev-server cold start

```
$ npx vite --port 5199
  VITE v8.2.1  ready in 400 ms
```

Compare against Next.js/Turbopack's own real `✓ Ready in 271ms` (captured in `react-nextjs-fundamentals`'s README) — NOT a fair apples-to-apples comparison on its own, since that app has 31 routes built up across F-201–F-214 versus this app's 3 source files; both frameworks report "ready" once the DEV SERVER PROCESS itself is listening, not once every route is compiled — actual route compilation happens on first request in both tools. The real, decisive comparison is the request PATTERN once a page actually loads — see below.

### The real, central mechanical difference: dev-mode network requests

Real `read_network_requests` capture, first load of this app in a browser (Vite dev server):

```
GET /@vite/client
GET /src/main.jsx
GET /@react-refresh
GET /node_modules/.vite/deps/react.js?v=...
GET /node_modules/.vite/deps/react-dom_client.js?v=...
GET /src/App.jsx
GET /node_modules/.vite/deps/react_jsx-dev-runtime.js?v=...
GET /node_modules/.vite/deps/rolldown-runtime-....js?v=...
GET /src/mathUtils.js
GET /node_modules/.vite/deps/react-dom.js?v=...
```

Every ONE of this app's own source files (`main.jsx`, `App.jsx`, `mathUtils.js`) is a SEPARATE real HTTP request for a native ES module — the browser's own `import` resolves them directly. Vite does not bundle application code in dev at all; it only pre-bundles third-party `node_modules` dependencies (via esbuild) for performance, visible above as the `.vite/deps/*.js` requests.

Real `read_network_requests` capture, `react-nextjs-fundamentals`'s `/about` page (Turbopack dev server):

```
GET /about
GET /_next/static/chunks/[root-of-the-server]__068_is3._.css
GET /_next/static/chunks/[turbopack]_browser_dev_hmr-client_hmr-client_ts_....js
GET /_next/static/chunks/node_modules_next_dist_compiled_next-devtools_index_....js
GET /_next/static/chunks/node_modules_next_dist_compiled_react-dom_....js
GET /_next/static/chunks/node_modules_next_dist_compiled_react-server-dom-turbopack_....js
... (10 more grouped chunk files)
```

No individual source file (`page.js`, a component file) appears as its own request — Turbopack GROUPS many modules into fewer chunk files, even in dev mode. This is the real, decisive, mechanical contrast: Vite's dev server does not bundle your own application code at all (native ESM, one request per module); Turbopack's dev server DOES bundle, incrementally and on-demand, but still produces grouped chunk files rather than one file per source module.

### Real, decisive tree-shaking proof

```
$ npm run build
dist/assets/LazyPanel-Cd07qVps.js    0.18 kB
dist/assets/index-DDDciBC5.js      192.04 kB

$ grep -c "TREE_SHAKE_MARKER_ADD_KEPT" dist/assets/index-*.js
1
$ grep -c "TREE_SHAKE_MARKER_SUBTRACT_DEAD" dist/assets/index-*.js
0
```

`add` (imported and called) survives into the production bundle; `unusedSubtract` (exported, never imported anywhere) is genuinely absent — real, verified dead-code elimination, not just a claim. The SAME test against Turbopack's own build output (`react-nextjs-fundamentals`'s `.next/static/chunks/`) produced the identical result — see that app's README.

### Real, decisive code-splitting proof

`LazyPanel-Cd07qVps.js` (0.18 kB) is a genuinely SEPARATE file from the main `index-DDDciBC5.js` bundle — `React.lazy()` + dynamic `import()` produced a real, independent chunk, only fetched when the "Load lazy panel" button is actually clicked, confirmed with `grep -c "LAZY_CHUNK_MARKER"` returning `0` in the main bundle and `1` in the lazy chunk. The identical result was reproduced with `next/dynamic` against Turbopack's own build.
