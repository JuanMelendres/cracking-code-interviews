---
title: "Flashcards: Modular Monolith as a Deliberate Choice"
slug: modular-monolith-as-a-deliberate-choice
document_type: flashcard-deck
domain: architecture
topic_id: T-910
canonical: ../handbook/architecture/modular-monolith-as-a-deliberate-choice.md
last_updated: 2026-09-02
---

# Flashcards: Modular Monolith as a Deliberate Choice

**Canonical chapter:** [`handbook/architecture/modular-monolith-as-a-deliberate-choice.md`](../handbook/architecture/modular-monolith-as-a-deliberate-choice.md)

## Card: Does a package name enforce a boundary?

**Prompt:**
Does naming a package `internal` actually stop another module from depending on it in Java?

**Answer:**
No — every class involved is still `public`, and nothing in the language enforces the naming convention. Only a real, automated architecture test does.

**Why it matters:**
Verified directly: a real class compiled and ran cleanly while directly violating the intended boundary, until a real ArchUnit rule caught it.

**Common trap:**
Assuming a naming convention alone provides real protection.

**Related:**
[Core Concepts](../handbook/architecture/modular-monolith-as-a-deliberate-choice.md#core-concepts)

## Card: Boundary violations vs. cycles

**Prompt:**
Why do module-level cycles need a separate check from single-direction boundary violations?

**Answer:**
A cycle is a structural defect between a *pair* of modules — neither individual dependency looks wrong in isolation, so a single-boundary rule (or a single-PR reviewer) can miss it entirely; a dedicated slice/cycle check is needed.

**Why it matters:**
This chapter reproduced a real cycle entering through one plausible-looking shortcut, invisible without a dedicated check.

**Common trap:**
Assuming a boundary-violation rule alone also catches cycles.

**Related:**
[Production Scenarios](../handbook/architecture/modular-monolith-as-a-deliberate-choice.md#production-scenarios)

## Card: When to extract a module into a service

**Prompt:**
When is a module in a modular monolith actually ready to become its own service?

**Answer:**
When the decomposition chapter's own criteria are met (multiple, independently-scheduled sub-teams) — and specifically, when the module's boundary has been real and continuously enforced long enough to trust it under production load, not just drawn on a diagram.

**Why it matters:**
An enforced boundary has already been exercised; an unenforced one may hide coupling extraction will only discover the hard way.

**Common trap:**
Treating "it's in its own package" as sufficient justification.

**Related:**
[Interview Questions, Question 2](../handbook/architecture/modular-monolith-as-a-deliberate-choice.md#interview-questions)
