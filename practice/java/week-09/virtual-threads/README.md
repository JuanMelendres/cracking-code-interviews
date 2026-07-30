# Week 9 Java — Virtual Threads — runnable verification

Two real demos. No external dependencies. Requires JDK 21+ (virtual threads are stable/final, no preview flags needed).

## Setup and run

```bash
cd practice/java/week-09/virtual-threads
mkdir -p out
javac -d out src/*.java
```

## 1. Scale: platform threads vs virtual threads — `VirtualThreadScaleDemo.java`

```bash
java -cp out VirtualThreadScaleDemo
```

**Real observed output (last run):**

```
== 200 platform threads, 5000 blocking 50ms tasks ==
platform pool (200 threads): 5000 tasks completed in 1347ms (theoretical minimum if fully parallel: 50ms)

== virtual-thread-per-task executor, same 5000 blocking 50ms tasks ==
virtual threads (one per task): 5000 tasks completed in 75ms (theoretical minimum if fully parallel: 50ms)
```

**What this proves:** identical IO-bound workload (5,000 tasks, each blocking 50ms), 18x faster wall-clock time under virtual threads versus a 200-thread platform pool — the achievable concurrency is no longer capped by platform-thread memory cost.

## 2. Pinning — `VirtualThreadPinningDemo.java`

**Must be run with the carrier pool forced down**, otherwise the default carrier count (usually = CPU core count) makes the pinning effect too small to see clearly:

```bash
java -Djdk.virtualThreadScheduler.parallelism=2 -cp out VirtualThreadPinningDemo
```

**Real observed output (last run):**

```
carrier parallelism = 2

== blocking INSIDE synchronized -- pins the carrier thread ==
20 tasks x 200ms blocking each, synchronized (pins): 2044ms wall time (unpinned lower bound with a small carrier pool is roughly (tasks/carriers)*blockMs)

== blocking INSIDE a ReentrantLock -- does NOT pin ==
20 tasks x 200ms blocking each, ReentrantLock (no pin): 206ms wall time (unpinned lower bound with a small carrier pool is roughly (tasks/carriers)*blockMs)
```

**What this proves:** with 2 carriers and 20 tasks each blocking 200ms, blocking inside `synchronized` measures 2044ms — matching `(20/2)*200ms = 2000ms`, i.e. effectively fully serialized onto the 2 carriers, exactly as if virtual threads weren't involved at all. The identical workload under `ReentrantLock` instead measures 206ms, close to the 200ms unpinned lower bound. Each task locks its own independent object, so this isolates the pinning effect from ordinary lock contention.
