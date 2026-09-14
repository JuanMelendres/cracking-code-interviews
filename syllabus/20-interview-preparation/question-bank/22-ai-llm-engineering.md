---
title: "Interview Question Bank — 22-ai-llm-engineering"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-14
related:
  - ../../22-ai-llm-engineering/INDEX.md
  - 21-frontend-web-nextjs.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — AI / LLM Engineering

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline. **This is the final domain in the 22-domain
plan** — see `00-project/interview-question-bank-plan.md` for the full initiative
summary.

**Honest count for this domain:** 6 chapters, each using the older numbered
`## 15. Interview Questions` template with a Junior→Staff leveled Q&A set (5 questions
per chapter, one per tier from Junior/Mid through Staff) and **no Flashcards section at
all** — the same pattern as `14-devops-containers`' Junior Fundamentals chapter, but
applied uniformly across every chapter in this domain, since this is a genuinely new
domain (added 2026-09-09) built with the leveled format from the start rather than
retrofitted. 6 chapters × 5 leveled questions = **30 real questions**. No separate
deep-question/quick-fire split exists here — each chapter's own 5 questions already
span the full seniority ladder directly, so they're presented as one leveled set per
chapter rather than forced into this compendium's usual 4-tier "What's expected"
breakdown.

---

## LLM API Integration Fundamentals

