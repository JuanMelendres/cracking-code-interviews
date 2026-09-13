---
title: "Mid → Senior, Week 12 — AI/LLM Engineering"
document_type: study-pack
week: 12
track: mid-to-senior
status: draft
estimated_hours: 10
---

# Week 12 — AI/LLM Engineering

## Weekly Outcome

By the end of this week you can integrate an LLM API correctly (streaming, retries, token accounting), explain what RAG actually solves and where it fails, describe embeddings and vector similarity with real measured numbers, apply at least two concrete prompt-engineering patterns, reason about tool-calling/agentic-loop failure modes, and explain why exact-match testing is the wrong default for evaluating LLM output.

## Why This Week Matters

This entire domain — six chapters, all self-declaring `interview_paths: [mid-to-senior, senior-to-staff, ...]` in their own front matter — had never been scheduled in any learning path. This week closes that gap. LLM-integrated features are now a real, common interview and production surface for backend engineers, not a specialist niche.

## Prerequisites

Week 5 (Testing, for the testing mindset `llm-evaluation-and-testing.md` extends into a non-deterministic-output context) and general REST API comfort from Junior → Mid (LLM APIs are consumed the same way any other HTTP API is).

## Schedule

| Day | Focus |
|---|---|
| Mon | LLM API Integration Fundamentals |
| Tue | RAG and Vector Databases |
| Wed | Embeddings |
| Thu | Prompt Engineering Patterns |
| Fri | Agentic Workflows and Tool Orchestration |
| Sat | LLM Evaluation and Testing |
| Sun | Review checklist below |

## Required Reading

[`syllabus/22-ai-llm-engineering/INDEX.md`](../../../syllabus/22-ai-llm-engineering/INDEX.md) — all six chapters in the order above (this domain's own dependency sequence: integration basics, then retrieval, then the embeddings mechanics retrieval depends on, then prompting, then orchestration, then evaluation).

## Hands-On Exercises

[`practice/java/llm-api-integration-fundamentals/`](../../../practice/java/llm-api-integration-fundamentals/), [`practice/java/rag-and-vector-databases/`](../../../practice/java/rag-and-vector-databases/), [`practice/java/embeddings-fundamentals/`](../../../practice/java/embeddings-fundamentals/), [`practice/java/prompt-engineering-patterns/`](../../../practice/java/prompt-engineering-patterns/), [`practice/java/agentic-workflows-and-tool-orchestration/`](../../../practice/java/agentic-workflows-and-tool-orchestration/), [`practice/java/llm-evaluation-and-testing/`](../../../practice/java/llm-evaluation-and-testing/) — follow the link from each chapter to its own matching demo.

## Production Cookbook Cross-Reference

None. This domain's six chapters each state `production_scenarios: []` in their own front matter — confirmed directly, not assumed — and no `production-cookbook/` entry currently matches this domain. Stated honestly rather than forcing a loose citation, per this pack's own established convention (see Mid → Senior Week 8's equivalent note).

## Interview Answer Drills

Answer, out loud: "why doesn't exact-match assertion work for testing LLM output, and what do you use instead" and "what specific failure does RAG solve that a larger context window alone doesn't" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week — this week's hands-on exercises are the six chapters' own real, executed demos (API integration, retrieval, embedding similarity, prompt patterns, tool orchestration, evaluation harness), not separate coding-interview problems.

## System Design Exercise

Design a RAG-backed support-chat feature: chunking strategy, embedding model choice, vector store, retrieval-then-generation flow, and specifically how you'd evaluate its output quality before shipping — using this week's own evaluation chapter's method, not exact-match assertions.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described "our chatbot gave a confidently wrong answer" incident, name at least two distinct root causes this week's chapters cover (a retrieval miss, a prompt-injection-adjacent framing issue, an untested prompt regression) and how you'd diagnose which one actually occurred, out loud, in under 5 minutes.

## Review Checklist

- [ ] Completed all six chapters' own Mastery Checklists.
- [ ] Reproduced all six chapters' own real, executed demos listed above.
- [ ] Confirmed directly (not assumed) that no cookbook cross-reference exists for this domain yet.

## Completion Criteria

- [ ] Can integrate an LLM API with correct streaming/retry/token-accounting handling.
- [ ] Can explain what RAG solves and its real failure modes (retrieval miss, stale index, chunking mismatch).
- [ ] Can explain embedding similarity with real measured numbers, not just "closer vectors are more similar."
- [ ] Can name and apply at least two prompt-engineering patterns with a stated reason for each.
- [ ] Can explain a real tool-calling/agentic-loop failure mode and its mitigation.
- [ ] Can explain why exact-match testing fails for LLM output and what replaces it.

## Retrospective

Note that this is a genuinely new-writing domain (added 2026-09-09/10) with no production-cookbook entries yet — if a real incident involving an LLM-integrated feature is encountered on the job, it's a strong, concrete candidate for this deliverable's next real batch, not a hypothetical to invent here.

## Next Week

This is the final week of the Mid → Senior program. Continue to [Senior → Staff](../../senior-to-staff/README.md).
