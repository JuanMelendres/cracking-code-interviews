---
title: "Stale Flag Read Exposed by a JVM Upgrade's JIT Timing"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/concurrency/java-memory-model-and-volatile.md
source: handbook/concurrency/java-memory-model-and-volatile.md#production-scenarios
---

# Stale Flag Read Exposed by a JVM Upgrade's JIT Timing

## Context

A background configuration-refresh thread updates a plain, non-`volatile` `boolean` feature-flag field every 30 seconds. A specific worker thread pool polls this field in a tight loop with no other synchronization.

## Symptoms

After a routine JVM minor-version upgrade, the affected worker thread pool stops picking up flag changes at all, continuing to run with a stale value indefinitely, while other parts of the system update normally.

## Impact

A feature intended to be quickly disabled in an emergency continued running for hours because the disabling flag update was never observed by the affected worker threads.

## Initial Hypotheses

- The configuration-refresh mechanism itself failed — checked and ruled out; logs show the refresh thread successfully updated the field on schedule.
- A deployment didn't actually roll out to the affected pool — checked and ruled out; process start time confirms it did.
- A JIT-level visibility issue — correct, confirmed by reproducing this chapter's exact demo pattern against the actual field.

## Evidence

The affected worker threads run a tight polling loop reading the plain `boolean` field with no other synchronization, reproducing the same visibility signature as the chapter's own controlled demo once tested directly against the actual field.

## Investigation Timeline

1. **Flag stopped propagating to one specific worker pool**, immediately after a JVM minor-version upgrade, with no matching code change.
2. **Refresh-mechanism and deployment hypotheses ruled out** using refresh logs and process start-time confirmation.
3. **Field's synchronization reviewed**, finding no `volatile` or other happens-before mechanism on the polled `boolean`.
4. **Reproduced deliberately**, confirming the pattern matches this chapter's own documented JIT-hoisting mechanism.

## Root Cause

The affected worker threads run a tight polling loop reading the plain `boolean` field with no other synchronization. Under the specific JIT tier and warm-up duration reached by this worker pool's workload, the JIT proved — correctly, for a single-threaded model — that the loop never itself modifies the field, and hoisted the read out of the loop. The prior JVM version's JIT compilation thresholds happened not to trigger this optimization within the process's typical lifetime; the upgraded JVM's more aggressive tiered compilation reached the optimizing tier sooner, exposing a latent bug that had been present all along.

## Immediate Mitigation

Restart the affected worker pool to reset its JIT compilation state as an immediate, temporary workaround.

## Permanent Fix

Mark the feature-flag field `volatile`, establishing the happens-before edge that makes the fix independent of JIT tier, warm-up duration, or JVM version.

## Alternatives Considered

Disabling aggressive JIT optimizations at the JVM-flag level. Rejected as treating the symptom for one field while leaving every other unsynchronized shared field in the codebase equally vulnerable, and at a real, ongoing performance cost application-wide.

## Trade-offs

`volatile` on a field read frequently in a hot loop has a small, real cost — forbidding certain compiler optimizations, plus the memory-barrier cost of the read/write itself. Accepted, since the alternative is an intermittent, environment-dependent correctness bug.

## Prevention

Any field written by one thread and read by another in a polling loop, with no other synchronization, is a code-review flag: is it `volatile`? "It worked in testing" is explicitly not evidence of correctness for this class of bug — the failure mode depends on JIT tier and warm-up duration, not just wall-clock testing time.

## Monitoring and Alerts

- A static analysis rule flagging any non-`volatile`, non-`final` field read in a loop on one thread and written on another with no other synchronization primitive — this is a purely structural check, catchable before the field is ever exercised long enough in production to trigger the optimizing JIT tier.
- A staleness check for any polled configuration value: log a warning if a worker's observed flag value hasn't changed across N consecutive refresh cycles while the source-of-truth value has, surfacing the exact symptom of this incident directly rather than only via a downstream feature-behavior complaint.

## Interview Story

This maps directly to the "why did a JVM upgrade break something with no code change" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a feature flag stopped propagating to one specific worker pool immediately after a routine JVM upgrade.
- **Task:** explain a correctness bug that appeared with no code change at all.
- **Action:** rule out the refresh mechanism and deployment rollout using existing logs and process metadata; inspect the field's synchronization directly; reproduce the exact JIT-hoisting mechanism this chapter documents, now triggered by the upgraded JVM's more aggressive compilation timing.
- **Result:** marked the field `volatile`, fixing the bug independent of JIT tier or JVM version, rather than working around the specific trigger.

## Staff-Level Discussion

This incident is the sharpest possible illustration of why "it worked in testing" is not evidence of thread-safety: the bug was present in every version of the code that ever ran, and its manifestation depended entirely on an environmental variable — JIT compilation timing — that no functional test exercises or controls. A JVM upgrade is a mundane, routine change that doesn't touch application code at all, which is exactly why it's dangerous: nobody reviews a JVM minor-version bump for its effect on unsynchronized shared state, because that effect is invisible until the specific optimization threshold is crossed. The Staff-level takeaway is treating any unsynchronized shared mutable field as a latent bug regardless of whether it has ever manifested — correctness under the Java Memory Model is a property of the code, not something empirically observed to hold so far.

## Related Handbook Chapters

- [Java Memory Model and Volatile](../syllabus/02-java/concurrency/java-memory-model-and-volatile.md) — canonical visibility and happens-before mechanics used here.
