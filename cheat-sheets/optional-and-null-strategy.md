---
title: "Cheat Sheet: Optional and Null Strategy"
slug: optional-and-null-strategy
document_type: cheat-sheet
domain: java-core
topic_id: T-109
canonical: ../handbook/java-core/optional-and-null-strategy.md
last_updated: 2026-09-02
---

# Optional and Null Strategy

**Canonical chapter:** [`handbook/java-core/optional-and-null-strategy.md`](../handbook/java-core/optional-and-null-strategy.md)

## Core Mental Model

`Optional<T>` forces "this might not have a value" to be handled explicitly at the type level, at the cost of a real allocated wrapper object — it's a communication tool for API boundaries (method return types), not a general-purpose replacement for `null`.

## Essential Definitions

- **`Optional.of(value)`** — throws `NullPointerException` immediately if `value` is null; use only when certain the value can't be null.
- **`Optional.ofNullable(value)`** — accepts null, produces an empty `Optional`; correct default when nullability is uncertain.
- **`orElse(x)`** — eager: `x` is evaluated on every call, present or not.
- **`orElseGet(supplier)`** — lazy: `Supplier` invoked only when actually empty.

## Decision Table

| Question | Answer |
|---|---|
| Is this a method return type where "no result" is a real outcome? | `Optional<T>` is the idiomatic, designed use case |
| Is this a field, parameter, or collection element? | Use a plain nullable reference instead — documented anti-pattern |
| Is the `orElse`/`orElseGet` fallback a real computation or call? | Always use `orElseGet()` — eager cost is real and measured |
| Is this a genuinely hot, high-frequency path? | Measure `Optional`'s real allocation overhead before assuming it's negligible |

## Key Numbers

- `orElse()` vs `orElseGet()` on an already-present value, expensive fallback, 5,000,000 calls: 3715ms vs 3ms — a real measured ~1238x difference.
- `Optional` field serialization: real, direct `NotSerializableException` — `Optional` does not implement `Serializable`.

## Common Pitfalls

- Calling `Optional.get()` without a presence check — reproduces the exact "forgot to null-check" bug with a different exception (`NoSuchElementException`).
- Using `orElse()` with a non-trivial fallback computation — pays the eager-evaluation cost on every call regardless of presence.
- Storing `Optional` as a field or parameter — breaks `Serializable`, adds allocation with no benefit over a plain nullable field.
- Using `Optional.of()` when nullability is genuinely uncertain — converts a graceful empty case into an immediate `NullPointerException`.

## Interview Answer Skeleton

**30-sec:** `Optional<T>` communicates "may have no result" at the type level, designed specifically as a method return type. `of(null)` throws immediately; `ofNullable(null)` doesn't. `orElse(x)` evaluates `x` eagerly every call — measured ~1200x slower than `orElseGet()` for an expensive, already-present-case fallback. `Optional` as a field breaks `Serializable`.

**2-min:** Add the real, dramatic measured number (1238.33x) for the eager-vs-lazy gap, and the real `NotSerializableException` proof for the field anti-pattern — both concrete, evidence-backed rather than style opinions.

**Whiteboard:** Method return type → `Optional` appropriate; field/parameter → avoid it, with the real `Serializable` consequence annotated. Beside it, draw eager vs lazy fallback evaluation with the measured ~1200x figure.

**Staff-level framing:** Any API offering both an eager and lazy variant of "provide a fallback" (`Map.getOrDefault()` vs `computeIfAbsent()`, guarded logging string formatting) hides the same easy-to-miss performance trap; a type designed narrowly for one role (API-boundary optionality) accumulates real structural costs (serialization, allocation, equality semantics) when stretched into fields/parameters.

## Production Warning Signs

- A cache-fallback latency regression from `orElse(expensiveDatabaseFallback(key))` unconditionally hitting the database on every request, including cache hits — fix by switching to `orElseGet()`.

## Related

- `handbook/java-core/lambdas-and-functional-interfaces.md`
- `handbook/java-core/streams-and-collectors.md`
- `handbook/java-core/serialization-hazards-and-alternatives.md`
