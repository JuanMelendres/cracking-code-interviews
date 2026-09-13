---
title: "Cheat Sheet: JIT: Tiered Compilation, Inlining, and Deoptimization"
slug: jit-tiered-compilation-and-deoptimization
document_type: cheat-sheet
domain: jvm
topic_id: T-308
canonical: ../syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md
last_updated: 2026-09-12
---

# JIT: Tiered Compilation, Inlining, and Deoptimization

**Canonical chapter:** [`syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md`](../syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md)

## Mental Model

The JIT is a translator who starts by reading a script aloud live (interpretation) while watching which lines get performed repeatedly (profiling). A popular line earns an increasingly polished, memorized performance — first a quick rough memorization (C1), then, if popular enough, a fully rehearsed, heavily optimized performance that takes shortcuts based on everything observed so far (C2). Those shortcuts are bets ("this call always goes to the same target"); if reality ever violates a bet, the translator stops, throws away the polished performance for that line, and falls back to reading it live again — that fallback is deoptimization.

## Decision Table

| Tier | Compiler | Profiling | Typical role |
|---|---|---|---|
| 0 | Interpreter | — | Cold-start execution |
| 1 | C1 | None | Simple methods with little to gain from profiling |
| 2/3 | C1 | Limited/Full | On-ramp for hot methods, gathers data for C2 |
| 4 | C2 | Uses tier-3 data | Fully optimized, for methods proven hot enough |
| Diagnostic flag | `-XX:+PrintCompilation` | — | Shows real tier transitions and "made not entrant"/deopt events |

## Decision Framework

Accept JIT warmup cost as an operational fact of life for any JVM service and mitigate it operationally (readiness gating, warmup traffic) rather than trying to eliminate it. When investigating an unexplained, GC-unrelated latency spike, check `-XX:+PrintCompilation` for "made not entrant" events correlated with the spike's timing before assuming a code-level bug.

## Common Pitfalls

- Saying "JIT warmup" without naming the actual mechanism (interpretation, then tiered C1/C2 compilation based on observed hotness).
- Treating every "made not entrant" log line as a problem — most are routine housekeeping (a better tier superseding an older one), not a deoptimization.
- Not knowing deoptimization exists at all, misattributing a real, measured latency anomaly to GC or an application bug.
- Assuming C2 is always dramatically faster than C1 — the real gap is workload-dependent, sometimes negligible.

## Interview Answer Skeleton

30 seconds: methods start interpreted, get profiled, and get progressively compiled (C1 then C2) as they prove hot; C2's speculative optimizations can be undone (deoptimization) if their assumptions are violated. 2 minutes: add the tier table and the distinction between routine "made not entrant" and a true deopt. Deep dive: walk a latency-spike investigation that used `-XX:+PrintCompilation` to correlate a deopt event with the spike, ruling out GC.

## Production Warning Signs

- Unexplained latency spike with no matching GC log entry — check compilation log for a correlated deoptimization.
- New deploy showing elevated latency that settles over minutes — expected JIT warmup, mitigate with readiness gating, not code changes.
- Polymorphic call sites in a hot path that were previously monomorphic — a likely deoptimization trigger.

## Related

- [JVM Memory Layout and Runtime Regions](../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md)
- [Escape Analysis and Scalar Replacement](../syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md)
- [Safepoints and Stop-the-World Mechanics](../syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md)
