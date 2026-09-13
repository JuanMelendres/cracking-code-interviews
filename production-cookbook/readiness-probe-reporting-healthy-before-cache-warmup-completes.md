---
title: "Readiness Probe Reporting Healthy Before Cache Warm-Up Completes"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md
source: syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md#production-scenarios
---

# Readiness Probe Reporting Healthy Before Cache Warm-Up Completes

## Context

A service's readiness probe returns healthy immediately after the process starts, while a background cache warm-up task is still running.

## Symptoms

Requests served in the first few seconds after startup hit an empty cache and fall through to a slow, cold-path database query, causing a measurable latency spike right after every deployment.

## Impact

Real, elevated tail latency and increased database load during every rolling deployment, specifically in the window before cache warm-up genuinely finishes.

## Initial Hypotheses

- A database connection-pool cold-start issue — checked, connections are pre-warmed correctly.
- The readiness probe endpoint itself has a bug — checked, it correctly returns what the application reports.
- The application reports "ready" without actually waiting for cache warm-up to complete — correct.

## Evidence

The readiness endpoint's handler has no dependency at all on the cache warm-up task's completion state — it returns healthy as soon as the HTTP listener itself is bound, entirely independent of whether background initialization work has finished.

## Investigation Timeline

1. Latency spike observed specifically in the first few seconds after every deployment.
2. Connection-pool and probe-endpoint-bug hypotheses ruled out via pool metrics and endpoint-logic review.
3. Readiness-handler code inspected, showing no coupling to the cache warm-up task's state at all.

## Root Cause

The application never modeled "N background initialization tasks must complete before I'm truly ready" as an explicit coordination point — exactly the shape a `CountDownLatch` exists for, initialized to the number of warm-up tasks, with the readiness handler checking (or awaiting, with a timeout) the latch rather than an unconditional "process started" flag.

## Immediate Mitigation

Manually delay traffic routing to newly-started instances by a fixed buffer window in the deployment tooling.

## Permanent Fix

Introduce a `CountDownLatch` sized to the number of warm-up tasks; each task calls `countDown()` on completion; the readiness handler reports healthy only once the latch reaches zero (or blocks briefly with a bounded `await(timeout, unit)` if the readiness protocol requires a synchronous check).

## Alternatives Considered

A simple `volatile boolean ready` flag flipped by the last-to-complete task — rejected, since it requires the warm-up tasks themselves to coordinate who's "last," reinventing exactly what `CountDownLatch` already provides correctly and atomically.

## Trade-offs

None meaningful — a `CountDownLatch` is the direct, idiomatic fit for this exact coordination shape.

## Prevention

Treat "wait for a fixed, known number of initialization tasks to complete" as a standing signal to reach for `CountDownLatch` explicitly, rather than an ad hoc flag or polling loop.

## Monitoring and Alerts

- Post-deploy latency dashboards windowed to the first minute after each instance's readiness transition, to catch this exact warm-up-window regression rather than averaging it away.
- An explicit metric for "requests served before warm-up latch reached zero" once the fix ships, to confirm the fix actually eliminates the window rather than just narrowing it.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent regression.

- **Situation:** every rolling deployment produced a brief but real latency spike right after new instances came up.
- **Task:** find why "ready" instances were still serving slow, cold-cache requests.
- **Action:** ruled out connection-pool and probe-bug hypotheses; traced the readiness handler and found it had no dependency on cache warm-up state at all.
- **Result:** introduced a `CountDownLatch` gating readiness on warm-up completion, eliminating the deploy-time latency spike.

## Staff-Level Discussion

This is Interview Question 1 in the canonical chapter's own Interview Questions section — "how would you implement an application readiness gate?" — arriving as a real, measurable production latency regression rather than a definitional question. The broader pattern worth generalizing: any "N things must finish before this system is truly ready" requirement is a `CountDownLatch`-shaped problem, and an unconditional "process started" readiness signal is a standing risk for every service with background initialization work, not just this one.

## Related Handbook Chapters

- [java.util.concurrent Synchronizers: CountDownLatch, CyclicBarrier, Semaphore](../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md) — the canonical startup-gate demo behind this incident.
