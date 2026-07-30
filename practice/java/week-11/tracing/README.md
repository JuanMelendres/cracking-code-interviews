# Week 11 Java — OpenTelemetry Tracing — runnable verification

One real demo, real OpenTelemetry SDK (no external collector needed — uses the console/logging exporter).

## Setup and run

```bash
cd practice/java/week-11/tracing
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/TracingDemo.java
java -cp "out:lib/*" TracingDemo
```

**Real observed output (last run, trace/span IDs will differ on your run):**

```
'order-service.validate' : 889ba9722928321ef6ddda8b315baf4e c7a01645cfd579bd INTERNAL
'payment-db.insert'      : 889ba9722928321ef6ddda8b315baf4e 71782e03d32aa31d INTERNAL
'payment-service.charge' : 889ba9722928321ef6ddda8b315baf4e 869a0257119f7fe4 INTERNAL
'POST /orders'           : 889ba9722928321ef6ddda8b315baf4e 43a86499714f5379 INTERNAL {http.route=/orders, http.method=POST}
```

**What this proves:** all 4 spans share the identical `traceId` (the first hex string after each span name) while each has a unique `spanId` (the second hex string) — the real mechanism that lets a tracing backend reconstruct the full call tree for one request, from a real OpenTelemetry SDK run, not a diagram.
