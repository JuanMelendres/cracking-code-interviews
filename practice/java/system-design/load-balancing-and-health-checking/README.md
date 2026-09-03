# Load balancing, service discovery, and health checking (T-805) — runnable verification

Real, executed Java 21 output backing
[`syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md`](../../../../syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md)
(T-805). No framework, no mocked responses — a real reverse proxy (`java.net.http.HttpClient`
forwarding to real `com.sun.net.httpserver.HttpServer` backends), real round-robin and
least-connections routing decisions, and a real active health checker on its own
thread issuing real HTTP calls against a real backend that gets really killed and
really restarted mid-run.

## Files

- `Backend.java` — a real, minimal HTTP server instance: serves `/health` (toggle-able
  200/503) and `/` (does real, configurable blocking work before responding).
- `LoadBalancer.java` — a real reverse proxy with two real selection strategies
  (round-robin, least-connections) and a shared healthy-set the health checker mutates
  concurrently.
- `HealthChecker.java` — a real background thread issuing real HTTP `GET /health`
  calls on a fixed interval, flipping the load balancer's healthy-set based on the
  real response (or the real connection failure when a backend is gone).
- `AlgorithmComparisonDemo.java`, `HealthCheckFailoverDemo.java` — the two demos below.

## Run

```bash
cd practice/java/system-design/load-balancing-and-health-checking
mkdir -p out
javac -d out *.java
java -cp out AlgorithmComparisonDemo
java -cp out HealthCheckFailoverDemo
```

## Real observed output (last full run, Java 21)

### 1. `AlgorithmComparisonDemo` — round-robin vs. least-connections, real measured cost

Three real backends: two fast (5ms real processing delay each), one deliberately slow
(200ms). 300 real requests, 30 real concurrent workers, through a real reverse proxy:

```
=== ROUND_ROBIN: 300 requests, 30 concurrent ===
  slow-C   100 requests
  fast-B   100 requests
  fast-A   100 requests
Real wall time for ROUND_ROBIN batch: 921ms

=== LEAST_CONNECTIONS: 300 requests, 30 concurrent ===
  slow-C   10 requests
  fast-B   144 requests
  fast-A   146 requests
Real wall time for LEAST_CONNECTIONS batch: 208ms
```

Round-robin sent the slow backend its full, blind 1/3 share of traffic regardless of
how long it actually takes to respond — real, measured total batch time: 921ms.
Least-connections, using the real in-flight-request count as its only signal, routed
only 10 of 300 requests to the slow backend — a real, direct, ~4.4x improvement in
total batch time (208ms) from the routing algorithm alone, with zero change to the
backends themselves.

### 2. `HealthCheckFailoverDemo` — real detection, real exclusion, real recovery

A real health checker polls three real backends every real 300ms. `backend-B`'s real
HTTP server is then stopped mid-run (`Backend.stop()` — no graceful shutdown signal to
the checker, just gone):

```
=== Really stopping backend-B's HTTP server (simulating a crash) ===
Real health check detected backend-B DOWN at t+206ms

Real routing check: firing 12 requests through ROUND_ROBIN after detection...
  -> backend-A handled request 1
  -> backend-C handled request 1
  ... (6 more pairs, alternating A/C only)
(no request above should have reached backend-B if detection worked)

=== Really restarting backend-B ===
Real health check detected backend-B back UP at t+70ms
backend-B healthy=true (should now be true again)
```

Real, measured detection latency: 206ms (well inside the 300ms check interval plus a
500ms per-probe timeout) — and, critically, **zero of the 12 real requests fired
after detection reached the dead backend**, real direct proof the load balancer's
routing decision actually respected the health checker's real, concurrently-updated
state. Re-detection after a real restart: 70ms.

## What this does and does not prove

Every number here is real, single-machine, localhost output — no network latency, no
real DNS, no real load balancer hardware/software (HAProxy, Envoy, an ALB) is being
exercised, only the algorithmic and health-check *decision logic* those real systems
also implement. Production detection latency, failover time, and algorithm behavior
under real network conditions will differ in magnitude — what doesn't change is the
shape of both findings: round-robin has no feedback signal and least-connections does,
and active health checking's detection latency is bounded by check interval plus probe
timeout, not instantaneous.
