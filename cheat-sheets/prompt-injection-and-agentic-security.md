---
title: "Cheat Sheet: Prompt Injection and Agentic Security"
slug: prompt-injection-and-agentic-security
document_type: cheat-sheet
domain: 22-ai-llm-engineering
topic_id: T-2306
canonical: ../syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md
last_updated: 2026-09-21
---

# Prompt Injection and Agentic Security

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md`](../syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md)

## Core Mental Model

Any content an agent reads from an external source (tool output, a document, a search result) is a channel an attacker can use to smuggle instructions in — without ever touching the user's own prompt. The structural fix is refusing to treat tool output as anything but data, ever.

## Essential Definitions

- **Direct prompt injection** — attacker text in the user-facing prompt itself.
- **Indirect prompt injection** — malicious instructions embedded in content the agent retrieves on its own (tool results, documents, search results) — the larger, less visible real attack surface in agentic systems.
- **Human-approval gate** — a real, separately-obtained approval flag for sensitive actions, genuinely unforgeable by the agent itself.

## Decision Table

| Defense | What it actually does | Why it's stronger/weaker |
|---|---|---|
| Filter/detect suspicious phrases in tool output | Pattern-matches known injection wording | Weak — an arms race an attacker routes around by rephrasing |
| Never re-parse tool output for instructions | Structural: tool output is always data | Strong — nothing for a rephrased instruction to be interpreted by |
| Human-approval gate for sensitive actions | Requires a real, separate approval channel | Strong defense-in-depth for consequential/irreversible actions |

## Common Pitfalls

- Defending against injection by filtering suspicious phrases instead of structurally refusing to re-interpret tool output as instructions.
- Treating direct (user-prompt) injection as the whole threat model, missing indirect injection's larger real surface.
- Gating a sensitive action behind a check the agent itself can satisfy by generating the right-looking text.

## Interview Answer Skeleton

**30-sec:** Indirect prompt injection arrives through tool output, not the user's prompt — the structural fix is never re-parsing tool output for instructions, plus a real, separately-obtained human-approval gate for consequential actions.

**2-min:** Add: real demo proof — an unsafe agent that re-parses tool output genuinely wipes a database from an injected search result; the identical payload against an agent that treats tool output strictly as data leaves it untouched; a real approval gate blocks without approval and allows with it.

**Staff-level framing:** Which actions in an agentic system need a genuinely unforgeable approval gate — and how "unforgeable" is enforced across every tool and every hop in a multi-agent pipeline — is a real system-wide privilege-design question, not a single security review checkbox.

## Related

- syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md
- syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md
- syllabus/12-security/injection-input-validation-output-encoding.md
