---
title: "Flashcards: Design Patterns Catalog Beyond the Core Four"
slug: design-patterns-catalog-beyond-the-core-four
document_type: flashcard-deck
domain: 04-software-design
topic_id: T-2432
canonical: ../syllabus/04-software-design/design-patterns-catalog-beyond-the-core-four.md
last_updated: 2026-09-28
---

# Flashcards: Design Patterns Catalog Beyond the Core Four

**Canonical chapter:** [`syllabus/04-software-design/design-patterns-catalog-beyond-the-core-four.md`](../syllabus/04-software-design/design-patterns-catalog-beyond-the-core-four.md)

## Card: Adapter vs. Facade vs. Proxy

**Prompt:**
All three sit in front of other objects. What distinguishes them?

**Answer:**
What happens to the interface. Adapter **changes** it (client wants B, the existing class speaks A). Facade **shrinks** it (many subsystem interfaces reduced to one). Proxy keeps it **identical** and controls access to the target — which is precisely what allows transparent insertion, and why Proxy is the mechanism behind `@Transactional` and AOP.

**Why it matters:**
It is the single most common pattern-comparison question, and the identical-interface property is the part that explains framework behavior.

**Common trap:**
Conflating Proxy and Decorator. Both preserve the interface, but a Decorator is composed deliberately by the caller to add optional behavior, while a Proxy controls access and is usually inserted without the caller knowing.

**Related:**
[Transactional Proxy Mechanics and Propagation](../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md)

## Card: Factory Method vs. Abstract Factory

**Prompt:**
What is the actual difference, and what does the more complex one buy you?

**Answer:**
A factory method produces **one** kind of product from a key. An abstract factory produces a **matched family**, so combining a German tax rule with American invoice wording becomes unrepresentable rather than merely discouraged. The cost: adding a new product *kind* to the family changes the factory interface and every implementation.

**Why it matters:**
Using an abstract factory for a single product type adds an interface for nothing.

**Common trap:**
Returning `null` for an unknown key. A factory needs a defined failure — the demo throws `IllegalArgumentException: Unknown payment provider: paypal`.

**Related:**
[Design Patterns Applied](../syllabus/04-software-design/design-patterns-applied.md)

## Card: The Observer failure that throws nothing

**Prompt:**
A listener unsubscribes itself during dispatch over an `ArrayList`. What happens?

**Answer:**
Measured directly: **no exception at all**, and a later listener was silently skipped. `hasNext()` is just `cursor != size`; removing the second of three elements made them equal, so iteration ended before the `modCount` check in `next()` could fire. A `CopyOnWriteArrayList` listener store runs every listener with no exception.

**Why it matters:**
A silently skipped subscriber is strictly worse than the `ConcurrentModificationException` most people predict, and it means fail-fast iteration cannot be relied on to surface listener bugs.

**Common trap:**
Asserting that mid-dispatch modification always throws.

**Related:**
[Fail-Fast vs. Weakly Consistent Iterators](../syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md)

## Card: Hardening Observer dispatch

**Prompt:**
One listener throws and later listeners stop running. What are the two fixes, and how much code are they?

**Answer:**
Four lines: iterate a snapshot (`List.copyOf(listeners)`) so subscription changes during dispatch are safe, and wrap each callback in try/catch so one failure cannot starve the rest, collecting failures for a per-listener metric. Measured: the naive loop skipped the third listener; the hardened one ran both survivors and reported the failure.

**Why it matters:**
Without isolation, registration order silently decides which subscribers survive an unrelated failure — a real production outage shape.

**Common trap:**
Wrapping the whole dispatch loop in one try/catch, which stops the crash but still skips listeners.

**Related:**
[Event-Driven Architecture Integration Styles](../syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md)

## Card: JDK dynamic proxy mechanics

**Prompt:**
What does `Proxy.newProxyInstance` actually produce, and what are its three consequences?

**Answer:**
A class generated at runtime implementing the given interfaces, routing every call to `InvocationHandler.invoke` — the demo prints its real name, `$Proxy0`. Consequences: (1) interfaces only, so concrete classes need CGLIB subclass proxies and `final` classes/methods cannot be proxied at all; (2) interception is uniform across methods, so selectivity must be coded; (3) `method.invoke` wraps target exceptions in `InvocationTargetException`, so the handler must unwrap the cause or callers see the wrong exception type.

**Why it matters:**
It is the direct answer to "how does `@Transactional` work?" and the unwrapping detail is one of the most common real bugs in hand-written proxies.

**Common trap:**
Adding retries uniformly with no idempotency requirement, no backoff, and no circuit breaker — which turns a 30-second outage into a sustained one.

**Related:**
[Resilience Patterns](../syllabus/11-system-design/resilience-patterns.md)