**Canonical treatment:** [§15, Interview Questions](../../22-ai-llm-engineering/llm-api-integration-fundamentals.md#15-interview-questions)

### Q1 (Junior/Mid) — Why does a multi-turn chat feature's cost grow with conversation length, even if each new message is short?

**What's expected:** The API is stateless — every request resends the entire conversation so far, so input token count (and cost) grows with every turn, not just with new-message length.

### Q2 (Mid) — What's the practical benefit of streaming a response instead of waiting for the whole thing?

**What's expected:** Dramatically better perceived latency for a user-facing feature — output starts appearing immediately instead of after the full generation completes, even though total generation time is the same either way.

### Q3 (Mid/Senior) — Walk through what happens, step by step, when a model decides to call a tool.

**What's expected:** The response comes back with `stop_reason: "tool_use"` and a tool-use block naming the function and arguments; the model has not executed anything; the backend must run the real function itself and send the result back as a `tool_result` message in a second request to get the model's final answer.

### Q4 (Senior) — Your API returns a 429. What does a correct client do?

**What's expected:** Read the real `retry-after` header and wait at least that long before retrying, rather than retrying immediately or using a hardcoded/guessed delay.

### Q5 (Senior/Staff) — A chat feature's LLM costs are 5x the original projection. Where do you look first?

**What's expected:** Check whether conversation history is growing unbounded and being resent in full on every turn; propose truncation, summarization, or a retrieval-based approach instead of ever-growing full-history resend.

---

## Embeddings

**Canonical treatment:** [§15, Interview Questions](../../22-ai-llm-engineering/embeddings.md#15-interview-questions)

### Q1 (Mid) — What is an embedding, in plain terms?

**What's expected:** A fixed-length vector representing a piece of text, such that texts with similar meaning produce vectors that are numerically close together under some similarity metric.

### Q2 (Mid/Senior) — Why isn't "these two sentences share a lot of words" the same thing as "these two sentences mean the same thing"?

**What's expected:** Word overlap and semantic similarity are different signals — a real trained embedding model captures meaning even without shared vocabulary (fixing the paraphrase problem) and distinguishes different meanings of a shared word by context (fixing the polysemy problem); a naive overlap-based scheme does neither.

### Q3 (Senior) — Why would increasing an embedding's dimension count improve retrieval quality?

**What's expected:** More dimensions reduce noise/collision between unrelated content (a true-negative pair's score dropping sharply while a true-positive pair's score stays roughly stable as dimensions increase), improving the separation a similarity threshold or ranking can rely on.

### Q4 (Senior/Staff) — Can you directly compare an embedding produced by Model A with one produced by Model B?

**What's expected:** No — a vector's numbers are only meaningful relative to the specific model (and dimension configuration) that produced it; there is no general cross-model comparability guarantee.

### Q5 (Staff) — An embedding provider announces a new model version. What's the real operational concern?

**What's expected:** Every previously-embedded document needs re-embedding before old and new vectors can be safely compared together — a real, potentially large-scale migration, not a transparent drop-in upgrade; pinning and explicitly tracking the model version in use is how a team controls when that migration happens rather than being forced into it silently.

---

## RAG and Vector Databases (pgvector)

**Canonical treatment:** [§15, Interview Questions](../../22-ai-llm-engineering/rag-and-vector-databases.md#15-interview-questions)

### Q1 (Mid) — What is Retrieval-Augmented Generation, and why does it exist?

**What's expected:** Retrieve the real documents relevant to a question and put them in the LLM's prompt as context, because the model has no access to your data and no memory of anything past its training cutoff otherwise.

### Q2 (Mid/Senior) — Why does cosine distance matter specifically for comparing text embeddings, versus plain Euclidean distance?

**What's expected:** Cosine distance (or L2 on pre-normalized vectors) is length-independent; raw L2 on unnormalized vectors is sensitive to document length/magnitude in a way that distorts relevance ranking.

### Q3 (Senior) — How does an ANN index like HNSW make vector search fast at scale, and what does it give up to do that?

**What's expected:** Builds a navigable graph structure so search only compares against a small neighborhood instead of every row, at the cost of being approximate — it can miss the true nearest neighbor in exchange for real, large speed at scale.

### Q4 (Senior/Staff) — A RAG feature's answers are technically fluent but often factually wrong. Where do you look first?

**What's expected:** Retrieval quality, not the LLM itself — check whether the retrieved context actually contains the relevant information; a wrong or irrelevant top-k means the model is being asked to answer from context that doesn't actually help.

### Q5 (Staff) — Would you put vectors in your existing Postgres via pgvector, or stand up a dedicated vector database? What decides it?

**What's expected:** `pgvector` for fewer moving parts and workloads that fit in an existing Postgres instance; a dedicated vector database when scale, query patterns, or operational requirements genuinely exceed what a general-purpose database extension can serve well — the same storage-selection reasoning applied to any new workload type.

---

## Prompt Engineering Patterns

**Canonical treatment:** [§15, Interview Questions](../../22-ai-llm-engineering/prompt-engineering-patterns.md#15-interview-questions)

### Q1 (Mid) — What's the real difference between zero-shot and few-shot prompting?

**What's expected:** Few-shot includes real worked examples (input/output pairs) in the same request, giving the model a concrete pattern to follow; zero-shot asks directly with no examples. Few-shot costs real, additional input tokens on every call.

### Q2 (Mid/Senior) — What does the temperature parameter actually control?

**What's expected:** It scales the probability distribution used for next-token sampling — lower values push toward the single most likely output (more deterministic); higher values genuinely raise the odds of a lower-probability but still valid continuation being picked (more varied).

### Q3 (Senior) — Your downstream code needs to parse the model's output as JSON. What's the reliable way to guarantee that?

**What's expected:** Use the API's real structured-output/`response_format` parameter (constrained decoding), not prompt wording alone — wording only influences probabilities, while a real structured-output mode removes invalid tokens from being sampled at all.

### Q4 (Senior/Staff) — Why does it matter whether an instruction is in the system role versus embedded in the user message?

**What's expected:** The system role is a structurally separate, higher-trust channel; instructions and untrusted user content sharing one channel are vulnerable to the user content overriding or contradicting the instruction — a real, structural prompt-injection risk, not just a style preference.

### Q5 (Staff) — How do you know a prompt change didn't regress production output quality?

**What's expected:** Prompts need the same version-control and regression-testing discipline as code — a real evaluation suite run against prompt changes before they ship, not ad hoc dashboard edits and hoping.

---

## Agentic Workflows and Tool Orchestration

**Canonical treatment:** [§15, Interview Questions](../../22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md#15-interview-questions)

### Q1 (Mid) — What's the difference between a single tool call and an agentic workflow?

**What's expected:** A single tool call is one request/response round trip; an agentic workflow is a loop of several such round trips, where the model decides what to do next based on the real results of previous steps, continuing until it has enough information or a safety limit stops it.

### Q2 (Mid/Senior) — Why does an agentic loop need a maximum iteration count?

**What's expected:** Without one, a task the model can never resolve (or a bug in the orchestration logic) causes the loop to run indefinitely, with each iteration a real, billed API round-trip — a real, unbounded cost and latency risk, not just a theoretical one.

### Q3 (Senior) — When is it safe to execute two tool calls in parallel, and when is it not?

**What's expected:** Safe when neither call's input depends on the other's output AND there's no shared side effect between them; unsafe if either condition is violated — data independence alone isn't sufficient if the calls have a side-effect conflict.

### Q4 (Senior/Staff) — How would you estimate the cost of an agentic feature before shipping it?

**What's expected:** Cost scales with the real number of round-trips a typical task takes, multiplied by each round-trip's real token cost — worth measuring the real iteration-count distribution across representative tasks, not just the cost of a single call, since the loop can silently multiply cost far beyond a single-call estimate.

### Q5 (Staff) — An agentic system has tool access to real actions (sending emails, modifying records). What's the real organizational concern beyond correctness?

**What's expected:** Every tool call is a real, potentially autonomous action across a multi-step loop, not a single human-reviewed step — tool permissions, argument validation, and a real audit trail of what actually happened become required, not optional, once the loop has access to anything with genuine side effects.

---

## LLM Evaluation and Testing

**Canonical treatment:** [§15, Interview Questions](../../22-ai-llm-engineering/llm-evaluation-and-testing.md#15-interview-questions)

### Q1 (Mid) — Why doesn't a normal `assertEquals`-style unit test work well for LLM output?

**What's expected:** An LLM call can produce several different, equally correct outputs for the same input — exact-match testing fails correct answers purely from wording variation, a real, direct consequence of non-determinism and multiple valid phrasings.

### Q2 (Mid/Senior) — What's a real alternative to exact-match for evaluating free-text LLM output?

**What's expected:** Rule-based/structured checks for checkable properties, embedding-based semantic-similarity scoring against a reference answer, or LLM-as-judge for nuanced, non-mechanical criteria — chosen based on which is applicable to the specific task.

### Q3 (Senior) — Does semantic-similarity scoring fully solve the exact-match brittleness problem?

**What's expected:** No — it reduces it but introduces its own real false-negative risk (a real paraphrase with low word overlap can score below threshold even though it's correct), a structural limitation, not a bug to patch away entirely.

### Q4 (Senior/Staff) — Your team wants to ship a prompt change. What do you actually check before approving it?

**What's expected:** Run a real, curated golden-dataset suite covering representative cases, not just a couple of spot-checked examples — the real risk is a change that improves some cases while regressing others, which spot-checking the wrong cases will miss entirely.

### Q5 (Staff) — How do you know your golden dataset itself is still testing the right thing?

**What's expected:** A golden dataset needs to be actively maintained as product requirements evolve — a stale dataset can pass every case while no longer reflecting what "correct" actually means for the current product, a real, permanent maintenance obligation, not a one-time setup cost.

---

## Related

- [`21-frontend-web-nextjs.md`](21-frontend-web-nextjs.md)
- [`21-frontend-web-react.md`](21-frontend-web-react.md)
- [`21-frontend-web-foundations.md`](21-frontend-web-foundations.md)
- [`20-interview-preparation.md`](20-interview-preparation.md)
- [`06-databases.md`](06-databases.md)
- [`15-cloud.md`](15-cloud.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
