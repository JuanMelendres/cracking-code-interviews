---
title: "Mid → Senior, Week 13 — Security Deep Dive: CSRF/CORS and Enterprise SSO"
document_type: study-pack
week: 13
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 13 — Security Deep Dive: CSRF/CORS and Enterprise SSO

## Weekly Outcome

By the end of this week you can explain, with real evidence, why a cookie alone is not enough to stop a forged cross-site request and how the synchronizer-token pattern fixes that; correctly distinguish a CORS misconfiguration from an authentication boundary (a common, real production confusion); and describe the difference between SSO and Federated SSO, walk through a SAML assertion's four required validation checks, and name at least two commercial IAM vendors (PingFederate, Okta) and where each fits in a real Spring Security integration.

## Why This Week Matters

`12-security` grew from 9 to 10 chapters after Week 9 was originally scheduled — two real chapters, CSRF/CORS/session security and Enterprise SSO/SAML/Federated Identity, were never folded into any study pack even though the domain's own `INDEX.md` already covered them. This week closes that gap. SSO/SAML specifically comes from a real, named job-posting requirement ("strong understanding of the I&AM landscape... experience with commercial I&AM solutions like PingFederate") — enterprise identity federation is a real Senior/Staff interview and production surface, not a niche topic.

## Prerequisites

Week 9 (Security and Observability) — this week assumes OWASP Top 10 and the AuthN-vs-AuthZ/RBAC-vs-ABAC distinction are already solid; both new chapters build directly on that foundation rather than re-deriving it.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | CSRF, CORS, and Session Security |
| Wed | Reproduce the real forged-request demo yourself |
| Thu–Fri | Enterprise SSO: SAML, Federated Identity, and Commercial IAM Integration |
| Sat | Read both cross-referenced cookbook entries |
| Sun | Review checklist below |

## Required Reading

[`syllabus/12-security/csrf-cors-and-session-security.md`](../../../syllabus/12-security/csrf-cors-and-session-security.md) — CSRF, CORS, and session security, including the real synchronizer-token-vs-cookie-only comparison. [`syllabus/12-security/enterprise-sso-saml-and-federated-identity.md`](../../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md) — SSO vs. Federated SSO, SAML's core artifacts, the SAML/OAuth2/OIDC comparison, the commercial IAM landscape, and real Spring Security SAML2 integration.

## Hands-On Exercises

[`practice/java/week-17/csrf-cors-session/`](../../../practice/java/week-17/csrf-cors-session/) — a real forged cross-site request against a cookie-only endpoint versus a synchronizer-token-protected one. The SSO/SAML chapter has no locally-executed demo — stated honestly in its own provenance note, since a real multi-party SAML exchange would require a live Identity Provider; its `pom.xml`/`application.yml`/`SecurityConfig` code is real and accurate, verified live against Spring Security's own current documentation, but not a reproducible local run.

## Production Cookbook Cross-Reference

- [`cors-mistaken-for-an-authentication-boundary-on-an-internal-endpoint.md`](../../../production-cookbook/cors-mistaken-for-an-authentication-boundary-on-an-internal-endpoint.md)
- [`custom-header-csrf-protection-reopened-by-a-legacy-form-endpoint.md`](../../../production-cookbook/custom-header-csrf-protection-reopened-by-a-legacy-form-endpoint.md)

None for SSO/SAML yet — confirmed absent, not assumed, per this pack's own established convention (see Week 12's equivalent note).

## Interview Answer Drills

Answer, out loud: "why doesn't a session cookie alone stop CSRF, and what does the synchronizer-token pattern add that a cookie can't provide" and "beyond checking the signature, what else must a Service Provider validate on an incoming SAML assertion, and why does each check matter" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week — this week's hands-on exercise is the CSRF/CORS chapter's own real, executed demo, not a separate coding-interview problem.

## System Design Exercise

None this week (see Week 8).

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described internal admin endpoint that "works from our own frontend but a security review flagged it," walk through, out loud, in under 5 minutes, how you'd determine whether the actual gap is a missing CSRF token, a CORS misconfiguration mistaken for an authentication boundary (as in the cookbook entry), or a genuine authorization hole.

## Review Checklist

- [ ] Completed both chapters' own L3/L4 Mastery Checklists.
- [ ] Reproduced the CSRF/CORS forged-request demo.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.
- [ ] Can name the SAML assertion's four required validation checks from memory.

## Completion Criteria

- [ ] Can explain why a cookie alone doesn't stop CSRF and what the synchronizer-token pattern adds.
- [ ] Can correctly distinguish a CORS misconfiguration from an authentication boundary, given a described symptom.
- [ ] Can define SSO vs. Federated SSO precisely and state the mechanism difference (shared session vs. signed, portable assertion).
- [ ] Can name at least two commercial IAM vendors and their typical deployment model (on-prem/hybrid federation server vs. cloud IDaaS vs. open-source self-hosted).

## Retrospective

Note whether either cookbook entry (CORS mistaken for an auth boundary, a legacy form endpoint reopening CSRF) describes a pattern your own team's services could currently have — both are real, easy-to-reintroduce mistakes, not one-off flukes.

## Next Week

This is the final week of the Mid → Senior program. Continue to [Senior → Staff](../../senior-to-staff/README.md).
