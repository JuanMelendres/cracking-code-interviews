---
title: "Raw Exception Message Leaking a Database Connection String"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/05-spring/bean-validation-and-global-exception-handling.md
source: syllabus/05-spring/bean-validation-and-global-exception-handling.md#production-scenarios
---

# Raw Exception Message Leaking a Database Connection String

## Context

A service has specific `@ExceptionHandler` methods for its known, anticipated exception types, but no catch-all `@ExceptionHandler(Exception.class)`. A misconfigured database connection pool starts throwing a raw exception whose message includes the connection string used to build it.

## Symptoms

A monitoring tool that scrapes public API responses for anomalies flags a `500` response body containing what looks like a partial credential.

## Impact

A real, active credential-exposure incident — external API consumers received a raw exception message containing internal infrastructure detail.

## Initial Hypotheses

- A logging misconfiguration exposing logs publicly — checked, logs were never public; the leak was in the actual HTTP response body.
- An intentional debug endpoint left enabled — checked, no such endpoint exists.
- Spring's default error-handling behavior surfaced the raw exception message directly because no catch-all handler intercepted it first — correct.

## Evidence

The leaked response body's structure matches Spring Boot's default `/error` fallback JSON shape, confirming no application-level `@ExceptionHandler(Exception.class)` was ever invoked for this exception type.

## Investigation Timeline

1. Monitoring flags a `500` response body containing what appears to be a partial credential.
2. Log-exposure and debug-endpoint hypotheses ruled out.
3. Response body structure compared against Spring Boot's default error shape, confirming no application-level handler intercepted the exception.
4. Reproduced locally by triggering the same connection failure in a non-production environment, confirming the raw exception message appeared in the HTTP response with no sanitization anywhere in the request path.

## Root Cause

No global, catch-all exception handler existed — only specific handlers for anticipated exception types — so an exception type nobody had thought to anticipate fell through to Spring's own default behavior, which does not know or care whether a given exception's message is safe to expose.

## Immediate Mitigation

Rotate the exposed credential immediately, treating it as compromised regardless of how briefly or how few requests saw it.

## Permanent Fix

Add a single `@ExceptionHandler(Exception.class)` catch-all, logging the full real exception server-side and returning a fixed, generic message to every client for any exception type not already specifically handled.

## Alternatives Considered

Auditing and individually handling every possible exception type across every dependency — rejected as fundamentally incomplete; new exception types can and will appear from library upgrades, new failure modes, and code paths nobody anticipated, which is precisely the case a catch-all exists to cover.

## Trade-offs

None meaningful — a catch-all handler costs one small, permanent piece of code and closes an entire class of future incidents of this same shape.

## Prevention

Treat the absence of a catch-all `@ExceptionHandler(Exception.class)` as a standing code-review finding for any Spring MVC service, not an optional nicety.

## Monitoring and Alerts

- Automated response-body scanning for credential-shaped patterns (connection strings, tokens, keys) on public-facing endpoints, as already caught this specific incident.
- A static-analysis or architecture-test check confirming every Spring MVC controller-advice class includes a catch-all `Exception.class` handler.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a public API leaked a database connection string in a `500` error response.
- **Task:** find why an unanticipated exception type reached the client with raw internal detail.
- **Action:** confirmed via the response shape that no catch-all handler existed; rotated the exposed credential immediately, then added a single `@ExceptionHandler(Exception.class)` catch-all.
- **Result:** closed the immediate exposure and eliminated the entire class of future incidents with the same shape.

## Staff-Level Discussion

This is Interview Question 2 in the canonical chapter's own Interview Questions section — "why do you need a catch-all exception handler if you already handle every exception type you can think of" — arriving as a real, credential-exposure-shaped incident rather than an abstract completeness argument. The organizational lesson: "handle every exception type you can think of" is structurally incomplete as a security posture, since new, unanticipated exception types are guaranteed to appear over a service's lifetime — a catch-all is the only defense against exactly the type nobody thought to anticipate.

## Related Handbook Chapters

- [Bean Validation and Global Exception Handling](../syllabus/05-spring/bean-validation-and-global-exception-handling.md) — the canonical catch-all `@ExceptionHandler` pattern behind this incident's fix.
