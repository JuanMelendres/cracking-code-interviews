---
title: "Hallucination, Groundedness, and Output Guardrails"
slug: hallucination-groundedness-and-output-guardrails
document_type: syllabus-topic
domain: 22-ai-llm-engineering
topic_id: T-2307
status: canonical
version: 1.0
last_updated: 2026-09-28
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - llm-api-integration-fundamentals.md
  - rag-and-vector-databases.md
related:
  - llm-evaluation-and-testing.md
  - prompt-injection-and-agentic-security.md
  - prompt-engineering-patterns.md
  - ../11-system-design/resilience-patterns.md
production_scenarios: []
interview_paths: [mid-to-senior, senior-to-staff]
official_references:
  - https://platform.openai.com/docs/guides/structured-outputs
  - https://docs.anthropic.com/en/docs/test-and-evaluate/strengthen-guardrails/reduce-hallucinations
  - https://json-schema.org/draft/2020-12/json-schema-core
---

# Hallucination, Groundedness, and Output Guardrails

> **Topic register.** T-2307, in the reserved `T-2300`–`T-2399` range for `syllabus/22-ai-llm-engineering/`. Added 2026-09-28 closing a real audit gap: the domain covered RAG, evaluation, and prompt injection, but never explained *why* a model states false things confidently, what "grounded" means precisely enough to measure, or what a guardrail is beyond the word.
> **Scope.** [LLM Evaluation and Testing](llm-evaluation-and-testing.md) owns how you measure quality over a dataset. This chapter owns the runtime question: what the failure is, why it happens, and what you put between the model and the user.

## Table of Contents

