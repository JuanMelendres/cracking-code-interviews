# Spring WebFlux and Reactive Programming — Real, Executed Demos

Backs [Spring WebFlux and Reactive Programming](../../../../handbook/spring/spring-webflux-and-reactive-programming.md)
(T-509). Real Spring Framework 6.1.14 + Spring WebFlux + Project Reactor 3.6.10
output, plain jars fetched directly from Maven Central, no Maven/Gradle
install, run with the `junit-platform-console-standalone` shaded jar.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -parameters -cp "lib/*" -d out src/demo/*.java
```

## Demo 1 — `ColdVsHotDemo`: cold vs. hot publishers

```bash
java -cp "out:lib/*" demo.ColdVsHotDemo
```

Real output:

```
=== COLD Flux: the source's side effect re-runs for EACH subscriber ===
Real side-effect executions: 2 (expect 2 -- once per subscriber, independently)

=== HOT Flux (ConnectableFlux): the source's side effect runs ONCE ===
Real side-effect executions: 1 (expect 1 -- both subscribers share the one real upstream subscription)
```

A cold `Flux` (built with `Flux.defer`) re-executes its source's side effect
independently for every subscriber — proven directly: 2 subscribers, 2 real
executions. A hot `Flux` (a `ConnectableFlux` from `.publish()`) subscribes to
its source exactly once, regardless of how many downstream subscribers
attach — proven directly: 2 subscribers, 1 real execution, because `.connect()`
triggers a single, shared upstream subscription multicast to both.

## Demo 2 — `BackpressureDemo`: demand-driven production, proven directly

```bash
java -cp "out:lib/*" demo.BackpressureDemo
```

Real output (truncated):

```
Requesting first batch of 4
Consumed 1 -- real upstream elements produced so far: 1
...
Consumed 4 -- real upstream elements produced so far: 4
Requesting next batch of 4
Consumed 5 -- real upstream elements produced so far: 5
...
Done. Final produced count: 12 (matches total consumed -- never ran ahead of demand)
```

A `BaseSubscriber` requests elements in small, explicit batches
(`request(4)`) instead of unbounded demand. `Flux.range` is demand-aware — it
only actually generates ("produces") elements up to the outstanding request.
The real, measured proof: the "produced" counter never runs ahead of what was
actually requested, confirming backpressure is a real, enforced contract
between producer and consumer, not a marketing term for "handles load well."

## Demo 3 — `SchedulersDemo`: `subscribeOn` vs. `publishOn`, and a real fusion surprise

```bash
java -cp "out:lib/*" demo.SchedulersDemo
```

Real output:

```
=== subscribeOn: moves the WHOLE chain, including the source ===
source runs on: boundedElastic-1
doOnNext observes: boundedElastic-1

=== publishOn: only moves operators AFTER it -- but ONLY if the source can't be fused ===
--- without .hide(): Reactor fuses this simple source into publishOn's own pull loop ---
source runs on: boundedElastic-1
doOnNext observes: boundedElastic-1
(real result: the source ALSO ran on boundedElastic -- fusion pulled it there)

--- with .hide(): fusion is disabled, restoring the textbook push-based distinction ---
source runs on: main
doOnNext observes: boundedElastic-1
(real result: the source stayed on main -- only doOnNext moved)
```

`subscribeOn` affects the entire chain, including the source — expected, and
confirmed. The `publishOn`-only case is where this pack hit a real, honest
surprise: see [Real discoveries](#real-discoveries-made-while-building-this-pack)
below.

## Demo 4 — `BlockingPitfallDemo`: the real cost of blocking a reactive scheduler

```bash
java -cp "out:lib/*" demo.BlockingPitfallDemo
```

Real output:

```
=== BUGGY: blocking calls run directly on the tiny event-loop scheduler ===
finished task 1 at +328ms
finished task 2 at +641ms
finished task 3 at +943ms
Total: 943ms (expect ~900ms -- serialized on the single event-loop thread)

=== FIXED: blocking calls offloaded to a real bounded elastic pool ===
finished task 1 at +312ms
finished task 2 at +315ms
finished task 3 at +316ms
Total: 316ms (expect ~300ms -- ran concurrently on separate threads)
```

Three real `Thread.sleep(300)` calls (simulating blocking I/O) run directly on
a real, tiny, fixed-size scheduler (`Schedulers.newParallel("event-loop", 1)`,
standing in for WebFlux's real Netty event loop) — they serialize, real total
~950ms. Offloaded to `Schedulers.boundedElastic()` instead, the same three
calls run concurrently — real total ~320ms, roughly a 3x real, measured
difference for identical work. This is the concrete mechanism behind "never
block the event loop" in a real WebFlux application.

## Demo 5 — `ReactiveStreamsStepVerifierTest` and `GreetingRouterTest`: testing reactive code and a real WebFlux endpoint

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher \
  --select-class demo.ReactiveStreamsStepVerifierTest \
  --select-class demo.GreetingRouterTest
```

Real result: all 3 tests pass. `StepVerifier` asserts an exact `Flux` sequence
declaratively; `StepVerifier.withVirtualTime` really compresses two simulated
hours of `Flux.interval` delay into a few real milliseconds of test runtime — no
test ever actually sleeps for it. `GreetingRouterTest` proves a real, working
`RouterFunction` (Spring WebFlux's functional routing API) end-to-end using
`WebTestClient` bound directly to it — no real Netty server needed to verify
real request-predicate matching and a real reactive response.

## Real discoveries made while building this pack

Three real, honest discoveries:

1. **Reactor's operator fusion silently defeats the textbook `publishOn`
   explanation for simple, fuseable sources.** The first run of `SchedulersDemo`
   used `Mono.fromSupplier(...).publishOn(Schedulers.boundedElastic())...block()`
   expecting the source to stay on the calling thread — the real result showed
   it running on `boundedElastic-1` instead, identical to the `subscribeOn`
   case. Root cause: `Mono.fromSupplier` is `Fuseable`, and `publishOn` can
   negotiate fusion with a fuseable upstream, switching from push-based
   `onNext` delivery to a pull-based `poll()` loop running entirely on
   `publishOn`'s own worker — which means the source itself executes there too.
   Adding `.hide()` (which strips fusion capability from a publisher) restored
   the textbook distinction: source stays on `main`, only `doOnNext` moves.
   Both variants are now in the demo, verified with real thread-name output,
   specifically because the "surprising" one is real, common, and genuinely
   Staff-level-relevant — many explanations of `subscribeOn`/`publishOn` skip
   this fusion caveat entirely.
2. **`WebTestClient`'s exchange-observation support needs
   `micrometer-observation`/`micrometer-commons` on the classpath**, the
   identical transitive dependency already discovered in this domain's
   [Spring Testing: Slices and Context Caching](../spring-testing-slices-and-context-caching/README.md)
   pack — a real `NoClassDefFoundError: io/micrometer/observation/ObservationConvention`
   surfaced only when `GreetingRouterTest` actually exchanged a request.
3. No `-parameters`-related failure this time, despite this pack using
   `@RequestParam`-equivalent query-parameter access — `ServerRequest.queryParam(...)`
   in the functional `RouterFunction` API takes an explicit string key rather
   than relying on compiled parameter-name reflection, so the gotcha already
   hit twice elsewhere in this domain (T-514, T-517) didn't recur here; worth
   noting honestly as a case where it *didn't* apply, not just where it did.
