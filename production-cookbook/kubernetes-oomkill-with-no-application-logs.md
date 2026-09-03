---
title: "Kubernetes OOMKill With No Application Logs"
document_type: production-cookbook-entry
domain: cloud
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md
source: handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md#production-scenarios
---

# Kubernetes OOMKill With No Application Logs

## Context

A service runs as a JVM process inside a container with a fixed memory limit and an `-Xmx` set below that limit, on the assumption that heap is the only significant memory consumer. A routine dependency upgrade changes that assumption without anyone revisiting the sizing.

## Symptoms

After a routine dependency upgrade, a service's pods begin restarting every few minutes under moderate load. Application logs show no exceptions, no stack traces, and no shutdown-hook output at all — the last log line before each restart is always mid-request, with no indication anything was wrong.

## Impact

The on-call team spends significant time investigating the application code for a crash — checking for uncaught exceptions and deadlocks — before realizing the process isn't failing at the application level at all.

## Initial Hypotheses

- A regression in the dependency upgrade causing an uncaught exception — checked and ruled out; no exception ever appears in logs, and the upgrade's changelog shows no relevant behavior change.
- A liveness probe misconfiguration causing premature restarts — checked and ruled out; probe configuration and timing are unchanged from before the incident.
- The container is being OOMKilled, not application-crashing — correct.

## Evidence

`kubectl describe pod` shows `Last State: Terminated, Reason: OOMKilled, Exit Code: 137` for every restart — information that was available the entire time but wasn't the team's first place to look, since the absence of application logs led them toward suspecting an application-level crash rather than an infrastructure-level kill.

## Investigation Timeline

1. **Restart loop observed**, with application logs showing no error signal of any kind before each restart.
2. **Application-level hypotheses ruled out**: no exception in logs, no relevant change in the dependency's changelog, no probe misconfiguration.
3. **Infrastructure-level check performed**: `kubectl describe pod` surfaces the actual termination reason directly.
4. **Mechanism traced to the dependency upgrade**: the new version increased the library's native/off-heap memory footprint without any corresponding change to the container's memory limit or `-Xmx`.

## Root Cause

The dependency upgrade increased the library's native/off-heap memory footprint — a larger native buffer pool used by the new version — without changing the container's memory limit or the JVM's `-Xmx`. Total resident memory (heap plus the now-larger off-heap footprint) exceeded the container's cgroup limit under load, triggering an OOMKill with no opportunity for the JVM to log anything, since the kill happens at the kernel/cgroup level rather than inside the process.

## Immediate Mitigation

Increase the container's memory limit temporarily to restore headroom while the root cause is addressed.

## Permanent Fix

Explicitly account for the dependency's documented off-heap memory usage when sizing the container's memory limit relative to `-Xmx`, and add a standing monitoring alert directly on the `OOMKilled` container-termination reason — not just on application-level exceptions — so this failure mode is visible immediately rather than requiring `kubectl describe` to be checked manually.

## Alternatives Considered

Reducing `-Xmx` to leave more headroom for the same container limit. A reasonable complementary step, but insufficient alone since it doesn't address that the memory budget between heap and native/off-heap usage was never explicitly reasoned about in the first place.

## Trade-offs

A larger container memory limit costs more cluster capacity per pod. Accepted, since the alternative is a repeat of the exact restart loop this incident describes.

## Prevention

Any container running a JVM should have its memory limit sized with an explicit accounting for both `-Xmx` (or the container-aware ergonomic default) and expected non-heap usage — metaspace, thread stacks, JIT code cache, direct buffers, any native library memory — never sized as if heap were the only memory consumer.

## Monitoring and Alerts

- A direct alert on container-termination reason `OOMKilled`, independent of and prior to any application-level exception alert — this is the single fastest fix available, since the information was present the entire time and simply wasn't the team's first place to look.
- Container memory utilization tracked as resident-set size against the limit, not heap usage against `-Xmx` alone, since the gap between the two is exactly what caused this incident.
- A dependency-upgrade checklist item to review the new version's documented resource footprint before rollout, catching this class of regression before it reaches production.

## Interview Story

This maps to a "pods keep restarting with no application-level error, walk me through diagnosing it" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** pods restarting every few minutes after a routine dependency upgrade, with zero application-level error signal.
- **Task:** find the actual cause after the obvious application-level leads (exceptions, probes) come up empty.
- **Action:** rule out an application-level crash and probe misconfiguration using existing logs and config history; check the container-level termination reason directly with `kubectl describe pod` instead of continuing to search application logs; trace the increased memory footprint to the specific dependency upgrade.
- **Result:** resized the container's memory budget to explicitly account for off-heap usage, and added a direct alert on `OOMKilled` so this failure mode is visible immediately in any future incident.

## Staff-Level Discussion

The costliest part of this incident wasn't the memory miscalculation — it was the investigation time spent looking in the wrong layer, because the team's mental model treated "no application logs" as "nothing to check" rather than as its own diagnostic signal (a kernel-level kill, by definition, gives the application no chance to log anything). The organizational fix is cheap and high-leverage: alert directly on `OOMKilled` as a first-class signal, so no future on-call engineer has to rediscover that `kubectl describe pod` should be an early step, not a late one, for an unexplained restart loop. More broadly, this is a recurring cost of treating dependency upgrades as low-risk by default — a resource-footprint review as part of the upgrade checklist would have caught this before it ever reached production, at a fraction of the cost of the incident.

## Related Handbook Chapters

- [Kubernetes Resource Limits, Probes, and JVM Sizing](../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md) — canonical cgroup/OOMKill mechanics and JVM container-sizing methodology used here.
- [JVM Flags and Container Ergonomics](../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md) — the `-Xmx`/container-aware ergonomic defaults this sizing decision is stated in terms of.
