# CQRS: read/write separation (T-904) — runnable verification

Real, executed Java 21 output backing
[`syllabus/17-architecture/cqrs-read-write-separation.md`](../../../../syllabus/17-architecture/cqrs-read-write-separation.md)
(T-904). No framework, no mocked timings — a real normalized write model, a real denormalized
read model, a real `BlockingQueue` + background thread as the asynchronous boundary between them,
and three real, measured demos: the honest floor of eventual-consistency lag, a deterministic
stale-read proof, and a real query-cost comparison.

## Files

- `Order.java` / `OrderItem.java` — write-side (command-side) domain: normalized, items in a
  separate list.
- `DomainEvent.java` — `OrderCreated` / `ItemAdded` / `OrderCompleted`, published by the command
  side, consumed by the projector. The write side never reads these back.
- `OrderCommandService.java` — the only thing allowed to mutate the write store; every mutation
  publishes an event.
- `OrderSummaryView.java` — the read side: one flattened, denormalized row per order, shaped for
  "show me this order," not for write-side invariants.
- `Projector.java` — the real asynchronous boundary. A background thread that drains the event
  queue and folds each event into the read model. Supports an artificial per-event delay so the
  stale-read demo can force a deterministic lag window instead of racing the JVM scheduler.
- `EventualConsistencyLagDemo.java`, `StaleReadDuringLagDemo.java`,
  `QueryComplexityComparisonDemo.java` — the three demos below.

## Run

```bash
cd practice/java/architecture/cqrs-read-write-separation
javac -d out *.java
java -cp out EventualConsistencyLagDemo
java -cp out StaleReadDuringLagDemo
java -cp out QueryComplexityComparisonDemo
```

## Real observed output (last full run, Java 21, single laptop, no network involved anywhere)

### 1. `EventualConsistencyLagDemo` — the honest floor of "eventually consistent"

5,000 orders, each polled from the read model immediately after its write commits, until it
becomes visible:

```
Samples: 5000
min=0.2us  avg=2.4us  p50=1.5us  p99=9.6us  max=588.1us
Events applied by projector: 5000 (expected 5000)
```

This is real, in-process, zero-network lag — no HTTP, no Kafka, no second machine, same JVM. It is
not zero, and it cannot be, because the write commits synchronously on the caller's thread while
the read model update happens on a different thread, mediated by a queue. The honest lesson: even
the *best possible case* for CQRS's async boundary — same process, uncontended queue, idle
machine — has a real, nonzero, measurable propagation delay. In a real distributed system (a
message broker over a real network, a separate consumer service, real backpressure) this number
would be orders of magnitude larger; this demo isolates the mechanism's own irreducible cost from
network cost.

### 2. `StaleReadDuringLagDemo` — a deterministic, observed stale read

The projector is deliberately slowed (150ms of real busy-work per event) so the lag window is
forced rather than raced for:

```
Write committed. Write-side ground truth right now:
  write model: status=COMPLETED items=1 total=59.97

Polling the read model immediately, before the projector has caught up:
  t+11ms: read model has NO record of order 1 yet (stale)
  t+96ms: read model has NO record of order 1 yet (stale)
  t+181ms: read model shows status=OPEN items=0 total=0
  t+266ms: read model shows status=OPEN items=0 total=0

Waiting for the projector to fully catch up (draining 3 real events at ~150ms each)...
  t+452ms: read model now CONSISTENT: status=COMPLETED items=3 total=59.97
```

The write side was correct the entire time — `rawOrder()` shows the fully completed order the
instant the command returns. The read side genuinely does not know the order exists for the first
~180ms, then genuinely shows a partially-applied intermediate state (`OPEN`, 0 items — the
`OrderCreated` event applied but `ItemAdded`/`OrderCompleted` not yet), then genuinely converges.
This is what "the read model is eventually consistent" means as an operational fact, not a design
doc phrase: a real caller, reading its own write through the read side, would really see a missing
or wrong answer for a real window of time.

### 3. `QueryComplexityComparisonDemo` — the actual reason a read model earns its keep

50,000 orders, 4 items each (300,000 total events), read model fully caught up before timing
starts — this measures query cost, not projection lag:

```
Orders: 50000, items/order: 4, total events: 300000
Write-side path (walk orders + items, sum per query): 15.84ms
Read-side path (sum precomputed per-order totals):    3.45ms
Speedup: 4.6x
Results identical: true
```

A second run measured 17.63ms vs 3.26ms (5.4x) — the exact multiplier moves with JIT
warm-up and scheduler noise, but the shape is stable and honest: computing "total spend per
customer" by walking every order and every item inside it (the write model's normalized shape)
is real, measurably more expensive than summing a number that was already precomputed onto each
read-model row at write time. Both paths are checked for exact equality (`Results identical:
true`) — the read model is not a different answer, it is the same answer, pre-shaped for the
query it exists to serve.

## What this does and does not prove

This demo isolates the *mechanism* of CQRS — a real async boundary, real eventual consistency,
real query-cost asymmetry — without a message broker or a second service, so the lag numbers above
are a floor, not a forecast: a real production CQRS system (Kafka/SQS between the write and read
sides, a separate read-side service, network hops) will show lag in the tens-to-thousands of
milliseconds, not microseconds. What does not change with distance is the *shape* of the trade-off
demonstrated here: a real asynchronous boundary, a real window where the two sides disagree, and a
real reason the read side is faster for the query it was built for.
