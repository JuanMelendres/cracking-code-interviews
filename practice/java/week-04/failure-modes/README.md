# Week 4 Java — Distributed Failure Mode Demos — runnable verification

Three real, measured simulations. Pure JDK — no external dependencies.

## Reproduce

```bash
cd practice/java/week-04/failure-modes
mkdir -p out
javac -d out src/*.java
java -cp out RetryStormDemo
java -cp out CacheStampedeDemo
java -cp out FencingTokenDemo
```

## 1. Retry storm — `RetryStormDemo.java`

A downstream service (fixed-size thread pool, capacity 4) is degraded (400ms per unit of work instead of its normal speed). 12 logical requests arrive in a burst, client timeout 700ms. Critically — and realistically — a client-side timeout does **not** cancel the work already submitted downstream; it keeps running and occupying a pool slot.

**Real output (last run):**

```
Downstream capacity=4, work=400ms, client timeout=700ms, 12 logical requests in a burst.

--- NO RETRY ---
Elapsed: 708ms, succeeded within SLA: 4/12, total work units submitted to downstream: 12

--- RETRY, NO BACKOFF (immediate resubmit) ---
Elapsed: 2114ms, succeeded within SLA: 4/12, total work units submitted to downstream: 28

--- RETRY, EXPONENTIAL BACKOFF + JITTER ---
Elapsed: 2606ms, succeeded within SLA: 12/12, total work units submitted to downstream: 24

=== Summary ===
Strategy                                    Succeeded Work units submitted    Amplification
No retry                                            4             12             1.0x
Retry, no backoff                                   4             28             2.3x
Retry, exponential backoff + jitter                 12             24             2.0x
```

**Reading this honestly:** retrying without backoff did not improve the success rate at all (still 4/12) — it just added 2.3x load to an already-struggling downstream for zero benefit, and took 3x longer wall-clock. This is "you added retries and made the outage worse," reproduced with real numbers. Backoff, by contrast, actually helped (12/12 succeeded) with *less* amplification than the no-backoff case, because fewer wasted immediate retries were fired while the pool was still saturated.

## 2. Cache stampede — `CacheStampedeDemo.java`

50 concurrent requests for the same just-expired cache key.

**Real output (last run):**

```
--- Naive cache-aside (no coordination) ---
Database load calls made: 50 (should be 1 with coordination)
Elapsed: 306ms

--- Single-flight (request coalescing) fix ---
Database load calls made: 1 (coordination working as intended)
Elapsed: 306ms
```

Both take the same wall-clock time (bounded by one database load's latency either way) — the difference invisible in latency and entirely visible in database load: 50x vs. 1x.

## 3. Fencing tokens — `FencingTokenDemo.java`

Node A acquires a lease (token 1), pauses (GC/network partition), its lease expires, Node B acquires a new lease (token 2) and writes correctly. Node A then "wakes up" and writes its stale data.

**Real output (last run):**

```
=== WITHOUT fencing tokens (split-brain corrupts data) ===
...
Final data: "stale-data-from-node-A"  <-- CORRUPTED by the stale node

=== WITH fencing tokens (stale write correctly rejected) ===
...
  REJECTED write with token 1 (a newer token 2 has already written) -- value would have been "stale-data-from-node-A"
Final data: "correct-data-from-node-B"  <-- correct
```
