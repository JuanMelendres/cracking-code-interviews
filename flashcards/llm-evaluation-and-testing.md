---
title: "Flashcards: LLM Evaluation and Testing"
slug: llm-evaluation-and-testing
document_type: flashcard-deck
domain: 22-ai-llm-engineering
topic_id: T-2305
canonical: ../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md
last_updated: 2026-09-12
---

# Flashcards: LLM Evaluation and Testing

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md`](../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md)

## Card: Why exact match fails correct answers

**Prompt:**
`assertEquals("Paris is the capital of France.", candidate)` fails when `candidate` is `"The capital of France is Paris."` — is the candidate wrong?

**Answer:**
No — both are correct, just worded differently. Exact match fails a genuinely correct answer purely from wording, which is exactly why it's insufficient alone for open-ended LLM output.

**Why it matters:**
A real, direct proof that naive string-equality testing is the wrong tool for LLM output evaluation.

**Common trap:**
Using `assertEquals`-style exact string comparison as the primary correctness check for open-ended generated text.

**Related:**
[LLM Evaluation and Testing](../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md)

## Card: Semantic-similarity scoring relocates, not eliminates, the problem

**Prompt:**
Does semantic-similarity scoring (embedding-based) solve the false-negative problem exact match has?

**Answer:**
It relocates the problem rather than eliminating it. Real evidence: a genuinely correct paraphrased answer scores 0.2860 similarity against a 0.30 threshold — a real false negative, the same fundamental limitation embeddings have for retrieval, now showing up in evaluation.

**Why it matters:**
No single automated metric is universally sufficient — a Staff-level eval strategy combines multiple signals.

**Common trap:**
Assuming switching from exact match to semantic similarity fully solves the wording-sensitivity problem.

**Related:**
[LLM Evaluation and Testing](../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md)

## Card: What spot-checking misses

**Prompt:**
After a prompt change, you manually re-check 2-3 examples and they still pass. Is that sufficient?

**Answer:**
Not necessarily — real evidence: a golden-dataset regression run of 5 real test cases against two model/prompt versions found a real regression on one case and a real improvement on another, in the same run. A team spot-checking only the two cases that still passed would have shipped the regression confidently.

**Why it matters:**
The concrete argument for running a full golden-dataset suite instead of spot-checking a couple of examples.

**Common trap:**
Treating "the examples I happened to check still pass" as equivalent to "nothing regressed."

**Related:**
[LLM Evaluation and Testing](../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md)
