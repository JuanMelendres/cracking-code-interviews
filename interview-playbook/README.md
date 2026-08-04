---
title: "Interview Playbook — Index"
document_type: playbook-index
status: draft
last_updated: 2026-08-04
---

# Interview Playbook

The Interview Playbook owns interview-craft content: reusable structures for *delivering* a technical answer under interview conditions, as distinct from `handbook/`'s canonical explanations of *what* a concept is and `cheat-sheets/`'s one-page rapid-review of a chapter's content. Per `CLAUDE.md`'s Canonical Content Ownership rule, entries here reference canonical `handbook/` chapters rather than restating their explanations, and should not be confused with the per-chapter "Interview Answer Framework" section every `handbook/` chapter already has — this directory is where the *shared method* underlying every one of those per-chapter sections is documented once, not duplicated per chapter.

## A note on scope

Four subdirectories exist per `CLAUDE.md`'s target repository structure: `technical-answers/`, `system-design/`, `coding/`, `behavioral/`. `technical-answers/`, `coding/`, and `system-design/` have content so far — `behavioral/` remains a placeholder (`.gitkeep` only). This deliverable is being built in bounded batches.

## technical-answers/

| Entry | Topic ID | IWI | What it's about |
|---|---|---|---|
| [The Technical Answer Framework — Nine Layers](technical-answers/technical-answer-framework.md) | T-1601 | 7.30 | The nine-layer structure (Opening → Staff extension) every substantive technical answer in this programme follows. The multiplier skill — improves delivery of every other topic rather than adding isolated content of its own. Elevated from `study-packs/week-01/03-technical-answer-framework.md`. |
| [Trade-off Narration and Architecture Decision Records](technical-answers/trade-off-narration-and-adrs.md) | T-1505/T-916 | 8.10 | The four-beat structure (Context → Options → Decision criterion → What it cost) for narrating any technical trade-off — this is layer 6 of the nine-layer framework above, given its own dedicated treatment since it's the single most commonly skipped beat. Elevated from `study-packs/week-02/05-trade-off-narration-and-adrs.md`. |

These two entries are deliberately cross-linked: the nine-layer framework is the general shape of a technical answer; trade-off narration is a dedicated deep-dive on that framework's layer 6, because it's the specific layer named interview feedback identified as most commonly missing.

## coding/

| Entry | Topic ID | What it's about |
|---|---|---|
| [Coding Interview Communication Protocol](coding/coding-interview-communication-protocol.md) | T-1419 | The six-phase narration protocol (Clarify → Re-state complexity) for live coding interviews — runs every coding session. Elevated from `study-packs/week-01/04-coding-interview-communication.md`. |

## system-design/

| Entry | Companion topic | What it's about |
|---|---|---|
| [System Design Interview Delivery: Time-Boxing and Mid-Round Changes](system-design/time-boxing-and-mid-round-changes.md) | T-801/T-802 (System Design Method and Estimation) | Two live-delivery skills distinct from the six-phase method itself: running the method inside a fixed clock, and responding to an interviewer-injected requirement change mid-round by revising the specific earlier decision it invalidates rather than bolting on a patch. Synthesized from real, recurring patterns across this programme's own Week 9–10 mock-interview scripts, not elevated from a single source file. |

## behavioral/

Not yet started. `behavioral/` interview delivery already has substantial coverage in `behavioral-handbook/` (STAR structure and delivery method), so an entry here, when built, should be genuinely playbook-shaped (interview-day logistics, question-pattern recognition across companies) rather than duplicating that existing canonical content.

## How this relates to other deliverables

- `handbook/` — canonical explanations of individual concepts, each with its own embedded per-chapter Interview Answer Framework section that follows the nine-layer method documented here.
- `cheat-sheets/` — one-page rapid-review of a single canonical chapter's content, for the day before an interview.
- `behavioral-handbook/` — the STAR-based structure and delivery method for behavioral (not technical) interview questions.
- `interview-playbook/` (this directory) — the shared technical-answer-delivery method itself, referenced by every canonical chapter rather than restated in each one.
