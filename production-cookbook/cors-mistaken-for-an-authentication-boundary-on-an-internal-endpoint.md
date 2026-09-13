---
title: "CORS Mistaken for an Authentication Boundary on an Internal Endpoint"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-fullstack-integration.md
source: syllabus/21-frontend-web/nextjs-fullstack-integration.md#production-scenarios
---

# CORS Mistaken for an Authentication Boundary on an Internal Endpoint

## Context

A frontend team adds CORS headers to a backend and assumes the integration is now secure.

## Symptoms

A security review flags that internal-only endpoints are reachable cross-origin once CORS is enabled broadly.

## Impact

Endpoints intended for internal use only are reachable by any client, browser-enforced-origin restrictions notwithstanding, since CORS was the only access control in place.

## Initial Hypotheses

- CORS itself is the security boundary and is working correctly — the team's original assumption, tested directly rather than accepted.
- A CORS misconfiguration is allowing too broad an origin list — checked, but even a correctly-scoped CORS policy wouldn't have closed the actual gap.
- CORS only controls whether a browser can read the response, not whether the request happens or who else can send it — correct.

## Evidence

A real curl request with no `Origin` header at all still reaches the endpoint fine, since CORS is not enforced server-side or by non-browser clients at all.

## Investigation Timeline

1. Security review flags internal endpoints as reachable cross-origin.
2. CORS-is-the-boundary assumption tested directly via a curl request with no `Origin` header.
3. Confirmed the endpoint has no independent credential check — CORS was the only configured restriction.

## Root Cause

CORS was mistaken for authentication; the endpoint had no independent credential check, so any client bypassing the browser entirely (curl, a script, another server) could reach it regardless of CORS configuration.

## Immediate Mitigation

Restrict network-level access to the internal endpoint (firewall/security-group rule) as an immediate stopgap while an application-level check ships.

## Permanent Fix

Add a real shared-secret or session/token check independent of CORS — CORS remains relevant only for endpoints truly meant for direct browser access.

## Alternatives Considered

Tightening the CORS origin allowlist further — rejected as insufficient on its own, since CORS provides no protection at all against non-browser clients, regardless of how narrow the allowlist is.

## Trade-offs

None meaningful — an independent credential check is standard practice for any endpoint not meant to be fully public, and adding it closes a real gap CORS was never designed to close.

## Prevention

Treat CORS configuration and authentication as two entirely separate concerns in every design review — CORS governs which browser-origins can read a response; authentication governs who can make the request at all.

## Monitoring and Alerts

- Access logs monitored for requests to internal-only endpoints missing the expected credential header, flagging exactly the gap this incident found.
- A security-review checklist item explicitly separating "is this endpoint's CORS policy correct" from "does this endpoint have its own authentication," so neither question is answered by the other.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent security review.

- **Situation:** internal-only endpoints were found reachable cross-origin after CORS headers were added, with the team believing this made the integration secure.
- **Task:** determine the real exposure without assuming CORS itself provided access control.
- **Action:** sent a real curl request with no `Origin` header, confirming CORS enforced nothing server-side, then added an independent credential check.
- **Result:** closed the actual gap with a real authentication mechanism, keeping CORS scoped to its actual purpose.

## Staff-Level Discussion

CORS only controls whether a browser can read the response, not whether the request happens or who else can send it — this is a common and consequential misunderstanding, since it's easy to conflate "the browser respects this policy" with "this endpoint is protected." The organizational lesson is treating CORS and authentication as structurally separate layers in every architecture review, never inferring one from the presence of the other.

## Related Handbook Chapters

- [Next.js Fullstack Integration](../syllabus/21-frontend-web/nextjs-fullstack-integration.md) — the canonical CORS-vs-authentication distinction behind this incident's fix.
