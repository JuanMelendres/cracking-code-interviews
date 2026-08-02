---
title: "Hands-On Lab — Week 19 (JVM Domain Full Closure)"
week: 19
document_type: study-pack-lab
status: draft
last_reviewed: 2026-08-02
---

# Hands-On Lab — Week 19 (JVM Domain Full Closure)

Six labs, one per topic. Lab 2 needs Docker; the rest are pure Java.

**Verification note:** all commands below are real and were executed on OpenJDK 21.0.12 (this JDK's Homebrew build includes ZGC and Shenandoah).

## Lab 1 — Reference-strength hierarchy (T-303)

```bash
cd practice/java/week-19/gc-roots-reachability/src
javac -d ../out ReferenceStrengthDemo.java
java -cp ../out ReferenceStrengthDemo
```

Expected: strong reference survives `System.gc()`; weak reference cleared immediately once unreferenced; soft reference survives the identical operation under no memory pressure; phantom reference's `get()` always `null`, enqueued only after collection.

## Lab 2 — G1 vs. ZGC pause times and allocation stalls (T-305)

```bash
cd practice/java/week-19/zgc-vs-g1/src
javac -d ../out AllocationChurnDemo.java
java -Xmx256m -XX:+UseG1GC -Xlog:gc:file=../out/g1.log:time,tags -cp ../out AllocationChurnDemo
java -Xmx256m -XX:+UseZGC -Xlog:safepoint:file=../out/zgc-safepoint.log:time -cp ../out AllocationChurnDemo
grep -oE "[0-9]+\.[0-9]+ms" ../out/g1.log | sort -n | tail -3
grep -oE "At safepoint: [0-9]+ ns" ../out/zgc-safepoint.log | sort -t: -k2 -n | tail -3
```

Expected: G1's max pause in the sub-millisecond range but measurably longer than ZGC's individual "At safepoint" durations, which land in the microsecond range.

## Lab 3 — Safepoint operation types and costs (T-310)

```bash
cd practice/java/week-19/safepoints/src
javac -d ../out SafepointDemo.java
java -Xlog:safepoint:file=../out/safepoint.log:time -cp ../out SafepointDemo &
PID=$!
sleep 2
jcmd $PID Thread.print > /dev/null
sleep 1
jcmd $PID GC.run > /dev/null
wait $PID
grep -E "PrintThreads|FindDeadlocks|G1CollectFull" ../out/safepoint.log
```

Expected: three distinctly-named safepoint operations, each with a genuinely different "At safepoint" duration — cheapest (`FindDeadlocks`) to most expensive (`G1CollectFull`) spanning roughly three orders of magnitude.

## Lab 4 — Compressed oops memory footprint (T-302)

```bash
cd practice/java/week-19/object-layout/src
javac -d ../out CompressedOopsFootprintDemo.java
java -Xmx1g -XX:+UseCompressedOops -cp ../out CompressedOopsFootprintDemo
java -Xmx1g -XX:-UseCompressedOops -cp ../out CompressedOopsFootprintDemo
```

Expected: a real, measurable memory-footprint difference (roughly 30-40%) between the two runs for the identical 5-million-node object graph.

## Lab 5 — Direct buffers vs. heap, and NMT evidence (T-311)

```bash
cd practice/java/week-19/native-memory/src
javac -d ../out DirectBufferDemo.java NmtDirectBufferDemo.java
java -Xmx32m -XX:MaxDirectMemorySize=256m -cp ../out DirectBufferDemo
java -Xmx64m -XX:MaxDirectMemorySize=256m -XX:NativeMemoryTracking=summary -cp ../out NmtDirectBufferDemo &
PID=$!
sleep 2
jcmd $PID VM.native_memory summary | grep -A2 "Java Heap\|Other"
wait $PID
```

Expected: `DirectBufferDemo` allocates far more than `-Xmx` allows before hitting a distinct `OutOfMemoryError: Direct buffer memory` at exactly the `MaxDirectMemorySize` limit. NMT shows `Java Heap` matching `-Xmx` exactly and `Other` matching the direct-buffer allocation exactly.

## Lab 6 — Escape analysis on vs. off (T-309)

```bash
cd practice/java/week-19/escape-analysis/src
javac -d ../out EscapeAnalysisDemo.java
java -Xmx64m -Xlog:gc:file=../out/ea-on.log -cp ../out EscapeAnalysisDemo
java -Xmx64m -XX:-DoEscapeAnalysis -Xlog:gc:file=../out/ea-off.log -cp ../out EscapeAnalysisDemo
grep -c "Pause" ../out/ea-on.log
grep -c "Pause" ../out/ea-off.log
```

Expected: zero (or near-zero) GC pauses with escape analysis enabled; a real, substantial number of GC pauses with it disabled — the identical source code and iteration count in both runs.

## Self-Check

- [ ] All six labs reproduced with your own matching (not necessarily identical) real output
- [ ] Can explain, for Lab 1, why the phantom reference's `get()` never returns the object even before collection
- [ ] Can explain, for Lab 2, why ZGC's individual pauses are shorter but its overall run completed less total work
- [ ] Can explain, for Lab 3, why "reaching safepoint" and "at safepoint" are separate, differently-caused costs
- [ ] Can explain, for Lab 4, which specific field (header vs. reference) accounts for the measured footprint difference
- [ ] Can explain, for Lab 5, why direct-buffer memory is invisible to a heap dump but visible in NMT's `Other` category
- [ ] Can explain, for Lab 6, why this optimization only applies to JIT-compiled code, not the interpreter
