---
title: "Week 8 Mock — Messaging Deep-Dive (45 min)"
week: 8
last_reviewed: 2026-07-29
---

# Week 8 Mock — Messaging Deep-Dive

**Format:** 45 minutes, Kafka-focused technical round, per `00-project/learning-roadmap.md` §4 Week 8.

## Table of Contents

1. [Part A — Candidate script](#part-a--candidate-script)
2. [Part B — Interviewer script](#part-b--interviewer-script)

---

## Part A — Candidate script

1. **(6 min)** Explain what Kafka does and does not guarantee about ordering, unprompted, and why partition count is effectively a one-way door for a keyed topic.
2. **(8 min)** "`acks=all` and you still lost a message. How?" Full answer naming the ISR mechanism and `min.insync.replicas`.
3. **(8 min)** "Your consumer group rebalances every 30 seconds. Diagnose it." Walk through the `max.poll.interval.ms` diagnosis, not just "something's wrong with the network."
4. **(8 min)** "Is exactly-once real? Explain precisely what Kafka provides and what it doesn't." Full answer including the outbox/idempotent-consumer fix for external systems.
5. **(8 min)** "One partition holds 60% of the traffic. Fix it." Discuss the compound-key trade-off explicitly.
6. **(7 min)** Deliver Story 11 (scaling/performance) using the four-beat structure.

## Part B — Interviewer script

1. On ordering: if the candidate says "Kafka guarantees ordering" without qualification, push: "across the whole topic, or something narrower?"
2. On `acks=all`: if the candidate says it's fully durable on its own, ask "what if the ISR only has one member when the write lands?"
3. On rebalancing: don't accept "network issue" as a complete answer — ask "what specifically times out, and why would a live process get evicted?"
4. On exactly-once: if the candidate claims Kafka is exactly-once end-to-end without qualification, redirect: "your consumer also writes to Postgres. Now what?"
5. On the hot partition: push past "add more partitions" — ask "does that fix a single hot customer, or does it change everyone's ordering?"
6. Score using `study-packs/week-01/10-week-1-evaluation-rubric.md`'s Technical Depth and Production Judgment dimensions.
