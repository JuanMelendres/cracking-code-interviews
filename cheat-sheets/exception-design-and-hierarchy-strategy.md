---
title: "Cheat Sheet: Exception Design and Hierarchy Strategy"
slug: exception-design-and-hierarchy-strategy
document_type: cheat-sheet
domain: java-core
topic_id: T-105
canonical: ../handbook/java-core/exception-design-and-hierarchy-strategy.md
last_updated: 2026-08-05
---

# Exception Design and Hierarchy Strategy

**Canonical chapter:** [`syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md`](../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md)

## Core Mental Model

An exception's job is to carry information to whoever eventually catches it — and every re-throw, wrap, or `finally` block is a place that information can be silently destroyed instead of preserved. Chaining the cause is the difference between "we know exactly what broke and why" and "something broke, we have no idea what." Try-with-resources exists specifically because a `finally` block's own exception would otherwise silently replace whatever exception the try block was already propagating.

## Essential Definitions

- **Cause chaining** — using `Throwable(String, Throwable)` (or `initCause()`) to preserve the original exception, retrievable via `getCause()`.
- **Suppressed exceptions** — when both a try-with-resources body and `close()` throw, the body's exception propagates as primary and `close()`'s is attached via `addSuppressed()`, retrievable via `getSuppressed()`.
- **Checked vs. unchecked** — checked forces callers to acknowledge a failure category at compile time (boilerplate, can encourage catch-and-ignore); unchecked propagates naturally (nothing forces awareness).

## Decision Table

| Question | Answer |
|---|---|
| Constructing an exception inside a `catch` block? | Always pass the caught exception as the cause |
| Class implements `AutoCloseable`, used in a `try` block? | Prefer try-with-resources over a manual `finally` block |
| Should this failure force callers to handle it explicitly? | Checked. Should it propagate like a programming error? | Unchecked |
| Custom exception class missing a cause-accepting constructor? | Add one — every custom exception type should support chaining |

**Trade-offs:** cause-chaining has no real cost and is strictly better than not chaining; checked exceptions trade boilerplate for compiler-enforced acknowledgment.

## Key Numbers (real, executed)

```
Wrapping WITHOUT chaining the cause:
  e.getCause() = null   <- the IOException and its stack trace are LOST

Wrapping WITH the cause chained:
  e.getCause() = java.io.IOException: disk full on volume /data
  printStackTrace() shows full "Caused by:" chain
```

```
try-with-resources, BOTH body and close() throw:
  Primary exception propagated: resource-A: failure during doWork()
  e.getSuppressed().length = 1  (close() failure attached, NOT lost)

Manual finally block, BOTH throw:
  Exception that actually propagated: resource-B: failure during close()
  (the ORIGINAL doWork() failure is completely gone -- silently replaced)
```

## Common Pitfalls

- Constructing a new exception inside a `catch` block without passing the caught exception as the cause.
- Using a manual `try`/`finally` for a resource that implements `AutoCloseable` instead of try-with-resources.
- Designing a custom exception class with only a message constructor, with no way to chain a cause at all.

## Interview Answer Skeleton

**30-sec:** Wrapping an exception without chaining its cause destroys the original failure's information — measured directly, `getCause()` returns `null`. Try-with-resources propagates the body's exception as primary and `close()`'s as suppressed, preserving both; a manual `finally`-block `close()` that also throws silently replaces the original entirely.

**2-min:** Add why (a low-level exception isn't always meaningful several layers up, but wrapping must preserve it) + the real measured evidence (a `getCause() == null` unchained wrap vs. a full "Caused by:" chain; a `getSuppressed().length == 1` try-with-resources vs. a total loss under manual `finally`) + the trade-off (cause-chaining is free; checked-vs-unchecked trades boilerplate for enforcement).

**Whiteboard:** The try-block body throwing Exception A, `close()` throwing Exception B during propagation, A shown propagating to the caller with B attached via `addSuppressed()`. Annotate: "without try-with-resources, B would silently REPLACE A instead of being attached to it."

**Staff-level framing:** cause-chaining and suppressed exceptions are both instances of a broader principle — a good exception design never has to choose between two pieces of information; it should always be possible to know both what ultimately propagated and what else happened along the way. Treat "does this custom exception type support chaining" as a mandatory review item, since the cost of adding it is zero.

## Production Warning Signs

- An on-call alert fires for a generic exception with no `getCause()` and no chained stack trace — the wrapping code likely used a message-only constructor inside a `catch` block, discarding the real root cause at the exact moment it was wrapped, turning a seconds-long diagnosis into an hour of manual reproduction.
- A resource-cleanup bug disappears when the code is refactored to try-with-resources — the original manual `finally` block was silently swallowing a real exception whenever `close()` also failed.
- **Prevention:** require every custom exception class to expose a cause-accepting constructor as code-review policy, and flag any `catch` block that constructs a new exception without referencing the caught variable.

## Related

- `syllabus/02-java/language-core/immutability-and-defensive-copying.md`
