---
title: "Cheat Sheet: JVM Startup Performance — CDS, AppCDS, and GraalVM Native Image"
slug: jvm-startup-performance-cds-and-native-image
document_type: cheat-sheet
domain: 16-performance-jvm
topic_id: T-2416
canonical: ../syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md
last_updated: 2026-09-18
---

# JVM Startup Performance: CDS, AppCDS, and GraalVM Native Image

**Canonical chapter:** [`syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md`](../syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md)

## Core Mental Model

Every technique here trades a real cost paid once, at build time, for a real cost avoided every single startup. CDS/AppCDS trades archive-generation time for less class-parsing work later. Native-image trades a longer, more constrained build (the closed-world assumption) for a runtime with categorically less to do at startup — it isn't "a faster JVM," it's a different kind of program.

## Essential Definitions

- **CDS (Class Data Sharing)** — pre-parses/pre-verifies classes into a shared, memory-mappable archive, skipping that work on future startups.
- **AppCDS** — extends CDS to an application's own classes; dynamic archives (`-XX:ArchiveClassesAtExit`) since JDK 19 need no manual class listing.
- **GraalVM Native Image** — ahead-of-time compiles an entire application's reachable code into a self-contained native executable; no JVM, interpreter, or JIT exists at runtime.
- **Closed-world assumption** — native-image must know every reachable class/method/reflective-access at build time; breaks naive reflection without explicit configuration.

## Decision Table

| Situation | What to reach for |
|---|---|
| Long-running server process | Don't optimize startup — it's a one-time, largely irrelevant cost |
| Framework-heavy app restarted with any real frequency | AppCDS — low effort, real (if variable) benefit |
| Serverless function / fast-autoscaling container / CLI tool | Evaluate GraalVM native-image seriously |
| Native-image build fails on a reflective call | Add reflection configuration, or use the framework's own AOT support |

## Key Numbers (real, executed — OpenJDK 21.0.12, Oracle GraalVM 21.0.12, Apple M4)

```
Startup-to-first-response, 8 runs each:
  Plain JVM, java -jar:            avg 136ms
  JVM + AppCDS:                    avg 141ms   -- no measurable win, tiny app
  GraalVM native-image:            avg 9ms     -- ~15x faster

Peak RSS:
  Plain JVM:            ~50.1MB
  GraalVM native-image: ~15.7MB   -- ~3.2x smaller
```

## Common Pitfalls

- Assuming AppCDS delivers a fixed percentage improvement regardless of app size — measured directly as zero for a tiny app; benefit is proportional to class-loading volume.
- Treating native-image as "just a faster JVM" instead of a categorically different runtime with a real closed-world constraint.
- Optimizing startup for a long-running server where it isn't the real bottleneck.
- Native-image-compiling a framework app with zero AOT support and expecting reflection to just work.

## Interview Answer Skeleton

**30-sec:** CDS/AppCDS skips re-parsing/re-verifying classes on JVM startup — benefit proportional to class-loading volume. Native-image AOT-compiles into a self-contained binary with no JVM at runtime — measured at ~15x faster startup, ~3.2x smaller memory, at the cost of a real closed-world reflection constraint.

**2-min:** Add the honest AppCDS result (zero measurable improvement for a tiny app) + why (nothing to save with few classes) + native-image's real numbers + the closed-world trade-off.

**Staff-level framing:** whether to standardize a native-image build path for serverless/CLI workloads platform-wide (real, ongoing reflection-config maintenance cost) vs. accepting JVM cold-start as a known, priced trade-off — weighed against actual invocation-volume profile.

## Production Warning Signs

- A serverless function shows bimodal latency (fast mostly, 800ms-1.5s on cold starts) correlated with low-traffic periods — real JVM startup cost, not a downstream dependency issue.
- **Fix:** migrate to GraalVM native-image (per this chapter's measured ~15x improvement), or use provisioned concurrency as a stopgap.

## Related

- `syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md`
- `syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md`
- `syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md`
