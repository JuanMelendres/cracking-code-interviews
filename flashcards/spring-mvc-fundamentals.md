---
title: "Flashcards: Spring MVC Fundamentals"
slug: spring-mvc-fundamentals
document_type: flashcard-deck
domain: 05-spring
topic_id: T-2203
canonical: ../syllabus/05-spring/spring-mvc-fundamentals.md
last_updated: 2026-09-07
---

# Flashcards: Spring MVC Fundamentals

**Canonical chapter:** [`syllabus/05-spring/spring-mvc-fundamentals.md`](../syllabus/05-spring/spring-mvc-fundamentals.md)

## Card: What dependency injection actually means

**Prompt:**
What does it mean for Spring to "inject" a dependency into a class?

**Answer:**
The class declares what it needs (usually as a constructor parameter) instead of constructing it itself with `new`. Spring builds the object graph and hands the dependency in — the class never knows how its dependency was built.

**Why it matters:**
Spring's central idea, and the mechanism behind everything else in this chapter.

**Common trap:**
Describing DI only in terms of the `@Autowired` annotation, missing the underlying idea.

**Related:**
[Spring MVC Fundamentals](../syllabus/05-spring/spring-mvc-fundamentals.md)

## Card: The real @PathVariable bug

**Prompt:**
Why did `@PathVariable Long id` (no explicit name) produce a real `500 Internal Server Error` in this chapter's own demo?

**Answer:**
Compiled Java bytecode doesn't retain parameter names by default (requires the `-parameters` compiler flag). Without an explicit name, Spring can't determine what to bind the path segment to, producing a genuine `IllegalArgumentException` before the controller method body even runs. Fix: `@PathVariable("id") Long id`.

**Why it matters:**
A real, commonly-hit first-project Spring MVC error, not a hypothetical one — this repository's own demo hit it while being built.

**Common trap:**
Assuming an unnamed `@PathVariable` would just bind `null` rather than genuinely fail the request.

**Related:**
[Spring MVC Fundamentals](../syllabus/05-spring/spring-mvc-fundamentals.md)

## Card: Constructor injection vs. field @Autowired

**Prompt:**
Why prefer constructor injection over field `@Autowired`?

**Answer:**
Constructor injection lists every dependency in one visible place (the constructor signature), lets the class be constructed with plain `new` in a unit test with no Spring context, and allows `final` fields. Field injection hides the dependency count and can't use `final`.

**Why it matters:**
A concrete, multi-reason answer beats "it's best practice" — interviewers probe for the actual reasoning.

**Common trap:**
Giving only one vague reason ("it's cleaner") instead of the concrete testability/visibility/immutability trio.

**Related:**
[Spring MVC Fundamentals](../syllabus/05-spring/spring-mvc-fundamentals.md)

## Card: Controller/service/repository layering

**Prompt:**
What belongs in the controller vs. the service in a Spring MVC app?

**Answer:**
The controller is the only layer that knows HTTP exists — it reads the request and calls the service, with no business logic. The service contains the actual business logic and never mentions HTTP, making it reusable and testable independent of any web request.

**Why it matters:**
"Walk me through your class design" and "why is your controller so thin" are both testing this exact separation.

**Common trap:**
Putting business logic directly in the controller because it's faster to write, making it untestable without a full HTTP stack.

**Related:**
[Spring MVC Fundamentals](../syllabus/05-spring/spring-mvc-fundamentals.md)

## Card: "No qualifying bean" startup failure

**Prompt:**
Your app fails to start after adding a second implementation of an interface your controller depends on. Why?

**Answer:**
Spring can no longer determine which implementation to inject — a genuine ambiguity it refuses to guess through, failing fast at startup with a "no qualifying bean" message rather than silently picking one.

**Why it matters:**
A real, common Mid-level Spring debugging scenario with a specific, nameable cause.

**Common trap:**
Assuming Spring picks the "first" or "most recent" bean rather than failing outright.

**Related:**
[Spring MVC Fundamentals](../syllabus/05-spring/spring-mvc-fundamentals.md)
