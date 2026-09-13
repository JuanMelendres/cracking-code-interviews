---
title: "Self-Hosted Next.js Deployment Missing Static Assets After a Docker Build"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-deployment-models.md
source: syllabus/21-frontend-web/nextjs-deployment-models.md#production-scenarios
---

# Self-Hosted Next.js Deployment Missing Static Assets After a Docker Build

## Context

A deployment uses `output: "standalone"`'s `server.js` directly, a real, common Docker pattern.

## Symptoms

The app returns real HTML with a correct `200`, but the page is unstyled and the console shows a wall of 404s for `/_next/static/...` paths.

## Impact

Every page renders functionally correct HTML but visually broken, with no images or stylesheets loading.

## Initial Hypotheses

- A CDN misconfiguration — checked, reproduces the same 404s when curling directly against the Node process, bypassing any CDN/proxy.
- A broken build — checked, the HTML response itself is genuinely correct.
- The image-build step never copied `public/` and `.next/static` alongside the standalone `server.js` — correct.

## Evidence

`curl` directly against the Node process reproduces the same 404s for static asset paths, while the HTML response itself is genuinely correct.

## Investigation Timeline

1. Deployment renders correct HTML but with broken styling and asset 404s.
2. CDN-misconfiguration and broken-build hypotheses ruled out via a direct `curl` against the Node process.
3. Dockerfile inspected, confirming `public/` and `.next/static` were never copied into the standalone image.

## Root Cause

The `output: "standalone"` build mode produces a minimal `server.js` that does not automatically bundle `public/` or `.next/static`; the Dockerfile never added the required `COPY` steps for either.

## Immediate Mitigation

Manually copy the missing directories into the running container as a stopgap while the image build is fixed.

## Permanent Fix

Add the two `COPY` steps for `public/` and `.next/static` to the Dockerfile, matching the official multi-stage pattern.

## Alternatives Considered

Serving static assets from a separate CDN/object-storage layer instead of alongside the standalone server — a real, valid alternative for larger deployments, but unnecessary complexity for this specific gap, which was simply a missing `COPY` step.

## Trade-offs

None meaningful — the fix is two lines in the Dockerfile with no functional trade-off.

## Prevention

Treat `output: "standalone"` deployments' Dockerfiles as needing an explicit, reviewed checklist against the official multi-stage pattern, since the minimal `server.js` output is easy to assume is self-contained when it isn't.

## Monitoring and Alerts

- A post-deploy smoke test verifying at least one static asset path (`/_next/static/...`) returns `200`, not just the page's own HTML.
- A Dockerfile-diff review against the official reference pattern whenever the standalone output mode's build steps are touched.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent deployment issue.

- **Situation:** a freshly self-hosted deployment served correct HTML but every image and stylesheet 404'd.
- **Task:** find why static assets were missing despite a successful, correct-looking build.
- **Action:** curled the Node process directly to rule out a CDN issue, then inspected the Dockerfile and found the required `COPY` steps for `public/` and `.next/static` were missing.
- **Result:** added the missing steps, matching the official multi-stage Docker pattern.

## Staff-Level Discussion

`output: "standalone"`'s minimal `server.js` is easy to assume is fully self-contained, but it deliberately excludes static assets to keep the server bundle small — a real, documented trade-off that's easy to miss without directly comparing a custom Dockerfile against the official reference pattern. The organizational lesson is verifying any deployment configuration against the framework's own documented reference, not assuming a minimal build output implies self-sufficiency.

## Related Handbook Chapters

- [Next.js Deployment Models](../syllabus/21-frontend-web/nextjs-deployment-models.md) — the canonical `output: "standalone"` Docker pattern behind this incident's fix.
