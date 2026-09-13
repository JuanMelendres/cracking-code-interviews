---
title: "Optimistic Proxy Auth Check Verifying Cookie Presence, Not Validity"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-authentication-patterns.md
source: syllabus/21-frontend-web/nextjs-authentication-patterns.md#production-scenarios
---

# Optimistic Proxy Auth Check Verifying Cookie Presence, Not Validity

## Context

A team's `proxy.js`, written early and never revisited, gates `/dashboard` with `!request.cookies.has('session')`.

## Symptoms

A security review (or a real incident) finds that any cookie named `session`, including an expired, forged, or tampered one, passes the Proxy's check.

## Impact

A route relying solely on this Proxy check for protection would be silently exposed to anyone presenting any cookie named `session`, regardless of validity.

## Initial Hypotheses

- The DAL's own session check makes this fine in practice regardless of Proxy's weakness — the initial hope, tested directly rather than assumed.
- The tampered-cookie test wouldn't actually reach a protected route — checked, a real deliberately-tampered JWT was sent and did reach `/dashboard` past Proxy's check.
- Proxy's check only verifies cookie presence, not cryptographic validity, and only the DAL's own independent check saves this specific route — correct.

## Evidence

Sending a real, deliberately tampered JWT to `/dashboard` with the naive Proxy check active shows Proxy letting it through (a real `x-proxy-hit` header present), with the request only redirected by the dashboard page's own DAL call, to `/login`.

## Investigation Timeline

1. Security review flags Proxy's auth check as verifying presence, not validity.
2. Deliberately tampered JWT sent against the live route to test the real consequence.
3. Confirmed Proxy passes the tampered cookie through; only the DAL's separate, per-page check actually redirects.

## Root Cause

The DAL saved this specific case, but only because every protected route in this app correctly calls `verifySession()` — a single new route added later without that call would be silently exposed, since Proxy's own check provides no real protection.

## Immediate Mitigation

Audit every existing protected route to confirm each one independently calls `verifySession()` in its DAL, since Proxy cannot be relied upon alone.

## Permanent Fix

Upgrade Proxy to real signature verification, using the same `decrypt()` the DAL uses, so the optimistic layer becomes a genuine, if coarse, real check — not a false sense of security.

## Alternatives Considered

Relying on documentation/convention to ensure every future route remembers to call `verifySession()` in its DAL — rejected as fragile; a single forgotten call silently reopens the exact gap this incident found.

## Trade-offs

Real signature verification in Proxy adds a small amount of per-request cryptographic work at the edge — accepted, since it closes a real gap that convention alone cannot guarantee stays closed.

## Prevention

Treat "does this optimistic/edge-layer check perform real validation, or only a presence check" as a standing security-review question for any layered auth architecture, not an assumption verified once at design time.

## Monitoring and Alerts

- A synthetic test sending a deliberately tampered session cookie against every protected route on a schedule, confirming Proxy's real validation continues to reject it.
- A code-review checklist item requiring every new protected route to explicitly confirm its DAL calls `verifySession()`, given that Proxy alone cannot be trusted to catch a missing call.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent security review.

- **Situation:** a security review found an edge-layer auth check that verified cookie presence but not validity.
- **Task:** determine the actual exposure, given a second, independent check existed deeper in the app.
- **Action:** sent a real, deliberately tampered JWT against the live route, confirming Proxy passed it through and only the DAL's own check caught it.
- **Result:** upgraded Proxy to real signature verification, closing the gap that previously depended entirely on every future route remembering its own DAL check.

## Staff-Level Discussion

The organizational risk here isn't that the current app was actually exposed — it wasn't, because every existing route happened to call `verifySession()` — it's that the safety net depended on a convention holding forever, with no structural enforcement. A Staff-level review specifically distinguishes "this is currently safe" from "this is structurally guaranteed to stay safe," and the fix (real validation at the optimistic layer) converts a convention-dependent guarantee into a structural one.

## Related Handbook Chapters

- [Next.js Authentication Patterns](../syllabus/21-frontend-web/nextjs-authentication-patterns.md) — the canonical layered-auth (Proxy + DAL) verification behind this incident's fix.
