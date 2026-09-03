---
title: "In-Memory Idempotency Map Breaking Under Horizontal Scaling"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/11-system-design/idempotency.md
source: handbook/system-design/idempotency.md#production-scenarios
---

# In-Memory Idempotency Map Breaking Under Horizontal Scaling

## Context

A checkout service's idempotency check is implemented as an in-memory `ConcurrentHashMap` guarding against duplicate keys. The service is scaled from one instance to three behind a load balancer.

## Symptoms

After the scale-out, customers occasionally report duplicate charges for the same checkout attempt — a regression that did not exist when the service ran as a single instance.

## Impact

Direct financial exposure and refund overhead.

## Initial Hypotheses

- A client-side double-submit — checked and ruled out; request logs show a single client request per affected checkout, retried by the client's own network layer.
- A payment-gateway bug — checked and ruled out.
- The idempotency implementation itself — correct.

## Evidence

Code review shows the idempotency check is implemented as an in-memory `ConcurrentHashMap` guarding against duplicate keys — correct for coordinating threads within one instance, but each of the three horizontally-scaled instances has its own separate map, with no shared state between them.

## Investigation Timeline

1. **Duplicate-charge reports began** shortly after the service's scale-out from one instance to three.
2. **Client and gateway hypotheses ruled out**, confirming a single original request per affected checkout and no vendor-side issue.
3. **Idempotency implementation reviewed directly**, finding an in-memory map rather than any shared, durable store.
4. **Mechanism confirmed**: a retried request load-balanced to a different instance than the original finds no record of the key locally and proceeds to charge again.

## Root Cause

A retried request, load-balanced to a different instance than the original attempt, finds no record of the key in that instance's local map and proceeds to charge again. The in-memory approach only ever solved the single-instance case, and horizontal scaling silently broke the guarantee it appeared to provide.

## Immediate Mitigation

Route all requests for a given idempotency key to the same instance via a temporary sticky-routing rule, while the durable fix is developed.

## Permanent Fix

Replace the in-memory map with a database-unique-constraint mechanism — durable, shared across all instances by construction, since it lives in the database rather than any one process's memory.

## Alternatives Considered

A distributed cache, such as Redis, with a `SETNX`-style atomic check. A viable alternative to a relational unique constraint, but the database-backed approach was chosen here since the payment write itself already required a relational transaction, keeping the idempotency check and the side effect in the same transactional boundary.

## Trade-offs

The database-backed mechanism adds a table and a small amount of write load to every payment attempt. Accepted, since the alternative — an in-memory approach that silently fails under horizontal scaling — is a correctness bug waiting to be triggered by any scaling event.

## Prevention

Any idempotency mechanism proposed in design review should be explicitly checked against horizontal scaling: does this mechanism's state live somewhere shared across all instances, or only within one process's memory?

## Monitoring and Alerts

- A synthetic idempotency test run periodically against the live service, deliberately sending a duplicate request and confirming only one charge occurs — this converts the horizontal-scaling gap from something discovered by customer complaint into something caught by a standing automated check.
- Per-instance idempotency-key hit rate tracked as a metric; a suspiciously low or zero hit rate across a scaled fleet is a direct signal that keys aren't being shared, visible before any actual duplicate charge occurs.

## Interview Story

This maps directly to "make a payment endpoint idempotent, full mechanism" — with the specific trap of an incomplete answer that omits where the state lives. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** duplicate charges began appearing specifically after a service was scaled from one instance to three.
- **Task:** find why an idempotency check that previously worked correctly stopped working.
- **Action:** rule out client and gateway-side causes; review the idempotency implementation directly; identify that its state — an in-memory map — was never shared across instances, only appearing to work when there was exactly one instance to begin with.
- **Result:** replaced the in-memory map with a database-unique-constraint mechanism, durable and shared by construction, and added a synthetic idempotency test to catch any future regression of this kind automatically.

## Staff-Level Discussion

This incident is a clean example of a correctness property that depends entirely on an unstated assumption — "there is only one instance" — which was true when the code was written and reviewed, and silently stopped being true the moment the service scaled out, with no code change to the idempotency logic itself triggering the regression. This is precisely why "make X idempotent" as an interview or design-review question is incomplete without also asking "where does the state that makes it idempotent live, and does that location survive this system's actual deployment topology" — an answer that only works for a single instance is not a complete answer, even if it looks correct in isolation and passes every test written against a single-instance environment. A Staff engineer reviewing any idempotency, deduplication, or rate-limiting mechanism should treat "does this survive horizontal scaling" as a standing, explicit design-review question, not an assumption.

## Related Handbook Chapters

- [Idempotency at System Edges](../syllabus/11-system-design/idempotency.md) — canonical idempotency-key mechanism and horizontal-scaling trap used here.
