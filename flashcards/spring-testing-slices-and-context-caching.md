---
title: "Flashcards: Spring Testing: Slices and Context Caching"
slug: spring-testing-slices-and-context-caching
document_type: flashcard-deck
domain: spring
topic_id: T-517
canonical: ../handbook/spring/spring-testing-slices-and-context-caching.md
last_updated: 2026-09-01
---

# Flashcards: Spring Testing: Slices and Context Caching

**Canonical chapter:** [`handbook/spring/spring-testing-slices-and-context-caching.md`](../handbook/spring/spring-testing-slices-and-context-caching.md)

## Card: What does @DirtiesContext actually cost, and who pays it?

**Prompt:**
`@DirtiesContext` is added to one test class. Who actually pays the cost of
that annotation?

**Answer:**
Every other test class sharing that same configuration signature — the
annotated class still gets a cache hit while running, but the eviction
afterward forces the *next* class using that configuration to rebuild from
scratch. Measured directly: a context-creation counter went from 1 to 2 only
after the `@DirtiesContext`-annotated class finished and a later,
identically-configured class ran.

**Why it matters:**
It's a real, measured cause of suite-wide CI slowdowns from a single-class
change — the cost is not local to where the annotation is written.

**Common trap:**
Assuming `@DirtiesContext` only affects the test class it's declared on.

**Related:**
[handbook/spring/spring-testing-slices-and-context-caching.md](../handbook/spring/spring-testing-slices-and-context-caching.md)

## Card: Why does a slice test need @MockBean for a service outside its slice?

**Prompt:**
A `@WebMvcTest` controller test throws `NoSuchBeanDefinitionException` for a
`@Service` the controller depends on. Why?

**Answer:**
`@WebMvcTest` loads only the web layer's real beans plus MVC infrastructure —
`@Service`/`@Repository` beans are genuinely not part of that context.
`@MockBean` supplies a Mockito stand-in to satisfy the dependency. Measured
directly: the real exception before the fix, and the passing test after adding
`@MockBean`.

**Why it matters:**
It proves slice tests build a genuinely reduced real context, not a full
context with some hidden auto-mocking layer.

**Common trap:**
Believing `@WebMvcTest` somehow makes every dependency automatically
satisfiable.

**Related:**
[handbook/spring/spring-testing-slices-and-context-caching.md](../handbook/spring/spring-testing-slices-and-context-caching.md), [Spring Cache Abstraction and Pitfalls](../handbook/spring/spring-cache-abstraction-and-pitfalls.md)

## Card: Why does @RequestParam String name sometimes throw an IllegalArgumentException about -parameters?

**Prompt:**
`@RequestParam String name` (no explicit `name` attribute) throws
`IllegalArgumentException: Name for argument of type [java.lang.String] not
specified`. Why?

**Answer:**
Spring MVC resolves the parameter's name via compiled bytecode metadata that
only exists if compiled with `-parameters` — the identical root cause already
seen for `@CacheEvict`'s SpEL named keys, just consumed by a different resolver
(`AbstractNamedValueMethodArgumentResolver` instead of SpEL).

**Why it matters:**
Recognizing this as one shared root cause recurring across unrelated Spring
subsystems is a real signal of understanding internals rather than memorizing
isolated gotchas.

**Common trap:**
Treating this as an unrelated, new bug rather than the same compiler-flag issue
already known from caching.

**Related:**
[handbook/spring/spring-testing-slices-and-context-caching.md](../handbook/spring/spring-testing-slices-and-context-caching.md), [Spring Cache Abstraction and Pitfalls](../handbook/spring/spring-cache-abstraction-and-pitfalls.md)
