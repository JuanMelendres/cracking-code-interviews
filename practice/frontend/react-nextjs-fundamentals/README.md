# Next.js Fundamentals demo app (F-201–F-214, plus F-301)

Real Next.js 16.3.1 (App Router) app backing [`syllabus/21-frontend-web/nextjs-fundamentals.md`](../../../syllabus/21-frontend-web/nextjs-fundamentals.md) (F-201), [`syllabus/21-frontend-web/nextjs-app-router-fundamentals.md`](../../../syllabus/21-frontend-web/nextjs-app-router-fundamentals.md) (F-202), [`syllabus/21-frontend-web/nextjs-server-vs-client-components.md`](../../../syllabus/21-frontend-web/nextjs-server-vs-client-components.md) (F-203), [`syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md`](../../../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md) (F-204), [`syllabus/21-frontend-web/nextjs-rendering-strategies.md`](../../../syllabus/21-frontend-web/nextjs-rendering-strategies.md) (F-205), [`syllabus/21-frontend-web/nextjs-streaming-and-suspense.md`](../../../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md) (F-206), [`syllabus/21-frontend-web/nextjs-route-handlers.md`](../../../syllabus/21-frontend-web/nextjs-route-handlers.md) (F-207), [`syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md`](../../../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md) (F-208), [`syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md`](../../../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md) (F-209), [`syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md`](../../../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md) (F-210), [`syllabus/21-frontend-web/nextjs-authentication-patterns.md`](../../../syllabus/21-frontend-web/nextjs-authentication-patterns.md) (F-211), [`syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md`](../../../syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md) (F-212), [`syllabus/21-frontend-web/nextjs-deployment-models.md`](../../../syllabus/21-frontend-web/nextjs-deployment-models.md) (F-213), and [`syllabus/21-frontend-web/nextjs-fullstack-integration.md`](../../../syllabus/21-frontend-web/nextjs-fullstack-integration.md) (F-214). Extended in place rather than scaffolding a new Next.js project each time, since each chapter is a direct continuation of the same file-based-routing playground.

> **F-301 note — Turbopack's own tree-shaking/code-splitting, plus the real dev-mode contrast against Vite:** the same real tree-shaking test as the new sibling app [`practice/frontend/build-tooling-comparison/`](../build-tooling-comparison/) (an unused export, real runtime markers, not comments) was reproduced here against a real Turbopack production build: the used function's marker survived, the unused one's did not. A `next/dynamic` lazy component landed in its own separate real chunk file, confirmed by grep. The central, decisive real contrast: a real `read_network_requests` capture of Vite's dev server shows every source file as its OWN separate native-ESM HTTP request (no app-code bundling in dev at all); the same capture against this app's own Turbopack dev server shows grouped, pre-bundled chunk files instead — Turbopack bundles even in dev, Vite doesn't. See the F-301 evidence section below.

> **F-214 note — closes D-F2, with a real separate Spring Boot backend:** a genuinely separate process ([`practice/java/full-stack-integration-backend/`](../../java/full-stack-integration-backend/), port 8080) was stood up specifically for this chapter. A real, live browser `fetch()` to it with no CORS config produced an exact, captured `TypeError: Failed to fetch` plus the real browser-console CORS-policy message; fixed with a real, explicit origin allowlist and retested. A real, subtle finding: `curl -H "Origin: ..."` confirmed `Access-Control-Allow-Origin` genuinely present on the wire, but the SAME successful browser `fetch()`'s own `res.headers.get('access-control-allow-origin')` returned `null` — that header isn't JS-readable by default. The real BFF pattern (`app/api/backend-proxy/route.js`) was built and tested end to end: a real, live, authenticated browser session (httpOnly cookie confirmed empty, per F-211) retrieved the Spring backend's protected data via Next.js alone, while the backend's own shared-secret credential never once reached the browser. See the F-214 evidence section below.

> **F-213 note — real, measured self-hosting mechanics, not marketing:** `output: "standalone"` measured a real ~13x smaller footprint (42MB vs. a naive 543MB `node_modules`+`.next`). Running the standalone `server.js` directly (no Docker) reproduced a real, common gotcha: static assets 404 until `public/`/`.next/static` are manually copied in — fixed and re-verified. A real, deliberately contrasted multi-instance test found the framework's own "shared Server Actions encryption key" requirement applies specifically to genuine closures — `app/notes/actions.js`'s plain, top-level `deleteNote` (`.bind()`-only, no closure) rendered the SAME action id across two independently-built instances and worked cross-instance with ZERO shared-key config, while a new, deliberately closure-capturing action (`app/deploy-test/`) rendered a real, visibly different encrypted field. A real `Cache-Control` header capture found one genuine mismatch against the framework's own documentation prose for static pages. A real `docker build`/`docker run` cycle was executed (the local Docker daemon was started specifically for this chapter, after an initial check found it unavailable). See the F-213 evidence section below.

> **F-212 note — a mutation can commit even while the visible response looks like a rejection:** with `app/notes/actions.js`'s `addNote` auth check temporarily removed (restored immediately after capture), an anonymous, no-cookie curl POST directly to `/notes` wrote a real row to `data/notes.json` — confirmed by reading the file directly — even though the HTTP response was a plain `307` to `/login`, indistinguishable from every other rejected request. Separately, two real, contrasted forms on the same page proved `useOptimistic`'s progressive-enhancement cost directly: a raw bound action reference (`deleteNote.bind(null, id)`) renders real, working hidden fields and survives a raw multipart curl POST with no JavaScript at all; the same kind of action wrapped in a closure (so `useOptimistic` could run first) rendered a dead `action="javascript:throw ..."` placeholder instead. A real, deliberately mismatched `Origin` header also confirmed the framework's own CSRF protection, with an exact captured server log message. This chapter's own production build attempt hit a real, external `httpbin.org` outage (503s) unrelated to any of this — it was blocking F-204/F-205's pre-existing routes, not F-212's own code, which compiled cleanly. See the F-212 evidence section below.

