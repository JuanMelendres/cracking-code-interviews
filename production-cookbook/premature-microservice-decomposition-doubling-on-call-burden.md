---
title: "Premature Microservice Decomposition Doubling On-Call Burden"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md
source: handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md#production-scenarios
---

# Premature Microservice Decomposition Doubling On-Call Burden

## Context

A four-person team, six months after a "best practices" migration, has split a single deployable application into five microservices, each with its own deployment pipeline, its own monitoring dashboards, and its own on-call rotation entry.

## Symptoms

Deployment velocity has decreased, not increased — nearly every feature still requires coordinated changes across three or four of the five services, deployed within the same release window. On-call load has measurably increased, with incidents now frequently requiring cross-service log correlation to diagnose.

## Impact

Slower delivery, higher operational cost, and no realized benefit from the split.

## Initial Hypotheses

- The team needs better cross-service tooling — partially true, but treats the symptom rather than the cause.
- The services need clearer API contracts — also partially true, same issue.
- The decomposition itself was premature for this team's size and structure — correct, on review.

## Evidence

Deployment logs show that in the prior quarter, deploys of any one of the five services were followed by a deploy of at least one other service within the same day in over 80% of cases — the "always co-deployed" signal, directly measurable.

## Investigation Timeline

1. **Symptoms noticed**: deployment velocity trending down and on-call load trending up, despite the split having been justified as an improvement to both.
2. **Tooling and contract hypotheses raised**, addressed partially, without resolving the underlying trend.
3. **Co-deployment measured directly** from deployment logs across the prior quarter, surfacing the 80%+ same-day co-deployment rate.
4. **Organizational precondition checked**: the team has four engineers and no separately-scheduled sub-teams, meaning the split's structural justification never existed.

## Root Cause

With four engineers, the team never had multiple, independently-scheduled sub-teams to begin with — the organizational precondition for microservices' benefit was never met. The split paid the full distributed-systems tax (network calls, eventual consistency, five times the operational surface area) while the coordinated-deployment pattern shows the independence benefit was never actually realized.

## Immediate Mitigation

Consolidate the two most tightly-coupled services — measured by co-deployment frequency and synchronous call volume — back into one deployable unit as a pilot.

## Permanent Fix

Evaluate merging the remaining services into a well-modularized monolith with clear internal module boundaries, following the same aggregate-boundary discipline but as one deployable unit, reserving actual service splits for the point at which the team genuinely divides into separate, independently-scheduled sub-teams.

## Alternatives Considered

Investing further in cross-service tooling — a service mesh, distributed tracing, a shared platform team — to make the existing decomposition more manageable. Rejected as treating the symptom, since the underlying organizational precondition for the split still wouldn't exist.

## Trade-offs

Merging services back together requires an explicit, visible "undo" that can feel like admitting the original decision was wrong. Accepted, since the alternative is continuing to pay a real, ongoing operational cost for a benefit that was never captured.

## Prevention

Before any decomposition, explicitly answer: does this team already have — or is it about to have — multiple, separately-scheduled sub-teams that this split would serve? If not, default to a well-modularized monolith.

## Monitoring and Alerts

- Co-deployment rate between services, tracked as a standing metric rather than something computed only after a slowdown is already suspected — this is the single concrete signal that turned a vague "things feel slower" complaint into a measurable, actionable finding.
- On-call incident count and cross-service log-correlation frequency per service, watched for a trend after any decomposition, not just deployment velocity alone.

## Interview Story

This maps to the "you have four engineers, does microservices still make sense" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a small team's microservices split increased on-call burden and slowed delivery instead of improving either.
- **Task:** determine whether the fix was better tooling or reversing the decomposition itself.
- **Action:** measure co-deployment frequency directly from deployment logs rather than relying on impression; check the organizational precondition — separately-scheduled sub-teams — that the split was supposed to serve.
- **Result:** piloted consolidating the most tightly-coupled services back together, and set an explicit precondition check before any future split.

## Staff-Level Discussion

This incident is a direct instance of a mistake that is easy to make and expensive to reverse: treating "microservices" as a best practice to adopt independent of the organizational shape it's meant to serve. The technical damage (network calls, eventual consistency, five times the surface area) is real, but the deeper cost is that reversing a decomposition carries a visible, morale-relevant "we were wrong" signal that makes teams reluctant to do it even once the evidence is clear — which is exactly why having an objective, pre-agreed measurement (co-deployment rate) matters: it turns a reversal from a subjective argument into a data-driven correction. A Staff engineer's real contribution here is establishing the precondition check *before* any future decomposition decision, not just diagnosing this one after the fact.

## Related Handbook Chapters

- [Microservice Decomposition and the Monolith Trade-off](../syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md) — canonical decomposition-precondition and co-deployment-detection methodology used here.
