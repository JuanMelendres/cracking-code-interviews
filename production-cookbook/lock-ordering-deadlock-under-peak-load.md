---
title: "Lock-Ordering Deadlock Under Peak Load"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md
source: handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#production-scenarios
---

# Lock-Ordering Deadlock Under Peak Load

## Context

A backend service exposes two code paths that touch the same two in-process locks: a normal request-handling flow, and a less-common admin/audit flow. Both acquire a user-record lock and an audit-log lock — but in opposite orders. Under low concurrency the two paths rarely interleave closely enough to race; under peak load, the odds of the exact interleaving needed to deadlock rise enough to matter.

## Symptoms

Roughly once a week, under peak load, all request-handling threads become unresponsive simultaneously. CPU usage drops to near-zero despite a full queue of pending requests. The process requires a manual restart to recover.

## Impact

Full service outage, recurring unpredictably under load, each requiring manual intervention to end.

## Initial Hypotheses

- Resource leak causing exhaustion — checked and ruled out; memory and file-descriptor metrics show no leak pattern.
- Infinite loop consuming no I/O — ruled out by the near-zero CPU usage, which is inconsistent with a busy-loop.
- A deadlock — correct.

## Evidence

A thread dump captured during the next occurrence (triggered proactively via `jstack` once the CPU-idle pattern was noticed) shows two request-handling threads in `BLOCKED` state, each waiting on a lock the other holds — a classic two-lock cycle. This was confirmed programmatically via `ThreadMXBean.findDeadlockedThreads()` rather than inferred by eye from the raw dump.

## Investigation Timeline

1. **Outage observed.** All request threads stop making progress; CPU idle; queue backing up.
2. **First two hypotheses ruled out.** Resource-exhaustion metrics and I/O activity both come back clean, pointing away from a leak or busy-loop.
3. **Deadlock suspected.** The near-zero CPU signature is the distinguishing evidence against a busy-loop and toward a blocked-thread state.
4. **Confirmed on the next occurrence.** A proactive `jstack` capture, checked programmatically with `ThreadMXBean.findDeadlockedThreads()`, shows the two-thread cycle directly rather than requiring visual inspection of the full dump.
5. **Code paths traced.** The two threads map to the normal request flow and the admin/audit flow, each acquiring the user-record lock and the audit-log lock in opposite order.

## Root Cause

Two code paths acquire the same two locks in opposite orders. Under low load the probability of the exact interleaving needed to deadlock is low enough to go unnoticed; under peak load it rises enough to occur roughly weekly.

## Immediate Mitigation

Restart the affected process — the pre-existing ad-hoc response — now paired with a scheduled health check that proactively calls `ThreadMXBean.findDeadlockedThreads()`, so future occurrences are detected and can trigger an automated restart faster than waiting for external symptoms to be noticed.

## Permanent Fix

Refactor both code paths to acquire the two locks in a single, consistent, documented order (for example, always the user-record lock before the audit-log lock), eliminating the cycle structurally rather than reactively.

## Alternatives Considered

Adding a lock-acquisition timeout so a stuck thread eventually gives up rather than deadlocking forever. Considered as defense-in-depth, but not a substitute for the structural fix — a timeout converts a permanent freeze into a repeated failure-and-retry pattern rather than eliminating the underlying bug.

## Trade-offs

Enforcing a global lock order requires every future code path touching these two locks to follow the same discipline, and the compiler cannot enforce it. Accepted, since the alternative is a recurring, production-impacting outage.

## Prevention

Any code path acquiring more than one lock should be reviewed specifically for consistent ordering against every other code path that acquires the same locks — a documented lock-ordering convention, checked in code review since static tooling cannot verify it.

## Monitoring and Alerts

- A scheduled health check calling `ThreadMXBean.findDeadlockedThreads()` (from Immediate Mitigation above), alerting on any non-empty result rather than waiting for request-queue backup to be noticed externally.
- Request-queue depth and near-zero-CPU-with-full-queue as a combined signal: CPU utilization dropping while queue depth climbs is a stronger, faster deadlock signature than latency alone, since a deadlock does not show up as gradually rising latency — it shows up as full-stop no progress.

## Interview Story

This maps directly to the "two threads deadlock in production, walk me through diagnosing it live" question. Present it as a representative scenario unless you have lived through an equivalent incident — do not claim it as personal history unless it is:

- **Situation:** a periodic full-service freeze under peak load, recovered only by manual restart.
- **Task:** find the root cause without being able to attach a debugger mid-incident.
- **Action:** rule out resource exhaustion and busy-looping using metrics already collected; capture a thread dump on the next occurrence; use `ThreadMXBean.findDeadlockedThreads()` rather than manual dump reading to get a definitive, mechanical answer.
- **Result:** identified the exact two-lock cycle and the two code paths responsible; shipped a lock-ordering fix and an automated detection health check.

## Staff-Level Discussion

A single lock-ordering fix closes this one incident, but the underlying risk is organizational, not local: any two code paths anywhere in the service that acquire the same two locks can reintroduce this exact class of bug, and nothing short of a documented, reviewed convention catches it before it reaches production. A Staff engineer's contribution here is less the fix itself and more turning a one-off incident into a standing review checklist item — and recognizing that `ThreadMXBean.findDeadlockedThreads()` deserves to be a permanent, scheduled health check rather than a one-time diagnostic reached for only after the fact. The cost of that health check is negligible; the cost of a second undetected weekly outage is not.

## Related Handbook Chapters

- [Deadlock, Race Conditions, and Thread Diagnostics](../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md) — canonical mechanics of lock cycles, thread-dump reading, and the `ThreadMXBean` API used here.
