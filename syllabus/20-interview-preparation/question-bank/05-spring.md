---
title: "Interview Question Bank — 05-spring"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.1
last_updated: 2026-09-29
related:
  - ../../05-spring/INDEX.md
  - 04-software-design.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Spring

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 16 chapters yielded 52 deep questions + 5
already-leveled Junior/Mid questions (from the domain's one Junior Fundamentals
chapter, `spring-mvc-fundamentals.md`) + 40 quick-fire questions = **97 real
questions**. (Updated 2026-09-27: `microservices-patterns-with-spring-boot.md`
had a complete Interview Questions section this index never picked up — a
stale-index gap, not a content gap, found via a generic interview checklist
audit. Added its 3 questions + 2 quick-fire cards, plus a cross-domain note
pointing to `17-architecture.md` for the Microservices-vs-Monolith trade-off
itself. Updated again, same day: `dto-entity-mapper-patterns.md` and
`spring-and-spring-boot-fundamentals.md` — previously flagged as known
remaining gaps — are now indexed too. Added 2 questions + 2 quick-fire cards
for the former; 20 questions for the latter — its own Interview Questions
section already tags each answer by seniority tier directly, so summarized
concisely below rather than re-derived; it has no embedded Flashcards section
in the canonical chapter to mine for quick-fire, only a standalone deck. Updated 2026-09-29:
`request-filters-interceptors-and-the-servlet-chain.md` is a new chapter closing a
real coverage gap — `HandlerInterceptor` appeared nowhere in the repository, while
the Servlet filter side was covered only through `security-filter-chain.md` — and
contributes 3 questions + 4 quick-fire cards.)

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

## Microservices Patterns with Spring Boot

**Cross-domain note:** "Microservices vs. Monolith" itself (the architectural trade-off, not its Spring-specific implementation) is covered in [`question-bank/17-architecture.md`](17-architecture.md), § "Microservice Decomposition and the Monolith Trade-off" — this section covers only how those patterns become concrete Spring Boot mechanisms.

### Q1 — Your circuit breaker's dashboard shows it has never opened, but users are reporting a specific downstream call is consistently slow. What's going on?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/microservices-patterns-with-spring-boot.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the breaker must be misconfigured with the wrong threshold *value* — the common mistake this question targets.
- **Senior:** Names the missing configuration precisely: the breaker's config almost certainly sets `failureRateThreshold` but not `slowCallDurationThreshold`/`slowCallRateThreshold` — a call that succeeds slowly isn't counted as a failure by default, so the breaker has nothing to react to.
- **Staff:** Raises the platform-wide version of the question — is this one service's oversight, or a template every service copied?

### Q2 — Why did a circuit breaker open on the 4th failing call rather than the 5th, given `minimumNumberOfCalls(5)`?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/microservices-patterns-with-spring-boot.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `minimumNumberOfCalls` means "N consecutive failures required" — the common mistake this question targets.
- **Senior:** Correctly reconstructs the sliding window's contents: it already held 1 prior healthy call plus the first 4 failing calls — 5 calls total, 80% failure rate, already past the 50% threshold. The window counts every recorded call, not just calls since the last failure began.
- **Staff:** Discusses count-based vs. time-based sliding windows and when each is the right choice for a given traffic pattern.

### Q3 — Why choose Spring's `@HttpExchange` over Spring Cloud OpenFeign for a brand-new microservice today?

