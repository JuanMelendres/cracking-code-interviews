---
title: "Custom-Header CSRF Protection Reopened by a Legacy Form Endpoint"
document_type: production-cookbook-entry
domain: security
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/12-security/csrf-cors-and-session-security.md
source: syllabus/12-security/csrf-cors-and-session-security.md#production-scenarios
---

# Custom-Header CSRF Protection Reopened by a Legacy Form Endpoint

## Context

A single-page application's API only accepts `POST` requests with a custom header (e.g., `X-Requested-With`), and the team believes this alone provides CSRF protection, since a plain HTML form can't set custom headers.

## Symptoms

A security review finds that one endpoint also accepts the same state-changing action via a plain form-encoded `POST` without requiring that header — kept for a legacy client, or "just in case."

## Impact

That one endpoint reopens the exact CSRF gap the header-based check elsewhere in the API closed, while every other endpoint remains genuinely protected — a real, exploitable gap disguised by the API's overall design intent.

## Initial Hypotheses

- The custom-header check itself is broken — checked, it works correctly and blocks forged cross-site form submissions everywhere it's enforced.
- Browsers no longer restrict custom headers on simple cross-site form submissions — checked, this restriction is real and current, and correctly relied upon elsewhere.
- One specific endpoint accepts the vulnerable request shape without requiring the header — correct, found via review, not automated scanning.

## Evidence

The flagged endpoint's request handler accepts a plain, form-encoded `POST` body with no header check, while every sibling endpoint enforces the custom-header requirement.

## Investigation Timeline

1. Security review audits every state-changing endpoint's request-shape requirements, not just a sample.
2. Custom-header-mechanism-broken and browser-behavior-changed hypotheses ruled out as the API's general design is confirmed sound.
3. One specific endpoint identified as accepting an unprotected request shape, kept for a legacy client.

## Root Cause

CSRF protection based on "browsers can't set this header from a plain form" is only as strong as its least-protected endpoint — any endpoint that also accepts the same action via an unrestricted request shape reopens the gap regardless of how well-protected every other endpoint is.

## Immediate Mitigation

Require the custom header on the flagged endpoint immediately, breaking the legacy client's unprotected path in favor of closing the exposure.

## Permanent Fix

Audit every state-changing endpoint for the same requirement uniformly (not by sampling), and require any genuinely necessary legacy-client accommodation to use a different, explicit CSRF defense (e.g., a synchronizer token) rather than silently exempting the header check.

## Alternatives Considered

Leaving the legacy endpoint as a documented, "accepted risk" exception — rejected, since a CSRF gap is directly exploitable and the legacy client can instead be updated to send the required header or use an explicit token-based defense.

## Trade-offs

Requiring the header (or an explicit token) on the legacy endpoint may break an unmaintained legacy client immediately — accepted, since an active CSRF exposure is a worse outcome than a forced client update.

## Prevention

Treat "does every state-changing endpoint require the same CSRF defense, with no silent exceptions" as a standing, endpoint-by-endpoint security-review checklist item, not an assumption verified once at design time.

## Monitoring and Alerts

- An automated API-schema/route audit comparing every state-changing endpoint's required headers/tokens against the documented CSRF-defense policy, flagging any endpoint that diverges.
- Security-review cadence explicitly re-checking this exact invariant on every new endpoint addition, not only during periodic audits.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent review finding.

- **Situation:** a security review found one legacy endpoint silently exempt from an otherwise-consistent CSRF defense.
- **Task:** determine the actual exposure and close it without assuming the overall design was sufficient just because most endpoints were protected.
- **Action:** audited every state-changing endpoint's request-shape requirements individually, found the one legacy exception, and required the same defense uniformly.
- **Result:** closed the reopened CSRF gap and established a standing per-endpoint audit to prevent recurrence.

## Staff-Level Discussion

Custom-header-based CSRF protection is real, working protection for exactly the stated reason — but it's fragile in a specific, easy-to-miss way: it is a property of the *API's design intent*, not of any individual endpoint, and any single endpoint that doesn't enforce it independently reopens the whole gap. The organizational lesson is that a security property claimed "for the API" needs to be verified endpoint-by-endpoint, since a security review that samples rather than audits exhaustively can miss exactly this kind of single-endpoint exception.

## Related Handbook Chapters

- [CSRF, CORS, and Session Security](../syllabus/12-security/csrf-cors-and-session-security.md) — the canonical custom-header CSRF defense behind this incident.
