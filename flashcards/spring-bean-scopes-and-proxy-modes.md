---
title: "Flashcards: Spring Bean Scopes and Proxy Modes"
slug: spring-bean-scopes-and-proxy-modes
document_type: flashcard-deck
domain: spring
topic_id: T-502
canonical: ../handbook/spring/spring-bean-scopes-and-proxy-modes.md
last_updated: 2026-09-01
---

# Flashcards: Spring Bean Scopes and Proxy Modes

**Canonical chapter:** [`handbook/spring/spring-bean-scopes-and-proxy-modes.md`](../handbook/spring/spring-bean-scopes-and-proxy-modes.md)

## Card: Why does a prototype bean behave like a singleton once injected into one?

**Prompt:**
You inject a `prototype`-scoped bean by plain constructor reference into a
`singleton`-scoped service. Why does every call see the same instance?

**Answer:**
Scope resolution happens once, when the singleton is constructed — the container
is asked for the prototype dependency exactly once, so "new instance every time"
never gets a second chance to run. Measured directly: `Greeter#1` returned on
three consecutive calls.

**Why it matters:**
It's a real, silent production bug class — no exception, no log line, just quietly
wrong shared state.

**Common trap:**
Assuming the bean's own `@Scope` annotation alone guarantees correct runtime
behavior regardless of injection site.

**Related:**
[handbook/spring/spring-bean-scopes-and-proxy-modes.md](../handbook/spring/spring-bean-scopes-and-proxy-modes.md), [Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation](../handbook/spring/transactional-proxy-mechanics-and-propagation.md)

## Card: What's actually injected with proxyMode = ScopedProxyMode.TARGET_CLASS?

**Prompt:**
What object does the injection site actually hold when a bean is declared with a
scoped proxy?

**Answer:**
A CGLIB (or JDK, for interfaces) proxy, not the real bean — every method call on
it is delegated through the bean's `Scope` implementation, which resolves the
current, correctly-scoped real instance on every single call. Measured directly:
three consecutive calls returned `Greeter#2`, `#3`, `#4` — a fresh instance every
time.

**Why it matters:**
Same proxy-delegation mechanism already proven for `@Transactional`/`@Cacheable`
— understanding one explains all three.

**Common trap:**
Believing the injected reference *is* the real bean, rather than a delegating
stand-in — which also means a direct field read on it won't see the real target's
state.

**Related:**
[handbook/spring/spring-bean-scopes-and-proxy-modes.md](../handbook/spring/spring-bean-scopes-and-proxy-modes.md), [Spring Cache Abstraction and Pitfalls](../handbook/spring/spring-cache-abstraction-and-pitfalls.md)

## Card: How do request/session scope actually work, mechanically?

**Prompt:**
Without a servlet container, how could you prove the mechanism behind
`@RequestScope`/`@SessionScope`?

**Answer:**
Register a custom `Scope` implementation via `CustomScopeConfigurer` (as a
`static` `@Bean`, since it's a `BeanFactoryPostProcessor`) — the same registry-
keyed-by-a-boundary mechanism request/session scope use, just keyed by a thread
instead of a request/session. Measured directly with Spring's own
`SimpleThreadScope`: the same thread got the same instance twice; a different
thread got a genuinely different one.

**Why it matters:**
Demystifies request/session scope as "just another `Scope` implementation," not
special-cased servlet magic.

**Common trap:**
Assuming request/session scope require understanding servlet-container internals
to reason about correctly.

**Related:**
[handbook/spring/spring-bean-scopes-and-proxy-modes.md](../handbook/spring/spring-bean-scopes-and-proxy-modes.md)
