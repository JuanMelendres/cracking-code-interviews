---
title: "Week 9 Checkpoint — Full 3-Round Loop"
week: 9
last_reviewed: 2026-07-29
---

# Week 9 Checkpoint — Full 3-Round Loop

**Format:** the full simulated loop per `00-project/learning-roadmap.md` §4, Week 9 — this is the first checkpoint gate since Week 6, and the first to span the entire register (W1–W9), not just the current week.

## Table of Contents

1. [Round 1 — Technical deep-dive (45 min)](#round-1--technical-deep-dive-45-min)
2. [Round 2 — Coding (45 min)](#round-2--coding-45-min)
3. [Round 3 — System design (45 min)](#round-3--system-design-45-min)
4. [Checkpoint scorecard](#checkpoint-scorecard)
5. [If you don't pass a dimension](#if-you-dont-pass-a-dimension)

---

## Round 1 — Technical deep-dive (45 min)

**Candidate script:**

1. **(6 min)** Explain `volatile` via happens-before, not caching — the specific pass criterion for this checkpoint's Java-fluency dimension. Use `01-java-memory-model-and-volatile.md` §3's real numbers if pushed for evidence.
2. **(6 min)** Any W1–W9 topic, interviewer's choice, 5+ follow-ups deep (see interviewer script).
3. **(6 min)** "Queue is unbounded and memory is climbing. Why?" Full answer with `02-executors-and-thread-pool-sizing.md`'s real measured numbers.
4. **(6 min)** "Two threads deadlock in production. Walk me through diagnosing it live." Name `ThreadMXBean`/`jstack` explicitly.
5. **(6 min)** "What actually changes for IO-bound workloads under virtual threads — and what's the catch?" Must name pinning unprompted.
6. **(8 min)** Diagnose from an artifact: interviewer hands over `practice/java/week-09/gc/gc.log` (reproduce it first) cold, candidate reads pause type, before/after occupancy, and trend without prior context.
7. **(7 min)** Story 12 (ambiguity/incomplete information) using the four-beat structure.

**Interviewer script:**

1. On `volatile`: if the candidate says "prevents caching," stop and redirect — this is the checkpoint's named failure mode, don't let it pass.
2. On the deep W1–W9 topic: pick genuinely at random from any prior week's chapter; push for 5+ follow-ups regardless of how strong the first answer is — depth under sustained pressure is what's being measured, not the first answer alone.
3. On the unbounded queue: if the candidate says "increase heap/memory" instead of naming the queue policy, redirect to "what SPECIFICALLY is unbounded here?"
4. On the GC log: give no context beyond the raw log lines. A candidate who asks "what was the workload doing / what's the heap size" before diagnosing is doing this correctly — note it as a strength, not a stall.
5. Score using `study-packs/week-01/10-week-1-evaluation-rubric.md`'s full rubric, all dimensions.

## Round 2 — Coding (45 min)

**Format:** two problems, unseen order, verbalized approach before coding (T-1419 communication during coding — named in the blueprint as absent from most candidates' practice and disproportionately valuable).

1. One concurrency problem from `06-java-coding-practice.md` (LC 1114/1115/1116), interviewer's choice — candidate must narrate the coordination primitive choice (why a `Semaphore` here, not a `synchronized`/`wait`/`notify` scheme) before writing code.
2. One DP problem, interviewer's choice from either this week's part 2 (LC 62/1143/416/5) or Week 8's part 1 (LC 70/198/322/300) — candidate must state the recurrence relation verbally before coding it.

**Pass bar for this checkpoint specifically** (per the roadmap's own table): Medium-difficulty problems solved in ≤25 minutes; 2 Hards solved cumulatively across the whole program to date, not just this round.

## Round 3 — System design (45 min)

**Problem:** *Design a distributed job scheduler.* Full six-phase method — see `09-design-exercise-distributed-job-scheduler.md` for the worked reference; do the live round cold, without the worked notes, then compare afterward.

**Interviewer script:** introduce a mid-round change after Phase 4 (e.g., "jobs now need exactly-once execution guarantees, not at-least-once") and observe whether the candidate revises the existing design coherently or bolts on a patch without reconsidering earlier decisions — this is the unseen-problem-handling criterion from the checkpoint table below, not a separate skill.

## Checkpoint scorecard

Per `00-project/learning-roadmap.md` §4, Week 9's own pass table — fill in honestly, including partial passes:

| Dimension | Pass bar | Evidence this week | Pass? |
|---|---|---|---|
| Technical depth | 5+ follow-ups on any W1–W9 topic | Round 1, item 2 | |
| Coding | 110+ cumulative; Medium ≤25 min; 2 Hards solved | Round 2, cumulative log across W1–W9 | |
| System design | 9 problems completed; unseen problem handled cleanly | Round 3 + cumulative design-exercise count across W1–W9 | |
| Behavioral | 12 stories; any answerable in 30s / 2min / 5min | Story 12 + cumulative story bank | |
| Java fluency | Explain `volatile` via happens-before, not caching | Round 1, item 1 | |
| Production judgment | Diagnose from an artifact (GC log, `EXPLAIN`, flame graph) | Round 1, item 6 | |

## If you don't pass a dimension

This checkpoint is diagnostic, not a gate that blocks the rest of Plan B — but a failed dimension should change what gets prioritized in Week 10 rather than being carried forward silently:

- **Technical depth or Java fluency fail** → revisit `01-java-memory-model-and-volatile.md` and `03-deadlock-races-and-thread-diagnostics.md` before starting Week 10; these are exactly the errata topics this week existed to correct.
- **Coding fail** → the deficit is almost always volume, not concept — schedule extra daily coding sessions rather than re-studying.
- **System design fail** → redo `09-design-exercise-distributed-job-scheduler.md` cold a second time before Week 10's design exercise, specifically practicing the mid-round-change response.
- **Behavioral fail** → this is the one dimension where the fix is time, not effort — a rushed story portfolio reads as rushed; don't try to compress it into a single extra session.
