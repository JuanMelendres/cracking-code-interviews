---
title: "Interview Playbook — Index"
document_type: playbook-index
status: draft
last_updated: 2026-08-04
---

# Interview Playbook

The Interview Playbook owns interview-craft content: reusable structures for *delivering* a technical answer under interview conditions, as distinct from `handbook/`'s canonical explanations of *what* a concept is and `cheat-sheets/`'s one-page rapid-review of a chapter's content. Per `CLAUDE.md`'s Canonical Content Ownership rule, entries here reference canonical `handbook/` chapters rather than restating their explanations, and should not be confused with the per-chapter "Interview Answer Framework" section every `handbook/` chapter already has — this directory is where the *shared method* underlying every one of those per-chapter sections is documented once, not duplicated per chapter.

## A note on scope

Four subdirectories exist per `CLAUDE.md`'s target repository structure: `technical-answers/`, `system-design/`, `coding/`, `behavioral/`. Only `technical-answers/` has content so far — `system-design/`, `coding/`, and `behavioral/` are placeholders (`.gitkeep` only). This is the first batch of this Phase 6 deliverable.

## technical-answers/

| Entry | Topic ID | IWI | What it's about |
|---|---|---|---|
| [The Technical Answer Framework — Nine Layers](technical-answers/technical-answer-framework.md) | T-1601 | 7.30 | The nine-layer structure (Opening → Staff extension) every substantive technical answer in this programme follows. The multiplier skill — improves delivery of every other topic rather than adding isolated content of its own. Elevated from `study-packs/week-01/03-technical-answer-framework.md`. |
| [Trade-off Narration and Architecture Decision Records](technical-answers/trade-off-narration-and-adrs.md) | T-1505/T-916 | 8.10 | The four-beat structure (Context → Options → Decision criterion → What it cost) for narrating any technical trade-off — this is layer 6 of the nine-layer framework above, given its own dedicated treatment since it's the single most commonly skipped beat. Elevated from `study-packs/week-02/05-trade-off-narration-and-adrs.md`. |

These two entries are deliberately cross-linked: the nine-layer framework is the general shape of a technical answer; trade-off narration is a dedicated deep-dive on that framework's layer 6, because it's the specific layer named interview feedback identified as most commonly missing.

## system-design/, coding/, behavioral/

Not yet started. `coding/` and `behavioral/` interview delivery already have substantial coverage elsewhere in the repository — `practice/java/` for coding problems and `behavioral-handbook/` for behavioral story structure and delivery — so entries here, when built, should be genuinely playbook-shaped (delivery method, interview-day logistics, question-pattern recognition) rather than duplicating that existing canonical content.

## How this relates to other deliverables

- `handbook/` — canonical explanations of individual concepts, each with its own embedded per-chapter Interview Answer Framework section that follows the nine-layer method documented here.
- `cheat-sheets/` — one-page rapid-review of a single canonical chapter's content, for the day before an interview.
- `behavioral-handbook/` — the STAR-based structure and delivery method for behavioral (not technical) interview questions.
- `interview-playbook/` (this directory) — the shared technical-answer-delivery method itself, referenced by every canonical chapter rather than restated in each one.