**Canonical treatment:** [§ Interview Questions, Q3](../../05-spring/microservices-patterns-with-spring-boot.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Claims Feign is deprecated or "wrong" — the common mistake this question targets.
- **Senior:** States the real trade-off honestly: `@HttpExchange` is part of Spring Framework itself (6.0+), needs no Spring Cloud BOM, and is Spring's own current recommendation; OpenFeign remains common in existing Spring-Cloud-heavy stacks.
- **Staff:** Frames this as a platform-consistency decision — a new service joining an existing Feign-heavy estate has a real cost to breaking pattern, independent of which client is technically better.

---

## DTO/Entity/Mapper Patterns

### Q1 — Why not just return the JPA entity directly from a `@RestController` method?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/dto-entity-mapper-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Answers only "it's best practice" with no specific mechanism named — the common mistake this question targets.
- **Senior:** Names at least two real mechanisms: a lazy-loaded association can throw `LazyInitializationException` once the entity leaves its persistence-context scope, internal-only fields may have no business reason to be exposed, and the API's public contract becomes accidentally coupled to the database schema.
- **Staff:** Frames DTO/Entity separation as an explicit, platform-wide contract-stability rule rather than a per-team convention.

### Q2 — Why does real MapStruct-generated code never reference an entity's sensitive field the DTO doesn't declare?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/dto-entity-mapper-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes this as the mapper "filtering out" the field, implying an active exclusion step — the common mistake this question targets.
- **Senior:** Correctly distinguishes "never declared as a target field" from "actively filtered" — MapStruct only generates code copying fields the target DTO actually has slots for, a structural guarantee provable via reflection on the DTO's own declared components.
- **Staff:** Connects this to a broader security principle: a boundary's safety should come from what it structurally cannot do, not from remembering to add a check.

---

## Spring and Spring Boot Fundamentals

A 20-question interview checklist audit found every concept already taught in this domain's prose, but none formatted as an actual, answerable interview question with a Junior baseline through a Staff extension — this chapter's own Interview Questions section closes that directly, tagging each answer by tier already, summarized concisely below.

### Q1 — What is the Spring Framework?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** A Java framework providing DI, a container managing "beans," and supporting modules (MVC, transactions, data access).
- **Mid:** Adds that Spring's core is the IoC container; everything else is a module built on it.
- **Senior:** Distinguishes Spring Framework (the programming model) from Spring Boot (starters, auto-configuration, embedded server) precisely.
- **Staff:** Frames adopting Spring as an organizational choice — a common vocabulary across teams, at the cost of some flexibility.

### Q2 — What is Dependency Injection (DI)?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** A class declares what it needs (usually a constructor parameter) instead of calling `new`; the container builds and hands it in.
- **Mid:** Names all three injection styles (constructor, setter, field) and states the recommended default.
- **Senior:** Explains DI as the specific mechanism implementing the broader IoC principle.
- **Staff:** Connects DI to organizational testability — constructor-injected code can be unit-tested with plain `new`, no Spring context needed, a real velocity difference at team scale.

### Q3 — Constructor Injection vs. Field Injection — which approach and why?

**Canonical treatment:** [§ Interview Questions, Q3](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Constructor injection is recommended — dependencies are visible in the constructor signature, and the class is testable with plain `new`.
- **Mid:** Adds the immutability point (`final` fields) and the "too many dependencies" visible-smell argument.
- **Senior:** States field injection makes a class untestable with plain `new` as the real, practical cost.
- **Staff:** Names the real circular-dependency divergence: constructor injection fails loudly (`BeanCurrentlyInCreationException`); field injection resolves the identical cycle silently.

### Q4 — What is IoC (Inversion of Control)?

**Canonical treatment:** [§ Interview Questions, Q4](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Control over object creation/wiring is handed to a container; your code just states what it needs.
- **Mid:** States explicitly that DI is one implementation of the broader IoC principle, not a synonym.
- **Senior:** Names the `ApplicationContext` as the concrete container performing the inversion.
- **Staff:** Discusses the real trade-off — a class's behavior can't be fully understood from its own source alone, since what gets injected depends on external container configuration.

### Q5 — What is a Spring Bean?

**Canonical treatment:** [§ Interview Questions, Q5](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Any object the Spring IoC container creates, configures, and manages — unlike a plain object your own code creates with `new`.
- **Mid:** States how a class becomes a bean (a stereotype annotation, or a `@Bean` method) and that the container discovers it via component scanning.
- **Senior:** Distinguishes "a bean" from "a POJO instantiated with `new`" precisely, even for the identical class.
- **Staff:** Connects bean management to every proxy-based framework feature (transactions, caching, security) built on it.

### Q6 — Explain the Spring Bean Lifecycle.

**Canonical treatment:** [§ Interview Questions, Q6](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Created → dependencies injected → initialized → destroyed; `@PostConstruct` is the everyday "ready" hook.
- **Mid:** States the constructor runs before field injection completes, which is exactly why `@PostConstruct` (not the constructor) is right for logic needing injected fields.
- **Senior:** Names `BeanPostProcessor` as the hook behind features like `@Transactional` proxy creation.
- **Staff:** Explains why knowing the exact lifecycle moment a feature hooks into is what lets an engineer correctly diagnose annotation-interaction surprises (e.g., `@Async` + `@Transactional`).

### Q7 — What are the different Bean Scopes?

**Canonical treatment:** [§ Interview Questions, Q7](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** `singleton` (default, one shared instance) and `prototype` (new instance every request).
- **Mid:** Names all six real scopes, including the four web-context ones (`request`/`session`/`application`/`websocket`).
- **Senior:** States the real gotcha: injecting a `prototype` bean into a `singleton` via ordinary injection resolves it once, permanently — not fresh each use.
- **Staff:** Names the actual fix (`ObjectProvider<T>` or a scoped proxy) and explains why a plain injected field can't solve it.

### Q8 — Difference between `@Component`, `@Service`, `@Repository`, and `@Controller`.

**Canonical treatment:** [§ Interview Questions, Q8](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** All four register a bean via component scanning; the other three are self-documenting names for conventional layers.
- **Mid:** Names the one real behavioral difference: `@Repository` enables persistence exception translation.
- **Senior:** States that `@Service` and `@Controller` add no behavior beyond `@Component` — pure documentation/convention.
- **Staff:** Frames using specific stereotypes (versus `@Component` everywhere) as an organizational-clarity decision at multi-team scale.

### Q9 — `@RestController` vs. `@Controller`.

**Canonical treatment:** [§ Interview Questions, Q9](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** `@RestController` = `@Controller` + `@ResponseBody` — the return value is written directly to the response body instead of resolved as a view name.
- **Mid:** States what `@Controller` alone does without `@ResponseBody` (server-rendered view resolution).
- **Senior:** Connects this composition pattern to its parallel: `@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`.
- **Staff:** Notes a mixed codebase (some `@Controller`, some `@RestController`) is a legitimate migration-era pattern, not automatically inconsistency.

### Q10 — How does `@Autowired` work internally?

**Canonical treatment:** [§ Interview Questions, Q10](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** The container inspects the annotated constructor/field/setter's type and looks for a matching bean in the `ApplicationContext`.
- **Mid:** States `@Autowired` has been unnecessary on a class's only constructor since Spring 4.3.
- **Senior:** Explains resolution order precisely: type → `@Qualifier` name → `@Primary` → `NoUniqueBeanDefinitionException`.
- **Staff:** Connects this to the recursive nature of dependency-graph resolution, the same mechanism behind `BeanCurrentlyInCreationException`.

### Q11 — What happens when multiple beans of the same type are available?

**Canonical treatment:** [§ Interview Questions, Q11](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Spring fails to start with `NoUniqueBeanDefinitionException`, unless resolved via `@Qualifier`/`@Primary`.
- **Mid:** States both mechanisms and their relationship.
- **Senior:** Notes this is a startup-time failure, not a runtime one — a deliberate fail-fast design choice.
- **Staff:** Discusses the trade-off of having multiple implementations of the same interface as beans at all, and the disambiguation discipline it requires everywhere.

### Q12 — `@Primary` vs. `@Qualifier`.

**Canonical treatment:** [§ Interview Questions, Q12](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** `@Qualifier("name")` picks a specific bean by name; `@Primary` marks the default when no qualifier narrows the choice.
- **Mid:** States the precedence: an explicit `@Qualifier` always wins over `@Primary`.
- **Senior:** Gives a concrete example distinguishing when each is the right tool.
- **Staff:** Notes the maintainability cost of `@Qualifier` string literals at scale versus a custom qualifier annotation.

### Q13 — What are `@Configuration` and `@Bean`?

**Canonical treatment:** [§ Interview Questions, Q13](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** `@Configuration` marks a bean-definition source class; a `@Bean` method's return value is registered as a bean — the alternative for classes you don't own.
- **Mid:** States precisely why: you can't add `@Component` to a third-party class.
- **Senior:** Explains `@Configuration` classes are CGLIB-proxied by default, so one `@Bean` method calling another returns the same singleton instance.
- **Staff:** Discusses when `proxyBeanMethods = false` is the deliberate right choice.

### Q14 — What is Component Scanning?

**Canonical treatment:** [§ Interview Questions, Q14](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Spring walks the application's packages at startup looking for `@Component`-annotated classes to register as beans.
- **Mid:** States the scanning boundary is set by `@ComponentScan` — a class outside it is never discovered.
- **Senior:** Names the real failure mode: a class outside the main application class's package tree is silently never scanned.
- **Staff:** Frames package structure itself as an architectural decision because of this mechanism.

### Q15 — What does `@SpringBootApplication` contain?

**Canonical treatment:** [§ Interview Questions, Q15](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** A composition of `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`.
- **Mid:** States what each of the three specifically contributes on its own.
- **Senior:** Connects `@ComponentScan`'s inclusion directly to Q14's package-boundary gotcha.
- **Staff:** Discusses why Spring Boot chose composition over three separately-required annotations — reducing universal boilerplate.

### Q16 — How does Spring Boot Auto-Configuration work?

**Canonical treatment:** [§ Interview Questions, Q16](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Spring Boot ships conditional `@Configuration` classes that activate based on classpath contents and existing beans.
- **Mid:** Names `@ConditionalOnMissingBean` as the guard letting an application override one piece of auto-configured behavior.
- **Senior:** States where these classes live (`spring-boot-autoconfigure`) and that `@EnableAutoConfiguration` turns the mechanism on.
- **Staff:** Connects this to a real risk: a test-scoped dependency leaking onto the production classpath can silently activate unintended auto-configuration.

### Q17 — What is `@EnableAutoConfiguration`?

**Canonical treatment:** [§ Interview Questions, Q17](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** The specific annotation, composed inside `@SpringBootApplication`, that turns on auto-configuration.
- **Mid:** States it's one of exactly three composed annotations, each with a distinct job.
- **Senior:** Explains it can, in principle, be used standalone — a real, independent annotation.
- **Staff:** Discusses why Spring Boot separates "turn on auto-configuration" from "scan for components" as distinct concerns.

### Q18 — What are Spring Boot Starter Dependencies?

**Canonical treatment:** [§ Interview Questions, Q18](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Curated dependency bundles (e.g., `spring-boot-starter-web`) with pre-aligned, compatible versions.
- **Mid:** States the specific problem solved: manually choosing and version-matching every dependency.
- **Senior:** Connects starters to auto-configuration directly — starters put things on the classpath; auto-configuration reacts to that classpath.
- **Staff:** Discusses the dependency-footprint trade-off of broad starters at organizational scale.

### Q19 — How do Profiles work?

**Canonical treatment:** [§ Interview Questions, Q19](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** A profile is a named label controlling which beans and config values load; `@Profile("dev")` restricts a bean to that profile.
- **Mid:** States the concrete swap-per-environment pattern (a real vs. sandbox gateway implementation) this enables.
- **Senior:** Connects `spring.profiles.active` to which profile-gated beans and property overrides take effect.
- **Staff:** Frames profile-per-environment as an organizational discipline question — who can introduce a new profile, and how config drift is prevented.

### Q20 — How do you handle Configuration Management across different environments?

**Canonical treatment:** [§ Interview Questions, Q20](../../05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Externalize environment-specific values into `application-{profile}.yml` or environment variables, activating the correct profile per environment.
- **Mid:** States Spring Boot's property-source precedence (CLI args/env vars override profile YAML, which overrides base YAML).
- **Senior:** Names secrets specifically as needing more than plain profile-scoped YAML — a real secrets manager, never committed.
- **Staff:** Frames this as the concrete Spring Boot instance of the general twelve-factor "config in the environment" principle.

---

## Request Filters, Interceptors, and the Servlet Chain

### Q1 — What is the difference between a Servlet filter and a Spring `HandlerInterceptor`, and give one consequence that actually matters?

**Canonical treatment:** [§ Interview Questions, Q1](../../05-spring/request-filters-interceptors-and-the-servlet-chain.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that filters are servlet-level and interceptors are Spring-level, and that filters run first — the memorized answer, with no consequence derived from it.
- **Senior:** Derives the consequences from the nesting: a filter wraps the whole `DispatcherServlet` so it runs for 404s and static resources but cannot see the resolved handler; an interceptor runs inside it and receives the `HandlerMethod` and its annotations. Names the decision rule (concern about the request → filter; about the method → interceptor) and knows `@ControllerAdvice` reaches interceptor exceptions but not filter exceptions.
- **Staff:** Raises the error-contract consequence — a filter failure produces the container's default body, a genuinely different JSON shape from the application's own, measured directly — so the API has two error schemas and only one is covered by contract tests.

### Q2 — An interceptor logs failures with `if (ex != null)` in `afterCompletion`. It logs nothing in production despite real 500s. Why?

**Canonical treatment:** [§ Interview Questions, Q2](../../05-spring/request-filters-interceptors-and-the-servlet-chain.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Blames log levels or the logging framework; or proposes moving the logic to `postHandle`, which is strictly worse since `postHandle` does not run on the error path at all.
- **Senior:** Knows `ex` is the *unresolved* exception, so anything `@ControllerAdvice` already handled arrives as `null` — measured: the controller threw, the advice mapped it to 422, and both interceptors still recorded `ex=null, status=422`. Fixes it by keying off the response status.
- **Staff:** Generalizes it — monitoring that infers failure from an exception object rather than the observable outcome under-reports precisely in services that handle errors well, so the better-engineered the service, the blinder the dashboard.

### Q3 — Your `preHandle` returns `false` to reject a request. What does and does not run afterwards?

**Canonical treatment:** [§ Interview Questions, Q3](../../05-spring/request-filters-interceptors-and-the-servlet-chain.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Correctly states the handler and `postHandle` are skipped; assumes the rejecting interceptor's own `afterCompletion` still runs.
- **Senior:** Knows `afterCompletion` runs only on interceptors *earlier* in the chain whose `preHandle` already returned `true` — Spring records `interceptorIndex` in `applyPreHandle` and `triggerAfterCompletion` unwinds only that far. Also knows Spring writes no response, so returning `false` without setting one yields an empty `200`, and that both filters still complete normally.
- **Staff:** Draws the operational conclusion — `preHandle` is an unsafe place to acquire anything needing release on the rejection path, and rejection metrics must be emitted inline — and argues for encoding that in a shared base class rather than rediscovering it per service.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What's the correct bean lifecycle order? | [Spring Auto-Configuration and Bean Lifecycle](../../05-spring/auto-configuration-and-bean-lifecycle.md#flashcards) |
| 2 | What mechanism creates a `@Transactional` proxy? | [Spring Auto-Configuration and Bean Lifecycle](../../05-spring/auto-configuration-and-bean-lifecycle.md#flashcards) |
| 3 | Is `@Transactional` broken on an `@Async` method? | [Spring Auto-Configuration and Bean Lifecycle](../../05-spring/auto-configuration-and-bean-lifecycle.md#flashcards) |
| 4 | Can a field-level annotation like `@NotBlank` express a rule spanning two fields? | [Bean Validation and Exception Handling](../../05-spring/bean-validation-and-global-exception-handling.md#flashcards) |
| 5 | If every exception type you throw has its own `@ExceptionHandler`, do you still need a catch-all? | [Bean Validation and Exception Handling](../../05-spring/bean-validation-and-global-exception-handling.md#flashcards) |
| 6 | What's the difference between a 401 and a 403? | [Spring Security Filter Chain](../../05-spring/security-filter-chain.md#flashcards) |
| 7 | Can a filter chain short-circuit before reaching the controller? | [Spring Security Filter Chain](../../05-spring/security-filter-chain.md#flashcards) |
| 8 | Why do CORS/CSRF checks typically run before authentication? | [Spring Security Filter Chain](../../05-spring/security-filter-chain.md#flashcards) |
| 9 | If one custom `HealthIndicator` reports `DOWN` while everything else is `UP`, what's the overall status? | [Spring Actuator](../../05-spring/spring-actuator-health-and-observability-hooks.md#flashcards) |
| 10 | With zero Actuator configuration beyond the dependency, which endpoints are exposed over HTTP? | [Spring Actuator](../../05-spring/spring-actuator-health-and-observability-hooks.md#flashcards) |
| 11 | How can application code make `/actuator/health/readiness` report a specific state? | [Spring Actuator](../../05-spring/spring-actuator-health-and-observability-hooks.md#flashcards) |
| 12 | You inject a `prototype`-scoped bean by plain constructor reference into a singleton — what happens? | [Spring Bean Scopes and Proxy Modes](../../05-spring/spring-bean-scopes-and-proxy-modes.md#flashcards) |
| 13 | What object does the injection site actually hold with `proxyMode = ScopedProxyMode.TARGET_CLASS`? | [Spring Bean Scopes and Proxy Modes](../../05-spring/spring-bean-scopes-and-proxy-modes.md#flashcards) |
| 14 | Without a servlet container, how could you prove the mechanism behind request/session scope? | [Spring Bean Scopes and Proxy Modes](../../05-spring/spring-bean-scopes-and-proxy-modes.md#flashcards) |
| 15 | Why doesn't `@Cacheable` take effect when a method calls another `@Cacheable` method on `this`? | [Spring Cache Abstraction and Pitfalls](../../05-spring/spring-cache-abstraction-and-pitfalls.md#flashcards) |
| 16 | If a `@Cacheable` method returns a mutable `List` and a caller mutates it, what happens? | [Spring Cache Abstraction and Pitfalls](../../05-spring/spring-cache-abstraction-and-pitfalls.md#flashcards) |
| 17 | Why does `@CacheEvict(key = "#id")` sometimes throw `IllegalArgumentException: Null key returned`? | [Spring Cache Abstraction and Pitfalls](../../05-spring/spring-cache-abstraction-and-pitfalls.md#flashcards) |
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
| 33 | What does a circuit breaker's sliding window actually count? | [Microservices Patterns with Spring Boot](../../05-spring/microservices-patterns-with-spring-boot.md#flashcards) |
| 34 | Does a default circuit breaker protect against a slow-but-successful downstream call? | [Microservices Patterns with Spring Boot](../../05-spring/microservices-patterns-with-spring-boot.md#flashcards) |
| 35 | Why does this chapter recommend `record` for DTOs but not for JPA entities? | [DTO/Entity/Mapper Patterns](../../05-spring/dto-entity-mapper-patterns.md#flashcards) |
| 36 | Is a mapper never referencing an undeclared sensitive field a real guarantee, or coincidence? | [DTO/Entity/Mapper Patterns](../../05-spring/dto-entity-mapper-patterns.md#flashcards) |
| 37 | In one sentence, what is the structural difference between a Servlet filter and a `HandlerInterceptor`? | [Request Filters and Interceptors](../../05-spring/request-filters-interceptors-and-the-servlet-chain.md#flashcards) |
| 38 | Can `@ControllerAdvice` handle an exception thrown in a filter? | [Request Filters and Interceptors](../../05-spring/request-filters-interceptors-and-the-servlet-chain.md#flashcards) |
| 39 | Why is `afterCompletion`'s `ex` parameter almost always null? | [Request Filters and Interceptors](../../05-spring/request-filters-interceptors-and-the-servlet-chain.md#flashcards) |
| 40 | Does a filter's `finally` block see the real response status for a container-generated 500? | [Request Filters and Interceptors](../../05-spring/request-filters-interceptors-and-the-servlet-chain.md#flashcards) |

---

## Related

- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
