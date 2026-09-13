---
title: "Flashcards: LLM API Integration Fundamentals"
slug: llm-api-integration-fundamentals
document_type: flashcard-deck
domain: 22-ai-llm-engineering
topic_id: T-2300
canonical: ../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md
last_updated: 2026-09-12
---

# Flashcards: LLM API Integration Fundamentals

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md`](../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md)

## Card: Why cost grows with conversation length

**Prompt:**
Why does a 10-turn chat conversation cost more per turn than a 2-turn one, even if each new user message is the same short length?

**Answer:**
The API has no server-side memory — every request resends the *entire* conversation so far. Turn 10 re-bills every token from turns 1–9 plus the new message, so input token count (and cost) grows with every turn.

**Why it matters:**
The single most consequential fact for budgeting an LLM-backed feature — no equivalent exists in typical stateless REST resource lookups.

**Common trap:**
Assuming cost scales only with the new message's length, not the whole conversation's.

**Related:**
[LLM API Integration Fundamentals](../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md)

## Card: What tool_use actually means

**Prompt:**
When a model's response has `stop_reason: "tool_use"`, has the function already run?

**Answer:**
No — the model only *requested* the call. Your backend must execute the real function itself and send the result back as a `tool_result` message in a second request, continuing the same conversation.

**Why it matters:**
A common misconception that treats the model as if it has real execution capability.

**Common trap:**
Assuming the model executed the tool and treating its response as the tool's real output.

**Related:**
[LLM API Integration Fundamentals](../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md)

## Card: Handling a real 429

**Prompt:**
An LLM API call returns `429` with `retry-after: 1s`. What's the correct client behavior?

**Answer:**
Back off for at least the stated `retry-after` duration before retrying — real evidence shows a request failing twice with `429` and succeeding on the third attempt once the required backoff actually elapsed.

**Why it matters:**
Retrying immediately or with a hardcoded delay ignores real, provider-stated guidance and can worsen rate-limit pressure.

**Common trap:**
Using a fixed retry delay instead of reading and honoring the real `retry-after` header value.

**Related:**
[LLM API Integration Fundamentals](../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md)
