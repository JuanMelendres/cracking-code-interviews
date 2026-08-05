---
title: "JWT Revocation Gap After Account Suspension"
document_type: production-cookbook-entry
domain: security
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/security/oauth2-oidc-and-jwt.md
source: handbook/security/oauth2-oidc-and-jwt.md#production-scenarios
---

# JWT Revocation Gap After Account Suspension

## Context

The system issues JWT access tokens with an 8-hour expiry, chosen for user convenience (fewer re-logins). Verification is a pure signature check with no external lookup, by design — the entire point of the JWT format here is avoiding a stateful check on every request.

## Symptoms

A user's account is suspended for fraudulent activity. Despite the suspension being immediately visible in the admin dashboard, the user's existing API session continues functioning normally for several more hours before finally failing.

## Impact

A suspended, potentially fraudulent account retains functional access well past the intended suspension point — a real security gap, not a display bug.

## Initial Hypotheses

- A caching layer serving stale suspension status — checked and ruled out; the suspension flag itself is read fresh on every relevant check.
- A bug in the suspension logic — checked and ruled out; the suspension correctly updates the user record immediately.
- The JWT's inherent non-revocability — correct.

## Evidence

The access token issued before suspension has a stated 8-hour expiry, and system logs confirm every request during the gap window passed JWT signature verification successfully — verification never touched the (correctly updated) suspended-user flag at all.

## Investigation Timeline

1. **Gap reported**: suspension visible in the admin dashboard, but API access continuing for hours afterward.
2. **Caching and suspension-logic hypotheses ruled out** by confirming the suspension flag itself updates correctly and immediately.
3. **Token lifetime checked against the gap window**: the observed access duration matches the token's stated 8-hour expiry exactly.
4. **Verification path confirmed to skip suspension entirely**: logs show every request in the gap window passing signature verification, with no suspension check anywhere in that path.

## Root Cause

JWT verification is, as designed, a pure signature check with no external lookup. The 8-hour expiry, chosen for convenience, directly determined the multi-hour window during which a suspended account's token remained fully functional, because nothing about suspension is checked during verification.

## Immediate Mitigation

Manually and forcibly terminate active sessions for the specific suspended account via an out-of-band mechanism — for example, rotating the signing key scoped to that user if supported, or another emergency-only mechanism.

## Permanent Fix

Reduce access-token expiry substantially (for example, to 15 minutes) paired with a refresh-token flow, bounding the maximum exposure window for any future suspension to a much smaller, explicitly accepted interval. For suspension specifically — a security-critical, low-frequency event — add a targeted deny-list check that only suspension-related code paths consult, rather than reintroducing a lookup on every request.

## Alternatives Considered

A full deny-list checked on every request. Rejected as the default choice — it reintroduces the stateful lookup cost the JWT format was chosen to avoid for the overwhelming majority of requests that never involve a suspension. Reserved instead for the specific, rare suspension case via the narrower mechanism above.

## Trade-offs

Shorter access-token expiry increases the frequency of refresh-token exchanges. Accepted, since the alternative is an unacceptably long exposure window for exactly the security-critical event — suspension — that matters most.

## Prevention

Any system issuing long-lived JWTs for security-sensitive operations should explicitly evaluate and document the maximum acceptable exposure window for account suspension or compromise, and choose expiry (or a targeted deny-list) accordingly — not default to a long expiry purely for convenience without that trade-off being made consciously.

## Monitoring and Alerts

- Time-to-effective-suspension as its own tracked metric (the gap between "suspension recorded" and "suspended account's last successful authenticated request"), not just "suspension recorded successfully" — the bug here was invisible to any alert that only checked the suspension write path.
- An audit log entry, and ideally an alert, on any authenticated request from an account flagged suspended, even if the request succeeded — this surfaces the exposure window directly rather than requiring it to be discovered by a user complaint.

## Interview Story

This maps to the "explain JWT revocation honestly" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a suspended account retained working API access for several hours after suspension.
- **Task:** explain why a correctly implemented suspension flag didn't stop access, and fix the exposure window.
- **Action:** rule out caching and suspension-logic bugs; match the exposure window to the token's stated expiry; explain that stateless JWT verification, by design, has no suspension check in its path.
- **Result:** shortened access-token expiry with a refresh-token flow, and added a targeted deny-list for the suspension case specifically, rather than reverting to a stateful check on every request.

## Staff-Level Discussion

Nothing here was a bug in the strict sense — the JWT verified correctly, exactly as its stateless design specifies. The actual failure was organizational: the 8-hour expiry was chosen for convenience without anyone explicitly reasoning about the worst-case exposure window it created for a security-critical event like suspension. This is a recurring Staff-level pattern in security design: a locally reasonable default (long expiry, fewer re-logins) can be globally wrong once you name the specific adversarial case it interacts badly with. The permanent fix deliberately avoids the blunt instrument (a deny-list on every request) in favor of a narrow one scoped to the rare, security-critical path — preserving the JWT format's performance benefit for the 99.9% of requests that never involve a suspension, while closing the gap for the case that actually matters.

## Related Handbook Chapters

- [OAuth2, OIDC, and JWT](../handbook/security/oauth2-oidc-and-jwt.md) — canonical token-verification and revocation-trade-off mechanics used here.
- [AuthN/AuthZ: RBAC vs. ABAC](../handbook/security/authn-authz-rbac-vs-abac.md) — the authorization model suspension is a case of.
