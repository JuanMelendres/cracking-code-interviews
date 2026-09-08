---
title: "Backend Java Specialization, Week 9 — Performance and JVM Tuning"
document_type: study-pack
week: 9
track: backend-java-specialization
status: draft
estimated_hours: 6
---

# Week 9 — Performance and JVM Tuning

## Weekly Outcome

By the end of this week you can profile a running JVM with async-profiler/JFR and read a flame graph, write a JMH benchmark that avoids the common measurement pitfalls (dead-code elimination, insufficient warmup), and apply Little's Law to reason about capacity headroom for a stated service.

## Why This Week Matters

[`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../../syllabus/00-overview/learning-paths/backend-java-specialization.md) closes this path with Performance & JVM Tuning deliberately: "both are genuinely usable once the rest of the stack is solid, and Performance/JVM's profiling and capacity-planning material is easiest to internalize once there's a real, complete backend system's worth of prior material to apply it to." This is the lightest week in the pack — 3 chapters, a natural closer after 8 weeks of denser reading.

## Prerequisites

Week 4 complete (JVM internals) — profiling and benchmarking only make sense once GC behavior, JIT warmup, and escape analysis are already understood; this week applies that knowledge rather than reintroducing it.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | Profiling: async-profiler, JFR, and Flame Graphs (T-1202) |
| Wed–Thu | Benchmarking & JMH Pitfalls (T-1203) |
| Fri–Sat | Capacity Planning & Headroom (T-1208) |
| Sun | Hands-on exercises and review checklist below; final review across all 9 weeks |

## Required Reading

The full Performance & JVM Tuning domain, per [`syllabus/16-performance-jvm/INDEX.md`](../../../syllabus/16-performance-jvm/INDEX.md) (the exhaustive, canonical source) — all 3 topics.

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | T-1202 — Profiling: async-profiler, JFR, and Flame Graphs | [`profiling-jfr-and-flame-graphs.md`](../../../syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md) |
| 2 | T-1203 — Benchmarking & JMH Pitfalls | [`benchmarking-and-jmh-pitfalls.md`](../../../syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md) |
| 3 | T-1208 — Capacity Planning & Headroom | [`capacity-planning-and-headroom.md`](../../../syllabus/16-performance-jvm/capacity-planning-and-headroom.md) |

## Hands-On Exercises

Real, executed demos exist for all 3 chapters:

- [`practice/java/jvm/profiling-jfr-and-flame-graphs/`](../../../practice/java/jvm/profiling-jfr-and-flame-graphs/) (T-1202 — `run-profiled-workload.sh`)
- [`practice/java/jvm/benchmarking-and-jmh-pitfalls/`](../../../practice/java/jvm/benchmarking-and-jmh-pitfalls/) (T-1203 — real JMH 1.37 output on JDK 21.0.12, run under two different Blackhole configurations)
- [`practice/java/performance/capacity-planning-and-headroom/`](../../../practice/java/performance/capacity-planning-and-headroom/) (T-1208 — real measured output from a bounded `ExecutorService` worker pool under controlled load)

## Interview Answer Drills

Answer, out loud, before checking the chapters' own expected answers: "why does a naive JMH benchmark of an unused computation often measure zero nanoseconds, and what does `Blackhole` fix?" and "what does Little's Law say about the relationship between concurrency, throughput, and latency, and how do you use it to size a worker pool?"

## Coding Problems

None — this pack is domain-depth reading, not coding-pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a flame graph description (a wide, flat frame dominating sample time at the top of the stack), state what that shape indicates about where CPU time is actually going, before checking T-1202's own worked example.

## Review Checklist

- [ ] Completed all 3 chapters' own L1–L4 Mastery Checklists.
- [ ] Reproduced all 3 real demos listed above.
- [ ] Can explain, unprompted, why a microbenchmark run without JIT warmup produces misleading numbers.

## Completion Criteria

- [ ] Can read a flame graph and identify the dominant cost center.
- [ ] Can name at least two JMH pitfalls (dead-code elimination, constant folding, insufficient warmup) and their fixes.
- [ ] Can apply Little's Law (`L = λW`) to size a worker pool for a stated arrival rate and target latency.
- [ ] Can trace a single realistic request through the full stack from memory — a Spring controller, through a transactional service method, an indexed database query, and a Kafka event published as a side effect — naming the specific chapter from Weeks 1–8 that covers each hop, per the learning path's own completion criteria.

## Retrospective

This is the final week of the pack — review which of the 9 weeks felt weakest overall, not just this week's 3 topics, and plan a second pass through those before moving on to [Senior → Staff](../../../syllabus/00-overview/learning-paths/senior-to-staff.md).

## Next Week

None — this is the final week of the Backend Java Specialization pack. See the pack's own [README](../README.md) "After this pack" section for the recommended next learning path.
