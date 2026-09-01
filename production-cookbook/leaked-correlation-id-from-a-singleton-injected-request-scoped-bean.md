---
title: "Leaked Correlation ID From a Singleton-Injected Request-Scoped Bean"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/spring/spring-bean-scopes-and-proxy-modes.md
source: handbook/spring/spring-bean-scopes-and-proxy-modes.md#production-scenarios
---

# Leaked Correlation ID From a Singleton-Injected Request-Scoped Bean

## Context

A correlation-ID holder bean was declared `@RequestScope`, correctly, to hold one value per HTTP request. Months later, an unrelated singleton service was refactored to inject that holder as a plain constructor dependency and cache it as a field.

## Symptoms

Under production load, log lines occasionally carried the wrong request's correlation ID, making distributed tracing unreliable specifically under concurrency. The bug never reproduced in local single-request testing.

## Impact

Distributed tracing became unreliable under real concurrent load — exactly the condition tracing exists to diagnose — undermining incident investigations that depended on correct correlation IDs.

## Initial Hypotheses

- A logging framework MDC (Mapped Diagnostic Context) propagation issue across thread pools — this was the first hypothesis pursued and did not hold up.

## Evidence

The correlation-ID holder bean's `@RequestScope` declaration was correct. The actual injection site was a singleton service that had been refactored months earlier to hold the bean as a cached constructor-injected field, resolving it once at singleton construction time rather than per request.

## Investigation Timeline

1. **Wrong correlation IDs observed** in logs under production load only, never in local single-request testing.
2. **MDC propagation investigated first**, on the assumption tracing infrastructure across thread pools was the culprit.
3. **Injection site inspected** once the MDC hypothesis failed to explain the pattern, surfacing the singleton's cached constructor-injected field.
4. **Scope-resolution timing confirmed** as the mechanism: the singleton resolved the request-scoped bean once, at its own construction time, and never re-resolved it per request afterward.

## Root Cause

The singleton's field held whichever request happened to be active at application startup (or first use), and every subsequent request read that same stale value — because a `@RequestScope` bean injected as a plain dependency into a singleton is resolved once, at the singleton's creation time, not on every access.

## Immediate Mitigation

None possible without a code change, since the bug was structural, not transient.

## Permanent Fix

Changed the injection to a scoped proxy (`proxyMode = ScopedProxyMode.TARGET_CLASS` on the `@RequestScope` bean), which resolves a fresh instance on every call rather than once at construction time.

## Alternatives Considered

Injecting an `ObjectProvider`/`ObjectFactory` and resolving the bean manually on each use. Not adopted as the primary fix since a scoped proxy achieves the same per-call resolution transparently, without requiring every call site to change its injection style.

## Trade-offs

A scoped proxy adds one extra indirection layer per call. This was judged negligible against correctness.

## Prevention

Added a static-analysis rule flagging any `@RequestScope`/`@SessionScope`/`@Scope("prototype")` bean injected into a bean of `singleton` scope without an explicit `proxyMode`.

## Monitoring and Alerts

- Static analysis at build time catching the scope mismatch before deployment, rather than relying on production log inspection to surface it.
- Correlation-ID consistency checks within a single logical request (e.g., verifying the same ID appears across all log lines emitted for one traced call) as a standing tracing-quality signal.

## Interview Story

This maps to a "why does our tracing occasionally show the wrong ID" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** correlation IDs in logs occasionally belonged to a different request than the one that logged them, only under concurrent load.
- **Task:** find the cause without assuming the request-scoped bean's own declaration was wrong.
- **Action:** ruled out MDC propagation; inspected the injection site and found a singleton caching the request-scoped bean as a field, resolved once at construction.
- **Result:** switched the injection to a scoped proxy, resolving a fresh instance per call, and added a static-analysis rule to catch the same mismatch elsewhere.

## Staff-Level Discussion

The bean's own scope declaration was entirely correct — the defect lived at the injection site, in a different file, added by a different engineer who had no reason to know the dependency's scope mattered for how it should be wired. This is the general risk of scope mismatches in a DI container: correctness is a property of the *pairing* between a bean's declared scope and how it's injected, not of either side in isolation, and the two sides can be edited independently by people with no shared context. A static-analysis rule that flags the mismatch mechanically, at build time, generalizes the fix beyond any one reviewer remembering to check injection sites against declared scopes during code review.

## Related Handbook Chapters

- [Spring Bean Scopes and Proxy Modes](../handbook/spring/spring-bean-scopes-and-proxy-modes.md) — canonical scope-resolution-timing model and scoped-proxy mechanism used here.
