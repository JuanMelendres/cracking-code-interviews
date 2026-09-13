---
title: "Cheat Sheet: Prompt Engineering Patterns"
slug: prompt-engineering-patterns
document_type: cheat-sheet
domain: 22-ai-llm-engineering
topic_id: T-2303
canonical: ../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md
last_updated: 2026-09-12
---

# Prompt Engineering Patterns

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md`](../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md)

## Core Mental Model

The real distinction: which patterns are just careful wording (few-shot examples, "think step by step"), and which are real, distinct request parameters (`temperature`, `response_format`, the `system` role). Wording *helps*; a real parameter *guarantees* (or measurably shifts) behavior.

## Essential Definitions

- **Zero-shot** — task with no examples. **Few-shot** — task plus worked input/output examples in the request.
- **`temperature`** — a real, numeric sampling parameter; `0.0` pushes toward the single most likely output, higher values allow less-likely outputs.
- **`response_format=json_object`** — a real request parameter forcing structured output; distinct from just asking nicely in the prompt.
- **System role** — a structurally separate channel from user content, not just stylistic placement.

## Decision Table

| Need | Mechanism |
|---|---|
| Give the model a concrete pattern to follow | Few-shot examples (wording only, in `messages`) |
| Control output randomness | `temperature` (real parameter — measured: 1 distinct output at `0.0`, 3 distinct at `0.9`, 20 trials each) |
| Guarantee parseable structured output | `response_format=json_object` (real: 0/20 parse failures vs. 8/20 with wording alone) |
| Keep instructions resistant to user override attempts | Put them in the `system` role, not concatenated into user text |

## Common Pitfalls

- Assuming `temperature=0` guarantees byte-for-byte identical output on a live provider every time — documented as *highly* deterministic, not absolutely guaranteed.
- Relying on prompt wording alone ("respond only in JSON") for output a downstream system parses — real evidence shows this fails 8/20 times versus 0/20 with the real parameter.
- Concatenating instructions and untrusted user content into one message — real evidence shows this is override-able in a way the system-role channel isn't.
- Reaching for few-shot examples by default without checking whether zero-shot was already sufficient — an avoidable token-cost multiplier.

## Interview Answer Skeleton

**30-sec:** Some prompt patterns are just wording (few-shot, chain-of-thought); some are real, distinct API parameters (`temperature`, `response_format`, `system` role) with measurably different guarantees. Know which is which.

**2-min:** Add: a real demo proves `response_format=json_object` eliminates JSON-parse failures (0/20) that plain wording alone doesn't (8/20), and that identical override-attempt text produces different real outcomes depending on whether the original instruction sits in the `system` channel or is concatenated into `user` text.

**Staff-level framing:** Treating "ask nicely in the prompt" as equivalent to a real API guarantee is a production-reliability risk, not a style choice — anything a downstream system parses or depends on structurally should use the real parameter, not wording alone.

## Related

- syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md
- syllabus/12-security/injection-input-validation-output-encoding.md
