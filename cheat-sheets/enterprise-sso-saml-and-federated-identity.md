---
title: "Cheat Sheet: Enterprise SSO, SAML, and Federated Identity"
slug: enterprise-sso-saml-and-federated-identity
document_type: cheat-sheet
domain: 12-security
topic_id: T-1309
canonical: ../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md
last_updated: 2026-09-14
---

# Enterprise SSO, SAML, and Federated Identity

**Canonical chapter:** [`syllabus/12-security/enterprise-sso-saml-and-federated-identity.md`](../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md)

## Core Mental Model

Treat a signed SAML assertion (or an OIDC ID token) as a sealed, notarized letter, not a phone call — the Service Provider never calls the Identity Provider back to confirm; the user's own browser physically carries the sealed document, and the SP's job is verifying the seal (signature), that the letter was addressed to *this* SP (audience), and that it hasn't expired.

## Essential Definitions

- **SSO** — one login for multiple apps inside one shared trust boundary, often a shared session.
- **Federated SSO** — SSO across separate organizations, via a signed, portable assertion/token, no shared session.
- **IdP (Identity Provider)** — authenticates the user, issues the signed assertion/token.
- **SP / RP (Service Provider / Relying Party)** — the application trusting the IdP's assertion.
- **SAML Assertion** — signed XML document: an authentication statement + (usually) an attribute statement.
- **SAML Metadata** — XML describing each party's endpoints and certificates; exchanging it establishes trust.

## Decision Table

| Situation | Lean toward |
|---|---|
| Enterprise IdP already established, legacy internal apps | SAML |
| Mobile app or SPA client | OIDC |
| Need identity proof, not just API access | OIDC's ID Token or a SAML Assertion — never a bare OAuth2 access token |
| Multiple partner IdPs | A separate Spring Security registration ID per IdP |
| On-premise/regulatory data residency requirement | Self-hosted IdP (PingFederate, Keycloak, ADFS) |
| Fast setup, low operational burden | Cloud IDaaS (Okta, Azure AD/Entra ID, Auth0) |

**SAML vs. OAuth2 vs. OIDC:**

| | SAML 2.0 | OAuth2 | OIDC |
|---|---|---|---|
| Solves | Federated authentication | Delegated authorization | Federated authentication on OAuth2 |
| Format | XML | Token (often JWT) | JSON/JWT (ID Token) |
| Published | 2005 | 2012 | 2014 |

## Commercial IAM Landscape

| Product | Model | Typical role |
|---|---|---|
| PingFederate | On-prem/hybrid | Large-regulated-enterprise federation hub (SAML/OAuth2/OIDC/WS-Fed) |
| Okta / Auth0 | Cloud (SaaS) | Dominant cloud-native IDaaS |
| Azure AD / Entra ID | Cloud, Microsoft-integrated | Default when already on Microsoft 365 |
| Keycloak | Self-hosted, open-source | Standard OSS alternative, local dev/test |
| ADFS | On-prem, Windows Server | Older on-premise-heavy enterprises |

## Spring Setup

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-saml2-service-provider</artifactId>
</dependency>
```

```yaml
spring:
  security:
    saml2:
      relyingparty:
        registration:
          pingfederate:
            assertingparty:
              entity-id: https://idp.example.com/pf
              singlesignon:
                url: https://idp.example.com/idp/SSO.saml2
              verification:
                credentials:
                  - certificate-location: classpath:pingfederate.crt
```

`.saml2Login(withDefaults())` on the `SecurityFilterChain` wires it in.

## Common Pitfalls

- Treating a bare OAuth2 access token as identity proof — it proves authorization, not who the user is.
- Validating only the assertion's signature and skipping Issuer/Audience/time-window checks.
- Hardcoding an IdP's signing certificate with no rotation-monitoring plan.
- Confusing SP-initiated (starts at the app) with IdP-initiated (starts at the IdP's portal) flows.

## Interview Answer Skeleton

**30-sec:** SSO is one login for multiple apps in one trust boundary; Federated SSO extends that across organizations via SAML (XML, since 2005) or OIDC (JSON/JWT, since 2014, on OAuth2). The SP validates a signed assertion/token, never sees the password. Enterprise backends integrate against a commercial IdP like PingFederate, Okta, or Azure AD rather than building identity themselves.

**2-min:** Add the SAML/OAuth2/OIDC distinction table, the real Spring config (`spring-security-saml2-service-provider`, `relyingparty.registration.*`, `.saml2Login()`), and the four-check assertion validation (signature, Issuer, Audience, time window).

**Staff-level framing:** A SAML-vs-OIDC or self-hosted-vs-cloud-IdP choice is a real organizational trade-off (existing federation landscape, data-residency requirements, operational capacity) — not a pure technical preference.

## Production Warning Signs

- Intermittent, unreproducible SAML login failures — check for clock drift between the SP and IdP's system clocks against the assertion's `NotBefore`/`NotOnOrAfter` window.
- Signature validation suddenly failing for everyone — check whether the IdP rotated its signing certificate.
- Login succeeds but roles/groups are empty — check the raw attribute names in the assertion against what the mapping `Converter` expects (enterprise IdPs use verbose namespaced URIs).

## Related

- syllabus/12-security/oauth2-oidc-and-jwt.md
- syllabus/12-security/authn-authz-rbac-vs-abac.md
- syllabus/12-security/secrets-management-and-key-rotation.md
- syllabus/05-spring/spring-mvc-fundamentals.md
- syllabus/15-cloud/twelve-factor-config.md
