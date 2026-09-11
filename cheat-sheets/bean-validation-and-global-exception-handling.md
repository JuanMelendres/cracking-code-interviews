---
title: "Cheat Sheet: Bean Validation and Global Exception Handling"
slug: bean-validation-and-global-exception-handling
document_type: cheat-sheet
domain: 05-spring
topic_id: T-518
canonical: ../syllabus/05-spring/bean-validation-and-global-exception-handling.md
last_updated: 2026-09-11
---

# Bean Validation and Global Exception Handling

**Canonical chapter:** [`syllabus/05-spring/bean-validation-and-global-exception-handling.md`](../syllabus/05-spring/bean-validation-and-global-exception-handling.md)

## Core Mental Model

Bean Validation is a checkpoint *before* business logic runs, not a check business logic performs itself — an invalid `@Valid` request never reaches the controller method body. A global exception handler is a building's single fire-alarm system, not individual smoke detectors: every controller's exceptions funnel through one small set of handler methods.

## Essential Definitions

- **Field-level constraint** (`@NotBlank`, `@Email`, `@Positive`) — checks one field's value in isolation.
- **Class-level constraint** (custom `@Constraint` + `ConstraintValidator<Annotation, WholeType>`) — the only way to express a rule depending on more than one field at once.
- **`@RestControllerAdvice`** — centralizes exception-to-HTTP-response mapping across every controller.
- **Catch-all `@ExceptionHandler(Exception.class)`** — the last line of defense for exception types nobody anticipated.

## Decision Table

| Situation | Mechanism | Real evidence |
|---|---|---|
| Single-field rule | Field-level annotation | Three independent violations reported together in one `400` |
| Cross-field rule ("X required only if Y") | Custom class-level `@Constraint` | Real `@ValidPayment` constraint firing as an `ObjectError` |
| Known, specific failure | Dedicated `@ExceptionHandler` | Real, specific `404` |
| Unanticipated exception | Catch-all `@ExceptionHandler(Exception.class)` | Real sensitive detail logged server-side, never reaching the client |

## Common Pitfalls

- Believing "I handle every exception type I can think of" removes the need for a catch-all — real evidence in this chapter shows exactly the opposite.
- Returning a caught exception's raw `getMessage()` in the response "for debugging convenience."
- Trying to express a cross-field rule with controller-level `if` logic instead of a proper class-level constraint.

## Interview Answer Skeleton

**30-sec:** `@Valid` + Jakarta annotations check a request DTO before the controller runs; a custom class-level `@Constraint` handles cross-field rules field-level annotations can't express; a `@RestControllerAdvice` catch-all is what real evidence shows prevents a sensitive exception detail from leaking to the client.

**2-min:** Add: `MethodArgumentNotValidException` collects every violation in one pass (proven with three simultaneous field errors); the class-level violation files as an `ObjectError`, not a `FieldError`; the catch-all's value is demonstrated directly — a database connection string with a credential, fully logged server-side, never appearing in the client's `500`.

**Staff-level framing:** The absence of a catch-all handler is a real, standing security gap, not a style preference — a system's default behavior for the unanticipated case is itself a security-relevant design decision.

## Related

- syllabus/05-spring/spring-mvc-fundamentals.md
- syllabus/05-spring/spring-data-jpa-repository-abstraction.md
- syllabus/12-security/owasp-top-10-for-backend-services.md
