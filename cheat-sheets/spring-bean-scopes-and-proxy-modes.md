---
title: "Cheat Sheet: Spring Bean Scopes and Proxy Modes"
slug: spring-bean-scopes-and-proxy-modes
document_type: cheat-sheet
domain: spring
topic_id: T-502
canonical: ../handbook/spring/spring-bean-scopes-and-proxy-modes.md
last_updated: 2026-09-01
---

# Spring Bean Scopes and Proxy Modes

**Canonical chapter:** [`handbook/spring/spring-bean-scopes-and-proxy-modes.md`](../handbook/spring/spring-bean-scopes-and-proxy-modes.md)

## Core Mental Model

A bean's scope answers one question: when the container is asked for this bean, does it return the same instance as last time, or a new one? Scope is a property of *how the container creates instances*, not something a normal field reference automatically respects. A singleton that captures a prototype dependency by ordinary constructor injection asks the container for that dependency exactly once — at the singleton's own construction — so "new instance every time" never gets a second chance to run. A scoped proxy fixes this by making the *reference itself* re-ask the container on every method call.

## Essential Definitions

- **Singleton** (default) — one instance per container. **Prototype** — new instance per `getBean()`/injection point. **Request/session** — one instance per HTTP request/session, needs a web-aware `ApplicationContext`.
- **Scoped proxy** (`@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS/INTERFACES)`) — a CGLIB/JDK proxy that re-resolves the real target from its scope on every method call, instead of holding one instance forever.
- **`ObjectProvider<T>`** — a proxy-free alternative; call `.getObject()` at the point of use to force fresh scope resolution.
- **Custom `Scope`** — any lifecycle boundary, registered via a **static** `CustomScopeConfigurer` `@Bean` method (must be static because it's a `BeanFactoryPostProcessor`, which must run before any other bean).

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Is a prototype/request/session-scoped bean injected into a singleton (or wider)? | Use a scoped proxy or `ObjectProvider<T>` — never plain injection |
| Does the injection site need to treat the dependency as a normal typed reference? | Scoped proxy — presents the same type as the real bean |
| Is the injection site a single, local call site? | `ObjectProvider<T>` — avoids proxy/CGLIB caveats entirely |
| Does the scope boundary need to be something other than a request/session (a job, a tenant, a thread)? | Register a custom `Scope` via `CustomScopeConfigurer` |

**Trade-offs:**

| Approach | Re-resolves per call? | Proxy involved? | Injected type |
|---|---|---|---|
| Plain injection (the bug) | No — resolved once | No | The real bean type |
| Scoped proxy (`TARGET_CLASS`) | Yes | Yes (CGLIB/JDK) | The real bean type (proxied) |
| `ObjectProvider<T>` | Yes | No | `ObjectProvider<T>` |

## Key Numbers (real, executed against Spring Framework 6.1.14)

The real bug — a prototype injected directly into a singleton — and two real, independent fixes:

```
=== BUGGY: prototype Greeter injected directly (by reference) into a singleton ===
call 1: Greeter#1
call 2: Greeter#1
call 3: Greeter#1

=== FIXED (scoped proxy, proxyMode = TARGET_CLASS) ===
call 1: Greeter#2
call 2: Greeter#3
call 3: Greeter#4

=== FIXED (ObjectProvider<Greeter>.getObject()) ===
call 1: Greeter#5
call 2: Greeter#6
call 3: Greeter#7
```

Real custom-scope proof — request/session's mechanism, without a servlet container:

```
=== Same thread, two calls to a 'thread'-scoped bean ===
t1a == t1b: true  (Greeter#2 vs Greeter#2)

=== A different thread, same bean name ===
t1a == fromOtherThread: false  (Greeter#2 vs Greeter#3)
```

## Common Pitfalls

- Assuming a bean's declared scope alone guarantees correct behavior, regardless of how or where it's injected.
- Injecting a prototype/request/session-scoped bean into a singleton without a scoped proxy or `ObjectProvider`, and not noticing because nothing throws an exception.
- Forgetting a `CustomScopeConfigurer` `@Bean` method must be `static`, hitting confusing `BeanFactoryPostProcessor` ordering issues.
- A direct field read on a scoped-proxy reference doesn't see the real target's state — only method calls are delegated (same CGLIB/Objenesis caveat as `@Transactional`/`@Cacheable`).

## Interview Answer Skeleton

**30-sec:** Singleton and prototype work everywhere; request/session need a web-aware context. Scope is resolved when the container creates the instance, not on every field access — injecting a prototype bean directly into a singleton captures exactly one instance forever. Fix: a scoped proxy or `ObjectProvider<T>`.

**2-min:** Add the measured bug (`Greeter#1` three times) and both measured fixes (`#2,#3,#4` via proxy; `#5,#6,#7` via `ObjectProvider`), plus that request/session scope is "just another `Scope` implementation" — proven with a real custom `"thread"` scope needing no servlet container.

**Whiteboard:** Draw a singleton box with one arrow going once, at startup, into "resolve dependency" that returns one instance forever — label it "happens exactly once." Draw a scoped-proxy box where every call first hits a proxy ring, which asks a "scope registry" for the current instance on every call — label it "happens on every call."

**Staff-level framing:** Connect scoped proxies to the same proxy-based-AOP family as `@Transactional`/`@Cacheable` — one mechanism, three uses. Frame scope-mismatched injection as a structural, silent bug class (no exception, no log line) that needs static analysis or code-review discipline, since testing alone (especially single-threaded testing) won't reveal it.

## Production Warning Signs

- Log lines under production load occasionally carrying the wrong request's correlation ID, only under concurrency, never reproducing in local single-request testing — check for a `@RequestScope` bean injected as a plain singleton field.
- `IllegalStateException: No thread-bound request found` — requesting request/session scope outside a web-aware context.
- `ScopedProxyMode.NO` (the default) accepted silently on a mismatched-scope injection — no runtime exception at all, purely structural.

## Related

- `handbook/spring/transactional-proxy-mechanics-and-propagation.md`
- `handbook/spring/spring-cache-abstraction-and-pitfalls.md`
- `handbook/spring/spring-testing-slices-and-context-caching.md`
