---
title: "Mastery Model"
document_type: syllabus-overview
status: extracted from the approved Syllabus Transformation Plan — Phase 1 scaffolding
last_updated: 2026-09-03
source: 00-project/syllabus-transformation-plan.md
---

> **Provenance note.** This file's content is extracted verbatim from `00-project/syllabus-transformation-plan.md` (approved 2026-09-03), not newly authored. The plan document remains the canonical source of record for any future dispute about intent; this file exists so the content lives at its designated `syllabus/` home per Phase 1 scaffolding.

# Mastery Model

## 5. Mastery model

### 5.1 The four levels

| Level | Name | What it verifies | Example criterion (HashMap, `T-201`) |
|---|---|---|---|
| **L1** | Foundation | Can explain what it is, why it exists, and when to reach for it | "Can state what a HashMap is, name its three or four practical use cases, and explain in one sentence why it's usually faster than a list for lookups — without being asked about internals." |
| **L2** | Practitioner | Can use it correctly and compare it to its alternatives | "Can explain the `equals`/`hashCode` contract, name what breaks when it's violated, state HashMap's average-case complexity, and choose correctly between HashMap/LinkedHashMap/TreeMap for three given scenarios." |
| **L3** | Senior | Can explain internals, reason about performance, and debug it in production | "Can explain bucket resizing, load factor, treeification, and walk through diagnosing a real lookup-latency regression caused by poor hash distribution, using only symptoms as the starting point." |
| **L4** | Staff | Can reason about systemic consequences and defend an architectural decision | "Can explain when an in-memory HashMap-based cache stops being sufficient for a given throughput/consistency requirement, and can defend the trade-off of moving to a distributed cache (or not) to a skeptical peer." |

### 5.2 Verification, not reading

Per the user's explicit requirement, **"read this chapter" is never a criterion.** Every level's criteria must be demonstrable in one of these forms, and every topic's Mastery Checklist section must use at least one:

1. **Explain-it-cold** — answer a specific interview-style question without notes (the existing Interview Questions section already supplies raw material for this).
2. **Apply-it** — solve a coding/practice exercise correctly (existing `practice/` code, once linked, already supplies this for most Java/DSA topics).
3. **Debug-it** — diagnose a described symptom to its root cause (existing `production-cookbook/` entries already supply this almost verbatim for the topics that have one).
4. **Compare-it** — correctly choose between named alternatives for a given scenario, with a stated reason.
5. **Defend-it** — produce a short written or spoken justification for a decision under a stated constraint (existing ADR template and `syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md` already supply the mechanics for this).

A topic's Mastery Checklist is a short table, e.g.:

```markdown
## Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | State what a HashMap is and give 2 real use cases | [Interview Q1](#question-1) |
| L2 | Explain equals/hashCode contract; choose HashMap vs TreeMap vs LinkedHashMap for 3 scenarios | [Practice Exercise 2](../../practice/java/collections/) |
| L3 | Explain resizing/treeification; diagnose the latency scenario in Production Scenarios | [Debugging Exercise](../../production-cookbook/...) |
| L4 | Defend when to move from in-memory HashMap to a distributed cache | [Design Exercise](#design-exercises) |
```

### 5.3 Why this is not four separate documents

This directly implements the user's "one TOPIC, not four seniority-versioned copies" requirement. A Junior reader opens the same file as a Staff reader; the Junior reader stops after L1/L2, the Staff reader skims L1/L2 (already known) and spends their time in L3/L4. The Table of Contents (already a mandatory section in the existing template) becomes the navigation mechanism — each level is its own set of headings, so a reader can jump straight to `## Level 3 — Senior Depth` without reading L1/L2 first.

---
