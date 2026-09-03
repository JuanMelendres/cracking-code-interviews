---
title: "Unguarded Debug-Log String Concatenation as the Real Checkout Bottleneck"
document_type: production-cookbook-entry
domain: performance
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md
  - ../syllabus/13-observability/performance-methodology-and-slo-error-budgets.md
source: handbook/performance/profiling-jfr-and-flame-graphs.md#production-scenarios
---

# Unguarded Debug-Log String Concatenation as the Real Checkout Bottleneck

## Context

A checkout service's discount-calculation logic includes a nested iteration over promotional rules — code that "looked" computationally heavy to a reviewer scanning it. A logging statement inside the same hot path built a debug message using string concatenation on every request, regardless of whether debug logging was even enabled, because the concatenation happened before the log level check.

## Symptoms

Checkout p99 latency exceeded its SLO during moderate load.

## Impact

A production latency SLO violation on the checkout path, initially misdirected toward the wrong piece of code for remediation effort.

## Initial Hypotheses

The team's initial code review focused on the recently-added discount-calculation loop, hypothesizing its algorithmic complexity needed optimizing, since nested iteration over promotional rules "looked" computationally heavy.

## Evidence

A real JFR profile captured during a load test — using the `-XX:StartFlightRecording=settings=profile` approach — showed the discount loop consuming a modest, unremarkable fraction of CPU samples; the dominant hotspot was the logging statement's string concatenation, executing unconditionally on every request regardless of whether debug logging was even enabled.

## Investigation Timeline

1. Checkout p99 latency SLO violation observed during moderate load.
2. Discount-calculation loop's nested iteration identified in initial code review as the suspected cause, based on how the code looked rather than a measurement.
3. A real JFR profile captured during a load test using `-XX:StartFlightRecording=settings=profile`.
4. Profile results inspected, showing the discount loop consuming only a modest, unremarkable fraction of CPU samples.
5. The dominant hotspot identified instead as a logging statement's unconditional string concatenation, executing on every request regardless of the active log level, because the concatenation occurred before the log-level check.

## Root Cause

The code that "looked" expensive (nested loops, business logic) was not the real cost; an innocuous, seemingly free logging line was — specifically because the debug message's string concatenation ran unconditionally, ahead of the log-level check that would have skipped it.

## Immediate Mitigation

Wrapped the debug log call in an explicit `isDebugEnabled()` check, avoiding the string concatenation entirely when the log level wouldn't emit it.

## Permanent Fix

Added a lint rule flagging string-concatenation-based log calls not guarded by a level check, and added periodic JFR profiling of the checkout path to the team's standard load-test process rather than relying on code review intuition alone.

## Alternatives Considered

None recorded beyond the `isDebugEnabled()` guard and the lint rule — both presented as the direct, sufficient fix rather than alternatives to weigh.

## Trade-offs

Continuous low-overhead profiling in load tests is a small real infrastructure cost, accepted in exchange for catching this class of issue before it reaches production under real load.

## Prevention

Any performance investigation now starts from a real profile, not from "which code looks suspicious" — the exact discipline this chapter's own demo is built to instill.

## Monitoring and Alerts

- Run the lint rule flagging unguarded string-concatenation log calls as a required CI check across the codebase, not just the checkout path, since the same unguarded-concatenation pattern can exist in any hot-path logging statement written before this incident's lesson was codified.
- Add periodic JFR profiling of the checkout path (and other latency-SLO-bound paths) to the standard load-test process, as the Permanent Fix specifies, tracking hotspot composition over time so a newly introduced unguarded log call or similarly "invisible" cost is caught in a routine profiling run rather than only after an SLO violation.
- Track p99 latency against log-level configuration as a diagnostic cross-check; if enabling or disabling debug logging measurably shifts checkout latency, that is itself a signal an unguarded concatenation (or an equivalent always-executed debug-only cost) exists somewhere in the path.

## Interview Story

This maps directly to "profiling reveals what intuition misses" backed by a real load-test profile. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** checkout p99 latency exceeded its SLO, and the team's initial suspicion focused on a nested-loop discount-calculation feature that "looked" computationally expensive.
- **Task:** find the actual bottleneck rather than optimizing based on how the code appeared.
- **Action:** captured a real JFR profile during a load test, which showed the discount loop consuming only a modest fraction of CPU time, and instead identified an unconditional debug-log string concatenation, executing before the log-level check, as the dominant hotspot.
- **Result:** guarded the log call with an explicit `isDebugEnabled()` check as an immediate fix, then added a lint rule and standard load-test JFR profiling to catch the same class of issue going forward.

## Staff-Level Discussion

This incident is a clean, concrete argument for a discipline that is easy to state abstractly and easy to skip under time pressure: start every performance investigation from a real profile, not from which code looks suspicious to a reviewer's eye. The near-miss here — the team's initial review effort was aimed entirely at the discount loop, which turned out to be innocent — represents real, wasted investigation time that a five-minute JFR capture would have avoided entirely, and that gap between "looks expensive" and "measured expensive" is exactly the intuition profiling exists to correct. The systemic fix (a lint rule plus routine load-test profiling) matters more than the one-line `isDebugEnabled()` guard, because unconditional, pre-log-level-check string concatenation is a pattern that can be introduced by any engineer writing what looks like an entirely ordinary debug log statement — a Staff engineer should recognize that the fix needs to prevent the pattern at write time (via the lint rule) and catch it under load at test time (via routine profiling), rather than depending on the next instance also happening to cause an SLO violation severe enough to trigger another manual investigation.

## Related Handbook Chapters

- [Profiling, JFR, and Flame Graphs](../syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md) — canonical JFR profiling methodology and the checkout-service measurement this incident reproduces.
- [Performance Methodology and SLO/Error Budgets](../syllabus/13-observability/performance-methodology-and-slo-error-budgets.md) — the SLO context that made this latency regression an actionable, prioritized incident rather than an unnoticed regression.
