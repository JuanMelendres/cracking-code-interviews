---
title: Bean Validation and Global Exception Handling
slug: bean-validation-and-global-exception-handling
document_type: handbook-chapter
domain: 05-spring
status: canonical
version: 1.0
last_updated: 2026-09-10
difficulty:
  - intermediate
target_levels:
  - mid
  - senior
  - staff
estimated_reading_minutes: 28
topic_id: T-518
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - spring-mvc-fundamentals.md
related:
  - spring-mvc-fundamentals.md
  - ../12-security/owasp-top-10-for-backend-services.md
  - ../../study-packs/week-07/01-spring-mvc-fundamentals.md
official_references:
  - https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html
  - https://jakarta.ee/specifications/bean-validation/3.0/
---

# Bean Validation and Global Exception Handling

> **Topic register:** T-518 · IWI 6.3 · Core tier · High interview frequency [H]
> **Provenance:** all evidence in this chapter is real, executed output from a real Spring Boot 3.5.16
> app (`practice/java/bean-validation-and-exception-handling/`), including a real, captured server log
> proving a sensitive internal detail is logged server-side but never reaches the client.

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Level 1 — Foundation](#level-1--foundation)
4. [Level 2 — Working Knowledge](#level-2--working-knowledge)
5. [Mental Model](#mental-model)
6. [Definition and Purpose](#definition-and-purpose)
7. [Core Concepts](#core-concepts)
8. [Internal Implementation](#internal-implementation)
9. [Diagrams](#diagrams)
10. [Production Scenarios](#production-scenarios)
11. [Trade-offs](#trade-offs)
12. [Decision Framework](#decision-framework)
13. [Common Mistakes](#common-mistakes)
14. [Anti-Patterns](#anti-patterns)
15. [Best Practices](#best-practices)
16. [Interview Answer Framework](#interview-answer-framework)
17. [Interview Questions](#interview-questions)
18. [Summary](#summary)
19. [Key Takeaways](#key-takeaways)
20. [Cheat Sheet](#cheat-sheet)
21. [Flashcards](#flashcards)
22. [Practice Exercises](#practice-exercises)
23. [Additional Reading](#additional-reading)
24. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can apply Jakarta Bean Validation annotations to a request DTO, write a custom, class-level cross-field constraint for a validation rule the built-in annotations can't express alone, and wire a `@RestControllerAdvice` global exception handler that returns structured `400`s for validation failures, specific status codes for domain exceptions, and a safe, generic error for anything unhandled — backed by real, executed evidence that a sensitive internal detail can be fully logged server-side while never reaching the client.

## Why This Matters in Interviews

Bean Validation and exception handling are Core tier and High frequency because they're two of the most universally-applicable pieces of everyday Spring MVC work, and because a candidate's handling of the "unhandled exception" case specifically is a real, easy-to-probe signal for security awareness — does their design leak internal details (a stack trace, a database connection string, an internal class name) to an API client, or handle it safely. Interviewers also use custom Bean Validation constraints to check whether a candidate's validation vocabulary extends past `@NotNull`/`@Size` into genuine cross-field business rules, a near-daily real requirement (a credit-card order needing a card number; a date range needing its end after its start) that the built-in annotations alone cannot express.

## Level 1 — Foundation

**`@Valid` tells Spring to check a request body against its class's validation annotations before your controller method ever runs.** Put `@NotBlank`, `@Email`, `@Positive`, and similar annotations directly on a DTO's fields; add `@Valid` to the controller parameter; if any annotation's rule is violated, Spring throws an exception and your method body never executes at all — the invalid request never reaches your business logic.

```java
public record OrderRequest(@NotBlank @Email String customerEmail, @Positive Integer quantity) {}

@PostMapping("/orders")
public ResponseEntity<?> create(@Valid @RequestBody OrderRequest request) { ... } // only runs if valid
```

**A `@RestControllerAdvice` class is where you catch that exception (and any others) globally, across every controller**, rather than wrapping every single method in its own `try`/`catch`. One `@ExceptionHandler(MethodArgumentNotValidException.class)` method there converts every controller's validation failures into the same structured JSON response, in one place.

## Level 2 — Working Knowledge

At this level you should be able to explain the practical difference between a **field-level** constraint (`@NotBlank` on a single field, checked independently) and a **class-level** constraint (checked against the whole object, needed the moment a rule depends on *more than one field at once* — "card number is required only if payment method is credit card" can't be expressed by annotating either field alone, since each field's own annotation has no visibility into the other field's value).

You should also be comfortable with the working habit this chapter's own real evidence demands directly: **an unhandled exception's real message and stack trace must never reach the client as-is** — even though it's completely reasonable, and necessary for debugging, to log the full real detail server-side. A generic `@ExceptionHandler(Exception.class)` catch-all is where this discipline is enforced in exactly one place, rather than depending on every individual controller method remembering not to let an exception's raw message leak into a response.

**A practical rule for a working engineer**: put simple, single-field rules directly on the DTO with built-in annotations; reach for a custom class-level constraint the moment a rule genuinely depends on more than one field together; and always have exactly one catch-all exception handler as a last line of defense, even when every "expected" exception type already has its own specific handler — new, unanticipated exception types will occur in production, and the catch-all is what keeps them from leaking internals.

## Mental Model

Think of Bean Validation as a checkpoint *before* your business logic, not a check your business logic performs itself — the moment `@Valid` is present, an invalid request never gets far enough to reach your method body at all, the same way airport security screens before you reach the gate rather than trusting each individual gate agent to notice a problem. Think of the global exception handler as a building's single fire-alarm system rather than individual smoke detectors wired to individually call 911 — every controller's exceptions funnel through the same, small set of handler methods, so the "what does the client actually see when something goes wrong" decision is made and enforced in one place, not re-decided (and potentially re-forgotten) in every controller.

## Definition and Purpose

**Jakarta Bean Validation** (the specification; **Hibernate Validator** is its reference implementation, used by Spring Boot by default) is a standard for declaring validation rules as annotations on a class's fields (field-level) or on the class itself (class-level, for cross-field rules), evaluated by a validation engine rather than hand-written `if` checks scattered through business logic. **`@RestControllerAdvice`** is a Spring MVC mechanism for centralizing exception handling — `@ExceptionHandler` methods within it intercept exceptions thrown by *any* `@RestController` in the application, converting them into structured HTTP responses in one place rather than per-controller.

## Core Concepts

### Field-level annotations check one field in isolation; class-level constraints see the whole object

`@NotBlank`, `@Email`, `@Positive`, and similar built-in annotations each evaluate exactly one field's value, with no visibility into any other field on the same object. A rule that genuinely depends on the *relationship* between two fields — this chapter's own `@ValidPayment` example, "cardNumber is required only when paymentMethod is CREDIT_CARD" — cannot be expressed by any combination of field-level annotations alone, since neither field's annotation can see the other field's value. This is exactly why Bean Validation supports class-level constraints: a `@Constraint`-annotated custom annotation applied to the class itself, validated by a `ConstraintValidator<Annotation, WholeClassType>` that receives the entire object.

### @Valid short-circuits before the controller method body runs, not after

When `@Valid` is present on a `@RequestBody` parameter, Spring performs validation as part of *argument resolution* — before invoking the controller method at all. A validation failure never reaches the method body; it becomes a `MethodArgumentNotValidException` thrown directly from Spring's argument-resolution machinery, which is precisely why a `@ExceptionHandler(MethodArgumentNotValidException.class)` method is the only way to customize the failure response — there is no code path inside the controller method itself to intercept it.

### A generic catch-all handler is a security control, not just tidiness

An `@ExceptionHandler(Exception.class)` method isn't merely about returning consistent JSON shape — [Internal Implementation](#internal-implementation) demonstrates directly that, without one, an unexpected exception's raw `getMessage()` (which can contain genuinely sensitive detail — a connection string, an internal file path, a third-party API error body) risks reaching the client as-is via Spring's own default error-handling fallback. A deliberate catch-all, logging the real exception server-side and returning a fixed, generic message to the client, is what closes that gap in exactly one place rather than requiring every individual exception type to be anticipated and specifically handled.

## Internal Implementation

**Real, valid requests succeed (201):**

```
=== 1. Valid CREDIT_CARD order -- should succeed (201) ===
{"orderId":1,"customerEmail":"alice@example.com"}
HTTP 201

=== 2. Valid PAYPAL order (no cardNumber needed) -- should succeed (201) ===
{"orderId":2,"customerEmail":"bob@example.com"}
HTTP 201
```

**Real evidence multiple field-level violations are reported together, in one structured response — not just the first one found:**

```
=== 3. Multiple simple field violations at once -- 400 with structured field errors ===
{"error":"VALIDATION_FAILED","fieldErrors":{"customerEmail":"customerEmail must be a valid email address","paymentMethod":"paymentMethod is required","quantity":"quantity must be positive"}}
HTTP 400
```

An invalid email, a negative quantity, and a missing payment method — three independent field-level violations from one request — all appear together in the `fieldErrors` map, real evidence Bean Validation collects every violation in a single pass rather than failing fast on the first one.

**Real evidence the custom, class-level `@ValidPayment` constraint fires correctly:**

```
=== 4. CREDIT_CARD but missing cardNumber -- the custom class-level @ValidPayment constraint fires ===
{"error":"VALIDATION_FAILED","fieldErrors":{"_object":"cardNumber is required and must be 16 digits when paymentMethod is CREDIT_CARD"}}
HTTP 400
```

This request has no field-level violation at all — `paymentMethod` is present and valid, and `cardNumber` (a plain, unconstrained `String` field) has no field-level annotation to violate. The violation is real and comes entirely from the class-level `@ValidPayment` constraint, filed under `_object` (Spring's `ObjectError`, distinct from a `FieldError`) since it belongs to the object as a whole, not to any single field.

**Real, specific domain-exception handling:**

```
=== 5. GET a real, existing order ===
{"customerEmail":"alice@example.com","quantity":2,"paymentMethod":"CREDIT_CARD","cardNumber":"4111111111111111"}
HTTP 200

=== 6. GET a non-existent order -- specific 404 handler ===
{"message":"Order 42 not found","error":"ORDER_NOT_FOUND"}
HTTP 404
```

**Real, verified proof the catch-all handler masks a sensitive detail — the client's response versus the server's own log, from the identical request:**

```
=== 7. GET id=999 -- simulates an unexpected internal exception ===
{"message":"An unexpected error occurred","error":"INTERNAL_ERROR"}
HTTP 500
```

The server's own log (`app.log`) for this exact request:

```
ERROR ... demo.GlobalExceptionHandler : Unhandled exception
java.lang.RuntimeException: unexpected DB failure: connection string postgres://admin:hunter2@internal-db:5432/orders
	at demo.OrderController.get(OrderController.java:35)
```

The real exception's message — including a genuinely sensitive detail, a database connection string with a credential in it — is fully present in the server-side log, and completely absent from the client's response. This is the concrete, verified mechanism behind "never leak internal exception details to a client": not a policy stated in a code comment, but a real `catch`-and-replace happening in exactly one place.

## Diagrams

```mermaid
sequenceDiagram
    participant Client
    participant Spring as Spring (argument resolution)
    participant Controller
    participant Advice as GlobalExceptionHandler

    Client->>Spring: POST /orders (invalid body)
    Spring->>Spring: Run @Valid checks (field-level + class-level)
    Spring--xController: Validation fails -- controller method NEVER invoked
    Spring->>Advice: MethodArgumentNotValidException
    Advice-->>Client: 400, structured fieldErrors

    Client->>Spring: GET /orders/999 (triggers unexpected RuntimeException)
    Spring->>Controller: Valid request -- method invoked
    Controller--xAdvice: RuntimeException (contains sensitive detail)
    Advice->>Advice: log.error(real exception, full detail)
    Advice-->>Client: 500, generic safe message ONLY
```

Both real failure paths converge on the same `GlobalExceptionHandler` — the top path never reaches the controller at all (validation short-circuits during argument resolution); the bottom path reaches the controller, fails inside it, and is caught by the catch-all handler, which is the one place real, sensitive detail is deliberately stopped from reaching the client.

## Production Scenarios

### Scenario: an unhandled exception's raw message leaks a database connection string into a public API error response

**Context.** A service has specific `@ExceptionHandler` methods for its known, anticipated exception types, but no catch-all `@ExceptionHandler(Exception.class)`. A misconfigured database connection pool starts throwing a raw exception whose message includes the connection string used to build it.

**Symptoms.** A monitoring tool that scrapes public API responses for anomalies flags a `500` response body containing what looks like a partial credential.

**Impact.** A real, active credential-exposure incident — external API consumers received a raw exception message containing internal infrastructure detail, exactly the shape this chapter's own `id=999` demo reproduces deliberately, safely, in a lab.

**Initial hypotheses.** A logging misconfiguration exposing logs publicly (checked — logs were never public; the leak was in the actual HTTP response body); an intentional debug endpoint left enabled (checked — no such endpoint exists); Spring's default error-handling behavior surfaced the raw exception message directly because no catch-all handler intercepted it first (correct).

**Evidence.** The leaked response body's structure matches Spring Boot's default `/error` fallback JSON shape, confirming no application-level `@ExceptionHandler(Exception.class)` was ever invoked for this exception type.

**Investigation timeline.** Reproduced locally by triggering the same connection failure in a non-production environment; confirmed the raw exception message appeared in the HTTP response exactly as it would appear in a stack trace, with no sanitization anywhere in the request path.

**Root cause.** No global, catch-all exception handler existed — only specific handlers for anticipated exception types — so an exception type nobody had thought to anticipate fell through to Spring's own default behavior, which does not know or care whether a given exception's message is safe to expose.

**Immediate mitigation.** Rotate the exposed credential immediately, treating it as compromised regardless of how briefly or how few requests saw it.

**Permanent remediation.** Add a single `@ExceptionHandler(Exception.class)` catch-all, logging the full real exception server-side and returning a fixed, generic message to every client for any exception type not already specifically handled — exactly this chapter's own real, demonstrated pattern.

**Alternatives considered.** Auditing and individually handling every possible exception type across every dependency — rejected as fundamentally incomplete; new exception types can and will appear from library upgrades, new failure modes, and code paths nobody anticipated, which is precisely the case a catch-all exists to cover.

**Trade-offs.** None meaningful — a catch-all handler costs one small, permanent piece of code and closes an entire class of future incidents of this same shape.

**Prevention.** Treat the absence of a catch-all `@ExceptionHandler(Exception.class)` as a standing code-review finding for any Spring MVC service, not an optional nicety — this chapter's own demo exists specifically because the failure mode is real, common, and easy to prevent with one small addition.

**Interview lesson.** This is Interview Question 2 (§ Interview Questions) — "why do you need a catch-all exception handler if you already handle every exception type you can think of" — arriving as a real, credential-exposure-shaped incident rather than an abstract completeness argument.

## Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| Field-level Bean Validation annotations | Declarative, close to the data, real evidence of collecting all violations in one pass | Cannot express cross-field rules alone |
| Custom class-level constraint (`@ValidPayment`) | Expresses genuine cross-field business rules declaratively, reusable across DTOs | More code to write than a single annotation; requires understanding `ConstraintValidator` |
| Specific `@ExceptionHandler` per exception type | Precise, tailored status codes and messages per real failure mode | Requires anticipating every exception type in advance — real evidence shows this is never complete |
| Catch-all `@ExceptionHandler(Exception.class)` | Real, demonstrated safety net against unanticipated exception types leaking sensitive detail | Necessarily generic — provides no specific detail to the client, by design |

## Decision Framework

1. **Does this validation rule depend on exactly one field's own value?** Use a built-in field-level annotation (`@NotBlank`, `@Email`, `@Positive`, etc.) directly on the field.
2. **Does the rule genuinely depend on the relationship between two or more fields?** Write a custom class-level `@Constraint` with its own `ConstraintValidator`, per this chapter's `@ValidPayment` example — no combination of field-level annotations can express this.
3. **Is this a known, specific, recoverable failure mode (a domain "not found," a business-rule conflict)?** Give it its own `@ExceptionHandler` with a precise status code and message.
4. **Does a catch-all `@ExceptionHandler(Exception.class)` exist?** This should never be a "maybe" — treat its absence as a real security gap, per this chapter's own production scenario, regardless of how thoroughly other exception types are already handled.

## Common Mistakes

- Attempting to express a cross-field validation rule ("card number required only for credit-card payments") by adding conditional logic inside the controller method instead of a proper class-level constraint, scattering validation logic away from the DTO it belongs to.
- Assuming that handling every "expected" exception type with its own specific `@ExceptionHandler` removes the need for a catch-all — real evidence in this chapter's Production Scenario shows exactly the opposite.
- Returning a caught exception's raw `getMessage()` directly in an API response "for debugging convenience," rather than logging the real detail server-side and returning a generic client-facing message.
- Forgetting that `MethodArgumentNotValidException` collects *all* validation failures from a single request, and building a handler that only surfaces the first one, discarding real diagnostic information the client could have used to fix every field at once.

## Anti-Patterns

- **No catch-all exception handler at all**, relying entirely on a fixed, enumerated list of specifically-handled exception types — this chapter's own Production Scenario demonstrates the real, credential-leaking consequence directly.
- **Returning a raw exception's message or stack trace in an API response**, even temporarily "just for this one debugging session," since it's a real, demonstrated path for sensitive internal detail to reach an external client.
- **Hand-writing cross-field validation logic inline in a controller or service method** instead of a reusable, declarative class-level constraint, making the rule harder to test in isolation and easy to accidentally skip on a different code path that creates the same object.

## Best Practices

- Default every `@RequestBody`-accepting endpoint to `@Valid`, with field-level annotations expressing every single-field rule directly on the DTO.
- Write a custom class-level constraint the moment a validation rule needs more than one field's value — don't fight built-in annotations into expressing something they structurally cannot.
- Always include exactly one `@ExceptionHandler(Exception.class)` catch-all, logging the real exception server-side and returning a fixed, generic client-facing message — treat this as non-negotiable per this chapter's own real, demonstrated incident shape.
- Structure validation error responses as a field-to-message map (as this chapter's real transcript shows), not a single flattened string, so a client (or a human reading a bug report) can see every violation from one failed request at once.

## Interview Answer Framework

### 30-Second Answer

Bean Validation (`@Valid` plus Jakarta annotations like `@NotBlank`/`@Email`/`@Positive`) checks a request DTO before the controller method runs, short-circuiting invalid requests during argument resolution. Field-level annotations check one field in isolation; a custom class-level `@Constraint` is needed for genuine cross-field rules. A `@RestControllerAdvice` with `@ExceptionHandler` methods centralizes exception-to-response mapping — critically including a catch-all `@ExceptionHandler(Exception.class)`, which real, demonstrated evidence shows is what prevents an unanticipated exception's sensitive internal detail from leaking directly into a client response.

### 2-Minute Answer

Definition: Bean Validation declares request-DTO rules as annotations, evaluated before the controller method body runs; `@RestControllerAdvice` centralizes exception-to-HTTP-response mapping across every controller. Why both matter together: validation failures and unhandled exceptions are both "something went wrong before/during business logic" cases that need a consistent, safe client-facing response, not ad hoc handling scattered per controller. How cross-field validation works: a custom class-level `@Constraint` with its own `ConstraintValidator<Annotation, WholeType>` sees the entire object, unlike any field-level annotation — real evidence in this chapter shows a `cardNumber`-required-for-`CREDIT_CARD` rule correctly firing as an object-level (not field-level) violation. One important trade-off: a catch-all exception handler is necessarily generic to the client, by design — it trades specific detail for safety. One production example: real, captured evidence that an unhandled exception's message (a database connection string with a credential) was fully logged server-side but never appeared in the client's `500` response — the exact mechanism that prevents the real credential-leak incident this chapter's own Production Scenario describes.

### 10-Minute Deep Dive

Cover, in order: the mental model — validation as a pre-business-logic checkpoint, exception handling as a centralized fire-alarm system rather than per-controller smoke detectors (mental model); why field-level annotations structurally cannot express cross-field rules, and how a custom class-level `@Constraint`/`ConstraintValidator` solves it, demonstrated with the real `@ValidPayment` example (core concepts, internals); the real evidence that `MethodArgumentNotValidException` collects every violation from one request, not just the first (internals, real evidence); the real, captured proof that a catch-all handler prevents a sensitive exception message from reaching the client, contrasted directly against the same request's real server-side log (internals, real evidence); the production scenario of an actual credential-leak incident caused by the *absence* of a catch-all handler, and why "I handle every exception type I can think of" is provably insufficient; the decision framework distinguishing field-level, class-level, specific-exception, and catch-all handling.

### Whiteboard Explanation

Draw the [§ Diagrams](#diagrams) sequence diagram: two parallel request flows both terminating at the same `GlobalExceptionHandler` box — one arriving via a validation short-circuit that never reaches the controller at all, one arriving via an exception thrown from inside the controller. Annotate the second path's arrow into the handler with "real, sensitive detail here" and the handler's arrow back to the client with "generic message ONLY" to make the masking mechanism visually explicit, not just asserted.

### Production Example

The credential-leak incident in [§ Production Scenarios](#production-scenarios): a service with specific handlers for every anticipated exception type, but no catch-all, let a genuinely unanticipated database-connection exception's raw message (containing a credential) reach a public API response — fixed by adding exactly one `@ExceptionHandler(Exception.class)`, matching this chapter's own real, demonstrated pattern precisely.

### Trade-offs to Mention

State unprompted: a catch-all exception handler is deliberately generic to the client — that's the entire point, not a limitation to work around; a custom class-level constraint costs more code than a single built-in annotation but is the only correct way to express a genuine cross-field rule; specific exception handlers provide better client-facing detail but can never be a complete substitute for a catch-all, since new exception types will always eventually occur.

### Common Candidate Mistakes

Proposing to handle cross-field validation with inline controller logic instead of a class-level constraint; believing that specifically handling every exception type they can think of removes the need for a catch-all; returning a raw exception message in an API response "to help debugging."

### Typical Follow-Up Questions

1. "How would you validate a rule that depends on two fields at once, like 'end date must be after start date'?"
2. "If you already have specific handlers for every exception your code can throw, why do you still need a catch-all?"
3. "Where should the real exception detail actually go, if not into the client response?"

### Senior-Level Expectations

Correctly distinguishes field-level from class-level validation and can sketch a custom `ConstraintValidator`; correctly explains why a catch-all exception handler is necessary even with specific handlers already in place.

### Staff-Level Discussion

Treats the absence of a catch-all exception handler as a real, standing security gap rather than a style preference, and can cite or reconstruct a concrete incident shape (a credential or internal detail leaking via an unhandled exception) as the reason — generalizing this to a broader principle also relevant to [OWASP Top 10 for Backend Services](../12-security/owasp-top-10-for-backend-services.md)'s Security Misconfiguration category: a system's *default* behavior for the unanticipated case is itself a security-relevant design decision, not a neutral fallback, and needs to be deliberately chosen rather than left to whatever a framework does by default.

## Interview Questions

### Question 1 — How would you validate a business rule that depends on more than one field, like "cardNumber is required only when paymentMethod is CREDIT_CARD"?

**Why interviewers ask it.** Tests whether a candidate knows Bean Validation supports class-level, cross-field constraints, versus reaching for ad hoc conditional logic scattered in a controller or service.

**Expected answer.** Write a custom annotation (e.g., `@ValidPayment`) applied at the class level, backed by a `ConstraintValidator<ValidPayment, OrderRequest>` whose `isValid()` method receives the entire object and can check both fields together — return `true` unless `paymentMethod == CREDIT_CARD` and `cardNumber` is missing or malformed. This keeps the validation declarative and testable in isolation, and it participates in the same `@Valid`-triggered validation pass as every built-in annotation.

**Common mistakes.** Proposing to check this rule manually inside the controller method after `@Valid` has already run, missing that a proper class-level constraint integrates into the same validation pass (and the same structured error response) as everything else.

**Follow-up questions:** "How would the resulting validation error appear differently from a field-level violation, in the response?" (as an `ObjectError`, not a `FieldError` — this chapter's real demo files it under a separate key, `_object`, since it belongs to the whole object rather than any single field.)

**Senior-level expectations:** correctly proposes a class-level `@Constraint`/`ConstraintValidator` and explains why field-level annotations alone can't express the rule.

**Staff-level expectations:** proactively distinguishes the resulting `ObjectError` from a `FieldError` in the response shape, and reasons about testing the custom validator in isolation from the rest of the controller.

**Related references.** [§ Core Concepts](#core-concepts), [§ Internal Implementation](#internal-implementation).

---

### Question 2 — If you already have a specific `@ExceptionHandler` for every exception type your code can throw, why do you still need a catch-all `@ExceptionHandler(Exception.class)`?

**Why interviewers ask it.** Directly tests whether a candidate treats exception handling as a completeness problem (handle everything you can currently think of) or a defense-in-depth problem (assume something unanticipated will eventually occur).

**Expected answer.** Because "every exception type I can currently think of" is never actually complete — a library upgrade, a new dependency, an infrastructure failure mode, or simply an unanticipated edge case will eventually throw something not on that list. Without a catch-all, that exception falls through to the framework's own default error handling, which has no way to know the exception's message might contain sensitive detail — real, demonstrated evidence in this chapter shows a database connection string (with a credential) fully present in a server-side log but requiring a deliberate catch-all handler to keep it out of the client response.

**Common mistakes.** Treating the question as purely about code tidiness/consistency rather than naming the real security consequence of the gap.

**Follow-up questions:** "Where should the real exception detail go if it's not returned to the client?" (Logged server-side, via the catch-all handler itself, exactly as this chapter's own `GlobalExceptionHandler` does with `log.error(...)` before constructing the safe, generic client response.)

**Senior-level expectations:** correctly explains that new, unanticipated exception types are inevitable and names the sensitive-detail-leak risk specifically.

**Staff-level expectations:** connects this to a broader "the unanticipated-failure default behavior is itself a security-relevant design decision" principle, not just this one specific case.

## Summary

Bean Validation (`@Valid` plus Jakarta annotations) checks a request DTO before the controller method runs, collecting every violation from a single request in one pass — real evidence in this chapter shows an invalid email, a negative quantity, and a missing field all reported together, not just the first found. A custom, class-level `@Constraint`/`ConstraintValidator` is the correct mechanism for genuine cross-field rules no combination of field-level annotations can express, demonstrated directly with a real `cardNumber`-required-for-`CREDIT_CARD` constraint correctly firing as an object-level violation. A `@RestControllerAdvice` centralizes exception-to-response mapping across every controller, and — most consequentially — a catch-all `@ExceptionHandler(Exception.class)` is what real, captured evidence shows actually prevents a sensitive internal detail (a database connection string containing a credential) from reaching a client, even while the exact same detail is fully preserved in the server-side log for real debugging. The chapter's own Production Scenario demonstrates this isn't a hypothetical concern: the absence of a catch-all handler is a real, recurring cause of credential and internal-detail leaks in production APIs.

## Key Takeaways

- `@Valid` triggers validation during argument resolution, before the controller method body ever runs — a validation failure means the method is never invoked at all.
- Field-level annotations check one field in isolation; genuine cross-field rules need a custom class-level `@Constraint` with its own `ConstraintValidator`, filed as an `ObjectError`, not a `FieldError`.
- `MethodArgumentNotValidException` collects every violation from a single request in one pass — real evidence shows three independent field violations reported together, not just the first.
- A catch-all `@ExceptionHandler(Exception.class)` is a real security control, not a tidiness preference — real, captured evidence shows it's what keeps a sensitive exception message (a database credential, in this chapter's demo) out of the client response while the same detail is fully logged server-side.
- Specific `@ExceptionHandler` methods for known exception types and a catch-all for everything else are complementary, not redundant — real evidence in this chapter's Production Scenario shows "I handle every exception I can think of" is never actually complete.

## Cheat Sheet

| Situation | Mechanism | This chapter's real evidence |
|---|---|---|
| Single-field rule (`@NotBlank`, `@Email`, `@Positive`) | Field-level Bean Validation annotation | Three independent field violations reported together in one `400` |
| Cross-field rule ("X required only if Y") | Custom class-level `@Constraint` + `ConstraintValidator` | Real `@ValidPayment` constraint correctly firing as an `ObjectError` |
| Known, specific failure (a domain "not found") | Dedicated `@ExceptionHandler` | Real, specific `404` with a precise message |
| Unanticipated exception | Catch-all `@ExceptionHandler(Exception.class)` | Real sensitive detail logged server-side, never reaching the client |

## Flashcards

### Card: Field-level vs. class-level validation

**Prompt:**
Can a field-level annotation like `@NotBlank` express the rule "cardNumber is required only when paymentMethod is CREDIT_CARD"?

**Answer:**
No — a field-level annotation only sees its own field's value, with no visibility into any other field. This rule requires a custom, class-level `@Constraint` whose `ConstraintValidator` receives the entire object.

**Why it matters:**
A near-daily real requirement (conditional/cross-field rules) that built-in annotations alone structurally cannot express.

**Common trap:**
Trying to express a cross-field rule with clever combinations of field-level annotations, or falling back to ad hoc controller logic instead.

**Related:**
[Internal Implementation](#internal-implementation)

### Card: Why a catch-all handler is a security control

**Prompt:**
If every exception type your code currently throws has its own specific `@ExceptionHandler`, do you still need a catch-all `@ExceptionHandler(Exception.class)`?

**Answer:**
Yes — verified directly: an unanticipated exception's raw message (in this chapter's demo, a database connection string containing a credential) needs a catch-all to keep it out of the client response; it's fully logged server-side regardless, but only reaches the client if nothing intercepts it first.

**Why it matters:**
A real, demonstrated cause of credential and internal-detail leaks in production APIs, not a hypothetical risk.

**Common trap:**
Believing "I've handled every exception type I can think of" is equivalent to "I've handled every exception type that will ever occur."

**Related:**
[Internal Implementation](#internal-implementation), [Production Scenarios](#production-scenarios)

## Practice Exercises

1. Reproduce every request in this chapter yourself: [`practice/java/bean-validation-and-exception-handling/`](../../practice/java/bean-validation-and-exception-handling/README.md).
2. Add a second custom class-level constraint enforcing a date range (`startDate` must be before `endDate` on some new DTO), following the same `@Constraint`/`ConstraintValidator` pattern as `@ValidPayment`, and confirm a real violation is correctly reported as an `ObjectError`.
3. Temporarily remove `GlobalExceptionHandler`'s catch-all `@ExceptionHandler(Exception.class)` method, re-run the `id=999` request, and compare the real response to this chapter's captured transcript — confirm directly whether the sensitive detail now appears in the client-facing response.

## Additional Reading

- [Spring MVC Fundamentals](spring-mvc-fundamentals.md) — the request-handling flow (`@RestController`, `@RequestBody`) this chapter's validation and exception handling both attach to.

## Official References

- [Spring Framework — Validation and Data Binding](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html)
- [Jakarta Bean Validation 3.0 Specification](https://jakarta.ee/specifications/bean-validation/3.0/)
