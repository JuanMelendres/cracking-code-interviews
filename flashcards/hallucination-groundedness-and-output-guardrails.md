---
title: "Flashcards: Hallucination, Groundedness, and Output Guardrails"
slug: hallucination-groundedness-and-output-guardrails
document_type: flashcard-deck
domain: 22-ai-llm-engineering
topic_id: T-2307
canonical: ../syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md
last_updated: 2026-09-28
---

# Flashcards: Hallucination, Groundedness, and Output Guardrails

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md`](../syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md)

## Card: Why models state false things confidently

**Prompt:**
Why does a model produce fluent, confident, false output?

**Answer:**
Generation samples tokens by likelihood conditioned on the prompt, and likelihood reflects how well text fits training patterns — not whether it corresponds to reality. There is no internal flag separating "I know this" from "this is a plausible sentence shape." A fabricated method name scores highly precisely because it looks exactly like real method names.

**Why it matters:**
The confidence a reader perceives is a property of prose style, not of model certainty — which is why it fools reviewers.

**Common trap:**
Treating it as a bug to be fixed rather than a property to be constrained.

**Related:**
[Hallucination, Groundedness, and Output Guardrails](../syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md)

## Card: The one diagnostic question

**Prompt:**
An answer is wrong. What do you ask first?

**Answer:**
"Were the right documents in the prompt?" If no, it is a **retrieval** failure — fix chunking, embeddings, query rewriting, reranking. If yes, it is a **generation** failure — fix instructions, verification, or the model. They are different bugs with different fixes, and answering this requires logging retrieved document IDs alongside every response.

**Why it matters:**
Without that log, every bad answer investigation starts by trying to reproduce the pipeline.

**Common trap:**
Jumping to a bigger model without identifying the failing stage.

**Related:**
[RAG and Vector Databases](../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md)

## Card: What schema validation does not guarantee

**Prompt:**
Structured outputs guarantee the model returns valid JSON matching your schema. What does that tell you about correctness?

**Answer:**
Nothing. Constrained decoding masks invalid tokens at each step, so it genuinely guarantees the **shape**. A perfectly valid `{"refund_amount": 4500, "policy_id": "POL-9"}` is still worthless if `POL-9` does not exist — which is why deterministic business validation sits alongside it.

**Why it matters:**
Schema enforcement is the layer that feels strongest and is among the narrowest.

**Common trap:**
Reporting "we use structured outputs" as a hallucination mitigation.

**Related:**
[Hallucination, Groundedness, and Output Guardrails](../syllabus/22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md)

## Card: The free guardrail most teams skip

**Prompt:**
What is the cheapest possible check against a fabricated citation?

**Answer:**
Assert that every `source_id` the model cited appears in the set of documents that were actually retrieved. It is a set lookup — microseconds, no model call — and a model citing a document that was never in its own context is fabrication with a zero-ambiguity test.

**Why it matters:**
Teams routinely reach for an LLM-judge groundedness check, which roughly doubles per-request cost, before running this one.

**Common trap:**
Treating the presence of a citation as verification. A real citation on an unsupported claim is more persuasive, not less.

**Related:**
[LLM Evaluation and Testing](../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md)

## Card: Why a 97% groundedness score can be false comfort

**Prompt:**
A dashboard reports 97% groundedness while users report wrong answers. What are the two likely causes?

**Answer:**
Scoring granularity and judge correlation. Whole-answer scoring marks an answer grounded when most claims are supported, so four good claims plus one fabrication passes — score **per atomic claim** instead. And if the judge is the same model family as the generator, their blind spots are correlated. Neither number means anything until the judge is calibrated against human labels.

**Why it matters:**
"How do you know your groundedness score is right?" is the follow-up that separates people who have run this from people who have read about it.

**Common trap:**
Measuring only answerable questions, which makes an always-answering system look perfect.

**Related:**
[LLM Evaluation and Testing](../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md)
