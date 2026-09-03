---
title: "Cheat Sheet: JUnit 5 Architecture and Advanced Features"
slug: junit5-architecture-and-advanced-features
document_type: cheat-sheet
domain: testing
topic_id: T-1102
canonical: ../handbook/testing/junit5-architecture-and-advanced-features.md
last_updated: 2026-08-05
---

# JUnit 5 Architecture and Advanced Features

**Canonical chapter:** [`syllabus/08-testing/junit5-architecture-and-advanced-features.md`](../syllabus/08-testing/junit5-architecture-and-advanced-features.md)

## Core Mental Model

JUnit 4 grew increasingly monolithic — Runners, Rules, and inheritance-based extension mechanisms accumulated as separate, not-fully-composable systems. JUnit 5 was a deliberate architectural reset: three independent modules with a defined contract (**Platform** — framework-agnostic discovery/launching; **Jupiter** — the modern programming model; **Vintage** — a compatibility layer for JUnit 3/4 tests). Extensions in Jupiter are a single, composable interface-based mechanism, replacing JUnit 4's separate Runner and Rule systems.

## Essential Definitions

- **JUnit Platform** — the `TestEngine`/launcher infrastructure, testing-framework-agnostic; lets Jupiter, Vintage, and other frameworks (Spock) run side by side.
- **JUnit Jupiter** — the modern model: `@Test`, `@ParameterizedTest`, `@TestFactory`, `@Nested`, the `Extension` interface family.
- **JUnit Vintage** — runs existing JUnit 3/4 tests unchanged, enabling incremental rather than big-bang migration.
- **`@ParameterizedTest` vs. `@TestFactory`** — fixed, known-in-advance data vs. runtime-generated `DynamicTest` instances.

## Decision Table

| Feature | Use for | Not for |
|---|---|---|
| `@ParameterizedTest` | Fixed, known-in-advance data-driven cases | Runtime-computed case sets |
| `@TestFactory` | Runtime-generated dynamic test cases | Simple, fixed data (unnecessary complexity) |
| Custom `Extension` (`@ExtendWith`) | Cross-cutting behavior across classes, composably | A single class's one-off setup |
| `@Tag` | Selective execution (fast/slow CI split) | Categorization with no execution-selection need |

**Trade-offs:** `@TestFactory`'s runtime flexibility costs some of `@ParameterizedTest`'s convenient built-in data-source annotations; a custom `Extension` centralizes cross-cutting behavior cleanly but adds an indirection a new team member must learn to trace.

## Key Numbers (real, executed — `AdvancedFeaturesDemoTest.java` via the programmatic `Launcher` API)

```
10 tests found, 10 successful, 0 failed
(5 @ParameterizedTest cases, 4 @TestFactory dynamic tests, 1 @Nested @Test,
 with a custom TimingExtension printing real per-test timing)
```

Real proof `@Tag` filtering genuinely partitions execution:

```
Filtering to tag=slow: 1 tests found, 1 succeeded
Filtering to tag=fast: 9 tests found, 9 succeeded
```

## Common Pitfalls

- Choosing `@TestFactory` for a fixed, small set of known cases where `@ParameterizedTest` would be simpler.
- Implementing a correct custom `Extension` but forgetting to register it via `@ExtendWith` — a silent no-op with no error.
- Relying on tag-name string literals with no shared constant, risking a silent typo mismatch between a test's tag and a CI filter.

## Interview Answer Skeleton

**30-sec:** JUnit 5 splits into three modules — Platform, Jupiter, Vintage — so each can evolve independently and legacy/modern tests run side by side during migration. Its unified `Extension` interface family replaces JUnit 4's separate Runner and Rule systems with one composable mechanism.

**2-min:** Add why (JUnit 4 accumulated fragmented, not-fully-composable extension mechanisms over a decade) + the real evidence (a real 10-test run mixing parameterized, dynamic, nested tests and a custom extension; real tag-filtering selecting exactly 1-of-10 vs. 9-of-10) + the trade-off (`@TestFactory`'s flexibility vs. `@ParameterizedTest`'s convenience for fixed data).

**Whiteboard:** Three stacked layers — "Platform" (bottom, framework-agnostic launcher), "Jupiter" and "Vintage" side by side on top, both feeding into Platform. Beside it: a test class with several `@ExtendWith` arrows from small "Extension" boxes, contrasted with a crossed-out single arrow from "AbstractBaseTest," labeled "composition, not inheritance."

**Staff-level framing:** the three-module split's practical payoff is Vintage enabling incremental, not big-bang, JUnit 4-to-5 migration — a deliberate design choice, not incidental compatibility. Tag-based CI-execution strategy is a first-class testing-infrastructure decision, not an afterthought.

## Production Warning Signs

- A team's test suite mixes fast unit tests and slow integration-style tests, and every commit waits for the full suite — tag the slow tests (`@Tag("slow")`) and configure CI to run only `fast` on every commit, full suite nightly, from the same test source, rather than restructuring into separate directories.
- A "fast-only" CI job unexpectedly runs a slow test — confirm the tag string matches exactly between `@Tag` and the CI filter configuration; tag names have no compile-time checking, and a typo silently fails to exclude.
- **Prevention:** prefer composable `@ExtendWith`-registered extensions over inheritance-based shared base test classes; define tag names as shared constants rather than repeating string literals.

## Related

- `syllabus/08-testing/test-strategy-and-test-doubles.md`
- `syllabus/08-testing/writing-tests-live-in-an-interview.md`
