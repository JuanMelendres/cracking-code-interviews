---
title: "Mid → Senior, Week 7 — Distributed Systems"
document_type: study-pack
week: 7
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 7 — Distributed Systems

## Weekly Outcome

By the end of this week you can correctly place a described system on the CAP triangle for a stated failure scenario, and name at least three real distributed-failure modes (retry amplification, split-brain, cascading failure) with a concrete mitigation for each.

## Why This Week Matters

Week 6's Kafka material was distributed systems in a specific, message-broker-shaped form. This week generalizes it — CAP trade-offs and cascading-failure mechanics recur across every remaining domain in this pack, especially Week 8's system design.

## Prerequisites

Week 6 (messaging) — several distributed-failure patterns (redelivery, rebalancing) are direct instances of this week's more general failure taxonomy.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | CAP Theorem and Consistency Models |
| Thu–Sat | Distributed Systems Failure Modes |
| Sun | Review checklist below |

## Required Reading

[`syllabus/10-distributed-systems/INDEX.md`](../../../syllabus/10-distributed-systems/INDEX.md) — this week's two priority topics (CAP Theorem and Consistency Models; Distributed Systems Failure Modes).

## Hands-On Exercises

Follow each priority chapter's own linked practice material directly — this domain's canonical chapters cite their own demos, and this pack does not maintain a separate index of them to avoid drift if a chapter's demo location changes.

## Production Cookbook Cross-Reference

- [`split-brain-from-promoting-a-standby-without-fencing-the-old-primary.md`](../../../production-cookbook/split-brain-from-promoting-a-standby-without-fencing-the-old-primary.md)
- [`retry-amplification-cascading-into-a-multi-service-outage.md`](../../../production-cookbook/retry-amplification-cascading-into-a-multi-service-outage.md)

## Interview Answer Drills

Answer, out loud: "you have a network partition — do you choose consistency or availability, and how does that decision change per endpoint in the same system?" and "how does a naive retry policy turn a transient blip into a full outage?" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week.

## System Design Exercise

Sketch, on paper, a fencing-token mechanism that would have prevented the split-brain cookbook scenario — a lightweight preview of Week 8's full system-design practice.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described service with a flaky downstream dependency, explain your retry strategy (backoff, jitter, circuit breaker) and why a naive retry-forever policy is dangerous, out loud, in under 5 minutes.

## Review Checklist

- [ ] Completed both chapters' own L3 Mastery Checklists.
- [ ] Sketched the fencing-token exercise above.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can place a described system correctly on CAP for a stated partition scenario.
- [ ] Can explain retry amplification's mechanism and at least two concrete mitigations (backoff+jitter, circuit breaker).
- [ ] Can explain what a fencing token is for and why promoting a standby without one is dangerous.

## Retrospective

Note which failure mode (split-brain or retry amplification) felt more abstract before this week — both are consistently among the highest-frequency Staff-adjacent follow-up questions once a Senior-level system-design answer is given.

## Next Week

[Week 8 — System Design](../week-08/README.md).
