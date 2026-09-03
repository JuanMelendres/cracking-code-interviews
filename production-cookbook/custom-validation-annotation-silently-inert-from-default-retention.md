---
title: "Custom Validation Annotation Silently Inert from Default Retention"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/java-core/annotations-and-annotation-processing.md
source: handbook/java-core/annotations-and-annotation-processing.md#production-scenarios
---

# Custom Validation Annotation Silently Inert from Default Retention

## Context

A team writes a custom `@ValidEmail` annotation, applies it to several DTO fields, and wires up reflection-based validation logic that scans for the annotation at request-handling time.

## Symptoms

In production, invalid emails are never rejected — the validation silently never fires, with no error, no exception, nothing in the logs.

## Impact

A silent validation gap lets invalid data through: a real data-quality/correctness bug with zero visible symptom pointing at the actual cause.

## Initial Hypotheses

- A bug in the validation logic itself — checked, and ruled out: the reflection-scanning code is correct and works in a standalone test.
- The annotation isn't actually applied to the right fields — checked, and ruled out: it clearly is, in the source code.
- The annotation's retention policy is wrong — correct.

## Evidence

`@ValidEmail` was declared with the default retention (`CLASS`, since no `@Retention` was specified at all). Direct proof matches the class's `getAnnotations()` behavior at runtime: it returns nothing for this annotation, exactly the observed silent failure.

## Investigation Timeline

1. **Production reports arrive** showing invalid email addresses stored despite `@ValidEmail` being present on the corresponding DTO fields.
2. **Validation logic reviewed in isolation** — a standalone test against the reflection-scanning code confirms it correctly detects and rejects invalid values when the annotation is visible to it.
3. **Annotation placement confirmed** in source — `@ValidEmail` is applied to exactly the fields expected, ruling out a misapplied-annotation mistake.
4. **Runtime annotation visibility checked directly** — calling `getAnnotations()` on the target field at the point the validation logic runs returns an empty array, despite the annotation being present in source.
5. **Retention policy inspected** — `@ValidEmail`'s declaration carries no explicit `@Retention`, defaulting to `CLASS`, which is retained in the compiled bytecode's `RuntimeInvisibleAnnotations` attribute but never surfaced by reflection's `getAnnotations()`/`getAnnotation()`.

## Root Cause

The default retention policy (`CLASS`), which applies whenever `@Retention` is not specified, is invisible to runtime reflection. The validation code was structurally correct; the annotation itself was never going to be visible to it, because it never reached the `RuntimeVisibleAnnotations` attribute reflection actually reads.

## Immediate Mitigation

Add `@Retention(RetentionPolicy.RUNTIME)` to `@ValidEmail` and redeploy, immediately restoring the intended validation behavior.

## Permanent Fix

Add a project-wide checklist item (or a compile-time check, where feasible) that every custom annotation intended for runtime reflection explicitly declares `@Retention(RetentionPolicy.RUNTIME)` — never relying on the (invisible-to-reflection) default.

## Alternatives Considered

None seriously — this is a straightforward fix once correctly diagnosed; the only "alternative" was continuing to silently ship the bug.

## Trade-offs

None. `RUNTIME` retention has negligible cost versus the default, and is required for the annotation to serve its actual intended purpose at all.

## Prevention

Any custom annotation meant to be read by reflection-based framework code should be reviewed specifically for an explicit `RUNTIME` retention declaration — a missing `@Retention` declaration produces exactly this kind of bug, with zero compiler warning.

## Monitoring and Alerts

- Add a startup-time self-check (a small reflective probe run once at application boot) that asserts every annotation type registered with the validation framework actually reports `RetentionPolicy.RUNTIME` — failing fast at deploy time rather than allowing a silently inert validator into production.
- Track a validation-rejection-rate metric per validated field/DTO; a validator that goes from rejecting some fraction of traffic to rejecting exactly zero, with no corresponding change in input quality, is a strong signal that the validator itself has gone silently inert rather than that input quality genuinely improved.
- Add a unit test asserting `getAnnotations()`/`getAnnotation()` actually returns the expected custom annotation for every field the validation framework is supposed to cover, run in CI so retention regressions are caught before merge, not after deployment.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a reflection-based email validator that appeared correctly implemented and correctly applied was letting invalid emails through in production with no error anywhere.
- **Task:** find the cause with no exception, no log line, and a validation implementation that tested correctly in isolation.
- **Action:** ruled out a validation-logic bug and a misapplied-annotation mistake, then checked the annotation's visibility to reflection directly and found the default `CLASS` retention was silently hiding it from `getAnnotations()`.
- **Result:** added the explicit `RUNTIME` retention, restoring validation immediately, and added a project-wide review checklist item (and a CI-run reflective assertion) to prevent every future instance of the same silent gap.

## Staff-Level Discussion

This bug class is expensive precisely because nothing in the ordinary developer workflow surfaces it: the annotation compiles cleanly, `javac` emits no warning for an annotation with no explicit `@Retention`, and the validation code itself is entirely correct in isolation. The defect lives entirely in the interaction between two pieces of code that never appear together in the same file — the annotation's declaration and the framework code that reflects on it — which is exactly the kind of gap code review by inspection tends to miss. A Staff engineer's response should treat this as a systemic risk in any codebase building its own reflection-based framework conventions (validation, mapping, DI-lite): the fix isn't just "add RUNTIME here," it's establishing an automated, CI-enforced invariant — every custom annotation intended for framework use is reflectively verified to be `RUNTIME`-retained — so the entire bug class becomes a build-time failure rather than a production data-quality discovery. This also has an organizational angle: teams that hand-roll annotation-based frameworks (rather than relying on well-tested library equivalents like Bean Validation) take on the ongoing cost of re-discovering gotchas like this one, and should weigh that against the convenience of the custom mechanism.

## Related Handbook Chapters

- [Annotations and Annotation Processing](../handbook/java-core/annotations-and-annotation-processing.md) — canonical retention-policy mechanics and the reflection-based mini-ORM demo this incident traces back to.
- [Reflection and Dynamic Proxies](../handbook/java-core/reflection-and-dynamic-proxies.md) — the reflective mechanism (`getAnnotations()`, `Field.getAnnotation()`) that this incident's validator depends on being able to see.
