---
title: "Flashcards: Enterprise SSO, SAML, and Federated Identity"
slug: enterprise-sso-saml-and-federated-identity
document_type: flashcard-deck
domain: 12-security
topic_id: T-1309
canonical: ../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md
last_updated: 2026-09-14
---

# Flashcards: Enterprise SSO, SAML, and Federated Identity

**Canonical chapter:** [`syllabus/12-security/enterprise-sso-saml-and-federated-identity.md`](../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md)

## Card: SSO vs. Federated SSO

**Prompt:**
What's the precise difference between SSO and Federated SSO?

**Answer:**
SSO is one login for multiple applications inside one shared trust boundary (often a shared session). Federated SSO extends that across separate organizations via a protocol-based trust relationship (SAML or OIDC) — no shared session, only a signed, portable assertion or token validated independently by each Service Provider.

**Why it matters:**
The two terms are routinely used interchangeably despite describing genuinely different trust mechanisms.

**Common trap:**
Treating Federated SSO as just "SSO with more applications" rather than a genuinely different, cross-organization trust model.

**Related:**
[Enterprise SSO, SAML, and Federated Identity](../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md)

## Card: What a SAML Assertion actually is

**Prompt:**
What is a SAML Assertion, mechanically?

**Answer:**
A signed XML document the Identity Provider produces, containing an authentication statement (confirming the user authenticated) and typically an attribute statement (claims about the user) — the Service Provider validates its signature and never sees the user's actual password.

**Why it matters:**
The concrete artifact underlying every SAML federation — knowing its two real parts separates real understanding from reciting "SAML uses XML."

**Common trap:**
Describing a SAML Assertion as if it were a session token rather than a one-time, signed proof document with its own short validity window.

**Related:**
[Enterprise SSO, SAML, and Federated Identity](../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md)

## Card: Why a bare OAuth2 access token isn't identity proof

**Prompt:**
Why can't a bare OAuth2 access token be used to reliably identify a user?

**Answer:**
OAuth2 was designed for delegated authorization ("can this app access this resource"), not authentication — an access token proves the bearer is allowed to call an API, nothing about who that bearer actually is. OIDC exists specifically to add a standardized identity layer (the ID Token) on top of OAuth2 for exactly this reason.

**Why it matters:**
A real, common architectural mistake — using access-token possession as a login mechanism — that OIDC's ID Token was purpose-built to prevent.

**Common trap:**
Assuming any OAuth2 integration automatically also answers "who is this user."

**Related:**
[Enterprise SSO, SAML, and Federated Identity](../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md), [OAuth2, OIDC, and JWT](../syllabus/12-security/oauth2-oidc-and-jwt.md)

## Card: The four-check assertion validation discipline

**Prompt:**
Beyond checking the signature, what else must a Service Provider validate on an incoming SAML assertion?

**Answer:**
The Issuer (matches the expected IdP), the Audience Restriction (the assertion was issued specifically for this SP, not a different one), and the Conditions (`NotBefore`/`NotOnOrAfter` — the assertion is currently within its valid time window).

**Why it matters:**
A signature alone only proves the IdP issued *some* assertion — it says nothing about whether this specific assertion was intended for this SP or is still valid, both real, exploitable gaps if skipped.

**Common trap:**
Answering "check the signature" as if that's the complete validation story.

**Related:**
[Enterprise SSO, SAML, and Federated Identity](../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md)

## Card: The commercial IAM landscape, by deployment model

**Prompt:**
Name a commercial/enterprise IAM product for each of: on-premise/hybrid federation server, cloud IDaaS, and open-source self-hosted.

**Answer:**
On-premise/hybrid: PingFederate (or ADFS). Cloud IDaaS: Okta or Azure AD/Entra ID. Open-source self-hosted: Keycloak.

**Why it matters:**
"Experience with commercial IAM solutions like PingFederate" is a real, recurring job-posting requirement — knowing the landscape by name and deployment model is part of demonstrating that experience.

**Common trap:**
Naming only cloud IDaaS products and being unable to name an on-premise federation server, missing the large-regulated-enterprise half of the landscape.

**Related:**
[Enterprise SSO, SAML, and Federated Identity](../syllabus/12-security/enterprise-sso-saml-and-federated-identity.md)
