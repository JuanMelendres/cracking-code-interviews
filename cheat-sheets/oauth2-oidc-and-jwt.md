---
title: "Cheat Sheet: OAuth2, OIDC, and JWT"
slug: oauth2-oidc-and-jwt
document_type: cheat-sheet
domain: security
topic_id: T-512/T-513
canonical: ../handbook/security/oauth2-oidc-and-jwt.md
last_updated: 2026-08-05
---

# OAuth2, OIDC, and JWT

**Canonical chapter:** [`syllabus/12-security/oauth2-oidc-and-jwt.md`](../syllabus/12-security/oauth2-oidc-and-jwt.md)

## Core Mental Model

A JWT's signature proves the bytes haven't changed since signing — it proves **nothing** about whether the world has changed since then. Verification is a pure function of the token's own content and the verifier's key; it never asks anything else a question. This single fact explains everything: tamper detection works because any byte change breaks the signature match; expiry checking works because expiry is part of the signed bytes; and revocation *doesn't* work, because there is no external state verification ever consults — "has this user been deleted" is a question about the world, and the token's bytes cannot know the answer.

## Essential Definitions

- **OAuth2** — an authorization framework: "what is this client allowed to do on behalf of a user," via an access token, without the client seeing the user's credentials.
- **OIDC** — an identity layer built on top of OAuth2: "who is this user," via an ID token (itself a JWT).
- **JWT** — three base64url segments (header.payload.signature); signature is a pure cryptographic computation over header + payload.
- **PKCE** — a verifier/challenge pair generated per-flow, protecting the *authorization code* from interception in transit — a different attack surface than a client secret protects.

## Decision Table

| Need | Reach for |
|---|---|
| Client acts on user's behalf, no credential sharing | OAuth2 access token |
| Know who the user is | OIDC ID token |
| Protect the authorization code in transit | PKCE — even for confidential clients |
| Bound exposure of a compromised token | Short expiry + refresh token |
| True, immediate revocation for a security-critical event | Targeted deny-list, not a blanket one |

**Trade-offs:**

| Approach | Benefit | Cost |
|---|---|---|
| Authorization Code + PKCE | Client never handles credentials; protects code-interception surface | More round-trips than a simpler grant |
| JWT for session state | Stateless — no server-side lookup to verify | Cannot be revoked before expiry without a stateful check |
| Short expiry + refresh token | Bounds exposure window | More moving parts (refresh endpoint, storage/rotation) |
| Deny-list for revocability | Solves revocation directly | Reintroduces the exact stateful lookup JWT was meant to avoid |

## Key Numbers (real, executed HMAC-SHA256 via `javax.crypto`)

```
Untampered token  -> Verification: VALID
Tampered payload  -> Verification: INVALID (signature mismatch)
Expired token     -> Verification: INVALID (expired)
Suspended-user's
still-valid token -> Verification: VALID  <-- nothing about deleting the user changes the token's bytes
```

The OAuth2/OIDC multi-party flow itself is conceptual (a real authorization-server/resource-server/client redirect demo was out of practice-budget scope) — stated explicitly rather than presented as executed. The JWT sign/verify/tamper/expiry behavior above is real, reproducible cryptography.

## Common Pitfalls

- Conflating OAuth2 (authorization) with OIDC (identity) as the same thing.
- Claiming a JWT "can be revoked" without naming the stateful mechanism that would actually be required.
- Treating PKCE and a client secret as solving the same problem — they protect different attack surfaces (code interception vs. token-exchange impersonation).

## Interview Answer Skeleton

**30-sec:** OAuth2 = authorization; OIDC = identity layered on top via an ID token. Authorization Code + PKCE is the modern default because PKCE protects the code from interception, a different surface than a client secret. A JWT's signature is pure computation — proves no tampering, cannot be revoked before expiry without a stateful deny-list.

**2-min:** Add why (delegated authorization, portable identity, compact self-verification are distinct problems) + the PKCE mechanism (verifier/challenge hash match) + the honest revocation answer (no external lookup exists in verification, period).

**Whiteboard:** Client generates verifier/challenge → redirect to AuthServer with challenge → user authenticates directly → redirect back with code → client exchanges code + verifier → AuthServer checks hash match → issues tokens. Circle the verifier-generation and hash-check steps, arrow labeled "must match" between them.

**Staff-level framing:** JWT revocation is the same class of trade-off as caching staleness and CAP's consistency-vs-availability choice — stateless vs. stateful, recurring across the handbook. Naming it as the same pattern in a new context is the signal, not memorizing each occurrence separately.

## Production Warning Signs

- A suspended/deleted user's session keeps working for hours after the suspension is visible in the admin dashboard — check the token's expiry against suspension time; this is the JWT behaving exactly as designed, not a bug.
- A mobile/SPA client's authorization code intercepted and exchanged by an attacker — missing PKCE; a client secret alone doesn't protect the code in transit.
- **Prevention:** any system issuing long-lived JWTs for security-sensitive operations should explicitly evaluate and document the maximum acceptable exposure window for suspension/compromise — don't default to long expiry purely for convenience without that trade-off being made consciously.

## Related

- `syllabus/12-security/authn-authz-rbac-vs-abac.md`
- `syllabus/05-spring/security-filter-chain.md`
- `production-cookbook/jwt-revocation-gap-after-account-suspension.md`
