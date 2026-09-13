---
title: "Safe Field Removal Verified by Consumer-Driven Contract Tests"
document_type: production-cookbook-entry
domain: testing
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/08-testing/contract-testing-for-services.md
source: syllabus/08-testing/contract-testing-for-services.md#production-scenarios
---

# Safe Field Removal Verified by Consumer-Driven Contract Tests

## Context

A provider team plans to remove a field from an API response they believe is unused, based on reading the API specification alone.

## Symptoms

Without contract testing, the only way to confirm no consumer actually depends on the field is either a manual audit across every consuming team, or waiting to see if anything breaks after deployment.

## Impact

A manual audit is slow, error-prone, and only as current as the last time it was performed; deploying and waiting to see what breaks risks a real, consumer-facing outage as the discovery mechanism.

## Initial Hypotheses

- The specification alone is sufficient evidence the field is unused — rejected as insufficient; a specification describes what's documented, not what every consumer actually reads at runtime.
- A manual cross-team audit is the only reliable option — a real option, but slow and stale the moment any consumer changes independently.
- Consumer-driven contract tests, already covering each real consumer's actual dependencies, can answer this directly — correct, and adopted.

## Evidence

Every real consumer's actual dependencies are already explicit in their contract tests; running the full contract-test suite against the proposed change either passes cleanly or names exactly which consumer and dependency would break.

## Investigation Timeline

1. Provider team proposes removing a field believed unused.
2. Manual-audit and deploy-and-observe options considered and rejected as slow or risky.
3. Full consumer-driven contract-test suite run against the proposed removal.

## Root Cause

Not applicable in the incident sense — this is a risk-avoidance decision, not a failure. The underlying condition contract testing addresses is that no other mechanism keeps a provider's knowledge of "who depends on what" current as consumers evolve independently.

## Immediate Mitigation

Not applicable — no incident occurred; the contract-test run itself is the safeguard.

## Permanent Fix

Adopt consumer-driven contract tests as the standing gate for any API-shape change, not a one-time check for this specific removal.

## Alternatives Considered

A cross-team manual audit before every API change — rejected as an ongoing process cost that degrades as the number of consumers grows, whereas contract tests scale automatically with each consumer's own test suite.

## Trade-offs

Consumer-driven contract tests require each consuming team to maintain their own contract test and keep it current — a real, distributed maintenance cost, accepted in exchange for a fast, automated, always-current answer to "is this change safe."

## Prevention

Require a passing consumer-driven contract-test run as a standing gate before any provider-side API-shape change ships, rather than relying on specification review or tribal knowledge of which fields are "probably unused."

## Monitoring and Alerts

- Contract-test suite results wired directly into the provider's CI/CD pipeline as a hard gate, not an optional check.
- A registry of which consumers have an active contract test on file, so a consumer with no contract test at all is a known, visible gap rather than a silent one.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent decision.

- **Situation:** a provider team wanted to remove an API field believed unused, with no reliable way to confirm that belief.
- **Task:** decide safely, without a slow manual audit or a risky deploy-and-observe approach.
- **Action:** ran the existing consumer-driven contract-test suite against the proposed removal.
- **Result:** the suite passed cleanly, confirming the removal was genuinely safe with no consumer impact.

## Staff-Level Discussion

The organizational value of consumer-driven contract testing is turning "is this change safe" from a guess or a slow audit into a fast, automated, always-current answer — every real consumer's actual dependencies are already explicit and checked, so a passing suite is real evidence, not an assumption. This scales specifically where manual coordination does not: as the number of consumers grows, a manual audit's cost grows with it, while the contract-test suite's cost stays roughly proportional to each consumer's own, independently-maintained test.

## Related Handbook Chapters

- [Contract Testing for Services](../syllabus/08-testing/contract-testing-for-services.md) — the canonical consumer-driven contract-testing pattern behind this decision.
