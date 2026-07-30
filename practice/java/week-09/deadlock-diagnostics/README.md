# Week 9 Java — Deadlock & Race Condition Diagnostics — runnable verification

Two real demos. No external dependencies.

## Setup and run

```bash
cd practice/java/week-09/deadlock-diagnostics
mkdir -p out
javac -d out src/*.java
```

## 1. A real deadlock, detected — `DeadlockDemo.java`

```bash
java -cp out DeadlockDemo
```

**Real observed output (last run):**

```
== states while deadlocked ==
thread-1-A-then-B: BLOCKED
thread-2-B-then-A: BLOCKED

== ThreadMXBean.findDeadlockedThreads() -- real detection, not a guess ==
DEADLOCKED: thread-1-A-then-B is BLOCKED, waiting on java.lang.Object@6acbcfc0 held by thread-2-B-then-A
DEADLOCKED: thread-2-B-then-A is BLOCKED, waiting on java.lang.Object@4f3f5b24 held by thread-1-A-then-B
```

**What this proves:** two threads acquiring the same two locks in opposite order genuinely deadlock (both `BLOCKED` forever), and `ThreadMXBean.findDeadlockedThreads()` — the real mechanism `jstack` uses — detects the exact cycle, naming which thread waits on which lock held by whom. The process self-terminates via `System.exit(0)` rather than actually hanging.

## 2. A race condition, measured — `RaceConditionDemo.java`

```bash
java -cp out RaceConditionDemo
```

**Real observed output (last run):**

```
== plain int, unsynchronized ++ ==
expected=1000000 actual=161906 lost=838094

== AtomicInteger.incrementAndGet() ==
expected=1000000 actual=1000000 lost=0
```

**What this proves:** 10 threads each incrementing a shared plain `int` 100,000 times lose 83.8% of updates to real, measured interleaving — not a rare edge case. `AtomicInteger` loses zero updates under identical load.
