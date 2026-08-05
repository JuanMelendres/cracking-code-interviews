---
title: "StackOverflowError Misdiagnosed as a Heap Sizing Problem"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/jvm/jvm-memory-layout-and-runtime-regions.md
source: handbook/jvm/jvm-memory-layout-and-runtime-regions.md#production-scenarios
---

# StackOverflowError Misdiagnosed as a Heap Sizing Problem

## Context

A service processes requests along multiple paths, including a recursive parser or recursive data-structure traversal for one specific endpoint that can encounter adversarially deep nesting.

## Symptoms

The service throws `StackOverflowError` on one specific request path, but the process's heap and overall memory usage look completely normal.

## Impact

A request-handling failure on a specific path, with no accompanying resource-pressure signal that would normally accompany a memory-related error, delaying correct diagnosis if the on-call engineer's first instinct is to check heap sizing.

## Initial Hypotheses

- A heap sizing problem, since the error is an `Error` type associated with memory exhaustion in most engineers' first-instinct mental model — a natural but incorrect first read.
- Unusually deep recursion hitting the per-thread stack limit, unrelated to heap capacity — correct.

## Evidence

`StackOverflowError` on this specific path, with heap and overall memory usage normal throughout, is the direct production signature of a per-thread stack limit being hit by unusually deep recursion — not a heap sizing problem at all.

## Investigation Timeline

1. **`StackOverflowError` observed on one specific request path**, with no corresponding heap-pressure symptoms elsewhere in the process.
2. **Heap-sizing hypothesis initially considered**, given the error's association with memory exhaustion in general.
3. **Heap and overall memory metrics checked directly**, found completely normal throughout the incident window.
4. **Request path traced**, identifying a recursive parser or traversal processing adversarially deep nested input as the specific mechanism.

## Root Cause

A recursive parser on deeply nested input, or a recursive data-structure traversal on an adversarially deep tree, exhausts the per-thread stack — a fixed-size region entirely separate from the heap — regardless of how much heap capacity is available.

## Immediate Mitigation

Reject or truncate the specific request(s) triggering the deep recursion while a permanent fix is developed.

## Permanent Fix

Bound the recursion depth by validating input nesting depth before processing. If the recursion is legitimate and bounded but simply deep, increase `-Xss` for the affected thread pool specifically, via the `Thread` constructor's stack-size parameter, or the JVM-wide `-Xss` flag if acceptable for all threads — never by increasing `-Xmx`, which has no effect on stack capacity.

## Alternatives Considered

Increasing `-Xmx` as a general "add more memory" response. Rejected outright — `-Xmx` governs heap size and has no effect on per-thread stack capacity, so it would not address the actual cause at all.

## Trade-offs

Bounding input nesting depth rejects some legitimately deep (but not adversarial) inputs, requiring a deliberate choice of an acceptable maximum. Accepted, since unbounded recursion depth is not safely supportable regardless of stack size — any fixed `-Xss` value has some depth that exceeds it.

## Prevention

Any recursive processing of external, untrusted input (a parser, a tree traversal) should have an explicit, validated depth bound as a matter of course, treated with the same seriousness as any other unbounded-input-size vulnerability.

## Monitoring and Alerts

- `StackOverflowError` occurrences tracked and alerted as their own distinct error category, separate from generic exception or heap-pressure alerting, so on-call is directed toward stack-specific diagnosis immediately rather than defaulting to a heap-sizing investigation.
- Input nesting depth logged (or sampled) for any endpoint accepting recursively structured input, surfacing unusually deep inputs before they trigger a failure, not only after.

## Interview Story

This maps to a "your service threw StackOverflowError, walk through diagnosing it" question, specifically testing whether the candidate defaults to a heap-sizing reflex. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a `StackOverflowError` on one specific request path, with heap and memory metrics showing nothing unusual.
- **Task:** diagnose correctly without defaulting to the common but wrong "increase heap" reflex.
- **Action:** check heap and memory metrics directly, confirming they're normal; trace the specific request path to a recursive parser or traversal; recognize the per-thread stack, not the heap, as the exhausted resource.
- **Result:** added an explicit input-nesting-depth bound as the primary fix, with `-Xss` tuning reserved for legitimately deep but bounded recursion.

## Staff-Level Discussion

This incident is a clean test of whether an engineer's mental model of JVM memory regions is accurate or merely associative: `StackOverflowError` and `OutOfMemoryError` are both `Error` subtypes commonly grouped together as "memory problems," but they exhaust two structurally separate regions — the per-thread stack and the shared heap — governed by two entirely different JVM flags (`-Xss` and `-Xmx` respectively), with zero overlap in remediation. Reaching for `-Xmx` here wastes investigation time and delivers no fix. A Staff engineer should recognize this distinction immediately and, more importantly, treat unbounded-depth recursion over untrusted input as a design defect worth catching in review — the same category of concern as an unbounded loop over untrusted input size, just less immediately obvious because "depth" doesn't read as naturally as "size" as an attacker-influenced input dimension.

## Related Handbook Chapters

- [JVM Memory Layout and Runtime Regions](../handbook/jvm/jvm-memory-layout-and-runtime-regions.md) — canonical stack-vs-heap region mechanics used here.
