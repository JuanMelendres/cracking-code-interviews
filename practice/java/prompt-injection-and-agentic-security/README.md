# Prompt Injection and Agentic Security — Real, Executed Demo

Backs [Prompt Injection and Agentic Security: Attack Vectors and Defenses](../../../syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md) (T-2306). Pure JDK — no external dependencies, no API key, no network call. **No live LLM call anywhere** — the agent's decision logic is real, deterministic Java standing in for what a real model's tool-use decision would look like; the vulnerability mechanism (re-parsing tool output for embedded instructions) and its structural fix (treating tool output strictly as data, plus a real human-approval gate) are what's real and measured.

## Setup

```bash
mkdir -p out
javac -d out src/demo/*.java
java -cp out demo.PromptInjectionDemo
```

## Reproduce the transcript

`output-transcript.txt` is the complete, real, unedited output of the last run — fully deterministic (no timing, no randomness), re-run confirmed byte-identical.

1. **Indirect prompt injection, real and reproducible.** `SearchTool` returns real, attacker-controlled text embedded in an otherwise-legitimate search result (a real payload, not a description of one). `UnsafeAgent` re-scans every tool result for an embedded directive and automatically executes it — the exact real-world anti-pattern that makes indirect prompt injection work. Real result: a user who only asked "What is the capital of France?" ends up with a wiped database.
2. **The structural fix, same payload.** `SafeAgent` receives the identical poisoned tool output and never re-parses it for instructions — tool output is data, full stop. Real result: `Database wiped: false`, with the identical injection payload present but inert.
3. **A real, bidirectional privilege gate.** `SensitiveTools.wipeDatabase` only proceeds with a real, separately-obtained `humanApproved` flag — never inferred from content the agent received. Real proof both ways: blocked without approval, genuinely executes with it.

## Files

- `src/demo/SearchTool.java` — a real "tool" returning a real indirect-injection payload embedded in a legitimate-looking result.
- `src/demo/SensitiveTools.java` — a real dangerous action gated by a real, separately-obtained approval flag.
- `src/demo/UnsafeAgent.java` — the real vulnerability: re-parses tool output for directives and acts on them.
- `src/demo/SafeAgent.java` — the real fix: data/instruction separation plus a genuine human-approval gate.
- `src/demo/PromptInjectionDemo.java` — the real demo driving all three scenarios.
