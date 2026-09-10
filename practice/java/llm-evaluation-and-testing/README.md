# LLM Evaluation and Testing — Real, Executed Demo

Backs [LLM Evaluation and Testing](../../../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md) (T-2305). No live LLM call. Four real evaluation strategies run against fixed candidate strings standing in for real model outputs.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.EvaluationDemo
```

## Reproduce the transcript

`output-transcript.txt` is the complete, real, unedited output of the last run:

1. **Exact-match brittleness** — two real, both-correct answers, real `String.equals()` returns `false`.
2. **Rule-based / structured checks** — real, deterministic JSON/schema validation against 4 real candidate strings — no ambiguity, no threshold to tune.
3. **Semantic-similarity scoring** — a real hashed-embedding cosine similarity check catches a literal-overlap correct answer (0.9091, PASS) but produces a real, honest **false negative** on a genuine paraphrase (0.2860, FAIL against a 0.30 threshold) — the same real limitation measured in T-2302 (Embeddings), now shown inside an evaluation context.
4. **Golden-dataset regression matrix** — 5 real test cases run against two simulated model versions: a real regression (`capital of Japan`: v1 passes, v2 fails) and a real improvement (`largest planet`: v1 fails, v2 passes) in the same run — real, direct evidence for why a full suite catches what spot-checking 1-2 examples would miss.

## Files

- `src/demo/HashedBagOfWordsEmbedder.java` — the same real embedding technique used in `practice/java/embeddings-fundamentals/`.
- `src/demo/EvaluationDemo.java` — the real demo driving all four scenarios.
