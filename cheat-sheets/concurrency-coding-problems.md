---
title: "Cheat Sheet: Concurrency Coding Problems"
slug: concurrency-coding-problems
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2116
canonical: ../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md
last_updated: 2026-09-06
---

# Concurrency Coding Problems

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/concurrency-coding-problems.md`](../syllabus/03-data-structures-algorithms/concurrency-coding-problems.md)

## Core Mental Model

A concurrency coding problem asks for a class whose methods are called from multiple threads simultaneously, and whose correctness must hold regardless of the exact thread-scheduling order — fundamentally different from a single-threaded algorithm's correctness, which only needs to hold for one deterministic execution path per input. A solution that "looks right" and even passes a single test run can still hide a race condition, deadlock, or ordering bug that only manifests under specific, unlucky scheduling — which is why this pattern's own verification standard requires repeated runs, not a single green pass.

## Essential Definitions

- **Standard building blocks** — `Semaphore` (permits controlling how many threads may proceed), intrinsic locks via `synchronized` (mutual exclusion), and `wait()`/`notify()`/`notifyAll()` (a thread waiting for a condition, other threads signaling it might now hold).
- **Semaphore-based baton-passing** — a thread acquires its semaphore, does its work, then releases whichever semaphore corresponds to the next thread's turn.
- **Asymmetric resource-acquisition order** — exactly one participant acquires its required resources in the opposite order from everyone else, breaking a potential circular-wait/deadlock condition.
- **Spurious wakeup** — the JLS explicitly permits a thread to return from `wait()` without any corresponding `notify()` ever having happened; the actual condition must always be re-checked in a `while` loop, never assumed true because `wait()` returned.

## Recognition Signals / When to Use This Pattern

| Signal in the problem | Technique | Notes |
|---|---|---|
| A fixed or data-dependent turn order must be enforced across threads | Semaphore-based baton-passing | Simpler to reason about than manual lock/condition coordination for a pure ordering constraint |
| Multiple threads each need 2+ shared resources, risking circular wait | Asymmetric acquisition order for exactly one participant | No additional synchronization primitive needed |
| Producer/consumer with a bounded buffer, hand-rolled (no `java.util.concurrent`) | `synchronized` + `wait()`/`notifyAll()`, condition re-checked in a `while` loop | Demonstrates the underlying mechanism `ArrayBlockingQueue` already provides |
| A "design"-sounding problem (rate limiter, queue) — check before assuming thread-safety is required | Verify the problem's actual tag/intent first | Not every rate-limiter/queue problem is a concurrency problem (see Design-Style Coding Problems) |

## Common Mistakes

- Assuming a semaphore permit cap alone protects every shared, mutable variable those permitted threads touch — a cap limiting concurrency to 2 threads does not, by itself, make a shared counter those 2 threads both mutate safe; it still needs its own `synchronized` block.
- Using `if (condition) wait();` instead of `while (condition) wait();` — a genuine, real correctness bug given the JLS explicitly permits spurious wakeup, not defensive-programming overkill.
- Trusting a single passing test run as sufficient evidence a concurrency solution is correct — scheduling-dependent bugs can pass on one run and fail on the next with no code change at all.

## Complexity / Verification Reference

- Real multi-threaded tests in this pattern were re-run 5 times for scheduling-dependent stability (10/10 assertions passing every time) — this repeated-run discipline is itself the chapter's central correctness lesson, not a formality.
- `notifyAll()` vs. `notify()` (bounded blocking queue): `notifyAll()` is a deliberate simplicity-over-throughput trade-off — with two distinct wait conditions active (full vs. empty), a plain `notify()` could wake a thread waiting on the wrong condition (functionally safe, but wasted work); `notifyAll()` avoids that at the cost of waking more threads than strictly necessary.

## Interview Answer Skeleton

**30-sec:** Concurrency coding problems test correctness under non-deterministic thread scheduling, not just for one fixed input — the standard toolkit is `Semaphore` for turn-based ordering, `synchronized` for mutual exclusion, and `wait()`/`notifyAll()` inside a `while`-loop condition check for hand-rolled blocking behavior.

**2-min:** Use a semaphore as an explicit "turn" token when a fixed or computable ordering must be enforced (Building H2O, Fizz Buzz Multithreaded). When multiple threads each need two or more shared resources, check for a possible circular wait and consider breaking it via asymmetric acquisition order for exactly one participant (Dining Philosophers), rather than reaching for a more complex fix. Always place `wait()` inside a `while` loop re-checking its actual condition, never an `if` — the standing defense against spurious wakeup.

**Whiteboard:** Draw five philosophers around a circular table with a fork between each pair. Show the naive version where every philosopher grabs their left fork first — mark all five arrows pointing the same rotational direction, creating a full cycle. Then flip exactly one philosopher's arrow (right-then-left) and show the cycle is broken because that one philosopher would need a fork a different, specific philosopher hasn't claimed as their own right fork yet.

**Staff-level framing:** The Dining Philosophers' asymmetric-lock-ordering deadlock fix is not an academic exercise — it is the exact mechanism behind real, documented production incidents where a system's threads acquired multiple locks in inconsistent order under load, producing the same circular-wait condition. The Staff-level pattern is establishing a *global, consistent lock-acquisition order* across an entire codebase (e.g., always lock lower-ID accounts before higher-ID ones in a funds-transfer system) as a standing team convention, rather than relying on each code path to independently reason about deadlock avoidance.

## Production Warning Signs

- **Symptom:** a hand-rolled bounded blocking queue occasionally hangs indefinitely under real concurrent load, with both a producer and a consumer thread each appearing permanently blocked in `wait()`.
- **Diagnose:** check whether every state-changing operation (`enqueue`, `dequeue`) calls `notifyAll()` after changing state — a missing or incorrectly-scoped notification means a waiting thread's condition may have genuinely become true but no signal ever wakes it. Separately confirm the wait condition uses a `while` loop, not `if`. Reproduce by running the exact scenario repeatedly rather than relying on a single hang to characterize the bug, since the triggering interleaving may not reproduce on every run.
- **Real, documented production incidents connected to this pattern:** [Opposite-Order Lock Acquisition Deadlock in a Funds Transfer](../production-cookbook/opposite-order-lock-acquisition-deadlock-in-a-funds-transfer.md) and [Lock Ordering Deadlock Under Peak Load](../production-cookbook/lock-ordering-deadlock-under-peak-load.md) — both real incidents of the exact circular-wait condition Dining Philosophers is designed to teach and fix, at production scale.

## Related

- syllabus/03-data-structures-algorithms/design-style-coding-problems.md
- syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md
- syllabus/01-computer-science-foundations/os-process-thread-model.md
