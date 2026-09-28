---
title: "Flashcards: Graceful Shutdown and Connection Draining"
slug: graceful-shutdown-and-connection-draining
document_type: flashcard-deck
domain: 14-devops-containers
topic_id: T-2434
canonical: ../syllabus/14-devops-containers/graceful-shutdown-and-connection-draining.md
last_updated: 2026-09-28
---

# Flashcards: Graceful Shutdown and Connection Draining

**Canonical chapter:** [`syllabus/14-devops-containers/graceful-shutdown-and-connection-draining.md`](../syllabus/14-devops-containers/graceful-shutdown-and-connection-draining.md)

## Card: Why readiness flips before the socket closes

**Prompt:**
On `SIGTERM`, why must you flip readiness and keep serving instead of immediately closing the listening socket?

**Answer:**
Because endpoint removal is eventually consistent and happens *concurrently* with the signal. For roughly a second or two, the routing layer is still sending new requests to a pod that has already been told to stop. Closing the socket first means those requests are refused at the TCP level, so the client sees a connection error rather than a retryable response.

**Why it matters:**
This ordering is the single most common cause of a 502 burst on every deploy.

**Common trap:**
Assuming Kubernetes removes the endpoint before sending `SIGTERM`. It does both in parallel.

**Related:**
[Graceful Shutdown and Connection Draining](../syllabus/14-devops-containers/graceful-shutdown-and-connection-draining.md)

## Card: What the client actually sees without a shutdown hook

**Prompt:**
A request is one second into a three-second call when `SIGTERM` arrives and the server has no shutdown handling. What does the client receive?

**Answer:**
Nothing usable. Measured directly: `curl: (52) Empty reply from server` with HTTP status **000** at 1.38 s. Not a 500, not a 503 — no status code at all. With the three-phase hook, the identical request returned **200** at 3.00 s and the process exited 2006 ms after `SIGTERM`.

**Why it matters:**
A client retry policy keyed on status codes has nothing to key on, and a non-idempotent operation is left in an unknown state.

**Common trap:**
Expecting a 5xx. A destroyed connection produces no HTTP response.

**Related:**
[Idempotency](../syllabus/11-system-design/idempotency.md)

## Card: The grace period as a budget

**Prompt:**
What must fit inside `terminationGracePeriodSeconds`, and what happens if it does not?

**Answer:**
Everything: the `preStop` hook, the drain, and pool shutdown. Exceeding it means `SIGKILL` arrives mid-drain — uncatchable — so you are back to dropped requests, only later and more confusingly. Every wait inside a shutdown hook must therefore be bounded, and the timeouts must sum to less than the budget.

**Why it matters:**
An unbounded `awaitTermination` converts a clean shutdown into a kill at the worst moment.

**Common trap:**
Raising the drain timeout without raising the grace period.

**Related:**
[Kubernetes Resource Limits, Probes, and JVM Sizing](../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md)

## Card: Consumers have no load balancer

**Prompt:**
How does graceful shutdown differ for a Kafka consumer?

**Answer:**
There is nothing to remove you from rotation — you remove yourself from the consumer group. Stop polling, finish the records you already hold, **commit offsets**, then `close(timeout)` so the group rebalances promptly instead of waiting for a session timeout. Stopping mid-batch without committing means redelivery, which is safe only if processing is idempotent.

**Why it matters:**
The readiness-probe pattern has no equivalent here, so the shape has to be re-derived rather than recalled.

**Common trap:**
Committing offsets before processing completes.

**Related:**
[Consumer Groups and Rebalancing](../syllabus/09-messaging-event-driven/consumer-groups-and-rebalancing.md)

## Card: Why graceful shutdown is not enough

**Prompt:**
You implemented a perfect shutdown sequence. Why can a payment still be double-charged?

**Answer:**
Because some terminations are never graceful — node failure, `SIGKILL`, a network partition dropping the response after the work committed. The client retries (correct behavior), finds no record of the first attempt, and charges again. Graceful shutdown is a *frequency reduction*; idempotency keys are what make the residual retries harmless.

**Why it matters:**
It is the answer to the follow-up "so are you done?" — no, correctness comes from idempotency.

**Common trap:**
Treating the dropped connection as the defect. The missing idempotency key is the defect.

**Related:**
[Idempotency](../syllabus/11-system-design/idempotency.md)
