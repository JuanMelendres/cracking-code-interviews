---
title: "Cheat Sheet: Hallucination, Groundedness, and Output Guardrails"
slug: hallucination-groundedness-and-output-guardrails
document_type: cheat-sheet
domain: 22-ai-llm-engineering
topic_id: T-2307
canonical: ../syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md
last_updated: 2026-09-28
---

# Hallucination, Groundedness, and Output Guardrails

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md`](../syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md)

## Core Mental Model

The model samples likely continuations. Likelihood reflects pattern fit, not truth, and there is no internal "I know this" flag. Fluency and correctness are uncorrelated.

## Definitions

- **Hallucination** — fluent, confident, false. Sub-kinds: fabricated fact, unsupported claim, contradiction of the source, overconfident uncertainty.
- **Grounded** — every claim supported by material actually in the request. Measurable, unlike "true."
- **Guardrail** — anything between output and consumer that can reject, constrain, or flag.

## Layers and What Each Misses

| Layer | Misses |
|---|---|
| Retrieval (RAG) | Everything, when retrieval returns the wrong docs |
| Prompt instructions | Whatever the model ignores — instructions are not constraints |
| Schema / structured output | A well-formed object full of false values |
| Citation + existence check | A real citation on a claim it does not support |
| Groundedness judge | Its own errors — correlated if it is the same model family |
| Deterministic business rules | Anything your rules do not cover |
| Human review | Scale, latency, cost |

**No layer is sufficient.** Schema guarantees *shape* only.

## The Key Diagnostic

**Were the right documents in the prompt?**
No → retrieval failure (chunking, embeddings, query rewriting, reranking).
Yes → generation failure (instructions, model, verification).

Log retrieved doc IDs with every response, or this is unanswerable.

## Cost Ordering (run cheap checks on everything)

| Guardrail | Latency | Cost |
|---|---|---|
| Schema-constrained decoding | ~0 | none |
| Citation-existence check | µs | none |
| Business-rule validation | ms | none |
| NLI per claim | 10s–100s ms | small model |
| LLM-judge groundedness | ~1s | ~doubles request cost |
| Human review | minutes+ | staff time |

## Abstention

Make `insufficient_context` a **required schema status**, and measure it with an eval set containing **unanswerable** questions. Without that, every prompt tweak optimizes toward always answering.

## Common Pitfalls

- "We use RAG, so we're grounded" — retrieval is 1 of 3 steps (retrieve, instruct, verify).
- Schema validity ≠ correctness.
- Same model generating and judging — correlated blind spots.
- Whole-answer groundedness scoring: 4 good claims + 1 fabricated still scores grounded. Score **per claim**.
- Thresholding on token log-probs — that measures fluency.
- Lowering temperature as a correctness fix. It is a consistency control.

## Interview Answer Skeleton

**30-sec:** Hallucination is fluent false output, caused by likelihood-based generation with no truth signal. Mitigation is layered: retrieve, instruct to use only context and abstain, constrain with a schema, verify deterministically (do the cited IDs exist?), check groundedness, and route high-stakes output to a human. No layer is sufficient alone.

## Related

- [RAG and Vector Databases](../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md)
- [LLM Evaluation and Testing](../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md)
- [Prompt Injection and Agentic Security](../syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md)
