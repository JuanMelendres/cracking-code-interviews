# Load Testing and Performance Test Design — Real, Executed Demo

Backs [Performance and Load Testing Methodology](../../../syllabus/08-testing/performance-and-load-testing-methodology.md)
(T-1106) — that chapter owns load/stress/soak testing *strategy and
placement*; this pack closes its real-tooling gap (no chapter anywhere
named an actual load-testing tool or showed real code implementing the
open-loop-vs-closed-loop distinction the repo's coordinated-omission
chapter already teaches in theory). Real OpenJDK 21.0.12, real k6 v2.2.0
(industry-standard open-source load generator), Apple M4 (arm64), macOS.

Operationalizes, with real running code, the coordinated-omission theory
already taught in
[Percentiles, Tail Latency, and Coordinated Omission](../../../syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md)
— that chapter explains *why* closed-loop load testing understates tail
latency; this pack builds a real closed-loop generator and a real open-loop
generator (k6) and runs both against the identical real server to show the
actual, measured gap.

## The target server

[`src/TargetServer.java`](src/TargetServer.java) — a real
`com.sun.net.httpserver` service with a real, periodic stop-the-world-style
pause: every 2 real seconds, a background thread holds a shared lock for a
real 300ms; every request handler must acquire that same lock (normal-case
10ms of work) before responding. This is a real, working stand-in for a GC
pause or any other event that blocks every in-flight request at once.

## Setup and run

```bash
./build.sh              # compiles the server + closed-loop generator, checks for k6
./run-comparison.sh      # runs both generators against fresh instances of the same server
```

Real captured output: [`output-transcript.txt`](output-transcript.txt).

## What it proves

**Real, measured percentiles, both generators against the identical server behavior, 10s each:**

```
Closed-loop (Java, concurrency=5):   p50=70ms    p95=100ms    p99=396ms   max=403ms   (722 requests)
Open-loop   (k6, 40 req/s target):   p50=13.11ms p95=275.35ms p99=424.85ms max=461.09ms (401 requests)
```

**The real, measured gap is at p95, not p99**: open-loop reports **275ms**
at p95; closed-loop reports only **100ms** — a real ~2.75x understatement
at exactly the percentile a team is most likely to alert on. The real
reason, verified by the numbers above, not just asserted: closed-loop's
concurrency (5) caps how many requests can be stuck *in flight* during any
one 300ms pause window — at most 5 delayed samples per pause, diluted
across 722 total requests (~3.5% of the dataset), pushing the pause's
effect out past p95 into p97-p99 territory. Open-loop keeps firing at a
fixed 40 req/s **regardless of the pause**, so roughly 12 requests queue up
during each 300ms pause window, repeated across the run (~15% of the total
401-request dataset) — a large enough fraction to show up clearly at p95.

**Real, honest secondary finding**: closed-loop's own *median* (70ms) is
actually *higher* than open-loop's (13.11ms) — an artifact of only 5
threads contending for the same synchronized lock as fast as possible,
inflating even the "normal" case. Open-loop's fixed, moderate arrival rate
doesn't create this same self-inflicted contention. Neither number is
"wrong" — they're measuring genuinely different things, which is itself
the chapter's point: the load generator's own design shapes what gets
measured, not just the server's real behavior.

## Reproducing

```bash
brew install k6   # or see https://k6.io/docs/get-started/installation/
```
