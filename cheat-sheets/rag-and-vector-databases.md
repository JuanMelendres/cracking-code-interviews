---
title: "Cheat Sheet: RAG and Vector Databases (pgvector)"
slug: rag-and-vector-databases
document_type: cheat-sheet
domain: 22-ai-llm-engineering
topic_id: T-2301
canonical: ../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md
last_updated: 2026-09-12
---

# RAG and Vector Databases (pgvector)

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/rag-and-vector-databases.md`](../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md)

## Core Mental Model

An embedding is a vector representing a text's meaning; "close" vectors (by cosine distance) are meant to represent similar meaning. RAG embeds a user's question, retrieves the *k* closest stored documents, and hands those to the LLM as context — pgvector stores/searches the vectors but has no opinion about how they were produced.

## Essential Definitions

- **Cosine distance** — measures the angle between two vectors; 0 means identical direction.
- **`pgvector`** — a PostgreSQL extension adding vector storage and nearest-neighbor search.
- **HNSW/IVFFlat** — approximate nearest-neighbor index types; approximate by design, not exact.

## Decision Table

| Need | Mechanism |
|---|---|
| Find k closest documents to a query | Vector similarity search (`<=>` cosine, `<->` L2) |
| Real semantic retrieval in production | A real, trained embedding model — never a naive word-overlap scheme |
| Fast search on a huge table | HNSW/IVFFlat index (approximate) |
| Fast search on a small table | Sequential scan — an ANN index adds maintenance cost with no benefit |

## Common Pitfalls

- Comparing unnormalized vectors with a distance metric that assumes normalization — real, measured evidence shows rankings silently change based on document length, not relevance.
- Assuming any "list of numbers" is a usable embedding — a naive hashed scheme captures vocabulary overlap, not meaning (real evidence: a paraphrased query loses the actually-correct document to two unrelated ones).
- Building an ANN index on a tiny table where sequential scan is already fast.
- Treating HNSW as exact when a use case genuinely needs the guaranteed true top-k.

## Interview Answer Skeleton

**30-sec:** RAG embeds a query, retrieves the *k* closest stored documents by vector distance, and feeds them to the LLM as context. pgvector handles storage/search; embedding quality is a separate, critical concern.

**2-min:** Add: a real demo shows a naive hashed embedding scheme correctly retrieving on literal keyword overlap but failing on a paraphrase — the actually-correct document drops to third place behind unrelated ones, purely because it shares few literal words. This is exactly why production RAG uses a real trained embedding model, not a hashing scheme.

**Staff-level framing:** Distance-metric choice and normalization are not interchangeable implementation details — a real, measured example shows raw unnormalized vectors with L2 distance ranking differently than cosine distance on normalized vectors, for the identical query and documents.

## Related

- syllabus/22-ai-llm-engineering/embeddings.md
- syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md
- syllabus/06-databases/jsonb-and-advanced-index-types.md
