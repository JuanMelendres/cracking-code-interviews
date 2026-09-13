---
title: "Cheat Sheet: JVM Flags and Container Ergonomics"
slug: jvm-flags-and-container-ergonomics
document_type: cheat-sheet
domain: jvm
topic_id: T-312
canonical: ../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md
last_updated: 2026-09-12
---

# JVM Flags and Container Ergonomics

**Canonical chapter:** [`syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md`](../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md)

## Mental Model

The JVM's container-awareness answers two separate census questions on startup: how much RAM does it actually have access to, and how many CPUs — both asked against the container's cgroup limits, not the host's real hardware. The RAM answer feeds a percentage rule (by default, up to 25% of that RAM for heap), leaving headroom for metaspace, thread stacks, and JIT code cache, all of which live outside the heap. The CPU answer feeds ergonomic defaults like GC thread counts and the common `ForkJoinPool` size — get the CPU count wrong (or let it default to the host's real core count on a heavily CPU-limited container) and those defaults become wrong too.

## Decision Table

| Flag | Controls | Default |
|---|---|---|
| `-XX:+UseContainerSupport` | Whether cgroup limits are read at all | On, since JDK 10 |
| `-XX:MaxRAMPercentage` | Heap cap as % of detected container memory | 25.0 |
| `-XX:InitialRAMPercentage` | Initial heap size as % of detected container memory | ~1.5625 |
| `-XX:ActiveProcessorCount` | Overrides detected CPU count directly | Auto-detected from cgroup quota |
| `-Xlog:gc+init` | Logs the actual detected CPUs/memory at startup | — |

## Decision Framework

Leave `MaxRAMPercentage` at its default unless there's a specific, measured reason to change it — the default's conservatism exists because metaspace/stack/off-heap needs are real and easy to under-provision for by accident. Set `-XX:ActiveProcessorCount` explicitly only when automatic detection doesn't match the deployment's intended concurrency level, not as a default habit.

## Common Pitfalls

- Assuming `Runtime.availableProcessors()` always reflects the host's physical core count — it reflects the container's cgroup CPU quota on any container-aware JVM.
- Raising a container's memory limit and expecting the heap cap to grow by the same absolute amount, without accounting for the percentage-based default.
- Forgetting GC thread counts and other CPU-scaled ergonomic defaults are affected by container CPU limits too, not just memory-sizing.
- Setting `-Xmx` to a fixed absolute value in a container context, which stops it from responding to memory-limit changes entirely.

## Interview Answer Skeleton

30 seconds: modern JVMs read cgroup memory/CPU limits, not host hardware, and default to 25% of detected memory for heap. 2 minutes: add the flag table and the CPU-detection story (GC threads, `ForkJoinPool` sizing). Deep dive: walk a case where `Runtime.availableProcessors()` under-reported CPUs due to a `--cpus` limit, sizing a thread pool wrong until `-XX:ActiveProcessorCount` was set explicitly.

## Production Warning Signs

- Thread pool or `ForkJoinPool` sized from `Runtime.availableProcessors()` in a container with a tight CPU quota — likely undersized without checking the actual detected count.
- Container memory limit raised without checking whether the heap cap moved proportionally (percentage-based default).
- `-Xmx` hardcoded in a container image, ignoring memory-limit changes across environments.

## Related

- [JVM Memory Layout and Runtime Regions](../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md)
- [Native Memory, Direct Buffers, and Off-Heap](../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md)
