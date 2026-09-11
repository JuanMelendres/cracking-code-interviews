---
title: "Flashcards: CSRF, CORS, and Session Security"
slug: csrf-cors-and-session-security
document_type: flashcard-deck
domain: 12-security
topic_id: T-1308
canonical: ../syllabus/12-security/csrf-cors-and-session-security.md
last_updated: 2026-09-11
---

# Flashcards: CSRF, CORS, and Session Security

**Canonical chapter:** [`syllabus/12-security/csrf-cors-and-session-security.md`](../syllabus/12-security/csrf-cors-and-session-security.md)

## Card: Does CORS stop CSRF?

**Prompt:**
Does a strict CORS policy protect against CSRF?

**Answer:**
No — CORS governs whether a browser lets JavaScript *read* a cross-origin response. CSRF exploits the browser automatically *sending* a request (with cookies attached) regardless of which page triggered it. A strict CORS policy says nothing about whether the forged write request was sent or executed.

**Why it matters:**
One of the most common security-topic conflations — treating CORS as a general cross-site defense.

**Common trap:**
Believing "we have CORS configured" answers "are we protected against CSRF."

**Related:**
[CSRF, CORS, and Session Security](../syllabus/12-security/csrf-cors-and-session-security.md)

## Card: The dangerous CORS misconfiguration

**Prompt:**
Why is reflecting the request's `Origin` header back verbatim in `Access-Control-Allow-Origin`, combined with `Access-Control-Allow-Credentials: true`, dangerous?

**Answer:**
It's effectively an unrestricted wildcard that also allows credentialed requests (cookies, auth headers) — any origin can make an authenticated cross-origin request and read the response.

**Why it matters:**
A real, common misconfiguration pattern, not a rare edge case.

**Common trap:**
Assuming reflecting the origin is safer than a literal `*` wildcard.

**Related:**
[CSRF, CORS, and Session Security](../syllabus/12-security/csrf-cors-and-session-security.md)

## Card: The one real fix for session fixation

**Prompt:**
What's the one change that closes a session-fixation vulnerability?

**Answer:**
Regenerate the session identifier at every successful authentication — never accept or continue using a session ID the client supplied before login. Identifier strength/randomness alone doesn't help if the server trusts a pre-authentication client-supplied ID.

**Why it matters:**
A specific, real fix, verified in this chapter with a real attack closed by exactly this change.

**Common trap:**
Assuming a sufficiently random session ID is safe even without regeneration on login.

**Related:**
[CSRF, CORS, and Session Security](../syllabus/12-security/csrf-cors-and-session-security.md)
