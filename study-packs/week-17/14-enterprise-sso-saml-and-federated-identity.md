---
title: "T-1309 · Enterprise SSO, SAML, and Federated Identity"
topic_id: T-1309
domain: Security
tier: Core
prerequisites: []
week: 17
last_reviewed: 2026-09-17
canonical: ../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md
---

# T-1309 · Enterprise SSO, SAML, and Federated Identity

**Addendum topic — added 2026-09-17.** This chapter was written 2026-09-14, after Week 17's original "Security Domain Closure" sprint (2026-08-02, `01`–`07`) had already shipped and closed. It was never folded back into this week's required reading — a real scheduling gap, closed by this file (alongside `13-csrf-cors-and-session-security.md`, its own later addition). The original seven-topic sprint is left untouched as an accurate historical record of that session.

**Canonical chapter:** [Enterprise SSO, SAML, and Federated Identity](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md). This file is this week's study-pack entry point for it — a short summary of each section plus a link to the full canonical treatment.

**Verification note, stated honestly:** unlike this week's other topics, this chapter's Spring Security configuration (`spring-security-saml2-service-provider`, the `spring.security.saml2.relyingparty.registration.*` property structure) is real and verified against Spring Security's own current reference documentation, but is **not** backed by a locally executed multi-party demo — a faithful end-to-end demo would require standing up a real SAML Identity Provider (PingFederate, Keycloak, or equivalent), out of scope for this project's practice budget. This chapter states that limitation explicitly rather than presenting it as executed, matching [OAuth2, OIDC, and JWT](../../syllabus/12-security/oauth2-oidc-and-jwt.md)'s own identical honesty discipline for the same reason. No hands-on lab is included in this addendum for that reason — treat this topic as reading-only this week.

## 1. The concept

Treat a signed SAML assertion (or an OIDC ID token) as a sealed, notarized letter, not a phone call — the Service Provider never calls the Identity Provider directly to verify it in real time. The user's own browser physically carries the sealed letter from the IdP to the SP; the SP's job is verifying the seal (signature), the addressee (audience restriction), and the expiry — the same "verification is a pure local computation" mental model [OAuth2, OIDC, and JWT](../../syllabus/12-security/oauth2-oidc-and-jwt.md) already establishes for JWTs, applied to a different envelope format (XML, not JSON). → [Mental Model](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#mental-model).

## 2. Why it exists

SAML 2.0 (OASIS, 2005) lets a user authenticate once, with one identity system their organization controls, and have that trusted by many separate applications without each one holding a copy of the user's credentials — the foundational protocol behind enterprise Federated SSO for nearly two decades before OIDC existed. **Federated identity** is the general pattern: separating *who authenticates the user* (the IdP) from *who relies on that authentication* (the SP), connected by a standards-based trust relationship, not a shared database. → [Definition and Purpose](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#definition-and-purpose).

## 3. The core distinction most candidates get imprecise

SAML, OAuth2, and OIDC are routinely conflated. The precise version: SAML and OIDC both solve federated *authentication* ("who is this user"); OAuth2 alone solves delegated *authorization* ("can this app access this resource"), and was never designed to answer "who is this user" at all — a bare access token proves nothing about identity by itself, exactly the gap OIDC closes by adding a proper identity layer (the ID Token) on top of OAuth2. → [Core Concepts](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#core-concepts) has the full three-way comparison table (format, published year, primary use case, trust artifact).

## 4. The commercial IAM landscape

Real enterprise backend work rarely means building an identity provider — it means integrating against one a separate team or vendor already operates (PingFederate, Keycloak, Okta, Azure AD, Auth0, ADFS among the real, named options this chapter covers) — knowing this landscape by name is a real, practical expectation for this role shape. → [The Commercial IAM Landscape](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#the-commercial-iam-landscape).

## 5. A real production scenario worth knowing cold

Intermittent, unreproducible SAML login failures, eventually traced to genuine clock drift between the SP's and IdP's servers — an assertion's validity window (`NotBefore`/`NotOnOrAfter`) is only a few minutes wide by design, and a few seconds of drift can occasionally push a validation attempt just outside it. Immediate fix: a small, explicit clock-skew tolerance in the SAML2 validation config. Permanent fix: real, monitored NTP sync on both sides — the tolerance is a legitimate permanent mitigation for real-world drift, not a substitute for actually keeping clocks synchronized. A candidate who's actually operated a SAML integration will often name clock skew unprompted as a first-suspect cause of intermittent SAML failures. → [Production Scenarios](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#production-scenarios).

## 6. Common mistakes

Using "SSO" and "Federated SSO" interchangeably without distinguishing a shared session boundary from a cross-organization trust relationship. Treating SAML as obsolete/replaceable-by-OIDC by default — the right choice is almost never free, it's dictated by whatever IdP the organization already operates. Assuming a bare OAuth2 access token identifies a user. → [Common Mistakes](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#common-mistakes).

## 7. Interview questions

1. What's the precise difference between what OAuth2 and OIDC each actually solve, and why was OIDC built on top of OAuth2 rather than as a separate protocol?
2. A team reports intermittent, unreproducible SAML login failures with no obvious pattern. What's your first suspect, and why?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups: → [Interview Questions](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#interview-questions).

## 8. Summary

→ [Summary](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#flashcards).

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md#practice-exercises) — reading/design exercises only, per this file's own verification note above (no executable demo).

## 13. Official References

- [OASIS: SAML 2.0 Core Specification](https://docs.oasis-open.org/security/saml/v2.0/saml-core-2.0-os.pdf)
- [Spring Security Reference: SAML 2.0 Login](https://docs.spring.io/spring-security/reference/servlet/saml2/index.html)
- [PingIdentity: Introduction to PingFederate](https://docs.pingidentity.com/pingfederate/13.1/introduction_to_pingfederate/pf_intro_to_pf.html)
