---
title: "decomposition-analysis.md Deliverable"
week: 5
last_reviewed: 2026-07-29
---

# `decomposition-analysis.md` Deliverable

**Take a real monolith you know. Propose boundaries. Then argue against your own proposal** — operational cost, latency inflation, transaction fragmentation, on-call burden. Conclude with a genuine recommendation. **The counter-argument is the deliverable** — a decomposition proposal with no counter-argument is not a complete answer to this exercise.

## Table of Contents

1. [Template](#1-template)
2. [Worked example](#2-worked-example)
3. [Exit check](#3-exit-check)

---

## 1. Template

```markdown
# Decomposition Analysis — [System Name]

## Current state
[What the monolith does, roughly, and its current pain points, if any.]

## Proposed boundaries
[Using the consistency-driven test from 01-microservice-decomposition.md §3 --
where does strong transactional consistency NOT cross the proposed line?]

## The case FOR this split
[Independent deployability, independent scaling, clearer ownership --
be specific to THIS system, not generic microservices benefits.]

## The counter-argument -- argue against your own proposal
### Operational cost
[Specific: how many new deployable units, new monitoring/alerting surface,
new on-call rotations or expanded existing ones.]

### Latency inflation
[Specific: which previously in-process calls become network calls, and
roughly what that costs.]

### Transaction fragmentation
[Specific: which previously-atomic operations now require a saga or
become eventually consistent, and what correctness guarantee is lost.]

### On-call burden
[Specific: who gets paged for a failure at the new service boundary,
and whether that team actually has the context to debug it.]

## Genuine recommendation
[Yes, no, or a scoped partial split -- with the specific reasoning
that decided it, not a hedge.]
```

## 2. Worked example

```markdown
# Decomposition Analysis — Order Processing Monolith (illustrative)

## Current state
A single deployable Spring Boot application handles order placement,
inventory reservation, payment processing, and notification. ~15
engineers across 2 teams currently share this one codebase and deploy
pipeline; deploys happen roughly twice a week and require both teams'
changes to be merged and tested together.

## Proposed boundaries
Split payment processing into its own service. Per the consistency
test: payment processing already communicates with an external
provider asynchronously (webhook-based confirmation), meaning it is
ALREADY eventually consistent with the rest of the order flow in
practice -- the proposed service boundary doesn't introduce a new
consistency relaxation, it just makes an existing one explicit.

## The case FOR this split
Payment processing has PCI compliance requirements the rest of the
system doesn't; isolating it simplifies the compliance boundary.
The payment team (5 of the 15 engineers) could deploy independently
of the order/inventory team, who currently must coordinate every
release.

## The counter-argument

### Operational cost
One new deployable service, one new deployment pipeline, and a new
on-call rotation specifically for payments -- realistically 0.5 FTE
of ongoing operational overhead that doesn't exist today.

### Latency inflation
The order-placement path currently makes an in-process method call to
initiate payment; post-split, this becomes a network call. Estimated
added latency: 5-15ms per order, which at current volume is
negligible, but is a real, permanent cost with no corresponding
benefit to the ORDER side of the system.

### Transaction fragmentation
Order and payment status are already loosely coupled today (via the
provider's async webhook), so this specific split does NOT meaningfully
fragment a transaction that was previously atomic -- this is the one
dimension where the counter-argument is genuinely weak, which is worth
stating honestly rather than manufacturing a cost that isn't really there.

### On-call burden
The payment team already has the domain context to debug payment
failures; today they're paged for issues in a codebase that also
contains inventory and notification logic they don't own, adding noise
to their on-call load. Splitting arguably REDUCES their on-call burden
by scoping their pages to code they actually understand.

## Genuine recommendation
Yes, recommend the split -- specifically because three of the four
counter-argument dimensions are weak or favorable in THIS case (the
consistency boundary already exists in practice; on-call burden likely
improves), and the one real cost (added network latency, added
operational surface) is modest and justified by the compliance-boundary
benefit alone. This would NOT be the right call for a system where the
proposed boundary crossed a genuinely atomic, frequently-invoked
transaction -- the recommendation here is specific to this system's
already-async payment integration, not a general endorsement of
splitting payment out of every order system.
```

**Why this is a complete deliverable:** the counter-argument is genuine, not token — it explicitly identifies the one dimension (transaction fragmentation) where the cost argument is actually weak in this specific case, rather than inflating every dimension equally to seem balanced. The final recommendation is tied to the *specific* reasoning, not a generic endorsement.

## 3. Exit check

Your own `decomposition-analysis.md` must include at least one counter-argument dimension that turns out to be a *real, non-trivial cost* in your specific system (not all four manufactured as equally weighty for appearance's sake) — and the final recommendation must follow directly from the balance of the specific arguments made, not be decided in advance and rationalized afterward.
