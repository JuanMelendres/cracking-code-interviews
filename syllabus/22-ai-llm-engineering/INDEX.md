---
title: "AI/LLM Engineering — Domain Index"
document_type: syllabus-domain-index
domain: 22-ai-llm-engineering
status: 6 of 6 originally-planned topics present (2026-09-10) — domain opened 2026-09-09, remains open to further topics
last_updated: 2026-09-12
---

# AI/LLM Engineering

LLM API integration, prompting, retrieval-augmented generation (RAG), vector databases, embeddings, and agentic patterns — a new backend domain, added 2026-09-09 during the same gap audit that produced `07-api-design`'s T-917/T-918 (GraphQL, gRPC). New topics in this domain are assigned IDs in the plan's reserved `T-2300`–`T-2399` range (see `00-project/syllabus-transformation-plan.md`).

> **Domain opened 2026-09-09.** Added as a new top-level domain rather than a subsection of `17-architecture` or `11-system-design` — the user's explicit choice, presented against those two alternatives, because the topic spans multiple independent concerns (API integration, retrieval infrastructure, agent orchestration, evaluation) too broad to fit either domain's existing scope without diluting it. Unlike `21-frontend-web`, this domain is part of the same Java-backend, Junior-through-Staff track as domains 01–20 — a Java backend engineer integrating an LLM API is still backend work, not a separate additive track. First chapter: [LLM API Integration Fundamentals](llm-api-integration-fundamentals.md) (T-2300), chosen as the foundational entry point because every later topic in this domain (RAG, vector databases, agents) assumes a reader already knows how to correctly call an LLM API — prompting, streaming, token/cost accounting, function calling, and rate-limit handling. Backed by a real, executed demo (`practice/java/llm-api-integration-fundamentals/`) exercising all four of those mechanics against a real local server implementing Anthropic's real Messages API wire contract.
>
> **Second chapter added 2026-09-10.** [RAG and Vector Databases (pgvector)](rag-and-vector-databases.md) (T-2301) — the natural next topic per T-2300's own note (RAG assumes a reader already knows how to call an LLM API). Backed by a real PostgreSQL 16 + `pgvector` container: real vector storage, real cosine/L2 distance queries, a real HNSW ANN index with real `EXPLAIN ANALYZE` evidence of a ~7x speedup at 5,000 rows, and — honestly, not glossed over — real output proving its own deterministic toy embedding scheme fails on a paraphrased query, used directly as the evidence for why production RAG needs a real trained embedding model.
>
> **Third chapter added 2026-09-10 (same day).** [Embeddings](embeddings.md) (T-2302) — T-2301's own note flagged its embedding-scheme limitation as deserving deeper treatment; this is that treatment. Backed by a real demo (pure JDK, no external dependency) computing real cosine similarity across four sentence-pair categories at two dimension counts, with a genuinely striking real finding: at 32 dimensions, a truly unrelated sentence pair scores 0.5634 — dangerously close to a truly related pair's 0.7591 — while at 512 dimensions the unrelated pair correctly drops to 0.0909. Also measures two distinct, opposite real failure modes (paraphrase under-scoring a real match; polysemy over-scoring a real mismatch) rather than just one.
>
> **Fourth chapter added 2026-09-10 (same day).** [Prompt Engineering Patterns](prompt-engineering-patterns.md) (T-2303) — organized around a real, useful distinction: which prompting patterns are just careful wording, and which are real, distinct request parameters. Backed by a real demo (`practice/java/prompt-engineering-patterns/`, no live model call): a real seeded sampler proving `temperature` genuinely controls output variance (20/20 identical at `temperature=0.0`, a real 3-way split at `0.9`), a real measured 8-vs-0 JSON-parse-failure gap between prompt-wording-only and a real structured-output parameter, and real deterministic logic showing a system-role instruction resists an override attempt that the identical instruction, concatenated into user text, does not.
>
> **Fifth chapter added 2026-09-10 (same day).** [Agentic Workflows and Tool Orchestration](agentic-workflows-and-tool-orchestration.md) (T-2304) — what happens when a task needs several chained tool calls (T-2300 covered one). Backed by a real demo (`practice/java/agentic-workflows-and-tool-orchestration/`, no live model call): a real 3-iteration multi-step chain where the second tool call's real argument only exists because the first tool call already ran; a real runaway-loop safety demo (a task that never resolves, correctly stopped by a real iteration cap); and a real, measured ~2x wall-clock speedup running two independent tool calls in parallel versus sequentially.
>
> **Sixth chapter added 2026-09-10 (same day) — original planned list now complete.** [LLM Evaluation and Testing](llm-evaluation-and-testing.md) (T-2305) — the real test-suite mechanism [Prompt Engineering Patterns](prompt-engineering-patterns.md)'s own Staff-level note said a prompt change needs. Backed by a real demo (`practice/java/llm-evaluation-and-testing/`, no live model call): real proof that `assertEquals`-style testing fails a genuinely correct answer purely from wording; a real, honest false negative in embedding-based similarity scoring (reusing T-2302's own mechanics) on a genuine paraphrase; and a real golden-dataset regression matrix that catches a real regression and a real improvement in the same run — exactly what spot-checking a couple of examples would miss. This closes the domain's originally-planned six-topic list; the domain itself remains open to further topics.

## Topics

| Topic ID | Title | Mastery levels covered | Location |
|---|---|---|---|
| T-2300 | [LLM API Integration Fundamentals](llm-api-integration-fundamentals.md) | L1, L2, L3, L4 — fully written, real demo (2026-09-09) | `syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md` |
| T-2301 | [RAG and Vector Databases (pgvector)](rag-and-vector-databases.md) | L1, L2, L3, L4 — fully written, real demo (2026-09-10) | `syllabus/22-ai-llm-engineering/rag-and-vector-databases.md` |
| T-2302 | [Embeddings](embeddings.md) | L1, L2, L3, L4 — fully written, real demo (2026-09-10) | `syllabus/22-ai-llm-engineering/embeddings.md` |
| T-2303 | [Prompt Engineering Patterns](prompt-engineering-patterns.md) | L1, L2, L3, L4 — fully written, real demo (2026-09-10) | `syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md` |
| T-2304 | [Agentic Workflows and Tool Orchestration](agentic-workflows-and-tool-orchestration.md) | L1, L2, L3, L4 — fully written, real demo (2026-09-10) | `syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md` |
| T-2305 | [LLM Evaluation and Testing](llm-evaluation-and-testing.md) | L1, L2, L3, L4 — fully written, real demo (2026-09-10) | `syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md` |

**Planned list complete.** This domain remains open — further topics (e.g., fine-tuning, multi-agent systems, cost optimization at scale) may be added later, using the next free ID in the `T-2300`–`T-2399` range.

> **Complementary-deliverable gap closed (2026-09-12).** User asked what else needed checking; a direct diff found all 6 chapters in this domain had zero cheat sheet and zero flashcard deck since they were written (2026-09-09/10) — this deliverable's batch process had simply never run against a domain this new. Closed in one pass: `cheat-sheets/README.md` and `flashcards/README.md` each gain 6 new entries (18 new flashcards total, 3 per deck), every fact extracted directly from each chapter's own Foundation/Core Concepts/Common Mistakes sections.

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md`'s 2026-09-09 extension note (in its Topic IDs subsection) for the full reasoning behind this domain's placement and ID range.
