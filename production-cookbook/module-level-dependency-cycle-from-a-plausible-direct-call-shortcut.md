---
title: "Module-Level Dependency Cycle Introduced Through a Plausible Direct-Call Shortcut"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/architecture/modular-monolith-as-a-deliberate-choice.md
  - ../handbook/system-design/distributed-transactions-saga-and-outbox.md
source: handbook/architecture/modular-monolith-as-a-deliberate-choice.md#production-scenarios
---

# Module-Level Dependency Cycle Introduced Through a Plausible Direct-Call Shortcut

## Context

The `shipping` module legitimately depends on `orders` through `orders.api`, its correct, intended usage. Separately, `orders.internal.OrderCreatedNotifier` was added so the `orders` module could notify `shipping` directly on order creation — a plausible, well-intentioned decision ("just call it directly instead of publishing an event").

## Symptoms

Two modules that were each independently well-structured start becoming difficult to reason about, test, or discuss independently — changes in one unpredictably require changes in the other.

## Impact

The two modules' independent reasonability and testability erode, undermining the specific benefit (independently understandable, independently testable modules) a modular monolith is meant to preserve.

## Initial Hypotheses

None recorded as separately investigated — the cycle was confirmed directly by running an architecture-level cycle-detection check rather than by ruling out other causes of the growing coupling difficulty.

## Evidence

[`CycleCheckDemo`](../../practice/java/architecture/modular-monolith-boundary-enforcement/README.md) reproduces exactly how this happens: `shipping` legitimately depends on `orders` (through `orders.api`); `orders.internal.OrderCreatedNotifier` was added so `orders` could notify `shipping` directly, completing a real cycle. ArchUnit's real slice-cycle check reports it precisely:

```
FAIL
Cycle detected: Slice orders ->
                Slice shipping ->
                Slice orders
```

with the real, specific dependency (constructor parameter, field, method call) responsible for each direction of the cycle.

## Investigation Timeline

1. Increasing difficulty reasoning about, testing, or discussing `orders` and `shipping` independently observed over time, despite each module appearing individually well-structured.
2. `shipping`'s dependency on `orders.api` reviewed and confirmed as the correct, intended direction of dependency.
3. `orders.internal.OrderCreatedNotifier`, added separately to let `orders` notify `shipping` directly on order creation, identified as a second, opposite-direction dependency between the same two modules.
4. ArchUnit's real slice-cycle check run against the compiled classes, reporting the exact cycle (`orders -> shipping -> orders`) along with the specific dependency responsible for each direction.
5. Diagnosis reached that neither individual dependency looks wrong in isolation — the defect exists only at the level of the pair, which is why a single pull-request-at-a-time review missed it.

## Root Cause

Neither individual dependency (`shipping` → `orders.api`, `orders.internal` → `shipping`) looks wrong in isolation — the defect only exists at the level of the *pair*, which is exactly why a human reviewing one pull request at a time is structurally unlikely to catch it without a real, automated cross-module check.

## Immediate Mitigation

Invert the dependency: have `orders` publish a real domain event (`OrderCreated`) that `shipping` subscribes to, rather than `orders` calling `shipping` directly — the same fix this program's event-driven material covers for cross-service cases, applied here at the intra-process module level.

## Permanent Fix

Add the cycle-detection rule to CI alongside the boundary rule, so a future well-intentioned shortcut is caught the same way.

## Alternatives Considered

Keeping the direct call from `orders.internal.OrderCreatedNotifier` to `shipping` and simply documenting the cycle — implicitly rejected, since the fix applied inverts the dependency rather than tolerating a known, undocumented cycle.

## Trade-offs

Event-based inversion trades away the directness (and easier debuggability) of a plain method call for a real decoupling benefit — worth it specifically because it's what keeps the two modules independently reasoned-about and, eventually, independently extractable.

## Prevention

Whenever a module needs its *dependent* to do something (rather than the reverse, needing something from a dependency), reach for an event or callback interface owned by the module being depended on, not a direct call back — the structural shape that avoids the cycle in the first place.

## Monitoring and Alerts

- Add the ArchUnit slice-cycle check as a required CI gate alongside the boundary-violation rule (from the companion package-naming-convention incident), so any future two-module cycle — not just this specific `orders`/`shipping` pair — fails the build automatically.
- Track the module dependency graph's overall shape (a simple metric: number of modules participating in any detected cycle) as a standing architecture-health indicator, since a cycle between any two modules erodes the same independent-reasoning benefit this specific incident demonstrated.
- Flag, at design-review time, any proposed change where a module needs its own dependent to react to something — the exact shape ("orders needs shipping to know about order creation") that led to this cycle — as a case requiring an event or callback interface rather than a direct call, per the Prevention guidance.

## Interview Story

This maps directly to "how do you catch an architectural problem that no single code review would catch" backed by a real, reproduced cross-module cycle. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** two independently well-structured modules gradually became difficult to reason about or test independently, with no single obviously wrong dependency to point to.
- **Task:** find the structural cause, given that each individual dependency looked reasonable on its own.
- **Action:** ran an automated cycle-detection check against the compiled module structure, which reported a real cycle between the two modules and named the specific dependency responsible for each direction.
- **Result:** inverted the newer, problematic dependency into a domain event the dependent module subscribes to instead of a direct call, and added the cycle-detection rule to CI so a future well-intentioned shortcut is caught automatically rather than accumulating unnoticed.

## Staff-Level Discussion

This incident is a sharper illustration of the same underlying lesson as the package-naming-convention violation, but harder to catch by review because the defect is genuinely invisible at single-pull-request granularity — reviewer A approves `shipping`'s correct, intended dependency on `orders.api` months before reviewer B, with no reason to think of the earlier change, approves `orders.internal`'s new direct call to `shipping`, and each review is individually correct given what that reviewer could see. This is precisely the argument for automated, whole-graph architecture checks as a category: some defects are structurally undetectable by any process that only ever looks at one diff at a time, no matter how careful the reviewer, and a Staff engineer should recognize "this class of problem is invisible to per-PR review" as a distinct signal that automated tooling (not more review diligence) is the appropriate response. The chosen fix's trade-off is also worth stating plainly: event-based inversion is less immediately debuggable than a direct method call (a stack trace across an event bus is harder to follow than a direct call chain), and that cost is deliberately accepted because it is the structural shape that makes the two modules' continued independent evolution — and eventual independent extraction, if that's ever needed — actually possible.

## Related Handbook Chapters

- [Modular Monolith as a Deliberate Choice](../handbook/architecture/modular-monolith-as-a-deliberate-choice.md) — canonical module-boundary and cycle-detection mechanics and the `CycleCheckDemo` this incident reproduces.
- [Distributed Transactions, Saga, and Outbox](../handbook/system-design/distributed-transactions-saga-and-outbox.md) — the event-based dependency-inversion pattern applied here at the intra-process module level.
