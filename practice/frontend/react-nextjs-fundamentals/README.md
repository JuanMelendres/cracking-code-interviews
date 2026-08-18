# Next.js Fundamentals demo app (F-201, F-202, F-203, F-204)

Real Next.js 16.3.1 (App Router) app backing [`handbook/frontend/nextjs-fundamentals.md`](../../../handbook/frontend/nextjs-fundamentals.md) (F-201), [`handbook/frontend/nextjs-app-router-fundamentals.md`](../../../handbook/frontend/nextjs-app-router-fundamentals.md) (F-202), [`handbook/frontend/nextjs-server-vs-client-components.md`](../../../handbook/frontend/nextjs-server-vs-client-components.md) (F-203), and [`handbook/frontend/nextjs-data-fetching-and-caching.md`](../../../handbook/frontend/nextjs-data-fetching-and-caching.md) (F-204). Extended in place rather than scaffolding a new Next.js project each time, since each chapter is a direct continuation of the same file-based-routing playground.

> **F-204 note:** this app's `next.config.mjs` does NOT set `cacheComponents: true`, so it runs under this Next.js version's "Previous Model" of caching (`fetch`'s own `cache`/`next.revalidate`/`next.tags` options) — confirmed by reading `node_modules/next/dist/docs/01-app/02-guides/caching-without-cache-components.md` directly, since this is exactly the API surface the register's F-204 topic names. Version 16 also ships an entirely different, opt-in "Cache Components" model (`"use cache"`, `cacheLife`, `cacheTag`) — out of scope for this chapter, briefly noted for context only.

> **F-203 note:** requires `.env.local` (gitignored, not committed) with `SERVER_SECRET_DEMO=SUPER_SECRET_VALUE_9f8e7d6c` for the Server-Component-secret demo to have a real value to read. Recreate it locally if cloning fresh — see the F-203 evidence section below for exactly what it proves.

## Run it

```bash
npm install
npm run dev    # app at http://localhost:5198
npm run build  # real next build — captures the real route manifest below
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

## Verification performed

- Live browser session: clicked real `<Link>` navigation across every route (Home, About, Blog ×2, Dashboard, Dashboard settings, Pricing), read every layout level's mount counter and each page's route/slug markers via direct DOM queries before and after each navigation.
- Confirmed genuine client-side routing via `performance.getEntriesByType('navigation').length`.
- Confirmed nested-layout unmounting via direct DOM node presence/absence (`querySelector` returning `null`), not inferred from a counter alone.
- Confirmed the route group's URL-stripping via both `window.location.pathname` in a live session and the real `next build` route manifest.
- `npm run build` — real production build (Turbopack), captured the real route manifest above, re-run after F-202's, F-203's, and F-204's routes were added.
- F-203: real `grep` against actual `.next/server` and `.next/static` build artifacts to prove the server-secret code/output distinction; two deliberate build/runtime breakages (a hook in a Server Component, an async Client Component), each captured and reverted; interactivity re-verified on a clean production `next build` + `next start` server.
- F-204: real `curl` requests (with precise real timestamps for the timed test) against a clean `next start` production server for all four fetch-caching strategies; a real deliberate build failure (an unreachable same-server API route during static prerendering), captured and fixed; a real browser click on a live `RevalidateButton` invoking a real Server Action.
