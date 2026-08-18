# Next.js Fundamentals demo app (F-201, F-202)

Real Next.js 16.3.1 (App Router) app backing [`handbook/frontend/nextjs-fundamentals.md`](../../../handbook/frontend/nextjs-fundamentals.md) (F-201) and [`handbook/frontend/nextjs-app-router-fundamentals.md`](../../../handbook/frontend/nextjs-app-router-fundamentals.md) (F-202). Extended in place rather than scaffolding a second Next.js project, since F-202's nested-layout and route-group demos are a direct continuation of F-201's same file-based-routing playground.

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

## Verification performed

- Live browser session: clicked real `<Link>` navigation across every route (Home, About, Blog ×2, Dashboard, Dashboard settings, Pricing), read every layout level's mount counter and each page's route/slug markers via direct DOM queries before and after each navigation.
- Confirmed genuine client-side routing via `performance.getEntriesByType('navigation').length`.
- Confirmed nested-layout unmounting via direct DOM node presence/absence (`querySelector` returning `null`), not inferred from a counter alone.
- Confirmed the route group's URL-stripping via both `window.location.pathname` in a live session and the real `next build` route manifest.
- `npm run build` — real production build (Turbopack), captured the real route manifest above, re-run after F-202's routes were added.
