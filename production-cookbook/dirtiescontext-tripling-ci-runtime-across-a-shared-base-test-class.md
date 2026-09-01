---
title: "@DirtiesContext Tripling CI Runtime Across a Shared Base Test Class"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/spring/spring-testing-slices-and-context-caching.md
source: handbook/spring/spring-testing-slices-and-context-caching.md#production-scenarios
---

# @DirtiesContext Tripling CI Runtime Across a Shared Base Test Class

## Context

A widely-shared base integration test class, extended by dozens of unrelated test classes, had `@DirtiesContext` added to it to silence one flaky test.

## Symptoms

Total CI test-suite runtime grew from roughly 4 minutes to roughly 12 minutes after a change that touched only one integration test class.

## Impact

Every engineer's CI pipeline slowed by roughly 3x, adding real, ongoing latency to every commit across the team — not a one-time cost but a standing regression on every future build.

## Initial Hypotheses

- A new, slow external dependency (a real database call) had been introduced — this was the first hypothesis pursued.

## Evidence

The actual diff added `@DirtiesContext` to a widely-shared base test class extended by dozens of unrelated integration test classes. It had been added to silence one flaky test that turned out to depend on mutated static state rather than a genuinely dirty Spring context.

## Investigation Timeline

1. **CI runtime regression noticed**, jumping from roughly 4 minutes to roughly 12 minutes after a single-class change.
2. **Slow-dependency hypothesis pursued first**, on the assumption a real database call had been added somewhere in the suite.
3. **Diff inspected directly**, surfacing a `@DirtiesContext` addition on a shared base class rather than any new external dependency.
4. **Mechanism confirmed**: because the base class's `@ContextConfiguration` was shared by the majority of the suite's integration tests, `@DirtiesContext` forced every one of them to rebuild the full `ApplicationContext` from scratch instead of reusing the cached one.

## Root Cause

`@DirtiesContext` evicts the cached `ApplicationContext` entry every other test class sharing that same configuration also depends on — its cost is not local to the class it is declared on. Because the base class was extended by dozens of unrelated test classes, one flaky-test workaround forced the full context to rebuild dozens of times instead of once.

## Immediate Mitigation

Reverted the `@DirtiesContext` addition.

## Permanent Fix

Fixed the actual flaky test's static-state leak directly, resetting the mutated static field in an `@AfterEach`, preserving the shared, cached context for the rest of the suite.

## Alternatives Considered

Isolating the flaky test into its own non-shared base class so `@DirtiesContext` would only affect it. Not pursued because it treats the symptom (context sharing) rather than the actual root cause (a static-state leak that should not exist regardless of context caching).

## Trade-offs

Fixing the real root cause took longer than the one-line `@DirtiesContext` addition. This was judged worthwhile against a 3x CI runtime regression affecting every engineer's pipeline.

## Prevention

Added a code-review rule flagging any new `@DirtiesContext` on a widely-extended base test class, requiring an explicit justification comment.

## Monitoring and Alerts

- CI runtime tracked as a first-class metric per commit, so a regression like this surfaces immediately rather than being noticed informally weeks later.
- A code-review checklist item specifically for `@DirtiesContext` additions on shared base classes, since its blast radius is invisible from the diff of the class it's added to.

## Interview Story

This maps to a "why did our CI suddenly get slower" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a one-line test annotation added to fix a flaky test tripled the whole suite's CI runtime.
- **Task:** find why a single-class change had a suite-wide effect.
- **Action:** ruled out a new slow dependency; inspected the actual diff and found `@DirtiesContext` on a shared base class, forcing full context rebuilds across dozens of unrelated test classes.
- **Result:** reverted the annotation, fixed the real static-state leak causing the original flakiness, and added a review rule for future `@DirtiesContext` additions on shared base classes.

## Staff-Level Discussion

`@DirtiesContext`'s cost model is easy to reason about in isolation — one annotation, one context rebuild — but its real cost scales with however many other test classes happen to share that context configuration, a fact invisible from the diff that adds it. This is a broader pattern in shared-fixture test infrastructure: the person adding a workaround to a shared base class rarely has visibility into every consumer of that base class, so a locally-reasonable-looking fix can have an organization-wide cost. The durable fix is a review gate specifically scoped to shared-fixture changes, since normal code review — focused on the diff's own class — has no natural mechanism to surface a cost that only appears when multiplied across dozens of unrelated files.

## Related Handbook Chapters

- [Spring Testing: Slices and Context Caching](../handbook/spring/spring-testing-slices-and-context-caching.md) — canonical context-caching and `@DirtiesContext` cost model used here.
