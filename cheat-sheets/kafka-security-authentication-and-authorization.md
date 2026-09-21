---
title: "Cheat Sheet: Kafka Security — SASL Authentication and ACL Authorization"
slug: kafka-security-authentication-and-authorization
document_type: cheat-sheet
domain: 09-messaging-event-driven
topic_id: T-2421
canonical: ../syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md
last_updated: 2026-09-21
---

# Kafka Security: SASL Authentication and ACL Authorization

**Canonical chapter:** [`syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md`](../syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md)

## Core Mental Model

Authentication (SASL) proves who a client is; authorization (ACLs) checks what an already-authenticated principal can do against a specific resource. Two independent layers, two independent real exception types — a wrong password never reaches ACL evaluation at all.

## Essential Definitions

- **SASL/PLAIN** — the simplest SASL mechanism: a real username/password pair, checked against configured JAAS credentials.
- **ACL** — (principal, operation, resource, permission type) — every grant is explicit and listable.
- **`StandardAuthorizer`** — the modern, KRaft-native ACL authorizer (replaces the older ZooKeeper-based `AclAuthorizer`).
- **Super user** — a principal ACL checks are skipped for entirely, not "a very permissive grant."

## Decision Table

| Failure | Real exception | Layer |
|---|---|---|
| Wrong username/password | `SaslAuthenticationException` | Authentication |
| Missing topic ACL | `TopicAuthorizationException` | Authorization |
| Missing consumer-group ACL | `GroupAuthorizationException` | Authorization |
| Principal is a super user | No failure — full bypass | Neither checked |

## Common Pitfalls

- Granting a topic ACL and assuming it covers consuming — a separate, real consumer-group ACL is also required (a genuine, discovered gotcha, not a textbook fact).
- Treating an authorization failure as a credentials problem, or vice versa — the two layers fail independently and never overlap.
- Handing an application service account `KAFKA_SUPER_USERS` status "to save time" — removes all future auditability of what it actually needed.
- Sharing one Kafka credential across multiple services/people — destroys the per-principal accountability SASL exists to provide.

## Interview Answer Skeleton

**30-sec:** SASL authenticates (who), ACLs authorize (what) — two independent layers with independent real failures: `SaslAuthenticationException` for a wrong password, `TopicAuthorizationException`/`GroupAuthorizationException` for a missing grant. A super user bypasses ACL checks entirely.

**2-min:** Add the real, discovered gotcha: a topic-level ACL grant doesn't cover consuming — Kafka's consumer protocol separately authorizes the consumer group resource, and a missing group grant produces a real `GroupAuthorizationException` even with full topic access. Verified directly: `alice` with `Read`/`Write`/`Describe` on her own topic still fails to consume until a second, independent `--operation Read --group "*"` grant is added.

**Staff-level framing:** The real recurring cost of this topic is process, not technology — an onboarding checklist that only requests topic access reproduces the identical `GroupAuthorizationException` incident for every future consumer onboarded against it.

## Production Warning Signs

- A newly onboarded consumer works in every test environment, then fails immediately in a shared cluster with `GroupAuthorizationException` — check whether the onboarding request included a group ACL, not just a topic ACL.
- A departing contractor's own credentials are disabled, but a job they set up keeps running — a shared service-account credential, not their own, defeating per-principal accountability.

## Real Measured Numbers

- Real `SaslAuthenticationException`: `alice` with a wrong password, rejected before any topic is considered.
- Real `TopicAuthorizationException`: `alice`, correctly authenticated, denied on a topic with zero ACL grants.
- Real `GroupAuthorizationException`: `alice`, with full topic access, still denied consuming until a second, independent group ACL is granted.
- Real super-user bypass: `admin` produces to and consumes from a topic `alice` has zero grants for, with zero errors.

## Related

- syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md
- syllabus/12-security/applied-cryptography-hashing-signing-tls.md
