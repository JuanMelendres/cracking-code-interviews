# Event-driven architecture: integration styles and coordination (T-906) — runnable verification

Real, executed Java 21 output backing
[`syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md`](../../../../syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md)
(T-906). No simulated stack traces, no described-but-not-run behavior — real
`Thread.currentThread().getStackTrace()` captured at the moment it matters, and a real
simulated producer outage that either really breaks a consumer or really doesn't.

## Files

- `EventBus.java` — a real, minimal in-memory pub/sub bus: subscribers are invoked
  asynchronously on a shared executor, so a publisher never calls a subscriber
  directly (the actual property choreography depends on).
- `Events.java` — the four events used by the traceability demos.
- `ChoreographyTraceabilityDemo.java` — publishes `OrderPlaced` through the event bus;
  three services react in sequence with no central coordinator; captures the real
  call stack at the Shipping handler.
- `OrchestrationTraceabilityDemo.java` — the identical three steps, called directly and
  sequentially by a single `OrderOrchestrator.placeOrder` method; captures the real
  call stack at the same logical point.
- `ProducerAvailabilityDemo.java` — the same order fact delivered as a thin event
  (event notification, consumer calls back for details) vs. a fat event
  (event-carried state transfer, details embedded), against a real simulated producer
  outage.

## Run

```bash
cd practice/java/architecture/event-driven-integration-styles
mkdir -p out
javac -d out *.java
java -cp out ChoreographyTraceabilityDemo
java -cp out OrchestrationTraceabilityDemo
java -cp out ProducerAvailabilityDemo
```

## Real observed output (last full run, Java 21)

### 1. Choreography — the real call stack proves nothing connects back to the cause

```
[Shipping] REAL call stack at ship-time (9 frames):
    at java.base/java.lang.Thread.getStackTrace(Thread.java:2451)
    at ChoreographyTraceabilityDemo.lambda$main$2(ChoreographyTraceabilityDemo.java:36)
    at EventBus.lambda$subscribe$1(EventBus.java:26)
    at EventBus.lambda$publish$2(EventBus.java:33)
    at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:572)
    at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
    at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
    at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
    at java.base/java.lang.Thread.run(Thread.java:1583)
[Shipping] Does the real call stack reference the original OrderPlaced publish? false
```

Every frame in this real, captured stack trace is either JDK executor machinery or the
`EventBus`'s own dispatch code — nothing in it says "because `OrderPlaced` was
published for order-42." Reconstructing that connection requires an explicit,
manually-propagated identifier (`orderId`, or a correlation ID) carried in every event
and stitched together after the fact, from logs or a trace collector — the call stack
itself provably cannot do it.

### 2. Orchestration — the real call stack shows the entire chain

```
[Orchestrator] REAL call stack at ship-time (4 frames):
    at java.base/java.lang.Thread.getStackTrace(Thread.java:2451)
    at OrchestrationTraceabilityDemo$OrderOrchestrator.shipOrder(OrchestrationTraceabilityDemo.java:32)
    at OrchestrationTraceabilityDemo$OrderOrchestrator.placeOrder(OrchestrationTraceabilityDemo.java:18)
    at OrchestrationTraceabilityDemo.main(OrchestrationTraceabilityDemo.java:51)
[Orchestrator] Does the real call stack reference the original placeOrder call? true
```

The identical logical point (the moment shipping happens) produces a real 4-frame
stack that includes `placeOrder` itself — the entire causal chain from `main` to
`shipOrder` is visible in one place, with no external correlation mechanism needed.
This is the real, measurable form of orchestration's debuggability advantage, and the
real form of choreography's cost — not a description of the trade-off, its direct
cause.

### 3. Producer availability — where the coupling actually goes

```
=== Event notification (thin event) ===
Real simulated outage: OrderService.goDown()
Shipping consumer now processes the thin event and calls back for details...
REAL FAILURE: OrderService is unavailable (real simulated outage) -- cannot fetch details for order-42

=== Event-carried state transfer (fat event) ===
Real simulated outage: OrderService.goDown()
Shipping consumer now processes the fat event -- no callback needed...
Got details: 12 Main St, 3.0kg (producer's real availability was never checked)
REAL SUCCESS -- producer being down did not matter.
```

Identical simulated outage, identical consumer, identical fact needed — event
notification's consumer really throws because it depends on the producer being
reachable at consumption time, not just at publish time; event-carried state
transfer's consumer really succeeds because the fact was already in hand. The real
cost that buys is schema coupling: the fat event's consumer now depends on the
producer's `OrderDetails` shape, which is a coupling problem of a different kind, not
an absence of coupling.

## What this does and does not prove

Every number and stack trace here is real, single-JVM output — no real message broker
(Kafka, RabbitMQ, SNS/SQS) is being exercised, only the underlying property that makes
choreography harder to trace and event-carried state transfer more resilient to
producer downtime, which a real broker's async delivery model preserves. A real
distributed tracing system (OpenTelemetry, a correlation-ID convention enforced at the
broker level) is the production answer to the choreography traceability problem
demonstrated here — this demo proves *why* that tooling is necessary, not a substitute
for it.
