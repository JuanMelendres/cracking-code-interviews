---
title: "Mock Interview: Kafka Messaging Technical Round (45 min)"
slug: kafka-messaging-technical-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - Per-partition ordering and the partition-count one-way door
  - "acks=all data-loss mechanism (ISR shrink)"
  - Repeated-rebalance diagnosis (max.poll.interval.ms)
  - Honest exactly-once scoping
  - Hot-partition remediation via compound key
  - Scaling/performance story
related:
  - ../../handbook/kafka/kafka-architecture-fundamentals.md
  - ../../handbook/kafka/producer-semantics-and-partition-keys.md
  - ../../handbook/kafka/consumer-groups-and-rebalancing.md
  - ../../handbook/kafka/delivery-semantics-and-exactly-once.md
  - ../../behavioral-handbook/04-production-incident-narratives.md
source: ../../study-packs/week-08/08-week-8-mock-interview.md
official_references: []
---

# Mock Interview: Kafka Messaging Technical Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below. Elevated from `study-packs/week-08/08-week-8-mock-interview.md`. Like the [Spring Technical Round](spring-technical-round.md), this source uses a `Part A — Candidate script` / `Part B — Interviewer script` structure with no inline per-question pass/fail signals; the Evaluator Section below constructs pass/borderline/fail signals grounded in the interviewer script's own push-back cues and the real, measured content of the canonical `handbook/kafka/` chapters (each carries a provenance note stating its traces are real, executed output against a live KRaft cluster, not invented). This is the fifth and final round in this conversion pass, closing the Mock Interviews deliverable at 12/12.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| Per-partition ordering, partition-count one-way door | Q1 | [Kafka Architecture Fundamentals](../../handbook/kafka/kafka-architecture-fundamentals.md) |
| `acks=all` data-loss mechanism (ISR shrink) | Q2 | [Kafka Producer Semantics: acks, Idempotence, and Partition Key Design](../../handbook/kafka/producer-semantics-and-partition-keys.md) |
| Repeated-rebalance diagnosis | Q3 | [Kafka Consumer Groups, Rebalancing, and Offset Management](../../handbook/kafka/consumer-groups-and-rebalancing.md) |
| Honest exactly-once scoping | Q4 | [Kafka Delivery Semantics and Exactly-Once Processing](../../handbook/kafka/delivery-semantics-and-exactly-once.md) |
| Hot-partition remediation | Q5 | [Kafka Producer Semantics: acks, Idempotence, and Partition Key Design](../../handbook/kafka/producer-semantics-and-partition-keys.md) |
| Scaling/performance story | Q6 | [Production Incident Narratives](../../behavioral-handbook/04-production-incident-narratives.md) |

## Interviewer Opening Script

*"This is a 45-minute Kafka-focused technical round. I'll ask about ordering guarantees, durability, rebalancing, exactly-once semantics, and hot partitions, then close with a story. Several of these questions have a plausible-sounding wrong answer and a more precise, scoped right one — I want the precise version, with the mechanism named, not just the conclusion. Let's start with ordering."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(6 min)** Explain what Kafka does and does not guarantee about ordering, unprompted, and why partition count is effectively a one-way door for a keyed topic.
2. **(8 min)** "`acks=all` and you still lost a message. How?" Give the full answer, naming the ISR mechanism and `min.insync.replicas`.
3. **(8 min)** "Your consumer group rebalances every 30 seconds. Diagnose it." Walk through the diagnosis, not just "something's wrong with the network."
4. **(8 min)** "Is exactly-once real? Explain precisely what Kafka provides and what it doesn't." Give the full answer, including the fix for external systems.
5. **(8 min)** "One partition holds 60% of the traffic. Fix it." Discuss the trade-off explicitly.
6. **(7 min)** Deliver a scaling/performance story using the four-beat structure.

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — Ordering guarantee + partition-count one-way door

