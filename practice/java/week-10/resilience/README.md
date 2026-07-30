# Week 10 Java — Resilience Patterns — runnable verification

Two real demos. No external dependencies.

## Setup and run

```bash
cd practice/java/week-10/resilience
mkdir -p out
javac -d out src/*.java
```

## 1. Circuit breaker — `CircuitBreakerDemo.java`

```bash
java -cp out CircuitBreakerDemo
```

**Real observed output (last run):**

```
== WITHOUT a circuit breaker: every call pays the full 200ms, even while the downstream is down ==
10 calls, 10 attempted (all of them), 4 succeeded, 2046ms total (== 10 x 200ms, every call pays full cost)

== WITH a circuit breaker (threshold=3, open for 500ms): fails fast once open ==
  [breaker] CLOSED -> OPEN (3 consecutive failures)
  [breaker] OPEN -> HALF_OPEN (cool-down elapsed, allowing one trial call)
  [breaker] HALF_OPEN -> CLOSED (trial call succeeded)
20 call attempts: 15 actually reached the downstream (200ms each), 5 rejected fast (~0ms), 12 succeeded, 3569ms total
```

**What this proves:** a real circuit breaker cycles through all three states (CLOSED, OPEN, HALF_OPEN) as a downstream genuinely fails then recovers, and measurably converts 5 of 20 calls from a 200ms cost each to ~0ms by failing fast while open.

## 2. Retry backoff + jitter — `RetryBackoffJitterDemo.java`

```bash
java -cp out RetryBackoffJitterDemo
```

**Real observed output (last run):**

```
== exponential backoff WITHOUT jitter: every failing client retries at the identical instant ==
attempt 1 (exponential cap=100ms): client delays = 100ms 100ms 100ms 100ms 100ms 
attempt 2 (exponential cap=200ms): client delays = 200ms 200ms 200ms 200ms 200ms 
attempt 3 (exponential cap=400ms): client delays = 400ms 400ms 400ms 400ms 400ms 
attempt 4 (exponential cap=800ms): client delays = 800ms 800ms 800ms 800ms 800ms 

== exponential backoff WITH full jitter: retries spread out ==
attempt 1 (exponential cap=100ms): client delays = 72ms 68ms 30ms 27ms 66ms 
attempt 2 (exponential cap=200ms): client delays = 180ms 73ms 55ms 92ms 156ms 
attempt 3 (exponential cap=400ms): client delays = 367ms 174ms 299ms 154ms 70ms 
attempt 4 (exponential cap=800ms): client delays = 475ms 167ms 660ms 137ms 469ms 
```

**What this proves:** without jitter, 5 independent clients retry at the EXACT same instant on every attempt (a real retry storm); with full jitter (seeded `Random(42)` for reproducibility), the same 5 clients' retry instants spread across the full backoff window instead.
