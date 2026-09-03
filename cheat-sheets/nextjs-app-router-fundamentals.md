---
title: "Cheat Sheet: Next.js App Router Fundamentals"
slug: nextjs-app-router-fundamentals
document_type: cheat-sheet
domain: frontend
topic_id: F-202
tier: Beginner
canonical: ../handbook/frontend/nextjs-app-router-fundamentals.md
last_updated: 2026-09-03
---

# Next.js App Router Fundamentals

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-app-router-fundamentals.md`](../syllabus/21-frontend-web/nextjs-app-router-fundamentals.md)

## Core Mental Model

A layout's persistence is scoped to exactly the part of the route tree that actually renders through it — not global. A nested layout gets the identical persistence guarantee the root layout gets, but only within routes that share it as an ancestor; leave that subtree and it genuinely unmounts. Route groups are a separate, unrelated mechanism: they scope a layout (or just organize files) to a set of routes WITHOUT those routes' URLs reflecting that grouping at all.

## Essential Definitions

- **Nested layout** — `layout.js` at any folder depth; wraps every route beneath it; composed root-down via `children`.
- **Layout unmounting** — happens when a navigation target no longer shares that layout as an ancestor; React has no matching tree position to reuse, so the instance is genuinely removed (measured: a DOM node going from present to `null`).
- **Route group** — a `(name)` folder; participates in layout composition but is explicitly excluded from URL construction.
- **Two separate build-time steps** — URL construction (strips parenthesized segments) and layout composition (still walks through a route group's `layout.js`) — related but distinct outcomes of the same file-tree walk.

## Decision Table

| Question | Choice |
|---|---|
| Does the nesting reflect a genuine sub-resource the URL should show (e.g. `/dashboard/settings`)? | Plain nested folder — no parentheses |
| Want several routes to share a layout/organization WITHOUT that showing in any URL? | Route group `(name)` |
| Unsure if a shared layout persists across a set of routes' internal navigation? | It will, automatically, if they share it as an ancestor — verify with a mount counter, don't assume |
| Need a shared layout across routes with otherwise unrelated URL shapes (`/`, `/pricing`, `/about`)? | Route group — ordinary nested folders can't do this without a shared URL prefix |

## Common Pitfalls

- Conflating route groups with ordinary nested routing — a `(name)` folder is a different mechanism (URL-exclusion + layout-scoping only), not just an "invisible" folder.
- Assuming a nested layout's persistence is unconditional/global rather than scoped to shared ancestry — the real DOM-node-disappearance evidence (`/dashboard/settings` → `/about`) is the concrete counter-example.
- Forcing a shared layout's routes under a common URL prefix with plain nested folders when a route group would give identical layout-scoping with better URLs.

## Interview Answer Skeleton

**30-sec:** Layouts nest by folder structure and get the same persistence guarantee at any depth — proven with a mount counter unchanged across nested navigation, and a real DOM node disappearing once ancestry no longer applies. Route groups scope a layout without adding a URL segment — proven via `window.location.pathname` and a real build manifest.

**2-min:** Cite the nested-layout evidence (`DashboardLayout` mount counter unchanged navigating within `/dashboard/*`, then a DOM node going to `null` navigating to `/about`). Then cover route groups as a separate mechanism: `app/(marketing)/pricing/page.js` resolves to `/pricing`, confirmed live and in the build manifest, solving the specific problem of sharing a layout across routes with unrelated URL shapes.

**Whiteboard:** Three-level tree (Root → DashboardLayout → two leaf pages); an arrow between the leaves keeps both ancestor boxes solid. A separate arrow to `/about`, outside the subtree, shows the DashboardLayout box disappearing while Root stays solid. Beside it, a dashed `(marketing)` box wrapping `pricing`, with an arrow to the resulting URL `/pricing` — explicitly skipping the group name.

**Senior-level framing:** State the precise condition for persistence vs. unmounting (shared ancestry, not "it's a layout so it persists"), and the precise URL-inclusion-vs-exclusion difference for route groups. This Beginner-tier chapter frames deeper implications only briefly: as an app's route tree grows, where to place layout boundaries becomes an architectural decision affecting how independently sections can evolve their own UI.

## Production Warning Signs

- Forcing marketing URLs under a `/marketing` prefix purely to get shared layout scoping via plain nested folders, when a route group achieves the same scoping with natural, unprefixed URLs.
- State living in a layout that actually unmounts on a given navigation (an in-progress form draft, a scroll position) being silently lost because persistence was assumed rather than verified.
- Renaming `layout.js` to something invalid (e.g. `_layout.js`) — the route still works but silently renders through the parent layout only, since `layout.js` is a naming convention, not a requirement for the route to exist.

## Related

- `syllabus/21-frontend-web/nextjs-fundamentals.md`
- `syllabus/21-frontend-web/nextjs-server-vs-client-components.md`
- `00-project/frontend-topic-register.md`
