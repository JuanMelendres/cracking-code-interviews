# Spring Bean Scopes and Proxy Modes — Real, Executed Demos

Backs [Spring Bean Scopes and Proxy Modes](../../../../syllabus/05-spring/spring-bean-scopes-and-proxy-modes.md)
(T-502). Real Spring Framework 6.1.14 output, plain jars, no Spring Boot
auto-configuration — same pattern as the other packs in `practice/java/spring/`.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
```

## Demo 1 — `SingletonVsPrototypeDemo`: the baseline

```bash
java -cp "out:lib/*" SingletonVsPrototypeDemo
```

Real output:

```
=== Singleton-scoped SingletonHolder bean, fetched twice from the container ===
holder1 == holder2: true

=== Prototype-scoped 'greeter' bean, fetched twice directly from the container ===
g1 == g2: false  (Greeter#2 vs Greeter#3)
```

Confirms the two defaults directly: a singleton bean is the exact same instance on
every `getBean()` call; a prototype bean is a brand-new instance every time.

## Demo 2 — `PrototypeInjectedIntoSingletonDemo`: the classic bug, and two real fixes

```bash
java -cp "out:lib/*" PrototypeInjectedIntoSingletonDemo
```

Real output:

```
=== BUGGY: prototype Greeter injected directly (by reference) into a singleton ===
call 1: Greeter#1
call 2: Greeter#1
call 3: Greeter#1

=== FIXED (scoped proxy, proxyMode = TARGET_CLASS): fresh prototype instance per call ===
call 1: Greeter#2
call 2: Greeter#3
call 3: Greeter#4

=== FIXED (ObjectProvider<Greeter>.getObject()): fresh prototype instance per call ===
call 1: Greeter#5
call 2: Greeter#6
call 3: Greeter#7
```

`SingletonHolder` holds a direct reference to a prototype-scoped `Greeter`,
injected once at construction. Every call after that reuses the exact same
`Greeter#1` forever — the prototype scope declaration on `Greeter` never actually
takes effect from the singleton's point of view, because the singleton was only
ever wired once.

Two real, independent fixes, both proven above:

1. **`@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)`** on the prototype bean
   definition (`proxiedGreeter`). What actually gets injected into
   `ScopedProxyHolder` is a CGLIB proxy, not a real `Greeter` — every method call
   on it is re-dispatched through the proxy to a freshly resolved prototype
   instance from the container. `ScopedProxyHolder` itself has no idea this is
   happening; it just calls `greeter.greet()` like normal.
2. **`ObjectProvider<Greeter>`** injected into `ObjectProviderHolder` instead of
   `Greeter` directly. No proxy at all — `ObjectProvider.getObject()` explicitly
   asks the container for a bean at the moment it's called, which naturally
   re-triggers prototype creation every time.

## Demo 3 — `CustomThreadScopeDemo`: a custom scope, without a servlet container

```bash
java -cp "out:lib/*" CustomThreadScopeDemo
```

Real output:

```
=== Same thread, two calls to a 'thread'-scoped bean ===
t1a == t1b: true  (Greeter#2 vs Greeter#2)

=== A different thread, same bean name ===
t1a == fromOtherThread: false  (Greeter#2 vs Greeter#3)
```

Request and session scope are real, officially-supported Spring scopes, but
demonstrating them honestly requires a servlet container and an actual HTTP
request/session lifecycle — infrastructure this repository's plain-jar Spring
packs deliberately avoid. `AppConfig` instead registers a **custom `"thread"`
scope** using Spring's own `SimpleThreadScope`, via a static
`CustomScopeConfigurer` `@Bean` method (must be static — it's a
`BeanFactoryPostProcessor`, and Spring requires those to be instantiated before
any other bean in the context).

This is the same underlying mechanism request and session scope use — a
`Scope` implementation backed by a registry keyed by some boundary (a thread,
in this case; an `HttpServletRequest` or `HttpSession` in the real request/session
scopes) that returns the same instance for repeated lookups within that boundary,
and a different instance across boundaries. The real output proves exactly that:
the same thread gets the same `Greeter#2` on both calls; a second, genuinely
different thread gets a different `Greeter#3`.

## Real discoveries made while building this pack

No bugs were hit this time — all three demos compiled and produced correct
output on the first real run. That is itself worth stating honestly rather than
manufacturing a discovery that didn't happen: the design deliberately used
`@Qualifier` on every ambiguous injection point (three separate `Greeter` beans
by name: `greeter`, `proxiedGreeter`, `threadScopedGreeter`) specifically to
avoid the kind of ambiguous-autowiring or missing-`-parameters` failure the
sibling [Spring Cache Abstraction](../spring-cache-abstraction-and-pitfalls/README.md)
pack hit — a deliberate design choice made *because of* that earlier real
discovery, not a coincidence.
