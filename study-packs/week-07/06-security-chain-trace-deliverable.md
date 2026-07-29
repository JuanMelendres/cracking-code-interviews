---
title: "security-chain-trace.md Deliverable"
week: 7
last_reviewed: 2026-07-29
---

# `security-chain-trace.md` Deliverable

**Trace one authenticated request through every filter, for a real or realistic system.** Use `02-spring-security-filter-chain.md`'s three real scenarios as the mechanical template; this deliverable applies the same tracing discipline to a specific, concrete endpoint rather than the generic demo.

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
A request arrives from an allowed origin, under the rate limit, with a
valid, unexpired JWT for a user with role CUSTOMER. CorsFilter passes.
RateLimitFilter increments the counter, still under threshold, passes.
JwtAuthenticationFilter verifies the signature (real HMAC-SHA256 check,
per 03-oauth2-oidc-and-jwt.md), confirms not expired, sets principal =
the JWT's `sub` claim. MethodSecurityFilter checks the principal's role
against @PreAuthorize's required role, matches, request proceeds to the
controller.

## Trace: a request that should be rejected -- at what point, and why
Same client, but their 101st request within the current minute. CorsFilter
passes (same origin). RateLimitFilter's counter is now over threshold --
SHORT-CIRCUITS here, returns 429 Too Many Requests. The JWT is never even
parsed, because the rate-limit check is cheaper and should run first --
this endpoint's actual filter order gets this right.

## Gaps found
The chain has no CSRF filter, which is correct for this specific endpoint
(a stateless, Bearer-token-authenticated API, not cookie-session-based --
CSRF specifically targets cookie-based session riding, which doesn't apply
here). This is a case where a MISSING filter is the right call, not a gap
-- worth stating explicitly rather than assuming every chain needs every
filter type uniformly.
```

**Why this is a complete deliverable:** it identifies a case where a filter is deliberately *absent* and explains why that's correct rather than treating filter-chain completeness as "more filters is always better" — the same judgment discipline as every other "argue against your own proposal"-style exercise in this programme.

## 3. Exit check

Your own trace must include both a successful path and a specific rejection case, and must reason about at least one filter's *position* in the chain (not just its presence) using the cheapest-first principle from `02-spring-security-filter-chain.md`.
