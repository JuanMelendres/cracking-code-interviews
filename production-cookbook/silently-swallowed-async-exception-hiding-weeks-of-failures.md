---
title: "Silently Swallowed @Async Exception Hiding Weeks of Failures"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/spring/auto-configuration-and-bean-lifecycle.md
source: handbook/spring/auto-configuration-and-bean-lifecycle.md#production-scenarios
---

# Silently Swallowed @Async Exception Hiding Weeks of Failures

## Context

A nightly reconciliation job invokes a service method annotated `@Async` and `@Transactional` that occasionally throws on a downstream validation failure. The calling code wraps the call in a try/catch expecting to log and alert on failure. The annotated method returns `void`.

## Symptoms

No alerts ever fire, yet a data audit weeks later reveals a small, steady percentage of reconciliation records were never processed.

## Impact

A real, recurring data-processing failure goes completely unnoticed for weeks because the failure-detection mechanism — the try/catch — was structurally incapable of seeing it.

## Initial Hypotheses

- The reconciliation logic itself silently swallows errors — checked and ruled out; the method body has no catch block, it lets exceptions propagate.
- The scheduler isn't invoking the job at all on affected records — checked and ruled out; logs confirm the job runs on every record.
- The `@Async` return type hides the exception from the caller — correct.

## Evidence

Spring's default logs show `Unexpected exception occurred invoking async method` entries at the exact times affected records were skipped, but the calling code's try/catch — which should have caught and alerted on exactly this — never triggered, because the annotated method returns `void` and the async invocation returns to the caller long before the exception is thrown.

## Investigation Timeline

1. **Steady, small percentage of unprocessed records found** during a routine data audit, with no alerts having fired.
2. **Reconciliation logic and scheduler hypotheses ruled out**, confirming exceptions propagate normally and the job runs on every record.
3. **Spring's own async-exception logs checked**, revealing the exceptions were occurring and being logged all along.
4. **Caller's try/catch reviewed**, finding it structurally could never observe the exception given the method's `void` return type and asynchronous execution.

## Root Cause

The transaction correctly rolled back on failure — data integrity was never at risk — but the `void` return type meant the caller's exception-handling code was checking a code path that had already returned successfully by the time the real work, and its failure, happened on a different thread.

## Immediate Mitigation

Add alerting directly on Spring's `AsyncUncaughtExceptionHandler` — a global hook designed for exactly this class of otherwise-invisible failure — so future occurrences are caught immediately rather than discovered via audit.

## Permanent Fix

Change the method's signature to return `CompletableFuture<Void>` and have the caller explicitly handle the future's exception path, restoring real visibility into success or failure for this specific call site.

## Alternatives Considered

Removing `@Async` entirely. Rejected — the reconciliation job's throughput genuinely benefits from asynchronous execution; the fix is restoring visibility, not removing the mechanism that caused the blind spot.

## Trade-offs

Returning `CompletableFuture<Void>` requires the caller to actually call `.get()` or attach a callback — if that discipline lapses again, the same blind spot silently returns. A global `AsyncUncaughtExceptionHandler` is a more robust safety net precisely because it doesn't depend on every call site remembering to check.

## Prevention

Configure a custom `AsyncUncaughtExceptionHandler` as a standing default for every `@Async` method in the codebase, so visibility into failures doesn't depend on each call site's discipline; treat a `void`-returning `@Async` method as a code-review flag by default.

## Monitoring and Alerts

- The `AsyncUncaughtExceptionHandler` global hook (the Immediate Mitigation above), configured as a standing, codebase-wide default rather than added reactively per incident — this converts every future `void`-returning async failure into an immediate alert instead of a silent gap.
- A periodic reconciliation-count audit comparing input record volume against processed-output volume, independent of any specific exception path — this is what actually surfaced this incident, and it is a useful general safety net for any batch pipeline regardless of its internal error-handling correctness.

## Interview Story

This maps to the "your `@Async` method's exceptions vanish, why" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a nightly job's try/catch, specifically designed to alert on failures, never fired despite a real, recurring failure rate.
- **Task:** explain how a functioning, transactionally correct system produced a silent data-processing gap.
- **Action:** rule out the reconciliation logic and scheduler as the cause; check Spring's own async-exception logs, finding the failures were occurring and logged the whole time; identify that a `void`-returning `@Async` method structurally cannot propagate its exception back to the caller's try/catch.
- **Result:** added a global `AsyncUncaughtExceptionHandler` as an immediate safety net, and changed the method to return `CompletableFuture<Void>` for real, per-call-site visibility.

## Staff-Level Discussion

The genuinely dangerous property of this bug is that every individual piece behaved correctly: the transaction rolled back properly, Spring logged the exception exactly as documented, and the caller's try/catch was written with the right intent. The failure is purely structural — a `void`-returning `@Async` method makes synchronous exception handling at the call site impossible by construction, not by mistake, and this is easy to miss because the code reads as if it handles errors. This is a recurring category worth naming explicitly in a Staff-level review: any asynchronous or fire-and-forget code path needs its own, independent failure-visibility mechanism (a global handler, a dead-letter queue, a reconciliation audit) rather than trusting that ordinary synchronous-looking error handling at the call site will actually observe what happens on the other thread.

## Related Handbook Chapters

- [Auto-Configuration and Bean Lifecycle](../handbook/spring/auto-configuration-and-bean-lifecycle.md) — canonical `@Async` execution and exception-handling mechanics used here.
