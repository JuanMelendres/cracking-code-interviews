---
title: "T-512/T-513 · OAuth2, OIDC, and JWT"
topic_id: T-512/T-513
domain: Security
tier: Advanced
iwi: 7.15
prerequisites: [T-511]
unlocks: []
week: 7
last_reviewed: 2026-07-30
canonical: ../../handbook/security/oauth2-oidc-and-jwt.md
---

# T-512 / T-513 · OAuth2, OIDC, and JWT

**IWI 7.15 · Advanced tier**

**Canonical chapter:** [OAuth2, OIDC, and JWT](../../handbook/security/oauth2-oidc-and-jwt.md). This file is the Week 7 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `08-design-exercise-authentication-service.md` cites §4 directly.

**Verification note:** the JWT sign/verify/tamper/expiry behavior behind this summary is real, executed HMAC-SHA256 cryptography via `javax.crypto` — genuine signature bytes, genuine mismatch detection. The OAuth2/OIDC flow is conceptual: a faithful multi-party demo (authorization server, resource server, client, real redirect flow) was out of scope for this pack's time budget, stated explicitly here rather than simulated as if it were executed.

## Table of Contents

1. [OAuth2 and OIDC — the concept](#1-oauth2-and-oidc--the-concept)
2. [Authorization Code + PKCE, walked through](#2-authorization-code--pkce-walked-through)
3. [JWT mechanics, reproduced](#3-jwt-mechanics-reproduced)
4. [Why you cannot revoke a JWT](#4-why-you-cannot-revoke-a-jwt)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. OAuth2 and OIDC — the concept

OAuth2 is an authorization framework (what can this client do); OIDC is an identity layer on top (who is this user, via an ID token). Frequently conflated; the precise distinction is authorization vs. authentication/identity. → [Definition and Purpose](../../handbook/security/oauth2-oidc-and-jwt.md#definition-and-purpose).

## 2. Authorization Code + PKCE, walked through

The modern default grant: client generates a verifier/challenge pair, user authenticates directly with the authorization server, client exchanges the code plus verifier for tokens. PKCE protects the authorization code from interception, a different attack surface than a client secret protects. → [Internal Implementation](../../handbook/security/oauth2-oidc-and-jwt.md#internal-implementation) has the full flow.

## 3. JWT mechanics, reproduced

Measured: real HMAC-SHA256 sign and verify (VALID); tamper with the payload (INVALID, signature mismatch); expired token (INVALID, expired). Verification is a pure computation over the token's own bytes, never a database lookup. → [Internal Implementation](../../handbook/security/oauth2-oidc-and-jwt.md#internal-implementation) has all three traces.

## 4. Why you cannot revoke a JWT

Measured: a token for a deleted/compromised user still verifies as VALID until natural expiry, because verification never looks anything up. Two honest mitigations: short expiry + refresh tokens (bounds exposure, doesn't enable revocation), or a deny-list (solves revocation, reintroduces statefulness). → [Core Concepts](../../handbook/security/oauth2-oidc-and-jwt.md#core-concepts).

## 5. Trade-offs

Authorization Code + PKCE costs more round-trips for better protection; JWTs are stateless but unrevocable before expiry; short expiry bounds exposure at the cost of more refresh traffic; a deny-list solves revocation but reintroduces the lookup JWTs were meant to avoid. → [Trade-offs](../../handbook/security/oauth2-oidc-and-jwt.md#trade-offs).

## 6. Interview questions

1. Explain JWT revocation honestly.
2. Why PKCE if you already have a client secret?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/security/oauth2-oidc-and-jwt.md#interview-questions).

## 7. Common mistakes

Conflating OAuth2 with OIDC; claiming JWTs can be revoked without naming the stateful mechanism required; treating PKCE and a client secret as solving the same problem. → [Common Mistakes](../../handbook/security/oauth2-oidc-and-jwt.md#common-mistakes).

## 8. Staff-level discussion

JWT revocation is a specific instance of the stateless-vs-stateful trade-off recurring throughout this project — caching staleness, CAP consistency-vs-availability, and here, statelessness vs. revocability. → [Staff-Level Discussion](../../handbook/security/oauth2-oidc-and-jwt.md#interview-answer-framework).

## 9. Summary

OAuth2 answers authorization, OIDC layers identity on top. A JWT's verification is a pure computation over its own bytes — real, demonstrated tamper and expiry detection — which means it structurally cannot be revoked before expiry without a stateful deny-list. → [Summary](../../handbook/security/oauth2-oidc-and-jwt.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/security/oauth2-oidc-and-jwt.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/security/oauth2-oidc-and-jwt.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/security/oauth2-oidc-and-jwt.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/security/oauth2-oidc-and-jwt.md#practice-exercises) and [Solutions](../../handbook/security/oauth2-oidc-and-jwt.md#solutions). Reproducible demo: `practice/java/week-07/security/src/JwtDemo.java`.

## 14. Additional Reading

- [Auth0 — PKCE explained](https://auth0.com/docs/get-started/authentication-and-authorization-flow/authorization-code-flow-with-pkce)

## 15. Official References

- [RFC 6749 — The OAuth 2.0 Authorization Framework](https://www.rfc-editor.org/rfc/rfc6749)
- [RFC 7519 — JSON Web Token (JWT)](https://www.rfc-editor.org/rfc/rfc7519)
- [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html)