> **F-211 note — upgrades F-208's own demo, with a real reproduced bypass:** `proxy.js`'s original naive `.has('session')` check (F-208) is upgraded to real JWT signature verification. A real, deliberately tampered token was tested against BOTH versions: the naive check let it through (real `x-proxy-hit` header present); the DAL caught it anyway (redirect to `/login`, not Proxy's `/`) — real, reproduced proof of defense-in-depth. Separately, the experimental `unauthorized()` primitive was tested three ways (no flag / flag+streaming / flag+Route-Handler) with three genuinely different real outcomes for the actual HTTP status. See the F-211 evidence section below.

> **F-210 note — a genuinely silent deprecation, unlike F-208's loud one:** the `priority` prop is deprecated in Next.js 16 in favor of `preload`, but real, direct testing (a real `next dev` console check and a full real `next build`) found ZERO warning anywhere for using the old prop — it just keeps working. Separately, `next.config.mjs`'s `remotePatterns`/`qualities` are real, hard-enforced allowlists at `/_next/image` (real `400`s captured before configuring them), while the `<Image>` component itself silently CLAMPS a disallowed `quality` instead of erroring. See the F-210 evidence section below.

> **F-209 note — completes an open F-206 thread, plus an unplanned real finding:** F-206 found bot-blocking was scoped to `generateMetadata` but never actually tested a SLOW one. F-209 built one (a real 1200ms delay) and got the decisive contrast: normal requests stream content at +44ms; a `Twitterbot/1.0` request doesn't get response HEADERS until +1246ms. Separately, removing `metadataBase` did NOT produce the build error the docs describe — only a warning, plus a real WRONG fallback URL (`localhost:3000`) baked into static HTML. See the F-209 evidence section below.

> **F-208 note — real, version-significant finding:** "Middleware" is deprecated as of Next.js 16; the current convention is `proxy.js` exporting a `proxy` function, and Proxy is hardcoded to the Node.js runtime (the Edge runtime is forbidden there outright — a real build error, not a warning). See the F-208 evidence section below for both real, contrasting captured build outputs.

> **F-207 note:** `lib/widgets-store.js` is module-level, in-memory state, shared across requests only because this app runs as a single long-lived `next start` Node process — the framework's own Route Handler docs warn this pattern breaks on a lambda-style host, where each request can land on a different instance with its own copy.

> **F-206 note:** verification for this chapter required a real chunk-timing observer script (`scripts/stream-observer.mjs`), not `curl`, per this Next.js version's own streaming docs — `curl`'s own buffering can hide real streaming behavior. Run it against a real `next start` server: `node scripts/stream-observer.mjs <url> [User-Agent]`.

> **F-205 note:** deliberately does NOT re-demonstrate ISR — F-204's `app/data-fetching/revalidate/page.js` already proved that mechanism with a real timed test, and F-205's own chapter cites it directly rather than duplicating it. F-205's new evidence covers SSR (via `headers()`) and SSG (via `generateStaticParams`), the two rendering strategies F-204 didn't demonstrate.

> **F-204 note:** this app's `next.config.mjs` does NOT set `cacheComponents: true`, so it runs under this Next.js version's "Previous Model" of caching (`fetch`'s own `cache`/`next.revalidate`/`next.tags` options) — confirmed by reading `node_modules/next/dist/docs/01-app/02-guides/caching-without-cache-components.md` directly, since this is exactly the API surface the register's F-204 topic names. Version 16 also ships an entirely different, opt-in "Cache Components" model (`"use cache"`, `cacheLife`, `cacheTag`) — out of scope for this chapter, briefly noted for context only.

> **F-203 note:** requires `.env.local` (gitignored, not committed) with `SERVER_SECRET_DEMO=SUPER_SECRET_VALUE_9f8e7d6c` for the Server-Component-secret demo to have a real value to read. Recreate it locally if cloning fresh — see the F-203 evidence section below for exactly what it proves.

## Run it

```bash
npm install
PORT=5198 npm run dev    # app at http://localhost:5198 -- PORT is not persisted anywhere; set it per run
npm run build            # real next build — captures the real route manifest below
npm start                # or: PORT=5198 npm start, for a production server
```

> This Next.js version (16.3.1) shipped after this assistant's training cutoff. Before writing any route code, the bundled docs at `node_modules/next/dist/docs/01-app/01-getting-started/03-layouts-and-pages.md` were read directly (per the auto-generated `AGENTS.md` this project scaffolds) to confirm current App Router conventions — notably that `params` in a dynamic route is a `Promise` that must be `await`ed.

Routes, all created purely by file location — no router config, no route registration:

**F-201:**
- `app/page.js` → `/`
- `app/about/page.js` → `/about`
- `app/blog/[slug]/page.js` → `/blog/<anything>` (dynamic segment)
- `app/layout.js` (root layout, wraps every route) + `app/components/PersistentHeader.js` (client component with a real mount counter)

**F-202 (added):**
- `app/dashboard/layout.js` + `app/dashboard/page.js` + `app/dashboard/settings/page.js` — a NESTED layout, three levels deep (root → dashboard → settings), scoped only to `/dashboard/*`.
- `app/(marketing)/layout.js` + `app/(marketing)/pricing/page.js` — a ROUTE GROUP: the `(marketing)` folder scopes a layout without adding a URL segment.
- `app/components/MountCounter.js` — the generic version of `PersistentHeader`'s counter, reused at every layout level so each one's persistence can be measured independently.

**F-203 (added):**
- `app/components/ServerSecretDemo.js` — a Server Component (no `"use client"`) reading a server-only env var directly.
- `app/components/ClientCounter.js` — a Client Component (`"use client"`, `useState`, an `onClick` handler).
- `app/server-vs-client/page.js` — renders both side by side.

**F-204 (added):**
- `app/data-fetching/default/page.js` — `fetch()` with no `cache` option.
- `app/data-fetching/no-store/page.js` — `fetch(url, { cache: 'no-store' })`.
- `app/data-fetching/force-cache/page.js` — `fetch(url, { cache: 'force-cache', next: { tags: [...] } })` + a real `RevalidateButton`.
- `app/data-fetching/revalidate/page.js` — `fetch(url, { next: { revalidate: 5 } })`.
- `app/actions.js` — a real Server Action calling `revalidateTag`.
- `app/components/RevalidateButton.js` — a real Client Component invoking that Server Action.
- All four fetch demos target `https://httpbin.org/uuid`, a real public endpoint returning a fresh random UUID on every real HTTP call it receives — used as an external, always-reachable "upstream" so build-time prerendering (see below) doesn't hit a chicken-and-egg problem with this app's own dev/start server not being up yet.

**F-205 (added):**
- `app/rendering-strategies/ssr/page.js` — reads `headers()` (a Request-time API), forcing genuine per-request SSR.
- `app/rendering-strategies/ssg/[id]/page.js` — `generateStaticParams()` returning `["1", "2"]`, real SSG for those two ids; any other id (e.g. `999`) is generated on first real request, then cached (ISR-style fallback for unlisted params).

**F-206 (added):**
- `app/streaming/sibling-boundaries/page.js` — three sibling `<Suspense>` boundaries with genuinely different artificial delays (300ms, 1200ms, 2500ms).
- `app/streaming/full-page/page.js` + `app/streaming/full-page/loading.js` — page-level streaming via the `loading.js` file convention.
- `scripts/stream-observer.mjs` — a real chunk-timing observer script (fetch + `ReadableStream` reader), per this Next.js version's own recommended verification method.

**F-207 (added):**
- `lib/widgets-store.js` — in-memory data source shared across requests within this one server process.
- `app/api/widgets/route.js` — `GET` (list, uncached) + `POST` (create, real 400 validation, 201 with `Location` header).
- `app/api/widgets/[id]/route.js` — `GET`/`PATCH`/`DELETE` on a dynamic segment, real 200/404/204 outcomes.
- `app/api/widgets/cached-count/route.js` — `export const dynamic = 'force-static'`, the real build-time-freeze demo.
- `app/api/uuid-proxy/route.js` — a real Backend-for-Frontend proxy, server-side call to `httpbin.org/uuid`, reshaped before returning.
- `app/api/echo/route.js` — `NextRequest.nextUrl` search params + header access.
- `app/api-demo/page.js` + `app/api-demo/WidgetsClient.js` — a real Client Component driving all of the above via browser `fetch()`.

**F-208 (added):**
- `proxy.js` (project root) — the current, real `proxy.js`/`proxy` file convention (NOT `middleware.js`), with a header injection, a real redirect (`/legacy-about` → `/about`), a cookie-gated auth check on `/dashboard`, and a `matcher` excluding static assets.

**F-209 (added):**
- `app/layout.js` — `metadataBase`, `title.default`/`title.template`.
- `app/about/page.js` — a plain `title` string (proves template application) + a relative OG image (proves `metadataBase` resolution).
- `app/products/[id]/page.js` — a real, artificially slow (1200ms) `generateMetadata`, `title.absolute`, `force-dynamic`.
- `app/robots.js` + `app/sitemap.js` — real, code-generated `robots.txt`/`sitemap.xml`.

**F-210 (added):**
- `scripts/make-png.mjs` — a real, dependency-free PNG generator (Node's built-in `zlib` only) producing the two real, correctly-dimensioned demo images.
- `app/images/profile.png` (400×300, static import) + `public/hero.png` (1200×630, local path) — real, generated images.
- `app/media-optimization/page.js` — static-import, local-path, and remote `next/image` sources.
- `app/layout.js` — `next/font/google` (`Geist`) self-hosting demo.
- `next.config.mjs` — real `images.remotePatterns`/`images.qualities` configuration, added after this chapter's own real "before" failures were captured.

**F-211 (added):**
- `lib/session.js` — real `jose`-based JWT sign/verify, `httpOnly` cookie creation/deletion.
- `lib/dal.js` — `getSession()`/`verifySession()`, `cache()`-wrapped.
- `app/actions/auth.js` — real `login`/`logout` Server Actions.
- `app/login/page.js` + `LoginForm.js` — a real, working login form.
- `app/dashboard/page.js` — upgraded to a real, DAL-protected Server Component (was a static placeholder since F-202).
- `proxy.js` — upgraded from F-208's naive `.has('session')` check to real JWT verification.
- `app/account/page.js` + `unauthorized.js` — the real, three-way-tested `unauthorized()`/`authInterrupts` demo.
- `app/api/profile/route.js` — a real, DAL-protected Route Handler.
- `scripts/gen-session-token.mjs` — mints a real, valid (and tamperable) token for reproducible curl testing.
- `next.config.mjs` — added `experimental.authInterrupts: true`.

**F-212 (added):**
- `lib/notes-store.js` — a real, disk-backed JSON file store (`data/notes.json`, gitignored, re-seeded on first read if absent) — survives a server restart, unlike F-207's in-memory `widgets-store.js`.
- `app/notes/actions.js` — real `addNote`/`deleteNote` Server Actions, DAL-protected, `revalidatePath`-driven, no Route Handler involved.
- `app/notes/page.js` — a DAL-protected Server Component reading the store directly (no `fetch()`).
- `app/notes/NotesClient.js` — `useActionState` + `useOptimistic`, a bound per-item delete action.
- `app/components/SubmitButton.js` — a real, reusable `useFormStatus` consumer.

**F-213 (added):**
- `next.config.mjs` — added `output: "standalone"`.
- `app/deploy-test/page.js` — a real, inline, closure-capturing Server Action, deliberately contrasted against F-212's top-level `deleteNote`.
- `Dockerfile` — a real, standard multi-stage build (deps → builder → runner) targeting the standalone output.
- `.dockerignore` — excludes `node_modules`, `.next`, `data` from the build context.

**F-214 (added):**
- `app/api/backend-proxy/route.js` — the real BFF Route Handler: checks F-211's DAL session, then calls the separate Spring backend server-to-server with a shared secret the browser never sees.
- `.env.local` — added `INTERNAL_API_KEY` (matches the Spring backend's own expected value).
- A real, separate Spring Boot backend: [`practice/java/full-stack-integration-backend/`](../../java/full-stack-integration-backend/) (its own README, its own captured evidence).

**F-301 (added):**
- `lib/f301-math-utils.js` — the same real tree-shaking test as the sibling `build-tooling-comparison` app.
- `app/components/F301LazyPanel.js` + `app/components/F301Demo.js` — a real `next/dynamic` code-splitting test.
- `app/build-tooling-demo/page.js` — renders the demo.
- A new sibling app: [`practice/frontend/build-tooling-comparison/`](../build-tooling-comparison/) (a real, minimal Vite + React app, its own README, its own captured evidence).

## Captured evidence (real browser session + real build output)

### File-based routing: a real route manifest, zero router config

```
$ npm run build   # captured after F-202's routes were added too
Route (app)
┌ ○ /
├ ○ /_not-found
├ ○ /about
├ ƒ /blog/[slug]
├ ○ /dashboard
├ ○ /dashboard/settings
└ ○ /pricing

○  (Static)   prerendered as static content
ƒ  (Dynamic)  server-rendered on demand
```

Every route in this table exists because of a `page.js` file's LOCATION in the `app/` directory — nothing in this project imports or configures a router. `/about` was created entirely by adding `app/about/page.js`; `/blog/[slug]` was created entirely by adding `app/blog/[slug]/page.js`, and that ONE file serves every `/blog/<anything>` URL. Note `/pricing` — NOT `/marketing/pricing` — despite living on disk at `app/(marketing)/pricing/page.js`; see the route-group evidence below.

### Layouts persist across navigation — a real, measured mount counter

`PersistentHeader.js` lives in the root layout and increments a `useRef` counter once per real mount (StrictMode double-invokes in dev, so a genuine single mount shows as 2). Real captured sequence, navigating Home → About → Blog via real `<Link>` clicks:

```
Home:               Layout mount count: 2 | Route: /
click "About" ->     Layout mount count: 2 | Route: /about        <- unchanged
click "Blog: hello-world" -> Layout mount count: 2 | Route: /blog/hello-world | params.slug = "hello-world"
```

The mount counter never moved past its initial value while the page content changed on every navigation — direct, measured proof of the App Router's documented guarantee that layouts "preserve state, remain interactive, and do not rerender" on navigation between routes that share them.

### The same navigation was genuinely client-side, not a hard reload

```
performance.getEntriesByType('navigation').length === 1
```

After three `<Link>`-driven page transitions (Home → About → Blog), the browser's Navigation Timing API reported exactly ONE navigation entry for the whole session — a real hard page reload would add a new entry each time. Combined with the mount-counter evidence above, this confirms the route changes were real client-side transitions, not full-page loads dressed up as "SPA-like."

### The dynamic segment resolves different values from the same file

```
/blog/hello-world        -> params.slug = "hello-world"
/blog/file-based-routing -> params.slug = "file-based-routing"
```

One file, `app/blog/[slug]/page.js`, correctly served two different real URLs with two different resolved `slug` values — no per-post file or route was ever created.

## Captured evidence for F-202 (nested layouts, route groups)

### A nested layout persists within its own subtree — both levels measured together

Real captured sequence, navigating `/dashboard` → `/dashboard/settings` via real `<Link>` clicks:

```
/dashboard:          Layout mount count: 2 | DashboardLayout mount count: 2 | Route: /dashboard
/dashboard/settings:  Layout mount count: 2 | DashboardLayout mount count: 2 | Route: /dashboard/settings   <- both unchanged
```

Both the ROOT layout's counter and the NESTED `DashboardLayout`'s counter stayed unchanged while navigating between two routes three levels deep in the tree — nested layouts get the exact same persistence guarantee as the root layout, at whatever level they're declared.

### A nested layout unmounts once its ancestry no longer applies

Real captured sequence, navigating `/dashboard/settings` → `/about` (a route that does NOT live under `app/dashboard/`):

```
At /dashboard/settings: document.querySelector('[data-testid="dashboard-layout"]') exists
At /about:               document.querySelector('[data-testid="dashboard-layout"]') === null
```

The `DashboardLayout` DOM node itself disappeared entirely — not just "stopped updating," genuinely unmounted — confirming layout persistence is SCOPED to the subtree that actually renders through that layout, not a global guarantee. Navigating back to `/dashboard` afterward showed `DashboardLayout` present again with a fresh mount count, confirming a real mount/unmount/remount cycle, not a hidden or cached state.

### A route group's folder name is stripped from the URL — proven live and in the build manifest

Real captured value after clicking into the Pricing page:

```
window.location.pathname === "/pricing"
```

`app/(marketing)/pricing/page.js` lives on disk inside a `(marketing)` folder, but the real, live URL is `/pricing` — confirmed both by the browser's own `window.location.pathname` and by the `next build` route manifest above, which lists `/pricing` with no `/marketing` segment anywhere. `MarketingLayout` (the route group's own scoped layout) mounted correctly for this route, exactly like `DashboardLayout` does for its own group.

## Captured evidence for F-203 (Server vs. Client Components)

### A server-only secret's CODE never reaches the client bundle — but its rendered OUTPUT does

`ServerSecretDemo.js` (a Server Component) reads `process.env.SERVER_SECRET_DEMO` and renders it directly. Real captured grep results against the actual `next build` output:

```
$ grep -c "SUPER_SECRET_VALUE_9f8e7d6c" .next/server/app/server-vs-client.html
1

$ grep -rl "SUPER_SECRET_VALUE_9f8e7d6c" .next/static/ | wc -l
0    (0 matches across all 15 files under .next/static — every file a browser could ever fetch)
```

The secret's VALUE appears once, in the prerendered HTML this page actually sends to a browser — expected, since the Server Component is explicitly rendering it. But the secret string appears in ZERO of the files under `.next/static`, the directory containing every JS chunk a browser can ever request. This is the precise, real distinction: a Server Component's CODE (and any literal it references) never ships as JavaScript to the client, but its RENDERED OUTPUT does become part of the page — "server-only" describes where the code runs, not whether its output reaches the browser.

### A Server Component using a hook is a real, caught build error

`ServerSecretDemo.js` was edited to add `import { useState } from "react"` and call it, with no `"use client"` directive. Real captured `next build` output:

```
Error: You're importing a module that depends on `useState` into a React Server Component module. This API is only available in Client Components. To fix, mark the file (or its parent) with the `"use client"` directive.
  Learn more: https://nextjs.org/docs/app/api-reference/directives/use-client

Import trace:
  Server Component:
    ./app/components/ServerSecretDemo.js
    ./app/server-vs-client/page.js
```

Reverted immediately after capturing this. The boundary isn't a convention — it's enforced by the build.

### An async Client Component: a real, version-specific finding

This Next.js version (16.3.1) shipped after this assistant's training cutoff. Based on older React docs, marking a Client Component's function `async` was expected to be a `next build`-time error. Real result: `npm run build` **succeeded** with `ClientCounter` marked `async function ClientCounter()`. The actual restriction still exists, but is enforced at RUNTIME, not build time, in this version — a real browser session against the dev server captured:

```
Console error: <ClientCounter> is an async Client Component. Only Server Components can be
async at the moment. This error is often caused by accidentally adding "use client" to a
module that was originally written for the server.
```

Clicking the (visually rendered but broken) counter button triggered Next.js's real dev error overlay, pinpointing the exact `<ClientCounter />` line in `app/server-vs-client/page.js`. Reverted `ClientCounter` back to a synchronous function afterward; re-verified interactivity on a clean `next build` + `next start` production server (not dev mode, to rule out any HMR-related noise): clicking "+1" moved `Client count: 0` to `Client count: 1`, a genuine, working click-to-re-render cycle.

## Captured evidence for F-204 (data fetching and caching)

All of this section's evidence required `next build && next start` — this Next.js version's own docs state plainly: **"In Development, Pages are always rendered on-demand and are never cached."** Testing any of these caching claims against `next dev` would silently show nothing meaningful.

### A real build-time discovery: `force-cache` fetches are attempted AT BUILD TIME

The first build attempt used this app's own `/api/time` Route Handler as the fetch target for the `force-cache` demo. Real captured failure:

```
Error occurred prerendering page "/data-fetching/force-cache".
[TypeError: fetch failed] { code: 'ECONNREFUSED', ... }
Export encountered an error on /data-fetching/force-cache/page: /data-fetching/force-cache, exiting the build.
```

`cache: 'force-cache'` makes the route eligible for static generation, so Next genuinely attempts that fetch DURING `next build` — before this app's own server is running, a real chicken-and-egg failure. Fixed by switching all four data-fetching demos to a real external endpoint (`https://httpbin.org/uuid`, reachable during the build) instead of a same-server API route; the app's own `/api/time` route was removed as unnecessary once this fix was in place.

### A real, decisive route manifest — with Revalidate/Expire columns

```
Route (app)                     Revalidate  Expire
├ ○ /data-fetching/default
├ ○ /data-fetching/force-cache
├ ƒ /data-fetching/no-store
├ ○ /data-fetching/revalidate           5s      1y
```

Note `default` is `○` (Static) — NOT `ƒ` (Dynamic) — despite this version's docs stating "fetch requests are not cached by default." This is the real, nuanced finding this chapter is built around; see the next section for the direct proof of what that actually means in practice.

### The real, nuanced finding: "fetch is uncached by default" ≠ "the route is uncached by default"

Real captured `curl` results against a clean `next start` production server, two requests per page:

```
default (no cache option):  450dc1ca-...  →  450dc1ca-...   SAME uuid both times
no-store (explicit):        d327b050-...  →  1120d331-...   DIFFERENT uuid every time
force-cache:                40ad35de-...  →  40ad35de-...   SAME uuid both times
```

`default` behaved EXACTLY like `force-cache`, not like `no-store` — a real, direct contradiction of a naive reading of "fetch requests are not cached by default." The precise, correct claim (confirmed by the route manifest's `○`/`ƒ` markers above): `fetch()`'s OWN default (no `cache` option) doesn't request HTTP-level caching, but if NOTHING else in the route forces dynamic rendering (no `cookies()`, no `headers()`, no explicit `cache: 'no-store'`), Next's separate ROUTE-level Full Route Cache still statically renders the page once and serves that same result to everyone — a different caching LAYER than `fetch`'s own option. Only an explicit `cache: 'no-store'` (or another Request-time API) reliably opts the whole route into per-request dynamic rendering.

### Real, timed proof of `next: { revalidate: 5 }`

Real captured `curl` results, precise real timestamps:

```
t=0s:  937f667a-...
t=1s:  937f667a-...   (unchanged — within the 5s window)
t=2s:  937f667a-...   (unchanged — within the 5s window)
[sleep 7 real seconds]
t=9s:  8f715caf-...   (changed — window expired)
t=9s:  8f715caf-...   (unchanged again — freshly re-cached)
```

The revalidation window held exactly as documented: stable for 2 real seconds within the window, changed once 7 real seconds had elapsed (crossing the 5-second boundary), then stable again immediately afterward.

### Real, on-demand proof of `revalidateTag`

Real captured sequence: the `force-cache` page held a stable uuid (`40ad35de-...`) across repeated checks. A real click on the live `RevalidateButton` (a Client Component invoking a real Server Action, `revalidateTimeTag()`, which calls `revalidateTag('uuid-tag')`) was performed in a real browser session — no timer, no wait:

```
Before click:  40ad35de-...
After click:   28f09c9d-...   (changed immediately)
Re-check:      28f09c9d-...   (stable again — freshly re-cached)
```

On-demand revalidation via `revalidateTag` genuinely bypasses the time-based window entirely — the cached value changed the instant the Server Action ran, not on the next scheduled revalidation.

## Captured evidence for F-205 (rendering strategies)

### A third real build-manifest marker: `●` (SSG) distinct from `○` (Static) and `ƒ` (Dynamic)

```
Route (app)                         Revalidate  Expire
├   /rendering-strategies/ssg/[id]
│ ├ ● /rendering-strategies/ssg/1
│ └ ● /rendering-strategies/ssg/2
├ ƒ /rendering-strategies/ssr
...
○  (Static)   prerendered as static content
●  (SSG)      prerendered as static HTML (uses generateStaticParams)
ƒ  (Dynamic)  server-rendered on demand
```

The real build output itself distinguishes SSG (routes generated via `generateStaticParams`) from plain static prerendering — a third category this chapter's own evidence surfaced, not something asserted from documentation.

### SSR: real proof the page re-renders fresh per request

Real captured `curl` results against a clean `next start` production server, two requests with different `User-Agent` headers:

```
Request 1 (User-Agent: FakeBrowserOne/1.0):  rendered "FakeBrowserOne/1.0"
Request 2 (User-Agent: FakeBrowserTwo/2.0):  rendered "FakeBrowserTwo/2.0"
```

The page rendered the ACTUAL header value sent with each individual request — direct proof this route is genuinely re-executed server-side per request (SSR), not served from any prerendered shell, exactly because `headers()` is a Request-time API.

### SSG: real proof of build-time-fixed content, plus real on-demand fallback for unlisted params

Real captured `curl` results, extracting the rendered build/generation timestamp:

```
/rendering-strategies/ssg/1 (listed in generateStaticParams), two requests:
  2026-08-18T18:46:17.287Z
  2026-08-18T18:46:17.287Z          <- identical, fixed at build time

/rendering-strategies/ssg/999 (NOT listed), two requests:
  2026-08-18T18:46:57.042Z
  2026-08-18T18:46:57.042Z          <- identical to EACH OTHER, but different
                                        from id=1's timestamp
```

`id=1` (explicitly returned by `generateStaticParams`) shows the SAME timestamp across requests — real proof it was rendered exactly once, at build time, and served as fixed static HTML ever since. `id=999` (never listed) shows a DIFFERENT timestamp from `id=1` (generated later, on its own first real request — `dynamicParams` defaults to `true`) but an IDENTICAL timestamp across its own two requests — real proof it was generated once, on demand, then cached for subsequent requests, exactly the ISR-style fallback behavior for unlisted dynamic params.

## Captured evidence for F-206 (streaming and Suspense)

### Sibling Suspense boundaries resolve independently — real chunk timestamps

`app/streaming/sibling-boundaries/page.js` wraps three async components (300ms, 1200ms, 2500ms artificial delays) in three separate `<Suspense>` boundaries. Real captured output from `node scripts/stream-observer.mjs http://localhost:5198/streaming/sibling-boundaries` against a clean `next start` server:

```
chunk 0 (+71ms)   bytes=8850  markers=["layout-mount-count","fast-fallback","medium-fallback","slow-fallback"]
chunk 2 (+352ms)  bytes=950   markers=["fast-widget"]
chunk 4 (+1252ms) bytes=126   markers=["medium-widget"]
chunk 6 (+2552ms) bytes=136   markers=["slow-widget"]
stream done at +2553ms
```

The static shell (all three fallbacks) arrived in the FIRST chunk, at 71ms. Each widget's real HTML then arrived as its OWN separate chunk, at a timestamp matching its own artificial delay almost exactly (352ms ≈ 300ms delay + startup, 1252ms ≈ 1200ms delay + startup, 2552ms ≈ 2500ms delay + startup) — direct proof each boundary streams independently. Total stream time (2553ms) is close to the SLOWEST widget's delay alone, not the SUM of all three (which would be ~4000ms) — proof the three async operations ran in parallel, not sequentially.

### Page-level streaming with `loading.js` — real proof

`app/streaming/full-page/page.js` has a sibling `loading.js`, with no explicit `<Suspense>` written in the page itself. Real captured output:

```
chunk 0 (+44ms)   bytes=8286  markers=["layout-mount-count","full-page-loading"]
chunk 2 (+1542ms) bytes=1263  markers=["full-page-content"]
stream done at +1543ms
```

The `loading.js` fallback (`full-page-loading`) arrived instantly at 44ms; the real page content (`full-page-content`) streamed in at 1542ms, matching its 1500ms artificial delay — confirming `loading.js`'s mere presence genuinely wraps the whole page in a real `<Suspense>` boundary, with no explicit import needed in the page component.

### A real, unexpected finding: a bot User-Agent did NOT block streaming for this page

Based on a surface reading of "Next.js detects [bots] and waits for `generateMetadata` to resolve before streaming," a bot request was expected to block until the full page finished, then arrive in one chunk. Real captured result, same sibling-boundaries page, with `User-Agent: Twitterbot/1.0`:

```
chunk 0 (+42ms)   markers=["fast-fallback","medium-fallback","slow-fallback"]
chunk 2 (+340ms)  markers=["fast-widget"]
chunk 4 (+1240ms) markers=["medium-widget"]
chunk 6 (+2541ms) markers=["slow-widget"]
```

Essentially IDENTICAL staggered timing to the non-bot request — the content streamed normally, contradicting the naive "bots always get one blocking response" reading. Re-reading the docs precisely resolves this: the blocking behavior is scoped specifically to `generateMetadata` resolution, not general Suspense/streaming content — this app's pages use only the root layout's static, synchronous metadata, so there was nothing for the bot-detection path to actually block on. A genuine, verified correction of an easy-to-overread claim, not a contradiction of the documented behavior once read precisely.

## Captured evidence for F-207 (Route Handlers)

### Route manifest: Route Handlers not cached by default, one explicit opt-in

Real `npm run build` output, re-run after F-207's `app/api/*` routes were added:

```
├ ƒ /api/echo
├ ƒ /api/uuid-proxy
├ ƒ /api/widgets
├ ƒ /api/widgets/[id]
├ ○ /api/widgets/cached-count
```

Every handler shows `ƒ` (Dynamic — not cached) except `cached-count`, which carries `export const dynamic = 'force-static'` and shows `○` — the SAME marker this app's pages use for static rendering, confirming Route Handlers share one static/dynamic classification system with pages, not a separate one.

### A real, live proof that a cached Route Handler freezes at build time

Real `curl` sequence against a clean `next start` server:

```
$ curl http://localhost:5198/api/widgets/cached-count
{"count":2,"note":"captured at build time"}

$ curl -X POST http://localhost:5198/api/widgets -H "Content-Type: application/json" -d '{"name":"Screwdriver","qty":8}'
{"id":"3","name":"Screwdriver","qty":8}      # 201, Location: /api/widgets/3

$ curl http://localhost:5198/api/widgets
[{"id":"1","name":"Wrench","qty":12},{"id":"2","name":"Hammer","qty":5},{"id":"3","name":"Screwdriver","qty":8}]

$ curl http://localhost:5198/api/widgets/cached-count
{"count":2,"note":"captured at build time"}   # STILL 2 -- frozen at build time, ignoring the mutation above
```

### Full real CRUD cycle over the `[id]` dynamic segment

```
GET  /api/widgets/1    -> 200 {"id":"1","name":"Wrench","qty":12}
GET  /api/widgets/999  -> 404 {"error":"No widget with id 999"}
PATCH /api/widgets/1   -> 200 {"id":"1","name":"Wrench","qty":99}   (body: {"qty":99})
DELETE /api/widgets/2  -> 204 (empty body)
POST /api/widgets      -> 400 {"error":"Body must include a non-empty string 'name' and a numeric 'qty'"}   (body: {"qty":3}, missing name)
```

### Automatic `405` and `OPTIONS` — no code written for either

```
$ curl -i -X PUT http://localhost:5198/api/widgets
HTTP/1.1 405 Method Not Allowed

$ curl -i -X OPTIONS http://localhost:5198/api/widgets
HTTP/1.1 204 No Content
allow: GET, HEAD, OPTIONS, POST
```

`app/api/widgets/route.js` exports only `GET` and `POST` — the `405` for `PUT` and the accurate `Allow` header for `OPTIONS` are both entirely framework-generated.

### Backend-for-Frontend proxy: two real, genuinely different external calls

```
$ curl http://localhost:5198/api/uuid-proxy
{"correlationId":"a530720b-02f1-45a9-808e-fe78b3ce81ac","source":"httpbin.org/uuid","reshapedBy":"app/api/uuid-proxy/route.js"}
```

A second call, this time triggered by a real button click in the live `/api-demo` browser session (not curl), returned a genuinely different `correlationId` (`6602717a-107e-4d7b-adeb-89cb9e653dc9`) — confirming a fresh real server-side call to `httpbin.org` on each client-initiated request, matching the handler's explicit `cache: 'no-store'`.

### Real browser session: a Client Component driving Route Handlers end to end

Live session at `/api-demo`: submitted the add-widget form (name "Pliers", qty 4) — a real `POST /api/widgets` from the browser — and confirmed the new widget appeared in the list (live count 3) while `cached-count` stayed at its frozen build-time value of 2, matching the curl evidence above exactly.

## Captured evidence for F-208 (Proxy, formerly Middleware, & the Edge Runtime)

### "Middleware" is deprecated naming — the real build output leads with "Proxy"

Real `npm run build` output with `proxy.js` present at the project root:

```
ƒ Proxy (Middleware)
```

The build tool's own summary names it "Proxy" first, keeping "(Middleware)" only as a parenthetical for the deprecated term.

### Three real Proxy behaviors, proven with curl against a clean `next start` server

```
$ curl -i http://localhost:5198/ | grep -i "x-proxy-hit\|HTTP/1.1"
HTTP/1.1 200 OK
x-proxy-hit: true

$ curl -i http://localhost:5198/legacy-about | grep -i "HTTP/1.1\|location"
HTTP/1.1 307 Temporary Redirect
location: /about

$ curl -i http://localhost:5198/dashboard | grep -i "HTTP/1.1\|location"
HTTP/1.1 307 Temporary Redirect
location: /

$ curl -i -b "session=abc" http://localhost:5198/dashboard | grep -i "HTTP/1.1\|x-proxy-hit"
HTTP/1.1 200 OK
x-proxy-hit: true
```

Notably, `/` is a fully static, prerendered page (confirmed `x-nextjs-cache: HIT` on the same response) and STILL carried `x-proxy-hit` — real proof Proxy runs before even a cached, static route is served.

### A real, verified `matcher` exclusion

```
$ curl -i http://localhost:5198/favicon.ico | grep -i "x-proxy-hit"
(no output -- header genuinely absent)
```

Every other route tested carried `x-proxy-hit`; `favicon.ico`, matched against the `matcher`'s negative-lookahead pattern, did not — real, direct proof the exclusion works, not just a description of the config's intent.

### The central finding: two real, deliberately contrasted build outputs for the Edge Runtime

Attempt 1 — `export const runtime = "edge";` added to `proxy.js`:

```
> Build error occurred
Error: Route segment config is not allowed in Proxy file at "./proxy.js". Proxy always runs on Node.js runtime. Learn more: https://nextjs.org/docs/messages/middleware-to-proxy
```

A real, hard, named build FAILURE. Reverted immediately after capture.

Attempt 2 — the SAME line added to `app/api/echo/route.js` (an ordinary Route Handler, not Proxy):

```
⚠ The Edge Runtime is deprecated. You can use the "nodejs" runtime instead. Learn more: https://nextjs.org/docs/messages/edge-runtime-deprecated
⚠ Using edge runtime on a page currently disables static generation for that page
✓ Generating static pages using 9 workers (20/20) in 656ms
```

Two real WARNINGS only — the build succeeded, and the route still worked. Reverted immediately after capture. This precise contrast (hard error in Proxy specifically vs. a tolerated-but-warned-against option everywhere else) is the chapter's central, verified finding.

## Captured evidence for F-209 (Metadata API & SEO)

### `title` merging: three real, distinct rendered outcomes

```
$ curl -s http://localhost:5198/ | grep -o "<title>[^<]*</title>"
<title>React + Next.js Fundamentals — F-201</title>

$ curl -s http://localhost:5198/about | grep -o "<title>[^<]*</title>"
<title>About | Next.js Fundamentals Demo</title>

$ curl -s http://localhost:5198/products/1 | grep -o "<title>[^<]*</title>"
<title>Product 1 (absolute, no template)</title>
```

Home (no `title` of its own) gets `title.default` verbatim; About (`title: "About"`) gets the root layout's `title.template` applied; the product page (`title: { absolute: ... }`) bypasses the template entirely.

### `metadataBase`: a real, correct resolution

```
$ curl -s http://localhost:5198/about | grep -o "<meta property=\"og:image\"[^>]*>"
<meta property="og:image" content="http://localhost:5198/og/about.png"/>
```

### The unplanned finding: removing `metadataBase` is a real WARNING, not a build error — with a real, wrong fallback

With `metadataBase` temporarily removed from `app/layout.js`:

```
$ npm run build
...
⚠ metadataBase property in metadata export is not set for resolving social open graph or twitter images, using "http://localhost:3000". See https://nextjs.org/docs/app/api-reference/functions/generate-metadata#metadatabase
✓ Compiled successfully
```

The build SUCCEEDED. The About page's actual baked static HTML:

```
$ grep -o "<meta property=\"og:image\"[^>]*>" .next/server/app/about.html
<meta property="og:image" content="http://localhost:3000/og/about.png"/>
```

`localhost:3000` — the WRONG port for this app (which runs on 5198) — silently baked into production HTML, with no build failure. `metadataBase` restored and re-verified immediately after capture (see the correct-resolution output above).

### The central finding: two real, contrasted chunk-timing traces, completing F-206's earlier open thread

`app/products/[id]/page.js` has a real, artificial 1200ms delay inside `generateMetadata`. Real `node scripts/stream-observer.mjs` output against a clean `next start` server:

```
$ node scripts/stream-observer.mjs http://localhost:5198/products/2 "Mozilla/5.0 NormalBrowser/1.0"
fetch() returned headers at +43ms
chunk 0 (+44ms) bytes=7821 markers=["layout-mount-count","product-body"]
chunk 1 (+1243ms) bytes=1275 markers=[]
chunk 2 (+1244ms) bytes=1923 markers=[]
stream done at +1244ms

$ node scripts/stream-observer.mjs http://localhost:5198/products/2 "Twitterbot/1.0"
fetch() returned headers at +1246ms
chunk 0 (+1247ms) bytes=9483 markers=["layout-mount-count","product-body"]
stream done at +1247ms
```

The normal request's page content streamed at +44ms, well before the 1200ms metadata delay resolved (the metadata-bearing chunks followed at +1243ms). The bot request didn't even receive response HEADERS until +1246ms — the entire response, content included, was held back until `generateMetadata` finished. This is the real, decisive test F-206 could only gesture at with static metadata.

### `robots.js`/`sitemap.js`: real, generated output

```
$ curl -s http://localhost:5198/robots.txt
User-Agent: *
Allow: /
Disallow: /dashboard/

Sitemap: http://localhost:5198/sitemap.xml

$ curl -s http://localhost:5198/sitemap.xml
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
<url>
<loc>http://localhost:5198/</loc>
<changefreq>monthly</changefreq>
<priority>1</priority>
</url>
...
```

Both routes showed `○` (Static) in the real build manifest, confirming this version's own docs' claim that they're a special, cached-by-default case of Route Handlers (F-207).

## Captured evidence for F-210 (Image/Font Optimization & Core Web Vitals)

### Three real image sources, real rendered `<img>` output

```
$ curl -s http://localhost:5198/media-optimization | grep -o "<img[^>]*>"
<img alt="Static-imported profile" width="400" height="300" ... />
<img alt="Local hero" loading="lazy" width="600" height="315" ... />
<img alt="Remote unconfigured" loading="lazy" width="300" height="200" ... />
```

The static import got its `width="400" height="300"` read directly from the real, generated PNG — no props needed. The other two required explicit dimensions.

### `remotePatterns`/`qualities`: real, exact 400s BEFORE configuration

```
$ curl -i "http://localhost:5198/_next/image?url=%2Fhero.png&w=1200&q=90"
HTTP/1.1 400 Bad Request
"q" parameter (quality) of 90 is not allowed

$ curl -i "http://localhost:5198/_next/image?url=https%3A%2F%2Fhttpbin.org%2Fimage%2Fjpeg&w=640&q=75"
HTTP/1.1 400 Bad Request
"url" parameter is not allowed
```

Note the `<Image quality={90}>` component itself, rendered on the same page BEFORE this fix, showed `q=75` in its actual `src`/`srcSet` — silently clamped, never even attempting the disallowed value.

### The same two requests, AFTER adding `images.remotePatterns`/`images.qualities`

```
$ curl -o /dev/null -w "HTTP %{http_code}\n" "http://localhost:5198/_next/image?url=%2Fhero.png&w=1200&q=90"
HTTP 200

$ curl -o /dev/null -w "HTTP %{http_code} content-type=%{content_type}\n" "http://localhost:5198/_next/image?url=https%3A%2F%2Fhttpbin.org%2Fimage%2Fjpeg&w=640&q=75"
HTTP 200 content-type=image/jpeg
```

And the rendered `<Image quality={90}>` now genuinely shows `q=90` in its `src` — no more clamping, since `90` is now allowlisted.

### `priority` vs. `preload`: a real, confirmed silent deprecation

With the demo page's `preload` prop temporarily swapped for the deprecated `priority` prop: a real `next dev` browser console (`read_console_messages`) showed no mention of "priority" or "deprecated" anywhere; a full real `next build` likewise produced zero matching output. The prop still fully worked — a real `<link rel="preload" as="image">` was confirmed present via a direct DOM query. Reverted to `preload` immediately after capture.

### `next/font`: a real, zero-Google-requests network trace

A real `read_network_requests` call filtered to `google` against the live, production `/media-optimization` page returned **no matches**. The actual font file request:

```
GET http://localhost:5198/_next/static/media/caa3a2e1cccd8315-s.p.0wgildi0cnwt9.woff2 → 200 OK
```

Same-origin, not `fonts.googleapis.com` or `fonts.gstatic.com`.

## Captured evidence for F-211 (Authentication Patterns)

### A real, working login/logout cycle in a live browser

Submitted the real `/login` form (demo credentials `demo`/`correct-horse`, pre-filled) — genuinely redirected to `/dashboard`, rendering `Signed in as: user-42`. Clicking the real `Log out` button genuinely redirected to `/`, and `/dashboard` was inaccessible again afterward (real redirect back to `/`).

### `httpOnly` proven directly — not just asserted

In the SAME authenticated browser session:

```js
document.cookie
// => ""
```

Empty, despite an active, real, server-verified session (the dashboard rendered signed-in content). Real, direct proof the `httpOnly` flag works.

### The central finding: a real, reproduced Proxy-vs-DAL bypass

A real, tamperable token minted via `scripts/gen-session-token.mjs` (same secret/algorithm as `lib/session.js`), with its last character flipped:

```
$ curl -o /dev/null -w "HTTP %{http_code}\n" -b "session=$TAMPERED" http://localhost:5198/dashboard
```

**With the upgraded Proxy (real JWT verification):** `HTTP 307`, `location: /` — rejected by Proxy itself.

**With Proxy reverted, live, to F-208's original naive `.has('session')` check, rebuilt:**

```
HTTP/1.1 307 Temporary Redirect
x-proxy-hit: true
location: /login
```

`x-proxy-hit: true` proves Proxy let the tampered token through — the redirect (to `/login`, NOT Proxy's own `/`) came from the DASHBOARD PAGE's own `verifySession()` DAL call instead. Proxy restored to the upgraded, real-verification version immediately after capture.

### `unauthorized()`: three real, distinct outcomes

**No `authInterrupts` flag:** `/account` without a session cookie returns a real `200`, a generic React error digest, no `noindex` tag, and the custom `unauthorized.js` UI never renders (stuck on the Suspense fallback).

**Flag enabled, Suspense-wrapped (`/account`):**

```
$ curl -s http://localhost:5198/account | grep -o "noindex\|NEXT_HTTP_ERROR_FALLBACK;401"
NEXT_HTTP_ERROR_FALLBACK;401
noindex
$ curl -o /dev/null -w "HTTP %{http_code}\n" http://localhost:5198/account
HTTP 200
```

Real digest, real `noindex` meta tag, real custom UI in the payload — but the real HTTP status stays `200` (streaming had already started).

**Flag enabled, Route Handler (`/api/profile`, not streamed):**

```
$ curl -o /dev/null -w "HTTP %{http_code}\n" http://localhost:5198/api/profile
HTTP 401
```

A genuine real `401`.

### DAL-protected surfaces, all real

```
$ curl -b "session=$TOKEN" http://localhost:5198/api/profile
{"userId":"user-42"}
```

Real `200` with real session data for a valid token; real `401` with none.

## Captured evidence for F-212 (Server Actions and mutations)

### The single-roundtrip response model — a real captured network trace

Real `read_network_requests` output after clicking the live "Add note" button once (JavaScript enabled):

```
GET  http://localhost:5198/notes → 200 OK   (page load)
GET  http://localhost:5198/notes → 200 OK   (page load, after login redirect)
POST http://localhost:5198/notes → 200 OK   (the ONE mutation request -- no follow-up GET)
```

The real response body for that single POST contained BOTH the action's own return value:

```
1:{"ok":true}
```

AND a freshly re-rendered RSC tree for the SAME response, already showing the new note:

```
...{"notes":[{"id":"2","text":"Second note (seeded)","authorId":"user-42"},
{"id":"1787146571567","text":"Browser-typed note for F-212","authorId":"user-42"}],"userId":"user-42"}...
```

No client-side refetch was ever written for this to work — React commits the second payload as a seeded navigation.

### Render-time gating is not authorization — a real, disk-verified bypass

With `addNote`'s own `if (!session?.userId) return {error: ...}` check ACTIVE, an anonymous curl POST (no session cookie, built from the real hidden-field format a bound action reference renders) got a real `307` to `/login`, and `data/notes.json` was confirmed unchanged.

With that check TEMPORARILY REMOVED (restored immediately after capture):

```
$ curl -s -i -F '$ACTION_REF_5=' -F '$ACTION_5:0={"id":"...","bound":"$@1"}' \
       -F '$ACTION_5:1=[{"error":null,"ok":false}]' \
       -F 'text=REAL BYPASS -- written with no session' \
       http://localhost:5198/notes
HTTP/1.1 307 Temporary Redirect
```

Same response shape as a normal rejection — but `data/notes.json`, read directly, gained a real new row:

```json
{ "id": "1787146688158", "text": "REAL BYPASS -- written with no session", "authorId": "anonymous-bypass-test" }
```

The write happens inside `addNote` itself; the `307` comes from a SEPARATE, later step — the page's own `verifySession()` running as part of the mandatory post-action re-render. The write already committed before that redirect fired. Both files restored immediately after capture; the bypass-test row removed from `data/notes.json` afterward.

### The framework's own CSRF protection — a real captured rejection, with the exact log line

```
$ curl -s -i -H "Origin: https://evil.example.com" \
       -F '$ACTION_REF_1=' -F '$ACTION_1:0={"id":"...","bound":"$@1"}' -F '$ACTION_1:1=["2"]' \
       -b "session=$TOKEN" http://localhost:5198/notes
HTTP/1.1 500 Internal Server Error
```

Real `next dev` server log, same request:

```
`x-forwarded-host` header with value `localhost:5198` does not match `origin` header with value `evil.example.com` from a forwarded Server Actions request. Aborting the action.
⨯ Error: Invalid Server Actions request.
```

The targeted note was confirmed still present afterward — the mismatched-Origin request never reached `deleteNote`'s own code at all.

### Progressive enhancement: two real forms, two real outcomes

Real rendered HTML, delete form (`deleteNote.bind(null, note.id)`, a raw bound reference):

```html
<form action="" encType="multipart/form-data" method="POST">
  <input type="hidden" name="$ACTION_REF_4"/>
  <input type="hidden" name="$ACTION_4:0" value="{&quot;id&quot;:&quot;...&quot;,&quot;bound&quot;:&quot;$@1&quot;}"/>
  <input type="hidden" name="$ACTION_4:1" value="[&quot;2&quot;]"/>
  <button type="submit">Delete</button>
</form>
```

A raw curl POST reproducing exactly those fields, with no JavaScript, no `fetch()`, no `Next-Action` header at all:

```
$ curl -s -i -b "session=$TOKEN" \
  -F '$ACTION_REF_2=' -F '$ACTION_2:0={"id":"...","bound":"$@1"}' -F '$ACTION_2:1=["1"]' \
  http://localhost:5198/notes
HTTP/1.1 200 OK
```

Note "1" was genuinely deleted — confirmed by the freshly rendered HTML in that same response showing only the remaining note.

Real rendered HTML, add-note form (wired through a `formAction` closure so `useOptimistic` can run first):

```html
<form action="javascript:throw new Error(&#x27;React form unexpectedly submitted.&#x27;)">
  <input type="text" placeholder="New note" required="" name="text"/>
  <button type="submit">Add note</button>
</form>
```

A real, dead placeholder — this form does nothing at all with JavaScript disabled, unlike the delete form above.

### `useOptimistic` + `useActionState` + `useFormStatus` — a real, captured mid-flight screenshot

Real, live browser session: submitted a new note through the closure-wrapped add form. A screenshot captured DURING the artificial 800ms delay showed all three pending signals active at once:

```
First note (seeded)             [Delete]
Second note (seeded)            [Delete]
Optimistic UI race test                       <- dimmed, no Delete button yet (useOptimistic)
[New note input, disabled]  [Working...]       <- useFormStatus's pending, on the button itself
useActionState pending: true                   <- useActionState's own separate pending signal
```

A follow-up screenshot after the delay resolved showed the optimistic item replaced by the real, server-confirmed note, now with a working `Delete` button — React correctly reconciled the optimistic patch away once the real data arrived. `data/notes.json` confirmed the real write directly.

## Captured evidence for F-213 (Deployment models: Vercel-native vs. self-hosting)

### `output: "standalone"` — a real, measured minimal footprint

```
$ du -sh node_modules .next
435M	node_modules
108M	.next

$ du -sh .next/standalone .next/standalone/node_modules
 42M	.next/standalone
 38M	.next/standalone/node_modules
```

543MB (naive `node_modules` + `.next`) versus 42MB (`.next/standalone`) — roughly a 13x reduction.

### The real, reproduced standalone gotcha

```
$ node .next/standalone/server.js &
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:5199/
200
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:5199/_next/static/chunks/2h9qyi1_ur4wz.css
404
```

After `cp -R public .next/standalone/public` and `cp -R .next/static .next/standalone/.next/static`, the SAME server (restarted) and the SAME asset request:

```
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:5199/_next/static/chunks/2h9qyi1_ur4wz.css
200
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:5199/favicon.ico
200
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:5199/notes
307
```

The last line confirms F-211/F-212's DAL and Proxy protections work identically from the minimal standalone output, not only from a full `next start`.

### Real, captured `Cache-Control` headers — three response types

```
$ curl -s -D - -o /dev/null http://localhost:5198/about | grep -i cache-control
Cache-Control: s-maxage=31536000

$ curl -s -D - -o /dev/null http://localhost:5198/rendering-strategies/ssr | grep -i cache-control
Cache-Control: private, no-cache, no-store, max-age=0, must-revalidate

$ curl -s -D - -o /dev/null http://localhost:5198/_next/static/chunks/2h9qyi1_ur4wz.css | grep -i cache-control
Cache-Control: public, max-age=31536000, immutable
```

The static page's header (`s-maxage=31536000`, no `public` keyword) does not match the self-hosting guide's own "Usage with CDNs" prose, which describes a fully static page as including `Cache-Control: public`.

### The central, decisive multi-instance Server Actions finding

`deleteNote` (top-level export, `.bind()`-only) rendered the SAME action id on two genuinely independent `next build` runs:

```
Instance A: {"id":"40e1e8888498a31341c98608873cf15f754eaa0b7a","bound":"$@1"}
Instance B: {"id":"40e1e8888498a31341c98608873cf15f754eaa0b7a","bound":"$@1"}   -- IDENTICAL
```

A raw request built from Instance A's own rendered hidden fields, sent to Instance B (a different build, no shared `NEXT_SERVER_ACTIONS_ENCRYPTION_KEY`), genuinely deleted the target note — real, cross-instance success with zero shared-key configuration.

By contrast, `app/deploy-test/page.js`'s inline `echoClosure` (closes over `instanceLabel`) rendered a visibly different, real encrypted field:

```
$ACTION_1:0 = {"id":"60cf39ebef5aa1ac0ac8f25f218bedf625f34fae76","bound":"$@1"}
$ACTION_1:1 = ["$@2"]
$ACTION_1:2 = "WcQ9oPJotCoT8ZDkHvzw/vmg5oY4k6SetzGaLK2Vf11mQp3+JDXtSOSvKBXbgkdppuzAaMU8G8sZayF1iR9bnApcyxGSbnKWfR4e5Bput/Am7mJBJJ2jUVG0Sg=="
```

`$ACTION_1:2` — entirely absent from `deleteNote`'s rendering — is the real, visible fingerprint of the encrypted-closure mechanism the framework's own multi-server guide describes. A full cross-instance failure reproduction for this closure case was attempted via `curl -F`, a hand-rolled Python multipart body, and a real browser `fetch()`/`FormData` call; none cleanly replicated the framework's own undocumented wire format for this path (each either failed to parse or silently fell through to a plain page render). The documented consequence (a shared key is required, or decryption fails) is therefore cited here as documented framework behavior, not independently reproduced in this session.

### A real, executed `docker build` / `docker run` cycle

The Docker daemon was not running when this chapter's verification began; `open -a Docker` was used to start it, and `docker ps` succeeded roughly 60 seconds later. A real `docker build` on the FIRST attempt failed pulling the base image (`DeadlineExceeded: context deadline exceeded` on `node:22-alpine`) — a real, transient network issue, not a Dockerfile problem; the retry succeeded. A SECOND real failure then hit the same `httpbin.org` outage documented in F-212 (now occurring inside the container's own build step) — worked around the same way, by temporarily moving `app/data-fetching/` out of the build context, then restoring it immediately after a successful image build:

```
$ docker build -t f213-nextjs-fundamentals .
...
#16 naming to docker.io/library/f213-nextjs-fundamentals:latest done

$ docker images | grep f213
f213-nextjs-fundamentals:latest   298MB (disk)   74.4MB (content)

$ docker run -d --name f213-container -p 3210:3000 f213-nextjs-fundamentals
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:3210/
200
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:3210/notes
307
$ curl -s -D - -o /dev/null http://localhost:3210/about | grep -i cache-control
Cache-Control: s-maxage=31536000
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:3210/_next/static/chunks/2h9qyi1_ur4wz.css
200
```

Every result matches the host-run evidence above exactly — the containerized deployment is functionally identical, not a separate code path. Container stopped and removed immediately after capture.

## Captured evidence for F-214 (Full-stack integration with a separate Spring backend)

### A real, exact browser CORS failure

Real browser console, `fetch('http://localhost:8080/api/public/greeting')` from `http://localhost:5198`, no CORS config on the Spring side:

```
Access to fetch at 'http://localhost:8080/api/public/greeting' from origin 'http://localhost:5198'
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

`fetch()` itself threw `TypeError: Failed to fetch`. Full detail, including the real fix and retest, in the backend's own [README.md](../../java/full-stack-integration-backend/README.md).

### A real, subtle finding: CORS headers on the wire vs. JS-readable headers

```
$ curl -s -i -H "Origin: http://localhost:5198" http://localhost:8080/api/public/greeting | grep -i access-control
Access-Control-Allow-Origin: http://localhost:5198
```

The SAME successful browser `fetch()` call:

```js
res.headers.get('access-control-allow-origin')  // => null
```

### The real BFF pattern, end to end

```
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:5198/api/backend-proxy
401   -- unauthenticated, Spring never called

$ TOKEN=$(node scripts/gen-session-token.mjs)
$ curl -s -b "session=$TOKEN" http://localhost:5198/api/backend-proxy
{"secret":"Only reachable with the real shared secret -- this backend never sees the browser's own Next.js session cookie."}
```

A real, live browser session (logged in via the actual `/login` form) confirmed the SAME result end to end:

```js
document.cookie                          // => ""  (httpOnly, per F-211)
(await fetch('/api/backend-proxy')).status  // => 200
```

A real, direct browser `fetch()` to Spring's protected endpoint (bypassing Next.js) failed with the SAME `TypeError: Failed to fetch` as the unfixed public endpoint — that path was deliberately left off the CORS allowlist entirely, an independent layer of protection.

## Captured evidence for F-301 (Build tooling: Vite vs. Turbopack)

### Real dev-mode network trace — Turbopack bundles even in dev

Real `read_network_requests` capture, this app's `/about` page, `next dev` (Turbopack):

```
GET /about
GET /_next/static/chunks/[root-of-the-server]__068_is3._.css
GET /_next/static/chunks/[turbopack]_browser_dev_hmr-client_hmr-client_ts_....js
GET /_next/static/chunks/node_modules_next_dist_compiled_next-devtools_index_....js
GET /_next/static/chunks/node_modules_next_dist_compiled_react-dom_....js
... (10 more grouped chunk files, no individual source file ever its own request)
```

Contrast against the sibling `build-tooling-comparison` app's Vite dev server, where `main.jsx`, `App.jsx`, and `mathUtils.js` each show up as their OWN separate real HTTP request (see that app's own README) — the real, decisive, mechanical difference this chapter is built around.

### Real, decisive tree-shaking proof (Turbopack)

```
$ npm run build
$ grep -rl "TREE_SHAKE_MARKER_ADD_KEPT" .next/static/chunks/
.next/static/chunks/3ifmfarey59be.js

$ grep -rl "TREE_SHAKE_MARKER_SUBTRACT_DEAD" .next/static/chunks/
(no output -- genuinely absent)
```

`add` (imported, used) survives; `unusedSubtract` (exported, never imported) does not — identical real result to the Vite side.

### Real, decisive code-splitting proof (`next/dynamic`)

```
$ grep -rl "LAZY_CHUNK_MARKER" .next/static/chunks/
.next/static/chunks/3h89ht1zezblo.js
```

A genuinely SEPARATE chunk file from the one containing `TREE_SHAKE_MARKER_ADD_KEPT` — `next/dynamic`'s lazy-loaded component landed in its own real chunk, matching the Vite side's own `LazyPanel-*.js` result.

### Real dev-server cold start

```
$ PORT=5198 npm run dev
✓ Ready in 271ms
```

Against Vite's own `VITE v8.2.1  ready in 400 ms` (captured in the sibling app's README) — noted honestly as NOT a fair comparison on its own, since this app has 31 routes built up across F-201–F-214 versus the Vite app's 3 source files; both servers report "ready" once the process is listening, not once every route is compiled.

## Verification performed

- Live browser session: clicked real `<Link>` navigation across every route (Home, About, Blog ×2, Dashboard, Dashboard settings, Pricing), read every layout level's mount counter and each page's route/slug markers via direct DOM queries before and after each navigation.
- Confirmed genuine client-side routing via `performance.getEntriesByType('navigation').length`.
- Confirmed nested-layout unmounting via direct DOM node presence/absence (`querySelector` returning `null`), not inferred from a counter alone.
- Confirmed the route group's URL-stripping via both `window.location.pathname` in a live session and the real `next build` route manifest.
- `npm run build` — real production build (Turbopack), captured the real route manifest above, re-run after F-202's, F-203's, F-204's, F-205's, F-206's, F-207's, F-208's, F-209's, F-210's, and F-211's routes/files were added.
- F-206: real `node scripts/stream-observer.mjs` runs (fetch + `ReadableStream` reader) against a clean `next start` server, comparing sibling-boundary parallel resolution, page-level `loading.js` streaming, and a bot-User-Agent request — the doc-recommended verification method over `curl`, which has its own buffering.
- F-301: a real, dual-tool comparison against a new, purpose-built Vite + React app (`build-tooling-comparison`) — a real `read_network_requests` capture contrasting Vite's per-module native-ESM dev requests against Turbopack's own grouped-chunk dev requests; the identical real tree-shaking test (an unused export, runtime markers not comments) run against a real production build of BOTH tools, with matching results; the identical real code-splitting test (`React.lazy()`/`next/dynamic`) run against both, each producing a genuinely separate chunk file, confirmed by grep.
- F-214: a real, separate Spring Boot backend stood up specifically for this chapter (its own JVM process, its own port); a real, live browser `fetch()` producing an exact, captured CORS failure and console error before any CORS config existed, then a real fix and retest; a real curl-vs-browser contrast showing `Access-Control-Allow-Origin` present on the wire but returning `null` from JavaScript's own `fetch()` `Headers` object; a real, live, authenticated browser session (login via the actual form) confirming the BFF pattern end to end, with `document.cookie` checked directly to reconfirm F-211's `httpOnly` proof; a real, independent CORS failure for a direct browser call to the backend's protected endpoint, confirming two separate, both-required protection layers.
- F-213: a real `du -sh` measurement comparing a naive deployment (`node_modules` + `.next`, 543MB) against `output: "standalone"` (42MB); a real, reproduced standalone-server static-asset 404, fixed by manually copying `public/`/`.next/static`, re-verified with a real 200; a real `Cache-Control` header capture across three response types, including a genuine mismatch against the framework's own CDN documentation prose; a real, decisive two-independent-build multi-instance test showing a plain top-level bound Server Action (`deleteNote`) renders an identical action id and works cross-instance with zero shared-key config, contrasted against a real inline closure action (`echoClosure`) that renders a visibly different, real encrypted field; a real `docker build`/`docker run` cycle (Docker daemon started specifically for this chapter), including two real, transient failures (a base-image pull timeout, and the same `httpbin.org` outage from F-212 recurring inside the container build) each diagnosed and worked around, ending in a fully working containerized deployment verified with the same curl checks used on the host.
- F-212: a real `read_network_requests` capture of the single-roundtrip response model (one POST, response body carrying both the action's return value and the re-rendered page); a real, disk-verified auth-bypass test (`addNote`'s own check temporarily removed, restored immediately after capture) proving a mutation can commit before a page-level redirect fires; a real, deliberately mismatched-`Origin` curl request capturing the framework's own CSRF rejection with its exact server log line; a real raw multipart curl POST (no JavaScript) reproducing a bound Server Action's own rendered hidden-field fallback, contrasted against a real, captured dead `javascript:` fallback for the SAME action wrapped in a closure for `useOptimistic`; a real, live browser screenshot captured mid-flight during a real 800ms artificial delay, showing `useOptimistic`, `useActionState`, and `useFormStatus`'s pending signals all active simultaneously.
- F-211: a real, live browser login/logout cycle (`document.cookie` checked directly to confirm `httpOnly`); a real, reproduced Proxy-vs-DAL bypass test using a deliberately tampered, `jose`-signed token against both the upgraded and (temporarily reverted, then restored) naive Proxy check; a real, three-way `unauthorized()`/`authInterrupts` test (no flag, flag+streaming, flag+Route-Handler) with distinct captured HTTP statuses for each.
- F-210: real, deliberate before/after `next.config.mjs` change (adding `images.remotePatterns`/`images.qualities`), with real `400` responses captured before and real `200` responses captured after, for both an unconfigured remote host and a disallowed image quality; a real `next dev` console check plus a full real `next build` confirmed zero deprecation warning for the `priority` prop, reverted to `preload` immediately after capture; a real `read_network_requests` trace confirmed zero requests to any Google domain for the self-hosted font.
- F-209: reused `scripts/stream-observer.mjs` from F-206 for two real, contrasted traces (normal vs. `Twitterbot/1.0` User-Agent) against a genuinely slow `generateMetadata`, completing F-206's own earlier, only-partially-tested finding; a real, deliberate `metadataBase` removal and rebuild, capturing an unplanned warning-not-error finding and a real wrong URL baked into static HTML, `metadataBase` restored and re-verified immediately after capture.
- F-208: real curl sequence proving Proxy's header injection, redirect, cookie-gated auth check, and `matcher` exclusion against a clean `next start` server; a real, live browser navigation confirming the `/legacy-about` redirect independently of curl; two real, deliberately contrasted `next build` attempts (a hard error inside `proxy.js`, a warning-only on an ordinary Route Handler) proving the precise Edge Runtime distinction, both reverted immediately after capture.
- F-207: real `curl` sequence covering the full CRUD lifecycle (200/201/400/404/204) against a clean `next start` server; a real live mutation proving a `force-static` Route Handler freezes at build time; real automatic `405`/`OPTIONS` proof; two real external network calls (one via curl, one via a real browser button click) to the Backend-for-Frontend proxy demo, confirming genuinely fresh server-side calls each time.
- F-203: real `grep` against actual `.next/server` and `.next/static` build artifacts to prove the server-secret code/output distinction; two deliberate build/runtime breakages (a hook in a Server Component, an async Client Component), each captured and reverted; interactivity re-verified on a clean production `next build` + `next start` server.
- F-204: real `curl` requests (with precise real timestamps for the timed test) against a clean `next start` production server for all four fetch-caching strategies; a real deliberate build failure (an unreachable same-server API route during static prerendering), captured and fixed; a real browser click on a live `RevalidateButton` invoking a real Server Action.
- F-205: real `curl` requests with distinct `User-Agent` headers against a clean `next start` production server to prove genuine per-request SSR; real extracted render timestamps to prove SSG's build-time-fixed content versus on-demand generation for an unlisted dynamic param.
