---
title: "Broken OG Image Preview From a Missing metadataBase"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md
source: syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md#production-scenarios
---

# Broken OG Image Preview From a Missing metadataBase

## Context

A team ships relative OG image paths across several pages, assuming a missing `metadataBase` would have been caught immediately as a build error.

## Symptoms

A marketing link shared on a social platform shows a broken image preview.

## Impact

Marketing content shared externally shows a broken preview to every viewer, undermining the campaign it supports.

## Initial Hypotheses

- The image file itself is missing or misnamed — checked, the file exists and is correctly referenced by path.
- A social platform's own caching of a previously-broken preview — checked, reproduces on a freshly-shared, never-before-shared URL.
- `metadataBase` was never set, and the build never failed to catch it — correct.

## Evidence

Inspecting the page's actual rendered `<head>` shows an `og:image` URL pointing at `http://localhost:3000/...` — a development-only fallback URL baked into the production build.

## Investigation Timeline

1. A shared marketing link shows a broken preview image on a social platform.
2. Image-file and platform-caching hypotheses ruled out.
3. Rendered `<head>` inspected directly, revealing a `localhost` URL baked into the production build's `og:image` tag.

## Root Cause

The team's assumption — that a missing `metadataBase` would be a loud build failure — is the exact naive reading the framework's real behavior contradicts: it's a real, easy-to-miss console warning during `next build`, not a failure that blocks a deploy.

## Immediate Mitigation

Set `metadataBase` explicitly and redeploy to fix the currently-broken shared links going forward (already-shared links with cached broken previews may not immediately refresh on the platform's side).

## Permanent Fix

Set `metadataBase` explicitly in the root layout, and treat the build-time warning as a real CI gate — grep build output for `metadataBase` and fail the pipeline if it appears, since the framework itself will not.

## Alternatives Considered

Relying on manual QA to check social-share previews before every release — rejected as unreliable and easy to skip under deadline pressure; an automated CI gate catches it every time with no reliance on a human remembering to check.

## Trade-offs

None meaningful — setting `metadataBase` is a one-line root-layout change with no downside.

## Prevention

Add a CI check grepping build output for the `metadataBase` warning and failing the pipeline if found, rather than relying on the assumption that a misconfiguration this significant would necessarily be a hard build failure.

## Monitoring and Alerts

- A CI step that fails the build if `next build`'s output contains the `metadataBase` warning string.
- A periodic automated check of key marketing pages' actual rendered OG tags against expected production URLs, catching drift even outside of deploy-time checks.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a shared marketing link showed a broken image preview in production.
- **Task:** find why a supposedly build-blocking misconfiguration reached production at all.
- **Action:** inspected the actual rendered `<head>`, found a `localhost` URL baked into the OG tag, and confirmed the missing-`metadataBase` warning was non-blocking.
- **Result:** set `metadataBase` explicitly and added a CI gate grepping for the warning string, so it can never silently reach production again.

## Staff-Level Discussion

The team's assumption ("a missing `metadataBase` would be a loud build failure") is the exact naive reading this chapter's real evidence contradicts — it's a real, easy-to-miss console warning, not a deploy-blocking error. The organizational lesson is that any framework behavior assumed to be "surely a hard failure" deserves direct verification before being relied upon, since the actual severity (warning vs. error) determines whether a real misconfiguration reaches production undetected.

## Related Handbook Chapters

- [Next.js Metadata API and SEO](../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md) — the canonical `metadataBase` build-warning behavior behind this incident.
