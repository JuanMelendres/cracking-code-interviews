---
title: "Session Fixation via a Password-Reset sessionid Query Parameter"
document_type: production-cookbook-entry
domain: security
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/12-security/csrf-cors-and-session-security.md
source: syllabus/12-security/csrf-cors-and-session-security.md#production-scenarios
---

# Session Fixation via a Password-Reset sessionid Query Parameter

## Context

A password-reset flow accepts a `sessionid` query parameter to "restore your session after clicking the reset link," and a session created this way skips the usual login step.

## Symptoms

A security review flags this design as accepting an externally-supplied session identifier as authenticated, without the usual login step's identifier generation.

## Impact

A session-fixation-shaped design flaw: any mechanism that lets a session identifier arrive from outside the server's own generation step is a candidate for an attacker pre-setting a known session identifier that later becomes authenticated.

## Initial Hypotheses

- This is only exploitable via a classic pre-set-cookie attack, which this flow doesn't use — checked and rejected as too narrow; the underlying risk is broader than the specific classic attack shape.
- The `sessionid` query parameter is only ever generated server-side and never attacker-influenceable — checked, the parameter is accepted as supplied, with no verification it originated from the server's own reset-link generation.
- Any mechanism letting a session identifier arrive from outside the server's own generation step (a URL parameter, a pre-set cookie, a value echoed back from a prior response) is a candidate for this same underlying risk — correct.

## Evidence

The reset flow's authenticated-session creation path accepts the `sessionid` value directly from the query string with no independent verification step distinguishing a legitimately-issued identifier from an attacker-supplied one.

## Investigation Timeline

1. Security review examines the password-reset flow's session-handling design specifically, not just its cookie behavior.
2. Classic-pre-set-cookie-attack-only framing considered and rejected as underestimating the risk's actual scope.
3. The query-parameter-based session restoration traced directly, confirming no server-side generation-origin check exists.

## Root Cause

A session that reaches an authenticated state can do so via an identifier that never passed through the server's own generation step, regardless of whether the specific mechanism resembles a "classic" session-fixation attack.

## Immediate Mitigation

Disable the `sessionid`-query-parameter restoration path until the fix ships, forcing affected users through the standard login flow instead.

## Permanent Fix

Generalize the rule: a session that reaches an authenticated state must always do so via a freshly server-generated identifier, regardless of which specific code path led there — the password-reset flow must issue its own fresh session identifier server-side rather than accepting one supplied externally.

## Alternatives Considered

Validating the `sessionid` parameter against a signature or expiry check instead of removing it — rejected as still accepting an externally-supplied identifier as the basis for authentication, rather than eliminating the pattern entirely.

## Trade-offs

Removing the query-parameter restoration path requires reworking the reset-link user experience (e.g., a one-time token exchanged server-side for a freshly generated session) — accepted, since the alternative retains a real, generalizable session-fixation risk.

## Prevention

Treat "does an authenticated session's identifier ever originate from outside the server's own generation step" as a standing question for any new authentication or session-restoration flow, not only for scenarios resembling the classic cookie-based attack.

## Monitoring and Alerts

- A design-review checklist item, applied to every new session-handling flow, explicitly checking for externally-supplied session identifiers reaching an authenticated state.
- Logging and alerting on any authenticated session whose identifier was received via a query parameter or other non-standard channel, as a detection layer for this class of design flaw recurring elsewhere.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent security review.

- **Situation:** a security review flagged a password-reset flow that skipped standard login using a `sessionid` query parameter.
- **Task:** determine the real scope of the risk beyond the specific mechanism used.
- **Action:** generalized the finding to any externally-supplied session identifier, not just the classic pre-set-cookie pattern, and required the flow to issue a freshly server-generated identifier instead.
- **Result:** closed the specific flow's exposure and established a standing design-review rule covering the broader class of risk.

## Staff-Level Discussion

The organizational value here is generalizing a specific finding into the underlying rule it actually represents: session fixation isn't only about cookies, it's about whether an authenticated session's identifier ever originated outside the server's own generation step. A Staff-level review looks past "does this match the textbook attack" and asks "does this satisfy the actual invariant the textbook attack is one instance of" — the same invariant applies to query parameters, echoed response values, and any other channel an identifier could arrive through.

## Related Handbook Chapters

- [CSRF, CORS, and Session Security](../syllabus/12-security/csrf-cors-and-session-security.md) — the canonical session-fixation invariant behind this incident.
