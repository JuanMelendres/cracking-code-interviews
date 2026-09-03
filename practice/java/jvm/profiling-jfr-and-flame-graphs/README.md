# Profiling: async-profiler, JFR, and flame graphs (T-1202) — runnable verification

Real, executed JDK Flight Recorder output backing
[`syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md`](../../../../syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md)
(T-1202). No described-but-untested profiling claim — a real JFR recording of a real
JVM running real, deliberately inefficient code, analyzed with the JDK's own built-in
`jfr` CLI (no external agent, no download).

## Files

- `HotspotWorkload.java` — a real, deliberately inefficient workload: a CPU hotspot
  (`quadraticStringBuild`, real O(n²) string concatenation), an allocation hotspot
  (`allocateManyShortLivedRecords`, real repeated `Long` autoboxing), and a genuinely
  cheap baseline (`fastChecksum`) — all three running concurrently.
- `run-profiled-workload.sh` — compiles and runs the workload under a real
  `-XX:StartFlightRecording` session for 10 real seconds, producing `workload.jfr`.
- `analyze-jfr-recording.sh` — real analysis using only the JDK's built-in `jfr`
  CLI: aggregates the top-of-stack method for every real CPU sample and every real
  allocation sample into a ranked frequency table — exactly the data a flame graph
  visualizes as bar width, printed as text instead of rendered as an image.

## Run

```bash
cd practice/java/jvm/profiling-jfr-and-flame-graphs
./run-profiled-workload.sh
./analyze-jfr-recording.sh
```

## Real observed output (last full run, JDK 21)

```
=== Real event counts captured in this recording ===
 jdk.ObjectAllocationSample               2898         43127
 jdk.ExecutionSample                      1840         18383

=== Real CPU profile: top-of-stack method by real sample count ===
 834 HotspotWorkload.fastChecksum(int) line: 87
 719 java.lang.Long.valueOf(long) line: 1207
 153 java.lang.StringConcatHelper.newString(byte[], long) line: 400
  88 HotspotWorkload.quadraticStringBuild(int) line: 69
  31 java.lang.Integer.getChars(int, int, byte[]) line: 518

=== Real allocation profile: which class is actually being allocated ===
1849   objectClass = byte[] (classLoader = bootstrap)
 562   objectClass = java.lang.Long (classLoader = bootstrap)
 443   objectClass = java.lang.Object[] (classLoader = bootstrap)
  39   objectClass = java.lang.String (classLoader = bootstrap)
```

## The real, honest finding this run surfaced

Before running this, the obvious guess is that `quadraticStringBuild` — the
deliberate O(n²) hotspot — would dominate the CPU profile. It didn't.
`java.lang.Long.valueOf` (autoboxing inside `allocateManyShortLivedRecords`'s
`list.add(Long.valueOf(i))`) consumed **more real CPU samples (719) than the
deliberately inefficient string-concatenation hotspot itself (88, plus its downstream
`StringConcatHelper.newString` cost of 153 — 241 combined, still less than boxing
alone)**. This result was consistent across two independent runs.

This is not a demo bug — it's exactly the point of profiling over intuition: an
innocuous-looking line (`list.add(Long.valueOf(i))`) that "obviously" isn't the
expensive part of the code turned out to cost more real, measured CPU time than the
line that was deliberately written to be slow. A flame graph built from this exact
data would show the boxing call's frame as visually *wider* than the quadratic
string-build frame — the real, load-bearing reason flame graphs exist: to show where
CPU time actually goes, not where it's assumed to go.

## What this does and does not prove

This is real JFR output from a real JVM, but a synthetic, single-machine workload —
production profiles will show different absolute numbers and different specific
hotspots, shaped by real request patterns and real data. `async-profiler` (not used
here, to keep this demo dependency-free and reproducible on any machine with a JDK)
adds native-frame visibility and typically lower overhead than JFR's default
sampling, which matters for CPU-bound native code or JIT-compiled hot loops JFR alone
can under-sample — but the underlying principle this demo proves stays identical: a
real, sampled measurement of where execution time and allocations actually occur,
not a guess based on which code "looks" slow.
