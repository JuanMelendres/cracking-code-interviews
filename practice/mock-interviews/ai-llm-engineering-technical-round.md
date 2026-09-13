---
title: "Mock Interview: AI/LLM Engineering Technical Round (45 min)"
slug: ai-llm-engineering-technical-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-09-13
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - Conversation-growth cost mechanic and cost-control levers
  - RAG failure-mode diagnosis (retrieval vs. generation)
  - Cross-model embedding non-comparability
  - System-role vs. user-role trust boundary and prompt injection
  - Agentic-loop cost estimation
  - Golden-dataset regression discipline before a prompt ships
  - Production/technical story
related:
  - ../../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md
  - ../../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md
  - ../../syllabus/22-ai-llm-engineering/embeddings.md
  - ../../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md
  - ../../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md
  - ../../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md
  - ../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md
source: null
official_references: []
---

# Mock Interview: AI/LLM Engineering Technical Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

**Sourcing note.** Unlike every other round in this deliverable, this one has no prior `study-packs/` mock-interview file to elevate — `22-ai-llm-engineering` postdates every study-pack program in this repository. Built fresh, but not invented: every question below is one of the 6 canonical chapters' own already-written Interview Questions (Section 15 of each), each already carrying a real "Expected answer" grounded in that chapter's own real, executed demo. This round's own contribution is the Mock Interview Standard structure around them — competencies table, interviewer script, pass/borderline/fail signals, scoring, debrief, remediation — not new technical content. This closes the mock-interview gap the same audit method already closed for `study-packs/`, `cheat-sheets/`, and `production-cookbook/` this session.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| Conversation-growth cost mechanic and cost-control levers | Q1 | [LLM API Integration Fundamentals](../../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md) |
| RAG failure-mode diagnosis (retrieval vs. generation) | Q2 | [RAG and Vector Databases](../../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md) |
| Cross-model embedding non-comparability | Q3 | [Embeddings](../../syllabus/22-ai-llm-engineering/embeddings.md) |
| System-role vs. user-role trust boundary | Q4 | [Prompt Engineering Patterns](../../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md) |
| Agentic-loop cost estimation | Q5 | [Agentic Workflows and Tool Orchestration](../../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md) |
| Golden-dataset regression discipline | Q6 | [LLM Evaluation and Testing](../../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md) |
| Production/technical story | Q7 | [Production Incident Narratives](../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md) |

## Interviewer Opening Script

*"This is a 45-minute round on integrating LLMs into a backend system — API mechanics, retrieval, embeddings, prompting, agentic workflows, and evaluation. Several of these questions have a plausible-sounding answer that's actually incomplete, and a more precise, scoped one — I want the precise version, with the mechanism named, not just the conclusion. Let's start with cost."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(6 min)** "A chat feature's LLM costs are 5x the original projection. Where do you look first?"
2. **(7 min)** "A RAG feature's answers are technically fluent but often factually wrong. Where do you look first?"
3. **(6 min)** "Can you directly compare an embedding produced by Model A with one produced by Model B?"
4. **(6 min)** "Why does it matter whether an instruction is in the system role versus embedded in the user message?"
5. **(6 min)** "How would you estimate the cost of an agentic feature before shipping it?"
6. **(6 min)** "Your team wants to ship a prompt change. What do you actually check before approving it?"
7. **(8 min)** Deliver a production/technical story about a system you built or debugged, using the four-beat structure — an LLM-related story if you have one, any real technical story otherwise.

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — LLM costs 5x the original projection

