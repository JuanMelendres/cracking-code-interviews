---
title: "Week 15 Flashcards — Cloud & Infrastructure"
week: 15
document_type: study-pack-flashcards
status: draft
last_reviewed: 2026-07-31
---

# Week 15 Flashcards — Cloud & Infrastructure

15 cards, three per topic, each naming the misconception it catches.

## Card 1

**Prompt:** What does JDK 10+ container-aware heap sizing actually read?
**Answer:** The container's cgroup memory limit, not the host machine's total memory — confirmed directly via `Runtime.getRuntime().maxMemory()` at different `--memory` limits.
**Why it matters:** Prevents a JVM from sizing its heap against memory it will never actually be allowed to use.
**Common trap:** Assuming a flat 25% heap ratio regardless of container size (small containers get a 50% floor).
**Related:** `01-kubernetes-resource-limits-probes-and-jvm-sizing.md`

## Card 2

**Prompt:** What's the structural difference between OutOfMemoryError and an OOMKill?
**Answer:** OutOfMemoryError is a JVM-level, catchable exception when the heap itself is exhausted (exit 1). An OOMKill is the Linux kernel's SIGKILL when total process memory exceeds the container's cgroup limit, with zero Java-level signal (exit 137, `OOMKilled=true`).
**Why it matters:** Determines where to look when debugging — application logs vs. container-level termination reason.
**Common trap:** Assuming an unexplained restart is an application bug without checking the container-level reason first.
**Related:** `01-kubernetes-resource-limits-probes-and-jvm-sizing.md`

## Card 3

**Prompt:** What's the difference between a readiness probe and a liveness probe?
**Answer:** A failing readiness probe removes the pod from load-balancing without restarting it. A failing liveness probe restarts the container.
**Why it matters:** Using one probe type for both purposes loses the ability to distinguish "temporarily overloaded" from "permanently broken."
**Common trap:** Using the same endpoint/probe for both readiness and liveness.
**Related:** `01-kubernetes-resource-limits-probes-and-jvm-sizing.md`

## Card 4

**Prompt:** Why does a Deployment manage ReplicaSets rather than Pods directly?
**Answer:** So updating the Pod template can create a new ReplicaSet and gradually shift replicas from old to new — the mechanism that makes rolling updates possible.
**Why it matters:** Explains the actual object model, not just "Kubernetes does rolling updates."
**Common trap:** Describing Deployments as directly managing Pods, skipping the ReplicaSet layer.
**Related:** `02-kubernetes-objects-scheduling-and-networking.md`

## Card 5

**Prompt:** What's the functional difference between `resources.requests` and `resources.limits`?
**Answer:** `requests` is what the scheduler uses to decide if a node has room; `limits` is enforced later, at runtime, by the kubelet/container runtime.
**Why it matters:** A common source of capacity-planning confusion.
**Common trap:** Assuming the scheduler enforces limits at placement time.
**Related:** `02-kubernetes-objects-scheduling-and-networking.md`

## Card 6

**Prompt:** Does `maxUnavailable: 0` guarantee performance is unaffected during a rollout?
**Answer:** No — it guarantees Pod count never drops below the desired replica count, but says nothing about whether a newly-ready Pod is actually at steady-state performance yet.
**Why it matters:** A real, measured gap between documented guarantee and commonly-assumed guarantee.
**Common trap:** Assuming "zero unavailable" means "zero performance impact."
**Related:** `02-kubernetes-objects-scheduling-and-networking.md`

## Card 7

**Prompt:** What should a capacity reservation be sized to — peak or steady baseline?
**Answer:** The confirmed steady baseline, never peak — reserving peak means paying the committed rate for capacity unused most of the day, which can cost more than correctly autoscaling.
**Why it matters:** A real, computable cost mistake this week's arithmetic demonstrates directly.
**Common trap:** Sizing a reservation to peak demand "to be safe."
**Related:** `03-cloud-cost-and-scaling-economics.md`

