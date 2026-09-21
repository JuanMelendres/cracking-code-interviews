---
title: "Flashcards: Sprint Retrospectives"
slug: sprint-retrospectives
document_type: flashcard-deck
domain: 18-engineering-practices
topic_id: T-1806
canonical: ../syllabus/18-engineering-practices/sprint-retrospectives.md
last_updated: 2026-09-21
---

# Flashcards: Sprint Retrospectives

**Canonical chapter:** [`syllabus/18-engineering-practices/sprint-retrospectives.md`](../syllabus/18-engineering-practices/sprint-retrospectives.md)

## Card: What retro theater is

**Prompt:**
What's "retro theater," and how would you recognize it on a team?

**Answer:**
A retrospective that produces the ritual of naming problems without the substance of fixing them — recognizable by the same complaints recurring sprint after sprint, usually because action items were written with no explicit owner or deadline and never revisited.

**Why it matters:**
`sdlc-and-agile-methodology-fundamentals.md` names retrospectives three times without ever explaining this specific, common failure mode.

**Common trap:**
Vaguely saying retros "can feel pointless sometimes" instead of naming the actual mechanism.

**Related:**
[Why This Matters](../syllabus/18-engineering-practices/sprint-retrospectives.md#1-why-this-matters)

## Card: Why every action item needs an owner AND a deadline

**Prompt:**
Why does a retrospective action item need both an explicit owner and an explicit deadline?

**Answer:**
Without an explicit owner, the item is nobody's job by default and quietly becomes nobody's job in practice. Without a deadline, there's no moment at which its absence becomes visible enough to notice.

**Why it matters:**
This is the real, structural mechanism behind retro theater — not vague "lack of accountability."

**Common trap:**
Naming only the owner half, missing why a deadline is equally necessary.

**Related:**
[Core Concepts](../syllabus/18-engineering-practices/sprint-retrospectives.md#4-core-concepts-l2)

## Card: Real measured evidence — the mechanical checker

**Prompt:**
What did a real, executed check prove about retrospective action items?

**Answer:**
A real Python script (`check_retro_action_items.py`) parses a retrospective's Action Items section and flags any item missing an explicit Owner or Deadline. Run against a real, filled-out example retrospective, it passes (`PASS ... 2 action item(s), all with an Owner and a Deadline`). Run against a deliberately incomplete one, it fails and names exactly the missing fields (`FAIL ... item missing Owner, Deadline: "- [ ] Figure out a better reviewer rotation"`).

**Why it matters:**
A real, cheap, mechanical floor against retro theater — the same real-artifact discipline this domain's ADR chapter uses (`check_adr_completeness.py`).

**Common trap:**
Assuming the mechanical check alone guarantees follow-through — it only confirms the fields exist, not that the owner will actually complete the item.

**Related:**
[Examples](../syllabus/18-engineering-practices/sprint-retrospectives.md#7-examples)
