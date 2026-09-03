---
title: "Cheat Sheet: Next.js Metadata API and SEO"
slug: nextjs-metadata-api-and-seo
document_type: cheat-sheet
domain: frontend
topic_id: F-209
tier: Intermediate
canonical: ../handbook/frontend/nextjs-metadata-api-and-seo.md
last_updated: 2026-09-03
---

# Next.js Metadata API and SEO

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md`](../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md)

Completes an open thread from F-206 (Streaming), whose own bot-blocking finding used only static metadata and was never actually exercised against a slow `generateMetadata`.

## Core Mental Model

Metadata resolution runs on the same two-lane system F-206 established for page content: a fast lane for normal clients, and a slower, blocking lane specifically for bots/crawlers that need `<head>` metadata present in the FIRST response because they don't execute JS or wait for streamed updates. For a normal request, page content streams immediately and metadata is injected separately once ready. For a bot User-Agent, the ENTIRE response — headers included — is held back until `generateMetadata` resolves.

## Essential Definitions

- **Static `metadata` object** — for `<head>` content independent of request-time data.
- **`generateMetadata`** — an async function for content depending on route params, search params, or fetched data; Server Component only (must resolve before the page renders).
- **`metadataBase`** — lets URL-based fields use short relative paths, composed into a fully-qualified URL.
- **`title.default` / `title` (string) / `title.absolute`** — verbatim for a childless title / templated by the nearest parent's `title.template` / bypasses the template entirely.

## Decision Table

| Question | Choice |
|---|---|
| Title/description/OG data depends on nothing but static, build-known content? | Static `metadata` object |
| Depends on route params, search params, or a real fetch? | `generateMetadata` |
| Using any relative URL in a metadata field? | Set `metadataBase` explicitly, and verify it in CI — don't trust a clean build as proof |
| Route's metadata involves a slow fetch and bot indexing matters? | Budget `generateMetadata`'s latency — bots experience the FULL delay as a blocked response |

## Key Numbers (real, curl'd and chunk-timed against a clean production server)

- Normal request to a route with a 1200ms `generateMetadata` delay: content in the first chunk at +44ms; metadata-bearing chunks afterward at +1243ms.
- Same route, `Twitterbot/1.0` User-Agent: response headers didn't arrive until +1246ms — the entire response blocked.
- Missing `metadataBase`: NOT a build error (despite the framework's own docs saying it "will cause a build error") — a real warning, plus a wrong `http://localhost:3000` fallback baked into a static page's actual production HTML (app runs on port 5198).

## Common Pitfalls

- Assuming a missing `metadataBase` is a build-time safety net that fails CI — it's a warning only, with a silently wrong fallback URL baked into real output.
- Assuming bots get the same fast, progressively-enhanced experience as browsers — they get the opposite: a fully blocking response.
- Expecting `title.template` set on a `page.js` (rather than a `layout.js`) to do anything — it has no effect there since a page is always a terminating segment.

## Interview Answer Skeleton

**30-sec:** Metadata resolves statically or via async `generateMetadata`, merging `title`/`title.template`/`title.absolute` down the layout tree. For dynamic pages, metadata streams separately from content for normal browsers but BLOCKS the entire response for bot User-Agents — verified with a real +1246ms header delay for a bot vs. +44ms for a normal request.

**2-min:** Cover the three real, distinct `title` merging outcomes, the `metadataBase` real (warning, not error) failure mode with the wrong baked-in URL, and the central chunk-timing contrast completing F-206's earlier, incomplete finding.

**Whiteboard:** Request to a dynamic route with slow `generateMetadata`, split by User-Agent: normal browser path shows content streaming fast (+44ms) then metadata later (+1243ms); bot path shows one single arrow starting only after the full delay (+1246ms) carrying both together. Separate small box: relative OG path through `metadataBase` set (correct URL) vs. missing (warning + wrong fallback URL).

**Staff-level framing:** Documentation prose ("will cause a build error") is a claim about intent, not verified current behavior — treat "the docs say X will error" as a hypothesis to test against real, current build output, and propose a concrete mitigation (a CI grep on the warning string) rather than just noting the discrepancy.

## Production Warning Signs

- OG images silently broken in production because `metadataBase` was never set — no build failure, only a shared social link revealing a broken preview pointing at a dev-only `localhost:3000` URL.
- The fix: set `metadataBase` explicitly, and grep `next build` output for the warning string as a real CI gate, since the framework itself won't fail the build.
- A slow, unbounded fetch inside `generateMetadata` for a page bots are expected to crawl directly costs crawl latency, since bot requests block on full resolution time.

## Related

- `syllabus/21-frontend-web/nextjs-streaming-and-suspense.md`
- `syllabus/21-frontend-web/nextjs-route-handlers.md`
- `syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md`
