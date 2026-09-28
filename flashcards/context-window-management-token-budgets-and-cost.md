---
title: "Flashcards: Context Window Management, Token Budgets, and Cost"
slug: context-window-management-token-budgets-and-cost
document_type: flashcard-deck
domain: 22-ai-llm-engineering
topic_id: T-2308
canonical: ../syllabus/22-ai-llm-engineering/context-window-management-token-budgets-and-cost.md
last_updated: 2026-09-28
---

# Flashcards: Context Window Management, Token Budgets, and Cost

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/context-window-management-token-budgets-and-cost.md`](../syllabus/22-ai-llm-engineering/context-window-management-token-budgets-and-cost.md)

## Card: What occupies the context window

**Prompt:**
List everything that competes for space in one request's context window.

**Answer:**
System prompt, tool definitions, conversation history, retrieved context, tool results — and the space reserved for the response, which is the one people forget because it is the only part not yet written when the request is assembled.

**Why it matters:**
A request that fits perfectly and then truncates the answer mid-sentence gets misdiagnosed as a model quality problem.

**Common trap:**
Estimating tokens as characters ÷ 4. JSON, code, and non-Latin scripts fragment badly — exactly the payloads that overflow.

**Related:**
[Context Window Management, Token Budgets, and Cost](../syllabus/22-ai-llm-engineering/context-window-management-token-budgets-and-cost.md)

## Card: Is more retrieved context better?

**Prompt:**
You have window to spare. Should you retrieve more chunks?

**Answer:**
Up to a plateau, then no. Precision falls as lower-ranked chunks are added, models attend less reliably to material buried in the middle of a long context, and cost and latency grow with input length. Past that point more context costs more **and** degrades answers. Set `k` by measuring quality across values on an evaluation set.

**Why it matters:**
A diluted context makes the supporting fact harder to locate, so the model fills the gap plausibly — a direct groundedness risk.

**Common trap:**
Treating a large window as a reason not to select, rerank, or compress.

**Related:**
[Hallucination, Groundedness, and Output Guardrails](../syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md)

## Card: Prompt caching and what breaks it

**Prompt:**
How do you order a prompt to benefit from prompt caching, and what silently defeats it?

**Answer:**
Put all invariant material — system prompt, tool definitions, stable reference documents — at the **front**, and everything variable at the end, so the cacheable prefix is byte-identical across requests. A single dynamic value placed early (a current date, a user name) invalidates the prefix on every request and removes the discount with no visible error.

**Why it matters:**
It improves cost *and* time-to-first-token, and costs nothing in quality — the cheapest real optimization available.

**Common trap:**
Investigating a disappointing hit rate anywhere other than the top of the prompt.

**Related:**
[Context Window Management, Token Budgets, and Cost](../syllabus/22-ai-llm-engineering/context-window-management-token-budgets-and-cost.md)

## Card: Truncation done safely

**Prompt:**
Your assembled prompt is over budget. What do you cut, and how?

**Answer:**
Drop **whole units** — a whole document, a whole turn — from the lowest-ranked end, never a cut mid-document. Naive truncation can remove the end of a prompt whose beginning stated rules that no longer appear, or break the structure of content the model was told to imitate. Reserve the output budget first, treat system and tools as fixed, bound history by policy, and fill the remainder in rank order.

**Why it matters:**
Half-included content is worse than excluded content: it costs tokens and contributes confusion.

**Common trap:**
Pinning nothing, so the system prompt itself becomes eligible for truncation on long agent runs.

**Related:**
[Agentic Workflows and Tool Orchestration](../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md)

## Card: Cutting LLM cost without changing models

**Prompt:**
Order the cost levers by return, without switching model.

**Answer:**
Send less (tighter retrieval budget, bounded history, prune the system prompt and tool catalogue — both grow by accretion); reorder for prompt caching; cap output with `max_tokens`, since output tokens usually carry the higher rate; cache whole responses for repeats; route easy requests to a cheaper model. Measure per-segment tokens first so the cuts hit the segment that actually dominates.

**Why it matters:**
A prompt change can double spend overnight with no deploy — as when a retrieval `k` went from 5 to 20 and input tokens tripled.

**Common trap:**
Trimming input when output dominates the bill.

**Related:**
[Cloud Cost and Scaling Economics](../syllabus/15-cloud/cloud-cost-and-scaling-economics.md)
