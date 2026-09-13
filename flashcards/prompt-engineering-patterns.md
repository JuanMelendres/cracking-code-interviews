---
title: "Flashcards: Prompt Engineering Patterns"
slug: prompt-engineering-patterns
document_type: flashcard-deck
domain: 22-ai-llm-engineering
topic_id: T-2303
canonical: ../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md
last_updated: 2026-09-12
---

# Flashcards: Prompt Engineering Patterns

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md`](../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md)

## Card: Wording vs. real parameter

**Prompt:**
Is "please respond only in JSON" in your prompt wording as reliable as setting `response_format=json_object`?

**Answer:**
No — real, measured evidence: wording alone produced 8 real JSON-parse failures out of 20 trials; the real `response_format` parameter produced 0 failures out of 20.

**Why it matters:**
The core distinction this chapter is organized around — some patterns are just careful wording, others are real, distinct API guarantees.

**Common trap:**
Relying on prompt wording alone for output a downstream system parses programmatically.

**Related:**
[Prompt Engineering Patterns](../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md)

## Card: The system role's real structural effect

**Prompt:**
Does putting an instruction in the `system` role versus concatenating it into `user` text make a measurable difference?

**Answer:**
Yes — real evidence: the identical instruction plus an identical adversarial override attempt produces different real outcomes depending on the channel. In the `system` role, the model resists the override; concatenated into `user` text, the same override succeeds.

**Why it matters:**
A real, structural argument for keeping instructions in `system` and untrusted content in `user`, not a stylistic preference.

**Common trap:**
Assuming role placement is purely organizational, with no real effect on the model's behavior.

**Related:**
[Prompt Engineering Patterns](../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md)

## Card: temperature is a real, measurable parameter

**Prompt:**
Is `temperature` a vague "creativity dial," or a real, measurable sampling parameter?

**Answer:**
Real and measurable — 20 trials at `temperature=0.0` produced 1 distinct output; 20 trials at `temperature=0.9` on the identical prompt produced 3 distinct outputs. It's a genuine sampling-randomness control, not a vague setting.

**Why it matters:**
Grounds "temperature controls randomness" in a real, verifiable measurement rather than an assumed behavior.

**Common trap:**
Describing temperature only qualitatively ("makes it more creative") without a concrete, measurable claim.

**Related:**
[Prompt Engineering Patterns](../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md)
