---
title: "Cheat Sheet: CSRF, CORS, and Session Security"
slug: csrf-cors-and-session-security
document_type: cheat-sheet
domain: 12-security
topic_id: T-1308
canonical: ../syllabus/12-security/csrf-cors-and-session-security.md
last_updated: 2026-09-11
---

# CSRF, CORS, and Session Security

**Canonical chapter:** [`syllabus/12-security/csrf-cors-and-session-security.md`](../syllabus/12-security/csrf-cors-and-session-security.md)

## Core Mental Model

Three separate boxes. CSRF: writes the browser sends without being asked (cookie attaches regardless of triggering page). CORS: reads a browser normally refuses (an opt-in relaxation for specific origins). Session fixation: identity trusted from the wrong source (a client-supplied session ID instead of a server-generated one). None substitutes for the others.

## Essential Definitions

- **CSRF** — exploits automatic, origin-agnostic cookie attachment; fixed by a synchronizer token the forging site never had access to read.
- **CORS** — a server-granted, opt-in relaxation of the browser's same-origin read restriction for specific origins.
- **Session fixation** — the server accepting a client-supplied session identifier instead of generating its own at authentication.

## Decision Table

| Risk | What it exploits | Correct defense | Wrong tool trap |
|---|---|---|---|
| CSRF | Automatic, origin-agnostic cookie attachment | Synchronizer token, verified server-side on every state-changing request | Believing CORS provides this protection |
| CORS misconfiguration | Reflecting any origin + credentialed access | Explicit origin allowlist | Wildcard `*` with `Access-Control-Allow-Credentials: true` |
| Session fixation | Server trusting a client-supplied session ID | Regenerate session ID at every successful authentication | Relying on identifier strength/randomness alone |

## Common Pitfalls

- Believing a strict CORS policy stops CSRF — it doesn't; CORS governs reads, CSRF is about writes.
- Believing a CSRF token stops session fixation, or vice versa — the three risks are fully independent.
- Reflecting the request's `Origin` header verbatim in `Access-Control-Allow-Origin` combined with `Allow-Credentials: true` — effectively a wildcard with credentials, a real vulnerability.

## Interview Answer Skeleton

**30-sec:** CSRF exploits automatic cookie attachment on writes; CORS is an opt-in relaxation of browser-enforced reads; session fixation is trusting a client-supplied session ID. None of the three defenses substitutes for another.

**2-min:** Add: a real demo proves an identical forged cross-site request succeeds against a cookie-only endpoint and fails (403) against a synchronizer-token-protected one; a real session-fixation attack is closed specifically by session-ID regeneration on login, not by anything CSRF- or CORS-related.

**Staff-level framing:** Treating these three as one "web security" bucket, rather than three independent failure modes each needing its own specific defense, is the exact conflation this chapter exists to correct.

## Related

- syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md
- syllabus/12-security/owasp-top-10-for-backend-services.md
