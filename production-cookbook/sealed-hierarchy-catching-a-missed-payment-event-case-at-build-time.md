---
title: "Sealed Hierarchy Catching a Missed Payment-Event Case at Build Time"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/java-core/records-sealed-types-and-pattern-matching.md
source: handbook/java-core/records-sealed-types-and-pattern-matching.md#production-scenarios
---

# Sealed Hierarchy Catching a Missed Payment-Event Case at Build Time

## Context

A team models `PaymentEvent` as `sealed interface PaymentEvent permits Authorized, Captured, Refunded, Failed`, each a record. Every `switch` over `PaymentEvent` across the codebase — a reconciliation job, an audit log writer, a customer notification service — relies on this sealed hierarchy for exhaustiveness checking. An engineer unfamiliar with the original design later adds a new `PartiallyRefunded` event type to the `permits` clause.

## Symptoms

The moment `PartiallyRefunded` is added to the `permits` clause, every `switch` over `PaymentEvent` across the codebase fails to compile.

## Impact

The build is blocked until the author explicitly decides what each of the three consumers — reconciliation, audit logging, customer notification — does with the new case, rather than the addition merging cleanly while some consumers silently ignore the new event type until a support ticket surfaces the gap.

## Initial Hypotheses

- The build failure is an unrelated compiler or tooling issue — checked, and ruled out: the failures are all genuine, consistent exhaustiveness errors pointing at every `switch` over `PaymentEvent`.
- The new event type was added incorrectly (a syntax error) — checked, and ruled out: the `permits` clause addition and the new record itself are both syntactically and semantically valid.
- The sealed hierarchy's exhaustiveness checking is working exactly as designed, correctly flagging every consumer that doesn't yet handle the new case — correct.

## Evidence

Each failing `switch` reports the same category of compiler error — the switch expression does not cover all possible input values — naming `PartiallyRefunded` specifically as the unhandled case, directly traceable to its addition in the `permits` clause.

## Investigation Timeline

1. **`PartiallyRefunded` added to the sealed `PaymentEvent` hierarchy's `permits` clause**, as part of a change to model a new payment scenario.
2. **Build immediately fails** across every module containing a `switch` over `PaymentEvent` — reconciliation, audit log writer, and customer notification service all report the identical exhaustiveness error.
3. **Failures confirmed as genuine compiler diagnostics**, not a tooling artifact — a clean rebuild reproduces every failure identically, each one naming the newly-added case.
4. **New record's own definition reviewed** and confirmed syntactically and semantically correct — the failures are not caused by a mistake in `PartiallyRefunded`'s own declaration.
5. **Mechanism confirmed as intended sealed-hierarchy behavior**: the compiler enumerates every possible `PaymentEvent` value from the `permits` clause and requires every switch to account for all of them, which is precisely why adding a new permitted type forces every consumer to be updated before the build can succeed.

## Root Cause

Not a defect — this is the sealed hierarchy's exhaustiveness guarantee working exactly as designed. Because `PaymentEvent` is `sealed` with an explicit `permits` clause, the compiler can enumerate every possible value and requires every `switch` over it to handle all of them; adding a new permitted subtype makes every switch that previously covered "all cases" now provably incomplete, converting the addition into a compile-time forcing function rather than a change that could merge silently.

## Immediate Mitigation

None required in the incident-response sense — the correct immediate action is exactly what the compiler is forcing: explicitly update each of the three failing consumers (reconciliation, audit log writer, customer notification service) to decide what happens for `PartiallyRefunded`, before the build can succeed at all.

## Permanent Fix

No further remediation needed beyond updating the three consumers — the sealed hierarchy itself is functioning as the permanent safeguard against this exact class of omission recurring in the future.

## Alternatives Considered

Modeling `PaymentEvent` as a plain interface or an `enum` tag with a `default: log.warn("unknown event")` branch instead of `sealed` — this is explicitly the counterfactual the design avoids: under that design, the identical omission would compile cleanly and fail silently (or fall into a `default` branch nobody watches) rather than blocking the build and forcing an explicit decision.

