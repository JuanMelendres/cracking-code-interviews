---
title: "Mid → Senior, Week 2 — JVM Internals"
document_type: study-pack
week: 2
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 2 — JVM Internals

## Weekly Outcome

By the end of this week you can read a GC log and identify whether a pause is a symptom of undersized heap, allocation pressure, or a genuine leak, and explain escape analysis well enough to predict when the JIT will stack-allocate an object instead of heap-allocating it.

## Why This Week Matters

Week 1's concurrency internals and this week's JVM internals are the two domains almost every later production-debugging scenario in this pack (Spring, databases, distributed systems) eventually traces back to — GC pauses and thread contention are the two most common root causes behind a vague "latency regression" ticket.

## Prerequisites

Week 1 — concurrency internals, since GC pause diagnosis often involves distinguishing a GC-caused pause from a lock-contention-caused one.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | GC Fundamentals and Log Analysis |
| Thu–Fri | Escape Analysis and Scalar Replacement |
| Sat | Practice exercises from both chapters |
| Sun | Review checklist below |

## Required Reading

[`syllabus/02-java/INDEX.md`](../../../syllabus/02-java/INDEX.md) — this week's two priority topics (GC Fundamentals and Log Analysis; Escape Analysis and Scalar Replacement), as named in the learning path's sequence table.

## Hands-On Exercises

Real, compiled demos exist under [`practice/java/jvm/`](../../../practice/java/jvm/) — `profiling-jfr-and-flame-graphs/`, `benchmarking-and-jmh-pitfalls/`. Follow the link from each priority chapter to its own matching demo.

## Production Cookbook Cross-Reference

- [`unconditional-heap-growth-and-memory-leak-diagnosis.md`](../../../production-cookbook/unconditional-heap-growth-and-memory-leak-diagnosis.md)
- [`jstack-triggered-safepoint-pause-misdiagnosed-via-gc-logs.md`](../../../production-cookbook/jstack-triggered-safepoint-pause-misdiagnosed-via-gc-logs.md)

## Interview Answer Drills

Answer, out loud: "how do you tell a GC pause from a lock-contention pause using only a thread dump and a GC log?" and "what makes an object eligible for escape analysis's scalar replacement?" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a real GC log excerpt (from the GC Fundamentals chapter's own practice material), classify the pause type and propose one concrete tuning change, cold, in under 10 minutes.

## Review Checklist

- [ ] Completed both chapters' own L3 Mastery Checklists.
- [ ] Reproduced the JFR/flame-graph and JMH-pitfalls demos.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can classify a GC log excerpt's pause type unprompted.
- [ ] Can explain escape analysis and scalar replacement with a correct code example.
- [ ] Can distinguish a true memory leak from allocation pressure using stated diagnostic steps.

## Retrospective

Note whether GC-log reading felt fast or slow this week — this is a skill that compounds directly into Week 4's database and Week 9's observability material, both of which also involve reading unfamiliar diagnostic output under time pressure.

## Next Week

[Week 3 — Spring Internals](../week-03/README.md).
