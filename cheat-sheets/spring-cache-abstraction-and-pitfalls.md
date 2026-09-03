---
title: "Cheat Sheet: Spring Cache Abstraction and Pitfalls"
slug: spring-cache-abstraction-and-pitfalls
document_type: cheat-sheet
domain: spring
topic_id: T-514
canonical: ../handbook/spring/spring-cache-abstraction-and-pitfalls.md
last_updated: 2026-09-01
---

# Spring Cache Abstraction and Pitfalls

**Canonical chapter:** [`syllabus/05-spring/spring-cache-abstraction-and-pitfalls.md`](../syllabus/05-spring/spring-cache-abstraction-and-pitfalls.md)

## Core Mental Model

Spring's cache abstraction is not a language feature — it is proxy-based AOP, identical in mechanism to `@Transactional`: a bean gets wrapped in a CGLIB (or JDK) proxy, and calling a `@Cacheable` method through that proxy is what triggers cache-check-then-store-or-return logic. The cache never copies or protects what it stores — it hands the exact same object reference to every future caller, so whatever the cache doesn't do (defensive copying, forced eviction on write) has to be handled deliberately by surrounding code, or it silently serves wrong data with no error at all.

## Essential Definitions

- **Proxy-mediated, not language-level** — `@Cacheable` only fires when a call passes through the Spring-managed proxy; a self-invocation (`this.method()`) never does.
- **No defensive copying, ever** — the cache stores the exact reference a method returns; a caller mutating it poisons the cache for every future caller.
- **Eviction is the caller's responsibility** — `@Cacheable` has no idea when underlying data changes; a write path without `@CacheEvict`/`@CachePut` leaves the cache silently, indefinitely stale.
- **`-parameters` compiler flag** — required for SpEL key expressions like `#id` to resolve a parameter by name; without it, Spring throws a real `IllegalArgumentException`, not a silent failure.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Does the cached method's return type get mutated anywhere after being returned? | Return an immutable copy, or make the type genuinely immutable |
| Is this method ever called from another method in the same class? | Extract it to a separate bean, or accept it will never be cached from that call site |
| Does any write path modify data a `@Cacheable` method serves? | It must carry a corresponding `@CacheEvict`/`@CachePut` — document this pairing explicitly |
| Does a `@CacheEvict`/`@CachePut` key expression reference `#paramName`? | Compile with `-parameters`, or use positional `#a0`/`#p0` instead |

**Trade-offs:**

| Pitfall | Symptom | Real fix |
|---|---|---|
| Self-invocation | Method looks cached but isn't (measured: 1 vs. 4 real calls) | Call through the proxy — self-inject, or extract to a separate bean |
| Mutable cached value | A caller's mutation corrupts data for every future caller | Return an immutable copy, or use a genuinely immutable type |
| Missing eviction | Reads are silently stale after a write (measured: 100 stays 100 after a real update to 5) | Pair every write path with `@CacheEvict`/`@CachePut` |
| Missing `-parameters` | A real, loud `IllegalArgumentException` at runtime, not build time | Compile with `-parameters`, or use positional SpEL references |

## Key Numbers (real, executed against Spring Framework 6.1.14)

Self-invocation vs. external call — real lookup counts:

```
=== Calling findById() externally, through the real Spring proxy, 3 times ===
Real underlying lookup count: 1 (expected 1 -- cached after the first real call)

=== Calling the SAME logic via self-invocation (this.findById(...)), 3 times ===
Real underlying lookup count: 4 (expected 4 = 1 + 3 -- self-invocation never went through the proxy)
```

Real stale-read result, before and after a real `@CacheEvict` fix:

```
=== Scenario A: UNSAFE -- update path has no @CacheEvict ===
Real read after the update: Product{sku-1, Widget, stock=100}   <!-- should be 5 -->

=== Scenario B: SAFE -- update path has a real @CacheEvict ===
Real read after the update: Product{sku-1, Widget, stock=5}
```

## Common Pitfalls

- Assuming `@Cacheable` "just works" once applied, without considering self-invocation, mutation, or eviction at all.
- Returning a mutable collection or object from a `@Cacheable` method without realizing every caller shares the exact same instance.
- Adding a new write path to data already served by an existing `@Cacheable` method without checking whether that method needs a corresponding eviction — this chapter's own production scenario (an inventory service that quietly oversold stock).
- Assuming `@CacheEvict`'s `key = "#paramName"` will "just work" without the `-parameters` compiler flag.

## Interview Answer Skeleton

**30-sec:** `@Cacheable` is proxy-based AOP, same mechanism as `@Transactional` — self-invocation bypasses it. The cache stores whatever reference a method returns with no defensive copying, so a mutable return value can be silently corrupted by any caller. Eviction is entirely the caller's responsibility.

**2-min:** Add the measured self-invocation proof (1 vs. 4 real calls), the real cache-poisoning mechanism (identical shared reference, no copy), and the "eviction is a contract, not an implementation detail" framing from the production scenario.

**Whiteboard:** Draw a bean box with a proxy ring around it. An external caller's arrow hits the ring (passing through the cache check) before reaching the real bean. A second arrow starting *inside* the ring (self-invocation) goes straight to the real bean, never touching the ring — label it "never cached, no matter how many times you call it."

**Staff-level framing:** Frame cache-eviction pairing as a documented contract responsibility that must survive organizational changes (a different engineer adding a later write path), and connect the self-invocation limitation explicitly to the same mechanism already proven for `@Transactional` — one shared Spring-wide constraint, not a caching-specific quirk.

## Production Warning Signs

- An inventory/stock service quietly overselling a limited-quantity item — check for a write path added later with no corresponding `@CacheEvict`.
- Data appears correct right after a fresh cache (e.g., after a restart) but becomes wrong after a specific write operation — reliably reproducible signature of a missing eviction.
- A bug that "starts" after some unrelated code path runs — suspect cache-object aliasing from a mutable cached value.
- A method decorated with `@Cacheable` still shows a real hit on every call in logs/metrics — check the call site (self-invocation), not the annotation.

## Related

- `syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md`
- `syllabus/05-spring/spring-bean-scopes-and-proxy-modes.md`
- `syllabus/11-system-design/caching-strategies-and-invalidation.md`
