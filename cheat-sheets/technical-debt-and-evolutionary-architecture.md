---
title: "Cheat Sheet: Technical Debt and Evolutionary Architecture"
slug: technical-debt-and-evolutionary-architecture
document_type: cheat-sheet
domain: architecture
topic_id: T-913
canonical: ../handbook/architecture/technical-debt-and-evolutionary-architecture.md
last_updated: 2026-09-02
---

# Technical Debt and Evolutionary Architecture

**Canonical chapter:** [`handbook/architecture/technical-debt-and-evolutionary-architecture.md`](../handbook/architecture/technical-debt-and-evolutionary-architecture.md)

## Core Mental Model

Technical debt, like financial debt, is not inherently bad — it's a deliberate or accidental trade of short-term speed for long-term cost, and the entire question is whether the "interest rate" (the ongoing tax it imposes on every future change) is understood and being paid down deliberately, or silently compounding unnoticed. Evolutionary architecture is the discipline of allowing a system to change incrementally and continuously while protecting the architectural characteristics that matter most, using automated, objective checks — fitness functions — instead of relying on someone remembering to look.

## Essential Definitions

- **Technical debt** — the accumulated cost of choices (deliberate or accidental) that made past delivery faster at the expense of making future delivery slower, less safe, or both.
- **Fitness function** — any automated, objective, repeatable check that verifies a system still meets some architectural characteristic that matters (coupling, cycle time, dependency direction), run continuously as the system evolves.
- **Evolutionary architecture** — allowing a system's structure to change incrementally over time, guided by fitness functions that catch regressions automatically.
- **Debt as an economic frame, not a quality frame** — "this code is ugly" rarely persuades; "this component's coupling is measurably increasing per-feature delivery time, and here's the trend" does, because it's a claim about business risk a stakeholder can weigh.
- **Deliberate vs. accidental debt** — deliberate debt (a known-simplified version with a plan to revisit) is a legitimate trade-off named explicitly; accidental debt (coupling that crept in unnoticed) is more common and more dangerous because no one decided to take it on.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Debt's cost is currently invisible to non-engineering stakeholders | Reframe it in measured delivery-time/risk terms before asking for remediation time |
| A component has an architectural characteristic worth protecting long-term | Add a fitness function for it now, not after the first regression |
| Remediation is large enough to carry real rewrite risk | Break it into incremental steps (see Strangler Fig) |
| Debt was taken on deliberately, with a plan to revisit | Track it explicitly (an ADR or ticket with a revisit trigger) |
| A fitness function's threshold is currently unowned or unreviewed | Assign explicit ownership before treating it as real governance |

**Approach comparison:**

| Approach to debt | Cost visibility | Prevents recurrence |
|---|---|---|
| Ignore it | Invisible until it compounds into a visible delivery problem | No |
| Advocate on code-quality grounds | Low — hard for non-engineers to weigh | No, by itself |
| Advocate on measured delivery-risk grounds | High — directly comparable to feature costs | No, by itself |
| Remediate + add a fitness function | High | Yes — the central mechanism |

## Key Numbers (real, executed `java.lang.reflect` output against real compiling classes)

- A minimal fitness function measuring efferent coupling (distinct non-JDK collaborator field types): `before.OrderProcessor` measured 10 collaborators against a threshold of 5 → FAIL. After three incremental extraction steps, `after.OrderProcessor` measured 4 against the same threshold of 5 → PASS.
- Both versions, run directly, produced the identical final computed price and identical set of side effects — the refactor changed only what `OrderProcessor` has to know about, not its behavior.

## Common Pitfalls

- Making the case for debt paydown as a code-quality or aesthetic argument.
- Treating a design review or ADR as sufficient, permanent protection for an architectural characteristic, with no automated check keeping it true as the system continues to change.
- Proposing a full rewrite as the default remediation, without considering incremental extraction.
- Adding a fitness function with a threshold that's never reviewed or revisited, turning it into either a permanent blocker or a target for silent bypass.

## Interview Answer Skeleton

**30-sec:** Technical debt is the accumulated cost of past choices that made delivery faster then and slower now. The persuasive framing is economic — measured delivery-time or risk impact — not code quality. Fitness functions are automated, objective checks that protect an important architectural characteristic continuously, which is what makes evolutionary architecture different from a one-time design review.

**2-min:** Add the real fitness-function measurement: 10 collaborators (fail, threshold 5) before, 4 (pass) after three incremental extractions, with identical preserved behavior — proof, not assertion, that incremental paydown works without a risky rewrite.

**Whiteboard:** Draw one box "OrderProcessor" with ten arrows radiating to ten labeled boxes. Redraw with the ten collapsed into three coordinator boxes plus one direct dependency. Under the first: "10, no automated check." Under the second: "4, protected by a fitness function with threshold 5."

**Staff-level framing:** Discuss the organizational process required for fitness-function thresholds to be real governance rather than theater (explicit ownership, a documented review process, visibility when bypassed). Connect incremental modernization explicitly to migration patterns as the same underlying discipline at different scales. Reason about deliberate vs. accidental debt as a framing choice that changes how each should be tracked.

## Production Warning Signs

- Per-feature delivery time on a specific component trending up over multiple releases with no corresponding increase in that component's actual business complexity — the signature of debt accrued silently across many individually-reasonable changes.
- A component accumulates dependencies one or two at a time across roughly a dozen separate, individually-reviewed pull requests, none flagged as risky in isolation — the aggregate cost only visible in delivery-velocity data much later.
- A fitness function with no owner or review process for its threshold — either blocks legitimate growth indefinitely or gets silently bypassed/deleted the first time it's inconvenient.
- Debt framed only in engineering-internal language ("this violates SOLID") to a non-engineering stakeholder, rather than in delivery-time or reliability terms they actually weigh decisions against.

## Related

- `handbook/architecture/strangler-fig-and-migration-patterns.md`
- `handbook/architecture/modular-monolith-as-a-deliberate-choice.md`
- `handbook/architecture/architecture-decision-records.md`
- `behavioral-handbook/11-technical-debt-advocacy.md`