**Ideal answer outline:** Kafka guarantees strict order only *within* a partition — nothing about relative order across partitions, even for records produced in sequence by the same producer. Per-entity ordering (e.g., all of one customer's events, in order) is achieved by keying: `hash(key) % partitionCount` routes all of one key's records to the same partition, in send order. Because assignment is derived from `partitionCount`, changing it remaps *every* key's assignment, silently invalidating existing ordering guarantees the moment the count changes — this makes partition count effectively permanent once a keyed topic is live, not a tunable capacity lever. If the candidate says "Kafka guarantees ordering" unconditionally, the correct push is: "across the whole topic, or something narrower?"
**Common weak answers:** claiming Kafka guarantees ordering across the whole topic; treating partition count as a freely adjustable scaling parameter.
**Pass signal:** correctly scopes ordering to per-partition, names the partition key as the per-entity-ordering mechanism, and states the one-way-door consequence unprompted.
**Borderline signal:** correctly scopes ordering to per-partition only after the push, but doesn't reach the one-way-door consequence unprompted.
**Fail signal:** claims topic-wide ordering even after the push, or doesn't understand key-based routing at all.

### Question 2 — `acks=all` and you still lost a message

**Ideal answer outline:** the In-Sync Replica set (ISR) had shrunk to just the leader at write time (a follower had fallen behind `replica.lag.time.max.ms` and been dropped) — `acks=all` means "wait for every replica in the *current* ISR," not "wait for `replication.factor` replicas." With the ISR down to one, `acks=all` acknowledged on a single copy, and that copy was then lost before the follower caught back up. `min.insync.replicas` is the setting that converts this from a silent loss into a loud `NotEnoughReplicasException` at write time. If the candidate claims `acks=all` is fully durable on its own, the correct push is: "what if the ISR only has one member when the write lands?"
**Common weak answers:** insisting `acks=all` is unconditionally durable with no ISR-shrink mechanism named.
**Pass signal:** names the ISR-shrink mechanism and `min.insync.replicas` as the fix, either unprompted or promptly under the push.
**Borderline signal:** senses `acks=all` alone isn't sufficient but can't name the specific ISR mechanism even under the push.
**Fail signal:** insists `acks=all` is fully durable on its own even after the push.

### Question 3 — Consumer group rebalances every 30 seconds

**Ideal answer outline:** rebalancing is triggered by group-membership changes, not a scheduled event — a periodic, recurring rebalance strongly suggests a live-but-slow consumer being mistaken for a dead one via a `max.poll.interval.ms` violation (the time between successive `poll()` calls exceeding the configured tolerance), rather than a genuine scaling event, crash, or network issue. Diagnosis: check consumer logs for `max.poll.interval.ms` warnings immediately preceding each rebalance, and measure actual per-batch processing time. If the candidate says "network issue" without qualification, the correct push is: "what specifically times out, and why does a live process get evicted?"
**Common weak answers:** "network issue" or "something's wrong with the connection" as a complete answer, with no mechanism named.
**Pass signal:** names `max.poll.interval.ms` violations as the likely cause and proposes checking processing time per batch, either unprompted or promptly under the push.
**Borderline signal:** recognizes membership change as the trigger but can't name the specific `max.poll.interval.ms` mechanism, even under the push.
**Fail signal:** insists on a network explanation even after being pushed for specifics.

### Question 4 — Is exactly-once real?

**Ideal answer outline:** neither confident flat answer ("yes" or "no") is correct. Kafka's transactional exactly-once semantics is real, but scoped to a read-process-write loop entirely *within* Kafka (consumed offsets and produced records commit as one atomic transaction). It does **not** cover a write to an external system (a database, an HTTP call) — there is no built-in atomicity between "the Kafka offset committed" and "the external write committed." Closing that gap requires either the transactional outbox pattern or an idempotent consumer (a durable dedupe key checked before the side effect). If the candidate claims Kafka is exactly-once end-to-end without qualification, the correct push is: "your consumer also writes to Postgres. Now what?"
**Common weak answers:** a flat "yes, Kafka is exactly-once" or a flat "no, that's marketing," either with no scoping.
**Pass signal:** correctly scopes exactly-once to Kafka-to-Kafka and, under the push, names the outbox pattern or an idempotent-consumer dedupe key as the fix for the external-system gap.
**Borderline signal:** correctly avoids a flat yes/no but can't name a specific external-system fix even under the push.
**Fail signal:** gives a flat "yes" or "no" even after the push, with no scoping.

### Question 5 — One partition holds 60% of the traffic

**Ideal answer outline:** this is key skew — a small number of key values (e.g., one very active customer) dominate traffic, and since a key deterministically maps to one partition, that partition becomes hot. "Add more partitions" alone doesn't fix it: it remaps the *entire* keyspace (breaking existing ordering per Question 1) without necessarily splitting the one hot key's traffic at all. The real fix is a compound key (e.g., `customerId + bucket`), explicitly trading a weaker, bucketed ordering guarantee for spread throughput. If the candidate reaches for "add more partitions" without addressing the skew, the correct push is: "does that fix a single hot customer, or does it change everyone's ordering?"
**Common weak answers:** "add more partitions" as a complete fix, with no discussion of whether it addresses the actual skewed key.
**Pass signal:** identifies key skew as the cause and proposes a compound key, explicitly naming the ordering-granularity trade-off being accepted.
**Borderline signal:** identifies key skew as the cause but, under the push, still can't articulate why "add more partitions" doesn't fix a single hot key specifically.
**Fail signal:** proposes only "add more partitions" even after the push, with no recognition that it remaps everyone's ordering without necessarily fixing the skew.

### Question 6 — Scaling/performance story

**Ideal answer outline:** a four-beat, clearly structured story (situation, action, the specific decision criterion used, and the outcome/cost) about scaling or performance work under real constraints.
**Common weak answers:** a story with no clear structure, or one that describes what changed without stating the specific reasoning behind the chosen approach.
**Pass signal:** clear four-beat structure with a specific decision criterion and outcome, scored per Technical Depth and Production Judgment (the two dimensions the source specifies for this round).
**Borderline signal:** the story is coherent but the decision criterion has to be extracted through follow-up.
**Fail signal:** no clear structure, or no identifiable decision criterion even on request.

## Scoring Rubric

Per the source mock's own instruction, score this round using the [shared six-dimension rubric](../../study-packs/week-01/10-week-1-evaluation-rubric.md)'s **Technical Depth** and **Production Judgment** dimensions specifically (1–5 scale, 3 = Mid, 4 = Senior, 5 = Staff) — matching the [Spring Technical Round](spring-technical-round.md)'s scope, since this source names the same two dimensions rather than all six.

## Debrief Guide

Walk the candidate through their scores, starting with the weakest. Questions 2 and 4 share the sharpest theme in this round: both are named in the canonical chapters as discriminating questions specifically *because* the confident wrong answers ("acks=all is fully durable," "Kafka is exactly-once end-to-end") sound plausible, and only a candidate who understands the underlying mechanism (the current ISR, not `replication.factor`; the Kafka-to-Kafka transactional scope, not a blanket guarantee) gives the precise, honest, scoped answer. Questions 1 and 5 share a different theme: both require recognizing that partition count is a one-way door, so "just add more partitions" is not a free fix for anything — a candidate who reaches for it reflexively on Q5 after correctly naming the one-way-door consequence on Q1 has an inconsistency worth naming directly in the debrief.

## Remediation Recommendations

- Weak Q1 → re-read [Kafka Architecture Fundamentals](../../handbook/kafka/kafka-architecture-fundamentals.md), specifically its real partition-routing trace.
- Weak Q2 → re-read [Kafka Producer Semantics: acks, Idempotence, and Partition Key Design](../../handbook/kafka/producer-semantics-and-partition-keys.md), specifically its ISR-shrink production scenario.
- Weak Q3 → re-read [Kafka Consumer Groups, Rebalancing, and Offset Management](../../handbook/kafka/consumer-groups-and-rebalancing.md), specifically its `max.poll.interval.ms` production scenario.
- Weak Q4 → re-read [Kafka Delivery Semantics and Exactly-Once Processing](../../handbook/kafka/delivery-semantics-and-exactly-once.md), specifically its at-least-once/at-most-once real traces and the outbox/idempotent-consumer material.
- Weak Q5 → re-read [Kafka Producer Semantics: acks, Idempotence, and Partition Key Design](../../handbook/kafka/producer-semantics-and-partition-keys.md)'s compound-key exercise.
- Weak Q6 → re-read [Production Incident Narratives](../../behavioral-handbook/04-production-incident-narratives.md) — the closest-fit chapter for a scaling/performance story, since this competency has no dedicated behavioral-handbook chapter yet.
- Any dimension scored below Senior (4) overall → retake this mock in full after remediation.
