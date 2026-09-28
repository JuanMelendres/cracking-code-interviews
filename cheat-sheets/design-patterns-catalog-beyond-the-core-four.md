---
title: "Cheat Sheet: Design Patterns Catalog Beyond the Core Four"
slug: design-patterns-catalog-beyond-the-core-four
document_type: cheat-sheet
domain: 04-software-design
topic_id: T-2432
canonical: ../syllabus/04-software-design/design-patterns-catalog-beyond-the-core-four.md
last_updated: 2026-09-28
---

# Design Patterns Catalog Beyond the Core Four

**Canonical chapter:** [`syllabus/04-software-design/design-patterns-catalog-beyond-the-core-four.md`](../syllabus/04-software-design/design-patterns-catalog-beyond-the-core-four.md)
**Companion:** [Design Patterns Applied](../syllabus/04-software-design/design-patterns-applied.md) — Strategy, Builder, Decorator, Singleton.

## Core Mental Model

Name what varies, and the pattern names itself. Which concrete type → factories. What shape the outside speaks → Adapter. What happens around a call → Proxy. How many steps a caller must know → Facade. Who cares about an event → Observer. When an action runs → Command. Which handler applies → Chain of Responsibility. The steps but not their order → Template Method.

## The Three Confusable Wrappers

| Pattern | What happens to the interface |
|---|---|
| Adapter | **Changed** — client wants B, existing class speaks A |
| Facade | **Shrunk** — many subsystem interfaces reduced to one |
| Proxy | **Identical** — which is what allows transparent insertion (AOP, `@Transactional`) |

Decorator also keeps the interface, but the caller composes it deliberately to add optional behavior; a Proxy controls access and is usually inserted without the caller knowing.

## One Line Each

| Pattern | Solves |
|---|---|
| Factory Method | Choosing one implementation without naming it at the call site |
| Abstract Factory | Producing a **matched family** so a mismatched combination is unrepresentable |
| Adapter | Making an unchangeable external type fit your interface |
| Proxy | Adding cross-cutting behavior invisibly (retry, cache, transaction, lazy load) |
| Facade | One entry point over a mandatory multi-step orchestration |
| Observer | Event fan-out where the publisher does not know the subscribers |
| Command | Actions as objects, so they can be queued, logged, scheduled, undone |
| Chain of Responsibility | An ordered pipeline where each handler handles or forwards |
| Template Method | Fixing the sequence (`final`) while varying the steps |

## Failure Mode Table

| Pattern | How it fails in production |
|---|---|
| Factory Method | God-switch; `null` for unknown keys |
| Abstract Factory | A new product kind edits every factory |
| Adapter | Adapter chains nobody can read |
| Proxy | Self-invocation bypass; unreadable stack traces; retry amplification |
| Facade | Grows into a god object |
| Observer | Throwing listener starves the rest; mid-dispatch unsubscribe; listeners never removed |
| Command | Unbounded undo history; stale captured state |
| Chain of Responsibility | Silent fall-through; order changes with no test |
| Template Method | Inheritance coupling; deep hierarchies |

## Measured Facts

- A JDK dynamic proxy retried a flaky client to success on attempt 3, real runtime class `$Proxy0`, with zero retry code in the implementation.
- A throwing listener in the middle of a naive dispatch loop meant the third listener never ran.
- A listener unsubscribing itself mid-dispatch threw **nothing** — `hasNext()` is `cursor != size`, and removing the second of three made them equal, so the third listener was **silently skipped**.

## Already in Your Stack

`@Transactional`, `@Cacheable`, `@Async` → Proxy. Servlet/Spring Security filter chains → Chain of Responsibility. `ApplicationEventPublisher`, `@EventListener` → Observer. `Runnable`/`Callable` → Command. `JdbcTemplate` → Facade + Template Method. `Arrays.asList`, `InputStreamReader` → Adapter.

## Common Pitfalls

- Listing patterns with no failure analysis.
- Calling every wrapper an Adapter.
- Observer dispatch with no snapshot and no per-listener try/catch.
- Retries through a proxy without idempotency, backoff, and a circuit breaker.
- Template Method where the variation is large — that is a Strategy.

## Interview Answer Skeleton

**30-sec:** Factories choose implementations; Adapter reshapes an interface; Proxy keeps it identical and adds behavior, which is how `@Transactional` works; Facade simplifies a subsystem; Observer fans events out; Command makes actions queueable and undoable; Chain of Responsibility pipelines requests; Template Method fixes a sequence while varying steps.

## Related

- [Reflection and Dynamic Proxies](../syllabus/02-java/language-core/reflection-and-dynamic-proxies.md)
- [Transactional Proxy Mechanics and Propagation](../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md)
- [Resilience Patterns](../syllabus/11-system-design/resilience-patterns.md)
