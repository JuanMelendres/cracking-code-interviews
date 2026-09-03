---
title: "Cross-Request ThreadLocal Leak from Pooled Thread Reuse"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/concurrency/scoped-values-and-threadlocal-migration.md
source: handbook/concurrency/scoped-values-and-threadlocal-migration.md#production-scenarios
---

# Cross-Request ThreadLocal Leak from Pooled Thread Reuse

## Context

A service uses a `ThreadLocal<RequestContext>` to carry per-request tracing metadata (user ID, trace ID) through deeply-nested call chains without threading it through every method signature.

## Symptoms

Under production load, a small but real fraction of log lines and traces are attributed to the wrong user — a request's logs occasionally show a *different* request's user ID.

## Impact

Incorrect audit/trace data — a genuine, if intermittent, data-integrity problem for anything relying on that tracing metadata (compliance logging, debugging, per-user rate limiting).

## Initial Hypotheses

- A logging framework bug — checked, and ruled out: the logging framework correctly reads whatever `ThreadLocal` value it's given.
- A race condition in request routing — checked, and ruled out: each request is handled by exactly one thread at a time, no concurrent access to the same `ThreadLocal` instance.
- A request handler path that doesn't correctly clear the `ThreadLocal` at the end of every request — correct.

## Evidence

An early-return or exception path in the request handler skips the `finally`-block cleanup that calls `RequestContext.remove()`, leaving the `ThreadLocal` set — on a pooled thread, the very next, unrelated request reusing that thread inherits the stale value.

## Investigation Timeline

1. **Cross-request attribution errors reported** — a small, recurring fraction of requests show tracing metadata (user ID) belonging to a different, unrelated request.
2. **Logging framework reviewed** and confirmed to correctly read whatever value the `ThreadLocal` currently holds — the bug is not in how the value is consumed once set.
3. **Request-routing logic reviewed** and confirmed each request is handled by exactly one thread at a time, with no concurrent access to the same `ThreadLocal` instance — ruling out a data race in the ordinary sense.
4. **Every exit path of the request handler audited**, revealing at least one early-return or exception path that skips the `finally`-block cleanup responsible for calling `RequestContext.remove()`.
5. **Thread-pool reuse confirmed as the mechanism**: on a pooled thread, the next, entirely unrelated request reusing that same physical thread inherits whatever value the previous request's handler left behind, since a `ThreadLocal` remains set until something explicitly removes it or the thread itself dies.

## Root Cause

The real, textbook thread-pool-reuse leak: at least one code path in the request handler doesn't guarantee `remove()` runs, and pooled-thread reuse turns that gap into cross-request data leakage.

## Immediate Mitigation

Audit every exit path of the request handler (including exception paths) to ensure `remove()` genuinely always runs, closing the specific leak found.

## Permanent Fix

Migrate the tracing context from `ThreadLocal` to `ScopedValue`, binding it via `ScopedValue.where(CONTEXT, requestContext).run(() -> handleRequest())` at the single point where request handling begins — structurally eliminating the possibility of a forgotten cleanup, since there's no cleanup step to forget.

## Alternatives Considered

Wrapping every request handler entry point in a `try`/`finally` with `remove()` — a real, workable fix, but requires correct discipline at every current and future call site; `ScopedValue` makes the guarantee structural instead of a discipline to maintain.

## Trade-offs

Migrating to `ScopedValue` requires the JDK preview flag and an explicit adoption decision — accepted here given the measured severity of the data-integrity risk it closes.

## Prevention

Any `ThreadLocal` carrying per-request or per-task state in a thread-pool-based system should be reviewed for guaranteed cleanup on every exit path — or migrated to `ScopedValue` to remove the risk structurally.

## Monitoring and Alerts

- Add a request-scoped assertion at the very start of each request handler checking whether the `ThreadLocal` is already non-empty before the current request sets it — a non-empty value at that point is direct, unambiguous evidence of a leaked cleanup from a prior request on the same pooled thread, and should be logged and alerted on immediately.
- Track a metric correlating trace-ID mismatches (a logged trace ID not matching the request's own generated trace ID) against thread-pool identity, so the specific pooled threads and code paths responsible for leaks are identifiable rather than only the aggregate leak rate.
- Add an integration test that deliberately exercises every early-return and exception path of the request handler against a single-thread pool (forcing thread reuse), asserting the `ThreadLocal` is empty immediately after each simulated request completes, regardless of which exit path was taken.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a service's tracing metadata occasionally showed one request's logs attributed to a different, unrelated request's user ID, under production load.
- **Task:** find a cross-request data leak with no logging bug, no routing race, and no obvious code defect at first inspection.
- **Action:** ruled out the logging framework and request-routing race conditions, then audited every exit path of the request handler and found at least one early-return path skipping the `finally`-block cleanup responsible for clearing the `ThreadLocal`.
- **Result:** closed the immediate leak by auditing and fixing every exit path, then permanently migrated the tracing context to `ScopedValue`, removing the possibility of a forgotten cleanup by construction rather than relying on ongoing discipline.

## Staff-Level Discussion

`ThreadLocal` thread-pool-reuse leaks are a recurring bug class precisely because the failure mode requires two independent things to both be true — a missed cleanup on some exit path, and physical thread reuse — neither of which is visible from looking at the `ThreadLocal.set()` call site in isolation. This makes the bug resistant to ordinary code review: the specific exit path that skips cleanup might be an exception branch added months after the original `try`/`finally` was written, by an engineer with no reason to know a `ThreadLocal` cleanup obligation exists elsewhere in the method. A Staff engineer's response to finding this once in a codebase should treat every `ThreadLocal` carrying request- or task-scoped state as carrying the identical structural risk, not just the one instance found — and should weigh, for each one, whether migrating to `ScopedValue` (removing the hazard by construction) is preferable to relying on disciplined `try`/`finally` coverage at every current and future call site. This is also a useful case for discussing preview-API adoption risk at the Staff level: `ScopedValue`'s real, structural safety improvement has to be weighed against the operational cost of running a preview feature in production, and different organizations will reasonably land differently on that trade-off depending on how severe and how frequent this exact leak pattern has been for them.

## Related Handbook Chapters

- [Scoped Values and ThreadLocal Migration](../handbook/concurrency/scoped-values-and-threadlocal-migration.md) — canonical mechanics of the thread-pool-reuse leak and `ScopedValue`'s structural immunity to it.
- [Structured Concurrency](../handbook/concurrency/structured-concurrency.md) — related mechanism for propagating context safely into forked subtasks, relevant if request handling itself fans out concurrently.
