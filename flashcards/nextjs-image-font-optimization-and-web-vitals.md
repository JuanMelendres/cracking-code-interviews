---
title: "Flashcards: Image and Font Optimization, and Core Web Vitals in Next.js"
slug: nextjs-image-font-optimization-and-web-vitals
document_type: flashcard-deck
domain: frontend
topic_id: F-210
tier: Intermediate
canonical: ../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md
last_updated: 2026-09-07
---

# Flashcards: Image and Font Optimization, and Core Web Vitals in Next.js

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md`](../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md)

## Card: Component vs. endpoint behavior for a disallowed image quality

**Prompt:**
If a `quality` prop value isn't in `next.config.js`'s `images.qualities` allowlist, does the `<Image>` component error?

**Answer:**
No. It silently clamps to the nearest allowed value — verified directly: `quality={90}` (with only `[75]` allowlisted) rendered a real `<img>` tag with `q=75`, not `90`, with no error or warning anywhere. The RAW `/_next/image` endpoint, hit directly with `q=90`, DOES hard-reject with a real `400`.

**Why it matters:**
A developer trusting the prop value alone, without inspecting real rendered output, would never notice the quality wasn't what they intended.

**Common trap:**
Assuming a disallowed value produces a visible error, missing the component's real, silent fallback behavior.

**Related:**
[Image and Font Optimization, and Core Web Vitals in Next.js](../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md)

## Card: Is `priority`'s deprecation loud or silent?

**Prompt:**
Next.js 16 deprecated the `priority` prop in favor of `preload`. Does using the old prop produce a warning?

**Answer:**
No — verified directly by checking a real `next dev` browser console and a complete real `next build` output; neither mentioned "priority" at all. The prop still fully works. This is a genuinely silent deprecation, in direct, real contrast to this app's F-208 chapter's Edge Runtime deprecation, which produces a loud, named warning every time.

**Why it matters:**
Not all deprecations in the same framework version behave consistently — relying on tooling to always self-report deprecated usage is a real, disprovable assumption.

**Common trap:**
Assuming a functioning prop with zero visible warnings must still be current/correct.

**Related:**
[Image and Font Optimization, and Core Web Vitals in Next.js](../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md) [Proxy (formerly Middleware) & the Edge Runtime in Next.js 16](../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md)
