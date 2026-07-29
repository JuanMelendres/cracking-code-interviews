---
title: "T-512/T-513 · OAuth2, OIDC, and JWT"
topic_id: T-512/T-513
domain: Security
tier: Advanced
iwi: 7.15
prerequisites: [T-511]
unlocks: []
week: 7
last_reviewed: 2026-07-29
---

# T-512 / T-513 · OAuth2, OIDC, and JWT

**IWI 7.15 · Advanced tier**

**Verification note:** the JWT sign/verify/tamper/expiry behavior in §3 is real, executed HMAC-SHA256 cryptography via `javax.crypto` — genuine signature bytes, genuine mismatch detection. The OAuth2/OIDC flow in §2 is conceptual: a faithful multi-party demo (authorization server, resource server, client, real redirect flow) was out of scope for this pack's time budget, stated explicitly here rather than simulated as if it were executed.

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

**OAuth2** is an authorization framework — it answers "what is this client allowed to do on behalf of a user," producing an access token scoped to specific permissions, without the client ever seeing the user's actual credentials. **OIDC (OpenID Connect)** is an identity layer built on top of OAuth2 — it answers the separate question "who is this user," via an additional ID token (itself a JWT) carrying identity claims. The two are frequently conflated; the precise distinction is authorization (OAuth2) versus authentication/identity (OIDC).

## 2. Authorization Code + PKCE, walked through

The Authorization Code grant with PKCE (Proof Key for Code Exchange) is the modern default for any client that can't securely hold a secret (a mobile app, a single-page app) — and increasingly the default even for server-side clients that can:

1. Client generates a random `code_verifier`, derives a `code_challenge` from it (a hash), and redirects the user to the authorization server with the `code_challenge`.
2. User authenticates with the authorization server directly — the client never sees the user's credentials.
3. Authorization server redirects back to the client with a short-lived `authorization_code`.
4. Client exchanges the `authorization_code` **plus the original `code_verifier`** for an access token (and, for OIDC, an ID token).
5. Authorization server verifies the `code_verifier` hashes to the `code_challenge` from step 1 before issuing the token.

**Why PKCE if you already have a client secret?** (this exact follow-up appears in the register): PKCE protects against a *different* attacker than a client secret does — an attacker who intercepts the authorization code (e.g., via a malicious app registered for the same redirect URI on a mobile OS) cannot exchange it for a token without also having the original `code_verifier`, which never left the legitimate client. A client secret protects the *token exchange* endpoint; PKCE protects the *authorization code* itself in transit.

## 3. JWT mechanics, reproduced

A JWT is three base64url-encoded segments — header, payload, signature — joined by dots. Verification is purely computational: recompute the signature over the header and payload using the shared secret (or public key, for asymmetric algorithms), and compare.

**Real output — issue and verify** (token redacted here — see `practice/java/week-07/security/README.md` for the full generated string from a demo-only secret):
```
Token: <base64url-header>.<base64url-payload>.<base64url-signature>
Verification: VALID
```

**Real output — tamper with the payload, signature no longer matches:**
```
Verification: INVALID (signature mismatch -- token was tampered with)
```

**Real output — expired token, signature still valid, but rejected anyway:**
```
Verification: INVALID (expired)
```

**Why this matters precisely:** a JWT's integrity guarantee is *only* about the header+payload not being altered since signing — it says nothing about whether the claims inside were true when issued, or whether they're still true now. Verification never consults a database; it's a pure function of the token's own bytes and the verifier's key.

## 4. Why you cannot revoke a JWT

**Real, demonstrated:** a token issued for a user, even if that user's account is deleted or compromised the very next instant, still verifies as `VALID` until it naturally expires — because verification never looks anything up:

```
Verification: VALID  <-- still VALID; nothing about deleting the user changes this token's bytes
```

**The honest two mitigations, named without pretending either is free:**

1. **Short expiry + refresh tokens.** Bound the exposure window to minutes, and require a separate (often stateful, checkable) refresh step to obtain a new access token. This doesn't make the access token revocable — it just shrinks how long a compromised one matters.
2. **A server-side deny-list, checked at verification time.** This directly solves revocability, but at the cost of reintroducing exactly the stateful lookup a JWT was chosen to avoid — at that point, the "stateless" benefit is gone for the specific operation of checking the deny-list, even if session *data* itself remains in the token.

## 5. Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| OAuth2 Authorization Code + PKCE | Client never handles user credentials; protects the code-interception attack surface | More round-trips than a simpler grant type |
| JWT for session/auth state | Stateless — no server-side lookup needed to verify | Cannot be revoked before expiry without reintroducing a stateful check |
| Short JWT expiry + refresh token | Bounds the exposure window of a compromised token | More moving parts (refresh endpoint, refresh-token storage/rotation) |
| Deny-list for true revocability | Solves revocation directly | Reintroduces the stateful lookup JWT was meant to avoid |

