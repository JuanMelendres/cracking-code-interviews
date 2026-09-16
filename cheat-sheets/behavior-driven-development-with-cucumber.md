---
title: "Cheat Sheet: Behavior-Driven Development with Cucumber"
slug: behavior-driven-development-with-cucumber
document_type: cheat-sheet
domain: 08-testing
topic_id: T-2412
canonical: ../syllabus/08-testing/behavior-driven-development-with-cucumber.md
last_updated: 2026-09-16
---

# Behavior-Driven Development with Cucumber

**Canonical chapter:** [`syllabus/08-testing/behavior-driven-development-with-cucumber.md`](../syllabus/08-testing/behavior-driven-development-with-cucumber.md)

## Core Mental Model

TDD and BDD share the same red-green rhythm. TDD writes the check in code, for developers. BDD writes it in structured natural language (Gherkin) readable by developers and non-developers together, then translates it to real code via step definitions.

## Essential Definitions

- **Gherkin** — the `Given`/`When`/`Then` structured-English syntax BDD tools use in `.feature` files.
- **Step definition** — the code (here, `@Given`/`@When`/`@Then`-annotated Java methods) Cucumber matches against a Gherkin line's wording and executes.
- **Scenario Outline + Examples** — one written scenario, multiple genuinely separate, independently-reported test instances (real data-driven testing, not a hidden loop).

## Decision Table

| Need | Reach for | Real evidence |
|---|---|---|
| Developer-only unit logic, fastest feedback | TDD, directly in code | See TDD chapter's own real kata |
| Feature-level criteria a non-developer must read | BDD (Gherkin + step defs) | Real feature file + step defs, 8/8 passing |
| Same behavior across several real inputs | `Scenario Outline` + `Examples` | 3 real, independent `Example` results |
| A step with no implementation yet | Use Cucumber's generated snippet | Real captured `PendingException` snippet |

## Common Pitfalls

- Treating BDD as "TDD with extra syntax" instead of an audience/vocabulary layer.
- Writing Gherkin scenarios nobody outside engineering ever reads.
- Padding an `Examples` table with rows that don't actually exercise the logic differently — a real 0%-discount row still passed with a genuine bug present.
- Confusing BDD/TDD (testing techniques) with Agile/Scrum/Waterfall (process models) as if they were alternatives.

## Interview Answer Skeleton

**30-sec:** BDD specifies behavior in Gherkin so developers and non-developers can both read and agree on it, then wires those scenarios to real step-definition code — same red-green mechanics as TDD, with a shared-vocabulary layer on top.

**2-min:** Add: `Scenario Outline`/`Examples` produces genuinely separate test instances, not a hidden loop. An undefined step is a real, reported failure with a real, Cucumber-generated method snippet. Choose BDD when a genuine non-developer audience needs the specification; plain TDD otherwise.

**Staff-level framing:** BDD scenarios nobody outside engineering reads have become extra-ceremony TDD — the fix is restoring real non-developer involvement, or honestly retiring the unread ones.

## Related

- syllabus/08-testing/writing-tests-live-in-an-interview.md
- syllabus/18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md
