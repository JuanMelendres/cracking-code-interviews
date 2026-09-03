---
title: "Module Boundary Violation That a Naming Convention Alone Couldn't Stop"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/17-architecture/modular-monolith-as-a-deliberate-choice.md
  - ../syllabus/17-architecture/clean-hexagonal-architecture.md
source: handbook/architecture/modular-monolith-as-a-deliberate-choice.md#production-scenarios
---

# Module Boundary Violation That a Naming Convention Alone Couldn't Stop

## Context

A modular monolith relies on an `internal` package-naming convention to signal which classes are private to a module — `shippinglegacy.LegacyShippingService` and `orders.internal.PricingEngine` both exist as ordinary `public` classes, with no build-level enforcement of the "internal" naming's intended meaning.

## Symptoms

A code review misses a new class that imports another module's `internal` package directly — nothing in the build failed, because nothing was checking.

## Impact

`LegacyShippingService` compiles and runs successfully while directly depending on `orders.internal.PricingEngine`, an implementation detail the `orders` module never intended to expose — a real, silent boundary violation with no error anywhere to surface it.

## Initial Hypotheses

None stated as separately investigated — the violation was confirmed directly by running an architecture test against the actual compiled classes rather than by ruling out alternative explanations.

## Evidence

[`BoundaryCheckDemo`](../../practice/java/architecture/modular-monolith-boundary-enforcement/README.md) demonstrates this precisely: `shippinglegacy.LegacyShippingService` directly imports and calls `orders.internal.PricingEngine`, compiling and running without any error, because every class involved is `public` and Java itself enforces nothing about the word "internal" in a package name. A real ArchUnit rule checked against the actual compiled classes catches it directly:

```
FAIL
Architecture Violation [Priority: MEDIUM] - Rule '...' was violated (3 times):
Constructor <shippinglegacy.LegacyShippingService.<init>()> calls constructor <orders.internal.PricingEngine.<init>()> ...
Field <shippinglegacy.LegacyShippingService.pricingEngine> has type <orders.internal.PricingEngine> ...
Method <shippinglegacy.LegacyShippingService.quoteShippingCost(...)> calls method <orders.internal.PricingEngine.computeInternalPrice(...)> ...
```

The identical rule checked against the clean `shipping` module, in the same run, real-passes — direct, side-by-side proof the mechanism discriminates correctly rather than failing everything.

## Investigation Timeline

1. `LegacyShippingService` found to depend directly on `orders.internal.PricingEngine`, with the build having accepted this without any error.
2. Confirmed via `BoundaryCheckDemo` that nothing about Java's own compilation or runtime enforces the "internal" naming convention's intended meaning — both classes are `public` and freely accessible.
3. A real ArchUnit rule run against the actual compiled classes, correctly failing with three specific violation instances (a constructor call, a field type, a method call) all naming the exact offending dependency.
4. The identical rule run against the clean `shipping` module in the same test session, passing — confirming the check discriminates correctly between violating and non-violating code rather than failing indiscriminately.

## Root Cause

A naming convention communicated intent to a human reviewer who missed it; nothing communicated intent to the build.

## Immediate Mitigation

Fix `LegacyShippingService` to depend on `orders.api.OrderLookup` instead, exactly as the correctly-structured `ShippingService` already does.

## Permanent Fix

Add the real architecture test to CI, so this exact class of defect fails the build automatically going forward, rather than depending on review vigilance holding indefinitely.

## Alternatives Considered

None recorded beyond adding the automated architecture test — the scenario treats build-level enforcement as the direct, necessary fix rather than reinforcing the naming convention alone.

## Trade-offs

Architecture tests need real, ongoing maintenance as legitimate new cross-module dependencies are added — a rule too rigid to evolve becomes an obstacle developers route around rather than respect.

## Prevention

Treat "does this change respect existing module boundaries" as a build-time, automated question from the very first module boundary drawn, not something introduced only after the first real violation is discovered in production.

## Monitoring and Alerts

- Add the ArchUnit boundary rule as a required CI check on every pull request touching any module, so a future violation fails the build immediately rather than depending on a reviewer noticing an `internal` package import.
- Track the count of ArchUnit violations caught in CI over time as a health metric for the modular monolith's boundary discipline; a rising trend indicates the convention is under pressure and may need either stronger tooling or a genuine architectural discussion about a boundary that no longer fits how the code wants to be organized.
- When the architecture test rule itself is modified (per the Trade-offs note on rules needing to evolve), require the same review rigor as a change to the boundaries themselves — a loosened rule is functionally equivalent to removing enforcement for whatever it now permits.

## Interview Story

This maps directly to "how do you actually enforce module boundaries in a monolith" backed by a real, reproduced violation and fix. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a new class was found directly importing another module's `internal` package, something a code review had missed entirely.
- **Task:** determine why the naming convention alone hadn't prevented this, and fix both the specific violation and the systemic gap.
- **Action:** confirmed Java itself enforces nothing about the "internal" naming convention, then ran a real ArchUnit rule against the compiled classes, which caught the exact violation with the specific offending dependency named, while passing cleanly against a correctly-structured sibling module.
- **Result:** fixed the specific class to depend on the module's public API instead, and added the architecture test to CI so the same defect class fails the build automatically in the future.

## Staff-Level Discussion

The structural lesson here generalizes well beyond this one incident: any convention that relies purely on a human noticing it during review will eventually fail, not because reviewers are careless, but because a package name conveying "don't depend on this" carries no compiler-enforced weight, and a reviewer scanning a diff for correctness bugs has no particular reason to also verify architectural intent unless something makes that check unavoidable. A Staff engineer introducing a modular-monolith boundary should treat the naming convention as documentation of intent, not enforcement of it, and budget for the architecture-test tooling as part of the boundary's actual cost from day one — the side-by-side pass/fail proof (the same rule correctly passing the clean module and failing the violating one) is exactly the kind of evidence that should accompany the tooling's introduction, since it demonstrates the mechanism discriminates correctly rather than being an indiscriminate, easily-dismissed blocker.

## Related Handbook Chapters

- [Modular Monolith as a Deliberate Choice](../syllabus/17-architecture/modular-monolith-as-a-deliberate-choice.md) — canonical module-boundary-enforcement mechanics and the `BoundaryCheckDemo` this incident reproduces.
- [Clean and Hexagonal Architecture](../syllabus/17-architecture/clean-hexagonal-architecture.md) — the broader boundary-drawing discipline (public API vs. internal implementation) this convention was meant to express.
