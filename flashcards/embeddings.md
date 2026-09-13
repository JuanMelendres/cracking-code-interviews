---
title: "Flashcards: Embeddings"
slug: embeddings
document_type: flashcard-deck
domain: 22-ai-llm-engineering
topic_id: T-2302
canonical: ../syllabus/22-ai-llm-engineering/embeddings.md
last_updated: 2026-09-12
---

# Flashcards: Embeddings

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/embeddings.md`](../syllabus/22-ai-llm-engineering/embeddings.md)

## Card: Dimension count and signal-to-noise

**Prompt:**
Does embedding dimension count only affect storage cost?

**Answer:**
No — real, measured evidence shows it controls signal-to-noise ratio. At 32 dimensions, a genuinely unrelated pair scored 0.5634, dangerously close to a genuinely related pair's 0.7591. At 512 dimensions, the same unrelated pair dropped to 0.0909 — clearly separated.

**Why it matters:**
Too few dimensions make any similarity threshold you set unreliable, regardless of how carefully tuned.

**Common trap:**
Picking the smallest available dimension count to save storage without checking retrieval quality at that size.

**Related:**
[Embeddings](../syllabus/22-ai-llm-engineering/embeddings.md)

## Card: Two opposite embedding failure modes

**Prompt:**
Name two real, opposite ways a naive (non-trained) embedding scheme can fail.

**Answer:**
Paraphrase failure — under-scores a genuine semantic match sharing little vocabulary (measured: 0.2611 similarity for two sentences meaning the same thing). Polysemy failure — over-scores an unrelated pair sharing one ambiguous word (e.g., "bank account" vs. "river bank").

**Why it matters:**
Both are measured directly in this chapter's demo, not asserted — real evidence for why naive schemes are unreliable for production semantic search.

**Common trap:**
Assuming a naive word-overlap scheme only has one kind of failure mode (usually assumed to be paraphrase-only).

**Related:**
[Embeddings](../syllabus/22-ai-llm-engineering/embeddings.md)

## Card: Cross-model vector comparability

**Prompt:**
Can you meaningfully compare an embedding produced by Model A against one produced by Model B (or the same model at a different dimension count)?

**Answer:**
No — a vector's numbers only mean something relative to the specific model and configuration that produced them. There is no cross-model comparability guarantee.

**Why it matters:**
A real, common integration mistake when swapping embedding providers or model versions without re-embedding existing stored vectors.

**Common trap:**
Assuming embeddings are a universal numeric format comparable across any model.

**Related:**
[Embeddings](../syllabus/22-ai-llm-engineering/embeddings.md)
