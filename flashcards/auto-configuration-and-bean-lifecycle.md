---
title: "Flashcards: Spring Auto-Configuration and Bean Lifecycle"
slug: auto-configuration-and-bean-lifecycle
document_type: flashcard-deck
domain: spring
topic_id: T-501
canonical: ../handbook/spring/auto-configuration-and-bean-lifecycle.md
last_updated: 2026-08-06
---

# Flashcards: Spring Auto-Configuration and Bean Lifecycle

**Canonical chapter:** [`syllabus/05-spring/auto-configuration-and-bean-lifecycle.md`](../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md)

## Card: Correct bean lifecycle order

**Prompt:**
What's the correct bean lifecycle order?

**Answer:**
Constructor → `BeanPostProcessor.before` → `@PostConstruct` → `InitializingBean.afterPropertiesSet()` → custom init-method → `BeanPostProcessor.after`.

**Why it matters:**
The fixed sequence every framework feature (transactions, validation) hooks into at a specific point.

**Common trap:**
Assuming `@PostConstruct` runs before any proxy wrapping occurs.

**Related:**
[Internal Implementation](../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#internal-implementation)

## Card: What creates a @Transactional proxy

**Prompt:**
What mechanism creates a `@Transactional` proxy?

**Answer:**
A `BeanPostProcessor`, running in `postProcessBeforeInitialization` — before `@PostConstruct`.

**Why it matters:**
Explains why the proxy, not the raw bean, is what's injected everywhere else in the container.

**Common trap:**
Assuming the proxy is created lazily at first method call rather than during bean initialization.

**Related:**
[Core Concepts](../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#core-concepts)

## Card: The @Async + @Transactional gotcha

**Prompt:**
Is `@Transactional` broken on an `@Async` method?

**Answer:**
No — the transaction works correctly; the caller just can't see a void method's failure without a `Future`/`CompletableFuture`.

**Why it matters:**
A near-universal real-world Spring trap that looks like a transaction bug but is actually a visibility gap.

**Common trap:**
Concluding the transaction itself is broken rather than identifying the visibility gap.

**Related:**
[Production Scenarios](../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#production-scenarios)
