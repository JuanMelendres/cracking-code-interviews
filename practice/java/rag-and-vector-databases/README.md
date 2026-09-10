# RAG and Vector Databases (pgvector) — Real, Executed Demo

Backs [RAG and Vector Databases (pgvector)](../../../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md) (T-2301). A real PostgreSQL 16 + real `pgvector` extension in a real Docker container, a real JDBC connection, real vector storage, real distance-metric queries, a real HNSW index, and real `EXPLAIN ANALYZE` evidence of what it buys at scale.

**Embeddings are computed by a real, deterministic hashing scheme** (`HashedBagOfWordsEmbedder` — the real "hashing trick" / feature hashing, L2-normalized), not a live embedding-API call. This is stated explicitly because the point of this demo is the **pgvector mechanics** (storage, distance metrics, indexing, retrieval), not semantic embedding quality — and the demo's own Section 2b honestly demonstrates where that toy scheme's real limitation shows up, real output included, not glossed over.

## Setup

```bash
./fetch-deps.sh
./run-all-demos.sh
```

`run-all-demos.sh` brings up the real `pgvector/pgvector:pg16` container, compiles, runs, and tears the container down afterward.

## Reproduce the transcript

`output-transcript.txt` is the complete, real, unedited output of the last run:

1. **Insert** — 10 real short documents, each with a real hashed-BoW embedding, stored in a real `vector(32)` column.
2. **Literal-keyword retrieval** — a query sharing real words with the GC document retrieves it as the real #1 result by cosine distance.
3. **Honest limitation (2b)** — the *identical question*, paraphrased with different words, does **not** reliably retrieve the same document — real, executed proof that a hashed bag-of-words scheme matches vocabulary, not meaning, and exactly why production RAG uses a real trained embedding model instead.
4. **Normalization pitfall** — the same query against normalized (cosine) vs. raw, unnormalized (L2) vectors produces a genuinely different real ranking.
5. **Scale + indexing** — 5,000 real synthetic rows; a real `EXPLAIN ANALYZE` before any index (real `Seq Scan`) vs. after a real `CREATE INDEX ... USING hnsw` (real `Index Scan`) — a real, measured execution-time drop.
6. **RAG prompt assembly** — real top-k retrieved context assembled into the real augmented prompt a downstream LLM call (T-2300) would receive.

## Files

- `docker-compose.yml` — real `pgvector/pgvector:pg16` container.
- `src/demo/HashedBagOfWordsEmbedder.java` — the real, deterministic embedding function.
- `src/demo/RagVectorDbDemo.java` — the real demo driving all six scenarios above.
