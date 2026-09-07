---
title: "Flashcards: Authentication Patterns in Next.js: DAL, JWT Sessions, and unauthorized()"
slug: nextjs-authentication-patterns
document_type: flashcard-deck
domain: frontend
topic_id: F-211
tier: Advanced
canonical: ../syllabus/21-frontend-web/nextjs-authentication-patterns.md
last_updated: 2026-09-07
---

# Flashcards: Authentication Patterns in Next.js: DAL, JWT Sessions, and unauthorized()

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-authentication-patterns.md`](../syllabus/21-frontend-web/nextjs-authentication-patterns.md)

## Card: Does Proxy's cookie-presence check alone stop a tampered session?

**Prompt:**
Does a Proxy check like `!request.cookies.has('session')` alone stop a genuinely tampered/forged session cookie?

**Answer:**
No — verified with a real, reproduced test. A genuinely tampered JWT passed straight through this naive check (confirmed via a real, present `x-proxy-hit` header), only getting caught by a separate DAL performing real cryptographic signature verification.

**Why it matters:**
Presence and validity are two independently-forgeable properties; a naive check confuses them, creating a real, exploitable gap this chapter reproduced directly.

**Common trap:**
Assuming a cookie's existence implies its contents are trustworthy.

**Related:**
[Authentication Patterns in Next.js: DAL, JWT Sessions, and unauthorized()](../syllabus/21-frontend-web/nextjs-authentication-patterns.md) [Proxy (formerly Middleware) & the Edge Runtime in Next.js 16](../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md)

## Card: Does `unauthorized()` always return a real 401 status?

**Prompt:**
With `authInterrupts` enabled, does calling `unauthorized()` always produce a real HTTP 401 status?

**Answer:**
No — verified with a real, three-way test. Inside an already-streaming `<Suspense>` boundary, the status stays a real `200` (the framework can't change a status line already sent), even though the correct digest, `noindex` meta tag, and custom `unauthorized.js` UI are all genuinely present. Only a check resolving BEFORE streaming starts (e.g., in a Route Handler) produced a genuine real `401`.

**Why it matters:**
Automated tooling that branches on HTTP status code (not response body) would misread a Suspense-boundary `unauthorized()` call as a successful `200` response.

**Common trap:**
Assuming the `authInterrupts` flag alone determines the returned status, rather than WHERE the check runs relative to streaming.

**Related:**
[Authentication Patterns in Next.js: DAL, JWT Sessions, and unauthorized()](../syllabus/21-frontend-web/nextjs-authentication-patterns.md) [Streaming & Suspense Boundaries in the App Router](../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md)