**Ideal answer outline:** the API is stateless — every request resends the *entire* conversation so far, so input token count (and cost) grows with every turn, not just with new-message length. Check first whether conversation history is being resent in full on every turn with no bound. The fix is truncation, summarization, or a retrieval-based approach (this domain's own RAG topic) instead of an ever-growing full-history resend, plus considering model tiering (cheaper models for simple requests). If the candidate jumps straight to "switch to a cheaper model" without checking the conversation-growth mechanic first, the correct push is: "before changing models — is anything about how much you're sending on each call unusual?"
**Common weak answers:** assuming the fix must be a cheaper model or a rate limit, without ever checking whether conversation history is growing unbounded.
**Pass signal:** names conversation-history resend as the first thing to check, and proposes truncation/summarization/retrieval as the fix, either unprompted or promptly under the push.
**Borderline signal:** eventually agrees conversation growth is worth checking under the push, but doesn't propose a concrete fix.
**Fail signal:** never considers conversation-history growth even after the push, defaulting only to "use a cheaper model."

### Question 2 — RAG answers are fluent but often wrong

**Ideal answer outline:** check retrieval quality, not the LLM itself, first — a wrong or irrelevant top-k means the model is being asked to answer from context that doesn't actually help, and a fluent-sounding wrong answer is exactly what happens when a capable model is given the wrong material to work from. Inspect what was actually retrieved for a few failing queries before assuming a prompting or model-capability problem. If the candidate proposes a bigger/different model as the first fix, the correct push is: "have you looked at what documents were actually retrieved for one of these failures?"
**Common weak answers:** assuming the model itself is "hallucinating" and proposing a different or larger model as the first fix, without ever inspecting retrieval output.
**Pass signal:** names retrieval quality as the first thing to check, unprompted or promptly under the push, and describes inspecting actual retrieved context for failing cases.
**Borderline signal:** agrees retrieval is worth checking only after the push, with no concrete inspection method.
**Fail signal:** insists the model itself is at fault even after the push, with no consideration of retrieval quality.

### Question 3 — Comparing embeddings across models

**Ideal answer outline:** no — a vector's numbers are only meaningful relative to the specific model (and dimension configuration) that produced it; there is no general cross-model comparability guarantee. Two models can place semantically identical text in entirely different, non-comparable vector spaces. The practical consequence: switching embedding providers requires re-embedding the entire existing corpus before old and new vectors can be compared together at all — not a transparent drop-in swap. If the candidate says "yes, cosine similarity would work across models" the correct push is: "even if the two models were trained completely independently, with different architectures?"
**Common weak answers:** assuming any two embedding vectors can be meaningfully compared with cosine similarity regardless of which model produced them.
**Pass signal:** correctly states no general cross-model comparability, and names the re-embedding requirement as the practical consequence of a provider/model switch.
**Borderline signal:** correctly says no under the push, but doesn't reach the re-embedding-migration consequence unprompted.
**Fail signal:** insists cross-model comparison is valid even after the push.

### Question 4 — System role vs. user role

**Ideal answer outline:** the system role is a structurally separate, higher-trust channel for instructions; the user role carries the actual (often untrusted) input. Putting an instruction in the user message alongside untrusted content means both share one channel, which is vulnerable to the user content overriding or contradicting the instruction — a real, structural prompt-injection risk, not just a style preference. If the candidate treats this as purely a formatting/readability choice, the correct push is: "what happens if the user's own message tries to say 'ignore your previous instructions'?"
**Common weak answers:** treating system vs. user role placement as a stylistic or organizational preference with no security implication.
**Pass signal:** names the trust-boundary/prompt-injection risk unprompted or promptly under the push.
**Borderline signal:** recognizes a difference exists only after the push, without naming prompt injection specifically.
**Fail signal:** maintains it's purely stylistic even after the push.

### Question 5 — Estimating agentic feature cost

**Ideal answer outline:** cost scales with the real number of round-trips a typical task takes, multiplied by each round-trip's real per-call token cost — worth measuring the real iteration-count distribution across representative tasks, not just the cost of a single call, since the loop can silently multiply cost far beyond a single-call estimate. This connects directly to Question 1's conversation-growth mechanic, since each iteration in a long-running agentic loop may also be re-sending accumulated context. If the candidate estimates cost from a single representative call, the correct push is: "does a real task always resolve in exactly one round trip?"
**Common weak answers:** estimating cost from a single example call's token count, without accounting for the loop's iteration count at all.
**Pass signal:** names iteration count as a multiplier on per-call cost and proposes measuring it across representative tasks, either unprompted or promptly under the push.
**Borderline signal:** agrees iteration count matters only after the push, without proposing a concrete measurement approach.
**Fail signal:** insists a single-call estimate is sufficient even after the push.

### Question 6 — What to check before shipping a prompt change

**Ideal answer outline:** run a real, curated golden-dataset suite covering representative cases, not just a couple of spot-checked examples — the real risk is a change that improves some cases while regressing others, which spot-checking the wrong cases will miss entirely. This requires the same version-control and regression-testing discipline as code, treating a prompt change as a real behavior change. If the candidate says "I'd try a few examples and see if they look right," the correct push is: "what if it improves the three examples you tried but breaks a case you didn't think to check?"
**Common weak answers:** proposing to manually try a handful of examples and eyeball the results, with no systematic regression suite.
**Pass signal:** names a golden-dataset regression suite as the requirement, either unprompted or promptly under the push.
**Borderline signal:** agrees ad hoc spot-checking is insufficient only after the push, without naming a concrete regression-suite approach.
**Fail signal:** maintains manual spot-checking is sufficient even after the push.

### Question 7 — Production/technical story

**Ideal answer outline:** a four-beat, clearly structured story (situation, action, the specific decision criterion used, and the outcome/cost) about real technical work under real constraints.
**Common weak answers:** a story with no clear structure, or one that describes what changed without stating the specific reasoning behind the chosen approach.
**Pass signal:** clear four-beat structure with a specific decision criterion and outcome, scored per Technical Depth and Production Judgment.
**Borderline signal:** the story is coherent but the decision criterion has to be extracted through follow-up.
**Fail signal:** no clear structure, or no identifiable decision criterion even on request.

## Scoring Rubric

Score this round using the [shared six-dimension rubric](../../study-packs/week-01/10-week-1-evaluation-rubric.md)'s **Technical Depth** and **Production Judgment** dimensions specifically (1–5 scale, 3 = Mid, 4 = Senior, 5 = Staff) — the same two-dimension scope the [Kafka Messaging Technical Round](kafka-messaging-technical-round.md) and [Spring Technical Round](spring-technical-round.md) use, since this domain's Interview Questions sections name Mid/Senior/Staff expectation tiers rather than all six shared-rubric dimensions.

## Debrief Guide

Walk the candidate through their scores, starting with the weakest. Questions 1, 2, and 5 share the sharpest theme in this round: each has a plausible-sounding "obvious" first move (switch to a cheaper model; blame the model's fluency for a wrong answer; estimate cost from one call) that skips the actual mechanism this domain's chapters exist to teach (conversation-growth cost, retrieval-quality diagnosis, iteration-count multiplication) — a candidate who reaches for the obvious move on more than one of these without the push has a real pattern worth naming directly, not three unrelated misses. Questions 3 and 4 are both "common misconception" traps (cross-model embedding comparability; system-role-as-mere-style) that a candidate with only surface AI-feature exposure is likely to get wrong the first time; getting either right unprompted is a strong signal of real, hands-on integration experience rather than familiarity with the vocabulary alone.

## Remediation Recommendations

- Weak Q1 → re-read [LLM API Integration Fundamentals](../../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md), specifically its Section 4/13 conversation-growth cost mechanic.
- Weak Q2 → re-read [RAG and Vector Databases](../../syllabus/22-ai-llm-engineering/rag-and-vector-databases.md), specifically its retrieval-quality diagnosis material.
- Weak Q3 → re-read [Embeddings](../../syllabus/22-ai-llm-engineering/embeddings.md), specifically its cross-model comparability and re-embedding-migration discussion.
- Weak Q4 → re-read [Prompt Engineering Patterns](../../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md), specifically its system-role/user-role trust-boundary material.
- Weak Q5 → re-read [Agentic Workflows and Tool Orchestration](../../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md), specifically its cost-estimation material connecting back to T-2300's per-call cost mechanic.
- Weak Q6 → re-read [LLM Evaluation and Testing](../../syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md), specifically its golden-dataset regression-matrix demo.
- Weak Q7 → re-read [Production Incident Narratives](../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md) — the closest-fit chapter for a technical story, since this domain has no dedicated behavioral-handbook chapter and no `production-cookbook/` entry yet (a genuinely new domain, confirmed via each chapter's own `production_scenarios: []` front matter).
- Any dimension scored below Senior (4) overall → retake this mock in full after remediation.
