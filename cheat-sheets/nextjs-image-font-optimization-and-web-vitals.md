---
title: "Cheat Sheet: Next.js Image, Font Optimization and Web Vitals"
slug: nextjs-image-font-optimization-and-web-vitals
document_type: cheat-sheet
domain: frontend
topic_id: F-210
tier: Intermediate
canonical: ../handbook/frontend/nextjs-image-font-optimization-and-web-vitals.md
last_updated: 2026-09-03
---

# Next.js Image, Font Optimization and Web Vitals

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md`](../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md)

## Core Mental Model

`next/image` and `next/font` trade a small amount of setup ceremony for automatic, verifiable prevention of the two most common Core Web Vitals regressions: unbounded image payload size (LCP) and layout-shifting content (CLS). Images operate at two real, distinct layers: the `<Image>` COMPONENT (which silently clamps a disallowed `quality`) and the `/_next/image` ENDPOINT (which hard-enforces the same allowlist with a real `400`). Fonts are simpler: `next/font/google` downloads the font once at build time and serves it from the app's own origin forever after.

## Essential Definitions

- **`next/image`** — extends `<img>` with automatic responsive `srcset`, format negotiation, lazy loading, and layout-space reservation via width/height.
- **`remotePatterns` / `qualities`** — required allowlists in `next.config.js` for the `/_next/image` optimization endpoint; exist to prevent an open, unrestricted image-fetching proxy (a real SSRF/abuse vector).
- **`next/font/google`** — self-hosts font files at build time; the end user's browser never talks to Google.
- **`priority` (deprecated in v16)** — replaced by `preload`; still works, but produces zero warning anywhere.

## Decision Table

| Question | Choice |
|---|---|
| Image is a static import from the app's own source tree? | Skip `width`/`height` — auto-detected, plus a real blur placeholder |
| Image is a `public/`-folder path or a remote URL? | `width`/`height` (or `fill`) required explicitly — no build-time access to inspect either |
| App needs images from an external host? | Configure `remotePatterns` narrowly — an unconfigured host gets a hard `400`, not a silent pass-through |
| App needs a non-default image quality? | Add it to `images.qualities` explicitly — an un-allowlisted value is silently clamped, not errored |

## Key Numbers (real, curl'd/traced against a clean production server)

- `quality={90}` with only `[75]` allowlisted: the `<Image>` COMPONENT rendered `q=75` in the actual HTML — silent clamp, zero warning.
- The same `q=90` hit directly against `/_next/image`: real, exact `400` — `"q" parameter (quality) of 90 is not allowed`.
- An unconfigured remote host hit directly: real `400` — `"url" parameter is not allowed`.
- `next/font/google` network trace: **0** requests to any Google domain; the font served from `/_next/static/media/...`.

## Common Pitfalls

- Assuming an un-allowlisted `quality` value will error loudly — the component silently clamps it, an easy-to-miss production gap.
- Forgetting `remotePatterns` for a remote host and assuming a graceful fallback — the real behavior is a hard `400`.
- Continuing to use `priority` out of habit — it's deprecated in v16, but nothing in the running app will ever tell you.

## Interview Answer Skeleton

**30-sec:** `next/image` automates responsive sizing and layout-shift prevention through a real `/_next/image` endpoint that hard-enforces `remotePatterns`/`qualities` allowlists. `next/font` self-hosts at build time, verified with zero Google requests. A real v16 finding: `priority` is deprecated in favor of `preload`, but using the old prop produces no warning anywhere.

**2-min:** Cover the three image source types and their dimension requirements, the two-layer allowlist enforcement (component clamps silently, endpoint hard-rejects), and the real before/after fix via `next.config.js`. Close with the silent `priority` deprecation.

**Whiteboard:** `<Image quality={90}>` branches left (via the component: a filter box clamps to 75 before any request) and right (a hand-built URL hitting the endpoint directly with q=90: a hard 400 stop). Separate small diagram: build machine → Google font servers (build-time only) vs. a crossed-out arrow from the user's browser to Google.

**Senior-level framing:** Name the specific enforcement mechanism (allowlist, hard 400 at the endpoint) and distinguish component-level from endpoint-level behavior for a disallowed value — this Intermediate-tier chapter reaches Staff-level framing only briefly, on treating every deprecation as needing its own verification of whether it's loud or silent.

## Production Warning Signs

- A developer sets `quality={95}` "for extra crispness"; the image looks unchanged after shipping — no error, no warning, just a missed intent, because the value was silently clamped to the allowlisted default.
- Confirm by inspecting the actual rendered `<img>` `src`/`srcset`, not the prop value.
- Fix: add the desired value to `images.qualities` explicitly, and treat every `quality`/`remotePatterns` value as needing a corresponding config entry, verified by inspecting real output.

## Related

- `syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md`
- `syllabus/21-frontend-web/nextjs-streaming-and-suspense.md`
- `syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md`
