---
title: "Eroded Hexagonal Boundary Blowing Up a Migration Estimate"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/architecture/clean-hexagonal-architecture.md
source: handbook/architecture/clean-hexagonal-architecture.md#production-scenarios
---

# Eroded Hexagonal Boundary Blowing Up a Migration Estimate

## Context

A service follows a `domain/`, `application/`, `infrastructure/` folder structure intended to implement hexagonal architecture, but has never had an automated check verifying the actual dependency direction between those layers.

## Symptoms

A team estimates two weeks to migrate the service's persistence layer from Hibernate to a lighter-weight JDBC-based mapper, based on the assumption that only the adapter layer needs to change. The migration takes over three months, touching a large fraction of the codebase.

## Impact

A migration planned and communicated to stakeholders as a low-risk, two-week effort becomes a multi-month project, eroding trust in future architecture-driven time estimates.

## Initial Hypotheses

- The new persistence library is harder to use than expected — checked and ruled out; the library itself is straightforward.
- The team underestimated testing time — checked and ruled out; testing was a small fraction of the overrun.
- The domain layer had accumulated direct JPA dependencies over time, despite the folder structure — correct.

## Evidence

A dependency-direction audit — using a tool like ArchUnit, run for the first time during the migration — finds dozens of domain classes directly importing `javax.persistence` annotations and, in several cases, calling `EntityManager` methods directly from what were labeled "domain services." The folder structure existed, but the dependency rule it was supposed to enforce had eroded silently over time with no automated check catching it.

## Investigation Timeline

1. **Estimate blown**, with the migration touching far more of the codebase than the "only the adapter layer changes" assumption predicted.
2. **Library difficulty and testing-time hypotheses ruled out**, neither explaining a meaningful fraction of the overrun.
3. **Dependency-direction audit run for the first time**, using an automated tool rather than manual code inspection.
4. **Violations found directly**: domain classes importing persistence annotations and calling `EntityManager` methods, despite living in the "domain" folder.

## Root Cause

The team had hexagonal-shaped folders but no enforcement of the dependency rule itself — the pattern was believed to be a folder layout rather than a verified dependency direction. The estimate assumed the pattern was actually in effect; it wasn't, and nobody had checked.

## Immediate Mitigation

Scope the migration down to the adapters that were genuinely clean first, delivering partial value while the domain-layer cleanup for the rest proceeds separately.

## Permanent Fix

Add an automated ArchUnit (or equivalent) dependency-direction check to the build pipeline, failing any future domain-layer import of an infrastructure type, so the pattern's actual guarantee — not just its folder appearance — is continuously verified going forward.

## Alternatives Considered

Trusting code review alone to catch future violations. Rejected — the original erosion happened gradually across many individually small, individually reviewed changes; only an automated, continuously enforced check catches this class of drift.

## Trade-offs

Adding the ArchUnit check requires an upfront cleanup of every existing violation before it can be turned on — a real, one-time cost. Accepted in exchange for preventing the same estimate-shattering surprise on the next infrastructure swap.

## Prevention

Any team claiming hexagonal architecture should verify the claim with an automated dependency-direction check from the start, not rely on folder naming or code review discipline alone.

## Monitoring and Alerts

- The ArchUnit (or equivalent) dependency-direction check itself, run in CI on every build rather than only at migration time — this is the direct, structural fix, not a monitoring afterthought, and it converts future erosion into an immediate failed build rather than a multi-month discovery.
- A periodic (e.g., quarterly) architecture-fitness report summarizing dependency-rule violations over time, even below the fail threshold, so gradual erosion trends are visible before they accumulate to the scale seen here.

## Interview Story

This maps to "which files change, and which must not" for a claimed hexagonal architecture. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a two-week persistence-layer migration, estimated on the assumption of clean architectural boundaries, took over three months.
- **Task:** explain why the estimate was so far off despite the codebase's stated architecture.
- **Action:** rule out library difficulty and testing time as explanations; run a dependency-direction audit for the first time; find that the domain layer had silently accumulated direct persistence dependencies over time.
- **Result:** added an automated ArchUnit check to the build pipeline, converting the architectural claim from an unverified assumption into a continuously enforced guarantee.

## Staff-Level Discussion

The estimate failure here isn't really about architecture — it's about the gap between a claimed invariant and a verified one. Folder structure is a naming convention with zero enforcement power; a dependency-direction rule is only real if something checks it on every change, because individually small, individually reasonable-looking violations accumulate invisibly otherwise, exactly as they did here. This generalizes well beyond hexagonal architecture: any architectural property a team relies on for planning — layering, module boundaries, "this service doesn't call that database directly" — should be treated as unverified, and therefore unsafe to plan around, until there's an automated check proving it holds. A Staff engineer's estimate for any "should be a simple swap" migration should include, as a first step, verifying the assumption the estimate depends on, not just trusting the folder layout.

## Related Handbook Chapters

- [Clean Hexagonal Architecture](../handbook/architecture/clean-hexagonal-architecture.md) — canonical dependency-direction rule and hexagonal-architecture mechanics used here.
