---
title: "Cheat Sheet: Reflection and Dynamic Proxies"
slug: reflection-and-dynamic-proxies
document_type: cheat-sheet
domain: java-core
topic_id: T-113
canonical: ../handbook/java-core/reflection-and-dynamic-proxies.md
last_updated: 2026-09-02
---

# Reflection and Dynamic Proxies

**Canonical chapter:** [`handbook/java-core/reflection-and-dynamic-proxies.md`](../handbook/java-core/reflection-and-dynamic-proxies.md)

## Core Mental Model

Reflection lets code ask an object "what are you, and what can you do?" at runtime instead of the compiler answering once at compile time. A dynamic proxy synthesizes an entirely new class on the fly that intercepts every answer before it's returned. Every reflective call pays a real cost for that flexibility.

## Essential Definitions

- **Reflection** — `Class`/`Method`/`Field` introspection and invocation, bypassing compile-time type checking and (with `setAccessible(true)`) access control.
- **`java.lang.reflect.Proxy`** — synthesizes a runtime class (`$ProxyN`) implementing given interfaces, routing every call through one `InvocationHandler`; can only proxy interfaces.
- **`MethodHandle`** — the modern (`java.lang.invoke`) alternative to classic reflection, still slower than a direct call but faster than `Method.invoke()`.
- **Nestmate access (JEP 181)** — since Java 11, private access between nested classes of the same top-level class needs no `setAccessible(true)` at all.

## Decision Table

| Question | Answer |
|---|---|
| Need to operate generically on types unknown at compile time? | Reflection — but measure real cost on the actual hot path |
| Performance-sensitive repeated invocation of a reflectively-discovered method? | Prefer `MethodHandle` over classic `Method.invoke()` |
| Does the target type implement at least one interface? | JDK `Proxy` is sufficient; if not, CGLIB/ByteBuddy (subclass-based) is required |
| Reflectively accessing a private member across a genuine class boundary? | `setAccessible(true)` needed — unless it's a nestmate-access case |

## Key Numbers

- 200,000,000 calls: direct 50ms, `Method.invoke()` 937ms (~18.7x slower), `MethodHandle.invoke()` 357ms (~7.1x slower than direct, ~2.5-2.6x faster than classic reflection).
- Proxying a concrete class throws a real `IllegalArgumentException` — interfaces only.

## Common Pitfalls

- Assuming reflection's cost is negligible without measuring — real ~18.7x slowdown for classic `Method.invoke()`.
- Attempting to proxy a concrete class with `java.lang.reflect.Proxy` — real `IllegalArgumentException`.
- Calling an annotated method (`@Transactional`, `@Async`) on `this` inside a Spring bean and expecting proxy behavior — self-invocation bypasses the proxy entirely.
- Assuming `setAccessible(true)` is always required for private-member access — nestmate access (JEP 181) may make it unnecessary.

## Interview Answer Skeleton

**30-sec:** Reflection introspects/invokes members at runtime with a real cost (~18.7x slower than a direct call for `Method.invoke()`, ~7.1x for `MethodHandle`). A dynamic proxy synthesizes a class at runtime implementing given interfaces, routing calls through an `InvocationHandler` — exactly Spring's interface-based AOP — but it can only proxy interfaces, the real reason CGLIB/ByteBuddy exist.

**2-min:** Add the real `$ProxyN` synthesis proof (`instanceof` the interface, NOT `instanceof` the real implementation) and the self-invocation gotcha: `this.method()` inside a bean bypasses the proxy entirely, silently defeating `@Transactional`.

**Whiteboard:** Caller → synthesized proxy → `InvocationHandler` → real delegate → back up the chain. Annotate the handler box "logging/transactions/security get inserted here." Draw a second arrow from "real object" to itself labeled "self-invocation (this.method()) — bypasses the proxy."

**Staff-level framing:** Any framework feature implemented via a wrapper/interception layer only intercepts calls that genuinely cross that layer's boundary — self-invocation, direct field access, package-private shortcuts silently skip it. Design code structure to make the boundary explicit rather than relying on every contributor to remember.

## Production Warning Signs

- `@Transactional` silently doesn't work on a self-invoked method (`this.otherMethod()`) — the call never routes through the proxy's `InvocationHandler`. Fix: split into separate beans, or `AopContext.currentProxy()`.

## Related

- `handbook/concurrency/completablefuture-and-async-composition.md`
- `handbook/java-core/classloaders-and-class-initialization.md`
- `handbook/java-core/annotations-and-annotation-processing.md`
