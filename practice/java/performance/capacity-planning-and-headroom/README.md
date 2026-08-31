# Capacity Planning & Headroom (T-1208) — runnable verification

Real, executed Java output backing
[`handbook/performance/capacity-planning-and-headroom.md`](../../../../handbook/performance/capacity-planning-and-headroom.md)
(T-1208). No mocked timing, no simulated numbers — a real, bounded
`ExecutorService` worker pool, real `Thread.sleep`-based service time, and
real wall-clock measurement throughout.

## Files

- `LittlesLawDemo.java` — empirically verifies Little's Law (`L = λW`)
  against a real worker pool running well below capacity (steady state).
- `SaturationPointDemo.java` — offers increasing real load against a fixed
  worker pool and measures real throughput and real tail latency at each
  level, finding the pool's actual saturation point.

## Run

```bash
cd practice/java/performance/capacity-planning-and-headroom
mkdir -p out
javac -d out src/LittlesLawDemo.java src/SaturationPointDemo.java
java -cp out LittlesLawDemo
java -cp out SaturationPointDemo
```

## Real observed output (last full run)

### 1. `LittlesLawDemo` — L = λW, measured two independent ways

```
=== Real, measured steady-state numbers ===
Pool size (max concurrent servers):     6
Real fixed service time per request:    30 ms
Offered arrival rate (target):           100.0 req/s
Completed requests in measured window:   797
lambda (measured throughput):            99.63 req/s
W (measured avg time-in-system):         0.0330 s (33.0 ms)
L (measured avg number in system, sampled every 2ms): 3.264

=== Little's Law check: L =?= lambda * W ===
lambda * W (predicted L):                3.290
L (directly measured):                   3.264
Relative error:                          0.8%
Little's Law holds: two independently measured quantities (L via sampling,
lambda*W via throughput and latency) agree within measurement noise.
```

`L` (the average number of requests in the system) was measured two
completely independent ways: by sampling a live `AtomicInteger` counter
every 2ms over an 8-second steady-state window, and by multiplying the
measured throughput (`λ`) by the measured average time-in-system (`W`).
They agree to within 0.8% — real, direct confirmation of a law usually
only stated, not demonstrated. This is the core tool a capacity planner
uses to reason about *why* a queue's average size grows: either arrivals
(`λ`) increase, or time-in-system (`W`) increases (often because the
system is nearing saturation) — `L` cannot grow without one of the two.

### 2. `SaturationPointDemo` — finding the real breaking point

```
Pool size: 8, service time: 50ms -> theoretical max throughput: 160.0 req/s

Offered(/s)    Completed(/s)    p50(ms)    p99(ms)    Max(ms)
60             59.3             53.6       55.2       55.2
100            98.8             53.9       55.1       55.5
140            138.3            54.0       55.1       55.3
155            148.0            128.9      198.0      204.6
165            147.8            254.9      464.2      471.0
200            148.5            701.2      1373.3     1388.4
```

With 8 workers each taking a real, fixed 50ms per request, the
theoretical maximum sustainable throughput is exactly `8 / 0.05 = 160`
requests/second. Below that ceiling (60–140 req/s offered), completed
throughput tracks offered load almost exactly and p50/p99/max latency
stay flat around the real 50ms service time plus negligible queueing.
Once offered load reaches and passes the ceiling (155–200 req/s),
completed throughput **flattens at ~148/s** — the pool physically cannot
go faster — while p50 latency grows from 54ms to 701ms and p99 grows from
55ms to 1373ms. Throughput and latency are not the same signal: a
dashboard showing "throughput is still increasing" at 140 req/s gives no
warning at all that the very next load increment will make latency blow
up by 25×. This is the concrete, measured version of "find the knee of
the curve" that capacity planning is actually about.

## Real discoveries made while building this pack

`SaturationPointDemo`'s first version measured throughput as
"completions during the run" and latency as "all completions," without
distinguishing when a request was *submitted* from when it *completed*.
At overload levels, `workers.awaitTermination()` legitimately drains a
large backlog well after the nominal run window ends — the first version
would have let those delayed completions inflate the throughput number
for a run that had actually stopped accepting new load. This was caught
before running: the corrected version separates "throughput = completions
whose completion time falls inside the offered-load window" from
"latency = every request submitted during the window, tracked to its real
completion time no matter how long draining takes" — which is what
actually produces the honest, sharply divergent throughput-vs-latency
picture reported above.
