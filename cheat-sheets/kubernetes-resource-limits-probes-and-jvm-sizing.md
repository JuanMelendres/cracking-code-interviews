---
title: "Cheat Sheet: Kubernetes Resource Limits, Probes, and JVM Sizing"
slug: kubernetes-resource-limits-probes-and-jvm-sizing
document_type: cheat-sheet
domain: cloud
topic_id: T-1003
canonical: ../handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md
last_updated: 2026-08-04
---

# Kubernetes Resource Limits, Probes, and JVM Sizing

**Canonical chapter:** [`syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md`](../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md)

## Core Mental Model

A container's memory limit and the JVM's heap size are two independent numbers that must be kept in agreement — and when they silently disagree, the failure mode depends entirely on which one is wrong. If heap can grow past what the container allows, the *operating system* kills the process with no Java-level warning. If the heap itself is set too small relative to genuine memory needs (with container room to spare), the *JVM* throws `OutOfMemoryError` — catchable and loggable. Same underlying problem (not enough memory), two completely different failure signatures, depending on which side of the JVM/container boundary the constraint actually binds.

## Essential Definitions

- **Container-aware JVM** — since JDK 10 (backported to JDK 8u191+), the JVM reads the container's cgroup memory limit rather than host total memory for ergonomic defaults, including default heap size (`-XX:MaxRAMPercentage`, default 25% of the detected limit, with a `-XX:MinRAMPercentage` floor of 50% at small container sizes).
- **Kubernetes probes** — let the platform ask a running container "are you healthy?" A failing **readiness** probe removes a pod from a Service's load-balancing pool without restarting it; a failing **liveness** probe restarts the container; a **startup** probe suppresses liveness checks until a potentially-slow initialization finishes.
- **OOMKill** — an operating-system-level `SIGKILL` with no Java involvement: if resident memory (heap + metaspace + thread stacks + JIT code cache + native/off-heap) exceeds the cgroup limit, the Linux kernel's OOM killer sends `SIGKILL` directly to the process — exit code 137 (128 + signal 9).
- **`java.lang.OutOfMemoryError`** — a normal, catchable JVM-level exception thrown from the allocation site when the JVM's own heap fills; the process can catch, log, clean up, or exit deliberately.

## Decision Table

| Choice | Benefit | Cost |
|---|---|---|
| Rely on container-aware default heap sizing (no explicit `-Xmx`) | Heap automatically tracks the limit, safer default | Doesn't account for non-heap memory automatically |
| Explicit `-Xmx` smaller than container-aware default | More predictable ceiling, more headroom | Requires manual sync as either the limit or the flag changes |
| Setting requests == limits for memory | Predictable scheduling, no throttling/eviction risk | Less cluster-wide flexibility (no bursting) |
| Generous memory limit "just in case" | Fewer OOMKills short term | Wastes capacity; masks accounting problems |

| Symptom | Likely cause |
|---|---|
| `java.lang.OutOfMemoryError` in logs, exit code 1 | Heap itself exhausted — JVM-level, catchable |
| Pod restarts, no application logs, exit code 137 | Container OOMKilled — check `OOMKilled=true` |
| Pod killed repeatedly during startup, never becomes ready | Missing/misconfigured `startupProbe` |
| Pod stays in load-balancing pool while genuinely overloaded | Missing/misconfigured `readinessProbe` |

## Key Numbers (real, executed — Docker 29.6.2, `eclipse-temurin:21-jre`)

```
--memory=256m: maxMemory() = 121 MiB   (~47% -- MinRAMPercentage floor applies)
--memory=512m: maxMemory() = 123 MiB   (~24%)
--memory=1g:   maxMemory() = 247 MiB   (~24%)

Scenario A (OutOfMemoryError): --memory=512m, -Xmx64m
  -> maxMemory()=61 MiB, retains 20MB then 40MB, then:
     java.lang.OutOfMemoryError: Java heap space   EXIT CODE 1

Scenario B (OOMKilled): --memory=100m, -Xmx256m
  -> maxMemory()=247 MiB, retains up to 220MB, then EXIT CODE 137
     docker inspect: OOMKilled=true exitcode=137
```

## Common Pitfalls

- Assuming JVM container-awareness alone is sufficient, without accounting for non-heap memory when sizing the container's memory limit
- Treating an unexplained restart loop as an application bug before checking the container-level termination reason
- Conflating readiness (should this pod receive traffic right now) with liveness (should this pod be restarted) — using one probe type for both purposes

## Interview Answer Skeleton

**30-sec:** The JVM has been container-aware since JDK 10, deriving heap from the cgroup limit not host memory — measured directly at three container sizes. `OutOfMemoryError` is a catchable JVM exception when heap fills; OOMKill (exit 137, `OOMKilled=true`) is the kernel killing the process with zero Java-level warning.

**2-min:** Add why it exists (MaxRAMPercentage 25% default, MinRAMPercentage 50% floor at small sizes) + the non-heap-memory blind spot trade-off + the measured OOME-exit-1 vs. OOMKill-exit-137 contrast.

**Whiteboard:** Draw the decision flowchart branching on "within heap ergonomics?" then "RSS exceeds cgroup limit?" Annotate the OOMKill branch: "this is invisible from inside the JVM — nothing in application code or logs will ever show this."

**Staff-level framing:** a failure's visibility depends entirely on which layer of the stack actually detects it, and a layer that doesn't detect a failure can't report on it — the JVM cannot log a failure that happens to it from outside its own process boundary. The most dangerous failures are the ones invisible to the layer everyone instinctively checks first (application logs).

## Production Warning Signs

- **Real incident pattern:** pods restart every few minutes under moderate load after a dependency upgrade, with no exceptions, stack traces, or shutdown-hook output — the last log line is always mid-request. `kubectl describe pod` shows `Last State: Terminated, Reason: OOMKilled, Exit Code: 137`. Root cause: the dependency upgrade increased native/off-heap buffer pool footprint, pushing total RSS past the cgroup limit with no `-Xmx` or limit change at all.
- Fix: explicitly account for the dependency's off-heap usage against `-Xmx`; add a standing alert on the `OOMKilled` termination reason, not just on application-level exceptions.

## Related

- [GC Fundamentals and Log Analysis](gc-fundamentals-and-log-analysis.md)
- `syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md`
