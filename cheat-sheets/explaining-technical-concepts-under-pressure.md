---
title: "Cheat Sheet: Explaining Technical Concepts Under Pressure"
slug: explaining-technical-concepts-under-pressure
document_type: cheat-sheet
domain: 20-interview-preparation/technical-answers
topic_id: T-1606
canonical: ../syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md
last_updated: 2026-09-30
---

# Explaining Technical Concepts Under Pressure

**Canonical chapter:** [`syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md`](../syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md)

## Core Mental Model

Knowing is **simultaneous** — the whole picture, available at once. Speech is **serial** — two words a second, one order, no going back. The failure is *linearisation*, not knowledge. That is why re-reading never fixes it.

## The Five Moves

1. **Claim first**, one sentence that stands alone if you're stopped immediately.
2. **One concrete instance** before any abstraction — a number, a name, a line.
3. **Signpost** when there are parts: "there are three things." You've now committed to a structure out loud.
4. **Name the boundary** of what you covered — hands the interviewer your preferred next question.
5. **Stop.** The silence is the interviewer taking a note.

## Before / After

> ❌ "So, `ArrayList` and `LinkedList` both implement `List`, and they're both in `java.util`, and the difference comes down to how they store things internally, which affects…"

> ✅ "**`ArrayList` is fast to read by index and slow to insert in the middle; `LinkedList` is the opposite.** Both implement `List`, so they're interchangeable at the type level — the difference is entirely in the data structure underneath."

Eleven words to a complete answer. Everything after is depth you control.

## Measured (this repo's own answers, 140 wpm)

| Layer | Budget | Median actual | Verdict |
|---|---|---|---|
| 30-Second Answer | 70 words | **30s (1.0x)** | Well calibrated |
| 2-Minute Answer | 280 words | **59s (0.5x)** | Half-written |
| 10-Minute Deep Dive | 1400 words | **42s (0.1x)** | **An outline, not an answer** |

Deep dives are "Cover, in order: A; B; C" sentences. Read one aloud and you produce ~40 seconds of speech with nine minutes left. **The expansion is your work, and it happens out loud, in advance.**

Re-run: `python3 scripts/audit_answer_delivery_length.py`

## Analogy Rules

- Must share the **mechanism**, not the category.
  - ✅ `ArrayList` = numbered parking spaces (contiguous, indexable, expensive to shift).
  - ❌ `ArrayList` = a shopping list (both are lists; nothing transfers).
- **State where it breaks, unprompted** — turns a liability into a credibility signal and moves you to real mechanism.
- One analogy per answer. Drop it once it has done its job.

## When It Will Not Come Out

| Situation | Move |
|---|---|
| Question too broad | Say the scope back, pick a lane: *"Let me start with X unless you'd rather Y."* |
| The word is gone | Describe the mechanism, keep moving. Nobody scores class-name retrieval speed |
| You said something wrong | Correct in one clause, continue. **Don't restart** |
| You don't know | Say so, then give the nearest thing you do know, and mark which is which |

A confident guess is the only genuinely bad option — the follow-up will find it.

## Self-Diagnosis (record 2 min, play back once)

1. **Time to claim** — target under 10s. Highest-leverage number.
2. **Longest unbroken stretch** — over ~40s and the listener is gone.
3. **Concrete instance** — is there anything checkable?
4. **The ending** — does it end, or fade?
5. **Filler density** — over ~1 per 10s means you're composing, not delivering. Fix is more rehearsal, not more self-monitoring.

## Practice Cadence

| When | What |
|---|---|
| Daily, 5 min | One concept, opening only, recorded. Check time-to-claim |
| Weekly, 20 min | [Explanation drill](../practice/mock-interviews/explanation-drill.md) — 3 audiences, 60s each |
| Per mock round | Score delivery on its own axis (all 17 rounds now carry it) |
| After a real interview | The question you knew and delivered badly = next week's daily rep |

## Common Pitfalls

- Burying the claim.
- Abstract before concrete — the natural instinct of someone who knows the topic.
- Answering a **narrower** question than asked (opening about `ArrayList` when asked `List` vs `Set` vs `Map`).
- Not stopping. Appended unrehearsed material is where imprecise claims come from.
- Riding an analogy past its usefulness.
- **Practising by re-reading.** Feels productive, trains the wrong skill.

## Related

- [The Technical Answer Framework — Nine Layers](../syllabus/20-interview-preparation/technical-answers/technical-answer-framework.md)
- [Trade-off Narration and ADRs](../syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md)
- [STAR Framework and Delivery](../syllabus/20-interview-preparation/behavioral/01-star-framework-and-delivery.md)
- [Explanation Drill](../practice/mock-interviews/explanation-drill.md)
