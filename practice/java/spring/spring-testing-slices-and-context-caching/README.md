# Spring Testing: Slices and Context Caching — Real, Executed Demos

Backs [Spring Testing: Slices and Context Caching](../../../../handbook/spring/spring-testing-slices-and-context-caching.md)
(T-517). Real Spring Framework 6.1.14 + Spring Boot 3.3.5 output, plain jars
fetched directly from Maven Central, no Maven/Gradle install, run with the
`junit-platform-console-standalone` shaded jar instead of a build tool.

Unlike this domain's other packs (`spring-cache-abstraction-and-pitfalls`,
`spring-bean-scopes-and-proxy-modes`), this topic — slice testing specifically —
does not exist in plain Spring Framework at all; `@WebMvcTest`, `@DataJpaTest`,
and `@MockBean` are Spring Boot Test features. Pulling in Spring Boot Test here
is a deliberate, topic-driven exception to the "plain Spring Framework jars"
pattern used elsewhere in this domain, not a departure from it.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -parameters -cp "lib/*" -d out src/demo/*.java
```

The `-parameters` flag is required — see [Real discoveries](#real-discoveries-made-while-building-this-pack)
below.

## Demo 1 — `ContextCachingDemo`: proving the TestContext framework's cache directly

```bash
java -cp "out:lib/*" demo.ContextCachingDemo
```

Real output:

```
=== TestClassA: first class to use this @ContextConfiguration ===
Real contexts created so far: 1

=== TestClassB: identical @ContextConfiguration ===
Real contexts created so far: 1 (expect still 1 -- context reused from the cache)

=== TestClassC: identical config, but annotated @DirtiesContext ===
Real contexts created so far: 1 (still 1 -- C reused the cache while running, then dirtied it on the way out)

=== TestClassD: identical config again, run AFTER C dirtied the cache ===
Real contexts created so far: 2 (expect 2 -- a real, fresh context was rebuilt)
```

`TestClassA`–`TestClassD` are plain marker classes carrying real
`@ContextConfiguration`/`@DirtiesContext` annotations — they are never run by a
JUnit test engine. `ContextCachingDemo.main()` drives `TestContextManager`
directly, the exact class `SpringExtension` delegates to internally, in
guaranteed sequential program order. This sidesteps JUnit's own
class-execution-order ambiguity entirely while still exercising the real,
identical caching mechanism `@ExtendWith(SpringExtension.class)` tests use.

`CountingConfig`'s `@Bean` factory method increments a static counter — proof
that a *fresh* `ApplicationContext` was actually built, since a cache hit skips
bean creation entirely. The real result: `A` and `B` (identical config) share one
context; `C` (identical config, `@DirtiesContext`) also reuses it while running,
but evicts it afterward; `D` (identical config again) is forced to rebuild —
counter goes from 1 to 2, proving the eviction was real.

## Demo 2 — `GreetingControllerSliceTest`: what a slice test actually excludes

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.GreetingControllerSliceTest
```

Real output (passing, after the fix described below):

```
GreetingControllerSliceTest ✔
  greetEndpointReturnsGreeting() ✔
```

`@WebMvcTest(GreetingController.class)` loads only the web-layer slice —
`GreetingController` plus Spring MVC infrastructure (`DispatcherServlet`,
argument resolvers, `MockMvc`) — not the full application context.
`GreetingController` depends on `GreetingService`, a plain `@Service`, which is
**not** part of the web slice. The real, verbatim failure this produced before
`@MockBean` was added:

```
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating
bean with name 'greetingController' defined in file [...GreetingController.class]:
Unsatisfied dependency expressed through constructor parameter 0: No qualifying
bean of type 'demo.GreetingService' available: expected at least 1 bean which
qualifies as autowire candidate. Dependency annotations: {}

Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException:
No qualifying bean of type 'demo.GreetingService' available: expected at least
1 bean which qualifies as autowire candidate. Dependency annotations: {}
```

The fix — a real Mockito stand-in supplied into the slice's context:

```java
@MockBean
private GreetingService greetingService;
```

## Demo 3 — `SpringBootTestFullContextTest`: the contrast

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher \
  --select-class demo.SpringBootTestFullContextTest \
  --select-class demo.GreetingControllerSliceTest
```

Real output:

```
Real GreetingService instances created so far: 1
...
SpringBootTestFullContextTest ✔
GreetingControllerSliceTest ✔
```

`@SpringBootTest` loads the **full** application context — `GreetingService`
is genuinely instantiated (its constructor really runs, real counter
increments to 1) — no `@MockBean` needed, because the real bean is actually
present. This is the direct, measured contrast with Demo 2: same collaborator,
present in one slice and absent from the other, purely because of which
Spring Boot test annotation was used.

## Real discoveries made while building this pack

Three real, honest discoveries, in the order they were hit:

1. **Spring Boot's `@SpringBootConfiguration` auto-detection cannot resolve
   from the default (unnamed) Java package.** Every source file in this pack
   started in the default package, following this domain's other packs. The
   very first run of `GreetingControllerSliceTest` failed immediately with:
   ```
   java.lang.IllegalStateException: Unable to find a @SpringBootConfiguration,
   you need to use @ContextConfiguration or @SpringBootTest(classes=...) with
   your test
   ```
   `@WebMvcTest` (and `@SpringBootTest` without an explicit `classes=`) locate
   their configuration by walking up from the test class's package looking for
   a `@SpringBootConfiguration`-annotated class (`DemoApplication`, in this
   pack) — a walk that requires a real package to walk up *from*. The fix: move
   every class into `package demo;`. This is a genuine, easy-to-hit gotcha for
   anyone trying a minimal Spring Boot reproduction without a package
   structure.
2. **`WebMvcAutoConfiguration`'s request-observation support needs
   `micrometer-observation`/`micrometer-commons` on the classpath**, even
   though nothing in this pack's code references Micrometer directly — a real
   `ClassNotFoundException: io.micrometer.observation.transport.RequestReplyReceiverContext`
   surfaced only once auto-configuration actually ran. Fixed by adding both
   jars; `fetch-deps.sh` includes them with this exact explanation.
3. **The same `-parameters` compiler-flag gotcha as
   [Spring Cache Abstraction](../spring-cache-abstraction-and-pitfalls/README.md),
   recurring in a different mechanism.** `@RequestParam String name` (no
   explicit `name` attribute) threw a real
   `IllegalArgumentException: Name for argument of type [java.lang.String] not
   specified, and parameter name information not available via reflection.
   Ensure that the compiler uses the '-parameters' flag.` — Spring MVC's
   argument-name resolution needs the identical bytecode metadata SpEL keys
   needed in the caching chapter, just consumed by a different piece of code
   (`AbstractNamedValueMethodArgumentResolver` instead of SpEL). Fixed the same
   way: compile with `-parameters`.