## Trade-offs

The sealed design imposes a real cost at exactly this moment: every consumer of `PaymentEvent` must be touched (or at minimum reviewed and deliberately given a fallback) whenever the hierarchy grows, rather than the change being isolable to one file. This is accepted as the direct trade for compiler-enforced completeness — the cost is paid once, at the point of the change, rather than deferred to an unbounded number of future support tickets from silently-unhandled cases.

## Prevention

Model any closed, fixed set of variants (event types, states, message kinds) as a `sealed` hierarchy with exhaustive `switch` consumers rather than a plain interface or `enum` tag with a `default` branch — this is precisely the pattern that converts a future omission into a build failure instead of a silent gap.

## Monitoring and Alerts

- No runtime monitoring is the right answer here — the value of this design is that the failure mode moves from "runtime/production" to "build time," so the correct target for observability is CI failure attribution: ensure the CI system surfaces sealed-exhaustiveness compiler errors clearly and attributes them to the specific commit/PR that added the new permitted type, so the author gets fast, unambiguous feedback.
- For any codebase that historically modeled similar concepts with a plain interface or enum-plus-`default`, consider a one-time audit for `default: log.warn(...)`-style branches over a closed, enumerable concept — each one is a candidate for migration to a sealed hierarchy, converting a latent silent-gap risk into the same compile-time guarantee this scenario demonstrates.
- Track, as a team-health metric, how often a new variant added to a modeled concept required touching N consumers versus how often a similar addition under a non-sealed design was later found to have silently skipped a consumer — this comparison is useful evidence when advocating for sealed-hierarchy adoption elsewhere in the codebase.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a team's sealed `PaymentEvent` hierarchy suddenly broke every switch statement over payment events across three unrelated modules the moment a new event type was added.
- **Task:** determine whether the sudden, widespread build failure was a bug to fix or a signal to act on, under time pressure from a blocked merge.
- **Action:** confirmed the failures were genuine, consistent exhaustiveness diagnostics rather than a tooling issue, verified the new event type itself was correctly defined, and recognized the compiler was correctly forcing an explicit decision in every consumer rather than reporting an actual defect.
- **Result:** updated each of the three consumers to explicitly handle the new case, merged the change with full confidence that no consumer had been silently left behind, and used the experience to argue for sealed hierarchies over plain interfaces for other closed, enumerable concepts in the codebase.

## Staff-Level Discussion

This scenario is valuable precisely because it inverts the usual production-cookbook shape: the "incident" is a design choice successfully doing its job, and the interesting judgment call is recognizing that a sudden, widespread build failure is not itself evidence of a problem to be reverted or worked around. A less experienced engineer's first instinct when three unrelated modules suddenly fail to compile is often to suspect the change itself, or to reach for a quick `default` branch to make the errors go away — which would silently reintroduce exactly the gap sealed types exist to prevent. The Staff-level insight is recognizing sealed types (and exhaustive pattern matching more broadly) as a deliberate transfer of risk from an unbounded number of future production incidents to a single, bounded, immediate build-time cost — and that this transfer is a genuinely good trade for any concept whose full set of variants is fixed and known, especially one (like payment events) where a silently-unhandled case has real business consequences. At an organizational level, this argues for treating "can this concept's variants be modeled as a closed, sealed set" as a standing architectural question during design review, not something considered only in hindsight after a silent-gap incident has already occurred under the older, non-sealed design.

## Related Handbook Chapters

- [Records, Sealed Types, and Pattern Matching](../handbook/java-core/records-sealed-types-and-pattern-matching.md) — canonical mechanics of sealed hierarchies, exhaustiveness checking, and the reproduced compile error.
- [Polymorphism and Dynamic Dispatch Mechanics](../handbook/java-core/polymorphism-and-dynamic-dispatch.md) — related mechanics for how a codebase might otherwise model a closed set of behaviors via overriding rather than sealed pattern matching.
