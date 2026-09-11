---
title: "Flashcards: Frontend Security (XSS, CSRF, and CSP)"
slug: frontend-security-xss-csrf-and-csp
document_type: flashcard-deck
domain: 21-frontend-web
topic_id: F-401
canonical: ../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md
last_updated: 2026-09-11
---

# Flashcards: Frontend Security (XSS, CSRF, and CSP)

**Canonical chapter:** [`syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md`](../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md)

## Card: What actually makes XSS execute

**Prompt:**
What's the real, mechanical reason an XSS payload executes in a browser?

**Answer:**
Untrusted input reaches the page unescaped, so the browser's HTML parser treats it as real markup (a tag, an attribute) instead of literal text — proven directly in this chapter's own demo, where an unescaped `<img onerror>` payload really executes, and the identical, escaped payload renders as visible text instead.

**Why it matters:**
Distinguishes a real, mechanical understanding from reciting "sanitize your inputs."

**Common trap:**
Assuming XSS is about "malicious code" in the abstract, rather than specifically about untrusted data landing in a markup-parsing position.

**Related:**
[Frontend Security: XSS, CSRF, and CSP](../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md)

## Card: CSP is real browser enforcement, not an app-level filter

**Prompt:**
Does a Content-Security-Policy header change what the server sends, or what the browser will execute?

**Answer:**
What the browser will execute. The server sends the identical HTML either way — CSP is a real, browser-engine-enforced restriction, proven directly in this chapter's own demo: the identical inline script runs with no CSP header and is genuinely blocked (with a real, captured browser console violation) when the CSP header is present.

**Why it matters:**
Clarifies that CSP is enforcement by the browser, independently verifiable — not application logic that could silently fail to run.

**Common trap:**
Treating CSP as equivalent to server-side input sanitization, rather than a genuinely separate, browser-side enforcement layer.

**Related:**
[Frontend Security: XSS, CSRF, and CSP](../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md)
