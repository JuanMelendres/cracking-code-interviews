# Micro-Frontends via Webpack Module Federation — Real Independent-Deploy Demo

Real, executed output backing
[`syllabus/21-frontend-web/micro-frontends-and-module-federation.md`](../../../syllabus/21-frontend-web/micro-frontends-and-module-federation.md)
(F-403). Two genuinely separate Webpack 5 applications — a `remote`
that exposes one component via `ModuleFederationPlugin`, and a `host`
that consumes it at *runtime*, over HTTP, from a different port — driven
by real headless Chromium (Playwright). Not a description of Module
Federation's contract; a real build, a real runtime fetch, and a real
rebuild-one-without-the-other test.

## Run it

```bash
npm install
npx playwright install chromium
npm run start   # builds remote + host, then runs the demo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## Layout

- `remote/` — a real, independent Webpack app. Its only job is exposing
  `./Button` (`remote/src/Button.js`) as a federated module, built to
  `remote/dist/remoteEntry.js` and served on port `3001`.
- `host/` — a real, independent Webpack app (`host/src/bootstrap.js`)
  that declares `remoteApp` as a runtime remote pointing at
  `http://localhost:3001/remoteEntry.js` and dynamically imports
  `remoteApp/Button`, built to `host/dist/bundle.js` and served on port
  `3002`. `host/index.html.template` is the one static file this build
  doesn't generate (webpack output isn't gitignored uniformly — `dist/`
  is — so `run.js` copies this template into `host/dist/` before serving).

## What it proves

- **The host's rendered UI genuinely comes from a separately built
  bundle, fetched at runtime.** Scenario 1 loads the host page and
  captures every network request the browser actually makes. The
  transcript shows real requests to `localhost:3001/remoteEntry.js` and
  `localhost:3001/src_Button_js.js` — the host's own `bundle.js` never
  contains the button's code; it contains only the *address* of where
  to ask for it at runtime.
- **Rebuilding only the remote, with the host's bundle never touched,
  changes what the host renders — real, verified independent
  deployability.** Scenario 2 edits `remote/src/Button.js` (new label,
  new color), rebuilds *only* the remote (`webpack --config
  remote/webpack.config.js` — the host's `bundle.js` file is never
  rebuilt or re-served), then reloads the same host page. The transcript
  shows the rendered button text changing from `Remote Button v1` to
  `Remote Button v2 -- deployed independently`, and its real computed
  `background-color` changing from the original blue to `rgb(197, 48,
  48)` — a real, measured proof that one team's deploy reached a shared
  page without the other team rebuilding or redeploying anything.

## Why this is the real mechanism, not a simulation

Module Federation's whole value proposition is a claim about *when* code
from another team becomes part of your page — at runtime, over the
network, not at your own build time. A demo that just imports a local
file and calls it "federation" wouldn't test that claim at all. This one
does: the remote and host are built by two separate `webpack` invocations
with no shared build step, served from two separate origins, and the
"host never rebuilt" step is enforced by literally never running the
host's own build command in scenario 2 — the same file (`host/dist/bundle.js`)
serves both scenario 1 and scenario 2's requests.
