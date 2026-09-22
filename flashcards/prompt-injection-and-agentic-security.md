---
title: "Flashcards: Prompt Injection and Agentic Security"
slug: prompt-injection-and-agentic-security
document_type: flashcard-deck
domain: 22-ai-llm-engineering
topic_id: T-2306
canonical: ../syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md
last_updated: 2026-09-21
---

# Flashcards: Prompt Injection and Agentic Security

**Canonical chapter:** [`syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md`](../syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md)

## Card: Direct vs. indirect prompt injection

**Prompt:**
What's the difference between direct and indirect prompt injection, and why is indirect injection more dangerous in an agentic system?

**Answer:**
Direct injection is attacker text in the user-facing prompt itself. Indirect injection arrives embedded in content the agent retrieves on its own — a tool result, a document, a search result — with no direct attacker interaction at all. It's more dangerous in agentic systems because an agent's own tool-calling loop routinely feeds tool output back into the same context used to decide the next action, giving an injected instruction a real channel a plain chatbot doesn't have.

**Why it matters:**
`llm-api-integration-fundamentals.md` already names prompt injection as a real risk category but never explained this distinction.

**Common trap:**
Only reasoning about securing the user's own prompt, missing indirect injection's larger real attack surface.

**Related:**
[Core Concepts](../syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md#4-core-concepts-l2)

## Card: Why filtering is weaker than structural refusal

**Prompt:**
Why is filtering tool output for suspicious phrases a weaker defense than never re-parsing tool output for instructions at all?

**Answer:**
A filter is an arms race an attacker can route around by rephrasing the injected instruction. A structural refusal to treat tool output as anything but data has nothing for a rephrased instruction to be interpreted by, regardless of wording.

**Why it matters:**
The real, structural fix this chapter's demo implements and measures directly.

**Common trap:**
Proposing a more sophisticated filter as the fix, rather than recognizing filtering itself as the weaker category of defense.

**Related:**
[Core Concepts](../syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md#4-core-concepts-l2)

## Card: Real measured evidence — the attack and its fix

**Prompt:**
What did a real demo prove about indirect prompt injection and its defense?

**Answer:**
A `SearchTool`'s own output carried a real embedded payload. An `UnsafeAgent` that re-parsed tool output for directives genuinely wiped a simulated database (`Database wiped: true`) from a user asking only a geography question. The identical payload against a `SafeAgent` that never re-parses tool output left it untouched (`Database wiped: false`). A separate human-approval gate was proven real in both directions: genuinely blocked without approval, genuinely allowed with it.

**Why it matters:**
Concrete, reproducible, deterministic evidence rather than an assertion about how prompt injection "could" work.

**Common trap:**
Assuming this class of vulnerability is theoretical or model-specific rather than an architectural property of how tool output is handled.

**Related:**
[Examples](../syllabus/22-ai-llm-engineering/prompt-injection-and-agentic-security.md#7-examples)
