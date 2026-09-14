---
title: "Interview Question Bank — 05-spring"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../05-spring/INDEX.md
  - 04-software-design.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Spring

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 12 chapters yielded 24 deep questions + 5
already-leveled Junior/Mid questions (from the domain's one Junior Fundamentals
chapter, `spring-mvc-fundamentals.md`) + 32 quick-fire questions = **61 real
questions**.

---

## Spring MVC Fundamentals

Junior Fundamentals chapter — its Interview Questions already tag each by seniority.

### Q1 — What does `@Autowired` do?

**Canonical treatment:** [§15](../../05-spring/spring-mvc-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Tells Spring to inject a dependency. Target tier.
- **Mid/Senior/Staff:** Not typically asked in this exact form; the strongest Junior answers already note that on a single constructor (Spring Framework 4.3+), the annotation isn't even required.

### Q2 — Walk me through what happens when a GET request hits your `/tasks/{id}` endpoint.

**Canonical treatment:** [§15](../../05-spring/spring-mvc-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** `DispatcherServlet` matches the path and method to the controller method, the path segment binds to the parameter, the controller calls the service, the service calls the repository, and the return value is serialized to JSON by a message converter. Target tier.
- **Senior/Staff:** Not typically asked in this exact form.

### Q3 — Why prefer constructor injection over field `@Autowired`?

**Canonical treatment:** [§15](../../05-spring/spring-mvc-fundamentals.md#15-interview-questions)

**What's expected:**
- **Mid:** Visibility of the full dependency list, testability with plain `new`, and the ability to use `final` fields; strongest answers add that an awkwardly-long constructor is a useful, visible design smell field injection hides. Target tier.
- **Junior/Senior/Staff:** Not typically asked in this exact form.

### Q4 — You add a second implementation of an interface your controller already depends on, and the app fails to start. Why, and how do you fix it?

**Canonical treatment:** [§15](../../05-spring/spring-mvc-fundamentals.md#15-interview-questions)

**What's expected:**
- **Mid/Senior:** Spring can no longer determine which implementation to inject — the "no qualifying bean" ambiguity; fixed with `@Qualifier`, marking one implementation `@Primary`, or reconsidering whether both should really be active beans simultaneously. Target tier.
- **Junior/Staff:** Not typically asked in this exact form.

### Q5 — Your team's controllers have started accumulating direct database calls instead of going through the service layer. How do you address it?

**Canonical treatment:** [§15](../../05-spring/spring-mvc-fundamentals.md#15-interview-questions)

**What's expected:**
- **Senior/Staff:** A convention-erosion problem, not a one-off bug; the fix is process/tooling (code review discipline, or an automated architecture test enforcing the layering) rather than a single code change. Target tier.
- **Junior/Mid:** Not typically asked in this exact form.

---

## Auto-Configuration and Bean Lifecycle

### Q1 — Explain why `@Transactional` on an `@Async` method behaves unexpectedly.

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/auto-configuration-and-bean-lifecycle.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that the caller can't see the failure, without precisely explaining the transaction's own correctness.
- **Senior:** Correctly separates "the transaction rolled back correctly" from "the caller can't see it" — a void async method returns before the work runs.
- **Staff:** Proposes returning `CompletableFuture<T>` (or a custom `AsyncUncaughtExceptionHandler`) and states the trade-off of each fix.

### Q2 — What does `@ConditionalOnMissingBean` actually guarantee about ordering?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/auto-configuration-and-bean-lifecycle.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that auto-configuration runs after application config, without stating why that matters.
- **Senior:** States the ordering guarantee — the condition correctly reflects whether the application already supplied its own bean.
- **Staff:** Explains the failure mode if the ordering were reversed — the application's own bean would arrive too late to prevent the auto-configured default.

---

## Bean Validation and Global Exception Handling

### Q1 — How would you validate "cardNumber is required only when paymentMethod is CREDIT_CARD"?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/bean-validation-and-global-exception-handling.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes checking this rule manually inside the controller after `@Valid` has run — the common mistake this question targets.
- **Senior:** Correctly proposes a class-level `@Constraint`/`ConstraintValidator` and explains why field-level annotations alone can't express the rule.
- **Staff:** Proactively distinguishes the resulting `ObjectError` from a `FieldError` in the response shape, and reasons about testing the custom validator in isolation.

### Q2 — If you have a specific `@ExceptionHandler` for every exception type you can think of, why do you still need a catch-all?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/bean-validation-and-global-exception-handling.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats the question as purely about code tidiness rather than naming the real security consequence.
- **Senior:** Correctly explains that new, unanticipated exception types are inevitable and names the sensitive-detail-leak risk specifically (a real credential in a stack trace, per this chapter's evidence).
- **Staff:** Connects this to a broader "the unanticipated-failure default behavior is itself a security-relevant design decision" principle.

---

## Security Filter Chain

### Q1 — Trace an authenticated request through your security filter chain.

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/security-filter-chain.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names authentication and authorization as distinct steps, without a precise filter-by-filter trace.
- **Senior:** Traces the chain correctly with the two gates distinguished.
- **Staff:** Explains why the order specifically matters — authorization is meaningless without an established principal from authentication first.

### Q2 — Why does CORS/CSRF filtering happen before authentication in the chain?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/security-filter-chain.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Gives a plausible reason, without the general cost/decisiveness framing.
- **Senior:** Gives a plausible ordering rationale — resolve cheap, lower-level HTTP/browser-security concerns before spending effort on the more expensive authentication check.
- **Staff:** Frames it as a general principle (cheapest, most-decisive rejection checks run earliest) rather than a memorized specific order.

---

## Spring Actuator, Health, and Observability Hooks

### Q1 — Why does `/actuator/beans` return a 404 even though the endpoint is documented and built in?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/spring-actuator-health-and-observability-hooks.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes a routing or deployment misconfiguration before checking the exposure property — the common mistake this question targets.
- **Senior:** Names the exact property (`management.endpoints.web.exposure.include`) and explains the secure-by-default rationale.
- **Staff:** Connects this to a real security-review discipline around widening the exposure list, including the risk of a forgotten wildcard override.

### Q2 — How does a custom `HealthIndicator` actually affect the application's overall reported health?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/spring-actuator-health-and-observability-hooks.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes each `HealthIndicator`'s status is reported independently with no effect on the overall result — the common mistake this question targets.
- **Senior:** Explains the aggregation mechanism precisely (a `StatusAggregator`, worst status wins) and notes the bean-name-to-component-key derivation.
- **Staff:** Discusses the design risk of an overly strict custom indicator needlessly pulling a healthy instance from rotation.

---

## Spring Bean Scopes and Proxy Modes

### Q1 — Why did my prototype-scoped bean behave like a singleton once I injected it into another bean?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/spring-bean-scopes-and-proxy-modes.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that prototype "doesn't work" when injected into a singleton, without explaining why.
- **Senior:** Explains the resolve-once-at-construction mechanism precisely and names a scoped proxy or `ObjectProvider<T>` as the fix.
- **Staff:** Connects this to the identical proxy-based-AOP family used by `@Transactional`/`@Cacheable`, and discusses why this bug produces no exception.

### Q2 — What is actually injected when you use `@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)`?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/spring-bean-scopes-and-proxy-modes.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that "it's a proxy" without explaining the per-call delegation mechanism.
- **Senior:** Names CGLIB/JDK explicitly and explains the per-call re-resolution, ideally citing the identical mechanism used by `@Transactional`/`@Cacheable`.
- **Staff:** Notes the CGLIB/Objenesis caveat — a direct field read on the proxy reference does not see the real target's state, only method calls are correctly delegated.

---

## Spring Cache Abstraction and Pitfalls

### Q1 — Why doesn't `@Cacheable` work when I call the method from within the same class?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/spring-cache-abstraction-and-pitfalls.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the annotation itself is broken, rather than understanding the proxy mechanism — the common mistake this question targets.
- **Senior:** Explains the proxy mechanism precisely and connects it to the same limitation in `@Transactional` — self-invocation bypasses the proxy entirely.
- **Staff:** Proposes a concrete fix (self-injection, or extracting to a separate bean) and discusses why this is a Spring-wide constraint, not caching-specific.

### Q2 — What happens if a `@Cacheable` method returns a mutable object and the caller modifies it?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/spring-cache-abstraction-and-pitfalls.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the cache somehow isolates each caller's copy — the common mistake this question targets.
- **Senior:** States the no-defensive-copy behavior explicitly — the cache stores the exact reference; every future caller receives the same, now-corrupted instance.
- **Staff:** Connects this to a broader principle: any API returning a cached, shared reference should be treated as read-only by contract.

---

## Spring Data JPA Repository Abstraction

### Q1 — A derived query method has a typo in a property name that doesn't exist on the entity. When does this actually fail, and why?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/spring-data-jpa-repository-abstraction.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the compiler would catch this, or that the failure happens lazily on first invocation — the common mistake this question targets.
- **Senior:** Correctly identifies `PropertyReferenceException` (or "some startup-time validation failure") at `ApplicationContext` startup, before the app can serve traffic.
- **Staff:** Connects this to a broader principle — Spring Data's derived-method vocabulary trades expressive flexibility for exactly this kind of early, structural validation.

### Q2 — When would a derived query method or a static `@Query` genuinely not be enough?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/spring-data-jpa-repository-abstraction.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes writing one overloaded derived method per observed filter combination — the common mistake this question targets, which becomes unmanageable in practice.
- **Senior:** Correctly names `Specification` and explains the runtime-composition distinction versus derived methods/`@Query`.
- **Staff:** Proactively recognizes "number of repository methods tracking number of observed filter combinations" as the specific signal a codebase has missed this decision point.

---

## Spring Framework vs. Spring Boot

### Q1 — Your Spring Boot app unexpectedly configured a database connection you never asked for. What would you check?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/spring-framework-vs-spring-boot.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats the unexpected configuration as a bug in Spring Boot itself — the common mistake this question targets.
- **Senior:** Correctly identifies the classpath as the thing to inspect, and names `@ConditionalOnClass` as the mechanism (likely a test-scoped dependency leaking into runtime).
- **Staff:** Proposes running with `--debug` for the exact reason from Spring Boot's conditions-evaluation report, and a build-time dependency-scope safeguard.

### Q2 — What's actually different about how a Spring Boot application is deployed, compared to a traditional Spring MVC WAR?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/spring-framework-vs-spring-boot.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes the difference only as "convenience," without the ownership-inversion mechanism.
- **Senior:** Correctly explains the embedded server as a library the application starts itself via `main()`, versus a WAR deployed into an externally-managed server.
- **Staff:** Names a real consequence: an application crash takes the embedded server down with it, a genuinely different operational model than a traditional app server hosting multiple WARs.

---

## Spring Testing Slices and Context Caching

### Q1 — Why did adding `@DirtiesContext` to one test class make the whole CI suite slower?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/spring-testing-slices-and-context-caching.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `@DirtiesContext` only affects the one class it's declared on — the common mistake this question targets.
- **Senior:** Explains the cache-key/eviction mechanism precisely and identifies shared base test classes as the highest-risk location.
- **Staff:** Proposes a concrete prevention (code-review rule, scoping to the minimum class/method) and connects it to a real production-style CI-cost incident.

### Q2 — Why does a `@WebMvcTest` sometimes need a `@MockBean` for a service the controller barely uses?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/spring-testing-slices-and-context-caching.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Believes `@WebMvcTest` somehow auto-mocks every dependency automatically — the common mistake this question targets.
- **Senior:** Names the real exception (`NoSuchBeanDefinitionException`) and explains `@MockBean`'s role precisely as filling that specific gap.
- **Staff:** Discusses the deliberate trade-off — faster, more focused tests at the cost of not exercising real inter-layer wiring.

---

## Spring WebFlux and Reactive Programming

### Q1 — What actually goes wrong if you call a blocking method inside a WebFlux reactive chain?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/spring-webflux-and-reactive-programming.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as a minor performance tip rather than a structural correctness concern — the common mistake this question targets.
- **Senior:** Explains the small-fixed-thread-pool mechanism precisely and names `subscribeOn(Schedulers.boundedElastic())` as the fix.
- **Staff:** Connects this to the specific risk of a partial reactive migration performing worse than no migration at all.

### Q2 — Given virtual threads exist, when would you still choose WebFlux over a blocking + virtual-threads approach?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/spring-webflux-and-reactive-programming.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Claims reactive is "always faster" or "always more scalable" without qualification — the common mistake this question targets.
- **Senior:** Names backpressure explicitly as the genuine, remaining differentiator — virtual threads solve concurrent-blocking-call scaling but don't provide backpressure as a first-class contract.
- **Staff:** Discusses the real organizational cost of reactive's learning curve as a factor in the decision, not just the technical capability difference.

---

## Transactional Proxy Mechanics, Rollback Rules, and Propagation

### Q1 — Method A calls `@Transactional` method B in the same class. What happens, and why? Name three fixes.

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/transactional-proxy-mechanics-and-propagation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that self-invocation causes a problem, without precise proxy terminology.
- **Senior:** Correctly explains the proxy mechanism (no transaction starts for B — the call bypasses the proxy entirely) and names at least one of the three fixes.
- **Staff:** Names all three fixes with their trade-offs, and explains why CGLIB — not a JDK dynamic proxy — is used when the bean implements no interface.

### Q2 — Your method threw a checked exception. Did it roll back? Why is that the default?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/transactional-proxy-mechanics-and-propagation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes any exception thrown inside `@Transactional` rolls back — the common mistake this question targets.
- **Senior:** Correctly states the default rule (checked exceptions don't roll back by default) and names `rollbackFor` as the fix.
- **Staff:** Explains the EJB-convention history behind the default — checked exceptions were conventionally treated as expected, recoverable business outcomes.

### Q3 — `REQUIRES_NEW`: give a real use case, and name the deadlock risk.

**Canonical treatment:** [§ Interview Questions, Q3](../../05-spring/transactional-proxy-mechanics-and-propagation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names a plausible use case (audit logging that must survive the caller's rollback), without the deadlock risk.
- **Senior:** States both the use case and the risk — `REQUIRES_NEW` suspends the outer transaction's connection; if the inner transaction needs a row lock the outer already holds, it blocks indefinitely.
- **Staff:** Proposes a detection method — lock-wait timeout monitoring, or a review process checking whether `REQUIRES_NEW` methods touch rows the outer transaction has already locked.

### Q4 — There's an HTTP call inside a transaction. What breaks, and at what load?

**Canonical treatment:** [§ Interview Questions, Q4](../../05-spring/transactional-proxy-mechanics-and-propagation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that this makes the request slow, without connecting it to pool exhaustion for other requests.
- **Senior:** Names the pool-exhaustion mechanism explicitly — the connection is held for the HTTP call's entire duration, exhausting the pool under moderate concurrent load.
- **Staff:** Quantifies the blast radius — a slow dependency inside a transaction can take down unrelated endpoints sharing the same connection pool.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What's the correct bean lifecycle order? | [Auto-Configuration and Bean Lifecycle](../../05-spring/auto-configuration-and-bean-lifecycle.md#flashcards) |
| 2 | What mechanism creates a `@Transactional` proxy? | [Auto-Configuration and Bean Lifecycle](../../05-spring/auto-configuration-and-bean-lifecycle.md#flashcards) |
| 3 | Is `@Transactional` broken on an `@Async` method? | [Auto-Configuration and Bean Lifecycle](../../05-spring/auto-configuration-and-bean-lifecycle.md#flashcards) |
| 4 | Can a field-level annotation like `@NotBlank` express a rule spanning two fields? | [Bean Validation and Exception Handling](../../05-spring/bean-validation-and-global-exception-handling.md#flashcards) |
| 5 | If every exception type you throw has its own `@ExceptionHandler`, do you still need a catch-all? | [Bean Validation and Exception Handling](../../05-spring/bean-validation-and-global-exception-handling.md#flashcards) |
| 6 | What's the difference between a 401 and a 403? | [Security Filter Chain](../../05-spring/security-filter-chain.md#flashcards) |
| 7 | Can a filter chain short-circuit before reaching the controller? | [Security Filter Chain](../../05-spring/security-filter-chain.md#flashcards) |
| 8 | Why do CORS/CSRF checks typically run before authentication? | [Security Filter Chain](../../05-spring/security-filter-chain.md#flashcards) |
| 9 | If one custom `HealthIndicator` reports `DOWN` while everything else is `UP`, what's the overall status? | [Spring Actuator](../../05-spring/spring-actuator-health-and-observability-hooks.md#flashcards) |
| 10 | With zero Actuator configuration beyond the dependency, which endpoints are exposed over HTTP? | [Spring Actuator](../../05-spring/spring-actuator-health-and-observability-hooks.md#flashcards) |
| 11 | How can application code make `/actuator/health/readiness` report a specific state? | [Spring Actuator](../../05-spring/spring-actuator-health-and-observability-hooks.md#flashcards) |
| 12 | You inject a `prototype`-scoped bean by plain constructor reference into a singleton — what happens? | [Spring Bean Scopes and Proxy Modes](../../05-spring/spring-bean-scopes-and-proxy-modes.md#flashcards) |
| 13 | What object does the injection site actually hold with `proxyMode = ScopedProxyMode.TARGET_CLASS`? | [Spring Bean Scopes and Proxy Modes](../../05-spring/spring-bean-scopes-and-proxy-modes.md#flashcards) |
| 14 | Without a servlet container, how could you prove the mechanism behind request/session scope? | [Spring Bean Scopes and Proxy Modes](../../05-spring/spring-bean-scopes-and-proxy-modes.md#flashcards) |
| 15 | Why doesn't `@Cacheable` take effect when a method calls another `@Cacheable` method on `this`? | [Spring Cache Abstraction](../../05-spring/spring-cache-abstraction-and-pitfalls.md#flashcards) |
| 16 | If a `@Cacheable` method returns a mutable `List` and a caller mutates it, what happens? | [Spring Cache Abstraction](../../05-spring/spring-cache-abstraction-and-pitfalls.md#flashcards) |
| 17 | Why does `@CacheEvict(key = "#id")` sometimes throw `IllegalArgumentException: Null key returned`? | [Spring Cache Abstraction](../../05-spring/spring-cache-abstraction-and-pitfalls.md#flashcards) |
| 18 | `findByTotlAmountGreaterThan` compiles without error. When does this actually break? | [Spring Data JPA Repositories](../../05-spring/spring-data-jpa-repository-abstraction.md#flashcards) |
| 19 | Why can't a derived query method or a static `@Query` handle "any subset of five independent optional filters"? | [Spring Data JPA Repositories](../../05-spring/spring-data-jpa-repository-abstraction.md#flashcards) |
| 20 | Is Spring Boot a separate framework from Spring? | [Spring Framework vs. Spring Boot](../../05-spring/spring-framework-vs-spring-boot.md#flashcards) |
| 21 | What does adding a Spring Boot starter dependency actually do? | [Spring Framework vs. Spring Boot](../../05-spring/spring-framework-vs-spring-boot.md#flashcards) |
| 22 | How do you override a Spring Boot auto-configured bean? | [Spring Framework vs. Spring Boot](../../05-spring/spring-framework-vs-spring-boot.md#flashcards) |
| 23 | `@DirtiesContext` is added to one test class. Who actually pays the cost of context rebuilding? | [Spring Testing Slices and Context Caching](../../05-spring/spring-testing-slices-and-context-caching.md#flashcards) |
| 24 | A `@WebMvcTest` controller test throws `NoSuchBeanDefinitionException` for a service — why? | [Spring Testing Slices and Context Caching](../../05-spring/spring-testing-slices-and-context-caching.md#flashcards) |
| 25 | Why does `@RequestParam String name` (no explicit name) sometimes throw about `-parameters`? | [Spring Testing Slices and Context Caching](../../05-spring/spring-testing-slices-and-context-caching.md#flashcards) |
| 26 | Two subscribers subscribe to the same cold `Flux`. How many times does the source run? | [Spring WebFlux and Reactive Programming](../../05-spring/spring-webflux-and-reactive-programming.md#flashcards) |
| 27 | Does `publishOn` guarantee the upstream source never runs on the scheduler it switches to? | [Spring WebFlux and Reactive Programming](../../05-spring/spring-webflux-and-reactive-programming.md#flashcards) |
| 28 | A service migrates from blocking Spring MVC to WebFlux, but one repository call stays blocking — what happens? | [Spring WebFlux and Reactive Programming](../../05-spring/spring-webflux-and-reactive-programming.md#flashcards) |
| 29 | Why does calling an `@Transactional` method via `this` not start a transaction? | [Transactional Proxy Mechanics](../../05-spring/transactional-proxy-mechanics-and-propagation.md#flashcards) |
| 30 | Does a checked exception roll back a `@Transactional` method by default? | [Transactional Proxy Mechanics](../../05-spring/transactional-proxy-mechanics-and-propagation.md#flashcards) |
| 31 | What's the specific deadlock risk unique to `REQUIRES_NEW`? | [Transactional Proxy Mechanics](../../05-spring/transactional-proxy-mechanics-and-propagation.md#flashcards) |
| 32 | Is `@Transactional(readOnly = true)` guaranteed to prevent writes? | [Transactional Proxy Mechanics](../../05-spring/transactional-proxy-mechanics-and-propagation.md#flashcards) |

---

## Related

- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
