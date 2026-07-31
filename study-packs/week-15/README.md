---
title: "Week 15 — Cloud & Infrastructure"
document_type: study-pack
week: 15
status: draft
estimated_hours: 12
---

# Week 15 — Cloud & Infrastructure

## Weekly Outcome

By the end of this week you can explain, defend, and reproduce with real artifacts the five highest-frequency Cloud & Infrastructure topics: Kubernetes resource limits/probes/JVM sizing (including the OutOfMemoryError-vs-OOMKilled distinction), Kubernetes objects/scheduling/networking, cloud cost and scaling economics, CI/CD pipeline design and deployment strategies, and AWS core services for backend engineers.

## Why This Week Matters

Cloud & Infrastructure is the third domain that had zero prior study-pack coverage in this program. T-1003 (Kubernetes resource limits, probes, and JVM sizing) is named in the blueprint as the highest-value entry in this domain — the intersection of JVM memory management (already covered in Week 9) and container resource limits, closing a real gap for candidates who know each half separately but not how they interact.

## Prerequisites

Week 9's GC Fundamentals and Log Analysis chapter (JVM memory concepts this week builds on directly).

## A note on this week's evidence

Unlike prior weeks, this domain is infrastructure-shaped rather than algorithm-shaped. Where a live Kubernetes cluster or AWS account isn't available, this pack uses the strongest available real substitute and states the scoping choice explicitly: real Docker containers with real cgroup memory limits (the same mechanism a Kubernetes pod's resource limit ultimately configures) for the JVM-sizing chapter, and real, syntax-validated Kubernetes/GitHub Actions YAML manifests (validated via a YAML parser, not applied against a live API server) for the objects/scheduling and CI/CD chapters — the same honesty convention this repository applies elsewhere (see `study-packs/week-11/02-integration-testing-against-real-dependencies.md` §4 for the same pattern with Testcontainers).

## Schedule

See `10-week-15-checklist.md` for the day-by-day breakdown.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-kubernetes-resource-limits-probes-and-jvm-sizing.md` | T-1003 — summary + link; full chapter canonical at `handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md` |
| 3 | `02-kubernetes-objects-scheduling-and-networking.md` | T-1002 — summary + link; full chapter canonical at `handbook/cloud/kubernetes-objects-scheduling-and-networking.md` |
| 4 | `03-cloud-cost-and-scaling-economics.md` | T-1007 — summary + link; full chapter canonical at `handbook/cloud/cloud-cost-and-scaling-economics.md` |
| 5 | `04-cicd-pipeline-design-and-deployment-strategies.md` | T-1009 — summary + link; full chapter canonical at `handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md` |
| 6 | `05-aws-core-services-for-backend-engineers.md` | T-1006 — summary + link; full chapter canonical at `handbook/cloud/aws-core-services-for-backend-engineers.md` |
| 7 | `06-hands-on-lab.md` | 5 labs reproducing this week's real container/YAML/arithmetic evidence |
| 8 | `07-flashcards.md` | 15 cards |
| 9 | `08-week-15-mock-interview.md` | 45-min Cloud & Infrastructure technical round |
| 10 | `09-design-exercise-deployment-infrastructure.md` | Full infrastructure/deployment design for a payments notification service |
| 11 | `10-week-15-checklist.md` | Day-by-day checklist |
| 12 | `resources.md` | Sources classified PRIMARY/SECONDARY |

## Hands-On Exercises

Complete all 5 labs in `06-hands-on-lab.md` — real Docker containers, real YAML validation, real worked arithmetic.

## Interview Answer Drills

Deliver the 30-second and 2-minute answers for each topic aloud, unprompted, from each canonical chapter's Interview Answer Framework section.

## Coding Problems

None this week in the usual LeetCode sense — see `06-hands-on-lab.md` for this week's hands-on equivalent.

## System Design Exercise

`09-design-exercise-deployment-infrastructure.md` — design the full deployment and infrastructure strategy for a payments notification service, applying all five of this week's topics.

## Behavioral Exercise

None formally scheduled this week; continue any in-progress STAR story work from earlier weeks.

## Mock Interview

`08-week-15-mock-interview.md` — 45-minute Cloud & Infrastructure technical round, candidate/evaluator sections hard-separated.

## Review Checklist

See `10-week-15-checklist.md`.

## Completion Criteria

- [ ] All five canonical chapters read in full
- [ ] All five labs in `06-hands-on-lab.md` reproduced with matching results
- [ ] Design exercise completed independently before checking the reference solution
- [ ] Mock interview average score ≥ 3.5

## Retrospective

Note which of the five topics needs a second pass, and whether the design exercise revealed a gap not caught by the individual chapter labs.

## Next Week

Java Core, Collections, and Cloud & Infrastructure — the three previously-uncovered domains — are now closed at their top-5-by-IWI depth. Next steps per the roadmap: either extend depth in these three domains (the remaining lower-priority topics noted as deferred in each week's MANIFEST), or continue into other planned Phase 5/6 work per `00-project/learning-roadmap.md`.
