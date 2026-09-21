---
title: "Flashcards: Kafka Security — SASL Authentication and ACL Authorization"
slug: kafka-security-authentication-and-authorization
document_type: flashcard-deck
domain: 09-messaging-event-driven
topic_id: T-2421
canonical: ../syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md
last_updated: 2026-09-21
---

# Flashcards: Kafka Security — SASL Authentication and ACL Authorization

**Canonical chapter:** [`syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md`](../syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md)

## Card: Authentication vs. authorization, with real exceptions

**Prompt:**
What's the real, distinct difference between Kafka SASL authentication and ACL authorization, and what exception does each produce on failure?

**Answer:**
Authentication (SASL) proves who a client is — a wrong password produces a real `SaslAuthenticationException`, before any resource is even considered. Authorization (ACLs) checks what an already-authenticated principal can do — a missing grant produces a resource-specific exception like `TopicAuthorizationException`.

**Why it matters:**
Conflating the two wastes real debugging time on the wrong layer — an authorization failure is never fixed by re-checking credentials.

**Common trap:**
Treating any Kafka access failure as one undifferentiated "security" problem.

**Related:**
[Kafka Security](../syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md)

## Card: The real group-ACL gotcha

**Prompt:**
A principal has full Read/Write/Describe ACL access granted on a topic. Can it consume that topic?

**Answer:**
Not necessarily — real, discovered evidence: consuming also requires a separate ACL grant on the consumer *group* resource. Without it, the client fails with a real `GroupAuthorizationException` despite complete topic access.

**Why it matters:**
A genuine, easy real-world mistake — an onboarding process requesting only topic access reproduces this exact incident for every consumer onboarded against it.

**Common trap:**
Re-granting the already-correct topic ACL instead of identifying the actually-missing group ACL.

**Related:**
[Kafka Security](../syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md)

## Card: What a super user really bypasses

**Prompt:**
Does a Kafka super user get a very permissive set of ACLs, or something architecturally different?

**Answer:**
Architecturally different — a super user's requests skip ACL evaluation entirely, for every resource. Verified directly: an `admin` super user produced to and consumed from a topic another principal had zero grants for, with no error at all.

**Why it matters:**
Explains why super-user status should never be handed to an application service account "to save time" — it removes all future auditability of what that principal actually needed.

**Common trap:**
Treating `KAFKA_SUPER_USERS` as equivalent to a broad wildcard ACL grant rather than a total, unconditional bypass.

**Related:**
[Kafka Security](../syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md)
