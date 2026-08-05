---
title: "Expensive Authorization Check Ahead of Cheap Filter-Chain Validation"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/spring/security-filter-chain.md
source: handbook/spring/security-filter-chain.md#production-scenarios
---

# Expensive Authorization Check Ahead of Cheap Filter-Chain Validation

## Context

A public-facing API's security filter chain runs a database-backed authorization filter before its CORS origin check.

## Symptoms

The API experiences elevated average latency and increased database load during a period of scanning and probing traffic — malformed requests, invalid origins — hitting the service at high volume, traffic that should have been rejected cheaply and immediately.

## Impact

Legitimate traffic experiences degraded latency because illegitimate traffic is consuming disproportionate processing time before being rejected.

## Initial Hypotheses

- A genuine traffic spike from real users — checked and ruled out; request patterns show malformed headers and disallowed origins, consistent with automated scanning.
- A database performance regression — checked and ruled out; query latency itself is normal, just called more often.
- Filter ordering placing an expensive check ahead of cheap ones — correct.

## Evidence

Tracing shows the authorization filter — which performs a database-backed permission lookup — runs before the CORS origin check in the configured filter chain, meaning every request, including those from disallowed origins, reaches the database-backed check before being rejected.

## Investigation Timeline

1. **Elevated latency and database load noticed** correlating with a period of scanning/probing traffic.
2. **Traffic-spike and database-regression hypotheses ruled out** using request-pattern analysis and query-latency metrics.
3. **Request tracing examined**, revealing the actual filter execution order.
4. **Order compared against the standard cost/decisiveness principle**, finding the expensive, database-backed check placed ahead of the cheap, in-memory one.

## Root Cause

The filter chain was configured without applying the cost/decisiveness ordering principle: a cheap, purely-in-memory origin check was placed after an expensive, database-backed authorization check, so illegitimate traffic paid the full cost of the expensive check before being rejected by a cheap one that never got a chance to reject it first.

## Immediate Mitigation

Reorder the filter chain to place CORS and CSRF checks ahead of authentication and authorization, consistent with the standard ordering principle.

## Permanent Fix

Add filter-chain-order verification to the security configuration's own test suite, asserting that cheap, stateless checks run before any check requiring a database or external call.

## Alternatives Considered

Adding a separate rate limiter ahead of the entire chain. A reasonable complementary defense, but not a substitute for fixing the underlying ordering mistake — even non-malicious, legitimately-disallowed-origin requests would still pay the expensive-check cost unnecessarily.

## Trade-offs

None significant — correct filter ordering has no real downside; it was simply misconfigured.

## Prevention

Treat filter and interceptor ordering as an explicit, reviewed architectural decision for any new cross-cutting concern, using the cost/decisiveness principle, rather than an incidental consequence of configuration order.

## Monitoring and Alerts

- Database query volume attributable to rejected (non-2xx) requests, tracked separately from queries backing successful requests — a rising share of database load from ultimately-rejected requests is a direct signal of exactly this misordering, visible before it becomes a latency incident.
- The filter-chain-order test (the Permanent Fix above), run in CI on every security configuration change, converting this class of misconfiguration into a failed build.

## Interview Story

This maps to applying the "cheapest, most-decisive check first" principle under real load. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** elevated latency and database load during a wave of scanning traffic that should have been rejected immediately.
- **Task:** find why illegitimate traffic was consuming meaningful processing time before rejection.
- **Action:** rule out a genuine traffic spike and a database regression using request-pattern and query-latency data; trace actual filter execution order; identify the expensive check running ahead of the cheap one.
- **Result:** reordered the filter chain and added an automated order-verification test to the security configuration's own suite.

## Staff-Level Discussion

Filter and interceptor ordering is exactly the kind of decision that's easy to leave implicit — whatever order the beans happen to register in — because it has no functional impact on legitimate traffic; every request that should succeed still succeeds regardless of order. The cost only appears under illegitimate or adversarial traffic, which makes this a latent risk that passes ordinary testing cleanly and only surfaces under exactly the load pattern (scanning, probing) a service is least prepared to absorb gracefully. The generalizable lesson for a Staff engineer is that any new cross-cutting filter or interceptor should be placed using an explicit cost/decisiveness rule, not registration order, and that rule should be enforced by a test, not left as institutional knowledge for whoever configured the chain originally.

## Related Handbook Chapters

- [Security Filter Chain](../handbook/spring/security-filter-chain.md) — canonical filter-ordering and cost/decisiveness principle used here.
