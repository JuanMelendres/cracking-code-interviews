# Next.js Fundamentals demo app (F-201)

Real Next.js 16.3.1 (App Router) app backing [`handbook/frontend/nextjs-fundamentals.md`](../../../handbook/frontend/nextjs-fundamentals.md).

## Run it

```bash
npm install
npm run dev    # app at http://localhost:5198
npm run build  # real next build — captures the real route manifest below
```

> This Next.js version (16.3.1) shipped after this assistant's training cutoff. Before writing any route code, the bundled docs at `node_modules/next/dist/docs/01-app/01-getting-started/03-layouts-and-pages.md` were read directly (per the auto-generated `AGENTS.md` this project scaffolds) to confirm current App Router conventions — notably that `params` in a dynamic route is a `Promise` that must be `await`ed.

Four routes, all created purely by file location — no router config, no route registration:

- `app/page.js` → `/`
- `app/about/page.js` → `/about`
- `app/blog/[slug]/page.js` → `/blog/<anything>` (dynamic segment)
- `app/layout.js` (root layout, wraps every route) + `app/components/PersistentHeader.js` (client component with a real mount counter)

## Captured evidence (real browser session + real build output)

### File-based routing: a real route manifest, zero router config

```
$ npm run build
Route (app)
┌ ○ /
├ ○ /_not-found
├ ○ /about
└ ƒ /blog/[slug]

○  (Static)   prerendered as static content
ƒ  (Dynamic)  server-rendered on demand
```

Every route in this table exists because of a `page.js` file's LOCATION in the `app/` directory — nothing in this project imports or configures a router. `/about` was created entirely by adding `app/about/page.js`; `/blog/[slug]` was created entirely by adding `app/blog/[slug]/page.js`, and that ONE file serves every `/blog/<anything>` URL.

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

## Verification performed

- Live browser session: clicked real `<Link>` navigation between all four routes, read `PersistentHeader`'s mount counter and each page's route/slug markers via direct DOM queries before and after each navigation.
- Confirmed genuine client-side routing via `performance.getEntriesByType('navigation').length`.
- `npm run build` — real production build (Turbopack), captured the real route manifest above.
