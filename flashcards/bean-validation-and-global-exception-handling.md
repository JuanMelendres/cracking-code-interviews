---
title: "Flashcards: Bean Validation and Global Exception Handling"
slug: bean-validation-and-global-exception-handling
document_type: flashcard-deck
domain: 05-spring
topic_id: T-518
canonical: ../syllabus/05-spring/bean-validation-and-global-exception-handling.md
last_updated: 2026-09-11
---

# Flashcards: Bean Validation and Global Exception Handling

**Canonical chapter:** [`syllabus/05-spring/bean-validation-and-global-exception-handling.md`](../syllabus/05-spring/bean-validation-and-global-exception-handling.md)

## Card: Field-level vs. class-level validation

**Prompt:**
Can a field-level annotation like `@NotBlank` express "cardNumber is required only when paymentMethod is CREDIT_CARD"?

**Answer:**
No — a field-level annotation only sees its own field's value. This rule requires a custom, class-level `@Constraint` whose `ConstraintValidator` receives the entire object.

**Why it matters:**
A near-daily real requirement (conditional/cross-field rules) built-in annotations structurally cannot express.

**Common trap:**
Trying to express a cross-field rule with clever combinations of field-level annotations, or ad hoc controller logic.

**Related:**
[Bean Validation and Global Exception Handling](../syllabus/05-spring/bean-validation-and-global-exception-handling.md)

## Card: Why a catch-all handler is a security control

**Prompt:**
If every exception type your code currently throws has its own specific `@ExceptionHandler`, do you still need a catch-all `@ExceptionHandler(Exception.class)`?

**Answer:**
Yes — an unanticipated exception's raw message (in this chapter's demo, a database connection string with a credential) needs a catch-all to keep it out of the client response; it's fully logged server-side regardless, but only reaches the client if nothing intercepts it first.

**Why it matters:**
A real, demonstrated cause of credential and internal-detail leaks in production APIs, not a hypothetical risk.

**Common trap:**
Believing "I've handled every exception type I can think of" is equivalent to "I've handled every exception type that will ever occur."

**Related:**
[Bean Validation and Global Exception Handling](../syllabus/05-spring/bean-validation-and-global-exception-handling.md)

## Card: Where a class-level violation is filed

**Prompt:**
When a custom class-level constraint fails, does it appear as a `FieldError` or something else?

**Answer:**
An `ObjectError` (filed under `_object` in this chapter's demo) — it belongs to the whole object, not any single field, since no single field's annotation could have detected it.

**Why it matters:**
A concrete detail that separates a candidate who's actually implemented a custom constraint from one reciting the concept abstractly.

**Common trap:**
Assuming every validation error is a `FieldError`.

**Related:**
[Bean Validation and Global Exception Handling](../syllabus/05-spring/bean-validation-and-global-exception-handling.md)
