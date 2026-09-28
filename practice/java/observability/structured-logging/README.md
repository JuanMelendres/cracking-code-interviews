# Structured Logging, Correlation IDs, and Log Hygiene — Real Demo

Backs [`syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md`](../../../../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md) (T-2436).

Deliberately dependency-free. A real service would use SLF4J + Logback with a JSON encoder and MDC; the MDC here is a `ThreadLocal<Map<String,String>>`, which is exactly what MDC is. The point is the record shape and the propagation rules, not a library.

Timestamps are pinned to a fixed instant so the transcript is byte-stable across runs.

## Run it

```bash
mkdir -p out
javac -d out src/StructuredLoggingDemo.java
java -cp out StructuredLoggingDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **The same event, unstructured then structured** — the prose line supports only a brittle regex; the JSON record supports `duration_ms > 400` as a query, with the message reduced to a stable event *name*.
- **Correlation ID propagation** — four records across one request all carry `correlation_id` without a single call site passing it, because it lives in the context map.
- **Context loss across a thread boundary, reproduced** — a task submitted to a pool logs with **no** `correlation_id` at all, because the pool thread has its own `ThreadLocal`. The snapshot-and-adopt version keeps it. The demo also clears the context afterwards, because pooled threads are reused and stale context is worse than missing context: it attributes work to the wrong request.
- **Redaction** — a real email and a real test PAN redacted by pattern, alongside the more important point that the design is to log stable references (`user_ref`, `card_last4`) so the sensitive value never reaches the pipeline at all.
- **Level discipline** — one record per level with an explicit `actionable` field, to make concrete what separates ERROR from WARN.
