# Agentic Workflows and Tool Orchestration — Real, Executed Demo

Backs [Agentic Workflows and Tool Orchestration](../../../syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md) (T-2304). Pure JDK — no external dependencies, no API key, no network call. **No live LLM call anywhere** — the agent's next-action decisions are real, deterministic Java logic standing in for what a real model's `tool_use` decision would look like; the loop mechanics (multi-step chaining, termination safety, parallel vs. sequential timing) are what's real and measured.

## Setup

```bash
mkdir -p out
javac -d out src/demo/*.java
java -cp out demo.AgenticWorkflowDemo
```

## Reproduce the transcript

`output-transcript.txt` is the complete, real, unedited output of the last run:

1. **Multi-step tool chain** — a real 3-iteration loop where the second tool call's real input (`"Paris"`) is the first tool call's real output — genuine sequential dependency, not two independent calls.
2. **Runaway-loop safety** — a task deliberately built so the tool never returns a resolvable answer; a real `maxIterations` cap (5) stops the loop instead of running forever, with the real iteration count logged.
3. **Sequential vs. parallel tool execution** — two independent tool calls (no data dependency), each with a real 200ms simulated latency (`Thread.sleep`, standing in for real network time). Real, measured wall-clock: ~408ms sequential vs. ~207ms parallel (via a real `ExecutorService`) — a real, measured ~2.0x speedup.

## Files

- `src/demo/Tools.java` — the real "tools" (capital lookup, weather lookup, a deliberately unhelpful tool for the safety demo), each with real, deliberate latency.
- `src/demo/AgenticWorkflowDemo.java` — the real demo driving all three scenarios.
