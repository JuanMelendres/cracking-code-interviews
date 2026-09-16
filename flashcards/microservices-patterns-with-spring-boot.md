---
title: "Flashcards: Microservices Patterns with Spring Boot"
slug: microservices-patterns-with-spring-boot
document_type: flashcard-deck
domain: 05-spring
topic_id: T-519
canonical: ../syllabus/05-spring/microservices-patterns-with-spring-boot.md
last_updated: 2026-09-16
---

# Flashcards: Microservices Patterns with Spring Boot

**Canonical chapter:** [`syllabus/05-spring/microservices-patterns-with-spring-boot.md`](../syllabus/05-spring/microservices-patterns-with-spring-boot.md)

## Card: Does a default circuit breaker catch a slow-but-successful downstream?

**Prompt:**
A `CircuitBreakerConfig` sets only `failureRateThreshold(50)`. The downstream now takes 5 seconds per call
but still returns 200 every time. Does the breaker open?

**Answer:**
No — verified directly: the same 300ms-slow downstream left a default-configured breaker `CLOSED` through
5 calls, while a second breaker with `slowCallDurationThreshold`/`slowCallRateThreshold` added opened on
the 5th call against the identical server. Without those two settings, a successful-but-slow response is
never counted as a failure.

**Why it matters:**
This is the single most common gap in a real "we have a circuit breaker" claim — it silently misses the
"hangs but doesn't error" failure mode, exactly the kind that exhausts thread/connection pools.

**Common trap:**
Treating "we have a circuit breaker on that call" as a complete resilience answer without stating whether
slow-call detection is configured.

**Related:**
[Microservices Patterns with Spring Boot](../syllabus/05-spring/microservices-patterns-with-spring-boot.md)

## Card: What does a circuit breaker's sliding window actually count?

**Prompt:**
A `CircuitBreaker` is configured with `slidingWindowSize(5)` and `minimumNumberOfCalls(5)`. After 1 healthy
call and 4 failing calls, has it opened?

**Answer:**
Yes, at a 50% (or lower) `failureRateThreshold` — the window already holds all 5 calls (1 success + 4
failures = 80% failure rate), which crosses the threshold on the 4th failing call. The window is a rolling
record of every recorded call, not a counter that resets when a failure streak begins.

**Why it matters:**
A real, executed Resilience4j run confirmed this exact behavior — easy to mis-predict as an off-by-one bug
when it's actually correct.

**Common trap:**
Assuming `minimumNumberOfCalls(N)` means "N *consecutive failures* required" rather than "N total calls
recorded in the window."

**Related:**
[Microservices Patterns with Spring Boot](../syllabus/05-spring/microservices-patterns-with-spring-boot.md)

## Card: `@HttpExchange` vs. Spring Cloud OpenFeign

**Prompt:**
For a brand-new Spring Boot microservice today, why reach for `@HttpExchange` over `@FeignClient`?

**Answer:**
`@HttpExchange` is part of Spring Framework itself (6.0+, in `spring-web`) — no Spring Cloud BOM or extra
dependency, and it's Spring's own current recommendation. OpenFeign isn't deprecated or wrong; it remains
common in existing/older codebases. Choosing Feign fresh today is usually consistency with an existing
pattern, not a technical requirement `@HttpExchange` can't meet.

**Why it matters:**
Interviewers probe for an honest trade-off here, not a claim that one library is broken.

**Common trap:**
Declaring Feign obsolete or wrong instead of stating the real first-party-vs-ecosystem trade-off.

**Related:**
[Microservices Patterns with Spring Boot](../syllabus/05-spring/microservices-patterns-with-spring-boot.md)

## Card: Why does a call while the breaker is OPEN never reach the network?

**Prompt:**
What actually happens when `CircuitBreaker.decorateSupplier(...).get()` is called while the breaker is
`OPEN`?

**Answer:**
The breaker's internal state check (`isCallPermitted()`) rejects the call immediately, throwing a real
`CallNotPermittedException` — the wrapped supplier (and therefore the real downstream call) is never
invoked at all. Verified directly: `server.requestCount()` stays flat across a rejected call while `OPEN`.

**Why it matters:**
This is what "fail fast" concretely means — not a fast-failing HTTP call, but no HTTP call at all.

**Common trap:**
Assuming an `OPEN` breaker still makes the call but just times it out faster.

**Related:**
[Microservices Patterns with Spring Boot](../syllabus/05-spring/microservices-patterns-with-spring-boot.md)
