---
title: "Brittle Class-Name Selectors Breaking 40 Tests on a Routine Icon Swap"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-testing.md
source: syllabus/21-frontend-web/react-testing.md#production-scenarios
---

# Brittle Class-Name Selectors Breaking 40 Tests on a Routine Icon Swap

## Context

A team migrates their icon components from one library to another; the visual result and all user-facing behavior are identical, but the new library renders different wrapper `div`s and class names.

## Symptoms

CI goes red across roughly 40 tests immediately after the migration.

## Impact

A safe, purely-internal migration is blocked, and the team's confidence in the test suite's signal is damaged.

## Initial Hypotheses

- The migration introduced real behavioral regressions — the team's first, alarmed assumption.
- A build-tooling incompatibility with the new icon library — checked, the build succeeds and the app functions correctly when clicked through manually.
- The failing tests query by class name or DOM structure, which changed cosmetically, not by any user-observable behavior — correct.

## Evidence

Every failing test queries by class name (`.icon-wrapper`, `.btn-icon-left`) or DOM structure, none by role or accessible name; manually clicking through the actual app shows every one of those 40 features working correctly for a real user.

## Investigation Timeline

1. CI turns red across ~40 tests immediately following the icon-library migration.
2. Real-regression hypothesis tested first by manually clicking through the app, which behaves correctly throughout.
3. Every failing test's query inspected, confirming all target implementation-detail selectors (class names, DOM structure), none target role or accessible name.

## Root Cause

The tests were coupled to implementation details (specific class names and DOM structure) that were never contractually meaningful to real users, so a purely cosmetic internal change broke them despite no actual behavioral regression.

## Immediate Mitigation

Triage the 40 failures as false negatives, not regressions, unblocking the release.

## Permanent Fix

Rewrite the failing queries to `getByRole`/`getByLabelText` equivalents, and add the principle (query by role/label first, testid last-resort only) as an explicit code-review checklist item.

## Alternatives Considered

Reverting the icon-library migration to avoid rewriting the tests — rejected as solving the wrong problem; the migration itself was safe and desirable, and reverting would only postpone the same test-fragility issue to the next unrelated visual refactor.

## Trade-offs

Rewriting 40 tests to query by role/label instead of class name is real, one-time work — accepted, since it removes the same false-alarm cost for every future purely-visual refactor, not just this one.

## Prevention

Default every new test to `getByRole`/`getByLabelText`-style queries, treating `class`-name or DOM-structure-based selectors as a reviewed exception, not the default approach.

## Monitoring and Alerts

- A lint rule flagging test queries that target class names or raw DOM selectors instead of role/label-based queries.
- Tracking false-negative rate for the test suite (failures later confirmed to be non-regressions) as its own metric, surfacing brittle-selector risk before it erodes trust in CI.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent migration.

- **Situation:** a routine, purely-cosmetic icon-library migration broke about 40 tests overnight.
- **Task:** determine whether these were real regressions or false alarms, and fix the actual underlying cause.
- **Action:** manually verified every affected feature worked correctly for real users, then confirmed every failing test queried by implementation detail rather than role or label.
- **Result:** triaged the failures as false negatives, unblocked the release, and rewrote the queries to role/label-based selectors as a standing practice.

## Staff-Level Discussion

Prevention's real cost is cultural, not technical: a team that has been burned by false negatives starts distrusting red CI in general, which is a far more dangerous failure mode than the original brittle tests. The organizational lesson is that test brittleness isn't just a maintenance annoyance — its true cost is eroded trust in the test suite's signal, which, once lost, is far harder to rebuild than the tests themselves are to rewrite.

## Related Handbook Chapters

- [React Testing](../syllabus/21-frontend-web/react-testing.md) — the canonical role/label-first query strategy behind this incident's fix.
