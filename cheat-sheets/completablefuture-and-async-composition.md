---
title: "Cheat Sheet: CompletableFuture and Async Composition"
slug: completablefuture-and-async-composition
document_type: cheat-sheet
domain: concurrency
topic_id: T-407
canonical: ../handbook/concurrency/completablefuture-and-async-composition.md
last_updated: 2026-09-02
---

# CompletableFuture and Async Composition

**Canonical chapter:** [`handbook/concurrency/completablefuture-and-async-composition.md`](../handbook/concurrency/completablefuture-and-async-composition.md)

## Core Mental Model

A `CompletableFuture` is a box filled exactly once, with a value or an exception. Every dependent stage is really asking "what code runs, and on what thread, at the moment this box gets filled?" Non-`Async` methods answer implicitly; `*Async` methods always answer "an explicit executor, no matter what."

## Essential Definitions

- **Non-`Async` methods** (`thenApply`, `handle`, ...) — run on whatever thread calls `complete()` if attached before completion, or inline/synchronously on the attaching thread if already complete.
- **`*Async` methods** — always dispatch to an executor (`ForkJoinPool.commonPool()` by default, or a supplied one) regardless of completion timing.
- **Silent exception loss** — an exception thrown in a pipeline nobody ever calls `join()`/`get()`/`exceptionally()`/`handle()` on produces zero observable signal.

## Decision Table

| Question | Answer |
|---|---|
| Callback needs to run on a specific (or off a specific) thread? | Use `*Async` with an explicit executor — never rely on non-`Async` contextual behavior |
| Two or more async calls are independent? | Submit all before calling `get()`/`join()` on any; combine with `thenCombine`/`allOf` |
| Will anything ever observe this pipeline's terminal stage? | If no (fire-and-forget), attach `.exceptionally()`/`.handle()` explicitly |
| CPU-bound or IO-bound callback via default `*Async` executor? | For IO-bound/high-volume, supply a dedicated `Executor` — don't starve the common pool |

## Key Numbers

- Attach-before-completion: callback runs on the completer thread. Attach-after-completion: callback runs inline on the attaching thread — the least-expected of the three behaviors.
- Sequential (get() before submitting next): 614ms. `thenCombine` (submit both first): 313ms — real ~2x cost of accidental serialization.

## Common Pitfalls

- Assuming a non-`Async` callback always runs on "the same thread as before" — attach-after-completion runs inline on the attaching thread instead.
- Writing fire-and-forget pipelines with no terminal `join()`/`get()`/`exceptionally()`/`handle()` — silently loses any exception.
- Calling `get()` on one future before submitting the next independent one — accidentally serializes work.
- Using `ForkJoinPool.commonPool()` for blocking IO — starves every other consumer.

## Interview Answer Skeleton

**30-sec:** A non-`Async` callback runs on the completing thread, or inline if already complete; `*Async` always dispatches to an executor. An unobserved pipeline exception is silently lost. `get()` on one independent future before submitting the next accidentally serializes them.

**2-min:** Add the real measured evidence: Case 1 (attach before completion) ran on `[completer-thread]`; Case 2 (attach after) ran inline on `[main]`; `thenApplyAsync` always dispatched to `ForkJoinPool.commonPool-worker-1` even when already complete. Fire-and-forget: "Main thread reached this line normally — no exception, no stack trace." With `join()`: the real `IllegalStateException` surfaces via `CompletionException`.

**Whiteboard:** Callback attached → is the future already complete? → "runs on completing thread" vs "runs inline on attaching thread." Separate box for `*Async` always dispatching regardless. Circle the "runs inline" branch — "this surprises people."

**Staff-level framing:** Any asynchronous, decoupled operation needs an explicit answer to "what happens to a failure nobody is synchronously waiting on?" — the same question applies to fire-and-forget message publishes, async cache writes, background job submissions.

## Production Warning Signs

- An audit-log write via `CompletableFuture.supplyAsync()` with no terminal stage silently drops exceptions for weeks — discovered only by an unrelated compliance review. Fix: attach `.exceptionally()` to log and surface the failure.

## Related

- `handbook/concurrency/executors-and-thread-pool-sizing.md`
- `handbook/concurrency/forkjoinpool-and-work-stealing.md`
- `handbook/java-core/reflection-and-dynamic-proxies.md`
