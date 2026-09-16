# Microservices Patterns with Spring Boot — Real, Executed Demos

Backs [Microservices Patterns with Spring Boot](../../../../syllabus/05-spring/microservices-patterns-with-spring-boot.md)
(T-519). Real Spring Framework 6.1.14 + Spring Boot 3.3.5 + a real
Resilience4j 2.2.0 `CircuitBreaker`, plain jars fetched directly from
Maven Central, no Maven/Gradle install. Follows the same
`fetch-deps.sh` + `junit-platform-console-standalone` pattern established
in [Spring Boot Actuator, Health, and Observability Hooks](../spring-actuator-health-and-observability-hooks/README.md).

## What this proves

A real "order-service" (`OrderService`, a genuine `@Service` bean wired
through a real `GenericApplicationContext`) calls a real "payment-service"
— stood in by `FlakyPaymentServer`, a real `com.sun.net.httpserver.HttpServer`
bound to `localhost` on a random port, not a mock — through **Spring's own
declarative HTTP client** (`@HttpExchange` + `HttpServiceProxyFactory`,
built into `spring-web` since Spring Framework 6.0, no Spring Cloud
OpenFeign dependency needed), with the call wrapped in a **real
Resilience4j `CircuitBreaker`**.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -parameters -cp "lib/*" -d out src/demo/*.java
```

## Demo 1 — `CircuitBreakerFailureTrippingTest`: the full state machine, driven by real failures

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.CircuitBreakerFailureTrippingTest
```

Real output ([full capture](failure-tripping-output.txt)):

```
--- Step 1: healthy downstream ---
Result: ChargeResult[success=true, status=CHARGED, fallbackReason=null]
--- Step 2: downstream starts failing, drive 5 real failed calls ---
Call 1: ChargeResult[success=false, status=null, fallbackReason=payment call failed: InternalServerError] | breaker state=CLOSED
Call 2: ChargeResult[success=false, status=null, fallbackReason=payment call failed: InternalServerError] | breaker state=CLOSED
Call 3: ChargeResult[success=false, status=null, fallbackReason=payment call failed: InternalServerError] | breaker state=CLOSED
Call 4: ChargeResult[success=false, status=null, fallbackReason=payment call failed: InternalServerError] | breaker state=OPEN
Call 5: ChargeResult[success=false, status=null, fallbackReason=circuit-open: payment service unavailable, try again shortly] | breaker state=OPEN
--- Step 3: breaker OPEN -- next call must fail fast, no network call ---
Result: ChargeResult[success=false, status=null, fallbackReason=circuit-open: payment service unavailable, try again shortly]
--- Step 4: wait out waitDurationInOpenState, downstream recovers ---
Trial call 1: ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=HALF_OPEN
Trial call 2: ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
--- Final state sequence confirmed: CLOSED -> OPEN -> HALF_OPEN -> CLOSED ---
```

Notice the breaker opens on the **4th** failing call, not the 5th, even
though `minimumNumberOfCalls(5)` and `slidingWindowSize(5)` are both set
to 5. This is real, not an off-by-one bug: the sliding window counts
*every* call recorded on the breaker, including the one healthy call from
Step 1. By the 4th failing call, the window holds 5 calls total (1
success + 4 failures) — 80% failure rate, already past the 50% threshold.
A breaker's window does not reset or exclude prior successes just because
a failure streak starts; it is a genuine rolling window over all calls.

The call made while `OPEN` (Step 3) is asserted against `server.requestCount()`
staying flat — proof the rejection genuinely happens in the `CircuitBreaker`
before any network call is attempted, via a real `CallNotPermittedException`,
not merely a fast-failing HTTP call.

## Demo 2 — `CircuitBreakerSlowCallGotchaTest`: the real discovery this pack set out to find

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.CircuitBreakerSlowCallGotchaTest
```

Real output ([full capture](slow-call-gotcha-output.txt)):

```
--- Default config: 5 real slow-but-successful calls (300ms each) ---
Call 1 (364ms): ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
Call 2 (306ms): ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
Call 3 (305ms): ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
Call 4 (306ms): ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
Call 5 (309ms): ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
Default-config breaker final state: CLOSED
--- Slow-call-aware config: same real slow server, slowCallDurationThreshold=200ms ---
Call 1: ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
Call 2: ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
Call 3: ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
Call 4: ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=CLOSED
Call 5: ChargeResult[success=true, status=CHARGED, fallbackReason=null] | breaker state=OPEN
Slow-aware breaker final state: OPEN
```

Two `CircuitBreaker`s, two identical `CircuitBreakerConfig`s except one
extra pair of settings, both driven against the exact same real, slow
(300ms) downstream:

- **Default config** (`failureRateThreshold` only): every one of the 5
  calls *succeeds* — the server really does return `200 CHARGED`, just
  slowly. The breaker has nothing to count as a failure. It stays `CLOSED`
  forever, no matter how slow the downstream gets.
- **Slow-call-aware config** (`slowCallDurationThreshold(200ms)` +
  `slowCallRateThreshold(50)` added): the *same* 300ms responses now
  count against the slow-call rate. The breaker opens on call 5.

This is the real gotcha: a circuit breaker's default configuration
protects you from a downstream that **errors**, not one that **hangs**.
A slow-but-technically-successful dependency can starve thread pools,
exhaust connection pools, and degrade every caller's latency indefinitely
while a naively-configured `CircuitBreaker` reports everything healthy.
`slowCallDurationThreshold`/`slowCallRateThreshold` are not optional
extras for a production configuration — without them, "the circuit
breaker will protect us" is only half true.

## Real discoveries made while building this pack

1. **The slow-call gotcha above** — the pack's actual point, verified
   directly rather than asserted from documentation: built the default
   config first, watched it stay `CLOSED` against a demonstrably slow
   downstream, then added the two slow-call settings and watched the
   identical scenario open on the fifth call.
2. **`RestClient.builder()` needs `micrometer-observation`/`micrometer-commons`
   on the classpath even when no metrics are configured** — a real
   `NoClassDefFoundError: io/micrometer/observation/ObservationRegistry`
   on the first run, from `DefaultRestClientBuilder`'s constructor
   referencing `ObservationRegistry` unconditionally. Same two jars
   [T-509](../spring-webflux-and-reactive-programming/README.md) and
   [T-516](../spring-actuator-health-and-observability-hooks/README.md)
   already needed for unrelated reasons — fetched proactively in this
   pack's own `fetch-deps.sh` from the start once found.
3. **The sliding-window-includes-prior-successes behavior in Demo 1** —
   not anticipated when the test was written with `minimumNumberOfCalls(5)`
   expecting the breaker to open on the 5th *failing* call; the real
   output showed it opening on the 4th, which led directly to the
   explanation now in Demo 1's own writeup above.
