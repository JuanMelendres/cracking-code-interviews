---
title: "JIT: Tiered Compilation, Inlining, and Deoptimization"
slug: jit-tiered-compilation-and-deoptimization
document_type: handbook-chapter
domain: jvm
status: draft
version: 1.0
last_reviewed: 2026-07-31
difficulty:
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - jvm-memory-layout-and-runtime-regions.md
related:
  - jvm-memory-layout-and-runtime-regions.md
  - safepoints-and-stop-the-world-mechanics.md
  - escape-analysis-and-scalar-replacement.md
  - ../../study-packs/week-16/05-jit-tiered-compilation-and-deoptimization.md
official_references:
  - https://docs.oracle.com/en/java/javase/21/vm/java-hotspot-virtual-machine-performance-enhancements.html
---

# JIT: Tiered Compilation, Inlining, and Deoptimization

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Mental Model](#mental-model)
4. [Definition and Purpose](#definition-and-purpose)
5. [Core Concepts](#core-concepts)
6. [Internal Implementation](#internal-implementation)
7. [Production Scenarios](#production-scenarios)
8. [Failure Modes and Debugging](#failure-modes-and-debugging)
9. [Trade-offs](#trade-offs)
10. [Decision Framework](#decision-framework)
11. [Common Mistakes](#common-mistakes)
12. [Best Practices](#best-practices)
13. [Interview Answer Framework](#interview-answer-framework)
14. [Interview Questions](#interview-questions)
15. [Summary](#summary)
16. [Key Takeaways](#key-takeaways)
17. [Cheat Sheet](#cheat-sheet)
18. [Flashcards](#flashcards)
19. [Practice Exercises](#practice-exercises)
20. [Solutions](#solutions)
21. [Additional Reading](#additional-reading)
22. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can explain why the JVM interprets bytecode before compiling it, name the tiered compilation levels HotSpot actually uses, explain what deoptimization is and why it's a necessary consequence of speculative optimization rather than a bug, and cite real measured evidence of both JIT warmup speedup and a genuine deoptimization triggered by a polymorphic call site.

## Why This Matters in Interviews

"Why is my service slow for the first few seconds/minutes after deploy, then fast" is one of the most common production-judgment questions tied to JIT behavior, and "JIT warmup" as a one-word answer is not sufficient at Senior level — the expected answer names the actual mechanism (interpreted execution, then progressively more aggressive tiered compilation) and, ideally, the specific operational mitigations (readiness gating, canary warmup traffic). Deoptimization is the sharper follow-up: most candidates have never heard of it, and it directly explains a real, surprising phenomenon — code that was fast suddenly getting slower at runtime, with no code change and no GC event, purely because a speculative optimization's assumption stopped holding.

## Mental Model

Think of the JIT as a translator who starts by reading a script aloud live (interpretation) while simultaneously watching which lines get performed over and over (profiling). Once a line has clearly proven itself popular, the translator prepares an increasingly polished, memorized performance of it — first a quick, rough memorization (C1), then, if it's popular enough to be worth the effort, a fully rehearsed, heavily optimized performance that takes shortcuts based on everything observed so far (C2). Those shortcuts are *bets* — "this call always goes to the same actor" — and if reality ever violates a bet (a different actor shows up), the translator has to stop, throw away the polished performance for that line, and fall back to reading it live again while re-learning what's actually happening now. That fallback is deoptimization.

## Definition and Purpose

**Tiered compilation** is HotSpot's strategy of executing bytecode through progressively more optimized tiers — pure interpretation, then C1 (the client compiler, fast to produce, moderately optimized, with added profiling instrumentation), then C2 (the server compiler, slower to produce, aggressively optimized using the profiling data C1 gathered) — rather than jumping straight to maximum optimization. **Deoptimization** is the mechanism by which a C1- or C2-compiled method is abandoned at runtime and execution falls back to the interpreter, triggered when a speculative optimization's underlying assumption (a monomorphic call site, a branch that's "always" taken, a type that's "always" this class) is violated by actual runtime behavior. Both exist for the same underlying reason: compiling everything maximally upfront is too slow to be practical, so the JVM defers aggressive optimization to code that's proven hot, and — because those aggressive optimizations are frequently speculative bets on observed behavior continuing — needs a safe, correct way to undo a bet that stops paying off.

## Core Concepts

### Tiered compilation has more than two levels, and they're all real, observable states

HotSpot's tiers are, in practice: level 0 (pure interpreter), level 1 (C1, no profiling — used for methods too simple to benefit from profiling), level 2 (C1, limited profiling), level 3 (C1, full profiling — the common "on-ramp" tier for hot methods), and level 4 (C2, fully optimized using the profiling data gathered at level 3). `-XX:+PrintCompilation` shows every one of these transitions live, including the specific tier number, method name, and bytecode size — this is real, directly observable behavior, not an abstraction.

### "Made not entrant" is how the JIT retires a superseded compiled version — related to, but distinct from, a true deoptimization

When a better-tier compilation of a method finishes (e.g., level 3 → level 4), the older version is marked "made not entrant" so new invocations use the better one — this is routine, expected tiered-compilation housekeeping, not a failure. A **true deoptimization** (an "uncommon trap") is different: it happens because a specific runtime assumption baked into an *already-executing* optimized compilation turned out to be wrong, forcing an in-flight bailout back to the interpreter, not just a "stop using the old version for new calls" housekeeping event.

### Deoptimization is a direct, necessary consequence of speculative optimization on real, observed type profiles

A classic trigger: a call site observed exclusively calling one concrete implementation of an interface (monomorphic) gets aggressively optimized around that assumption (potentially inlined directly, skipping virtual dispatch entirely). The moment a second concrete type appears at that same call site, the assumption is violated — this is measurably different from a bug; it's the direct, correct consequence of having optimized based on real, but incomplete, past observation.

## Internal Implementation

**Real JIT warmup speedup, pure interpreter vs. tiered compilation** (`practice/java/week-16/jit-compilation/WarmupSpeedupDemo.java`, a tight arithmetic loop, OpenJDK 21.0.12, steady-state after warmup):

| Mode | Steady-state ns/op |
|---|---|
| `-Xint` (pure interpreter, JIT disabled entirely) | ~330 ns/op |
| Default (tiered compilation enabled) | ~34 ns/op |
| `-XX:TieredStopAtLevel=1` (C1 only, no C2) | ~34.5 ns/op |

**~9.6x speedup from tiered compilation versus pure interpretation, measured directly on identical code and workload.** For this particular simple, allocation-free arithmetic loop, C1 alone (`TieredStopAtLevel=1`) captured nearly all of the available speedup — a real, useful data point that not every workload needs C2's more aggressive optimizations to realize most of the JIT benefit; workloads with virtual dispatch, allocation, or branch-heavy logic typically show a larger C1-to-C2 gap than this one does.

**Real `-XX:+PrintCompilation` output** for the hot method, showing the actual tier progression:

```
56    5       3       WarmupSpeedupDemo::compute (34 bytes)
56    6       4       WarmupSpeedupDemo::compute (34 bytes)
57    5       3       WarmupSpeedupDemo::compute (34 bytes)   made not entrant
```

Compilation `#5` at level 3 (C1, full profiling) compiles first; compilation `#6` at level 4 (C2, fully optimized) follows almost immediately after, using the profiling data level 3 gathered; the level-3 version is then marked "made not entrant" — the level-4 version has taken over, exactly the routine tiered-compilation housekeeping described above.

**A real, genuine deoptimization**, triggered by type-profile pollution (`practice/java/week-16/jit-compilation/DeoptDemo.java`): `sumAreas()` is warmed up thoroughly against a call site seeing only `Circle` (monomorphic), then the same method is called again with a mix of `Circle` and `Square`.

```java
// Phase 1: warm up sumAreas() with ONLY Circle -- JIT can speculate monomorphic
double t1 = sumAreas(monoBatch);   // Circle only

// Phase 2: SAME call site, now Circle + Square mixed
double t2 = sumAreas(mixedBatch);  // forces deoptimization if speculated monomorphic

// Phase 3: same mixed workload again, after recompilation
double t3 = sumAreas(mixedBatch);
```

Real `-XX:+PrintCompilation` output, filtered to the relevant method, timestamps in milliseconds:

```
37   19 %     4       DeoptDemo::sumAreas @ 11 (42 bytes)
38   20       4       DeoptDemo::sumAreas (42 bytes)
...
42   19 %     4       DeoptDemo::sumAreas @ 11 (42 bytes)   made not entrant
74   147 %     3       DeoptDemo::sumAreas @ 11 (42 bytes)
74   149 %     4       DeoptDemo::sumAreas @ 11 (42 bytes)
```

`sumAreas` reaches level 4 (C2) by timestamp 38ms while the workload is still monomorphic (Circle only). At timestamp 42ms — right as phase 2 introduces `Square` at the same call site — the level-4 compilation is marked "made not entrant": this is the deoptimization event, the compiled version's monomorphic-dispatch assumption has just been violated by real execution. Execution falls back to the interpreter/lower tiers and recompiles from scratch (level 3 at 74ms, level 4 again at 74ms) once the JIT has re-profiled the now-polymorphic call site.

**Real measured cost of this deoptimization**, from the same run's stdout:

| Phase | Workload | Elapsed |
|---|---|---|
| Phase 2 | Mixed Circle+Square, first exposure (deopt just happened) | 2.62 ms |
| Phase 3 | Same mixed workload, re-run after recompilation | 1.27 ms |

Phase 3 is roughly **2x faster than phase 2 on the identical workload** — direct, measured evidence that the deoptimization-and-recompilation cycle itself has a real, non-trivial one-time cost, distinct from the steady-state cost of running polymorphic (rather than monomorphic) code at all.

## Production Scenarios

**A service's p99 latency spikes briefly, with no GC event and no deploy, correlated with a specific feature flag rollout that introduces a second implementation of a previously-single-implementation interface at a hot call site.** This is exactly the deoptimization pattern measured above: a call site the JIT had speculatively optimized around a single observed type gets a second type introduced by the flag rollout, forcing a deoptimization-and-recompilation cycle for every affected thread hitting that code path around the same time. The fix isn't code-level (polymorphism at that call site may be entirely legitimate and intended) — it's operationally accepting a brief, one-time recompilation cost during rollout, or, if the cost is unacceptable, warming the JIT against the full expected type mix (both implementations) *before* the flag reaches production traffic.

## Failure Modes and Debugging

- **Symptom: service is measurably slower for the first several seconds to minutes after a deploy or scale-up event, then recovers.** This is expected JIT warmup (interpretation → C1 → C2, per the measured ~9.6x steady-state improvement above) — the mitigation is operational (readiness-gate traffic until warmup completes, or serve synthetic warmup traffic before accepting real requests), not a code fix.
- **Symptom: a brief, unexplained latency spike correlated with a feature flag or A/B rollout, no GC event, no deploy.** Suspect deoptimization from newly-introduced type diversity at a previously-monomorphic call site — confirm via `-XX:+PrintCompilation`, looking for "made not entrant" events around the same time window.
- **Anti-pattern to rule out first:** assuming every latency anomaly is GC-related — deoptimization produces a real, measurable, but GC-unrelated latency cost, and checking GC logs alone will show nothing.

## Trade-offs

Tiered compilation trades a small amount of steady-state peak performance (C1's output is less aggressively optimized than pure-C2-from-the-start would theoretically be) for dramatically faster time-to-reasonable-performance and lower total compilation overhead, since only methods that prove hot enough ever reach the expensive C2 tier. Speculative optimization (the mechanism behind most of C2's biggest wins, and the direct cause of deoptimization when violated) trades peak performance under the common case for occasional, real recompilation cost when the speculated assumption stops holding — a trade that's overwhelmingly favorable for workloads with stable type/branch profiles, and progressively less favorable for workloads with genuinely volatile polymorphism at hot call sites.

## Decision Framework

Accept JIT warmup cost as an operational fact of life for any JVM service and mitigate it operationally (readiness gating, warmup traffic) rather than trying to eliminate it — it's a direct consequence of tiered compilation's core trade-off and not something application code can opt out of. When investigating an unexplained, GC-unrelated latency spike, check `-XX:+PrintCompilation` for "made not entrant" events correlated with the spike's timing before assuming a code-level bug — deoptimization is a real, distinct root cause with its own diagnostic signature.

## Common Mistakes

- Saying "JIT warmup" without being able to name the actual mechanism (interpretation, then tiered C1/C2 compilation based on observed hotness).
- Treating every "made not entrant" log line as a problem — most are routine tiered-compilation housekeeping (a better-tier version superseding an older one), not evidence of a deoptimization.
- Not knowing deoptimization exists at all, and therefore misattributing a real, measured latency anomaly to GC or an application-level bug.
- Assuming C2 is always dramatically faster than C1 — measured directly, a simple arithmetic loop showed almost no difference between C1-only and full tiered compilation; the gap is workload-dependent.

## Anti-Patterns

Deliberately avoiding polymorphism at hot call sites purely out of fear of deoptimization cost, when the polymorphism is otherwise a legitimate, correct design choice — the one-time recompilation cost (measured here at roughly a 2x slowdown on one affected call for one workload) is real but typically small relative to premature design compromises made to avoid it; profile first, only restructure if deoptimization is measurably a real problem for the specific hot path in question.

## Best Practices

Warm a service's JIT against its full expected production type/branch profile (not just a narrow happy-path subset) before it receives real traffic, specifically to surface any deoptimization-prone call sites during a controlled warmup rather than during live traffic. Use `-XX:+PrintCompilation` (cheap, low-overhead) as a first-line diagnostic whenever a latency anomaly doesn't correlate with GC activity.

## Interview Answer Framework

### 30-Second Answer

The JVM interprets bytecode first, then progressively compiles hot methods through tiers — C1 (fast, moderately optimized, with profiling) then C2 (slower to produce, aggressively optimized using that profiling data). C2's aggressive optimizations are often speculative bets on observed behavior (like a call site always dispatching to one type); when reality violates that bet, the JVM deoptimizes — falls back to the interpreter and eventually recompiles with corrected assumptions.

### 2-Minute Answer

Definition: tiered compilation runs bytecode through interpretation, then C1, then C2, each progressively more optimized and more expensive to produce; deoptimization is falling back from a compiled version when its speculative assumptions are violated at runtime. Why it exists: compiling everything maximally upfront is too slow to be practical, so the JVM only pays C2's cost for methods proven hot, and speculative optimization (the source of many of C2's biggest wins) needs a safe undo mechanism. How it works: `-XX:+PrintCompilation` shows real tier transitions and "made not entrant" events directly. One trade-off: speculative optimization trades peak performance under the common case for a real, measurable recompilation cost when the speculation is violated. One production example: measured directly, a monomorphic call site optimized to C2 deoptimized the instant a second type was introduced, and the first mixed-type call after that deopt took roughly 2x longer than the identical call re-run after recompilation.

### 10-Minute Deep Dive

Cover: the full tier list (0 interpreter, 1/2/3 = C1 variants, 4 = C2) and what distinguishes them; the measured ~9.6x speedup from tiered compilation vs. pure interpretation, and the workload-dependent finding that C1-only nearly matched full tiered compilation for a simple arithmetic loop; "made not entrant" as routine tier-supersession housekeeping versus a true deoptimization as a violated runtime assumption; the measured real deoptimization (monomorphic Circle call site, C2-compiled, deoptimizing the instant Square is introduced) with the exact `-XX:+PrintCompilation` evidence; the measured 2.62ms → 1.27ms (phase2 → phase3) recovery, quantifying the one-time recompilation cost directly; the production scenario (feature-flag rollout introducing a second type at a hot call site causing a correlated, GC-unrelated latency spike) and its operational mitigation (pre-warm against the full expected type mix, or accept the one-time cost).

### Whiteboard Explanation

Draw a horizontal progression: "Interpreter" → "C1 (fast, profiled)" → "C2 (slow, aggressive)," with an arrow curving back from C2 to Interpreter labeled "deoptimization (assumption violated)." Below the C2 box, write "speculative bet: this call site always sees type X." Draw a second type (type Y) appearing at that call site, with an arrow pointing at the curved-back "deoptimization" arrow, to show explicitly what triggers the fallback.

### Production Example

An e-commerce checkout service's pricing-calculation call site sees only a single `StandardPricingStrategy` implementation for months, getting aggressively optimized by C2. A new `PromotionalPricingStrategy` is introduced behind a gradual rollout flag. During rollout, requests hitting the newly-polymorphic call site show a brief, real latency bump correlated exactly with the flag's exposure percentage increasing — confirmed via `-XX:+PrintCompilation` showing "made not entrant" for the pricing method at the same timestamps. The team decides the one-time cost is acceptable given the flag's gradual rollout schedule, rather than restructuring the pricing code to avoid polymorphism.

### Trade-offs to Mention

Speculative optimization trades common-case peak performance for a real, one-time recompilation cost whenever the speculated assumption is violated — measured directly at roughly 2x slower on the first affected call versus the same call re-run after recompilation.

### Common Candidate Mistakes

Saying "JIT warmup" without naming the tier mechanism; not knowing deoptimization exists, and therefore attributing a real deopt-caused latency spike to GC or a code bug.

### Typical Follow-Up Questions

"What's the actual difference between 'made not entrant' and a deoptimization?" → the former is routine tier-supersession; the latter is a runtime assumption violation forcing an in-flight bailout. "What's the operational mitigation for warmup-related latency?" → readiness gating or synthetic warmup traffic before accepting real requests. "How would you confirm a latency spike is deoptimization-related rather than GC-related?" → check `-XX:+PrintCompilation` for "made not entrant" events correlated with the spike, and confirm GC logs show nothing unusual at the same time.

### Senior-Level Expectations

Correctly names the tier progression and explains deoptimization as a consequence of speculative optimization, not a bug.

### Staff-Level Expectations

Connects deoptimization risk to operational rollout planning (pre-warming against the full expected type/branch profile before a gradual flag rollout reaches significant traffic), and distinguishes it clearly from GC-related latency as a diagnostic category with its own evidence trail, rather than defaulting to "probably GC" for any unexplained anomaly.

## Interview Questions

### Question 1

**Your service is measurably slower for the first minute after every deploy, then recovers. Explain why, and what would you do about it operationally.**

**Expected answer:** JIT warmup — code starts interpreted, then progressively compiles through C1/C2 as methods prove hot; mitigate via readiness gating or synthetic warmup traffic before accepting real requests, not a code-level fix.

**Common mistakes:** saying "JIT warmup" without explaining the actual mechanism; proposing a code change instead of an operational mitigation.

**Follow-up questions:** "How much of a speedup are we actually talking about?" (measured here: ~9.6x steady-state, interpreter vs. tiered JIT)

**Senior-level expectations:** correctly names the tier mechanism and an appropriate operational mitigation.

**Staff-level expectations:** proposes pre-warming against the full expected production type/branch profile specifically, not just generic traffic.

### Question 2

**A brief, unexplained latency spike correlates with a feature-flag rollout introducing a second implementation of an interface at a hot call site. No GC event, no deploy. What's happening?**

**Expected answer:** likely deoptimization — the call site was speculatively optimized around the single previously-observed type, and the new type violates that assumption, forcing a fallback and recompilation; confirm via `-XX:+PrintCompilation`'s "made not entrant" events correlated with the spike.

**Common mistakes:** assuming it must be GC-related without checking; not knowing deoptimization is a real, distinct root cause.

**Follow-up questions:** "How would you fix or mitigate this if the cost matters?" (pre-warm against the full type mix before the flag reaches significant traffic)

**Senior-level expectations:** correctly identifies deoptimization as the likely cause and names the confirming diagnostic.

**Staff-level expectations:** proposes the pre-warming mitigation and explicitly distinguishes this from a GC-related or code-bug explanation with reasoning, not just a guess.

## Summary

The JVM interprets bytecode first, then compiles proven-hot methods through tiers (C1: fast, profiled; C2: slow, aggressively optimized) — measured directly, this produces roughly a 9.6x steady-state speedup over pure interpretation for one representative workload, though the C1-to-C2 gap itself is workload-dependent. C2's aggressive optimizations frequently rely on speculative bets derived from observed runtime behavior (like a monomorphic call site); deoptimization is the JVM's mechanism for safely undoing such a bet when reality violates it, falling back to the interpreter and eventually recompiling with corrected assumptions. Measured directly: a monomorphic call site's C2 compilation was marked "made not entrant" the instant a second type appeared, and the first affected call took roughly 2x longer than the identical call re-run after recompilation — a real, quantified deoptimization cost, distinct from and often mistaken for a GC-related latency anomaly.

## Key Takeaways

- Tiered compilation (interpreter → C1 → C2) trades a small steady-state performance ceiling for much faster time-to-reasonable-performance and lower total compilation overhead.
- "Made not entrant" is usually routine tier-supersession, not a problem — a true deoptimization is a runtime-assumption violation forcing an in-flight bailout.
- Deoptimization is a direct, necessary consequence of speculative optimization on observed type/branch profiles, not a bug.
- Measured: ~9.6x speedup from tiered JIT vs. pure interpretation; a real deoptimization cost roughly 2x on the first affected call versus the same call re-run after recompilation.
- Diagnose GC-unrelated latency anomalies with `-XX:+PrintCompilation`, checking for "made not entrant" correlated with the spike, before assuming a code bug.
- Mitigate warmup and deopt-related latency operationally (readiness gating, pre-warming against the full expected type/branch profile) rather than avoiding legitimate polymorphism out of fear.

## Cheat Sheet

| Tier | Compiler | Profiling | Typical role |
|---|---|---|---|
| 0 | Interpreter | — | Cold-start execution |
| 1 | C1 | None | Simple methods with little to gain from profiling |
| 2/3 | C1 | Limited/Full | On-ramp for hot methods, gathers data for C2 |
| 4 | C2 | Uses tier-3 data | Fully optimized, for methods proven hot enough |
| Diagnostic flag | `-XX:+PrintCompilation` | — | Shows real tier transitions and "made not entrant"/deopt events |

## Flashcards

**Q: What's the difference between "made not entrant" and a true deoptimization?**
A: "Made not entrant" is routine — an older compiled version retired because a better-tier one exists. A true deoptimization is a runtime assumption (e.g., monomorphic dispatch) getting violated, forcing an in-flight bailout to the interpreter.

**Q: Why does speculative optimization exist if it can cause deoptimization?**
A: It's the source of C2's biggest wins (e.g., inlining a call site assumed monomorphic) — the trade is common-case peak performance for a real but occasional recompilation cost when the assumption is violated.

**Q: Measured directly, roughly how much steady-state speedup did tiered JIT compilation give over pure interpretation?**
A: ~9.6x (330ns/op interpreted vs. ~34ns/op tiered-compiled, same workload).

## Practice Exercises

1. Reproduce `practice/java/week-16/jit-compilation/WarmupSpeedupDemo.java` with `-Xint`, default, and `-XX:TieredStopAtLevel=1`. Confirm your own measured speedup ratios are in the same ballpark.
2. Reproduce `DeoptDemo.java` with `-XX:+PrintCompilation` and locate the "made not entrant" event for `sumAreas` yourself. Confirm its timestamp correlates with the introduction of the second type.

## Solutions

1. Ratios should be broadly similar (interpreter dramatically slower; C1-only close to full tiered for this simple workload) though exact numbers will vary by machine.
2. The "made not entrant" line for `DeoptDemo::sumAreas` at a C2 (level 4) tier should appear at a timestamp matching when phase 2 (the mixed-type workload) begins executing, not before.

## Additional Reading

- [Aleksey Shipilëv — JVM Anatomy Quarks: Deoptimization](https://shipilev.net/jvm/anatomy-quarks/2-deoptimization/)

## Official References

- [Java HotSpot VM Performance Enhancements (Java 21)](https://docs.oracle.com/en/java/javase/21/vm/java-hotspot-virtual-machine-performance-enhancements.html)
