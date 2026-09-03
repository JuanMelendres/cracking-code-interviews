# API gateway, BFF, and edge concerns (T-911) — runnable verification

Real, executed Java 21 output backing
[`syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md`](../../../../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md)
(T-911). No framework, no mocked responses — a real API gateway
(`com.sun.net.httpserver.HttpServer` accepting requests, `java.net.http.HttpClient`
forwarding to real backends), real path-based routing, a real centralized
edge-concern check, and real concurrent fan-out for a BFF-style aggregation
endpoint. Follows the same plain-JDK, no-dependency pattern established in
[`practice/java/system-design/load-balancing-and-health-checking/`](../load-balancing-and-health-checking/README.md).

## Files

- `DownstreamService.java` — a real, minimal backend microservice: serves one
  JSON body at a configurable path, with a real, configurable processing
  delay and a real request counter.
- `ApiGateway.java` — the real gateway: path-based routing to real backends, a
  real API-key check enforced centrally before any backend is reached, and a
  real `/bff/dashboard` endpoint that fans out to two backends concurrently
  via `CompletableFuture` and combines their real responses into one.
- `RoutingDemo.java`, `EdgeConcernDemo.java`, `BffAggregationDemo.java` — the
  three demos below.

## Run

```bash
cd practice/java/system-design/api-gateway-bff-and-edge-concerns
mkdir -p out
javac -d out *.java
java -cp out RoutingDemo
java -cp out EdgeConcernDemo
java -cp out BffAggregationDemo
```

## Real observed output (last full run, Java 21)

### 1. `RoutingDemo` — real path-based routing to the correct backend

```
=== GET /orders through the gateway ===
Response: {"orders":[{"id":1,"item":"Widget"}]}
Real orders backend request count: 1 (expect 1)
Real users backend request count: 0 (expect 0 -- routed correctly)

=== GET /users through the gateway ===
Response: {"users":[{"id":7,"name":"Ada"}]}
Real orders backend request count: 1 (expect 1, unchanged)
Real users backend request count: 1 (expect 1)
```

Each real backend's own request counter proves the gateway dispatched to
exactly the right service for each path — not just that a response came back.

### 2. `EdgeConcernDemo` — a real, centralized edge concern

```
=== Request with NO API key ===
Real gateway status: 401 (expect 401)
Real body: {"error":"missing or invalid API key"}
Real orders backend request count: 0 (expect 0 -- the backend was NEVER reached)

=== Request WITH the correct API key ===
Real gateway status: 200 (expect 200)
Real orders backend request count: 1 (expect 1 -- now really forwarded)
```

The real backend's request count staying at 0 for the rejected request is the
actual proof point: the API-key check happens once, at the gateway, and a
failing request never reaches the backend at all — the whole justification
for centralizing a cross-cutting concern at the edge instead of duplicating it
into every service.

### 3. `BffAggregationDemo` — real concurrent fan-out vs. sequential direct calls

```
=== Client calling both backends directly, sequentially (2 round trips) ===
Real total client time: 357ms (expect ~300ms -- two sequential round trips)

=== Client calling the BFF endpoint ONCE (gateway fans out concurrently) ===
Real total client time: 159ms (expect ~150ms -- ONE client round trip, backends fanned out in parallel)
```

Both backends have a real, identical 150ms processing delay. Calling them
directly and sequentially costs a real ~300ms and two client round trips.
Calling the BFF endpoint once costs a real ~150ms — the gateway's two backend
calls run concurrently via `CompletableFuture`, and the client only ever makes
one round trip. This is the real, measured mechanism behind the BFF pattern's
actual benefit: fewer client round trips, and a response shaped for exactly
what that client needs, not a generic pass-through.

## Real discoveries made while building this pack

No bugs were hit this time — all three demos produced correct, expected
output on the first real run. Worth stating honestly rather than
manufacturing a discovery: this pack deliberately reused the exact
plain-JDK `com.sun.net.httpserver`/`java.net.http.HttpClient` pattern already
proven working in `practice/java/system-design/load-balancing-and-health-checking/`,
which is very likely why no new integration issues surfaced.
