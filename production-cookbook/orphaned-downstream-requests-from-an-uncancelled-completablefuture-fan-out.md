---
title: "Orphaned Downstream Requests from an Uncancelled CompletableFuture Fan-Out"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/concurrency/structured-concurrency.md
  - ../syllabus/02-java/concurrency/completablefuture-and-async-composition.md
source: handbook/concurrency/structured-concurrency.md#production-scenarios
---

# Orphaned Downstream Requests from an Uncancelled CompletableFuture Fan-Out

## Context

A service fans out to three downstream services concurrently via `CompletableFuture.supplyAsync()` to assemble a response.

## Symptoms

When one downstream call times out and the handler returns an error to the client, monitoring shows the other two downstream calls' connections and threads remaining active for their full configured timeout, well after the client has already received an error response.

## Impact

Wasted downstream load and held connections/threads for calls whose results the caller will never use, compounding under any real-world timeout-heavy incident — exactly when the downstream services are already struggling and can least afford wasted extra load.

## Initial Hypotheses

- A connection-pool leak in the HTTP client itself — checked, and ruled out: connections are correctly returned to the pool once each call actually completes.
- A bug in the error-handling path — checked, and ruled out: the error handling correctly returns to the client.
- The fan-out has no mechanism to cancel siblings once the overall response has already failed — correct.

## Evidence

Reproducing the exact shape of the fan-out against the real handler confirms it: each `supplyAsync` branch runs to its own full completion or timeout independently, with zero relationship to any sibling branch's outcome.

## Investigation Timeline

1. **Monitoring flags downstream connections/threads remaining active** well past the point the client has already received an error response for a given request.
2. **Connection-pool behavior reviewed** and confirmed correct — connections are properly returned to the pool once each individual call completes; the issue is not a leak in the traditional sense.
3. **Error-handling path reviewed** and confirmed correct — the handler correctly returns an error to the client as soon as the timed-out branch fails.
4. **Fan-out structure inspected directly**, confirming three independent `CompletableFuture.supplyAsync()` calls with no code relating one branch's outcome to another's.
5. **Orphaned-task mechanism confirmed**: each branch's lifetime is fully decoupled from the others' — a failing branch has no way to signal its siblings to stop, so the other two continue running to their own full completion or timeout regardless of the overall response having already been returned to the client.

## Root Cause

`CompletableFuture` composition has no built-in concept of "the sibling branches should stop because the overall operation already failed" — each branch's lifetime is fully decoupled from the others'.

## Immediate Mitigation

Reduce each downstream call's individual timeout so orphaned calls at least resolve faster, shrinking (not eliminating) the wasted-load window.

## Permanent Fix

Migrate the fan-out to `StructuredTaskScope.ShutdownOnFailure` (once past preview, or accepted as a preview feature for this JVM version in agreement with the team) so a failing branch automatically interrupts the others, eliminating the wasted downstream load structurally rather than merely shrinking its window.

## Alternatives Considered

Manually tracking and cancelling sibling `CompletableFuture`s on any branch's failure — a real, workable alternative, but requires disciplined, correct bookkeeping at every fan-out call site; `StructuredTaskScope` provides the same guarantee structurally, by construction, at every call site automatically.

## Trade-offs

Adopting a preview API requires `--enable-preview` at both compile and runtime, and an explicit organizational decision about running preview features in production — a non-trivial adoption cost weighed against the measured resource-leak cost of not adopting it.

## Prevention

Any concurrent fan-out where one branch's failure should logically stop the others should be reviewed for this exact leak — "does anything actually cancel my siblings when this fails?" is the right question, and `CompletableFuture` alone never answers yes without explicit extra code.

## Monitoring and Alerts

- Track "requests where the client received a response before all fan-out branches completed" as a standing metric — a non-zero, sustained rate of this is the direct signature of orphaned work and should be visible on a dashboard independent of any specific incident.
- Alert on downstream connection/thread hold duration exceeding the point at which the originating request already completed, correlating connection lifetime against request lifetime rather than monitoring each in isolation.
- During any incident where a downstream service is already degraded (elevated latency or error rate), specifically check whether upstream fan-out patterns are compounding the load with orphaned, already-useless calls — this is exactly the scenario where the wasted load matters most and is easiest to overlook under incident pressure.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a service fanning out to three downstream dependencies concurrently was found, via monitoring, to hold connections and threads open for two of them well after a timeout in the third had already caused an error response to be returned to the client.
- **Task:** determine why "the request already failed and returned" didn't stop the still-running sibling calls consuming real downstream resources.
- **Action:** ruled out a connection-pool leak and an error-handling bug, then examined the fan-out's structure directly and confirmed each `CompletableFuture` branch's lifetime was completely decoupled from its siblings', with no mechanism to cancel one when another failed.
- **Result:** reduced individual downstream timeouts as an immediate mitigation, then migrated the fan-out to `StructuredTaskScope.ShutdownOnFailure`, giving the scope automatic, structural cancellation of sibling branches on any one branch's failure.

## Staff-Level Discussion

This incident captures a class of resource waste that's easy to miss because nothing in it looks like a "bug" in the traditional sense — every individual piece of code does exactly what it was written to do, and the wasted work is a property of the composition, not any single line. The risk compounds precisely when it matters most: during an incident where a downstream service is already degraded, every upstream fan-out still dutifully sending it now-useless work adds real load at the worst possible time, potentially turning a partial degradation into a harder outage. A Staff engineer evaluating a system's fan-out patterns should treat "what happens to my siblings when one branch fails" as a standing question for any concurrent composition, not something to discover via monitoring after the fact — and should weigh `StructuredTaskScope`'s preview status explicitly as an adoption decision rather than deferring the fix indefinitely because the mechanism is not yet finalized. This also has a systemic dimension worth raising with other teams: if this fan-out pattern exists in one service, it likely exists in several, and the same silent resource-leak risk is probably present anywhere else in the organization that composes multiple `CompletableFuture` calls without an explicit cancellation strategy.

## Related Handbook Chapters

- [Structured Concurrency](../syllabus/02-java/concurrency/structured-concurrency.md) — canonical mechanics of `StructuredTaskScope`'s automatic cancellation propagation and the measured orphaned-task reproduction this incident traces back to.
- [CompletableFuture and Async Composition](../syllabus/02-java/concurrency/completablefuture-and-async-composition.md) — the composition model whose lack of built-in cancellation propagation is this incident's root cause.
