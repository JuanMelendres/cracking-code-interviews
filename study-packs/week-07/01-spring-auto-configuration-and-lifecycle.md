---
title: "T-506/T-501 · Spring Auto-Configuration and Bean Lifecycle"
topic_id: T-506/T-501
domain: Spring
tier: Advanced
iwi: 7.30
prerequisites: [T-503]
unlocks: [T-511]
week: 7
last_reviewed: 2026-07-30
canonical: ../../handbook/spring/auto-configuration-and-bean-lifecycle.md
---

# T-506 / T-501 · Spring Auto-Configuration and Bean Lifecycle

**IWI 7.30 · Advanced tier · Prerequisite:** T-503 (Week 3) — auto-configuration and proxying are two different mechanisms that interact

**Canonical chapter:** [Spring Auto-Configuration and Bean Lifecycle](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md). This file is the Week 7 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `09-week-7-checklist.md` cites §1–4 directly.

**Verification note:** the lifecycle order and the `@Async`+`@Transactional` behavior behind this summary are real, executed Spring Framework 6.1.14 output. Source: `practice/java/week-07/spring-internals/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Bean lifecycle order, observed](#3-bean-lifecycle-order-observed)
4. [Auto-configuration internals](#4-auto-configuration-internals)
5. [The `@Async` + `@Transactional` gotcha, reproduced](#5-the-async--transactional-gotcha-reproduced)
6. [Trade-offs](#6-trade-offs)
7. [Interview questions](#7-interview-questions)
8. [Common mistakes](#8-common-mistakes)
9. [Staff-level discussion](#9-staff-level-discussion)
10. [Summary](#10-summary)
11. [Key Takeaways](#11-key-takeaways)
12. [Cheat Sheet](#12-cheat-sheet)
13. [Flashcards](#13-flashcards)
14. [Practice Exercises](#14-practice-exercises)
15. [Additional Reading](#15-additional-reading)
16. [Official References](#16-official-references)

---

## 1. The concept

Every Spring bean passes through a fixed sequence of lifecycle callbacks, mirrored on shutdown. Auto-configuration is a separate mechanism layered on top: conditionally-applied `@Configuration` classes Spring Boot evaluates based on the classpath and what the application already defined. → [Definition and Purpose](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#definition-and-purpose).

## 2. Why it exists

Different integration points need to hook in at different, precise moments — a `BeanPostProcessor` must run before a bean's own init to wrap it (how `@Transactional` proxies get created); `@PostConstruct` must run after injection completes. Auto-configuration eliminates boilerplate while staying overridable. → [Definition and Purpose](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#definition-and-purpose).

## 3. Bean lifecycle order, observed

Measured: constructor → `BeanPostProcessor.before` → `@PostConstruct` → `InitializingBean.afterPropertiesSet()` → custom init-method → `BeanPostProcessor.after`. The `@Transactional` proxy is created at the `before` step, prior to `@PostConstruct`. → [Internal Implementation](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#internal-implementation) has the full trace.

## 4. Auto-configuration internals

`@ConditionalOnMissingBean` requires auto-configuration to run after application configuration — if the application already defined the bean, the condition is false and the default is silently skipped, letting one bean be overridden without disabling auto-configuration entirely. → [Core Concepts](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#core-concepts).

## 5. The `@Async` + `@Transactional` gotcha, reproduced

Measured: a void `@Async` `@Transactional` method returns to its caller in 12ms with no visible exception, while the transaction correctly rolls back invisibly on the executor thread (row count 0). The transaction is correct; caller visibility is the actual gap. → [Internal Implementation](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#internal-implementation) has the full trace.

## 6. Trade-offs

`@PostConstruct` is simple but Spring-bean-only; `InitializingBean` is explicit but couples to Spring's API; a void `@Async` method has zero failure visibility; `CompletableFuture` restores it but only if the caller actually checks it. → [Trade-offs](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#trade-offs).

## 7. Interview questions

1. Explain why `@Transactional` on an `@Async` method behaves unexpectedly.
2. What does `@ConditionalOnMissingBean` actually guarantee about ordering?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#interview-questions).

## 8. Common mistakes

Believing `@Transactional` "doesn't work" on `@Async` methods; assuming cross-bean `@PostConstruct` ordering is guaranteed; assuming overriding auto-configuration requires disabling it entirely. → [Common Mistakes](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#common-mistakes).

## 9. Staff-level discussion

Any mechanism that changes which thread executes code changes what "the caller" can observe — every async boundary needs an explicit answer to "how does the caller find out this failed." → [Staff-Level Discussion](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#interview-answer-framework).

## 10. Summary

Bean lifecycle callbacks fire in a fixed, observable order; auto-configuration runs after application configuration specifically so `@ConditionalOnMissingBean` reliably detects an override. `@Async` + `@Transactional` produces a correct transaction with an invisible failure path — reproduced with real numbers. → [Summary](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#summary).

## 11. Key Takeaways

→ [Key Takeaways](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#key-takeaways).

## 12. Cheat Sheet

→ [Cheat Sheet](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#cheat-sheet).

## 13. Flashcards

→ [Flashcards](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 14. Practice Exercises

→ [Practice Exercises](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#practice-exercises) and [Solutions](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md#solutions). Reproducible demos: `practice/java/week-07/spring-internals/`.

## 15. Additional Reading

- [Spring Framework documentation — Bean Factory](https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html)

## 16. Official References

- [Spring Framework documentation — Task Execution and Scheduling (`@Async`)](https://docs.spring.io/spring-framework/reference/integration/scheduling.html)
- [Spring Boot documentation — Auto-configuration](https://docs.spring.io/spring-boot/reference/using/auto-configuration.html)
