---
title: "Flashcards: Agentic Workflows and Tool Orchestration"
slug: agentic-workflows-and-tool-orchestration
document_type: flashcard-deck
domain: 22-ai-llm-engineering
topic_id: T-2304
canonical: ../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md
last_updated: 2026-09-12
---

# Flashcards: Agentic Workflows and Tool Orchestration

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md`](../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md)

## Card: Why an iteration cap is non-negotiable

**Prompt:**
What happens to an agentic loop with no iteration limit, given a task the model can never resolve?

**Answer:**
It keeps calling tools — and keeps paying for tokens — forever. Real evidence: a deliberately unresolvable task, capped at `maxIterations=5`, aborts after exactly 5 real iterations instead of looping indefinitely.

**Why it matters:**
A real, billable runaway-cost risk, not a theoretical edge case — this is a required safety mechanism, not an optional nicety.

**Common trap:**
Shipping an agentic loop assuming the model will always eventually stop on its own.

**Related:**
[Agentic Workflows and Tool Orchestration](../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md)

## Card: Sequential vs. parallel tool calls

**Prompt:**
Two tool calls have no data dependency between them. Should they run sequentially or in parallel?

**Answer:**
In parallel — real, measured evidence shows 408ms sequential versus 207ms parallel for the identical two calls, a real ~2x speedup. Running independent calls sequentially is a real, avoidable cost with no benefit.

**Why it matters:**
A concrete, measured performance argument, not a theoretical "concurrency is faster" claim.

**Common trap:**
Running every tool call sequentially out of habit, even when no dependency requires it.

**Related:**
[Agentic Workflows and Tool Orchestration](../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md)

## Card: When parallel tool calls are a correctness bug

**Prompt:**
If tool call B's argument depends on tool call A's real result, is running them in parallel just a missed performance optimization?

**Answer:**
No — it's a correctness bug, not a performance choice. Call B's input genuinely doesn't exist until call A's real result arrives; running them in parallel means B either can't run or runs with a wrong/missing argument.

**Why it matters:**
Distinguishes a real dependency (must be sequential) from independence (safe to parallelize) — conflating the two breaks correctness, not just speed.

**Common trap:**
Parallelizing tool calls indiscriminately for a perceived speedup without checking for data dependencies first.

**Related:**
[Agentic Workflows and Tool Orchestration](../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md)
