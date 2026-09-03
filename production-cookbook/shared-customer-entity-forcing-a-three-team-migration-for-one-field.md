---
title: "Shared Customer Entity Requiring a Three-Team Migration for One New Field"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md
  - ../handbook/architecture/ddd-tactical-design-aggregates.md
source: handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md#production-scenarios
---

# Shared Customer Entity Requiring a Three-Team Migration for One New Field

## Context

Billing, Support, and Marketing services had historically been built directly against one shared `Customer` JPA entity in a shared library, with no context boundary drawn between the three teams' actually-different needs.

## Symptoms

Adding a single new field to the shared `Customer` entity (a loyalty-tier flag, needed only by the Marketing team) required a coordinated deployment across all three services.

## Impact

A change scoped to one team's need (Marketing's loyalty-tier flag) carried a full cross-team regression-testing and coordinated-deployment cost, slowing a small, low-risk-looking change to the speed of the slowest of three independent teams' release cycles.

## Initial Hypotheses

The field addition itself was risky.

## Evidence

The actual risk had nothing to do with the new field — it was that Billing's tax-calculation logic and Support's account-lookup logic were both silently coupled to the exact same class as Marketing's loyalty logic, so *any* team's change to that class required regression-testing all three teams' unrelated use cases.

## Investigation Timeline

1. Coordinated cross-team deployment requirement observed for a single new field addition, prompting the "is the field itself risky" initial hypothesis.
2. Actual coupling investigated: found Billing's tax-calculation logic, Support's account-lookup logic, and Marketing's loyalty logic all directly depend on the same shared `Customer` class.
3. Diagnosis reached that three genuinely different bounded contexts — Billing's tax/payment concerns, Support's ticket/account-status concerns, Marketing's loyalty/campaign concerns — had been collapsed into one Conformist-style shared model with no context boundary drawn at all.
4. Shared entity frozen as an immediate step, requiring explicit sign-off from all three teams for any future change, converting the technical coupling into a visible, explicit process cost while a permanent fix was designed.
5. Each team's actual field-level needs mapped against the shared entity to determine which fields were genuinely shared (customer ID, name) versus context-specific (tax jurisdiction and payment methods; ticket history and account status; loyalty tier and campaign eligibility).

## Root Cause

Three genuinely different bounded contexts had been collapsed into one Conformist-style shared model with no context boundary drawn at all — a naming/organizational convenience (one `Customer` class) that never reflected the actual difference in what each team's domain logic needed from "a customer."

## Immediate Mitigation

Froze the shared entity, requiring sign-off from all three teams for any change, turning the technical coupling into an explicit process cost.

## Permanent Fix

Each team introduced its own bounded-context-local `Customer` representation — a genuine Shared Kernel for the handful of fields all three legitimately need (customer ID, name), with everything else owned locally — with an Anti-Corruption Layer at each boundary translating from the legacy shared entity during the migration window.

## Alternatives Considered

Continuing with the frozen shared entity and permanent cross-team sign-off — implicitly rejected as a long-term solution, since it was adopted only as an immediate mitigation while the actual context-mapping fix was built.

## Trade-offs

Three `Customer` representations now exist instead of one, a real duplication cost, deliberately accepted in exchange for independent deployability.

## Prevention

Any new cross-team shared entity now requires an explicit context-mapping decision (Shared Kernel vs. ACL vs. Conformist) recorded in an ADR before it's built, not discovered after the third team joins.

## Monitoring and Alerts

- Add a dependency/coupling audit (similar in spirit to a static-analysis fitness function) that flags any class or entity depended on directly by more than one team's service, surfacing a shared-model risk before it accumulates to three teams the way this incident did.
- Track cross-team sign-off frequency on the frozen shared entity as a visible cost metric during the migration window, making the coupling's real organizational tax explicit rather than absorbed silently into each team's velocity.
- Once the Anti-Corruption Layer is in place, track its translation error or mismatch rate, since it is the boundary most likely to surface a subtle divergence between the legacy shared model and each team's new bounded-context-local representation during the migration.

## Interview Story

This maps directly to "two teams disagree about what 'Order' (or 'Customer') means" arriving as a real, production coupling cost. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** adding one new field needed only by Marketing to a shared `Customer` entity required a coordinated deployment across Billing, Support, and Marketing.
- **Task:** determine whether the risk was in the new field or somewhere else.
- **Action:** traced the actual coupling to three teams' unrelated domain logic all depending directly on the same shared class, diagnosed it as three genuinely different bounded contexts collapsed into one Conformist-style model, and froze the entity as an immediate mitigation while designing a real fix.
- **Result:** introduced bounded-context-local `Customer` representations per team, a genuine Shared Kernel for the truly common fields, and an Anti-Corruption Layer during migration — accepting real data duplication in exchange for each team regaining independent deployability.

## Staff-Level Discussion

The organizational failure here predates the specific field-addition incident: a shared entity is easy to create early, when three teams' needs genuinely do overlap heavily, and the coupling cost only becomes visible once the teams' domain logic has diverged enough that "any change requires regression-testing all three" stops being a hypothetical and starts being every single pull request's actual cost. A Staff engineer's contribution here is recognizing that the fix isn't "communicate better" or "review more carefully" — it's drawing an explicit context boundary and accepting the real, measurable cost of duplication (three `Customer` representations instead of one) as the price of independent deployability, which is a trade a team without DDD vocabulary might not even recognize as available. The prevention step — requiring an explicit context-mapping decision recorded in an ADR before any new cross-team shared entity is built — is the durable fix, because it forces the "is this genuinely one context or three" question to be asked once, deliberately, rather than discovered reactively after the third team's needs have already diverged past the point of an easy split.

## Related Handbook Chapters

- [DDD Strategic Design: Bounded Contexts and Context Mapping](../handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md) — canonical context-mapping patterns (Shared Kernel, Anti-Corruption Layer, Conformist) this incident's resolution applies.
- [DDD Tactical Design: Aggregates](../handbook/architecture/ddd-tactical-design-aggregates.md) — the aggregate-boundary discipline each team's new bounded-context-local `Customer` representation must respect.
