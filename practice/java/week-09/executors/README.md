# Week 9 Java — Executor & Thread Pool Sizing — runnable verification

One real demo, two scenarios. No external dependencies.

## Setup and run

```bash
cd practice/java/week-09/executors
mkdir -p out
javac -d out src/*.java
java -cp out ExecutorSizingDemo
```

**Real observed output (last run):**

```
== newFixedThreadPool(2): backed by an UNBOUNDED LinkedBlockingQueue ==
200ms after submitting 500 tasks to a 2-thread pool: queue size=496, completed=2, active=2 (every unstarted task sits in memory in the unbounded queue)
after full drain: completed=500 (all 500 eventually ran -- unbounded means no rejection, just unbounded memory growth under sustained overload)

== ThreadPoolExecutor with a BOUNDED queue + AbortPolicy: backpressure, not silent growth ==
submitted 20 tasks to a 2-thread pool with a 5-slot bounded queue: accepted=7 rejected=13
(2 running + 5 queued = 7 can be accepted immediately; the rest are rejected loudly instead of silently piling up)
```

**What this proves:** `Executors.newFixedThreadPool()`'s default unbounded queue really does accumulate 496 of 500 tasks in memory 200ms after submission with zero rejection or backpressure signal. A `ThreadPoolExecutor` built explicitly with a bounded queue and `AbortPolicy` accepts exactly `corePoolSize + queueCapacity` (2+5=7) and rejects the rest immediately — the arithmetic matches exactly.
