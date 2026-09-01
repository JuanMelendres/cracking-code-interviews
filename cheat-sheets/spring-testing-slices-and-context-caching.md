---
title: "Cheat Sheet: Spring Testing — Slices and Context Caching"
slug: spring-testing-slices-and-context-caching
document_type: cheat-sheet
domain: spring
topic_id: T-517
canonical: ../handbook/spring/spring-testing-slices-and-context-caching.md
last_updated: 2026-09-01
---

# Spring Testing: Slices and Context Caching

**Canonical chapter:** [`handbook/spring/spring-testing-slices-and-context-caching.md`](../handbook/spring/spring-testing-slices-and-context-caching.md)

## Core Mental Model

Every Spring test class carries a *context configuration signature* — `@ContextConfiguration`, active profiles, property sources, and any Boot test-slice annotations, all combined. The TestContext framework caches `ApplicationContext` instances keyed by that signature: two test classes with an identical signature share one context; a different signature means a different cache entry and a real, separate build. A slice annotation (`@WebMvcTest`, `@DataJpaTest`) is really just a narrower signature — it swaps "load everything" for "load only this layer's beans plus supporting infrastructure," at the direct cost that anything outside that layer must be explicitly mocked.

## Essential Definitions

- **Context caching** — the TestContext framework's default behavior of reusing an already-built `ApplicationContext` across test classes sharing an identical configuration signature.
- **`@DirtiesContext`** — evicts a cache entry *after* the annotated class finishes; it still benefits from a cache hit while running. The cost is paid by whichever *later* test needs that same configuration.
- **Slice test** (`@WebMvcTest`, `@DataJpaTest`, `@JsonTest`) — a Spring Boot Test feature loading a real, but deliberately narrow, context for one architectural layer plus its supporting auto-configuration.
- **`@MockBean`** — supplies a Mockito stand-in for a real collaborator the slice's context doesn't include; not a workaround, an explicit acknowledgment of what the slice excludes.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Does the test need to verify one architectural layer's behavior in isolation? | A slice test (`@WebMvcTest`/`@DataJpaTest`) with `@MockBean` for its dependencies |
| Does the test need to verify real, end-to-end wiring across layers? | `@SpringBootTest` |
| Is a test genuinely mutating shared application state that would corrupt later tests? | `@DirtiesContext` — but only on that one class, never a shared base class |
| Is CI test-suite runtime growing without a corresponding new slow dependency? | Audit for `@DirtiesContext` on shared base classes and configuration drift |

**Trade-offs:**

| Approach | What's loaded | Speed | Needs `@MockBean`? |
|---|---|---|---|
| `@SpringBootTest` | Full application context | Slowest to build (once, then cached) | No — the real bean is present |
| `@WebMvcTest` | Web layer + MVC infrastructure only | Fast to build | Yes, for any non-web-layer collaborator |
| `@DataJpaTest` | JPA/repository layer + embedded DB config only | Fast to build | Yes, for any non-persistence-layer collaborator |

## Key Numbers (real, executed against Spring Framework 6.1.14 + Spring Boot 3.3.5)

Real, decisive context-caching result:

```
=== TestClassA: first class to use this @ContextConfiguration ===
Real contexts created so far: 1

=== TestClassB: identical @ContextConfiguration ===
Real contexts created so far: 1 (reused from the cache)

=== TestClassC: identical config, but annotated @DirtiesContext ===
Real contexts created so far: 1 (reused while running, dirtied on the way out)

=== TestClassD: identical config, run AFTER C dirtied the cache ===
Real contexts created so far: 2 (a real, fresh context was rebuilt)
```

Real slice-test failure and fix:

```
org.springframework.beans.factory.NoSuchBeanDefinitionException:
No qualifying bean of type 'demo.GreetingService' available
```

Fixed with `@MockBean private GreetingService greetingService;` inside a `@WebMvcTest`.

## Common Pitfalls

- Reaching for `@SpringBootTest` by default for every test, paying its full context cost for tests that only need one layer.
- Adding `@DirtiesContext` to silence a flaky test without checking whether the real cause is mutated shared/static state — the chapter's own production scenario (a `@DirtiesContext` on a shared base class tripled CI runtime, 4 min → 12 min).
- Placing Boot test classes in the default Java package — `IllegalStateException: Unable to find a @SpringBootConfiguration` (Boot's config-class search walks up from the test's own package).
- The `-parameters` compiler flag requirement recurring here for `@RequestParam`/`@PathVariable` resolution — the identical root cause already seen for SpEL cache keys in Spring Cache Abstraction.

## Interview Answer Skeleton

**30-sec:** Spring's TestContext framework caches `ApplicationContext` instances by their full configuration signature; identical test classes share one context. `@DirtiesContext` evicts it afterward, forcing a rebuild for whoever's next. Slice tests load a narrower context for one layer, so anything outside needs `@MockBean`.

**2-min:** Add the measured cache-reuse-then-eviction sequence (counter 1 → still 1 → still 1 → 2) and the real slice-test failure/fix (`NoSuchBeanDefinitionException` → `@MockBean`), contrasted directly against `@SpringBootTest` where the same collaborator is genuinely present.

**Whiteboard:** Draw a "context cache" box keyed by configuration signature. Two identical-signature test classes point into the same cache entry. A third, `@DirtiesContext`-tagged class leaves an arrow "evicted here" afterward; a fourth identical-signature class is forced into a brand-new entry. Separately: a "full context" circle with a smaller "web slice" circle inside it, a service bean sitting outside the smaller circle — label the gap "needs @MockBean."

**Staff-level framing:** Connect `@DirtiesContext` misuse to suite-wide CI cost (as in the production scenario), and frame slice-vs-full-context choice as an explicit test-strategy decision with a real speed/realism trade-off — not a default habit.

## Production Warning Signs

- CI test-suite runtime growing 3x after a change that touched only one integration test class — audit for `@DirtiesContext` added to a widely-shared base test class.
- A slice test throwing `NoSuchBeanDefinitionException`/`UnsatisfiedDependencyException` — the fix is `@MockBean`, not switching to `@SpringBootTest`.
- `IllegalArgumentException: Name for argument ... not specified ... use the '-parameters' flag` — recurring across SpEL keys and MVC argument resolution, one shared root cause.

## Related

- `handbook/spring/auto-configuration-and-bean-lifecycle.md`
- `handbook/spring/spring-cache-abstraction-and-pitfalls.md`
- `handbook/testing/integration-testing-against-real-dependencies.md`
