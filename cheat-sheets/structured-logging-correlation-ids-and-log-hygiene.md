---
title: "Cheat Sheet: Structured Logging, Correlation IDs, and Log Hygiene"
slug: structured-logging-correlation-ids-and-log-hygiene
document_type: cheat-sheet
domain: 13-observability
topic_id: T-2436
canonical: ../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md
last_updated: 2026-09-28
---

# Structured Logging, Correlation IDs, and Log Hygiene

**Canonical chapter:** [`syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md`](../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md)

## Core Mental Model

Logs are rows in a table nobody defined. Structured logging defines the columns; the correlation ID is the join key.

## Record Shape

`ts` (UTC ISO-8601) · `level` · `service` / `version` / `instance` · `event` (a **stable name**, not prose) · `correlation_id` / `trace_id` · `user_ref` / `tenant_id` (**references, never values**) · domain fields (`order_id`, `duration_ms`)

Prose: `Order ORD-1001 placed successfully in 412ms` → regex that breaks on rewording.
Structured: `{"event":"order_placed","order_id":"ORD-1001","duration_ms":"412"}` → `duration_ms > 400` is a filter.

## Levels, Decidably

| Level | Test | In prod |
|---|---|---|
| ERROR | A human must act | On, alertable |
| WARN | Heading toward ERROR | On, trended |
| INFO | Business event trail | On |
| DEBUG | Reproducing a specific bug | Off |

An ERROR nobody acts on trains everyone to ignore ERROR.

## The Context-Loss Trap (measured)

MDC is a `ThreadLocal`. Work submitted to a pool logs with **no `correlation_id`** — nothing throws, the trail just appears to stop.

```java
Map<String,String> captured = snapshot();   // submitting thread
pool.submit(() -> {
    adopt(captured);
    try { ... } finally { clear(); }        // pooled threads are REUSED
});
```

Stale context is worse than missing context: it attributes work to the wrong request.

## Sensitive Data

1. **Design:** log `user_ref`, `card_last4` — never the email or the PAN.
2. **Net:** pattern redaction at the sink, for exception messages carrying payloads.

Logs replicate to hot search, cold archive, vendor systems, and laptops — none covered by your DB access review. The realistic leak is a temporary debug aid that outlived its incident.

## Volume Levers, In Order

1. Delete record types nobody queries. 2. Sample by **outcome** (never sample errors). 3. Tier retention. 4. Move counts to metrics — logs answer "what happened to *this* one."

## Common Pitfalls

- Prose + regex parsing.
- Assuming MDC crosses a thread boundary.
- Setting context without clearing it.
- Logging whole requests/responses/objects.
- `log.debug("x: " + expensive())` — concatenates even when DEBUG is off. Use parameterized logging.
- Async appenders **drop** records when the queue fills — during the load spike you are investigating.

## Interview Answer Skeleton

**30-sec:** Structured records make filtering possible and event names stable. A correlation ID from the edge, carried in MDC, ties one request together. The two traps: MDC does not follow work onto a pool thread, and sensitive values should never reach the log at all — log references.

## Related

- [Logging, Metrics, Tracing, and OpenTelemetry](../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md)
- [Metric Cardinality and Alert Fatigue](../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md)
- [Scoped Values and ThreadLocal Migration](../syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md)
