---
title: "Week 9 Checklist"
week: 9
last_reviewed: 2026-07-29
---

# Week 9 Checklist

Day-by-day, matching `README.md`'s schedule. This is a checkpoint week — Sunday is the full 3-round loop, not a single 45-min mock.

---

- [ ] **Monday**
  - [ ] Read `01-java-memory-model-and-volatile.md` in full
  - [ ] **Reproduce the visibility demo yourself, 3 times**
  - [ ] LC 1114 (Print in Order)

- [ ] **Tuesday**
  - [ ] Read `02-executors-and-thread-pool-sizing.md` in full
  - [ ] **Reproduce the unbounded-queue and bounded-queue demos**
  - [ ] LC 1115 (Print FooBar Alternately)

- [ ] **Wednesday**
  - [ ] Read `03-deadlock-races-and-thread-diagnostics.md` in full
  - [ ] **Reproduce all three demos (thread states, deadlock, race condition)**
  - [ ] LC 1116 (Print Zero Even Odd)

- [ ] **Thursday**
  - [ ] Read `04-virtual-threads.md` in full
  - [ ] **Reproduce both demos, including the pinning demo with the parallelism flag**
  - [ ] LC 62 + LC 1143 (DP part 2, first two)
  - [ ] Story 12 (ambiguity/incomplete information)

- [ ] **Friday**
  - [ ] Read `05-gc-fundamentals-and-log-analysis.md` in full
  - [ ] **Reproduce the allocation-storm demo and read your own captured `gc.log`**
  - [ ] LC 416 + LC 5 (DP part 2, remaining two)

- [ ] **Saturday**
  - [ ] Full distributed-job-scheduler design, 45 min timed (`09-design-exercise-distributed-job-scheduler.md`)
  - [ ] Review all five chapters' Interview Questions sections cold, out loud

- [ ] **Sunday**
  - [ ] Full 3-round checkpoint loop (`08-week-9-checkpoint.md`) — technical deep-dive, coding, system design
  - [ ] Fill in the checkpoint scorecard honestly, including partial passes
  - [ ] Review against `README.md`'s exit criteria

---

## If you fall behind — priority order

1. The Sunday checkpoint loop and scorecard — this week's entire structural point is the diagnostic gate, don't skip it even if everything else is compressed
2. The `volatile`/happens-before and deadlock-diagnostics demo reproductions — these are the two named errata topics, run them even if surrounding reading is deferred
3. Story 12
4. The executor-sizing, virtual-threads, and GC-log reading — defer full depth if truly short on time, but reproduce all demos regardless; this week's chapters lean unusually heavily on "the number is the point," not the prose around it
