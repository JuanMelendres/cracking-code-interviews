---
title: "Interview Question Bank — 15-cloud"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-27
related:
  - ../../15-cloud/INDEX.md
  - 14-devops-containers.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Cloud

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 5 chapters yielded 11 deep questions + 14 quick-fire
questions = **25 real questions**. (Updated 2026-09-27: added a "works in staging,
fails in production" systematic-troubleshooting question to The Twelve-Factor App —
a genuinely missing, very commonly-asked scenario question found via a generic
interview checklist audit. Updated again, same day: `serverless-lambda-execution-model-cold-starts-and-concurrency.md`
had a complete Interview Questions section never indexed — a stale-index gap, not a
content gap. Added 2 questions; no Flashcards section in that chapter.) No Junior
Fundamentals chapter exists in this domain — this is a genuinely small domain
(5 chapters).

---

## AWS Core Services for Backend Engineers

### Q1 — You migrated to DynamoDB for scale, and a new reporting feature can't be built against it. What happened, and how do you fix it?

**Canonical treatment:** [§ Interview Questions, Q1](../../15-cloud/aws-core-services-for-backend-engineers.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Recognizes DynamoDB's access-pattern rigidity as the cause, even without a specific fix proposal.
- **Senior:** Correctly diagnoses the access-pattern mismatch and proposes a separate, CDC-fed store for the new need, rather than forcing the ad-hoc pattern onto the primary table.
- **Staff:** Connects this explicitly to the general access-pattern method applied to *future*, not just current, needs, proposing it as a standing practice for future storage-technology decisions.

### Q2 — When would you choose EKS over ECS, given both are "managed containers"?

**Canonical treatment:** [§ Interview Questions, Q2](../../15-cloud/aws-core-services-for-backend-engineers.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that EKS is Kubernetes-based and ECS is AWS-proprietary, even without the portability/tooling reasoning.
- **Senior:** Correctly names the portability/ecosystem trade-off as the deciding factor — EKS gives the real Kubernetes API and ecosystem at the cost of a steeper operational learning curve.
- **Staff:** Connects this to an organizational factor — a team already operating Kubernetes elsewhere gets real value from EKS's consistency with existing operational knowledge, while a team with no prior exposure may find ECS's simpler model a better fit.

---

## Azure and GCP for Backend Engineers

### Q1 — Your team is being acquired by an org running entirely on GCP. Walk me through what changes about your service's architecture.

**Canonical treatment:** [§ Interview Questions, Q1](../../15-cloud/azure-and-gcp-for-backend-engineers.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names at least the storage and database GCP equivalents correctly, even without deep migration-risk discussion.
- **Senior:** Correctly maps every category and explicitly flags the DynamoDB-to-Firestore-or-Bigtable decision as requiring real access-pattern analysis, not a blind swap.
- **Staff:** Frames the whole migration as an organizational risk/cost decision — which categories are genuinely low-risk (Kubernetes-based compute) versus which carry real re-architecture risk — before committing to a timeline or cost estimate.

### Q2 — Why might Cosmos DB be a better fit than a straight DynamoDB-equivalent read in some cases?

**Canonical treatment:** [§ Interview Questions, Q2](../../15-cloud/azure-and-gcp-for-backend-engineers.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that Cosmos DB and DynamoDB are not feature-identical, even without naming the specific consistency-tuning difference.
- **Senior:** Names the tunable-consistency-level difference specifically as the deciding factor — Cosmos DB supports multiple selectable consistency levels per request, a real point of difference from DynamoDB's more fixed model.
- **Staff:** Generalizes this to the broader principle that "equivalent" services across providers are a starting point for research, not a guarantee of identical behavior.

---

## Cloud Cost and Scaling Economics

### Q1 — Walk me through the actual math for whether we should reserve or stay on-demand for this workload.

**Canonical treatment:** [§ Interview Questions, Q1](../../15-cloud/cloud-cost-and-scaling-economics.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Attempts a real calculation, even if it conflates peak and baseline.
- **Senior:** Produces a real, if simplified, cost comparison with actual arithmetic — on-demand cost versus reserved cost for the confirmed steady portion specifically.
- **Staff:** Explicitly separates the steady baseline (correct reservation target) from peak (should scale on-demand/spot instead), and states the specific risk of reserving the wrong number.

### Q2 — When would autoscaling NOT save money?

**Canonical treatment:** [§ Interview Questions, Q2](../../15-cloud/cloud-cost-and-scaling-economics.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that autoscaling doesn't always help, even without the precise mechanism.
- **Senior:** Correctly identifies flat/steady demand as the condition where autoscaling doesn't meaningfully save money — the savings come specifically from the peak/trough demand gap.
- **Staff:** Connects this to the broader principle that autoscaling and reservation strategy are two sides of the same demand-shape question, and a genuinely flat workload should be reserved near its steady level rather than autoscaled at all.

---

## The Twelve-Factor App: Config, Precedence, and Fail-Fast Validation

### Q1 — Why does a service that passed its health check sometimes still fail immediately on real traffic?

**Canonical treatment:** [§ Interview Questions, Q1](../../15-cloud/twelve-factor-config.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that "something's misconfigured" without explaining why the health check didn't catch it — the common mistake this question targets.
- **Senior:** Explains the health-check/config-completeness gap precisely and names startup validation as the fix — a health check verifies the process is running, not that every piece of configuration is present and correct.
- **Staff:** Proposes a deployment-pipeline-level prevention (diffing required config across environments) in addition to runtime validation.

### Q2 — What's the real precedence order when the same config key is set in a file, an environment variable, and a command-line argument?

**Canonical treatment:** [§ Interview Questions, Q2](../../15-cloud/twelve-factor-config.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names roughly the right order without stating that only the overridden keys change.
- **Senior:** States the full order precisely — command-line arguments, then environment variables, then the config file, then hardcoded defaults — and notes it matches the order most frameworks (e.g., Spring Boot) use.
- **Staff:** Discusses why this specific order makes operational sense — defaults are safest, file is per-environment, env vars are orchestrator-injected, CLI args are per-invocation debugging overrides.

### Q5 — A service works perfectly in staging but fails immediately in production, with no code difference. What's your systematic troubleshooting approach?

**Canonical treatment:** [§ Interview Questions, Q5](../../15-cloud/twelve-factor-config.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Guesses "probably a config difference" with no structured way to continue if that guess is wrong — the common mistake this question targets.
- **Senior:** Produces an ordered methodology: config/env vars, secrets/credentials, feature flags, data/schema differences, infrastructure, then scale-only limits — since identical code means the bug lives in something that legitimately varies by environment.
- **Staff:** Proposes an automated resolved-config diff between environments as a pre-deployment gate, treating environment drift as a standing organizational risk, not a one-off incident.

---

## Serverless: Lambda Execution Model, Cold Starts, and Concurrency

### Q1 — Your Lambda-based API's p99 latency spikes during traffic bursts even though average latency looks healthy. How would you investigate?

**Canonical treatment:** [§ Interview Questions, Q1](../../15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes cold start only affects the first request after a lull, not every concurrent request beyond the warm count — the common mistake this question targets.
- **Senior:** Checks `InitDuration`/cold-start metrics correlated with the concurrency spike — each concurrent request beyond the warm-environment count triggers its own cold start.
- **Staff:** Discusses provisioning ahead of a known future spike versus reactive provisioned-concurrency tuning after the first incident.

### Q2 — What's the actual difference between what runs in a Lambda function's initializer versus its handler, and why does it matter?

**Canonical treatment:** [§ Interview Questions, Q2](../../15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Conflates the two phases, or claims all Lambda code runs "per request" — the common mistake this question targets.
- **Senior:** Correctly states initializer code (static/global scope) runs once per execution environment during INIT; handler code runs once per invocation during INVOKE, reusing what the initializer built.
- **Staff:** Connects this to language/runtime choice — a JVM-based function's real class-loading cost as the dominant INIT contributor, versus a more lightweight runtime.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What does the EC2 → ECS/EKS → Lambda spectrum trade off? | [AWS Core Services for Backend Engineers](../../15-cloud/aws-core-services-for-backend-engineers.md#flashcards) |
| 2 | What's the actual access-model difference between S3, EBS, and EFS? | [AWS Core Services for Backend Engineers](../../15-cloud/aws-core-services-for-backend-engineers.md#flashcards) |
| 3 | What's the difference between SQS and SNS, and why are they often combined? | [AWS Core Services for Backend Engineers](../../15-cloud/aws-core-services-for-backend-engineers.md#flashcards) |
| 4 | What's the actual difference between a Security Group and a Network ACL? | [AWS Core Services for Backend Engineers](../../15-cloud/aws-core-services-for-backend-engineers.md#flashcards) |
| 5 | What was Azure Active Directory (Azure AD) renamed to, and when? | [Azure and GCP for Backend Engineers](../../15-cloud/azure-and-gcp-for-backend-engineers.md#flashcards) |
| 6 | DynamoDB is one AWS service. What are its two GCP counterparts, and how do they differ? | [Azure and GCP for Backend Engineers](../../15-cloud/azure-and-gcp-for-backend-engineers.md#flashcards) |
| 7 | Which category is genuinely, not just conceptually, the same across all three cloud providers? | [Azure and GCP for Backend Engineers](../../15-cloud/azure-and-gcp-for-backend-engineers.md#flashcards) |
| 8 | What does the on-demand → reserved → spot spectrum trade for a lower price? | [Cloud Cost and Scaling Economics](../../15-cloud/cloud-cost-and-scaling-economics.md#flashcards) |
| 9 | What should a capacity reservation be sized to — peak or steady baseline? | [Cloud Cost and Scaling Economics](../../15-cloud/cloud-cost-and-scaling-economics.md#flashcards) |
| 10 | When does autoscaling NOT save meaningful money? | [Cloud Cost and Scaling Economics](../../15-cloud/cloud-cost-and-scaling-economics.md#flashcards) |
| 11 | What does the twelve-factor app's Factor III (config) require, precisely? | [The Twelve-Factor App](../../15-cloud/twelve-factor-config.md#flashcards) |
| 12 | What's the real config precedence order between a file, an environment variable, and a CLI argument? | [The Twelve-Factor App](../../15-cloud/twelve-factor-config.md#flashcards) |
| 13 | Why does fail-fast startup validation matter for a missing required config value? | [The Twelve-Factor App](../../15-cloud/twelve-factor-config.md#flashcards) |
| 14 | A service works in staging but fails in production with no code difference — what's the ordered troubleshooting checklist? | [The Twelve-Factor App](../../15-cloud/twelve-factor-config.md#flashcards) |

---

## Related

- [`14-devops-containers.md`](14-devops-containers.md)
- [`13-observability.md`](13-observability.md)
- [`12-security.md`](12-security.md)
- [`11-system-design.md`](11-system-design.md)
- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
