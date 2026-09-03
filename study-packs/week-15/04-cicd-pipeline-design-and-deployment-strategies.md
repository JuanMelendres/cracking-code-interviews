---
title: "T-1009 · CI/CD Pipeline Design and Deployment Strategies"
topic_id: T-1009
domain: Cloud
tier: Core
iwi: 5.80
prerequisites: [T-1002]
unlocks: []
week: 15
last_reviewed: 2026-07-31
canonical: ../../handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md
---

# T-1009 · CI/CD Pipeline Design and Deployment Strategies

**IWI 5.80 · Core tier · High interview frequency**

**Canonical chapter:** [CI/CD Pipeline Design and Deployment Strategies](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md). This file is the Week 15 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the workflow behind this summary is real, syntax-validated GitHub Actions YAML at `practice/k8s/week-15/ci-cd-pipeline.yaml`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The three-stage canary pipeline, validated](#3-the-three-stage-canary-pipeline-validated)
4. [Why the promotion gate needs a human, not just an alert](#4-why-the-promotion-gate-needs-a-human-not-just-an-alert)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

Rolling, blue-green, and canary deployments each bound release risk differently: by shrinking exposure, by an instant reversible cutover, or by a deliberately monitored slice of real traffic. → [Definition and Purpose](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#definition-and-purpose).

## 2. Why it exists

Every deployment carries risk, and these strategies differ in exactly how that risk is bounded. → [Definition and Purpose](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#definition-and-purpose).

## 3. The three-stage canary pipeline, validated

Validated: a real 3-job GitHub Actions pipeline (`build-and-test` → `deploy-canary` → `promote-to-stable`) parses correctly. The canary stage deploys to a separate Deployment and watches metrics for 10 minutes before the job completes. → [Internal Implementation](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#internal-implementation) has the full manifest.

## 4. Why the promotion gate needs a human, not just an alert

`promote-to-stable` uses a GitHub environment protection rule requiring manual approval — because "no alert fired" and "a human confirmed this looks healthy" are different levels of evidence. → [Core Concepts](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#core-concepts).

## 5. Trade-offs

Blue-green's instant rollback costs double infrastructure during the transition; a human approval gate adds latency but catches what thresholds alone would miss. → [Trade-offs](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#trade-offs).

## 6. Interview questions

1. Your canary passed with no alerts, but the release still caused a production issue. What's your process gap?
2. When would blue-green be the wrong choice despite its instant-rollback benefit?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#interview-questions).

## 7. Common mistakes

Treating "no alert fired" as equivalent to "confirmed healthy"; choosing a deployment strategy by habit rather than per-service risk profile. → [Common Mistakes](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#common-mistakes).

## 8. Staff-level discussion

Any fully-automated gate is only as good as the specific conditions it was explicitly designed to check — the same gap applies to test suites, code review, and escalation decisions generally. → [Staff-Level Discussion](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#interview-answer-framework).

## 9. Summary

Canary's entire value depends on the signal actually being evaluated. A real-shaped incident shows a fully-automated promotion missing a regression a human reviewer would have caught. → [Summary](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#practice-exercises) and [Solutions](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#solutions). Manifest: `practice/k8s/week-15/ci-cd-pipeline.yaml`.

## 14. Additional Reading

- Martin Fowler, "BlueGreenDeployment" and "CanaryRelease"

## 15. Official References

- [GitHub Actions documentation](https://docs.github.com/en/actions)
