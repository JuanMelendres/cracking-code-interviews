---
title: "Docker Build-Time Regression From a Collapsed COPY Layer"
document_type: production-cookbook-entry
domain: cloud
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/cloud/container-image-internals.md
source: handbook/cloud/container-image-internals.md#production-scenarios
---

# Docker Build-Time Regression From a Collapsed COPY Layer

## Context

A recent Dockerfile refactor had merged the previously separate "copy `pom.xml`, resolve dependencies" and "copy source, compile" steps into a single `COPY . .` followed by one `RUN mvn package`, in the name of simplifying the Dockerfile.

## Symptoms

A CI pipeline's Docker build step regressed from 90 seconds to 9 minutes after a routine dependency bump, with no change to application logic.

## Impact

Every CI build — which ran the build step dozens of times per day — paid the regressed build time, turning a one-time Dockerfile "simplification" into a standing, compounding cost across the whole team's pipeline.

## Initial Hypotheses

- A slow CI runner, registry pull throttling, or a genuinely larger dependency tree — these were the initial hypotheses pursued.

## Evidence

`docker history` on the new image showed the dependency-resolution layer's size roughly unchanged. `docker build` logs showed the dependency-resolution `RUN` step re-executing on every build, including ones that only changed application source — something that had not been true a week earlier.

## Investigation Timeline

1. **Build-time regression noticed**, from 90 seconds to 9 minutes, coinciding with a routine dependency bump.
2. **Runner and registry hypotheses pursued first**, on the assumption the regression was environmental rather than structural.
3. **`docker history` inspected**, showing the dependency layer's size was roughly unchanged, ruling out a genuinely larger dependency tree as the sole cause.
4. **Build logs examined directly**, revealing the dependency-resolution `RUN` step re-executing on every build, including source-only changes — a cache-invalidation pattern that had not existed a week earlier.

## Root Cause

A recent Dockerfile refactor merged the previously separate "copy manifest, resolve dependencies" and "copy source, compile" steps into a single `COPY . .` followed by one `RUN mvn package`. That collapsed two independent cache keys into one: any source change now invalidated dependency resolution too, forcing it to re-run on every build regardless of whether dependencies had actually changed.

## Immediate Mitigation

Reverted the merge, restoring the two-step `COPY pom.xml` → resolve → `COPY src` → compile shape.

## Permanent Fix

Added a CI check asserting the Dockerfile copies dependency manifests before application source, and documented the reasoning — the team's own reproduced timing numbers — so the "simplification" would not recur without someone re-deriving the cost.

## Alternatives Considered

Using a build-cache-mount (`--mount=type=cache`) for the dependency directory instead of restoring the two-step `COPY` order. Not adopted as the primary fix because it addresses the same symptom with additional BuildKit-specific mechanism, whereas the two-step `COPY` order is the simpler, already-proven fix requiring no additional build-tooling dependency.

## Trade-offs

The two-step form is a few lines longer and slightly less obvious to a first-time reader. The team judged the roughly 9x rebuild-time cost of the "simpler" version to be the wrong trade for a step that runs dozens of times per day.

## Prevention

Added a short comment directly above the split, and a linked reference to the chapter documenting the reasoning, rather than relying on every future editor rediscovering the reason independently.

## Monitoring and Alerts

- CI build-step duration tracked per pipeline stage, so a regression in the Docker build step specifically is visible rather than folded into overall pipeline runtime.
- An automated CI check asserting dependency-manifest copies precede source copies in the Dockerfile, catching a future "simplification" before it merges.

## Interview Story

This maps to a "why did our Docker build get slower" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a CI pipeline's Docker build step regressed from 90 seconds to 9 minutes after a routine dependency bump.
- **Task:** find why a dependency bump alone caused such a large regression.
- **Action:** ruled out a slower CI runner and a larger dependency tree via `docker history`; found in the build logs that the dependency-resolution step was re-executing on every build after a recent Dockerfile refactor collapsed two `COPY` steps into one.
- **Result:** reverted to the two-step `COPY` order, restoring independent cache keys for dependency resolution and source compilation, and added a CI check to prevent recurrence.

## Staff-Level Discussion

"We flattened the Dockerfile and CI got slower" is a concrete, defensible story that demonstrates understanding *why* layer order matters, not just that a style guide recommends splitting `COPY` steps — because cache invalidation only cascades forward from the first changed layer, layer ordering is a genuine architectural decision about which inputs are allowed to invalidate which downstream cost, not a cosmetic style preference. This has a direct organizational consequence: a "simplification" that reads as harmless in a code review (fewer lines, one `COPY` instead of two) can carry a real, compounding runtime cost invisible in the diff itself, which is exactly why the durable fix here is an automated CI check rather than a style-guide comment — the check enforces the invariant mechanically regardless of whether a future editor has read or remembers the reasoning.

## Related Handbook Chapters

- [Containers & Image Internals](../handbook/cloud/container-image-internals.md) — canonical layer-caching and `COPY`-ordering mechanism used here.
