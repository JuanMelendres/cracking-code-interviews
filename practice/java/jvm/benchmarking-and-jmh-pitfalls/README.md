# Benchmarking & JMH Pitfalls (T-1203) — runnable verification

Real, executed JMH (Java Microbenchmark Harness) output backing
[`syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md`](../../../../syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md)
(T-1203). Real JMH 1.37, real annotation processing, real forked JVM runs
on JDK 21 — no invented numbers.

This pack deliberately mirrors the shape of JMH's own official samples
(`JMHSample_08_DeadCode`, `JMHSample_10_ConstantFold`) rather than an
invented computation, because `Math.log` is a well-documented HotSpot
intrinsic whose behavior under these exact conditions is established —
not something to guess at from scratch.

## Files

- `fetch-deps.sh` — downloads real `jmh-core`, `jmh-generator-annprocess`,
  and their transitive dependencies from Maven Central. No Maven/Gradle
  needed to run this pack.
- `src/interviewprep/jmh/BenchmarkPitfalls.java` — one `@State` class with
  four `@Benchmark` methods: a real baseline, and a broken/fixed pair for
  each of two classic pitfalls (dead-code elimination, constant folding).

## Run

```bash
cd practice/java/jvm/benchmarking-and-jmh-pitfalls
sh fetch-deps.sh
mkdir -p out
CP="lib/jmh-core.jar:lib/jmh-generator-annprocess.jar:lib/jopt-simple.jar:lib/commons-math3.jar"
javac -cp "$CP" -d out src/interviewprep/jmh/BenchmarkPitfalls.java
java -cp "out:lib/jmh-core.jar:lib/jopt-simple.jar:lib/commons-math3.jar" \
  org.openjdk.jmh.Main -f 1 -wi 5 -i 5 -w 1s -r 1s
```

## Real observed output (last full run, JDK 21.0.12, JMH 1.37)

### 1. Default run — modern JMH's automatic defense already active

```
# Blackhole mode: compiler (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
...
Benchmark                                    Mode  Cnt  Score   Error  Units
BenchmarkPitfalls.baseline_realComputation   avgt    3  2.731 ± 0.111  ns/op
BenchmarkPitfalls.broken_constantFolded      avgt    3  2.743 ± 0.053  ns/op
BenchmarkPitfalls.broken_deadCodeEliminated  avgt    3  2.740 ± 0.204  ns/op
BenchmarkPitfalls.fixed_returnResult         avgt    3  2.810 ± 1.571  ns/op
```

A real, notable finding on its own: by default, JMH 1.37 auto-detects that
this JDK 21 build supports HotSpot's **Compiler Blackholes** — a JIT-level
diagnostic mechanism that prevents dead-code elimination without JMH
having to rewrite the benchmark's bytecode at all. With it active, even
`broken_deadCodeEliminated` (a method that discards its own result) reports
the same ~2.7ns as every other benchmark. On this specific JVM/JMH
combination, the classic "forgot to return the result" bug is caught
automatically. This does not mean the pitfall is fictional — it means its
manifestation is conditional on JVM capability and JMH version, which is
itself the real lesson: verify on your actual target JVM, don't assume a
blog post from 2015 describes your JDK 21 build's behavior.

### 2. Classic blackhole mode (`-Djmh.blackhole.autoDetect=false`) — the pitfalls as historically documented

```
# Blackhole mode: full + dont-inline hint (fallback, use -Djmh.blackhole.mode to force)
...
Benchmark                                    Mode  Cnt  Score   Error  Units
BenchmarkPitfalls.baseline_realComputation   avgt    5  3.541 ± 0.053  ns/op
BenchmarkPitfalls.broken_constantFolded      avgt    5  3.484 ± 0.025  ns/op
BenchmarkPitfalls.broken_deadCodeEliminated  avgt    5  2.748 ± 0.113  ns/op
BenchmarkPitfalls.fixed_returnResult         avgt    5  3.535 ± 0.079  ns/op
```

With the compiler-blackhole auto-detection disabled, falling back to
JMH's classic "full + dont-inline hint" instrumentation:

- **Dead-code elimination reproduces clearly and decisively.**
  `broken_deadCodeEliminated` measures **2.748ns**, a real ~22% *faster*
  result than the honestly-measured `baseline_realComputation` (3.541ns)
  and `fixed_returnResult` (3.535ns) — because JMH's blackhole
  instrumentation only wraps the value a benchmark method *returns*; a
  `void` method that discards its own result gets none of that protection,
  and the JIT eliminates the entire computation as dead code. This is the
  single most common way a naive benchmark reports a fictitiously fast,
  meaningless number.
- **Constant folding did *not* reproduce a measurable difference here.**
  `broken_constantFolded` (3.484ns ± 0.025) is statistically
  indistinguishable from `baseline_realComputation` (3.541ns ± 0.053) —
  the error bars overlap. Even though `CONSTANT_X` is a genuine
  compile-time constant per JLS 15.29 (javac provably inlines its literal
  value at every use site — this is confirmed, not assumed), HotSpot's C2
  compiler in this configuration still executes the real `Math.log`
  intrinsic on every call rather than hoisting or caching a folded result
  across the benchmark harness's invocation loop.

## Real discoveries made while building this pack

The constant-folding non-reproduction above is reported honestly rather
than reshaped into a cleaner-looking story. It was investigated two ways
before being accepted as a genuine finding rather than a bug in the demo:
first with a short 300ms warmup/measurement run (already showed no gap),
then with a full 1-second, 5-iteration run to rule out insufficient
warmup as the explanation (same result, tighter error bars). The most
likely explanation is that HotSpot's `Math.log` intrinsic is invoked
directly per call rather than becoming a loop-invariant value the JIT can
prove is safe to hoist across JMH's generated measurement loop — but this
pack states that as a plausible interpretation, not a verified internal
JIT fact. The actionable, verified lesson is the one this chapter leads
with: JMH's own official samples still recommend `@State` fields as the
defensive default specifically *because* constant folding is a real,
documented risk for other call shapes and JVM versions — this pack's
result shows that risk did not manifest for this particular intrinsic on
this particular JDK build, which is itself the reason to verify pitfalls
empirically rather than trust unconditional folklore.
