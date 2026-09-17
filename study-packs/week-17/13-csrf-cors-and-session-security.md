---
title: "T-1308 · CSRF, CORS, and Session Security"
topic_id: T-1308
domain: Security
tier: Core
prerequisites: [T-1302]
week: 17
last_reviewed: 2026-09-17
canonical: ../../syllabus/12-security/csrf-cors-and-session-security.md
---

# T-1308 · CSRF, CORS, and Session Security

**Addendum topic — added 2026-09-17.** This chapter was written 2026-09-10, after Week 17's original "Security Domain Closure" sprint (2026-08-02, `01`–`07` above) had already shipped and closed. It was never folded back into this week's required reading or hands-on lab — a real scheduling gap, closed by this file. The original seven-topic sprint (`README.md`, `MANIFEST.md`) is left untouched as an accurate historical record of that specific session; this file and `14-enterprise-sso-saml-and-federated-identity.md` are appended alongside it.

**Canonical chapter:** [CSRF, CORS, and Session Security](../../syllabus/12-security/csrf-cors-and-session-security.md). This file is this week's study-pack entry point for it — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the demo behind this summary is real, executed output from `practice/java/week-17/csrf-cors-session/` (pure JDK `com.sun.net.httpserver.HttpServer`/`java.net.http.HttpClient`, no external dependencies) — re-run 2026-09-17 while writing this addendum, still passing.

## 1. The concept

Three genuinely different controls, routinely confused for one another: **CSRF** is about writes a browser sends without being asked (a cookie rides along automatically); **CORS** is about reads a browser normally refuses (an opt-in relaxation, not a request-blocking mechanism); **session fixation** is about identity the server trusted from the wrong source (a client-supplied session ID, not a server-generated one). None substitutes for either of the others. → [Mental Model](../../syllabus/12-security/csrf-cors-and-session-security.md#mental-model).

## 2. Why it exists

CSRF exploits a browser default (automatic, origin-agnostic cookie attachment) that has nothing to do with authentication being weak — the forged request carries a completely genuine session cookie. CORS is how a server opts specific origins *back in* to reading a response the same-origin policy would otherwise hide from their JavaScript. Session fixation is closed by one unconditional rule: authentication always mints a fresh session identifier, discarding anything the client presented beforehand. → [Definition and Purpose](../../syllabus/12-security/csrf-cors-and-session-security.md#definition-and-purpose).

## 3. The measured evidence

Real forged request: identical cookie, no CSRF token — `200 TRANSFERRED $500` against the vulnerable endpoint, `403 Blocked: missing or invalid csrfToken` against the fixed one. Real CORS header logic: `Access-Control-Allow-Origin` present only for the allowlisted `Origin`, with the demo's own printed caveat that a non-browser client (this one, or `curl`) always receives the body either way — CORS enforcement happens in the browser, not the server. Real session fixation: an attacker's pre-chosen session ID becomes a genuinely authenticated session against the vulnerable endpoint (`true`); the fixed endpoint ignores it and mints a fresh one (`false`), with `Set-Cookie` also carrying `HttpOnly; Secure; SameSite=Strict`. → [Internal Implementation](../../syllabus/12-security/csrf-cors-and-session-security.md#internal-implementation) has the full transcript.

## 4. Trade-offs

A synchronizer token adds real implementation surface (generate, embed, verify on every state-changing request) and can complicate page caching. A permissive CORS policy simplifies legitimate cross-origin consumption but expands which origins' JavaScript can read authenticated responses. Regenerating the session ID on every login is essentially free, but requires auditing every code path that reaches an authenticated state to confirm none skip it. → [Trade-offs](../../syllabus/12-security/csrf-cors-and-session-security.md#trade-offs).

## 5. Interview questions

1. A single-page app's API requires a custom header on state-changing requests and the team believes this alone is CSRF protection. Is it, and what's the fragile assumption?
2. A password-reset flow accepts a `sessionid` query parameter to "restore your session." What's wrong with this, independent of any specific attack demo?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups: → [Interview Questions](../../syllabus/12-security/csrf-cors-and-session-security.md#interview-questions).

## 6. Common mistakes

Believing a strict CORS policy stops CSRF (it doesn't — CORS governs response *reads*, not request *sends*). Treating CORS's absence as "protection" rather than simply "no origin opted in yet." Assuming a cryptographically strong session ID defends against fixation — it doesn't, since the attacker supplies the ID themselves rather than guessing it. → [Common Mistakes](../../syllabus/12-security/csrf-cors-and-session-security.md#common-mistakes).

## 7. Hands-on lab (new, this addendum)

```bash
cd practice/java/week-17/csrf-cors-session
mkdir -p out
javac -d out src/CsrfCorsSessionDemo.java
java -cp out CsrfCorsSessionDemo
```

Expected: the vulnerable endpoint's forged transfer succeeds (`200`), the fixed endpoint blocks it (`403`); the CORS section shows `Access-Control-Allow-Origin` present only for the allowlisted origin; the session-fixation section shows the attacker's pre-chosen ID becoming a real authenticated session against the vulnerable endpoint (`true`) and failing to against the fixed one (`false`).

- [ ] Lab reproduced with your own matching (not necessarily identical) real output
- [ ] Can explain why the forged request's session cookie is completely genuine, not stolen or guessed
- [ ] Can explain why this Java client receives the response body regardless of the `Origin` header, and why that's not a bug in the demo

## 8. Summary

CSRF, CORS, and session fixation are three structurally different controls governing different parts of a request's lifecycle — confusing them (assuming one substitutes for another) is the single most common real mistake. → [Summary](../../syllabus/12-security/csrf-cors-and-session-security.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/12-security/csrf-cors-and-session-security.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/12-security/csrf-cors-and-session-security.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/12-security/csrf-cors-and-session-security.md#flashcards).

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/12-security/csrf-cors-and-session-security.md#practice-exercises). Reproducible demo: `practice/java/week-17/csrf-cors-session/`.

## 13. Official References

- [OWASP: Cross-Site Request Forgery Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [OWASP: Session Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html)
- [MDN: Cross-Origin Resource Sharing (CORS)](https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CORS)
