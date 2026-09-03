---
title: "Interview Playbook — Index"
document_type: playbook-index
status: draft
last_updated: 2026-08-04
---

# Interview Playbook

The Interview Playbook owns interview-craft content: reusable structures for *delivering* a technical answer under interview conditions, as distinct from `handbook/`'s canonical explanations of *what* a concept is and `cheat-sheets/`'s one-page rapid-review of a chapter's content. Per `CLAUDE.md`'s Canonical Content Ownership rule, entries here reference canonical `handbook/` chapters rather than restating their explanations, and should not be confused with the per-chapter "Interview Answer Framework" section every `handbook/` chapter already has — this directory is where the *shared method* underlying every one of those per-chapter sections is documented once, not duplicated per chapter.

## A note on scope

Four subdirectories exist per `CLAUDE.md`'s target repository structure: `technical-answers/`, `system-design/`, `coding/`, `behavioral/`. All four now have content. This deliverable was built in bounded batches. A fifth subdirectory, `frontend/`, was opened per the Scope Addendum in `CLAUDE.md` (2026-08-12) once the frontend domain's 37 canonical `handbook/frontend/` chapters were confirmed complete with zero corresponding interview-craft content of their own.

A sixth subdirectory, `company-prep/`, was opened 2026-09-03 at explicit user request, ahead of a specific, named interview — per `CLAUDE.md`'s explicit allowance ("do not overfit content to any single company's interview process unless explicitly requested"), this is a company-specific exception, not a change to the programme's general company-agnostic scope. Entries here are personal, targeted prep guides, not full canonical chapters — they audit existing handbook coverage against a specific topic list, close any real gaps found directly in the relevant canonical chapter, and then link back to it rather than duplicating it.

## company-prep/

| Entry | What it's about |
|---|---|
| [Nordstrom — Senior Software Engineer, Backend (Java/Spring/AWS/Kubernetes, Remote)](company-prep/nordstrom-senior-backend-remote.md) | Audited 8 user-specified topics against existing handbook coverage; 6 of 8 already covered with sufficient depth, 5 canonical chapters received targeted additions to close real gaps (atomic conditional UPDATE, HashMap's JDK 7 concurrent-resize infinite loop, a unified pool-exhaustion-plus-circuit-breaker narrative, ALB/Auto Scaling, a practical kubectl debugging workflow), plus a new synthesized p95-high-CPU-normal troubleshooting checklist. |

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
| [System Design Narration and Whiteboard Discipline](system-design/system-design-narration-and-whiteboard-discipline.md) | T-1602 | A third, distinct live-delivery skill: how to actually draw and narrate the architecture itself (not the method's own meta-diagram, and not clock management) — diagram sequencing, notation discipline, signposting, and handling an interviewer's mid-diagram redirection by integrating it rather than defending against it. New entry; a genuine register gap confirmed absent everywhere else in the handbook. |

## behavioral/

| Entry | Topic ID | IWI | What it's about |
|---|---|---|---|
| [Behavioral Interview-Day Logistics: Loop Structures and Question-Pattern Recognition](behavioral/company-loop-structures-and-question-pattern-recognition.md) | T-1604 | 6.50 | The delivery-logistics layer `behavioral-handbook/` deliberately doesn't cover: recognizing which of three question shapes a prompt is asking (retrospective/STAR, hypothetical/live-judgment, values-worded/reframed) before selecting a story, and a live, day-of story-usage tracking grid that prevents accidentally repeating the same story across a multi-round loop. Elevated from `00-project/knowledge-architecture-blueprint.md`'s previously-unelevated T-1604 (Company loop structures & calibration). Genuinely playbook-shaped — no restatement of STAR mechanics, story-portfolio design, or the per-principle reframing lens, all of which stay in `behavioral-handbook/`. |

## frontend/

| Entry | What it's about |
|---|---|
| [Frontend Live-Coding & Debugging Protocol](frontend/frontend-live-coding-and-debugging-protocol.md) | The two frontend-specific live round formats (build a feature; debug a given component) and a six-phase protocol adapted from `coding/coding-interview-communication-protocol.md` for what's actually different: component decomposition and state ownership instead of algorithmic invariants, and real-browser/DevTools verification of render behavior instead of hand-tracing an example. Not elevated from a study-pack source — a new entry closing the previously-empty `interview-playbook/frontend/` directory. |

## How this relates to other deliverables

- `handbook/` — canonical explanations of individual concepts, each with its own embedded per-chapter Interview Answer Framework section that follows the nine-layer method documented here.
- `cheat-sheets/` — one-page rapid-review of a single canonical chapter's content, for the day before an interview.
- `behavioral-handbook/` — the STAR-based structure and delivery method for behavioral (not technical) interview questions.
- `interview-playbook/` (this directory) — the shared technical-answer-delivery method itself, referenced by every canonical chapter rather than restated in each one.
