---
title: "Cheat Sheet: Profiling with JFR and Flame Graphs"
slug: profiling-jfr-and-flame-graphs
document_type: cheat-sheet
domain: performance
topic_id: T-1202
canonical: ../handbook/performance/profiling-jfr-and-flame-graphs.md
last_updated: 2026-09-02
---

# Profiling: async-profiler, JFR, and Flame Graphs

**Canonical chapter:** [`syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md`](../syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md)

## Core Mental Model

A profiler answers one question — "where is the JVM actually spending time (or allocating memory) right now?" — by sampling, not by instrumenting every line. A CPU/wall-clock profile samples the call stack of running threads at a fixed interval; a method's sample count is proportional to how much real time it was observed executing. A flame graph is simply a visual encoding of these sample counts: each frame's width is proportional to how often it appeared in a sampled stack, stacked vertically by call depth — the widest frames, wherever they sit in the stack, are the real hotspots, regardless of how "obviously slow" the corresponding code looks in a code review.

## Essential Definitions

- **JDK Flight Recorder (JFR)** — a low-overhead, production-safe profiling and event-recording facility built into the JVM since JDK 11 (backported to 8u); a recording starts with a single JVM flag, no external agent.
- **async-profiler** — a separately-distributed sampling profiler attaching as a native agent, offering lower overhead and native-frame visibility (JIT threads, GC threads, native library calls) that JFR's default configuration doesn't always capture as precisely.
- **Flame graph** — renders sampled stack-trace frequency data as a graph where frame width encodes sample count; height only encodes call depth.
- **Sampling, not instrumentation** — both tools periodically capture stack traces rather than measuring every method call, which is what makes them low-overhead enough for production use.
- **CPU vs. allocation samples are different views** — a method can be a CPU hotspot without being an allocation hotspot, and vice versa; check both separately.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| One-off, ad hoc investigation on a JDK-only environment | JFR — zero install, immediately available |
| Hotspot suspected in native code, JIT compilation, or GC internals | async-profiler — better native-frame visibility |
| Continuous, low-overhead profiling wanted in production or load tests | JFR — designed for this use case |
| Symptom is CPU-bound (high CPU utilization, slow request processing) | CPU/execution-sample profiling |
| Symptom is memory-related (high GC frequency, memory growth) | Allocation-sample profiling |

**Tool comparison:**

| Tool | Install | Overhead | Native-frame visibility |
|---|---|---|---|
| JFR | Built into JDK 11+ | Low, production-safe | Good, not always complete |
| async-profiler | Separate native agent | Typically lower | Excellent |
| Flame graph | Visualization built from either tool's data | N/A | N/A |

## Key Numbers (real, executed JDK Flight Recorder output, real JVM, deliberately inefficient code)

- Real CPU profile top-of-stack sample counts: `HotspotWorkload.fastChecksum` = 834; `java.lang.Long.valueOf` (autoboxing inside the allocation-hotspot method) = 719; `java.lang.StringConcatHelper.newString` = 153; `HotspotWorkload.quadraticStringBuild` (the deliberately-written O(n²) hotspot) = 88; `java.lang.Integer.getChars` = 31.
- `Long.valueOf` alone (719) consumed more real CPU samples than the deliberate O(n²) hotspot plus its own downstream string-allocation cost combined (88 + 153 = 241). Consistent across independent runs.

## Common Pitfalls

- Optimizing the code that "looks" slowest without a real profile.
- Misreading flame graph height as importance rather than width.
- Profiling under unrepresentative (idle or synthetic) load and drawing production conclusions from it.
- Treating JFR and async-profiler as interchangeable in every situation, without considering native-frame visibility needs.
- Profiling in a debug/non-JIT-warmed-up state and drawing conclusions about steady-state production performance from cold-start samples.

## Interview Answer Skeleton

**30-sec:** A profiler samples running stacks periodically to find where CPU time or allocations actually go, rather than guessing from code review. JFR is built into every JDK with low overhead suitable for production; async-profiler is a separate agent with better native-frame visibility. A flame graph visualizes sample frequency as frame width — wide frames are the real hotspots, regardless of how the corresponding code looks.

**2-min:** Add the real measured result: an innocuous autoboxing call (`Long.valueOf`, 719 samples) consumed more real CPU than a method deliberately written to be an O(n²) hotspot plus its downstream cost (241 samples combined) — exactly the kind of result code-review intuition would never predict.

**Whiteboard:** Draw a flame graph shape: a wide base frame ("main"), narrowing upward through call-depth layers, with one frame partway up drawn unusually wide relative to its siblings. Point at the wide frame, not the tallest stack: "width is the signal; this frame, regardless of how deep it sits, is where the time actually went."

**Staff-level framing:** Discuss continuous production profiling as a standing practice rather than a reactive tool, connecting it to load-testing discipline. Reason about the organizational cost of skipping profiling in favor of intuition-driven optimization. Discuss the overhead/completeness trade-off between JFR and async-profiler as a deliberate tooling choice, not a default.

## Production Warning Signs

- Checkout p99 latency exceeds SLO, and the team's initial code review focuses on a "computationally heavy-looking" nested loop — a real JFR profile instead shows that loop consuming an unremarkable fraction of CPU, with the dominant hotspot being a debug-log string concatenation built on every request regardless of log level, because the concatenation happened before the level check.
- Fix: wrap the debug log call in an explicit `isDebugEnabled()` check; add a lint rule flagging unguarded string-concatenation-based log calls; add periodic JFR profiling of hot paths to the standard load-test process.
- An optimization based on "this loop looks slow" doesn't measurably improve the metric it was meant to fix — trust the profile, not the intuition.
- A profile captured at idle or under synthetic load that doesn't match production traffic patterns can miss the real hotspot entirely, since sampling only captures what's actually executing during the recording window.

## Related

- `syllabus/13-observability/performance-methodology-and-slo-error-budgets.md`
- `syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md`
- `syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md`
- `syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md`
