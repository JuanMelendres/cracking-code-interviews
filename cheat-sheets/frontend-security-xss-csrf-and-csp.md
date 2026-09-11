---
title: "Cheat Sheet: Frontend Security (XSS, CSRF, and CSP)"
slug: frontend-security-xss-csrf-and-csp
document_type: cheat-sheet
domain: 21-frontend-web
topic_id: F-401
canonical: ../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md
last_updated: 2026-09-11
---

# Frontend Security: XSS, CSRF, and Content Security Policy

**Canonical chapter:** [`syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md`](../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md)

## Core Mental Model

XSS executes because untrusted input reaches the page unescaped, so the browser's HTML parser treats it as real markup instead of literal text. CSP is a real, browser-enforced second layer — it changes what the browser will *execute*, not what the server sends. CSRF and CSP/XSS are independent defenses against independent risks.

## Essential Definitions

- **XSS** — untrusted data parsed as markup by the browser, not "malicious code" in the abstract.
- **CSP (`Content-Security-Policy`)** — a real, browser-enforced restriction on what scripts may run, verifiable directly via a real browser console violation.
- **CSRF token** — a per-session token verified server-side, defeating a forged cross-site request a cookie alone can't stop.

## Decision Table

| Need | Defense |
|---|---|
| Prevent untrusted data from being parsed as markup | Default output escaping (framework-provided) |
| A real, browser-enforced second layer against script execution | `Content-Security-Policy: script-src 'self'` (no `'unsafe-inline'`) |
| Prevent a forged, cookie-authenticated request | A per-session CSRF token, verified server-side |
| Stop a browser from attaching cookies to cross-site requests | `SameSite=Lax` or `SameSite=Strict` on the session cookie |
| Verify a CSP header is real and enforced, not just present | Check a real browser's console for a real violation on a deliberately-blocked action |

## Common Pitfalls

- Assuming XSS is about "malicious code" abstractly, rather than untrusted data landing in a markup-parsing position specifically.
- Treating CSP as equivalent to server-side sanitization rather than a genuinely separate, browser-side enforcement layer.
- Relying on `SameSite` cookies alone as a complete CSRF defense without a verified server-side token.

## Interview Answer Skeleton

**30-sec:** XSS executes because unescaped input is parsed as markup; CSP is a real, browser-enforced second layer (verifiable via console violations); CSRF needs a server-verified token, independent of XSS/CSP defenses.

**2-min:** Add: a real demo proves an unescaped `<img onerror>` payload genuinely executes while the escaped version renders as text, and an identical inline script runs with no CSP header but is genuinely blocked (real captured console violation) once `script-src 'self'` is set.

**Staff-level framing:** These are three independent risks needing three independent defenses — a strong CSP doesn't stop CSRF, and a CSRF token doesn't stop XSS.

## Related

- syllabus/12-security/csrf-cors-and-session-security.md
