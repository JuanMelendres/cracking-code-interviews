---
title: "Flaky CI Integration Tests From Shared Container State"
document_type: production-cookbook-entry
domain: testing
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/08-testing/integration-testing-against-real-dependencies.md
source: handbook/testing/integration-testing-against-real-dependencies.md#production-scenarios
---

# Flaky CI Integration Tests From Shared Container State

## Context

A service's CI configuration runs the integration test suite with test-class-level parallelism against a single shared Postgres container.

## Symptoms

The integration test suite passes reliably on every developer's machine but fails roughly 1 in 10 CI runs, always on a different, seemingly unrelated test, with no consistent error.

## Impact

Intermittent CI failures erode trust in the test suite; developers begin re-running failed builds reflexively instead of investigating, letting a real underlying issue persist unaddressed.

## Initial Hypotheses

- CI infrastructure is simply slower or less reliable than local machines — checked and ruled out; failures don't correlate with CI load or timing, they correlate with specific test co-occurrence.
- A genuine flaky bug in the code under test — checked and ruled out; the same "failing" assertions pass when the failing test is run in isolation.
- Tests are sharing state in a single database container run in parallel — correct.

## Evidence

The CI configuration runs the integration test suite with test-class-level parallelism against a single shared Postgres container. The intermittent failures always involve two specific tests that both insert a row with the same hardcoded ID, and the failure only occurs when both happen to run concurrently against the shared schema.

## Investigation Timeline

1. **Flakiness noticed**: roughly 1-in-10 CI failures, never reproducible locally, no consistent failing test.
2. **Load and timing hypotheses ruled out** by checking failures against CI resource metrics, which show no correlation.
3. **Isolation test run**: the same assertions that fail intermittently in CI pass reliably when the specific test is run alone, ruling out a genuine bug in the code under test.
4. **Co-occurrence pattern found**: failures trace to two specific tests, both inserting a row with the same hardcoded ID, always failing only when scheduled concurrently against the shared container.

## Root Cause

Unclean or shared state between test runs — specifically, parallel tests racing against the same container and schema with overlapping hardcoded test data, rather than each test using isolated data or a reset schema.

## Immediate Mitigation

Disable test-class-level parallelism temporarily to stop the races while a real fix is implemented.

## Permanent Fix

Give each test class its own schema, or wrap each test in a transaction rolled back afterward, so concurrent tests can never observe each other's data — restoring safe parallelism without the race.

## Alternatives Considered

Using unique, randomly generated IDs per test instead of hardcoded ones. Accepted as a supplementary practice, but rejected as the sole fix — it doesn't address every possible form of shared-state interference, such as aggregate counts or sequences that any concurrent test could still perturb.

## Trade-offs

Per-test-class schema isolation adds setup and teardown overhead per test class. Accepted, since the alternative is an unreliable CI signal that erodes confidence in every test run, not just the affected ones.

## Prevention

Any integration test suite introducing parallelism should isolate each parallel unit's data explicitly — isolated schema, or transactional rollback — verified before parallelism is enabled, not discovered via intermittent CI failures months later.

## Monitoring and Alerts

- CI flake rate tracked per test and per test-pair co-occurrence, not just an aggregate pass/fail rate — the co-occurrence signal is what actually identifies the two colliding tests here, and a per-pair view surfaces it far faster than eyeballing failure logs across many runs.
- A standing CI dashboard distinguishing "failed and never passed on retry" from "failed once, passed on retry" — the latter pattern is the direct signature of exactly this class of flakiness and deserves its own alert rather than being lumped in with genuine failures.

## Interview Story

This maps to the "tests pass locally but fail intermittently in CI" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** an integration suite failing roughly 1 in 10 CI runs, always a different test, never reproducible locally.
- **Task:** find the cause without a consistent failing test or error message to anchor the investigation.
- **Action:** rule out CI infrastructure and a genuine code bug using targeted checks (load correlation, isolated re-run); find the specific test pair that always fails together; trace it to hardcoded IDs colliding under parallel execution against a shared container.
- **Result:** moved to per-test-class schema isolation, restoring safe parallelism and eliminating the intermittent failures.

## Staff-Level Discussion

The organizational cost of this bug class is easy to underestimate because it's not "the test suite is broken" — it's "the test suite is broken 10% of the time," which is exactly the failure rate that teaches developers to route around the signal (reflexive re-runs) rather than trust it. That habit is the real damage: once re-running on failure becomes normal, a genuine regression hiding behind a flaky-looking failure will get re-run into a false pass instead of being investigated. The permanent fix (schema isolation) is a one-time infrastructure investment, but the harder Staff-level judgment is recognizing that "our CI is a little flaky" is never a low-priority complaint — it directly degrades the reliability of every other signal the team depends on to ship safely.

## Related Handbook Chapters

- [Integration Testing Against Real Dependencies](../syllabus/08-testing/integration-testing-against-real-dependencies.md) — canonical Testcontainers setup and test-isolation methodology used here.
