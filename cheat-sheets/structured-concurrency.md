---
title: "Cheat Sheet: Structured Concurrency"
slug: structured-concurrency
document_type: cheat-sheet
domain: concurrency
topic_id: T-411
canonical: ../handbook/concurrency/structured-concurrency.md
last_updated: 2026-09-02
---

# Structured Concurrency

**Canonical chapter:** [`handbook/concurrency/structured-concurrency.md`](../handbook/concurrency/structured-concurrency.md)

## Core Mental Model

A structured concurrent operation has exactly one entry and exactly one exit — like a method call, but for a tree of threads instead of stack frames. `StructuredTaskScope.close()` cannot return while any forked subtask is still alive.

## Essential Definitions

- **`StructuredTaskScope`** (JEP 453, **preview API in JDK 21**, second preview) — `fork()`s subtasks in a try-with-resources block; `join()` waits for all; a shutdown policy decides what happens on failure/success.
- **`ShutdownOnFailure`** — shuts the scope down (interrupting every other running subtask) the moment any one subtask throws; `throwIfFailed()` re-raises the failure.
- **`ShutdownOnSuccess`** — the inverse: shuts down (cancelling the rest) the moment any one subtask succeeds — "first result wins."
- **Orphaned task** — a `CompletableFuture` sibling with no automatic relationship to another branch's failure; it keeps running to completion regardless.

## Decision Table

| Question | Answer |
|---|---|
| A fan-out's subtasks should stop if one logically fails? | `StructuredTaskScope.ShutdownOnFailure` gives this structurally; `CompletableFuture` does not without extra manual work |
| "First result wins" racing pattern? | `StructuredTaskScope.ShutdownOnSuccess` |
| Preview API acceptable in this JVM version for this team? | If not, `CompletableFuture` remains stable — but track the orphaned-task cost as a known trade-off |
| Subtasks' lifetimes genuinely need to be independent (best-effort side effects)? | The coupling `StructuredTaskScope` enforces may be the wrong fit |

## Key Numbers

- Two real concurrent subtasks (~200ms, ~250ms), joined: real elapsed 264ms (tracks the slower, not the sum ~450ms).
- Sibling fails at ~100ms with a 5-second budget: real elapsed ~116ms — the sibling really was interrupted almost immediately.
- The unstructured `CompletableFuture` equivalent: caller "moves on" at ~109ms, but `isDone()` on the sibling is `false` — it finishes at real ~2011ms, fully orphaned.

## Common Pitfalls

- Assuming `CompletableFuture` composition automatically cancels sibling tasks on one branch's failure — it does not, a real, sizeable resource leak.
- Using `StructuredTaskScope` in production without an explicit preview-API risk decision.
- Forgetting `join()` alone does not re-raise a failure — `throwIfFailed()` (or checking `exception()`) is required.
- Coupling genuinely independent subtasks' lifetimes under one scope when independence was intentional.

## Interview Answer Skeleton

**30-sec:** Structured concurrency binds concurrent subtasks' lifetimes to a lexical scope — the scope can't exit while any subtask is running, and `ShutdownOnFailure` automatically interrupts siblings the moment one fails. `CompletableFuture` doesn't provide this: a sibling keeps running to completion regardless, a real, measurable resource leak.

**2-min:** Add the real measured numbers: automatic cancellation cut a 5-second-budget sibling down to ~116ms elapsed; the identical unstructured shape left the sibling running a full, real ~2 seconds after the caller had already "moved on" at ~109ms.

**Whiteboard:** Subtask A fails at ~100ms; the scope interrupts subtask B; the whole operation completes at ~116ms instead of B's full 5-second budget. Beside it, the `CompletableFuture` contrast: nothing interrupts B, it runs its real full ~2 seconds regardless.

**Staff-level framing:** Any spawned work whose lifetime should logically couple to a larger operation's outcome creates a real, invisibly-growing resource-leak surface when decoupled by default — the same pattern shows up in unbounded background job submission and orphaned distributed-trace spans. Ask "what happens to the other branches when one fails, and is that the desired behavior?"

## Production Warning Signs

- A fan-out API call leaves orphaned downstream requests running their full timeout after one branch already failed and the client received an error response — monitoring shows connections/threads active well past the response. Fix: migrate to `StructuredTaskScope.ShutdownOnFailure`.

## Related

- `handbook/concurrency/completablefuture-and-async-composition.md`
- `handbook/concurrency/forkjoinpool-and-work-stealing.md`
- `handbook/concurrency/scoped-values-and-threadlocal-migration.md`
