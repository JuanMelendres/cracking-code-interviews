# Week 9 Java — GC Log Analysis — runnable verification

One real demo, producing a real GC log. No external dependencies.

## Setup and run

```bash
cd practice/java/week-09/gc
mkdir -p out
javac -d out src/AllocationStormDemo.java
java -Xmx64m "-Xlog:gc*:file=gc.log:time,level,tags" -cp out AllocationStormDemo
cat gc.log
```

**Real observed output (program stdout, last run):**

```
Allocated ~4902MB total, retained 400 objects
(retained.size() referenced here so the JIT can't dead-code-eliminate the retention list)
```

**Real captured GC log (`gc.log`, pause events, last run):**

```
[2026-07-29T21:59:05.263-0600][info][gc,start    ] GC(0) Pause Young (Normal) (G1 Evacuation Pause)
[2026-07-29T21:59:05.264-0600][info][gc          ] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 24M->1M(64M) 0.329ms
[2026-07-29T21:59:05.267-0600][info][gc,start    ] GC(1) Pause Young (Normal) (G1 Evacuation Pause)
[2026-07-29T21:59:05.267-0600][info][gc          ] GC(1) Pause Young (Normal) (G1 Evacuation Pause) 38M->1M(64M) 0.302ms
[2026-07-29T21:59:05.270-0600][info][gc,start    ] GC(2) Pause Young (Normal) (G1 Evacuation Pause)
[2026-07-29T21:59:05.270-0600][info][gc          ] GC(2) Pause Young (Normal) (G1 Evacuation Pause) 38M->1M(64M) 0.186ms
[2026-07-29T21:59:05.272-0600][info][gc,start    ] GC(3) Pause Young (Normal) (G1 Evacuation Pause)
[2026-07-29T21:59:05.273-0600][info][gc          ] GC(3) Pause Young (Normal) (G1 Evacuation Pause) 38M->6M(64M) 0.605ms
```

**What this proves:** allocating ~4.9GB of mostly short-lived objects (plus a smaller retained stream) into a genuinely constrained 64MB heap forces four real G1 young-generation collections, each sub-millisecond, with a rising post-collection occupancy trend (1M -> 1M -> 1M -> 6M) as more retained objects accumulate toward promotion — a real artifact to practice reading, not a synthesized example. `gc.log` is not committed (regenerate it via the command above); `.gitignore`'s `*.log` rule already covers it.
