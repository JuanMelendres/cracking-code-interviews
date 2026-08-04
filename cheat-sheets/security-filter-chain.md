---
title: "Cheat Sheet: Spring Security Filter Chain"
slug: security-filter-chain
document_type: cheat-sheet
domain: spring
topic_id: T-511
canonical: ../handbook/spring/security-filter-chain.md
last_updated: 2026-08-04
---

# Spring Security Filter Chain

**Canonical chapter:** [`handbook/spring/security-filter-chain.md`](../handbook/spring/security-filter-chain.md)

## Core Mental Model

The filter chain is a sequence of yes/no gates, and any gate can end the request early. Each filter either does its work and passes the request to the next filter, or short-circuits the chain entirely by returning a response directly. The order of the gates is the entire security model: a gate that needs to know *who* the request is (authorization) cannot meaningfully run before the gate that establishes *who* it is (authentication) — reversing that order would make authorization checks meaningless.

## Essential Definitions

- **Spring Security filter chain** — Spring Security intercepts every HTTP request through a chain-of-responsibility pipeline of servlet filters; each filter can inspect the request, do work, and either call the next filter or short-circuit the chain (e.g., a 401 or 403). Exists so cross-cutting security concerns — CORS, CSRF, authentication, authorization — apply uniformly, in a guaranteed order, before a request ever reaches application code.
- **Authentication vs. authorization** — authentication establishes *who* the request is; authorization decides *what* they're allowed to do. Authentication succeeding does not imply authorization succeeds.
- **Filter ordering principle** — cheaper, more decisive rejection checks (CORS origin validation, CSRF token presence) run before more expensive ones (authentication, which may involve cryptographic verification or a database lookup), so a request that's going to be rejected anyway is rejected as cheaply as possible.

## Decision Table

| Approach | Benefit | Cost |
|---|---|---|
| Filter-chain short-circuiting | Cheap rejection before any business logic runs | Every filter added is on the critical path of every request, even rejected ones |
| Stateless (JWT-based) authentication | No session-store lookup per request | The chain cannot revoke a still-valid token |
| Method-level security (`@PreAuthorize`) | Finer-grained, closer to the specific operation | Runs later than the filter chain — some processing cost already incurred |

| Situation | What it means |
|---|---|
| 401 Unauthorized | Authentication failed — no valid principal established |
| 403 Forbidden | Authentication succeeded, authorization failed |
| A filter short-circuits early | Cheapest, most-decisive rejection checks should run first |
| New cross-cutting concern to add | Place it by cost/decisiveness, not declaration convenience |

## Key Numbers

Real, executed request trace confirms the ordered chain-of-responsibility mechanism (`SecurityFilterChainDemo.java`). No latency/throughput figures are given in the mechanism section itself — the production incident below is described qualitatively ("elevated average latency and increased database load"), not with specific numbers.

## Common Pitfalls

- Conflating authentication (who is this) with authorization (what are they allowed to do) as a single step
- Assuming a successfully authenticated request is automatically authorized for every endpoint
- Placing expensive checks (e.g., a database-backed permission lookup) earlier in the chain than cheap ones (CORS origin check), wasting work on requests that could have been rejected for free

## Interview Answer Skeleton

**30-sec:** Spring Security intercepts every request through an ordered chain of filters; any filter can short-circuit and return a response directly. Authentication (who) and authorization (what) are separate, sequential gates — a 401 means authentication failed, a 403 means it succeeded but authorization didn't.

**2-min:** Add why it exists (uniform ordered application vs. hand-rolled per endpoint) + the CORS/CSRF → authentication → authorization → controller flow + the misordered-filter production incident.

**Whiteboard:** Draw Request → CorsFilter → CsrfFilter → AuthenticationFilter (branch to 401) → AuthorizationFilter (branch to 403) → Controller; point out both branch-off points explicitly.

**Staff-level framing:** the filter chain is a concrete instance of a general architectural pattern — ordering cross-cutting concerns from cheapest/most-decisive to most expensive/most-specific — that shows up throughout distributed systems design, not just in one framework's security layer. Designing any new cross-cutting concern (rate limiting, request logging, feature-flag evaluation) means asking "what's the cheapest check that can reject the most requests, and does it run first."

## Production Warning Signs

- **Real incident pattern:** a public-facing API experiences elevated average latency and increased database load during a period of scanning/probing traffic — legitimate traffic degrades because illegitimate traffic consumes disproportionate processing time before rejection. Root cause: the authorization filter (a database-backed permission lookup) runs *before* the CORS origin check in the configured chain.
- Fix: reorder CORS/CSRF ahead of authentication/authorization; add filter-chain-order verification to the security config's test suite. No real downside — correct ordering was simply misconfigured.

## Related

- [Spring Transactional Proxy Mechanics and Propagation](transactional-proxy-mechanics-and-propagation.md)
- `handbook/security/oauth2-oidc-and-jwt.md`
