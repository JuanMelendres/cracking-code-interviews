---
title: "Week 10 Checklist"
week: 10
last_reviewed: 2026-07-31
---

# Week 10 Checklist

Day-by-day, matching `README.md`'s schedule.

---

- [ ] **Monday**
  - [ ] Read `01-saga-outbox-and-distributed-transactions.md` in full
  - [ ] **Reproduce the dual-write hazard demo**
  - [ ] LC 215 (Kth Largest Element)

- [ ] **Tuesday**
  - [ ] **Reproduce the full outbox crash-recovery sequence** (`08-outbox-implementation-deliverable.md`)
  - [ ] Begin writing your own `outbox-implementation.md` notes
  - [ ] LC 347 (Top K Frequent Elements)

- [ ] **Wednesday**
  - [ ] Read `02-sharding-and-partitioning-strategies.md` and `03-consistent-hashing.md` in full
  - [ ] **Reproduce both the partition-pruning EXPLAIN demo and the consistent-hashing demo**
  - [ ] LC 23 (Merge K Sorted Lists)

- [ ] **Thursday**
  - [ ] Read `04-resilience-patterns.md` in full
  - [ ] **Reproduce both the circuit-breaker and retry-jitter demos**
  - [ ] LC 295 (Find Median from Data Stream)

- [ ] **Friday**
  - [ ] Read `05-zero-downtime-migration.md` in full
  - [ ] **Reproduce the blocking-vs-CONCURRENTLY index scripts**
  - [ ] Finish `outbox-implementation.md`

- [ ] **Saturday**
  - [ ] Full distributed-cache design, 45-60 min timed (`10-design-exercise-distributed-cache.md`)

- [ ] **Sunday**
  - [ ] 60-min architecture-round mock (`09-week-10-mock-architecture-round.md`)
  - [ ] Review against `README.md`'s exit criteria

---

## If you fall behind — priority order

1. `outbox-implementation.md` — this week's named deliverable, and the highest-IWI-adjacent topic (T-618 is the convergence point of three earlier weeks)
2. The outbox crash-recovery reproduction specifically — the one demo that directly proves this week's central claim (at-least-once, zero loss)
3. The 60-min architecture mock
4. Sharding, consistent-hashing, resilience, and zero-downtime-migration reading — defer full depth if truly short on time, but reproduce all five demos regardless; every chapter this week leans on a specific measured number, not prose alone
