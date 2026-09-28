---
title: "Interview Question Bank — 14-devops-containers"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-27
related:
  - ../../14-devops-containers/INDEX.md
  - 13-observability.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — DevOps and Containers

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 7 chapters yielded 12 deep questions + 5 leveled
Junior/Mid questions + 12 quick-fire questions = **29 real questions**. This is a
genuinely small domain (7 chapters); `docker-and-containers-fundamentals.md` is this
domain's Junior Fundamentals chapter (older numbered `## 15. Interview Questions`
template, no Flashcards section of its own). (Updated 2026-09-27:
`docker-compose-multi-service-orchestration.md` and
`horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md` had complete
Interview Questions sections never indexed — a stale-index gap, not a content gap.
Added 4 questions + 3 quick-fire cards.)

---

## Docker and Containers Fundamentals (Junior Fundamentals)

**Canonical treatment:** [§15, Interview Questions](../../14-devops-containers/docker-and-containers-fundamentals.md#15-interview-questions)

### Q1 (Junior) — What's the difference between a Docker image and a Docker container?

**What's expected:** An image is a read-only template; a container is one running (or stopped) instance created from that image — the same class/object relationship as in object-oriented programming.

### Q2 (Junior/Mid) — What's the difference between a container and a virtual machine?

**What's expected:** A container shares the host's kernel and is isolated via kernel features (namespaces/cgroups); a VM runs its own complete guest kernel on virtualized hardware, making it heavier and slower to start.

### Q3 (Mid) — You ran a container but can't reach it from your browser. What do you check first?

**What's expected:** Whether the container was started with an explicit `-p` port mapping — `EXPOSE` alone in the Dockerfile does not publish a port to the host.

### Q4 (Mid) — Why does Dockerfile instruction order affect build speed?

**What's expected:** Each instruction produces a cached layer, and a change invalidates that layer plus every layer after it; ordering stable instructions (dependency installation) before volatile ones (source code) maximizes cache reuse.

### Q5 (Mid/Senior) — Why did the industry largely move to containers over VMs for scaling backend services?

**What's expected:** Sub-second container startup versus a VM's full-OS-boot startup time makes horizontal scaling faster and cheaper, at the cost of a weaker isolation boundary (shared kernel) judged an acceptable trade-off for most application workloads.

---

## CI/CD Pipeline Design and Deployment Strategies

### Q1 — Your canary passed with no alerts, but the release still caused a production issue. What's your process gap?

**Canonical treatment:** [§ Interview Questions, Q1](../../14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes tightening the specific alert threshold that missed this incident, even without the broader process-gap framing.
- **Senior:** Correctly identifies the human-review gap in fully-automated canary promotion — a numeric threshold can only catch exactly what it was designed to catch.
- **Staff:** Generalizes "no alert ≠ confirmed healthy" beyond this specific incident, proposing it as a standing principle for any automated gate.

### Q2 — When would blue-green be the wrong choice despite its instant-rollback benefit?

**Canonical treatment:** [§ Interview Questions, Q2](../../14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that blue-green costs more infrastructure, even without the stateful-service nuance.
- **Senior:** Correctly names the double-infrastructure cost (running two full production-sized environments) as the primary limiting factor.
- **Staff:** Adds the stateful-system nuance — blue-green's clean two-environment model works best for stateless or externally-stateful services and gets substantially more complex for anything holding in-process or instance-local state.

---

## Containers & Image Internals

### Q1 — Why is this Docker image so much larger than it needs to be, and how would you fix it?

**Canonical treatment:** [§ Interview Questions, Q1](../../14-devops-containers/container-image-internals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes "use a smaller base image" without diagnosing which layer is actually the problem — the common mistake this question targets, often paired with a `RUN rm -rf` "cleanup" that doesn't actually shrink the image.
- **Senior:** Uses `docker history` as evidence before proposing a fix, and explains multi-stage builds structurally — union filesystem layers are additive, so deleting files in a later layer doesn't remove their bytes from an earlier one.
- **Staff:** Proposes a base-image standard across the team/org to prevent recurrence, and discusses the CVE-surface argument for keeping approved base images small and few.

### Q2 — Walk through what actually happens, at the OS level, when you run `docker run my-image`.

**Canonical treatment:** [§ Interview Questions, Q2](../../14-devops-containers/container-image-internals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes it as "starting a lightweight VM" — factually wrong, the common mistake this question targets.
- **Senior:** Names the specific kernel mechanisms — namespaces (PID, network, mount, UTS, IPC) for isolation and cgroups for limits — and explains they're provided by the host kernel, no second kernel involved.
- **Staff:** Discusses the security implication directly: because containers share the host kernel, a kernel-level vulnerability can be a cross-container or container-to-host escape, structurally impossible with true hardware virtualization.

---

## Kubernetes Objects, Scheduling, and Networking

### Q1 — What actually happens, step by step, when you update a Deployment's container image?

**Canonical treatment:** [§ Interview Questions, Q1](../../14-devops-containers/kubernetes-objects-scheduling-and-networking.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that a rolling update happens gradually, even without the ReplicaSet-level mechanism.
- **Senior:** Correctly explains the Deployment/ReplicaSet/Pod layering — the Deployment controller creates a new ReplicaSet and scales it up while scaling the old one down, respecting `maxSurge`/`maxUnavailable`.
- **Staff:** Notes the readiness-probe dependency explicitly — a new Pod only starts receiving traffic once it passes readiness, connecting rollout safety directly to correct probe configuration.

### Q2 — Why does the scheduler care about `requests` but not `limits` when placing a Pod?

**Canonical treatment:** [§ Interview Questions, Q2](../../14-devops-containers/kubernetes-objects-scheduling-and-networking.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats requests and limits as interchangeable, or assumes the scheduler enforces limits at placement time — the common mistake this question targets.
- **Senior:** Correctly distinguishes what the scheduler reasons about (requests, a scheduling-time guarantee) from what's enforced at runtime (limits, via kubelet CPU throttling or OOMKill).
- **Staff:** Connects this to a specific operational risk — sizing cluster capacity planning around `limits` rather than `requests` can produce a cluster that looks like it has room but doesn't, or vice versa.

---

## Kubernetes Resource Limits, Probes, and JVM Sizing

### Q1 — Your pods are restarting with no application logs at all. What's your first check?

**Canonical treatment:** [§ Interview Questions, Q1](../../14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the absence of application logs rules out a memory problem, rather than being the exact signature of one — the common mistake this question targets.
- **Senior:** Checks the pod's/container's termination reason (`kubectl describe pod` / `OOMKilled` field) for an OOMKill before assuming an application-level crash, correctly distinguishing `OutOfMemoryError` from OOMKilled.
- **Staff:** Proposes standing monitoring directly on the OOMKilled termination reason, not just application-level exceptions, as prevention.

### Q2 — Why would a `startupProbe` matter for a Spring Boot application specifically?

**Canonical treatment:** [§ Interview Questions, Q2](../../14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that Spring Boot can be slow to start, even without connecting it precisely to the probe mechanism.
- **Senior:** Correctly distinguishes readiness from liveness probe purposes and explains that a `startupProbe` suppresses liveness checks until genuinely-slow startup completes.
- **Staff:** Notes the failure mode if misconfigured — a slow-starting pod gets killed and restarted repeatedly, a startup crash-loop that looks like an application bug but is actually a probe-timing misconfiguration.

---

## Docker Compose: Multi-Service Orchestration

### Q1 — `api` depends on `db` via `depends_on: [db]`. On a fresh clone, `api` sometimes fails to connect on startup but always works on retry. Why?

**Canonical treatment:** [§ Interview Questions, Q1](../../14-devops-containers/docker-compose-multi-service-orchestration.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `depends_on` guarantees readiness — the common mistake this question targets.
- **Senior:** Names the specific mechanism: `depends_on`'s plain list form only waits for `db`'s container to start, not for Postgres to finish `initdb` and start accepting connections — a genuinely fresh volume races this.
- **Staff:** Explains why this bug is invisible on a developer's own already-initialized volume but reliably reappears for a genuinely fresh checkout or CI runner.

### Q2 — How does the `api` service know how to reach `db` — does it need the database's IP address?

**Canonical treatment:** [§ Interview Questions, Q2](../../14-devops-containers/docker-compose-multi-service-orchestration.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes an IP address or `localhost` is needed — the common mistake this question targets.
- **Senior:** Correctly states Compose creates a private network per project with real DNS resolution by service name — `api` reaches `db` simply by connecting to the hostname `"db"`.
- **Staff:** Connects this to why hardcoding IPs or `localhost` in multi-service Compose configs is a common, avoidable portability bug.

---

## Horizontal Pod Autoscaling: Mechanics, Metrics, and Scaling Behavior

### Q1 — A team reports their HPA is "too slow" to scale down after a traffic spike ends. Walk me through how you'd investigate.

**Canonical treatment:** [§ Interview Questions, Q1](../../14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Immediately assumes a metrics pipeline bug without checking the stabilization-window explanation first — the common mistake this question targets.
- **Senior:** Checks whether this matches expected stabilization-window behavior (default 300s) — the controller deliberately holds at the highest recent recommendation, not the latest, to protect against premature shrinkage.
- **Staff:** Discusses when shortening the window is actually justified, and what measurement should precede that decision.

### Q2 — What does the HPA's tolerance band actually protect against, and does it apply the same way for scale-up as scale-down?

**Canonical treatment:** [§ Interview Questions, Q2](../../14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Conflates the tolerance band with the scale-up/scale-down asymmetry as a single rule — the common mistake this question targets.
- **Senior:** Correctly states the tolerance band suppresses recommended changes within a default 10% margin symmetrically in both directions — separate from the fact that scale-up applies immediately while scale-down is stabilized.
- **Staff:** Discusses tuning both parameters independently for a given workload's real traffic shape.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | How do rolling, blue-green, and canary deployments each bound release risk? | [CI/CD Pipeline Design and Deployment Strategies](../../14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#flashcards) |
| 2 | What does canary deployment's entire value depend on? | [CI/CD Pipeline Design and Deployment Strategies](../../14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#flashcards) |
| 3 | What's the real cost of blue-green deployment, beyond "it's more complex"? | [CI/CD Pipeline Design and Deployment Strategies](../../14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md#flashcards) |
| 4 | Why does a Deployment manage ReplicaSets rather than Pods directly? | [Kubernetes Objects, Scheduling, and Networking](../../14-devops-containers/kubernetes-objects-scheduling-and-networking.md#flashcards) |
| 5 | What's the functional difference between `resources.requests` and `resources.limits`? | [Kubernetes Objects, Scheduling, and Networking](../../14-devops-containers/kubernetes-objects-scheduling-and-networking.md#flashcards) |
| 6 | Does `maxUnavailable: 0` guarantee performance is unaffected during a rollout? | [Kubernetes Objects, Scheduling, and Networking](../../14-devops-containers/kubernetes-objects-scheduling-and-networking.md#flashcards) |
| 7 | What does JDK 10+ container-aware heap sizing actually read? | [Kubernetes Resource Limits, Probes, and JVM Sizing](../../14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#flashcards) |
| 8 | What's the structural difference between `OutOfMemoryError` and an OOMKill? | [Kubernetes Resource Limits, Probes, and JVM Sizing](../../14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#flashcards) |
| 9 | What's the difference between a readiness probe and a liveness probe? | [Kubernetes Resource Limits, Probes, and JVM Sizing](../../14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#flashcards) |
| 10 | Does `depends_on: [db]` guarantee `db` is ready to accept connections before the dependent service starts? | [Docker Compose: Multi-Service Orchestration](../../14-devops-containers/docker-compose-multi-service-orchestration.md#flashcards) |
| 11 | How do you make Compose wait for a dependency to be genuinely ready, not just started? | [Docker Compose: Multi-Service Orchestration](../../14-devops-containers/docker-compose-multi-service-orchestration.md#flashcards) |
| 12 | How does one service in a `docker-compose.yml` reach another — by IP address? | [Docker Compose: Multi-Service Orchestration](../../14-devops-containers/docker-compose-multi-service-orchestration.md#flashcards) |

---

## Related

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
