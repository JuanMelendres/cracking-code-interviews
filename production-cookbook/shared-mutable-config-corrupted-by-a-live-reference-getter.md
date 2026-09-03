---
title: "Shared Mutable Config Corrupted by a Live-Reference Getter"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/language-core/immutability-and-defensive-copying.md
source: handbook/java-core/immutability-and-defensive-copying.md#production-scenarios
---

# Shared Mutable Config Corrupted by a Live-Reference Getter

## Context

A service loads a `Config` object once at startup and passes it by reference to multiple independent subsystems. `Config` exposes its underlying `Map<String, Boolean>` of feature flags directly via a getter, with no defensive copy.

## Symptoms

Weeks after a refactor, one subsystem begins behaving as if a feature flag changed mid-request, even though the flag was never intentionally toggled. Other subsystems using the same `Config` instance intermittently see the same unexpected flip.

## Impact

A shared, supposedly-static configuration silently changes at runtime, producing inconsistent behavior across subsystems that all assumed the config was immutable for the life of the process.

## Initial Hypotheses

- A race condition in flag evaluation logic — checked and ruled out; the flag read itself is a simple field access, no concurrency bug there.
- An external config-reload mechanism firing unexpectedly — checked and ruled out; no reload mechanism exists in this service.
- One subsystem is mutating the shared `Config` object directly, since it was handed a live reference rather than a copy — correct.

## Evidence

The `Config` class exposes its underlying `Map<String, Boolean>` of feature flags directly via a getter with no defensive copy, and a recently added subsystem calls `config.getFlags().put(...)` internally as a local override mechanism — not realizing that the `Map` is the exact same shared instance every other subsystem also holds.

## Investigation Timeline

1. **Inconsistent behavior reported** across multiple subsystems, all tracing to the same feature flag appearing to flip unexpectedly.
2. **Concurrency and reload-mechanism hypotheses ruled out**, neither of which is present in this service at all.
3. **Getter inspected directly**, revealing it returns the live, mutable `Map` rather than a copy or immutable view.
4. **Mutating call site found**: a recently added subsystem calling `.put()` on the returned map as a local override, unaware the reference is shared.

## Root Cause

A getter returning a live reference to mutable internal state, allowing one caller's local mutation to silently become global mutation, affecting every other holder of the same reference.

## Immediate Mitigation

Have the offending subsystem stop mutating the shared map directly, using a local copy for its override logic instead.

## Permanent Fix

Change `Config.getFlags()` to return an immutable view (`Map.copyOf(flags)`), so any future attempt to mutate it fails loudly with `UnsupportedOperationException` at the point of the mistake, rather than silently corrupting shared state for every other holder.

## Alternatives Considered

Documenting "do not mutate this map" as a comment on the getter. Rejected — the whole premise of this failure mode is that such conventions are silently violated exactly when someone doesn't realize the object is shared; only a structural guarantee actually prevents the bug.

## Trade-offs

None meaningful — `Map.copyOf()` costs a one-time copy at construction and returns an already-immutable view thereafter, with no ongoing cost difference from the mutable version for read-only callers.

## Prevention

Any object shared across multiple independent subsystems, especially configuration or reference data intended to be read-only, should expose its collection-typed fields via immutable views by default, not by convention.

## Monitoring and Alerts

- No runtime metric directly detects this class of bug before it manifests as inconsistent behavior — the real leverage is static, not observability-based: a lint rule or code-review check flagging any getter that returns a mutable collection field without `Collections.unmodifiableX()` or `X.copyOf()`.
- Once suspected, a targeted log line at every mutation call site into a shared configuration object (temporary, for diagnosis) is a faster path to the offending call site than tracing symptom reports across subsystems.

## Interview Story

This maps to a "shared state silently corrupted, no concurrency involved" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a feature flag appeared to flip unexpectedly across multiple unrelated subsystems, with no concurrency or config-reload mechanism in play.
- **Task:** find the mutation source without a race condition to chase.
- **Action:** rule out concurrency and reload mechanisms directly; inspect the getter's return type and realize it hands out the live internal map; trace the actual mutating call site in a recently added subsystem.
- **Result:** switched the getter to return `Map.copyOf(flags)`, converting a silent, hard-to-trace corruption into a loud `UnsupportedOperationException` at the exact point of the mistake.

## Staff-Level Discussion

This bug class is worth taking seriously precisely because it has no concurrency signature at all — no race, no timing dependency, nothing a stress test would surface — which makes it easy to dismiss "shared mutable getters" as a low-priority style nit rather than a real production risk. The fix itself is nearly free (`Map.copyOf()` instead of a raw getter), which is exactly the argument for making it a structural default rather than a per-review judgment call: any object shared across module or subsystem boundaries should expose collection-typed state immutably by construction, and a lint rule enforcing that catches this entire class before it ships, at effectively zero ongoing cost.

## Related Handbook Chapters

- [Immutability and Defensive Copying](../syllabus/02-java/language-core/immutability-and-defensive-copying.md) — canonical live-reference-leak mechanics and defensive-copy patterns used here.
