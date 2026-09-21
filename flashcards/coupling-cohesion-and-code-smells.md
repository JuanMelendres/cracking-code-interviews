---
title: "Flashcards: Coupling, Cohesion, and Code Smells"
slug: coupling-cohesion-and-code-smells
document_type: flashcard-deck
domain: 04-software-design
topic_id: T-1703
canonical: ../syllabus/04-software-design/coupling-cohesion-and-code-smells.md
last_updated: 2026-09-21
---

# Flashcards: Coupling, Cohesion, and Code Smells

**Canonical chapter:** [`syllabus/04-software-design/coupling-cohesion-and-code-smells.md`](../syllabus/04-software-design/coupling-cohesion-and-code-smells.md)

## Card: Coupling vs. cohesion, precisely

**Prompt:**
What's the actual difference between coupling and cohesion?

**Answer:**
Coupling is about how much a class depends on *other* classes' internal details; cohesion is about whether a class's *own* responsibilities belong together. A class can score well on one axis and poorly on the other — they're independent measurements, not two names for the same idea.

**Why it matters:**
"Low coupling, high cohesion" is used constantly as a single phrase; being able to define each term independently is the actual interview skill.

**Common trap:**
Treating the two as interchangeable, or unable to say which one a specific piece of code violates.

**Related:**
[Coupling, Cohesion, and Code Smells](../syllabus/04-software-design/coupling-cohesion-and-code-smells.md)

## Card: Does decomposing a God Class always reduce coupling everywhere?

**Prompt:**
A God Class is decomposed into six single-purpose classes plus a thin orchestrator. Does coupling go down everywhere as a result?

**Answer:**
No — real, measured: each individual decomposed piece drops from coupling 5 (the God Class) to coupling 1, but the new orchestrator's own coupling count rises to 6, *higher* than the original God Class. Decomposition doesn't eliminate coupling system-wide, it redistributes it — something still has to coordinate the pieces.

**Why it matters:**
An honest, measured finding that avoids overclaiming "decomposition always helps everywhere" — a real, accepted trade-off, not a flaw.

**Common trap:**
Assuming any decomposition is an unqualified coupling win with no cost anywhere in the system.

**Related:**
[Coupling, Cohesion, and Code Smells](../syllabus/04-software-design/coupling-cohesion-and-code-smells.md)

## Card: The Law of Demeter's real, measured payoff

**Prompt:**
What did an identical internal-structure change to a `Wallet` class (single card to multiple cards) reveal about train-wreck-style code vs. Demeter-compliant code?

**Answer:**
Of 6 client files calling into the object graph, the 3 written as train-wrecks (`customer.getWallet().getCard()...`) failed with real compiler errors after the change; the 3 written Demeter-compliant (`customer.getCardLast4Digits()`, delegating through `Customer`) compiled completely unchanged. The exact same real change broke exactly the files that violated the principle.

**Why it matters:**
Turns "the Law of Demeter reduces fragility" from an abstract claim into a concrete, measured, reproducible count.

**Common trap:**
Treating Law of Demeter as a style preference rather than a real, checkable predictor of which code breaks when.

**Related:**
[Coupling, Cohesion, and Code Smells](../syllabus/04-software-design/coupling-cohesion-and-code-smells.md)
