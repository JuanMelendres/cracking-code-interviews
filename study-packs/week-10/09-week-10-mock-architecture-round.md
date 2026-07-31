---
title: "Week 10 Mock — Architecture Round (60 min)"
week: 10
last_reviewed: 2026-07-31
---

# Week 10 Mock — Architecture Round

**Format:** 60 minutes, per `00-project/learning-roadmap.md` §4 Week 10 — longer than a standard 45-min round because this week's material is genuinely Staff-tier and the design exercise itself needs the extra time.

## Table of Contents

1. [Part A — Candidate script](#part-a--candidate-script)
2. [Part B — Interviewer script](#part-b--interviewer-script)

---

## Part A — Candidate script

1. **(6 min)** "You wrote to the DB and published to Kafka. Prove no message is lost." Full answer using `01-saga-outbox-and-distributed-transactions.md` §3/§4's real numbers (3 orders, 4 deliveries, 0 lost).
2. **(6 min)** "Compensate a charged payment." Full Saga-compensation answer, explicitly not framed as a rollback.
3. **(6 min)** "Chose the wrong shard key. Recovery plan?" Connect explicitly to `05-zero-downtime-migration.md`'s expand-contract technique.
4. **(6 min)** "Add a node — how much data moves?" Must cite the real 92.5% vs 9.2% measurement, not just name consistent hashing.
5. **(6 min)** "Set the timeout — from what data?" Full answer grounded in latency percentiles, not a round number.
6. **(25 min)** Full six-phase distributed-cache design, cold, timed — see `10-design-exercise-distributed-cache.md` for the worked reference; do the live round first, without it.
7. **(5 min)** Story 13, if scheduled — check `README.md`'s Track C note for this week.

## Part B — Interviewer script

1. On the outbox proof: if the candidate proposes "just retry the Kafka call" without addressing that a crash before the call means nothing exists to retry, push: "what retries the retry?"
2. On payment compensation: if the candidate says "roll back the charge," stop and redirect — no cross-service rollback exists; ask "what ACTUAL API call reverses a completed charge?"
3. On the wrong shard key: if the candidate proposes a quick reconfiguration, push: "the data physically lives on the wrong shards right now — walk me through what actually has to happen."
4. On consistent hashing: if the candidate names it without a number, ask "roughly what fraction, and why not more?"
5. On the distributed-cache design: introduce a mid-round change after Phase 4 (e.g., "the cache must now survive a full node failure without a cold-start latency spike") and observe whether the candidate revises the existing design coherently — same unseen-problem-handling criterion as Week 9's checkpoint Round 3.
6. Score using `study-packs/week-01/10-week-1-evaluation-rubric.md`'s full rubric, weighting System Design and Production Judgment heavier than usual for this round given the week's Staff-tier topic mix.
