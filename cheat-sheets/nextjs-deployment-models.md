---
title: "Cheat Sheet: Next.js Deployment Models"
slug: nextjs-deployment-models
document_type: cheat-sheet
domain: frontend
topic_id: F-213
tier: Advanced
canonical: ../handbook/frontend/nextjs-deployment-models.md
last_updated: 2026-09-03
---

# Next.js Deployment Models

**Canonical chapter:** [`handbook/frontend/nextjs-deployment-models.md`](../handbook/frontend/nextjs-deployment-models.md)

The register itself names the trap this chapter avoids: "real trade-offs, not marketing." No Vercel account was used — every self-hosting claim was independently verified (real `docker build`/`docker run`, a real minimal `node server.js` run, real captured headers).

## Core Mental Model

Self-hosting Next.js genuinely supports "all features" on a Node.js server or Docker container — but "supports" and "configured correctly by default" are two different claims. `output: "standalone"` produces a real, minimal server directory (measured ~13x smaller) but does NOT include `public/`/`.next/static` by default. Multi-instance Server Actions need a shared encryption key ONLY for actions that genuinely close over an outer-scope variable — not universally.

## Essential Definitions

- **`output: "standalone"`** — traces the app's actual runtime dependency graph and copies only what's needed into `.next/standalone`, plus a generated `server.js`.
- **Self-hosting** — a Node.js server (`next start`) or Docker container; shifts reverse proxy, cache storage, and multi-instance coordination onto the operator.
- **Server Functions encryption key** — protects a Server Action's closure-captured values on their round trip to the client and back; applies specifically to genuine closures, not action references in general.

## Decision Table

| Question | Choice |
|---|---|
| Want deployment mechanics to be someone else's problem? | A platform (Vercel or similar) |
| Self-hosting for cost/compliance/infrastructure-ownership reasons? | `output: "standalone"` as the minimal Docker base — but explicitly test for the missing-static-assets gotcha before calling it done |
| Running multiple self-hosted instances behind a load balancer? | Audit every Server Action for genuine closures — only those need a shared `NEXT_SERVER_ACTIONS_ENCRYPTION_KEY` |
| Fronting self-hosted deployment with a CDN? | Verify the actual `Cache-Control` header a static page sends — don't trust documentation prose at face value |

## Key Numbers (real, measured)

- `output: "standalone"`: **42MB**, versus a naive `node_modules` (435MB) + `.next` (108MB) = 543MB — roughly a **13x** reduction.
- Static page (`/about`) real `Cache-Control`: `s-maxage=31536000` — NOT `public`, contradicting the framework's own docs prose for this case.
- Dynamic page: `private, no-cache, no-store, max-age=0, must-revalidate`. Immutable static asset: `public, max-age=31536000, immutable`. Both match the docs.
- A plain top-level, `.bind()`-only Server Action rendered the IDENTICAL action reference id across two independently-built instances, and a cross-instance request succeeded with zero shared-key configuration. A genuine inline closure rendered a real, distinct encrypted blob field instead.

## Common Pitfalls

- Assuming `output: "standalone"` is a complete deployment artifact — it 404s on every static asset until `public/`/`.next/static` are manually copied in.
- Assuming EVERY Server Action needs a shared encryption key across self-hosted instances — only genuine closures do.
- Trusting a documentation summary's exact wording for a response header over a direct capture.

## Interview Answer Skeleton

**30-sec:** Self-hosting supports all features on Node/Docker, verified with a real ~13x smaller `output: "standalone"` build and a real working Docker cycle. But self-hosting shifts real configuration onto the operator: two concrete gotchas — missing static assets by default, and a Server Actions encryption key needed only for genuine closures, not universally.

**2-min:** Cover the measured size reduction, the reproduced static-asset gotcha and its fix (two `COPY` steps), and the decisive multi-instance finding distinguishing closure-capturing actions from plain bound ones.

**Whiteboard:** Two boxes: "Full deployment: 435MB + 108MB" vs. "output: standalone (42MB)," arrow labeled "~13x, measured." Below the standalone box, a dashed box "NOT included: public/, .next/static" → "real 404 until copied in." Separate section: a plain top-level action (same id across builds, no key needed) vs. an inline closure (real encrypted field, needs shared key).

**Staff-level framing:** The real cost of self-hosting is a short, specific, one-time checklist (copy static assets, audit for closures, verify CDN header semantics) that a platform automates but a team can equally well codify in CI. Weigh that bounded engineering cost against a platform's ongoing per-request pricing and organizational constraints (data residency, existing Kubernetes investment, compliance).

## Production Warning Signs

- A freshly self-hosted deployment renders correct HTML but every image/stylesheet 404s — the Docker image built from `output: "standalone"`'s `server.js` never copied `public/`/`.next/static` alongside it.
- Diagnosis: `curl` directly against the Node process itself (bypassing any CDN/proxy) reproduces the same 404s, isolating it from infrastructure.
- A blanket "self-hosting needs a shared Server Actions key" policy adds unnecessary key-management overhead for a codebase using only top-level, bound actions.

## Related

- `handbook/frontend/nextjs-server-actions-and-mutations.md`
- `handbook/cloud/kubernetes-objects-scheduling-and-networking.md`
- `handbook/cloud/cloud-cost-and-scaling-economics.md`
- `handbook/frontend/nextjs-fullstack-integration.md`
