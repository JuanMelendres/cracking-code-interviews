---
title: "Flashcards: Spring Security Filter Chain"
slug: security-filter-chain
document_type: flashcard-deck
domain: spring
topic_id: T-511
canonical: ../handbook/spring/security-filter-chain.md
last_updated: 2026-08-06
---

# Flashcards: Spring Security Filter Chain

**Canonical chapter:** [`syllabus/05-spring/security-filter-chain.md`](../syllabus/05-spring/security-filter-chain.md)

## Card: 401 vs 403

**Prompt:**
What's the difference between a 401 and a 403?

**Answer:**
401 = authentication failed (who are you?); 403 = authentication succeeded but authorization failed (you're known, but not allowed).

**Why it matters:**
The precise, testable distinction between two sequential security gates.

**Common trap:**
Treating both as generic "access denied" responses with no distinction.

**Related:**
[Core Concepts](../syllabus/05-spring/security-filter-chain.md#core-concepts)

## Card: Filters can short-circuit

**Prompt:**
Can a filter chain short-circuit before reaching the controller?

**Answer:**
Yes — any filter can return a response directly instead of calling the next filter.

**Why it matters:**
The mechanism that makes cheap, early rejection possible.

**Common trap:**
Assuming every request always reaches the controller regardless of filter outcomes.

**Related:**
[Definition and Purpose](../syllabus/05-spring/security-filter-chain.md#definition-and-purpose)

## Card: Why CORS/CSRF run first

**Prompt:**
Why do CORS/CSRF checks typically run before authentication?

**Answer:**
They're cheaper, more decisive rejections — reject early before spending effort on the more expensive authentication check.

**Why it matters:**
The general cost/decisiveness ordering principle, applicable to any new cross-cutting concern.

**Common trap:**
Reciting a memorized filter order with no underlying reasoning principle.

**Related:**
[Staff-Level Discussion](../syllabus/05-spring/security-filter-chain.md#interview-answer-framework)
