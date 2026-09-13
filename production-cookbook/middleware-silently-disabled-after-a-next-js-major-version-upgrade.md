---
title: "Middleware Silently Disabled After a Next.js Major Version Upgrade"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md
source: syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md#production-scenarios
---

# Middleware Silently Disabled After a Next.js Major Version Upgrade

## Context

A team's existing `middleware.ts` (written against an older major version's conventions) sits untouched through a version bump to Next.js 16.

## Symptoms

Requests that used to be redirected or header-tagged by that file behave as if it doesn't exist — an old marketing URL that used to 307-redirect now 404s.

## Impact

Previously-working redirect and header-tagging behavior silently stops functioning across every route the middleware used to handle.

## Initial Hypotheses

- A deploy configuration or DNS issue — checked, no such change occurred, and other routing behaves normally.
- A regression in the redirect logic itself — checked, the file's contents are unchanged since the last known-working deploy.
- The framework no longer recognizes the file under its old name after the major version upgrade — correct.

## Evidence

A real `next build` shows no `ƒ Proxy (Middleware)` line in its summary at all — the file that would produce it isn't present under its new required name.

## Investigation Timeline

1. Previously-working redirects stop functioning immediately after a Next.js major version upgrade.
2. Deploy-config and logic-regression hypotheses ruled out.
3. A fresh `next build` run, confirming no middleware/proxy line appears in the build summary at all.

## Root Cause

`middleware.ts` is deprecated file-naming from a prior major version; the new version looks for `proxy.ts`/`proxy.js` specifically, and the old filename is silently ignored rather than erroring.

## Immediate Mitigation

Roll back to the prior major version temporarily if the missing redirects are causing active user-facing impact, while the rename is prepared.

## Permanent Fix

Rename the file and its exported function to match the new convention, using the framework's own automated codemod (`npx @next/codemod@canary middleware-to-proxy .`) — a known, common, real upgrade pitfall the framework explicitly ships tooling for.

## Alternatives Considered

Manually renaming and hand-verifying every export — a real, valid alternative to the codemod, but strictly more error-prone and slower for a mechanical, well-defined rename the framework's own tooling already automates correctly.

## Trade-offs

None meaningful — the codemod-based rename is a mechanical, low-risk change once identified.

## Prevention

Treat any major-version upgrade as requiring an explicit check of the build output summary (confirming expected lines like `ƒ Proxy (Middleware)` still appear) rather than assuming a clean build implies unchanged runtime behavior.

## Monitoring and Alerts

- A post-upgrade smoke test specifically exercising every route the middleware/proxy file is expected to affect, rather than relying on general functional tests alone.
- A CI check parsing `next build`'s own output summary for expected middleware/proxy lines, failing the pipeline if one silently disappears.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent upgrade.

- **Situation:** a routine major-version upgrade silently disabled all middleware-driven redirects, with no build error.
- **Task:** diagnose a "the file that used to work now doesn't" symptom with no obvious error to follow.
- **Action:** ran a fresh build and inspected its summary directly, finding the expected middleware/proxy line entirely absent, then applied the framework's own automated codemod.
- **Result:** restored the redirect behavior under the new required filename, verified by confirming the build summary line reappeared.

## Staff-Level Discussion

`middleware.ts` is deprecated file-naming from a prior major version, and the framework even ships an automated codemod precisely because this is a known, common, real upgrade pitfall — the organizational lesson is that a clean build with no errors is not the same as "nothing changed," and any major-version upgrade needs an explicit check of the build's own reported summary against what's actually expected, not just an absence-of-errors check.

## Related Handbook Chapters

- [Next.js Proxy and Edge Runtime](../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md) — the canonical `middleware.ts` → `proxy.ts` rename behind this incident's fix.
