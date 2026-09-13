---
title: "Cheat Sheet: LLM API Integration Fundamentals"
slug: llm-api-integration-fundamentals
document_type: cheat-sheet
domain: 22-ai-llm-engineering
topic_id: T-2300
canonical: ../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md
last_updated: 2026-09-12
---

# LLM API Integration Fundamentals

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md`](../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md)

## Core Mental Model

An LLM API has no server-side conversation state — every request resends the *entire* conversation so far, because the model has no memory between calls. If your backend wants multi-turn chat, your backend is what remembers it.

## Essential Definitions

- **Token** — the unit the model reads/writes in (~4 characters); every provider bills input and output tokens separately.
- **Streaming** — the reply arrives as incremental events (Server-Sent Events) over one connection instead of blocking until fully generated.
- **Tool/function calling** — the model can *request* a function call (`stop_reason: "tool_use"`); it never executes anything itself.
- **`usage`** — the response field reporting exact token counts consumed, the real basis for cost tracking.

## Decision Table

| Need | Mechanism |
|---|---|
| Multi-turn conversation | Resend every prior message each call — no server memory |
| Fast perceived latency for long replies | Streaming (SSE): `message_start` → `content_block_delta`* → `message_stop` |
| Let the model call real functions | Declare `tools`; on `tool_use`, run the function yourself, send `tool_result` back |
| Handle rate limiting correctly | Read the real `retry-after` header on a `429`, back off at least that long |
| Track real cost | Read the response's own `usage` block — never estimate from character/word count |

## Common Pitfalls

- Re-sending a growing conversation without realizing cost grows with every turn, not just the new message.
- Buffering an entire streaming response before showing anything — defeats streaming's whole purpose.
- Treating a `tool_use` response as if the model already ran the function — it only requested the call.
- Retrying a `429` immediately or with a hardcoded delay instead of honoring the real `retry-after` header.

## Interview Answer Skeleton

**30-sec:** LLM APIs are stateless — every call resends the full conversation. Streaming (SSE) improves perceived latency; tool calling lets the model request (not execute) function calls; `usage` reports real token cost per call.

**2-min:** Add: a growing conversation's cost grows with every turn since each turn re-bills all prior tokens — the single most consequential cost fact. A real demo proves the two-round tool-calling protocol (model requests a call, backend executes it, sends `tool_result` back) and a real `429`-then-backoff-then-success sequence honoring `retry-after`.

**Staff-level framing:** Cost isn't a fixed per-request number for a chat feature — it scales with conversation length, which changes how you reason about pricing a multi-turn product versus a single-shot one.

## Related

- syllabus/22-ai-llm-engineering/rag-and-vector-databases.md
- syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md
- syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md
