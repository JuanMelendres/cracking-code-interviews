# Spring Boot Actuator, Health, and Observability Hooks — Real, Executed Demos

Backs [Spring Boot Actuator, Health, and Observability Hooks](../../../../handbook/spring/spring-actuator-health-and-observability-hooks.md)
(T-516). Real Spring Boot 3.3.5 + Spring Boot Actuator + Micrometer output,
plain jars fetched directly from Maven Central, no Maven/Gradle install, run
with the `junit-platform-console-standalone` shaded jar. Follows the same
Spring Boot Test pattern established in
[Spring Testing: Slices and Context Caching](../spring-testing-slices-and-context-caching/README.md)
(real package from the start, `-parameters` compiled in from the start).

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -parameters -cp "lib/*" -d out src/demo/*.java
```

## Demo 1 — `ActuatorHealthAggregationTest`: a custom `HealthIndicator` really drives overall health

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.ActuatorHealthAggregationTest
```

Real output:

```
Real /actuator/health body (downstream UP):
{"status":"UP","components":{"diskSpace":{...},"downstream":{"status":"UP","details":{"latencyMs":12}},"ping":{"status":"UP"}}}

Real /actuator/health body after flipping downstream DOWN:
{"status":"DOWN","components":{"diskSpace":{...},"downstream":{"status":"DOWN","details":{"reason":"downstream dependency unreachable"}},"ping":{"status":"UP"}}}
```

`CustomHealthIndicator` is a plain `@Component("downstreamHealthIndicator")`
implementing `HealthIndicator` — Boot derives the component key (`downstream`)
from the bean name by stripping the `HealthIndicator` suffix. Flipping the real
`DownstreamDependency`'s availability flag flips the real, aggregate
`/actuator/health` status from `UP` to `DOWN` — proof that Actuator genuinely
aggregates every registered `HealthIndicator`'s status into one overall result,
not just displaying them independently. `management.endpoint.health.show-details=always`
is required for this test — by default, `/actuator/health` only reports the
aggregate status with no component breakdown.

## Demo 2 — `ActuatorDefaultExposureSecurityTest`: secure by default

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.ActuatorDefaultExposureSecurityTest
```

Real result: all 3 tests pass. With **no** `management.endpoints.web.exposure.include`
configured at all, `/actuator/health` returns `200`, while both `/actuator/beans`
(a built-in endpoint) and `/actuator/greetingStats` (this pack's own custom
endpoint) return real `404`s. Boot only exposes `health` and `info` over HTTP
by default — every other endpoint, built-in or custom, must be explicitly
opted into `management.endpoints.web.exposure.include`.

## Demo 3 — `ActuatorCustomEndpointAndMetricsTest`: a custom `@Endpoint` and a real Micrometer counter

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.ActuatorCustomEndpointAndMetricsTest
```

Real output:

```
Real /actuator/greetingStats body: {"realGreetingsServed":2.0}
Real /actuator/metrics/greeting.requests body: {"name":"greeting.requests","description":"Real count of greetings served","measurements":[{"statistic":"COUNT","value":2.0}],"availableTags":[]}
```

`GreetingStatsEndpoint` is a real `@Endpoint(id = "greetingStats")` with a
`@ReadOperation` — not a Spring MVC controller — exposed at
`/actuator/greetingStats` once explicitly included in
`management.endpoints.web.exposure.include`. `GreetingCounterService` registers
a real Micrometer `Counter` against the injected `MeterRegistry`; calling
`greet(...)` twice really increments it, and that same real value is visible
both through the custom endpoint and through Actuator's built-in
`/actuator/metrics/{name}` endpoint — proof that ordinary application-level
Micrometer instrumentation and Actuator's HTTP surface are the same underlying
registry, not two separate systems.

The test computes a live "before" reading rather than assuming the counter
starts at 0, so it stays correct regardless of test-class execution order or
shared, cached-context state — a deliberate application of the context-caching
lesson from [Spring Testing: Slices and Context Caching](../spring-testing-slices-and-context-caching/README.md).

## Demo 4 — `ReadinessLivenessTest`: real, programmatic control over Kubernetes probes

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.ReadinessLivenessTest
```

Real output:

```
Real readiness before: {"status":"UP"}
Real readiness after REFUSING_TRAFFIC: {"status":"OUT_OF_SERVICE"}
```

`/actuator/health/readiness` and `/actuator/health/liveness` are the exact
paths this repository's own
[`practice/k8s/week-15/deployment-with-probes-and-limits.yaml`](../../../k8s/week-15/deployment-with-probes-and-limits.yaml)
already configures as real Kubernetes readiness/liveness probes.
`management.endpoint.health.probes.enabled=true` is required outside a real
Kubernetes environment (Boot auto-detects and enables it there itself).
Publishing a real `AvailabilityChangeEvent` with `ReadinessState.REFUSING_TRAFFIC`
— the exact real signal Boot's own graceful-shutdown machinery publishes
internally — flips the real readiness probe's response from `UP` to
`OUT_OF_SERVICE`, proving this is genuine, programmatic control over what a
real Kubernetes readiness probe would observe, not a static, hardcoded value.

## Real discoveries made while building this pack

No bugs were hit this time — all four test classes passed on the first real
run. Worth stating honestly rather than manufacturing a discovery: this pack
deliberately applied every lesson already learned building the two prior
Spring Boot Test packs in this domain from the start — a real package from the
very first file (not the default package, per
[T-517's discovery](../spring-testing-slices-and-context-caching/README.md#real-discoveries-made-while-building-this-pack)),
`-parameters` compiled in from the first `javac` invocation (per
[T-514](../spring-cache-abstraction-and-pitfalls/README.md#real-discoveries-made-while-building-this-pack)
and [T-517](../spring-testing-slices-and-context-caching/README.md#real-discoveries-made-while-building-this-pack)),
and `micrometer-observation`/`micrometer-commons` fetched proactively (per
T-517's and T-509's identical discovery). Applying prior real discoveries up
front, rather than rediscovering them, is itself a real, honest outcome worth
recording.
