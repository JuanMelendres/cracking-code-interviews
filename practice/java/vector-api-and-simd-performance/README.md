# Vector API and SIMD Performance — Real, Executed Demo

Backs [Vector API and SIMD Performance](../../../syllabus/16-performance-jvm/vector-api-and-simd-performance.md).
Real OpenJDK 21.0.12 (`jdk.incubator.vector`, JEP 460's 6th incubator round
as of JDK 21), Apple M4 (arm64, 128-bit NEON), macOS.

## Setup and run

```bash
javac --add-modules jdk.incubator.vector -d out src/VectorApiDemo.java
java --add-modules jdk.incubator.vector -Xmx4g -cp out VectorApiDemo
java --add-modules jdk.incubator.vector -XX:-UseSuperWord -Xmx4g -cp out VectorApiDemo
```

Real captured output: [`output-transcript.txt`](output-transcript.txt).

## What it proves

A real element-wise fused-multiply-add (`out[i] = a[i]*b[i] + a[i]`) over a
1-million-element float array, repeated 2,000 times (cache-resident,
compute-bound, not memory-bandwidth-bound), computed two ways: an ordinary
scalar loop using `Math.fma` (a true single-rounding FMA, for a fair
apples-to-apples comparison against the Vector API's own `fma()`), and the
Vector API's explicit SIMD loop. Every one of the 1,000,000 outputs is
verified bit-for-bit identical between the two implementations.

**Real result under default JVM settings — essentially no difference:**

```
Scalar loop:  201ms
Vector API:   206ms
Speedup:      0.98x
```

**The honest reason, verified directly**: HotSpot's C2 JIT compiler already
auto-vectorizes simple, regular loops like this one via its own SuperWord
optimization (`-XX:+UseSuperWord`, on by default) — the "scalar" loop was
never actually running as pure scalar machine code to begin with.

**Real result with `-XX:-UseSuperWord` (auto-vectorization disabled — a genuinely scalar baseline):**

```
Scalar loop:  471ms
Vector API:   212ms
Speedup:      2.22x
```

**A real, measured ~2.2x speedup — but only once the JIT's own automatic
vectorization is disabled.** The honest lesson this demo exists to teach:
the Vector API's real value isn't "free SIMD over whatever the JIT already
does" — for simple, regular array loops, the JIT frequently already gets
you most of the way there automatically. The Vector API earns its keep for
patterns the auto-vectorizer can't safely or effectively handle: complex
control flow inside the loop body, gather/scatter access patterns,
cross-lane shuffles, or explicit control over the exact SIMD width and
instruction selection used.

## A note on correctness and floating-point rounding

An earlier version of this demo compared the Vector API's `fma()` against a
plain scalar `a[i]*b[i] + a[i]` and found the outputs were *not*
bit-identical, despite both being "the same" arithmetic. The real reason:
`fma()` performs the multiply and add as a single IEEE 754 rounding step,
while separate `*` and `+` in Java perform two independent roundings —
genuinely different floating-point results, not a bug in either
implementation. Fixed by using `Math.fma()` for the scalar baseline too,
making both sides compute the identical single-rounding operation.
