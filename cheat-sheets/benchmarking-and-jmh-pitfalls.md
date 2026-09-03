---
title: "Cheat Sheet: Benchmarking & JMH Pitfalls"
slug: benchmarking-and-jmh-pitfalls
document_type: cheat-sheet
domain: jvm
topic_id: T-1203
canonical: ../handbook/jvm/benchmarking-and-jmh-pitfalls.md
last_updated: 2026-09-01
---

# Benchmarking & JMH Pitfalls

**Canonical chapter:** [`syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md`](../syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md)

## Core Mental Model

A JIT-compiled runtime does not execute your code as written — it executes whatever the compiler proves is behaviorally equivalent, which often means executing *less* than what you wrote, or executing it once and reusing the answer, whenever it can prove doing so has no observable effect. A microbenchmark's entire job is to make the code path under test *look* like it has an effect the JIT cannot optimize away — without a harness helping deliberately, whatever "effect" a handwritten loop appears to produce is exactly the kind of code a good JIT is designed to eliminate.

## Essential Definitions

- **Warmup** — separates "JIT hasn't compiled this yet" (interpreted/C1) from steady-state (C2) performance; JMH runs and discards warmup iterations before measuring.
- **Dead-code elimination (DCE)** — the JIT removes a computation whose result is never consumed (never returned, never stored externally).
- **Constant folding** — a computation is replaced with a precomputed value when its inputs never vary (`static final` compile-time constants per JLS 15.29).
- **`Blackhole`** — an explicit "this value is used" signal to the JIT (`blackhole.consume(result)`, or automatic when a `@Benchmark` method returns a value) — the actual mechanism preventing DCE, not a style nicety.

## Decision Table

1. Is the number small enough (ns to low µs per operation) that JIT warmup/optimization genuinely matter? Yes → use JMH, not a hand-timed loop.
2. Does every `@Benchmark` method return its result or explicitly consume it via `Blackhole`? No → real risk of measuring dead code.
3. Are any benchmark inputs `static final` fields or literals? Yes → move them into `@State` instance fields set at runtime, defensively — even if a specific run shows no measurable folding.
4. Is a specific performance number about to justify an architectural decision? Yes → verify it was measured with proper warmup and forking, not accepted on the strength of where it was published.

**Trade-offs:**

| Choice | Helps | Hurts |
|---|---|---|
| Hand-written timing loop | Fast, zero dependencies | Vulnerable to warmup skew, DCE, constant folding, no built-in defense |
| JMH with default settings | Structural defenses (warmup, forking, blackhole injection) | Real setup cost vs. a five-line loop |
| Trusting a documented benchmarking claim | Fast, no verification cost | A documented pitfall (constant folding) can fail to reproduce on a different JVM/version |

## Key Numbers (real, executed JMH 1.37 on JDK 21.0.12, two blackhole configurations)

Dead-code elimination — a real, decisive ~22% gap:

```
broken_deadCodeEliminated (void, discarded result): 2.748ns ± 0.113
baseline_realComputation (correctly returned):        3.541ns ± 0.053
```

Constant folding — an honestly-reported non-reproduction on this specific JVM/operation:

```
broken_constantFolded: 3.484ns ± 0.025 (statistically indistinguishable from the honest baseline)
```

Modern JVMs can defend against DCE automatically: with HotSpot's Compiler Blackholes auto-detected (JMH 1.37 default on this JDK 21 build), all four benchmarks measured identically at ~2.7ns — forcing `-Djmh.blackhole.autoDetect=false` reproduced the textbook DCE gap.

## Common Pitfalls

- Timing a loop with `System.nanoTime()` around a small number of iterations and treating the result as steady-state performance.
- Writing a JMH benchmark whose method returns `void` and never calls `Blackhole.consume()` — a real, measured ~22% cost.
- Using `static final` fields for benchmark inputs "for convenience," a live constant-folding risk even when it doesn't happen to manifest.
- Assuming a benchmarking pitfall from an old blog post applies unconditionally to a current JVM version rather than re-verifying it.

## Interview Answer Skeleton

**30-sec:** `System.nanoTime()` around a loop isn't a valid microbenchmark — a JIT can eliminate computations whose result is never consumed, or fold away calls whose inputs never vary, and early iterations run under a different compilation tier than steady state. JMH defends against both via warmup and `Blackhole` consumption.

**2-min:** Add the measured ~22% DCE gap (2.748ns discarded vs. 3.541ns correctly returned) and the honest non-reproduction of constant folding on this specific JVM/operation — the chapter's central lesson: verify a pitfall's applicability on your actual target runtime rather than trusting an unconditional claim.

**Whiteboard:** A benchmark method computing a value, branching on "is the value consumed?" No → "JIT proves no observable effect → DCE (measured 2.75ns, ~22% too fast)." Yes → "JIT must actually produce the value → real cost (measured 3.54ns)." Circle the gap: "this is not a rounding error — the broken version is measuring an empty method body."

**Staff-level framing:** The real organizational risk isn't one bad benchmark — it's an unverified performance claim propagating through design docs because it was never subjected to rigor. Argue for a lightweight standard ("any performance claim cited in an ADR must link to a JMH benchmark") and weigh that cost against a wrong architectural bet made on a fictitious number.

## Production Warning Signs

- A "5x faster" serialization-library claim citing a hand-written `System.nanoTime()` loop whose result was never read — re-measured under JMH with proper warmup/forking, the real improvement was a far more modest ~15%.
- A benchmark that returns `void` and discards its computed result — measures an empty method body, not the intended computation.
- A performance claim used to justify a migration with no visible benchmark source code — a skeptical reviewer asking to see it is enough to stall the decision, and should be.

## Related

- `syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md`
- `syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md`
- `syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md`
