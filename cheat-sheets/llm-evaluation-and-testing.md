---
title: "Cheat Sheet: LLM Evaluation and Testing"
slug: llm-evaluation-and-testing
document_type: cheat-sheet
domain: 22-ai-llm-engineering
topic_id: T-2305
canonical: ../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md
last_updated: 2026-09-12
---

# LLM Evaluation and Testing

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md`](../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md)

## Core Mental Model

No single evaluation strategy is universally sufficient — each of the four real strategies has a different, real reliability profile, and each relocates rather than eliminates some failure mode. A prompt or model change is a real behavior change; without a real test suite, you find out from users, not from yourself.

## Essential Definitions

- **Exact match** — fails a correct answer purely from wording difference.
- **Rule-based/structured checks** — 100% reliable for what they check (valid JSON, required field), but check form, not correctness.
- **Semantic-similarity scoring** — embeds and compares reference vs. candidate; catches some wording variation exact match misses, but has its own real false-negative risk.
- **Golden-dataset regression testing** — a fixed, curated test set diffed across every prompt/model change.

## Decision Table

| Need | Strategy |
|---|---|
| Exact, fixed-format output | Exact match |
| Structural correctness (valid JSON, required fields) | Rule-based/structured checks |
| Wording-tolerant correctness scoring | Semantic-similarity scoring (embeddings), with a validated threshold |
| Catch a regression a prompt change silently introduces | Golden-dataset regression testing |
| Nuanced, hard-to-rule criteria (tone, helpfulness) | LLM-as-judge (a second model call grading the first) |

## Common Pitfalls

- Using `assertEquals` against a single expected string for open-ended LLM output — real evidence shows this fails a genuinely correct, differently-worded answer.
- Trusting a single evaluation metric for every task — semantic-similarity scoring has a real, measured false negative on a genuine paraphrase (0.2860 vs. a 0.30 threshold).
- Spot-checking 2-3 examples after a prompt change instead of running the full golden-dataset suite — real evidence shows a regression and an improvement occurring simultaneously, in cases spot-checking would have missed.
- Setting a similarity threshold by guessing instead of validating it against real known-correct and known-incorrect examples.

## Interview Answer Skeleton

**30-sec:** Four real strategies — exact match, rule-based checks, semantic-similarity scoring, golden-dataset regression — each with a different reliability profile. No single one is sufficient alone.

**2-min:** Add: real evidence shows exact match failing `"Paris is the capital of France."` against `"The capital of France is Paris."` (both correct); semantic-similarity scoring failing a genuine paraphrase (0.2860 similarity against a 0.30 threshold) — the same limitation embeddings have for retrieval, showing up as a false negative in evaluation.

**Staff-level framing:** A Staff-level eval strategy combines multiple signals rather than trusting one metric alone — the structural risk (any single automated metric can disagree with a correct human judgment on some real fraction of cases) never fully disappears, even with a better embedding model.

## Related

- syllabus/22-ai-llm-engineering/embeddings.md
- syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md
