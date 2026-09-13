---
title: "Flashcards: RAG and Vector Databases (pgvector)"
slug: rag-and-vector-databases
document_type: flashcard-deck
domain: 22-ai-llm-engineering
topic_id: T-2301
canonical: ../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md
last_updated: 2026-09-12
---

# Flashcards: RAG and Vector Databases (pgvector)

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/rag-and-vector-databases.md`](../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md)

## Card: Why a paraphrased query retrieves worse

**Prompt:**
A naive hashed-word embedding scheme retrieves the correct document for a query. The identical question, paraphrased with different words, retrieves it worse — why?

**Answer:**
The scheme only captures literal word/hash overlap, not meaning. Real evidence: the correct document drops to third place, behind two unrelated documents, purely because the paraphrase shares almost no literal words with it. A real trained embedding model would place them close together because it learned they mean similar things.

**Why it matters:**
The concrete, measured reason production RAG systems use a real trained embedding model instead of a hashing scheme.

**Common trap:**
Assuming any vector-producing scheme is "good enough" for semantic retrieval without checking paraphrase robustness.

**Related:**
[RAG and Vector Databases](../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md)

## Card: Normalization and distance metrics

**Prompt:**
Does it matter whether you compare vectors with cosine distance versus L2 distance on unnormalized vectors?

**Answer:**
Yes — real, measured evidence shows the identical query and documents rank differently depending on which is used. L2 distance on raw term counts is sensitive to document length (a longer document can look "farther away" purely from length, independent of relevance); normalizing to unit length removes that sensitivity.

**Why it matters:**
A real, common, silent ranking bug — picking the wrong metric/normalization combination changes results without any error being thrown.

**Common trap:**
Treating cosine and L2 distance as interchangeable regardless of whether vectors are normalized.

**Related:**
[RAG and Vector Databases](../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md)

## Card: When an ANN index helps

**Prompt:**
Should you add an HNSW index to a vector column on a table with only a few hundred rows?

**Answer:**
No — a sequential scan is already fast at that scale; an ANN index adds real index-maintenance cost with no measurable retrieval benefit. ANN indexes pay off at real scale, and are approximate by design (not exact top-k), a trade-off to know before assuming they're a free upgrade.

**Why it matters:**
A real, common premature-optimization mistake with vector search specifically.

**Common trap:**
Adding an ANN index reflexively "because it's a vector column," regardless of table size.

**Related:**
[RAG and Vector Databases](../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md)
