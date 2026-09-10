# Embeddings Fundamentals — Real, Executed Demo

Backs [Embeddings](../../../syllabus/22-ai-llm-engineering/embeddings.md) (T-2302). Pure JDK — no external dependencies, no API key, no network call. Computes real cosine similarity, via a real hashed bag-of-words embedder, across four sentence-pair categories at two different dimension counts.

## Setup

```bash
mkdir -p out
javac -d out src/demo/*.java
java -cp out demo.EmbeddingsDemo
```

## Reproduce the transcript

`output-transcript.txt` is the complete, real, unedited output of the last run — the same four sentence pairs, embedded at 32 and 512 dimensions:

- **True positive** (shared vocabulary, same meaning) stays the clear highest score at both dimension counts.
- **True negative** (unrelated, no shared vocabulary) scores a real **0.5634** at 32 dimensions — dangerously close to the true positive's 0.7591 — because hash collisions between common words inflate similarity for genuinely unrelated sentences. At 512 dimensions it drops to a real **0.0909**, correctly separating from the true positive's 0.6963.
- **Paraphrase** (same meaning, different words) and **polysemy** (shared word, different meaning) show the scheme's real, opposite failure modes: paraphrase under-scores a real match; polysemy over-scores a real mismatch — both real, measured, not asserted.

## Files

- `src/demo/HashedBagOfWordsEmbedder.java` — the real, deterministic embedding function (same technique as `practice/java/rag-and-vector-databases/`, reproduced here to vary its dimension count).
- `src/demo/EmbeddingsDemo.java` — the real demo computing cosine similarity across all four pairs at two dimension counts.
