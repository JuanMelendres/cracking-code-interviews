---
title: "Cheat Sheet: Spring WebFlux and Reactive Programming"
slug: spring-webflux-and-reactive-programming
document_type: cheat-sheet
domain: spring
topic_id: T-509
canonical: ../handbook/spring/spring-webflux-and-reactive-programming.md
last_updated: 2026-09-01
---

# Spring WebFlux and Reactive Programming

**Canonical chapter:** [`syllabus/05-spring/spring-webflux-and-reactive-programming.md`](../syllabus/05-spring/spring-webflux-and-reactive-programming.md)

## Core Mental Model

A reactive pipeline is a *declared* sequence of transformations that does nothing until subscribed to — building a `Flux` chain is like writing a recipe, not cooking a meal. Once subscribed, data flows downstream (`onNext`), but *demand* flows upstream first: nothing is produced until a subscriber requests it, and only up to however much was requested. This single idea explains backpressure, cold-vs-hot semantics, and why blocking inside the pipeline is dangerous: a small, fixed number of threads juggle many concurrent flows — block one, and every other flow sharing it stalls too.

## Essential Definitions

- **`Mono<T>`** (0 or 1 element), **`Flux<T>`** (0 to N elements) — Reactor's Reactive Streams (`Publisher`/`Subscriber`/`Subscription`) implementations.
- **Cold vs. hot publisher** — cold re-executes its source per subscriber, independently; hot subscribes once and multicasts to every subscriber.
- **Backpressure** — a real, enforced demand contract: a `Subscriber` calls `request(n)`; a well-behaved `Publisher` never emits more than the outstanding requested amount.
- **`subscribeOn`** moves subscription-time execution (including the source); **`publishOn`** moves only downstream signal delivery — *usually*. Operator fusion can let `publishOn` pull data via its own worker thread instead, silently moving the source too.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Is the workload genuinely I/O-bound with very high concurrent connection counts? | Consider reactive (or virtual threads — evaluate both) |
| Does the workload need explicit, enforced backpressure? | Reactive — this is its clearest unique advantage |
| Team unfamiliar with reactive, workload not backpressure-sensitive? | Prefer blocking + virtual threads |
| Is even one dependency in the call path still a blocking driver (JDBC, a blocking HTTP client)? | Do not adopt WebFlux yet |

**Trade-offs:**

| Model | Threads needed | Backpressure | Best fit |
|---|---|---|---|
| Servlet (blocking) | ~1 per concurrent request | None (implicit) | Most CRUD/moderate-concurrency services |
| Servlet + virtual threads (JDK 21+) | Many cheap virtual threads | None (implicit) | High-concurrency I/O-bound, without reactive's complexity |
| WebFlux (reactive) | Small, fixed pool | Explicit, enforced | Very high concurrency, streaming, backpressure-sensitive pipelines |

## Key Numbers (real, executed against Project Reactor 3.6.10 + Spring WebFlux 6.1.14)

Cold vs. hot:

```
COLD Flux: Real side-effect executions: 2 (once per subscriber, independently)
HOT Flux (ConnectableFlux): Real side-effect executions: 1 (shared one upstream subscription)
```

Blocking-vs-offloaded timing:

```
BUGGY: blocking calls on the tiny event-loop scheduler -- Total: 943ms
FIXED: blocking calls offloaded to Schedulers.boundedElastic() -- Total: 316ms
```

Operator-fusion surprise:

```
without .hide(): source runs on: boundedElastic-1   <!-- NOT "main" -- fusion pulled it there -->
with .hide():     source runs on: main               <!-- textbook behavior restored -->
```

## Common Pitfalls

- Calling a blocking method (JDBC, `Thread.sleep`, a blocking HTTP client) inside a reactive chain without offloading it.
- Assuming `publishOn` never affects the source — operator fusion on simple, fuseable sources (e.g., `Mono.fromSupplier`) can pull the source itself onto `publishOn`'s worker thread.
- Treating "reactive" as synonymous with "faster" rather than "scales differently under I/O-bound concurrency."
- A *partial* reactive migration with leftover blocking calls — often worse than not migrating at all.

## Interview Answer Skeleton

**30-sec:** WebFlux is Spring's reactive, non-blocking web framework built on Reactor's `Mono`/`Flux`. It scales a small, fixed thread pool across many concurrent I/O-bound operations via real, enforced backpressure — but every call in the chain must be non-blocking, or you starve the whole pool.

**2-min:** Add the measured cold-vs-hot proof (2 vs. 1 executions), the demand-driven backpressure proof, and the measured ~3x blocking-vs-offloaded slowdown connected to a real production incident: a partial reactive migration performing worse than no migration at all.

**Whiteboard:** Draw a small, fixed row of worker threads ("event loop — never block these"). Draw a request flowing through operator boxes with an arrow going *backward* labeled "demand." Then shade the whole row red after one blocking operator — "one blocking call here stalls every other request sharing this thread."

**Staff-level framing:** Discuss the partial-migration failure mode as a structural risk of adopting reactive incrementally, and give a calibrated recommendation on reactive versus virtual threads for a given workload — naming backpressure/streaming as the concrete, remaining differentiator now that virtual threads cover much of reactive's scaling benefit for blocking code.

## Production Warning Signs

- A WebFlux service showing dramatically worse p99 latency under load than the blocking version it replaced — check for a leftover blocking JDBC call with no `subscribeOn(Schedulers.boundedElastic())`.
- `IllegalStateException: block()/blockFirst()/blockLast() are blocking, which is not supported in thread ...` — Reactor's own defensive check against blocking on a reactive scheduler thread.
- `publishOn` "not working" for a simple, synchronous source — check for operator fusion; add `.hide()` to confirm.

## Related

- `syllabus/02-java/concurrency/virtual-threads.md`
- `syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md`
- `syllabus/11-system-design/realtime-delivery-websocket-sse-and-long-polling.md`
