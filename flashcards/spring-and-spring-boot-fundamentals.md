---
title: "Flashcards: Spring and Spring Boot Fundamentals"
slug: spring-and-spring-boot-fundamentals
document_type: flashcard-deck
domain: 05-spring
topic_id: T-2213
canonical: ../syllabus/05-spring/spring-and-spring-boot-fundamentals.md
last_updated: 2026-09-26
---

# Flashcards: Spring and Spring Boot Fundamentals

**Canonical chapter:** [`syllabus/05-spring/spring-and-spring-boot-fundamentals.md`](../syllabus/05-spring/spring-and-spring-boot-fundamentals.md)

## Card: IoC vs. DI

**Prompt:**
Are Inversion of Control (IoC) and Dependency Injection (DI) the same thing?

**Answer:**
No — IoC is the broader principle (control of object creation and wiring is handed to a container instead of your own code). DI is Spring's specific mechanism for implementing that principle: a class declares what it needs, and the container builds and injects it.

**Why it matters:**
The single most commonly conflated pair in this domain — a candidate who treats them as interchangeable misses the question entirely.

**Common trap:**
Answering "what is IoC" with a definition of DI, with no distinction between the two at all.

**Related:**
[Foundation](../syllabus/05-spring/spring-and-spring-boot-fundamentals.md#3-foundation-l1)

## Card: All six bean scopes

**Prompt:**
Name all of Spring's bean scopes, not just the two most commonly cited.

**Answer:**
`singleton` (default, one shared instance), `prototype` (a new instance every request from the container), `request`, `session`, `application`, and `websocket` (the last four require a web context).

**Why it matters:**
Most candidates stop at singleton/prototype — naming all six, unprompted, is a real differentiator.

**Common trap:**
Requesting a `request`- or `session`-scoped bean outside an active HTTP request/session throws `BeanCreationException` — there's nothing for the container to scope it to.

**Related:**
[Core Concepts](../syllabus/05-spring/spring-and-spring-boot-fundamentals.md#4-core-concepts-l2)

## Card: The one stereotype with real extra behavior

**Prompt:**
Of `@Component`, `@Service`, `@Repository`, and `@Controller`, which one actually changes runtime behavior versus just naming a layer?

**Answer:**
Only `@Repository` — it enables persistence exception translation, converting a technology-specific exception (e.g., a JDBC `SQLException`) into one of Spring's own unchecked `DataAccessException` subtypes. `@Service` and `@Controller` add no behavior beyond `@Component` itself; their value is documentation and convention only.

**Why it matters:**
A genuinely surprising fact most candidates haven't considered, and a real differentiator when stated confidently.

**Common trap:**
Claiming `@Service` has enforced behavioral differences from `@Component` — it doesn't.

**Related:**
[Interview Questions](../syllabus/05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions) (Question 8)

## Card: `@Autowired`'s real resolution order

**Prompt:**
When Spring resolves an `@Autowired` dependency with multiple candidate beans, what's the actual order it checks?

**Answer:**
By type first. If multiple beans of that type exist, by `@Qualifier` name if present at the injection point, then by `@Primary` if one candidate is marked as the default. If neither disambiguates it, Spring fails to start with `NoUniqueBeanDefinitionException` — never a silent, arbitrary pick.

**Why it matters:**
Tests whether "how does `@Autowired` work" is understood as a real, ordered resolution process or just "it injects the thing."

**Common trap:**
Assuming Spring picks "the first bean found" silently when the type is ambiguous — it fails loudly at startup instead.

**Related:**
[Interview Questions](../syllabus/05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions) (Question 10)

## Card: What `@Profile` actually gates

**Prompt:**
Does `@Profile` only affect which configuration values get loaded?

**Answer:**
No — `@Profile` gates two things at once: which *beans* get created, and which config values get loaded. A `@Profile("prod")`-annotated bean is simply never registered in the application context unless the `prod` profile is active.

**Why it matters:**
The standard mechanism for swapping an entire implementation per environment (a real payment gateway vs. a sandbox one), not just for tweaking property values.

**Common trap:**
Believing profiles are purely a property-file mechanism, with no awareness they can gate bean existence itself.

**Related:**
[Interview Questions](../syllabus/05-spring/spring-and-spring-boot-fundamentals.md#15-interview-questions) (Question 19)
