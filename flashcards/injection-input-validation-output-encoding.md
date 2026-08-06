---
title: "Flashcards: Injection, Input Validation, and Output Encoding"
slug: injection-input-validation-output-encoding
document_type: flashcard-deck
domain: security
topic_id: T-1305
canonical: ../handbook/security/injection-input-validation-output-encoding.md
last_updated: 2026-08-06
---

# Flashcards: Injection, Input Validation, and Output Encoding

**Canonical chapter:** [`handbook/security/injection-input-validation-output-encoding.md`](../handbook/security/injection-input-validation-output-encoding.md)

## Card: Why prepared statements prevent SQL injection

**Prompt:**
Why do prepared statements prevent SQL injection?

**Answer:**
They send the query structure and parameter values as separate protocol messages — the database compiles the structure first and binds values as pure data, never re-parsing them as SQL syntax.

**Why it matters:**
The precise mechanism, not just "prepared statements are safer" as an unexplained rule.

**Common trap:**
Describing prepared statements as safer without explaining the structure/data separation that actually causes the safety.

**Related:**
[handbook/security/injection-input-validation-output-encoding.md](../handbook/security/injection-input-validation-output-encoding.md)

## Card: Is input validation alone sufficient

**Prompt:**
Is input validation alone sufficient to prevent injection?

**Answer:**
No — it's a necessary early filter but not sufficient by itself; output encoding or parameterization at the actual point of use is the defense that matters.

**Why it matters:**
Prevents treating input validation as the complete defense rather than one layer of it.

**Common trap:**
Believing a strong input-validation layer alone closes off injection risk entirely.

**Related:**
[handbook/security/injection-input-validation-output-encoding.md](../handbook/security/injection-input-validation-output-encoding.md)

## Card: Why generic sanitization is weaker than context-specific encoding

**Prompt:**
Why is a generic "sanitize this string" function a weaker model than context-specific output encoding?

**Answer:**
Different rendering contexts (HTML body, HTML attribute, JavaScript, URL) have different special characters and different encoding rules — encoding correct for one context may not be correct, or even present, for another.

**Why it matters:**
Explains why a single sanitize-everywhere function is a common, real source of missed encoding bugs.

**Common trap:**
Applying one generic sanitization function across every output context regardless of its specific encoding rules.

**Related:**
[handbook/security/injection-input-validation-output-encoding.md](../handbook/security/injection-input-validation-output-encoding.md)
