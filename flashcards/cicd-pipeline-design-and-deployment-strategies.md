---
title: "Flashcards: CI/CD Pipeline Design and Deployment Strategies"
slug: cicd-pipeline-design-and-deployment-strategies
document_type: flashcard-deck
domain: cloud
topic_id: T-1009
canonical: ../handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md
last_updated: 2026-08-06
---

# Flashcards: CI/CD Pipeline Design and Deployment Strategies

**Canonical chapter:** [`syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md`](../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md)

## Card: How the three strategies bound risk

**Prompt:**
How do rolling, blue-green, and canary deployments each bound release risk?

**Answer:**
Rolling shrinks exposure as the rollout proceeds (incidental to its mechanism); blue-green keeps new at zero traffic until an instant, reversible cutover; canary deliberately routes a small, monitored slice of real traffic first.

**Why it matters:**
Different mechanisms, different costs — the right choice depends on the specific service's risk profile.

**Common trap:**
Treating all three as interchangeable "ways to deploy" without their distinct risk-bounding mechanisms.

**Related:**
[Definition and Purpose](../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#definition-and-purpose)

## Card: What canary's value depends on

**Prompt:**
What does canary deployment's entire value depend on?

**Answer:**
Someone (or something reliable) actually evaluating the canary's signal before promoting further — "no alert fired" alone is a weaker signal than an explicit confirmation of health.

**Why it matters:**
A fully-automated promotion gate can only catch what its thresholds were explicitly designed to catch.

**Common trap:**
Automatically promoting after a fixed wait with no alert, treating silence as proof of health.

**Related:**
[Production Scenarios](../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#production-scenarios)

## Card: Blue-green's real cost

**Prompt:**
What's the real cost of blue-green deployment, beyond "it's more complex"?

**Answer:**
Double infrastructure cost during the transition window — both full environments run simultaneously.

**Why it matters:**
The specific, concrete trade-off against blue-green's instant-rollback benefit.

**Common trap:**
Describing only blue-green's benefits without naming this specific cost.

**Related:**
[Trade-offs](../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#trade-offs)
