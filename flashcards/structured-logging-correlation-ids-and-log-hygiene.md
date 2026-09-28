---
title: "Flashcards: Structured Logging, Correlation IDs, and Log Hygiene"
slug: structured-logging-correlation-ids-and-log-hygiene
document_type: flashcard-deck
domain: 13-observability
topic_id: T-2436
canonical: ../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md
last_updated: 2026-09-28
---

# Flashcards: Structured Logging, Correlation IDs, and Log Hygiene

**Canonical chapter:** [`syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md`](../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md)

## Card: What structured logging changes

**Prompt:**
Beyond "it's JSON," what two things change when you convert a prose log line into a structured record?

**Answer:**
Values become **named fields**, so `duration_ms > 400` is a filter instead of a regex capture; and the message becomes a **stable event name** (`order_placed`) instead of prose, so dashboards survive someone rewording the sentence. Same information, queryable shape.

**Why it matters:**
It is the difference between describing grepping and describing querying when asked how you found a root cause.

**Common trap:**
Adding fields while keeping prose in the message, which preserves the regex habit.

**Related:**
[Structured Logging, Correlation IDs, and Log Hygiene](../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md)

## Card: Why a request's log trail goes silent

**Prompt:**
A request's records stop halfway through, with no error logged anywhere. What happened?

**Answer:**
The work crossed a thread boundary. MDC is a `ThreadLocal` map, so a task on a pool thread has its own empty context — measured directly, the pooled record carried **no** `correlation_id` at all. The records still exist; they are just unlabelled, so a query by that ID stops returning them and it looks like execution stopped.

**Why it matters:**
It is the most common reason an investigation dead-ends, and nothing warns you.

**Common trap:**
Concluding the records were lost, or that `InheritableThreadLocal` fixes it — inheritance happens at thread creation, long before your request.

**Related:**
[Scoped Values and ThreadLocal Migration](../syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md)

## Card: Why clearing context matters as much as setting it

**Prompt:**
You propagate context into a pooled task. Why must you also clear it in a `finally`?

**Answer:**
Because pool threads are reused. Context left behind is inherited by the next task, attributing that work to the previous request's correlation ID. A stale ID is worse than a missing one: the records look complete and point at the wrong request.

**Why it matters:**
It turns a logging bug into actively misleading evidence during an incident.

**Common trap:**
Clearing only on the happy path rather than in a `finally`.

**Related:**
[Structured Logging, Correlation IDs, and Log Hygiene](../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md)

## Card: Keeping sensitive data out of logs

**Prompt:**
What is the primary control for sensitive data in logs, and why is redaction only secondary?

**Answer:**
Log **stable references** instead of values — `user_ref`, `card_last4`, never the email or the PAN. A log that never received the value cannot leak it. Pattern-based redaction only catches shapes you anticipated and is defeated by free-text fields, so it is a safety net for cases like an exception message carrying a payload.

**Why it matters:**
Logs replicate to hot search clusters, cold archives, vendor systems, and laptops during incidents — none covered by the access review that covers the database.

**Common trap:**
Assuming the realistic failure is someone deliberately logging a password. It is usually a temporary debugging aid that outlived its incident.

**Related:**
[OWASP Top 10 for Backend Services](../syllabus/12-security/owasp-top-10-for-backend-services.md)

## Card: ERROR, defined so it can be trusted

**Prompt:**
What is the decidable test for logging at ERROR rather than WARN?

**Answer:**
ERROR means a human must do something: the operation failed and will not self-heal. WARN means it is heading that way or a degraded path was taken. INFO is the business event trail; DEBUG is for reproducing a specific bug and is off in production.

**Why it matters:**
An ERROR nobody acts on trains everyone to ignore ERROR, and the next real one is missed — the same trust dynamic as alert fatigue.

**Common trap:**
Logging anything unexpected at ERROR.

**Related:**
[Metric Cardinality and Alert Fatigue](../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md)
