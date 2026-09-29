---
title: "Cheat Sheet: Production Troubleshooting Methodology"
slug: production-troubleshooting-methodology
document_type: cheat-sheet
domain: 13-observability
topic_id: T-2442
canonical: ../syllabus/13-observability/production-troubleshooting-methodology.md
last_updated: 2026-09-29
---

# Production Troubleshooting Methodology

**Canonical chapter:** [`syllabus/13-observability/production-troubleshooting-methodology.md`](../syllabus/13-observability/production-troubleshooting-methodology.md)

## Core Mental Model

A doctor, not a mechanic. You cannot open the patient to browse. Work from symptom → differential → the test that best **discriminates** between the top candidates. A test that comes back normal is not wasted if it eliminated three conditions.

## The Three Triage Questions (ask before any tool)

1. **Everything, or one endpoint?** Shared cause (JVM, host, pool, shared dependency) vs. scoped cause (that path's query, downstream call, data).
2. **Step change, or gradual slope?** Discrete event (deploy, config, flag, migration, dependency incident) vs. something growing (table, cache, heap, queue).
3. **What changed around then?** Usually answered by the deploy pipeline, not by observability tooling.

Each roughly halves the candidate list. All three are free.

## Two Latency Checks Worth More Than Any Profiler

**Is throughput flat under rising load?** Flat = a bounded resource is the ceiling. Optimising code will not help; neither will more instances behind that *same* pool.

**Percentiles, never averages.** Measured: p50 held at **1 ms** at concurrency 200 while p99 hit **152 ms**. A mean-latency dashboard calls that system healthy.

## Why Dev Cannot Catch It (measured)

| rows | naive (N+1) | batched | ratio |
|---|---|---|---|
| 10 | 2,809 us | 510 us | 6x |
| 5,000 | **1,283,121 us** | 507 us | **2531x** |

Different **curves**, not different constants. The dev dataset sits at the one point where they look alike, and a faster machine moves both curves down without closing the gap. The two structural prod differences are **data volume** and **concurrency** — never assume "prod hardware is slower."

## "Nothing in the Logs" — Three Causes

| Cause | Evidence recoverable? |
|---|---|
| Swallowed in a bare `catch` | **No.** Never written. Needs a code change. |
| Logged at `DEBUG`, prod runs `INFO` | **Yes, cheaply.** Config only, often no deploy. |
| Catch-all `@ExceptionHandler` that doesn't log | **Yes**, but needs a change to the advice. |

**Discriminator:** raise the app package to `DEBUG`. Verified — it recovered the wrong-level case and **only** that one. Output → wrong level. Silence → the exception is being *destroyed*, not merely unprinted.

**Free first check:** the error body's *shape*. Container default (`timestamp`/`status`/`error`/`path`) = failure in a filter, outside the `DispatcherServlet`, where `@ControllerAdvice` never runs.

## Tool Costs — Cheap Narrows *Where*, Expensive Identifies *What*

| Tool | Cost | Answers |
|---|---|---|
| Access log / metrics | Free | Is it happening, where, how often |
| GC logs | Very low | Is the JVM pausing |
| `jcmd Thread.print` | Brief safepoint | What threads do *now* — hangs, deadlocks, pool starvation |
| `jcmd GC.class_histogram` | Low | Which types dominate |
| `jcmd GC.heap_dump` | Pauses app; heap-sized file | What **retains** them |
| `jcmd VM.native_memory summary` | Low | Off-heap growth heap metrics cannot see |
| JFR / profiler | Low at defaults | Where time goes |
| Tracing | Sampling overhead | Which hop |

```bash
kubectl logs <pod> --previous   # the CRASHED container, not the new one
kubectl describe pod <pod>      # Events + Last State -- usually the real answer
```

## Decision Table

| Signal | Conclusion |
|---|---|
| All endpoints degraded | JVM / host / shared pool / shared dependency |
| One endpoint degraded | That path: its query, its downstream, its data volume |
| Throughput flat, load rising | Bounded resource — not the code |
| p50 flat, p99 exploding | Queueing. Alert on p99 and pool-wait time |
| Gradual slope over days | Something growing: table, cache, heap, queue |
| Step change, no deploy | Traffic, a flag, a data threshold, a dependency's incident |

## Common Pitfalls

- Opening with a tool instead of a hypothesis.
- Checking the average latency.
- Blaming the log shipper before checking the access log.
- Acting (restart, resize, add an index) before any check has discriminated.
- Restart-first debugging — clears the symptom **and** destroys the evidence.
- Concluding "prod hardware is slower."
- Reproducing in dev, failing, and concluding the report is wrong.
- Changing more than one thing at a time.

## Interview Answer Skeleton

1. Three triage questions, each justified by **what it eliminates**.
2. For latency: flat throughput? percentiles not averages?
3. Name the structural dev-vs-prod difference (volume, concurrency) — not hardware.
4. Only then a tool, chosen to separate your top two hypotheses.
5. If the evidence doesn't exist, shipping a log line or metric **is** the next step.

## Production Warning Signs

- Pool-wait time climbing while user latency still looks fine (the early signal).
- An access log with no 5xx rate because nothing logs one.
- A catch-all advice with no `log.error`.
- Load tests run to expected peak rather than to saturation, leaving the ceiling unmeasured.

## Related

- [Percentiles, Tail Latency, and Coordinated Omission](../syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md)
- [Incident Response and Blameless Postmortems](../syllabus/13-observability/incident-response-and-blameless-postmortems.md)
- [Production Cookbook — symptom index](../production-cookbook/README.md)
- [Mock Interview: Production Debugging Round](../practice/mock-interviews/production-debugging-round.md)
