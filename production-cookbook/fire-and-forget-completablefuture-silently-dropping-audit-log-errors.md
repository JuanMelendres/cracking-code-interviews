---
title: "Fire-and-Forget CompletableFuture Silently Dropping Audit-Log Errors"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/concurrency/completablefuture-and-async-composition.md
source: handbook/concurrency/completablefuture-and-async-composition.md#production-scenarios
---

# Fire-and-Forget CompletableFuture Silently Dropping Audit-Log Errors

## Context

A service asynchronously writes an audit-log entry after handling each request, using `CompletableFuture.supplyAsync(() -> auditClient.write(entry))` with no further chaining.

## Symptoms

Weeks later, a compliance review finds gaps in the audit log corresponding to a period when the audit service was intermittently returning errors — but the main service's logs show no errors at all during that window.

## Impact

A silent data-integrity gap (missing audit records) with zero operational signal at the time it happened, discovered only by an unrelated downstream audit.

## Initial Hypotheses

- The audit client itself silently discards failed writes — checked, and ruled out: the client throws on failure, as designed.
- A logging configuration issue suppressed the error logs — checked, and ruled out: other, synchronous errors from the same period are present in the logs.
- The async write's exception was never observed by anything — correct.

## Evidence

The call site shows exactly the fire-and-forget pattern: `CompletableFuture.supplyAsync(...)` with no `.join()`, `.get()`, `.exceptionally()`, or `.handle()` ever called on the resulting future. The exception thrown inside `auditClient.write()` had nowhere to go.

## Investigation Timeline

1. **Compliance review flags gaps** in the audit log corresponding to a known period of intermittent audit-service errors.
2. **Main service's own logs checked for the same period** and found to show no errors at all — an unexpected absence given the audit service was known to be failing.
3. **Audit client's failure behavior reviewed and confirmed correct** — it throws on failure as designed; it does not silently swallow anything itself.
4. **Logging configuration reviewed for the affected window** and confirmed correct — other, unrelated synchronous errors from the identical period are present in the logs, ruling out a logging-suppression issue.
5. **Call site inspected directly**, revealing `CompletableFuture.supplyAsync(() -> auditClient.write(entry))` attached with no terminal `.join()`, `.get()`, `.exceptionally()`, or `.handle()` call anywhere — the exception thrown inside the supplier had no observer and was discarded along with the future once it went out of scope.

## Root Cause

A `CompletableFuture` pipeline exception is invisible unless something forces the result. The audit write's exception was thrown on a background thread, stored inside the `CompletableFuture`, and discarded with it once it went out of scope — nothing ever asked the future for its outcome.

## Immediate Mitigation

Reconstruct the missing audit entries from request-level access logs where possible, and flag the compliance gap for the period in question.

## Permanent Fix

Attach `.exceptionally(ex -> { log.error("audit write failed", ex); return null; })` (or `.handle(...)`) to every fire-and-forget `CompletableFuture`, converting the silent failure into a logged, alertable one.

## Alternatives Considered

Making the audit write synchronous — rejected, since the whole point of the async write was to avoid adding audit-service latency to the request path; the fix is observing the failure, not removing the async behavior.

## Trade-offs

None significant — attaching `.exceptionally()` costs nothing at runtime on the success path and turns a silent failure into a logged one on the failure path.

## Prevention

Any code review of a `CompletableFuture` pipeline that has no terminal `join()`/`get()`/`exceptionally()`/`handle()` should be flagged — a pipeline that is never observed is a pipeline whose failures are, by construction, invisible.

## Monitoring and Alerts

- Add a static-analysis rule flagging any `CompletableFuture.supplyAsync()`/`runAsync()` call chain with no terminal `.join()`, `.get()`, `.exceptionally()`, `.handle()`, or `.whenComplete()` reachable from it, catching the fire-and-forget pattern at review time before it ships.
- Once `.exceptionally()`/`.handle()` is attached, alert on the resulting error-log rate for the audit-write path directly, rather than relying on a downstream compliance audit to surface the gap — this converts what was previously an invisible failure into a normal, monitorable error-rate metric.
- Add a reconciliation job comparing request volume against audit-log entry volume on a rolling basis, so a systematic gap (whatever its cause) is caught automatically and promptly rather than depending on a periodic manual compliance review.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a compliance review discovered gaps in an audit log corresponding to a period when the audit service was known to be failing intermittently, yet the main service's own logs showed no errors at all during that window.
- **Task:** explain how failures that definitely occurred left absolutely no trace anywhere in the calling service.
- **Action:** ruled out the audit client silently swallowing failures and a logging-configuration issue, then inspected the call site directly and found a fire-and-forget `CompletableFuture.supplyAsync()` call with no terminal method ever invoked on the resulting future.
- **Result:** reconstructed what audit data could be recovered from access logs, then attached `.exceptionally()` to the audit-write pipeline so every future failure is logged and alertable, closing the entire class of silent async failure.

## Staff-Level Discussion

This incident is a strong example of a correctness gap that exists entirely in what the code *doesn't* do — there is no bug in the audit client, no bug in the logging configuration, and no bug in the exception-throwing behavior itself; the defect is purely the absence of a terminal call on the future. This is exactly the kind of omission that's invisible in a code read unless the reviewer specifically knows to ask "what observes this future's outcome?" for every async call, which makes it a strong candidate for automated enforcement (the static-analysis rule) rather than relying on review discipline alone. The organizational lesson generalizes beyond `CompletableFuture` specifically: any fire-and-forget asynchronous operation — a background thread, a message published without a delivery acknowledgment, an async client call with no completion handler — carries the same structural risk of failures vanishing with zero operational signal, and a Staff engineer auditing a codebase's asynchronous code should treat "does every async operation have an explicit failure-observation path" as a standing architectural question, not something addressed reactively after a compliance or audit discovery. The multi-week detection latency here (found by an unrelated compliance review rather than any internal alert) is itself the strongest argument for building the alerting in from the start rather than treating fire-and-forget as inherently low-risk because it "usually works."

## Related Handbook Chapters

- [CompletableFuture and Async Composition](../syllabus/02-java/concurrency/completablefuture-and-async-composition.md) — canonical completion-and-threading model and the measured exception-swallowing reproduction this incident traces back to.
- [Structured Concurrency](../syllabus/02-java/concurrency/structured-concurrency.md) — related, purpose-built alternative that structurally prevents an async task's outcome from being silently lost.
