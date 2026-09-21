# Chaos Engineering: Fault Injection and Resilience Verification — Real Demo

Backs [`syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md`](../../../../syllabus/13-observability/chaos-engineering-fault-injection-and-resilience-verification.md) (T-2423).

Pure JDK, no dependencies. No mocked clock -- every phase uses real
`System.currentTimeMillis()` and real `Thread.sleep()` between requests
at ~10 req/sec.

## Run it

```bash
mkdir -p out
javac -d out src/*.java
java -cp out ChaosExperimentDemo
```

Real captured output in [`output-transcript.txt`](output-transcript.txt),
re-run twice to confirm reliability -- only real-clock timing jitter (a
few ms) differs between runs; the request counts are identical.

## What it proves

Follows the real structure of a chaos experiment (per the public
"Principles of Chaos Engineering"): establish a steady-state hypothesis,
inject a real variable simulating a real-world event with a minimized
blast radius, verify the hypothesis breaks and that monitoring actually
detects it, then roll back and verify recovery.

1. **Phase 1, steady state** — `OrderService` handles 20 real requests
   with chaos inactive. Real burn-rate monitoring confirms `paging =
   false`: the steady-state hypothesis holds.
2. **Phase 2, the experiment** — a real fault is injected against
   `OrderService`'s downstream payment call, scoped to only the 25%
   canary cohort (every 4th request, deterministically) — a real,
   minimized blast radius, not 100% of traffic. Real multi-window
   burn-rate monitoring (the same technique documented in
   [Metric Cardinality and Alert Fatigue](../../../../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md),
   T-2409: both a short and a long window must independently exceed a
   14.4 burn-rate threshold) detects the injected fault in real time —
   real captured output: alert fired 12 real requests / ~1.1s after
   injection began.
3. **Phase 3, rollback** — the fault is removed; real monitoring
   confirms the alert clears once the windows fill with healthy
   requests again — real captured output: cleared ~310ms after
   rollback.

## Honest limitations

- The short/long detection windows are compressed to 500ms/2000ms so
  this demo runs in a few real seconds. The 1:4 window ratio and the
  14.4 burn-rate threshold are the same real technique documented in
  `metric-cardinality-and-alert-fatigue.md` (real Google SRE Workbook
  multi-window, multi-burn-rate alerting) — only the absolute window
  sizes are scaled down, not the mechanism.
- The 25% canary cohort is chosen deterministically (every 4th request)
  rather than by a real service-mesh fault-injection rule or feature
  flag, so this demo's result is exactly reproducible run to run — real
  chaos tooling (e.g. a service mesh, Gremlin, AWS Fault Injection
  Service) selects the cohort by request/user attribute instead.
- This demo runs against a single in-process simulated service, not a
  real distributed system or a real production environment — the
  principle "run experiments in production" (per the same source
  material) is discussed in the canonical chapter's Trade-offs section,
  not claimed here.
