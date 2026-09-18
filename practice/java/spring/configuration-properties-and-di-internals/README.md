# Spring DI and Config Internals — Real Demos

Backs five chapters, closing a gap-audit batch:

- [`syllabus/15-cloud/twelve-factor-config.md`](../../../../syllabus/15-cloud/twelve-factor-config.md) — `@ConfigurationProperties`
- [`syllabus/05-spring/spring-mvc-fundamentals.md`](../../../../syllabus/05-spring/spring-mvc-fundamentals.md) — circular dependency resolution and `@Lazy`
- [`syllabus/05-spring/auto-configuration-and-bean-lifecycle.md`](../../../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md) — `@Configuration(proxyBeanMethods=...)`, `@Scheduled`
- [`syllabus/05-spring/spring-framework-vs-spring-boot.md`](../../../../syllabus/05-spring/spring-framework-vs-spring-boot.md) — `CommandLineRunner`

Real Spring Framework 6.2.19 + Spring Boot 3.5.16 (`spring-boot` module only —
no web/Tomcat needed, plain `AnnotationConfigApplicationContext`), OpenJDK
21.0.12.

## Run it

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
java -cp "out:lib/*" ConfigurationPropertiesDemo
java -cp "out:lib/*" CircularDependencyDemo
java -cp "out:lib/*" ConfigurationProxyDemo
java -cp "out:lib/*" SchedulingAndRunnersDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What each demo proves

**`ConfigurationPropertiesDemo`** — `@ConfigurationProperties` relaxed
binding and fail-fast validation, real and verified:
- Kebab-case keys (`app.max-retries`) bind to a camelCase record field, and
  a nested prefix (`app.retry.*`) binds into a nested record — zero manual
  parsing.
- The same top-level fields also bind correctly from real
  `SystemEnvironmentPropertySource`-shaped keys (`APP_MAX_RETRIES`).
- A malformed value (`app.max-retries=not-a-number`) fails the *entire*
  bind at context-startup time with one `BindException` naming the exact
  offending key — not a scattered failure at whichever `@Value` injection
  point happens to use it first.

**`CircularDependencyDemo`** — three real, different outcomes for what
looks like "the same" circular dependency:
- Two beans requiring each other via **constructor** injection is a real,
  unresolvable `BeanCurrentlyInCreationException` at startup — there is no
  valid construction order.
- The identical cycle via **field** injection resolves silently — Spring
  calls each no-arg constructor first (both objects now exist), then wires
  `@Autowired` fields afterward, so there's no ordering problem at all.
- `@Lazy` on one side of a constructor-injected cycle fixes it by injecting
  a real CGLIB proxy instead of the bean itself — and the demo catches a
  genuine limitation of that proxy: a **direct field read** through it
  returns the wrong (uninitialized) value, while a **method call** through
  the identical proxy correctly delegates to the real target. Lazy proxies
  only intercept method calls, never field access.

**`ConfigurationProxyDemo`** — why calling one `@Bean` method from inside
another, on the same `@Configuration` class, doesn't create a second
object:
- With the default `@Configuration` (`proxyBeanMethods = true`), the
  configuration class itself is a real CGLIB proxy; calling `engine()` as a
  plain Java method call from inside `car()` is intercepted and returns the
  container's existing singleton — verified with a real instance counter
  staying at 1 across three calls.
- With `@Configuration(proxyBeanMethods = false)`, `engine()` is a
  completely ordinary Java method call with no interception at all — the
  identical code now genuinely constructs 3 separate `Engine` instances,
  each bean holding its own, uncoordinated copy.

**`SchedulingAndRunnersDemo`** — two independent real proofs:
- `@Scheduled(fixedRate = 100)` on a plain bean method genuinely runs on a
  background scheduler thread, entirely independent of the caller — a real
  counter reaches 6 after the main thread sleeps for 550ms doing nothing
  else.
- The identical `CommandLineRunner` bean is **never invoked** under a plain
  `AnnotationConfigApplicationContext` (the bean exists, but nothing ever
  calls `.run()` on it) — and **is** invoked under `SpringApplication.run()`,
  proving the mechanism lives in `SpringApplication` itself, not Spring
  Framework's core container. Real, observed order:  `SpringApplication`
  logs its own `Started ... in N seconds` banner line *before* the runner
  actually executes — a precise, real fact worth knowing exactly, not just
  "it runs at startup" — but the runner still completes before
  `SpringApplication.run()` returns control to the caller.
