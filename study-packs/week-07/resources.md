---
title: "Week 7 Resources"
week: 7
last_reviewed: 2026-07-29
---

# Week 7 Resources

Classified by authority: **PRIMARY**, **BOOK**, **TOOL**, **SECONDARY**.

---

## T-506/T-501 — Auto-Configuration and Bean Lifecycle

| Source | Type | Note |
|---|---|---|
| [Spring Framework documentation — Bean Factory](https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html) | PRIMARY | |
| [Spring Boot documentation — Auto-configuration](https://docs.spring.io/spring-boot/reference/using/auto-configuration.html) | PRIMARY | |
| Spring Framework 6.1.14 via Maven Central | TOOL | Produced the real lifecycle-order and `@Async`+`@Transactional` demonstrations; see `practice/java/week-07/spring-internals/` |

## T-511 — Security Filter Chain

| Source | Type | Note |
|---|---|---|
| [Spring Security documentation — Architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html) | PRIMARY | |
| [Jakarta Servlet specification](https://jakarta.ee/specifications/servlet/) | PRIMARY | The underlying `Filter`/`FilterChain` pattern |

## T-512/T-513 — OAuth2, OIDC, JWT

| Source | Type | Note |
|---|---|---|
| [RFC 6749 — OAuth 2.0](https://www.rfc-editor.org/rfc/rfc6749) | PRIMARY | |
| [RFC 7519 — JWT](https://www.rfc-editor.org/rfc/rfc7519) | PRIMARY | |
| [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html) | PRIMARY | |
| [Auth0 — PKCE explained](https://auth0.com/docs/get-started/authentication-and-authorization-flow/authorization-code-flow-with-pkce) | SECONDARY | |
| Plain JDK `javax.crypto` | TOOL | Produced the real HMAC-SHA256 JWT sign/verify/tamper/expiry demonstration; see `practice/java/week-07/security/` |

## General

| Source | Type | Note |
|---|---|---|
| `00-project/knowledge-base-audit.md` | PRIMARY | Confirmed only 1 shallow row on Spring Security/OAuth2/JWT combined in the original knowledge base |
| `00-project/learning-roadmap.md` §4 (Week 7) | PRIMARY | Full Week 7 (Plan B) spec this pack implements |