1. [Why This Matters](#1-why-this-matters)
2. [Prerequisites](#2-prerequisites)
3. [Foundation (L1)](#3-foundation-l1)
4. [Core Concepts (L2)](#4-core-concepts-l2)
5. [How It Works Internally (L3)](#5-how-it-works-internally-l3)
6. [Practical Usage](#6-practical-usage)
7. [Examples](#7-examples)
8. [Common Mistakes](#8-common-mistakes)
9. [Edge Cases](#9-edge-cases)
10. [Performance Implications](#10-performance-implications)
11. [Trade-offs](#11-trade-offs)
12. [Senior-Level Considerations (L3)](#12-senior-level-considerations-l3)
13. [Staff/System-Level Considerations (L4)](#13-staffsystem-level-considerations-l4)
14. [Production Scenarios](#14-production-scenarios)
15. [Interview Questions](#15-interview-questions)
16. [Coding/Practice Exercises](#16-codingpractice-exercises)
17. [Debugging Exercises](#17-debugging-exercises)
18. [Design Exercises](#18-design-exercises)
19. [Further Reading](#19-further-reading)
20. [Mastery Checklist](#20-mastery-checklist)

## 1. Why This Matters

Every team shipping an LLM feature meets the same objection in the same review: "what happens when it makes something up?" A vague answer — "we use RAG," "we tell it not to" — is the difference between a feature that ships and one that stalls. The precise answer requires knowing what the failure actually is, which mitigations address which part of it, and which residual risk you are accepting on purpose.

Interviewers ask about it because it is where the systems thinking lives. Retrieval, schema validation, citation checking, and human review are not competing answers; they are layers that address different failure modes, and a candidate who names one is guessing while a candidate who names the layering has built something.

## 2. Prerequisites

- [LLM API Integration Fundamentals](llm-api-integration-fundamentals.md) — messages, temperature, tokens.
- [RAG and Vector Databases](rag-and-vector-databases.md) — retrieval as the main grounding mechanism.
- Comfort with JSON Schema, since structured output enforcement is schema-shaped.

## 3. Foundation (L1)

A language model predicts likely continuations of text. It is not looking anything up, and it has no internal flag distinguishing "I know this" from "this is a plausible sentence shape." That single fact explains the whole topic.

**Hallucination** is the model producing content that is fluent, confident, and false. It is not a bug in the usual sense — nothing malfunctioned. The model did exactly what it does: produced a likely continuation. "The `computeDigest()` method takes a `Charset` parameter" is a very likely sentence in a corpus full of Java documentation, whether or not that method exists.

The word covers several distinct failures worth separating from the start:

- **Fabricated facts** — an invented API, citation, price, or date.
- **Unsupported claims** — a statement not contradicted by the sources, but not supported by them either.
- **Contradiction of the source** — the retrieved document says 30 days, the answer says 60.
- **Overconfident uncertainty** — a real answer, stated as certain, where the honest answer was "the documents do not say."

**Grounded** means every claim in the output is supported by material actually provided in the request — retrieved documents, tool results, the conversation. Groundedness is a property of an answer *relative to given sources*, which is what makes it measurable in a way that "true" is not: you can check whether a sentence is supported by a document you have in hand.

A **guardrail** is anything between the model's output and its consumer that can reject, constrain, or flag it: a schema that the output must satisfy, a check that cited documents exist, a rule that a refund over a threshold needs a human, a filter for unsafe content.

## 4. Core Concepts (L2)

### The layers, and what each one actually catches

| Layer | Catches | Misses |
|---|---|---|
| Retrieval (RAG) | Questions about facts the model never memorized or learned wrong | Nothing, when retrieval returns the wrong documents or none |
| Prompt instructions ("only use the context; say you do not know") | A meaningful share of unsupported claims | Anything the model ignores under pressure — instructions are not constraints |
| Structured output / schema | Malformed shape, invalid enum values, missing fields | A well-formed object full of false values |
| Citation requirement + verification | Claims with fabricated or non-existent sources | A real citation attached to a claim it does not support |
| Post-hoc groundedness check (model or NLI) | Claims unsupported by the retrieved text | Its own errors — it is another probabilistic judge |
| Deterministic business validation | Values outside allowed ranges, non-existent IDs, policy violations | Anything your rules do not cover |
| Human review | Everything, in principle | Scale, latency, cost, and reviewer fatigue |

The lesson is the last column. No single row is sufficient, and the ones that feel strongest — schema validation especially — are the narrowest: a schema guarantees the *shape* of an answer and says nothing whatsoever about its truth. A perfectly valid `{"refund_amount": 4500, "policy_id": "POL-9"}` is worthless if `POL-9` does not exist, which is why the deterministic row exists.

### Grounding is retrieval plus instruction plus verification

Saying "we use RAG so we are grounded" skips two of three steps. Retrieval puts candidate facts in the prompt. Instruction tells the model to rely on them and to say when they are insufficient. Verification checks the output against them afterwards. Teams routinely do the first, do the second badly, and skip the third entirely — and then are surprised when an answer cites a document that says the opposite.

A retrieval failure and a generation failure are also different bugs with different fixes, and distinguishing them is the single most useful diagnostic habit here. Ask: *were the right documents in the prompt?* If no, fix retrieval — chunking, embeddings, query rewriting, reranking. If yes and the answer is still wrong, fix generation — instructions, model, temperature, verification.

### Abstention is a feature, not a failure

The most valuable behavior a grounded system can have is saying "the documents do not answer this." Models are poor at this by default, because every training incentive rewards producing an answer.

Two things help concretely. First, making abstention an explicit, first-class output — a schema with `"answer" | "insufficient_context"` as a required field forces the choice rather than leaving it implicit. Second, measuring it: an evaluation set containing questions that *cannot* be answered from the corpus, scored on whether the system abstained. Without that second half, every prompt tweak optimizes for answering, because that is all anyone measures.

### Temperature is a small lever, not a fix

Lower temperature makes output more deterministic and slightly reduces some fabrication. It does not make the model know things. A confidently wrong answer at temperature 0 is confidently wrong every time, which is arguably worse for debugging than an inconsistent one — at least inconsistency is visible. Treat temperature as a consistency control, not a correctness control.

## 5. How It Works Internally (L3)

**Why fluency and truth are uncorrelated.** Generation samples one token at a time from a distribution over the vocabulary, conditioned on everything so far. The probability assigned to a token reflects how well it fits the patterns in training data, not whether the resulting sentence corresponds to reality. A fabricated method name can carry a high probability because it *looks* exactly like real method names. The confidence a reader perceives is a property of the prose style, not of the model's certainty.

**Why token probabilities are a weak confidence signal.** Some APIs expose log-probabilities, and it is tempting to threshold them. In practice they measure the model's confidence about *the next token given its own preceding tokens*, which is largely a fluency measure — a fabricated but idiomatic sentence scores high. They are weakly useful as one input among several and actively misleading as a primary gate.

**How structured outputs are enforced.** Constrained decoding restricts sampling at each step to tokens that keep the output valid against a supplied schema — an invalid token is masked out before sampling. That is why modern structured-output modes can guarantee schema validity in a way that "please reply in JSON" never can: it is a decoding constraint, not an instruction. It also, again, says nothing about the values being correct.

**How a groundedness check works.** Decompose the answer into atomic claims; for each, ask whether the provided sources entail it. Implementations range from an NLI model per claim to an LLM judge with the claim and the sources in its prompt. The judge is itself probabilistic, so its errors are correlated with the generator's when both are the same model family — which is the core reason it belongs *alongside* deterministic validation rather than instead of it. Cost and latency are the real constraints: a per-claim check roughly multiplies the request cost, which is why teams often sample rather than check every response.

## 6. Practical Usage

The defensible default stack for a factual assistant, in order:

1. Retrieve, and log what was retrieved with the response so failures are diagnosable later.
2. Instruct explicitly: answer only from the context, cite the source for each claim, and return `insufficient_context` when the context does not support an answer.
3. Constrain the output with a schema that makes citations and the abstention option structural, not optional.
4. Validate deterministically: do the cited document IDs exist in what was retrieved? Are values within policy? Do referenced entities exist in your database?
5. Check groundedness on a sample (or on everything, if the stakes justify the cost), and log the score.
6. Route by stakes: display with citations, or hold for human review above a threshold.

Steps 1 and 4 are cheap and catch a surprising amount. Skipping straight to step 5 is the common mistake, because it is the most interesting one.

## 7. Examples

**A schema that makes abstention and citation structural:**

```json
{
  "type": "object",
  "required": ["status"],
  "properties": {
    "status": { "enum": ["answered", "insufficient_context"] },
    "answer":  { "type": "string" },
    "claims": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["text", "source_id"],
        "properties": {
          "text":      { "type": "string" },
          "source_id": { "type": "string" }
        }
      }
    }
  }
}
```

Every claim must name a source, and "I cannot answer" is a valid, representable outcome rather than something the model has to improvise.

**The deterministic check that costs nothing and catches fabricated citations:**

```java
// Pseudocode-adjacent sketch: the point is that this is ordinary code,
// not a model call, and it runs on every response.
Set<String> retrievedIds = retrieved.stream().map(Document::id).collect(toSet());

List<Claim> unsupported = response.claims().stream()
        .filter(c -> !retrievedIds.contains(c.sourceId()))
        .toList();

if (!unsupported.isEmpty()) {
    metrics.counter("llm.fabricated_citation").increment(unsupported.size());
    return Result.rejected("cited a document that was never retrieved");
}
```

A model citing a document ID that was not in its own context is a fabrication with a zero-cost, zero-ambiguity test. Many teams never run it.

**An instruction that carries the abstention pressure:**

```text
Answer using ONLY the numbered context documents below.
For every factual statement, cite the document number that supports it.
If the documents do not contain enough information to answer, set
status to "insufficient_context" and do not guess.
Do not use knowledge from outside these documents, even if you are confident.
```

The last line matters more than it looks: without it, models blend retrieved content with parametric knowledge, producing answers that are partly grounded and partly not — the hardest kind to detect, because the citations are real.

## 8. Common Mistakes

- **Treating RAG as a solved-it.** Retrieval changes what is in the prompt. It does not compel the model to use it, and it cannot help when retrieval returns nothing relevant.
- **Believing schema validation implies correctness.** It guarantees shape only.
- **Using an LLM judge as the only check**, especially the same model that generated the answer — the errors correlate.
- **Not distinguishing retrieval failures from generation failures**, so fixes are applied to the wrong stage.
- **Never measuring abstention**, which quietly optimizes the whole system toward always answering.
- **Thresholding on token log-probabilities** as if they were a truth signal.
- **Logging the answer but not the retrieved context**, making post-hoc diagnosis impossible.

## 9. Edge Cases

- **Partially grounded answers** — three correct cited claims plus one fabricated aside. Per-claim checking catches this; whole-answer scoring usually does not.
- **Correct answers from the wrong source.** The claim is true and the citation does not support it. Ungrounded by definition, and a real problem when the user clicks through.
- **The context itself is wrong.** A faithfully grounded answer to a stale document is still wrong to the user. Groundedness is a property relative to sources, so source quality is a separate obligation.
- **Conflicting sources.** Two retrieved documents disagree; a good system surfaces the conflict rather than silently picking one.
- **Adversarial context.** Retrieved content containing instructions is an injection vector — see [Prompt Injection and Agentic Security](prompt-injection-and-agentic-security.md).
- **Long context, buried facts.** Relevant material placed in the middle of a very long context is used less reliably than material at the edges, which is a retrieval-and-assembly problem, not a model failure.

## 10. Performance Implications

Every guardrail costs latency or money, and they differ by an order of magnitude:

| Guardrail | Added latency | Added cost |
|---|---|---|
| Schema-constrained decoding | Negligible | None |
| Citation-existence check | Microseconds (a set lookup) | None |
| Business-rule validation | Milliseconds | None |
| NLI per claim | Tens to hundreds of ms | Small model inference per claim |
| LLM-judge groundedness | A full model call (often ~1s) | Roughly doubles per-request cost |
| Human review | Minutes to hours | Staff time |

The ordering is the design: run the free deterministic checks on everything, and reserve model-based checking for sampled traffic or high-stakes paths. A team that starts with the LLM judge pays the most for the weakest guarantee.

## 11. Trade-offs

- **Strict grounding versus usefulness.** A system that abstains whenever the context is imperfect is trustworthy and frequently unhelpful. The abstention rate is a product decision, not a technical constant.
- **Verification cost versus coverage.** Checking every response doubles cost; sampling 5% gives you a quality signal but no per-response guarantee. Which you need depends on whether a single bad answer is an annoyance or an incident.
- **Citations versus readability.** Per-claim citations are verifiable and make answers denser and uglier.
- **Human review versus throughput.** It is the only true catch-all and does not scale; routing by stakes is the compromise, and the threshold is where the real argument happens.

## 12. Senior-Level Considerations (L3)

Own the distinction between retrieval failure and generation failure operationally, not just conceptually: log the retrieved document IDs and scores alongside every response so that when a bad answer is reported, the first question — "were the right documents even there?" — is answerable in seconds rather than by re-running the pipeline and hoping it reproduces.

Build the evaluation set to include unanswerable questions from the start. A set of only answerable questions makes an always-answering system look perfect, and every prompt change will then drift further toward confident answering. See [LLM Evaluation and Testing](llm-evaluation-and-testing.md) for the dataset discipline.

Treat the guardrail stack as defense in depth with known gaps, and be able to state which layer catches which failure — the table in Section 4 is the artifact worth being able to reproduce from memory in a design review.

## 13. Staff/System-Level Considerations (L4)

The decision that outlives any prompt is **where the risk is allowed to land**. A system that can be wrong is acceptable when a human reviews before anything irreversible happens, and unacceptable when its output is applied directly to money, health, or legal state. That is an architecture decision about which actions are model-authorized, and it belongs in a written design, not in a prompt.

The second Staff-level concern is **correlated failure across an organization**. If five teams each build their own retrieval and their own guardrails, the failure modes are five different shapes and the quality bar varies by team. A shared platform — retrieval, schema enforcement, citation verification, evaluation harness — makes the bar uniform and the incidents comprehensible, at the cost of the usual platform coupling.

Third is **accountability for the residual**. Every layer has a miss column, so the honest posture is a stated error budget for ungrounded output, monitored like any other SLO, with a defined response when it is breached — rather than an implicit assumption that the guardrails made the problem go away. A team that cannot say its current groundedness rate does not have a guardrail strategy; it has hopes.

Finally, **the abstention threshold is a business decision with a real cost on both sides**, and it should be set with the product owner, revisited with data, and monitored — because an assistant that says "I do not know" too often gets abandoned, and one that guesses too often gets escalated.

## 14. Production Scenarios

### Scenario: a support assistant invents a refund policy

**Symptoms.** A customer quotes the assistant promising a 60-day refund window. Policy is 30 days. The answer cited a real policy document.

**Evidence collected.** Logged retrieval shows the 30-day document was retrieved and included. The generated answer said 60 and cited that document.

**Diagnosis.** Generation failure, not retrieval failure — the correct source was present and contradicted. No verification layer existed between the model and the customer, and the citation being real made the answer *more* convincing, not less.

**Immediate mitigation.** A deterministic check that numeric claims about policy durations match values extracted from the cited document, plus routing any answer containing a commitment (amount, date, entitlement) to human review.

**Permanent remediation.** Per-claim groundedness checking on this answer class, an evaluation set containing policy questions with known answers, and a schema requiring a `source_id` per claim so the check has something to check.

**Trade-offs.** Human review on commitment-bearing answers adds minutes of latency to a minority of requests. Cheaper than the alternative, which is honoring a promise the system invented.

### Scenario: a groundedness score that looked fine and was not

**Symptoms.** A dashboard reports 97% groundedness. Users still report wrong answers.

**Diagnosis.** The judge scored whole answers as grounded when *most* claims were supported, so an answer with four good claims and one fabricated one scored as grounded. The same model family generated and judged, so both shared the same blind spots.

**Remediation.** Decompose into atomic claims and score per claim; use a different model for judging than for generating; and calibrate the judge against a human-labelled sample before trusting the number at all.

**Interview lesson.** A metric nobody calibrated is a source of false confidence. "How do you know your groundedness score is right?" is the follow-up that separates people who have run this from people who have read about it.

## 15. Interview Questions

### Question 1 — What is a hallucination, why do models produce them, and what actually reduces them?

**Expected answer.** Fluent, confident, false output. It happens because generation samples likely continuations conditioned on the prompt — likelihood reflects pattern fit, not truth, and the model has no internal "I know this" flag. Reductions come in layers: retrieval puts real facts in the prompt; explicit instructions to use only that context and to abstain otherwise; schema-constrained output making citation and abstention structural; deterministic verification that cited sources exist and values are within policy; a groundedness check on the output; and human review for high-stakes actions.

**Minimum acceptable answer.** Defines it correctly and names RAG as the main mitigation.

**Strong Senior answer.** Emphasizes that no layer is sufficient, names what each one misses, and distinguishes retrieval failure from generation failure as separate bugs with different fixes. Notes that schema validation guarantees shape only.

**Staff-level extension.** Frames it as risk placement — which actions a model is authorized to take without a human — plus a stated error budget for ungrounded output, monitored like any other SLO.

**Common mistakes.** "Lower the temperature"; treating RAG as a complete answer; claiming structured outputs prevent false content.

**Likely follow-ups.** "How would you measure it?" (Per-claim groundedness against retrieved context, plus an unanswerable-question set for abstention.) "Would fine-tuning fix it?" (It changes style and format reliably; it is a poor mechanism for factual currency.)

### Question 2 — Your assistant cited a real document and still gave the wrong answer. Walk me through it.

**Expected answer.** First, determine whether the right documents were retrieved. If they were, this is a generation failure: the model contradicted or blended beyond its context. The fix is verification — decompose into claims, check each against the cited source, reject or escalate on mismatch — plus instructions that forbid using outside knowledge, and a schema forcing a per-claim `source_id`.

**Strong Senior answer.** Points out that a real citation makes the answer *more* persuasive, so citation presence must never be treated as verification. Notes the cheapest check first: does the cited ID even appear in the retrieved set? That catches pure fabrication for free.

**Staff-level extension.** Routes commitment-bearing outputs (amounts, dates, entitlements) to human review by policy, because the cost asymmetry justifies the latency, and tracks the ungrounded rate as a monitored budget.

**Common mistakes.** Assuming any citation implies grounding; jumping to a bigger model rather than diagnosing the stage.

### Question 3 — How do you evaluate whether your guardrails work?

**Expected answer.** With a labelled evaluation set that includes unanswerable questions, scoring groundedness per claim rather than per answer, and calibrating any model-based judge against human labels before trusting its number. Track abstention rate alongside accuracy, since improving one at the cost of the other is the easiest way to look better and be worse.

**Strong Senior answer.** Names the correlated-error problem — judging with the same model family that generated the answer hides shared blind spots — and the whole-answer scoring trap, where one fabricated claim among four good ones still scores as grounded.

**Staff-level extension.** Treats it as an SLO with an owner and a breach response, and argues for a shared evaluation harness so quality is comparable across teams rather than each team grading its own homework differently.

**Common mistakes.** Reporting a single accuracy number; evaluating only on questions the corpus can answer; trusting an uncalibrated judge.

## 16. Coding/Practice Exercises

1. Write the citation-existence check: given a response with `claims[].source_id` and the set of retrieved document IDs, reject any response citing an ID that was never retrieved. Add a metric.
2. Define a JSON Schema where `insufficient_context` is a first-class status and every claim requires a source. Feed it a deliberately unanswerable question and confirm the abstention path is representable.
3. Build a 30-question evaluation set in which 10 questions cannot be answered from the corpus. Score answer accuracy and abstention rate separately.
4. Implement per-claim scoring: split an answer into atomic claims and check each against its cited source. Compare the result with a whole-answer score on the same data.

## 17. Debugging Exercises

1. An answer is wrong. Using only your logs, determine whether the correct document was retrieved. If you cannot, fix the logging first — that is the actual finding.
2. Groundedness reports 97% while users report errors. Find the scoring granularity and the judge's model family, and explain how each could produce that gap.
3. A system abstains on questions it should answer. Determine whether the cause is retrieval returning nothing relevant or instructions that are too strict, and say what evidence distinguishes them.

## 18. Design Exercises

1. Design the guardrail stack for an assistant that can issue refunds up to $50 automatically. State which layer catches which failure and where a human enters.
2. Design evaluation for a medical-information assistant where abstention is strongly preferred to a wrong answer. Define the metrics and the thresholds.
3. You are asked to cut LLM spend by 40% and the groundedness judge is the largest line item. Propose a sampling and routing strategy that keeps a usable quality signal, and state what guarantee you lose.

## 19. Further Reading

- [RAG and Vector Databases](rag-and-vector-databases.md) — the retrieval half of grounding.
- [LLM Evaluation and Testing](llm-evaluation-and-testing.md) — dataset and scoring discipline.
- [Prompt Injection and Agentic Security](prompt-injection-and-agentic-security.md) — adversarial context as a distinct threat.
- [Prompt Engineering Patterns](prompt-engineering-patterns.md) — instruction design that carries abstention pressure.
- [Context Window Management, Token Budgets, and Cost](context-window-management-token-budgets-and-cost.md) — why retrieved context has to be selected, not merely gathered.

## 20. Mastery Checklist

- [ ] Can define hallucination and groundedness precisely, and explain why the second is measurable and the first is not.
- [ ] Can name the guardrail layers and, for each, what it misses.
- [ ] Can distinguish a retrieval failure from a generation failure using logs.
- [ ] Can explain why schema-constrained output guarantees shape and not truth.
- [ ] Can design an evaluation set that measures abstention, not only accuracy.
- [ ] Can state the cost and latency of each guardrail and order them accordingly.
- [ ] Can argue where a human belongs in the loop, in terms of irreversibility rather than confidence.
