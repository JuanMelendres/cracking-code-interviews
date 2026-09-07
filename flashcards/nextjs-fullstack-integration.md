---
title: "Flashcards: Full-Stack Integration: Next.js with a Separate Java/Spring Backend"
slug: nextjs-fullstack-integration
document_type: flashcard-deck
domain: frontend
topic_id: F-214
tier: Expert
canonical: ../syllabus/21-frontend-web/nextjs-fullstack-integration.md
last_updated: 2026-09-07
---

# Flashcards: Full-Stack Integration: Next.js with a Separate Java/Spring Backend

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-fullstack-integration.md`](../syllabus/21-frontend-web/nextjs-fullstack-integration.md)

## Card: Does CORS block a `curl` request the way it blocks a browser's `fetch()`?

**Prompt:**
If a server has no CORS configuration, does a `curl` request to it fail the same way a browser's `fetch()` call does?

**Answer:**
No — verified with a real, direct contrast. The same unconfigured endpoint returned a real, successful response to `curl` every time, while a real browser `fetch()` call genuinely failed with `TypeError: Failed to fetch` and an exact CORS-policy console error. CORS is enforced entirely by the browser after receiving the response; curl has no such enforcement at all.

**Why it matters:**
This is the real mechanism behind "works in curl, fails in the browser" bug reports — the server did nothing wrong from its own perspective in either case.

**Common trap:**
Debugging the server when the actual failure is a browser-side enforcement decision the server never sees.

**Related:**
[Full-Stack Integration: Next.js with a Separate Java/Spring Backend](../syllabus/21-frontend-web/nextjs-fullstack-integration.md) [Route Handlers: Building a Backend-for-Frontend Layer in Next.js](../syllabus/21-frontend-web/nextjs-route-handlers.md)

## Card: Can JavaScript read the `Access-Control-Allow-Origin` header from a successful cross-origin `fetch()`?

**Prompt:**
After a cross-origin `fetch()` succeeds (CORS allowed it through), can the calling JavaScript read the response's own `Access-Control-Allow-Origin` header value?

**Answer:**
No, not by default — verified directly. `curl` confirmed the header was genuinely present on the wire, but the SAME successful browser `fetch()` call's `res.headers.get('access-control-allow-origin')` returned `null`. Only a small default set of headers (like `Content-Type`) is exposed to script unless the server adds `Access-Control-Expose-Headers`.

**Why it matters:**
A real, easy trap for anyone trying to branch client-side logic on a CORS header's own value.

**Common trap:**
Assuming a CORS-permitted response exposes ALL of its headers to the calling script.

**Related:**
[Full-Stack Integration: Next.js with a Separate Java/Spring Backend](../syllabus/21-frontend-web/nextjs-fullstack-integration.md)
