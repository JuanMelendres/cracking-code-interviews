---
title: "Validation Framework Silently Skips Rules from Constructor Dispatch"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/java-core/polymorphism-and-dynamic-dispatch.md
source: handbook/java-core/polymorphism-and-dynamic-dispatch.md#production-scenarios
---

# Validation Framework Silently Skips Rules from Constructor Dispatch

## Context

A shared `AbstractValidator` base class's constructor calls a `getRules()` method that subclasses override to supply their specific validation rules. A refactor consolidates several subclasses to build their rule list via a field initializer (`private final List<Rule> rules = buildRules();`) instead of a constructor body.

## Symptoms

After the refactor, validation silently stops enforcing any rules for every affected subclass — no exception, records that should be rejected are accepted.

## Impact

Invalid records pass validation and propagate downstream, discovered only when a data-quality audit flags records that should have been rejected at the point of entry.

## Initial Hypotheses

- `buildRules()` itself has a logic bug — checked, and ruled out: calling it directly, after construction, returns the correct rule list.
- The validation-invocation code has a bug — checked, and ruled out: it correctly calls `getRules()` and iterates the result.
- The base constructor's call to `getRules()` runs before the subclass field is initialized — correct.

## Evidence

Adding a log line inside `getRules()` shows it returns an empty list the one time it's called from `AbstractValidator`'s constructor — but returns the correct, populated list when called again afterward.

## Investigation Timeline

1. **Data-quality audit flags records** that should have been rejected by existing validation rules, with no exception ever raised anywhere in the validation path.
2. **`buildRules()` tested in isolation**, called directly after full construction, and confirmed to return the correct, populated rule list.
3. **Validation-invocation code reviewed** and confirmed to correctly call `getRules()` and iterate whatever it returns — the bug is not in how the result is consumed.
4. **Diagnostic logging added inside `getRules()`** itself, revealing it returns an empty list specifically on the one call made from `AbstractValidator`'s own constructor, but the correct list on every subsequent call.
5. **Construction-order mechanism confirmed** — the base class's constructor invokes the overridden `getRules()` before the subclass's own field initializer (`buildRules()`) has run, since field initializers execute only after `super()` returns.

## Root Cause

The base class's constructor calls an overridable method whose result depends on subclass state that isn't initialized yet at that point in construction. The refactor moved rule construction from an explicit constructor body (which happened to run after the base constructor completed in the old design) to a field initializer (which runs immediately after `super()` returns but is still observed, by the base constructor's earlier call, at its default `null` value).

## Immediate Mitigation

Revert the affected subclasses to building their rule list in an explicit constructor, after calling `super()`, restoring the previous (accidentally correct) ordering.

## Permanent Fix

Remove the base constructor's call to an overridable method entirely; replace it with either a two-phase pattern (construct, then call an explicit `init()` method after full construction completes) or a constructor parameter (`AbstractValidator(List<Rule> rules)`, with each subclass passing its own already-built list via `super(buildRules())`, sidestepping the ordering problem since `buildRules()` here doesn't depend on instance state).

## Alternatives Considered

Making `getRules()` `final` in the base class and having each subclass instead override a protected field-setting hook — rejected as adding indirection without removing the underlying "base constructor observes not-yet-initialized subclass state" risk if that hook itself depended on subclass fields.

## Trade-offs

The constructor-parameter fix requires every subclass's rule-building logic to not depend on `this` (since it runs before `this` is fully an instance of the subclass) — acceptable here since rule lists were already static, data-only structures.

## Prevention

Treat any base-class constructor calling a non-`final`, non-`private` method as a design-review flag by default, and prefer passing pre-built state through the constructor parameter list over calling back into overridable methods during construction.

## Monitoring and Alerts

- Add a startup or first-request self-check per validator subclass, asserting `getRules()` returns a non-empty list (where a non-empty list is expected) immediately after full construction, failing fast rather than allowing a silently rule-less validator to serve traffic.
- Track a validation-rejection-rate metric per validator type; a subclass whose rejection rate drops to exactly zero with no corresponding change in input quality is a strong signal of a silently empty rule set, matching this exact failure mode.
- Add a static-analysis or code-review lint flagging any non-`final`, non-`private` method called from within a constructor in a class with subclasses, since this is the general shape of the bug independent of the specific `AbstractValidator` case.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a shared base-class validation framework silently stopped enforcing any rules for several subclasses after what looked like a purely cosmetic refactor moved rule-building from a constructor body to a field initializer.
- **Task:** find a bug with zero exceptions and zero obviously wrong output, discovered only via a downstream data-quality audit.
- **Action:** verified the rule-building logic and the validation-invocation code were both individually correct, then added targeted logging inside the rule-supplying method and found it returned an empty list only on the base constructor's own call — pointing directly at construction-order dependent dispatch.
- **Result:** restored correct behavior immediately by reverting to constructor-body rule building, then permanently removed the base constructor's dependency on overridable subclass behavior via a constructor-parameter redesign.

## Staff-Level Discussion

This bug class is a direct consequence of Java's dynamic dispatch being active even during construction — a base constructor's call to an overridable method genuinely runs the subclass's override, but that override can observe subclass fields still at their default value, since field initializers run after `super()` returns rather than before it starts. What makes this dangerous in practice is that the "before" state (rule-building in an explicit constructor body) and the "after" state (rule-building in a field initializer) look like a purely stylistic refactor to anyone who doesn't already know this ordering rule cold — there's nothing in the diff that visually signals a behavior change. A Staff engineer reviewing or designing a base class with this shape should treat "does my constructor call an overridable method" as a standing design-review question independent of any specific incident, because the answer to "will this break" depends entirely on what every future subclass author chooses to do, not on anything in the base class's own code. The more durable fix generalizes beyond this one incident: any framework-style base class should prefer passing fully-built state through constructor parameters over calling back into overridable hooks during construction, since the former makes the dependency direction explicit and the latter makes it an implicit contract that's easy to violate silently.

## Related Handbook Chapters

- [Polymorphism and Dynamic Dispatch Mechanics](../handbook/java-core/polymorphism-and-dynamic-dispatch.md) — canonical mechanics of `invokevirtual` during construction and the reproduced uninitialized-subclass-state pitfall.
- [Records, Sealed Types, and Pattern Matching](../handbook/java-core/records-sealed-types-and-pattern-matching.md) — related design alternative for closed, data-carrying hierarchies that avoids overridable-method dispatch entirely.
