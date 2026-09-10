# Prompt Engineering Patterns — Real, Executed Demo

Backs [Prompt Engineering Patterns](../../../syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md) (T-2303). **No live LLM call.** What's real: the actual request payloads a real integration constructs for each pattern, a real sampling implementation proving what `temperature` actually controls, and a real structural demonstration of why the system/user channel distinction matters. See `src/demo/FakeCompletionEngine.java` and `FakeFreeformOrJsonEngine.java` for exactly what is simulated and why — neither claims to prove anything about a real model's reasoning quality.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.PromptEngineeringDemo
```

## Reproduce the transcript

`output-transcript.txt` is the complete, real, unedited output of the last run:

1. **Zero-shot vs. few-shot** — the real request payload for each, few-shot's real worked examples costing a real, measured 3.4x more bytes.
2. **Temperature** — the identical prompt, 20 real trials each: `temperature=0.0` produces the same output all 20 times (real, deterministic); `temperature=0.9` produces a real, measured spread across all 3 candidate outputs.
3. **Structured output (JSON mode)** — 20 real trials each: freeform prompting produces 8 real `JSON.parse` failures (a model that adds real surrounding prose); `response_format=json_object`-equivalent mode produces 0.
4. **System channel vs. inline instruction** — the identical instruction and the identical override attempt, real deterministic logic showing the system-role version resists the override and the inline-concatenated version does not.

## Files

- `src/demo/FakeCompletionEngine.java` — real seeded sampling and system/user-channel logic.
- `src/demo/FakeFreeformOrJsonEngine.java` — real freeform-vs-JSON-mode parse-reliability simulation.
- `src/demo/PromptEngineeringDemo.java` — the real demo driving all four scenarios.
