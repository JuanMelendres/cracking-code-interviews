---
title: "Report Generator DIP Violation Blocking Unit Tests on a Real Database"
document_type: production-cookbook-entry
domain: software-design
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/04-software-design/solid-principles.md
source: syllabus/04-software-design/solid-principles.md#production-scenarios
---

# Report Generator DIP Violation Blocking Unit Tests on a Real Database

## Context

A reporting service's `ReportGenerator` class directly constructs a `MySqlConnection` inside its own constructor.

## Symptoms

A team trying to write a unit test for the report-formatting logic finds they cannot run the test without a real, reachable MySQL database.

## Impact

Genuinely unit-testable formatting logic is coupled to a real database connection purely because of how the dependency was obtained, slowing test authoring and CI reliability (a database-dependent "unit" test is really an integration test in disguise).

## Initial Hypotheses

- The formatting logic itself genuinely requires live data to test meaningfully — checked, the formatting logic's inputs and outputs are fully determinable without touching a real database.
- The test framework lacks a mocking capability — checked, the framework supports test doubles; the blocker is architectural, not tooling.
- `ReportGenerator` constructs its own concrete database dependency instead of receiving an abstraction — correct.

## Evidence

`ReportGenerator`'s constructor directly instantiates `MySqlConnection`, with no constructor parameter or setter through which a test double could be substituted.

## Investigation Timeline

1. Team attempts to write a unit test for report formatting and finds a real database is required to even instantiate the class under test.
2. Formatting-logic-needs-live-data and tooling-limitation hypotheses ruled out.
3. `ReportGenerator`'s constructor inspected, confirming the concrete `MySqlConnection` is built internally rather than injected.

## Root Cause

A live Dependency Inversion Principle violation: the report-formatting logic depends on a concrete, low-level database connection type instead of an abstraction, so nothing about the class's own design allows substituting a fake for testing.

## Immediate Mitigation

Stand up a disposable test database instance for CI in the short term, accepting slower and less isolated test runs while the fix ships.

## Permanent Fix

Constructor-inject a `DataSource` abstraction instead of constructing `MySqlConnection` directly — the exact pattern `OrderServiceFixed`'s demo uses — letting the identical test run against an in-memory fake instead, with zero changes to `ReportGenerator`'s own formatting logic.

## Alternatives Considered

Adding a database-agnostic in-memory database (e.g., an embedded engine) purely to speed up the existing architecture's tests — rejected as treating the symptom; the class still couples formatting logic to a concrete connection type, just a faster one, and the same problem recurs for any other consumer needing a true unit test.

## Trade-offs

Introducing a `DataSource` abstraction adds one constructor parameter and a small amount of wiring at the composition root — accepted, since it's a one-time cost that unlocks fast, isolated unit tests for the class's entire remaining lifetime.

## Prevention

Treat any class that directly constructs a concrete infrastructure dependency (a specific database driver, a specific HTTP client implementation) inside its own constructor as a standing design-review flag, specifically when that class also contains logic that should be independently unit-testable.

## Monitoring and Alerts

- CI test-suite composition tracked over time (unit vs. integration test count and runtime), flagging when previously-fast test suites start requiring live infrastructure.
- A code-review checklist item for any new class whose constructor directly instantiates an infrastructure client rather than receiving one.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent refactor.

- **Situation:** a team couldn't write a fast unit test for reporting logic because doing so required a real database.
- **Task:** determine whether this was a genuine data dependency or an avoidable architectural coupling.
- **Action:** inspected the class's constructor, confirmed the concrete database connection was built internally rather than injected, and introduced a `DataSource` abstraction via constructor injection.
- **Result:** the exact same formatting logic became testable against an in-memory fake, with no change to the logic itself.

## Staff-Level Discussion

This is a live Dependency Inversion Principle violation with a very concrete cost: testability, not just an abstract layering concern. The organizational pattern worth generalizing is that "can this be unit-tested without live infrastructure" is a fast, mechanical proxy for whether a class's dependencies are properly inverted — a class that fails that test is very often coupled to a concrete implementation detail it never needed to know about.

## Related Handbook Chapters

- [SOLID Principles](../syllabus/04-software-design/solid-principles.md) — the canonical Dependency Inversion Principle violation and fix behind this incident.
