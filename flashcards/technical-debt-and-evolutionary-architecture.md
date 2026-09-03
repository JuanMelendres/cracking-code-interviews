---
title: "Flashcards: Technical Debt and Evolutionary Architecture"
slug: technical-debt-and-evolutionary-architecture
document_type: flashcard-deck
domain: architecture
topic_id: T-913
canonical: ../handbook/architecture/technical-debt-and-evolutionary-architecture.md
last_updated: 2026-09-02
---

# Flashcards: Technical Debt and Evolutionary Architecture

**Canonical chapter:** [`syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md`](../syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md)

## Card: Economic framing vs. code-quality framing

**Prompt:**
Why does "this code is messy" usually fail to persuade a stakeholder, while "delivery time on this component increased 40%" usually succeeds?

**Answer:**
Because "messy" is an aesthetic claim a non-engineer has no way to weigh against a competing feature request, while a measured delivery-time or risk impact is stated in the same currency (business cost) the stakeholder already reasons in.

**Why it matters:**
This is the register's own named misconception on this topic, and the single fastest way to lose a skeptical PM in an interview answer.

**Common trap:**
Leading with a code-quality principle (SOLID, DRY) as if it were self-evidently persuasive outside engineering.

**Related:**
[handbook/architecture/technical-debt-and-evolutionary-architecture.md](../syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md)

## Card: What is a fitness function, concretely?

**Prompt:**
Give a concrete, minimal example of a fitness function.

**Answer:**
A reflection-based check that counts a class's distinct non-JDK field types (efferent coupling) and fails a build if that count exceeds a threshold — this chapter's real, executed example measured 10 (fail, threshold 5) before a refactor and 4 (pass) after, using nothing but `java.lang.reflect`.

**Why it matters:**
Candidates often describe fitness functions vaguely; a concrete example demonstrates real understanding of the mechanism, not just the term.

**Common trap:**
Assuming a fitness function requires a specific tool (ArchUnit, a commercial product) rather than understanding it as a concept any automated, objective, repeatable check satisfies.

**Related:**
[handbook/architecture/technical-debt-and-evolutionary-architecture.md](../syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md)

## Card: Why can't a design review alone prevent this?

**Prompt:**
Why doesn't a good architecture review at project kickoff prevent the kind of coupling erosion this chapter demonstrates?

**Answer:**
Because the erosion happens gradually, across many individually-reasonable subsequent changes, each of which looks fine in isolation at the time it's reviewed — the aggregate cost is only visible in hindsight, which is exactly what a continuously-run, automated fitness function catches and a one-time review cannot.

**Why it matters:**
It's the core justification for evolutionary architecture as a distinct discipline from "just do good design reviews."

**Common trap:**
Believing sufficiently rigorous human review process alone (without automation) is enough to prevent this kind of drift.

**Related:**
[handbook/architecture/technical-debt-and-evolutionary-architecture.md](../syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md)
