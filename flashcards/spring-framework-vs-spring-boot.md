---
title: "Flashcards: Spring Framework vs. Spring Boot"
slug: spring-framework-vs-spring-boot
document_type: flashcard-deck
domain: spring
topic_id: T-506 / T-501
canonical: ../handbook/spring/spring-framework-vs-spring-boot.md
last_updated: 2026-09-02
---

# Flashcards: Spring Framework vs. Spring Boot

**Canonical chapter:** [`handbook/spring/spring-framework-vs-spring-boot.md`](../handbook/spring/spring-framework-vs-spring-boot.md)

## Card: What Spring Boot actually is

**Prompt:**
Is Spring Boot a separate framework from Spring?

**Answer:**
No — it's built on top of Spring Framework's own mechanisms (`@Configuration`, `@Bean`, `@Conditional`), adding starters, auto-configuration, and an embedded server.

**Why it matters:**
The single most common shallow-answer trap for this near-universal warm-up question.

**Common trap:**
Describing Boot as competing with or replacing Spring Framework.

**Related:**
[Definition and Purpose](../handbook/spring/spring-framework-vs-spring-boot.md#definition-and-purpose)

## Card: What a starter mechanically does

**Prompt:**
What does adding a Spring Boot starter dependency actually do?

**Answer:**
Changes the classpath by pulling in a version-aligned set of libraries — it doesn't directly configure anything itself; auto-configuration's `@ConditionalOnClass` checks react to that classpath change.

**Why it matters:**
Explains why adding one dependency line changes runtime behavior, not just what compiles.

**Common trap:**
Assuming a starter directly wires up beans, rather than changing what auto-configuration's conditions see.

**Related:**
[Internal Implementation](../handbook/spring/spring-framework-vs-spring-boot.md#internal-implementation)

## Card: How to override an auto-configured bean

**Prompt:**
How do you override a Spring Boot auto-configured bean?

**Answer:**
Define your own bean of the matching type — `@ConditionalOnMissingBean` on the auto-configuration lets your bean win automatically, with no explicit exclusion needed.

**Why it matters:**
The mechanism behind "Boot doesn't need to be disabled, just told what you want instead."

**Common trap:**
Reaching for `@SpringBootApplication(exclude = ...)` reflexively when defining a competing bean is usually simpler.

**Related:**
[Core Concepts](../handbook/spring/spring-framework-vs-spring-boot.md#core-concepts)
