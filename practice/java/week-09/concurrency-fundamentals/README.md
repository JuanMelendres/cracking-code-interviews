# Week 9 Java — Concurrency Fundamentals (JMM, volatile, thread states) — runnable verification

Two real demos. No external dependencies.

## Setup and run

```bash
cd practice/java/week-09/concurrency-fundamentals
mkdir -p out
javac -d out src/*.java
```

## 1. Visibility failure — `VisibilityDemo.java`

```bash
java -cp out VisibilityDemo
```

**Real observed output (last run, reproduced identically across 3 separate runs):**

```
== non-volatile flag: does the worker thread ever see the update? ==
worker STILL RUNNING 5002ms after the flag was set -- update never observed (this run)

== volatile flag: same test ==
worker stopped 0ms after the flag was set, having run 5848056485 iterations
```

**What this proves:** a plain (non-`volatile`) boolean field's update from another thread is genuinely, reliably never observed within a 5-second bounded wait — not a rare timing fluke, a real JIT optimization (hoisting the loop-invariant read) reproduced consistently. `volatile` fixes it completely, every run.

## 2. Corrected thread-state lifecycle — `ThreadStateDemo.java`

Errata correction: the source material invented a "Running" state and omitted `TIMED_WAITING`.

```bash
java -cp out ThreadStateDemo
```

**Real observed output (last run):**

```
Before start(): NEW
Inside monitor.wait() (no timeout): WAITING
After join() returns: TERMINATED

== TIMED_WAITING, the state the source material's diagram omitted ==
While inside Thread.sleep(2000): TIMED_WAITING
After it wakes and finishes: TERMINATED

Real Thread.State enum, for reference: [NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED]
```

**What this proves:** the real `Thread.State` enum, printed directly from a running JVM, has exactly six values including `TIMED_WAITING` — not the five-state model with an invented "Running" state from the source material. `BLOCKED` is demonstrated separately in `../deadlock-diagnostics/`, since it requires genuine lock contention.
