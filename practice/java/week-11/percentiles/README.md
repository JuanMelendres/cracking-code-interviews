# Week 11 Java — Coordinated Omission — runnable verification

One real demo. No external dependencies.

## Setup and run

```bash
cd practice/java/week-11/percentiles
mkdir -p out
javac -d out src/CoordinatedOmissionDemo.java
java -cp out CoordinatedOmissionDemo
```

**Real observed output (last run):**

```
== closed-loop (naive): send next request only after the previous completes ==
closed-loop: p50=10ms  p90=10ms  p99=500ms  p99.9=500ms  max=500ms

== open-loop (correct): requests are scheduled every 50ms regardless of how long the previous one took ==
open-loop: p50=10ms  p90=380ms  p99=830ms  p99.9=1370ms  max=2110ms
```

**What this proves:** the identical simulated service (98% of requests at 10ms, 2% stalling at 500ms, same random seed so the exact same sequence of stalls occurs in both runs), measured two ways. The closed-loop (naive) load generator's p99 is exactly the stall duration (500ms) and nothing more — it never captures the queueing delay a stall causes for requests stuck behind it. The open-loop (correct) generator's p90 (380ms) and p99 (830ms) reveal that real cost, purely from correcting the measurement methodology, not from changing the service at all.
