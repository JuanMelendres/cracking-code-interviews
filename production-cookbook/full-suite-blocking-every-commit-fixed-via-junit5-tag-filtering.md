---
title: "Full Suite Blocking Every Commit, Fixed via JUnit 5 Tag Filtering"
document_type: production-cookbook-entry
domain: testing
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/testing/junit5-architecture-and-advanced-features.md
source: handbook/testing/junit5-architecture-and-advanced-features.md#production-scenarios
---

# Full Suite Blocking Every Commit, Fixed via JUnit 5 Tag Filtering

## Context

A team's test suite has grown to include both fast unit tests and a handful of genuinely slow integration-style tests, all run together as one suite on every commit.

## Symptoms

Every commit now waits for the full suite to complete, including the slow integration-style tests, regardless of whether the change touches anything those tests cover.

## Impact

Developer velocity degrades as commit-to-feedback latency grows with the suite's slowest tests, not its typical ones — every trivial change pays the full cost of the slowest integration test in the suite.

## Initial Hypotheses

- The slow tests themselves need to be optimized or removed — considered, but rejected as the wrong lever; the tests provide real value and their slowness is inherent to what they verify, not a defect in how they're written.
- The suite's structure — running fast and slow tests together as a single undifferentiated unit — is the actual bottleneck, not any individual test — correct.

## Evidence

The suite mixes fast unit tests with a handful of genuinely slow integration-style tests, and every commit runs the entire mixed set, so the fast majority of commits are gated by the slow minority of tests regardless of relevance to the specific change.

## Investigation Timeline

1. **Commit-to-feedback latency identified as a developer-productivity complaint**, traced to the full test suite running on every commit.
2. **Splitting into separate test source directories or projects considered**, recognized as a real, disruptive reorganization with its own cost and risk.
3. **Tag-based filtering evaluated as an alternative**, requiring only annotation changes rather than structural reorganization.
4. **Solution implemented**: tagging the slow tests (`@Tag("slow")`) and configuring CI to run only the fast-tagged subset on every commit.

## Root Cause

The suite ran fast and slow tests together as a single undifferentiated unit on every commit, so the full suite's total runtime — dominated by the slow, genuinely valuable integration-style tests — gated every change regardless of that change's actual relevance to the slow tests' coverage.

## Immediate Mitigation

None needed beyond the permanent fix — this was identified and resolved directly via JUnit 5's tag mechanism without requiring an interim workaround.

## Permanent Fix

Tag the slow tests (`@Tag("slow")`) and configure CI to run only the fast-tagged subset on every commit, with the full suite — including the slow tests — run on a nightly schedule or before merge, solving the problem without restructuring the codebase.

## Alternatives Considered

Splitting into separate test source directories or projects. Rejected as a real, disruptive reorganization compared to a tag-based filtering change, which achieves the same effective separation with far less structural risk and effort.

## Trade-offs

Fast, per-commit feedback no longer includes the slow tests' coverage, meaning a regression only the slow tests would catch could reach a commit before the nightly or pre-merge full run surfaces it. Accepted, since the alternative — every commit paying the full suite's cost — was measurably degrading development velocity for every change, not just the ones the slow tests actually cover.

## Prevention

Any test suite growing to include a meaningful mix of fast and slow tests should adopt tag-based (or equivalent) filtering proactively, before commit latency becomes a visible productivity complaint, rather than reactively once the pain is already being felt.

## Monitoring and Alerts

- Per-commit CI runtime tracked as a standing metric, alerting on a sustained upward trend — this surfaces the underlying suite-growth problem before it becomes a widely felt developer complaint.
- The nightly/pre-merge full-suite run's pass/fail status tracked separately and visibly, ensuring the slow tests' coverage — now decoupled from every commit — is still actively monitored and not silently neglected once it's off the fast path.

## Interview Story

This maps to "your test suite got slow, how do you speed up feedback without losing coverage." Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a growing test suite's slow integration-style tests were gating every commit's feedback loop, regardless of relevance.
- **Task:** restore fast commit-to-feedback latency without discarding the slow tests' real coverage value.
- **Action:** rule out removing or optimizing the slow tests as the wrong lever, since their slowness reflects genuine, valuable coverage; adopt tag-based filtering to decouple "runs on every commit" from "runs at all," rather than restructuring the codebase.
- **Result:** fast tests run on every commit; the full suite, including slow tests, runs nightly or pre-merge, restoring commit velocity while preserving coverage on a defined cadence.

## Staff-Level Discussion

The key judgment call in this scenario is recognizing that the actual constraint isn't "we have slow tests" — slow, valuable integration-style tests are a legitimate and necessary part of a test suite — but "we run all tests on every commit regardless of speed tier," which is a policy decision, not an inherent property of the tests themselves. This distinction matters because it points toward the low-cost fix (tag-based filtering, a policy and configuration change) rather than the high-cost one (restructuring the codebase into separate projects, or worse, cutting valuable slow tests to save time). A Staff engineer facing a "our tests are too slow" complaint should first ask whether the problem is test cost or test *scheduling* policy — the two have very different fixes, and conflating them leads to either unnecessary reorganization or a real loss of coverage.

## Related Handbook Chapters

- [JUnit 5 Architecture and Advanced Features](../handbook/testing/junit5-architecture-and-advanced-features.md) — canonical tag-filtering mechanism used here.
- [Test Strategy and Test Doubles](../handbook/testing/test-strategy-and-test-doubles.md) — the broader fast-vs-real-dependency test-composition trade-off this scenario is an instance of.
