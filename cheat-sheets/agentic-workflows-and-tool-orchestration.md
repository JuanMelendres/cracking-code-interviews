---
title: "Cheat Sheet: Agentic Workflows and Tool Orchestration"
slug: agentic-workflows-and-tool-orchestration
document_type: cheat-sheet
domain: 22-ai-llm-engineering
topic_id: T-2304
canonical: ../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md
last_updated: 2026-09-12
---

# Agentic Workflows and Tool Orchestration

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md`](../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md)

## Core Mental Model

An agentic workflow is a loop: ask the model what to do next, do it, tell the model what happened, repeat — until the model says it's done or a safety limit stops it. Each pass is one real API round-trip; each `tool_use` response is one real decision point.

## Essential Definitions

- **Orchestration** — the loop that runs each step's output as the next step's input, when a later call genuinely depends on an earlier one's real result.
- **`maxIterations`** — a real, required safety cap preventing an unresolvable task from looping (and billing) forever.
- **Independent tool calls** — calls with no data dependency between them; safe and beneficial to run concurrently.

## Decision Table

| Need | Mechanism |
|---|---|
| A later tool call's input depends on an earlier call's real output | Sequential orchestration — must run in order |
| Multiple tool calls with no dependency between them | Parallel execution (real, measured ~2x wall-clock speedup) |
| Prevent an unresolvable task from looping forever | A real `maxIterations` cap, aborting instead of looping |
| Trust boundary for tool arguments | Treat model-chosen tool arguments as untrusted input, same as any external input |

## Common Pitfalls

- Shipping an agentic loop with no iteration cap — a task that never resolves runs forever, each iteration a real, billed round-trip.
- Running independent tool calls sequentially out of habit — real, measured 2x wall-clock cost for zero benefit.
- Running dependent tool calls in parallel — a correctness bug, not a performance choice, since the second call's input doesn't exist yet.
- Treating a tool's model-chosen arguments as trusted input.

## Interview Answer Skeleton

**30-sec:** An agentic loop repeatedly asks the model what to do, executes real tool calls, and feeds results back — bounded by a real iteration cap. Independent tool calls should run in parallel; dependent ones must run sequentially.

**2-min:** Add: a real three-iteration demo shows the second tool call's argument only exists because the first call's real output arrived first — the actual reason this is orchestration, not just "call a few tools." A real measured 408ms (sequential) vs. 207ms (parallel) for two independent calls proves the real ~2x speedup, not just claims it.

**Staff-level framing:** Termination is a real safety and cost concern at Staff scope — an unbounded agentic loop is a real, billable runaway-cost risk, not just a theoretical edge case, and needs a concrete cap, not just "the model will figure out to stop."

## Related

- syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md
- syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md
