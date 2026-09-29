# Production Troubleshooting — Real, Executed Demo

Backs [Production Troubleshooting Methodology](../../../../syllabus/13-observability/production-troubleshooting-methodology.md) (T-2442).

Two independent demos, both real and both measured on OpenJDK 21.0.12 (10 cores, macOS):

- **`demo.TroubleshootingApplication`** — a Spring Boot 3.5.16 app on embedded Tomcat 10.1.55 that returns HTTP 500 four different ways, three of which produce no diagnostic output at the default log level. Transcript: `silent-failures-transcript.txt`.
- **`demo.LoadProfileDemo`** — no Spring, no database. Shows why identical code is fast in development and slow in production, for two independent reasons. Transcript: `load-profile-transcript.txt`.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
```

## Demo 1 — "we get 500s and the logs show nothing"

```bash
java -cp "out:lib/*" demo.TroubleshootingApplication
# then, in another terminal:
curl -s -w " <- HTTP %{http_code}\n" http://localhost:8080/silent/swallowed
curl -s -w " <- HTTP %{http_code}\n" http://localhost:8080/silent/wrong-level
curl -s -w " <- HTTP %{http_code}\n" http://localhost:8080/silent/unlogged-advice
curl -s -w " <- HTTP %{http_code}\n" http://localhost:8080/visible/logged

# then re-run the app with the application package at DEBUG and repeat:
java -cp "out:lib/*" demo.TroubleshootingApplication --logging.level.demo=DEBUG
```

All four endpoints return **exactly** `{"error":"internal error"}` with HTTP 500. From outside the process they are indistinguishable. Inside, they fail for three genuinely different reasons:

| Endpoint | Cause | Is the evidence recoverable? |
|---|---|---|
| `/silent/swallowed` | `catch (Exception ignored) {}` | **No.** The stack trace was never written anywhere. Only a code change recovers it. |
| `/silent/wrong-level` | Logged at `DEBUG`; production runs at `INFO` | **Yes**, and cheaply — one config change, no deploy. |
| `/silent/unlogged-advice` | A catch-all `@ExceptionHandler` that standardises the response and logs nothing | **Yes**, but only by changing the advice. |
| `/visible/logged` | Control: logged at `ERROR` | Full stack trace present. |

**What the two runs prove together.** At the default level, three of the four failures produce no application log line at all — the only proof they happened is the access-log filter's own record. Re-running with `--logging.level.demo=DEBUG` recovered `/silent/wrong-level` and **only** that one; the other two stayed silent. That is what makes a log-level change a genuinely useful diagnostic *step* rather than a guess: it discriminates between the candidate causes instead of merely hoping for more output.

An honest detail visible in the transcript: every access-log line records `escaped_exception=none`, including the failing ones. None of these exceptions escaped the `DispatcherServlet` — each was converted into a 500 response inside it. A filter can only see what escapes it, which is exactly why the filter records the **status** as well.

The `AccessLogFilter` is the fix that turns "nothing in the logs" into a bounded problem: it records every request and its status unconditionally, so a 500 is at minimum *visible* even when the code that produced it logged nothing.

## Demo 2 — "fast in dev, slow in production"

```bash
java -cp "out:lib/*" demo.LoadProfileDemo
```

Nothing about the code under test changes between the dev and production runs. Only the data volume and the concurrency do.

**Measured baseline:** one uncontended round trip = **256 microseconds** on this machine.

### Scenario A — identical code, different data volume

| rows | naive (N+1) | batched | ratio |
|---|---|---|---|
| 10 | 2,809 us | 510 us | 6x |
| 100 | 25,798 us | 512 us | 50x |
| 1,000 | 255,640 us | 514 us | 497x |
| 5,000 | **1,283,121 us** | 507 us | **2531x** |

At 10 rows — a normal dev seed — the naive version is 2.8 ms and the batched version is 0.5 ms. Both are "instant"; no developer would notice, and no test would fail. At 5,000 rows the same code takes **1.28 seconds**. The naive version is linear in row count and the batched version is flat, so the gap is not a constant factor that a faster machine fixes — it is a different curve, and the dev dataset sits at the only point on it where the two look alike.

### Scenario B — identical code and data, different concurrency

Connection pool size 10; each request makes 5 round trips.

| concurrency | p50 | p95 | p99 | throughput |
|---|---|---|---|---|
| 1 | 1 ms | 1 ms | 1 ms | 668 req/s |
| 5 | 1 ms | 1 ms | 1 ms | 3,857 req/s |
| 10 | 1 ms | 1 ms | 1 ms | 7,623 req/s |
| 50 | 1 ms | 23 ms | **36 ms** | 7,345 req/s |
| 200 | 1 ms | 140 ms | **152 ms** | 7,538 req/s |

Two findings, both measured:

1. **Throughput stops climbing at the pool size.** It plateaus around 7,600 req/s from concurrency 10 onwards and does not improve at 200. The pool, not the code, is the ceiling — so "make the code faster" is the wrong fix, and adding application instances behind the same pool would not help either.
2. **The median never moves.** p50 is 1 ms at *every* concurrency level, including 200, while p99 goes from 1 ms to 152 ms. The extra time is queueing, distributed unevenly because the semaphore is unfair and some waiters are overtaken repeatedly. A dashboard showing average or median latency reports this system as perfectly healthy while one request in a hundred takes 152 times longer.

Finding 2 is the reason percentiles exist, and it connects directly to [Percentiles, Tail Latency, and Coordinated Omission](../../../../syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md).
