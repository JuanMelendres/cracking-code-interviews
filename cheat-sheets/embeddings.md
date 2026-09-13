---
title: "Cheat Sheet: Embeddings"
slug: embeddings
document_type: cheat-sheet
domain: 22-ai-llm-engineering
topic_id: T-2302
canonical: ../syllabus/22-ai-llm-engineering/embeddings.md
last_updated: 2026-09-12
---

# Embeddings

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/embeddings.md`](../syllabus/22-ai-llm-engineering/embeddings.md)

## Core Mental Model

An embedding is a fixed-length vector representing a text's meaning, such that texts with similar meaning produce numerically close vectors (cosine similarity). Dimension count controls signal-to-noise ratio, not just storage cost — too few dimensions and unrelated content can score almost as "similar" as related content.

## Essential Definitions

- **Cosine similarity** — 1.0 identical direction, 0 unrelated; a real dot product on length-normalized vectors.
- **Dimension count** — how many numbers per vector; a real, consequential choice affecting retrieval reliability, not an arbitrary setting.
- **Paraphrase failure** — under-scoring a genuine semantic match that shares little vocabulary.
- **Polysemy failure** — over-scoring an unrelated pair that happens to share one ambiguous word.

## Decision Table

| Need | Consideration |
|---|---|
| Reliable separation between related/unrelated content | Higher dimension count (real evidence: 512 dims separate a true-negative pair to 0.09 vs. 32 dims leaving it at 0.56, near a true-positive's 0.76) |
| Detect real semantic similarity, not just word overlap | A real, trained embedding model — never a hashed bag-of-words scheme |
| Compare vectors across models/configs | Don't — a vector's numbers only mean something relative to the specific model/config that produced them |

## Common Pitfalls

- Treating word/character overlap as a proxy for semantic similarity — a naive scheme's real, measured polysemy failure (e.g., "bank account" vs. "river bank" scoring falsely high).
- Choosing the smallest embedding dimension to save storage without checking retrieval quality at that size.
- Comparing embeddings from two different models or dimension counts as if interchangeable.
- Assuming bigger dimension count is always strictly better with no cost — storage/compute scale with it.

## Interview Answer Skeleton

**30-sec:** An embedding is a vector representing meaning; cosine similarity measures closeness. Dimension count is a real reliability lever, not just a storage knob — too few dimensions blur the signal between related and unrelated content.

**2-min:** Add: real, measured evidence at 32 vs. 512 dimensions shows a true-negative pair scoring 0.5634 (32-dim, dangerously close to a true-positive's 0.7591) versus 0.0909 (512-dim, clearly separated). Two real, opposite failure modes: paraphrase (under-scores a real match) and polysemy (over-scores a real mismatch) — both measured directly, not asserted.

**Staff-level framing:** Embedding quality and dimension count are production reliability decisions, not implementation trivia — a badly-chosen dimension count makes every downstream similarity threshold unreliable, regardless of how well-tuned the threshold itself is.

## Related

- syllabus/22-ai-llm-engineering/rag-and-vector-databases.md
- syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md
