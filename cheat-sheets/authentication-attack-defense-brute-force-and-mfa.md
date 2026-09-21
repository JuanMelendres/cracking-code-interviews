---
title: "Cheat Sheet: Authentication Attack Defense"
slug: authentication-attack-defense-brute-force-and-mfa
document_type: cheat-sheet
domain: 12-security
topic_id: T-1310
canonical: ../syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md
last_updated: 2026-09-21
---

# Authentication Attack Defense: Brute Force, Credential Stuffing, and MFA

**Canonical chapter:** [`syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md`](../syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md)

## Core Mental Model

Two attacker positions, two different controls. Doesn't have a valid credential yet (brute force, password spraying) — defend with rate limiting/lockout. Already has one, stolen from elsewhere (credential stuffing) — lockout sees zero failures and can't help; only MFA closes this.

## Essential Definitions

- **Brute force** — many password guesses against one account.
- **Credential stuffing** — one valid, already-correct password (from an unrelated breach), replayed unmodified across many accounts; no guessing.
- **Account lockout** — locks an account after N failed attempts; stops brute force, but is itself a denial-of-service lever if keyed by username alone.
- **TOTP (RFC 6238)** — a deterministic function of a shared secret and the current 30-second time window, not a random code.

## Decision Table

| Threat | Detects via | Correct defense | Blind spot |
|---|---|---|---|
| Brute force / spraying | High failed-attempt volume | Rate limiting, account lockout | Distributed/low-and-slow guessing |
| Credential stuffing | Zero failed attempts on the successful login; login velocity across distinct usernames | MFA (TOTP) | Real-time phishing that relays a live code |
| Lockout's own DoS risk | Legitimate user locked out with no memory of failing | Per-IP tracking, CAPTCHA alongside lockout | — |

## Common Pitfalls

- Treating "we have account lockout" as complete authentication defense — it does nothing against credential stuffing, since the attacker's request never fails.
- Believing TOTP codes are random — they're `HOTP(secret, floor(unixTime/30))`, deterministic, computed independently on both sides.
- Keying lockout by username alone with no per-IP/CAPTCHA layer — turns lockout into an easy DoS vector against a known user.

## Interview Answer Skeleton

**30-sec:** Brute force guesses many passwords against one account; lockout stops it. Credential stuffing reuses one already-valid stolen password across many accounts and generates zero failures, so lockout can't see it — only MFA closes that gap.

**2-min:** Add: lockout's real cost is it can be turned into a DoS lever against the exact user it protects (keyed by username); production layers per-IP tracking or CAPTCHA alongside it. Real demo: a from-scratch TOTP implementation verified against all 5 of RFC 6238's official test vectors, then a credential-stuffing attacker with the *correct* password still rejected without the code.

**Staff-level framing:** Which account tiers get mandatory MFA is a risk/friction trade-off, not a purely technical one — support-cost of lost-device recovery vs. the fact that password reuse across services is now close to universal.

## Related

- syllabus/12-security/owasp-top-10-for-backend-services.md
- syllabus/12-security/authn-authz-rbac-vs-abac.md
- syllabus/12-security/oauth2-oidc-and-jwt.md
