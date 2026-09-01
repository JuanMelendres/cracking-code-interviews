---
title: "Actuator /env Exposed to the Public Internet via a Wildcard Override"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/spring/spring-actuator-health-and-observability-hooks.md
source: handbook/spring/spring-actuator-health-and-observability-hooks.md#production-scenarios
---

# Actuator /env Exposed to the Public Internet via a Wildcard Override

## Context

A `management.endpoints.web.exposure.include=*` property had been added months earlier during a debugging session to "just see everything" while diagnosing an unrelated issue, and was never reverted.

## Symptoms

An external security researcher reported that a service's `/actuator/env` endpoint was publicly reachable and returned the application's full configuration property source list.

## Impact

Configuration property names and some non-secret values were exposed to the public internet for months before discovery, alongside access to several other built-in endpoints (`heapdump`, `threaddump`, `configprops`) that reveal real, sensitive operational detail.

## Initial Hypotheses

- A misconfigured API gateway was routing unintended paths through to the application — this was the first hypothesis pursued.

## Evidence

The actual cause was a single `management.endpoints.web.exposure.include=*` property, added months earlier during an unrelated debugging session and never reverted. Boot's actual default is deliberately restrictive (`health`, `info` only); the wildcard override had silently widened the exposed surface to every built-in Actuator endpoint.

## Investigation Timeline

1. **External report received** that `/actuator/env` was publicly reachable and returning configuration data.
2. **Gateway misrouting investigated first**, on the assumption an infrastructure-level routing mistake was exposing an internal path.
3. **Application configuration inspected directly**, surfacing the wildcard `management.endpoints.web.exposure.include=*` property.
4. **Origin traced** to a debugging session months earlier, where the property had been added to inspect Actuator endpoints and never reverted.

## Root Cause

A wildcard `management.endpoints.web.exposure.include=*` override, added temporarily during debugging and never reverted, replaced Spring Boot's deliberately restrictive default endpoint exposure (`health`, `info` only) with exposure of every built-in Actuator endpoint, including several that reveal sensitive operational detail.

## Immediate Mitigation

Reverted to an explicit, minimal include list.

## Permanent Fix

Added a policy requiring any `management.endpoints.web.exposure.include` value beyond a documented, reviewed allowlist to require security sign-off, and added a scheduled external scan checking for unexpectedly-exposed Actuator endpoints.

## Alternatives Considered

Relying on network-layer controls (e.g., a gateway rule blocking `/actuator/*` from the public internet) as the sole safeguard. Not adopted as a substitute for fixing the application-level exposure, since a future gateway misconfiguration would then reintroduce the exact same exposure with no application-level defense remaining.

## Trade-offs

Engineers lose the convenience of `include=*` during local debugging in shared/staging environments. This was judged acceptable against the real exposure risk it created.

## Prevention

Default `application.yml` templates for new services now ship with an explicit, minimal include list rather than a wildcard, matching Boot's own secure-by-default posture rather than fighting it.

## Monitoring and Alerts

- A scheduled external scan checking for unexpectedly-exposed Actuator endpoints across all production services, catching a future recurrence before an external party does.
- A configuration-drift check flagging any `management.endpoints.web.exposure.include` value that differs from the reviewed, allowlisted default template.

## Interview Story

This maps to a "how did sensitive operational data get exposed publicly" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** `/actuator/env` was reachable from the public internet and had been for months.
- **Task:** find how a leftover debugging convenience became a real security exposure.
- **Action:** ruled out gateway misrouting; found a wildcard `exposure.include=*` property added during a past debugging session and never reverted.
- **Result:** reverted to an explicit minimal include list, added a security sign-off requirement for any broader exposure, and added a scheduled external scan for the same class of issue.

## Staff-Level Discussion

Spring Boot's restrictive default (`health`, `info` only) is a real security control, not just a sane default to be overridden freely during development — and the incident shows exactly how that control gets eroded in practice: not through a deliberate decision to expose sensitive endpoints, but through a temporary debugging convenience that nobody owned the responsibility of reverting. The organizational lesson generalizes beyond Actuator: any "just for now" widening of a security-relevant default needs either an expiration mechanism or a periodic external audit, because relying on the original engineer to remember to revert it is not a control, it's a hope. The scheduled external scan is the durable fix precisely because it doesn't depend on anyone remembering anything.

## Related Handbook Chapters

- [Spring Boot Actuator, Health, and Observability Hooks](../handbook/spring/spring-actuator-health-and-observability-hooks.md) — canonical default-exposure model and endpoint-security posture used here.