## Card 8

**Prompt:** When does autoscaling NOT save meaningful money?
**Answer:** When demand is genuinely flat/steady — the savings come specifically from the peak/trough demand gap, which a flat workload doesn't have.
**Why it matters:** Prevents applying autoscaling as a default assumed-cost-win regardless of demand shape.
**Common trap:** Treating autoscaling as inherently cost-reducing.
**Related:** `03-cloud-cost-and-scaling-economics.md`

## Card 9

**Prompt:** What does the on-demand → reserved → spot spectrum trade for a lower price?
**Answer:** Flexibility/guarantee — reserved requires a term commitment, spot accepts sudden reclamation risk.
**Why it matters:** The right choice depends on demand predictability, not just per-unit price.
**Common trap:** Choosing purely by discount percentage without considering the workload's tolerance for the trade-off.
**Related:** `03-cloud-cost-and-scaling-economics.md`

## Card 10

**Prompt:** How do rolling, blue-green, and canary deployments each bound release risk?
**Answer:** Rolling shrinks exposure as the rollout proceeds; blue-green keeps new at zero traffic until an instant, reversible cutover; canary deliberately routes a small, monitored slice of real traffic first.
**Why it matters:** Different mechanisms, different costs — the right choice depends on the specific service's risk profile.
**Common trap:** Treating all three as interchangeable "ways to deploy."
**Related:** `04-cicd-pipeline-design-and-deployment-strategies.md`

## Card 11

**Prompt:** What does canary deployment's entire value depend on?
**Answer:** Someone (or something reliable) actually evaluating the canary's signal before promoting further — "no alert fired" alone is a weaker signal than an explicit confirmation of health.
**Why it matters:** A fully-automated promotion gate can only catch what its thresholds were explicitly designed to catch.
**Common trap:** Automatically promoting after a fixed wait with no alert, treating silence as proof of health.
**Related:** `04-cicd-pipeline-design-and-deployment-strategies.md`

## Card 12

**Prompt:** What's the real cost of blue-green deployment, beyond "it's more complex"?
**Answer:** Double infrastructure cost during the transition window — both full environments run simultaneously.
**Why it matters:** The specific, concrete trade-off against blue-green's instant-rollback benefit.
**Common trap:** Describing only blue-green's benefits without naming this specific cost.
**Related:** `04-cicd-pipeline-design-and-deployment-strategies.md`

## Card 13

**Prompt:** What does the EC2 → ECS/EKS → Lambda spectrum trade off?
**Answer:** Control for reduced operational ownership — EC2 gives full control and full ownership; Lambda gives zero server management at the cost of execution limits and a different programming model.
**Why it matters:** The core organizing principle for AWS compute choices.
**Common trap:** Choosing a compute service by popularity rather than this actual trade-off.
**Related:** `05-aws-core-services-for-backend-engineers.md`

## Card 14

**Prompt:** What's the actual access-model difference between S3, EBS, and EFS?
**Answer:** S3 is object storage (HTTP-style API, not mounted); EBS is block storage attached to one instance at a time; EFS is a shared, network-attached filesystem multiple instances can mount simultaneously.
**Why it matters:** Prevents treating all three as interchangeable "AWS storage."
**Common trap:** Choosing based on price alone without matching to the actual access model needed.
**Related:** `05-aws-core-services-for-backend-engineers.md`

## Card 15

**Prompt:** What's the difference between SQS and SNS, and why are they often combined?
**Answer:** SQS is point-to-point durable delivery (one message, one consumer); SNS is pub/sub fan-out (one message, many subscribers). Combined via SNS fanning out to multiple SQS queues.
**Why it matters:** They solve different problems; a workflow needing both fan-out and durable per-consumer processing needs both services together.
**Common trap:** Treating SQS and SNS as alternatives rather than complementary.
**Related:** `05-aws-core-services-for-backend-engineers.md`
