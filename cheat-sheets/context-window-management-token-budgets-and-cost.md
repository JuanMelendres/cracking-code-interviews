---
title: "Cheat Sheet: Context Window Management, Token Budgets, and Cost"
slug: context-window-management-token-budgets-and-cost
document_type: cheat-sheet
domain: 22-ai-llm-engineering
topic_id: T-2308
canonical: ../syllabus/22-ai-llm-engineering/context-window-management-token-budgets-and-cost.md
last_updated: 2026-09-28
---

# Context Window Management, Token Budgets, and Cost

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/context-window-management-token-budgets-and-cost.md`](../syllabus/22-ai-llm-engineering/context-window-management-token-budgets-and-cost.md)

## The Arithmetic

```text
system + history + retrieved context + tool definitions + tool results + reserved output <= window
```

**Reserve output first.** A request that fits and then truncates the answer mid-sentence gets misdiagnosed as a quality problem.

## Budget Table

| Segment | Policy |
|---|---|
| System prompt | Fixed, versioned, audited — grows by accretion |
| Tool definitions | Fixed per enabled tool; a big catalogue is a real cost |
| History | Bounded: last N, or summary + last N verbatim |
| Retrieved context | Capped by **token budget**, not document count |
| Reserved output | Sized to the longest acceptable answer |

## More Context ≠ Better Answers

Precision falls as lower-ranked chunks are added; models attend less reliably to material buried mid-context; cost and latency rise. Past a plateau, more context costs more **and** answers get worse. Set `k` by measuring on an eval set.

## History Strategies

| Strategy | Loses |
|---|---|
| Sliding window (last N) | Anything older, abruptly |
| Running summary | Detail; degrades as summaries summarize summaries |
| Summary + last N verbatim | Mid-range detail (the common default) |
| Retrieval over history | Continuity and flow |

Keep structured facts (constraints, chosen options, IDs) in a **separate slot** so summarization cannot drop them.

## Prompt Caching

Stable prefix first, variable material last:

```text
[stable]   system prompt (no timestamps, no user detail)
[stable]   tool definitions
[stable]   rarely-changing reference docs
---------- cacheable prefix ends ----------
[variable] retrieved chunks, recent turns, the question
```

One dynamic value placed early invalidates the whole prefix. That is the bug to look for when hit rate disappoints.

## Cost Levers, In Order of Return

1. Send less (retrieval budget, history bound, prune system prompt + tool catalogue).
2. Cache the stable prefix — free.
3. Cap output (`max_tokens`); output rates are higher.
4. Route by difficulty to a cheaper model.
5. Cache whole responses for repeats.
6. Change model — last, needs re-evaluation.

## Common Pitfalls

- Estimating tokens as characters ÷ 4. Wrong for JSON, code, non-Latin — exactly the payloads that overflow.
- Forgetting the output reservation.
- Truncating mid-document or mid-turn instead of dropping whole units.
- Not logging **per-segment** token counts — then "spend doubled" is undiagnosable.
- Multi-step agents: context fills as the task progresses, so failures appear on the hardest tasks and look like a capability limit.

## Performance

Time-to-first-token scales with **input** length; total time scales with **output** length. Prompt caching improves both cost and TTFT.

## Interview Answer Skeleton

**30-sec:** The window holds system prompt, tools, history, retrieved context, tool results, and the reserved output. Budget it explicitly: reserve output first, bound history, fill the rest with ranked context, drop whole units. Count with the real tokenizer, and log tokens per segment.

## Related

- [RAG and Vector Databases](../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md)
- [Hallucination, Groundedness, and Output Guardrails](../syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md)
- [Cloud Cost and Scaling Economics](../syllabus/15-cloud/cloud-cost-and-scaling-economics.md)
