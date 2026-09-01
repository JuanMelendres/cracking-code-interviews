---
title: "Flashcards: Spring Cache Abstraction and Pitfalls"
slug: spring-cache-abstraction-and-pitfalls
document_type: flashcard-deck
domain: spring
topic_id: T-514
canonical: ../handbook/spring/spring-cache-abstraction-and-pitfalls.md
last_updated: 2026-09-01
---

# Flashcards: Spring Cache Abstraction and Pitfalls

**Canonical chapter:** [`handbook/spring/spring-cache-abstraction-and-pitfalls.md`](../handbook/spring/spring-cache-abstraction-and-pitfalls.md)

## Card: Why does self-invocation break @Cacheable?

**Prompt:**
Why doesn't `@Cacheable` take effect when a method calls another `@Cacheable`
method on `this` from within the same class?

**Answer:**
`@Cacheable` only applies via Spring's proxy for the bean; a self-invocation never
passes through that proxy, so the caching advice never runs — the identical
mechanism already proven for `@Transactional`. Measured directly: 1 real call
externally (cached), 4 real calls via self-invocation (never cached).

**Why it matters:**
It's a shared, Spring-wide proxy-based-AOP constraint, not a caching-specific quirk
— understanding it once explains it everywhere.

**Common trap:**
Assuming the annotation is broken rather than understanding the proxy mechanism.

**Related:**
[handbook/spring/spring-cache-abstraction-and-pitfalls.md](../handbook/spring/spring-cache-abstraction-and-pitfalls.md), [Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation](../handbook/spring/transactional-proxy-mechanics-and-propagation.md)

## Card: Does the cache protect against mutation?

**Prompt:**
If a `@Cacheable` method returns a mutable `List`, and a caller mutates it, what
happens to the cache?

**Answer:**
It's silently poisoned — the cache stores the exact object reference returned, with
no defensive copy, so every future caller receives the same, now-corrupted
instance. Measured directly: a caller's mutation was visible to a second, unrelated
caller on the next cache hit.

**Why it matters:**
This is a real, silent, hard-to-trace bug class with no exception or log line at
the moment of corruption.

**Common trap:**
Assuming the cache isolates each caller's own copy of the returned value.

**Related:**
[handbook/spring/spring-cache-abstraction-and-pitfalls.md](../handbook/spring/spring-cache-abstraction-and-pitfalls.md)

## Card: What does -parameters have to do with caching?

**Prompt:**
Why does `@CacheEvict(key = "#id")` sometimes throw `IllegalArgumentException: Null key returned`?

**Answer:**
SpEL resolves `#id` by looking up that parameter's name in the compiled class file —
which only exists if compiled with the `-parameters` flag. Without it, Spring can't
resolve the named reference and fails loudly rather than guessing.

**Why it matters:**
A real, honest discovery made while building this chapter's own demos — an easy,
common gotcha for any team adopting SpEL-keyed cache annotations.

**Common trap:**
Assuming named SpEL parameter references work automatically with any Java
compilation.

**Related:**
[handbook/spring/spring-cache-abstraction-and-pitfalls.md](../handbook/spring/spring-cache-abstraction-and-pitfalls.md)
