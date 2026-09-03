---
title: "Cheat Sheet: Next.js Server vs. Client Components"
slug: nextjs-server-vs-client-components
document_type: cheat-sheet
domain: frontend
topic_id: F-203
tier: Intermediate
canonical: ../handbook/frontend/nextjs-server-vs-client-components.md
last_updated: 2026-09-03
---

# Next.js Server vs. Client Components

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-server-vs-client-components.md`](../syllabus/21-frontend-web/nextjs-server-vs-client-components.md)

Flagged in the topic register as **"the single most-tested modern Next.js concept."**

## Core Mental Model

`"use client"` doesn't mark a component — it marks a MODULE BOUNDARY. Everything below that directive in the import graph gets bundled and shipped to the browser; everything else, by default, runs only on the server and never ships as JavaScript at all. A Server Component can render a Client Component as a child (crossing the boundary once); a Client Component cannot import and render a Server Component the same way — but a Server Component CAN still be passed into a Client Component as `children`/a prop, because it was already rendered on the server before crossing the boundary.

## Essential Definitions

- **Server Component (default)** — no directive; can do server-only work (DB, filesystem, secrets) directly; cannot use hooks/state/effects/browser APIs; CAN be `async`.
- **Client Component (`"use client"`)** — opt-in for interactivity; its code is bundled and shipped for hydration.
- **Code vs. output** — a Server Component's CODE never reaches the client bundle, but its RENDERED OUTPUT (HTML/RSC payload) does. These are commonly conflated.
- **RSC payload** — the serialized description of the component tree, including where Client Component boundaries sit, letting the browser hydrate only those boundaries.

## Decision Table

| Concern | Server Component (default) | Client Component (`"use client"`) |
|---|---|---|
| Code shipped to browser | None (measured: 0 matches in client bundle) | Yes — bundled |
| Can use hooks/browser APIs | No (real build-time error) | Yes — the reason to opt in |
| Can be `async` | Yes | Restricted — in this Next.js version, fails at RUNTIME, not build time (a live-verified finding) |
| Direct secret/DB/filesystem access | Yes, safely (code never reaches client) | Not safely for non-`NEXT_PUBLIC_` secrets |
| Best fit | Static or server-data-driven, non-interactive | Anything needing state, effects, event handlers |

## Key Numbers (real, measured against Next.js 16.3.1)

- A secret string appeared **1 time** in the real prerendered HTML (`.next/server/app/server-vs-client.html`) and **0 times** across all 15 files under `.next/static` (the entire client bundle).
- Hook in a Server Component → real `next build` failure: `Error: You're importing a module that depends on useState into a React Server Component module.`
- `async` Client Component → `next build` **succeeded**; the error (`Only Server Components can be async at the moment`) only appeared at runtime, in the browser, when the component actually rendered — contradicting a pre-cutoff expectation of a build-time error.

## Common Pitfalls

- Adding `"use client"` "just in case" or to match a sibling file, silently converting server-only logic (and its bundle cost) into client-shipped code.
- Assuming "Server Component" means the DATA never reaches the browser, rather than the CODE never reaching the browser — a component can still render (and thus expose) a secret's raw value.
- Assuming every Server/Client restriction is a build-time error — the async-Client-Component restriction in this version is runtime-only, a real discovered discrepancy.

## Interview Answer Skeleton

**30-sec:** Server Components (default) run only on the server — code never ships, verified by grepping real build output. Client Components (`"use client"`) opt into browser execution and their code IS bundled. The boundary is enforced by the build for statically provable violations, but not every restriction is build-time — the async-Client-Component restriction in this Next.js version is a live-verified runtime-only check.

**2-min:** Add the code-vs-output distinction (1 match in HTML, 0 in the client bundle), the real captured hook-in-Server-Component build error, and the async-Client-Component finding as a concrete lesson in verifying current framework behavior rather than trusting memorized docs.

**Whiteboard:** A vertical "`use client` boundary" line. Left: Server Component code, arrow to the browser icon crossed out (0 matches, measured), a separate arrow for "rendered output" that DOES cross (1 match in real HTML). Right: Client Component code with a solid arrow to the browser (code IS shipped).

**Staff-level framing:** A server-secret-in-Client-Component leak is exactly the kind of risk to catch with an automated, repeatable check (a CI grep against build artifacts for known-sensitive strings) rather than relying on code review — the diff looks nearly identical either way, so review is structurally weak here.

## Production Warning Signs

- A developer adds `"use client"` to a file handling a server-side API key "for consistency" with a sibling file — the key's handling code is now genuinely shipped to every visitor's browser, and a code-review diff skim is likely to miss it.
- The concrete defense: grep real `.next/static` output for known-sensitive strings in CI — expect zero matches, not a one-off manual audit.
- A `next build`-only CI check will NOT catch a runtime-only restriction violation (like the async Client Component case) — a rendered smoke test or E2E check is needed too.

## Related

- `syllabus/21-frontend-web/nextjs-app-router-fundamentals.md`
- `syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md`
- `syllabus/21-frontend-web/react-testing.md`
- `00-project/frontend-topic-register.md`
