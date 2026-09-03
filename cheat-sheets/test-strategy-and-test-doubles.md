---
title: "Cheat Sheet: Test Strategy and Test Doubles"
slug: test-strategy-and-test-doubles
document_type: cheat-sheet
domain: testing
topic_id: T-1103
canonical: ../handbook/testing/test-strategy-and-test-doubles.md
last_updated: 2026-08-04
---

# Test Strategy and Test Doubles

**Canonical chapter:** [`syllabus/08-testing/test-strategy-and-test-doubles.md`](../syllabus/08-testing/test-strategy-and-test-doubles.md)

## Core Mental Model

A test double's whole job is to let you isolate the one thing you actually want to verify from everything that thing merely calls. A mock goes one step further than a stub: it doesn't just return a canned value, it lets the test assert on the *interaction* itself — how many times something was called, with what arguments — which is often the actual bug surface for logic like retries, where the return value alone can look identical whether the retry logic is correct or broken.

## Essential Definitions

- **Test strategy** — the discipline of deciding what to test at which level (unit, integration, end-to-end) rather than writing tests reactively.
- **Test double** (mock, stub, fake, spy) — stands in for a real dependency so a unit test can isolate the logic actually under test from everything it merely calls.
- **Ice-cream-cone anti-pattern** — many slow, brittle end-to-end tests and almost no fast unit tests, the inverse of what actually catches bugs cheaply.
- **Coverage** — measures that lines/branches were executed at least once — nothing about whether the assertions in those tests are meaningful.

## Decision Table

| Level | Speed | What it catches | What it misses |
|---|---|---|---|
| Unit (mocked deps) | Fast (460ms for 3 tests, JVM-startup-dominated) | Logic bugs, branch coverage, exact interaction counts | Anything about how the real dependency actually behaves |
| Integration (real deps) | Slower (real Docker, real network round-trips) | Real behavior of the boundary code (SQL correctness, serialization, real error codes) | Doesn't need to cover every logic branch — that's unit's job |
| End-to-end | Slowest, most brittle | Whether the whole system works together | Expensive to maintain — target a handful of critical paths, not comprehensive coverage |

| Situation | Test level |
|---|---|
| Pure business logic, no IO | Unit test, no mocks needed |
| Logic calling a slow/external/flaky dependency | Unit test, mock the dependency |
| Code whose whole job IS talking to a real system (DB, API client) | Integration test, real dependency |
| A handful of critical, whole-system user flows | End-to-end test, sparingly |

## Key Numbers (real, executed — JUnit 5 console launcher, Mockito)

```
Test run: 3 tests, 0 failed, finished after 460ms (including JVM startup)
verify(gateway, times(3)) -- gateway called exactly 3 times with ("cust-1", 5000)
service.processPayment("cust-1", 5000) -> true   (PaymentService, maxAttempts=3)
succeedsImmediatelyWithNoRetriesNeeded test: verify(gateway, times(1))
```

## Common Pitfalls

- Treating coverage percentage as a quality target rather than a diagnostic tool for finding untested code
- Mocking the exact dependency an integration test exists to verify (mocking the database in a repository test)
- Building an ice-cream-cone test suite — many slow end-to-end tests, few fast unit tests — because it feels more thorough

## Interview Answer Skeleton

**30-sec:** A test double lets a test verify an interaction (call count, arguments), not just a return value — `verify(gateway, times(3))` proves a retry loop called its dependency the exact expected number of times. The testing pyramid (many fast unit tests, fewer integration, very few end-to-end) reflects a real cost/coverage trade-off; inverting it trades feeling thorough for a slow, flaky suite.

**2-min:** Add why it exists (isolate logic from what it calls) + coverage measures execution, not assertion quality + the mocked-repository production incident.

**Whiteboard:** Draw the pyramid (wide unit base, narrower integration band, thin end-to-end cap). Draw the ice-cream-cone inversion next to it, annotated "feels thorough, produces a slow flaky suite developers learn to skip."

**Staff-level framing:** the choice of what to mock is itself an architectural decision, not a testing detail. "This code is hard to unit test" is a signal about the code's structure, not just a testing problem to work around with more mocks — flakiness itself is often a more useful design signal than a coverage number.

## Production Warning Signs

- **Real incident pattern:** a repository layer has a comprehensive, all-passing, fully-mocked test suite. A migration renames a column; the mocked suite still passes; the first real signal is a production error-rate spike right after deployment — every write through the affected repository method fails.
- Root cause: a repository test that mocks the database verifies only the test's own assumptions, never real SQL against the real schema. Prevention: any boundary-code test suite consisting entirely of mocked dependencies should be flagged in review as providing false confidence, even at 100% coverage.

## Related

- `syllabus/08-testing/integration-testing-against-real-dependencies.md`
- [Clean and Hexagonal Architecture](clean-hexagonal-architecture.md)