## 6. Interview questions

### Q1. Explain JWT revocation honestly.

- **Expected answer:** you cannot revoke a valid, non-expired JWT without a stateful check (deny-list), which undermines the statelessness that motivated using a JWT in the first place; the practical mitigations are short expiry + refresh tokens, or accepting the deny-list cost.
- **Common mistakes:** claiming JWTs "can be revoked" without naming the stateful mechanism required, implying revocation is free.
- **Follow-up questions:** "If you add a deny-list, what have you actually given up?"
- **Senior-level expectations:** correctly states JWTs can't be revoked without extra machinery.
- **Staff-level expectations:** names both mitigations and is explicit that a deny-list reintroduces the exact cost (a stateful lookup) the token format was chosen to avoid.

### Q2. Why PKCE if you already have a client secret?

- **Expected answer:** §2's answer — they protect against different attack surfaces (code interception vs. token-exchange impersonation).
- **Common mistakes:** treating PKCE and a client secret as redundant.
- **Follow-up questions:** "Does a mobile app typically have a client secret at all?"
- **Senior-level expectations:** states that PKCE protects the authorization code specifically.
- **Staff-level expectations:** notes that public clients (mobile, SPA) generally *can't* hold a secret securely at all, making PKCE not just complementary but often the *only* real protection available.

## 7. Common mistakes

- Conflating OAuth2 (authorization) with OIDC (authentication/identity) as the same thing.
- Claiming a JWT can be revoked without naming the stateful mechanism that would actually be required.
- Treating PKCE and a client secret as solving the same problem.

## 8. Staff-level discussion

The JWT revocation question is a specific instance of the stateless-vs-stateful trade-off that recurs throughout this programme — Week 4's caching (staleness tolerance), Week 5's CAP (consistency vs. availability), and here, statelessness vs. revocability. The Staff-level pattern-recognition signal is naming this as the *same class of trade-off* appearing in a new context, rather than treating each occurrence as an unrelated fact to memorize separately.

## 9. Summary

OAuth2 answers authorization, OIDC layers identity on top via an ID token. Authorization Code + PKCE is the modern default specifically because PKCE protects the authorization code in transit, a different attack surface than a client secret protects. A JWT's verification is a pure computation over its own bytes — real, demonstrated tamper detection and expiry checking — which means it structurally cannot be revoked before expiry without a stateful deny-list that undoes the point of using a stateless token in the first place.

## 10. Key Takeaways

- OAuth2 = authorization; OIDC = identity, built on OAuth2.
- PKCE protects the authorization code from interception; a client secret protects the token-exchange endpoint — different attack surfaces.
- JWT verification is pure computation — no database lookup, confirmed by real tamper and expiry tests.
- JWT revocation before expiry requires a stateful deny-list, which reintroduces the cost statelessness was meant to avoid.

## 11. Cheat Sheet

See §5's trade-off table.

## 12. Flashcards

1. **Q: OAuth2 vs. OIDC, in one line each?** A: OAuth2 = authorization (what can this client do); OIDC = identity (who is this user), built on OAuth2 via an ID token.
2. **Q: Why PKCE if you already have a client secret?** A: They protect different attack surfaces — PKCE protects the authorization code in transit; the secret protects the token-exchange call.
3. **Q: Can a valid, non-expired JWT be revoked?** A: Not without a stateful deny-list check — verification alone is a pure computation over the token's own bytes.
4. **Q: Two honest JWT-revocation mitigations?** A: Short expiry + refresh tokens (bounds exposure), or a deny-list (solves it directly but reintroduces a stateful lookup).

(Full week-level deck: `05-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce the JWT demo yourself: `practice/java/week-07/security/JwtDemo.java`.
2. Extend the demo to add a simple in-memory deny-list, and demonstrate that a previously-valid token is now correctly rejected once added to it — noting exactly what statefulness this reintroduces.
3. Diagram the Authorization Code + PKCE flow from §2 for a specific real or hypothetical client, labeling exactly where the `code_verifier` and `code_challenge` are generated, sent, and checked.

## 14. Additional Reading

- [Auth0 — PKCE explained](https://auth0.com/docs/get-started/authentication-and-authorization-flow/authorization-code-flow-with-pkce)

## 15. Official References

- [RFC 6749 — The OAuth 2.0 Authorization Framework](https://www.rfc-editor.org/rfc/rfc6749)
- [RFC 7519 — JSON Web Token (JWT)](https://www.rfc-editor.org/rfc/rfc7519)
- [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html)
