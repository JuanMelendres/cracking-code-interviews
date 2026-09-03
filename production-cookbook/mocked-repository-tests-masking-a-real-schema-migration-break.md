---
title: "Mocked Repository Tests Masking a Real Schema Migration Break"
document_type: production-cookbook-entry
domain: testing
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/08-testing/test-strategy-and-test-doubles.md
source: handbook/testing/test-strategy-and-test-doubles.md#production-scenarios
---

# Mocked Repository Tests Masking a Real Schema Migration Break

## Context

A service's repository layer has a comprehensive test suite, all passing, all using a mocked database interface rather than a real database.

## Symptoms

A schema migration renames a column the repository code still references by its old name. The mocked test suite continues to pass unchanged; the first real signal is a production error rate spike immediately after deployment.

## Impact

A change that should have been caught by the test suite reaches production, causing every write through the affected repository method to fail.

## Initial Hypotheses

- The migration itself was flawed — checked and ruled out; the migration ran correctly and matches the intended schema.
- The deployment pipeline skipped running tests — checked and ruled out; the full suite ran and passed.
- The repository tests mock the database and therefore never execute real SQL against the new schema — correct.

## Evidence

Every repository test's mock is configured to return a canned value regardless of what SQL string the repository code actually constructs. The mocks were never updated because nothing forced them to reflect the schema change, and no test in the suite ever sent a real query to a real database.

## Investigation Timeline

1. **Production error spike observed** immediately after deployment, despite a fully passing test suite.
2. **Migration correctness and pipeline execution ruled out**, both confirmed working as intended.
3. **Repository test suite inspected directly**, finding every test mocks the database interface entirely.
4. **Mock behavior examined**: mocks return canned values regardless of the actual SQL constructed, meaning no test could have detected the column-name mismatch.

## Root Cause

A repository test that mocks the database verifies only the test's own assumptions about what the database does, never whether the actual SQL is valid against the real schema. The suite gave 100% passing confidence while testing nothing about real SQL correctness.

## Immediate Mitigation

Roll back the deployment, and manually verify the repository's SQL against the new schema before re-deploying.

## Permanent Fix

Add real integration tests for every repository method, run against a real, ephemeral database instance, specifically so a schema mismatch fails the build instead of reaching production.

## Alternatives Considered

Adding more unit tests with more detailed mocks. Rejected — more elaborate mocking still can't verify real SQL correctness; the fix has to include a real database somewhere in the pipeline.

## Trade-offs

Integration tests are slower and require Docker or database infrastructure in CI. Accepted, since the alternative — mocked-only coverage — demonstrably let a real production-breaking change through.

## Prevention

Any repository or boundary-code test suite consisting entirely of mocked dependencies should be flagged in review as providing false confidence about the boundary itself, even at 100% coverage.

## Monitoring and Alerts

- Test-suite composition tracked as its own metric — the ratio of boundary-code tests (repository, external-client) backed by a real dependency versus a mock — reviewed periodically, not just at incident time, since 100% pass rate gives no signal about this ratio on its own.
- A pre-deployment schema-compatibility check (a lightweight query against the target schema for every repository method's SQL) as a deployment-pipeline gate, independent of the existing test suite, catching this class of mismatch even if integration test coverage has gaps elsewhere.

## Interview Story

This maps directly to "your test suite is green but production broke anyway" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a fully passing, comprehensive test suite didn't catch a schema migration that broke every write in production.
- **Task:** explain how 100% passing tests provided zero protection against this specific class of change.
- **Action:** rule out a flawed migration and a skipped test run; inspect the repository test suite directly; identify that every test mocks the database and therefore never executes real SQL against a real schema.
- **Result:** added real integration tests against an ephemeral database for every repository method, converting a class of previously invisible risk into a build-time failure.

## Staff-Level Discussion

"100% test coverage" and "tested" are not the same claim, and this incident is the clearest possible demonstration of the gap: every line of the repository code was covered by a passing test, and none of those tests could have caught this specific, real-world-breaking change, because coverage measures whether code executed, not whether what it asserts corresponds to reality. Fully mocked boundary tests are a particularly dangerous version of this gap because they *look* rigorous — comprehensive, fast, green — while systematically excluding exactly the layer (real SQL against a real schema) most likely to break during routine operational changes like migrations. A Staff engineer reviewing test strategy should treat "does any test in this suite touch a real instance of this boundary" as a distinct, necessary question from "is this code covered," for any code that crosses a genuine system boundary (database, external API, message broker).

## Related Handbook Chapters

- [Test Strategy and Test Doubles](../syllabus/08-testing/test-strategy-and-test-doubles.md) — canonical mock-vs-real-dependency trade-off mechanics used here.
- [Integration Testing Against Real Dependencies](../syllabus/08-testing/integration-testing-against-real-dependencies.md) — the integration-testing approach used as the permanent fix.
