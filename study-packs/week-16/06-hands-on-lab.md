---
title: "Hands-On Lab — Week 16 (JVM Internals Depth)"
week: 16
document_type: study-pack-lab
status: draft
last_reviewed: 2026-07-31
---

# Hands-On Lab — Week 16 (JVM Internals Depth)

This week's deliverable follows Week 15's pattern — the material is JVM-internals-shaped, not algorithm-shaped, so the hands-on work is reproducing real flag-driven demos and their measured output rather than solving LeetCode-style problems.

**Verification note:** all commands below are real and were executed on OpenJDK 21.0.12 (Docker 29.6.2 / `eclipse-temurin:21-jre` for the two container-based labs).

## Lab 1 — G1 remembered-set cost, low vs. high cross-region writes (T-304)

```bash
cd practice/java/week-16/g1-remembered-sets
javac -d out src/RememberedSetCostDemo.java
java -Xmx128m -Xlog:gc+phases=debug:file=out/low.log  -cp out RememberedSetCostDemo low
java -Xmx128m -Xlog:gc+phases=debug:file=out/high.log -cp out RememberedSetCostDemo high
grep "Dirty Cards:" out/low.log  | grep -oE "Sum: [0-9]+" | awk -F': ' '{s+=$2} END{print "low Dirty Cards Sum:", s}'
grep "Dirty Cards:" out/high.log | grep -oE "Sum: [0-9]+" | awk -F': ' '{s+=$2} END{print "high Dirty Cards Sum:", s}'
```

Expected: low run's Dirty Cards Sum in the low tens; high run's in the tens of thousands — a difference far larger than the difference in pause count between the two runs.

## Lab 2 — Memory-leak diagnosis via live-object histogram (T-307)

```bash
cd practice/java/week-16/memory-leak-diagnosis
javac -d out src/LeakyListenerDemo.java
java -cp out LeakyListenerDemo &
PID=$!
sleep 0.5;  jmap -histo:live $PID | grep Session
sleep 1.5;  jmap -histo:live $PID | grep Session
wait $PID
```

Expected: two samples showing live `Session` instance counts growing, roughly matching the app's own logged listener count. Re-run with `--fix` and confirm the histogram shows zero (or near-zero) `Session` instances at both sample points instead.

## Lab 3 — Metaspace exhaustion, independent of heap (T-301)

```bash
cd practice/java/week-16/memory-layout
javac -d out src/MetaspaceExhaustionDemo.java
java -Xmx512m -XX:MaxMetaspaceSize=32m -cp out MetaspaceExhaustionDemo
```

Expected: a real `OutOfMemoryError: Metaspace` after several thousand dynamically-generated classes, with heap usage reported at only a small fraction of the 512MB max — proof metaspace and heap are exhausted independently.

## Lab 4 — Thread-stack depth vs. `-Xss`, heap held constant (T-301)

```bash
cd practice/java/week-16/memory-layout
javac -d out src/StackDepthDemo.java
java -Xmx512m -Xss256k -cp out StackDepthDemo
java -Xmx512m -Xss1m   -cp out StackDepthDemo
java -Xmx512m -Xss8m   -cp out StackDepthDemo
```

Expected: recursion depth at `StackOverflowError` scaling by roughly two orders of magnitude across the three `-Xss` values, with `-Xmx` unchanged in every run.

## Lab 5 — Container CPU/memory ergonomics (T-312)

```bash
cd practice/java/week-16/container-ergonomics
javac -d out src/ContainerErgonomicsDemo.java
docker run --rm --cpus=2 --memory=1g -v "$(pwd)/out:/app" eclipse-temurin:21-jre \
  java -Xlog:gc+init -cp /app ContainerErgonomicsDemo
docker run --rm --cpus=6 --memory=1g -v "$(pwd)/out:/app" eclipse-temurin:21-jre \
  java -Xlog:gc+init -cp /app ContainerErgonomicsDemo
docker run --rm --cpus=2 --memory=1g -v "$(pwd)/out:/app" eclipse-temurin:21-jre \
  java -XX:MaxRAMPercentage=75.0 -cp /app ContainerErgonomicsDemo
```

Expected: the `CPUs: {host total} total, {--cpus value} available` line changing to match your `--cpus` value each time; the heap cap (`maxMemory`) scaling up when `MaxRAMPercentage` is raised, with the container's own `--memory` limit unchanged.

## Lab 6 — JIT warmup and a real deoptimization (T-308)

```bash
cd practice/java/week-16/jit-compilation
javac -d out src/WarmupSpeedupDemo.java src/DeoptDemo.java
java -Xint -cp out WarmupSpeedupDemo | tail -3
java -cp out WarmupSpeedupDemo | tail -3
java -XX:+PrintCompilation -cp out DeoptDemo | grep -iE "sumAreas|made not entrant"
```

Expected: a large ns/op gap between `-Xint` and default (tiered) runs; a `DeoptDemo::sumAreas ... made not entrant` line appearing right around when the mixed-type workload begins, following an earlier C2 (level 4) compilation reached during the monomorphic-only phase.

## Self-Check

- [ ] All six labs reproduced with your own matching (not necessarily identical) real output
- [ ] Can explain, for Lab 1, why the dirty-card ratio (not the pause-count ratio) is the real evidence
- [ ] Can explain, for Lab 2, why `:live` specifically matters for trusting the histogram
- [ ] Can explain, for Lab 3, why the OOM says "Metaspace" and not "Java heap space"
- [ ] Can explain, for Lab 5, why `availableProcessors()` reflects `--cpus`, not the host's real core count
- [ ] Can explain, for Lab 6, the difference between "made not entrant" as routine housekeeping versus this lab's genuine deoptimization
