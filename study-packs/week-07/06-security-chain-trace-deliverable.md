---
title: "security-chain-trace.md Deliverable"
week: 7
last_reviewed: 2026-07-31
---

# `security-chain-trace.md` Deliverable

**Trace one authenticated request through every filter, for a real or realistic system.** Use `02-spring-security-filter-chain.md`'s three real scenarios as the mechanical template, applied to a specific, concrete endpoint rather than the generic demo.

## Table of Contents

1. [Template](#1-template)
2. [Worked example](#2-worked-example)
3. [Exit check](#3-exit-check)

---

## 1. Template

```markdown
# Security Chain Trace — [Endpoint]

## Filters, in actual configured order
1. [Filter name] — [what it checks]
2. [Filter name] — [what it checks]
...

## Trace: successful request
[Step through each filter with a real or realistic request, showing what
each one does and what state it adds (e.g., principal set after auth).]

## Trace: a request that should be rejected -- at what point, and why
[Pick a specific rejection case relevant to this endpoint. Show exactly
which filter catches it and what response results.]

## Gaps found
[Any filter missing that should exist for this endpoint, or ordering that
doesn't follow the cheapest-first principle from 02-spring-security-filter-chain.md.]
```

## 2. Worked example

```markdown
# Security Chain Trace — POST /api/orders (illustrative)

## Filters, in actual configured order
1. CorsFilter -- rejects disallowed origins
2. RateLimitFilter -- rejects if this client IP exceeds 100 req/min
3. JwtAuthenticationFilter -- validates the Bearer token, sets principal
4. MethodSecurityFilter (@PreAuthorize("hasRole('CUSTOMER')")) -- role check

## Trace: successful request
Request from an allowed origin, under the rate limit, valid unexpired JWT
for a CUSTOMER user. CorsFilter passes. RateLimitFilter's counter is
still under threshold, passes. JwtAuthenticationFilter verifies the
signature (real HMAC-SHA256 check, per 03-oauth2-oidc-and-jwt.md),
confirms not expired, sets principal = the JWT's `sub` claim.
MethodSecurityFilter matches the principal's role against
@PreAuthorize's required role, request proceeds to the controller.

## Trace: a request that should be rejected -- at what point, and why
Same client, 101st request this minute. CorsFilter passes (same origin).
RateLimitFilter's counter is over threshold -- SHORT-CIRCUITS, returns
429 Too Many Requests. The JWT is never parsed, because the rate-limit
check is cheaper and runs first -- this endpoint's filter order gets
this right.

## Gaps found
No CSRF filter -- correct for this endpoint (stateless, Bearer-token-
authenticated, not cookie-session-based; CSRF targets cookie-based
session riding, which doesn't apply here). A MISSING filter is the
right call here, not a gap -- worth stating explicitly rather than
assuming every chain needs every filter type uniformly.
```

**Why this is complete:** identifies a case where a filter is deliberately *absent* and explains why, rather than treating completeness as "more filters is always better" — the same judgment discipline as every "argue against your own proposal" exercise in this programme.

## 3. Exit check

Your own trace must include both a successful path and a specific rejection case, and must reason about at least one filter's *position* in the chain (not just its presence) using the cheapest-first principle from `02-spring-security-filter-chain.md`.
