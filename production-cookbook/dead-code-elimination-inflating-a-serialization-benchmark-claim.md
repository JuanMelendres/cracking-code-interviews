---
title: "Dead-Code Elimination Inflating a Serialization Benchmark Claim"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/jvm/benchmarking-and-jmh-pitfalls.md
source: handbook/jvm/benchmarking-and-jmh-pitfalls.md#production-scenarios
---

# Dead-Code Elimination Inflating a Serialization Benchmark Claim

## Context

A team's internal wiki cited a benchmark claiming a new serialization library was "5x faster" than the one currently in production, used to justify a proposed migration.

## Symptoms

The proposed migration stalled when a skeptical reviewer asked to see the benchmark code behind the "5x faster" claim.

## Impact

A migration decision was nearly made on a fabricated-by-measurement-error performance claim, which — had it gone unchallenged — would have consumed real migration engineering effort for a benefit that did not actually exist at the claimed magnitude.

## Initial Hypotheses

- The new library genuinely is faster; the benchmark used unrealistic (too-small or too-uniform) input data; the benchmark measured something other than steady-state performance — these were the hypotheses considered once the benchmark was questioned.

## Evidence

The cited benchmark was a hand-written loop timing 100 iterations with `System.nanoTime()`, and the serialized result was written to a local variable that was never read afterward.

## Investigation Timeline

1. **Migration proposal stalled** when a reviewer asked to see the benchmark code supporting the "5x faster" claim.
2. **Benchmark code inspected directly**, revealing a hand-written 100-iteration loop timed with `System.nanoTime()`.
3. **Two compounding measurement errors identified**: too few iterations for the JIT to reach steady-state compilation, and an unconsumed result enabling dead-code elimination of large parts of the "faster" library's serialization path.
4. **Re-measurement performed under JMH** with proper warmup, forking, and result consumption via the blackhole, producing a corrected number.

## Root Cause

Two compounding errors: 100 iterations was nowhere near enough for the JIT to reach steady-state compilation, so the benchmark partly measured interpreted/C1 execution; and because the serialized result was never read, the JIT was free to eliminate large parts of the "faster" library's serialization path as dead code, while the baseline library's calls — which had a necessary side effect elsewhere — were not eliminated. The benchmark was comparing a partially-real cost against a partially-eliminated one.

## Immediate Mitigation

The migration was paused pending a re-measurement.

## Permanent Fix

Re-ran the comparison under JMH with proper warmup, forking, and both candidates returning their result so JMH's blackhole would consume it. The real, correctly-measured difference was a genuine but far more modest roughly 15% improvement — still worth adopting, but nowhere near the original claim.

## Alternatives Considered

Accepting the original "5x faster" figure with a smaller, informal sanity-check re-run rather than a full JMH benchmark. Rejected because the original error was structural (dead-code elimination and insufficient warmup), not a one-off fluke — only a properly configured JMH run with warmup, forking, and consumed results reliably rules out both mechanisms at once.

## Trade-offs

The correct benchmark took real engineering time to write and run — multiple forks, warmup iterations, statistical reporting — versus the five minutes the original hand-rolled loop took.

## Prevention

The team adopted a standing rule: any performance claim used to justify an architectural decision must cite a JMH benchmark (or equivalent rigor for non-JVM code) with its source, not a hand-timed loop.

## Monitoring and Alerts

- A standing policy gate requiring a citable, reproducible benchmark (JMH or equivalent rigor) before any performance claim is used to justify an architectural or migration decision.
- Benchmark source code linked directly alongside any performance number cited in planning documents, so a reviewer can check for warmup, forking, and result-consumption discipline without needing to ask.

## Interview Story

This maps to a "how do you validate a performance claim before acting on it" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a proposed library migration was justified by a benchmark claiming a 5x speedup.
- **Task:** determine whether the claim was trustworthy before committing engineering effort to the migration.
- **Action:** inspected the benchmark code, found a hand-written 100-iteration loop with an unconsumed result, and identified both insufficient warmup and dead-code elimination as compounding errors.
- **Result:** re-ran the comparison under JMH with proper warmup, forking, and consumed results, revealing a genuine but far more modest roughly 15% improvement.

## Staff-Level Discussion

"We had to walk back a stated performance number because the original benchmark measured dead code" is a concrete, credible story that shows understanding of *why* the discipline matters, not just that JMH is the "correct tool" to name — the specific failure mode (an unconsumed result letting the JIT eliminate real work) is invisible unless someone actually reads the benchmark's source, which is precisely why the permanent fix is a policy requiring the benchmark's source to be citable and reviewed, not a reminder to "use JMH." The organizational risk this reveals is that performance claims travel through an organization as bare numbers, disconnected from the methodology that produced them, and a wrong number can drive a real architectural decision — a migration, a technology choice, a capacity plan — before anyone with the relevant expertise has a chance to check the number's provenance.

## Related Handbook Chapters

- [Benchmarking & JMH Pitfalls](../handbook/jvm/benchmarking-and-jmh-pitfalls.md) — canonical dead-code-elimination and warmup-discipline mechanism used here.
