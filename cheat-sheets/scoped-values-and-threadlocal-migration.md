---
title: "Cheat Sheet: Scoped Values and ThreadLocal Migration"
slug: scoped-values-and-threadlocal-migration
document_type: cheat-sheet
domain: concurrency
topic_id: T-412
canonical: ../handbook/concurrency/scoped-values-and-threadlocal-migration.md
last_updated: 2026-09-02
---

# Scoped Values and ThreadLocal Migration

**Canonical chapter:** [`handbook/concurrency/scoped-values-and-threadlocal-migration.md`](../handbook/concurrency/scoped-values-and-threadlocal-migration.md)

## Core Mental Model

A `ScopedValue` behaves like a dynamically-scoped variable, bound for exactly the duration of one call and automatically, unconditionally unbound the instant that call returns — no `set()` to forget, no `remove()` to skip. A `ThreadLocal` behaves like a mutable slot glued to a physical thread, staying set until explicitly removed or the thread dies.

## Essential Definitions

- **`ScopedValue<T>`** (JEP 446, **preview API in JDK 21**) — `ScopedValue.where(value, x).run(() -> ...)` binds for the exact dynamic extent of the call; no `set()` method exists at all.
- **Thread-pool-reuse leak** — a `ThreadLocal.set()` with a forgotten `remove()` leaves the value visible to the next, unrelated task reusing the same pooled thread.
- **Propagation gap** — a plain `ThreadLocal` doesn't propagate to any new thread automatically; `ScopedValue` genuinely propagates into `StructuredTaskScope` subtasks.

## Decision Table

| Question | Answer |
|---|---|
| Per-request/per-task context that must never leak across pooled-thread reuse? | `ScopedValue` closes this structurally |
| Context must be visible inside `StructuredTaskScope` subtasks/virtual threads? | `ScopedValue` — purpose-built; plain `ThreadLocal` doesn't propagate |
| Value genuinely needs mutation after being set (not just re-bound in a nested scope)? | `ThreadLocal` remains the right tool — `ScopedValue` has no `set()` |
| Preview API acceptable for this codebase/team? | If not, `ThreadLocal` remains stable — but track the leak risk as a known trade-off |

## Common Pitfalls

- Assuming `ThreadLocal.remove()` is optional cleanup rather than a required step on every exit path in thread-pool-based code.
- Assuming a plain `ThreadLocal` automatically propagates to any new thread — it doesn't; only `InheritableThreadLocal` copies, and only once, at creation time.
- Trying to call a `set()`-style method on `ScopedValue` — none exists; the only binding mechanism is `where(...).run(...)`/`.call(...)`.
- Adopting `ScopedValue` in production without an explicit, deliberate preview-API risk decision.

## Interview Answer Skeleton

**30-sec:** `ScopedValue` (JEP 446, preview in JDK 21) binds immutably for the exact duration of `run()`/`call()` — no `set()`, nothing to clean up, structurally immune to `ThreadLocal`'s classic thread-pool-reuse leak. It also propagates into `StructuredTaskScope` subtasks, unlike a plain `ThreadLocal`.

**2-min:** Add the real, measured side-by-side leak comparison: `ThreadLocal` set by Task 1 and never removed IS visible to unrelated Task 2 on the same reused pooled thread — a real leak; `ScopedValue` bound only for Task 1's `run()` shows no leak at all. Add the real propagation difference: a child `Thread` sees `null` for a plain `ThreadLocal`; a `StructuredTaskScope` subtask genuinely sees the bound `ScopedValue`.

**Whiteboard:** `ThreadLocal` path — set, forget to remove, thread reused, leak — beside the `ScopedValue` path — bind, `run()` returns, binding automatically ends, thread reused, no leak. The side-by-side on the identical "thread gets reused" step is the entire argument.

**Staff-level framing:** Any mutable state attached to a long-lived, reused resource (a pooled thread, a pooled connection, a cached object) carries a real leak risk unless its lifecycle is structurally tied to the logical unit of work rather than the physical resource — treat "is cleanup structural or disciplinary?" as a standing design question.

## Production Warning Signs

- A request-tracing `ThreadLocal` occasionally attributes one user's request to another — an exception path skips the `finally`-block `remove()`, and pooled-thread reuse leaks the stale context into the next, unrelated request. Fix: migrate to `ScopedValue`'s structurally-guaranteed cleanup.

## Related

- `handbook/concurrency/structured-concurrency.md`
- `handbook/concurrency/virtual-threads.md`
- `handbook/concurrency/completablefuture-and-async-composition.md`
