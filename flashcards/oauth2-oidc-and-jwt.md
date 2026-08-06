---
title: "Flashcards: OAuth2, OIDC, and JWT"
slug: oauth2-oidc-and-jwt
document_type: flashcard-deck
domain: security
topic_id: T-512/T-513
canonical: ../handbook/security/oauth2-oidc-and-jwt.md
last_updated: 2026-08-06
---

# Flashcards: OAuth2, OIDC, and JWT

**Canonical chapter:** [`handbook/security/oauth2-oidc-and-jwt.md`](../handbook/security/oauth2-oidc-and-jwt.md)

## Card: OAuth2 vs OIDC

**Prompt:**
OAuth2 vs. OIDC, in one line each?

**Answer:**
OAuth2 = authorization (what can this client do); OIDC = identity (who is this user), built on OAuth2 via an ID token.

**Why it matters:**
The precise distinction most frequently conflated in practice.

**Common trap:**
Treating OAuth2 and OIDC as the same thing.

**Related:**
[Definition and Purpose](../handbook/security/oauth2-oidc-and-jwt.md#definition-and-purpose)

## Card: Why PKCE if you have a client secret

**Prompt:**
Why PKCE if you already have a client secret?

**Answer:**
They protect different attack surfaces — PKCE protects the authorization code in transit; the secret protects the token-exchange call.

**Why it matters:**
Prevents treating the two mechanisms as redundant.

**Common trap:**
Assuming a client secret alone makes PKCE unnecessary.

**Related:**
[Internal Implementation](../handbook/security/oauth2-oidc-and-jwt.md#internal-implementation)

## Card: Can a valid JWT be revoked

**Prompt:**
Can a valid, non-expired JWT be revoked?

**Answer:**
Not without a stateful deny-list check — verification alone is a pure computation over the token's own bytes.

**Why it matters:**
The honest, scoped answer to this domain's sharpest discriminating question.

**Common trap:**
Claiming JWTs can simply be revoked with no further mechanism.

**Related:**
[Internal Implementation](../handbook/security/oauth2-oidc-and-jwt.md#internal-implementation)

## Card: Honest JWT revocation mitigations

**Prompt:**
Two honest JWT-revocation mitigations?

**Answer:**
Short expiry + refresh tokens (bounds exposure), or a deny-list (solves it directly but reintroduces a stateful lookup).

**Why it matters:**
Neither mitigation is free — a Staff-level answer states both trade-offs explicitly.

**Common trap:**
Presenting one mitigation as if it were a complete, cost-free solution.

**Related:**
[Core Concepts](../handbook/security/oauth2-oidc-and-jwt.md#core-concepts)
