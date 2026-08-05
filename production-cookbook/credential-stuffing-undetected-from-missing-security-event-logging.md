---
title: "Credential Stuffing Undetected From Missing Security-Event Logging"
document_type: production-cookbook-entry
domain: security
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/security/owasp-top-10-for-backend-services.md
source: handbook/security/owasp-top-10-for-backend-services.md#production-scenarios
---

# Credential Stuffing Undetected From Missing Security-Event Logging

## Context

The application logs successful logins and generic errors, but never logs failed-authentication attempts with enough context — source IP, username attempted, timestamp — to distinguish a mistyped password from an automated attack.

## Symptoms

A credential-stuffing attack against a login endpoint — an automated tool trying tens of thousands of username/password pairs — runs undetected for weeks.

## Impact

An active, ongoing attack against the authentication boundary produces no internal alert, discovered only through some external signal (a customer report, an unrelated audit) rather than the system's own monitoring.

## Initial Hypotheses

- The attack is somehow evading standard authentication logging — investigated directly against the actual logging configuration.
- Security-relevant events were never logged as their own first-class category with sufficient context to distinguish attack traffic from normal usage noise — correct.

## Evidence

The application logs successful logins and generic errors, but never logs failed-authentication attempts with the context — source IP, username attempted, timestamp — needed to distinguish a single mistyped password from a systematic, automated attempt sweep.

## Investigation Timeline

1. **Credential-stuffing activity discovered** via a signal external to the application's own monitoring, after running for weeks.
2. **Logging configuration reviewed directly**, to determine why an attack of this scale produced no internal alert.
3. **Gap identified**: failed-authentication attempts were logged generically, without the specific fields needed to distinguish attack-pattern traffic from ordinary usage.
4. **Absence characterized correctly**: not a missing feature so much as a missing decision — security-relevant events were never treated as their own reviewable, alertable category.

## Root Cause

The absence isn't a missing feature so much as a missing decision: security-relevant events — auth failures, authorization denials, privilege escalations — need to be logged as a first-class category, separately reviewable from general application logs, with alerting thresholds tuned to the traffic pattern of an actual attack rather than normal usage noise.

## Immediate Mitigation

Manually review available access logs and any external signal (rate-limiter logs, WAF logs, if present) to reconstruct the attack's scope and affected accounts, and force a password reset for any accounts showing suspicious login activity.

## Permanent Fix

Add structured, first-class security-event logging for authentication failures, authorization denials, and privilege escalations — including source IP, attempted identity, and timestamp — with alerting thresholds specifically tuned to attack-pattern traffic volume (many failed attempts across many usernames from a small set of sources, or many attempts against one username in a short window) rather than generic error-rate alerting.

## Alternatives Considered

Relying on a third-party WAF or bot-detection service alone to catch this class of attack. Considered as a complementary layer, but rejected as a substitute for the application's own security-event visibility — an external layer can be bypassed or misconfigured, and the application's own authentication path should be able to detect and alert on attack patterns independent of any single external tool.

## Trade-offs

Structured security-event logging adds log volume and requires a dedicated review or alerting pipeline separate from general application logs. Accepted, since the alternative — the gap this incident demonstrates — is weeks of undetected active attack against the authentication boundary.

## Prevention

Treat security-relevant event logging (authentication failures, authorization denials, privilege escalations) as a required category at the time any authentication or authorization code path is built, not something added after an incident demonstrates its absence.

## Monitoring and Alerts

- Failed-authentication-attempt rate alerting, tuned specifically to distinguish attack-pattern shapes (many usernames from few sources; many attempts against one username in a short window) from normal user error, using the structured logging fields added in the permanent fix.
- A standing security-event review process, separate from general application-log review, ensuring authentication and authorization failure logs are actually monitored on a recurring basis rather than only reviewed reactively after an external signal prompts investigation.

## Interview Story

This maps to a direct OWASP A09 (Security Logging and Monitoring Failures) scenario. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a credential-stuffing attack against the login endpoint ran undetected for weeks, discovered only via an external signal.
- **Task:** explain why an attack of that scale produced no internal alert.
- **Action:** review the authentication logging directly; find that failed attempts were logged only generically, without the fields needed to distinguish attack traffic from normal usage; recognize this as a missing decision about what counts as a security-relevant event, not a logging bug.
- **Result:** added structured, first-class security-event logging with attack-pattern-tuned alerting thresholds, and established a standing review process for security events specifically.

## Staff-Level Discussion

This incident demonstrates that "we have logging" and "we have security observability" are different claims — the application logged plenty, but none of it was structured or reviewed in a way that could distinguish a credential-stuffing attack from routine noise, which meant the attack was invisible not because nothing was recorded but because nothing treated authentication failures as a security-relevant category worth its own alerting logic. This generalizes into a standing principle for authentication and authorization code: security-relevant events deserve first-class, structured logging and dedicated alerting from the moment the code path is built, evaluated against known attack shapes, not retrofitted after an incident demonstrates the gap. A Staff engineer reviewing a new authentication feature should treat "what does a credential-stuffing attack against this endpoint look like in our logs, and would we notice it" as a standard design-review question, not an afterthought.

## Related Handbook Chapters

- [OWASP Top 10 for Backend Services](../handbook/security/owasp-top-10-for-backend-services.md) — canonical A09 security logging and monitoring failure category used here.
- [AuthN/AuthZ: RBAC vs. ABAC](../handbook/security/authn-authz-rbac-vs-abac.md) — the authorization model whose failures this logging discipline also covers.
